<template>
  <div class="fullheight">
    <ht-table
      ref="dataSourceTable"
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      :selection="true"
      quick-search-props="name,alias"
      :show-export="false"
      :show-custom-column="false"
      :defaultSorter="[{'property':'CREATE_TIME_','direction':'DESC'}]"
    >
      <template v-slot:toolbar>
        <el-button-group>
          <el-button size="small" icon="el-icon-plus" @click="handleCommand({command:'add'})">添加</el-button>
          <ht-delete-button
            size="small"
            :url="dataSourceDeleteUrl"
            :htTable="$refs.dataSourceTable"
          >删除</ht-delete-button>
        </el-button-group>
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column
          prop="name"
          label="名称"
          width="300"
          :sortable="true"
          :show-overflow-tooltip="true"
        >
          <template v-slot="{row}">
            <el-link @click="handleCommand({row:row,command:'edit'})" type="primary">{{row.name}}</el-link>
          </template>
        </ht-table-column>
        <ht-table-column prop="alias" label="别名" :sortable="true" />
        <ht-table-column
          prop="enabled"
          label="是否可用"
          :filters="[{text:'是', value:1},{text:'否', value:0}]"
        >
          <template v-slot="{row}">
            <el-tag type="success" v-if="row.enabled">是</el-tag>
            <el-tag type="primary" v-if="!row.enabled">否</el-tag>
          </template>
        </ht-table-column>
        <!-- <ht-table-column width="150" label="操作">
              <template v-slot="{row}">
                <el-dropdown
                  size="mini"
                  split-button
                  @command="handleCommand"
                  @click="handleCommand({row:row,command:'edit'})"
                >
                  <span>
                    <i class="el-icon-edit"></i>编辑
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-menu" :command="{row:row,command:'get'}">查看</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
        </ht-table-column>-->
      </template>
    </ht-table>

    <el-dialog
      width="50%"
      :title="dataSourceTitle"
      :visible="dialogVisible"
      :before-close="handleClose"
      :close-on-click-modal="false"
    >
      <el-form
        :disabled="dataSourceDisabled"
        v-model="sourceProp"
        data-vv-scope="editdataSourceForm"
      >
        <ht-form-item label="名称" prop="name" label-width="130px" class="dataSource-input-width">
          <ht-input
            v-model="sourceProp.name"
            autocomplete="off"
            :validate="{required:true}"
            placeholder="请输入名称"
          ></ht-input>
        </ht-form-item>
        <ht-form-item
          label="别名(唯一)"
          prop="alias"
          label-width="130px"
          class="dataSource-input-width"
        >
          <ht-input
            v-model="sourceProp.alias"
            v-pinyin="sourceProp.name"
            autocomplete="off"
            :validate="{required:true,alpha_num:true}"
            placeholder="请输入别名"
            :disabled="sourceProp.id?true:false"
          ></ht-input>
        </ht-form-item>
        <ht-form-item
          label="数据源类型"
          label-width="130px"
          class="dataSource-item-bottom dataSource-input-width"
        >
          <ht-select
            v-model="sourceProp.dbType"
            :options="dbTypeList"
            :props="{key:'value',value:'value'}"
            :validate="{'required':true}"
            @change="currentSel"
          />
        </ht-form-item>
        <ht-form-item
          label="是否生效"
          label-width="130px"
          class="dataSource-item-bottom dataSource-input-width"
        >
          <ht-select
            v-model="sourceProp.enabled"
            :options="enableds"
            :validate="{'required':true}"
          />
        </ht-form-item>
        <ht-form-item
          label="数据源"
          label-width="130px"
          class="dataSource-item-bottom dataSource-input-width"
        >
          <ht-select
            v-model="dsId"
            :options="sysDataSourceDefs"
            :props="{key:'id',value:'name'}"
            :validate="{'required':true}"
            @change="settingSel"
          />
        </ht-form-item>
        <ht-form-item
          v-for="(item,index) in sourceProp.settingJson"
          :key="item.comment"
          v-show="sourceProp.settingJson.length>0 && !isExpand?(index<4):true"
          :label="item.comment"
          label-width="130px"
          class="dataSource-item-bottom dataSource-input-width"
        >
          <ht-input
            v-model="item.value"
            :validate="{required:true}"
            :placeholder="'请输入'+item.comment"
          > <el-button slot="append" v-if="item.name=='password' && dsId=='1' && item.value.length<60 "  @click="encrypt(item)" >加密</el-button></ht-input>
          ({{item.type}})
        </ht-form-item>
      </el-form>
      <div style="text-align: center;" v-if="sourceProp.settingJson.length>0">
        <el-button
          size="mini"
          @click="isExpand = !isExpand"
          :icon="isExpand? 'icon-expand':'icon-hide'"
          circle
          :title="isExpand? '收起':'展开'"
        ></el-button>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="checkConnection()" type="primary">测试连接</el-button>
        <ht-submit-button
          v-show="!dataSourceDisabled"
          :url="savedataSourceUrl()"
          :model="newSourceProp"
          :is-submit="isSubmit"
          request-method="POST"
          scope-name="editdataSourceForm"
          @before-save-data="beforeSaveData"
          @after-save-data="afterSaveData"
        >{{$t('eip.common.save')}}</ht-submit-button>
        <el-button @click="dialogCancle('dialogVisible')">{{$t('eip.common.cancel')}}</el-button>
      </div>
    </el-dialog>
    <!-- 加载数据 用作编辑数据源和查看数据源-->
    <ht-load-data
      :url="settingDataUrl"
      context="portal"
      @after-load-data="afterLoadSourcesettingData"
    ></ht-load-data>
    <!-- 加载数据 用作编辑数据源和查看数据源-->
    <ht-load-data :url="loadDataUrl" context="portal" @after-load-data="afterLoadData"></ht-load-data>
  </div>
