<template>
  <el-dialog
    title="控件设置"
    :visible.sync="dialogVisible"
    width="45%"
    appendToBody
    :close-on-click-modal="false"
    :before-close="handleClose">

      <el-form v-model="ctrlFiled" data-vv-scope="ctrlFieldForm">

           <table class="form-table" cellspacing="0" cellpadding="0" border="0">
            <tbody>
              <tr>
                <th width="15%">
                  字段描述：
                </th>
                <td width="35%">
                  <span>{{ctrlFiled.name}}</span>
                </td>
                <th width="15%">
                  字段名称：
                </th>
                <td width="35%">
                    <span>{{ctrlFiled.cm}}</span>
                </td>
              </tr>
              <tr>
                <th width="15%">
                  字段类型：
                </th>
                <td width="35%">
                  <span>{{ctrlFiled.ty}}</span>
                </td>
                <th width="15%">
                  控件类型：
                </th>
                <td width="35%">
                    <span v-if="ctrlFiled.ct=='select'">下拉选项</span>
                    <span v-if="ctrlFiled.ct=='radio'">单选按钮</span>
                    <span v-if="ctrlFiled.ct=='customDialog'">自定义对话框</span>
                </td>
              </tr>
              <tr v-if="ctrlFiled.ct!='customDialog'">
                <th width="15%">
                  下拉框选项：
                </th>
                <td width="35%" colspan="3">
                    <table  class="table-list custom_condition_tab table">
                        <tbody>
                          <tr>
                              <th colspan="3">
                                  <el-button @click="addControlContent" type="primary" size="mini" style="float:left;" icon="el-icon-plus">添加</el-button>
                              </th>
                          </tr>
                          <tr v-for="(item,index) in controlContentArray" :key="index">
                            <td>
                              		值： <ht-input v-model="item.key" :validate="{required:true}"></ht-input>
                            </td>
                            <td>
                                  选项： <ht-input v-model="item.value" :validate="{required:true}"></ht-input>
                            </td>
                            <td>
                                <el-button @click='sort(index,"down");' size="small" icon="el-icon-arrow-down" plain></el-button>
                                <el-button @click='sort(index,"up");' size="small" icon="el-icon-arrow-up" plain></el-button>
                                <el-button @click='remove(index);' size="small" icon="el-icon-delete" plain></el-button>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                </td>
              </tr>
              <tr v-if="ctrlFiled.ct=='customDialog'">
                <th width="15%">
                  自定义对话框：
                </th>
                <td width="35%">
                  <ht-select
                    @change="changeDialog"
                    v-model="controlContentObject.alias"
                    :options="customDialogs"
                    :validate="{'required':true}"
                  />
                </td>
                <th width="15%">
                  返回字段：
                </th>
                <td width="35%">
                    <ht-select
                      v-model="controlContentObject.resultField"
                      @change="changeResultField"
                      :validate="{'required':true}"
                      :options="custDialogFields"
                    />
                </td>
              </tr>
             </tbody>
           </table>

      </el-form>


    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="onConfirm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </span>
  </el-dialog>
</template>
<script>
import form from "@/api/form.js";

export default {
  name: "template-ctrl-field-dialog",
  components:{
  },
  props: {},
  data() {
    return {
      ctrlFiled:{},
      controlContentObject:{},
      controlContentArray:[],
      customDialogs:[],
      custDialogFields:[],
      dialogVisible: false,
    };
  },
  methods: {
    initData(){
      if(this.ctrlFiled.ct=='customDialog'){
        if(this.ctrlFiled.controlContent){
          this.controlContentObject = this.ctrlFiled.controlContent;
        }
        this.changeDialog();
        form.getCustomDialogs().then(response =>{
          this.customDialogs = [];
          if(response && response.length>0){
            response.forEach(d =>{
              this.customDialogs.push({key:d.alias,value:d.name});
            })
          }
        });
      }else{
        if(this.ctrlFiled.controlContent && this.ctrlFiled.controlContent.length > 0){
            this.controlContentArray = this.ctrlFiled.controlContent;
        }else{
            this.controlContentArray = [];
        }
      }
    },
    handleClose(){
      this.dialogVisible = false;
    },
    showDialog(row) {
      this.ctrlFiled = row;
      this.dialogVisible = true;
      this.initData();
    },
    addControlContent(){
      this.controlContentArray.push({key:'',value:''});
    },
    changeDialog(){
      if(this.controlContentObject.alias){
         form.getCustomDialogByAlias(this.controlContentObject.alias).then(response =>{
           this.custDialogFields = [];
           this.controlContentObject.resultField = '';
           if(response && response.resultfield){
             let fields = JSON.parse(response.resultfield);
             fields.forEach(f =>{
               this.custDialogFields.push({key:f.comment,value:f.comment});
             })
           }
        });
      }
    },
    onConfirm(selection) {
      if(this.ctrlFiled.ct=='customDialog'){
        this.ctrlFiled.controlContent = this.controlContentObject;
      }else{
        this.ctrlFiled.controlContent = this.controlContentArray;
      }
      this.dialogVisible = false;
      this.$emit('onConfirm', this.ctrlFiled);
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
          let temp = this.controlContentArray[index - 1];
          this.$set(this.controlContentArray, index - 1, this.controlContentArray[index]);
          this.$set(this.controlContentArray, index, temp);
        }
      } else {
        if (index === this.controlContentArray.length - 1) {
          this.$message({
            message: "已经是列表中最后一位",
            type: "warning"
          });
        } else {
          let i = this.controlContentArray[index + 1];
          this.$set(this.controlContentArray, index + 1, this.controlContentArray[index]);
          this.$set(this.controlContentArray, index, i);
        }
      }
    },
    //删除显示字段
    remove(index) {
      this.controlContentArray.splice(index,1);
    },
    changeResultField(){
        this.$forceUpdate();
    }
  }
};
</script>
<style  scoped>
div >>>.el-dialog__body{
  padding:10px ;
}
</style>
