<template>
  <el-container>
    <el-form ref="form" label-width="80px" style="width:100%;">
      <el-button
        icon="el-icon-plus"
        size="small"
        type="primary"
        style="margin-bottom: 10px;"
        @click="addManageBtns"
        >添加</el-button
      >
      <el-table
        ref="manageSettingTable"
        border
        class="dt-manage__table"
        :max-height="tabHeight"
        :data="manageFields"
        tooltip-effect="dark"
      >
        <el-table-column prop="name" label="类型" width="220">
          <template scope="scope">
            <ht-select
              @change="handleManageChange(scope.row)"
              v-model="scope.row.name"
              :options="btnoptions"
              :validate="{ required: true }"
            />
          </template>
        </el-table-column>
        <el-table-column prop="desc" label="名称" width="320">
          <template scope="scope">
            <el-input
              style="width:60%"
              v-model="scope.row.desc"
              placeholder="请输入按钮名称"
            ></el-input>
            <span style="float:right;">
              <el-button
                v-if="scope.row.name === 'url'"
                size="small"
                @click="setButtonValue('url',scope.row)"
                icon="el-icon-edit"
                >设置Url地址</el-button
              >
              <el-button
                v-if="scope.row.name ==='switch'"
                size="small"
                @click="setButtonValue('switch',scope.row)"
                icon="el-icon-edit"
              >设置开关值</el-button>
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="right"
          label="权限"
          :render-header="mrightRenderHeader"
          width="300"
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
            <ht-radio
              v-if="scope.row.name === 'url'"
              v-model="scope.row.type"
              :options="[
                { key: '1', value: '列表按钮' },
                { key: '2', value: '表头按钮' }
              ]"
            />
            <ht-radio
                    v-if="scope.row.name === 'add'||scope.row.name === 'edit'||scope.row.name === 'detail'"
                    v-model="scope.row.openType"
                    :options="[
                { key: 'old', value: '当前页面打开' },
                { key: 'new', value: '新窗口打开' }
              ]"
            />
            <div v-if="scope.row.name == 'import'" style="display: inline-block;padding-right: 10px;">
                <span>导入行数限制</span>&nbsp;
                <el-input-number style="width:120px" size="small" controls-position="right" v-model="scope.row.limit" :min="1" :max="10000" :step="10"></el-input-number>
            </div>
            <el-select
              multiple
              style="margin-right:15px;"
              v-if="scope.row.name === 'produceQRCode' || scope.row.name==='switch'"
              v-model="scope.row.display"
              placeholder="请选择映射字段"
            >
              <el-option
                v-for="item in displayField"
                :key="item.name"
                :label="item.desc"
                :value="JSON.stringify(item)"
              ></el-option>
            </el-select>

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
            <el-button
              @click="copy(scope.$index)"
              type="primary"
              size="small"
              icon="el-icon-document-copy"
              plain
            ></el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <!-- 选择对话框  -->
    <eip-auth-dialog
      ref="eipAuthDialog"
      name="eipAuthDialog"
      @onConfirm="authDialogOnConfirm"
      append-to-body
    />
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="50%"
      appendToBody
      :close-on-click-modal="false"
      destory-on-close
      top="8vh"
    >
      <el-input v-model="url" placeholder="URL地址"  v-if="dialogTitle==='设置Url地址'"></el-input>
      <div v-if="dialogTitle === '设置开关值'" align="center" width="500px">
          <table width="80%">
            <tr>
              <td>开状态下值</td>
              <td><el-input v-model="switchOn" placeholder="开时置值"></el-input></td>
            </tr>
            <tr>
              <td>开状态下显示</td>
              <td><el-input v-model="switchOnLabel" placeholder="开时显示值"></el-input></td>
            </tr>
            <tr>
              <td>关状态下值</td>
              <td><el-input v-model="switchOff" placeholder="关时置值" style="padding-top:20px"></el-input></td>
            </tr>
            <tr>
              <td>关状态下显示</td>
              <td><el-input v-model="switchOffLabel" placeholder="关时显示值"></el-input></td>
            </tr>
            <tr>
              <td>默认状态</td>
              <td>
                <el-select v-model="switchDefaultTrue" placeholder="默认状态" style="padding-top:20px" width="100%">
                <el-option
                  v-for="item in switchOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                  >          
                </el-option>
                 </el-select>
              </td>
            </tr>
          </table>
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button
          type="primary"
          @click="dialogOnconfirm()"
          >确 定</el-button
        >
        <el-button @click="dialogVisible = false" size="medium"
          >取 消</el-button
        >
      </span>
    </el-dialog>
  </el-container>
</template>
<script>
const eipAuthDialog = () => import("@/components/dialog/EipAuthDialog.vue");

