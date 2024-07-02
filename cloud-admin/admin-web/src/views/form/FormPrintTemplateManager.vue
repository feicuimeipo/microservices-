<template>
  <ht-table
    @load="loadData"
    :data="data"
    quick-search-props="fileName"
    :page-result="pageResult"
    :default-querys="[{ property: 'formKey', value: formKey }]"
    :show-export="false"
    :show-custom-column="false"
    ref="htTable"
  >
    <template v-slot:toolbar>
      <el-button-group>
        <el-upload
          class="upload-demo"
          :action="actionUrl"
          :headers="header"
          :show-file-list="false"
          :on-success="success"
          :on-progress="progress"
          :before-upload="beforeAvatarUpload"
        >
          <el-tooltip placement="top" effect="light">
            <div slot="content">附件格式支持：docx</div>
            <el-button icon="el-icon-plus">word模板</el-button>
          </el-tooltip>
        </el-upload>
      </el-button-group>
      <el-button icon="el-icon-plus" @click="addFormTemplate()">表单模板</el-button>
      <ht-delete-button :url="formDeleteUrl" :htTable="$refs.htTable">删除</ht-delete-button>
    </template>
    <template>
      <ht-table-column type="index" width="50" align="center" label="序号" />
      <ht-table-column
        prop="fileName"
        label="名称"
        :show-overflow-tooltip="true"
      />
      <ht-table-column
        prop="createTime"
        label="创建时间"
        :show-overflow-tooltip="true"
      />
      <ht-table-column prop="isMain" width="80" label="主版本">
        <template v-slot="{ row }">
          <el-tag v-if="row.isMain == 'Y'">是</el-tag>
          <el-tag type="danger" v-else>否</el-tag>
        </template>
      </ht-table-column>
      <ht-table-column prop="printType" width="120" label="打印类型">
        <template v-slot="{ row }">
          <el-tag v-if="row.printType == 'word'" type="success">word套打</el-tag>
          <el-tag v-if="row.printType == 'form'" type="warning">表单模板</el-tag>
        </template>
      </ht-table-column>
      <ht-table-column width="150" label="操作" align="left">
        <template v-slot="{ row }">
          <el-dropdown
            size="mini"
            split-button
            @command="handleCommand"
            @click="handleCommand({ row: row, command: 'download' })"
          >
            <span v-if="row.printType == 'word'"> <i class="el-icon-tickets"></i>下载</span>
            <span v-if="row.printType == 'form'"> <i class="el-icon-tickets"></i>编辑</span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                icon="el-icon-menu"
                :command="{ row: row, command: 'setMainVersion' }"
                v-if="row.isMain == 'N'"
                >设为主版本</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </ht-table-column>
    </template>
    <el-dialog
      width="60%"
      title="添加表单打印模板"
      :visible="dialogSaveCopyVisible"
      :before-close="handleCloseSaveCopy"
      :destroy-on-close="true"
      :append-to-body="true"
    >
      <form v-form data-vv-scope="saveCopyForm">
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr>
              <th width="140px">表单名称:</th>
              <td>{{ oldForm.name }}</td>
              <th width="140px" class="is-required">表单打印模板名称</th>
              <td>
                <ht-input
                  v-model="newForm.name"
                  :validate="{ required: true }"
                />
              </td>
            </tr>
            <tr>
              <th width="140px">表单别名:</th>
              <td>{{ oldForm.formKey }}</td>
              <th width="140px" class="is-required">表单打印模板别名</th>
              <td>
                <ht-input
                  v-model="newForm.formKey"
                  v-pinyin="newForm.name"
                  autocomplete="off"
                  :validate="{
                    required: true,
                    alpha_num: true,
                    isExist: '${form}/form/form/v1/checkKey?key='
                  }"
                  placeholder="请输入别名"
                ></ht-input>
              </td>
            </tr>
            <tr>
              <th width="140px">表单分类:</th>
              <td>{{ oldForm.typeName }}</td>
              <th width="140px" class="is-required">表单打印模板分类</th>
              <td>
                <EipSysTypeSelector
                  placeholder="请选择表单分类"
                  cat-id="7"
                  v-model="newForm.typeName"
                  :sys-type-id.sync="newForm.typeId"
                  :validate="{ required: true }"
                />
              </td>
            </tr>
          </tbody>
        </table>
      </form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="save()">保存</el-button>
        <el-button @click="handleCloseSaveCopy">
          {{ $t("eip.common.cancel") }}
        </el-button>
      </div>
    </el-dialog>
    <el-dialog
      class="form-editor-dialog"
      fullscreen
      :destroy-on-close="true"
      :visible.sync="formEditorDialogVisible"
      :before-close="handleCloseFormEditor"
      :close-on-press-escape="false"
      :append-to-body="true"
    >
      <FormDesigner
        :visible.sync="formEditorDialogVisible"
        :form-id.sync="formId"
        :form-def-id.sync="formDefId"
        :bos="bpmForm.bos"
        :add-bpm-form="bpmForm"
      />
    </el-dialog>
  </ht-table>
