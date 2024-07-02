<template>
  <el-container>
    <el-main>
      <el-row v-for="(grid, i) in gridList" :key="i" :gutter="grid.options.gutter">
        <div v-if="grid.type=='grid'">
          <el-col v-for="(columns, j) in grid.columns" :Key="j" :span="columns.span">
            <ht-column v-for="(cl, k) in columns.list" :key="k" :column-alias="cl.alias"/>
          </el-col>
        </div>
        <div v-if="!grid.type">
          <el-col :span="24">
            <ht-column :column-alias="grid.alias" />
          </el-col>
        </div>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import portal from "@/api/portal.js";
let Base64 = require("js-base64").Base64;
import HtColumn from "@/components/common/HtColumn.vue";
export default {
  components: { HtColumn },
  data() {
    return {
      layout: {},
      gridList: []
    };
  },
  created() {
    portal.getHomeLayout().then(data => {
      this.layout = JSON.parse(Base64.decode(data.value) || "{}");
      if (this.layout && this.layout.list && this.layout.list.length > 0) {
        this.gridList = this.layout.list;
      }
    });
  },
  methods: {}
};
</script>