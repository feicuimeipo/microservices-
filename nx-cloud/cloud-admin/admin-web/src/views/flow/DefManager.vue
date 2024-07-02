<template>
  <el-container class="fullheight" style="border: 1px solid #eee">
    <ht-aside-tree
      cat-id="6"
      @node-click="handleNodeClick"
      @delete="selectTypeIds ='';$refs.htTable.load(); "
      @check="check"
    />
    <el-container>
      <el-main>
        <ht-table
          @load="loadData"
          :data="data"
          :pageResult="pageResult"
          :selection="true"
          quick-search-props="name,defKey,desc"
          ref="htTable"
          :show-export="false"
          :defaultSorter="[{'property':'createTime','direction':'DESC'}]"
        >
          <template v-slot:toolbar>
            <el-button-group>
              <el-button size="small" @click="handleCommand({command:'add'})" icon="el-icon-plus">新增</el-button>
              <el-button
                size="small"
                @click="importDialogVisible = true;flowTypeSelectorCatId='';flowTypeSelectorCatName=''"
                icon="el-icon-back"
              >导入</el-button>
              <el-button size="small" @click="handExport" icon="el-icon-right">导出</el-button>
              <el-button @click="openTypeSetDialog()">设置分类</el-button>
              <el-button size="small" @click="claeaCache" icon="el-icon-refresh">清除缓存</el-button>
            </el-button-group>
          </template>

          <template>
            <ht-table-column type="index" width="50" align="center" label="序号" />
            <ht-table-column
              prop="name"
              label="名称"
              :hiden="true"
              width="200"
              :show-overflow-tooltip="true"
              :sortable="true"
            >
              <template v-slot="{row}">
                <span v-if="!row.authorizeRight.m_edit">{{row.name}}</span>
                <el-link
                  type="primary"
                  v-if="row.authorizeRight.m_edit"
                  @click="handleCommand({row:row,command:'edit'})"
                  title="编辑流程"
                >{{row.name}}</el-link>
              </template>
            </ht-table-column>
            <ht-table-column prop="defKey" label="流程key" width="100" :show-overflow-tooltip="true" />
            <ht-table-column
              prop="desc"
              label="流程描述"
              :show-overflow-tooltip="true"
              :sortable="true"
            />
            <ht-table-column
              prop="typeName"
              label="流程分类"
              hidden
              :show-overflow-tooltip="true"
              width="100"
              :sortable="true"
            />
            <ht-table-column prop="status" label="状态" width="80" :filters="statusArray">
              <template v-slot="{row}">
                <el-tag
                  v-show="row.status==s.value"
                  :type="s.type"
                  v-for="s in statusArray"
                  :key="s.value"
                >{{s.text}}</el-tag>
              </template>
            </ht-table-column>
            <ht-table-column
              prop="testStatus"
              label="生产状态"
              width="90"
              :filters="[{text:'测试', value:'test'},{text:'正式', value:'run'}]"
            >
              <template v-slot="{row}">
                <el-tag type="info" v-if="row.testStatus =='test'">测试</el-tag>
                <el-tag type="danger" v-if="row.testStatus =='run'">正式</el-tag>
              </template>
            </ht-table-column>
            <ht-table-column prop="version" label="版本号" width="60" />
            <ht-table-column width="170" label="操作">
              <template v-slot="{row}">
                <el-dropdown
                  size="mini"
                  split-button
                  v-if="row.status != 'draft'"
                  @command="handleCommand"
                  @click="row.authorizeRight.m_start ? handleCommand({ row: row, command: 'startFlow' }):handleCommand({ row: row, command: 'bindRelation' })"
                >
                  <span v-if="row.authorizeRight.m_start">
                    <i class="el-icon-paperclip"></i>启动
                  </span>
                  <span v-if="!row.authorizeRight.m_start ">
                    <i class="el-icon-paperclip"></i>绑定关系
                  </span>

                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      v-if="row.authorizeRight.m_del"
                      icon="el-icon-delete"
                      :command="{ row: row, command: 'delete' }"
                    >删除</el-dropdown-item>

                    <el-dropdown-item
                      v-if="row.authorizeRight.m_clean && row.testStatus =='test'"
                      icon="el-icon-close"
                      :command="{ row: row, command: 'cleanData' }"
                    >清除数据</el-dropdown-item>

                    <el-dropdown-item
                      v-if="row.authorizeRight.m_start"
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'bindRelation' }"
                    >绑定关系</el-dropdown-item>
                    <el-dropdown-item
                      v-if="row.status == 'deploy'"
                      icon="el-icon-menu"
                      :command="{ row: row, command: 'processSimulation' }"
                      size="mini"
                    >流程仿真配置</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
                <el-button
                  v-if="row.status == 'draft'"
                  icon="el-icon-delete"
                  @click="handleCommand({ row: row, command: 'delete' })"
                  size="mini"
                >删除</el-button>
                
              </template>
            </ht-table-column>
          </template>
        </ht-table>
      </el-main>
    </el-container>
    <ht-sidebar-dialog
      width="28%"
      title="绑定关系"
      :visible.sync="dialogVisible2"
      :before-close="handleClose"
    >
      <el-row>
        <el-col>
          PC表单实体对象：
          <span v-if="bindData.pcEnt">
            <el-tag style="margin-right: 5px;cursor: pointer;" @click="handleClose">
              <router-link
                :to="{path:'businessObj',query:{id:bindData.pcEnt.id_}}"
              >{{bindData.pcEnt.description_}}</router-link>
            </el-tag>
          </span>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col>
          Mobile表单实体对象：
          <span v-if="bindData.mobileEnt">
            <el-tag style="margin-right: 5px;cursor: pointer;" @click="handleClose">
              <router-link
                :to="{path:'businessObj',query:{id:bindData.mobileEnt.id_}}"
              >{{bindData.mobileEnt.description_}}</router-link>
            </el-tag>
          </span>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col>
          PC表单：
          <span v-if="bindData.pcForm">
            <el-tag style="margin-right: 5px" @click="handleClose">{{bindData.pcForm.pcName}}</el-tag>
          </span>
        </el-col>
      </el-row>
      <br />
      <el-row>
        <el-col>
          Mobile表单：
          <span v-if="bindData.mobileForm">
            <el-tag
              style="margin-right: 5px"
              @click="handleClose"
            >{{bindData.mobileForm.mobileName}}</el-tag>
          </span>
        </el-col>
      </el-row>
    </ht-sidebar-dialog>

    <ht-sidebar-dialog
      width="100%"
      :close-on-click-modal="false"
      :visible="dialogVisible"
      :before-close="handleClose"
      class="flow_conf"
    >
      <el-container class="fullheight">
        <el-header height="48px" style="padding:0">
          <div
            class="flex"
            style="float:left;justify-content: center;background: #f5f5f5;height: 47px;width:180px;border-bottom:1px solid #eee;border-right:1px solid #eee"
          >
            <el-page-header @back="handleClose" content="流程编辑"></el-page-header>
          </div>
          <div
            class="flex"
            style="float:left;justify-content: space-between;height: 47px;width:calc(100% - 181px);border-bottom:1px solid #eee;"
          >
            <el-breadcrumb separator=">" style="margin-left:20px;">
              <el-breadcrumb-item :to="{ path: '/' }">流程设计</el-breadcrumb-item>
              <el-breadcrumb-item>{{defNameAndVrsionTitle}}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <el-link class="bpmn-xml-link" @click="watchBPMNXML">
            <i class="icon-technology" />
            BPMNXML
          </el-link>
        </el-header>
        <el-main style="padding:0">
          <el-tabs
            class="flow-edit-container"
            v-if="dialogVisible"
            type="card"
            style="height: 100%;"
            tab-position="left"
            v-model="activeName"
            @tab-click="handlePaneClick"
            @tab-remove="handlePaneRemove"
            :before-leave="beforePaneleave"
            ref="flowEditTables"
          >
            <el-tab-pane label="流程设计" name="defDesign">
              <BpmEditor
                :defId="curSelectDefId"
                @close-def-manager="handleClose"
                @switch-config-refresh="handleDefSignSuccess"
              />
            </el-tab-pane>
            <el-tab-pane label="流程配置" v-if="!isShowSubFlowSet" name="defConfig">
              <keep-alive v-if="!flowConfigShouldRefresh">
                <FlowConfig :defId="curSelectDefId" />
              </keep-alive>
              <FlowConfig v-else :defId="curSelectDefId" />
            </el-tab-pane>
            <el-tab-pane label="流程配置" v-if="isShowSubFlowSet" name="defConfig">
              <SubFlowConfig v-if="activeName == 'defConfig'" :defId="curSelectDefId" />
            </el-tab-pane>
            <el-tab-pane label="初始赋值" name="boSetting">
              <FlowEditBoSetting
                v-if="isCurDefPublish && activeName =='boSetting'"
                :defId="curSelectDefId"
              />
            </el-tab-pane>
            <el-tab-pane label="变量管理" name="varManage">
              <FlowVarList
                v-if="isCurDefPublish && activeName =='varManage'"
                :defId="curSelectDefId"
              />
            </el-tab-pane>
            <el-tab-pane label="版本管理" name="versionManage">
              <FlowVersionList
                v-if="isCurDefPublish && activeName =='versionManage'"
                :defId="curSelectDefId"
              />
            </el-tab-pane>
            <el-tab-pane label="其它设置" name="otherSetting">
              <FlowOtherSetting
                v-if="isCurDefPublish && activeName =='otherSetting'"
                :defId="curSelectDefId"
                @toggleTab="toggleTab"
              />
            </el-tab-pane>
            <el-tab-pane :closable="true" label="子流程设置" v-if="isShowSubFlowSet" name="subFlowSet">
              <SubFlowConfig
                v-if="activeName == 'subFlowSet'"
                :defId="subDefId"
                :topDefKey="topDefKey"
              />
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </ht-sidebar-dialog>

    <el-dialog
      title="导入流程"
      :visible.sync="importDialogVisible"
      width="40%"
      top="30vh"
      :close-on-click-modal="false"
      v-if="importDialogVisible"
    >
      <div style="height:150px;padding-left: 20px ;">
        <eip-sys-type-selector
          placeholder="请选择分类"
          type-key="FLOW_TYPE"
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
          <el-button size="small" icon="el-icon-upload">选择流程</el-button>
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
    <eip-sys-type-dialog
      ref="typeSetDialog"
      name="typeSetDialog"
      :cat-id="'6'"
      @onConfirm="sysTypeDialogOnConfirm"
    />
    <processSimulation ref="processSimulation" ></processSimulation>
  </el-container>
