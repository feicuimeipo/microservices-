<template>
  <el-container class="fullheight" style="border: 1px solid #eee">
    <ht-aside-tree
      cat-id="12"
      :has-navbar-collapse="false"
      @node-click="handleNodeClick"
      :hasFooter="false"
    />
    <el-container>
      <report-line-aside-tree
        style="width:250px"
        :type-id="typeId"
        :has-navbar-collapse="false"
        v-if="isLineTreeShow"
      />
    </el-container>
  </el-container>
</template>
<script>
import portalApi from "@/api/portal.js";
const reportLineAsideTree = () =>
  import("@/views/uc/reportlinetree/ReportLineAsideTree.vue");
const htAsideTree = () => import("@/components/common/HtAsideTree.vue");
export default {
  components: {
    reportLineAsideTree,
    htAsideTree
  },
  data() {
    return {
      catId: "",
      options: [],
      typeId: "",
      isLineTreeShow: false
    };
  },
  computed: {},
  mounted() {
    // this.handleSelectOptions();
  },
  methods: {
    handleNodeClick(data) {
      console.log(data);
      if (data.parentId == 0) {
        this.$message.warning("请选择具体的汇报线分类");
        return;
      }
      this.isLineTreeShow = true;
      this.typeId = data.id;
    }
  }
};
</script>

<style lang="scss" scoped>
</style>
