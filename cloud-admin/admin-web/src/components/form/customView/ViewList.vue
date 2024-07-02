<template>
  <div class="fullheight">
    <el-page-header @back="back" content="视图列表" style="margin: 20px 0"/>
    <ht-table
        @load="loadData"
        :data="data"
        :pageResult="pageResult"
        :selection="true"
        quick-search-props="name,alias"
        :show-export="false"
        :show-custom-column="false"
        ref="table">
      <template v-slot:toolbar>
        <el-button size="small" @click="edit()" icon="el-icon-plus">添加</el-button>
        &nbsp;
        <ht-delete-button
            :url="deleteUrl"
            :htTable="$refs.table">删除
        </ht-delete-button>
        &nbsp;
<!--        <el-button size="small" @click="back" icon="el-icon-back">返回</el-button>-->
      </template>
      <ht-table-column type="index" width="50" align="center" label="序号" />
      <ht-table-column
          prop="name"
          label="视图名称"
          :sortable="true">
        <template slot-scope="scope">
          <el-button type="text" @click="edit(scope.row.id)">{{scope.row.name}}</el-button>
        </template>
      </ht-table-column>
      <ht-table-column
          prop="alias"
          label="视图别名"
          :sortable="true"/>
      <ht-table-column
          prop="sqlAlias"
          label="sql别名"
          :sortable="true"/>
      <ht-table-column
          label="操作">
        <template slot-scope="scope">
          <el-button @click="preview(scope.row.sqlAlias,scope.row.alias)" icon="el-icon-view">预览</el-button>
        </template>
      </ht-table-column>
    </ht-table>
    <view-edit ref="viewEdit" :id="selectedId" :sqlAlias="selectedAlias" @after-save="afterSave"/>
  </div>
</template>

<script>
  import form from "@/api/form.js";
  import {Base64} from "js-base64";
  import {mapState} from "vuex";
  const viewEdit = () => import("@/components/form/customView/ViewEdit.vue");
  export default {
    components:{
      viewEdit
    },
    computed: {
      ...mapState({
        currentUser: state => state.login.currentUser
      })
    },
    data(){
      return{
        data:[],
        pageResult:{
          page:0,
          pageSize:50,
          total:0
        },
        alias:"",
        selectedId:"",
        selectedAlias:"",
        deleteUrl:window.context.form+"/form/query/queryView/remove"
      }
    },
    methods:{
      loadData(param,cb){
        form.getViewList(param,this.$route.query.alias).then(response=>{
            this.data = response.rows;
            this.pageResult = {
              page:response.page,
              pageSize:response.pageSize,
              total:response.total
            }
          })
          .finally(()=>{
            cb();
          })
      },
      preview(sqlAlias,alias){
        window.open(window.context.front+"/statement/querySql/queryView/"+sqlAlias+"/"+alias+"/true?token="+this.currentUser.token);
      },
      edit(id){
        this.selectedAlias = this.$route.query.alias;
        if (id){
          this.selectedId = id;
        }else{
          this.selectedId = "";
        }
        this.$refs.viewEdit.handleOpen();
      },
      back(){
        this.$router.go(-1);
      },
      afterSave(){
        this.$refs.table.load();
      }
    }
  }
</script>

<style scoped>
</style>
