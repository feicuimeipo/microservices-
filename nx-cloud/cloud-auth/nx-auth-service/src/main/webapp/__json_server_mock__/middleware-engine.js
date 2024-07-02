

const EventEmitter = require('events').EventEmitter;
const life = new EventEmitter();
module.exports = (req, res, next) => {
    //这里不用on，也可以用addEventListener
    //这个on最多可以添加10个，添加11个后，会报出一个警告
/*    life.on('doSth', function(who){
        engine-console.log('给 ' + who + ' 倒水');
    })
    life.emit('doSth','Sunny');*/

/*   engine-console.log("req.path:",req.path);s
    engine-console.log("req.body:",req.body);*/

    //弄一个监听器
    // if (req.path === "/sso/me") {
    //     //todo: 和passport通信或soo-token通信，保存token有效
    //     const token = auth.getToken();
    //     if (!token){
    //        return  res.status(401).json(auth.fail(401,"未登录！"));
    //     }
    //     const ret = auth.getReturnAuthInfo();
    //     ret.token = token;
    //     return  res.status(200).json(auth.success(ret));
    // };
    // //弄一个监听器
    // if (req.path === "/sso/token/refresh") {
    //     const token = auth.getToken();
    //     //将token刷新到本地，用于认证使用
    //     return  res.status(200).json(auth.success({}));
    // };
    // //弄一个监听器
    // if (req.path === "/sso/token/aop") {
    //     //const token = auth.getToken();
    //     //将token刷新到本地，用于认证使用
    //     return  res.status(200).json(auth.success({}));
    // };

  next();
};


function getHomeInfo(){
    const data = {
        info: "You are welcome to Engine!"
    };
   return data;
}

function getProductList(){
    const products= [
        {
            id: "101",
            name: "C2M",
            code: "c2m",
            href: "http://localhost:7001",
        },
        {
            id: "102",
            name: "平面设计",
            code: "c2m",
            href: "http://localhost:7001",
        },
        {
            id: "103",
            name: "企业印刷",
            code: "c2m",
            href: "http://localhost:7001",
        },
        {
            id: "101",
            name: "C2M",
            code: "c2m",
            href: "http://localhost:7001",
        },
    ]
    return products;
}
