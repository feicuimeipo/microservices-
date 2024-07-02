<template>
  <el-container>
    <el-row :gutter="20" style="width:100%;">
      <el-col :span="7">
        <el-table
          ref="displaySettingTable"
          border
          :max-height="tabHeight"
          :data="displaySettingFields"
          tooltip-effect="dark"
          @row-dblclick="fillToDisplay"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column
            prop="name"
            label="列名"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column
            prop="desc"
            label="注释"
            show-overflow-tooltip
          ></el-table-column>
          <el-table-column
            prop="type"
            label="类型"
            width="80"
          ></el-table-column>
        </el-table>
      </el-col>
      <el-col :span="1">
        <el-button
          type="primary"
          circle
          icon="icon-hide"
          title="将所选字段添加为显示字段"
          style="-webkit-transform: rotate(270deg);transform: rotate(270deg)"
          @click="allFillToDisplay()"
        />
      </el-col>
      <el-col :span="16">
        <el-table
          ref="displayTable"
          border
          :max-height="tabHeight"
          :data="displayFields"
          tooltip-effect="dark"
          style="width: 100%;"
        >
          <el-table-column
            label="序号"
            type="index"
            width="50"
          ></el-table-column>
          <el-table-column
            prop="name"
            label="列名"
            width="115"
          ></el-table-column>
          <el-table-column prop="desc" label="注释" width="135">
            <template scope="scope">
              <el-input
                v-model="scope.row.desc"
                placeholder="请输入字段注释"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column
            prop="right"
            :render-header="rightRenderHeader"
            label="显示权限"
            width="220"
          >
            <template scope="scope">
              <span>{{ rightToDesc(scope.row.right) }}</span>
              <span style="float:right;">
                <el-button
                  size="small"
                  @click="setFieldRightDialog(scope.row)"
                  icon="el-icon-edit"
                ></el-button>
              </span>
            </template>
          </el-table-column>
          <el-table-column label="管理">
            <template slot-scope="scope">
              <el-button @click="setting(scope.row)" size="small">
                设置
              </el-button>
              <el-button
                @click="sort(scope.$index, 'down')"
                size="small"
                icon="el-icon-arrow-down"
                plain
              ></el-button>
              <el-button
                @click="sort(scope.$index, 'up')"
                size="small"
                icon="el-icon-arrow-up"
                plain
              ></el-button>
              <el-button
                @click="remove(scope.$index)"
                type="danger"
                size="small"
                icon="el-icon-delete"
                plain
              ></el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-col>
    </el-row>
    <!-- 选择对话框  -->
    <eip-auth-dialog
      ref="eipAuthDialog"
      name="eipAuthDialog"
      @onConfirm="authDialogOnConfirm"
      append-to-body
    />
    <el-dialog
      title="设置"
      width="50%"
      append-to-body
      :visible.sync="settingVisible"
    >
      <el-tabs v-model="activeName" type="card">
        <el-tab-pane label="格式化设置" name="formatter">
          <el-button
            type="primary"
            style="margin-bottom: 10px"
            @click="addSetting"
            >添加</el-button
          >
          <el-table :data="selectedRow.formatterData" border>
            <el-table-column label="值">
              <template slot-scope="scope">
                <el-input v-model="scope.row.key_" />
              </template>
            </el-table-column>
            <el-table-column label="标签">
              <template slot-scope="scope">
                <el-input v-model="scope.row.value_" />
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button
                  icon="el-icon-delete"
                  @click="removeSetting(scope.$index)"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="设置链接" name="url">
          <table class="form-table" cellspacing="0" cellpadding="0" border="0">
            <tbody>
              <tr>
                <td>链接类型</td>
                <td>
                  <el-radio-group v-model="selectedRow.urlType">
                    <el-radio label="edit">查询明细</el-radio>
                    <el-radio label="reportForm">选择其它报表</el-radio>
                    <el-radio label="url">URL地址</el-radio>
                    <el-radio label="noUrl">无链接</el-radio>
                  </el-radio-group>
                </td>
              </tr>
              <tr v-if="selectedRow.urlType == 'url'">
                <td>内容</td>
                <td>
                  <el-input
                    v-model="selectedRow.url"
                    placeholder="请输入url地址"
                  />
                </td>
              </tr>
            </tbody>

            <tbody v-if="selectedRow.urlType == 'reportForm'">
              <tr>
                <td>选择报表</td>
                <td>
                  <ht-input
                    type="text"
                    @focus="showDataTemplateDialog"
                    placeholder="请选择报表"
                    v-model="reportName"
                  >
                    <el-button
                      slot="append"
                      type="primary"
                      icon="el-icon-search"
                      @click="showDataTemplateDialog"
                      >选择</el-button
                    >
                  </ht-input>
                </td>
              </tr>
              <tr v-if="conditionField.length > 0">
                <td>参数关系</td>
                <td>
                  <el-table :data="conditionField">
                    <el-table-column :label="data.data.name">
                      <template slot-scope="scope">
                        <ht-select
                          v-model="scope.row.parameter"
                          :props="{ key: 'name', value: 'desc' }"
                          :options="displayFields"
                        >
                        </ht-select>
                      </template>
                    </el-table-column>
                    <el-table-column :label="reportName">
                      <template slot-scope="scope">
                        <ht-select
                          v-model="scope.row.key"
                          :options="conditionField"
                        >
                        </ht-select>
                      </template>
                    </el-table-column>
                  </el-table>
                </td>
              </tr>
            </tbody>
			<tbody v-if="selectedRow.urlType == 'url'">
              <tr>
                <td>追加参数</td>
                <td>
                    <table class="form-table" cellspacing="0" cellpadding="0" border="0">
                        <tbody>
                          <tr class="linkageTable-tr">
                            <td width="120px">参数名</td>
                            <td width="120px">取值列</td>
                            <td width="120px">
                              操作
                              <el-button size="small"
                                  icon="el-icon-plus"
                                  @click="urlParamsAdd()"
                                ></el-button>
                            </td>
                          </tr>
                          <tr
                            class="linkageTable-tr"
                            v-for="(uparam, index) in urlParams"
                            :key="index"
                          >
                              <td><el-input size="mini" type="text" v-model="uparam.name" /></td>
                              <td>
                                  <ht-select
                                    v-model="uparam.field"
                                    :options="formFieldList"
                                    clearable
                                    filterable
                                    :props="{ key: 'name', value: 'desc' }"
                                  />
                              </td>
                              <td>
                                <el-button size="small"
                                  icon="el-icon-plus"
                                  @click="urlParamsAdd()"
                                ></el-button>
                                <el-button
                                  icon="el-icon-delete"
                                  @click="urlParamsRemove(uparam)"
                                ></el-button>
                              </td>
                          </tr>
                        </tbody>
                    </table>
                </td>
              </tr>
            </tbody>
            <tbody v-if="selectedRow.urlType != 'noUrl'">
              <tr>
                <td>打开方式</td>
                <td>
                  <el-radio-group v-model="selectedRow.openType">
                    <el-radio label="old">当前页面打开</el-radio>
                    <el-radio label="new">新窗口打开</el-radio>
                  </el-radio-group>
                </td>
              </tr>
            </tbody>
          </table>
        </el-tab-pane>
      </el-tabs>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogOk" size="medium"
          >确 定</el-button
        >
        <el-button @click="cancelSetting" size="medium">取 消</el-button>
      </span>
    </el-dialog>

    <eip-data-template-dialog
      :appendToBody="true"
      ref="dataTemplateDialog"
      :single="true"
      @onConfirm="onDataTemplateConfirm"
    ></eip-data-template-dialog>
  </el-container>
