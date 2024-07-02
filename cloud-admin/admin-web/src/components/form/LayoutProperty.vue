<template>
  <div style="margin: 15px">
    <template v-if="data && data.ctrlType == 'grid'">
      <ht-form-item label="栅格间隔" label-width="65px">
        <ht-input
          type="number"
          v-model="data.options.gutter"
          size="mini"
          width="156px"
        >
          <template slot="append">
            <span>px</span>
          </template>
        </ht-input>
      </ht-form-item>
      <ht-form-item label-width="65px">
        <template slot="label">
          <el-tooltip
            content="一行被切分为24分栏，通过列配置可以将一行划分为多列，每一列指定包含几个分栏。"
          >
            <i class="property-tip icon-question" />
          </el-tooltip>
          <span>列配置</span>
        </template>
        <draggable
          tag="ul"
          class="column-ul"
          :list="data.columns"
          v-bind="{
            group: { name: 'options' },
            ghostClass: 'ghost',
            handle: '.drag-item'
          }"
          handle=".drag-item"
        >
          <li v-for="(item, index) in data.columns" :key="index">
            <i class="drag-item icon-draggable"></i>
            <ht-input
              size="mini"
              width="130px"
              :min="0"
              :max="24"
              type="number"
              v-model.number="item.span"
            />
            &nbsp;&nbsp;<el-button
              type="text"
              @click="handleGridColumnStyle(index)"
              >样式</el-button
            >
            <el-button
              @click="handleGridColumnRemove(index)"
              circle
              plain
              class="grid-remove-button"
              type="danger"
              size="mini"
              icon="el-icon-minus"
            ></el-button>
          </li>
        </draggable>
        <div style="margin-left: 22px;">
          <el-button type="text" @click="handleGridColumnAdd"
            >添加一列</el-button
          >
        </div>
      </ht-form-item>
      <ht-form-item label="横向排列" label-width="65px">
        <ht-select
          v-model="data.options.justify"
          :options="[
            { key: 'start', value: '左对齐' },
            { key: 'end', value: '右对齐' },
            { key: 'center', value: '居中' },
            { key: 'space-around', value: '两侧间隔相等' },
            { key: 'space-between', value: '两端对齐' }
          ]"
        />
      </ht-form-item>
      <ht-form-item label="纵向排列" label-width="65px">
        <ht-select
          v-model="data.options.align"
          :options="[
            { key: 'top', value: '顶部对齐' },
            { key: 'middle', value: '居中' },
            { key: 'bottom', value: '底部对齐' }
          ]"
        />
      </ht-form-item>
    </template>

    <!--table选项卡配置-->
    <template v-else-if="data && data.ctrlType == 'tab'">
      <ht-form-item label-width label="风格类型">
        <el-radio-group v-model="data.options.type">
          <el-radio-button label>默认</el-radio-button>
          <el-radio-button label="card">选项卡</el-radio-button>
          <el-radio-button label="border-card">卡片化</el-radio-button>
        </el-radio-group>
      </ht-form-item>
      <ht-form-item label-width label="选项卡位置">
        <el-radio-group v-model="data.options.align">
          <el-radio-button label="top">顶部</el-radio-button>
          <el-radio-button label="left">左侧</el-radio-button>
          <el-radio-button label="right">右侧</el-radio-button>
          <el-radio-button label="bottom">底部</el-radio-button>
        </el-radio-group>
      </ht-form-item>
      <ht-form-item label-width>
        <ht-checkbox
          v-model="data.options.nextCheck"
          :options="[{ key: 'true', value: '页签、切换前是否校验当前页' }]"
        />
      </ht-form-item>
      <ht-form-item label-width label="标签配置项">
        <draggable
          tag="ul"
          class="column-ul"
          :list="data.columns"
          v-bind="{
            group: { name: 'options' },
            ghostClass: 'ghost',
            handle: '.drag-item'
          }"
          handle=".drag-item"
        >
          <li v-for="(item, index) in data.columns" :key="index">
            <i class="drag-item icon-draggable"></i>
            <ht-input size="mini" width="130px" v-model="item.span" />
            <el-button
              @click="handleGridColumnRemove(index)"
              circle
              plain
              class="grid-remove-button"
              type="danger"
              size="mini"
              icon="el-icon-minus"
            ></el-button>
          </li>
        </draggable>
        <div style="margin-left: 22px;">
          <el-button type="text" @click="handleGridColumnAdd('tab')"
            >添加标签</el-button
          >
        </div>
      </ht-form-item>
    </template>

    <!--分页配置-->

    <template v-else-if="data && data.ctrlType == 'page'">
      <h3>无内容</h3>
    </template>
    <!--分页步骤条-->
    <template v-else-if="data && data.ctrlType == 'pageSteps'">
      <ht-form-item label-width label="是否显示分页步骤条">
        <el-switch
          v-model="data.isShow"
          active-color="#13ce66"
          inactive-color="#ff4949"
        ></el-switch>
      </ht-form-item>
      <ht-form-item label-width label="分页步骤名配置">
        <li v-for="(item, index) in data.pageStepsArr" :key="index">
          <i class="drag-item icon-draggable"></i>
          <ht-input size="mini" width="130px" v-model="item.name" />
        </li>
      </ht-form-item>
    </template>

    <!--分页按钮配置-->
    <template v-else-if="data && data.ctrlType == 'pageButton'">
      <h1>分页按钮配置:</h1>
      <h3>下一步按钮配置:</h3>
      <ht-form-item label-width label="按钮名">
        <ht-input v-model="data.nextButton.name" />
      </ht-form-item>

      <ht-form-item label-width label="按钮图标">
        <div
          style="float: right;
  margin-right: 15px;"
        >
          <IconDialog @selected="pegeNextIcon" />
        </div>
      </ht-form-item>
      <ht-form-item label-width label="按钮颜色">
        <div style="height: 0px;margin-top:10px;margin-bottom:20px;">
          <el-color-picker
            v-model="data.nextButton.color"
            size="mini"
          ></el-color-picker>
        </div>
      </ht-form-item>
      <!-- <ht-form-item label-width label="按钮前置脚本">
        <ht-input v-model="data.nextButton.preScript" />
        <el-button class="btn-padding" icon="el-icon-plus" @click="autoRunJSScript(1,true)">设置自定义脚本</el-button>
      </ht-form-item>
      <ht-form-item label-width label="按钮后置脚本">
        <ht-input v-model="data.nextButton.rearScript" />
      </ht-form-item>-->
      <h3>上一步按钮配置:</h3>
      <ht-form-item label-width label="按钮名">
        <ht-input v-model="data.backButton.name" />
      </ht-form-item>

      <ht-form-item label-width label="按钮图标">
        <div
          style="float: right;
  margin-right: 15px;"
        >
          <IconDialog @selected="pegeBackIcon" />
        </div>
      </ht-form-item>
      <ht-form-item label-width label="按钮颜色">
        <div style="height: 0px;margin-top:10px;margin-bottom:20px;">
          <el-color-picker
            v-model="data.backButton.color"
            size="mini"
          ></el-color-picker>
        </div>
      </ht-form-item>
      <el-dialog
        title="按钮脚本"
        :visible.sync="pageBtnScriptDialog"
        append-to-body
        class="urgent-text"
        :before-close="pageBtnScriptDialogOk"
        :close-on-click-modal="false"
        width="600px"
      >
        <el-row style="height:100%">
          <textarea
            v-model="pageBtnScriptBase"
            type="textarea"
            rows="30"
            autocomplete="off"
            ref="scriptText"
            style="width: 99%;height:100%;"
          ></textarea>
        </el-row>
        <div slot="footer" class="dialog-footer">
          <el-button @click="pageBtnScriptDialogClose">取 消</el-button>
          <el-button type="primary" @click="pageBtnScriptDialogOk"
            >确 定</el-button
          >
        </div>
      </el-dialog>
    </template>

    <!--折叠面板配置-->
    <template v-else-if="data && data.ctrlType == 'accordion'">
      <ht-form-item label-width label="折叠面板配置">
        <!--<ht-checkbox v-model="data.options.accordion" :options="[{key: 'true', value: '是否手风琴模式'}]" />-->
        <ht-checkbox
          v-model="data.options.nextCheck"
          :options="[{ key: 'true', value: '面板、折叠前是否校验当前面板' }]"
        />
      </ht-form-item>

      <ht-form-item label-width label="面板配置">
        <draggable
          tag="ul"
          class="column-ul"
          :list="data.columns"
          v-bind="{
            group: { name: 'options' },
            ghostClass: 'ghost',
            handle: '.drag-item'
          }"
          handle=".drag-item"
        >
          <li v-for="(item, index) in data.columns" :key="index">
            <i class="drag-item icon-draggable"></i>
            <ht-input size="mini" width="130px" v-model="item.span" />
            <el-switch
              v-model="item.isOpen"
              @change="switchChange(item)"
              active-text="是否展开"
              inactive-value="false"
              active-value="true"
              style="margin-left: 10px"
            ></el-switch>
            <el-button
              @click="handleGridColumnRemove(index)"
              style="margin-left: 10px"
              circle
              plain
              class="grid-remove-button"
              type="danger"
              size="mini"
              icon="el-icon-minus"
            ></el-button>
          </li>
        </draggable>
        <div style="margin-left: 22px;">
          <el-button type="text" @click="handleGridColumnAdd('collapse')"
            >添加面板</el-button
          >
        </div>
      </ht-form-item>
    </template>

    <!--子表配置-->
    <template
      v-else-if="
        data &&  (data.ctrlType == 'subtable' || data.ctrlType == 'subDiv' || data.ctrlType == 'hottable')
      "
    >
      <ht-form-item label-width label="绑定子表">
        <ht-select
          filterable
          v-model="data.options.boSubEntity"
          :options="subTables"
          :props="{ key: 'name', value: 'desc' }"
          @change="subTableChange"
        ></ht-select>
      </ht-form-item>
      <ht-form-item label-width label="子表标题">
        <ht-input v-model="data.desc" style="width: 215px">
          <el-button
            icon="el-icon-search"
            slot="append"
            @click="editI18nMessage"
            >国际化</el-button
          >
        </ht-input>
      </ht-form-item>
      <el-button icon="icon-technology" v-if="data.ctrlType != 'hottable'" size="mini" @click="includdingFile"
        >自定义表头</el-button
      >
      <el-button v-if="data.ctrlType != 'hottable'" @click="addRelation">添加关联关系</el-button>
      <el-row class="hottable_btn_row" v-if="data.ctrlType == 'hottable'">
        <el-button v-if="data.ctrlType == 'hottable'" @click="setColHeader">设置表头</el-button>
        <el-button v-if="data.ctrlType == 'hottable'" @click="setCrossMapping">跨表取数</el-button>
        <el-button v-if="data.ctrlType == 'hottable'" @click="setNestedHeaders">嵌套表头</el-button>
      </el-row>
      <el-row class="hottable_btn_row" v-if="data.ctrlType == 'hottable'">
        <el-button v-if="data.ctrlType == 'hottable'" @click="setMainTableCalc">主表汇总</el-button>
      </el-row>
      <div style="margin-top:10px;margin-bottom:10px;">
        <el-tooltip
          content="需先配置自定义对话框，实现效果：配置子表回填后会在子表上显示配置的按钮，点击后弹出对话框，选择数据将已选数据回填到子表中。"
        >
          <i class="property-tip icon-question" />
        </el-tooltip>
        <el-checkbox v-model="data.subtableBackfill">子表回填</el-checkbox>
        <el-checkbox v-model="data.initTemplateData" v-if="data.subtableBackfill">数据初始化</el-checkbox>
      </div>
      <div style="margin-top:10px;margin-bottom:10px;" v-if="(data.subtableBackfill && data.initTemplateData) || data.ctrlType == 'hottable'">
        <el-tooltip
          content="当填单用户需要手动追加多份初始化回填数据时配置。"
        >
          <i class="property-tip icon-question" />
        </el-tooltip>
        <el-checkbox v-model="data.addInitTemplateData" v-if="data.subtableBackfill && data.initTemplateData">追加初始化</el-checkbox>
        <el-checkbox v-if="data.ctrlType == 'hottable'" v-model="data.initSumRow">初始化统计行</el-checkbox>
      </div>
      <ht-form-item v-if="data.initTemplateData" label-width="100px" label="初始化类型">
        <el-radio-group v-model="data.initTemplateDataType">
          <el-radio-button label="empty">数据为空</el-radio-button>
          <el-radio-button label="add">数据追加</el-radio-button>
          <el-radio-button label="cover">数据覆盖</el-radio-button>
        </el-radio-group>
      </ht-form-item>
      <ht-form-item v-if="data.initSumRow" label-width="120px" label="统计行标签字段">
        <ht-select
          v-model="data.options.initSumRowField"
          :options="
            subTables.filter(
              item => item.name === data.options.boSubEntity
            )
          "
          :props="{ key: 'name', value: 'desc' }"
          clearable
          filterable
        >
          <template slot-scope="{ options }">
            <el-option-group
              v-for="(group, idenx) in options"
              :key="idenx"
              :label="group.desc"
            >
              <el-option
                v-for="item in group.children"
                :key="item.id"
                :label="item.desc"
                :value="item.name"
              ></el-option>
            </el-option-group>
          </template>
        </ht-select>
      </ht-form-item>
      <ht-form-item v-if="data.initSumRow" label-width="100px" label="统计行标签值">
        <el-input v-model="data.options.initSumRowValue" placeholder="统计行标签字段值" />
      </ht-form-item>
      <ht-form-item v-if="data.initTemplateData && data.addInitTemplateData" label-width="100px" label="追加按钮名称">
        <el-input v-model="data.addInitBtnName" placeholder="追加初始化数据按钮名称" />
      </ht-form-item>
      <ht-load-data
        url="/form/customDialog/v1/getAll"
        requestMethod="post"
        context="form"
        @after-load-data="afterCustomDialogLoadData"
      ></ht-load-data>
      <ht-form-item
          v-if="data.ctrlType == 'hottable'"
          label-width="100px"
          class="customQuery-inputs custDialog-item"
        >
          <template slot="label">
            <el-tooltip content="可见区域百分比">
              <i class="property-tip icon-question" />
            </el-tooltip>
            <span>控件高度</span>
          </template>
          <ht-input size="mini" width="130px" :min="5" type="number" v-model="data.options.height" /> vh
        </ht-form-item>
      <div v-if="data.ctrlType == 'hottable' || data.ctrlType == 'subtable' || data.ctrlType == 'subDiv'" class="custDialog-div">
          <el-button
            class="custDialog-btn"
            icon="icon-list2"
            size="mini"
            @click="custOrgClick"
            >设置自动回填</el-button
          >
        </div>
      <span v-if="data.subtableBackfill">
        <div class="custDialog-div">
          <el-button
            class="custDialog-btn"
            v-if="data.customDialogjson.custDialog.alias"
            icon="icon-list2"
            size="mini"
            @click="custDialogClick"
            >设置返回值</el-button
          >
        </div>
        <ht-form-item
          label-width="100px"
          class="customQuery-inputs custDialog-item"
        >
          <template slot="label">
            <el-tooltip content="所选择的自定义查询作为选项时的绑定">
              <i class="property-tip icon-question" />
            </el-tooltip>
            <span>选择对话框</span>
          </template>
          <ht-select
            clearable
            filterable
            @change="changeCustDialog"
            v-model="data.customDialogjson.custDialog.alias"
            :options="customDialogs"
            :props="{ key: 'alias', value: 'name' }"
          />
        </ht-form-item>
        <!-- 参数绑定 -->
        <div class="basics-property"
            v-if="data.customDialogjson.custDialog.conditions
            && data.customDialogjson.custDialog.conditions.length > 0">
          <ht-form-item>
            <template slot="label">
              <el-tooltip content="自定义对话框需要的参数传入">
                <i class="property-tip icon-question" />
              </el-tooltip>
              <span>参数绑定</span>
            </template>
          </ht-form-item>
        </div>
        <table
          v-if="data.customDialogjson.custDialog.conditions
          && data.customDialogjson.custDialog.conditions.length > 0"
          class="form-table custDialog-table"
          cellspacing="0"
          cellpadding="0"
          border="0"
        >
          <tbody>
          <tr class="linkageTable-tr">
            <td width="100px;">参数名</td>
            <td>取值对象</td>
            <!-- <td>默认值</td> -->
          </tr>
          <tr
            class="linkageTable-tr"
            v-for="(condition, indexCondition) in data.customDialogjson
              .custDialog.conditions"
            :key="indexCondition"
            >
              <td>{{ condition.comment }}</td>
              <td>
                <ht-select
                  validate="required"
                  v-model="condition.bind"
                  :options="tablefields.filter(tab => tab.type == 'main')"
                  :props="{ key: 'name', value: 'desc' }"
                  :clearable="true"
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="group in options"
                      :key="group.name"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="'data.' + item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
            </tr>
          </tbody>
        </table>
        <ht-form-item
          label="按钮名称"
          label-width="100px"
          class="customQuery-inputs custDialog-item"
        >
          <ht-input v-model="data.customDialogjson.name"></ht-input>
        </ht-form-item>

        <div class="custDialog-div">
          <span>按钮图标</span>
          <div
            style="float: right;
  margin-right: 15px;"
          >
            <IconDialog @selected="icons" />
          </div>
          <i
            :class="this.data.customDialogjson.icon"
            style="float: right; line-height: 28px"
          ></i>
        </div>
      </span>
      <el-dialog
        title="设置子表对话框返回值"
        :visible.sync="dialogcustDialogVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="600px"
      >
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="150px">返回结果字段</td>
              <td width="220px">绑定显示的属性</td>
              <td width="80px">解除绑定</td>
            </tr>

            <tr
              class="linkageTable-tr"
              v-for="(field, index) in this.data.customDialogjson.resultField"
              :key="index"
            >
              <td>{{ field.comment }}</td>
              <td>
                <ht-select
                  v-model="custDialogprop[field.comment]"
                  :options="
                    subTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
              <td>
                <el-button
                  icon="el-icon-delete"
                  @click="unbind(field.comment)"
                ></el-button>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="custDialogOk">确 定</el-button>
          <el-button @click="dialogcustDialogVisible = false">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="自定义表头"
        :visible.sync="dialogincluddingFileVisible"
        append-to-body
        class="urgent-text"
        :before-close="includeFilesOk"
        :close-on-click-modal="false"
        width="900px"
      >
        <el-row style="height:100%">
          <el-col :span="13">
            <textarea
              v-model="data.customHeader"
              type="textarea"
              rows="30"
              autocomplete="off"
              ref="scriptText"
              style="width: 99%;height:100%;"
            ></textarea>
          </el-col>
          <el-col :span="10" :push="1">
            <el-card class="box-card">
              <div slot="header" class="clearfix">
                <span>使用说明</span>
              </div>
              <ol class="guide-ol">
                <li>可从
                  <code>引入脚本</code>处复制头部
                  <code>thead标签</code>中行
                  <code>tr标签</code>所有数据作为二级头部信息
                </li>
                <li>
                  参照复制的二级头部数据，进行
                  <code>th标签</code>的修改合并为一级头部
                </li>
                <li>
                  示例：
                  <el-input v-model="tableHeaderExample" :autosize="{ minRows: 2, maxRows: 7}" type="textarea"/>
                </li>
              </ol>
            </el-card>
          </el-col>
        </el-row>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="includeFilesOk">确 定</el-button>
          <el-button
            @click="
              dialogincluddingFileVisible = false;
              data.customHeader = '';
            "
            >取 消</el-button
          >
        </div>
      </el-dialog>
      <i18n-message-edit
        ref="i18nMessageEdit"
        :messageKey="i18nMessageKey"
        @after-save="afterSaveI18n"
      />
    </template>

    <template v-else-if="data && (data.ctrlType == 'suntable' || data.ctrlType == 'sunDiv')" >
      <ht-form-item label-width label="绑定孙表">
        <ht-select
          filterable
          v-model="data.options.boSubEntity"
          :options="sunTables"
          :props="{ key: 'name', value: 'desc' }"
          @change="subTableChange"
        ></ht-select>
      </ht-form-item>
      <ht-form-item label-width label="孙表标题">
        <ht-input v-model="data.desc" style="width: 215px">
          <el-button
            icon="el-icon-search"
            slot="append"
            @click="editI18nMessage"
            >国际化</el-button
          >
        </ht-input>
      </ht-form-item>
      <el-button icon="icon-technology" size="mini" @click="includdingFile"
        >自定义表头</el-button
      >
      <div style="margin-top:10px;margin-bottom:10px;">
        <el-checkbox v-model="data.subtableBackfill">孙表回填</el-checkbox>
        <el-tooltip
          content="需先配置自定义对话框，实现效果：配置孙表回填后会在孙表上显示配置的按钮，点击后弹出对话框，选择数据将已选数据回填到孙表中。"
        >
          <i class="property-tip icon-question" />
        </el-tooltip>
      </div>
      <ht-load-data
        url="/form/customDialog/v1/getAll"
        requestMethod="post"
        context="form"
        @after-load-data="afterCustomDialogLoadData"
      ></ht-load-data>
      <span v-if="data.subtableBackfill">
        <div class="custDialog-div">
          <el-button
            class="custDialog-btn"
            v-if="data.customDialogjson.custDialog.alias"
            icon="icon-list2"
            size="mini"
            @click="custDialogSunClick"
            >设置返回值</el-button
          >
        </div>

        <ht-form-item
          label-width="100px"
          class="customQuery-inputs custDialog-item"
        >
          <template slot="label">
            <el-tooltip content="所选择的自定义查询作为选项时的绑定">
              <i class="property-tip icon-question" />
            </el-tooltip>
            <span>选择对话框</span>
          </template>
          <ht-select
            clearable
            filterable
            @change="changeCustDialog"
            v-model="data.customDialogjson.custDialog.alias"
            :options="customDialogs"
            :props="{ key: 'alias', value: 'name' }"
          />
        </ht-form-item>

        <ht-form-item
          label="按钮名称"
          label-width="100px"
          class="customQuery-inputs custDialog-item"
        >
          <ht-input v-model="data.customDialogjson.name"></ht-input>
        </ht-form-item>

        <div class="custDialog-div">
          <span>按钮图标</span>
          <div
            style="float: right;
  margin-right: 15px;"
          >
            <IconDialog @selected="icons" />
          </div>
          <i
            :class="this.data.customDialogjson.icon"
            style="float: right; line-height: 28px"
          ></i>
        </div>
      </span>
      <el-dialog
        title="设置对话框返回值"
        :visible.sync="dialogcustDialogVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="600px"
      >
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="150px">返回结果字段</td>
              <td width="220px">绑定显示的属性</td>
              <td width="80px">解除绑定</td>
            </tr>

            <tr
              class="linkageTable-tr"
              v-for="(field, index) in this.data.customDialogjson.resultField"
              :key="index"
            >
              <td>{{ field.comment }}</td>
              <td>
                <ht-select
                  v-model="custDialogprop[field.comment]"
                  :options="
                    subTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
              <td>
                <el-button
                  icon="el-icon-delete"
                  @click="unbind(field.comment)"
                ></el-button>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="custDialogOk">确 定</el-button>
          <el-button @click="dialogcustDialogVisible = false">取 消</el-button>
        </div>
      </el-dialog>


      <i18n-message-edit
        ref="i18nMessageEdit"
        :messageKey="i18nMessageKey"
        @after-save="afterSaveI18n"
      />
    </template>
    <template v-else-if="data && data.ctrlType == 'dataView'">
      <div class="custDialog-div">
        <span>数据视图设置</span>
        <el-button
          class="custDialog-btn"
          icon="icon-list2"
          size="mini"
          v-if="data.templateObj.length !=0"
          @click="dataViewClick()"
        >设置参数绑定</el-button>
      </div>
      <ht-form-item label-width="120px" class="customQuery-inputs custDialog-item">
        <template slot="label">
          <el-tooltip content="所选择的数据报表作为选项时的绑定">
            <i class="property-tip icon-question" />
          </el-tooltip>
          <span>选择数据报表</span>
        </template>
        <eip-data-template-selector
            v-model="data.templateObj"
            placeholder="请选择数据报表"
            :single="true"
            v-if="templateShow"
        ></eip-data-template-selector>
      </ht-form-item>
      <!-- <div style="margin-top:10px;margin-bottom:10px;" v-if="templateObj.length !=0">
        <el-tooltip
          content="配置数据报表回填表单字段数据"
        >
          <i class="property-tip icon-question" />
        </el-tooltip>
        <el-checkbox  v-model="data.subtableBackfill">数据报表回填</el-checkbox>
        <span v-if="data.subtableBackfill">
        <div class="custDialog-div">
          <el-button
            class="custDialog-btn"
            icon="icon-list2"
            size="mini"
            @click="custDialogClick"
            >设置返回值</el-button
          >
        </div>
        </span>
      </div> -->

      <el-dialog
        title="设置数据报表回填"
        :visible.sync="dialogcustDialogVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="600px"
      >
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="150px">返回结果字段</td>
              <td width="220px">绑定表单的字段属性</td>
              <td width="80px">解除绑定</td>
            </tr>
            <tr
              class="linkageTable-tr"
              v-for="(field, index) in data.templateField"
              :key="index"
            >
              <td>{{ field.desc }}</td>
              <td>
                <ht-select
                  filterable
                  validate="required"
                  v-model="custDialogprop[field.name]"
                  :options="tablefields"
                  :props="{ key: 'name', value: 'desc' }"
                >
                  <template slot-scope="{ options, propKey, propValue }">
                    <el-option-group
                      v-for="group in options"
                      :key="group.name"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children.filter(obj => {
                          return filterFields(obj);
                        })"
                        :key="item[propKey]"
                        :label="item[propValue]"
                        :value="item[propKey]"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
              <td>
                <el-button
                  icon="el-icon-delete"
                  @click="unbind(field.name)"
                ></el-button>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="custDialogOk">确 定</el-button>
          <el-button @click="dialogcustDialogVisible = false;">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="设置数据视图参数"
        :visible.sync="dialogDataViewVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="600px"
      >

        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="150px">
                <el-tooltip content="绑定主键时，需要启动流程后才有值">
                  <i class="property-tip icon-question" />
                </el-tooltip>
                <span>关联查询字段</span>
              </td>
              <td>
                <ht-select
                  validate="required"
                  v-model="data.options.selectField"
                  :options="tablefields"
                  :props="{ key: 'name', value: 'desc' }"
                  :clearable="true"
                  filterable
                >
                  <template slot-scope="{ options, propKey, propValue }">
                    <el-option-group
                      v-for="group in options"
                      :key="group.name"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children.filter(obj => {
                          return filterFields(obj);
                        })"
                        :key="item[propKey]"
                        :label="item[propValue]"
                        :value="item[propKey]"
                      ></el-option>
                      <el-option :key="group.pkKey" label="主键" :value="group.pkKey"></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
              <td>
                <el-select v-model="data.options.bindSelectd" clearable placeholder="请选择">
                  <el-option
                    v-for="item in data.templateField"
                    :key="item.id"
                    :label="item.desc"
                    :value="item.name"
                  >
                  </el-option>
                </el-select>
              </td>
            </tr>
            <tr class="linkageTable-tr">
              <td width="150px">
                <el-tooltip content="绑定主键时，需要启动流程后才有值">
                  <i class="property-tip icon-question" />
                </el-tooltip>
                <span>关联填充字段</span>
              </td>
              <td>
                <ht-select
                  validate="required"
                  v-model="data.options.fillField"
                  :options="tablefields"
                  :props="{ key: 'name', value: 'desc' }"
                  :clearable="true"
                  filterable
                >
                  <template slot-scope="{ options, propKey, propValue }">
                    <el-option-group
                      v-for="group in options"
                      :key="group.name"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children.filter(obj => {
                          return filterFields(obj);
                        })"
                        :key="item[propKey]"
                        :label="item[propValue]"
                        :value="item[propKey]"
                      ></el-option>
                      <el-option :key="group.pkKey" label="主键" :value="group.pkKey"></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
              <td>
                <el-select v-model="data.options.bindFilld" clearable placeholder="请选择">
                  <el-option
                    v-for="item in data.templateField"
                    :key="item.id"
                    :label="item.desc"
                    :value="item.name"
                  >
                  </el-option>
                </el-select>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="confirmDataView()">确 定</el-button>
          <el-button @click="dialogDataViewVisible = false;">取 消</el-button>
        </div>
      </el-dialog>
      <el-dialog
        title="设置孙表对话框返回值"
        :visible.sync="dialogSunDialog"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="600px"
      >
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="150px">返回结果字段</td>
              <td width="220px">绑定显示的属性</td>
              <td width="80px">解除绑定</td>
            </tr>

            <tr
              class="linkageTable-tr"
              v-for="(field, index) in this.data.customDialogjson.resultField"
              :key="index"
            >
              <td>{{ field.comment }}</td>
              <td>
                <ht-select
                  v-model="custDialogprop[field.comment]"
                  :options="
                    sunTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
              <td>
                <el-button
                  icon="el-icon-delete"
                  @click="unbind(field.comment)"
                ></el-button>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="custDialogSunOk">确 定</el-button>
          <el-button @click="dialogSunDialog = false">取 消</el-button>
        </div>
      </el-dialog>
      <el-dialog
        title="自定义表头"
        :visible.sync="dialogincluddingFileVisible"
        append-to-body
        class="urgent-text"
        :before-close="includeFilesOk"
        :close-on-click-modal="false"
        width="600px"
      >
        <el-row style="height:100%">
          <textarea
            v-model="data.customHeader"
            type="textarea"
            rows="30"
            autocomplete="off"
            ref="scriptText"
            style="width: 99%;height:100%;"
          ></textarea>
        </el-row>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="includeFilesOk">确 定</el-button>
          <el-button
            @click="
              dialogincluddingFileVisible = false;
              data.customHeader = '';
            "
            >取 消</el-button
          >
        </div>
      </el-dialog>
    </template>
    <template v-else>
      <div class="property-empty">当前布局字段没有可配置属性</div>
    </template>
    <el-dialog
      title="设置栅格布局样式"
      :visible.sync="dialogStyleVisible"
      append-to-body
      class="urgent-text"
      :before-close="includeStyleNo"
      :close-on-click-modal="false"
      width="600px"
      ><span style="color: red; "
        >编辑的只能为Style样式内容，如：border: 1px solid #ccc;&nbsp;&nbsp;border-left: none;
        &nbsp;&nbsp;border-right: none;&nbsp;&nbsp;border-top: none;&nbsp;&nbsp;border-bottom: none;
    </span
      >
      <el-row style="height:100%">
        <codemirror
          v-model="style"
          :options="cmStyle"
          style="width: 99%;height:100%;"
        ></codemirror>
        <div style="width: 15%; float: left">
          <div>颜色：</div>
          <div class="block">
            <el-color-picker v-model="clickColor"></el-color-picker>
          </div>
        </div>
        <div style="width: 30%; float: left; margin-right: 25px">
          <div>类型：</div>
          <div class="block">
            <el-select v-model="selectBorderType" placeholder="请选择">
              <el-option v-for="item in borderType" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </div>
        </div>
        <div style="width: 30%; float: left">
          <div>可见性：</div>
          <el-select v-model="hiddenBorder" multiple collapse-tags placeholder="请选择">
            <el-option v-for="item in hiddenBorderType" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </div>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="includeStyleOk">确 定</el-button>
        <el-button @click="includeStyleNo">取 消</el-button>
      </div>
    </el-dialog>
    <el-dialog
      title="添加关联关系"
      :visible.sync="relationVisible"
      append-to-body
      width="600px">
      <el-form :inline="true" >
        <el-button type="primary" @click="add">添加</el-button>
      </el-form>
      <el-table
          style="width: 100%"
          :data="relations"
          border>
        <el-table-column type="selection" width="55"/>
        <el-table-column label="子表字段">
          <template slot-scope="scope">
            <el-select v-model="scope.row.key_">
              <el-option
                  v-for="result in data.list"
                  :key="result.key"
                  :label="result.name"
                  :value="result.name"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="value_" label="主表字段">
          <template slot-scope="scope">
            <el-select v-model="scope.row.value_">
              <el-option
                  v-for="result in tablefields[0].children"
                  :key="result.id"
                  :label="result.name"
                  :value="result.path+'.'+result.name"/>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-delete" @click="remove(scope.$index)"></el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submit">确定</el-button>
        <el-button @click="relationVisible=false">取消</el-button>
      </div>
    </el-dialog>
    <hot-table-field-dialog
      ref="hotTableFieldDialog"
      @after-save="hotTableFieldSave"
    />

    <el-dialog
        title="设置自动回填"
        :visible.sync="dialogcustOrgVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="600px"
      >
        <table v-if="data.customDialogjson && data.customDialogjson.orgConfig" class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="150px">单位名称</td>
              <td width="220px">
                  <ht-select
                  filterable
                  v-model="data.customDialogjson.orgConfig.name"
                  :options="
                    subTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
            </tr>
            <tr class="linkageTable-tr">
              <td width="150px">单位ID</td>
              <td width="220px">
                  <ht-select
                  v-model="data.customDialogjson.orgConfig.id"
                  :options="
                    subTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
            </tr>
            <tr class="linkageTable-tr">
              <td width="150px">单位编码</td>
              <td width="220px">
                  <ht-select
                  v-model="data.customDialogjson.orgConfig.code"
                  :options="
                    subTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
            </tr>
            <tr class="linkageTable-tr">
              <td width="150px">实例ID</td>
              <td width="220px">
                  <ht-select
                  v-model="data.customDialogjson.orgConfig.instId"
                  :options="
                    subTables.filter(
                      item => item.name === data.options.boSubEntity
                    )
                  "
                  :props="{ key: 'name', value: 'desc' }"
                  clearable
                  filterable
                >
                  <template slot-scope="{ options }">
                    <el-option-group
                      v-for="(group, idenx) in options"
                      :key="idenx"
                      :label="group.desc"
                    >
                      <el-option
                        v-for="item in group.children"
                        :key="item.id"
                        :label="item.desc"
                        :value="item.path + '.' + item.name"
                      ></el-option>
                    </el-option-group>
                  </template>
                </ht-select>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="custOrgDialogOk">确 定</el-button>
          <el-button @click="dialogcustOrgVisible = false">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="设置表头"
        :visible.sync="dialogHotHeaderDialogVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="1000px"
      >
        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
          <tbody>
            <tr class="linkageTable-tr">
              <td width="100px">字段别名</td>
              <td width="130px">字段名称</td>
              <td width="100px">首行汇总</td>
              <td width="420px">操作 <el-button size="mini" icon="el-icon-refresh" @click="initHotColHeaders()">初始化表头</el-button></td>
            </tr>

            <tr
              class="linkageTable-tr"
              v-for="(field, index) in this.data.options.colHeadersRelations"
              :key="index"
            >
              <td>{{ field.name }}</td>
              <td><ht-input v-model="field.desc" style="width: 175px" /></td>
              <td><el-checkbox v-model="field.isSum"></el-checkbox></td>
              <td>
                <el-button
                  icon="el-icon-edit"
                  @click="setHotColumType(field)"
                >控件</el-button>
                <el-button
                  icon="el-icon-edit"
                  @click="setHotMathExp(field)"
                >行内统计</el-button>
                <el-button
                  icon="el-icon-edit"
                  @click="setHotRowMathExp(field)"
                >跨行统计</el-button>
                <el-button
                  icon="el-icon-delete"
                  @click="hotHeaderRemove(field)"
                ></el-button>
                <el-button size="small"
                  icon="el-icon-arrow-up"
                  @click="up(index,data.options.colHeadersRelations)"
                ></el-button>
                <el-button
                   icon="el-icon-arrow-down"
                   @click="down(index,data.options.colHeadersRelations)"
                ></el-button>
              </td>
            </tr>
          </tbody>
        </table>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="hotHeaderDialogOk">确 定</el-button>
          <el-button @click="dialogHotHeaderDialogVisible = false">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="设置跨表取数"
        :visible.sync="dialogHotCrossMappingVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="1080px"
      >
        <el-container style="height:350px">
          <el-header style="height:30px;line-height:30px;background: #fafafa; font-weight: bold;font-size: 14px;">
            <el-button size="small" type="text" icon="el-icon-plus" @click="addHotCrossMapping()">添加</el-button>
          </el-header>
          <el-main style="padding:0 0 0 20px">
              <table class="form-table" cellspacing="0" cellpadding="0" border="0">
                <tbody>
                  <tr class="linkageTable-tr">
                    <td width="160px">取数字段</td>
                    <td width="90px">行号</td>
                    <td width="500px">取数源
                      <el-tooltip
                        content="如果是从多个字段取数，则是做求和运算"
                      >
                        <i class="property-tip icon-question" />
                      </el-tooltip>
                    </td>
                    <td width="220px">操作</td>
                  </tr>

                  <tr
                    class="linkageTable-tr"
                    v-for="(field, index) in this.data.options.crossMapping"
                    :key="index"
                  >
                    <td>
                      <ht-select
                        v-model="field.toField"
                        :options="
                          subTables.filter(
                            item => item.name === data.options.boSubEntity
                          )
                        "
                        :props="{ key: 'name', value: 'desc' }"
                        clearable
                        filterable
                      >
                        <template slot-scope="{ options }">
                          <el-option-group
                            v-for="(group, idenx) in options"
                            :key="idenx"
                            :label="group.desc"
                          >
                            <el-option
                              v-for="item in group.children"
                              :key="item.id"
                              :label="item.desc"
                              :value="item.path + '.' + item.name"
                            ></el-option>
                          </el-option-group>
                        </template>
                      </ht-select>
                    </td>
                    <td><ht-input size="mini" width="80px" type="number" v-model="field.toIndex" /></td>
                    <td>
                        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
                          <tbody>
                            <tr class="linkageTable-tr">
                              <td width="180px">源子表</td>
                              <td width="160px">源字段</td>
                              <td width="90px">行号</td>
                              <td width="220px">操作 <el-button  icon="el-icon-plus" @click="addHotCrossMappingField(field)"></el-button></td>
                            </tr>
                            <tr
                                class="linkageTable-tr"
                                v-for="(ffield, index) in field.fromFields"
                                :key="index"
                              >
                                  <td>
                                    <ht-select
                                      v-model="ffield.fromSubTabPath"
                                      :options="
                                        subTables.filter(
                                          item => item.name !== data.options.boSubEntity
                                        )
                                      "
                                      clearable
                                      filterable
                                      :props="{ key: 'path', value: 'desc' }"
                                    />
                                </td>
                                <td>
                                    <ht-select
                                      v-model="ffield.fromField"
                                      :options="
                                        subTables.filter(
                                          item => item.path === ffield.fromSubTabPath
                                        )
                                      "
                                      :props="{ key: 'name', value: 'desc' }"
                                      clearable
                                      filterable
                                    >
                                      <template slot-scope="{ options }">
                                        <el-option-group
                                          v-for="(group, idenx) in options"
                                          :key="idenx"
                                          :label="group.desc"
                                        >
                                          <el-option
                                            v-for="item in group.children"
                                            :key="item.id"
                                            :label="item.desc"
                                            :value="item.path + '.' + item.name"
                                          ></el-option>
                                        </el-option-group>
                                      </template>
                                    </ht-select>
                                </td>
                                <td><ht-input size="mini" width="80px" type="number" v-model="ffield.fromIndex" /></td>
                                <td>
                                  <el-button  icon="el-icon-plus" @click="addHotCrossMappingField(field)"></el-button>
                                  <el-button
                                    icon="el-icon-delete"
                                    @click="hotCrossMappingFiledRemove(field,ffield)"
                                  ></el-button>
                                </td>
                            </tr>
                          </tbody>
                        </table>
                    </td>
                    <td>
                      <el-button
                        icon="el-icon-delete"
                        @click="hotCrossMappingRemove(field)"
                      ></el-button>
                      <el-button size="small"
                        icon="el-icon-arrow-up"
                        @click="up(index,data.options.crossMapping)"
                      ></el-button>
                      <el-button
                        icon="el-icon-arrow-down"
                        @click="down(index,data.options.crossMapping)"
                      ></el-button>
                      <el-button
                        icon="el-icon-document-copy"
                        @click="data.options.crossMapping.push(JSON.parse(JSON.stringify(field)))"
                      ></el-button>
                    </td>
                  </tr>
                </tbody>
              </table>
          </el-main>
        </el-container>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="hotCrossMappingDialogOk">确 定</el-button>
          <el-button @click="dialogHotCrossMappingVisible = false">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="主表汇总"
        :visible.sync="dialogMainTableCalcVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="1080px"
      >
        <el-container style="height:350px">
          <el-header style="height:30px;line-height:30px;background: #fafafa; font-weight: bold;font-size: 14px;">
            <el-button size="small" type="text" icon="el-icon-plus" @click="addHotMainTableCalc()">添加</el-button>
          </el-header>
          <el-main style="padding:0 0 0 20px">
              <table class="form-table" cellspacing="0" cellpadding="0" border="0">
                <tbody>
                  <tr class="linkageTable-tr">
                    <td width="160px">名称</td>
                    <td width="160px">主表字段</td>
                    <td width="160px">求和字段</td>
                    <td width="220px">操作</td>
                  </tr>

                  <tr
                    class="linkageTable-tr"
                    v-for="(field, index) in this.data.options.mainTableCalcs"
                    :key="index"
                  >
                    <td><ht-input size="mini" width="130px" v-model="field.name" /></td>
                    <td>
                        <ht-select
                          v-model="field.mainField"
                          :options="boDefData.children"
                          :props="{ key: 'name', value: 'desc' }"
                          clearable
                          filterable
                        >
                          <template slot-scope="{ options }">
                            <el-option-group
                              v-for="(group, idenx) in options"
                              :key="idenx"
                              :label="group.desc"
                            >
                              <el-option
                                v-for="item in group.children"
                                v-show="item.nodeType=='field'"
                                :key="item.id"
                                :label="item.desc"
                                :value="item.path + '.' + item.name"
                              ></el-option>
                            </el-option-group>
                          </template>
                        </ht-select>
                    </td>
                    <td>
                      <ht-select
                        v-model="field.sumField"
                        :options="
                          subTables.filter(
                            item => item.name === data.options.boSubEntity
                          )
                        "
                        :props="{ key: 'name', value: 'desc' }"
                        clearable
                        filterable
                      >
                        <template slot-scope="{ options }">
                          <el-option-group
                            v-for="(group, idenx) in options"
                            :key="idenx"
                            :label="group.desc"
                          >
                            <el-option
                              v-for="item in group.children"
                              :key="item.id"
                              :label="item.desc"
                              :value="item.path + '.' + item.name"
                            ></el-option>
                          </el-option-group>
                        </template>
                      </ht-select>
                    </td>

                    <td>
                      <el-button
                        icon="el-icon-delete"
                        @click="hotMainTableCalcRemove(field)"
                      ></el-button>
                      <el-button size="small"
                        icon="el-icon-arrow-up"
                        @click="up(index,data.options.mainTableCalcs)"
                      ></el-button>
                      <el-button
                        icon="el-icon-arrow-down"
                        @click="down(index,data.options.mainTableCalcs)"
                      ></el-button>
                    </td>
                  </tr>
                </tbody>
              </table>
          </el-main>
        </el-container>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="hotMainTableCalcDialogOk">确 定</el-button>
          <el-button @click="dialogMainTableCalcVisible = false">取 消</el-button>
        </div>
      </el-dialog>

      <el-dialog
        title="设置嵌套表头"
        :visible.sync="dialogHotNestedHeadersVisible"
        append-to-body
        class="urgent-text"
        :close-on-click-modal="false"
        width="1200px"
      >
        <el-container style="height:350px">
          <el-header style="height:30px;line-height:30px;background: #fafafa; font-weight: bold;font-size: 14px;">
            <el-button size="small" type="text" icon="el-icon-plus" @click="addHotNestedHeaders()">添加</el-button>
          </el-header>
          <el-main style="padding:0 0 0 20px">
              <table class="form-table" cellspacing="0" cellpadding="0" border="0">
                <tbody>
                  <tr class="linkageTable-tr">
                    <td width="60px">序号</td>
                    <td>表头设置</td>
                    <td width="200px">操作</td>
                  </tr>

                  <tr
                    class="linkageTable-tr"
                    v-for="(field, index) in data.options.nestedHeaders"
                    :key="index"
                  >
                    <td>
                      {{index+1}}
                    </td>
                    <td>
                        <table class="form-table" cellspacing="0" cellpadding="0" border="0">
                            <tbody>
                              <tr class="linkageTable-tr">
                                <td width="120px">合并列名</td>
                                <td width="150px">开始列</td>
                                <td width="150px">结束列</td>
                                <td width="220px">操作</td>
                              </tr>
                              <tr
                                class="linkageTable-tr"
                                v-for="(header, index) in field.headers"
                                :key="index"
                              >
                                  <td><ht-input size="mini" type="text" v-model="header.name" /></td>
                                  <td>
                                      <ht-select
                                        v-model="header.startField"
                                        :options="data.options.colHeadersRelations"
                                        :props="{ key: 'name', value: 'desc' }"
                                        clearable
                                        filterable
                                      >
                                        <template slot-scope="{ options }">
                                          <el-option
                                            v-for="(item, cidenx) in options"
                                            v-show="cidenx!=0"
                                            :key="item.id"
                                            :label="item.desc"
                                            :value="item.name"
                                          ></el-option>
                                        </template>
                                      </ht-select>
                                  </td>
                                  <td>
                                      <ht-select
                                        v-model="header.endField"
                                        :options="data.options.colHeadersRelations"
                                        :props="{ key: 'name', value: 'desc' }"
                                        clearable
                                        filterable
                                      >
                                        <template slot-scope="{ options }">
                                          <el-option
                                            v-for="(item, cidenx) in options"
                                            v-show="cidenx!=0"
                                            :key="item.id"
                                            :label="item.desc"
                                            :value="item.name"
                                          ></el-option>
                                        </template>
                                      </ht-select>
                                  </td>
                                  <td>
                                    <el-button size="small"
                                      icon="el-icon-plus"
                                      @click="hotNestedHeadersItemAdd(field.headers)"
                                    ></el-button>
                                    <el-button
                                      icon="el-icon-delete"
                                      @click="hotNestedHeadersItemRemove(field.headers,header)"
                                    ></el-button>
                                    <el-button size="small"
                                      icon="el-icon-arrow-up"
                                      @click="up(index,field.headers)"
                                    ></el-button>
                                    <el-button
                                      icon="el-icon-arrow-down"
                                      @click="down(index,field.headers)"
                                    ></el-button>
                                  </td>
                              </tr>
                            </tbody>
                        </table>

                    </td>
                    <td>
                      <el-button
                        icon="el-icon-delete"
                        @click="hotNestedHeadersRemove(field)"
                      ></el-button>
                      <el-button size="small"
                        icon="el-icon-arrow-up"
                        @click="up(index,data.options.nestedHeaders)"
                      ></el-button>
                      <el-button
                        icon="el-icon-arrow-down"
                        @click="down(index,data.options.nestedHeaders)"
                      ></el-button>
                    </td>
                  </tr>
                </tbody>
              </table>
          </el-main>
        </el-container>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="hotNestedHeadersDialogOk">确 定</el-button>
          <el-button @click="dialogHotNestedHeadersVisible = false">取 消</el-button>
        </div>
      </el-dialog>

       <el-dialog
          title="统计函数"
          ref="hotMathDialog"
          :visible.sync="dialogHotCountVisible"
          destroy-on-close
          append-to-body
          :close-on-click-modal="false"
        >
          <math-dialog
            ref="mathDialog"
            :bo-def-data="boDefData"
            :visible.sync="dialogHotCountVisible"
          />
        </el-dialog>

      <hot-subDialog ref="hotSubDialog"
        :custdialog="data.customDialogjson"  >
      </hot-subDialog>

      <el-dialog
          title="跨行统计函数"
          ref="hotRowsMathDialog"
          :visible.sync="dialogHotRowsCountVisible"
          destroy-on-close
          append-to-body
          :close-on-click-modal="false"
        >
          <math-row-dialog
            ref="mathRowDialog"
            :bo-sub-entity="data.options.boSubEntity"
            :bo-def-data="boDefData"
            :sub-tables="subTables"
            :visible.sync="dialogHotRowsCountVisible"
          />
        </el-dialog>
  </div>
