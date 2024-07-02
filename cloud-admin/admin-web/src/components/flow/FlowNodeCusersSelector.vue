<template>
  <div>
    <el-dialog
      title="用户选择"
      :visible.sync="dialogVisible"
      v-if="dialogVisible"
      width="80%"
      appendToBody
      :close-on-click-modal="false"
      top="8vh"
    >
      <form v-form>
        <el-container style="height:520px">
          <table class="table table-bordered" style="border-spacing: 0;width: 100%;">
            <tbody>
              <tr>
                <th style="width: 15%;">用户</th>
                <td>
                  <el-radio v-model="data.source" label="currentUser">当前登录用户</el-radio>
                  <el-radio v-model="data.source" label="start">发起人</el-radio>
                  <el-radio v-model="data.source" label="prev">上一步执行人</el-radio>
                  <el-radio v-model="data.source" label="var">变量</el-radio>
                  <el-radio v-model="data.source" label="spec">指定用户人</el-radio>
                </td>
              </tr>
              <tr v-if="data.source=='var'">
                <th>变量选择</th>
                <td>
                  <FlowVarSelector :defId="parentParam.defId" @node-click="varTreeOnConfirm" :removeSub="false" />
                  <span>注意这里需要绑定用户的账号</span>
                  <el-input type="textarea" :rows="4" v-model="data.var.name" :disabled="true"></el-input>
                </td>
              </tr>
              <tr v-show="data.source=='spec'">
                <th>指定用户</th>
                <td>
                  <eip-user-selector
                    :appendToBody="true"
                    v-model="user.name"
                    :config="{id:'user.id',account:'user.account'}"
                  />
                </td>
              </tr>
            </tbody>
          </table>
        </el-container>
      </form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleClose" size="medium">确 定</el-button>
        <el-button @click="dialogVisible = false" size="medium">取 消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import flow from "@/api/flow.js";
import req from "@/request.js";
import { mapState, mapActions } from "vuex";

const eipUserSelector = () =>
  import("@/components/selector/EipUserSelector.vue");

const FlowVarSelector = () => import("@/components/flow/FlowVarSelector.vue");

import utils from "@/hotent-ui-util.js";
export default {
  components: { eipUserSelector, FlowVarSelector },
  data() {
    return {
      userConditionTab: "first",
      dialogVisible: false,
      data: {},
      user: { list: [] },
      parentParam: {}
    };
  },
  watch: {
    "data.source": function(newVal, oldVal) {
      if (newVal == "var" && !this.data.var) {
        this.data.var = {};
      }
    }
  },
  methods: {
    showDialog: function(param) {
      this.dialogVisible = true;
      this.parentParam = param;
      if (param.calc) {
        this.data = JSON.parse(JSON.stringify(param.calc));
      }
      // 指定人员
      if (this.data.source == "spec" && this.data.userName != "") {
        this.user.name = this.data.userName;
        this.user.account = this.data.account;
        if (this.user.name) {
          let arrName = this.user.name.split(",");
          let arrAccount = this.user.account.split(",");
          for (let i = 0; i < arrName.length; i++) {
            var item = { fullname: arrName[i], account: arrAccount[i] };
            this.user.list.push(item);
          }
        }
      } else {
        this.user = { list: [] };
      }
    },
    handleClose: function() {
      //指定人员
      if (this.data.source == "spec") {
        this.data.userName = this.user.name;
        this.data.account = this.user.account;
      }
      //处理描述
      if (this.data.source == "currentUser") {
        this.data.description = "当前登录用户";
      } else if (this.data.source == "start") {
        this.data.description = "发起人";
      } else if (this.data.source == "prev") {
        this.data.description = "上一步执行人";
      } else if (this.data.source == "var") {
        this.data.description = this.data["var"]
          ? "[变量]" + this.data["var"].name
          : "[变量]";
      } else if (this.data.source == "spec") {
        if (this.user.name) {
          this.data.description = "[指定用户]" + this.user.name;
        } else {
          this.data.description = "";
        }
      }
      this.$emit("cuserSelectorConfirm", this.data);
      this.dialogVisible = false;
    },
    varTreeOnConfirm(node) {
      let keyStr = node.name;
      let source = "BO";
      // 子表情况做提示
      // if (
      //   node.nodeType == "sub" ||
      //   (node.path && node.path.indexOf(".sub_") != -1)
      // ) {
      //   keyStr = "";
      //   this.$message.warn("提示信息", "不支持子表");
      // } // 主表bo
      // else 
      if (node.nodeType == "var") {
        keyStr = node.name;
        source = "flowVar";
      } else {
        keyStr = node.path + "." + node.name;
      }
      let json = {
        source: source,
        name: keyStr,
        executorType: "user",
        userValType: "account"
      };
      this.data.vars = JSON.stringify(json);
      this.data["var"] = json;
      this.$forceUpdate();
    }
  }
};
</script>

<style  scoped>
div >>> .el-dialog__body {
  padding: 10px;
}

.table > thead > tr > th,
.table > thead > th,
.table > tbody > tr > th,
.table > tfoot > tr > th,
.table > thead > tr > td,
.table > tbody > tr > td,
.table > tfoot > tr > td {
  border-top: 1px solid #e7eaec;
  border-left: 1px solid #e7eaec;
  line-height: 1.42857;
  padding: 8px;
  vertical-align: middle;
}
.table {
  border-bottom: 1px solid #e7eaec;
  border-right: 1px solid #e7eaec;
}
.table >>> .el-button {
  padding: 6px 8px;
  margin-left: 0px;
  margin-right: 5px;
}
div >>> .el-tabs__content {
  height: 450px;
  overflow: auto;
}
</style>
