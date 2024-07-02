


const fail = (code, msg) => {
    return {
        code: code,
        msg: msg,
        data: null,
    };
};

const success = (val) => {
    return {
        code: 200,
        data: val ? val : {},
        msg: "",
    };
};


//生成从minNum到maxNum的随机数
const randomNum = (minNum,maxNum) => {
    return parseInt(Math.random()*(maxNum-minNum+1)+minNum,10);
}

const randomNumberByRange =(start, end) => {
    return Math.round(Math.random() * (end - start) + start);
}

const getUrlParam = (name) =>{
    //uncodeURIComponent(url) 编码
    const url = decodeURIComponent(window.location.href);
    const queryInfo = url.split('?')[1]   //qycode=1001&qyname=%E4%BC%81%E4%B8%9A%E5%BF%99   截取到参数部分
    const searchParams = new URLSearchParams('?'+queryInfo)  //将参数放在URLSearchParams函数中
    const value = searchParams.get(name)   //1001
    return value;
}

// const getOriginDomain = (url) => {
//     if (url) {
//         const urls = url.split("/");
//         if (urls[2]) {
//             const domain = urls[2];
//             return domain;
//         }
//     }
//     return null;
// }

//
// const os = require('os');
// function getLocalIP() {
//     let map = [];
//     let ifaces = os.networkInterfaces();
//     console.log(ifaces);
//     for (var dev in ifaces) {
//         if (dev.indexOf('eth0') != -1) {
//             var tokens = dev.split(':');
//             var dev2 = null;
//             if (tokens.length == 2) {
//                 dev2 = 'eth1:' + tokens[1];
//             } else if (tokens.length == 1) {
//                 dev2 = 'eth1';
//             }
//             if (null == ifaces[dev2]) {
//                 continue;
//             }
//             // 找到eth0和eth1分别的ip
//             var ip = null, ip2 = null;
//             ifaces[dev].forEach(function(details) {
//                 if (details.family == 'IPv4') {
//                     ip = details.address;
//                 }
//             });
//             ifaces[dev2].forEach(function(details) {
//                 if (details.family == 'IPv4') {
//                     ip2 = details.address;
//                 }
//             });
//             if (null == ip || null == ip2) {
//                 continue;
//             }
//             // 将记录添加到map中去
//             if (ip.indexOf('10.') == 0 ||
//                 ip.indexOf('172.') == 0 ||
//                 ip.indexOf('192.') == 0) {
//                 map.push({"intranet" : ip, "internet" : ip2});
//             } else {
//                 map.push({"intranet" : ip2, "internet" : ip});
//             }
//         }
//     }
//     return map;
// }


module.exports ={
    randomNum,success,fail,randomNumberByRange,getUrlParam
}
