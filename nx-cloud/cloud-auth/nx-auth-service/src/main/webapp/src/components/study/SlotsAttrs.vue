<script setup lang="ts">

import {onMounted, useAttrs, useSlots} from "vue";

const slots = useSlots();

const attrs = useAttrs();

onMounted(()=>{
  //console.log(slots.header && slots.header());
  console.log(attrs)
})
</script>

<template>
  <slot name="header"/>
</template>



<style scoped>

</style>
