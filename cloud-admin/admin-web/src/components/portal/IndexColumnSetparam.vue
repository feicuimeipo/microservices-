<template>
    <el-dialog :title="title" width="1000px" :visible.sync="dialogVisible" :before-close="handleClose" :close-on-click-modal="false">
        <div style="margin-bottom: 20px;">
            <el-button size="small" @click="addParam" icon="el-icon-plus">添加</el-button>
        </div>
        <el-table 
          :data="dataParams"
          :border="true"
          ref="htTable"
          v-if="dialogVisible"
        >
            <el-table-column prop="name" label="参数名">
                <template v-slot="{row}">
                   <ht-input
                        class="ht"
                        v-model="row.name"
                        autocomplete="off"
                        :validate="{required:true}"
                        placeholder="请输入别名"
                    ></ht-input>
                </template>
            </el-table-column>
            <el-table-column prop="type" label="参数类型">
                <template v-slot="{row}">
                    <ht-select v-model="row.type" :options="typeArr" ></ht-select>
                </template>
            </el-table-column>
            <el-table-column prop="mode" label="值来源">
                 <template v-slot="{row}">
                    <ht-select v-model="row.mode" :options="modeArr" ></ht-select>
                </template>
            </el-table-column>
            <el-table-column prop="value" label="参数值" width="300">
                 <template v-slot="{row}">
                    <ht-input type="textarea" v-model="row.value" rows="4"></ht-input>
                </template>
            </el-table-column>
            <el-table-column width="150">
                <template v-slot:header>
                    <span style="color: #2274af">操作</span>
                </template>
                <template v-slot="{row}">
                    <el-button @click="delParam(row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="handleSave" >确 定</el-button>
          <el-button @click="handleClose">取 消</el-button>
        </div>
    </el-dialog>
</template>
<script>
export default {
    name:'index-column-setparam',
    props:{
        setParams:{type:String}
    },
    data(){
        return {
            title:"设置参数",
            dialogVisible:false,//是否显示对话框
            typeArr:[
                {key:'string',value:'string'},
                {key:'int',value:'int'},
                {key:'float',value:'float'},
                {key:'double',value:'double'},
                {key:'byte',value:'byte'},
                {key:'short',value:'short'},
                {key:'long',value:'long'},
                {key:'boolean',value:'boolean'},
                {key:'date',value:'date'}],
            modeArr:[
                {key:'0',value:'固定值'},
                {key:'1',value:'动态传入'},
                {key:'2',value:'脚本'},
            ]
        }
    },
    computed:{
        dataParams:function(){
            return JSON.parse(this.setParams||'[]');
        }
    },
    methods:{
        showDialog(){
            this.dialogVisible = true;
        },
        handleSave(){
            for(let item of this.dataParams){
                if(!item.name || !item.type || !item.mode){
                    this.$message({message:"参数名、参数类型、值来源都不能为空",type:"warning"});
                    return ;
                }
            }
            this.$emit("handleDataparamSave",JSON.stringify(this.dataParams))
            this.dialogVisible = false;
        },
        handleClose(){
            this.dialogVisible = false;
        },
        addParam(){
            this.dataParams.push({name:"",type:"string",mode:"0",value:""});
        },
        delParam(param){
            this.dataParams.remove(param)
        }
    }
}
</script>