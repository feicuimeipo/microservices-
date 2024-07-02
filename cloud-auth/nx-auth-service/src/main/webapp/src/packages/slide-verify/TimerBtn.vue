<template>
<!--<el-button ref="mobileSMSButtonRef" @click="onMobileSMSClick"  type="primary" link text >发送验证码</el-button>-->
  <el-button @click="run" :disabled="data.disabled || data.time > 0"  type="primary"  link text>{{ text }}</el-button>
</template>

<script setup lang="ts">
import {computed, reactive, ref} from "vue";

const data = reactive({
  time:0,
  second: 60,
  disabled: false
})


//const ref = toRef(props.refId)

const emit = defineEmits(["click"]);

const run = () => {
  emit("click");
};

const start= () => {
  data.time = data.second;
  timer();
};
const stop = () =>  {
  data.time = 0;
  data.disabled = false;
};
const setDisabled = (val:boolean) => {
  data.disabled = val;
};

const timer = () => {
  if (data.time > 0) {
    data.time--;
    setTimeout(timer, 1000);
  }else{
    data.disabled = false;
  }
}


const text = computed(() => {return data.time > 0 ? data.time + 's 后重获取' : "获取验证码";})

defineExpose({start,setDisabled,stop})

</script>

<style scoped>

</style>
