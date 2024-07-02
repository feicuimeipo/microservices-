<template>
    <div>
        <div style="height: 41px; line-height: 46px">
            <el-tooltip
                    class="item"
                    effect="dark"
                    content="一个业务对象有且仅有一个主实体，可以添加任意个子实体"
                    placement="right-start"
            >
                <span class="el-icon-question" style="margin-left: 10px"><strong class="labelTitle" style="margin-left: 1px">实体列表</strong></span>
            </el-tooltip>
            <el-button style="margin-left: 28px" @click="addEntRows()" type="primary"
            >添加实体</el-button
            >
            <el-button @click="addEntExts()" type="primary"
            >添加外部表</el-button
            >
        </div>
        <el-divider></el-divider>
        <div :style="'height:' + winHeight + 'px'">
        <el-scrollbar class="scrollbar-fullheight" :style="'max-height:'+winHeight+'px;'">
            <div v-for="(item,index) in formData.ents" :key="index" :class="item.isCheck?'checkBorder':'defaultBorder'">
                <div @click="getEntsByIndex(index)">
                    <div :class="item.isCheck?'checkTitle':'defaultTitle'" style="margin-bottom: 10px">
                        <span :class="item.isCheck?'labelTitle3':'labelTitle2'" style="margin-left: 13px">{{item.show}}</span>
                        <span :class="item.isExternal == '1'?'el-icon-search':'el-icon-plus'"
                              :style="item.isExternal == '1'?'cursor:pointer;margin-left: 47%':'cursor:pointer;margin-left: 54%;'"
                              v-if="item.show != '主实体'"
                              @click.stop="item.isExternal == '1'?addEntExts('search') : addGrandSonEnt(index)"
                        ></span>
                        <span
                                :class="item.isCheck?'el-icon-delete-solid2':'el-icon-delete-solid'"
                                :style="item.show != '主实体' && (!item.external && item.isExternal != '1')? 'cursor:pointer;margin-left: 5%':((item.external || item.isExternal == '1') ? 'cursor:pointer;margin-left: 5%' : 'cursor:pointer;margin-left: 70%')"
                                v-if="!formData.deployed || formData.deployed && !item.id"
                                @click.stop="deleteEntRows(index)"
                        ></span>
                    </div>
                    <div style="height: 32px;margin-bottom: 20px">
                        <div
                                style="height: 32px;line-height: 32px;width: 20%;float: left;margin-left: 10px"
                        >
                            实体描述
                        </div>
                        <div style="height: 32px;width: 72%;float: left">
                            <el-form-item>
                                <el-input
                                        :disabled="item.id && formData.deployed?true:false"
                                        v-model="item.comment"
                                        placeholder="请输入内容"
                                        :id="'changeEntsDesc'+index"
                                        @input="chineseFormat('entName'+index,formData.ents,'name',index,item.comment)"
                                        clearable
                                        :validate="{ required: true }"
                                ></el-input>
                            </el-form-item>
                        </div>
                    </div>
                    <div style="height: 42px;margin-bottom: 10px">
                        <div
                                style="height: 32px;line-height: 32px;width: 20%;float: left;;margin-left: 10px"
                        >
                            实体名称
                        </div>
                        <div style="height: 32px;width: 72%;float: left">
                            <el-form-item>
                                <ht-input
                                        :disabled="item.id?true:false"
                                        @change="checkIsChinese()"
                                        v-model="item.name"
                                        placeholder="请输入内容"
                                        :id="'entName'+index"
                                        :name="'entName'+index"
                                        :validate="'required: true|regex:^[a-zA-Z][a-zA-Z0-9_]*$,只能输入字母、数字、下划线，且以字母开头'"
                                ></ht-input>
                            </el-form-item>
                        </div>
                    </div>
                    <div v-if="item.show != '主实体' && item.show != '主实体-外'" style="height: 32px">
                        <div
                                style="height: 32px;line-height: 32px;width: 20%;float: left;margin-left: 10px"
                        >
                            关系
                        </div>
                        <div style="height: 32px;float: left">
                            <el-form-item>
                                <ht-radio :disabled="item.id && formData.deployed ? true : false" :options="relationData" v-model="item.relation"></ht-radio>
                            </el-form-item>
                        </div>
                    </div>
                </div>
                <div v-if="item.children && item.children.length > 0">
                    <el-collapse v-model="activeNames" accordion v-for="(v,i) in item.children" :key="i">
                        <div @click.stop="getGrandSonEntsByIndex(i,index)">
                            <el-collapse-item :name="i">
                                <template slot="title">
                                    {{v.show}}<span class="el-icon-plus"
                                                    :style="v.isExternal ? 'cursor:pointer;margin-left: 53%;':'cursor:pointer;margin-left: 60%;'"
                                                    @click.stop="addGrandSonEnt(index)"
                                ></span>
                                    <span
                                            class="el-icon-delete-solid"
                                            style="cursor:pointer;margin-left: 5%"
                                            v-if="!formData.deployed || formData.deployed && !v.id"
                                            @click.stop="deleteGrandSonEntRows(i,index)"
                                    ></span>
                                </template>
                                <div style="border-bottom: 1px solid #EBEEF5;margin-bottom: 15px">
                                    <div style="height: 32px;margin-bottom: 20px">
                                        <div
                                                style="height: 32px;line-height: 32px;width: 20%;float: left;margin-left: 10px"
                                        >
                                            实体描述
                                        </div>
                                        <div style="height: 32px;width: 72%;float: left">
                                            <el-form-item>
                                                <el-input
                                                        :disabled="v.id && formData.deployed? true : false"
                                                        v-model="v.desc"
                                                        placeholder="请输入内容"
                                                        :id="'changeGrandSonEntsDesc'+i"
                                                        @blur="entBlur(i)"
                                                        @input="chineseFormat('changeGrandSonEntsName'+ index +i,formData.ents[index].children,'name',i,v.desc)"
                                                        :validate="{ required: true }"
                                                        clearable
                                                ></el-input>
                                            </el-form-item>
                                        </div>
                                    </div>
                                    <div style="height: 42px;margin-bottom: 10px">
                                        <div
                                                style="height: 32px;line-height: 32px;width: 20%;float: left;;margin-left: 10px"
                                        >
                                            实体名称
                                        </div>
                                        <div style="height: 32px;width: 72%;float: left">
                                            <el-form-item>
                                                <ht-input
                                                        :disabled="v.id ? true : false"
                                                        @change="checkIsChinese()"
                                                        v-model="v.name"
                                                        placeholder="请输入内容"
                                                        :id="'changeGrandSonEntsName'+ index +i"
                                                        :name="'changeGrandSonEntsName'+i"
                                                        @blur="entBlur(i)"
                                                        :validate="'required: true|regex:^[a-zA-Z][a-zA-Z0-9_]*$,只能输入字母、数字、下划线，且以字母开头'"
                                                ></ht-input>
                                            </el-form-item>
                                        </div>
                                    </div>
                                    <div v-if="item.show != '主实体'" style="height: 32px">
                                        <div
                                                style="height: 32px;line-height: 32px;width: 20%;float: left;margin-left: 10px"
                                        >
                                            关系
                                        </div>
                                        <div style="height: 32px;float: left">
                                            <el-form-item>
                                                <ht-radio :disabled="v.id && formData.deployed ? true : false" :options="relationData" v-model="v.relation"></ht-radio>
                                            </el-form-item>
                                        </div>
                                    </div>
                                </div>
                            </el-collapse-item>
                        </div>
                    </el-collapse>
                </div>
            </div>
        </el-scrollbar>
        </div>
    </div>
