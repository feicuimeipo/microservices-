<template>
  <div class="login-register-card" >

     <el-link @click="toRegister" class="go-to-register" :underline="false">注册</el-link>
     <div style="height: 20px"></div>
    <el-tabs  v-model="activeName" class="login-tabs"  @tab-click="handleTabClick">
      <el-tab-pane label="帐号登录" name="accountLogin" class="login-tabs">
        <div class="login-form">
          <el-form :model="accountFormData" ref="accountFormRef" :rules="accountFormRules" >
            <el-form-item prop="account">
              <el-input prefix-icon="UserFilled" placeholder="帐号/手机号/邮箱" onkeyup="this.value.replace(/[^a-zA-Z0-9_@]/g,'');"  v-model="accountFormData.account"></el-input>
            </el-form-item>
            <el-form-item  prop="password">
              <el-input prefix-icon="Key" type="password" placeholder="请输入密码" v-model="accountFormData.password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <button-with-slide-verify :verify-type="VerifyType.accountLogin" @success="onAccountSubmitClick" label="登录" placement="top"/>
              <div style="width: 98%;text-align: center">
                <div style="position: relative;float: left"> <el-checkbox label="记住我" v-model="accountFormData.rememberMe"></el-checkbox></div>
                <div></div>
                <div style="position: relative;float: right"><el-link :underline="false">忘记密码</el-link></div>
              </div>
            </el-form-item>
          </el-form>

        </div>

      </el-tab-pane>
      <el-tab-pane label="手机号登录" name="mobileLogin"  @tab-click="handleTabClick">
        <div class="login-form">
          <el-form  :model="mobileFormData" ref="mobileFormRef" :rules="mobileFormRules">
            <el-form-item>
              <mobile-sms-verify :verify-type="VerifyType.mobileLogin" placement="top" @updateMobileNo="updateMobileNo" @updateSmsVerifyCode="updateSmsVerifyCode" rowSpace="22px"/>
            </el-form-item>
            <el-form-item prop="agreePrivacyPolicy">
              <el-input type="hidden" v-model="mobileFormData.mobileNo"/>
              <el-input type="hidden" v-model="mobileFormData.smsVerifyCode"/>
              <el-checkbox v-model="mobileFormData.agreePrivacyPolicy"><el-space>我已阅读并同意&nbsp; <ServiceAndPricyPolicy /></el-space></el-checkbox>
              <el-button type="primary" @click="onMobileSubmitClick"  class="submitBtn">登录</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="扫码登录" name="qrCodeLogin" disabled></el-tab-pane>
    </el-tabs>
    <div v-show="data.privacyPolicyBottomShow" class="privacyPolicy">
      <ServiceAndPricyPolicy />
    </div>
  </div>

</template>

<!--
window.postMessage() 方法可以安全地实现跨源通信。
通常，对于两个不同页面的脚本，只有当执行它们的页面位于具有相同的协议（通常为https），端口号（443为https的默认值），
以及主机 (两个页面的模数 Document.domain设置为相同的值) 时，这两个脚本才能相互通信。
window.postMessage() 方法提供了一种受控机制来规避此限制，只要正确的使用，这种方法就很安全。
-->

<script lang="ts" setup>
import {onMounted, reactive, ref} from "vue";
import {VerifyType} from "@/packages/slide-verify/vertify-utils";
import {toRouterLinkByParam} from "@/utils";
import type {FormInstance, FormRules, TabsPaneContext} from "element-plus";
import {type AccountLogin, type MobileLogin, useAuthStore} from "@/stores/auth-store";
import ButtonWithSlideVerify from "@/packages/slide-verify/ButtonWithSlideVerify.vue";
import MobileSmsVerify from "@/packages/slide-verify/MobileSmsVerify.vue";
import {ElMessage} from "element-plus/es";


const data = reactive({
  privacyPolicyBottomShow: true
 });

const accountFormData = reactive<AccountLogin>({
  account:"",
  password:"",
  rememberMe: false,
  sliderVerityResult: null,
});

const mobileFormData = reactive<MobileLogin>({
  mobileNo: '',
  smsVerifyCode: '',
  agreePrivacyPolicy: false,
});

const accountFormRules = reactive<FormRules>({
  account:[
    { required: true, message: '请输入帐号/手机号/邮箱', trigger: 'blur' },
  ],
  password:[
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
});

const mobileFormRules = reactive<FormRules>({
  mobileNo:[
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3456789]\d{9}$/, message:'请输入正确的手机号码', trigger: ['blur', 'change'] },
  ],
  smsVerifyCode:[
    { required: true, message: '手机验证码不可以为空', trigger: 'blur' },
  ],
  agreePrivacyPolicy:[
    { required: true, message: '手机验证码不可以为空', trigger: 'blur' },
  ],
})

const updateMobileNo = (mobileNo:string) =>{
  mobileFormData.mobileNo = mobileNo;
  console.log("updateMobileNo="+mobileNo)
}


const updateSmsVerifyCode = (smsVerifyCode:string) =>{
  mobileFormData.smsVerifyCode = smsVerifyCode;
  console.log("updateSmsVerifyCode="+smsVerifyCode)
}

const toRegister = () => {
  return toRouterLinkByParam("/register");
}


//帐号登录
const accountFormRef=ref<FormInstance>();
const mobileFormRef = ref<FormInstance>()
const authStore = useAuthStore();
const onAccountSubmitClick = (val:any) =>{
  const obj = val;
  console.log("obj="+JSON.stringify(obj));

  //点击
  accountFormRef.value?.validate(valid => {
    if (valid) {
      console.log('submit!')
      authStore.accountLogin(accountFormData).then(res=>{
        toRouterLinkByParam("/loginSuccess");
      }).catch(err=>{
        ElMessage.error(err.message);
      });
    } else {
      console.log('error submit!')
      return false
    }
  })

}


const onMobileSubmitClick = (event:Event) => {
  const obj = mobileLoginSubmitBtnRef.value?.getResultValue();
  console.log("objobj="+JSON.stringify(obj));

  //点击
  mobileFormRef.value?.validate(valid => {
    if (valid) {
      console.log('submit!')
      authStore.mobileLogin(mobileFormData).then(res=>{
        toRouterLinkByParam("/loginSuccess");
      }).catch(err=>{
        ElMessage.error(err.message);
      });
    } else {
      console.log('error submit!')
      return false
    }
  })
}

/**
 * tab相关
 */
const activeName = ref("accountLogin")
const handleTabClick = (tab: TabsPaneContext, event: Event) => {
  console.log("activeName="+activeName.value+",tab="+tab.paneName+",name="+tab.props.name);
  if (tab.paneName === "accountLogin"){
    data.privacyPolicyBottomShow = true;
  }else{
    data.privacyPolicyBottomShow= false;
  }
}
onMounted(()=> {
  if (authStore.isLogin) {
    toRouterLinkByParam("/loginSuccess")
  }
})

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
