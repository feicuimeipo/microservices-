<template>
  <div class="login-register-card" >
    <div>
      <el-link @click="toLogin" class="go-to-login" :underline="false">登录</el-link>
      <div style="height: 20px"></div>
    </div>

    <el-tabs  v-model="activeName"  class="login-tabs"  @tab-click="handleTabClick">
      <el-tab-pane label="帐号注册" name="accountLogin" class="login-tabs">
        <div class="login-form">
          <el-form :model="accountFormData" ref="accountFormRef" :rules="accountFormRules" >
            <el-form-item>
              <el-input prefix-icon="UserFilled" placeholder="输入登录名"  v-model="accountFormData.account"  onkeyup="this.value.replace(/[^a-zA-Z0-9_@]/g,'');"></el-input>
            </el-form-item>
            <el-form-item>
              <el-input prefix-icon="Key" type="password" placeholder="请输入密码" v-model="accountFormData.password" show-password  onkeyup="this.value.replace(/[^a-zA-Z0-9~!@#$%^&*()_|]/g,'');"></el-input>
            </el-form-item>
            <el-form-item>
              <el-input prefix-icon="Key" type="password" placeholder="再次输入密码" v-model="accountFormData.rePassword" show-password  onkeyup="this.value.replace(/[^a-zA-Z0-9~!@#$%^&*()_|]/g,'');"></el-input>
            </el-form-item>
            <el-form-item>
              <mobile-sms-verify :verify-type="VerifyType.accountRegister" placement="top" @updateMobileNo="updateAccountMobileNo" @updateSmsVerifyCode="updateAccountSmsVerifyCode" rowSpace="22px"/>
              <el-input type="hidden" v-model="accountFormData.mobileNo"></el-input>
              <el-input type="hidden" v-model="accountFormData.smsVerifyCode"></el-input>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="accountFormData.agreePrivacyPolicy"><el-space>我已阅读并同意&nbsp; <service-and-pricy-policy /></el-space></el-checkbox>
              <el-button type="primary" @click="onAccountSubmitClick" class="submitBtn">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="手机号注册" name="mobileLogin"  @tab-click="handleTabClick">
        <div class="login-form">
          <el-form  :model="mobileFormData" ref="mobileFormRef" :rules="mobileFormRules" >
            <el-form-item>
              <el-input type="hidden" v-model="mobileFormData.mobileNo"></el-input>
              <el-input type="hidden" v-model="mobileFormData.smsVerifyCode"></el-input>
              <mobile-sms-verify :verify-type="VerifyType.mobileRegister" placement="top" @updateMobileNo="updateMobileMobileNo" @updateSmsVerifyCode="updateMobileSmsVerifyCode" rowSpace="22px"/>
            </el-form-item>
            <el-form-item prop="agreePrivacyPolicy">
              <el-checkbox v-model="mobileFormData.agreePrivacyPolicy"><el-space>我已阅读并同意&nbsp; <service-and-pricy-policy /></el-space></el-checkbox>
              <el-button type="primary" @click="onMobileSubmitClick"  class="submitBtn">注册</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>

</template>

<!--
window.postMessage() 方法可以安全地实现跨源通信。
通常，对于两个不同页面的脚本，只有当执行它们的页面位于具有相同的协议（通常为https），端口号（443为https的默认值），
以及主机 (两个页面的模数 Document.domain设置为相同的值) 时，这两个脚本才能相互通信。
window.postMessage() 方法提供了一种受控机制来规避此限制，只要正确的使用，这种方法就很安全。
-->


<script lang="ts" setup>
import {reactive, ref, unref} from "vue";
import {VerifyType} from "@/packages/slide-verify/vertify-utils";
import type {FormInstance, FormRules} from "element-plus";
import {toRouterLinkByParam} from "@/utils";
import ServiceAndPricyPolicy from "@/components/common/ServiceAndPricyPolicy.vue";
import {type AccountRegister, authApi, type MobileRegister} from "@/api/authApi";
import MobileSmsVerify from "@/packages/slide-verify/MobileSmsVerify.vue";