</template>

<script>
    export default {
        name: "BusinessObjEnts",
        props: ["formData"],
        data(){
          return {
              formDataEnts: {},
              relationData: [
                  { key: "onetoone", value: "一对一" },
                  { key: "onetomany", value: "一对多" }
              ],
              winHeight: 969,
              activeNames: ['-1'],
            }
        },
        methods:{
            addEntRows(){
                this.$emit("addEntRows");
            },
            addEntExts(param){
                if(param){
                    this.$emit("addEntExts","search");
                }else{
                    this.$emit("addEntExts")
                }
            },
            getEntsByIndex(index){
                this.$emit("getEntsByIndex", index);
                this.$forceUpdate();
            },
            addGrandSonEnt(index){
                this.$emit("addGrandSonEnt", index);
            },
            deleteEntRows(index){
                this.$emit("deleteEntRows", index);
            },
            entBlur(index){
                this.$emit("entBlur", index);
            },
            chineseFormat(id,list,param,index,v){
                this.$emit("chineseFormat",id,list,param,index,v);
            },
            checkIsChinese(){
                this.$emit("checkIsChinese");
            },
            getGrandSonEntsByIndex(i, index){
                this.$emit("getGrandSonEntsByIndex", i, index);
            },
            deleteGrandSonEntRows(i, index){
                this.$emit("deleteGrandSonEntRows", i, index);
            },
        },
        watch:{
            formData: function (v) {
                this.formDataEnts = v;
            }
        },
        mounted() {
            let height = this.winHeight - 280;
            if(this.winHeight == height){
                return;
            }
            if (window.innerHeight) {
                this.winHeight = window.innerHeight;
            } else if ((document.body) && (document.body.clientHeight)) {
                this.winHeight = document.body.clientHeight;
            }
            this.winHeight = this.winHeight - 280;
        }
    }
</script>

<style scoped>
    .checkBorder{
        width:281px;
        background:rgba(255,255,255,1);
        border:1px solid rgba(91,157,255,1);
        margin-bottom: 10px;
        margin-left: 10px;
    }
    .defaultBorder{
        width: 281px;
        background:rgba(255,255,255,1);
        border:1px solid rgba(236,237,241,1);
        margin-bottom: 10px;
        margin-left: 10px;
    }
    .checkTitle{
        width:280px;
        height:30px;
        background:rgba(91,157,255,1);
        border:1px solid rgba(91,157,255,1);
    }
    .defaultTitle{
        width:280px;
        height:30px;
        background:rgba(244,244,245,1);
        border:1px solid rgba(236,237,241,1);
    }
    .labelTitle3{
        width:43px;
        height:15px;
        font-size:14px;
        font-family:Microsoft YaHei;
        font-weight:400;
        color:rgba(255,255,255,1);
        line-height:24px;
    }
    .labelTitle2{
        width:43px;
        height:15px;
        font-size:14px;
        font-family:Microsoft YaHei;
        font-weight:400;
        color:rgba(145,145,145,1);
        line-height:30px;
    }
    .el-icon-delete-solid2{
        color: white;
        width: 12px;
        height: 12px;
    }
    .el-icon-delete-solid2:before {
        content: "\E7C9";
    }
</style>
