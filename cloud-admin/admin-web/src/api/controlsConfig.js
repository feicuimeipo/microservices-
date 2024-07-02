export const basicComponents = [
  {
    ctrlType: "input",
    name: "",
    desc: "单行文本",
    icon: "icon-single",
    options: {
      width: "100%",
      isWidth: true,
      defaultValue: "",
      isCountDate: false,
      mathExp: "",
      isStartDate: "",
      isEndDate: "",
      countFormat: "day",
      dataType: "string|number",
      validateList: [],
      validate: "",
      validateType:
        "confirmed|email|regex|min|max|min_value|max_value|isAfter|isBefore|isStart|isEnd|numeric|between|is|digits|mobile|required|row_unique|alpha_spaces|alpha_dash|alpha_num|alpha|frontJSValidate|backendValidate",
      advancedProperty: "isInputEdit|isBindIdentity|inputType|bindPreAndSufFix",
      placeholder: "",
      disabled: false,
      basicsProperty: "input|dateCount|placeholder",
      bindIdentityjson: {}, //流水号对象
      isBindPreAndSufFix: false,
      bindPreAndSufFixjson: { preFix: "", sufSix: "" },
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "textarea",
    name: "",
    desc: "多行文本",
    icon: "icon-textarea",
    options: {
      width: "100%",
      isWidth: true,
      defaultValue: "",
      dataType: "string|text",
      validateList: [],
      validateType: "regex|min|max|required|row_unique|frontJSValidate|backendValidate",
      advancedProperty: "isEditor|isInputEdit",
      disabled: false,
      placeholder: "",
      basicsProperty: "placeholder",
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "property-text",
    name: "",
    desc: "属性文本",
    icon: "icon-textarea",
    options: {
      width: "100%",
      defaultValue: "",
      dataType: "string|text",
      validateList: [],
      validateType: "regex|min|max|required|row_unique|frontJSValidate|backendValidate",
      disabled: false,
      placeholder: "",
      basicsProperty: "textValue",
      advancedProperty: "isEditor",
      textValue: "",
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "number",
    name: "",
    desc: "数字",
    icon: "icon-number",
    options: {
      width: "100%",
      isWidth: true,
      defaultValue: 0,
      validateType:
        "min_value|max_value|confirmed|numeric|between|digits|required|row_unique|frontJSValidate|mobile",
      validateList: [],
      dataType: "number",
      filterthousandBit: "",
      filtercurrency: "",
      min: 0,
      max: 99999,
      step: 1,
      decimalDigits: 0,
      maxDecimalDigits: 0,
      mathExp: "",
      disabled: false,
      controlsPosition: "",
      advancedProperty: "inputType|bindPreAndSufFix",
      basicsProperty: "number|currency|placeholder|dateCount",
      company: "", //扩展单位配置项,
      formulasDiyJs: "",
      bindPreAndSufFixjson: { preFix: "", sufSix: "" },
    }
  },
  {
    ctrlType: "currency",
    name: "",
    desc: "货币",
    icon: "icon-currency",
    options: {
      width: "100%",
      isWidth: true,
      required: false,
      dataType: "number",
      defaultValue: 0,
      filterthousandBit: "",
      filtercurrency: "",
      min: 1,
      max: 99999,
      step: 1,
      decimalDigits: 0,
      maxDecimalDigits: 0,
      disabled: false,
      controlsPosition: "",
      mathExp: "",
      advancedProperty: "currencyControl",
      basicsProperty: "currency|placeholder",
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "radio",
    name: "",
    desc: "单选框",
    icon: "icon-radio-active",
    options: {
      inline: false,
      defaultValue: "",
      showLabel: false,
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      options: [],
      required: false,
      width: "",
      remote: false,
      remoteOptions: [],
      props: {
        value: "value",
        label: "label"
      },
      remoteFunc: "",
      disabled: false,
      advancedProperty: "isVertical|choiceType", //高级属性
      choiceType: "static", //默认固定值
      customQuery: {}, //动态值对象
      bind: [], //绑定的返回值
      linkage: [], //联动校验表达式
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "checkbox",
    name: "",
    desc: "多选框",
    icon: "icon-check-box",
    options: {
      inline: false,
      defaultValue: [],
      showLabel: false,
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      options: [],
      required: false,
      width: "",
      remote: false,
      remoteOptions: [],
      props: {
        value: "value",
        label: "label"
      },
      remoteFunc: "",
      disabled: false,
      advancedProperty: "isVertical|choiceType", //高级属性
      choiceType: "static", //默认固定值
      customQuery: {}, //动态值对象
      bind: [], //绑定的返回值
      linkage: [], //联动校验表达式
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "date",
    name: "",
    desc: "日期",
    icon: "icon-date",
    options: {
      defaultValue: "",
      readonly: false,
      disabled: false,
      dataType: "date",
      editable: true,
      clearable: true,
      placeholder: "",
      startPlaceholder: "",
      validateType: "isAfter|isBefore|isStart|isEnd|required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      endPlaceholder: "",
      type: "date",
      inputFormat: "yyyy-MM-dd",
      timestamp: false,
      required: false,
      basicsProperty: "date|placeholder",
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "time",
    name: "",
    desc: "时间",
    icon: "icon-time",
    options: {
      defaultValue: "21:19:56",
      readonly: false,
      dataType: "date",
      disabled: false,
      editable: true,
      clearable: true,
      placeholder: "",
      startPlaceholder: "",
      type: "select", //选择器格式
      endPlaceholder: "",
      isRange: false,
      arrowControl: true,
      format: "HH:mm:ss",
      inputFormat: "HH:mm:ss",
      required: false,
      basicsProperty: "time",
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "selector",
    name: "",
    desc: "选择器",
    icon: "icon-user",
    options: {
      width: "100%",
      isWidth: true,
      defaultValue: "",
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      required: false,
      dataType: "string",
      pattern: "",
      placeholder: "",
      disabled: false,
      selector: { type: { alias: "eip-user-selector" } }, //选择器对象
      bind: [
        {
          alias: "eip-user-selector",
          key: "id",
          value: "用户ID"
        },
        {
          alias: "eip-user-selector",
          key: "account",
          value: "用户账号"
        },
        {
          alias: "eip-user-selector",
          key: "fullname",
          value: "用户姓名"
        },
        {
          alias: "eip-user-selector",
          key: "mobile",
          value: "用户手机"
        },
        {
          alias: "eip-user-selector",
          key: "email",
          value: "用户邮箱"
        }
      ],
      basicsProperty: "selector"
    }
  },
  {
    ctrlType: "attachment",
    name: "",
    desc: "附件",
    icon: "icon-attachment",
    options: {
      width: "100%",
      validateType: "required",
      validateList: [],
      validate: "",
      defaultValue: "",
      required: false,
      dataType: "string",
      pattern: "",
      placeholder: "",
      disabled: false,
      basicsProperty: "fileupload",
      propConf: [
        { name: "prop1" },
        { name: "prop2" },
        { name: "prop3" },
        { name: "prop4" },
        { name: "prop5" },
        { name: "prop6" }
      ], //附件扩展属性配置
      file: {
        multiple: true,
        limit: "5",
        acceptType:'any',
        acceptStr:'',
        size:'50',
        accept: [
          "jpg",
          "jpeg",
          "png",
          "bmp",
          "pdf",
          "JPG",
          "JPEG",
          "PNG",
          "BMP",
          "PDF",
          "doc",
          "docx",
          "xls",
          "xlsx",
          "ppt",
          "pptx",
          "rtf",
          "txt",
          "zip",
          "rar",
          "vsd",
          "dwg",
          "mp4",
          "avi",
          "3gp",
          "rmvb",
          "rm",
          "wmv"
        ]
      } //文件上传对象
    }
  },
  {
    ctrlType: "select",
    name: "",
    desc: "下拉框",
    isMultiple: false,
    icon: "icon-select",
    options: {
      width: "100%",
      isWidth: true,
      defaultValue: "",
      multiple: false,
      disabled: false,
      clearable: false,
      placeholder: "",
      required: false,
      showLabel: false,
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      options: [],
      remote: false,
      filterable: false,
      allowCreate: false,
      remoteOptions: [],
      props: {
        value: "value",
        label: "label"
      },
      remoteFunc: "",
      basicsProperty: "selectConfig|choiceType|placeholder",
      choiceType: "static", //默认固定值
      customQuery: {
        custQueryJson: [],
        valueBind: "",
        labelBind: "",
        conditionfield: []
      }, //动态值对象
      bind: [], //绑定的返回值
      linkage: [], //联动校验表达式
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "dropdown",
    name: "",
    desc: "下拉树",
    icon: "icon-dropdown-tree",
    isMultiple: false,
    options: {
      defaultValue: "",
      multiple: false,
      disabled: false,
      clearable: false,
      placeholder: "",
      required: false,
      showLabel: false,
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      width: "",
      options: [],
      remote: false,
      filterable: false,
      allowCreate: false,
      remoteOptions: [],
      props: {
        value: "value",
        label: "label"
      },
      remoteFunc: "",
      basicsProperty: "selectConfig|choiceType|placeholder",
      choiceType: "dynamic", //默认固定值
      customQuery: {
        custQueryJson: [],
        valueBind: "",
        labelBind: "",
        conditionfield: []
      }, //动态值对象
      bind: [], //绑定的返回值
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "switch",
    name: "",
    desc: "开关",
    icon: "icon-switch",
    options: {
      defaultValue: "true",
      activeValue: "true",
      inactiveValue: "false",
      activeText: "",
      inactiveText: "",
      disabled: true,
      validateType: "required",
      validateList: [],
      validate: "",
      formulasDiyJs: ""
    }
  },
  {
    ctrlType: "text",
    name: "",
    desc: "文本",
    icon: "icon-text",
    options: {
      defaultValue: "This is a text",
      noBindModel: true,
      customClass: "",
      basicsProperty: "textValue"
    }
  },
  {
    ctrlType: "dic",
    name: "",
    desc: "数据字典",
    icon: "icon-dic",
    options: {
      defaultValue: "",
      basicsProperty: "dicConfig",
      bind: [], //绑定
      dic: "", //字典别名
      filterable: false, //字典是否可搜索
      placeholder: ""
    }
  },
  {
    ctrlType: "iframe",
    name: "",
    desc: "iframe面板",
    icon: "icon-iframe",
    noBasics: true,
    options: {
      iframeSrc: "",
      iframeSrcHeight: "300",
      noBindModel: true,
      iframeSrcWidth: "100%",
      frameborder: "1",
      lableColor: "",
      basicsProperty: "iframe"
    }
  },
  {
    ctrlType: "imageViewer",
    name: "",
    desc: "图片",
    icon: "icon-image",
    options: {
      defaultValue: [],
      size: {},
      isDisplay: false,
      uploadType: "local",
      fileJson: "",
      tokenFunc: "funcGetToken",
      basicsProperty: "imageViewer",
      validateType: "required",
      validateList: [],
      validate: "",
      file: {
        multiple: true,
        limit: "3"
      }
    }
  },
  {
    ctrlType: "image",
    name: "",
    desc: "背景图片",
    icon: "icon-image",
    options: {
      defaultValue: [],
      size: {},
      isDisplay: false,
      noBindModel: true,
      imgSrc: "",
      fileJson: "",
      tokenFunc: "funcGetToken",
      basicsProperty: "img"
    }
  }
];

export const advanceComponents = [
  {
    ctrlType: "amap",
    name: "",
    desc: "高德地图",
    icon: "icon-landmark",
    options: {
      width: "100%",
      defaultValue: "",
      addressMap: "",
      addressName: "",
      dataType: "varchar",
      noBindModel: true,
      placeholder: "",
      heightMap: "350"
    }
  },
  {
    ctrlType: "button",
    name: "",
    desc: "按钮",
    icon: "icon-button",
    options: {
      noBindModel: false,
      defaultType: "String",
      basicsProperty: "onetextBtn",
      bindEventjson: { name: "选择", icon: "", isShowInput: true }
    },
    noTitle: false,
    noPlaceholder: false,
    noTooltip: false
  },
  {
    ctrlType: "dialog",
    name: "",
    desc: "对话框",
    icon: "icon-dialog",
    placeholder: "",
    options: {
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      defaultType: "String",
      basicsProperty: "custDialog|placeholder",
      customDialogjson: {
        name: "请选择",
        icon: "",
        custDialog: {
          selectNum: "",
          conditions: [],
          mappingConf: [],
          custQueryJson: []
        },
        resultField: []
      }
    }
  },
  // {
  //     ctrlType: 'relation',
  //     name: '',
  //     desc: '关联数据',
  //     icon: 'icon-relation',
  //     options: {
  //         validateType: 'required|row_unique|frontJSValidate',
  //         validateList: [],
  //         validate: "",
  //         defaultType: 'String',
  //         basicsProperty:'relation',
  //         customQuery:{valueBind:"",labelBind:"",conditionfield:[]},//动态值对象
  //         bind:[],//绑定的返回值
  //     }
  // },
  {
    ctrlType: "related-process",
    name: "",
    desc: "相关流程",
    icon: "icon-flow2",
    options: {
      defaultType: "String",
      basicsProperty: "isPaging",
      isPaging: true, //是否分页
      pageSize: 10 //默认分页大小
    }
  },
  {
    ctrlType: "immediate-single",
    name: "",
    desc: "实时单行",
    icon: "icon-single2",
    options: {
      width: "100%",
      defaultType: "String",
      noBindModel: true,
      advancedProperty: "script"
    }
  },
  {
    ctrlType: "immediate-textarea",
    name: "",
    desc: "实时多行",
    icon: "icon-textarea2",
    options: {
      width: "100%",
      defaultValue: "",
      dataType: "string|text",
      noBindModel: true,
      placeholder: "",
      basicsProperty: "script"
    }
  },
  {
    ctrlType: "milepost",
    name: "",
    desc: "里程碑",
    icon: "icon-milepost",
    noTitle: true,
    noPlaceholder: true,
    noTooltip: true,
    options: {
      direction: "horizontal",
      placeholder: "",
      defaultValue: "",
      width: "100%",
      basicsProperty: "stepControl",
      steps: [{ title: "", description: "" }]
    }
  },
  {
    ctrlType: "autocomplete",
    name: "",
    desc: "输入建议",
    icon: "icon-autocomplete",
    options: {
      dataType: "string|number",
      validateType:
        "confirmed|email|regex|min|max|min_value|max_value|isAfter|isBefore|isStart|isEnd|numeric|between|is|digits|mobile|required|row_unique|alpha_spaces|alpha_dash|alpha_num|alpha|frontJSValidate",
      placeholder: "",
      defaultValue: "",
      validateList: [],
      disabled: false,
      validate: "",
      required: false,
      width: "100%",
      basicsProperty: "selectConfig|choiceType", //高级属性
      choiceType: "static", //默认固定值
      customQuery: {}, //动态值对象
      bind: [], //绑定的返回值
      options: []
    }
  },
  {
    ctrlType: "eip-cascader",
    name: "",
    desc: "级联",
    icon: "icon-cascader",
    options: {
      required: false,
      validateType: "required|row_unique|frontJSValidate",
      validateList: [],
      validate: "",
      defaultValue: [],
      width: "",
      placeholder: "",
      disabled: false,
      clearable: false,
      remote: true,
      remoteOptions: [],
      props: {
        value: "value",
        label: "label",
        children: "children"
      },
      remoteFunc: "",
      basicsProperty: "selectConfig|choiceType",
      choiceType: "dynamic", //默认固定值
      customQuery: { valueBind: "", labelBind: "", conditionfield: [] }, //动态值对象
      bind: [], //绑定的返回值
      linkage: [] //联动校验表达式
    }
  }
];

export const layoutComponents = [
  {
    ctrlType: "tab",
    name: "",
    desc: "tab布局",
    icon: "icon-tab",
    isLayout: true,
    columns: [
      {
        span: "标签页1",
        list: []
      }
    ],
    options: {
      gutter: 0,
      nextCheck: "",
      type: "", //风格类型
      justify: "start",
      align: "top"
    }
  },
  {
    ctrlType: "grid",
    name: "",
    desc: "栅格布局",
    icon: "icon-grid",
    isLayout: true,
    columns: [
      {
        span: 12,
        list: []
      },
      {
        span: 12,
        list: []
      }
    ],
    options: {
      gutter: 0,
      justify: "start",
      align: "top"
    }
  },
  {
    ctrlType: "page",
    name: "",
    desc: "分页布局",
    icon: "icon-page-seprator",
    isLayout: true,
    columns: [
      {
        list: []
      }
    ]
  },
  {
    ctrlType: "accordion",
    name: "",
    desc: "折叠面板",
    icon: "icon-accordion",
    isLayout: true,
    columns: [
      {
        span: "折叠面板",
        idKey: Date.parse(new Date()) + "a",
        isOpen: false,
        list: []
      }
    ],
    options: {
      gutter: 0,
      justify: "start",
      nextCheck: "",
      accordion: "false",
      activeNames: [],
      align: "top"
    }
  },
  {
    ctrlType: "subtable",
    name: "子表",
    desc: "子表",
    icon: "icon-table2",
    isLayout: true,
    customHeader: "",
    list: [],
    options: {
      showLabel: true,
      boSubEntity: "",
      subTablePath: "",
      relation: ""
    },
    customDialogjson: {
      name: "子表回填",
      icon: "",
      custDialog: { conditions: [], mappingConf: [] },
      orgConfig: { name: '', code: '', id: '' },
      resultField: []
    },
    subtableBackfill: false,
    initTemplateData: false,
    initTemplateDataType:'empty',
  },
  {
    ctrlType: "subDiv",
    name: "div子表",
    desc: "div子表",
    icon: "icon-table2",
    isLayout: true,
    customHeader: "",
    list: [],
    options: {
      showLabel: true,
      boSubEntity: "",
      subTablePath: "",
      subDivTablePath: "",
      relation: ""
    },
    customDialogjson: {
      name: "子表回填",
      icon: "",
      custDialog: { conditions: [], mappingConf: [] },
      orgConfig: { name: '', code: '', id: '' },
      resultField: []
    },
    subtableBackfill: false,
    initTemplateData: false,
    initTemplateDataType:'empty',
  },
  {
    ctrlType: "suntable",
    name: "孙表",
    desc: "孙表",
    icon: "icon-table2",
    isLayout: true,
    customHeader: "",
    list: [],
    options: {
      showLabel: true,
      boSubEntity: "",
      subTablePath: "",
      relation: ""
    },
    customDialogjson: {
      name: "孙表回填",
      icon: "",
      custDialog: { conditions: [], mappingConf: [] },
      resultField: []
    },
    subtableBackfill: false
  },
  {
    ctrlType: "sunDiv",
    name: "div孙表",
    desc: "div孙表",
    icon: "icon-table2",
    isLayout: true,
    customHeader: "",
    list: [],
    options: {
      showLabel: true,
      boSubEntity: "",
      subTablePath: "",
      relation: ""
    },
    customDialogjson: {
      name: "孙表回填",
      icon: "",
      custDialog: { conditions: [], mappingConf: [] },
      resultField: []
    },
    subtableBackfill: false
  },
  {
      ctrlType: "hottable",
      name: "hotTable",
      desc: "hotTable",
      icon: "icon-table2",
      isLayout: true,
      customHeader: "",
      list: [],
      options: {
          showLabel: true,
          boSubEntity: "",
          subTablePath: "",
          relation: "",
          colHeadersRelations: [],//表头数据
          templateContent: {},
          initFillbackData: [],
          height: 80,
          crossMapping: [],//跨表取数
          nestedHeaders: [],//嵌套表头
          mainTableCalcs: [],//主表字段统计
          initSumRowField: '', //统计行标签字段
          initSumRowValue: '',//统计行标签字段值
      },
      customDialogjson: {
          name: "子表回填",
          icon: "",
          custDialog: { conditions: [], mappingConf: [] },
          resultField: [],
          orgConfig: { name: '', code: '', id: '' },
      },
      subtableBackfill: false,
      initTemplateData: false,
      initTemplateDataType:'empty',
      addInitTemplateData: false, //允许添加初始化数据
      addInitBtnName: '添加初始化数据',//添加初始化数据按钮
      initSumRow:false, //初始化统计行
  },
  {
    ctrlType: "dataView",
    name: "数据视图",
    desc: "数据视图",
    icon: "icon-list",
    isLayout: true,
    templateKey: "",
    templateId: "",
    templateObj:{},
    templateField:[],
    options: {
      showLabel: true,
      boDefAlias: "",
      selectField: "",
      bindSelectd: "",
      fillField: "",
      bindFilld: "",
      selectValue: "",
      fillValue: ""
    },
    customDialogjson: {
      name: "数据视图",
      custDialog: { conditions: [], mappingConf: [] },
      resultField: []
    },
    subtableBackfill: false
  }
];

export const validateRules = [
  {
    key: "confirmed",
    isInput: true,
    isBoData: true,
    type: "string|number|text|date",
    inputType: "select", //输入框类型
    name: "相同的值"
  },
  {
    key: "email",
    name: "电子邮箱"
  },
  {
    key: "regex",
    isInput: true,
    inputType: "input", //输入框类型
    name: "*正则表达式"
  },
  {
    key: "min",
    isInput: true,
    inputType: "input", //输入框类型
    name: "*最小文本长度"
  },
  {
    key: "max",
    isInput: true,
    inputType: "input", //输入框类型
    name: "*最大文本长度"
  },
  {
    key: "min_value",
    isInput: true,
    inputType: "input", //输入框类型
    name: "最小数值"
  },

  {
    key: "max_value",
    isInput: true,
    inputType: "input", //输入框类型
    name: "最大数值"
  },
  {
    key: "isAfter",
    isInput: true,
    inputType: "select", //输入框类型
    type: "date",
    name: "日期晚于"
  },
  {
    key: "isBefore",
    isInput: true,
    inputType: "select", //输入框类型
    type: "date",
    name: "日期早于"
  },
  {
    key: "isStart",
    isInput: true,
    inputType: "select", //输入框类型
    type: "date",
    name: "日期不早于"
  },
  {
    key: "isEnd",
    isInput: true,
    inputType: "select", //输入框类型
    type: "date",
    name: "日期不晚于"
  },
  {
    key: "numeric",
    name: "整数"
  },
  {
    key: "between",
    isInput: true,
    inputType: "twoInput", //输入框类型
    name: "指定范围的数字(包含最小/大值)"
  },
  {
    key: "is",
    isInput: true,
    inputType: "input", //输入框类型
    name: "*指定的值"
  },
  {
    key: "digits",
    isInput: true,
    inputType: "input", //输入框类型
    name: "指定位数的数字"
  },
  {
    key: "mobile",
    name: "手机号码"
  },
  {
    key: "required",
    name: "*必填"
  },
  {
    isInput: true,
    key: "row_unique",
    name: "*行唯一"
  },
  {
    key: "alpha_spaces",
    name: "字母空格"
  },
  {
    key: "alpha_dash",
    name: "字母数字横线下划线"
  },
  {
    key: "alpha_num",
    name: "字母数字"
  },
  {
    key: "alpha",
    name: "字母"
  },
  {
    key: "frontJSValidate",
    isInput: true,
    inputType: "button", //输入框类型
    name: "JS方法校验"
  },
  {
    key: "backendValidate",
    isInput: true,
    inputType: "input", //输入框类型
    name: "后端校验"
  }
  // {
  //   key: "uniqueValidate",
  //   name: "唯一性校验"
  // }
];
export default {
  isLayoutComponents(type) {
    if (
      type == "tab" ||
      type == "subtable" ||
      type == "subDiv" ||
      type == "accordion" ||
      type == "page" ||
      type == "grid" ||
      type == "dataView"
    ) {
      return true;
    }
    return false;
  },
  isGridLayoutComponents(type) {
    if (
      type == "tab" ||
      type == "accordion" ||
      type == "page" ||
      type == "sunDiv" ||
      type == "suntable"
    ) {
      return true;
    }
    return false;
  },
  handleLayoutComponents(vueInst) {
    let this_ = vueInst;
    let isPass = true;
    vueInst.data.list.forEach(l => {
      if (this.isLayoutComponents(l.ctrlType)) {
        if (l.columns) {
          l.columns.forEach(c => {
            if (c.list) {
              c.list = c.list.filter(element => {
                if (
                  l.ctrlType == "tab" ||
                  l.ctrlType == "accordion" ||
                  l.ctrlType == "subtable"
                ) {
                  if (this.isGridLayoutComponents(element.ctrlType) && !(l.ctrlType=='accordion' && element.ctrlType=='tab')) {
                    this_.$message.warning("布局字段中不允许再拖入布局字段");
                    isPass = false;
                    return false;
                  }
                } else {
                  if (
                    l.ctrlType == "grid" &&
                    (element.ctrlType == "sunDiv" ||
                      element.ctrlType == "suntable")
                  ) {
                    this_.$message.warning("孙表只能存在子表中");
                    isPass = false;
                    return false;
                  }
                  if (this.isLayoutComponents(element.ctrlType)) {
                    this_.$message.warning("布局字段中不允许再拖入布局字段");
                    isPass = false;
                    return false;
                  }
                  if (l.ctrlType == "grid" && element.ctrlType == "hottable") {
                      this_.$message.warning("布局字段中不允许再拖入布局字段");
                      isPass = false;
                      return false;
                    }
                }

                return true;
              });
            }
          });
        } else if (l.list) {
          if (l.ctrlType == "subDiv") {
            l.list = l.list.filter(c => {
              if (this.isLayoutComponents(c.ctrlType) && c.ctrlType != "grid") {
                this_.$message.warning("布局字段中不允许再拖入布局字段");
                isPass = false;
                return false;
              }
              return true;
            });
          } else {
            l.list = l.list.filter(c => {
              if (this.isLayoutComponents(c.ctrlType)) {
                this_.$message.warning("布局字段中不允许再拖入布局字段");
                isPass = false;
                return false;
              }
              return true;
            });
          }
        }
      }
    });
    return isPass;
  }
};
