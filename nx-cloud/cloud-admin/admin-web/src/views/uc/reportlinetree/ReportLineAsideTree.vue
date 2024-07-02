<template>
  <div style="width: 100%">
    <el-aside
      class="fullheight"
      :width="width"
      style="border-right: 1px solid #eee"
      v-show="asideShow"
    >
      <report-line-tree
        class="ht-sys-type-tree"
        :type-id="typeId"
        :type-key="typeKey"
        highlight-current
        ref="htSysTypeTree"
        :support-filter="supportFilter"
        :default-expand-all="isExpand"
        :show-checkbox="showCheckbox"
        expand-on-click-node="false"
        @node-click="handleNodeClick"
        @check="check"
      >
        <span class="custom-tree-node" slot-scope="{ node, data }">
          <span class="show-ellipsis" :title="node.label">{{ node.label }}</span>
          <el-dropdown @command="handleCommand">
            <span>
              <i class="icon-more" title="更多操作" />
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                icon="el-icon-plus"
                v-if="data.children.length<1 || data.parentId != -1"
                :command="{node:node,data,action:'add'}"
              >添加汇报线</el-dropdown-item>
              <el-dropdown-item
                icon="el-icon-edit"
                v-if="data.parentId!='-1'"
                :command="{node:node,data,action:'edit'}"
              >编辑</el-dropdown-item>
              <el-dropdown-item
                icon="el-icon-delete"
                v-if="data.parentId!='-1'"
                :command="{node:node,data,action:'delete'}"
              >删除</el-dropdown-item>
              <el-dropdown-item
                icon="icon-password-manager"
                v-if="data.parentId!='-1'"
                :command="{node:node,data,action:'distributionAdmin'}"
              >分配管理员</el-dropdown-item>
              <el-dropdown-item
                icon="icon-reload"
                divided
                :command="{node:node,data,action:'refresh'}"
              >刷新</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
      </report-line-tree>
    </el-aside>

    <div
      class="navbar-collapse"
      v-if="hasNavbarCollapse"
      :style="navbarCollapseStyle"
      @click="asideShow=!asideShow"
    >
      <div class="navbar-collapse-bg">
        <i
          class="navbar-collapse-arrow"
          :class="{'el-icon-arrow-left':asideShow,'el-icon-arrow-right':!asideShow}"
        ></i>
      </div>
    </div>
    <!-- 添加汇报线 -->
    <ht-sidebar-dialog
      width="28%"
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      append-to-body
      :before-close="handleClose"
    >
      <table>
        <tr>
          <td>
            <label>用户组类型:</label>
          </td>
          <td>
            <ht-radio v-model="param.type" :options="userType" />
          </td>
        </tr>
        <tr>
          <td v-if="param.type === 'user'">
            <label>用户:</label>
          </td>
          <td v-if="param.type === 'org'">
            <label>组织:</label>
          </td>
          <td v-if="param.type === 'pos'">
            <label>岗位:</label>
          </td>
          <td v-if="param.type === 'role'">
            <label>角色:</label>
          </td>
          <td>
            {{selectName}}
            <el-button icon="icon-add" size="mini" type="primary" @click="select">选择</el-button>
          </td>
        </tr>
      </table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="submit" type="primary">保存</el-button>
        <el-button @click="dialogVisible = false">取 消</el-button>
      </div>
    </ht-sidebar-dialog>

    <ht-sidebar-dialog
      width="28%"
      title="分配管理员"
      :visible="isDistributionAdmin"
      :before-close="handleClose"
    >
      <diastrbution-admin :rel-code="selectRoleAlias" v-if="isDistributionAdmin"></diastrbution-admin>
    </ht-sidebar-dialog>
    <eip-user-dialog ref="eipUserDialog" single @onConfirm="selectUser" />
    <eip-org-dialog ref="eipOrgDialog" single @onConfirm="selectOrg" />
    <eip-post-dialog ref="eipPostDialog" single @onConfirm="selectPost" />
    <eip-role-dialog ref="eipRoleDialog" single @onConfirm="selectRole" />
  </div>
</template>

<script>
import styles from "@/assets/css/element-variables.scss";
import ucApi from "@/api/uc.js";
const diastrbutionAdmin = () =>
  import("@/views/uc/reportlinetree/DiastrbutionAdmin.vue");