</template>

<script>
const eipAuthDialog = () => import("@/components/dialog/EipAuthDialog.vue");
const EipDataTemplateDialog = () =>
  import("@/components/dialog/EipDataTemplateDialog.vue");

export default {
  components: {
    eipAuthDialog,
    EipDataTemplateDialog
  },
  name: "display-setting",
  props: ["data"],
  data() {
    return {
      activeName: "formatter",
      reportName: "",
      conditionField: [],
      dataTemplate: {},
      displaySettingFields: [],
      displayFields: [],
      permissionMap: {},
      permissionList: [],
      rightList: [
        { key: "", value: "请选择" },
        { key: "none", value: "无" },
        { key: "everyone", value: "所有人" }
      ],
      tabHeight: `${document.documentElement.clientHeight}` - 245,
      currentAuthRow: null,
      settingVisible: false,
	  selectedRow: [],
      formFieldList: [],
      urlParams:[],
    };
  },
  mounted() {
    this.dataTemplate = this.data.bpmDataTemplate;
    debugger
    if (this.data.displaySettingFields) {
      this.displaySettingFields = JSON.parse(this.data.displaySettingFields);
      this.displaySettingFields = this.displaySettingFields.filter(item => item.status!=='hidden' && item.status===null);
    }
    this.templates = this.data.templates;
    this.permissionMap = this.data.permissionList;
    this.initData();
  },
  methods: {
    dialogOk() {
      var conditionField = this.conditionField;
      this.selectedRow.parameter = [];
      conditionField.forEach(item => {
        this.selectedRow.parameter.push({
          property: item.key,
          value: item.parameter,
          group: "main",
          operation: item.qt,
          relation: "AND"
        });
      });
	  if(this.selectedRow.urlType == 'url' && this.urlParams){
        this.selectedRow.urlParams = [...this.urlParams];
        this.urlParams = [];
      }
      this.settingVisible = false;
    },
    onDataTemplateConfirm(selectedNode) {
      if (selectedNode && selectedNode.length > 0) {
        this.$nextTick(() => {
          this.conditionField = [];
          var conditionField = JSON.parse(selectedNode[0].conditionField);
          conditionField.forEach(item => {
            var obj = {
              key: item.colPrefix + item.name,
              parameter: "",
              qt: item.qt,
              value: item.cm
            };
            this.conditionField.push(obj);
          });
          this.reportName = selectedNode[0].name;
          this.selectedRow.reportNameConfigure = {
            alias: selectedNode[0].alias,
            reportName: selectedNode[0].name,
            conditionField: this.conditionField
          };
        });
      } else {
        this.reportName = "";
        this.conditionField = [];
        this.selectedRow.reportNameConfigure = {};
      }
    },
    //保存显示列数据
    saveDisplayField() {
      this.dataTemplate.displayField = this.displayFields
        ? JSON.stringify(this.displayFields)
        : null;
    },
    //初始化处理
    initData() {
      if (this.dataTemplate.displayField) {
        this.displayFields = JSON.parse(this.dataTemplate.displayField);
      }
      if (this.permissionMap) {
        for (let key in this.permissionMap) {
          this.permissionList.push({
            type: key,
            title: this.permissionMap[key]
          });
        }
      }
	  if(this.dataTemplate.formField){
        this.formFieldList = [];
        const formFieldList = JSON.parse(this.dataTemplate.formField);
        let fieldMap = {};
        let _this = this;
        formFieldList.forEach(ffield =>{
          if(!fieldMap[ffield.name]){
            _this.formFieldList.push(ffield);
            fieldMap[ffield.name] = true;
          }
        })
      }
    },
    showDataTemplateDialog() {
      this.$refs.dataTemplateDialog.showDialog();
    },
    columnFilter(type) {
      return type != "sub" && type != "tabs";
    },
    //双击字段列表中的字段时将该字段加入到显示字段
    fillToDisplay(row, event, column) {
      let isIn = this.isInDisplayFields(row.name);
      if (!isIn) {
        this.displayFields.push(row);
      }
    },
    //将字段列表中的已选字段加入到显示字段列表
    allFillToDisplay() {
      let selectrows = this.$refs.displaySettingTable.store.states.selection;
      if (!selectrows || selectrows.length < 1) {
        this.$message({
          message: "请在左侧列表中选择要显示的字段",
          type: "warning"
        });
      }
      selectrows.forEach(obj => {
        if (!this.isInDisplayFields(obj.name)) {
          this.displayFields.push(obj);
        }
      });
    },
    //打开设置权限
    setFieldRightDialog(row) {
      let conf = {
        right: JSON.parse(row.right),
        permissionList: this.permissionList
      };
      this.currentAuthRow = row;
      this.$refs.eipAuthDialog.showDialog(conf);
    },
    //设置权限
    authDialogOnConfirm(data) {
      if (this.currentAuthRow) {
        this.currentAuthRow.right = JSON.stringify(data);
      }
    },
    //显示字段排序
    sort(index, type) {
      if ("up" == type) {
        if (index === 0) {
          this.$message({
            message: "已经是列表中第一位",
            type: "warning"
          });
        } else {
          let temp = this.displayFields[index - 1];
          this.$set(this.displayFields, index - 1, this.displayFields[index]);
          this.$set(this.displayFields, index, temp);
        }
      } else {
        if (index === this.displayFields.length - 1) {
          this.$message({
            message: "已经是列表中最后一位",
            type: "warning"
          });
        } else {
          let i = this.displayFields[index + 1];
          this.$set(this.displayFields, index + 1, this.displayFields[index]);
          this.$set(this.displayFields, index, i);
        }
      }
    },

    //删除显示字段
    remove(index) {
      this.displayFields.splice(index, 1);
    },
    //判断字段是否已在显示字段列表中
    isInDisplayFields(name) {
      let isIn = false;
      if (this.displayFields && this.displayFields.length > 0) {
        this.displayFields.forEach(obj => {
          if (obj.name == name) {
            isIn = true;
            return;
          }
        });
      }
      return isIn;
    },
    //显示权限信息
    rightToDesc(right) {
      if (right) {
        right = JSON.parse(right);
      }
      let desc = "";
      let _this = this;
      right.forEach(r => {
        if (desc) {
          desc += " 和 ";
        }
        var str = _this.permissionMap[r.type];
        if (r.name) {
          str += ":" + r.name;
        } else if (r.id) {
          str += ":" + r.id;
        }
        desc += str;
      });
      return desc;
    },
    //标题统一权限设置
    rightRenderHeader(h, para) {
      //下拉框选项
      let _this = this;
      let rights = [
        { key: "", value: "请选择" },
        { key: "none", value: "无" },
        { key: "everyone", value: "所有人" }
      ];
      let rightMap = { "": "请选择", none: "无", everyone: "所有人" };
      //下拉框内容包裹在一个div里面
      return h("div", {}, [
        h(
          "span",
          {
            //div里面有一个文字提示：下拉框所属内容
            style: {},
            class: "level-font-class"
          },
          para.column.label
        ),
        h(
          "el-select",
          {
            //el-select实现下拉框
            size: "mini",
            style: {
              width: "120px",
              marginLeft: "10px"
            },
            on: {
              input: value => {
                //随着下拉框的不同，文字框里的内容在边
                _this.rightLab = rightMap[value];
                if (
                  value &&
                  _this.displayFields &&
                  _this.displayFields.length > 0
                ) {
                  _this.displayFields.forEach(field => {
                    if (field.right) {
                      field.right = JSON.parse(field.right);
                    }
                    if (field.right[0].hasOwnProperty("v")) {
                      field.right[0] = { v: value };
                    } else {
                      field.right[0] = { type: value };
                    }
                    field.right = JSON.stringify(field.right);
                  });
                }
              }
            },
            props: {
              value: _this.rightLab //文字框的内容取决于这个value，如果value不存在，会报错
            }
          },
          [
            //下拉框里面填充选项，通过rights遍历map，为每一个选项赋值。
            rights.map(item => {
              return h("el-option", {
                props: {
                  value: item.key,
                  label: item.value
                }
              });
            })
          ]
        )
      ]);
    },
    setting(row) {
      this.settingVisible = true;
      this.selectedRow = row;
      if(this.selectedRow.reportNameConfigure){
        this.reportName = this.selectedRow.reportNameConfigure.reportName;
        this.conditionField =
          this.selectedRow.reportNameConfigure.conditionField == undefined
            ? []
            : this.selectedRow.reportNameConfigure.conditionField;
      }

      if (!this.selectedRow.formatterData) {
        this.selectedRow.formatterData = [];
      }
	  if(this.selectedRow.urlType == 'url' && this.selectedRow.urlParams && this.selectedRow.urlParams.length>0){
        this.urlParams = [...this.selectedRow.urlParams];
      }
    },
    addSetting() {
      this.selectedRow.formatterData.push({
        key_: "",
        value_: ""
      });
    },
    removeSetting(index) {
      this.selectedRow.formatterData.splice(index, 1);
    },
    cancelSetting() {
      this.settingVisible = false;
      this.selectedRow.formatterData = [];
    },
    urlParamsAdd(){
      this.urlParams.push({name:'',field:''});
    },
    urlParamsRemove(item){
      this.urlParams.remove(item);
    },
  }
};
</script>
<style scoped>
.table > thead > tr > th,
.table > thead > th,
.table > tr > th,
.table > tfoot > tr > th,
.table > thead > tr > td,
.table > tr > td,
.table > tfoot > tr > td {
  border-top: 1px solid #e7eaec;
  border-left: 1px solid #e7eaec;
  line-height: 1.42857;
  padding: 4px 8px;
  vertical-align: middle;
  text-align: center;
}
</style>
