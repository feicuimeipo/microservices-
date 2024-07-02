<template>
  <el-container class="fullheight" style="border: 1px solid #eee">
    <ht-aside-tree
      type-key="FORM_TYPE"
      @node-click="handleNodeClick"
      @check="check"
    />
    <el-container>
      <el-main>
        <ht-table
          @load="loadData"
          :data="data"
          :pageResult="pageResult"
          :selection="true"
          quick-search-props="name,alias"
          :show-export="false"
		  :default-sorter="[{ direction: 'DESC', property: 'create_time_' }]"
          ref="htTable"
        >
          <template v-slot:toolbar>
            <el-button-group>
              <el-button
                size="small"
                @click="handleFormSelectOpen"
                icon="el-icon-plus"
                >添加</el-button
              >
              <el-button
                size="small"
                @click="importDialogVisible = true;flowTypeSelectorCatId='';flowTypeSelectorCatName=''"
                icon="el-icon-back"
              >导入</el-button>
              <el-button size="small" @click="handExport" icon="el-icon-right">导出</el-button>              
              <ht-delete-button :url="formDeleteUrl" :htTable="$refs.htTable"
                >删除</ht-delete-button
              >
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
              <template slot-scope="scope">
                <el-link
                  type="primary"
                  @click="edit(scope.row)"
                  title="查看详情"
                  >{{ scope.row.name }}</el-link
                >
              </template>
            </ht-table-column>
            <ht-table-column prop="alias" label="别名" :sortable="true" />
            <ht-table-column
              prop="boDefAlias"
              label="业务对象别名"
              :sortable="true"
            />
            <ht-table-column prop="typeName" label="分类" />
            <ht-table-column prop="subject" label="绑定流程名称" />
            <ht-table-column width="150" label="操作">
              <template v-slot="{ row }">
                <el-button icon="el-icon-view" @click="preview(row)"
                  >预览</el-button
                >
              </template>
            </ht-table-column>
          </template>
        </ht-table>
      </el-main>
    </el-container>

    <el-dialog
      title="导入数据报表"
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
          v-model="flowTypeSelectorCatName"
          :sys-type-id.sync="flowTypeSelectorCatId"
          :validate="{required:true}"
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
          :data="{isCheck:true}"
          :auto-upload="false"
          ref="upload"
        >
          <el-button size="small" icon="el-icon-upload">选择数据报表</el-button>
        </el-upload>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="submitImport"
          element-loading-text="拼命导入中"
          v-loading.fullscreen.lock="fullscreenLoading"
        >确 定</el-button>
        <el-button @click="importDialogVisible = false">取 消</el-button>
      </span>
    </el-dialog>

    <!-- 添加数据报表 开始 -->
    <el-dialog
      width="50%"
      title="选择表单"
      :visible="selectFormVisible"
      :before-close="handleFormSelectClose"
    >
      <template>
        <ht-table
          @load="loadFormData"
          :data="formData"
          :pageResult="formPageResult"
          :selectable="false"
          :show-export="false"
          :show-custom-column="false"
		  :default-sorter="[{ direction: 'DESC', property: 'updateTime' }]"
          quick-search-props="name,alias"
          v-if="selectFormVisible"
        >
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
              :show-overflow-tooltip="true"
              :sortable="true"
            >
              <template slot-scope="scope">
                <el-link
                  type="primary"
                  @click="addFormTemplate(scope.row)"
                  title="选择"
                  >{{ scope.row.name }}</el-link
                >
              </template>
            </ht-table-column>
            <ht-table-column prop="formKey" label="表单key" :sortable="true" />
            <ht-table-column prop="version" label="版本" :sortable="true" />
            <ht-table-column prop="typeName" label="分类" :sortable="true" />
          </template>
        </ht-table>
      </template>
    </el-dialog>
    <!-- 添加数据报表 结束 -->

    <!-- 编辑数据报表  开始-->
    <ht-sidebar-dialog
      width="100%"
      :show-close="false"
      :close-on-click-modal="false"
      :visible="datatemplateEditVisible"
      :before-close="handleClose"
      class="dt-edit__dialog"
    >
      <template v-slot:title>
        <div class="flex" style="justify-content: space-between">
          <el-page-header
            @back="datatemplateEditVisible = false"
            :content="dataTemplateName"
          ></el-page-header>
          <el-button-group>
            <el-button
              @click="handleTemplateDataSave"
              type="primary"
              icon="el-icon-check"
              >保存</el-button
            >
            <el-button
              v-if="
                currentDataTemplateData.data && currentDataTemplateData.data.id
              "
              @click="handleTemplateDataPreview"
              icon="el-icon-view"
              >预览</el-button
            >
            <el-button
              v-if="
                currentDataTemplateData.data && currentDataTemplateData.data.id
              "
              @click="handleTemplateEdit"
              icon="el-icon-edit"
              >编辑模板</el-button
            >
            <el-button
              v-if="
                currentDataTemplateData.data && currentDataTemplateData.data.id
              "
              @click="addToMenu('manage')"
              icon="el-icon-plus"
              >添加到后端菜单</el-button
            >
            <el-button
              v-if="
                currentDataTemplateData.data && currentDataTemplateData.data.id
              "
              @click="addToMenu('front')"
              icon="el-icon-plus"
              >添加到前端菜单</el-button
            >
          </el-button-group>
        </div>
      </template>
      <template>
        <el-tabs
          v-if="datatemplateEditVisible"
          type="card"
          style="height: 100%;"
          v-model="activeName"
          @tab-click="handlePaneClick"
        >
          <el-tab-pane label="基本信息" name="baseSetting">
            <BaseSetting
              ref="BaseSetting"
              :focusAlias="focusAlias"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="显示字段" name="displaySetting">
            <DisplaySetting
              ref="DisplaySetting"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="查询字段" name="conditionSetting">
            <ConditionSetting
              ref="ConditionSetting"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="排序字段" name="sortSetting">
            <SortSetting
              ref="SortSetting"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="筛选字段" name="filterFiledSetting">
            <FilterFiledSetting
              ref="FilterFiledSetting"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="数据过滤" name="filterSetting">
            <FilterSetting
              ref="FilterSetting"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="功能按钮" name="manageSetting">
            <ManageSetting
              ref="ManageSetting"
              :data.sync="currentDataTemplateData"
            />
          </el-tab-pane>

          <el-tab-pane label="过滤树" name="filterTreeSetting">
            <filter-tree-setting
                ref="FilterTreeSetting"
                :data.sync="currentDataTemplateData"
                />
          </el-tab-pane>
        </el-tabs>
      </template>
    </ht-sidebar-dialog>
    <!-- 编辑数据报表  结束-->

    <!-- 数据报表预览 开始 -->
    <ht-sidebar-dialog
      width="100%"
      title="报表预览"
      :visible="previewDialogVisible"
      :before-close="handlePreviewClose"
    >
      <TemplatePreviewDialog
        ref="TemplatePreviewDialog"
        :alias="currentPreviewTemplateAlias"
      />
    </ht-sidebar-dialog>
    <!-- 数据报表预览 结束 -->
    <el-dialog
      title="选择显示BO表"
      :visible.sync="boVisible"
      width="30%">
      <el-form>
        <ht-form-item label="BO表">
          <ht-select
              v-model="boId"
              :options="boData"
              :props="{key:'id',value:'desc'}"/>
        </ht-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer" >
        <el-button @click="addFormTemplateByFormKey(selectFormKey,boId)" type="primary">确定</el-button>
        <el-button @click="boVisible = false">取消</el-button>
      </div>
    </el-dialog>
  </el-container>
