<!--父向子传数据-->
<template>
  <h5> {{ msg }}</h5>
  <ul>
    <div v-for="(item,index) in ageList" :key=index>
      <li><h5>{{item}}</h5></li>
    </div>
  </ul>
</template>

<script setup lang="ts">
  const props = withDefaults(defineProps<{
    msg:string,
    ageList: Array<number>,
  }>(),{
    msg: "hello",
    ageList: ()=>[1,2]
  })
</script>

<style scoped>

</style>
