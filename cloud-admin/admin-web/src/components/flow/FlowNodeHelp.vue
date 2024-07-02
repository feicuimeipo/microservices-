<template>
    <el-dialog :title="title" width="1000px" :visible.sync="dialogVisible" :before-close="handleClose" append-to-body :close-on-click-modal="false">
      <vue-ueditor-wrap v-model="helpValue"></vue-ueditor-wrap>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSave" >确 定</el-button>
        <el-button @click="handleClose">取 消</el-button>
      </div>
    </el-dialog>
</template>

<script>
import { Message } from 'element-ui';
import req from "@/request.js";
export default {
  name: "FlowNodeHelp",
  components:{},
  props: {
    title: {
      type: String,
      required: true,
      default: "任务帮助提示"
    },
    //帮助内容
    value: {
      type: String,
      required: true
    },
    //任务ID
    nodeId: {
      type: String,
      required: true
    }
  },
  data() {
    return {
        dialogVisible:false,//是否显示对话框
        helpValue:"",//帮助内容
    }
  },
  methods: {
    //显示对话框
    showDialog() {
      this.dialogVisible=true;
      this.helpValue=this.value;
    },
    //确认对话框
    handleSave(){
      let Base64 = require('js-base64').Base64;
      this.dialogVisible = false;
      //发布确认事件
      this.$emit("handle-save",Base64.encode(this.helpValue))
    },
    //关闭对话框
    handleClose() {
      this.dialogVisible = false;
    }
  },
}
</script>

<style lang="scss" scoped>
.el-main {
  padding-top: 0px;
}
</style>