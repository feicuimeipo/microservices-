<template>
  <div class="fullheight">
    <ht-table
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      :selection="true"
      quick-search-props="name,memo"
      :default-querys="defaultQuerys"
      :show-export="false"
      ref="layoutTable"
    >
      <template v-slot:toolbar>
        <el-button-group>
          <el-button size="small" @click="addLayout(0)" icon="el-icon-plus" v-if="orgManagerNow.layoutPerms && orgManagerNow.layoutPerms.indexOf('add')>=0">添加管理端布局</el-button>
          <el-button size="small" @click="addLayout(1)" icon="el-icon-plus" v-if="orgManagerNow.layoutPerms && orgManagerNow.layoutPerms.indexOf('add')>=0">添加手机端布局</el-button>
          <el-button size="small" @click="addLayout(2)" icon="el-icon-plus" v-if="orgManagerNow.layoutPerms && orgManagerNow.layoutPerms.indexOf('add')>=0">添加应用端布局</el-button>
          <ht-delete-button :url="deleteUrl" :htTable="$refs.layoutTable" v-if="orgManagerNow.layoutPerms && orgManagerNow.layoutPerms.indexOf('delete')>=0">删除</ht-delete-button>
        </el-button-group>
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column prop="id" label="主键" :sortable="true" hidden />
        <ht-table-column prop="name" label="布局名称" :sortable="true" :show-overflow-tooltip="true">
          <template v-slot="{row}">
            <el-link @click="handleCommand({row:row,command:'edit'})" type="primary" title="点击编辑" v-if="orgManagerNow.layoutPerms && orgManagerNow.layoutPerms.indexOf('edit')>=0">{{row.name}}</el-link>
            <span v-else>{{row.name}}</span>
          </template>
        </ht-table-column>
        <ht-table-column prop="memo" label="布局描述" :sortable="true" :show-overflow-tooltip="true" />
        <ht-table-column
          prop="layoutType"
          label="布局类型"
          :filters="[{text:'管理端',value:0},{text:'手机端',value:1},{text:'应用端',value:2}]"
        >
          <template v-slot="{row}">
            <el-tag v-if="row.layoutType==0" type="info">管理端</el-tag>
            <el-tag v-if="row.layoutType==1" type="success">手机端</el-tag>
            <el-tag v-if="row.layoutType==2" type="warning">应用端</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column
          prop="shareToSub"
          label="共享子部门"
          :filters="[{text:'否',value:0},{text:'是',value:1}]"
        >
          <template v-slot="{row}">
            <el-tag v-if="row.shareToSub==0" type="danger">否</el-tag>
            <el-tag v-if="row.shareToSub==1" type="primary">是</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column
          prop="enable"
          label="是否启用"
          :filters="[{text:'否',value:0},{text:'是',value:1}]"
        >
          <template v-slot="{row}">
            <el-tag v-if="row.enable==0" type="danger">已停用</el-tag>
            <el-tag v-if="row.enable==1" type="primary">已启用</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column label="操作" width="150">
          <template v-slot="{row}">
            <el-dropdown
              size="mini"
              split-button
              @command="handleCommand"
              @click="handleCommand({row:row,command:'preview'})"
            >
              <span>
                <i class="icon icon-monitor"></i>预览
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-if="row.shareToSub==0"
                  :command="{row:row,command:'shareToSub',share:1}"
                ><i class="icon icon-share"></i>共享</el-dropdown-item>
                <el-dropdown-item
                  v-if="row.shareToSub==1"
                  :command="{row:row,command:'shareToSub',share:0}"
                ><i class="icon icon-share"></i>取消共享</el-dropdown-item>
                <el-dropdown-item
                  v-if="row.enable==0"
                  icon="icon-switch"
                  :command="{row:row,command:'enable',enable:1}"
                >启用</el-dropdown-item>
                <el-dropdown-item
                  v-if="row.enable==1"
                  icon="icon-switch"
                  :command="{row:row,command:'enable',enable:0}"
                >停用</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
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

let Base64 = require("js-base64").Base64;
export default {
  components: { PortalDesignerDialog,LayoutPreviewDialog },
  // props: ["orgId"],
  props: {
    orgId:{
      type:String
    },
    orgManagerNow: {  //传入的组织对象 用于判断用户是否有增删改权限
      type: Object
    }
  },
  mounted() {
    console.log(this.orgManagerNow)
  },
  data() {
    return {
      designShow: false,
      sidebarTitle: "",
      dialogVisible: false,
      layout: {
        name: "",
        memo: "",
        templateHtml: "",
        templateHtml2: ""
      },
      data: [],
      pageResult: {
        page: 1,
        pageSize: 30,
        total: 0
      },
      loadDataUrl: ""
    };
  },
  computed: {
    defaultQuerys: function() {
      return [
        {
          property: "orgId",
          value: this.orgId,
          group: "main",
          operation: "EQUAL",
          relation: "AND"
        }
      ];
    },
    deleteUrl: function() {
      return (
        window.context.portal +
        "/portal/sysIndexLayoutManage/sysIndexLayoutManage/v1/remove"
      );
    }
  },
  methods: {
    addLayout(layoutType) {
      if (!this.orgId) {
        this.$message({ message: "请先选择一个组织", type: "warning" });
        return;
      }
      if (layoutType == 0) {
        //管理端
        this.$refs.designDialog.showDialog("", layoutType, this.orgId);
      } else if (layoutType == 1) {
        //手机端
        this.$refs.designDialog.showDialog("", layoutType, this.orgId);
      } else if (layoutType == 2) {
        this.$refs.designDialog.showDialog("", layoutType, this.orgId);
        //应用端
      }
    },
    loadData(param, cb) {
      portal
        .getLayoutManagerPage(param)
        .then(response => {
          this.data = response.rows;
          this.pageResult = {
            page: response.page,
            pageSize: response.pageSize,
            total: response.total
          };
        })
        .finally(() => cb && cb());
    },
    handleCommand(params) {
      switch (params.command) {
        case "preview":
          this.preview(params.row.id);
          break;
        case "shareToSub":
          this.shareToSub(params.row.id, params.share);
          break;
        case "enable":
          this.enable(params.row.id, params.enable);
          break;
        case "edit":
          this.$refs.designDialog.showDialog(
            params.row.id,
            params.row.layoutType
          );
          break;
      }
    },
    shareToSub(id, share) {
      this.$confirm(`${share==1?'确认共享给子部门':'确认取消共享'}吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        portal.setlayoutMngShareToSub(id, share).then(data => {
          if(data.state){
            this.$message({message:data.message,type:'success'});
            this.$refs.layoutTable.load();
          }else{
            this.$message({message:data.message,type:'error'});
          }
        });
      });
    },
    enable(id, enable) {
      this.$confirm(`确定${enable==1?'启用':'停用'}该布局吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        portal.setlayoutMngEnable(id, enable).then(data => {
          if(data.state){
            this.$message({message:data.message,type:'success'});
            this.$refs.layoutTable.load();
          }else{
            this.$message({message:data.message,type:'error'});
          }
        });
      });
    },
    designClose(val) {
      this.$refs.layoutTable.load();
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
a:hover {
  cursor: pointer;
  color: blue;
}
</style>
