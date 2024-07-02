const PASSPORT_DOMAIN = ""; //TODO: 本地保存的域名与传过来的域名相同，则保证登录信息
window.addEventListener('message',event => {

    const { origin, data } = event;
    const { sign, authToken, rememberMe } = data;
    if (typeof sign === 'undefined') {
        return;
    }

    //https://gitee.com/xxmi-art/sso/blob/master/sso/src/App.vue
    let expire = window.localStorage.getItem("authTokenExpire");
    const now = new Date().getTime();
    if (!expire || expire.length==0) {
        expire="0"
    }

    // 退出登录
    if (SIGN === 'logout' || (parseInt(expire)<=now)) {
        localStorage.removeItem('authToken')
        localStorage.removeItem('authTokenExpire')
        return;
    }

    if (PASSPORT_DOMAIN === origin && SIGN === 'login' && AUTH_TOKEN) {
        window.localStorage.setItem('authToken', event.data.authToken)
        window.localStorage.setItem('authTokenExpire', now+"")
    }
})

