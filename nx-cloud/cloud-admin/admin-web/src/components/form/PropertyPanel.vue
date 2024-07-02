<template>
  <el-form data-vv-scope="field" size="small" inline label-position="left">
    <el-tabs class="property-tabs" v-model="activeTabName" type="card" @tab-click="handleClick">
      <el-tab-pane label="字段属性" name="field">
        <el-collapse v-if="field && !field.isLayout" accordion v-model="avtiveCollapseName">
          <el-collapse-item title="基础属性" name="basic">
            <!-- iframe 配置项显示在基础属性中 -->
            <template v-if="!field.isLayout && field.ctrlType == 'iframe'">
              <BasicsProperty
                :data="field"
                :main-bo-fields="tableFields"
                :all-bo-data="allBoData"
                :bo-def-data="boDefData"
              />
            </template>
            <template v-if="!field.isLayout && !field.noBasics">
              <ht-form-item label-width="100px" v-if="!field.options.noBindModel">
                <template slot="label">
                  <el-tooltip content="请选择字段所绑定的业务对象属性">
                    <i class="property-tip icon-question" />
                  </el-tooltip>
                  <span>绑定属性</span>
                </template>
                <ht-select
                  validate="required"
                  v-model="field.target"
                  :options="tableFields"
                  :props="{ key: 'name', value: 'desc' }"
                  filterable
                  @change="change"
                >
                  <template slot-scope="{ options, propKey, propValue }">
                    <el-option-group v-for="group in options" :key="group.name" :label="group.desc">
                      <el-option
                        v-for="item in group.children.filter(obj => {
                          return filterFields(obj);
                        })"
                        :key="item[propKey]"
                        :label="item[propValue]"
                        :value="item[propKey]"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </ht-form-item>
              <ht-form-item label="字段标题" label-width="100px" v-if="!field.noTitle">
                <ht-input v-model="field.desc" style="width: 215px">
                  <el-button
                    icon="el-icon-search"
                    slot="append"
                    style="width:80px"
                    @click="editI18nMessage('')"
                  >国际化</el-button>
                </ht-input>
              </ht-form-item>
              <ht-form-item label="控件类型" label-width="100px">
                <el-select v-model="field.ctrlType" @change="isChangeWatch">
                  <el-option-group label="基础字段">
                    <el-option
                      v-for="item in basicComponents"
                      :key="item.ctrlType"
                      :label="item.desc"
                      :value="item.ctrlType"
                    />
                  </el-option-group>
                  <el-option-group label="高级字段">
                    <el-option
                      v-for="item in advanceComponents"
                      :key="item.ctrlType"
                      :label="item.desc"
                      :value="item.ctrlType"
                    />
                  </el-option-group>
                </el-select>
              </ht-form-item>

              <ht-form-item label="标题宽度" label-width="100px" v-if="!field.noTitle">
                <ht-input v-model="field.options.labelstyleWidth" placeholder="宽度：100%"></ht-input>
              </ht-form-item>
              <!-- 高德地图 -->
              <template v-if="field.ctrlType == 'amap'">
                <ht-form-item label-width="100px">
                  <template slot="label">
                    <el-tooltip content="请选择地图返回的地址要绑定的字段">
                      <i class="property-tip icon-question" />
                    </el-tooltip>
                    <span>绑定字段</span>
                  </template>
                  <ht-select
                    validate="required"
                    v-model="field.options.addressName"
                    :options="tableFields"
                    :props="{ key: 'name', value: 'desc' }"
                    @change="changeMap"
                  >
                    <template slot-scope="{ options, propKey, propValue }">
                      <el-option-group
                        v-for="group in options"
                        :key="group.name"
                        :label="group.desc"
                      >
                        <el-option
                          v-for="item in group.children.filter(obj => {
                            return filterFields(obj);
                          })"
                          :key="item[propKey]"
                          :label="item[propValue]"
                          :value="item[propKey]"
                        ></el-option>
                      </el-option-group>
                    </template>
                  </ht-select>
                </ht-form-item>
                <ht-form-item label-width="100px">
                  <template slot="label">
                    <el-tooltip content="高德地图控件的高度，默认高度为：350px。">
                      <i class="property-tip icon-question" />
                    </el-tooltip>
                    <span>地图高度</span>
                  </template>
                  <ht-input type="number" v-model="field.options.heightMap" v-if="!field.noTitle"></ht-input>
                </ht-form-item>
              </template>
              <!-- 对应某些控件是基础属性 但又不是所有控件的基础属性放入BasicsProperty组件中 -->
              <BasicsProperty
                :data="field"
                :main-bo-fields="tableFields"
                :all-bo-data="allBoData"
                :bo-def-data="boDefData"
              />
            </template>
          </el-collapse-item>
          <el-collapse-item title="高级属性" name="advanced" v-if="!field.noAdvanced">
            <AdvancedProperty
              :data.sync="field"
              :main-bo-fields="tableFields"
              :all-bo-data="allBoData"
              :boDefData="boDefData"
              :fieldIndexData="fieldIndexData"
            />
          </el-collapse-item>
        </el-collapse>
        <template v-else-if="field && field.isLayout">
          <LayoutProperty
            :data.sync="field"
            :sub-tables="subTables"
            :sun-tables="sunTables"
            :tablefields="tableFields"
            :bo-def-data="boDefData"
          />
        </template>
        <div v-else class="field-empty">选择一个字段进行属性设置</div>
      </el-tab-pane>
      <el-tab-pane
        label="表单属性"
        name="form"
        style="padding: 0 10px 10px;"
        data-vv-scope="editBpmForm"
      >
        <el-form :model="formData" data-vv-scope="editBpmForm">
          <ht-form-item label="表单名称" label-width="73px">
            <ht-input
              v-model="formData.name"
              autocomplete="off"
              :validate="{ required: true }"
              placeholder="请输入名称"
            ></ht-input>
          </ht-form-item>
          <ht-form-item label="表单别名" label-width="73px">
            <ht-input
              v-model="formData.formKey"
              v-pinyin="formData.name"
              :disabled="formData.id ? true : false"
              autocomplete="off"
              :validate="{
              required: true,
              isExist: '${form}/form/form/v1/checkKey?key='
            }"
              placeholder="请输入别名"
            ></ht-input>
            <el-tooltip
              class="item"
              effect="dark"
              content="别名只能填写英文、数字、下划线"
              placement="right-start"
            >
              <span class="el-icon-question" style="margin-left: 10px"></span>
            </el-tooltip>
          </ht-form-item>
          <ht-form-item label="表单分类" label-width="73px">
            <EipSysTypeSelector
              placeholder="请选择表单分类"
              cat-id="7"
              v-model="formData.typeName"
              :sys-type-id.sync="formData.typeId"
              :validate="{ required: true }"
            />
          </ht-form-item>
          <ht-form-item label="描述" label-width="73px">
            <ht-input type="textarea" v-model="formData.desc" placeholder="请输入描述" />
          </ht-form-item>
        </el-form>

        <!-- <h3>表单名称</h3>
        <el-input v-model="formData.name" style="width:100%"  autocomplete="off"
            :validate="{ required: true }"
            placeholder="请输入名称" />
        <h3>表单标识  <el-tooltip class="item" effect="dark" content="别名只能填写英文和数字" placement="right-start">
            <span class="el-icon-question" style="margin-left: 10px"></span>
          </el-tooltip></h3>
        <el-input
          v-model="formData.formKey"
          :disabled="formData.id ? true : false"
          style="width:100%"
           autocomplete="off"
            :validate="{
              required: true,
              alpha_num: true,
              isExist: '${form}/form/form/v1/checkKey?key='
            }"
            placeholder="请输入别名"
        />

        <h3>表单分类</h3>
        <EipSysTypeSelector
            placeholder="请选择表单分类"
            cat-id="7"
            v-model="formData.typeName"
            :sys-type-id.sync="formData.typeId"
            :validate="{ required: true }"
          />
        <h3>描述</h3>
        <el-input
          type="textarea"
          :rows="4"
          placeholder="请输入描述内容"
          v-model="formData.desc"
        />-->
        <!-- <p>
          <el-button icon="icon-technology" size="mini" @click="includdingFile"
            >引入脚本</el-button
          >
        </p>-->
        <!--   <h3>宏模板</h3>
        <ht-select v-model="formData.macroAlias" :options="templateList.macroTemplate"  placeholder="请选择"  :props="{key:'alias',value:'templateName'}" >
        </ht-select>
        <h3>表单模板</h3>
        <ht-select v-model="formData.mainAlias" :options="templateList.mainTemplate"  placeholder="请选择"  :props="{key:'alias',value:'templateName'}" ></ht-select>
        <h3>子实体模板</h3>
        <ht-select v-model="formData.subEntity"  :options="templateList.subTableTemplate"  placeholder="请选择"  :props="{key:'alias',value:'templateName'}"></ht-select>
        -->
        <br />
        <br />
      </el-tab-pane>
    </el-tabs>
    <el-dialog
      title="引入脚本"
      :visible.sync="dialogincluddingFileVisible"
      append-to-body
      class="urgent-text"
      :close-on-click-modal="false"
      width="1024"
    >
      <el-row style="height:100%">
        <el-col :span="12" style="padding:5px;height:100%;">
          <codemirror
            ref="mycode"
            v-model="formHtml"
            :options="cmOptions"
            class="mycode"
            style="width: 99%;height:100%;"
          ></codemirror>
        </el-col>
        <el-col :span="12" style="padding:5px;">
          <ht-form-item label="自定义JS脚本" label-width>
            <el-dropdown @command="variablesClick" size="mini">
              <el-button type="primary">
                常用变量
                <i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item :command="{ value: 'currentUser.account' }">当前用户账号</el-dropdown-item>
                <el-dropdown-item :command="{ value: 'currentUser.username' }">当前用户名称</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </ht-form-item>
          <span style="color: red;">编辑的脚本内容会直接填充到【script】里面；如需使用到表单BO数据例：data.实体表名.实体表字段</span>
          <codemirror
            ref="mycode2"
            v-model="includeFiles.diyJs"
            :options="cmOptions2"
            class="code"
            style="width: 99%;height:100%;"
          ></codemirror>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="includeFilesOk">确 定</el-button>
        <el-button
          @click="
            dialogincluddingFileVisible = false;
            includeFiles.diyJs = '';
          "
        >取 消</el-button>
      </div>
    </el-dialog>
    <ht-load-data :url.sync="formHtmlUrl" context="form" @after-load-data="afterformHtml"></ht-load-data>
    <i18n-message-edit
      ref="i18nMessageEdit"
      :messageKey="i18nMessageKey"
      @after-save="afterSaveI18n"
    />
  </el-form>
