import {ElMessage} from 'element-plus';
import {start,done} from "@/utils/nporgress";
import {useAuthStore} from "@/stores/auth-store";
import { saveAs } from 'file-saver'
import router from "@/router"
import type {Canceler} from "axios";
import axios from "axios";


const baseUrl = import.meta.env.VITE_APP_BASE_API;

const cancelToken = axios.CancelToken;
const service = axios.create({
    baseURL: baseUrl,     // 所有的请求地址前缀部分
    timeout: 100000,      // 请求超时时间毫秒
    withCredentials: true, // 异步请求携带cookie
    headers: {
        // 设置后端需要的传参类型
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
    },
})


interface IPedding{
    u:string,
    f: Canceler,
    data: any
}

/* 防止重复提交，利用axios的cancelToken */
let pending: IPedding[] = []; // 声明一个数组用于存储每个ajax请求的取消函数和ajax标识
const removePending:any = (config: any,c:Canceler) => {
    let exist = <boolean>false;
    for (let i=0;i<pending.length;i++) {

        const newData = config.data? JSON.stringify(config.data):null;
        const oldData = pending[i].data? JSON.stringify(pending[i].data):null;
        if (pending[i].u === config.url + '&' + config.method && newData===oldData) { //当当前请求在数组中存在时执行函数体
            //console.log("取消重复提交的操作！")
            pending[i].f(); //执行取消操作
            pending.splice(i, 1); //把这条记录从数组中移除
            exist = true;
        }
    }

    if (!exist && c!=null){
        pending.push({ u: config.url + '&' + config.method, f: c, data: config.data});
    }
}

// 添加请求拦截器
service.interceptors.request.use(
     (config:any) =>{
         start(); //开始

         config.headers = config.headers || {};
         if (config.headers && config.headers.constructor == String) {
             try { config.headers = JSON.parse(config.headers);  } catch (e) {  ElMessage.error(`请求头部不是有效的JSON格式:${config.headers}`); throw e;}
         }
         config.headers['Accept-Language'] = localStorage.getItem("lang") || "zh-CN";

        const  authStore  = useAuthStore();
        //在这里可以统一修改请求头，例如 加入 用户 token 等操作
         if (authStore) {
             if (authStore.$state.authToken) {
                 config.headers['Authorization'] = "Bearer " + authStore.$state.authToken; // 让每个请求携带token--['X-Token']为自定义key
             }
             if (authStore.$state.tenantId) {
                 config.headers['x-tenantId'] =  "TenantId "+authStore.$state.tenantId; // 让每个请求携带token--['X-Token']为自定义key
             }

             //如果是请求auth接口，则不带token
             if (config.url.indexOf('/auth?') === -1 && config.url.indexOf('/auth/refresh') === -1) {
                 // 判断是否需要刷新token
                 const currentTime: number = new Date().getTime();
                 const loginTime : number = authStore.$state.loginTime?authStore.$state.loginTime:0;
                 const expiration :number = authStore.$state.expiration? authStore.$state.expiration:0;
                 if ( currentTime - loginTime >= (expiration / 5) * 1000) {
                     authStore.refreshToken();
                 }
             }
         }


         // neverCancel 配置项，允许多个请求
         if (!config.neverCancel) {  // 生成cancelToken
             config.cancelToken = new cancelToken((c:any) => {
                 removePending(config, c);
             });
         }

         // config.cancelToken = new cancelToken((c) => {
         //     pending.push({ u: config.url + '&' + config.method, f: c, data: config.data });    // 将请求的地址和请求的方式构建为一个字符串存放到请求数组中
         // });

        return config;
    },
    (error:any) =>{
        console.log(error);
        done();
        return Promise.reject(error);
    }
)

//响应拦截
service.interceptors.response.use(
     (res) => {
        // 2xx 范围内的状态码都会触发该函数。
         removePending(res.config, null);

         // 获取返回数据，并处理。按自己业务需求修改。

         //console.log("res.data="+JSON.stringify(ret));
         if (res.data && (res.data.state != undefined && res.data.state === false) || (res.data.code !=undefined && res.data.code !==0 && res.data.code !==200) ) {
             ElMessage.error(res.data.msg);
         }

         const ret = res.data;

        done();

         // 附件下载
         if (res.status===200 && res.headers &&
             res.headers["content-disposition"] &&
             res.headers["content-disposition"].startsWith("attachment;")
         ) {
             const blob = new Blob([res.data]);
             const fileName = decodeURIComponent(
                 res.headers["content-disposition"].split(";")[1].split("filename=")[1]
             );
             saveAs(blob, fileName);
         }
         return res;

    },
    function (error){
        // 超出 2xx 范围的状态码都会触发该函数。
        const errorMessage = error && error.response && error.response.data && error.response.data.msg ? error.response.data.msg:error.message;

        if (error && error.response && error.response.status === 401) {
            //未登录，则返回登录界面
            localStorage.clear();
            sessionStorage.clear();
            router.push({path: "/login"});
        }else if (error && error.response && error.response.status === 402) {
            //权限不足
            ElMessage.error("权限不足:" + error.response.data.msg);
            //location.href = '/#/';
        }else if (error && error.response && error.response.status === 403) {
            //权限不足
            ElMessage.error("企业未认证:" + error.response.data.msg);
            //location.href = '/#/';
        }else if (error && error.response && error.response.status === 500) {
            if (error.response.data && error.response.data.message) {
                ElMessage.error(error.response.data.message);
            } else {
                ElMessage.error(error.message);
            }
        }else if(errorMessage && typeof(errorMessage)!='undefined'){
            ElMessage.error(error.message);
        }
        // 异常处理
        console.log(error)
        pending = [];
        done();
        return Promise.resolve(error.response);
    }
)


export default {
    request(req:any){
        const data = req.data || {};
        const requestData = {
            url: req.url,
            data: data,
            method: req.method,
            params: req.params || {},
            onUploadProgress: req.onUploadProgress || null,
            headers: req.headers || '',
            responseType: req.responseType || 'json'
        };
        return service(requestData)
    },
    remove(url:string) {
        return this.request({ url, method: "DELETE" });
    },
    post(url:string, data: object, responseType:string|'json') {
        return this.request({ url, data, method: "POST", responseType: responseType });
    },
    put(url:string, data: object, responseType:string|'json') {
        return this.request({ url, data, method: "PUT", responseType: responseType });
    },
    get(url:string, type: string|'json') {
        return this.request({ url, responseType: type, });
    },
    download(url:string) {
        return this.request({ url, responseType: "arraybuffer" });
    },
    data(res:any):object|null{
        const data = res.data.data? res.data.data:res.data;
        return data? data:null;
    }
};
