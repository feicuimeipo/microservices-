<template>
  <el-container :style="backStyle">
    <el-header height="80px">
      <div class="logo-container" v-if="!showTenant">
        <div>
          <img :src="require(`@/assets/svg/logo.svg`)" style="margin: 0; height:80px;padding:0"/>
        </div>
        <div class="subheader-container">
          <span class="system-name" v-if="!showTenant">{{name}}</span>
          <span class="system-name" v-else>{{companyName}}</span>
        </div>
      </div>
      <div class="logo-container" v-if="showTenant">
          <a class="logo-link" v-if="showLogo">
            <img :src="logo" style="margin-top: 0; height:80px;" />
          </a>
         <a  class="logo-link" v-if="!showLogo">
           <img :src="require(`../assets/svg/logo.svg`)" style="margin: 0; height:80px;"/>
         </a>
      </div>
      <div class="site-div"></div>
    </el-header>
    <el-main>
      <div class="login-card__container fullheight">
        <el-card shadow="always" class="login-card">
          <div :style="totemStyle" class="totem-div">
          </div>
          <div class="login-div">
            <el-form style="margin: 45px" :model="principal" :rules="rules" ref="principal">
              <div class="welcome-title">欢迎登录 </div>
              <el-form-item prop="account">
                <el-input
                  size="medium"
                  clearable
                  prefix-icon="icon-account"
                  v-model="principal.account"
                ></el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  type="password"
                  clearable
                  prefix-icon="icon-password"
                  @keyup.enter.native="submitForm('principal')"
                  @focus="inputGetFocus"
                  size="medium"
                  v-model="principal.password"
                ></el-input>
              </el-form-item>
              <el-alert v-if="responseError" :title="responseError" type="error" :closable="false"></el-alert>
              <el-form-item style="text-align: center;margin-top: 10px;">
                <el-button
                  size="medium"
                  type="primary"
                  @focus="inputGetFocus"
                  @click="submitForm('principal')"
                  :loading="loading"
                >登录</el-button>
                <el-button size="medium" @click="resetForm('principal')" :disabled="loading">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </div>
    </el-main>
    <el-footer class="copyright">Copyright © 版权所有 念熹技术 2019 <a href="http://www.nianxi.cc" target="_blank"><i class="icon-guanwang icon-guanwang-F"></i></a> </el-footer>
  </el-container>
</template>