</template>

<script>
import form from "@/api/form.js";
import { Base64 } from "js-base64";
import deepmerge from "deepmerge";
import {
  advanceComponents,
  basicComponents,
  layoutComponents
} from "@/api/controlsConfig.js";
import LayoutProperty from "@/components/form/LayoutProperty.vue";
import AdvancedProperty from "@/components/form/AdvancedProperty.vue";
import BasicsProperty from "@/components/form/BasicsProperty.vue";
import htEditor from "@/components/common/HtEditor.vue";
import i18nMessageEdit from "@/components/system/I18nMessageEdit.vue";
import WidgetFormBus from "@/components/form/bus/WidgetFormBus.js";
import utils from "@/hotent-ui-util.js";

const EipSysTypeSelector = () =>
  import("@/components/selector/EipSysTypeSelector.vue");
export default {
  name: "property-panel",
  props: [
    "data",
    "boDefData",
    "formData",
    "mainBoFields",
    "subTables",
    "sunTablesMap"
  ],
  components: {
    LayoutProperty,
    AdvancedProperty,
    BasicsProperty,
    htEditor,
    i18nMessageEdit,
    EipSysTypeSelector
  },
  data() {
    return {
      basicComponents,
      layoutComponents,
      advanceComponents,
      activeTabName: "field",
      avtiveCollapseName: "basic",
      field: this.data,
      formObj: this.formData,
      dialogincluddingFileVisible: false, //引入脚本dialog
      formHtmlUrl: "", //获取表单HTML url
      formHtml: "",
      isWatch: false,
      includeFiles: { diyFile: "", diyCss: "", diyJs: "" }, //引入脚本和样式对象
      tableFields: [],
      filterListType: ["number", "currency", "date", "amap"],
      allBoData: [],
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
      cmOptions2: {
        value: "",
        mode: "javascript",
        readOnly: false,
        smartIndent: true,
        tabSize: 2,
        theme: "base16-light",
        lineNumbers: true,
        line: true
      },
      i18nMessageKey: "",
      createKeyMap: {
        text: true,
        image: true,
        "immediate-single": true,
        "immediate-textarea": true
      },
      sunTables: [],
      currentBoSubEntity: "",
      fieldIndexData: {}
    };
  },
  methods: {
    saveValidate() {
      const this_ = this;
      utils
        .validateForm(this, "editBpmForm")
        .then(() => {
          //手机表单保存时 表单类型指定为手机表单类型
          if (this_.formData.formType && this_.formData.formType != "mobile") {
            this_.formData.formType = "pc";
          }
          // this.formData.name = this.formData.formName;
          this_.$emit("saveEnd");
        })
        .catch(reason => {
          let rules = reason.map(obj => {
            return obj.rule;
          }); //获取到报错后的规则数组
          if (rules.includes("required")) {
            this.$message.error("请完整填写表单内容");
          } else if (rules.includes("isExist")) {
            this.$message.error("表单别名重复");
          }
          this.activeTabName = "form";
        });
    },
    filterFields(obj) {
      if (this.filterListType.indexOf(this.field.ctrlType) > -1) {
        return obj.dataType == this.field.options.dataType;
      } else if (obj.nodeType == "sub") {
        return false;
      } else {
        return true;
      }
    },
    //切换控件类型对应改变属性
    changeCtrlType(isClean) {
      const me_ = this;
      const selectObj = deepmerge({}, me_.field, { clone: true });
      basicComponents.forEach(item => {
        if (me_.field.ctrlType == item.ctrlType) {
          me_.field = deepmerge({}, item, { clone: true });
          me_.field.parentNodeType = me_.data.parentNodeType;
          me_.field.boSubEntity = me_.data.boSubEntity;
          me_.field.key = me_.data.key;
          if (!isClean) {
            me_.field.boDefId = selectObj.boDefId;
            me_.field.target = selectObj.target;
            me_.field.boAttrId = selectObj.boAttrId;
            me_.field.fieldPath = selectObj.fieldPath;
            me_.field.name = selectObj.name;
            me_.field.desc = selectObj.desc;
            me_.field.title = selectObj.desc;
            me_.field.entId = selectObj.entId;
            me_.field.boDefAlias = selectObj.boDefAlias;
            me_.field.tableName = selectObj.tableName;
            me_.field.columnType = selectObj.columnType;
            me_.field.options.format = selectObj.options.format;
            me_.field.options.inputFormat = selectObj.options.format;
            me_.field.options.maxDecimalDigits = selectObj.decimalLen;
          }
          WidgetFormBus.$emit("changeCtrlType", me_.field);
        }
      });
      advanceComponents.forEach(item => {
        if (me_.field.ctrlType == item.ctrlType) {
          me_.field = deepmerge({}, item, { clone: true });
          me_.field.parentNodeType = me_.data.parentNodeType;
          me_.field.boSubEntity = me_.data.boSubEntity;
          me_.field.key = me_.data.key;
          if (!isClean) {
            me_.field.boDefId = selectObj.boDefId;
            me_.field.target = selectObj.target;
            me_.field.boAttrId = selectObj.boAttrId;
            me_.field.fieldPath = selectObj.fieldPath;
            me_.field.name = selectObj.name;
            me_.field.desc = selectObj.desc;
            me_.field.title = selectObj.desc;
            me_.field.entId = selectObj.entId;
            me_.field.boDefAlias = selectObj.boDefAlias;
            me_.field.tableName = selectObj.tableName;
            me_.field.columnType = selectObj.columnType;
            me_.field.options.format = selectObj.format;
            me_.field.options.inputFormat = selectObj.format;
            me_.field.options.maxDecimalDigits = selectObj.decimalLen;
          }
          WidgetFormBus.$emit("changeCtrlType", me_.field);
        }
      });
      me_.$emit("update:data", me_.field);
    },
    //引入脚本和样式对象保存
    includeFilesOk() {
      if (!this.formHtml) {
        this.$message.warning("模板HTML不能为空！");
        return;
      }
      this.dialogincluddingFileVisible = false;
      form
        .saveFormJs({
          formId: this.formData.id,
          diyJs: this.includeFiles.diyJs,
          formHtml: Base64.encode(this.formHtml, "utf-8")
        })
        .then(resp => {
          if (resp.state) {
            this.$message.success(resp.message);
          }
        });
    },
    variablesClick(data) {
      this.includeFiles.diyJs += data.value;
    },
    afterformHtml(data) {
      this.formHtml = data.bpmForm.formHtml;
      this.includeFiles.diyJs = data.bpmForm.diyJs;
    },
    includdingFile() {
      this.dialogincluddingFileVisible = true;
      this.formHtmlUrl =
        "/form/form/v1/previewDesignVue?formId=" + this.formData.id;
      this.includeFiles = this.formData.includeFiles
        ? Base64.decode(this.formData.includeFiles, "utf-8")
        : { diyFile: " ", diyCss: " ", diyJs: " " };
    },
    handleClick(tab, event) {
      console.info(tab);
    },
    // 改变地图地址绑定的字段 设置fieldPath
    changeMap(value) {
      let selectObj = null;
      this.tableFields.forEach(boData => {
        if (!selectObj) {
          selectObj = boData.children.find(opt => opt.name === value);
          if (selectObj) {
            selectObj.boDefAlias =
              boData.boDefAlias || boData.fieldPath.split(".")[0];
            selectObj.tableName = boData.name;
          }
        }
      });
      this.field.options.addressMap = selectObj.path + "." + selectObj.name;
    },
    // 改变绑定的字段 设置fieldPath
    change(value) {
      let selectObj = null;
      this.fieldIndexData = {};
      this.tableFields.forEach(boData => {
        boData.children.forEach((v, i) => {
          if (v.isRequired && v.name == value) {
            if (!this.fieldIndexData.fieldName) {
              this.fieldIndexData.fieldName = v.fieldName;
              this.fieldIndexData.index = i;
            }
          }
        });
        if (!selectObj) {
          selectObj = boData.children.find(opt => opt.name === value);
          if (selectObj) {
            selectObj.boDefAlias =
              boData.boDefAlias || boData.path.split(".")[0];
            selectObj.tableName = boData.name;
          }
        }
      });
      this.field.boDefId = selectObj.boDefId;
      this.field.boAttrId = selectObj.id;
      if (this.field.parentNodeType == "sub") {
        this.field.fieldPath = "item." + selectObj.name;
      } else {
        this.field.fieldPath = selectObj.path + "." + selectObj.name;
      }
      this.field.name = selectObj.name;
      this.field.desc = selectObj.desc;
      this.field.title = selectObj.desc;
      this.field.entId = selectObj.entId;
      this.field.boDefAlias = selectObj.boDefAlias;
      this.field.tableName = selectObj.tableName;
      this.field.columnType = selectObj.columnType;
      if (selectObj.dataType == "date") {
        this.field.options.format = selectObj.format;
        this.field.options.inputFormat = selectObj.format;
      } else if (selectObj.dataType == "number") {
        this.field.options.maxDecimalDigits = selectObj.decimalLen;
      }
    },
    isChangeWatch() {
      this.isWatch = true;
    },
    editI18nMessage(after) {
      this.i18nMessageKey = this.field.fieldPath || this.field.path;
      if (this.createKeyMap[this.field.ctrlType]) {
        this.i18nMessageKey =
          this.formData.formKey +
          "." +
          this.field.ctrlType +
          Math.random() * 5000;
      }
      if (this.i18nMessageKey && after) {
        this.i18nMessageKey += after;
      }
      this.$refs.i18nMessageEdit.handleOpen();
    },
    afterSaveI18n(data) {
      data.key = data.key.replace("$", "#");
      if (data.prop.endsWith("placeholder")) {
        this.field.options.placeholder = data.key;
        this.field.options.placeholder_zh = data.desc;
      } else if (data.prop.endsWith("tip")) {
        this.tooltip = data.key;
      } else {
        this.field.desc = data.key;
        this.field.desc_zh = data.desc;
      }
    }
  },
  watch: {
    data(val) {
      this.field = val;
    },
    field: {
      handler(val) {
        if (val.parentNodeType == "sub" || val.parentNodeType == "sun") {
          if (val.boSubEntity) {
            this.currentBoSubEntity = val.boSubEntity;
            this.tableFields = this.subTables.filter(
              item => item.name === val.boSubEntity
            );
          }
          //如果是孙表，则选取子表里的孙表供选择绑定
          if (
            (val.ctrlType == "suntable" || val.ctrlType == "sunDiv") &&
            this.currentBoSubEntity
          ) {
            this.sunTables = this.sunTablesMap[this.currentBoSubEntity] || [];
          }
          //如果是孙表里的控件，则找到这个一个孙表供其选择属性
          if (val.parentNodeType == "sun") {
            for (const subName in this.sunTablesMap) {
              let sunTabs = this.sunTablesMap[subName];
              if (sunTabs) {
                sunTabs.forEach(sunTab => {
                  if (sunTab.name == this.currentBoSubEntity) {
                    this.tableFields = [sunTab];
                  }
                });
              }
            }
          }
        } else {
          if (val && val.ctrlType == "time") {
            this.tableFields = JSON.parse(JSON.stringify(this.mainBoFields));
            this.tableFields.forEach(table => {
              if (table.children) {
                let newFileds = [];
                table.children = table.children.filter(
                  f => f.dataType != "date" && f.dataType != "number"
                );
              }
            });
          } else {
            this.mainBoFields.forEach(item => {
              for (let q = 0; q < item.children.length; q++) {
                for (let i = 0; i < item.children.length - 1 - q; i++) {
                  if (item.children[i].index > item.children[i + 1].index) {
                    let temp = item.children[i];
                    item.children[i] = item.children[i + 1];
                    item.children[i + 1] = temp;
                  }
                }
              }
            });
            this.tableFields = this.mainBoFields;
          }
        }
        //合并数组对象
        let allBoData = [];
        if (
          ((val.ctrlType == "suntable" || val.ctrlType == "sunDiv") &&
            this.currentBoSubEntity) ||
          val.parentNodeType == "sun"
        ) {
          if (this.sunTables && this.sunTables.length > 0) {
            this.sunTables.forEach(sunTable => {
              if (val.parentNodeType == "sun") {
                if (sunTable.name == val.boSubEntity) {
                  allBoData.push({ ...sunTable });
                }
              } else {
                allBoData.push({ ...sunTable });
              }
            });
          }
        } else {
          this.mainBoFields.forEach(mainBoField => {
            allBoData.push({ ...mainBoField });
          });
          this.subTables.forEach(subTable => {
            allBoData.push({ ...subTable });
          });
        }

        this.allBoData = allBoData;
        this.$emit("update:data", val);
      },
      deep: true
    },
    "field.ctrlType": {
      handler(newVal, oldVal) {
        if (oldVal == undefined || newVal == undefined || newVal == oldVal) {
          return;
        }
        if (!this.isWatch) {
          return;
        }
        if (oldVal == "number" || oldVal == "currency") {
          if (newVal == "date" || newVal == "time") {
            this.changeCtrlType(true);
          } else {
            this.changeCtrlType(false);
          }
        } else if (oldVal == "date" || oldVal == "time") {
          if (newVal == "number" || newVal == "currency") {
            this.changeCtrlType(true);
          } else {
            this.changeCtrlType(false);
          }
        } else {
          if (newVal == "number" || newVal == "currency") {
            if (this.field.columnType == "number") {
              this.changeCtrlType(false);
            } else {
              this.changeCtrlType(true);
            }
          } else if (newVal == "date" || newVal == "time") {
            if (this.field.columnType == "date") {
              this.changeCtrlType(false);
            } else {
              this.changeCtrlType(true);
            }
          } else {
            this.changeCtrlType(false);
          }
        }
        this.isWatch = false;
      }
    }
  },
  mounted() {
    // 声明
    this.$validator = this.$root.$validator;
  },
  destroyed() {}
};
</script>
<style lang="scss" scoped>
@import "@/assets/css/element-variables.scss";
@import "@/assets/css/form-editor.scss";
>>> .el-form-item__content {
  margin-left: 0px !important;
}
>>> .el-dialog__body {
  padding: 5px !important;
  color: #606266;
  font-size: 14px;
  word-break: break-all;
}
>>> .el-dialog__footer {
  padding: 10px !important;
  text-align: right;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
}
.property-tabs {
  min-width: 300px;
}
.property-tabs >>> .el-tabs__nav-scroll {
  padding: 10px 0 0 10px;
}
.mycode >>> .CodeMirror {
  font-family: monospace;
  height: 400px !important;
  color: black;
  direction: ltr;
}
.property-tabs >>> .el-collapse-item__header {
  font-weight: bold;
  color: #999;
  padding-left: 10px;
}

.property-tabs >>> .el-collapse-item__header:hover {
  color: $--color-primary;
}

.property-tabs >>> .el-collapse-item__content {
  padding: 0 10px 10px;
}

.field-empty {
  text-align: center;
  width: 100%;
  margin-top: 150px;
  font-size: 16px;
  color: #ccc;
}

i.property-tip {
  color: #999;
  font-size: 14px;
  margin-right: 3px;
}

i.property-tip:hover {
  color: $--color-primary;
}

.el-aside {
  padding-top: 0px;
}

.el-main {
  padding-top: 0px;
  overflow: hidden;
}
</style>