const reportLineTree = () =>
  import("@/views/uc/reportlinetree/ReportLineTree.vue");
const eipUserDialog = () => import("@/components/dialog/EipUserDialog.vue");
const eipOrgDialog = () => import("@/components/dialog/EipOrgDialog.vue");
const eipPostDialog = () => import("@/components/dialog/EipPostDialog.vue");
const eipRoleDialog = () => import("@/components/dialog/EipRoleDialog.vue");
export default {
  name: "ht-aside-tree",
  components: {
    reportLineTree,
    eipUserDialog,
    eipOrgDialog,
    eipPostDialog,
    eipRoleDialog,
    diastrbutionAdmin
  },
  props: {
    width: {
      type: String,
      default: "100%"
    },
    typeId: {
      type: String,
      default: ""
    },
    typeKey: {
      type: String,
      default: ""
    },
    supportFilter: {
      type: Boolean,
      default: false
    },
    hasFooter: {
      type: Boolean,
      default: true
    },
    hasNavbarCollapse: {
      type: Boolean,
      default: true
    }
  },
  computed: {
    navbarCollapseStyle: function() {
      if (this.asideShow) {
        return { left: parseInt(this.width) + 16 + "px" };
      }
      return { left: "0px" };
    }
  },
  data() {
    return {
      asideShow: true,
      isExpand: false,
      showCheckbox: false,
      dialogVisible: false,
      sysType: {
        children: [],
        icon: "{}",
        isLeaf: "N",
        struType: 1,
        typeKey: "",
        name: ""
      },
      parentSysType: {
        name: ""
      },
      param: {
        //记录请求参数
        type: "user", // 用户组类型
        value: "",
        parentAlias: "",
        alias: ""
      },
      userType: [
        { key: "user", value: "用户" },
        { key: "org", value: "组织" },
        { key: "pos", value: "岗位" },
        { key: "role", value: "角色" }
      ],
      selectName: "", //当前选择的名称
      dialogType: "",
      isDistributionAdmin: false,
      selectRoleAlias: "",
      dialogTitle: "",
      isEditFirstChange: false
    };
  },
  watch: {
    typeId: function(newVal, oldVal) {
      if (newVal && newVal != oldVal) {
        setTimeout(() => {
          this.$refs.htSysTypeTree.loadData();
        });
      }
    },
    "param.type": function(newVal, oldVal) {
      if (newVal && newVal != oldVal && !this.isEditFirstChange) {
        this.param.value = "";
        this.param.alias = "";
        this.$set(this, "selectName", "");
      } else {
        this.isEditFirstChange = false;
      }
    }
  },
  methods: {
    handleNodeClick(node) {
      this.$emit("node-click", node);
    },
    check(data, checkedObj) {
      this.$emit("check", data, checkedObj);
    },
    syncTree() {
      this.$refs.htSysTypeTree.loadData();
    },
    handleExpand() {
      this.isExpand = !this.isExpand;
      let elTree = this.$refs.htSysTypeTree.$refs.htTree.$refs.elTree;
      for (var i = 0; i < elTree.store._getAllNodes().length; i++) {
        elTree.store._getAllNodes()[i].expanded = this.isExpand;
      }
    },
    handleCommand(command) {
      let node = command.node;
      let data = command.data;
      let action = command.action;
      switch (action) {
        case "add":
          this.dialogType = "add";
          this.dialogTitle = "新增汇报线";
          this.showDialog(data, action);
          break;
        case "edit":
          this.dialogType = "edit";
          this.param.id = data.id;
          this.dialogTitle = "编辑汇报线";
          this.showDialog(data, node.parent.data.alias);
          break;
        case "delete":
          this.delSysType(data);
          break;
        case "distributionAdmin":
          this.selectRoleAlias = data.alias;
          this.isDistributionAdmin = false;
          this.$nextTick(() => {
            this.isDistributionAdmin = true;
          });
          break;
        case "refresh":
          this.refresh();
          break;
        default:
          break;
      }
    },
    refresh(){
      this.syncTree();
    },
    showDialog(data, parentAlias) {
      if (this.dialogType === "edit") {
        this.param.parentAlias = data.parentAlias;
        this.param.alias = data.alias;
        this.param.value = data.id;
        this.selectName = data.name;
        this.param.parentAlias = parentAlias;
        if (this.param.type != data.groupType) {
          this.param.type = data.groupType;
          this.isEditFirstChange = true;
        }
      } else {
        this.selectName = "";
        this.param = { parentAlias: data.alias, type: "user" };
      }
      this.dialogVisible = true;
    },
    delSysType(data) {
      this.$confirm(`是否确定删除 [${data.name}] ?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        ucApi
          .deleteUserRel(data.alias)
          .then(
            data => {
              if (data.state) {
                this.$message({
                  type: "success",
                  message: data.message || "删除成功"
                });
                this.$refs.htSysTypeTree.loadData();
              } else {
                this.$message.error(data.message || "删除成功");
              }
            },
            error => {
              this.$message.error(error || "删除失败");
            }
          )
          .catch(() => {
            this.dialogVisible = false;
          });
      });
    },
    handleClose() {
      this.param = { type: "user", value: "", parentAlias: "", alias: "" };
      setTimeout(() => {
        this.$refs.htSysTypeTree.loadData();
      });
      this.dialogVisible = false;
      this.isDistributionAdmin = false;
    },
    afterSaveData() {
      this.$refs.htSysTypeTree.loadData();
      if (this.sysType.id) {
        this.dialogVisible = false;
        return;
      }
      this.sysType.name = "";
      this.sysType.typeKey = "";
      this.$confirm("是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "success"
      })
        .then(() => {})
        .catch(() => {
          this.dialogVisible = false;
        });
    },
    submit() {
      if (this.dialogType === "add") {
        ucApi.addUserRel([this.param]).then(resp => {
          this.dialogVisible = false;
          this.$message({
            message: "新增成功",
            type: "success"
          });
          this.$refs.htSysTypeTree.loadData();
        });
      } else if (this.dialogType === "edit") {
        ucApi.updateUserRel(this.param).then(resp => {
          this.dialogVisible = false;
          this.$message({
            message: "新增成功",
            type: "success"
          });
          this.$refs.htSysTypeTree.loadData();
        });
      }
      this.selectName = ""; //保存成功清楚数据
      this.param = { type: "user", value: "", parentAlias: "", alias: "" };
    },
    select() {
      if (this.param.type === "user") {
        this.$refs.eipUserDialog.showDialog();
      } else if (this.param.type === "pos") {
        this.$refs.eipPostDialog.showDialog();
      } else if (this.param.type === "org") {
        this.$refs.eipOrgDialog.showDialog();
      } else if (this.param.type === "role") {
        this.$refs.eipRoleDialog.showDialog();
      }
    },
    selectUser(data) {
      this.param.value = data[0].id;
      this.param.alias = data[0].account;
      this.selectName = data[0].fullname;
    },
    selectOrg(data) {
      this.param.value = data[0].id;
      this.param.alias = data[0].code;
      this.selectName = data[0].name;
    },
    selectPost(data) {
      this.param.value = data[0].id;
      this.param.alias = data[0].code;
      this.selectName = data[0].name;
    },
    selectRole(data) {
      this.param.value = data[0].id;
      this.param.alias = data[0].code;
      this.selectName = data[0].name;
    }
  }
};
</script>

<style lang="scss" scoped>
>>> .navbar-collapse {
  position: absolute;
  top: 41%;
  cursor: pointer;
  width:20px;
}

>>> .el-dropdown {
  display: none;
}

>>> .el-tree-node__content:hover .el-dropdown {
  display: inline-block;
}

>>> .navbar-collapse:hover {
  transform: scale(1.1) translateY(2px);
}

>>> .navbar-collapse:hover .navbar-collapse-arrow {
  color: #333333;
}

>>> .navbar-collapse-arrow {
  position: absolute;
  top: 38%;
  right: -3px;
  font-size: 18px;
  color: #a8a8a8;
}

>>> .navbar-collapse-bg {
  -webkit-transition: all 0.12s ease;
  height: 50px;
  border-bottom: 8px solid transparent;
  border-right: none;
  border-left: 12px solid #ebebeb;
  border-top: 8px solid transparent;
  opacity: 0.9;
}
.el-aside-footer {
  position: absolute;
  bottom: 7px;
}

>>> .custom-tree-node {
  width: calc(100% - 40px);
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.custom-tree-node i {
  visibility: hidden;
  font-weight: bold;
}

.custom-tree-node:hover i {
  visibility: visible;
}
</style>
