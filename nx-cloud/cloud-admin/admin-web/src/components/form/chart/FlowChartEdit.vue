<template>
  <div class="fullheight">
    <el-page-header @back="$router.go(-1)" content="视图列表" style="margin: 20px 0"/>
    <div style="margin: 10px 0">
      <ht-input v-model="data.name"/>&nbsp;
      <el-button @click="edit()">添加报表</el-button>
      <el-button type="primary" @click="save">保存</el-button>
      <el-button v-if="data.type!=='1' && data.id" @click="publish">发布</el-button>
    </div>
    <div v-for="(option,index) in options">
      <el-button size="mini" @click="edit(option.id)">编辑</el-button>
      <el-button size="mini" @click="remove(option.id)">删除</el-button>
      <el-button size="mini" @click="refresh(option.id,index)">刷新</el-button>
      <chart :option="option" :index="index" :is-convert-option="true"/>
    </div>
    <report-setting ref="reportSetting" :id="selectedId" :report="selectedReport" @after-save="afterSave"/>
  </div>
</template>

<script>
  import flow from "@/api/flow.js";
  const chart = () => import("@/components/form/chart/Chart.vue");
  const reportSetting = () => import("@/components/form/chart/ReportSetting.vue");
  export default {
    components:{
      chart,
      reportSetting
    },
    props:{
      id:String
    },
    data(){
      return {
        data:{},
        options:[],
        selectedId:'',
        selectedReport:{}
      }
    },
    mounted() {
      this.getReportChartData();
    },
    methods:{
      getReportChartData(){
        if(this.$route.query.id){
          flow.getReportList(this.$route.query.id).then(data=>{
            if (data){
              this.data = data;
            }else{
              this.data = {};
            }
          });
          flow.getEchartsData(this.$route.query.id).then(data=>{

            if (this.options){
              this.options = data.value;
            }else{
              this.options = [];
            }
          });
        }else{
          this.data = {};
          this.options = [];
        }
      },
      edit(id){
        this.selectedReport = {id:this.data.id,name:this.data.name};
        if (id){
          this.selectedId = id;
        }else{
          this.selectedId = "";
        }
        this.$refs.reportSetting.handleOpen();
      },
      remove(id){
        this.$confirm("确定删除吗？").then(()=>{
          flow.removeAct(id).then(data=>{
            this.$message("删除成功");
            this.getReportChartData();
          })
        }).catch(()=>{
        });
      },
      afterSave(){
        this.$router.go(0);
      },
      save(){
        let data={
          name:this.data.name,
          type:this.data.type,
          id:this.data.id
        };
        flow.saveReport(data).then(data=>{
          if (data.state) {
            this.data.id=data.value;
            this.$message(data.message);
            this.$router.go(-1);
          } else {
            this.$message.error(data.message);
          }
        })
      },
      publish(){
        flow.publishReport(this.data.id).then(data=>{
          if (data.state) {
            this.$message("发布成功!");
            this.data.type='1';
            this.$emit("after-save",{});
          } else {
            this.$message.error(data.message);
          }
        })
      },
      refresh(id,index){
        flow.getSingleEchartsData(id).then(data=>{
          this.options.splice(index,1);
          this.options.splice(index,0,data.value);
        })
      }
    }
  }
</script>

<style scoped>
  .fullheight{
    overflow: auto;
  }
</style>
