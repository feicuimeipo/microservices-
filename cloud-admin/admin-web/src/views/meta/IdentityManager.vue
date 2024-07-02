<template>
  <div class="fullheight">
    <ht-table
      ref="identityTable"
      @load="loadData"
      :data="data"
      :pageResult="pageResult"
      :selection="true"
      quick-search-props="name,alias"
      :show-export="false"
      :defaultSorter="[{'property':'CREATE_TIME_','direction':'DESC'}]"
    >
      <template v-slot:toolbar>
        <el-button
            icon="el-icon-plus"
            @click="handleCommand({ command: 'add' })"
        >添加</el-button
        >
        <ht-delete-button
            :url="identityDeleteUrl"
            :htTable="$refs.identityTable"
        >删除</ht-delete-button
        >
      </template>
      <template>
        <ht-table-column type="index" width="50" align="center" label="序号" />
        <ht-table-column
          prop="name"
          label="名称"
          width="300"
          :sortable="true"
          :show-overflow-tooltip="true"
        >
          <template v-slot="{ row }">
            <el-link
              @click="handleCommand({ row: row, command: 'edit' })"
              type="primary"
              >{{ row.name }}</el-link
            >
          </template>
        </ht-table-column>
        <ht-table-column prop="alias" label="别名" :sortable="true" />
        <ht-table-column
          prop="genType"
          label="生成类型"
          :filters="[
            { text: '递增', value: 0 },
            { text: '每天生成', value: 1 },
            { text: '每月生成', value: 2 },
            { text: '每年生成', value: 3 }
          ]"
        >
          <template v-slot="{ row }">
            <el-tag type="success" v-if="row.genType === 0">递增</el-tag>
            <el-tag type="primary" v-if="row.genType === 1">每天生成</el-tag>
            <el-tag type="warning" v-if="row.genType === 2">每月生成</el-tag>
            <el-tag type="danger" v-if="row.genType === 3">每年生成</el-tag>
          </template>
        </ht-table-column>
        <ht-table-column prop="regulation" label="规则" sortable />
        <ht-table-column prop="noLength" label="长度" sortable />
        <!-- <ht-table-column width="150" label="操作">
              <template v-slot="{row}">
                <el-button size="mini" class="el-icon-edit"  @click="handleCommand({row:row,command:'edit'})">编辑</el-button>
                <el-dropdown
                  size="mini"
                  split-button
                  @command="handleCommand"
                  @click="handleCommand({row:row,command:'edit'})"
                >
                  <span>
                    <i class="el-icon-edit"></i>编辑
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item icon="el-icon-menu" :command="{row:row,command:'get'}">查看</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
        </ht-table-column>-->
      </template>
    </ht-table>

    <el-dialog
      width="50%"
      :title="identiyTitle"
      :visible="dialogVisible"
      :before-close="handleClose"
    >
      <el-form
        :disabled="identityDisabled"
        v-model="identity"
        data-vv-scope="editIdentityForm"
      >
        <ht-form-item
          label="流水号名称"
          prop="name"
          label-width="120px"
          class="identity-input-width"
        >
          <ht-input
            v-model="identity.name"
            autocomplete="off"
            :validate="{ required: true }"
            placeholder="请输入名称"
          ></ht-input>
        </ht-form-item>
        <ht-form-item
          label="流水号别名"
          prop="alias"
          label-width="120px"
          class="identity-input-width"
        >
          <ht-input
            v-model="identity.alias"
            v-pinyin="identity.name"
            autocomplete="off"
            :validate="{ required: true, alpha_num: true }"
            placeholder="请输入别名"
            :disabled="identity.id ? true : false"
          ></ht-input>
        </ht-form-item>
        <ht-form-item
          label="流水号规则"
          label-width="120px"
          class="identity-item-bottom identity-input-width"
        >
          <ht-input
            width="100%"
            v-model="identity.regulation"
            :validate="{ required: true }"
            placeholder="请输入流水号规则"
          />
        </ht-form-item>
        <ht-form-item label label-width="120px" class="identity-item-bottom">
          <ul>
            <li>{yyyy}{MM}{dd}{NO}</li>
            <li>{yyyy}:表示年份</li>
            <li>
              {MM} :表示月份，如果月份小于10，则加零补齐，如1月份表示为01。
            </li>
            <li>{mm} :表示月份，月份不补齐，如1月份表示为1。</li>
            <li>{DD} :表示日，如果小于10号，则加零补齐，如1号表示为01。</li>
            <li>{dd} :表示日，日期不补齐，如1号表示为1。</li>
            <li>{NO} :表示流水号，前面补零。</li>
            <li>{no} :表示流水号，后面补零。</li>
            <li>
              {xx.xxx}
              :表示表单中的字符，xx为数据建模实体名称，xxx为字段名称。例如：{student.name}
            </li>
          </ul>
        </ht-form-item>
        <ht-form-item
          label="生成类型"
          label-width="120px"
          class="identity-item-bottom identity-input-width"
        >
          <ht-radio
            :validate="{ required: true }"
            v-model="identity.genType"
            :options="genTypes"
          />
        </ht-form-item>
        <ht-form-item label label-width="120px" class="identity-item-bottom">
          <ul>
            <li>1.每天生成。每天从初始值开始计数。</li>
            <li>2.递增，流水号一直增加。</li>
          </ul>
        </ht-form-item>
        <ht-form-item
          label="流水号长度"
          label-width="120px"
          class="identity-item-bottom identity-input-width"
        >
          <ht-input
            :validate="'required: true|regex:^[0-9]*$,只能输入数字'"
            v-model="identity.noLength"
            placeholder="请输入流水号长度"
          />
        </ht-form-item>
        <ht-form-item label label-width="120px" class="identity-item-bottom">
          <ul>
            <li>
              这个长度表示当前流水号的长度数，只包括流水号部分{NO},如果长度为5，当前流水号为5，则在流水号前补4个0，表示为00005。
            </li>
            <li>
              {no}如果长度为5，当前流水号为501，则在流水号后面补5个0，表示为50100000。
            </li>
          </ul>
        </ht-form-item>
        <ht-form-item
          label="初始值"
          label-width="120px"
          class="identity-item-bottom identity-input-width"
        >
          <ht-input
            :validate="'required: true|regex:^[0-9]*$,只能输入数字'"
            v-model="identity.initValue"
            placeholder="请输入初始值"
          />
        </ht-form-item>
        <ht-form-item label label-width="120px" class="identity-item-bottom">
          <ul>
            <li>
              这个初始值表示流水号部分{NO}的初始值。如
              2015102700001,初始值为1，则流水号部分的初始值为00001
            </li>
          </ul>
        </ht-form-item>
        <ht-form-item
          label="步长"
          label-width="120px"
          class="identity-item-bottom identity-input-width"
        >
          <ht-input
            :validate="'required: true|regex:^[0-9]*$,只能输入数字'"
            v-model="identity.step"
            placeholder="请输入步长"
          />
        </ht-form-item>
        <ht-form-item label label-width="120px" class="identity-item-bottom">
          <ul>
            <li>
              流水号每次递加的数字，默认步长为1。比如步长为2，则每次获取流水号则在原来的基础上加2。
            </li>
          </ul>
        </ht-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <ht-submit-button
          v-show="!identityDisabled"
          :url="saveIdentityUrl()"
          :model="identity"
          :is-submit="isSubmit"
          :request-method="requestMethod"
          scope-name="editIdentityForm"
          @before-save-data="beforeSaveData"
          @after-save-data="afterSaveData"
          >{{ $t("eip.common.save") }}</ht-submit-button
        >
        <el-button @click="dialogCancle('dialogVisible')">{{
          $t("eip.common.cancel")
        }}</el-button>
      </div>
    </el-dialog>
    <!-- 加载数据 用作编辑流水号和查看流水号-->
    <ht-load-data
      :url="loadDataUrl"
      context="portal"
      @after-load-data="afterLoadData"
    ></ht-load-data>
  </div>
