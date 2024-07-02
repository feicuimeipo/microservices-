<template>
  <div class="fullheight">
    <ht-table
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      quick-search-props="name,alias"
      ref="columnTable"
      :show-export="false"
    >
      <template v-slot:toolbar>
        <el-button-group>
          <el-button
            size="small"
            @click="handleCommand({ command: 'add' })"
            icon="el-icon-plus"
            >添加</el-button
          >
          <ht-delete-button :url="deleteUrl" :htTable="$refs.columnTable"
            >删除</ht-delete-button
          >
        </el-button-group>
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column prop="id" label="主键" :sortable="true" hidden />
        <ht-table-column
          prop="name"
          label="栏目名称"
          :sortable="true"
          :show-overflow-tooltip="true"
        >
          <template v-slot="{ row }">
            <el-link
              @click="handleCommand({ row: row, command: 'edit' })"
              type="primary"
              title="点击编辑"
              >{{ row.name }}</el-link
            >
          </template>
        </ht-table-column>
        <ht-table-column prop="alias" label="别名" :sortable="true" />
        <ht-table-column prop="dataMode" label="数据加载方式" :sortable="true">
          <template v-slot="{ row }">
            <el-tag v-if="row.dataMode == 1" type="info">自定义查询方式</el-tag>
            <el-tag v-if="row.dataMode == 2" type="success"
              >webservice方法</el-tag
            >
            <el-tag v-if="row.dataMode == 3" type="primary">RESTful接口</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column
          prop="dataFrom"
          label="方法路径"
          :sortable="true"
          :show-overflow-tooltip="true"
        />
        <ht-table-column
          prop="isPublic"
          label="栏目使用平台"
          :filters="[
            { text: '管理端', value: 0 },
            { text: '手机端', value: 1 },
            { text: '应用端', value: '2' }
          ]"
        >
          <template v-slot="{ row }">
            <el-tag v-if="row.isPublic == 0" type="info">管理端</el-tag>
            <el-tag v-if="row.isPublic == 1" type="success">手机端</el-tag>
            <el-tag v-if="row.isPublic == 2" type="warning">应用端</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column prop="tenantId" label="数据类型" :sortable="true" >
          <template v-slot="{row}">
            <el-tag v-if="row.tenantId!=-1" type="warning">私有数据</el-tag>
            <el-tag v-if="row.tenantId==-1" type="success">平台公用数据</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column width="150" label="操作">
          <template v-slot="{ row }">
            <el-dropdown
              size="mini"
              split-button
              @command="handleCommand"
              @click="handleCommand({ row: row, command: 'preview' })"
            >
              <span> <i class="icon icon-monitor"></i>预览 </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  icon="el-icon-menu"
                  :command="{ row: row, command: 'auth' }"
                  >展示授权</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </ht-table-column>
      </template>
    </ht-table>

    <el-dialog
      width="55%"
      top="1vh"
      :title="sidebarTitle"
      :visible="dialogVisible"
      :before-close="handleClose"
      :close-on-click-modal="false"
    >
      <el-form v-model="column" data-vv-scope="editForm">
        <el-row>
          <el-col :span="12">
            <ht-form-item label="栏目名称" prop="name" label-width="100px">
              <ht-input
                class="ht"
                v-model="column.name"
                autocomplete="off"
                :validate="{ required: true }"
                placeholder="请输入名称"
              ></ht-input>
            </ht-form-item>

          </el-col>
          <el-col :span="12">
            
        <ht-form-item label="栏目别名" prop="code" label-width="100px">
          <ht-input
            class="ht"
            v-model="column.alias"
            v-pinyin="column.name"
            @change="removeStyle"
            name="columnAlias"
            autocomplete="off"
            :validate="
              'required:true|isExist:${portal}/portal/sysIndexColumn/sysIndexColumn/v1/getByAlias?alias=,' +
                (column.id || '')
            "
            placeholder="请输入别名"
            :disabled="column.id ? true : false"
          ></ht-input>
        </ht-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">

        <ht-form-item label="栏目分类" label-width="100px">
          <eip-sys-type-selector
            typeKey="INDEX_COLUMN_TYPE"
            v-model="column.catalogName"
            :sys-type-id.sync="column.catalog"
          ></eip-sys-type-selector>
        </ht-form-item>
          </el-col>
          <el-col :span="12">

        <ht-form-item label="栏目类型" label-width="100px">
          <ht-select
            v-model="column.colType"
            :disabled="column.id ? true : false"
            class="ht"
            :validate="{ required: true }"
            :options="[
              { key: 0, value: '一般类型栏目' },
              { key: 1, value: '图表类型栏目' }
            ]"
          ></ht-select>
        </ht-form-item>
          </el-col>
        </el-row> 
        <ht-form-item
          label="选择图表类型"
          v-if="column.colType == 1"
          label-width="100px"
        >
          <ht-radio
            v-model="column.chartType"
            :options="[
              { key: 1, value: '流程统计分析报表' },
              { key: 2, value: '自定义图表' }
            ]"
          />
        </ht-form-item>
        <ht-form-item
          label="选择图表"
          v-if="column.colType == 1"
          label-width="100px"
          style="display:block;"
        >
          <eip-chart-selector
            :dataParam.sync="column.dataParam"
            :chartType="column.chartType"
          ></eip-chart-selector>
        </ht-form-item>
        <ht-form-item
          label="更多路径"
          v-if="column.colType != 1"
          label-width="100px"
        >
          <ht-input
            autocomplete="off"
            v-model="column.colUrl"
            class="ht"
            style="width:350px;"
          ></ht-input>
        </ht-form-item>
        <ht-form-item
          label="数据加载方式"
          v-if="column.colType != 1"
          label-width="100px"
        >
          <el-input
            v-model="column.dataFrom"
            autocomplete="off"
            placeholder="请输入方法路径"
            :readonly="column.dataMode===1"
          >
            <el-select
              v-model="column.dataMode"
              slot="prepend"
              placeholder="请选择"
              style="width:150px;"
            >
              <el-option label="自定义查询方式" :value="1"></el-option>
              <el-option label="webservice方法" :value="2"></el-option>
              <el-option label="RESTful接口" :value="3"></el-option>
            </el-select>
            <el-button
              type="primary"
              v-if="
                column.dataMode == 3 ||
                  column.dataMode == 2 ||
                  column.dataMode == 0
              "
              @click="showSetParamDialog()"
              slot="append"
              >参数设置</el-button
            >
            <el-button
              type="primary"
              v-if="column.dataMode == 1"
              icon="el-icon-search"
              @click="selectQuery()"
              slot="append"
              >选 择</el-button
            >
          </el-input>
        </ht-form-item>
        <ht-form-item
          label="请求类型"
          v-if="column.dataMode == 3 && column.colType != 1"
          label-width="100px"
        >
          <ht-radio
            v-model="column.requestType"
            :options="[
              { key: 'POST', value: 'POST' },
              { key: 'GET', value: 'GET' }
            ]"
          />
        </ht-form-item>
        <ht-form-item
          label="栏目使用平台"
          label-width="100px"
          style="display:block;"
        >
          <ht-radio
            v-model="column.isPublic"
            v-if="column.isPublic ? column.isPublic : (column.isPublic = '0')"
            :options="[
              { key: '0', value: '管理端' },
              { key: '1', value: '手机端' },
              { key: '2', value: '应用端' }
            ]"
          />
        </ht-form-item>
        <el-row>
          <el-col :span="12">

        <ht-form-item label="栏目高度" label-width="100px">
          <ht-input
            v-model="column.colHeight"
            type="number"
            placeholder="输入高度"
          ></ht-input
          >(单位：px)
        </ht-form-item>
          </el-col>
          <el-col :span="12">
        <ht-form-item
          label="首页分页"
          v-if="column.colType != 1 || column.chartType != 2"
          label-width="100px"
        >
          <ht-radio
            v-model="column.needPage"
            v-if="column.needPage ? column.needPage : (column.needPage = '0')"
            :options="[
              { key: '0', value: '不分页' },
              { key: '1', value: '分页' }
            ]"
          />
        </ht-form-item>

          </el-col>
        </el-row> 
        <ht-form-item label="描述" label-width="100px">
          <ht-input v-model="column.memo"></ht-input>
        </ht-form-item>
        <ht-form-item
          label="栏目模版"
          v-if="column.colType != 1 || column.chartType != 2"
          label-width="100px"
        >
          <codemirror
            ref="mycode"
            v-model="column.templateHtml2"
            :options="cmOptions"
            class="code"
          ></codemirror>
        </ht-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <ht-submit-button
          request-method="POST"
          :url="saveUrl"
          :model="tempSaveObj"
          :is-submit="isSubmit"
          scope-name="editForm"
          @before-save-data="beforeSaveData"
          @after-save-data="afterSaveData"
          >{{ $t("eip.common.save") }}</ht-submit-button
        >
        <el-button @click="handleClose">{{
          $t("eip.common.cancel")
        }}</el-button>
      </div>
    </el-dialog>
    <ht-load-data
      :url="loadDataUrl"
      context="portal"
      @after-load-data="afterLoadData"
    ></ht-load-data>
    <index-column-setparam
      ref="indexColumnSetparam"
      :set-params="column.dataParam"
      @handleDataparamSave="handledataParamSave"
    ></index-column-setparam>

    <el-dialog
      class="form-editor-dialog"
      destroy-on-close
      :visible="previewShow"
      :before-close="() => (previewShow = false)"
    >
      <ht-column :column-alias="previewAlias" v-if="previewShow" :fromPreview="true"/>
    </el-dialog>

    <eip-auth-dialog @onConfirm="columnAuthConfirm" ref="columnAuth" />
    <custom-dialog-dialog @onConfirm="selectedCustomDialog" ref="customDialogDialog" :single="true" styleType="0"/>
  </div>
