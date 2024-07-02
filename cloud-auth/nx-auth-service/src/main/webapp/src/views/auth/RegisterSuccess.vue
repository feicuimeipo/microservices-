<template>
  <div>
    <el-space><div>注册成功，转到 <el-link @click="toLogin" class="go-to-login" :underline="false">登录</el-link> 页，进行登录操作！</div></el-space>
  </div>
</template>

<script lang="ts" setup>
import {toRouterLinkByParam} from "@/utils";
const toLogin = () => {
  return toRouterLinkByParam("/login");
}

</script>

<style scoped>

</style>
