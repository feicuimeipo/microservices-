<template>
  <div style="display: inline-block;">
    <el-popover v-if="type !='2'" placement="bottom-start" v-model="visible">
      <ht-tree
        style="max-height:400px;width:300px;overflow: auto;"
        v-if="visible"
        :data="data"
        node-key="id"
        :expand-on-click-node="false"
        :props="defaultProps"
        support-filter
        default-expand-all
        @node-click="handleNodeClick"
      ></ht-tree>
      <el-button slot="reference">选择变量</el-button>
    </el-popover>
    <ht-tree
      v-if="type =='2'"
      style="height:100%;width:300px;overflow: auto;"
      :data="data"
      node-key="id"
      :expand-on-click-node="false"
      :props="defaultProps"
      support-filter
      default-expand-all
      @node-click="handleNodeClick"
      :highlight-current="true"
    ></ht-tree>
  </div>
</template>

<script>
import flow from "@/api/flow.js";
import req from "@/request.js";
import { mapState, mapActions } from "vuex";
import FlowNodeCusersSelector from "@/components/flow/FlowNodeCusersSelector.vue";
import utils from "@/hotent-ui-util.js";
export default {
  props: [
    "defId",
    "nodeId",
    "type",
    "value",
    "removeSub",
    "includeBpmConstants",
    "isBpmForm"
  ], //type 1，流程其它设置，2，流程人员规则设置
  components: { FlowNodeCusersSelector },
  data() {
    return {
      visible: false,
      dialogVisible: false,
      data: [],
      defaultProps: {
        children: "children",
        label: "desc"
      },
      thisDefId: ""
    };
  },
  computed: mapState({
    defConfigData: state => state.flow.defConfigData
  }),
  methods: {
    handleNodeClick(selection, node, nodeCompent) {
      if (selection && selection.children && selection.children.length > 0) {
        return;
      }
      this.visible = false;
      this.handleValue(node, selection);
      this.$emit("node-click", selection, node);
    },
    handleValue(node, selection) {
      var keyStr = node.data.name;
      var parentNode = node.parent;
      var boDefAlias = parentNode.data.boDefAlias;
      var typeMoth =
        node.data.dataType == "number" ? ".getInt()" : ".getString";
      if (node.data.nodeType != "var") {
        while (parentNode && !boDefAlias) {
          let pParentNode = parentNode.parent;
          if (!pParentNode) {
            break;
          }
          boDefAlias = pParentNode.data.boDefAlias;
        }
      }

      // 子表情况做提示
      if (node.data.nodeType == "sub") {
        keyStr =
          boDefAlias +
          ".getSubByKey('" +
          node.data.name +
          "') /* 获取子表,return List<BoData> */";
      } // 主表bo
      else if (parentNode.data.nodeType == "main") {
        if (this.type == "1") {
          keyStr = boDefAlias + "." + node.data.name;
        } else if (this.type == "2") {
          keyStr = boDefAlias + "." + node.data.name;
        } else {
          keyStr =
            boDefAlias +
            typeMoth +
            '("' +
            node.data.name +
            '") /*数据类型：' +
            node.data.dataType +
            "*/";
        }
      } else if (parentNode.nodeType == "sub") {
        var mainTableName = boDefAlias;
        keyStr =
          mainTableName +
          '.getSubByKey("' +
          parentNode.name +
          '") /*获取子表数据 ，返回数据：return List<BoData> 。子表字段：' +
          node.name +
          ", 请根据实际情况处理子表数据的获取*/";
      } else if (node.data.nodeType == "var") {
        keyStr = node.data.name;
      }
      if (this.type == "1") {
        keyStr = "{" + node.data.desc + ":" + keyStr + "}";
      }
      node.data.pathValue = keyStr;
      selection.pathValue = keyStr;
      this.$emit("input", keyStr);
    }
  },
  created() {
    this.utils = utils;
    let this_ = this;
    this.thisDefId = this.defId;
    if (!this.thisDefId && this.defConfigData && this.defConfigData.initData) {
      this.thisDefId = this.defConfigData.initData.bpmDefinition.defId;
    }
    let isRemoveSub = this.removeSub;
    if (isRemoveSub !== false) {
      isRemoveSub = true;
    }

    let includeBpmConst = this.includeBpmConstants;
    if (includeBpmConst !== false) {
      includeBpmConst = true;
    }
    let bpmForm = this.isBpmForm;
    if (bpmForm !== false) {
      bpmForm = true;
    }
    req
      .post(req.getContext().bpmModel + "/flow/node/v1/varTree", {
        defId: this.thisDefId,
        nodeId: this.nodeId,
        includeBpmConstants: includeBpmConst,
        removeSub: isRemoveSub,
        bpmForm: bpmForm
      })
      .then(response => {
        if (this_.type == "1") {
          for (const d of response.data) {
            if (d.desc == "流程变量" && d.nodeType == "root") {
              d.children.push({
                desc: "流程标题",
                name: "title",
                nodeType: "var"
              });
              d.children.push({
                desc: "发起时间",
                name: "startDate",
                nodeType: "var"
              });
            }
          }
        }
        this_.data = response.data;
      });
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