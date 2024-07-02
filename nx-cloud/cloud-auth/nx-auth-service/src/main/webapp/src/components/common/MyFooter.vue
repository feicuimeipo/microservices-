<template>
  <div class="layout">
    <div  class="left"></div>
    <div class="center">
<!--      Copyright© 2020~2022 念熹科技-->
    </div>
    <div class="right"></div>
  </div>
</template>

<script>

</script>

<style scoped lang="scss">
.layout{
  display: flex;
  float: right;
  flex-direction: row;
  position: fixed;
  color: $font-color;
  height: 60px;
  bottom: 0px;
  right:0px;
  width: 100vw;
}

.left{
  width: 50px;
}

.center{
  width: calc(100% - 100px);
}

.right{
  width: 50px;
}
</style>
