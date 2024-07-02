<template>
    <div>
        <div style="height: 41px;line-height: 41px">
            <el-tooltip
                    class="item"
                    effect="dark"
                    content="按F2可快速添加字段"
                    placement="top">
                <el-button @click="addAttrRows" :disabled="formData.ents[entIndex] && formData.ents[entIndex].isExternal != '1' || grandSonIndex != -1 && formData.ents[entIndex].children && formData.ents[entIndex].children[grandSonIndex] && formData.ents[entIndex].children[grandSonIndex].isExternal != '1'?false:true" type="primary"  style="margin-top: 9px;" >
                    <el-tooltip
                            class="item"
                            effect="dark"
                            content="在实体列表中选中某个实体时，可以查看/编辑该实体下的字段信息"
                            placement="top"><i class="icon-question" style="margin-right: 8px" /></el-tooltip>
                    添加字段</el-button>
            </el-tooltip>
            <el-button @click="refreshAttr()" :disabled="formData.ents[entIndex] && formData.ents[entIndex].isExternal == '1' || grandSonIndex != -1 && formData.ents[entIndex].children && formData.ents[entIndex].children[grandSonIndex] &&  formData.ents[entIndex].children[grandSonIndex].isExternal == '1'?false:true" type="primary"  style="margin-top: 9px;" >
                刷新字段</el-button>
        </div>
        <el-divider></el-divider>
        <div v-if="formData.ents.length!=0">
            <el-table
                    :data="attrData"
                    style="width: 100%"
                    ref="attrTableScrollbarHeight"
                    data-vv-scope="attr"
                    :max-height="winHeight"
            >
                <el-table-column label="序号" type="index" align="center" width="40"></el-table-column>
                <el-table-column
                        label="注释"
                        align="center"
                ><template slot-scope="scope">
                    <el-tooltip class="item" effect="dark" :content="scope.row.comment" placement="top-start">
                      <el-form-item class="table-item">
                        <span v-if="scope.row.id && formData.deployed || scope.row.status == 'hide'">{{scope.row.comment}}</span>
                          <el-input
                                        v-else
                                        v-model="scope.row.comment"
                                        placeholder="请输入内容"
                                        :id="scope.row.name+'comment'+ scope.$index"
                                        @blur="entBlur(scope.$index)"
                                        @input="chineseFormat(scope.row.name+'attrName'+scope.$index, attrData,'name',scope.$index,scope.row.comment)"
                                        clearable
                                        :validate="{ required: true }"
                          ></el-input>
                      </el-form-item>
                    </el-tooltip>
                </template>
                </el-table-column>
                <el-table-column label="名称" align="center"
                ><template slot-scope="scope">
                    <el-form-item class="table-item">
                        <span v-if="scope.row.id && formData.deployed
                        || scope.row.status=='hide'
                        || (openGrandSonAttr ? grandSonIndex != -1 && formData.ents[entIndex].children[grandSonIndex].isExternal == '1' : formData.ents[entIndex].isExternal == '1')">{{scope.row.name}}</span>
                        <ht-input
                                v-else
                                @change="checkIsChinese()"
                                :id="scope.row.name+'attrName'+scope.$index"
                                v-model="scope.row.name"
                                @blur="entBlur(scope.$index)"
                                placeholder="请输入内容"
                        ></ht-input>
                    </el-form-item>
                </template>
                </el-table-column>
                <el-table-column label="必填" width="70" align="center">
                    <template slot-scope="scope">
                        <el-form-item class="table-item">
                      <span v-if="scope.row.id && formData.deployed
                      || scope.row.status=='hide'
                      || (openGrandSonAttr ? grandSonIndex != -1 && formData.ents[entIndex].children[grandSonIndex].isExternal == '1' : formData.ents[entIndex].isExternal == '1')">
                        <span v-if="scope.row.isRequired ==1">是</span>
                        <span v-else>否</span>
                      </span>
                            <el-switch
                                    v-else
                                    v-model="scope.row.isRequired"
                                    inactive-value='0'
                                    active-value='1'
                            />
                        </el-form-item>
                    </template>
                </el-table-column>
                <el-table-column label="数据类型" align="center">
                    <el-table-column
                            prop="name"
                            label="类型"
                            align="center"
                    >
                        <template slot-scope="scope">
                            <el-form-item class="table-item">
                                <span v-if="scope.row.id && formData.deployed
                                || scope.row.status=='hide'
                                || (openGrandSonAttr ? grandSonIndex != -1 && formData.ents[entIndex].children[grandSonIndex].isExternal == '1' : formData.ents[entIndex].isExternal == '1')">{{scope.row.dataType}}</span>
                                <ht-select
                                        v-else
                                        class="m-r"
                                        :options="dataType"
                                        v-model="scope.row.dataType"
                                        @change="defaultDataFormat(scope.row, scope.$index)"
                                />
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column align="center" label="格式" width="210">
                        <template slot-scope="scope">
                            <el-form-item class="table-item">
                        <span
                                v-if="scope.row.dataType != 'date' && scope.row.dataType != '日期' ? false :
                          (scope.row.id && formData.deployed
                          || scope.row.status=='hide'
                          || (openGrandSonAttr ? grandSonIndex != -1 && formData.ents[entIndex].children[grandSonIndex].isExternal == '1' : formData.ents[entIndex].isExternal == '1'))
                      "
                        >{{scope.row.format}}</span>
                                <ht-select
                                        v-else-if="scope.row.dataType == 'date'"
                                        v-model="scope.row.format"
                                        :props="{ key: 'key', value: 'value' }"
                                        :options="dateFormat"
                                ></ht-select>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table-column>
                <el-table-column label="属性长度" align="center">
                    <el-table-column
                            prop="name"
                            label="整数长度"
                            align="center"
                    >
                        <template slot-scope="scope">
                            <el-form-item class="table-item">
                        <span
                                v-if="
                        scope.row.attrLength != 0 &&
                          scope.row.id && formData.deployed
                          || scope.row.status=='hide' && scope.row.attrLength!=0
                          || (openGrandSonAttr ? grandSonIndex != -1 && formData.ents[entIndex].children[grandSonIndex].isExternal == '1' : formData.ents[entIndex].isExternal == '1')
                      "
                        >{{scope.row.attrLength}}</span>
                                <el-input
                                        v-else-if="
                            scope.row.dataType != 'date' &&
                              scope.row.dataType != 'clob'
                               && scope.row.dataType != '日期'
                               && scope.row.dataType != '大文本'
                          "
                                        :id="scope.row.name+'attrLen'+scope.$index"
                                        v-model="scope.row.attrLength"
                                ></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                    <el-table-column label="小数长度" align="center">
                        <template slot-scope="scope">
                            <el-form-item class="table-item">
                        <span
                                v-if="
                                scope.row.dataType != 'number' && scope.row.dataType != '数字' ? false :
                          (scope.row.id && formData.deployed
                          || scope.row.status=='hide'
                          || (openGrandSonAttr ? grandSonIndex != -1 && formData.ents[entIndex].children[grandSonIndex].isExternal == '1' : formData.ents[entIndex].isExternal == '1'))
                      "
                        >{{scope.row.decimalLen}}</span>
                                <el-input
                                        v-else-if="scope.row.dataType == 'number'"
                                        :id="scope.row.name+'decima'+scope.$index"
                                        v-model="scope.row.decimalLen"
                                ></el-input>
                            </el-form-item>
                        </template>
                    </el-table-column>
                </el-table-column>
                <el-table-column label="默认值" align="center">
                    <template slot-scope="scope">
                        <el-form-item class="table-item">
                            <ht-input
                                    v-if="scope.row.dataType != 'clob' && scope.row.dataType != 'date' "
                                    v-model="scope.row.defaultValue"
                                    placeholder="请输入内容"
                                    :name="'defaultValue'+scope.$index"
                            ></ht-input>
                            <ht-date v-else-if="scope.row.dataType == 'date'"  v-model="scope.row.defaultValue" :format="scope.row.format" :value-format="scope.row.format" placeholder="请选择日期"></ht-date>
                        </el-form-item>
                    </template>
                </el-table-column>
                <el-table-column
                        label="操作"
                        align="center"
                        width="100"
                >
                    <template slot-scope="scope">
                        <el-button v-if="scope.row.status == 'hide'" @click="recovery(scope.row)">恢复</el-button>
                        <el-dropdown size="mini" split-button v-else-if="formData.ents[entIndex].attributeList.length > 1 && scope.row.isExternal != '1' || formData.ents[entIndex].children && attrTableData.length > 1" @command="handleCommand" @click="handleCommand({command:'del', index:scope.$index, row:scope.row})">
                            <i class="el-icon-delete-solid"></i>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item :disabled="scope.$index==0?true:false" :command="{command:'up', row:scope.row, index:scope.$index}">上升<i class="el-icon-arrow-up"></i></el-dropdown-item>
                                <el-dropdown-item :disabled="scope.$index==(openGrandSonAttr ? attrTableData.length-1:formData.ents[entIndex].attributeList.length-1)?true:false" :command="{command:'down', row:scope.row, index:scope.$index}">下降<i class="el-icon-arrow-down"></i></el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                        <el-button size="mini" class="el-icon-delete-solid" plain v-else disabled></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
    import form from "@/api/form.js";
    import utils from "@/hotent-ui-util.js";

    export default {
        name: "BusinessObjAttr",
        props: ["openGrandSonAttr", "attrTableData", "formData", "entIndex", "grandSonIndex"],
        watch:{
            openGrandSonAttr: function (v) {
                this.isOpenGrandSonAttr = v;
            },
            attrTableData: function (v) {
                this.sonData = v;
            },
            formData: function (v) {
                this.tableData = v;
            },
            entIndex: function (v) {
                this.index = v;
            },
            grandSonIndex: function (v) {
                this.sonIndex = v;
            }
        },
        data(){
            return{
                index: 0,
                sonIndex: 0,
                winHeight: 969,
                isOpenGrandSonAttr: false,
                hideAttr: [],
                sonData: [],
                tableData: [],
                dataType: [
                    { key: "varchar", value: "字符串" },
                    { key: "number", value: "数字" },
                    { key: "date", value: "日期" },
                    { key: "clob", value: "大文本" }
                ],
                dateFormat: [
                    { key: "yyyy-MM-dd HH:mm:ss", value: "yyyy-MM-dd HH:mm:ss" },
                    { key: "yyyy-MM-dd", value: "yyyy-MM-dd" }
                    // { key: "HH:mm:ss", value: "HH:mm:ss" },//现在可以用日期控件和时间控件调整
                    // { key: "HH:mm", value: "HH:mm" }
                ],
            }
        },
        computed: {
            attrData: function(){
                const data = this.openGrandSonAttr ? this.sonData:this.formData.ents[this.entIndex].attributeList;
                return data;
            }
        },
        methods:{
            entBlur(index){
                this.$emit("entBlur", index);
            },
            chineseFormat(id,list,param,index,v){
                this.$emit("chineseFormat",id,list,param,index,v);
            },
            checkIsChinese(){
                this.$emit("checkIsChinese");
            },
            //新增字段
            addAttrRows() {
                let row = {
                    comment: "",
                    name: "",
                    isRequired: '0',
                    dataType: "varchar",
                    format: "",
                    intLen: "",
                    attrLength: 200,
                    decimalLen: 0,
                    isNew: true,
                    defaultValue: "",
                    desc: "",
                    index: this.openGrandSonAttr ? this.sonData.length +1 : this.formData.ents[this.entIndex].attributeList.length + 1
                };
                if(this.openGrandSonAttr){
                    this.sonData.push(row);
                }else{
                    this.formData.ents[this.entIndex].attributeList.push(row);
                }
            },
            //数据类型切换设置默认值
            defaultDataFormat(row, index) {
                let selectAttr = this.formData.ents[this.entIndex].attributeList;
                if(this.openGrandSonAttr){
                    selectAttr = this.sonData
                }
                selectAttr[index].attrLength = 0;
                selectAttr[index].decimalLen = 0;
                selectAttr[index].format = "";
                if (row.dataType == "date") {
                    selectAttr[index].format =
                        "yyyy-MM-dd HH:mm:ss";
                } else if (row.dataType == "varchar") {
                    selectAttr[index].attrLength = 200;
                } else if (row.dataType == "number") {
                    selectAttr[index].attrLength = 10;
                    selectAttr[index].decimalLen = 2;
                }
            },
            handleCommand(param){
                switch (param.command) {
                    case 'del':
                        this.deleteAttrRows(param.row,param.index);
                        break;
                    case 'up':
                        this.attrUp(param.row, param.index);
                        break;
                    case 'down':
                        this.attrDown(param.row, param.index);
                        break;
                    default:
                        break;
                }
            },
            isDel(row, index){
                if (!this.formData.deployed) {
                    form.deleteAttr(row.id).then(resp => {
                        if (resp.data.state) {
                            this.$message({message: resp.data.message, type: "success"});
                        } else {
                            this.$message({message: resp.data.message, type: "error"});
                            return;
                        }
                    }).then(() =>{
                        this.formData.ents[this.entIndex].attributeList.splice(index,1);
                        this.formData.ents[this.entIndex].attributeList.forEach((item, attrIndex) => {
                            if (attrIndex == index) {
                                item.index = item.index - 1;
                                ++index;
                            }
                        })
                        form.saveEntData(this.formData).then(resp => {})
                    })
                } else {
                    form.removeAttr(row).then(resp => {
                        if (resp.data.state) {
                            this.$message({message: resp.data.message+"，建模数据已更新！", type: "success"});
                            this.$emit("getFormData");
                        } else {
                            this.$message({message: resp.data.message, type: "error"});
                            return;
                        }
                    })
                }
            },
            //删除字段
            deleteAttrRows(row, index) {
                if(!this.openGrandSonAttr) {
                    if (row.id == undefined) {
                        this.formData.ents[this.entIndex].attributeList.splice(index, 1);
                        this.formData.ents[this.entIndex].attributeList.forEach((item, attrIndex) => {
                            if (attrIndex == index) {
                                item.index = item.index - 1;
                                ++index;
                            }
                        })
                        return;
                    }
                    this.isDel(row,index);
                }else{
                    if(!row.id) {
                        this.attrTableData.splice(index, 1);
                        this.attrTableData.forEach((item, i) => {
                            if (i == index) {
                                item.index = item.index - 1;
                                ++index;
                            }
                        })
                        return;
                    }
                    this.isDel(row, index);
                }
            },
            attrUp(row,index){
                if(!this.openGrandSonAttr) {
                    this.formData.ents[this.entIndex].attributeList[index].index = this.formData.ents[this.entIndex].attributeList[index].index - 1;
                    this.formData.ents[this.entIndex].attributeList[index - 1].index = this.formData.ents[this.entIndex].attributeList[index - 1].index + 1;
                    this.formData.ents[this.entIndex].attributeList = utils.arrayMove(this.formData.ents[this.entIndex].attributeList, row, 'up');
                }else{
                    this.sonData[index].index = this.sonData[index].index - 1;
                    this.sonData[index - 1].index = this.sonData[index - 1].index + 1;
                    this.sonData = utils.arrayMove(this.sonData, row, 'up');
                }
            },
            attrDown(row,index){
                if(!this.openGrandSonAttr) {
                    this.formData.ents[this.entIndex].attributeList[index].index = this.formData.ents[this.entIndex].attributeList[index].index + 1;
                    this.formData.ents[this.entIndex].attributeList[index + 1].index = this.formData.ents[this.entIndex].attributeList[index + 1].index - 1;
                    this.formData.ents[this.entIndex].attributeList = utils.arrayMove(this.formData.ents[this.entIndex].attributeList, row, 'down');
                }else{
                    this.sonData[index].index = this.sonData[index].index + 1;
                    this.sonData[index + 1].index = this.sonData[index + 1].index - 1;
                    this.sonData = utils.arrayMove(this.sonData, row, 'down');
                }
            },
            recovery(row){
                form.recovery(row).then(resp=>{
                    if(resp.data.state){
                        this.$message({message: resp.data.message+"，建模数据已更新", type: "success"});
                        this.$emit("getFormData");
                    }
                })
            },
            maxHeight() {
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
            },
            refreshAttr(cb){
                let param = {
                    dsalias: this.formData.ents[this.entIndex].dsName,
                    isTable: "1",
                    objName: this.formData.ents[this.entIndex].tableName
                };
                if(this.openGrandSonAttr){
                    param.objName = this.formData.ents[this.entIndex].children[this.grandSonIndex].tableName;
                    param.dsalias = this.formData.ents[this.entIndex].children[this.grandSonIndex].dsName;
                }
                form.getHideAttr(param.objName).then(resp =>{
                    if(resp){
                        this.hideAttr = resp;
                    }
                }).then(() =>{
                    form.getTableList(param).then(resp => {
                        this.isExit = false;
                        this.refreshData = resp;
                    }).then(() =>{
                        for(let i=0; i<this.refreshData.data.length; i++){
                            if(this.refreshData.data[i].name == param.objName){
                                form.changeTableName(param).then(resp => {
                                    let json = JSON.parse(
                                        JSON.stringify(resp.data.table.columnList)
                                            .replace(/charLen/g, "attrLength")
                                            .replace(/fieldName/g, "name")
                                            .replace(/columnType/g, "dataType")
                                            .replace(/fdataType/g, "fcolumnType")
                                    );
                                    let _this = this;
                                    let data = {};
                                    if(this.hideAttr.length>0){
                                        let attr = "";
                                        this.hideAttr.forEach(v =>{
                                            attr +=v.fieldName + ","
                                        })
                                        json = json.filter(value => {
                                            return !attr.includes(value.fieldName);
                                        })
                                    }
                                    if(this.openGrandSonAttr){
                                        this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList = json;
                                        data = this.formData.ents[this.entIndex].children[this.grandSonIndex];
                                    }else{
                                        this.formData.ents[this.entIndex].attributeList = json;
                                        data = this.formData.ents[this.entIndex];
                                    }
                                    console.log(data.attributeList)
                                    data.attributeList = data.attributeList.filter(item =>{
                                        let val = item.name.toUpperCase();
                                        return !item.isPk && val != 'F_FORM_DATA_REV_' && val != 'REF_ID_' &&(!_this.formData.ents.fk || val != _this.formData.ents.fk.toUpperCase());
                                    });
                                    data.attributeList.forEach((item,index) =>{
                                        item.index = index + 1;
                                    });
                                    this.$message({message: "刷新成功", type: "success"});
                                    this.isExit = true;
                                });
                                break;
                            }
                        }
                        setTimeout(()=>{
                            if(!this.isExit){
                                if(this.openGrandSonAttr){
                                    this.formData.ents[this.entIndex].children[this.grandSonIndex].attributeList = [];
                                    this.getGrandSonEntsByIndex(this.grandSonIndex, this.entIndex);
                                }else{
                                    if(this.entIndex == 0){
                                        form.removeBusinessObj(this.formData.id).then(() =>{
                                            this.handleDialogClose();
                                        })
                                    }else{
                                        this.formData.ents[this.entIndex].attributeList = [];
                                        this.$emit("getEntsByIndex",this.entIndex);
                                    }
                                }
                            }
                        },500)
                        this.isExit = false;
                    })
                })
            }
        },
        mounted() {
            this.maxHeight();
            let _this = this;
            document.onkeydown = function(){
                if(_this.formData.ents && _this.formData.ents.length > 0) {
                    let key = window.event.keyCode;
                    if (key == 113) {//== 83 && event.ctrlKey
                        _this.addAttrRows();
                        setTimeout(function () {
                            _this.$refs.attrTableScrollbarHeight.bodyWrapper.scrollTop = _this.$refs.attrTableScrollbarHeight.bodyWrapper.scrollHeight;
                        }, 500);
                    }
                }
            };
        }
    }
</script>

<style lang="scss" scoped>
.table-item {
    margin: 9px auto;
}
</style>