</template>

<script>
import {mapState} from "vuex";

import flow from "@/api/flow.js";
import EipUserDialog from "@/components/dialog/EipUserDialog.vue";
import BpmEditor from "@/views/flow/BpmEditor.vue";
import req from "@/request.js";

const htAsideTree = () => import("@/components/common/HtAsideTree.vue");
const FlowConfig = () => import("@/components/flow/FlowConfig.vue");
const SubFlowConfig = () => import("@/components/flow/FlowConfig.vue");
const FlowOtherSetting = () => import("@/components/flow/FlowOtherSetting.vue");
const FlowVarList = () => import("@/components/flow/FlowVarList.vue");
const FlowVersionList = () => import("@/components/flow/FlowVersionList.vue");
const eipSysTypeSelector = () =>
  import("@/components/selector/EipSysTypeSelector.vue");
const FlowEditBoSetting = () =>
  import("@/components/flow/FlowEditBoSetting.vue");
const eipSysTypeDialog = () =>
  import("@/components/dialog/EipSysTypeDialog.vue");
const processSimulation = () =>
  import("@/views/flow/simulation/ProcessSimulation.vue");

export default {
  components: {
    BpmEditor,
    EipUserDialog,
    htAsideTree,
    FlowConfig,
    FlowOtherSetting,
    SubFlowConfig,
    FlowVarList,
    FlowVersionList,
    FlowEditBoSetting,
    eipSysTypeSelector,
    eipSysTypeDialog,
    processSimulation
  },
  data() {
    return {
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      dialogVisible: false,
      defEditTabComponent: [
        { alias: "defConfig", name: "流程设计" },
        { alias: "defVarList", name: "变量管理" }
      ],
      statusArray: [
        { text: "已发布", value: "deploy", type: "info" },
        { text: "未发布", value: "draft" },
        { text: "禁用", value: "forbidden", type: "danger" },
        { text: "禁止实例", value: "forbidden_instance", type: "warning" }
      ],
      activeName: "defConfig",
      curSelectDefId: "",
      // 配置页面是否需要刷新缓存
      flowConfigShouldRefresh: false,
      topDefKey: "",
      subDefId: "",
      isShowSubFlowSet: false,
      isCurDefPublish: false,
      selectTypeIds: "",
      fullscreenLoading: false,
      importDialogVisible: false,
      flowTypeSelectorCatId: "",
      flowTypeSelectorCatName: "",
      startDef: "",
      defNameAndVrsionTitle: "",
      dialogVisible2: false,
      bindData: {}
    };
  },
  computed: mapState({
    isForbidden: function() {
      return [
        { text: "正常", value: "0", type: "success" },
        { text: "挂起", value: "1", type: "danger" }
      ];
    },
    deleteUrl: function() {
      return (
        window.context.bpmModel + "/flow/def/v1/removeByDefIds?cascade=true"
      );
    },
    uploadHeaders: function(mapState) {
      return { Authorization: "Bearer " + mapState.login.currentUser.token };
    },
    imporCheckUrl: function(mapState) {
      return (
        window.context.bpmModel +
        "/flow/def/v1/importCheck?typeId=" +
        this.flowTypeSelectorCatId
      );
    },
    account: state => state.login.currentUser.account,
    token: state => state.login.currentUser.token,
    frontUrl: function(state) {
      return (
        window.context.front +
        "/agentStart/" +
        this.startDef +
        "/0?token=" +
        this.token
        // Base64.encode(this.account)
      );
    }
  }),
  mounted() {
    //数据建模-》查看绑定关系
    if (this.$route.query.bpmId) {
      this.curSelectDefId = this.$route.query.bpmId;
      this.activeName = "defConfig";
      this.dialogVisible = true;
      this.$router.push("flowDesign#defManager");
    }
  },
  methods: {
    handleDefSignSuccess() {
      this.flowConfigShouldRefresh = true;
      if (!this.curSelectDefId) {
        return;
      }
      let this_ = this;
      req
        .get("${bpmModel}/flow/def/v1/defGet?defId=" + this.curSelectDefId)
        .then(function(resp) {
          let data = resp.data;
          if (data) {
            this_.defNameAndVrsionTitle =
              data.name + "(版本" + data.version + ")";
          }
        });
    },
    //跳转指定选项卡
    toggleTab(name) {
      this.activeName = name;
      this.flowConfigShouldRefresh = true;
    },
    //启动流程
    startFlow(id) {
      this.startDef = id;
      window.open(this.frontUrl, "_blank");
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
              req
                .post(
                  req.getContext().bpmModel +
                    "/flow/def/v1/importSave?cacheFileId=" +
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
                  "/flow/def/v1/importSave?cacheFileId=" +
                  response.value +
                  "&confirmImport=" +
                  false
              );
              this_.importDialogVisible = false;
              this.$refs.upload.clearFiles();
            });
        } else {
          this.$message.error(response.message || "流程导入失败");
        }
      }
    },
    handleImportSuccess() {
      this.$message.success("流程导入成功");
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
        this.$message.warning("请选择要导入的流程!");
        return false;
      }
      if (!this.flowTypeSelectorCatId) {
        this.$message.warning("请选择要导入的分类!");
        return false;
      }
      this.$refs.upload.submit();
    },
    watchBPMNXML() {
      let url =
        req.getContext().bpmModel +
        "/flow/def/v1/bpmnXml?defId=" +
        this.curSelectDefId;
      window.open(url, "_blank");
    },
    beforePaneleave(activeName, oldActiveName) {
      if (!this.isCurDefPublish) {
        this.$message.warning("流程还未发布,无法进行配置");
        return false;
      }
    },
    handlePaneClick: function(data) {
      const this_ = this;
      if (data.alias) {
        this.currentTabComponent = data.alias;
      }
      //流程设计
      if (data.name == "defConfig") {
        this.flowConfigShouldRefresh = false;
      }
    },
    handlePaneRemove: function(data) {
      this.activeName = "defConfig";
      this.flowConfigShouldRefresh = true;
      this.isShowSubFlowSet = false;
    },
    handleNodeClick(node) {
      if (node.id == "6") {
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
    handleClose() {
      this.dialogVisible = false;
      this.dialogVisible2 = false;
      this.$refs.htTable.load(true);
    },
    handleCloseMenuPerm() {
      this.dialogVisibleMenuPerm = false;
    },

    dialogCancle(dialogVisible) {
      this[dialogVisible] = false;
    },
    loadData(param, cb) {
      param.querys = param.querys || [];
      if (this.selectTypeIds) {
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
      } else {
        param.querys = param.querys.filter(q => {
          return q.property != "typeId";
        });
      }
      flow
        .getDefPage(param)
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
        case "edit": //编辑
          this.curSelectDefId = params.row.defId;
          this.defNameAndVrsionTitle =
            params.row.name + "(版本" + params.row.version + ")";
          if (params.row.status == "draft") {
            this.isCurDefPublish = false;
            this.activeName = "defDesign";
          } else {
            this.isCurDefPublish = true;
            this.activeName = "defConfig";
          }
          this.dialogVisible = true;
          break;
        case "add": //新增
          this.curSelectDefId = "";
          this.activeName = "defDesign";
          this.dialogVisible = true;
          this.isCurDefPublish = false;
          break;
        case "restore": //恢复
          break;
        case "isForbidden": //挂起/取消挂起
          this.forbiddenOrUnForbiddenInst(params);
          break;
        case "cleanData":
          this.cleanData(params.row.id);
          break;
        case "startFlow":
          this.startFlow(params.row.id);
          break;
        case "bindRelation":
          this.bindRelation(params.row.defId);
          break;
        case "delete":
          this.deleteFlow(params.row.defId);
          break;
        case "processSimulation":
          this.$refs.processSimulation.showProcessSimulation(params.row.defKey);
          break;
        default:
          break;
      }
    },
    bindRelation(defId) {
      this.dialogVisible2 = true;
      flow.getBindRelation(defId).then(resp => {
        this.bindData = resp.data;
      });
    },
    cleanData(defId) {
      let this_ = this;
      this.$confirm(
        "此操作会清除该流程所有的实例任务等数据，是否确认清除？",
        "提示",
        {
          cancelButtonText: "取消",
          confirmButtonText: "确定",
          type: "warning",
          closeOnClickModal: false
        }
      ).then(() => {
        req
          .post(
            req.getContext().bpmModel + "/flow/def/v1/cleanData?defId=" + defId
          )
          .then(function(response) {
            let data = response.data;
            if (data.state) {
              this_.$message.success(data.message);
            } else {
              this_.$message.fail(data.message);
            }
          });
      });
    },
    claeaCache() {
      let this_ = this;
      req
        .post("${portal}/i18n/custom/i18nMessage/v1/clearCache")
        .then(function(response) {
          this_.$message.success("操作成功");
        });
    },
    deleteFlow(defId) {
      let this_ = this;
      this.$confirm("删除后不可恢复，是否确定删除?", "提示", {
        cancelButtonText: "取消",
        confirmButtonText: "确定",
        type: "warning",
        closeOnClickModal: false
      }).then(() => {
        req.remove(this_.deleteUrl + "&ids=" + defId).then(function(response) {
          if (response.data && response.data.state) {
            this_.$message.success("删除成功");
            this_.$refs.htTable.load();
          }
        });
      });
    },
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
        req.getContext().bpmModel
      }/flow/def/v1/exportXml?bpmDefId=${ids}`;
      req.download(url);
    },
    //设置分类
    openTypeSetDialog() {
      let elTable = this.$refs.htTable;
      if (this.$refs.htTable.$refs && this.$refs.htTable.$refs.htTable) {
        elTable = this.$refs.htTable.$refs.htTable;
      }
      if (elTable && elTable.selection && elTable.selection.length == 0) {
        this.$message.warning("请选择至少一项记录");
        return;
      }

      this.$refs.typeSetDialog.showDialog({});
    },
    sysTypeDialogOnConfirm(data) {
      let elTable = this.$refs.htTable;
      if (this.$refs.htTable.$refs && this.$refs.htTable.$refs.htTable) {
        elTable = this.$refs.htTable.$refs.htTable;
      }
      if (elTable && elTable.selection && elTable.selection.length == 0) {
        this.$message.warning("请选择至少一项记录");
        return;
      }

      let id = [];

      for (let item of elTable.selection) {
        id.push(item["id"]);
      }
      let this_ = this;
      this.$http
        .post("${bpmModel}/flow/def/v1/defSetCategory", {
          typeName: data.name,
          typeId: data.id,
          defIds: id.join(",")
        })
        .then(function(resp) {
          if (resp.data && resp.data.state) {
            this_.$message({ message: resp.data.message, type: "success" });
            setTimeout(function() {
              this_.$refs.htTable.load();
            }, 3000);

            return;
          }
          this_.$message.error(resp.data.message);
        });
    }
  },
  created() {
    let this_ = this;
    this.$root.$on("set-sub-flow", function(data) {
      this_.activeName = "";
      this_.showSubFlowSet = false;
      req
        .get(
          req.getContext().bpmModel +
            "/flow/def/v1/subFlowDetail?defId=" +
            data.defId +
            "&nodeId=" +
            data.nodeId
        )
        .then(response => {
          let data = response.data;
          this_.topDefKey = data.topDefKey;
          this_.subDefId = data.defId;
          this_.activeName = "subFlowSet";
          this_.isShowSubFlowSet = true;
        });
    });
  }
};
</script>
<style lang="scss" scoped>
@import "@/assets/css/element-variables.scss";

div[aria-invalid="true"] >>> .el-input__inner,
div[aria-invalid="true"] >>> .el-input__inner:focus {
  border-color: #f56c6c;
}
div.flow_conf >>> .el-dialog__header {
  display: none;
}
div.flow_conf >>> .el-dialog__header,
div.flow_conf >>> .el-dialog__body {
  height: 100%;
  padding: 0;
}

div >>> .el-tabs__content > .el-tab-pane {
  height: 100%;
}

div >>> .el-form-item__error {
  display: none;
}

.el-container /deep/ .quick-search {
  width: 300px !important;
}

.flow-edit-container /deep/ > .el-tabs__header {
  width: 180px;
  background: #f5f5f5;
  border-right: 1px solid #eee;
  margin-right: 0;
}

.flow-edit-container
  /deep/
  > .el-tabs__header
  > .el-tabs__nav-wrap
  > .el-tabs__nav-scroll
  > .el-tabs__nav {
  border: none;
}

.flow-edit-container
  /deep/
  > .el-tabs__header
  > .el-tabs__nav-wrap
  > .el-tabs__nav-scroll
  > .el-tabs__nav
  > .el-tabs__item {
  border: none !important;
  color: #666;
  font-weight: bold;
  margin: 5px 0;
  padding-left: 30px !important;
}

.flow-edit-container
  /deep/
  > .el-tabs__header
  > .el-tabs__nav-wrap
  > .el-tabs__nav-scroll
  > .el-tabs__nav
  > .el-tabs__item.is-active {
  background: $--color-primary;
  color: #fff;
}

.flow-edit-container /deep/ > .el-tabs__content {
  height: calc(100% - 3px);
}

.bpmn-xml-link {
  position: fixed;
  bottom: 30px;
  left: 35px;
  z-index: 9999;
}

.bpmn-xml-link /deep/ > .el-link--inner {
  font-size: 13px;
  color: $--color-primary;
}

.bpmn-xml-link /deep/ > .el-link--inner > i {
  font-size: 13px;
  font-weight: bold;
  color: $--color-primary;
}
</style>
