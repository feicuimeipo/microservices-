<template>
  <el-scrollbar class="scrollbar-fullheight">
    <div class="control-list">
      <template v-if="layoutComponents.length">
        <div class="widget-cate">布局字段</div>
        <draggable
                tag="ul"
                class="flex-container"
                :list="layoutComponents"
                v-bind="{group:{ name:'form', pull:'clone',put:false},sort:false, ghostClass: 'ghost'}"
                @end="handleMoveEnd"
                @start="handleMoveStart"
                :move="handleMove"
        >
          <li
                  class="flex-item"
                  :class="{'no-put': item.type == 'divider'}"
                  v-for="(item, index) in layoutComponents"
                  :key="index"
          >
            <a>
              <i :class="item.icon"></i>
              <span>{{item.desc}}</span>
            </a>
          </li>
        </draggable>
      </template>


      <template v-if="basicComponents.length">
        <div class="widget-cate">基础字段</div>
        <draggable
          class="flex-container"
          tag="ul"
          :list="basicComponents"
          v-bind="{group:{ name:'form', pull:'clone',put:false},sort:false, ghostClass: 'ghost'}"
          @end="handleMoveEnd"
          @start="handleMoveStart"
          :move="handleMove"
        >
          <li
            class="flex-item"
            :class="{'no-put': item.type == 'divider'}"
            v-for="(item, index) in basicComponents"
            :key="index"
          >
            <a>
              <i :class="item.icon"></i>
              <span>{{item.desc}}</span>
            </a>
          </li>
        </draggable>
      </template>



      <template v-if="advanceComponents.length">
        <div class="widget-cate">高级字段</div>
        <draggable
          tag="ul"
          class="flex-container"
          :list="advanceComponents"
          v-bind="{group:{ name:'form', pull:'clone',put:false},sort:false, ghostClass: 'ghost'}"
          @end="handleMoveEnd"
          @start="handleMoveStart"
          :move="handleMove"
        >
          <li
            class="flex-item"
            :class="{'no-put': item.type == 'divider'}"
            v-for="(item, index) in advanceComponents"
            :key="index"
          >
            <a>
              <i :class="item.icon"></i>
              <span>{{item.desc}}</span>
            </a>
          </li>
        </draggable>
      </template>
    </div>
  </el-scrollbar>
</template>
<script>
import Draggable from "vuedraggable";
import deepmerge from "deepmerge";
import {
  basicComponents,
  layoutComponents,
  advanceComponents
} from "@/api/controlsConfig.js";

export default {
  name: "field-panel",
  components: { Draggable },
  props: {},
  data() {
    return {
      basicComponents,
      layoutComponents,
      advanceComponents
    };
  },
  methods: {
    handleMoveEnd: function(evt) {},
    handleMoveStart: function(evt) {
      // 新增控件时  将控件属性切断联系
      evt.item._underlying_vm_ = deepmerge({}, evt.item._underlying_vm_, {
        clone: true
      });
      const key =
        Date.parse(new Date()) + "_" + Math.ceil(Math.random() * 99999);
      evt.item._underlying_vm_.key = key;
    },
    handleMove: function(evt) {}
  }
};
</script>
<style lang="scss" scoped>
@import "@/assets/css/form-editor.scss";

.widget-cate {
  padding: 10px 0 0 10px;
  font-weight: bold;
  color: #777;
}

ul.flex-container {
  margin: 0;
  list-style: none;
  padding: 10px 5px;
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

ul.flex-container > li.flex-item {
  padding: 5px;
  height: 17px;
  border: 1px solid #ccc;
  margin: 4px;
  width: 87px;
  border-radius: 5px;
  cursor: pointer;
}

ul.flex-container > li.flex-item:hover {
  box-shadow: 0 2px 5px rgba(86, 96, 117, 0.15);
}

li.flex-item > a > i {
  font-size: 16px;
}

li.flex-item > a > span {
  margin-left: 8px;
}
</style>
