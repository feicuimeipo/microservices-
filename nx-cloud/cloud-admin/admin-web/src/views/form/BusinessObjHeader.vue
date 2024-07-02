<template>
    <div>
        <el-row>
            <div style="height: 38px">
                <div
                        style="width: 5px;height: 15px;background-color: #5B9DFF;border-radius: 2px;float: left;margin-top: 2px"
                ></div>
                <div
                        style="font-size: 16px;font-weight: bold;float: left;padding-left: 20px;color: rgba(28,28,28,1);font-family:Microsoft YaHei"
                >
                    业务对象
                </div>
            </div>
        </el-row>
        <el-row>
            <el-col :span="5">
                <ht-form-item label="描述" label-width="60px">
                    <ht-input
                            autocomplete="off"
                            v-model="formData.description"
                            :validate="{ required: true }"
                            :disabled="dataView.type ? true : false"
                    ></ht-input>
                </ht-form-item>
            </el-col>
            <el-col :span="5">
                <ht-form-item label="别名" label-width="80px">
                    <ht-input
                            v-model="formData.alias"
                            :disabled="dataView.type ? true : false"
                            @change="checkIsChinese()"
                            autocomplete="off"
                            @blur="entBlur"
                            name="alias"
                            v-pinyin="formData.description"
                            :validate="
              'required: true|regex:^[a-zA-Z][a-zA-Z0-9_]*$,只能输入字母、数字、下划线，且以字母开头'
            "
                    ></ht-input>
                </ht-form-item>
            </el-col>
            <el-col :span="7">
                <ht-form-item label="分类" label-width="80px">
                    <eip-sys-type-selector
                            placeholder="请选择分类"
                            :cat-id="'9'"
                            v-model="formData.categoryName"
                            :sys-type-id.sync="formData.categoryId"
                            :validate="{ required: true }"
                    />
                </ht-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="支持数据库">
                    <el-tooltip
                            class="item"
                            effect="dark"
                            content="勾选后，会在数据库生成对应的库表用于存储业务数据"
                            placement="top-end"
                    >
                        <el-switch
                                :disabled="dataView.type ? true : false"
                                v-model="formData.supportDb"
                        ></el-switch>
                    </el-tooltip>
                </el-form-item>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    const eipSysTypeSelector = () =>
        import("@/components/selector/EipSysTypeSelector.vue");
    export default {
        components: {
            eipSysTypeSelector
        },
        name: "BusinessObjHeader",
        props: ["dataView", "formData"],
        data() {
            return {
            };
        },
        watch:{
            formData: function (v) {
                this.formDataHeader = v;
            }
        },
        methods: {
            checkIsChinese() {
                this.$emit("checkIsChinese");
            },
            entBlur() {
                this.$emit("entBlur");
            },
        }
    };
</script>

<style scoped></style>