export default {
  components: {
    eipAuthDialog
  },
  name: "manage-setting",
  props: ["data"],
  data() {
    return {
      dialogVisible: false,
      dialogModel: {},
      url: {},
      dataTemplate: {},
      manageFields: [],
      permissionMap: {},
      permissionList: [],
      displayField: [],
      btnoptions: [
        { key: "add", value: "新增" },
        { key: "edit", value: "编辑" },
        { key: "del", value: "删除" },
        { key: "detail", value: "明细" },
        { key: "record", value: "修改记录"},
        { key: "printDetail", value: "打印明细" },
        { key: "sub", value: "子表数据" },
        { key: "export", value: "导出" },
        { key: "import", value: "导入" ,limit:1000},
        { key: "startFlow", value: "启动流程" },
        { key: "produceQRCode", value: "生成二维码" },
        { key: "print", value: "打印" },
        { key: "url", value: "URL按钮" },
        { key: "switch", value: "开关" }
      ],
      tabHeight: `${document.documentElement.clientHeight}` - 280,
      currentAuthRow: null,
      dialogTitle:"",
      switchOn:"",
      switchOff:"",
      switchDefaultTrue:false,
      switchOptions:[{
        value:true,
        label:'默认为开'
      },{
        value:false,
        label:'默认为关'
      }],
      switchOnLabel:"",
      switchOffLabel:""
    };
  },
  mounted() {
    this.dataTemplate = this.data.bpmDataTemplate;
    this.manageFields = this.dataTemplate.manageField
      ? JSON.parse(this.dataTemplate.manageField)
      : [];
    this.displayField = this.dataTemplate.displayField
      ? JSON.parse(this.dataTemplate.displayField)
      : [];

    this.permissionMap = this.data.permissionList;
    if (this.permissionMap) {
      for (let key in this.permissionMap) {
        this.permissionList.push({ type: key, title: this.permissionMap[key] });
      }
    }
  },
  methods: {
    copy(index) {
      let obj = JSON.parse(JSON.stringify(this.manageFields[index]));
      this.manageFields.push(obj);
    },
    //保存按钮数据
    saveManageField() {
      this.dataTemplate.manageField = this.manageFields
        ? JSON.stringify(this.manageFields)
        : null;
    },
    validateManageField(){
      //开关按钮校验，开关按钮必须绑定一列，开时值和关时值必填
      let this_ = this;
      let fields = this_.manageFields;
      for(let i=0;i<fields.length;i++){
        if(fields[i].name === 'switch'){
          if(!fields[i].display){
            this_.$message({type:"error",message:"绑定字段获取失败"});
            return false;
          }
          else if(fields[i].display.length==0){
            this_.$message({type:"error",message:"请为开关添加字段"});
            return false;
          }else if(fields[i].display.length > 1){
            this_.$message({type:"error",message:"开关只能绑定一个字段，若要控制多个字段，请设置多个开关"});
            return false;
          }
          if(!fields[i].switchOn || !fields[i].switchOff){
            this_.$message({type:"error",message:"请完善开关的开时值和关时值"});
            return false;
          }
          if(!fields[i].desc || !fields[i].name){
            this_.$message({type:"error",message:"请完善开关信息"});
            return false;
          }
        }
      }
      return true;
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
          let temp = this.manageFields[index - 1];
          this.$set(this.manageFields, index - 1, this.manageFields[index]);
          this.$set(this.manageFields, index, temp);
        }
      } else {
        if (index === this.manageFields.length - 1) {
          this.$message({
            message: "已经是列表中最后一位",
            type: "warning"
          });
        } else {
          let i = this.manageFields[index + 1];
          this.$set(this.manageFields, index + 1, this.manageFields[index]);
          this.$set(this.manageFields, index, i);
        }
      }
    },
    //删除显示字段
    remove(index) {
      this.manageFields.splice(index, 1);
    },
    //添加管理按钮
    addManageBtns() {
      let mf = { desc: "新增", name: "add", type: "1",openType:"old" };
      if (!this.noRights) {
        mf.right = JSON.stringify([{ type: "everyone" }]);
      }
      this.manageFields.push(mf);
    },
    //处理按钮切换
    handleManageChange(row) {
      this.btnoptions.forEach(btn => {
        if (btn.key == row.name) {
          if (btn.limit) {
            row.limit = btn.limit;
          }
          row.desc = btn.value;
          return;
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
    //显示权限信息
    rightToDesc(right) {
      if (right) {
        try {
          right = JSON.parse(right);
        } catch (error) {}
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
    mrightRenderHeader(h, para) {
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
                  _this.manageFields &&
                  _this.manageFields.length > 0
                ) {
                  _this.manageFields.forEach(field => {
                    if (field.right) {
                      try {
                        field.right = JSON.parse(field.right);
                      } catch (error) {}
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
    //URL按钮和开关按钮设置事件
    setButtonValue(option,row){
      this.dialogVisible = true;
      if(option === 'url'){
        this.dialogTitle = "设置Url地址";
        this.url = row.url;
      }else if(option ==='switch'){
        this.dialogTitle = "设置开关值";
        this.switchOn = row.switchOn;
        this.switchOff = row.switchOff;
        this.switchOnLabel = row.switchOnLabel;
        this.switchOffLabel = row.switchOffLabel;
      }
      this.dialogModel = row;
    },
    //设置URL按钮和开关后确认
    dialogOnconfirm(){
      if(this.dialogTitle ==='设置开关值'){
        this.dialogModel.switchOn = this.switchOn;
        this.dialogModel.switchOff = this.switchOff;
        this.dialogModel.isActive = "off";
        this.dialogModel.switchDefaultTrue = this.switchDefaultTrue;
        this.dialogModel.switchOnLabel = this.switchOnLabel;
        this.dialogModel.switchOffLabel = this.switchOffLabel;
        if(!this.switchOn||!this.switchOff){
          this.$message({type:"warning",message:"开关需要设置开时值和关时值"});
        }    
      }else if(this.dialogTitle === '设置Url地址'){
        this.dialogModel.url = this.url;
      }
      this.dialogVisible = false;
    }
  }
};
</script>
<style lang="scss" scoped>
.dt-manage__table >>> .cell {
  text-align: center;
}
</style>
