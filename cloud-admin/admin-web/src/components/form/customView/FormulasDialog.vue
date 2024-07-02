<template>
  <el-dialog
    title="公式编辑"
    :close-on-click-modal="false"
    :visible.sync="formulasDialogVisible"
    width="80%"
    :append-to-body="true"
  >
    <div v-if="formulasDialogVisible">
      <el-row>
        <el-col :span="3" v-if="nodeType!=='sun'">
          <el-checkbox v-model="checked" style="margin-left: 10px">子表单列运算</el-checkbox>
        </el-col>
        <el-col :span="21" style="font-size:14px;margin-bottom: 10px;">
          当前选中字段:
          <el-tag type="danger">{{field.desc}}</el-tag>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <!-- <el-input
          type="textarea"
          :rows="2"
          placeholder="请输入内容"
          v-model="formulasDiyJs"
          id="singleText"
          ></el-input>-->
          <codemirror
            ref="mycode2"
            v-model="formulasDiyJs"
            :options="cmOptions2"
            class="code"
            id="singleText"
          ></codemirror>
        </el-col>
      </el-row>
      <el-row :gutter="20" style="margin-top: 15px;">
        <el-col :span="8">
          变量
          <el-card shadow="never">
            <div style="height:300px;overflow-y:auto">
              <el-tree
                :data="[boDefData]"
                :props="defaultProps"
                :default-expand-all="expandAll"
                @node-click="handleNodeClick"
                ref="varTree">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                  <i
                    class="icon-number bo-tree__icon"
                    title="数字类型的字段"
                    v-if="data.columnType=='number'"
                  />
                  <i
                    class="icon-text bo-tree__icon"
                    title="字符串类型的字段"
                    v-if="data.columnType=='varchar'"
                  />
                  <i
                    class="icon-date bo-tree__icon"
                    title="日期类型的字段"
                    v-if="data.columnType=='date'"
                  />
                  <span class="bo-tree__label" :title="node.label">{{ node.label }}</span>
                </span>
              </el-tree>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          函数
          <el-card shadow="never">
            <div style="height:300px;overflow-y:auto">
              <el-tree
                :data="functionData"
                :default-expand-all="false"
                @node-click="functionNodeClick"
                :filter-node-method="filterNode"
                ref="funcTree"
              >
                <span
                  class="custom-tree-node"
                  slot-scope="{ node, data }"
                  style="width:100%;"
                  @mouseenter="mouseenter(data)"
                  @mouseleave="mouseleave(data)"
                >
                  <span>{{ node.label }}</span>
                </span>
              </el-tree>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          帮助
          <el-card shadow="never">
            <div style="height:300px;overflow-y:auto;">
              <div style="white-space:pre;font-size:12px;line-height: 20px;">{{helpDesc}}</div>
              <span style="font-size:16px;color: red;">注意事项:</span>
              <div style="font-size:12px;line-height: 20px;">
                1.此功能使用VUE自定义指令为底层实现,写法遵循VUE指令写法,指令会实时监听文本表达式的值改变,一但改变会把表达式式最终值赋值给对应字段(未监听页面加载时的值改变)
                <br />2.不可字段之间互相引用,例:字段a的公式中包含了字段b,字段b的公式中就不可再包含字段a了,不然会发生页面死循环
                <br />3.配置完公式后,请务必在浏览器开发者模式进行多次调试,以确保公式语法正确!
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
     </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="handleClose('close')">取 消</el-button>
        <el-button type="primary" @click="handleClose('true')">确 定</el-button>
      </span>

  </el-dialog>
</template>
<script>
import { codemirror } from "vue-codemirror";
require("codemirror/mode/python/python.js");
require("codemirror/addon/fold/foldcode.js");
require("codemirror/addon/fold/foldgutter.js");
require("codemirror/addon/fold/brace-fold.js");
require("codemirror/addon/fold/xml-fold.js");
require("codemirror/addon/fold/indent-fold.js");
require("codemirror/addon/fold/markdown-fold.js");
require("codemirror/addon/fold/comment-fold.js");

