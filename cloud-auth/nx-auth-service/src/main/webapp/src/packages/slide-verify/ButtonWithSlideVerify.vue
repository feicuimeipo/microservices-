<template>
  <el-button type="primary" ref="accountSubmitBtnRef" @click="onAccountSubmitClick" class="submitBtn">{{props.label}}</el-button>
  <div class="sliderVerify">
    <el-popover ref="sliderVerifyPopover" :virtual-ref="accountSubmitBtnRef"  trigger="click" virtual-triggering :width="330"  :placement="props.placement">
      <slide-verify  @success="onSuccess"  ref="sliderVerityRef"  />
    </el-popover>
 </div>
</template>

<script setup lang="ts">
import {SuccessData, VerifyType} from "@/packages/slide-verify/vertify-utils";
import SlideVerify from "@/packages/slide-verify/SlideVerify.vue";
import {onMounted, reactive, ref, unref} from "vue";

const emit = defineEmits(["success"]);

const props = withDefaults(defineProps<{verifyType: VerifyType,placement:string,label:string}>(),{
  verifyType: VerifyType.default,
  placement: "top",
  label: "提交"
});


const sliderVerifyPopover = ref();
const sliderVerityRef = ref<{setVerifyType(type:VerifyType):void}>();
const accountSubmitBtnRef = ref();

const data = reactive<{verifyCodeObj:SuccessData|null,verifyType:VerifyType,success:true|false|null}>({
  verifyCodeObj:null,
  verifyType: props.verifyType,
  success: null,
})


const onAccountSubmitClick = () =>{
  sliderVerityRef.value?.setVerifyType(props.verifyType);
  data.verifyCodeObj=null;
  unref(sliderVerifyPopover).popperRef?.delayHide?.();
  //点击
}


//得到验证码的值
//得到验证码的值
const getResultValue = () =>{
  return {
    verifyCodeObj: data.verifyCodeObj,
    success: data.success,
  };
}


/*
* 验证码
* @param data
*/
const onSuccess = (val:SuccessData|null) =>{
  //console.log("1.验证码识别成功！"+JSON.stringify(data));
  data.verifyCodeObj=val;
  data.success = true;
  emit("success",{verifyCodeObj:data.verifyCodeObj})
  unref(sliderVerifyPopover).popperRef?.delayHide?.();
}

//mount
onMounted(() => {
  sliderVerityRef.value?.setVerifyType(props.verifyType);
});

defineExpose({getResultValue})
</script>

<style scoped lang="scss">
.submitBtn{
  width: 100vw;
  background-color: var(--ep-color-primary);
  color: white;
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
