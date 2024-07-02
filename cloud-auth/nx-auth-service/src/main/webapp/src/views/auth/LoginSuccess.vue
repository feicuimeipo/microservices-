<template>
  <div>successFul!</div>
</template>

<script lang="ts" setup>

import {useAuthStore} from "../../stores/auth-store";
import {authApi} from "../../api/authApi";

const authStore = useAuthStore();
if (authStore.isLogin) {
  const authToken = authStore.$state.authToken;
  const expiration = authStore.$state.expiration;
  const returnUrl = authApi.getReturnUr();
  const domain = authApi.getOriginDomain();
  if (domain!=null ){
    const callback = authApi.getCallbackUrl(domain);
    const url = callback + "?returnUrl="+returnUrl + "&authToken="+authToken+"&sign=login&expiration="+expiration
    window.location.href = url;
  }
}

</script>

<style scoped>

</style>