export default {
  name: "formulas-dialog",
  components: {
    codemirror
  },
  props: ["boDefData", "field"],
  mounted() {},
  watch: {
    field(val) {
      this.formulasDiyJs = val.options.formulasDiyJs;
    },
    checked(val){
      this.$refs.funcTree.filter(val);
    }
  },
  methods: {
    mouseenter(data) {
      if (data.helpDesc) {
        this.helpDesc = data.helpDesc;
      }
    },
    mouseleave(data) {},
    handleClose(type) {
      if (type !== "close") {
        this.field.options.formulasDiyJs = this.formulasDiyJs;
      }
      this.formulasDialogVisible = false;
    },
    functionNodeClick(data, treeNode) {
      if (this.checked){
        if (data.subValue){
          this.insert(data.subValue, 1);
          return;
        }
      }
      this.insert(data.value, 1);
    },
    handleNodeClick(data, treeNode) {
      debugger
      //不可自己计算自己
      if (data.path + "." + data.name === this.field.fieldPath) {
        this.$message("无法运算字段本身");
        return;
      }
      if (!data.nodeType || data.nodeType!=='field'){
        this.$message("选项不是字段");
        return;
      }
      if (!(data.path && data.path.split('.').length<2) && !this.checked && this.nodeType==='main'){
        this.$message("不可选子表、孙表字段");
        return;
      }
      if (!(data.path && data.path.split('.').length!==2) && this.nodeType==='sun'){
        this.$message("不可选子表字段");
        return;
      }
      let isMain = treeNode.parent.data.nodeType == "main";
      let path = "data." + data.path + "." + data.name;
      let dataPaths = data.path.split(".");
      if (!isMain) {
        if (this.checked){
          if (dataPaths.length===3){
            path = "{ data: data."+dataPaths[0]+"."+dataPaths[1]+"[index]."+dataPaths[2]+",path: '"+data.name+"'}";
          }else{
            path = "{ data: data."+ data.path + ", path: '" + data.name +"' }";
          }
        }else{
          path = "item." + data.name;
        }
      }
      this.insert(path);
    },
    //统计函数相关配置
    async insert(myValue, lineIndex) {
      const myField = this.$refs.mycode2;

      myField.codemirror.replaceSelection(myValue);
      let line = myField.codemirror.doc.getCursor();
      if (lineIndex) {
        line.ch = line.ch - lineIndex;
        myField.codemirror.doc.setCursor(line);
      }
      myField.codemirror.display.input.focus();
    },
    handleOpen() {
      this.formulasDialogVisible = true;
      if (!this.field.parentNodeType){
        this.nodeType = "main";
      }else {
        this.nodeType = this.field.parentNodeType;
      }
      this.formulasDiyJs = this.field.options.formulasDiyJs;
      this.checked = false;
    },
    filterNode(value, data){
      if (this.checked){
        return data.subValue;
      }
      return true;
    }
  },
  data() {
    return {
      helpDesc: "",
      expandAll: true,
      formulasDialogVisible: false,
      formulasDiyJs: this.field.options.formulasDiyJs,
      cmOptions2: {
        showCursorWhenSelecting: true,
        value: "",
        mode: "javascript",
        readOnly: false,
        smartIndent: true,
        tabSize: 2,
        theme: "base16-light",
        lineNumbers: true,
        line: true,
        autofocus: true,
        inputStyle: "textarea"
      },
      defaultProps: {
        children: "children",
        label: "desc"
      },
      functionData: [
        {
          label: "数学函数",
          children: [
            {
              value: "$Formulas.ABS()",
              label: "ABS",
              helpDesc: "ABS函数可以获取一个数的绝对值\n用法：ABS(数字)\n示例：ABS(-8)可以返回8，也就是-8的绝对值"
            },
            {
              value: "$Formulas.AVERAGE()",
              label: "AVERAGE",
              helpDesc: "AVERAGE函数可以获取一组数值的算术平均值\n用法：AVERAGE(数字1,数字2,...)\n示例：AVERAGE({语文成绩},{数学成绩}, {英语成绩})返回三\n门课程的平均分",
              subValue: "$Formulas.AVERAGESUB()"
            },
            {
              value: "$Formulas.CEILING()",
              label: "CEILING",
              helpDesc: "CEILING函数可以将数字增大到最接近原值的指定因数的倍数\n用法：CEILING(数字,因数)\n示例：CEILING(7,6)返回12，因为12比7大的同时，也是6的\n倍数中最接近7的数字"
            },
            {
              value: "$Formulas.COUNT()",
              label: "COUNT",
              helpDesc: "COUNT函数可以获取参数的数量\n用法：COUNT(值,值,...)\n示例：COUNT(小明,小王,小张,小李)返回4，也就是人员的数\n量",
              subValue: "$Formulas.COUNTSUB()"
            },
            {
              value: "$Formulas.COUNTIF()",
              label: "COUNTIF",
              helpDesc: "COUNTIF函数可以获取数组中满足条件的参数个数\n用法：COUNTIF(数组,'条件')\n示例：COUNTIF(子表单.性别, '女')，可得到子表单中性别填\n的是'女'的数据条数；COUNTIF([1,2,3,4],'>2')，可得到1,2,3,\n4中大于2的数字数量，结果为2。",
              subValue: "$Formulas.COUNTIFSUB()"
            },
            {
              value: "$Formulas.FIXED()",
              label: "FIXED",
              helpDesc: "FIXED函数可将数字舍入到指定的小数位数并输出为文本\n用法：FIXED(数字,小数位数)\n示例：FIXED(3.1415,2)返回'3.14'"
            },
            {
              value: "$Formulas.FLOOR()",
              label: "FLOOR",
              helpDesc: "FLOOR函数可将数字减小到最接近原值的指定因数的倍数\n用法：FLOOR(数字,因数)\n示例：FLOOR(7,6)返回6，因为6比7小的同时，也是6的倍数\n中最接近7的数字"
            },
            {
              value: "$Formulas.INT()",
              label: "INT",
              helpDesc: "INT函数可以获取一个数的整数部分\n用法：INT(数字)\n示例：INT(3.1415)返回3，也就是3.1415的整数部分"
            },
            {
              value: "$Formulas.LARGE()",
              label: "LARGE",
              helpDesc: "LARGE函数可以获取数据集中第k个最大值\n用法：LARGE(数组,k)\n示例：LARGE({学生成绩.数学成绩},1)返回子表单'学生成绩'\n中排名第1的'数学成绩'",
              subValue: "$Formulas.LARGESUB()"
            },
            {
              value: "$Formulas.LOG()",
              label: "LOG",
              helpDesc: "LOG函数可以根据指定底数返回数字的对数\n用法：LOG(数字,底数)\n示例：LOG(100,10)返回2，也就是以10为底数100的对数"
            },
            {
              value: "$Formulas.MAX()",
              label: "MAX",
              helpDesc: "MAX函数可以获取一组数值的最大值\n用法：MAX(数字1,数字2,...)\n示例：MAX({语文成绩},{数学成绩},{英语成绩})返回三门课程\n中的最高分",
              subValue: "$Formulas.MAXSUB()"
            },
            {
              value: "$Formulas.MIN()",
              label: "MIN",
              helpDesc: "MIN函数可以获取一组数值的最小值\n用法：MIN(数字1,数字2,...)\n示例：MIN({语文成绩},{数学成绩},{英语成绩})返回三门课程\n中的最低分",
              subValue: "$Formulas.MINSUB()"
            },
            {
              value: "$Formulas.MOD()",
              label: "MOD",
              helpDesc: "MOD函数可以获取两数相除的余数\n用法：MOD(被除数,除数)\n示例：MOD(4,3)返回1，也就是4/3的余数"
            },
            {
              value: "$Formulas.POWER()",
              label: "POWER",
              helpDesc: "POWER函数可以获取数字乘幂的结果\n用法：POWER(数字,指数)\n示例：POWER(3，2)返回9，也就是3的2次方"
            },
            {
              value: "$Formulas.PRODUCT()",
              label: "PRODUCT",
              helpDesc: "PRODUCT函数可以获取一组数值的乘积\n用法：PRODUCT(数字1,数字2,...)\n示例：PRODUCT({单价}, {数量})获取总价，也就是单价和\n数量的乘积",
              subValue: "$Formulas.PRODUCTSUB()"
            },
            {
              value: "$Formulas.RAND()",
              label: "RAND",
              helpDesc: "RAND函数可返回大于等于0且小于1的均匀分布随机实数\n用法：RAND()\n示例：RAND()返回0.424656"
            },
            {
              value: "$Formulas.ROUND()",
              label: "ROUND",
              helpDesc: "ROUND函数可以将数字四舍五入到指定的位数\n用法：ROUND(数字,数字位数)\n示例：ROUND(3.1485,2)返回3.15"
            },
            {
              value: "$Formulas.SMALL()",
              label: "SMALL",
              helpDesc: "SMALL函数可以返回数据集中第k个最小值\n用法：SMALL(数组,k)\n示例：SMALL({学生成绩.数学成绩}, 1)返回子表单'学生成\n绩'中排名倒数第一的'数学成绩'",
              subValue: "$Formulas.SMALLSUB()"
            },
            {
              value: "$Formulas.SQRT()",
              label: "SQRT",
              helpDesc: "SQRT函数可以获取一个数字的正平方根\n用法：SQRT(数字)\n示例：SQRT(9)返回3，也就是9的正平方根"
            },
            {
              value: "$Formulas.SUM()",
              label: "SUM",
              helpDesc: "SUM函数可以获取一组数值的总和\n用法：SUM(数字1,数字2,...)\n示例：SUM({语文成绩},{数学成绩}, {英语成绩})返回三门课\n程的总分",
              subValue: "$Formulas.SUMSUB()"
            },
            {
              value: "$Formulas.SUMPRODUCT()",
              label: "SUMPRODUCT",
              helpDesc: "SUMPRODUCT函数可以将数组间对应的元素相乘，并返\n回乘积之和，适用于加权求和\n用法：SUMPRODUCT(数组,数组...)\n示例：SUMPRODUCT([1,2,3],[0.1,0.2,0.3])返回1.4，也就\n是 1×0.1 + 2×0.2 + 3×0.3的值",
              subValue: "$Formulas.SUMPRODUCTSUB()"
            }
          ]
        },
        {
          label: "文本函数",
          children: [
            {
              value: "$Formulas.CONCATENATE()",
              label: "CONCATENATE",
              helpDesc:
                "CONCATENATE函数可以将多个文本合并成一个文本\n用法：CONCATENATE(文本1,文本2,...)\n示例：CONCATENATE('三年二班','周杰伦')会返回'三年二班周杰伦'"
            },{
              value: "$Formulas.CHAR()",
              label: "CHAR",
              helpDesc:"CHAR函数可以将计算机字符集的数字代码转换为对应字符\n用法：CHAR(数字)\n示例：CHAR(10)会返回换行字符"
            },{
              value: "$Formulas.EXACT()",
              label: "EXACT",
              helpDesc:"EXACT函数可以比较两个文本是否完全相同，完全相同则\n返回true，否则返回false\n用法：EXACT(文本1, 文本2)\n示例：EXACT(手机号,中奖手机号)，如果两者相同，返\n回true，如果不相同，返回false"
            },{
              value: "$Formulas.ISEMPTY()",
              label: "ISEMPTY",
              helpDesc:"ISEMPTY函数可以用来判断值是否为空文本、空对象或者空数组\n用法：ISEMPTY(文本)\n示例：略"
            },{
              value: "$Formulas.LEFT()",
              label: "LEFT",
              helpDesc:"LEFT函数可以从一个文本的第一个字符开始返回指定个数的字符\n用法：LEFT(文本,文本长度)\n示例：LEFT('三年二班周杰伦',2)返回'三年'，也就是'三年二班周杰伦'的从左往右的前2个字符"
            },{
              value: "$Formulas.LEN()",
              label: "LEN",
              helpDesc:"LEN函数可以获取文本中的字符个数\n用法：LEN(文本)\n示例：LEN('朝辞白帝彩云间')返回7，因为这句诗中有7个字符"
            },{
              value: "$Formulas.LOWER()",
              label: "LOWER",
              helpDesc:"LOWER函数可以将一个文本中的所有大写字母转换为小写字母\n用法：LOWER(文本)\n示例：LOWER('JAYZ')返回'jayz'"
            },{
              value: "$Formulas.MID()",
              label: "MID",
              helpDesc:"MID返回文本中从指定位置开始的指定数目的字符\n用法：MID(文本,开始位置_数字,指定数目)\n示例：MID('宏天快速开发平台',4,6)返回'快速开发平台'"
            },{
              value: "$Formulas.REPLACE()",
              label: "REPLACE",
              helpDesc:"REPLACE函数可以根据指定的字符数，将部分文本替换为不同的文本\n用法：REPLACE(文本,开始位置,替换长度,新文本)\n示例：REPLACE('宏天快速开发平台',4,6,'企业数据管理平台')返回'宏天企业数据管理平台'"
            },{
              value: "$Formulas.REPT()",
              label: "REPT",
              helpDesc:"REPT函数可以将文本重复一定次数\n用法：REPT(文本,重复次数)\n示例：REPT('宏天',3)返回'宏天宏天宏天'"
            },{
              value: "$Formulas.RIGHT()",
              label: "RIGHT",
              helpDesc:"RIGHT函数可以获取由给定文本右端指定数量的字符构成的文本值\n用法：RIGHT(文本,文本长度)\n示例：RIGHT('三年二班周杰伦',3)返回'周杰伦'，也就是'三年二班周杰伦'从右往左的前3个字符"
            },
            // {
            //   value: "$Formulas.SEARCH()",
            //   label: "SEARCH",
            //   helpDesc:"SEARCH函数可以获取文本1在文本2中的开始位置\n用法：SEARCH(文本1,文本2)\n示例：SEARCH('2016','宏天2016')返回4"
            // },{
            //   value: "$Formulas.SPLIT()",
            //   label: "SPLIT",
            //   helpDesc:"SPLIT函数可以将文本按指定分割符分割成数组\n用法：SPLIT(文本,分隔符_文本)\n示例：SPLIT('宏天-快速开发平台','-')返回'宏天，快速开发平台'"
            // },
            {
              value: "$Formulas.TEXT()",
              label: "TEXT",
              helpDesc:"TEXT函数可以将数字转化成文本\n用法：TEXT(数字)\n示例：TEXT(3.1415)返回'3.1415'"
            },{
              value: "$Formulas.TRIM()",
              label: "TRIM",
              helpDesc:"TRIM函数可以删除文本首尾的空格\n用法：TRIM(文本)\n示例：TRIM(' 宏天 ')返回'宏天'"
            },{
              value: "$Formulas.UPPER()",
              label: "UPPER",
              helpDesc:"UPPER函数可以将一个文本中的所有小写字母转换为大写字母\n用法：UPPER(文本)\n示例：UPPER('jayz')返回'JAYZ'"
            },{
              value: "$Formulas.VALUE()",
              label: "VALUE",
              helpDesc:"VALUE函数可以将文本转化为数字\n用法：VALUE(文本)\n示例：VALUE('3.1415')返回3.1415"
            },
          ]
        },
        {
          label: "日期函数",
          children: [
            {
              value: "$Formulas.DATE()",
              label: "DATE",
              helpDesc:"DATE函数可以将时间戳转换为日期对象\n用法：DATE(时间戳)\n示例：略"
            },
            {
              value: "$Formulas.DATEDELTA()",
              label: "DATEDELTA",
              helpDesc:"DATEDELTA函数可以将指定日期加/减指定天数\n用法：DATEDELTA(指定日期,需要加减的天数)\n示例：略"
            },
            {
              value: "$Formulas.DAY()",
              label: "DAY",
              helpDesc:"DAY函数可以获取某日期是当月的第几日\n用法：DAY(时间戳)\n示例：略"
            },
            {
              value: "$Formulas.DAYS()",
              label: "DAYS",
              helpDesc:"DAYS函数可以返回两个日期之间相差的天数。\n用法：DAYS(结束日期,开始日期)\n示例：略"
            },
            {
              value: "$Formulas.DAYS360()",
              label: "DAYS360",
              helpDesc:"DAYS360按照一年 360 天的算法，返回两个日期间相差的天数\n用法：DAYS360(结束日期,开始日期)\n示例：略"
            },
            {
              value: "$Formulas.HOUR()",
              label: "HOUR",
              helpDesc:"HOUR函数可以返回某日期的小时数\n用法：HOUR(时间戳)\n示例：略"
            },
            {
              value: "$Formulas.ISOWEEKNUM()",
              label: "ISOWEEKNUM",
              helpDesc:"ISOWEEKNUM函数可以返回指定日期在全年中的ISO周数\n用法：ISOWEEKNUM(指定日期)\n示例：略"
            },
            {
              value: "$Formulas.MINUTE()",
              label: "MINUTE",
              helpDesc:"MINUTE函数可以返回某日期的分钟数\n用法：MINUTE(时间戳)\n示例：略"
            },
            {
              value: "$Formulas.MONTH()",
              label: "MONTH",
              helpDesc:"MONTH返回某日期的月份\n用法：MONTH(时间戳)\n示例：略"
            },
            {
              value: "$Formulas.NOW()",
              label: "NOW",
              helpDesc:"NOW函数可以获取当前时间\n用法：NOW()\n示例：略"
            },
            {
              value: "$Formulas.SECOND()",
              label: "SECOND",
              helpDesc:"SECOND函数可以返回某日期的秒数\n用法：SECOND(时间戳)\n示例：略"
            },
            // {
            //   value: "$Formulas.SYSTIME()",
            //   label: "SYSTIME",
            //   helpDesc:"SYSTIME函数可以获取当前服务器时间\n用法：SYSTIME()\n示例：略"
            // },
            {
              value: "$Formulas.TIME()",
              label: "TIME",
              helpDesc:"TIME函数可以返回特定时间的十进制数字\n用法：TIME(时_数字,分_数字,秒_数字)\n示例：略"
            },
            {
              value: "$Formulas.TIMESTAMP()",
              label: "TIMESTAMP",
              helpDesc:"TIMESTAMP函数可以将日期对象转换成时间戳。\n用法：TIMESTAMP(日期)\n示例：略"
            },
            {
              value: "$Formulas.TODAY()",
              label: "TODAY",
              helpDesc:"TODAY函数可以返回今天\n用法：TODAY()\n示例：略"
            },
            {
              value: "$Formulas.WEEKNUM()",
              label: "WEEKNUM",
              helpDesc:"WEEKNUM函数可以返回指定日期在当年是第几周\n用法：WEEKNUM(指定日期)\n示例：略"
            },
            {
              value: "$Formulas.YEAR()",
              label: "YEAR",
              helpDesc:"YEAR函数可以返回某日期的年份\n用法：YEAR(时间戳)\n示例：略"
            }
          ]
        },
        {
          label: "逻辑函数",
          children: [
            {
              value: "$Formulas.AND()",
              label: "AND",
              helpDesc:"如果所有参数都为真，AND函数返回布尔值true，否则返回布尔值 false\n用法：AND(逻辑表达式1,逻辑表达式2,...)\n示例：AND(语文成绩>90,数学成绩>90,英语成绩>90)，如果三门课成绩都> 90，返回true，否则返回false"
            },
            {
              value: "$Formulas.FALSE()",
              label: "FALSE",
              helpDesc:"FALSE函数返回布尔值false\n用法：FALSE()\n示例：略"
            },
            {
              value: "$Formulas.IF()",
              label: "IF",
              helpDesc:"IF函数判断一个条件能否满足；如果满足返回一个值，如果不满足则返回另外一个值\n用法：IF(逻辑表达式,为true时返回的值,为false时返回的值)\n示例：IF(语文成绩>60,\"及格\",\"不及格\")，当语文成绩>60时返回及格，否则返回不及格。"
            },
            {
              value: "$Formulas.NOT()",
              label: "NOT",
              helpDesc:"NOT函数返回与指定表达式相反的布尔值。\n用法：NOT(逻辑表达式)\n示例：NOT(语文成绩>60)，如果语文成绩大于60返回false，否则返回true"
            },
            {
              value: "$Formulas.OR()",
              label: "OR",
              helpDesc:"如果任意参数为真，OR 函数返回布尔值true；如果所有参数为假，返回布尔值false。\n用法：OR(逻辑表达式1,逻辑表达式2,...)\n示例：OR(语文成绩>90,数学成绩>90,英语成绩>90)，任何一门课成绩> 90，返回true，否则返回false"
            },
            {
              value: "$Formulas.TRUE()",
              label: "TRUE",
              helpDesc:"TRUE函数返回布尔值true\n用法：TRUE()\n示例：略"
            },
            {
              value: "$Formulas.XOR()",
              label: "XOR",
              helpDesc:"XOR函数可以返回所有参数的异或值\n用法：XOR(逻辑表达式1, 逻辑表达式2,...)\n示例：XOR(语文成绩>90,数学成绩>90)，如果两门成绩都>90,返回false；如果两门成绩都<90，返回false；如果其中一门>90，另外一门<90，返回true"
            }
          ]
        },
        // {
        //   label: "高级函数",
        //   children: [
        //     {
        //       value: "$Formulas.UUID()",
        //       label: "UUID",
        //       helpDesc:""
        //     }
        //   ]
        // }
      ],
      checked: false,
      nodeType: ""
    };
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/element-variables.scss";
@import "@/assets/css/form-editor.scss";

.code >>> .CodeMirror {
  font-family: monospace;
  height: 100px !important;
  color: black;
  direction: ltr;
}
</style>
