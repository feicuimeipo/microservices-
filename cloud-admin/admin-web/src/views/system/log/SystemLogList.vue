<template>
  <div class="fullheight">
    <ht-table
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      quick-search-props="opeName,executor,ip,reqUrl"
      :default-sorter="[{ direction: 'DESC', property: 'executionTime' }]"
      ref="htTable"
      :show-export="false"
    >
      <template v-slot:toolbar>
        <el-button-group>
          <ht-delete-button :url="deleteUrl" :htTable="$refs.htTable" @after-delete="afterDelete">删除</ht-delete-button>
        </el-button-group>
      </template>

      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column prop="opeName" label="操作名称" :sortable="true" :show-overflow-tooltip="true"></ht-table-column>
        <ht-table-column prop="executionTime" label="执行时间" :sortable="true" width="140" />
        <ht-table-column prop="executor" label="执行人" width="160" />
        <ht-table-column prop="ip" label="客户端IP" width="120" />
        <ht-table-column prop="reqUrl" label="请求地址" width="180" />
        <ht-table-column prop="logType" label="日志类型" width="100" :filters="typeArray">
          <template v-slot="{row}">
            <el-tag
              v-show="row.logType==s.value"
              type="info"
              v-for="s in typeArray"
              :key="s.value"
            >{{s.text}}</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column prop="moduleType" label="模块类型" width="100" :filters="statusArray">
          <template v-slot="{row}">
            <el-tag
              v-show="row.moduleType==s.value"
              type="info"
              v-for="s in statusArray"
              :key="s.value"
            >{{s.text}}</el-tag>
          </template>
        </ht-table-column>
      </template>
    </ht-table>
  </div>
</template>
<script>
import req from "@/request.js";
export default {
  components: {},
  data() {
    return {
      defaultProps: {
        children: "children",
        label: "name"
      },
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      curRow: {},
      dialogVisible: false,
      statusArray: [
        { text: "用户微服务", value: "uc-eureka" },
        { text: "portal微服务", value: "portal-eureka" },
        { text: "表单微服务", value: "form-eureka" },
        { text: "bpm-model微服务", value: "bpm-model-eureka" },
        { text: "bpm-runtime微服务", value: "bpm-runtime-eureka" }
      ],
      typeArray: [
        { text: "操作日志", value: "操作日志" },
        { text: "登录日志", value: "登录日志" },
        { text: "异常日志", value: "异常日志" }
      ]
    };
  },
  computed: {
    deleteUrl: function() {
      return window.context.portal + "/sys/sysLogs/v1/removes";
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false;
    },
    openDetail(row) {
      this.curRow = row;
      this.dialogVisible = true;
    },
    loadData(param, cb) {
      req
        .post(req.getContext().portal + "/sys/sysLogs/v1/list", param)
        .then(data => {
          let response = data.data;
          this.data = response.rows;
          this.pageResult = {
            page: response.page,
            pageSize: response.pageSize,
            total: response.total
          };
        })
        .finally(() => cb());
    },
    afterDelete() {
      this.$refs.htTable.load();
    },
    reinvoke(id) {
      let this_ = this;
      req
        .post(req.getContext().portal + "/portal/messageLog/v1/reinvoke/" + id)
        .then(function(response) {
          let data = response.data;
          if (data.state) {
            this_.$message.success(data.message);
            this_.$refs.htTable.load();
          } else {
            this_.$message.fail(data.message);
          }
        });
    },
    signSuccess(id) {
      let this_ = this;
      req
        .post(
          req.getContext().portal + "/portal/messageLog/v1/signSuccess/" + id
        )
        .then(function(response) {
          let data = response.data;
          if (data.state) {
            this_.$message.success(data.message);
            this_.$refs.htTable.load();
          } else {
            this_.$message.fail(data.message);
          }
        });
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/element-variables.scss";

.aside-width {
  width: $--aside-width !important;
}
</style>
