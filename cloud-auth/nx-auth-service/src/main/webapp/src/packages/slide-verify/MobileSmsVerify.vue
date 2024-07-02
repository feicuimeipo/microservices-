<template style="display: flex;flex-direction:row">
  <el-input  placeholder="输入手机号" v-model="data.mobileNo" maxlength="11" clearable @blur="onBlueMobile" :style="data.rowSpaceStyle">
    <template #prepend><span style="color:darkgray">+86</span></template>
  </el-input>
  <el-input placeholder="请输入验证码" style="width: 260px;" v-model="data.smsVerifyCode" @blur="onBlueSmsVerifyCode"></el-input>
  <TimerBtn @click="onMobileSMSClick" ref="timeBtnRef" :second="60"/>
  <el-space class="tip">
    <CircleCheck  v-if="data.message.circleCheck"  class="color:green" />
    <CircleClose  v-if="data.message.circleClose"  class="color:red"/>
    <div :v-if="data.message.circleCheck || data.message.circleClose">{{ data.message.text }}</div>
  </el-space>
  <el-button ref="sliderVerifyBtnRef" @click="onClickVerifyBtnBtnClick"  :id="data.sliderVerifyBtnId" style="width: 400px;padding-top:20px;border:0px solid;background-color: transparent;z-index: -10" />
  <div class="sliderVerify">
    <el-popover ref="sliderVerifyPopover" :virtual-ref="sliderVerifyBtnRef"  trigger="click" virtual-triggering :width="330"  :placement="props.placement">
      <slide-verify  @success="onSuccess"  ref="sliderVerityRef"  />
    </el-popover>
  </div>
</template>

<script setup lang="ts">
import {SuccessData, VerifyType, vertifyUtils} from "@/packages/slide-verify/vertify-utils";
import SlideVerify from "@/packages/slide-verify/SlideVerify.vue";
import {onMounted, reactive, ref, unref} from "vue";
import TimerBtn from "@/packages/slide-verify/TimerBtn.vue";
const props = withDefaults(defineProps<{verifyType: VerifyType,placement:string,rowSpace:string}>(),{
  verifyType: VerifyType.default,
  placement: "top",
  rowSpace: "22px",
});

const sliderVerifyPopover = ref();
const sliderVerityRef = ref<{setVerifyType(type:VerifyType):void}>();
const sliderVerifyBtnRef = ref();
const timeBtnRef = ref<{start():void,stop():void,setDisabled(val:boolean):void}>();
enum MessageStatus{
  success,fail,close
}

const data = reactive<{verifyCodeObj:SuccessData|null,mobileNo:string,smsVerifyCode:string,sliderVerifyBtnId:string,rowSpaceStyle:string,success:boolean|null,message:any}>({
  verifyCodeObj:null,
  mobileNo: "",
  smsVerifyCode: "",
  sliderVerifyBtnId: "hiddenSliderVerifyBtn"+props.verifyType,
  success: null,
  rowSpaceStyle: "margin-bottom:"+props.rowSpace,
  message: {
      text: "",
      circleClose: false,
      circleCheck: false,
    },
})




const onMobileSMSClick = () => {
  sliderVerityRef.value?.setVerifyType(VerifyType.mobileLogin);
  data.verifyCodeObj = null;
  document.getElementById(data.sliderVerifyBtnId)?.click();
}

const onClickVerifyBtnBtnClick = () =>{
  unref(sliderVerifyPopover).popperRef?.delayHide?.();
  timeBtnRef.value?.setDisabled(true);
  timeBtnRef.value?.start();
}

//得到验证码的值
const getResultValue = () =>{
  return {
    verifyCodeObj: data.verifyCodeObj,
    mobileNo: data.mobileNo,
    smsVerifyCode: data.smsVerifyCode,
    success: data.success
  };
}


const setMessage =(text:string,status:MessageStatus)=>{
  data.message.text = text;
  data.message.circleClose = false;
  data.message.circleCheck = false;
  if (status===MessageStatus.close){
    data.message.circleClose = true;
  }else{
    data.message.circleCheck = true;
  }
}

/*
* 验证码
* @param data
*/
const onSuccess = (result:SuccessData) =>{
 if (result) {
   data.verifyCodeObj = result;
   unref(sliderVerifyPopover).popperRef?.delayHide?.();
   vertifyUtils.sendSMS(data.verifyCodeObj, data.mobileNo).then((res) => {
     if (res === true) {
       setMessage("短信验证码已发送，可能会有延后，请耐心等待", MessageStatus.success)
     } else {
       setMessage("发送短信验证码失败，请稍后再试！", MessageStatus.fail)
     }
   }).catch(err => {
     setMessage("发送短信验证码失败，请稍后再试！", MessageStatus.fail)
   })
 }else{
   setMessage("发送短信验证码失败，请稍后再试！", MessageStatus.fail)
 }
}

onMounted(() => {
  sliderVerityRef.value?.setVerifyType(props.verifyType);
  setMessage("",MessageStatus.close);
});


const emit = defineEmits(["updateMobileNo","updateSmsVerifyCode"]);
const onBlueMobile = () =>{
  emit("updateMobileNo",data.mobileNo)
}

const onBlueSmsVerifyCode = () =>{
  emit("updateSmsVerifyCode",data.smsVerifyCode)
}



</script>

<style scoped lang="scss">

.sliderVerify{
  position: relative;
  position:absolute;
  top: 2rem;
  left: 13rem;
  padding-left: 10px;
  text-align: left;
  opacity:0.5;
}
.tip{
  padding-top: 5px;
   color: $font-color;
}
</style>
