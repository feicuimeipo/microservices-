<template>
  <div class="fullheight">
    <ht-table
        @load="loadData"
        :data="data"
        :pageResult="pageResult"
        :selection="true"
        :show-export="false"
        :show-custom-column="false"
        quick-search-props="name,alias"
        ref="table">
      <template v-slot:toolbar>
        <el-button-group>
          <el-button size="small" @click="edit()" icon="el-icon-plus">添加</el-button>
          <ht-delete-button
              :url="deleteUrl"
              :htTable="$refs.table">删除
          </ht-delete-button>
        </el-button-group>
      </template>
      <ht-table-column
          prop="name"
          label="名称"
          :sortable="true"
          :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <el-link type="primary"  @click="edit(scope.row.id)" title="查看详情" >{{scope.row.name}}</el-link>
        </template>
      </ht-table-column>
      <ht-table-column
          prop="type"
          label="是否已发布"
          :filters="[
            { text: '否', value: '0' },
            { text: '是', value: '1' }
          ]">
        <template v-slot="{ row }">
          <el-tag type="danger" v-if="row.type === '0'">否</el-tag>
          <el-tag type="success" v-if="row.type === '1'">是</el-tag>
        </template>
      </ht-table-column>
    </ht-table>
    <report-setting ref="reportSetting" @after-save="afterSave"/>
  </div>
</template>

<script>
  import flow from "@/api/flow.js";
  const flowChartEdit = () => import("@/components/form/chart/FlowChartEdit.vue");
  const reportSetting = () => import("@/components/form/chart/ReportSetting.vue");
  export default {
    components:{
      flowChartEdit,
      reportSetting
    },
    data(){
      return {
        data:[],
        pageResult:{
          page:0,
          pageSize:50,
          total:0
        },
        selectedId:"",
        deleteUrl:window.context.bpmRunTime+"/runtime/report/v1/removeList"
      }
    },
    methods:{
      loadData(param,cb){
        flow
          .getFlowChartList(param)
          .then(response=>{
            this.pageResult = {
              page:response.page,
              pageSize: response.pageSize,
              total: response.total
            };
            this.data = response.rows;
          })
          .finally(()=>{
            cb();
          })
      },
      edit(id){
        if (id){
          this.$router.push({path:"/reportManager/flowChartEdit",query:{id:id}});
        }else{
          this.$refs.reportSetting.handleOpen();
        }
      },
      afterSave(){
        this.$refs.table.load();
      },
    }
  }
</script>

<style scoped>

</style>
