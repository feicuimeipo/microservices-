/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmcube.api.conf.feign.FeignConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.nianxi.api.feign.dto.bpm.BoDataModifyRecordDTO;
import org.nianxi.api.feign.dto.bpm.BpmBusLinkDTO;
import org.nianxi.api.feign.dto.bpm.StartFlowParamObjectDTO;
import org.nianxi.api.feign.dto.bpm.StartResultDTO;
import org.nianxi.api.feign.fallback.BpmServiceApiFallbackFactory;
import org.nianxi.api.model.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.nianxi.api.feign.BpmServiceApi.AppName;



@FeignClient(name= "${remote.feign.bpm-service.name}",url="${remote.feign.bpm-service.url}",contextId = "${remote.feign.bpm-service.name}",fallbackFactory= BpmServiceApiFallbackFactory.class ,  configuration= FeignConfig.class)
public interface BpmServiceApi {

	public static final String AppName ="bpm-service";

	/**
	 * 获取常用语
	 * @param defKey
	 * @param typeId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/feign/flow/approvalItem/v1/getApprovalByDefKeyAndTypeId",method=RequestMethod.GET, produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="获取常用语")
	public List<String> getApprovalByDefKeyAndTypeId(
			@Schema(name="defKey",description="流程定义key", required = true) @RequestParam(value = "defKey", required = true) String defKey,
			@Schema(name="typeId",description="流程分类id", required = true)  @RequestParam(value = "typeId", required = true) Optional<String> typeId,
			@Schema(name="userId",description="当前用户id") @RequestParam(name = "userId", required = true) Optional<String> userId);

	/**
	 * 执行催办任务
	 */
	@RequestMapping(value="/feign/flow/bpmTaskReminder/v1/executeTaskReminderJob",method=RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="执行催办任务")
	public CommonResult<String> executeTaskReminderJob();

