<template>
  <div class="login-register-card" >
    <div>
<!--   <el-link class="go-to-register" :underline="false">注册</el-link>-->
      <el-link @click="toRegister" class="go-to-register" :underline="false">注册</el-link>
      <div style="height: 20px"></div>
    </div>

    <div>
    <el-tabs  v-model="activeName"  class="login-tabs"  @tab-click="handleTabClick">
      <el-tab-pane label="帐号登录" name="accountLogin" class="login-tabs">
        <div class="login-form">
          <el-form :model="accountLoginForm" ref="accountLoginFormRef" :rules="accountLoginFormRules" >
            <el-form-item>
              <el-input prefix-icon="UserFilled" placeholder="帐号/手机号/邮箱"  v-model="accountLoginForm.account"></el-input>
            </el-form-item>
            <el-form-item>
              <el-input prefix-icon="Key" type="password" placeholder="请输入密码" v-model="accountLoginForm.password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-input type="hidden" v-model="accountLoginForm.sliderVerityResult" ></el-input>
              <el-button type="primary" ref="accountSubmitBtnRef" @click="onAccountSubmitClick" class="submitBtn">登录</el-button>
              <div style="width: 98%;text-align: center">
                <div style="position: relative;float: left"> <el-checkbox label="记住我" v-model="accountLoginForm.rememberMe"></el-checkbox></div>
                <div></div>
                <div style="position: relative;float: right"><el-link :underline="false">忘记密码</el-link></div>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="手机号登录" name="mobileLogin"  @tab-click="handleTabClick">
        <div class="login-form">
          <el-form  :model="mobileLoginForm" ref="mobileLoginFormRef" :rules="mobileLoginFormRules">
            <el-form-item>
              <el-input prefix-icon="Iphone" placeholder="手机号"  v-model="mobileLoginForm.mobilePhone"></el-input>
            </el-form-item>
            <el-form-item>
               <el-space>
                <el-input prefix-icon="Key" style="width: 250px;" placeholder="验证码" v-model="mobileLoginForm.mobileSMS" ></el-input>
                <el-button ref="mobileSMSButtonRef" @click="onMobileSMSClick" class="buttonSMS" text>发送验证码</el-button>
               </el-space>
            </el-form-item>
            <el-form-item>
              <el-input type="hidden" v-model="mobileLoginForm.sliderVerityResult" ></el-input>
              <el-button type="primary" @click="onMobileSubmitClick"  class="submitBtn">登录</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="扫码登录" name="qrCodeLogin" disabled>
        <div class="login-form">
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
  <div class="sliderVerify">
    <el-popover ref="sliderVerifyPopover" :virtual-ref="sliderVerifyBtn"  trigger="click" virtual-triggering :width="330"  placement="bottom">
      <slide-verify  @success="onSliderVerifySuccess"  ref="sliderVerity"  />
    </el-popover>
    <el-button ref="sliderVerifyBtn" @click="onSliderVerifyBtnClick" id="sliderVerifyBtn" style="width: 5px;border:0px solid;background-color: transparent" />
  </div>
    <div v-show="false">
      <iframe ref="ssoIframe" :src="sso"/>
    </div>
    <div class="privacyPolicy"><el-link :underline="false" href="service.html" target="_blank">服务条款</el-link>、<el-link :underline="false" href="policy.html" target="_blank">隐私政策</el-link></div>
  </div>

</template>

<!--
window.postMessage() 方法可以安全地实现跨源通信。
通常，对于两个不同页面的脚本，只有当执行它们的页面位于具有相同的协议（通常为https），端口号（443为https的默认值），
以及主机 (两个页面的模数 Document.domain设置为相同的值) 时，这两个脚本才能相互通信。
window.postMessage() 方法提供了一种受控机制来规避此限制，只要正确的使用，这种方法就很安全。
-->


<script lang="ts" setup>
import {onMounted, reactive, ref, unref} from "vue";
import SlideVerify from "@/packages/slide-verify/SlideVerify.vue";
import {VerifyType} from "@/packages/slide-verify/vertify-utils";
import type {FormInstance, FormRules} from "element-plus";
import {useAuthStore} from "@/stores/auth-store";
import {getAllUrlParam, toRouterLinkByParam} from "@/utils";


