import axios from "axios";
import {done, start} from "@/utils/nporgress";
import {ElMessage} from "element-plus/es";


const baseUrl = import.meta.env.VITE_APP_BASE_API;
const authRequest = axios.create({
    baseURL: baseUrl,     // 所有的请求地址前缀部分
    timeout: 100000,      // 请求超时时间毫秒
    withCredentials: true, // 异步请求携带cookie
    headers: {
        // 设置后端需要的传参类型
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest',
    },
})

const cancelToken = axios.CancelToken;
/* 防止重复提交，利用axios的cancelToken */
let pending: any[] = []; // 声明一个数组用于存储每个ajax请求的取消函数和ajax标识
const removePending:any = (config: any,c:any) => {
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
authRequest.interceptors.request.use(
    (config:any) =>{
        start(); //开始

        config.headers = config.headers || {};
        if (config.headers && config.headers.constructor == String) {
            try { config.headers = JSON.parse(config.headers);  } catch (e) {  ElMessage.error(`请求头部不是有效的JSON格式:${config.headers}`); throw e;}
        }
        config.headers['Accept-Language'] = localStorage.getItem("lang") || "zh-CN";

        // neverCancel 配置项，允许多个请求
        if (!config.neverCancel) {  // 生成cancelToken
            config.cancelToken = new cancelToken((c:any) => {
                removePending(config, c);
            });
        }

        return config;
    },
    (error:any) =>{
        console.log(error);
        done();
        return Promise.reject(error);
    }
)

//响应拦截
authRequest.interceptors.response.use(
    (res) => {
        // 2xx 范围内的状态码都会触发该函数。
        removePending(res.config, null);

        // 获取返回数据，并处理。按自己业务需求修改。
        const ret = res.data;
        if (res.data && (res.data.state != undefined && res.data.state === false) || (res.data.code !=undefined && res.data.code !==0 && res.data.code !==200) ) {
            ElMessage.error(res.data.msg);
        }

        done();

        return res;
    },
    function (error){
        // 超出 2xx 范围的状态码都会触发该函数。
        const errorMessage = error && error.response && error.response.data && error.response.data.msg ? error.response.data.msg:error.message;

        if (error && error.response && error.response.status !== 200) {
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

export default authRequest;
