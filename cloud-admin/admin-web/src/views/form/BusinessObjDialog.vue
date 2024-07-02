<template>
  <ht-sidebar-dialog
    :visible.sync="dialogVisible"
    :before-close="handleDialogClose"
    :show-close="false"
    class="cd-column__dialog"
    destroy-on-close
    width="100%"
  >
    <template slot="title">
      <div class="flex" style="justify-content: space-between">
        <el-page-header @back="handleDialogClose" :content="title"></el-page-header>
        <el-button-group>
          <el-button type="primary" @click="validatorForm('save')">保 存</el-button>
          <el-button v-if="!formData.deployed" @click="validatorForm('deployed')">发 布</el-button>
          <el-button @click="validatorForm('createTableForm')">创建表单</el-button>
          <el-button @click="handleDialogClose">取 消</el-button>
        </el-button-group>
      </div>
    </template>
    <el-form :model="formData" data-vv-scope="form">
      <el-container>
        <el-header height="90px">
          <business-obj-header @blur="blur()" :dataView="dataView" @checkIsChinese="checkIsChinese()" ref="objHeader" :formData="formData" />
        </el-header>
        <el-container>
        <el-aside width="320px">
          <business-obj-ents
                  @addEntRows="addEntRows"
                  @addEntExts="addEntExts"
                  @getEntsByIndex="getEntsByIndex"
                  @addGrandSonEnt="addGrandSonEnt"
                  @deleteEntRows="deleteEntRows"
                  @entBlur="entBlur"
                  @chineseFormat="chineseFormat"
                  @checkIsChinese="checkIsChinese"
                  @getGrandSonEntsByIndex="getGrandSonEntsByIndex"
                  @deleteGrandSonEntRows="deleteGrandSonEntRows"
                  :formData="formData"
                  :height="winHeight"
          />
        </el-aside>
        <el-main style="padding-top: 0px;padding-right: 0px;padding-left: 10px;position: relative">
          <business-obj-attr
                  @entBlur="entBlur"
                  @chineseFormat="chineseFormat"
                  @checkIsChinese="checkIsChinese"
                  @getFormData="getFormData"
                  @getEntsByIndex="getEntsByIndex"
                  :openGrandSonAttr="openGrandSonAttr"
                  :attrTableData="attrTableData"
                  :formData="formData"
                  :entIndex="entIndex"
                  :grandSonIndex="grandSonIndex"
          />
        </el-main>
        </el-container>
      </el-container>
    </el-form>

    <ht-sidebar-dialog
      width="60%"
      title="外部表"
      v-if="dialogVisible2"
      :visible="dialogVisible2"
      :before-close="handleClose"
      :append-to-body="true"
      destroy-on-close
    >
      <el-form :inline="true" :model="entExts" data-vv-scope="externalForm" class="external-form">
        <ht-form-item label="描述" prop="comment" label-width="200px">
          <ht-input
            placeholder="请输入描述"
            :disabled="addFk ? true : false"
            v-model="entExts.comment"
            @blur="extsBlur"
            name="entExtsDesc"
            :validate="{ required: true }"
          />
        </ht-form-item>
        <ht-form-item label="名称" prop="name" label-width="200px">
          <ht-input
            placeholder="请输入名称"
            v-pinyin="entExts.comment"
            v-model="entExts.name"
            name="entExtsName"
            @blur="extsBlur"
            :disabled="addFk ? true : false"
            :validate="{ required: true, alpha_dash: true }"
          />
        </ht-form-item>
        <ht-form-item label="数据源" prop="dsName" label-width="200px">
          <ht-select
            :options="dataSource"
            :props="{ key: 'alias', value: 'name' }"
            v-model="entExts.dsName"
            name="entExtsDsName"
            :disabled="addFk ? true : false"
            @change="changeSource();extsBlur()"
            :validate="{ required: true }"
          ></ht-select>
        </ht-form-item>
        <ht-form-item label="表名" prop="searchTableName" label-width="200px">
          <ht-input v-model="searchTableName" style="width: 215px">
            <el-button slot="append" icon="el-icon-search" style="width:45px" @click="getTableList('select')" class="table-name-search"></el-button>
          </ht-input>
        </ht-form-item>
        <ht-form-item label="选择外部表" prop="tableName" label-width="200px">
          <ht-select
            :options="tableList"
            :props="{ key: 'name', value: 'comment' }"
            name="entExtsSelectName"
            :disabled="addFk ? true : false"
            @change="changeTableName();extsBlur()"
            :validate="{ required: true }"
            v-model="entExts.tableName"
          >
          </ht-select>
        </ht-form-item>
        <ht-form-item label="相关配置" label-width="200px" class="config-item">
          <ht-form-item label="主键" label-width="80px">
            <ht-input disabled placeholder="" v-model="entExts.pk"/>
          </ht-form-item>
          <ht-form-item label="外键" label-width="80px">
            <ht-select
              name="fkVal"
              @change="extsBlur()"
              :options="entExtsTable"
              v-model="entExts.fk"
              :props="{key:'name',value:'comment'}"
              clearable
            ></ht-select>
          </ht-form-item>
          <ht-form-item label="主键类型" label-width="80px">
            <el-radio-group v-model="entExts.pkType">
              <el-radio label="varchar">字符串</el-radio>
              <el-radio label="number">数字</el-radio>
            </el-radio-group>
          </ht-form-item>
        </ht-form-item>
      </el-form>
      <el-scrollbar class="scrollbar-fullheight" :style="'max-height:'+entExtsTableHeight+'px;'">
      <el-table
        :data="entExtsTable"
        width="100%"
        border
      >
        <el-table-column label="字段信息" align="center">
          <el-table-column prop="comment" label="注释" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="name" label="名称" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="isRequired" label="是否必填" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span disabled v-if="scope.row.isRequired == 0">否</span>
              <span disabled v-else>是</span>
            </template>
          </el-table-column>
          <el-table-column prop="dataType" label="数据类型" :show-overflow-tooltip="true">
            <template slot-scope="scope">
              <span disabled v-if="scope.row.dataType == 'varchar'">字符串</span>
              <span disabled v-if="scope.row.dataType == 'number'">数字</span>
              <span disabled v-if="scope.row.dataType == 'date'">日期</span>
              <span disabled v-if="scope.row.dataType == 'text'">大文本</span>
            </template>
          </el-table-column>
          <el-table-column prop="fcolumnType" label="属性长度" :show-overflow-tooltip="true"></el-table-column>
          <el-table-column prop="defaultValue" label="默认值" :show-overflow-tooltip="true" width="240">
            <template slot-scope="scope">
              <ht-date v-if="scope.row.dataType == 'date'"  v-model="scope.row.defaultValue" :value-format="scope.row.format"></ht-date>
              <ht-input v-else v-model="scope.row.defaultValue"></ht-input>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      </el-scrollbar>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" :disabled="entExtConfirm" @click="validatorEntExts()">确 定</el-button>
        <el-button @click="handleClose">取 消</el-button>
      </span>
    </ht-sidebar-dialog>
  </ht-sidebar-dialog>
