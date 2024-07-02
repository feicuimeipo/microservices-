<template>
  <el-scrollbar style="height:100%">
    <ht-tree
      :data="treeData"
      :props="defaultProps"
      :highlight-current="highlightCurrent"
      :default-expand-all="defaultExpandAll"
      node-key="id"
      :show-checkbox="showCheckbox"
      :render-content="renderContent"
      :expand-on-click-node="false"
      @node-click="handleNodeClick"
      @check="check"
      @refresh="loadData"
      :support-filter="true"
      ref="htTree"
      lazy
      :load="load"
    >
      <!-- 作用域插槽：插槽prop -->
      <slot slot-scope="{ node, data }" :node="node" :data="data"></slot>
    </ht-tree>
  </el-scrollbar>
</template>

<script>
import org from "@/api/org.js";
import utils from "@/hotent-ui-util.js";
export default {
  name: "ht-org-tree",
  props: {
    orgId: {
      type: String | Number,
      required: true
    },
    supportFilter: {
      type: Boolean,
      default: false
    },
    highlightCurrent: {
      type: Boolean,
      default: false
    },
    defaultExpandAll: {
      type: Boolean,
      default: false
    },
    showCheckbox: {
      type: Boolean,
      default: false
    },
    renderContent: {
      type: Function
    }
  },
  data() {
    return {
      treeData: [],
      defaultProps: {
        children: "children",
        label: "name"
      }
    };
  },
  methods: {
    handleNodeClick(node) {
      this.$emit("node-click", node);
    },
    check(data, checkedObj) {
      this.$emit("check", data, checkedObj);
    },
    loadData(cb) {
      let param = {
        isOrgAuth: "true",
        parentId: this.orgId
      };
      org.getByParentAndDem(param).then(data => {
        this.treeData = utils.tile2nest(data);
      }).finally(()=>{
        cb && cb();
      });
    },
    load(node, resolve){
      if (!node.data.id){
        org.get(this.orgId).then(data => {
          resolve(data);
        })
        return;
      }
      org.getChildrenByOrgId(node.data.id).then(data => {
        resolve(data);
      })
    }
  },
  mounted() {
    this.loadData();
  }
};
</script>
<style lang="scss" scoped>
>>> .el-tree .el-tree__empty-block {
  margin: 20px 20px;
}

>>> .el-tree {
  width: 100%;
}

>>> .el-scrollbar__wrap {
  overflow-x: hidden;
}
</style>
