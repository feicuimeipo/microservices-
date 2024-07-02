<template>
  <div class="el-header">
    <div class="left"></div>
    <div  class="logo">
      <el-space>
        <h1>NX</h1>
        <h3>{{ subtitle }}</h3>
      </el-space>
    </div>
    <div class="center"></div>
    <div class="right">
       <el-link :underline="false" href="about.html" target="_blank">关于</el-link>
    </div>
  </div>
</template>

<script setup lang="ts">
  import {toRouterLinkByParam} from "@/utils";
  const props = withDefaults(defineProps<{
    subtitle:string
  }>(),{
    subtitle: ""
  });
  

  const toAbout = () =>{
    toRouterLinkByParam("/toLink")
  }

</script>

<style scoped lang="scss">
.el-header{
  width: 100vw;
  left: 0;
  margin: 0;
  z-index: 999999;
  height: 100vh;
  display: flex;
  position: fixed;
  background-color: white;
  align-items: center;
  vertical-align: middle;
  -webkit-box-shadow:0px 1px 1px #d8d8d8;
  -moz-box-shadow:0px 1px 1px #d8d8d8;
  box-shadow:0px 1px 1px #d8d8d8;
  color: $font-color;
}

.left{
  width: 1rem;
}
.logo{

}

h1{
  font-family: 黑体;
  color: $color-primary;
  font-weight: bolder;
  letter-spacing: 6px;
  font-size: 26px;
}

h3{
  letter-spacing: 0px;
  font-size: 18px;
  color: #222222;
  font-weight: normal;
  font-family: "Microsoft YaHei";
  vertical-align: bottom;
  letter-spacing: 2px;
}


.center{
  width: calc(100% - 500px);
}

.right{
  padding-right: 0px;
  right: 10px;
  width: 300px;
  text-align: right;
}

</style>