</template>
<script>
import Draggable from "vuedraggable";
import IconDialog from "@/components/dialog/IconDialog.vue";
import i18nMessageEdit from "@/components/system/I18nMessageEdit";
import HotTableFieldDialog from "@/components/form/HotTableFieldDialog";
import HotSubDialog from "@/components/form/HotSubDialog";
import MathDialog from "@/components/form/customView/MathDialog.vue";
import MathRowDialog from "@/components/form/customView/MathRowDialog.vue";
import { Base64 } from "js-base64";
import utils from "@/hotent-ui-util.js";
import form from "@/api/form.js";
const EipDataTemplateSelector = () => import("@/components/selector/EipDataTemplateSelector.vue");

export default {
  name: "layout-property",
  components: { Draggable, IconDialog, i18nMessageEdit, EipDataTemplateSelector,HotTableFieldDialog,HotSubDialog,MathDialog,MathRowDialog },
  props: ["data", "subTables", "sunTables", "tablefields", "boDefData"],
  data() {
    return {
      customDialogs: [], //对话框列表数据,
      dialogcustDialogVisible: false,
      dialogSunDialog:false,
      custDialogprop: {},
      dialogincluddingFileVisible: false,
      pageBtnScriptDialog: false,
      pageBtnScriptBase: "",
      i18nMessageKey: "",
      dialogStyleVisible: false, //编写栅格布局样式对话框
      // templateObj:[],
      // templateField:[],
      dialogDataViewVisible: false, //
      templateShow:false,
      style: "", //栅格布局样式
      styleIndex: "", //栅格布局样式下标
      cmStyle: {
        value: "",
        mode: "style",
        readOnly: false,
        smartIndent: true,
        tabSize: 2,
        theme: "base16-light",
        lineNumbers: true,
        line: true
      },
      relationVisible: false,
      relations:[],
      dialogHotHeaderDialogVisible:false,
      dialogHotCrossMappingVisible:false,
      dialogHotNestedHeadersVisible:false,
      dialogHotCountVisible:false,
      dialogHotRowsCountVisible:false,
      currentHotColumnField:{},
      dialogcustOrgVisible: false,
      dialogMainTableCalcVisible: false,
      currentMainTableCalcField: {},
      clickColor: "",
      selectBorderType: "",
      borderType: [
        {value:" border-style: solid; ", label:"————————————"},
        {value:" border-style: dotted; ", label:"●●●●●●●●●●●●●●●●●●●●"},
        {value:" border-style: dashed; ", label:"------------------------------"},
        {value:" border-style: double; ", label:"=================="},
        ],
      hiddenBorder: "",
      hiddenBorderType: [
        {value: " border-left: none; ", label: "隐藏左边框"},
        {value: " border-right: none; ", label: "隐藏右边框"},
        {value: " border-top: none; ", label: "隐藏上边框"},
        {value: " border-bottom: none; ", label: "隐藏下边框"}
      ],
      tableHeaderExample:"<tr class=\"sub-table-header\" @click=\"transitionIndex = -1\">\n" +
              "\n" +
              "    <th colspan=\"2\" style=\"min-width: 200px;font-weight:normal;color:#969799;;;\"><span>*</span>\n" +
              "        <span style=\"color:#969799;margin-left: 5px;\">一级表头名称</span>\n" +
              "    </th>\n" +
              "\n" +
              "</tr>\n" +
              "<tr class=\"sub-table-header\" @click=\"transitionIndex = -1\">\n" +
              "\n" +
              "    <th v-if=\"permission.fields.表名.字段名!='n'\" style=\"min-width: 200px;font-weight:normal;color:#969799;;;\"><span\n" +
              "            v-if=\"permission.fields.表名.字段名=='b'\">*</span>\n" +
              "        <span style=\"color:#969799;margin-left: 5px;\">默认显示字段名</span>\n" +
              "    </th>\n" +
              "    <th v-if=\"permission.fields.表名.字段名!='n'\" style=\"min-width: 200px;font-weight:normal;color:#969799;;;\"><span\n" +
              "            v-if=\"permission.fields.表名.字段名=='b'\">*</span>\n" +
              "        <span style=\"color:#969799;margin-left: 5px;\">默认显示字段名</span>\n" +
              "    </th>\n" +
              "\n" +
              "</tr>"
    };
  },
  mounted() {
    //回填显示数据视图控件
    if(this.data.ctrlType){
      if(this.data.templateId){
        if(this.data.templateId!=""){
          form.getBpmDataTemplateById(this.data.templateId,"").then(response => {
            this.data.templateObj = response.data;
            this.templateShow = true;
          });
        }
      }else{
        this.templateShow = true;
      }
    }
  },
  watch: {
    "data.templateObj" : function(newVal, oldVal) {
      if(JSON.stringify(newVal) !="{}"){
        //保存boDefAlias
        this.tablefields.forEach(item => {
          if(item.type == 'main'){
            this.data.options.boDefAlias = item.boDefAlias;
          }
        });
        this.data.templateKey = newVal.alias;
        this.data.templateId = newVal.id;
        let data = newVal.formField;
        if(data){
          this.data.templateField = JSON.parse(data);
        }
      }else{
        this.templateObj={};
        this.data.templateField = [];
        this.data.templateKey = "";
        this.data.templateId = "";
        this.data.options = {};
        this.data.options.showLabel = true;
      }
    },
    "data.ctrlType" :function(newVal, oldVal){
      if(newVal != oldVal){
        if(newVal == 'hottable'){
          if(!this.data.customDialogjson.orgConfig){
              this.data.customDialogjson.orgConfig = { name: '', code: '', id: '' };
          }
          if(!this.data.options.hasOwnProperty('initSumRowField')){
            this.data.options.initSumRowField = '';
          }
          if(!this.data.options.hasOwnProperty('initSumRowValue')){
            this.data.options.initSumRowValue = '';
          }
        }
      }
    }
  },
  methods: {
    filterFields(obj) {
      if(obj.nodeType == 'sub'){
        return false;
      } else {
        return true;
      }
    },
    //编写栅格布局样式后取消事件
    includeStyleNo() {
      this.style = "";
      this.styleIndex = "";
      this.clickColor = "";
      this.hiddenBorder = "";
      this.selectBorderType = "";
      this.dialogStyleVisible = false;
    },
    //编写栅格布局样式后确认事件
    includeStyleOk() {
      let color = "";
      if(this.clickColor){
        color = ' border-color: ' + this.clickColor + "; "
      }
      let hiddenBorder = "";
      if(this.hiddenBorder){
        this.hiddenBorder.forEach(v =>{
          hiddenBorder += v;
        })
      }
      if (this.style || color || this.selectBorderType || hiddenBorder) {
        this.data.columns[this.styleIndex].style = this.style + color + this.selectBorderType + hiddenBorder;
      }else{
        this.data.columns[this.styleIndex].style = "";
      }
      this.style = "";
      this.clickColor = "";
      this.hiddenBorder = "";
      this.selectBorderType = "";
      this.styleIndex = "";
      this.dialogStyleVisible = false;
    },
    //打开编写栅格布局样式对话框
    handleGridColumnStyle(index) {
      this.styleIndex = index;
      if (this.data.columns[index].style) {
        this.style = this.data.columns[index].style;
      } else {
        this.style = "";
      }
      this.dialogStyleVisible = true;
    },
    autoRunJSScript(type, isBnt) {
      this.BtnScriptType = type;
      let script;
      if (type == 1) {
        //下一步按钮的前置脚本
        script = this.data.options.nextBntjson.preScript;
      }
      this.pageBtnScriptDialog = true;
      if (!script) return;
      this.pageBtnScriptBase = Base64.decode(script);
    },
    pageBtnScriptDialogOk() {
      this.pageBtnScriptDialog = false;
    },
    pageBtnScriptDialogClose() {
      this.pageBtnScriptDialog = false;
    },
    pegeNextIcon(icon) {
      this.data.nextButton.icon = icon;
    },
    pegeBackIcon(icon) {
      this.data.backButton.icon = icon;
    },
    includeFilesOk() {
      this.dialogincluddingFileVisible = false;
      if (this.data.customHeader) {
        this.data.customHeader = Base64.encode(this.data.customHeader, "utf-8");
      }
    },
    includdingFile() {
      this.dialogincluddingFileVisible = true;
      this.data.customHeader = this.data.customHeader
        ? Base64.decode(this.data.customHeader, "utf-8")
        : "";
    },
    unbind(key) {
      for (var item in this.custDialogprop) {
        if (key == item) {
          this.$set(this.custDialogprop, key, "");
          delete this.custDialogprop[key];
        }
      }
    },
    editI18nMessage() {
      this.i18nMessageKey = this.data.options.subTablePath;
      this.$refs.i18nMessageEdit.handleOpen();
    },
    afterSaveI18n(data) {
      data.key = data.key.replace("$", "#");
      this.data.desc = data.key;
      this.data.desc_zh = data.desc;
    },
    // 子表对话框确定事件
    custDialogOk(isDialog) {
      //对话框返回值
      var mappingConf = [];
      for (var key in this.custDialogprop) {
        mappingConf.push({
          from: key,
          target: [this.custDialogprop[key]]
        });
      }
      this.data.customDialogjson.custDialog.mappingConf = mappingConf;
      this.dialogcustDialogVisible = false;
    },
    custOrgDialogOk(){
      this.dialogcustOrgVisible = false;
    },
    //打开设置子表对话框返回值
    custDialogClick() {
      this.dialogcustDialogVisible = true;
      this.custDialogprop = null;
      let tempGroup = {};
      var initConf = this.data.customDialogjson;
      if (initConf.custDialog.mappingConf) {
        for (var i = 0, c; (c = initConf.custDialog.mappingConf[i++]); ) {
          if (!c) continue;
          var target = c.target;
          if (target) target = target.join(",");
          tempGroup[c.from] = target;
        }
      }
      initConf.resultField.forEach(f => {
        if (!tempGroup[f.comment]) {
          tempGroup[f.comment] = "";
        }
      });
      this.custDialogprop = tempGroup;
    },
    custOrgClick(){
      if(!this.data.customDialogjson.orgConfig){
          this.data.customDialogjson.orgConfig = { name: '', code: '', id: '',instId:'' };
      }
      this.dialogcustOrgVisible = true;
    },
    initHotColHeaders(){
        let nowHeaders = this.data.options.colHeadersRelations;
        let children = [];
        this.subTables.forEach(sub =>{
          if(sub.name == this.data.options.boSubEntity){
              children = sub.children;
          }
        });
        if(children.length>0){
            for (let index = 0; index < children.length; index++) {
              let child = children[index];
              let isExist = false;
              nowHeaders.forEach(f =>{
                if(f.name == child.name){
                  isExist = true;
                }
              })
              if(!isExist){
                let columnPropertis = {data:child.name,readOnly:false,mergeCell:false,type:'text',source:[],dateFormat:'',mathExp:'',rowMathExp:[],width:90,style:{size:0,color:'',bold:false,background:'',diyStyle:''}};
                this.data.options.colHeadersRelations.push({name:child.name,desc:child.comment,column:columnPropertis,isSum:false});
              }
            }
        }
    },
    setColHeader() {
      this.dialogHotHeaderDialogVisible = true;
    },
    setCrossMapping(){
      if(!this.data.options.crossMapping){
        this.data.options.crossMapping = [{toField:'',toIndex:1,fromFields:[{fromSubTabPath:'',fromField:'',fromIndex:1}]}];
      }
      this.dialogHotCrossMappingVisible = true;
    },
    setNestedHeaders(){
      if(!this.data.options.nestedHeaders){
        this.data.options.nestedHeaders = [];
      }
      this.dialogHotNestedHeadersVisible = true;
    },
    setMainTableCalc(){
      if(!this.data.options.mainTableCalcs){
        this.data.options.mainTableCalcs = [];
      }
      this.dialogMainTableCalcVisible = true;
    },
    hotHeaderDialogOk(isDialog) {
      if(this.data.options.colHeadersRelations){
        let newColHeadersRelations = JSON.stringify(this.data.options.colHeadersRelations);
        this.data.options.colHeadersRelations = [];
        this.data.options.colHeadersRelations = JSON.parse(newColHeadersRelations);
      }
      this.dialogHotHeaderDialogVisible = false;
    },
    hotCrossMappingDialogOk(){
      this.dialogHotCrossMappingVisible = false;
    },
    hotMainTableCalcDialogOk(){
      this.dialogMainTableCalcVisible = false;
    },
    hotNestedHeadersDialogOk(){
      this.dialogHotNestedHeadersVisible = false;
    },
    addHotCrossMapping(){
      this.data.options.crossMapping.push({toField:'',toIndex:1,fromFields:[{fromSubTabPath:'',fromField:'',fromIndex:1}]});
    },
    addHotCrossMappingField(field){
      field.fromFields.push({fromSubTabPath:'',fromField:'',fromIndex:1});
    },
    addHotMainTableCalc(){
      this.data.options.mainTableCalcs.push({name:'',mainField:'',sumField:''});
    },
    addHotNestedHeaders(){
      this.data.options.nestedHeaders.push({headers:[{name:'',startField:'',endField:''}]});
    },
    hotHeaderRemove(item) {
      this.data.options.colHeadersRelations.remove(item);
    },
    hotCrossMappingRemove(item) {
      this.data.options.crossMapping.remove(item);
    },
    hotCrossMappingFiledRemove(field,item){
      field.fromFields.remove(item);
    },
    hotMainTableCalcRemove(item){
      this.data.options.mainTableCalcs.remove(item);
    },
    hotMainTableCalcEdit(item){
      this.currentHotColumnField = {};
      this.dialogHotCountVisible = true;
      let _this = this;
      setTimeout(function(){
        _this.$refs.mathDialog.mathExpAssign(item.script);
        _this.currentMainTableCalcField = item;
      },0);
    },
    hotNestedHeadersRemove(item) {
      this.data.options.nestedHeaders.remove(item);
    },
    hotNestedHeadersItemAdd(headers){
      headers.push({name:'',startField:'',endField:''});
    },
    hotNestedHeadersItemRemove(headers,item) {
      headers.remove(item);
    },
    setHotMathExp(fillField){
      this.currentMainTableCalcField = {};
      this.dialogHotCountVisible = true;
      let _this = this;
      setTimeout(function(){
        _this.$refs.mathDialog.mathExpAssign(fillField.column.mathExp);
        _this.currentHotColumnField = fillField;
      },0);
    },
    setHotRowMathExp(fillField){
      this.currentMainTableCalcField = {};
      this.dialogHotRowsCountVisible = true;
      let _this = this;
      setTimeout(function(){
        if(!fillField.column.rowMathExp){
          fillField.column.rowMathExp = [];
        }
        _this.$refs.mathRowDialog.mathExpAssign(fillField.column.rowMathExp);
        _this.currentHotColumnField = fillField;
      },0);
    },
    setHotColumMathExp(exp){
      if(Object.keys(this.currentMainTableCalcField)!=0){
        this.currentMainTableCalcField.script = exp;
      }else if(Object.keys(this.currentHotColumnField)!=0){
        this.currentHotColumnField.column.mathExp = exp;
      }
      this.$refs.mathDialog.mathExpAssign('');
    },
    setHotColumRowMathExp(exp){
      this.currentHotColumnField.column.rowMathExp = exp;
      this.$refs.mathRowDialog.mathExpAssign([]);
    },
    setHotColumType(item){
      if(item.column.mergeCell == '' || item.column.mergeCell == 'undefined'){
        item.column.mergeCell = false;
      }
      if(!item.column.width){
        item.column.width = 90;
      }
      if(!item.column.style){
        item.column.style = {size:0, color:'', bold:false, background:'', diyStyle:''};
      }
      this.$refs.hotTableFieldDialog.handleOpen(item);
    },
    hotTableFieldSave(field){

    },
    // 孙表对话框确定事件
    custDialogSunOk(isDialog) {
      //对话框返回值
      var mappingConf = [];
      for (var key in this.custDialogprop) {
        mappingConf.push({
          from: key,
          target: [this.custDialogprop[key]]
        });
      }
      this.data.customDialogjson.custDialog.mappingConf = mappingConf;
      this.dialogSunDialog = false;
    },
    //打开设置孙表对话框返回值
    custDialogSunClick(){
      debugger
      this.dialogSunDialog = true;
      this.custDialogprop = null;
      let tempGroup = {};
      var initConf = this.data.customDialogjson;
      if (initConf.custDialog.mappingConf) {
        for (var i = 0, c; (c = initConf.custDialog.mappingConf[i++]); ) {
          if (!c) continue;
          var target = c.target;
          if (target) target = target.join(",");
          tempGroup[c.from] = target;
        }
      }
      initConf.resultField.forEach(f => {
        if (!tempGroup[f.comment]) {
          tempGroup[f.comment] = "";
        }
      });
      this.custDialogprop = tempGroup;
    },
    //数据视图控件方法
    dataViewClick(){
      this.dialogDataViewVisible = true;
    },
    confirmDataView(){
      this.dialogDataViewVisible = false;
    },
    afterCustomDialogLoadData(data) {
      this.customDialogs = data;
    },
    icons(icon) {
      this.data.customDialogjson.icon = icon;
    },
    changeCustDialog() {
      this.custDialogprop = {};
      this.data.customDialogjson.custDialog.conditions = [];
      if (this.customDialogs.length == 0) return;
      if (this.data.customDialogjson.custDialog.alias == null) {
        this.data.customDialogjson.custDialog.conditions = [];
        return;
      }
      for (var i = 0, d; (d = this.customDialogs[i++]); ) {
        if (d.alias == this.data.customDialogjson.custDialog.alias) {
          var treeData;
          if (d.listDialog && d.listDialog.resultfield) {
            treeData = eval("(" + d.listDialog.resultfield + ")");
            this.data.customDialogjson.custDialog.type = "combiDialog";
          }
          if (d.resultfield) {
            treeData = eval("(" + d.resultfield + ")");
            this.data.customDialogjson.custDialog.type = "custDialog";
          }
          for (var q = 0, f; (f = treeData[q++]); ) {
            f.field = f.comment;
          }
          this.data.customDialogjson.resultField = treeData;
          var conditionList = eval("(" + d.conditionfield + ")");
          this.data.customDialogjson.custDialog.conditions = [];
          //只处理类型等于1的对话框参数（defaultType：1：用户输入，2：固定值 ，3：参数传入）
          if (conditionList && conditionList.length > 0) {
            for (var j = 0, c; (c = conditionList[j++]); ) {
              if (c.defaultType == "3") {
                var has = false;
                if (!has)
                  this.data.customDialogjson.custDialog.conditions.push(c);
              }
            }
          }
        }
      }
    },
    handleGridColumnRemove(index) {
      if (this.data.options.activeNames != undefined) {
        this.data.options.activeNames.remove(this.data.columns[index].idKey);
      }
      this.data.columns.splice(index, 1);
    },
    handleGridColumnAdd(type) {
      if (type == "tab") {
        this.data.columns.push({
          span: "标签页" + (this.data.columns.length + 1),
          list: []
        });
      } else if (type == "page") {
        this.data.columns.push({
          span: "分页" + (this.data.columns.length + 1),
          list: []
        });
      } else if (type == "collapse") {
        this.data.columns.push({
          span: "折叠面板" + (this.data.columns.length + 1),
          idKey: Date.parse(new Date()) + utils.getName(),
          isOpen: false,
          list: []
        });
      } else {
        this.data.columns.push({
          span: "",
          list: []
        });
      }
    },
    switchChange(item) {
      if (eval(item.isOpen)) {
        this.data.options.activeNames.push(item.idKey);
      } else {
        this.data.options.activeNames.remove(item.idKey);
      }
    },
    subTableChange(value, data) {
      this.data.name = data.name;
      this.data.list = [];
      this.data.desc = data.desc;
      this.data.options.subTablePath = data.path;
      if(data.show == '子实体'){
        this.data.options.subDivTablePath = data.path;
      }
      this.data.options.relation = data.relation;
      if(this.data.ctrlType=='hottable' && data.children){
        let fields = [];
        let list = [];
        for (let index = 0; index < data.children.length; index++) {
          let child = data.children[index];
          let columnPropertis = {data:child.name,readOnly:false,mergeCell:false,type:'text',source:[],dateFormat:'',mathExp:'',rowMathExp:[],width:90,style:{size:0,color:'',bold:false,background:'',diyStyle:''}};
          fields.push({name:child.name,desc:child.comment,column:columnPropertis});
          let listField = {options:{}};
          listField.boDefId = child.boDefId;
          listField.boAttrId = child.id;
          listField.fieldPath = child.path + "." + child.name;
          listField.name = child.name;
          listField.desc = child.desc;
          listField.title = child.desc;
          listField.entId = child.entId;
          listField.boDefAlias = child.boDefAlias;
          listField.tableName = child.tableName;
          listField.columnType = child.columnType;
          if (child.dataType == "date") {
            listField.options.format = child.format;
            listField.options.inputFormat = child.format;
          } else if (child.dataType == "number") {
            listField.options.maxDecimalDigits = child.decimalLen;
          }
          list.push(listField);
        }
        this.data.list = list;
        this.data.options.colHeadersRelations = fields;
      }
    },
    addRelation(){
      this.relationVisible = true;
      this.relations = [];
      this.data.list.forEach(item=>{
        if (item.options.mapping){
          this.relations.push({
            key_:item.name,
            value_:item.options.mapping
          })
        }
      })
    },
    add(){
      this.relations.push({
        key_:"",
        value_:""
      })
    },
    remove(index) {
      this.relations.splice(index, 1);
    },
    submit(){
      let flag = false;
      let map = new Map();
      this.relations.forEach(item=>{
        if (!item.key_ || !item.value_){
          flag = true;
        }
        map.set(item.key_,item.value_);
      });
      if (flag){
        this.$message("映射关系不能为空")
        return;
      }
      this.data.list.forEach(item=>{
        if (map.has(item.name)){
          item.options.mapping = map.get(item.name);
        }else{
          delete item.options.mapping;
        }
      });
      this.relationVisible = false;
    },
    initHotTableData(){
      this.$refs.hotSubDialog.showDialog();
    },
    hotTableInitData(initData){
      this.data.options.initFillbackData = initData;
    },
    up(index, data) {
      if (index === 0) {
        this.$message({
          message: "已经是列表中第一位",
          type: "warning"
        });
      } else {
        let temp = data[index - 1];
        Vue.set(data, index - 1, data[index]);
        Vue.set(data, index, temp);
      }
    },
    down(index, data) {
      if (index === data.length - 1) {
        this.$message({
          message: "已经是列表中最后一位",
          type: "warning"
        });
      } else {
        this.isTransition = true;
        let i = data[index + 1];
        Vue.set(data, index + 1, data[index]);
        Vue.set(data, index, i);
      }
    },
  }
};
</script>
<style lang="scss" scoped>
@import "@/assets/css/form-editor.scss";