</template>

<script>
    import form from "@/api/form.js";
    import req from "@/request.js";

    const businessObjHeader = () =>
        import("@/views/form/BusinessObjHeader.vue");
const businessObjEnts = () =>
        import("@/views/form/BusinessObjEnts.vue");
const businessObjAttr = () =>
        import("@/views/form/BusinessObjAttr.vue");
export default {
  components: {
    businessObjHeader,
    businessObjEnts,
    businessObjAttr
  },
  props: ["dataView", "deployedId", "clickTree", "data"],
  data() {
    return {
      title: '',
      entExtsTableHeight: 0,
      primaryKeyType: [
        { key: "varchar", value: "字符串" },
        { key: "number", value: "数字" }
      ],
      typeData: [],
      entIndex: 0, //选中实体下标
      dataSource: [],
      tableList: [], //外部表
      entExtsTable: [], //外部表表格
      isHide:true,
      searchTableName:"",
      entExts: {
        comment: "",
        desc: "",
        dsName: "",
        index: 0,
        isExternal: "1",
        name: "",
        packageId: "",
        pk: "",
        pkType: "",
        relation: "",
        show: "",
        status: "",
        tableName: "",
        attributeList: []
      },
      formData: {
        //表单
        alias: "",
        categoryId: "",
        categoryName: "",
        deployed: 0,
        description: "",
        dsName: "",
        isCreateTable: "",
        isExternal: "",
        status: "normal",
        supportDb: true,
        ents: [

        ]
      },
      countAttrLength: 0,
      dialogVisible: false,
      dialogVisible2: false,
      openGrandSonAttr: false,
      grandSonIndex: -1,
      attrTableData: [],
      winHeight: 969,
      hideAttr: [],
      addFk: false,
      extLen: 0,
      showMessage: false,
      entExtConfirm: false
    };
  },
  created() {
    this.$validator = this.$root.$validator;
  },
  watch: {
    data: function (v) {
      if (this.dataView.id) {
        this.title = '编辑建模';
        this.formData = v;
      }
    }
  },
  mounted(){
    this.maxHeight();
  },
  methods: {
    maxHeight() {
      let height = this.winHeight - 280;
      if(this.winHeight == height){
        return;
      }
      if (window.innerHeight) {
        this.winHeight = window.innerHeight;
      } else if ((document.body) && (document.body.clientHeight)) {
        this.winHeight = document.body.clientHeight;
      }
      this.winHeight = this.winHeight - 280;
    },
    addGrandSonEnt(index){
      if (!this.validatorForm()) {
        return false;
      }
      this.getEntsByIndex(index, "noCheck");
      let row = {
        desc: "",
        index: this.entIndex,
        name: "",
        packageId: "",
        show: "孙实体",
        relation: "onetoone",
        attributeList: [],
        status: "",
        isCheck: true
      };
      if(!this.formData.ents[index].children){
        this.$set(this.formData.ents[index],"children",[])
      }
      if(this.checkGrandSonEnts(index)){
        return;
      };
      this.formData.ents[index].children.push(row);
      this.grandSonIndex = this.formData.ents[index].children.length-1;
      this.openGrandSonAttr = true;
      this.attrTableData = this.formData.ents[index].children[this.grandSonIndex].attributeList;
    },
    checkGrandSonEnts(index,param){
      if (this.formData.ents[index].children && this.formData.ents[index].children.length == 0 || !this.formData.ents[index].children) {
        return false;
      }
      let child = this.formData.ents[index].children;
      let len = this.formData.ents[index].children.length - 1;
      child[len].comment = child[len].desc;
      child[len].description = child[len].desc;
      if (!child[len].desc) {
        this.$message({message: "请输入孙实体描述",type: "warning"});
        return true;
      }else if(!child[len].name){
        this.$message({message: "请输入孙实体名称",type: "warning"});
        return true;
      }else if(!param && child[len].attributeList.length == 0){
        this.$message({message: "请添加字段信息",type: "warning"});
        return true;
      }else if(child[len].attributeList.length > 0){
        for(let i=0; i<child[len].attributeList.length; i++){
          if (!child[len].attributeList[i].name){
            this.$message({message: "请输入注释",type: "warning"});
            return true;
          }else if (!child[len].attributeList[i].name) {
            this.$message({message: "请输入字段名称",type: "warning"});
            return true;
          }
        }
      }
    },
    //输入检测
    checkIsChinese(param) {
      var check = new RegExp("[\u4e00-\u9fa5]");
      var pattern = new RegExp(
        "[%--`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——| {}【】‘；：”“'。，、？]"
      );
      if (check.test(param) || pattern.test(param)) {
        this.$message({ message: "请勿输入中文或特殊字符", type: "warning" });
        return true;
      }
    },
    //输入检测
    checkInSpecialCharacters(param) {
      var pattern = new RegExp(
        "[%--`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——| {}【】‘；：”“'。，、？]"
      );
      if (pattern.test(param)) {
        this.$message({ message: "请勿输入特殊字符", type: "warning" });
        return true;
      }
    },
    getGrandSonEntsByIndex(i, index){
      if (!this.getEntsByIndex(index)) {
        return false;
      }
      this.openGrandSonAttr = true;
      this.grandSonIndex = i;
      if(this.formData.ents[this.entIndex].children && this.formData.ents[this.entIndex].children[i]) {
        this.attrTableData = this.formData.ents[this.entIndex].children[i].attributeList;
      }
    },
    //获取选中实体
    getEntsByIndex(index, param) {
      let isCheckSon = this.openGrandSonAttr;
      this.openGrandSonAttr = false;
      if(!this.passReq){
        if(this.formData.ents[index] && (this.formData.ents[index].isCheck || this.formData.ents[index].isCheck && isCheckSon) && !param) {
          //this.grandSonIndex = -1;
          let resultValidate = this.validatorForm();
          if(!(resultValidate == true) ){
            if(isCheckSon){
              this.openGrandSonAttr = true;
            }
            return false;
          }
        }
      }
      if(this.formData.ents[index]) {
        this.entIndex = index;
        this.formData.ents.forEach(data =>{
          data.isCheck = false;
        })
        this.formData.ents[index].isCheck = true;
        return true;
      }
    },

    //表单对话框
    handleOpen() {
      this.dialogVisible = true;
      this.init();
    },
    init(){
      this.title = '新建建模';
      this.formData.categoryId = this.clickTree.id;
      this.formData.categoryName = this.clickTree.name;
      if (this.dataView.entIndex) {
        this.entIndex = this.dataView.entIndex;
      }
    },
    //表单数据加载
    getFormData() {
      if(this.dataView.id) {
        form.getEntData(this.dataView.id, resp => {
          resp.data.ents.forEach((entData, index) => {
            if (entData.isExternal == '1' && index == 0) {
              entData.show = "主实体-外";
            } else if (entData.isExternal == '1') {
              entData.show = "子实体-外";
            }
            if (index === 0) {
              entData.isCheck = true;
            }
            if (entData.children) {
              entData.children.forEach(v => {
                if (v.isExternal == '1') {
                  v.show = "孙实体-外";
                }
              })
            }
            for (let q = 0; q < entData.attributeList.length; q++) {
              if (entData.attributeList[q]) {
                if (entData.attributeList[q].isRequired == 0) {
                  entData.attributeList[q].isRequired = '0';
                } else {
                  entData.attributeList[q].isRequired = '1';
                }
              }
              for (let i = 0; i < entData.attributeList.length - 1 - q; i++) {
                if (entData.attributeList[i].index > entData.attributeList[i + 1].index) {
                  let temp = entData.attributeList[i];
                  entData.attributeList[i] = entData.attributeList[i + 1];
                  entData.attributeList[i + 1] = temp;
                }
              }
            }
          })
          this.formData = resp.data;
        });
      }
    },
    //关闭表单对话框
    handleDialogClose() {
      this.$emit("loadTableData");
      this.dialogVisible = false;
      this.dialogVisible2 = false;
      this.isSave = false;
      this.entIndex = 0;
      this.grandSonIndex = -1;
      this.openGrandSonAttr = false;
      this.formData = {
        alias: "",
        categoryId: "",
        categoryName: "",
        deployed: 0,
        description: "",
        dsName: "",
        isCreateTable: "",
        isExternal: "",
        status: "normal",
        supportDb: true,
        ents: []
      }
    },
    //新增实体
    addEntRows() {
      if(this.formData.ents.length > 0){
        let resultValidate = this.validatorForm();
        if(!(resultValidate == true) ){
          return;
        }
      }
      this.formData.ents.forEach(data =>{
        data.isCheck = false;
      })
      this.entIndex = ++this.entIndex;
      let row = {
        desc: "",
        index: this.entIndex,
        name: "",
        packageId: "",
        show: "子实体",
        children: [],
        relation: "onetoone",
        attributeList: [],
        status: "",
        isCheck: true
      };
      if(this.formData.ents.length==0){
        this.entIndex=0;
        row={
          desc: "",
          index: 0,
          name: "",
          packageId: "",
          show: "主实体",
          relation: "main",
          status: "",
          attributeList: [],
          isCheck: true
        }
      }
      if(this.formData.ents.length > 0 && this.checkGrandSonEnts(this.formData.ents.length - 1)){
        return;
      }
      this.formData.ents.push(row);
    },
    //删除实体
    deleteEntRows(index) {
      this.entIndex = index;
      if (!this.formData.ents[this.entIndex].attributeList) {
        this.formData.ents[this.entIndex].attributeList = [];
      }
      this.formData.ents.splice(index, 1);
      if(this.entIndex>=this.formData.ents.length){
        this.entIndex=this.formData.ents.length-1;
        this.getEntsByIndex(this.entIndex)
      }
      if(this.formData.ents.length>0){
        if (index == 0) {
          if(this.formData.ents[0].isExternal != "1"){
            this.formData.ents[0].show = "主实体";
            this.formData.ents[0].relation = "main";
            this.formData.ents[0].children = [];
          }else{
            this.formData.ents[0].show = "主实体-外";
            this.formData.ents[0].relation = "main";
          }
        }
      }
    },
    deleteGrandSonEntRows(i,index) {
      this.getEntsByIndex(index, "noCheck");
      this.grandSonIndex = i;
      if (!this.formData.ents[this.entIndex].attributeList) {
        this.formData.ents[this.entIndex].children[i].attributeList = [];
      }
      this.formData.ents[this.entIndex].children.splice(i, 1);
      this.openGrandSonAttr = false;
      this.attrTableData = this.formData.ents[this.entIndex].attributeList;
      if(this.formData.ents[this.entIndex].children[i-1] && this.formData.ents[this.entIndex].children[i-1].attributeList.length>0){
        this.attrTableData = this.formData.ents[this.entIndex].children[i-1].attributeList;
      }
    },
    //数据源
    addEntExts(param) {
      this.dialogVisible2 = true;
      form.getDataSource().then(resp => {
        this.dataSource = resp.data;
        if(!this.entExts.dsName){
          this.entExts.dsName = "LOCAL";
          this.changeSource();
        }
      });
      if(param && this.formData.ents[this.entIndex].tableName){
        this.entExts.comment = this.formData.ents[this.entIndex].comment;
        this.entExts.name = this.formData.ents[this.entIndex].name;
        this.entExts.dsName = this.formData.ents[this.entIndex].dsName;
        this.entExts.tableName = this.formData.ents[this.entIndex].tableName;
        this.getTableList();
        setTimeout(() =>{
          this.changeTableName();
        },500);
        this.addFk = true;
      }
    },
    changeSource(){
      this.entExts.tableName="";
      this.searchTableName="";
      this.entExtsTable=[];
      this.getTableList();
    },
    //外部表数据
    getTableList(type) {
      const this_ = this;
      this.entExtConfirm = true;
      if(type){
        this.showMessage = true;
      }
      if(!this.entExts.dsName){
        return;
      }
      var param = {
        dsalias: this.entExts.dsName,
        isTable: "1",
        objName: this.searchTableName
      };
      form.getTableList(param).then(resp => {
        if(resp.data){
          resp.data.forEach(v => {
            v.comment = v.name + "(" + v.comment + ")";
          });
          if(this.showMessage){
            this.$message({ message: "查询成功，请选择外部表", type: "success" });
          }
          this.tableList = resp.data;
        }
      })
      .finally(() => {
        this.entExtConfirm = false;
        this.showMessage = false;
      });
    },
    //外部表选择
    changeTableName(data) {
        this.entExtConfirm = true;
        let winHeight = 969;
        if (window.innerHeight) {
          winHeight = window.innerHeight;
        } else if ((document.body) && (document.body.clientHeight)) {
          winHeight = document.body.clientHeight;
        }
      this.entExtsTableHeight=winHeight - 555;
      let param = {
        dsalias: this.entExts.dsName,
        isTable: "1",
        objName: this.entExts.tableName
      };
      form.getHideAttr(param.objName).then(resp =>{
        if(resp){
          this.hideAttr = resp;
        }
      }).then(() =>{
        form.changeTableName(param).then(resp => {
          this.entExtsTable = JSON.parse(
                  JSON.stringify(resp.data.table.columnList)
                          .replace(/charLen/g, "attrLength")
                          .replace(/fieldName/g, "name")
                          .replace(/columnType/g, "dataType")
                          .replace(/fdataType/g, "fcolumnType")
          );
          this.entExtsTable.forEach(item =>{
            item.fieldName = item.name;
          });
          if(this.hideAttr.length>0){
            let attr = "";
            this.hideAttr.forEach(v =>{
              attr +=v.fieldName + ","
            })
            this.entExtsTable = this.entExtsTable.filter(value => {
              return !attr.includes(value.fieldName);
            })
          }
          resp.data.table.primayKey.forEach(data =>{
            this.entExts.pk = data.fieldName;
            this.entExts.pkType = data.columnType;
          })
          this.hideAttr = [];
        })
        .finally(() => {
          this.entExtConfirm = false;
        });
      })
    },
    validatorForm(param) {
      this.passReq = false;
      for(let i=0; i<this.formData.ents.length; i++){
        if(this.checkGrandSonEnts(i)){
          return ;
        }
      }
      this.countAttrLength = 0;
      this.checkEnt=true;
      this.checkAttr=true;
      this.checkDefaultValue=true;
      if (
        this.checkIsChinese(this.formData.alias) ||
        this.checkInSpecialCharacters(this.formData.description)
      ) {
        return;
      }
      if (!this.formData.description) {
        this.$message({ message: "请输入描述", type: "warning" });
        return false;
      } else if (!this.formData.alias) {
        this.$message({ message: "请输入别名", type: "warning" });
        return false;
      } else if (!this.formData.categoryId) {
        this.$message({ message: "请选择分类", type: "warning" });
        return false;
      }
      if(!this.checkEntFunc(this.formData.ents)){
        return false;
      };
      this.extLen = 0;
      if (param == "deployed") {
        this.deployedEntData();
        return true;
      } else if (param == "save") {
        this.saveEntData();
        return true;
      } else if (param == "createTableForm") {
        this.createTableForm();
        return true;
      }
      return true;
    },
    changeDataType(){
      let formData=this.formData;
      if(formData.deployed){
        formData.ents.forEach(e =>{
          e.attributeList.forEach(a =>{
            switch (a.dataType) {
              case "数字":
                a.dataType = "number";
                break;
              case "字符串":
                a.dataType = "varchar";
                break;
              case "日期":
                a.dataType = "date";
                break;
              case "大文本":
                a.dataType = "clob";
                break;
            }
          })
        })
      }
    },
    changeDataTypeToChinese(){
      let formData=this.formData;
      if(formData.deployed){
        formData.ents.forEach(e =>{
          e.attributeList.forEach(a =>{
            switch (a.dataType) {
              case "number":
                a.dataType = "数字";
                break;
              case "varchar":
                a.dataType = "字符串";
                break;
              case "date":
                a.dataType = "日期";
                break;
              case "clob":
                a.dataType = "大文本";
                break;
            }
          })
        })
      }
    },
    checkEntFunc(ent){
      let entVal = {};
      if(ent.length < 1){
        this.$message({type: "warning", message: "请添加实体对象！"});
        return false;
      }
      for (let i = 0; i < ent.length; i++) {
        ent[i].description = ent[i].comment;
        ent[i].desc = ent[i].comment;
        if(!entVal['desc'+ent[i].desc]){
          entVal['desc'+ent[i].desc] = ent[i].desc;
        }else{
          this.$message({ message: "实体描述重复", type: "warning" });
          this.entBlurDescRepeatIndex=i;
          if(document.getElementById('changeEntsDesc'+i)){
            document.getElementById('changeEntsDesc'+i).focus();
            document.getElementById('changeEntsDesc'+i).style.border="1px solid red";
          }
          return false;
        }
        if(!entVal['name'+ent[i].name]){
          entVal['name'+ent[i].name] = ent[i].name;
        }else{
          this.entBlurNameRepeatIndex=i;
          this.$message({ message: "实体名称重复", type: "warning" });
          if(document.getElementById('entName'+i)){
            document.getElementById('entName'+i).getElementsByTagName("div")[0].lastElementChild.focus();
            document.getElementById('entName'+i).getElementsByTagName("div")[0].firstElementChild.style.border="1px solid red";
          }
          return false;
        }
        if(ent[i].isExternal == "1"){
          ++this.extLen;
          if(i != 0 && this.extLen > 1 && !ent[i].fk){
            this.$message({ message: "请给"+ ent[i].comment +"实体添加外键！", type: "warning" });
            this.passReq = true;
            return false;
          }
        }
        if (this.checkIsChinese(ent[i].name)) {
          this.checkEnt=false;
          this.checkEntIndex=i;
          if(!ent[i].show.includes("孙实体")){
            document.getElementById('entName'+i).getElementsByTagName("div")[0].firstElementChild.style.border="1px solid red";
            document.getElementById('entName'+i).getElementsByTagName("div")[0].firstElementChild.focus();
          }
          return false;
        }
        if (!ent[i].comment) {
          this.$message({ message: "请输入实体描述", type: "warning" });
          return false;
        } else if (!ent[i].name) {
          this.$message({ message: "请输入实体名称", type: "warning" });
          return false;
        } else if (ent[i].attributeList.length > 0) {
          if(!this.checkAttrFunc(ent[i], i)){
            return false;
          }
        } else {
          this.$message({ message: "请添加字段！", type: "warning" });
          return false;
        }
        if(ent[i].children && ent[i].children.length > 0){
          this.countAttrLength = 0;
          if(!this.checkEntFunc(ent[i].children)){
            return false;
          }
        }
        this.countAttrLength = 0;
      }
      return true;
    },
    checkAttrFunc(ent, i){
      let regPos = new RegExp("^\\d*(?:\\.\\d{1,2})?$");
      let attrVal = {};
      let attr = ent.attributeList;
      for (let j = 0; j < attr.length; j++) {
        attr[j].desc = attr[j].comment
        var checkName = new RegExp("^[a-zA-Z]+[a-zA-z0-9_]*$");
        attr[j].defaultValue = attr[j].defaultValue.trim();
        if (!attr[j].name) {
          this.$message({message: "请输入字段名！", type: "warning"});
          return false;
        } else if (!attr[j].comment) {
          this.$message({message: "请输入字段注释！", type: "warning"});
          this.entAttrIndex = i;
          this.attrCommentElementId = attr[j].name + 'comment' + j;
          document.getElementById(this.attrCommentElementId).focus();
          document.getElementById(this.attrCommentElementId).style.border = "1px solid red";
          return false;
        }
        this.attrCommentElementId = attr[j].name + 'comment' + j;
        if (ent.isExternal != '1') {
          if (!checkName.test(attr[j].name)) {
            this.$message({message: "字段名称只能输入字母、数字、下划线，且以字母开头", type: "warning"});
            this.attrNameElementId = attr;
            this.entAttrIndex = i;
            this.attrNameIndex = j;
            document.getElementById(this.attrNameElementId[j].name + "attrName" + j).focus();
            document.getElementById(this.attrNameElementId[j].name + "attrName" + j).getElementsByTagName("div")[0].firstElementChild.style.border = "1px solid red";
            return false;
          }
          if (!attrVal['name' + attr[j].name]) {
            attrVal['name' + attr[j].name] = attr[j].name;
          } else {
            this.entAttrIndex = i;
            this.attrNameIndex = j;
            this.attrNameElementId = attr;
            document.getElementById(this.attrNameElementId[j].name + "attrName" + j).focus();
            document.getElementById(this.attrNameElementId[j].name + "attrName" + j).getElementsByTagName("div")[0].firstElementChild.style.border = "1px solid red";
            this.$message({message: "字段名重复", type: "warning"});
            return false;
          }
          if (this.checkIsChinese(attr[j].name)) {
            this.entAttrIndex = i;
            this.checkAttr = false;
            this.checkAttrNameIndex = j;
            document.getElementById(attr[j].name + j).getElementsByTagName("div")[0].firstElementChild.focus();
            document.getElementById(attr[j].name + j).getElementsByTagName("div")[0].firstElementChild.style.border = "1px solid red";
            return false;
          }
          this.attrLenElementId = attr[j].name + 'attrLen' + j;
          if (attr[j].attrLength && attr[j].attrLength !== 0) {
            if (!regPos.test(attr[j].attrLength)) {
              this.entAttrIndex = i;
              document.getElementById(this.attrLenElementId).focus();
              document.getElementById(this.attrLenElementId).style.border = "1px solid red";
              this.$message({message: "请输入正确数字", type: "warning"});
              return false;
            }
          }
          this.attrDecimaElementId = attr[j].name + 'decima' + j;
          if (attr[j].decimalLen &&
                  attr[j].decimalLen !== 0) {
            if (!regPos.test(attr[j].decimalLen)) {
              this.entAttrIndex = i;
              document.getElementById(this.attrDecimaElementId).focus();
              document.getElementById(this.attrDecimaElementId).style.border = "1px solid red";
              this.$message({message: "请输入正确数字", type: "warning"});
              return false;
            }
          }
          if (attr[j].decimalLen) {
            if (
                    attr[j].decimalLen.length > 1
            ) {
              let decimalLen = attr[j].decimalLen.substr(0, 1);
              if (decimalLen == "0") {
                attr[j].decimalLen = attr[j].decimalLen.substr(1, attr[j].decimalLen.length);
              }
            }
            if (attr[j].decimalLen < 0 || attr[j].decimalLen > 30) {
              this.entAttrIndex = i;
              document.getElementById(this.attrDecimaElementId).focus();
              document.getElementById(this.attrDecimaElementId).style.border = "1px solid red";
              this.$message({
                message: "小数长度不能小于0或者大于30",
                type: "warning"
              });
              return false;
            }
          }
          if (attr[j].dataType === "varchar") {
            this.countAttrLength += parseInt(attr[j].attrLength);
          }
          if (attr[j].attrLength) {
            if (attr[j].attrLength.length > 1) {
              let attrLength = attr[j].attrLength.substr(0, 1);
              if (attrLength == "0") {
                attr[j].attrLength = attr[j].attrLength.substr(1, attr[j].attrLength.length);
              }
            }
            if (attr[j].dataType === "varchar") {
              if (attr[j].attrLength < 1 || this.countAttrLength > 21500) {
                this.entAttrIndex = i;
                document.getElementById(this.attrLenElementId).focus();
                document.getElementById(this.attrLenElementId).style.border = "1px solid red";
                this.$message({
                  message: "整数长度不能小于1或者长度总和超出21500",
                  type: "warning"
                });
                return false;
              }
            }
            if (attr[j].dataType === "number" || attr[j].dataType == "数字") {
              let defaultVal = attr[j].defaultValue.split(".");
              if (Number(attr[j].attrLength) < 1) {
                this.entAttrIndex = i;
                document.getElementById(this.attrLenElementId).focus();
                document.getElementById(this.attrLenElementId).style.border = "1px solid red";
                this.$message({message: "整数长度不能小于1", type: "warning"});
                return false;
              }
              if ((Number(attr[j].attrLength) + Number(attr[j].decimalLen)) > 65) {
                this.entAttrIndex = i;
                document.getElementById(this.attrLenElementId).focus();
                document.getElementById(this.attrLenElementId).style.border = "1px solid red";
                this.$message({message: "整数长度与小数长度之和不能超过65", type: "warning"});
                return false;
              }
              if (defaultVal[1] && Number(defaultVal[1].length) > Number(attr[j].decimalLen)) {
                this.defaultValueIndex = j;
                this.defaultValueType = "number";
                this.checkDefaultValue = false;
                document.getElementsByName('defaultValue' + j)[0].focus();
                document.getElementsByName('defaultValue' + j)[0].style.border = "1px solid red";
                this.$message({message: "默认值中小数长度超出设置值", type: "warning"});
                return false;
              }
              if (!regPos.test(Number(attr[j].defaultValue))) {
                this.defaultValueIndex = j;
                this.defaultValueType = "number";
                this.checkDefaultValue = false;
                document.getElementsByName('defaultValue' + j)[0].focus();
                document.getElementsByName('defaultValue' + j)[0].style.border = "1px solid red";
                this.$message({message: "默认值应为数字", type: "warning"});
                return false;
              }
            }
            let d = attr[j].defaultValue;
            if (d.indexOf(".") > -1) {
              let i = d.indexOf(".");
              let start = d.substring(0, i);
              let end = d.substring(i + 1, attr[j].defaultValue.length);
              d = start + end;
            }
            if (d.length > (attr[j].attrLength + attr[j].decimalLen)) {
              this.defaultValueIndex = j;
              this.defaultValueType = "varchar";
              this.checkDefaultValue = false;
              document.getElementsByName('defaultValue' + j)[0].focus();
              document.getElementsByName('defaultValue' + j)[0].style.border = "1px solid red";
              this.$message({message: "默认值长度需小于整数长度", type: "warning"});
              return false;
            }
          } else if (attr[j].dataType === "varchar" || attr[j].dataType === "number") {
            this.entAttrIndex = i;
            document.getElementById(this.attrLenElementId).focus();
            document.getElementById(this.attrLenElementId).style.border = "1px solid red";
            this.$message({message: "请添加整数长度", type: "warning"});
            return false;
          }
        }
      }
      return true;
    },
    entBlur(index){
      if(this.formData.ents[this.entIndex] && (this.formData.ents[this.entIndex].attributeList.length>0 || this.formData.ents[this.entIndex].children && this.formData.ents[this.entIndex].children.length>0 && this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList.length > 0)){
        if (!this.validatorForm()) {
          return false;
        }
      }
      if(this.openGrandSonAttr && this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList && this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList.length > 0){
        this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList[index].desc = this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList[index].comment;
      }
      if(typeof this.entBlurDescRepeatIndex == "number"){
          document.getElementById('changeEntsDesc'+this.entBlurDescRepeatIndex).style.border='';
      }
      if(typeof this.entBlurNameRepeatIndex == "number"){
          document.getElementById('entName'+this.entBlurNameRepeatIndex).getElementsByTagName("div")[0].firstElementChild.style.border='';
      }
      if(this.checkEnt && typeof this.checkEntIndex == "number"){
          document.getElementById('entName'+this.checkEntIndex).getElementsByTagName("div")[0].firstElementChild.style.border='';
          this.checkEnt=true;
      }
      if(this.attrCommentElementId && document.getElementById(this.attrCommentElementId)){
        document.getElementById(this.attrCommentElementId).style.border="";
      }
      if(typeof this.attrNameIndex == "number"){
          document.getElementById(this.attrNameElementId[this.attrNameIndex].name+"attrName"+this.attrNameIndex).getElementsByTagName("div")[0].firstElementChild.style.border="";
      }
      if(this.checkAttr && typeof this.checkAttrNameIndex == "number"){
            document.getElementById(this.formData.ents[this.entAttrIndex].attributeList[this.checkAttrNameIndex].name+this.checkAttrNameIndex).getElementsByTagName("div")[0].firstElementChild.style.border="";
            this.checkAttr=true;
      }
      if(this.checkDefaultValue && this.defaultValueType){
            document.getElementsByName('defaultValue'+this.defaultValueIndex)[0].style.border="";
            this.checkAttr=true;
      }
      if(this.attrLenElementId && document.getElementById(this.attrLenElementId)){
            document.getElementById(this.attrLenElementId).style.border="";
      }
      if(this.attrDecimaElementId && document.getElementById(this.attrDecimaElementId)){
            document.getElementById(this.attrDecimaElementId).style.border="";
      }
    },
    //发布并保存实体
    deployedEntData() {
      let formData=this.formData;
      this.formData.deployed = true;
      form.saveEntData(this.formData).then(resp => {
        if (resp.data.state) {
          this.$confirm("发布成功，是否跳转到业务表单页面？", "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          })
            .then(() => {
              this.handleDialogClose();
              if(!formData.id){
                  formData.id=resp.data.message;
                  formData.pkVal=resp.data.message;
              }
              this.$router.push({path:"/form#formManager",query:{
                bos:[formData]
              }});
            })
              .catch(() => {
                  this.handleDialogClose();
                  this.dataView.id = "";
              });
        }
      }).catch(() => {
          this.formData.deployed = false;
      });;
    },
    //保存实体
    saveEntData() {
       let formData=this.formData;
      this.changeDataType();
       if(this.isSave){
         this.formData.deployed = true;
       }
      form.saveEntData(this.formData).then(resp => {
        this.changeDataTypeToChinese();
        if (resp.data.state) {
          let message = '是否继续操作？';
          if(this.isSave){
              message = '是否跳转到业务表单页面';
          }
          this.$confirm("保存成功，"+message, "提示", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          })
            .then(() => {
              if(this.isSave){
                if(!formData.id){
                  formData.id=resp.data.message;
                  formData.pkVal=resp.data.message;
                }
                this.$router.push({path:"/form#formManager",query:{
                  bos:[formData]
                }});
              }
              if (resp.data.message != null) {
                this.dataView.id = resp.data.message;
              }
              this.handleDialogClose();
              this.$emit("opreta", "edit");
              this.$emit("opretaId", this.dataView.id);
            })
              .catch(() => {
                  this.$emit("loadTableData");
                  this.handleDialogClose();
                  this.dataView.id = "";
              });
        }
      });
    },
    handleClose() {
      this.dialogVisible2 = false;
      this.addFk = false;
      this.tableList = [];
      this.entExts = {
        desc: "",
        dsName: "",
        index: 0,
        isExternal: "1",
        name: "",
        packageId: "",
        pk: "",
        pkType: "",
        relation: "",
        show: "",
        status: "",
        tableName: "",
        attributeList: []
      };
      this.entExtsTable = [];
    },
    validatorEntExts(){
        const checkName = new RegExp("^[a-zA-Z]+[a-zA-z0-9_]*$");
        if (!this.entExts.comment) {
            this.$message({ message: "请输入描述", type: "warning" });
            return;
        }
        if (!this.entExts.name) {
            this.$message({ message: "请输入名称", type: "warning" });
            return;
        }
        if (!this.entExts.dsName) {
            this.$message({ message: "请选择数据源", type: "warning" });
            return;
        }
        if (!this.entExts.tableName) {
            this.$message({ message: "请选择外部表", type: "warning" });
            return;
        }
        for (let i = 0; i < this.entExtsTable.length; i++) {
          if (!checkName.test(this.entExtsTable[i].fieldName)) {
            this.$message({message: "字段名称只能输入字母、数字、下划线，且以字母开头", type: "warning"});
            return;
          }
        }
        this.inserToFormData();
    },
    extsBlur(){
        if (this.entExts.desc) {
            document.getElementsByName("entExtsDesc")[0].style.border="";
        } else if (this.entExts.name) {
            document.getElementsByName("entExtsName")[0].style.border="";
        } else if (this.entExts.dsName) {
            document.getElementsByName("entExtsDsName")[0].style.border="";
        } else if (this.entExts.tableName) {
            document.getElementsByName("entExtsSelectName")[0].style.border="";
        }
    },
    //外部表数据加入表单
    inserToFormData() {
      let selectEnt = this.entIndex;
      this.entExts.index = this.formData.ents.length;
      this.entIndex = this.entExts.index;
      this.entExts.relation = "onetoone";
      this.entExts.show = "子实体-外";
      let data = {};
      if (this.formData.ents.length == 0 && !this.addFk) {
          this.entExts.show = "主实体-外";
          this.entExts.relation = "main";
          this.formData.ents.splice(this.entIndex, 1, this.entExts);
          this.formData.ents[this.entIndex].attributeList = this.entExtsTable;
          data = this.formData.ents[this.entIndex];
      } else if(this.openGrandSonAttr && !this.addFk){
        this.formData.ents[this.entIndex -1].children[this.grandSonIndex] = this.entExts;
          this.formData.ents[this.entIndex -1].children[this.grandSonIndex].attributeList = this.entExtsTable;
          data = this.formData.ents[this.entIndex -1].children[this.grandSonIndex];
          data.desc = this.entExts.comment;
          data.name = this.entExts.name;
          data.show = "孙实体-外";
      } else if(!this.addFk) {
        this.formData.ents.push(this.entExts);
        this.formData.ents[this.entIndex].attributeList = this.entExtsTable;
        data = this.formData.ents[this.entIndex];
      }else if(this.addFk){
        this.entIndex = selectEnt;
        this.formData.ents[selectEnt].fk = this.entExts.fk;
        this.formData.ents[selectEnt].attributeList = this.entExtsTable;
        data = this.formData.ents[selectEnt];
      }
      let _this = this;
      data.attributeList = data.attributeList.filter(item =>{
        let val = item.name.toUpperCase();
        return val != _this.entExts.pk.toUpperCase() && val != 'F_FORM_DATA_REV_' && val != 'REF_ID_' &&(!_this.entExts.fk || val != _this.entExts.fk.toUpperCase());
      })
      this.dialogVisible2 = false;
      this.entExtsTable = [];
      data.attributeList.forEach((item,index) =>{
        item.index = index + 1;
      });
      if(this.openGrandSonAttr){
        this.getGrandSonEntsByIndex(this.grandSonIndex, this.entIndex -1)
      }else{
        this.getEntsByIndex(this.entIndex);
      }
      this.addFk = false;
      this.tableList = [];
      this.entExtsTable = [];
      this.entExts = {
        desc: "",
        dsName: "",
        index: 0,
        isExternal: "1",
        name: "",
        packageId: "",
        pk: "",
        pkType: "",
        relation: "",
        show: "",
        status: "",
        tableName: "",
        attributeList: []
      };
    },
    //创建表单
    createTableForm() {
      if (!this.dataView.id) {
        this.deployedEntData();
      } else {
        this.isSave=true;
        //this.saveEntData();
        this.deployedEntData();

      }
    },
    chineseFormat(id,list,param,index,v){
        let _this = this
        let d = document.getElementById(id);
        // disabled 或者 readonly 时 不需要请求
        let timer = null;
        if (d===null || d.__vue__.disabled || d.__vue__.readonly || !v) return;
        if(timer){
            clearTimeout(timer);
        }
        timer = setTimeout(function () {
            req.request({
                url: `${window.context.uc}/base/tools/v1/getPinyin`,
                method: "GET",
                params: { chinese: v, type: 0 }
            }).then(res => {
                if (res.data.state) {
                    list[index][param] = res.data.value;
                    _this.checkIsChinese(res.data.value)
                }
            })
        },500)
    },
  }
};
</script>

<style scoped>
  >>> .el-aside {
    overflow: hidden;
  }
  .labelTitle{
    width:56px;
    height:15px;
    font-size:14px;
    font-family:Microsoft YaHei;
    font-weight:400;
    color:rgba(28,28,28,1);
    line-height:48px;
  }

  .el-scrollbar__wrap {
      overflow-x: hidden;
  }
  .el-divider--horizontal{
      margin-bottom: 15px;
      margin-top: 10px;
  }
  >>> td .el-form-item__error{
      top: 100%;
  }
  >>> .el-dialog__body{
    overflow-y: hidden;
  }
  >>> .el-dialog__body{
    overflow-x: hidden;
  }
  >>> .el-collapse-item__header{
    margin-left: 18px;
  }
  .cd-column__dialog /deep/  .el-dialog > .el-dialog__header {
    padding: 8px 20px;
  }
  .config-item /deep/ .el-form-item__content > .el-form-item{
    display: block;
  }
  .table-name-search {
    cursor: pointer;
  }
</style>
