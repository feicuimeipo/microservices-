
<script setup lang="ts">
//绑定父子组的事件
 const  emits = defineEmits<{
   (e: "parentClick",data:string):void
 }>();

 const clickEmit = () =>{
   emits('parentClick',"我是子组件,小明！")
 }
</script>

<template>
  <el-button @click="clickEmit">子传父</el-button>
</template>


<style scoped>

</style>
