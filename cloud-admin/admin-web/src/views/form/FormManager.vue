<template>
  <el-container class="fullheight" style="border: 1px solid #eee">
    <ht-aside-tree cat-id="7" @node-click="handleNodeClick" @check="check" />
    <el-container>
      <el-main>
        <ht-table
          @load="loadData"
          :data="data"
          :pageResult="pageResult"
          :selection="true"
          quick-search-props="name,alias"
          :default-querys="[{ property: 'formType', value: 'pc' }]"
          :default-sorter="[{ direction: 'DESC', property: 'updateTime' }]"
          :show-export="false"
          ref="htTable"
          @select="tableSelect"
          @select-all="tableSelect"
        >
          <template v-slot:toolbar>
            <el-button-group>
              <el-button size="small" @click="openAddDailog" icon="el-icon-plus"
                >添加</el-button
              >
              <ht-delete-button
                style="margin: 0px"
                :url="formDeleteUrl"
                :htTable="$refs.htTable"
                >删除</ht-delete-button
              >
              <el-button
                size="small"
                @click="
                  importDialogVisible = true;
                  formTypeSelectorCatId = '';
                  formTypeSelectorCatName = '';
                "
                style="margin: 0px"
                icon="el-icon-back"
                >导入</el-button
              >
              <el-button size="small" @click="handExport" icon="el-icon-right"
                >导出</el-button
              >

              <el-button @click="openTypeSetDialog()">设置分类</el-button>
            </el-button-group>
          </template>
          <template>
            <ht-table-column
              type="index"
              width="50"
              align="center"
              label="序号"
            />
            <ht-table-column
              prop="name"
              label="名称"
              :sortable="true"
              :show-overflow-tooltip="true"
            >
              <template v-slot="{ row }">
                <el-link
                  @click="handleCommand({ row: row, command: 'edit' })"
                  type="primary"
                  >{{ row.name }}</el-link
                >
              </template>
            </ht-table-column>
            <ht-table-column prop="formKey" label="别名" :sortable="true" />
            <ht-table-column
              prop="status"
              label="状态"
              width="80"
              :filters="[
                { text: '草稿', value: 'draft' },
                { text: '已发布', value: 'deploy' }
              ]"
            >
              <template v-slot="{ row }">
                <el-tag type="info" v-if="row.status == 'deploy'"
                  >已发布</el-tag
                >
                <el-tag type="danger" v-if="row.status == 'draft'">草稿</el-tag>
              </template>
            </ht-table-column>
            <ht-table-column prop="version" label="版本" width="250">
              <template v-slot="{ row }">
                <el-tag
                  type="info"
                  style="margin-left:10px;cursor:pointer"
                  title="查看表单主版本"
                  @click="handleCommand({ row: row, command: 'edit' })"
                  >主版本:{{ row.version }}</el-tag
                >
                <el-tag
                  style="margin-left:10px;cursor:pointer"
                  title="查看表单的所有版本"
                  @click="openFormVersion(row)"
                  type="info"
                  >版本管理({{ row.versionCount }})</el-tag
                >
              </template>
            </ht-table-column>
            <ht-table-column prop="typeName" label="分类" :sortable="true" />
            <ht-table-column prop="desc" label="描述" :hidden="true" />
            <ht-table-column width="150" label="操作">
              <template v-slot="{ row }">
                <el-dropdown
                  size="mini"
                  split-button
                  @command="handleCommand"
                  @click="handleCommand({ row: row, command: 'preview' })"
                >
                  <span> <i class="el-icon-tickets"></i>预览 </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'publish' }"
                      v-if="row.status == 'draft'"
                      >发布</el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'copy' }"
                      >复制</el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'relation' }"
                      >绑定关系</el-dropdown-item
                    >
                    <el-dropdown-item
                      v-if="row.status == 'deploy'"
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'formRight' }"
                      >表单权限</el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'mobileForm' }"
                      >生成手机表单</el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'printForm' }"
                      >设置打印模板</el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'manage' }"
                      >添加为后端菜单</el-dropdown-item
                    >
                    <el-dropdown-item
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'front' }"
                      >添加为前端菜单</el-dropdown-item
                    >
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </ht-table-column>
          </template>
        </ht-table>
      </el-main>
      <FlowFormAuth ref="flowFormAuth" />
    </el-container>
    <!-- 添加表单  -->
    <el-dialog
      width="30%"
      title="新建表单"
      :visible="dialogVisible"
      :before-close="handleClose"
      destroy-on-close
    >
      <el-form :model="bpmForm" data-vv-scope="editBpmForm">
        <ht-form-item label="选择数据模型" label-width="120px">
          <eip-bo-selector
            v-model="bpmForm.bos"
            placeholder="请选择数据模型"
            :validate="{ required: true }"
          ></eip-bo-selector>
        </ht-form-item>

        <ht-form-item label="表单名称" label-width="120px">
          <ht-input
            v-model="bpmForm.formName"
            autocomplete="off"
            :validate="{ required: true }"
            placeholder="请输入名称"
          ></ht-input>
        </ht-form-item>
        <ht-form-item label="表单别名" label-width="120px">
          <ht-input
            v-model="bpmForm.formKey"
            v-pinyin="bpmForm.formName"
            autocomplete="off"
            :validate="{
              required: true,
              alpha_num: true,
              isExist: '${form}/form/form/v1/checkKey?key='
            }"
            placeholder="请输入别名"
          ></ht-input>
          <el-tooltip
            class="item"
            effect="dark"
            content="别名只能填写英文和数字"
            placement="right-start"
          >
            <span class="el-icon-question" style="margin-left: 10px"></span>
          </el-tooltip>
        </ht-form-item>
        <ht-form-item label="表单分类" label-width="120px">
          <EipSysTypeSelector
            placeholder="请选择表单分类"
            cat-id="7"
            v-model="bpmForm.typeName"
            :sys-type-id.sync="bpmForm.typeId"
            :validate="{ required: true }"
          />
        </ht-form-item>
        <ht-form-item label="描述" label-width="120px">
          <ht-input
            type="textarea"
            v-model="bpmForm.desc"
            placeholder="请输入描述"
          />
        </ht-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="toFormDesigner()">下一步</el-button>
        <el-button @click="dialogVisible = false">
          {{ $t("eip.common.cancel") }}
        </el-button>
      </div>
    </el-dialog>
    <el-dialog
      class="form-editor-dialog"
      fullscreen
      destroy-on-close
      :visible="formEditorDialogVisible"
      :before-close="handleCloseFormEditor"
      :close-on-press-escape="false"
    >
      <FormDesigner
        :visible.sync="formEditorDialogVisible"
        :form-id.sync="formId"
        :form-def-id.sync="formDefId"
        :bos="bpmForm.bos"
        :add-bpm-form="bpmForm"
      />
    </el-dialog>
    <el-dialog
      title="表单版本管理"
      class="form-version__dialog"
      width="60%"
      destroy-on-close
      :visible.sync="formVersionVisible"
      :before-close="handleCloseFormVersion"
      top="8vh"
    >
      <FormVersionManager
        v-if="formVersionVisible"
        :visible.sync="formVersionVisible"
        :form-key="formKey"
      />
    </el-dialog>
    <el-dialog
      title="表单打印模板设置"
      class="form-print__dialog"
      width="60%"
      :destroy-on-close="true"
      :visible.sync="formPrintVisible"
      :before-close="handleCloseFormPrint"
      top="8vh"
    >
      <FormPrintTemplateManager
        v-if="formPrintVisible"
        :visible.sync="formPrintVisible"
        :form-key="formKey"
        :form-row="formRow"
      />
    </el-dialog>
    <eip-sys-type-dialog
      ref="typeSetDialog"
      name="typeSetDialog"
      :cat-id="'7'"
      @onConfirm="sysTypeDialogOnConfirm"
    />
    <el-dialog
      width="60%"
      title="复制表单"
      :visible="dialogSaveCopyVisible"
      :before-close="handleCloseSaveCopy"
      :destroy-on-close="true"
    >
      <form v-form data-vv-scope="saveCopyForm">
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr>
              <th width="140px">原表单名称:</th>
              <td>{{ formRow.name }}</td>
              <th width="140px" class="is-required">新表单名称</th>
              <td>
                <ht-input
                  v-model="newForm.name"
                  :validate="{ required: true }"
                />
              </td>
            </tr>
            <tr>
              <th width="140px" class="is-required">原表单别名:</th>
              <td>{{ formRow.formKey }}</td>
              <th width="140px" class="is-required">新表单别名</th>
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
              <th width="140px" class="is-required">原表单分类:</th>
              <td>{{ formRow.typeName }}</td>
              <th width="140px" class="is-required">新表单分类</th>
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
        <ht-submit-button
          url="${form}/form/form/v1/saveCopy"
          :model="newForm"
          scope-name="saveCopyForm"
          @after-save-data="afterSaveData"
          >{{ $t("eip.common.save") }}</ht-submit-button
        >
        <el-button @click="handleCloseSaveCopy">
          {{ $t("eip.common.cancel") }}
        </el-button>
      </div>
    </el-dialog>

    <eip-bo-dialog
      ref="eipBoDialog"
      name="eipBoDialog"
      @onConfirm="handleDialogSure"
      :single="false"
      append-to-body
    />

    <el-dialog
      title="导入表单"
      :visible.sync="importDialogVisible"
      width="40%"
      top="30vh"
      :close-on-click-modal="false"
      v-if="importDialogVisible"
    >
      <div style="height:150px;padding-left: 20px ;">
        <eip-sys-type-selector
          placeholder="请选择分类"
          type-key="FORM_TYPE"
          v-model="formTypeSelectorCatName"
          :sys-type-id.sync="formTypeSelectorCatId"
          :validate="{ required: true }"
        />
        <br /><br />
        <el-upload
          style="display: inline-block;"
          :action="imporCheckUrl"
          :on-success="hadleUploadResult"
          :on-error="hadleUploadResult"
          :headers="uploadHeaders"
          :on-exceed="onExceed"
          accept=".zip"
          :before-upload="beforeUpload"
          :limit="1"
          :data="{ isCheck: true }"
          :auto-upload="false"
          ref="upload"
        >
          <el-button size="small" icon="el-icon-upload">选择表单</el-button>
        </el-upload>

      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="submitImport"
          element-loading-text="拼命导入中"
          v-loading.fullscreen.lock="fullscreenLoading"
          >确 定</el-button
        >
        <el-button @click="importDialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>
    <ht-sidebar-dialog
      width="28%"
      title="绑定关系"
      :visible.sync="dialogVisible2"
      :before-close="handleClose"
    >
      <el-row>
        <el-col>
          实体对象：
          <span v-for="(item, index) in bindData.entData" :key="index">
            <el-tag
              style="margin-right: 5px;cursor: pointer;"
              @click="handleClose"
            >
              <router-link
                :to="{ path: '/form#businessObj', query: { id: item.id_ } }"
                >{{ item.description_ }}</router-link
              >
            </el-tag>
          </span>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col>
          流程实例：
          <span v-for="(item, index) in bindData.bpmData" :key="index">
            <el-tag style="margin-right: 5px" @click="handleClose">
              <router-link
                :to="{ path: 'flowDesign', query: { bpmId: item.def_id_ } }"
                >{{ item.name_ }}</router-link
              >
            </el-tag>
          </span>
        </el-col>
      </el-row>
    </ht-sidebar-dialog>
    <FormMenusAdd ref="formMenusAdd" :formId="formId"/>
  </el-container>