	/**
	 * 检测bo是否已绑定流程
	 */
	@ApiResponse(description="检测bo是否已绑定流程")
	@RequestMapping(value="/feign/flow/def/v1/isBoBindFlowCheck",method=RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	public CommonResult<Boolean> isBoBindFlowCheck(@Schema(name="boCode",description="bo定义别名", required = true)  @RequestParam(value = "boCode", required = true) String boCode, @RequestParam(value = "formKey", required = true) String formKey);

	/**
	 * 获取我的常用流程key
	 */
	@RequestMapping(value="/feign/bpmModel/BpmOftenFlow/v1/getMyOftenFlowKey",method=RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description = "获取我的常用流程key")
	public Set<String> getMyOftenFlowKey();

	@RequestMapping(value = "/feign/flow/def/v1/bpmDefinitionData",method=RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description = "获取流程")
	public List<Map<String, String>> bpmDefinitionData(@RequestParam(value = "alias", required = true) String alias);


	//RuntimeApi
	@RequestMapping(value = "/feign/runtime/instance/v1/start", method = RequestMethod.POST,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="客户端启动流程")
	public StartResultDTO start(@Schema(name = "startFlowParamObject", description = "流程启动参数", required = true)  @RequestBody StartFlowParamObjectDTO dto);

	@RequestMapping(value = "/feign/runtime/instance/v1/getBusLink", method = RequestMethod.POST,produces = { "application/json; charset=utf-8" })
	@ApiResponse( description="根据流程实例获取关联数据")
	public List<String> getBusLink(@Schema(name = "params", required = true) @RequestBody ObjectNode startFlowParamObject) ;

	@RequestMapping(value = "/feign/runtime/instance/v1/getByBusinesKey", method = RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="获取流程实例与业务数据关系")
	public BpmBusLinkDTO getByBusinesKey(
			@Schema(name = "businessKey",description="业务数据id", required = true) @RequestParam(value = "businessKey", required = true) String businessKey,
			@Schema(name = "formIdentity", description = "表单标识") @RequestParam(value = "formIdentity") String formIdentity,
			@Schema(name = "isNumber",description="是否数字类型", required = true) @RequestParam(value = "isNumber", required = true) Boolean isNumber) ;

	@RequestMapping(value = "/feign/runtime/instance/v1/getDataByDefId", method = RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="根据流程定义id获取bo数据")
	public List<ObjectNode> getDataByDefId(@Schema(name = "defId",description="流程定义id") @RequestParam(value = "defId", required = true) String defId) ;

	//@RequestMapping(value = "/runtime/instance/v1/getDataByInst", method = RequestMethod.GET)
	//public List<ObjectNode> getDataByInst(@RequestParam(value = "instId", required = true) String instId) ;

	@RequestMapping(value = "/feign/runtime/instance/v1/isSynchronize", method = RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="根据流程实例ID,任务节点,审批状态验证审批记录")
	public Boolean isSynchronize(
			@Schema(name = "instId", required = false) @RequestParam(value = "instId", required = true) String instId,
			@Schema(name = "nodeIds", required = false)  @RequestParam(value = "nodeIds", required = true) String nodeIds,
			@Schema(name = "status", required = false) @RequestParam(value = "status", required = true) String status,
			@Schema(name = "lastStatus", required = false)  @RequestParam(value = "lastStatus", required = true) String lastStatus,
			@Schema(name = "lastNodeIds", required = false)  @RequestParam(value = "lastNodeIds", required = true) String lastNodeIds) ;

	//@RequestMapping(value = "/runtime/instance/v1/getFlowFieldList", method = RequestMethod.POST)
	//public List<Map<String,Object>> getFlowFieldList(@RequestBody QueryFilter queryFilter) ;
	@RequestMapping(value = "/feign/runtime/instance/v1/getFlowFieldListByJsonNode", method = RequestMethod.POST,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="获取流程字段信息")
	public List<Map<String,Object>> getFlowFieldListByJsonNode(@Schema(required = true, name = "queryFilter",description="查询参数对象") @RequestBody JsonNode queryFilter) ;


	@RequestMapping(value="/feign/runtime/instance/v1/getSubDataSqlByFk",method=RequestMethod.POST,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="根据外键获取子表数据sql")
	public CommonResult<String> getSubDataSqlByFk(
			@Schema(name="boEnt",description="bo实体") @RequestBody(required = true) ObjectNode boEnt,
			@Schema(name="fkValue",description="外键值") @RequestParam(value = "fkValue", required = true) Object fkValue,
			@Schema(name="defId",description="定义id") @RequestParam(value = "defId", required = false) String defId,
			@Schema(name="nodeId",description="节点id") @RequestParam(value = "nodeId", required = false) String nodeId,
			@Schema(name="parentDefKey",description="父流程key") @RequestParam(value = "parentDefKey", required = false) String parentDefKey) ;

	@RequestMapping(value = "/feign/bpm/bpmAutoStartConf/v1/defAutoStart", method = RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="批量启动流程")
	public ObjectNode defAutoStart() ;

	//获取节点表单
	@RequestMapping(value="/feign/runtime/instance/v1/printBoAndFormKey",method=RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	public ObjectNode printBoAndFormKey(@Schema(required = false, name = "defId",description="流程定义Id") @RequestParam(value = "defId", required = false) String defId,
										@Schema(required = false, name = "nodeId",description="节点Id") @RequestParam(value = "nodeId", required = false) String nodeId,
										@Schema(required = false, name = "procInstId",description="流程实例Id") @RequestParam(value = "procInstId", required = false) String procInstId) ;

	@RequestMapping(value="/feign/bpm/boDataModifyRecord/v1/handleBoDateModify",method=RequestMethod.POST,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="添加修改记录")
	public CommonResult<String> handleBoDateModify(@Schema(name="params",description="修改记录数据", required = true) @RequestBody Map<String, Object> params) ;

	@RequestMapping(value = "/feign/bpm/boDataModifyRecord/v1/getJson", method = RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description = "流程表单数据修改记录数据详情")
	public BoDataModifyRecordDTO getModifyById(@Schema(name="id",description="业务对象主键", required = true) @RequestParam(value = "id", required = true) String id) ;

	@RequestMapping(value = "/feign/runtime/task/v1/getTaskListByTenantId", method = RequestMethod.GET,produces = { "application/json; charset=utf-8" })
	@ApiResponse(description="根据租户id获取任务列表")
	public List<ObjectNode> getTaskListByTenantId(@Schema(name="tenantId",description="租户id", required = true) @RequestParam(value = "tenantId", required = true) String tenantId);
}
