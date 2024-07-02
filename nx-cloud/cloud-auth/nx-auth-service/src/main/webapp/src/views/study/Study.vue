<script setup lang="ts" >
import DefineProps from "@/components/study/DefineProps.vue";
import DefineEmits from "@/components/study/DefineEmits.vue";
import SlotsAttrs from "@/components/study/SlotsAttrs.vue";
import DefineExpose from "@/components/study/DefineExpose.vue";
import {onMounted,ref} from "vue";
import Vuex from "@/components/study/Vuex.vue";


const parentClick = (msg:string) => {
  alert(msg);
};


const childExposeRef = ref()
const handleClick = () => {
  childExposeRef.value.handleNodeClick();
}

</script>


<template>
  <div>
    <label>define-props</label>
    <div><DefineProps msg="myHello" :age-list=[1,2,3] /></div>
  </div>
  <hr/>
  <div>
    <label>define-emit</label>
    <div><DefineEmits @parentClick="parentClick"/></div>
  </div>
 <hr/>
  <div>
    <label>slots-attrs</label>
    <div>
      <!--a="1" b="2" msg="hello" class="child" style="color:red" 通过useAttrs获取-->
      <SlotsAttrs a="1" b="2" msg="hello" class="child" style="color:red">
        <template #header>
            <h4>父组件定义的模板信息内容，传入子组件中（通过header参数)</h4>
        </template>
      </SlotsAttrs>
    </div>
  </div>

  <hr/>
  <div>
    <label>defineExpose-父组件得到子组件的属性</label>
    <div>
      <DefineExpose ref="childExposeRef" />
    </div>
  </div>

  <hr/>
  <Vuex />
</template>


<style scoped>

</style>