>>> .el-dialog__body {
  padding: 10px !important;
  color: #606266;
  font-size: 14px;
  word-break: break-all;
}
.column-ul {
  list-style: none;
  padding: 0;
  margin: 4px 0 0 0;
}

.column-ul li {
  margin-bottom: 5px;
}

.column-ul .icon-draggable {
  font-weight: bold;
  font-size: 18px;
  cursor: move;
  margin-right: 8px;
}

.grid-remove-button {
  margin-left: 50px;
}

.ghost {
  background: #f56c6c;
  border: 2px solid #f56c6c;
  outline-width: 0;
  height: 3px;
  box-sizing: border-box;
  font-size: 0;
  content: "";
  overflow: hidden;
  padding: 0;
}

.property-empty {
  text-align: center;
  width: 100%;
  margin-top: 150px;
  font-size: 16px;
  color: #ccc;
}
.linkageTable-tr td {
  text-align: center;
}
.custDialog-div {
  height: 28px;
  line-height: 28px;
  margin-bottom: 3px;
  float: left;
}
.custDialog-div .custDialog-btn {
  float: right;
  margin-right: 15px;
}
.custDialog-table td {
  padding: 5px;
}
>>> .custDialog-item {
  margin-bottom: 5px;
}

.basics-property .el-form-item {
  margin-bottom: 0px;
}
.linkageTable-tr td {
  text-align: center;
}
.basics-property >>> .el-form-item {
  margin-bottom: 0px;
}
.basics-label >>> .el-form-item__label {
  text-align: center;
}
.hottable_btn_row{
  margin: 5px;
}
</style>
