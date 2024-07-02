<template>
  <div class="fullheight">
    <ht-table
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      :selection="true"
      :default-sorter="[{direction: 'DESC',property: 'updateTime'}]"
      quick-search-props="name,code"
      ref="roleTable"
      :show-custom-column="false"
      :show-export="false"
    >
      <template v-slot:toolbar>
        <el-button-group>
          <el-button size="small" @click="showDialog()" icon="el-icon-plus">添加</el-button>
          <ht-delete-button :url="roleDeleteUrl" :htTable="$refs.roleTable">删除</ht-delete-button>
        </el-button-group>
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column prop="id" label="主键" :sortable="true" hidden />
        <ht-table-column prop="name" label="名称" :sortable="true" :show-overflow-tooltip="true">
          <template v-slot="{row,column,$index}">
            <el-link
              type="primary"
              @click="handleCommand({row:row,command:'assignMenu'})"
              title="点击菜单授权"
            >{{row.name}}</el-link>
          </template>
        </ht-table-column>
        <ht-table-column prop="code" label="编码" :sortable="true" />
        <ht-table-column
          prop="enabled"
          label="状态"
          :filters="[{text:'启用', value:1},{text:'禁用', value:0}]"
        >
          <template v-slot="{row}">
            <el-tag type="success" v-if="row.enabled==1">启用</el-tag>
            <el-tag type="danger" v-else>禁用</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column width="150" label="操作">
          <template v-slot="{row}">
            <el-dropdown
              size="mini"
              split-button
              @command="handleCommand"
              @click="handleCommand({row:row,command:'assignUser'})"
            >
              <span>
                <i class="el-icon-edit"></i>分配用户
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  icon="el-icon-plus"
                  :command="{row:row,command:'copyRoleMenuPerm'}"
                >权限复制</el-dropdown-item>
                <el-dropdown-item icon="el-icon-plus" :command="{row:row,command:'edit'}">编辑</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <!-- <el-dropdown
              size="mini"
              split-button
              @command="handleCommand"
              @click="handleCommand({row:row,command:'methodAuth'})"
            >
              <span>
                <i class="el-icon-edit"></i>接口授权
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item icon="el-icon-user" :command="{row:row,command:'assignUser'}">分配用户</el-dropdown-item>
                <el-dropdown-item
                  icon="el-icon-plus"
                  :command="{row:row,command:'copyRoleMenuPerm'}"
                >权限复制</el-dropdown-item>
                <el-dropdown-item icon="el-icon-plus" :command="{row:row,command:'edit'}">编辑</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown> -->
          </template>
        </ht-table-column>
      </template>
    </ht-table>

    <ht-sidebar-dialog
      width="28%"
      title="添加角色"
      :visible="dialogVisible"
      :before-close="handleClose"
      append-to-body
    >
      <el-form v-model="role" data-vv-scope="editRoleForm">
        <ht-form-item label="角色名称" prop="name" label-width="100px">
          <ht-input
            v-model="role.name"
            autocomplete="off"
            :validate="{required:true,regex: {
                        exp: '^[\\s\\S]{1,30}$',
                        message: '内容超出输入限制'
                      }}"
            placeholder="请输入名称"
          ></ht-input>
        </ht-form-item>
        <ht-form-item label="角色别名" prop="code" label-width="100px">
          <ht-input
            v-model="role.code"
            v-pinyin="role.name"
            autocomplete="off"
            :validate="{required:true,alpha_num:true,regex: {
                        exp: '^[\\s\\S]{1,30}$',
                        message: '内容超出输入限制'
                      }}"
            placeholder="请输入别名"
            :disabled="role.id?true:false"
          ></ht-input>
        </ht-form-item>
        <ht-form-item label="状态" label-width="100px">
          <ht-select
            v-model="role.enabled"
            :options="[{'key': 0, 'value':'禁用'},{'key': 1, 'value':'启用'}]"
            :validate="{'required':true}"
          />
        </ht-form-item>
        <ht-form-item label="描述" label-width="100px">
          <ht-input type="textarea" v-model="role.description" placeholder="请输入描述" />
        </ht-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <ht-submit-button
          :url="saveRoleUrl"
          :model="role"
          :is-submit="isSubmit"
          :request-method="requestMethod"
          scope-name="editRoleForm"
          @before-save-data="beforeSaveData"
          @after-save-data="afterSaveData"
        >{{$t('eip.common.save')}}</ht-submit-button>
        <el-button @click="dialogCancle('dialogVisible')">{{$t('eip.common.cancel')}}</el-button>
      </div>
    </ht-sidebar-dialog>

    <ht-sidebar-dialog
      width="1040px"
      title="菜单权限复制"
      :visible="dialogVisibleMenuPerm"
      :before-close="handleCloseMenuPerm"
      ref="menuPermSidebardialog"
      append-to-body
    >
      <el-form :model="menuPerm" data-vv-scope="RoleMenuPermForm">
        <ht-form-item label="原角色名称" prop="name" label-width="120px">
          <ht-input v-model="menuPerm.name" permission="r"></ht-input>
        </ht-form-item>
        <ht-form-item label="原角色别名" prop="code" label-width="120px">
          <ht-input v-model="menuPerm.code" permission="r"></ht-input>
        </ht-form-item>
        <ht-form-item label="权限复制角色" label-width="120px">
          <ht-select
            v-model="menuPerm.newCodes"
            :options="roleCodesOptions"
            multiple
            :validate="{'required':true}"
          />
        </ht-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <ht-submit-button
          :url="saveRoleCopy"
          :isSubmit="isSubmit"
          scopeName="RoleMenuPermForm"
          @after-save-data="dialogVisibleMenuPerm = false;"
        >{{$t('eip.common.save')}}</ht-submit-button>
        <el-button @click="dialogCancle('dialogVisibleMenuPerm')">{{$t('eip.common.cancel')}}</el-button>
      </div>
    </ht-sidebar-dialog>

    <ht-load-data
      ref="htLoadData"
      :url.sync="loadDataUrl"
      context="uc"
      @after-load-data="afterLoadData"
    ></ht-load-data>
    <!-- 角色用户管理 -->
    <user-role-manager  append-to-body ref="userRoleManager"></user-role-manager>
    <!-- 菜单授权 -->
    <role-menu-auth  append-to-body ref="roleMenuAuth"></role-menu-auth>
    <!-- 接口授权 -->
    <role-method-auth  append-to-body ref="roleMethodAuth"></role-method-auth>
  </div>