<script>
export default {
  name: "login",
  props: ["tenant"],
  data() {
    var checkAccount = (rule, value, callback) => {
      if (!value) {
        return callback(new Error("请输入账号"));
      }
      if (/^[a-zA-Z0-9_-]{2,20}$/.test(value)) {
        return callback();
      } else {
        return callback(new Error("账号格式不符合规范"));
      }
    };
    return {
      logo: "",
      name:"EIP",
      showTenant:false,
      showLogo:false,
      companyName:"",
      tenantName: "",
      principal: {
        tenantId: "",
        account: "",
        password: ""
      },
      rules: {
        account: [{ validator: checkAccount, trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }]
      },
      responseError: "",
      loading: false,
      backStyle: {
        width: "100%",
        background: `url(${require("@/assets/svg/back.svg")}) center / 100% auto no-repeat`
      },
      totemStyle: {
        width: "250px",
        height: "320px",
        float: "left",
        background: ` url(${require("@/assets/svg/totem.svg")}) left bottom / 100% auto no-repeat`
      },
      isIeExplorer:false
    };
  },
  mounted() {
    //获取租户信息
    let tenant = this.tenant;
    if(tenant){
      let url = "${uc}/uc/tenantManage/v1/getTenantByCode?code=" + tenant;
      let _this = this;
      this.$http.get(url).then(resp => {
        if(resp.data){
          let tenantManage = resp.data;
          _this.showTenant = true;
          _this.companyName = tenantManage.name;
          if(tenantManage.ico){
            let ico = JSON.parse(tenantManage.ico);
            if(ico && ico.length >0 ){
              _this.showLogo = true;
              _this.logo = window.context.portal + "/system/file/v1/downloadFile?fileId=" + ico[0].id;
            }
          }
        }
      });
    }else{
      //不是租户
      this.sysSetting();
    }
  },
  methods: {
    //获取系统默认配置 系统Logo、名称
    sysSetting(){
      const _this = this;
      let url = window.context.portal+"/sys/sysProperties/v1/getDecryptByAlias?alias=sysSetting";
      this.$http.get(url).then(response => {
          if(response && response.data && response.data.value){
            _this.showTenant = true;
            let  sysSettingData = JSON.parse(response.data.value);
            if(sysSettingData.manageName==""){
              _this.companyName = _this.name;
            }else{
              _this.companyName = sysSettingData.manageName;
            }
            if(sysSettingData.ico.length>0){
              let ico = sysSettingData.ico;
              _this.showLogo = true;
              _this.logo = window.context.portal + "/system/file/v1/downloadFile?fileId=" + ico[0].id;
            }
          }
      });
    },
    querySearch(queryString, cb) {
      if (!queryString) {
        return cb([]);
      }
      let queryFilter = {};
      queryFilter.pageBean = {
        page: 1,
        pageSize: 20,
        total: 0,
        showTotal: true
      };
      let query = {
        property: "name_",
        value: queryString,
        group: "main",
        operation: "LIKE",
        relation: "AND"
      };
      if (!queryFilter.querys) {
        queryFilter.querys = [];
      }
      queryFilter.querys.push(query);

      this.$http
        .post(`${window.context.uc}/uc/tenantManage/v1/listJson`, queryFilter)
        .then(res => {
          // 调用 callback 返回建议列表的数据
          cb(res.data.rows);
        });
    },
    handleSelect(item) {
      this.tenantName = item.name;
      this.principal.tenantId = item.id;
    },
    setRouterPath() {
      this.$store.dispatch("login/actionLoginAccount", this.principal.account);
      localStorage.setItem(
        this.principal.account + "loginRoutePath",
        this.$route.path
      );
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.inputGetFocus();
          this.loading = true;
          this.setRouterPath();
          this.$store
            .dispatch("login/loginByPrincipal", this.principal)
            .then(loginStatus => {
              this.loading = false;
              if (loginStatus && loginStatus == true) {
                this.$store.dispatch("menu/actionMenus").then(data => {
                  this.$router.push({
                    path: this.$route.query.redirect
                      ? this.$route.query.redirect
                      : "/"
                  });
                });
              } else if (loginStatus == false) {
                this.$router.push({
                  name: "resetPwdView",
                  params: { account: this.principal.account }
                });
              }
            })
            .catch(msg => {
              this.loading = false;
              if (msg.startsWith("timeout of")) {
                msg = "登录超时";
              }
              this.responseError = msg;
            });
        } else {
          return false;
        }
      });
    },
    inputGetFocus() {
      this.responseError = "";
    },
    resetForm(formName) {
      this.responseError = "";
      this.$refs[formName].resetFields();
    }
  },  
  created(){
     let USER_AGENT = navigator.userAgent.toLowerCase();
     let isChrome = /.*(chrome)\/([\w.]+).*/;
     if(!isChrome.test(USER_AGENT)){
       this.isIeExplorer = true;
     }
  },
  components: {}
};
</script>
<style lang="scss" scoped>
@import "@/assets/css/element-variables.scss";

.login-card__container {
  display: -webkit-box;
  display: -moz-box;
  display: -ms-flexbox;
  display: -o-box;
  display: box;
  display: -webkit-flex; /* Safari */
  display: flex;
  flex-direction: column;
  justify-content: center;
  -webkit-box-align: center;
  -webkit-align-items: center;
  -ms-flex-align: center;
  align-items: center;

}

.logo-container {
  margin-top: 10px;
  margin-left: 30px;
  display: -webkit-box;
  display: -moz-box;
  display: -ms-flexbox;
  display: -o-box;
  display: box;
  display: -webkit-flex; /* Safari */
  display: flex;
  flex-direction: row;
}

.subheader-container{
  height: 80px;
  margin: 0;
  padding: 0;
  margin-top: 40px;
}

.system-name {
  color: #666;
  font-weight: bold;
  font-size: 16px;
  vertical-align: bottom;
  alignment: left;
}

.site-div {
  float: right;
  margin-top: 25px;
  margin-right: 40px;
}

.site-div > a {
  color: #5b9dff;
  font-size: 12px;
  text-decoration: none;
}

.site-div > a > i {
  font-weight: bold;
  margin-right: 5px;
}


.login-card {
  width: 600px;
  height: 320px;
}

.totem-div {
  /*-moz-box-shadow: 2px 2px 5px #999;*/
  -moz-box-shadow: 0px 0px 0px #999;
  -webkit-box-shadow: 0px 0px 0px #999;
  box-shadow: 0px 0px 0px #999;
}

.login-div {
  width: 350px;
  height: 320px;
  float: right;
}

.welcome-title {
  margin-top: 40px;
  font-size: 18px;
  margin-bottom: 20px;
}

.copyright {
  color: #999;
  font-size: 13px;
  text-align: center;
}

>>> .el-card__body {
  padding: 0;
}

.login-card /deep/ .el-alert__content {
  padding: 5px;
}

.icon-guanwang-F{
  width: 20px;
  height: 20px;
}
</style>