</template>
<script>
import { mapState } from "vuex";
import portal from "@/api/portal.js";
import sysType from "@/api/sysType.js";
import utils from "@/hotent-ui-util.js";
import IndexColumnSetparam from "@/components/portal/IndexColumnSetparam.vue";
import EipChartSelector from "@/components/selector/EipChartSelector.vue";
import EipSysTypeSelector from "@/components/selector/EipSysTypeSelector.vue";
import HtColumn from "@/components/common/HtColumn.vue";
const eipAuthDialog = () => import("@/components/dialog/EipAuthDialog.vue");
import CustomDialogDialog from "@/components/dialog/CustomDialogDialog.vue";

let Base64 = require("js-base64").Base64;

import { codemirror } from "vue-codemirror";
import "codemirror/theme/ambiance.css";
require("codemirror/mode/javascript/javascript");
export default {
  components: {
    IndexColumnSetparam,
    EipChartSelector,
    EipSysTypeSelector,
    HtColumn,
    codemirror,
    eipAuthDialog,
    CustomDialogDialog
  },
  data() {
    return {
      sidebarTitle: "",
      dialogVisible: false,
      previewShow: false,
      column: {},
      blankObj: {
        name: "",
        alias: "",
        memo: "",
        catalog: "",
        catalogName: "",
        colType: "",
        dataMode: "",
        dataFrom: "",
        dataParam: "",
        dsAlias: "",
        colHeight: "",
        colUrl: "",
        templateHtml: "",
        isPublic: 0,
        supportRefesh: "",
        refeshTime: "",
        showEffect: "",
        requestType: "",
        needPage: 0,
        templateHtml2: ""
      },
      isSubmit: true,
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      loadDataUrl: "",
      previewAlias: "",
      cmOptions: {
        value: "",
        mode: "vue",
        readOnly: false,
        smartIndent: true,
        tabSize: 2,
        theme: "base16-light",
        lineNumbers: true,
        line: true
      },
      curAuthColumnId: "",
      tempSaveObj: {}
    };
  },
  mounted() {
    this.$validator = this.$root.$validator;
  },
  computed: {
    deleteUrl: function() {
      return (
        window.context.portal +
        "/portal/sysIndexColumn/sysIndexColumn/v1/remove"
      );
    },
    saveUrl: function() {
      return (
        window.context.portal + "/portal/sysIndexColumn/sysIndexColumn/v1/save"
      );
    },
    codemirror() {
      return this.$refs.mycode.codemirror;
    },
    ...mapState({
      currentUser: state => state.login.currentUser
    })
  },
  methods: {
    preview(alias) {
      this.previewAlias = alias;
      this.previewShow = true;
    },
    handleClose() {
      this.dialogVisible = false;
      this.loadDataUrl = "";
    },
    afterLoadData(data) {
      if (this.dialogVisible) {
        if (data.colType == 1) {
          data.chartType = 1;
        } else if (data.colType == 4) {
          data.colType = 1;
          data.chartType = 2;
        }
        this.column = { ...this.blankObj, ...data };
        this.column.templateHtml2 = Base64.decode(this.column.templateHtml);
        this.column.isPublic = "" + this.column.isPublic;
        this.column.needPage = "" + this.column.needPage;
        this.tempSaveObj = this.column;
        setTimeout(() => this.$validator.validateAll("editForm"));
      }
    },
    showDialog(row) {
      this.dialogVisible = true;
      if (row != undefined && row.id != "") {
        this.tempSaveObj = row;
        this.loadDataUrl = `/portal/sysIndexColumn/sysIndexColumn/v1/getJson?id=${
          row.id
        }`;
      } else {
        this.tempSaveObj = this.column;
      }
    },
    loadData(param, cb) {
      portal
        .getIndexColumnPage(param)
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
        case "edit":
          this.sidebarTitle = "编辑栏目";
          this.showDialog(params.row);
          break;
        case "add":
          this.column = JSON.parse(JSON.stringify(this.blankObj));
          this.sidebarTitle = "添加栏目";
          this.showDialog();
          break;
        case "preview":
          if (params.row.isPublic == 2) {
            window.open(
              window.context.front +
                "/column/preview/" +
                params.row.alias +
                "?token=" +this.currentUser.token,
                // Base64.encode(this.currentUser.account),
              "_blank"
            );
          } else {
            this.previewAlias = params.row.alias;
            this.previewShow = true;
          }
          break;
        case "auth":
          this.columnAuth(params.row);
          break;
      }
    },
    async beforeSaveData() {
      let check = new RegExp("[\u4e00-\u9fa5]");
      let pattern = new RegExp(
        "[%--`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——| {}【】‘；：”“'。，、？]"
      );
      if (check.test(this.column.alias) || pattern.test(this.column.alias)) {
        this.$message({ message: "请勿输入中文或特殊字符", type: "warning" });
        document.getElementsByName("columnAlias")[0].style.border =
          "1px red solid";
        document.getElementsByName("columnAlias")[0].focus();
        this.isSubmit = false;
      } else if(this.column.colType===0 && (this.column.dataMode===2||this.column.dataMode===3) &&  !this.column.templateHtml2){
        this.$message({message: "请填写栏目模板", type: "warning"});
        this.isSubmit = false;
      } else {
        this.column.templateHtml = Base64.encode(this.column.templateHtml2);
        this.tempSaveObj = this.column;
        if (this.tempSaveObj.chartType == 2) {
          this.tempSaveObj.colType = 4;
        }
        this.isSubmit = true;
      }
    },
    afterSaveData() {
      this.dialogVisible = false;
      this.$refs.columnTable.load();
    },
    showSetParamDialog() {
      this.$refs.indexColumnSetparam.showDialog();
    },
    handledataParamSave(val) {
      this.column.dataParam = val;
    },
    columnAuth(row) {
      let this_ = this;
      this.curAuthColumnId = row.id;
      this.$http
        .get(
          "${portal}/sys/authUser/v1/getRightsAndDefaultRightType?id=" +
            row.id +
            "&objType=indexColumn"
        )
        .then(function(resp) {
          if (resp.data) {
            let conf = {
              right: resp.data.right,
              permissionList: resp.data.type,
              autoClose: false
            };
            this_.$refs.columnAuth.showDialog(conf);
          }
        });
    },
    columnAuthConfirm(data) {
      let param = {
        id: this.curAuthColumnId,
        objType: "indexColumn",
        ownerNameJson: JSON.stringify(data)
      };
      let this_ = this;
      this.$http
        .post("${portal}/sys/authUser/v1/saveRights", param)
        .then(function(resp) {
          if (resp.data) {
            if (resp.data.state) {
              this_.$message.success("授权成功");
              this_.$refs.columnAuth.closeDialog();
            } else {
              this_.$message.error(resp.data.message);
            }
          }
        });
    },
    selectQuery() {
      this.$refs.customDialogDialog.showDialog();
    },
    selectedCustomDialog(rsl){
      this.column.dataFrom= JSON.stringify(rsl[0]);
    },
    removeStyle() {
      document.getElementsByName("columnAlias")[0].style.border = "";
    }
  }
};
</script>
<style scoped>
div[aria-invalid="true"] >>> .el-input__inner,
div[aria-invalid="true"] >>> .el-input__inner:focus {
  border-color: #f56c6c;
}
.el-select .el-input {
  width: 400px;
}

.code >>> .CodeMirror {
  font-family: monospace;
  height: 100px !important;
  color: black;
  direction: ltr;
}
</style>
