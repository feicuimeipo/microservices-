
window.context = {
  manage: 'http://127.0.0.1:8080/mvue', //管理端页面
  front: 'http://127.0.0.1:8081/fvue', //前端页面
  mobile: 'http://127.0.0.1:8082/mobilevue', //手机端页面
  portal: 'http://127.0.0.1:8088',
  bpmRunTime: 'http://127.0.0.1:8088',
  bpmModel: 'http://127.0.0.1:8088',
  uc: 'http://127.0.0.1:8088',
  form: 'http://127.0.0.1:8088'
};
// 单点配置
window.ssoConfig = {
  mode: "", // 空则不使用单点  支持的模式有  cas oauth basic
  url: 'http://www.hotent.org:7080/cas',
  logout: 'http://www.hotent.org:7080/cas/logout'
}