</template>
<script>
import { mapState } from "vuex";
import { Loading } from "element-ui";
import utils from "@/hotent-ui-util.js";
import req from "@/request.js";
import form from "@/api/form.js";
const EipSysTypeSelector = () => import("@/components/selector/EipSysTypeSelector.vue");
const FormDesigner = () => import("@/components/form/FormDesigner.vue");

export default {
  name: "FormPrintTemlateManager",
  props: {
    formKey: {
      type: String,
      required: true
    },
    visible: {
      type: Boolean
    },
    formRow:{
      type: Object
    }
  },
  components: {
    EipSysTypeSelector,
    FormDesigner
  },
  data() {
    return {
      dialogSaveCopyVisible: false,
      formEditorDialogVisible: false,
      formId: null,
      formDefId: null,
      bpmForm: {
        formKey: "",
        bos: "",
        desc: ""
      },
      newForm: { id: "", name: "", formKey: "", typeId: "", typeName: "" },
      oldForm:{},
      data: [],
      pageResult: {
        page: 1,
        pageSize: 20,
        total: 0
      },
    };
  },
  computed: {
    ...mapState({
      currentUser: state => state.login.currentUser,
      header: state => {
        return { Authorization: `Bearer ${state.login.currentUser.token}` };
      },
      actionUrl: function() {
        return `${window.context.portal}/system/file/v1/upload`;
      },
      formDeleteUrl: function() {
        return `${window.context.form}/form/printTemplate/v1/removes`;
      }
    })
  },
  mounted() {
    this.$validator = this.$root.$validator;
    this.$root.$emit("resize");
    this.oldForm = this.formRow;
  },
  methods: {
    handleCommand(params) {
      switch (params.command) {
        case "setMainVersion":
          var url =
            "${form}/form/printTemplate/v1/setDefaultVersion?id=" +
            params.row.id +
            "&formKey=" +
            params.row.formKey +
            "&printType=" +
            params.row.printType;
          req.get(url).then(res => {
            this.$message({
              type: "success",
              message: res.data.message
            });
            this.$refs.htTable.load();
          });
          break;
        case "download":
          if(params.row.printType == "word"){
            req.download(
              "${portal}/system/file/v1/downloadFile?fileId=" + params.row.fileId
            );
          }else if(params.row.printType == "form"){
            //编辑表单
            this.bpmForm.bos = [];
            this.formId = params.row.formId;
            this.formDefId = params.row.defId;
            this.formEditorDialogVisible = true;
          }
          break;
        default:
          break;
      }
    },
    progress() {
      Loading.service("文件上传中");
    },
    success(response) {
      let loadingInstance = Loading.service("文件上传中");
      if (response.success) {
        let param = {
          fileId: response.fileId,
          fileName: response.fileName,
          formKey: this.formKey,
          printType: "word"
        };
        req.post("${form}/form/printTemplate/v1/save", param).then(response => {
          this.$message({
            type: "success",
            message: "上传模板成功"
          });
          var param = {
            sorter: [{ direction: "DESC", property: "createTime" }],
            querys: [
              {
                group: "defaultQueryGroup",
                operation: "EQUAL",
                relation: "AND",
                property: "formKey",
                value: this.formKey
              }
            ]
          };
          this.$refs.htTable.load();
        });
      } else {
        this.$message({
          type: "error",
          message: "模板上传失败"
        });
      }
      this.$nextTick(() => {
        // 以服务的方式调用的 Loading 需要异步关闭
        loadingInstance.close();
      });
    },
    beforeAvatarUpload(file) {
      var FileExt = file.name.replace(/.+\./, "");
      if ("docx" != FileExt.toLowerCase()) {
        this.$message({
          type: "warning",
          message: "请上传后缀名为docx的文档"
        });
        return false;
      }
    },
    close() {
      this.$emit("update:visible", false);
    },
    loadData(param, cb) {
      req
        .post("${form}/form/printTemplate/v1/getPrintList", param)
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
    handleClose() {
      this.dialogVisible = false;
    },
    //添加表单模板
    addFormTemplate(){
      this.newForm.id = this.oldForm.id;
      this.dialogSaveCopyVisible = true;
    },
    handleCloseSaveCopy() {
      this.dialogSaveCopyVisible = false;
    },
    save(){
      utils.validateForm(this, "saveCopyForm")
              .then(() =>{
                form.savePrintTemplate(this.newForm).then(resp =>{
                  if(resp.state){
                    this.dialogSaveCopyVisible = false;
                    this.handleCloseSaveCopy();
                    this.$refs.htTable.load();
                  }
                })
              })
              .catch(reason =>{
                let rules = reason.map(obj => {
                  return obj.rule;
                });
                if (rules.includes("isExist")) {
                  this.$message.error("已存在同名模板");
                }
              })
    },
    handleCloseFormEditor() {
      this.formEditorDialogVisible = false;
      this.$refs.htTable.load();
    },
  }
};
</script>

<style lang="scss" scoped>
.form-editor-dialog >>> .el-dialog__header {
  display: none;
}

.form-version__dialog /deep/ > .el-dialog > .el-dialog__body {
  padding: 0 10px;
}

.form-editor-dialog >>> .el-dialog__body {
  padding: 0;
  height: 100%;
}
div >>> .el-form-item__error {
  display: none;
}
</style>
