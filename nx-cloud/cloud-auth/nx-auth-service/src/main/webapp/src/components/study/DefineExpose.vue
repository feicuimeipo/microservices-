

<script setup lang="ts">
//vue也有自带的获取DOM的方法，那就是ref。它不仅可以获取DOM元素还可以获取组件。
import {defineExpose} from "vue";

const handleNodeClick = () =>{
  console.log('子组件要执行的方法')
}

//将方法暴露出
defineExpose({
  handleNodeClick
})


</script>

<style scoped>

</style>
