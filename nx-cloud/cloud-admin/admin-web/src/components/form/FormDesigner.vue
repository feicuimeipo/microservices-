<template>
  <el-container class="fullheight">
    <el-header class="header" height="48px">
      <FormNavigator
        ref="FormNavigator"
        @saveValidate="saveValidate"
        @close-dialog="close"
        :widgetForm="widgetForm"
        :form-data="formData"
        :form-id="formId"
      />
    </el-header>
    <el-container>
      <el-aside class="controler-container" width="230px">
        <field-panel />
      </el-aside>
      <el-main class="widget-form-container">
        <widget-panel ref="widgetForm" :data="widgetForm" :select.sync="widgetFormSelect" />
      </el-main>
      <el-aside width="350px" class="property-container">

        <property-panel
          @saveEnd="saveEnd"
          ref="propertyPanel"
          :data.sync="widgetFormSelect"
          :bo-def-data="boDefData"
          :main-bo-fields.sync="mainBoFields"
          :sub-tables="subTables"
          :form-data.sync="formData"
          :sun-tables-map="sunTablesMap"
        />
      </el-aside>
    </el-container>
  </el-container>
</template>
<script>
import FormNavigator from "@/components/form/FormNavigator.vue";
import FieldPanel from "@/components/form/FieldPanel.vue";
import WidgetPanel from "@/components/form/WidgetPanel.vue";
import PropertyPanel from "@/components/form/PropertyPanel.vue";
import request from "@/request.js";

export default {
  props: ["visible", "formId", "formDefId", "addBpmForm"],
  components: { FormNavigator, FieldPanel, WidgetPanel, PropertyPanel },
  data() {
    return {
      widgetForm: {
        list: [],
        config: {
          labelWidth: 100,
          labelPosition: "right",
          size: "small"
        }
      },
      formData: this.addBpmForm,
      boDefData: null,
      widgetFormSelect: { options: { validateType: "" } },
      mainBoFields: [],
      subTables: [],
      sunTablesMap: {},
    };
  },
  watch: {
    visible: {
      handler: function(newVal) {
        if (newVal) {
          this.initData(this.formId, this.addBpmForm.bos);
        }
      },
      immediate: true
    }
  },
  mounted() {},
  methods: {
    saveEnd(){
      this.$refs.FormNavigator.saveEnd();
    },
    saveValidate(){
        this.$refs.propertyPanel.saveValidate();
    },
    close(value) {
      if(value){
        this.$emit("update:formDefId", value.formData.defId);
        this.$emit("update:formId", value.formData.id);
        this.formData = value.formData;
        this.formData.rev = value.rev;
      }else{
        this.$emit("update:visible", false);
      }
    },
    initData(formId, bos) {
      if (this.formDefId) {
        request
          .get("${form}/form/formDef/v1/get?formDefId=" + this.formDefId)
          .then(response => {
            this.widgetForm = JSON.parse(response.data.value.expand);
          });
      }

      if (bos && bos.length > 0) {
        this.getBoDefData(bos.extractByKey("id").join(","));
        this.widgetForm.boDefList = bos;
        return;
      }

      if (formId) {
        request
          .get("${form}/form/form/v1/formDesign?formId=" + formId)
          .then(response => {
            this.formData = response.data;
            this.formData.id = formId;
            this.getBoDefData(
              JSON.parse(this.formData.bos)
                .extractByKey("id")
                .join(",")
            );
          });
      }
    },
    getBoDefData(boDefId) {
      request.post("${form}/bo/def/v1/getBOTree", boDefId).then(response => {
        this.boDefData = response.data;
        this.getMainBoFields();
      });
    },
    getMainBoFields() {
      this.mainBoFields = [];
      // 多个BO   一个Bo 对应一个主表
      this.boDefData.children.forEach(element => {
        let boData = { ...element };
        let boSubTables = boData.children.filter(
          field => field.nodeType == "sub"
        );

        boSubTables.forEach(subTable => {
          //收集孙实体
          const sunTables = subTable.children.filter(field =>  field.nodeType == "sub");
          if(sunTables && sunTables.length>0){
            this.sunTablesMap[subTable.name] = sunTables;
          }

          let sunTable1 = {...subTable};
          sunTable1.children = subTable.children.filter(field =>  (field.status != "hide" && field.nodeType != "sub"));
          this.subTables.push(sunTable1);
        });

        // 删除子表字段
        boData.children = boData.children.filter(field => {
          return field.nodeType != "sub" && field.status != "hide";
        });

        this.mainBoFields.push(boData);
      });
    }
  }
};
</script>
<style scoped>
.header {
  padding: 0;
  background: #fff;
  z-index: 7;
}

.controler-container {
  margin-right: 3px;
  box-shadow: 2px 0 5px #ededed;
}

.property-container {
  margin-left: 3px;
  box-shadow: -2px 0 5px #ededed;
}

.widget-form-container {
  background: #fafafa;
  position: relative;
  padding: 0 5px;
  margin-top: 1px;
}

.footer-container {
  border-top: 1px solid #ededed;
}
</style>