</template>
<script>
    import {mapState} from "vuex";
    import form from "@/api/form.js";
    import req from "@/request.js";
    import utils from "@/hotent-ui-util.js";

    const FormMenusAdd = () => import("@/views/form/FormMenusAdd.vue");
const htAsideTree = () => import("@/components/common/HtAsideTree.vue");
const EipSysTypeSelector = () =>
  import("@/components/selector/EipSysTypeSelector.vue");
const FormDesigner = () => import("@/components/form/FormDesigner.vue");
const EipBoSelector = () => import("@/components/selector/EipBoSelector.vue");
const FlowFormAuth = () => import("@/components/flow/FlowFormAuth.vue");
const FormVersionManager = () => import("@/views/form/FormVersionManager.vue");
const FormPrintTemplateManager = () =>
  import("@/views/form/FormPrintTemplateManager.vue");
const eipSysTypeDialog = () =>
  import("@/components/dialog/EipSysTypeDialog.vue");
  const eipBoDialog = () => import("@/components/dialog/EipBoDialog.vue");

export default {
  components: {
    htAsideTree,
    EipSysTypeSelector,
    FormDesigner,
    EipBoSelector,
    FlowFormAuth,
    FormVersionManager,
    FormPrintTemplateManager,
    eipSysTypeDialog,
    eipBoDialog,
    FormMenusAdd
  },
  data() {
    return {
      asideShow: true,
      treeData: [],
      defaultProps: {
        children: "children",
        label: "name"
      },
      data: [],
      pageResult: {
        page: 1,
        pageSize: 20,
        total: 0
      },
      dialogVisible: false,
      dialogVisible2: false,
      formEditorDialogVisible: false,
      dialogSaveCopyVisible: false,
      formId: null,
      formDefId: null,
      bpmForm: {
        formKey: "",
        bos: "",
        desc: ""
      },
      typeIdQuery: null,
      formVersionVisible: false,
      formPrintVisible: false,
      formKey: null,
      updateTableData: [],
      formRow: {},
      newForm: { id: "", name: "", formKey: "", typeId: "", typeName: "" },
      importDialogVisible: false,
      formTypeSelectorCatId: "",
      formTypeSelectorCatName: "",
      fullscreenLoading: false,
      bindData: []
    };
  },
  watch: {
    formEditorDialogVisible: function(newVal, oldVal) {
      if (newVal == false && oldVal == true) {
        this.handleCloseFormEditor();
      }
    }
  },
  computed: {
    ...mapState({
      currentUser: state => state.login.currentUser,
      formDeleteUrl: function() {
        return `${window.context.form}/form/form/v1/remove`;
      },
      uploadHeaders: function(mapState) {
        return { Authorization: "Bearer " + mapState.login.currentUser.token };
      },
      imporCheckUrl: function(mapState) {
        return (
          window.context.form +
          "/form/form/v1/importCheck?typeId=" +
          this.formTypeSelectorCatId
        );
      }
    })
  },
  created() {
    //判断页面是否从建模过来的
    let bos = this.$route.query.bos;
    //第二种情况是按了F5刷新的,这种也不进入表单编辑页面
    if (bos && bos !== "[object Object]") {
      this.handleDialogSure(bos, true);
    }
  },
  mounted() {
    this.$validator = this.$root.$validator;
    if(this.$route.query.formId && this.$route.query.defId){
      let row = {
        id:this.$route.query.formId,
        defId:this.$route.query.defId
      }
      this.handleCommand({ row: row, command: 'edit' });
      this.$router.push("form#formManager");
    }
  },
  methods: {
    //显示表单添加到菜单dialog
    addToMenu(type){
      this.$refs.formMenusAdd.showDialog(type,"addReport");
    },
    handleDialogSure(data, boo) {
      this.bpmForm.bos = data;
      this.bpmForm.formType = "pc";
      //this.bpmForm.name = this.bpmForm.formName;
      this.formId = "";
      this.formDefId = "";
      this.formEditorDialogVisible = true;
      if (!boo) {
        this.$refs.eipBoDialog.handleClose();
      }
    },
    hadleUploadResult(response, file, fileList) {
      var height =
        (document.documentElement.clientHeight || document.body.clientHeight) *
          0.85 +
        "px";
      this.fullscreenLoading = false;
      let this_ = this;
      if (response.state) {
        this_.handleImportSuccess();
      } else {
        if (
          response.message &&
          response.message.indexOf("是否继续为其新增版本") > 0
        ) {
          this.$confirm(
            '<div style="overflow-x:hidden;overflow-y:auto ;max-height:' +
              height +
              '">' +
              response.message +
              "</div>",
            "提示",
            {
              cancelButtonText: "取消",
              dangerouslyUseHTMLString: true,
              confirmButtonText: "确定",
              type: "warning",
              closeOnClickModal: false
            }
          )
            .then(() => {
              this_.fullscreenLoading = true;
              this_.$http
                .post(
                  `${
                    window.context.form
                  }/form/form/v1/importSave?cacheFileId=` +
                    response.value +
                    "&confirmImport=true&typeId=" +
                    this.formTypeSelectorCatId
                )
                .then(function(resp) {
                  this_.fullscreenLoading = false;
                  let data = resp.data;
                  if (data.state) {
                    this_.handleImportSuccess();
                  } else {
                    this_.$message.error(data.message);
                  }
                });
            })
            .catch(action => {
              this_.$http.post(
                `${
                  window.context.form
                }/form/form/v1/importSave?confirmImport=false&cacheFileId=` +
                  response.value
              );
              this_.importDialogVisible = false;
              this.$refs.upload.clearFiles();
            });
        }
      }
    },
    handleImportSuccess() {
      this.$message.success("表单导入成功");
      this.importDialogVisible = false;
      this.$refs.htTable.load();
      this.$refs.upload.clearFiles();
    },
    beforeUpload(file) {
      if (!file.name.endsWith(".zip")) {
        this.$message.warning("只能导入zip文件!");
        return false;
      }
      this.imporActionUrl = this.imporCheckUrl;
      this.fullscreenLoading = true;
    },
    onExceed(file) {
      this.$message.warning("只能选择一个zip文件!");
    },
    submitImport() {
      if (
        !this.$refs.upload.uploadFiles ||
        this.$refs.upload.uploadFiles.length == 0
      ) {
        this.$message.warning("请选择要导入的表单!");
        return false;
      }
      if (!this.formTypeSelectorCatId) {
        this.$message.warning("请选择要导入的分类!");
        return false;
      }
      this.$refs.upload.submit();
    },
    handExport() {
      if (this.updateTableData.length == 0) {
        this.$message.warning("请选择至少一项记录");
        return;
      }
      let ids = [];
      for (let item of this.updateTableData) {
        ids.push(item["id"]);
      }
      if (ids.length == 0) {
        this.$message.warning("请选择至少一项记录");
        return;
      }
      let url = `${window.context.form}/form/form/v1/exportForm?formIds=${ids}`;
      req.download(url);
    },
    //表格选中数据
    tableSelect(selection) {
      this.updateTableData = selection;
    },
    //设置分类
    openTypeSetDialog() {
      if (this.updateTableData.length == 0) {
        this.$message({
          message: "请先选择需要设置分类的数据",
          type: "warning"
        });
        return;
      }
      this.$refs.typeSetDialog.showDialog({});
    },
    sysTypeDialogOnConfirm(data) {
      var id = [];
      for (let i = 0; i < this.updateTableData.length; i++) {
        id.push(this.updateTableData[i].id);
      }
      let this_ = this;
      this.$http
        .get(
          "${portal}/sys/sysType/v1/updateEntitySysType?typeID=" +
            data.id +
            "&entityIds=" +
            id.join(",")
        )
        .then(function(resp) {
          if (resp.data && resp.data.state) {
            this_.$message({ message: resp.data.message, type: "success" });
            this_.updateTableData = [];
            setTimeout(function() {
              this_.dialogVisible = false;
              this_.$refs.htTable.load();
            }, 3000);

            return;
          }
          this_.$message.error(resp.data.message);
        });
    },
    openFormVersion(row) {
      this.formKey = row.formKey;
      this.formVersionVisible = true;
    },
    handleCloseFormVersion() {
      this.formVersionVisible = false;
      this.$refs.htTable.load();
    },
    handleCloseFormPrint() {
      this.formPrintVisible = false;
    },
    formAuth(formKey) {
      let param = {
        flowKey: "",
        formKey: formKey,
        nodeId: "",
        parentflowKey: "",
        type: "1"
      };
      this.$refs.flowFormAuth.showDialog(param);
    },
    handleNodeClick(node) {
      if (node.typeKey == "FORM_TYPE") {
        this.typeIdQuery = null;
      } else {
        this.bpmForm.typeName = node.name;
        this.typeIdQuery = {
          property: "typeId",
          value: node.id,
          group: "typeId",
          operation: "EQUAL",
          relation: "AND"
        };
      }
      this.$refs.htTable.load();
    },
    check(data, checkObj) {
      if (checkObj.checkedKeys.length == 0) {
        this.typeIdQuery = null;
      } else {
        this.typeIdQuery = {
          property: "typeId",
          value: checkObj.checkedKeys.join(","),
          group: "typeId",
          operation: "IN",
          relation: "AND"
        };
      }
      this.$refs.htTable.load();
    },
    loadData(param, cb) {
      let _param = {};
      _param = { ..._param, ...param };
      if (this.typeIdQuery) {
        _param.querys.push(this.typeIdQuery);
      }

      form
        .getPageJson(_param)
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
      this.formRow = params.row;
      switch (params.command) {
        case "edit":
          this.bpmForm.bos = [];
          this.formId = params.row.id;
          this.formDefId = params.row.defId;
          this.formEditorDialogVisible = true;
          break;
        case "preview":
          if (this.currentUser && this.currentUser.account) {
            window.open(
              window.context.front +
                "/previewForm/" +
                params.row.id +
                "?token=" +
                this.currentUser.token,
              // Base64.encode(this.currentUser.account),
              "_blank"
            );
          }
          break;
        case "publish":
          this.$confirm("是否确定发布表单?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          })
            .then(() => {
              this.$http
                .post(
                  "${form}/form/form/v1/publish",
                  {},
                  { formId: params.row.id }
                )
                .then(response => {
                  if (response.data.state) {
                    this.$message({
                      type: "success",
                      message: response.data.message
                    });
                    this.$refs.htTable.load();
                  } else {
                    this.$message.$error(response.data.message);
                  }
                });
            })
            .catch(() => {});

          break;
        case "formRight":
          this.formAuth(params.row.formKey);
          break;
        case "mobileForm":
          this.$confirm("是否确定生成手机表单?", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          })
            .then(() => {
              this.createMobileForm(params.row);
            })
            .catch(() => {});

          break;
        case "copy":
          this.saveCopy();
          break;
        case "relation":
          this.bindRelation(params);
          break;
        case "printForm":
          this.formKey = params.row.formKey;
          this.formPrintVisible = true;
          break;
        case "manage":
          this.formId = params.row.id;
          this.addToMenu("manage");
          break;
        case "front":
          this.formId = params.row.id;
          this.addToMenu("front");
          break;
        default:
          break;
      }
    },
    bindRelation(params) {
      this.dialogVisible2 = true;
      form.getBindRelation(params.row.defId, params.row.formKey).then(resp => {
        this.bindData = resp.data;
      });
    },
    saveCopy() {
      this.newForm.id = this.formRow.id;
      this.dialogSaveCopyVisible = true;
    },
    handleCloseSaveCopy() {
      this.dialogSaveCopyVisible = false;
    },
    afterSaveData() {
      this.handleCloseSaveCopy();
      this.$refs.htTable.load();
    },
    handleClose() {
      this.dialogVisible = false;
      this.dialogVisible2 = false;
    },
    openAddDailog() {
      this.$refs.eipBoDialog.showDialog([]);
      // this.dialogVisible = true;
      this.$set(this.bpmForm, "formKey", "");
      this.$set(this.bpmForm, "bos", "");
      this.$set(this.bpmForm, "desc", "");
      this.$set(this.bpmForm, "formName", "");
      this.$set(this.bpmForm, "name", "");
      if (this.bpmForm.typeName) {
        this.$set(this.bpmForm, "typeName", this.bpmForm.typeName);
      } else {
        this.$set(this.bpmForm, "typeName", "");
      }
      this.$set(this.bpmForm, "typeId", "");
      this.$set(this.bpmForm, "id", "");
      this.$set(this.bpmForm, "defId", "");
      this.$set(this.bpmForm, "rev", 1);
    },
    handleCloseFormEditor() {
      this.formEditorDialogVisible = false;
      this.$refs.htTable.load();
    },
    toFormDesigner() {
      utils
        .validateForm(this, "editBpmForm")
        .then(() => {
          this.bpmForm.formType = "pc";
          this.bpmForm.name = this.bpmForm.formName;
          this.formId = "";
          this.formDefId = "";
          this.formEditorDialogVisible = true;
          this.handleClose();
        })
        .catch(reason => {
          let rules = reason.map(obj => {
            return obj.rule;
          }); //获取到报错后的规则数组
          if (rules.includes("required")) {
            this.$message.error("请完整填写表单内容");
          } else if (rules.includes("alpha_num")) {
            this.$message.error("信息未正确填写");
          } else if (rules.includes("isExist")) {
            this.$message.error("表单别名重复");
          }
        });
    },
    createMobileForm(row) {
      this.$http
        .post("${form}/form/form/v1/pcForm2MobileForm?formId=" + row.id)
        .then(response => {
          if (response.data.state) {
            this.$message({
              type: "success",
              message: response.data.message
            });
            this.$refs.htTable.load();
          } else {
            this.$message.$error(response.data.message);
          }
        });
    }
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
