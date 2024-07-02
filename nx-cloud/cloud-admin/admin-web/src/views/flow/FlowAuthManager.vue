<template>
  <div class="fullheight">
    <ht-table
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      quick-search-props="authorizeDesc"
      :show-export="false"
      ref="htTable"
    >
      <template v-slot:toolbar>
        <el-button-group>
          <el-button size="small" @click="showDialog()" icon="el-icon-plus"
            >添加</el-button
          >
          <ht-delete-button
            url="${bpmModel}/flow/defAuthorize/v1/del"
            :htTable="$refs.htTable"
            >删除</ht-delete-button
          >
        </el-button-group>
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column prop="id" label="主键" :sortable="true" hidden />
        <ht-table-column
          prop="authorizeDesc"
          label="权限描述"
          :sortable="true"
          :show-overflow-tooltip="true"
        >
          <template v-slot="{ row, column, $index }">
            <el-link
              type="primary"
              @click="handleCommand({ row: row, command: 'edit' })"
              title="点击编辑"
              >{{ row.authorizeDesc }}</el-link
            >
          </template>
        </ht-table-column>
        <ht-table-column prop="creator" label="创建人" :sortable="true" />
        <ht-table-column prop="createTime" label="创建时间" :sortable="true" />
      </template>
    </ht-table>

    <el-dialog
      width="1040px"
      :title="(authId ? '编辑' : '添加') + '分管授权'"
      :visible="dialogVisible"
      :before-close="handleClose"
      :destroy-on-close="true"
    >
      <form v-form data-vv-scope="editFlowAuthForm">
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr>
              <th width="140px" class="is-required">权限描述:</th>
              <td>
                <ht-input
                  v-model="flowAuth.authorizeDesc"
                  validate="required"
                />
              </td>
            </tr>
            <tr>
              <th width="140px" class="is-required">权限类型</th>
              <td>
                <el-checkbox
                  v-for="(key, value) in authorizeTypes"
                  v-model="authorizeTypes_[value]"
                  :key="value"
                  >{{ key }}</el-checkbox
                >
              </td>
            </tr>
            <tr>
              <th width="140px" class="is-required">授权人员名称:</th>
              <td>
                <el-table :data="calOwnerNameJson">
                  <el-table-column prop="title">
                    <template slot="header">
                      <el-button
                        type="primary"
                        size="mini"
                        icon="el-icon-plus"
                        @click="showAuthDialog"
                        >选择</el-button
                      >权限分类
                    </template>
                  </el-table-column>
                  <el-table-column prop="name" label="授权给"></el-table-column>
                  <!-- <el-table-column prop="name" label="实例查询权限范围" >
                    <template v-slot="{ row, column, $index }">
                      <eip-org-selector
                        v-if="row.type != 'everyone'"
                        v-model="row.authOrgName"
                        :config="{id:'calOwnerNameJson['+$index+'].authOrg'}"
                        append-to-body
                      />
                    </template>
                  </el-table-column>-->
                </el-table>
              </td>
            </tr>
            <tr>
              <th width="140px" class="is-required">授权流程类型</th>
              <td>
                <ht-radio
                  v-model="flowAuth.multiple"
                  :options="[
                    { key: '1', value: '流程' },
                    { key: '2', value: '分类' }
                  ]"
                  validate="required"
                ></ht-radio>
              </td>
            </tr>
            <tr v-show="flowAuth.multiple == '1'">
              <th width="140px" class="is-required">授权流程名称:</th>
              <td>
                <el-table :data="defNameJson">
                  <el-table-column prop="defName" width="200">
                    <template slot="header">
                      <el-button
                        type="primary"
                        size="mini"
                        icon="el-icon-plus"
                        @click="showFlowDialog"
                        >选择</el-button
                      >流程名称
                    </template>
                  </el-table-column>
                  <el-table-column label="授权内容">
                    <el-table-column :render-header="renderLastHeader">
                      <template v-slot="{ row, column, $index }">
                        <div v-show="authorizeTypes_.management">
                          定义(
                          <el-checkbox v-model="row.right.m_edit"
                            >编辑</el-checkbox
                          >
                          <el-checkbox v-model="row.right.m_del"
                            >删除</el-checkbox
                          >
                          <el-checkbox v-model="row.right.m_start"
                            >启动</el-checkbox
                          >
                          <!-- <el-checkbox v-model="row.right.m_set">设置</el-checkbox> -->
                          <el-checkbox v-model="row.right.m_clean"
                            >清除数据</el-checkbox
                          >)
                        </div>
                        <div v-show="authorizeTypes_.instance">
                          实例(
                          <el-checkbox v-model="row.right.i_del"
                            >删除</el-checkbox
                          >)
                        </div>
                      </template>
                    </el-table-column>
                  </el-table-column>
                  <el-table-column label="操作" width="100">
                    <template v-slot="{ row, column, $index }">
                      <el-button
                        type="danger"
                        @click="defNameJson.remove(defNameJson[$index])"
                        icon="el-icon-delete"
                        >删除</el-button
                      >
                    </template>
                  </el-table-column>
                </el-table>
              </td>
            </tr>
            <tr v-show="flowAuth.multiple == '2'">
              <th width="140px" class="is-required">授权流程分类名称:</th>
              <td>
                <el-table :data="defAllNameJson">
                  <el-table-column prop="defName" width="200">
                    <template slot="header">
                      <el-button
                        type="primary"
                        size="mini"
                        icon="el-icon-plus"
                        @click="showSysTypeDialog"
                        >选择</el-button
                      >流程分类名称
                    </template>
                  </el-table-column>
                  <el-table-column label="授权内容">
                    <el-table-column :render-header="renderLastHeader">
                      <template v-slot="{ row, column, $index }">
                        <div v-show="flowAuth.authorizeTypes.management">
                          定义(
                          <el-checkbox v-model="row.right.m_edit"
                            >设计</el-checkbox
                          >
                          <el-checkbox v-model="row.right.m_del"
                            >删除</el-checkbox
                          >
                          <el-checkbox v-model="row.right.m_start"
                            >启动</el-checkbox
                          >
                          <el-checkbox v-model="row.right.m_set"
                            >设置</el-checkbox
                          >
                          <el-checkbox v-model="row.right.m_clean"
                            >清除数据</el-checkbox
                          >)
                        </div>
                        <div v-show="flowAuth.authorizeTypes.instance">
                          实例(
                          <el-checkbox v-model="row.right.i_del"
                            >删除</el-checkbox
                          >)
                        </div>
                      </template>
                    </el-table-column>
                  </el-table-column>
                  <el-table-column label="操作" width="100">
                    <template v-slot="{ row, column, $index }">
                      <el-button
                        type="danger"
                        @click="defAllNameJson.remove(defAllNameJson[$index])"
                        icon="el-icon-delete"
                        >删除</el-button
                      >
                    </template>
                  </el-table-column>
                </el-table>
              </td>
            </tr>
          </tbody>
        </table>
      </form>
      <div slot="footer" class="dialog-footer">
        <ht-submit-button
          url="${bpmModel}/flow/defAuthorize/v1/save"
          :model="flowAuth"
          :is-submit="isSubmit"
          scope-name="editFlowAuthForm"
          @before-save-data="beforeSaveData"
          @after-save-data="afterSaveData"
          >{{ $t("eip.common.save") }}</ht-submit-button
        >
        <el-button @click="dialogCancle('dialogVisible')">{{
          $t("eip.common.cancel")
        }}</el-button>
      </div>
    </el-dialog>
    <!-- 选择对话框  -->
    <eip-auth-dialog
      ref="eipAuthDialog"
      name="eipAuthDialog"
      @onConfirm="dialogOnConfirm"
      append-to-body
    />
    <!-- 流程选择对话框  -->
    <eip-flow-dialog
      ref="eipFlowDialog"
      name="eipFlowDialog"
      @onConfirm="dialogOnConfirm"
      append-to-body
    />

    <!-- 分类对话框  -->
    <eip-sys-type-dialog
      ref="flowTypeDialog"
      name="flowTypeDialog"
      show-checkbox
      type-key="FLOW_TYPE"
      @onConfirm="dialogOnConfirm"
    ></eip-sys-type-dialog>

    <ht-load-data ref="htLoadData"></ht-load-data>
  </div>
