const path = require("path");
function resolve(dir) {
  return path.join(__dirname, dir);
}
module.exports = {

  configureWebpack:{
    name: 'x7plus',
    resolve: { // 配置解析模块路径别名 : 优点 简写路径 , 缺点 路径没有提示
      alias: { "@": resolve("src"),  },  // 定义一个 @ 变量 , 可在 import 引入时使用
    },
  },
  publicPath: '/mvue',
  indexPath: "index.html",
  assetsDir: "src/assets",
  runtimeCompiler: true,
  chainWebpack:config=>{
    config.devServer.disableHostCheck(true);
    config.entry('index').add('babel-polyfill');
  },
  productionSourceMap: false,
  devServer: {
    port: 1080,
    disableHostCheck: true,
    allowedHosts: [
      'www.x7.com',
      '*.eip.com',
      '.eip.com',
      'localhost',
      '127.0.0.1'
    ],
    proxy: null,
  }
}