const toLogin = () => {
  return toRouterLinkByParam("/login");
}

const accountFormData = reactive<AccountRegister>({
  account:"",
  password:"",
  rePassword:"",
  mobileNo: "",
  smsVerifyCode:"",
  agreePrivacyPolicy: false,
})

const mobileFormData = reactive<MobileRegister>({
  mobileNo: "",
  smsVerifyCode: "",
  agreePrivacyPolicy: false,
})

const  accountFormRules = reactive<FormRules>({
  account:[
    { required: true, message: '请输入帐号/手机号/邮箱', trigger: 'blur' },
    { min: 3, max: 100, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  password:[
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^((0\d{2,3}-\d{7,8})|(1[3584]\d{9}))$/,message: '请输入正确的手机号码或者座机号',trigger: 'blur' },
  ],
  // rePassword:[
  //   { required: true, message: '请输入密码', trigger: 'blur' },
  //   { min: 3, max: 20, message: 'Length should be 3 to 5', trigger: 'blur' },
  // ],
});

const  mobileFormRules = reactive<FormRules>({
  mobileNo:[
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { min: 1, max: 20, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  // smsVerifyCode:[
  //   { required: true, message: '验证码不可以为空', trigger: 'blur' },
  //   { min: 1, max: 20, message: '请输入手机验证码', trigger: 'blur' },
  // ],
});


const updateAccountMobileNo = (mobileNo:string) =>{
  accountFormData.mobileNo = mobileNo;
  console.log("updateMobileNo="+mobileNo)
}

const updateAccountSmsVerifyCode = (smsVerifyCode:string) =>{
  accountFormData.smsVerifyCode = smsVerifyCode;
  console.log("updateSmsVerifyCode="+smsVerifyCode)
}

const updateMobileMobileNo = (mobileNo:string) =>{
  mobileFormData.mobileNo = mobileNo;
  console.log("updateMobileNo="+mobileNo)
}

const updateMobileSmsVerifyCode = (smsVerifyCode:string) =>{
  mobileFormData.smsVerifyCode = smsVerifyCode;
  console.log("updateSmsVerifyCode="+smsVerifyCode)
}

//帐号登录
const accountFormRef=ref<FormInstance>();
const mobileFormRef = ref<FormInstance>()
const accountLoginMobileSMSRef = ref<{getResultValue():any}>();
const mobileLoginMobileSMSRef = ref<{getResultValue():any}>();
const onAccountSubmitClick = (event:Event) =>{
  const obj = accountLoginMobileSMSRef.value?.getResultValue();
  console.log("obj="+JSON.stringify(obj));

  //点击
  accountFormRef.value?.validate(valid => {
    if (valid) {
      console.log('submit!')
      //authStore.accountLogin
      authApi.accountRegister(accountFormData).then(res=>{
          toRouterLinkByParam("/registerSuccess");
      })
    } else {
      console.log('error submit!')
      return false
    }
  })

}

const onMobileSubmitClick = (event:Event) => {
  const obj = mobileLoginMobileSMSRef.value?.getResultValue();
  console.log("obj="+JSON.stringify(obj));
  //点击
  accountFormRef.value?.validate(valid => {
    if (valid) {
      console.log('submit!')

      authApi.mobileRegister(accountFormData).then(res=>{
        toRouterLinkByParam("/registerSuccess");
      })
    } else {
      console.log('error submit!')
      return false
    }
  })
  //authApi.mobileRegister()
}


/**
 * tab相关
 */
const activeName = ref("accountLogin")
const handleTabClick = () => {
  console.log("");
}


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
.go-to-login{
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

input{
  //height:45px;
  //line-height: 40px;
  text-space: 2px;
  font-size: 16px;
}

.submitBtn{
//  height: 40px;
  width: 100vw;
  background-color: var(--ep-color-primary);
  color: white;
}

.buttonSMS{
 // height: 40px;
  //background-color: var(--ep-color-primary);
  //color: var(--ep-color-primary);
  //border: 0px;
  //width: 50px;
  position: relative;
  float: right;
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


</style>
