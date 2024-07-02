


const utils = require("./utils")


module.exports = (req, res, next) => {
    console.log("req.path:",req.path);
    console.log("req.body:",req.path)

    if (req.path === "/auth/verifyInfo") {
        const ret  = utils.success(verfyInfo(req));
        console.log("x="+JSON.stringify(ret))
        return res.status(200).json(ret);
    };


    if (req.path === "/auth/refresh") {
        const ret  = utils.success(verfyInfo(req));
        console.log("x="+JSON.stringify(ret))
        return res.status(200).json(ret);
    };

    if (req.path === "/auth/checkLoginStatus") {
        const ret  = utils.success(verfyInfo(req));
        console.log("x="+JSON.stringify(ret))
        return res.status(200).json(ret);
    };

    // if (req.path === "/auth/client/connect") {
    //     const query = req.body;
    //     const appId = query.appId;
    //     const secretKey = query.secretKey;
    //
    //
    //   const ret  =utils.success(data);
    //
    //   console.log("ret="+JSON.stringify(ret));
    //
    //     return res.status(200).json(ret);
    // };

    // if (req.path === "/auth/client"){
    //     const query = req.query;
    //     const webDomain = query.domain
    //     //TODO: 查询对应的后端ApiDomain
    //     //登录回call
    //     const apiDomain = "http://.../sso/login/callback";
    //     return ret;
    // },
    //     if (req.path === "/auth/checkLoginStatus"
    //     || req.path === "/auth/fetchLoginStatus"
    // ) {
    //
    //     // const query = req.query;
    //     // const successUrl = query.successURL?query.successUR:null;
    //     // const domain = utils.getOriginDomain(successUrl)
    //     // if (domain===null){
    //     //     const ret  =utils.fail(403,"当前客户端未在该当前登录中注册过!");
    //     //     return res.status(200).json(ret);
    //     // }
    //     //TODO: 判断domain是否在当前认证中心注册过
    //     const Days = 7;
    //     const sessionId = req.sessionId;
    //     if (req.session.get("sessionId")){
    //        const authToken = req.session.get("sessionId");
    //         //通过authToken得到AuthUser
    //        const authUser = {
    //            userId: "0123",
    //            username: "myAdmin",
    //            loginType: 1,
    //            loginAccount: "18601106333",
    //            displayName: "我的测试帐号",
    //            tenantId: "0",
    //            loginTime: new Date().getTime(),
    //            expiration: Days*24*60*60*1000,
    //            authToken: "11234567890",
    //        }
    //
    //        const cookies = req.Cookies;
    //        const exp = new Date().getTime() + authUser.expiration;
    //        cookies.set("JSESSIONID",sessionId,{expires: exp});
    //        //res.cookie(aaa..)
    //
    //         const ret = utils.success(authUser);
    //         // res.code = ret.code;
    //         // res.msg = ret.msg;
    //         // res.json(ret.data);
    //         return res.status(200).json(ret);
    //     }
    //     const ret  =utils.fail(401,"用户找不到!");
    //     return res.status(200).json(ret);
    // }

    if (req.path === "/auth/isAuth") {
        const ret  =utils.fail(402,"没有权限!");
        return res.status(200).json(ret);
    }

    next();
};

// params方式
// http://localhost:3000/products/123/why
// query方式
// http://localhost:3000/login?username=why&pasword=123
const  verfyInfo=(req) => {
    // L: block真实长度  w:图片长度 h:图片高度 r:block半径
    // L: block real length; w: the image width; h: the image height; r: block radius
    // L = block.l + block.r * 2 + 3
    // 3.1产生位置信息 - generate
    //const param = req.params;
    const query = req.body;
    console.log('req.query:',query);

    const L = parseInt(query.L);
    const r = parseInt(query.r);
    const w = parseInt(query.w);
    const h = parseInt(query.h);

    console.log("L="+L+",r="+r+"w:="+w+",h="+h);
    const x = utils.randomNumberByRange(L + 10, w - (L + 10));
    const y = utils.randomNumberByRange( 10 + r * 2, h - (L + 10) );

    const data = {
        blockX: x,
        blockY: y,
    }

    return data
}



const getCookieValue = (req, name) => {
    const Cookies = {};
    if (req.headers.cookie != null) {
        req.headers.cookie.split(";").forEach((l) => {
            const parts = l.split["="];
            const key = parts[0].trim();
            const value = (parts[1] || "").trim();
            if (key === name) {
                return value;
            }
        });
    }
    return null;
};
