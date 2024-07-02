import router from "@/router";

export const getUrlParam = (name:string|null):string|null =>{
    const url = decodeURIComponent(window.location.href);
    const queryInfo = url.split('?')[1]   //qycode=1001&qyname=%E4%BC%81%E4%B8%9A%E5%BF%99   截取到参数部分
    if (name!=null && name.length>0) {
        const searchParams = new URLSearchParams('?' + queryInfo)  //将参数放在URLSearchParams函数中
        const value = searchParams.get(name)   //1001
        return value;
    }else{
        if (queryInfo && queryInfo!=undefined && queryInfo!=null) {
            return queryInfo;
        }else{
            return null;
        }
    }
}

export const getAllUrlParam = ():any =>{
    const url = decodeURIComponent(window.location.href);
    if (url.indexOf("?")<=-1){
        return null;
    }
    const queryInfo = url.split('?')[1]   //qycode=1001&qyname=%E4%BC%81%E4%B8%9A%E5%BF%99   截取到参数部分
    const searchParams = new URLSearchParams('?'+queryInfo)  //将参数放在URLSearchParams函数中
    const paramMap:any = {};
    const keyArr = searchParams.keys();
    for(const key of keyArr){
        paramMap[key] = searchParams.get(key);
    }

    return paramMap;
}

export const toRouterLinkByParam = (path:string) => {
    const paramMap = getAllUrlParam();
    if (paramMap && paramMap!=null && paramMap!=undefined){
       return router.push({path: path, query: paramMap});
    }else{
        return router.push({path: path});
    }
}


/**
 * 不需要授权的界面
 */
export const logoutURL = () => {
    return getUrlParam("failureUrl");
}


export const getUrlParams = ():any =>{
    const url = decodeURIComponent(window.location.href);

    const queryInfo = url.split('?')[1]
    const array:string[] = queryInfo.split('&')
    const valueObj:any = {}  //创建空对象，接收截取的参数
    for(let i = 0;i < array.length;i++ ){
        console.log(i)
        const item:string[] = array[i].split('=')
        const key = item[0]
        const value = item[1]
        valueObj[key] = value
    }
    return valueObj;
}