const authStore = useAuthStore();
if (authStore.isLogin) {
  const paramMap = getAllUrlParam();
  toRouterLinkByParam("/loginSuccess")
}

const toRegister = () => {
  return toRouterLinkByParam("/register");
}

// export default defineComponent({
//   name: "LoginView",
//
//
// });
//帐号登录
const accountLoginFormRef=ref<FormInstance>();
const accountLoginForm = reactive({
  account:"",
  password:"",
  rememberMe: false,
  sliderVerityResult: "",
})

const accountLoginFormRules = reactive<FormRules>({
  account:[
    { required: true, message: '请输入帐号/手机号/邮箱', trigger: 'blur' },
    { min: 3, max: 100, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  password:[
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
})


const mobileLoginFormRef = ref<FormInstance>()
const mobileLoginForm = reactive({
  mobilePhone: '',
  mobileSMS: '',
  sliderVerityResult: "",
})

const mobileLoginFormRules = reactive<FormRules>({
  mobilePhone:[
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { min: 1, max: 20, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  mobileSMS:[
    { required: true, message: '手机验证码不可以为空', trigger: 'blur' },
    { min: 1, max: 20, message: '请输入手机验证码', trigger: 'blur' },
  ],
})


//验证码相关
const sliderVerifyPopover = ref();
const sliderVerity = ref<{setVerifyType(type:VerifyType):void}>();
const sliderVerifyBtn = ref();

const onSliderVerifyBtnClick = () =>{
  unref(sliderVerifyPopover).popperRef?.delayHide?.();
}

const onAccountSubmitClick = () =>{
  sliderVerity.value?.setVerifyType(VerifyType.accountLogin);
  accountLoginForm.sliderVerityResult="";
  document.getElementById("sliderVerifyBtn")?.click();


}


const onMobileSubmitClick = (event:Event) => {

}

/*
* 验证码
* @param data
*/
const onSliderVerifySuccess = (data:any) =>{
  //console.log("1.验证码识别成功！"+JSON.stringify(data));
  const jsonStr = JSON.stringify(data);
  if (data.verifyType === VerifyType.mobileLogin) {
    mobileLoginForm.sliderVerityResult = jsonStr;
    accountLoginForm.sliderVerityResult = "";
  }else {
    mobileLoginForm.sliderVerityResult = "";
    accountLoginForm.sliderVerityResult = jsonStr;
  }
  unref(sliderVerifyPopover).popperRef?.delayHide?.();
}

/**
 * tab相关
 */
const activeName = ref("accountLogin")
const handleTabClick = () => {
  console.log("");
}


//mount
onMounted(() => {
  sliderVerity.value?.setVerifyType(VerifyType.accountLogin);

});


</script>
<style lang="scss" scoped>
.login-register-card{
  background-image: url($loginRegisterBtn);
  background-repeat: no-repeat;
  background-position: right top;
  //上，右，下，左
  padding: 10px 6px 10px 46px;
  box-shadow: var(--ep-box-shadow-light);
  background-color: white;
  //border: #282828 1px solid;
}

.go-to-register {
  position: relative;
  float: right;
  margin: 0px;
  top: 0;
  color:#282828;
  font-size: 16px;
  font-weight: normal;
  text-decoration: none;
}


.login-form{
  padding: 10px 60px 10px 10px;
  width: 360px;
}

//input{
//  //height:45px;
//  //line-height: 40px;
//  text-space: 2px;
//  font-size: 16px;
//}

.submitBtn{
//  height: 40px;
  width: 100vw;
  background-color: var(--ep-color-primary);
  color: white;
}

.buttonSMS{
 // height: 40px;
  //background-color: var(--ep-color-primary);
  color: var(--ep-color-primary);
  border: 0px;
  width: 50px;

  //font-size: 16px;
}

.verifyPopover{
  padding: 10px 60px 10px 10px;
  width: 360px;
  position: relative;
  float: left;
}

.privacyPolicy{
  position: relative;
  position:absolute;
  bottom: 2rem;
  padding-left: 10px;
  text-align: left;
}

.sliderVerify{
  position: relative;
  position:absolute;
  top: 2rem;
  left: 13rem;
  padding-left: 10px;
  text-align: left;
  opacity:0.5;
}
</style>