</template>
<script>
import sys from "@/api/portal.js";
export default {
  name: "meta-dataSource",
  components: {},
  data() {
    return {
      isExpand: false,
      dataSourceDisabled: false,
      dataSourceTitle: "",
      loadDataUrl: "",
      settingDataUrl: "",
      dialogVisible: false,
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      isSubmit: true,
      enableds: [
        { key: true, value: "是 " },
        { key: false, value: "否" }
      ],
      dbType: null,
      dbTypeList: [
        {
          value: "mysql",
          driverName: "com.mysql.cj.jdbc.Driver",
          url:
            "jdbc:mysql://主机:3306/数据库名?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&zeroDateTimeBehavior=convertToNull",
          validationquery: "select 1 from dual"
        },
        {
          value: "oracle",
          driverName: "oracle.jdbc.OracleDriver",
          url: "jdbc:oracle:thin:@主机:1521:数据库实例",
          validationquery: "select 1 from dual"
        },
        {
          value: "mssql2012",
          driverName: "com.microsoft.sqlserver.jdbc.SQLServerDriver",
          url: "jdbc:sqlserver://主机:1433;databaseName=数据库名;",
          validationquery: "select 1"
        },
        {
          value: "mssql2008",
          driverName: "com.microsoft.sqlserver.jdbc.SQLServerDriver",
          url: "jdbc:sqlserver://主机:1433;databaseName=数据库名;",
          validationquery: "select 1"
        }
      ],
      sysDataSourceDefs: [],
      dsId: "",
      sourceProp: {
        settingJson: [],
        initOnStart: false,
        enabled: null,
        dbType: "",
        classPath: "",
        initMethod: "",
        closeMethod: "",
        name: "",
        alias: "",
        dbType: "",
        id: ""
      },
      newSourceProp: {},
      sysDataSourceRow: {}
    };
  },
  watch: {
    dsId: function(newVal, oldVal) {
      if (!newVal) {
        this.sourceProp.settingJson = [];
      }
    }
  },
  computed: {
    dataSourceDeleteUrl: function() {
      return window.context.portal + "/sys/sysDataSource/v1/remove";
    }
  },
  methods: {
    encrypt(item){
      this.$http.get(window.context.portal + "/base/tools/v1/encryptDbPassword?password="+item.default).then(res => {
        if(this.dsId!='1'){
          this.$message.error("不支持密码加密");
          return ;
        }
        if(!res.data.state){
          this.$message.error("加密失败");
          return ;
        }
        item.value = res.data.value.password;
        let publicKey = {"name":"connectionProperties","comment":"公钥","type":"java.lang.String","baseAttr":"1","default": "","value":"config.decrypt=true;config.decrypt.key="+res.data.value.publicKey};
        this.sourceProp.settingJson.splice(this.sourceProp.settingJson.findIndex(item => item.name === "connectionProperties"), 1)
        this.sourceProp.settingJson.push(publicKey);
      })
    },
    checkConnection() {
      if (this.sourceProp.settingJson.length == 0) {
        this.$message.error("连接失败");
        return;
      }
      var newSourceProp = {};
      Object.assign(newSourceProp, this.sourceProp);
      newSourceProp.settingJson = JSON.stringify(newSourceProp.settingJson);
      sys.checkConnection(newSourceProp).then(
        data => {
          if (data.state) {
            this.$message.success(data.message);
          }
        },
        error => {
          this.$message.error(error || "连接失败");
        }
      );
    },
    currentSel(val, data) {
      var list = data.value;
      for (var i in this.dbTypeList) {
        var d = this.dbTypeList[i];
        if (d.value != val) continue;
        for (var i = 0; i < this.sourceProp.settingJson.length; i++) {
          var attr = this.sourceProp.settingJson[i];
          if (attr.name.toLowerCase().indexOf("url") != -1) {
            attr.value = d.url;
          } else if (attr.name.toLowerCase().indexOf("driver") != -1) {
            attr.value = d.driverName;
          } else if (attr.name.toLowerCase().indexOf("validationquery") != -1) {
            attr.value = d.validationquery;
          }
        }
      }
    },
    settingSel(dsId, data) {
      if (data && data.settingJson) {
        this.sourceProp.settingJson = JSON.parse(data.settingJson);
        //改变了数据池id，那么需要输入的属性也变了
        for (var i = 0; i < this.sysDataSourceDefs.length; i++) {
          var def = this.sysDataSourceDefs[i];
          if (def.id != dsId) continue;
          var settingJson = JSON.parse(def.settingJson);
          this.sourceProp.classPath = def.classPath;
          this.sourceProp.initMethod = def.initMethod;
          this.sourceProp.closeMethod = def.closeMethod;
          //处理配置的初始化值
          this.sourceProp.settingJson = [];
          settingJson.forEach(obj => {
            obj.value = obj["default"];
            this.sourceProp.settingJson.push(obj);
          });
        }

        //选择了数据源类型 就替换数据源中的连接地址和驱动
        for (var i in this.dbTypeList) {
          var d = this.dbTypeList[i];
          if (d.value != this.sourceProp.dbType) continue;
          for (var i = 0; i < this.sourceProp.settingJson.length; i++) {
            var attr = this.sourceProp.settingJson[i];
            if (attr.name.toLowerCase().indexOf("url") != -1) {
              attr.value = d.url;
            } else if (attr.name.toLowerCase().indexOf("driver") != -1) {
              attr.value = d.driverName;
            } else if (
              attr.name.toLowerCase().indexOf("validationquery") != -1
            ) {
              attr.value = d.validationquery;
            }
          }
        }
        //数据源配置别名跟这里的别名一致
        for (var i = 0; i < this.sourceProp.settingJson.length; i++) {
          var attr = this.sourceProp.settingJson[i];
          if (attr.name.toLowerCase().indexOf("alias") != -1) {
            attr.value = attr.alias;
          }
        }
      }
    },
    handleSelectOptions(query) {
      let _me = this;
      return new Promise((resolve, reject) => {
        setTimeout(() => {
          _me.dbType = _me.dbTypeList;
          resolve();
        }, 1000);
      });
    },
    beforeSaveData() {
      this.newSourceProp = {};
      //克隆一个新对象以免影响页面数据动态绑定上了
      Object.assign(this.newSourceProp, this.sourceProp);
      this.newSourceProp.settingJson = JSON.stringify(
        this.newSourceProp.settingJson
      );
    },
    savedataSourceUrl: function() {
      return window.context.portal + "/sys/sysDataSource/v1/save"; //window.context.portal +
    },
    afterSaveData() {
      this.dialogVisible = false;
      this.$refs.dataSourceTable.load();
    },
    afterLoadData(data) {
      // 编辑数据源
      if (this.dialogVisible) {
        this.sourceProp = data;
        this.sourceProp.settingJson = JSON.parse(data.settingJson);
        this.oldAlias = this.sourceProp.alias;
        let _me = this;
        this.sysDataSourceDefs.forEach(item => {
          if (item.classPath == _me.sourceProp.classPath) {
            _me.dsId = item.id;
          }
        });
        setTimeout(() => this.$validator.validateAll("editdataSourceForm"));
      }
    },
    afterLoadSourcesettingData(data) {
      this.sysDataSourceDefs = data;
      if (this.sysDataSourceRow && this.sysDataSourceRow.id) {
        this.loadDataUrl =
          `/sys/sysDataSource/v1/getJson?id=` + this.sysDataSourceRow.id;
      }
    },
    dialogCancle(dialogVisible) {
      this.loadDataUrl = "";
      this.settingDataUrl = "";
      this[dialogVisible] = false;
      setTimeout(() => (this.dataSourceDisabled = false), 500);
    },
    handleClose() {
      this.loadDataUrl = "";
      this.settingDataUrl = "";
      this.dialogVisible = false;
      setTimeout(() => (this.dataSourceDisabled = false), 500);
    },
    showDialog(row) {
      this.dialogVisible = true;
      this.settingDataUrl = `/sys/sysDataSourceDef/v1/getAll`;
      if (row && row.id) {
        this.sysDataSourceRow = row;
        //this.loadDataUrl = `/sys/sysDataSource/v1/getJson?id=` + row.id;
      }
    },
    handleNodeClick(node) {},
    loadData(param, cb) {
      sys
        .getDataSourcePageJson(param)
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
          this.dataSourceTitle = "编辑数据源";
          this.showDialog(params.row);
          break;
        case "add":
          this.sysDataSourceRow = {};
          this.sourceProp.dbType = "";
          this.sourceProp.id = "";
          this.sourceProp.alias = "";
          this.sourceProp.name = "";
          this.sourceProp.enabled = null;
          this.dsId = "";
          //this.dataSource.genType ='1'
          this.dataSourceTitle = "添加数据源";
          this.showDialog();
          break;
        case "get":
          this.dataSourceTitle = "查看数据源";
          this.isSubmit = false;
          this.dataSourceDisabled = true;
          this.showDialog(params.row);
          break;
        default:
          break;
      }
    }
  }
};
</script>

<style scoped>
ul {
  padding: 0px;
  margin: 0px;
}

li {
  list-style-type: none;
}

.dataSource-input-width .inputs {
  width: 100%;
}
</style>
