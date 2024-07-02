<template>
     <vue-ueditor-wrap v-model="inputVal" :config="ueditorConf"></vue-ueditor-wrap>
</template>

<script>

export default {
  name: "ht-ueditor",
  props: {
    config: {
      type: Object,
      default(){
        return {};
      }
    },
    value: {
      type: String,
      default: ""
    },
  },
  computed: {
    inputVal: {
      get: function() {
        return this.value;
      },
      set: function(val) {
        this.$emit("input", val);
      }
    },
    ueditorConf: function() {
      return {...this.defaultConf,...this.config};
    }
  },
  data() {
    return {
      defaultConf : {
        UEDITOR_HOME_URL: './UEditor/'
      },
    };
  }
};
</script>

<style lang="scss" scoped>


</style>