</template>
<script>
import uc from "@/api/uc.js";
import utils from "@/hotent-ui-util.js";
import eipUserDialog from "@/components/dialog/EipUserDialog.vue";
const userRoleManager = () => import("@/views/uc/UserRoleManager.vue");
const roleMenuAuth = () => import("@/views/uc/RoleMenuAuth.vue");
const roleMethodAuth = () => import("@/views/uc/RoleMethodAuth.vue");

export default {
  components: {
    eipUserDialog,
    userRoleManager,
    roleMenuAuth,
    roleMethodAuth
  },
  computed: {
    roleDeleteUrl: function() {
      return window.context.uc + "/api/role/v1/role/deleteRoleByIds";
    },
    saveRoleUrl: function() {
      let preUrl = window.context.uc + "/api/role/v1/role";
      if (this.role.id) {
        preUrl += "/updateRole";
      } else {
        preUrl += "/addRole";
      }
      return preUrl;
    },
    requestMethod: function() {
      return this.role.id ? "PUT" : "POST";
    },
    roleCodesOptions: function() {
      let result = [];
      for (let role of this.allowRoles) {
        let obj = {};
        obj.key = role.code;
        obj.value = role.name;
        result.push(obj);
      }
      return result;
    },
    saveRoleCopy: function() {
      return `${window.context.portal}/sys/sysRoleAuth/v1/saveCopy?oldCode=${this.menuPerm.code}&newCodes=${this.menuPerm.newCodes}`;
    }
  },
  data() {
    return {
      dialogVisible: false,
      dialogVisibleMenuPerm: false,
      role: {
        name: "",
        code: "",
        enabled:1
      },
      isSubmit: false,
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      loadDataUrl: "",
      menuPerm: {},
      allowRoles: []
    };
  },
  mounted() {
    this.$validator = this.$root.$validator;
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    handleCloseMenuPerm() {
      this.dialogVisibleMenuPerm = false;
    },
    showDialog(row) {
      this.dialogVisible = true;
      if (row) {
        // this.role = row;
        //setTimeout(() => this.$validator.validateAll());
        this.loadDataUrl = `/api/role/v1/role/getRole?code=${row.code}`;
      } else {
        this.role = { name: "", code: "",enabled:1 };
      }
    },
    dialogCancle(dialogVisible) {
      this[dialogVisible] = false;
    },
    loadData(param, cb) {
      uc.getRolePage(param)
        .then(response => {
          this.data = response.rows;
          this.pageResult = {
            page: response.page,
            pageSize: response.pageSize,
            total: response.total
          };
        })
        .finally(() => cb());
    },
    handleCommand(params) {
      switch (params.command) {
        case "edit":
          this.showDialog(params.row);
          break;
        case "delete":
          break;
        case "assignUser":
          this.$refs.userRoleManager.showDialog(params.row.code);
          break;
        case "assignMenu":
          this.$refs.roleMenuAuth.showDialog(params.row.code);
          break;
        case "methodAuth":
          this.$refs.roleMethodAuth.showDialog(params.row.code);
          break;
        case "copyRoleMenuPerm":
          this.menuPerm = params.row;
          this.dialogVisibleMenuPerm = true;
          this.loadDataUrl = `/api/role/v1/roles/getNotCodeAll?code=${params.row.code}`;
          break;
        default:
          break;
      }
    },
    async beforeSaveData() {
      //  提交前可以改变model中的数据
      // this.role
      // this.isSubmit 是否提交数据到后台
      // this.isSubmit = true  提交数据到后天
      // this.isSubmit = false 不提交
      // this.isSubmit = false;
      // console.log(new Date().getTime());
      // for(let i=0; i<1000000000;i++){

      // }
      //  console.log(new Date().getTime());
      this.isSubmit = true;
    },
    afterSaveData() {
      this.dialogVisible = false;
      this.$refs.roleTable.load();
    },
    afterLoadData(data) {
      // 菜单权限复制
      if (this.dialogVisibleMenuPerm) {
        this.allowRoles = data;
      }
      // 编辑角色
      if (this.dialogVisible) {
        this.role = data.value;
        setTimeout(() => this.$validator.validateAll("editRoleForm"));
      }
    }
  }
};
</script>
<style scoped>
div[aria-invalid="true"] >>> .el-input__inner,
div[aria-invalid="true"] >>> .el-input__inner:focus {
  border-color: #f56c6c;
}
</style>
