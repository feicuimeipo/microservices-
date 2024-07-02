<script  setup lang="ts">
import AuthView from "@/views/auth/AuthView.vue";
</script>

<template style="margin:0 auto">
  <!--Config Provider 被用来提供全局的配置选项，让你的配置能够在全局都能够被访问到，Config Provider 使用了-->
  <el-config-provider namespace="ep">
    <AuthView />
  </el-config-provider>
</template>

<style>
#app {
  text-align: center;
  color: var(--el-text-color-primary);
}
</style>
