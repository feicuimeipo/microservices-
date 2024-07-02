<template>
  <div  class="login-layout" ref="loginLayout">
      <!--背景-->
    <el-container>
      <el-header>
        <MyHeader subtitle="SSO" />
      </el-header>
      <el-main>
        <div class="login-content">
          <div class="market">
            公告通知
          </div>
          <RouterView />
        </div>
      </el-main>
      <el-footer><MyFooter /></el-footer>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
import MyHeader from "@/components/common/MyHeader.vue";
import MyFooter from "@/components/common/MyFooter.vue";



</script>
<style lang="scss" scoped>
.login-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  background-image: url($loginBackGround);
  background-size: cover;
  text-align:center;
}

.el-header{
  width: 100vw;
  height: 60px;
}

.el-footer{
  width: 100vw;
  height: 60px;
}

.el-main{
  height: calc(100vh - 120px);
  width: 100vw;
  vertical-align: middle;
  text-align: center;
}

.login-content{
  position: absolute;
  margin: auto;
  display: flex;
  flex-direction: row;
  top: calc((100vh - 120px - 580px)/2);
  left: calc((100vw - 360px)/2);
  height: 560px;
  //width: 40vw;
}


.market{
  //width: 45vw;
  width: 0vw;
  height: 560px;
  font-size: 16px;
  vertical-align: middle;
  text-align: center;
  background-color: transparent;
  color: transparent;
  display: block;
  //background-color: $base-fill-color;
}


</style>
