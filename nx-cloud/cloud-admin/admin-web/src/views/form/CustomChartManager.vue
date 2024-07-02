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
          <el-button size="small" @click="showDialog()" icon="el-icon-plus">添加</el-button>
          <ht-delete-button
              :url="deleteUrl"
              :htTable="$refs.table">删除
          </ht-delete-button>
        </el-button-group>
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column
          prop="name"
          label="名称"
          :sortable="true"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <el-link type="primary"  @click="showDialog(scope.row.id)" title="查看详情" >{{scope.row.name}}</el-link>
          </template>
        </ht-table-column>
        <ht-table-column
          prop="alias"
          label="别名"
          :sortable="true"
          :show-overflow-tooltip="true"
        />
        <ht-table-column
          prop="style"
          label="图表类型"
          width="120"
          :filters="[
            { text: '折线图/柱状图', value: '1' },
            { text: '饼图', value: '3' },
            { text: '雷达图',value: '4' },
            { text: '漏斗图',value: '5' },
            { text: '散点图',value: '6' },
            { text: '热力图',value: '7' }
          ]"
        >
          <template v-slot="{ row }">
            <el-tag v-if="row.style === 1 || row.style === 2">折线图/柱状图</el-tag>
            <el-tag type="warning" v-if="row.style === 3">饼图</el-tag>
            <el-tag type="danger" v-if="row.style === 4">雷达图</el-tag>
            <el-tag type="info" v-if="row.style === 5">漏斗图</el-tag>
            <el-tag type="success" v-if="row.style === 6">散点图</el-tag>
            <el-tag type="warning" v-if="row.style === 7">热力图</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column
          prop="isTable"
          label="查询方式"
          width="100"
          :filters="[
            { text: '表', value: '1' },
            { text: '自定义SQL', value: '2' }
          ]"
        >
          <template v-slot="{ row }">
            <el-tag type="info" v-if="row.isTable === 1">表</el-tag>
            <el-tag type="danger" v-if="row.isTable === 2">自定义SQL</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column
          prop="dsalias"
          label="数据源别名"
          width="110"
          :sortable="true"
          :show-overflow-tooltip="true"
        />
        <ht-table-column label="操作" width="110">
          <template slot-scope="scope">
            <el-button size="mini" icon="el-icon-view" @click="preview(scope.row.id)">预览</el-button>
          </template>
        </ht-table-column>
      </template>
    </ht-table>
    <el-dialog
      width="40%"
      title="编辑图表"
      :visible="dialogVisible"
      :before-close="beforeClose">
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="基础设置" name="basicSetting">
          <el-form v-model="prop">
            <ht-form-item label="名称">
              <ht-input
                v-model="prop.name"
                placeholder="请输入图表名称"
                :validate="{ required: true }"
              />
            </ht-form-item>
            <ht-form-item label="别名">
              <ht-input
                v-model="prop.alias"
                placeholder="请输入图表别名"
                name="chartName"
                v-pinyin="prop.name"
                @change="changeName()"
                :validate="{ required: true,alpha_num:true }"
                :disabled="prop.id ? true : false"
              />
            </ht-form-item>
            <ht-form-item label="图表类型">
              <ht-select
                  v-model="prop.style"
                  :options="styles"
                  :validate="{required:true}"
                  :props="{key:'value',value:'label'}"/>
            </ht-form-item>
            <ht-form-item label="图表尺寸">
              <ht-input v-model="prop.width" placeholder="请输入宽度" />px
              <ht-input v-model="prop.height" placeholder="请输入高度" />px
            </ht-form-item>
            <template v-if="!prop.id">
              <ht-form-item label="数据源">
                <ht-select
                    v-model="prop.dsalias"
                    :options="dataSources"
                    :props="{key:'alias',value:'name'}"/>
              </ht-form-item>
              <ht-form-item label="查询方式">
                <el-select v-model="prop.isTable">
                  <el-option key="table" label="表" :value="1" />
                  <el-option key="diySql" label="自定义SQL" :value="2" />
                </el-select>
              </ht-form-item>
              <template id="table" v-if="prop.isTable===1">
                <ht-form-item label="选择表">
                  <el-select v-model="prop.objName">
                    <el-option
                        v-for="item in tablesOrViews"
                        :key="item.name"
                        :label="item.name+'('+item.comment+')'"
                        :value="item.name"/>
                  </el-select>
                  <ht-input v-model="state" placeholder="请输入表名" />
                  <el-button type="primary" @click="getByDsObjectName()">查询</el-button
                  >
                </ht-form-item >
              </template>
            </template>
            <div class="dbProp" style="width: 100%" v-if="prop.id">数据源：{{prop.dsalias}},表名：{{prop.objName}}</div>
            <ht-form-item label="自定义SQL" v-if="prop.isTable===2">
              <ht-input
                  type="textarea"
                  v-model="prop.diySql"
                  :autosize="{ minRows: 5, maxRows: 5}"
              />
              <el-button type="primary" @click="checkSql">验证SQL</el-button>
            </ht-form-item>
            <ht-form-item label="最大数据量">
              <el-tooltip class="item" effect="dark" content="输入0为无限制" placement="top-start">
                <i class="el-icon-question"/>
              </el-tooltip>
              <ht-input v-model="config.maxLength"/>
            </ht-form-item>
            <ht-form-item label="设置列">
              <el-button type="primary" @click="columnSetting()"
              >设置列</el-button
              >
            </ht-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="标题设置" name="titleSetting">
          <el-form v-model="config.title">
            <ht-form-item label="标题显示">
              <el-switch
                v-model="config.title.show"
                active-color="#13ce66"
                inactive-color="#ff4949"
              >
              </el-switch>
            </ht-form-item>
            <template v-if="config.title.show">
              <ht-form-item label="水平对齐">
                <el-radio-group v-model="config.title.left">
                  <el-radio-button label="auto">默认</el-radio-button>
                  <el-radio-button label="left">左</el-radio-button>
                  <el-radio-button label="center">中</el-radio-button>
                  <el-radio-button label="right">右</el-radio-button>
                </el-radio-group>
              </ht-form-item>
              <ht-form-item label="垂直对齐">
                <el-radio-group v-model="config.title.top">
                  <el-radio-button label="auto">默认</el-radio-button>
                  <el-radio-button label="top">上</el-radio-button>
                  <el-radio-button label="middle">中</el-radio-button>
                  <el-radio-button label="bottom">下</el-radio-button>
                </el-radio-group>
              </ht-form-item>
              <ht-form-item label="字体">
                <el-select v-model="config.title.textStyle.fontFamily">
                  <el-option
                      v-for="fontFamily in fontFamilies"
                      :key="fontFamily"
                      :label="fontFamily"
                      :value="fontFamily"
                  >
                  </el-option>
                </el-select>
              </ht-form-item>
              <ht-form-item label="字体风格">
                <el-select v-model="config.title.textStyle.fontStyle">
                  <el-option
                      v-for="fontStyle in fontStyles"
                      :key="fontStyle"
                      :label="fontStyle"
                      :value="fontStyle"
                  >
                  </el-option>
                </el-select>
              </ht-form-item>
              <ht-form-item label="字体大小">
                <ht-input
                    v-model="config.title.textStyle.fontSize"
                    placeholder="请输入字体大小"
                >
                </ht-input>
              </ht-form-item>
              <ht-form-item label="副标题">
                <ht-input
                    v-model="config.title.subtext"
                    placeholder="请输入副标题"
                >
                </ht-input>
              </ht-form-item>
            </template>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="图例设置" name="legendSetting">
          <el-form v-model="config.legend">
            <ht-form-item label="图例显示">
              <el-switch
                v-model="config.legend.show"
                active-color="#13ce66"
                inactive-color="#ff4949"
              >
              </el-switch>
            </ht-form-item>
            <template v-if="config.legend.show">
              <ht-form-item label="图例类型">
                <el-radio-group v-model="config.legend.type">
                  <el-radio-button label="plain">普通图例</el-radio-button>
                  <el-radio-button label="scroll">可翻页图例</el-radio-button>
                </el-radio-group>
              </ht-form-item>
              <ht-form-item label="水平对齐">
                <el-radio-group v-model="config.legend.left">
                  <el-radio-button label="auto">默认</el-radio-button>
                  <el-radio-button label="left">左</el-radio-button>
                  <el-radio-button label="center">中</el-radio-button>
                  <el-radio-button label="right">右</el-radio-button>
                </el-radio-group>
              </ht-form-item>
              <ht-form-item label="垂直对齐">
                <el-radio-group v-model="config.legend.top">
                  <el-radio-button label="auto">默认</el-radio-button>
                  <el-radio-button label="top">上</el-radio-button>
                  <el-radio-button label="middle">中</el-radio-button>
                  <el-radio-button label="bottom">下</el-radio-button>
                </el-radio-group>
              </ht-form-item>
              <ht-form-item label="布局朝向">
                <el-radio-group v-model="config.legend.orient">
                  <el-radio-button label="horizontal">水平布局</el-radio-button>
                  <el-radio-button label="vertical">垂直布局</el-radio-button>
                </el-radio-group>
              </ht-form-item>
            </template>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="数据展示" name="dataSetting">
          <el-form>
            <template v-if="prop.style===1">
              <ht-form-item label="开启双Y轴">
                <el-switch
                    v-model="line.doubleYAxis"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                </el-switch>
              </ht-form-item>
              <ht-form-item label="是否堆叠">
                <el-switch
                    v-model="line.stack"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                </el-switch>
              </ht-form-item>
              <ht-form-item label="XY轴转换">
                <el-switch
                    v-model="line.showType"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                </el-switch>
              </ht-form-item>
              <ht-form-item label="折线平滑">
                <el-switch
                    v-model="line.smooth"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                </el-switch>
              </ht-form-item>
              <ht-form-item label="缩放区域">
                <el-switch
                    v-model="line.dataZoom"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                </el-switch>
              </ht-form-item>
            </template>
            <template v-else-if="prop.style===3">
              <ht-form-item label="数据调用方式">
                <ht-radio v-model="pie.showType" :options="[{ key: true, value:'列数据展示'}, { key: false, value:'行数据展示'}]"/>
              </ht-form-item>
              <ht-form-item label="文本标签位置">
                <ht-radio v-model="pie.label.position" :options="[{ key: 'outside', value:'外部'}, { key: 'inside', value:'内部'}]"/>
              </ht-form-item>
              <ht-form-item label="开启选中模式">
                <ht-radio v-model="pie.selectedMode" :options="[{ key: false, value:'不选'}, { key: 'single', value:'单选'}, { key: 'multiple', value:'多选'}]"/>
              </ht-form-item>
              <ht-form-item label="南丁格尔玫瑰">
                <ht-radio v-model="pie.roseType" :options="[{ key: false, value:'不显示'}, { key: 'radius', value:'半径显示'}, { key: 'area', value:'同角显示'}]"/>
              </ht-form-item>
              <ht-form-item label="半径">
                <ht-input v-model="pie.radius[0]"/>%~
                <ht-input v-model="pie.radius[1]"/>%
              </ht-form-item>
              <ht-form-item label="中心位置">
                水平位置：<ht-input v-model="pie.center[0]"/>%<br>
                垂直位置：<ht-input v-model="pie.center[1]"/>%
              </ht-form-item>
            </template>
            <template v-else-if="prop.style===4">
              <ht-form-item label="数据调用方式">
                <ht-radio v-model="radar.showType" :options="[{ key: true, value:'列数据展示'}, { key: false, value:'行数据展示'}]"/>
              </ht-form-item>
              <ht-form-item label="区域阴影">
                <el-switch
                    v-model="radar.areaStyle"
                    active-color="#13ce66"
                    inactive-color="#ff4949">
                </el-switch>
              </ht-form-item>
              <ht-form-item label="半径">
                <ht-input v-model="radar.radius"/>
              </ht-form-item>
              <ht-form-item label="中心位置">
                水平位置：<ht-input v-model="radar.center[0]"/>%<br>
                垂直位置：<ht-input v-model="radar.center[1]"/>%
              </ht-form-item>
            </template>
            <template v-else-if="prop.style===5">
              <ht-form-item label="数据调用方式">
                <ht-radio v-model="funnel.showType" :options="[{ key: true, value:'列数据展示'}, { key: false, value:'行数据展示'}]"/>
              </ht-form-item>
              <ht-form-item label="文本标签位置">
                <ht-radio v-model="funnel.label.position" :options="[{ key: 'outside', value:'外部'}, { key: 'inside', value:'内部'}]"/>
              </ht-form-item>
              <ht-form-item label="图形对齐">
                <ht-radio v-model="funnel.funnelAlign" :options="[{ key: 'left', value:'靠左'}, { key: 'center', value:'居中'},{key:'right',value:'靠右'}]"/>
              </ht-form-item>
              <ht-form-item label="图形排序">
                <ht-radio v-model="funnel.sort" :options="[{ key: 'ascending', value:'金字塔'}, { key: 'descending', value:'倒金字塔'},{key:'none',value:'不排序'}]"/>
              </ht-form-item>
            </template>
            <template v-else-if="prop.style===6">
              <ht-form-item label="数据调用方式">
                <ht-radio v-model="scatter.showType" :options="[{ key: true, value:'列数据展示'}, { key: false, value:'行数据展示'}]"/>
              </ht-form-item>
              <ht-form-item label="坐标轴类型">
                <ht-radio v-model="scatter.xAxisType" :options="[{key: 'category', value:'类目轴'},{key: 'value',value:'数值轴'}]"/>
                <el-tooltip class="item" effect="dark" content="当X轴的值不是Number时，请选择类目轴，否则会导致显示异常。">
                  <i class="el-icon-question"/>
                </el-tooltip>
              </ht-form-item>
            </template>
            <template v-else>
              <div>暂无配置</div>
            </template>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save()">{{
          $t("eip.common.save")
          }}</el-button>
        <el-button @click="close('dialogVisible')">{{
          $t("eip.common.cancel")
        }}</el-button>
      </div>
      <eip-chart-setting
          ref="columnSetting"
          :chartStyle="prop.style"
          :param="param"
          :yAxis="prop.displayfield"
          :xAxis="prop.xaxisField"
          :condition="prop.conditionfield"
          :sort="prop.sortfield"
          @save="saveColumnSetting"/>
    </el-dialog>
    <el-dialog
      title="图表预览"
      width="55%"
      :visible="chartDialogVisible"
      :before-close="beforeChartClose">
      <eip-chart :id="id" v-if="chartDialogVisible"/>
    </el-dialog>
  </div>