</template>
<script>
import sys from "@/api/portal.js";
export default {
  name: "meta-identity",
  components: {},
  data() {
    return {
      identityDisabled: false,
      identiyTitle: "",
      loadDataUrl: "",
      dialogVisible: false,
      data: [],
      pageResult: {
        page: 1,
        pageSize: 50,
        total: 0
      },
      isSubmit: true,
      identity: {
        name: "",
        alias: "",
        regulation: "",
        genType: "",
        noLength: "",
        initValue: "",
        step: ""
      },
      genTypes: [{ key: 3, value: "每年生成"}, { key: 2, value: "每月生成"}, { key: 1, value: "每天生成 " }, { key: 0, value: "递增" }]
    };
  },
  computed: {
    requestMethod: function() {
      return "POST";
    },
    identityDeleteUrl: function() {
      return window.context.portal + "/sys/identity/v1/remove";
    }
  },
  methods: {
    beforeSaveData() {
      this.isSubmit = true;
    },
    saveIdentityUrl: function() {
      return window.context.portal + "/sys/identity/v1/save";
    },
    afterSaveData() {
      this.dialogVisible = false;
      this.$refs.identityTable.load();
    },
    afterLoadData(data) {
      // 编辑流水号
      if (this.dialogVisible) {
        this.identity = data;
        setTimeout(() => this.$validator.validateAll("editIdentityForm"));
      }
    },
    dialogCancle(dialogVisible) {
      this.loadDataUrl = "";
      this[dialogVisible] = false;
      setTimeout(() => (this.identityDisabled = false), 500);
    },
    handleClose() {
      this.loadDataUrl = "";
      this.dialogVisible = false;
      setTimeout(() => (this.identityDisabled = false), 500);
    },
    showDialog(row) {
      this.dialogVisible = true;
      if (row) {
        this.loadDataUrl = `/sys/identity/v1/getJson?id=${row.id}`;
      }
    },
    handleNodeClick(node) {},
    loadData(param, cb) {
      sys
        .getIdentityPageJson(param)
        .then(response => {
          this.data = response.rows;
          this.pageResult = {
            page: response.page,
            pageSize: response.pageSize,
            total: response.total
          };
        })
        .finally(() => cb());
    },
    handleCommand(params) {
      switch (params.command) {
        case "edit":
          this.identiyTitle = "编辑流水号";
          this.showDialog(params.row);
          break;
        case "add":
          this.identity = {
            name: "",
            alias: "",
            regulation: "",
            genType: "",
            noLength: "",
            initValue: "",
            step: ""
          };
          this.identiyTitle = "添加流水号";
          this.showDialog();
          break;
        case "get":
          this.identiyTitle = "查看流水号";
          this.isSubmit = false;
          this.identityDisabled = true;
          this.showDialog(params.row);
          break;
        default:
          break;
      }
    }
  }
};
</script>
<style scoped>
ul {
  padding: 0px;
  margin: 0px;
}

li {
  list-style-type: none;
}

.identity-item-bottom {
  margin-bottom: 5px;
}

.identity-input-width .inputs {
  width: 100%;
}
</style>
