<template>
  <el-popover popper-class="header-popper" :visible-arrow="false" trigger="hover">
    <el-link slot="reference" :underline="false">{{$i18n.locale | langValue }}</el-link>
    <template>
      <ul class="i18n-select-ul">
        <li v-show="$i18n.locale!='zh-CN'" @click="handleSetLanguage('zh-CN')">简体</li>
        <li v-show="$i18n.locale!='zh-TW'" @click="handleSetLanguage('zh-TW')">繁體</li>
        <li v-show="$i18n.locale!='en-US'" @click="handleSetLanguage('en-US')">En</li>
      </ul>
    </template>
  </el-popover>
</template>

<script>
export default {
  name: "lang-select",
  computed: {
    language() {
      return localStorage.getItem("lang") || "zh-CN";
    }
  },
  methods: {
    handleSetLanguage(lang) {
      this.$i18n.locale = lang;
      this.$validator.locale = lang;

      // 日期控件格式化
      if ("zh-CN" == lang) {
        // moment.locale('zh-cn');
      } else {
        // moment.locale('en');
      }

      localStorage.setItem("lang", lang);
      this.$message({
        message: "success",
        type: "success"
      });
    }
  },
  filters: {
    langValue: val => {
      let resultVal = val;
      if ("zh-CN" == val) {
        resultVal = "中";
      }
      if ("zh-TW" == val) {
        resultVal = "繁";
      }
      if ("en-US" == val) {
        resultVal = "En";
      }
      return resultVal;
    }
  }
};
</script>

<style scoped>
.el-popover{

}
.el-link {
  border:2px solid #5e5e5e;
  border-radius: 5px;
  padding: 3px;
  margin-right: 20px;
  font-size: 9px;
  width: 10px;
  height:10px
}

.i18n-select-ul {
  list-style: none;
  padding: 0;

}
.i18n-select-ul > li {
  padding: 10px;
  cursor: pointer;
}
.i18n-select-ul > li:hover {
  background: #f7f7f7;
}
</style>