</template>

<script>
import form from "@/api/form.js";
const eipChart = () => import("@/components/form/chart/EipChart.vue");
const eipChartSetting = () => import("@/components/form/chart/EipChartSetting.vue");

export default {
  name: "customChartManager",
  components: {
    eipChart,
    eipChartSetting
  },
  data() {
    return {
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      activeTab: "basicSetting",
      dialogVisible: false,
      chartDialogVisible: false,
      //基本设置的属性
      initProp:{
        width: 800,
        height: 400,
        system: 1,
        isTable: 1,
        dsalias: "LOCAL", // 本地数据源
        displayfield: [],
        xaxisField: [],
        sortfield: [],
        conditionfield: [],
        alias:''
      },
      prop: {},
      //标题设置，图例设置与数据展示
      initConfig:{
        maxLength:0,//最大数据量
        yMin: "1",
        xShowAll: "1",
        theme: "default",//主题
        //title,legend参考echarts的属性
        title: {
          show: true,
          textStyle: {
            fontStyle: "normal",
            fontFamily: "sans-serif",
            fontSize: 18
          },
          subtext: "",
          left: "auto",
          top: "auto"
        },
        legend: {
          show: true,
          type: "plain",
          left: "auto",
          top: "auto",
          orient: "horizontal"
        },
        series: {}
      },
      config: {},
      line:{
        doubleYAxis:true,
        stack:false,
        showType:true,
        smooth:true,
        dataZoom:false
      },
      pie:{
        showType:true,
        roseType:false,
        selectedMode:false,
        radius:["0","75"],
        center:["50","50"],
        label:{
          position:"outside"
        }
      },
      radar:{
        showType:true,
        radius:"75",
        center:["50","50"],
        areaStyle:true
      },
      funnel:{
        showType:true,
        funnelAlign:"center",
        sort:"none",
        label:{
          position:"inside"
        }
      },
      scatter:{
        showType:true,
        xAxisType:'category'
      },
      fontFamilies: ["sans-serif", "monospace", "Arial", "Courier New", "Microsoft YaHei", "serif"],
      fontStyles: ["normal", "italic", "oblique"],
      styles: [
        { label: "折线图/柱状图", value: 1 },
        { label: "饼图", value: 3 },
        { label: "雷达图",value: 4},
        { label: "漏斗图",value: 5},
        { label: "散点图",value: 6},
        { label: "热力图",value: 7}
      ],
      dataSources: [],
      tablesOrViews: [],
      isCheckSql: "notCheck",
      isSubmit: false,
      id: "",
      param:{},
      state:"",
      deleteUrl:window.context.form+"/form/customChart/v1/removes",
      chartType:{"1":"line","3":"pie","4":"radar","5":"funnel","6":"scatter","7":"heatMap"}
    }
  },
  created() {
    this.config = {...this.initConfig};
  },
  mounted() {
    this.getDataSource();
  },
  watch:{
    //修改过SQL语句把状态改为未验证
    'prop.diySql':{
      handler(newValue,oldValue){
        if(newValue!==oldValue){
          this.isCheckSql = "notCheck";
        }
      }
    }
  },
  methods: {
    loadData(param, cb) {
      param.sorter=[{ direction: "DESC", property: "updateTime" }]
      form.getCustomChartList(param).then(response => {
          this.data = response.rows;
          this.pageResult = {
            page: response.page,
            pageSize: response.pageSize,
            total: response.total
          };
        })
        .finally(() => {
          cb();
        });
    },
    showDialog(id) {
      this.dialogVisible = true;
      if (id != null) {
        form.getCustomChartById(id).then(data=>{
          this.prop = data;
          this.prop.displayfield = JSON.parse(data.displayfield);
          this.prop.xaxisField = JSON.parse(data.xaxisField);
          this.prop.sortfield = JSON.parse(data.sortfield);
          this.prop.conditionfield = JSON.parse(data.conditionfield);
          this.config = JSON.parse(data.conf);
          this[this.chartType[this.prop.style+""]] = this.config.series;
          this.isCheckSql = "suc";
        });
      }else{
        this.prop = {...this.initProp};
        this.config = {...this.initConfig};
      }
    },
    beforeClose() {
      this.dialogVisible = false;
    },
    beforeChartClose() {
      this.chartDialogVisible = false;
    },
    //获取数据源
    getDataSource() {
      form.getDataSource().then(response => {
        this.dataSources = response.data;
      });
    },
    //根据关键字查询数据源中的表
    getByDsObjectName() {
      if (this.prop.dsalias === null) {
        this.$message({ message: "请选择数据源", type: "warning" });
        return;
      }
      let data = {
        dsalias: this.prop.dsalias,
        isTable: this.prop.isTable,
        objName: this.state
      };
      form.getTableList(data).then(response => {
        if (!response.data || response.data.length === 0) {
          this.$message({
            message: "该数据源中未查询到表或视图",
            type: "warning"
          });
          return;
        }
        this.tablesOrViews = response.data;
      });

    },
    columnSetting() {
      if (!this.checkSelectWay())
        return;
      if (this.prop.dsType === "dataSource" && this.prop.objName == null) {
        this.$message({ type: "warning", message: "请选择目标表或视图" });
      }
      this.param = {
        dsalias:this.prop.dsalias,
        isTable:this.prop.isTable,
        objName:this.prop.objName,
        diySql:this.prop.diySql
      };
      this.$refs.columnSetting.handleOpen();
    },
    checkSelectWay() {
      let errMsgArr = [];
      if (!this.prop.style || this.prop.style === "") {
        errMsgArr.push("请选择图表类型");
      }
      if (this.prop.isTable === 1 && this.prop.objName == null) {
        errMsgArr.push("请选择目标表");
      }
      if (this.prop.isTable === 2) {
        if (this.prop.diySql == null) {
          errMsgArr.push("请填写SQL语句并验证");
        } else if (this.isCheckSql === "notCheck") {
          errMsgArr.push("请验证SQL语句");
        } else if (this.isCheckSql === "fail") {
          errMsgArr.push("SQL语句验证不通过");
        }
      }
      if (errMsgArr.length > 0) {
        this.$message({ message: errMsgArr.join(","), type: "warning" });
        return false;
      } else {
        return true;
      }
    },
    close(dialogVisible) {
      this[dialogVisible] = false;
    },
    changeName(){
      document.getElementsByName("chartName")[0].style.border="";
    },
    save() {
      if (!this.checkSelectWay())
        return;
      this.prop.conf = JSON.stringify(this.config);
      let param = {...this.prop};
      if (!param.xaxisField || param.xaxisField.length < 1 || !param.displayfield || param.displayfield.length < 1) {
        this.$message({ message: "请设置列", type: "warning" });
        return;
      }
      param.displayfield = JSON.stringify(param.displayfield);
      param.xaxisField = JSON.stringify(param.xaxisField);
      param.conditionfield = JSON.stringify(param.conditionfield);
      param.sortfield = JSON.stringify(param.sortfield);
      this.config.series = this[this.chartType[param.style+""]];
      param.conf=JSON.stringify(this.config);
      delete param.resultfield;
      form.saveCustomChart(param).then(response => {
          if (response.state) {
            this.$message({ message: "保存成功", type: "success" });
            this.dialogVisible = false;
            this.$refs.table.load();
          }else{
            document.getElementsByName("chartName")[0].focus();
            document.getElementsByName("chartName")[0].style.border="1px solid red";
          }
        })
        .catch(() => {
          this.$message.error("保存失败");
        });
    },
    preview(id) {
      this.id = id;
      this.chartDialogVisible = true;
    },
    //验证sql
    checkSql() {
      let data = {sql:this.prop.diySql, dsName:this.prop.dsalias};
      if(!this.prop.diySql){
        this.$message('请填写自定义sql!');
        return;
      }
      form.checkSql(data).then(response=>{
        if (response.state){
          this.isCheckSql='suc';
          this.$message({type: "success", message: response.message});
        } else {
          this.isCheckSql='fail';
        }
      },error => {
        this.$message.error("验证失败");
      });
    },
    //设置列弹框保存回调，保存设置列数据
    saveColumnSetting(data){
      this.prop.displayfield = data.yAxisField;
      this.prop.xaxisField = data.xAxisField;
      this.prop.conditionfield = data.conditionField;
      this.prop.sortfield = data.sortField;
    }
  }
};
</script>

<style scoped>
.theme-plan-group {
  display: flex;
  flex-wrap: wrap;
  justfy-content: space-between;
  width: 145px;
  height: 20px;
  overflow: hidden;
  border: 1px solid #eee;
  padding: 5px;
  border-radius: 4px;
  margin: 10px 0;
}
.theme-plan-color {
  width: 20px;
  height: 20px;
  margin-bottom: 10px;
  margin-left: 2px;
  margin-right: 2px;
  display: inline-block;
  border-radius: 3px;
}
  .dbProp{
    width: 100%;
    margin: 0 0 10px 15px;
    font-size: 14px;
  }
</style>
