<template>
<!--  <button @click="increment">{{ count }}</button>-->
  <el-button @click="increment">{{ count }}</el-button>
</template>

<script setup lang="ts">

import {useStore} from "@/stores";
import {ref} from "vue";

const store = useStore();
const count = ref(store.doubleCount)

const  increment = () =>{
  store.increment();
  count.value = store.doubleCount
  //store.commit("increment")

}

</script>

<style scoped>

</style>