</template>
<script>
import form from "@/api/form.js";
import $ from "jquery";
import { mapState, mapActions } from "vuex";
const htAsideTree = () => import("@/components/common/HtAsideTree.vue");
const BaseSetting = () =>
  import("@/components/form/dataTemplate/BaseSetting.vue");
const DisplaySetting = () =>
  import("@/components/form/dataTemplate/DisplaySetting.vue");
const ConditionSetting = () =>
  import("@/components/form/dataTemplate/ConditionSetting.vue");
const SortSetting = () =>
  import("@/components/form/dataTemplate/SortSetting.vue");
const FilterSetting = () =>
  import("@/components/form/dataTemplate/FilterSetting.vue");
const ManageSetting = () =>
  import("@/components/form/dataTemplate/ManageSetting.vue");
const TemplatePreviewDialog = () =>
  import("@/components/form/dataTemplate/TemplatePreviewDialog.vue");
const filterTreeSetting = () => import("@/components/form/dataTemplate/FilterTreeSetting.vue");
const eipSysTypeSelector = () =>
  import("@/components/selector/EipSysTypeSelector.vue");
  
const FilterFiledSetting = () =>
  import("@/components/form/dataTemplate/FilterFiledSetting.vue");
  
import req from "@/request.js";


export default {
  components: {
    htAsideTree,
    BaseSetting,
    DisplaySetting,
    ConditionSetting,
    SortSetting,
    FilterSetting,
    ManageSetting,
    TemplatePreviewDialog,
    filterTreeSetting,
    eipSysTypeSelector,
    FilterFiledSetting
  },
  data() {
    return {
      uploadHeaders:{ Authorization: "Bearer " + this.$store.state.login.currentUser.token },
      asideShow: true,
      treeData: [],
      defaultProps: {
        children: "children",
        label: "name"
      },
      pageResult: {
        page: 1,
        pageSize: 20,
        total: 0
      },
      data: [],
      selectTypeIds: "",
      fullscreenLoading: false,
      importDialogVisible: false,
      flowTypeSelectorCatId: "",//数据报表分类ID
      flowTypeSelectorCatName: "",//数据报表分类名称
      datatemplateEditVisible: false,
      selectFormVisible: false,
      formEditorDialogVisible: false,
      previewDialogVisible: false,
      templateId: null,
      selectFormKey: null,
      formData: [],
      formPageResult: {
        page: 1,
        pageSize: 20,
        total: 0
      },
      currentDataTemplateData: {},
      activeName: "baseSetting",
      currentPreviewTemplateAlias: "",
      focusAlias: false,
      boVisible:false,
      boData:[],
      boId:""
    };
  },

  computed: mapState({
    imporCheckUrl: function(mapState) {
      return (
        window.context.form +
        "/form/dataTemplate/v1/importCheck?typeId=" +
        this.flowTypeSelectorCatId
      );
    },
    frontUrl: function(state) {
      return (
        window.context.front +
        "/statement/template/preview/" +
        this.currentPreviewTemplateAlias + "/true"+
        "?token=" +
        state.login.currentUser.token
        // Base64.encode(state.login.currentUser.account)
      );
    },
    formDeleteUrl: function() {
      return `${window.context.form}/form/dataTemplate/v1/remove`;
    },
    dataTemplateName: function() {
      return this.currentDataTemplateData.bpmDataTemplate &&
        this.currentDataTemplateData.bpmDataTemplate.name
        ? this.currentDataTemplateData.bpmDataTemplate.name
        : "";
    }
  }),
  mounted() {},
  methods: {
    //导出
    handExport() {
      let elTable = this.$refs.htTable;
      if (this.$refs.htTable.$refs && this.$refs.htTable.$refs.htTable) {
        elTable = this.$refs.htTable.$refs.htTable;
      }
      if (elTable && elTable.selection && elTable.selection.length == 0) {
        this.$message.warning("请选择至少一项记录");
        return;
      }

      let ids = [];

      for (let item of elTable.selection) {
        ids.push(item["id"]);
      }

      if (ids.length == 0) {
        this.$message.warning("请选择至少一项记录");
        return;
      }
      let url = `${
        req.getContext().form
      }/form/dataTemplate/v1/exportXml?ids=${ids}`;
      req.download(url);
    },
    submitImport() {
      if (
        !this.$refs.upload.uploadFiles ||
        this.$refs.upload.uploadFiles.length == 0
      ) {
        this.$message.warning("请选择要导入的数据报表");
        return false;
      }
      if (!this.flowTypeSelectorCatId) {
        this.$message.warning("请选择要导入的分类");
        return false;
      }
      this.$refs.upload.submit();
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
          response.message.indexOf("是否覆盖") > 0
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
              req
                .post(
                  req.getContext().bpmModel +
                    "/form/dataTemplate/v1/importSave?cacheFileId=" +
                    response.value +
                    "&confirmImport=" +
                    true +
                    "&typeId=" +
                    this.flowTypeSelectorCatId
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
              req.post(
                req.getContext().bpmModel +
                  "/form/dataTemplate/v1/importSave?cacheFileId=" +
                  response.value +
                  "&confirmImport=" +
                  false
              );
              this_.importDialogVisible = false;
              this.$refs.upload.clearFiles();
            });
        } else {
          this.$message.error(response.message || "数据报表导入失败");
        }
      }
    },
    handleImportSuccess() {
      this.$alert("如果报表对应表单不存在请先导入表单，报表才能正常使用", "数据报表导入成功", {
        confirmButtonText: "关闭",
        type: "success"
      }).then(() => {
        this.importDialogVisible = false;
        this.$refs.htTable.load();
        this.$refs.upload.clearFiles();
      });
    },
    onExceed(file) {
      this.$message.warning("只能选择一个zip文件!");
    },
    beforeUpload(file) {
      if (!file.name.endsWith(".zip")) {
        this.$message.warning("只能导入zip文件!");
        return false;
      }
      this.imporActionUrl = this.imporCheckUrl;
      this.fullscreenLoading = true;
    },
    handleNodeClick(node) {
      if (node.id == "7") {
        this.selectTypeIds = "";
      } else {
        this.selectTypeIds = node.id;
      }
      this.$refs.htTable.load();
    },
    check(data, checkObj) {
      this.selectTypeIds = checkObj.checkedKeys.join(",");
      this.$refs.htTable.load();
    },
    loadData(param, cb) {
      if (this.selectTypeIds) {
        param.querys = param.querys || [];
        let hasTypeQuery = false;
        for (const query of param.querys) {
          if (query.property == "typeId") {
            query.value = this.selectTypeIds;
            hasTypeQuery = true;
            break;
          }
        }
        if (!hasTypeQuery) {
          param.querys.push({
            group: "main",
            operation: "IN",
            property: "typeId",
            relation: "AND",
            value: this.selectTypeIds
          });
        }
      }
      if (!param.sorter){
        param.sorter = [
          {
            direction: "DESC",
            property: "UPDATE_TIME_"
          }
        ];
      }else{
        param.sorter.push(
          {
            direction: "DESC",
            property: "UPDATE_TIME_"
          }
        )
      }
      form
        .getDataTemplateData(param)
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
    loadFormData(param, cb) {
      form
        .getFormData(param)
        .then(response => {
          this.formData = response.rows;

          this.formPageResult = {
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
          this.templateId = params.row.id;
          this.editFormTemplate(this.templateId);
          break;
        case "delete":
          break;
        case "preview":
          this.currentPreviewTemplateAlias = params.row.alias;
          this.handleTemplateDataPreview();
          break;
        case "assignMenu":
          break;
        default:
          break;
      }
    },
    edit(params) {
      this.templateId = params.id;
      this.editFormTemplate(this.templateId,params.boDefId);
    },
    preview(params) {
      this.currentPreviewTemplateAlias = params.alias;
      this.handleTemplateDataPreview();
    },
    //报表预览
    handleTemplateDataPreview() {
      window.open(this.frontUrl, "_blank");
      //this.previewDialogVisible = true;
    },
    //编辑报表模板
    handleTemplateEdit() {
      this.$refs.BaseSetting.showHtmlEditDialog();
    },
    //数据报表添加到菜单
    addToMenu(type) {
      this.$refs.BaseSetting.showAddToMenuDialog(type);
    },
    handlePreviewClose() {
      this.previewDialogVisible = false;
      this.currentPreviewTemplateId = "";
    },
    handleClose() {
      this.dialogVisible = false;
    },
    handleFormSelectOpen() {
      this.selectFormVisible = true;
      this.selectFormKey = null;
    },
    handleFormSelectClose() {
      this.selectFormVisible = false;
    },
    addFormTemplate(row) {
      this.selectFormKey = row.formKey;
      this.boData = [];
      form.getBODefByFormId(row.defId).then(data=>{
        if (data.length>1){
          this.boVisible = true;
          this.boData = data;
        }
      }).then(()=>{
        if (this.boData.length<=1){
          this.addFormTemplateByFormKey(row.formKey,"");
        }
      })

    },
    addFormTemplateByFormKey(formKey,boId){
      this.selectFormVisible = false;
      form.getBpmDataTemplate(formKey,boId).then(response => {
        this.currentDataTemplateData = response;
        this.currentPreviewTemplateAlias = response.data.alias;
        this.boVisible = false;
        this.datatemplateEditVisible = true;
      });
    },
    editFormTemplate: function(templateId, boId) {
      form.getBpmDataTemplateById(templateId, boId).then(response => {
        this.currentDataTemplateData = response;
        this.currentPreviewTemplateAlias = response.data.alias;
        this.datatemplateEditVisible = true;
      });
    },
    handlePaneClick: function(data) {
      if (data.alias) {
        this.currentTabComponent = data.alias;
      }
      if (data.name == "defConfig") {
        this.flowConfigShouldRefresh = false;
      }
    },
    //保存数据视图
    handleTemplateDataSave: function() {
      let this_ = this;
      this.$refs.BaseSetting.validateForm(function() {
        this_.saveTemolate();
      });
    },
    saveTemolate() {
      this.$refs.DisplaySetting.saveDisplayField(); //保存显示列数据
      let result = this.$refs.ConditionSetting.saveConditionField(); //保存查询条件数据
      if ("false" == result) {
        return;
      }
      this.$refs.SortSetting.saveSortField(); //保存排序数据
      if(!this.$refs.ManageSetting.validateManageField()){
        return ;
      }
      this.$refs.FilterSetting.saveFilterField(); //保存按钮数据
      this.$refs.ManageSetting.saveManageField(); //保存按钮数据     
      this.$refs.FilterFiledSetting.saveFilteringField();
      this.$refs.FilterTreeSetting.saveFilterTreeField();
      let templateData = this.currentDataTemplateData.bpmDataTemplate;
      let sortArr = JSON.parse(templateData.sortField);
      for(let s=0; s<sortArr.length; s++){
        if(!sortArr[s].sort){
          this.$message({type: "error", message: "请选择排序方式"});
          return false;
        }
      }
      if (!templateData.alias) {
        this.$message.error("报表别名不能为空，请输入报表别名！");
        return;
      }
      if (!templateData.name) {
        this.$message.error("报表名称不能为空，请输入报表名称！");
        return;
      }
      if (templateData.resetTemp == 1 && !templateData.templateAlias) {
        this.$message.error("请选择数据模板！");
        return;
      }
      //判断是否添加模糊查询
      let isIndistinct = "hide";
      let conditionAllName = "";
      let conditionAllDesc = "";
      let conditionFields = this.$refs.ConditionSetting.conditionFields;
      for (var i = 0; i < conditionFields.length; i++) {
        if (conditionFields[i].mg) {
          isIndistinct = "show";
          conditionAllName += conditionFields[i].na + ",";
          conditionAllDesc += conditionFields[i].cm + "/";
        }
      }
      if (conditionAllName != "") {
        conditionAllName = conditionAllName.substring(
          0,
          conditionAllName.length - 1
        );
      }
      if (conditionAllDesc != "") {
        conditionAllDesc = conditionAllDesc.substring(
          0,
          conditionAllDesc.length - 1
        );
      }
      templateData.isIndistinct = isIndistinct;
      templateData.conditionAllName = conditionAllName;
      templateData.conditionAllDesc = conditionAllDesc;
      //添加表单字段
      if(this.currentDataTemplateData.formField){
        templateData.formField = JSON.stringify(this.currentDataTemplateData.formField);
      }
      let _this = this;
      form.saveTemplateData(JSON.stringify(templateData)).then(response => {
        if (response.state) {
          this.$message.success(response.message);
          this.datatemplateEditVisible = false;
          this.currentDataTemplateData = {};
          _this.$refs.htTable.load(true);
          this.focusAlias = false;
        } else {
          this.focusAlias = true;
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

.form-editor-dialog >>> .el-dialog__body {
  padding: 0;
  height: 100%;
}

div >>> .el-dialog__body {
  height: calc(100% - 10px);
  padding: 10px;
}

.dt-edit__dialog /deep/ > .el-dialog > .el-dialog__header {
  padding: 8px 20px;
}
</style>
