import axios from "axios";
import { Message } from "element-ui";
import { Loading } from "element-ui";
import $store from "@/store";
import store from "@/store/login";
import tenant from "@/store/tenant";
import router from "@/router.js";
import NProgress from "nprogress";
import utils from "@/hotent-ui-util.js";
import "nprogress/nprogress.css";
import { saveAs } from "file-saver";
axios.defaults.timeout = 100000;

let pending = []; //声明一个数组用于存储每个ajax请求的取消函数和ajax标识
let cancelToken = axios.CancelToken;
let removePending = ever => {
  for (let p in pending) {
    if (
      pending[p].u === ever.url + "&" + ever.method &&
      pending[p].data === eval.data
    ) {
      //当当前请求在数组中存在时执行函数体
      pending[p].f(); //执行取消操作
      pending.splice(p, 1); //把这条记录从数组中移除
    }
  }
};

// 请求拦截（配置发送请求的信息）
axios.interceptors.request.use(
  config => {
    // 处理请求之前的配置
    NProgress.start(); //开始
    store.state.loadding = true; //在请求发出之前进行一些操作
    config.headers = config.headers || {};
    if (config.headers && config.headers.constructor == String) {
      try {
        config.headers = JSON.parse(config.headers);
      }
      catch (e) {
        Message.error(`请求头部不是有效的JSON格式:${config.headers}`);
        throw e;
      }
    }
    config.headers["Accept-Language"] = localStorage.getItem("lang") || "zh-CN";
    if (store.state.loginAccount) {
      config.headers["Tenant-Code"] =
        localStorage
          .getItem(store.state.loginAccount + "loginRoutePath")
          .replace("/login/", "") || "";
    }
    const currentUser = store.state.currentUser;
    currentUser && currentUser.token && !config.headers.Authorization && (config.headers.Authorization = `Bearer ${currentUser.token}`);
    if (tenant.state.tenantId && config.url.indexOf("tenantId=") == -1) {
      let joinChar = "?";
      if (config.url.indexOf("?") != -1) {
        joinChar = "&";
      }
      config.url = config.url + joinChar + "tenantId=" + tenant.state.tenantId;
    }
    //如果是请求auth接口，则不带token
    if (config.url.indexOf('/auth?') != -1) {
       delete config.headers.Authorization;
    }
    removePending(config); //在一个ajax发送前执行一下取消操作
    config.cancelToken = new cancelToken(c => {
      // 将请求的地址和请求的方式构建为一个字符串存放到请求数组中
      pending.push({
        u: config.url + "&" + config.method,
        f: c,
        data: config.data
      });
    });

    // 判断是否需要刷新token
    let currentTime = new Date().getTime();
    if( currentUser && currentUser.loginTime && currentTime -currentUser.loginTime >= (currentUser.expiration/5)*1000 && config.url.indexOf('/refresh') == -1){
      $store.dispatch("login/refreshAndGetAuthenticationToken");
    }

    return config;
  },
  error => {
    // 请求失败的处理
    NProgress.done(); // 结束
    return Promise.reject(error);
  }
);

// 响应拦截（配置请求回来的信息）
axios.interceptors.response.use(
  res => {
    // 处理响应数据
    removePending(res.config); //在一个ajax响应后再执行一下取消操作，把已经完成的请求从pending中移除
    if (res.data && res.data.state != undefined && res.data.state === false) {
      Message.error(res.data.message);
    }
    store.state.loadding = false; //在这里对返回的数据进行处理
    NProgress.done(); // 结束
    // 附件下载
    if (
      res &&
      res.status == 200 &&
      res.headers &&
      res.headers["content-disposition"] &&
      res.headers["content-disposition"].startsWith("attachment;")
    ) {
      let blob = new Blob([res.data]);
      const fileName = decodeURIComponent(
        res.headers["content-disposition"].split(";")[1].split("filename=")[1]
      );
      saveAs(blob, fileName);
    }
    return res;
  },
  error => {
    // 处理响应失败
    let loadingInstance = Loading.service({ fullscreen: true }); //开始
	const errorMessage = error && error.response && error.response.data && error.response.data.message ? error.response.data.message:error.message;
    if (error && error.response && error.response.status === 401) {
      sessionStorage.clear();
      router.push({ path: localStorage.getItem("loginRoutePath") || "/login" });
    } else if (error && error.response && error.response.status === 403) {
      let msg = "您没有权限请求此接口";
      if (error.response.data && error.response.data.path) {
        msg += ":" + error.response.data.path;
      }
      Message.error(msg + ",请联系管理员");
    }
    if (error && error.response && error.response.status === 500) {
      if (error.response.config.url.indexOf("saveForm") == -1) {
        //设计表单页面-控件没有绑定属性，点击保存后会有多个提示
        if (error.response.data && error.response.data.message) {
          Message.error(error.response.data.message);
        } else {
          Message.error(error.message);
        }
      }
    } else if (error && error.isAxiosError && error.message) {
		Message.error(errorMessage);
    }else if(errorMessage && typeof(errorMessage)!='undefined'){
    	Message.error(errorMessage);
    }
    NProgress.done(); // 结束
    loadingInstance.close(); // 结束
    return Promise.reject(error);
  }
);

export default {
  request(data) {
    let reqData = data.data || {};
    //支持表达式作为域名解析
    data.url = utils.parseUrl(data.url);
    //统一拼接域名接口
    if (data.url.indexOf("http") == -1) {
      data.url = context.manage + data.url;
    }
    let requestData = {
      url: data.url,
      data: reqData,
      method: data.method || "GET",
      params: data.params || {},
      onUploadProgress: data.onUploadProgress || null,
      headers: data.headers || "",
      responseType: data.responseType || "json"
    };
    return axios(requestData);
  },
  download(url) {
    return this.request({ url, responseType: "arraybuffer" });
  },
  get(url, type) {
    return this.request({ url, responseType: type });
  },
  post(url, data, params, responseType) {
    return this.request({
      url,
      data,
      params,
      method: "POST",
      responseType: responseType
    });
  },
  remove(url) {
    return this.request({ url, method: "DELETE" });
  },
  put(url) {
    return this.request({ url, method: "PUT" });
  },
  getContext() {
    return context;
  }
};