</template>
<script>
import request from "@/request.js";
import { Base64 } from "js-base64";
import utils from "@/hotent-ui-util.js";
const eipAuthDialog = () => import("@/components/dialog/EipAuthDialog.vue");
const eipOrgSelector = () => import("@/components/selector/EipOrgSelector.vue");
const eipFlowDialog = () => import("@/components/dialog/EipFlowDialog.vue");
const eipSysTypeDialog = () =>
  import("@/components/dialog/EipSysTypeDialog.vue");

export default {
  components: {
    eipAuthDialog,
    eipOrgSelector,
    eipFlowDialog,
    eipSysTypeDialog
  },
  computed: {},
  data() {
    return {
      dialogVisible: false,
      dialogVisibleMenuPerm: false,
      flowAuth: {
        authorizeTypes: { start: true },
        defAllNameJson: [],
        defNameJson: [],
        multiple: "1"
      },
      authorizeTypes_: { start: true },
      authorizeTypes: {
        start: "启动",
        management: "定义",
        task: "任务",
        instance: "实例"
      },
      calOwnerNameJson: [],
      defAllNameJson: [],
      defNameJson: [],
      isSubmit: true,
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      loadDataUrl: "",
      authId: ""
    };
  },
  mounted() {
    this.$validator = this.$root.$validator;
  },
  methods: {
    renderLastHeader(h) {
      //下拉框选项
      let managementFilters = [
        { key: "m_edit", value: "编辑", falseLabel: "un_m_edit" },
        { key: "m_del", value: "删除", falseLabel: "un_m_del" },
        { key: "m_start", value: "启动", falseLabel: "un_m_start" }
      ];
      if (this.flowAuth.multiple == 2) {
        managementFilters.push({
          key: "m_set",
          value: "设置",
          falseLabel: "un_m_set"
        });
      }
      managementFilters.push({
        key: "m_clean",
        value: "清除数据",
        falseLabel: "un_m_clean"
      });
      let instanceFilters = [
        { key: "i_del", value: "删除", falseLabel: "un_i_del" }
      ];
      return h("div", {}, [
        h(
          "span",
          {
            //div里面有一个文字提示：多选所属内容
            style: {}
          },
          this.appendHtml(1)
        ),
        [
          managementFilters.map(item => {
            if (this.authorizeTypes_.management) {
              return h("el-checkbox", {
                props: {
                  trueLabel: item.key,
                  falseLabel: item.falseLabel,
                  label: item.value
                },
                on: {
                  change: this.updateAllSelected // 选中事件
                }
              });
            }
          })
        ],
        h("span", {}, this.appendHtml(2)),
        h("div", {}, ""),
        h("span", {}, this.appendHtml(3)),
        [
          instanceFilters.map(item => {
            if (this.authorizeTypes_.instance) {
              return h("el-checkbox", {
                props: {
                  trueLabel: item.key,
                  falseLabel: item.falseLabel,
                  label: item.value
                },
                on: {
                  change: this.updateAllSelected // 选中事件
                }
              });
            }
          })
        ],
        h("span", {}, this.appendHtml(4))
      ]);
    },
    appendHtml(op) {
      if (op == 1 && this.authorizeTypes_.management) {
        return "定义(";
      } else if (op == 2 && this.authorizeTypes_.management) {
        return ")";
      } else if (op == 3 && this.authorizeTypes_.instance) {
        return "实例(";
      } else if (op == 4 && this.authorizeTypes_.instance) {
        return ")";
      }
      return "";
    },
    updateAllSelected(oValue) {
      let res = true;
      if (oValue.indexOf("un_") != -1) {
        res = false;
      }
      var name = oValue.replace("un_", "");
      if (this.flowAuth.multiple == 1) {
        this.defNameJson.forEach(item => {
          item.right[name] = res;
        });
      } else {
        this.defAllNameJson.forEach(item => {
          item.right[name] = res;
        });
      }
    },
    showSysTypeDialog() {
      this.$refs.flowTypeDialog.showDialog();
    },
    showAuthDialog() {
      let conf = {
        right: [],
        permissionList: [
          { type: "everyone", title: "所有人" },
          { type: "user", title: "用户" },
          { type: "org", title: "组织" },
          { type: "pos", title: "岗位" },
          { type: "role", title: "角色" }
          // ,{ type: "none", title: "无" }
        ]
      };
      if (this.calOwnerNameJson) {
        conf.right = this.calOwnerNameJson;
      }
      this.$refs.eipAuthDialog.showDialog(conf);
    },
    dialogOnConfirm(data, name) {
      if (name == "eipAuthDialog") {
        this.calOwnerNameJson = data;
      }
      if (name == "eipFlowDialog") {
        data.forEach(element => {
          let isEquals = false;
          this.defNameJson.forEach(item => {
            if (!isEquals && item.defKey == element.defKey) {
              isEquals = true;
            }
          });

          if (!isEquals) {
            let defaultRight = {
              m_edit: false,
              m_del: false,
              m_start: false,
              m_set: false,
              m_clean: false,
              i_del: false,
              i_log: false
            };
            this.defNameJson.push({
              defName: element.name,
              defKey: element.defKey,
              right: defaultRight
            });
          }
        });
      }

      if (name == "flowTypeDialog") {
        data.forEach(element => {
          let isEquals = false;
          this.defAllNameJson.forEach(item => {
            if (!isEquals && item.defKey == element.id) {
              isEquals = true;
            }
          });

          if (!isEquals) {
            let defaultRight = {
              m_edit: false,
              m_del: false,
              m_start: false,
              m_set: false,
              m_clean: false,
              i_del: false,
              i_log: false
            };
            this.defAllNameJson.push({
              defName: element.name,
              defKey: element.id,
              right: defaultRight
            });
          }
        });
      }
    },
    showFlowDialog() {
      this.$refs.eipFlowDialog.showDialog();
    },
    handleClose() {
      this.dialogVisible = false;
    },
    handleCloseMenuPerm() {
      this.dialogVisibleMenuPerm = false;
    },
    showDialog(row) {
      this.dialogVisible = true;
      const me = this;
      if (row) {
        this.authId = row.id;
        this.$refs.htLoadData
          .loadData(
            "${bpmModel}/flow/defAuthorize/v1/defAuthorizeGet?id=" + row.id
          )
          .then(data => {
            me.flowAuth = data;
            me.authorizeTypes_ = JSON.parse(data.authorizeTypes);
            me.flowAuth.authorizeTypes = me.authorizeTypes_;
            me.defNameJson = JSON.parse(data.defNameJson);

            // right 转为对象
            me.defNameJson.forEach(item => {
              if (item.right && item.right.constructor == String) {
                item.right = JSON.parse(item.right);
              }
            });

            me.defAllNameJson = JSON.parse(data.defAllNameJson);

            // right 转为对象
            me.defAllNameJson.forEach(item => {
              if (item.right && item.right.constructor == String) {
                item.right = JSON.parse(item.right);
              }
            });

            me.calOwnerNameJson = JSON.parse(data.ownerNameJson);
          });
      } else {
        this.authId = "";
        me.flowAuth = {
          authorizeTypes: { start: true },
          defAllNameJson: [],
          defNameJson: [],
          multiple: "1"
        };
        me.defNameJson = [];
        me.defAllNameJson = [];
        me.calOwnerNameJson = [];
        me.authorizeTypes_ = { start: true };
      }
    },
    dialogCancle(dialogVisible) {
      this[dialogVisible] = false;
    },
    loadData(param, cb) {
      request
        .post("${bpmModel}/flow/defAuthorize/v1/listJson", param)
        .then(response => {
          this.data = response.data.rows;
          this.pageResult = {
            page: response.data.page,
            pageSize: response.data.pageSize,
            total: response.data.total
          };
        })
        .finally(() => cb());
    },
    handleCommand(params) {
      switch (params.command) {
        case "edit":
          this.showDialog(params.row);
          break;
        default:
          break;
      }
    },
    async beforeSaveData() {
      this.isSubmit = true;

      let isSelectType = false;
      for (const type in this.authorizeTypes_) {
        if (this.authorizeTypes_[type]) {
          isSelectType = true;
        }
      }
      if (!isSelectType) {
        this.$message.error("请选择权限类型");
        this.isSubmit = false;
        return;
      }

      if (this.calOwnerNameJson.length == 0) {
        this.$message.error("请选择授权人员");
        this.isSubmit = false;
        return;
      }

      if (this.flowAuth.multiple == "1") {
        if (this.defNameJson.length == 0) {
          this.$message.error("请选择授权流程");
          this.isSubmit = false;
          return;
        }
        this.flowAuth.defAllNameJson = [];
      } else {
        if (this.defAllNameJson.length == 0) {
          this.$message.error("请选择授权分类");
          this.isSubmit = false;
          return;
        }
        this.flowAuth.defNameJson = [];
      }
      this.flowAuth.ownerNameJson = JSON.stringify(this.calOwnerNameJson);

      this.flowAuth.defNameJson = JSON.stringify(this.defNameJson);
      this.flowAuth.defAllNameJson = JSON.stringify(this.defAllNameJson);
      this.flowAuth.authorizeTypes = JSON.stringify(this.authorizeTypes_);
    },
    afterSaveData() {
      this.dialogVisible = false;
      this.$refs.htTable.load();
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
