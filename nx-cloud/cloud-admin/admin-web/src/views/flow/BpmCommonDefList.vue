<template>
    <div style="height:100%">
        <ht-table
            @load="loadData"
            :data="tableData"
            :pageResult="pageResult"
            :nopagination="false"
            :show-export="false"
            :show-custom-column="false"
            quick-search-props="name"
            ref="htTable"
        >
            <template v-slot:toolbar>
                <el-button-group>
                <el-button size="small" @click="showDialog()" icon="el-icon-plus">添加</el-button>
                <ht-delete-button
                :url="deleteUrl"
                :htTable="$refs.htTable"
                >删除</ht-delete-button>
            </el-button-group>
            </template>
            <ht-table-column type="index" width="50" align="center" label="序号" />
            <ht-table-column  prop="name" align="center" :show-overflow-tooltip="true" label="流程名称" :sortable="true"/>
            <ht-table-column  prop="userName" width="200" align="center" label="所属人" :sortable="true"/>
        </ht-table>
        <!-- 流程选择对话框  -->
        <eip-flow-dialog
            ref="eipFlowDialog"
            name="eipFlowDialog"
            @onConfirm="dialogOnConfirm"
            append-to-body
        />
    </div>
 </template>

<script>
import { Message } from 'element-ui';
import req from "@/request.js";
const eipFlowDialog = () => import("@/components/dialog/EipFlowDialog.vue");
export default {
    components:{eipFlowDialog},
    name: "BpmCommonDefList",
    computed: {
        //删除对话框数据的URL
        deleteUrl: function() {
            return "${bpmModel}/bpmModel/BpmOftenFlow/v1/removes";
        },
    },
    data() {
        return {
            tableData:[],//列表数据
            pageResult: {
                page: 1,
                pageSize: 20,
                total: 0
            },
        }
    },
    methods: {
        //打开流程选择器对话框
        showDialog(){
            this.$refs.eipFlowDialog.showDialog([]);
        },
        //监听常用流程确认事件
        dialogOnConfirm(data) {
            let defkeys = [];
            for(let i=0;i<data.length;i++){
                defkeys.push(data[i].defKey);
            }
            if(defkeys.length==0){
                Message.warning("至少选择一条流程");
                return;
            }else{
                const this_ = this;
                req.post("${bpmModel}/bpmModel/BpmOftenFlow/v1/save", defkeys.join(",")).then(function(resp){
                    resp = resp.data;
                    if(resp.state){
                        Message.success("添加常用流程成功");
                        this_.$refs.htTable.load();//重新加载列表数据
                    }else{
                        Message.error( resp && resp.message ?resp.message: '添加常用流程失败');
                    }
                })
            }
        },
        //页面加载显示数据
        loadData(param, cb) {
            const this_ = this;
            req.post("${bpmModel}/bpmModel/BpmOftenFlow/v1/list",param).then(response => {
                this_.tableData = response.data.rows;
                this_.pageResult = {
                    page: response.data.page,
                    pageSize: response.data.pageSize,
                    total: response.data.total
                };
            }).finally(() => cb());
        }
    }
}
</script>
<style lang="scss" scoped>
.el-main {
  padding-top: 0px;
}
.urgent-text .inputs {
  width: 100%;
}
</style>
