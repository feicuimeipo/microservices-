<template>
  <div class="fullheight">
    <ht-table
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      :selection="true"
      quick-search-props="name,memo"
      ref="layoutTable"
      :show-export="false"
    >
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column prop="id" label="主键" :sortable="true" hidden />
        <ht-table-column prop="name" label="布局名称" :sortable="true" :show-overflow-tooltip="true"  >
          <template v-slot="{row}">
            <el-link @click="handleCommand({row:row,command:'edit'})" type="primary" title="点击编辑">{{row.name}}</el-link>
          </template>
        </ht-table-column>
        <ht-table-column prop="memo" label="布局描述" :sortable="true" :show-overflow-tooltip='true'/>
        <ht-table-column prop="layoutType" label="布局类型"
              :filters="[{text:'管理端',value:0},{text:'手机端',value:1},{text:'应用端',value:2}]">
          <template v-slot="{row}">
            <el-tag v-if="row.layoutType==0" type="info">管理端</el-tag>
            <el-tag v-if="row.layoutType==1" type="success">手机端</el-tag>
            <el-tag v-if="row.layoutType==2" type="warning">应用端</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column label="操作" width="150">
          <template v-slot="{row}">
            <el-button @click="handleCommand({row:row,command:'preview'})"><i class="icon icon-monitor"></i>预览</el-button>
          </template>
        </ht-table-column>
      </template>
    </ht-table>

    <PortalDesignerDialog ref="designDialog" @close="designClose" />
    <LayoutPreviewDialog ref="previewDialog" />
  </div>
</template>
<script>
import portal from "@/api/portal.js";
import utils from "@/hotent-ui-util.js";
const PortalDesignerDialog = () => import("@/components/portal/PortalDesignerDialog.vue");
const LayoutPreviewDialog = () => import("@/views/portal/LayoutPreviewDialog.vue");
let Base64 = require('js-base64').Base64;
export default {
    components:{PortalDesignerDialog,LayoutPreviewDialog},
    data() {
    return {
      data: [],
      pageResult: {
        page: 1,
        pageSize: 30,
        total: 0
      }
    };
  },
  methods: {
    loadData(param, cb) {
      portal.getDefaultLayoutManagerPage(param)
        .then(response => {
          this.data = response.rows;
          this.pageResult = {
            page: response.page,
            pageSize: response.pageSize,
            total: response.total
          };
        })
        .finally(() => cb&&cb());
    },
    designClose(val){
      this.$refs.layoutTable.load();
    },
    handleCommand(params) {
      switch (params.command) {
        case "preview":
          this.preview(params.row.id);
          break;
        case "edit":
          this.$refs.designDialog.showDialog(params.row.id,params.row.layoutType);
          break;
      }
    },
    preview(id){
      this.$refs.previewDialog.showDialog(id);
    }
  }
};
</script>
<style scoped>
div[aria-invalid="true"] >>> .el-input__inner,
div[aria-invalid="true"] >>> .el-input__inner:focus {
  border-color: #f56c6c;
}
a:hover{
  cursor:pointer;
  color:blue;
}
</style>
