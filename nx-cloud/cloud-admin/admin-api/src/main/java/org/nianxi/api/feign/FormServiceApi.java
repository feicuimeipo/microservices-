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
import com.pharmcube.api.model.page.PageList;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.nianxi.api.feign.dto.form.FormRestfulModelDTO;
import org.nianxi.api.feign.fallback.FormServiceApiFallbackFactory;
import org.nianxi.api.model.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.nianxi.api.feign.FormServiceApi.AppName;

/**
 * public interface AdminServiceApi {
 *
 * 	public static final String AppName ="admin-service";
 */
@FeignClient(name= "${remote.feign.form-service.name}",url="${remote.feign.form-service.url}",contextId = "${remote.feign.form-service.name}",fallbackFactory= FormServiceApiFallbackFactory.class, configuration= FeignConfig.class)
public interface FormServiceApi {

	public static final String AppName ="form-service";

	/**
	 * 调用form模块的restful接口。根据业务对象别名或id获取主BoEnt
	 * 
	 * @param alias
	 * @param defId
	 * @ 
	 * @returnBpmForm boDefService.getByName
	 * 返回 主BoEnt对象
	 */
	@ApiResponse(description="根据业务对象别名或id获取主BoEnt")
	@RequestMapping(value="/feign/form/formServiceController/v1/getMainBOEntByDefAliasOrId",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	public ObjectNode getMainBOEntByDefAliasOrId(@Schema(name="alias",description="业务对象别名") @RequestParam(value = "alias", required = true) String alias, @RequestParam(value = "defId", required = true) String defId) ;


	/**
	 * 根据别名获取bo数据的保存方式
	 * @param alias
	 * @return
	 */
	@ApiResponse(description="获取所有bo实体列表")
	@RequestMapping(value="/feign/bo/def/v1/getSupportDb",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	public boolean getSupportDb(@Schema(name="alias",description="bo别名", required = true)  @RequestParam(value = "alias", required = true) String alias) ;


	/**
     * 调用form模块接口处理bo数据
//     * @param id  boid。空为新增。不为空则更新
//     * @param defId bo定义id
//     * @param boData 业务数据
//     * @param saveType 保存类型。1，database 。2，boObject
     * @return
     * 需先根据 saveType用boInstanceFactory.getBySaveType(saveType);获取对应的handler。然后调用handler的handSaveData。返回List<BoResult>
     * @
     */
	@RequestMapping(value="/feign/form/formServiceController/v1/handlerBoData",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="处理bo数据,boid。空为新增。不为空则更新 ")
	public List<ObjectNode>  handlerBoData(@Schema(name="model",description="model")  @RequestBody(required = true) FormRestfulModelDTO param) ;

	/**
	 * 对应bodatahandler.getByBoDefCode
	 * @param saveMode 保存方式
	 * @param code  bocode
	 * @return 返回值不变
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getBodataByDefCode",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据别名获取BoData")
	public ObjectNode getBodataByDefCode(
			@Schema(name="saveMode",description="保存方式") @RequestParam(value = "saveMode", required = true) String saveMode,
			@Schema(name="code",description="别名") @RequestParam(value = "code", required = true) String code) ;


	/**
	 *  对应handler.getById
	 *  根据实例ID和bo定义code获取BODATA，只返回两层。
	 * 1.根据bodefCode获取bo定义。
	 * 2.根据bo定义获取数据。
//	 * @param saveMode
//	 * @param id
//	 * @param code
	 * @return 返回值不变
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getBodataById",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据实例ID和bo定义code获取BODATA，只返回两层 ")
	public ObjectNode getBodataById(@Schema(name="model",description="model") @RequestBody(required = true) FormRestfulModelDTO param) ;


	/**
	 * 调用form模块的restful接口。根据formkey获取表单
	 *
	 * @param formKey
	 * @
	 * @;//throws 
	 * @returnBpmForm  返回form对象
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getByFormKey",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据formkey获取表单")
	public ObjectNode getByFormKey(@Schema(name="formKey",description="key") String formKey) ;

	/**
	 *   根据formKey 导出表单
	 * @param string
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getFormExportXml",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据formKey 导出表单")
	public String getFormExportXml(@Schema(name="formKeys",description="key集合") @RequestBody(required = true) String string) ;

	/**
	 *   根据bodef获得导出用的xml文件
//	 * @param formKeys
	 * @return
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getBoDefExportXml",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据bodef获得导出用的xml文件")
	public String getBoDefExportXml(@Schema(name="bodef",description="bodefJson数据") ObjectNode bodef) ;;//throws JAXBException, IOException;

	/**
	 *   根据FormRight获得导出用的xml文件
	 * @return
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getFormRightExportXml",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据FormRight获得导出用的xml文件")
	public String getFormRightExportXml(@Schema(name="bpmFormRights",description="bpmFormRightsJson数据") @RequestBody(required = true) ObjectNode bodef); //throws JAXBException, IOException;

	/**
	 *   导入 bo
	 * @return
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/importBo",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="导入 bo")
	public CommonResult<String> importBo(@Schema(name="bodefXml",description="") @RequestParam(value = "bodefXml", required = true) String bodefXml) ;

	/**
	 *   导入bodef对象
	 * @param bos
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/importBoDef",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="导入bodef对象")
	public ObjectNode importBoDef(@RequestBody(required = true) List<ObjectNode> bos) ;

	/**
	 *   导入form
	 * @param formfXml
	 * @return
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/importForm",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="导入form")
	public CommonResult<String>  importForm(@Schema(name="formfXml",description="formfXml") @RequestBody(required = true) String formfXml) ;

	/**
	 *   导入formRigths
//	 * @param formfXml
	 * @return
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/importFormRights",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="导入formRigths")
	public CommonResult<String> importFormRights(@Schema(name="formRightsXml",description="formRightsXml") @RequestBody(required = true) String formRightsXml) ;

	/**
	 * 根据表单ID取得表单对象。
	 * @param formId
	 * @return ObjectNode FormService.getByFormId
	 * @
	 * @;//throws 
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getByFormId",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据表单ID取得表单对象。")
	public ObjectNode getByFormId(@Schema(name="formId",description="表单ID") @RequestParam(value = "formId", required = true) String formId) ;

	/**
	 * 获取流程实例表单的权限。
	 * <pre>
	 * {
	 * 	field：{"NAME": "w", "SEX": "r"}
	 * 	table：{"TABLE1": "r", "TABLE2": "w"}
	 * 	opinion：{"领导意见": "w", "部门意见": "r"}
	 * }
	 * </pre>
	 *
//	 * @param formKey	表单KEY 对应BPM_FROM key字段。
//	 * @param userId
//	 * @param flowKey
	 * @return
	 * @
	 * @;//throws 
	 * @
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getInstPermission",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="获取流程实例表单的权限。")
	public String getInstPermission(@Schema(name="model",description="model") @RequestBody(required = true) ObjectNode param);

	/**
	 * 获取流程启动时的表单权限
//	 * @param formKey
//	 * @param flowKey
//	 * @param nodeId 节点id
//	 * @param nextNodeId 下一个节点id
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getStartPermission",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="获取流程启动时的表单权限")
	public String getStartPermission(@Schema(name="model",description="model") @RequestBody(required = true) ObjectNode param) ;

	/**
	 * 获取表单权限
	 * <pre>
	 * {
	 * 	field：{"NAME": "w", "SEX": "r"}
	 * 	table：{"TABLE1": "r", "TABLE2": "w"}
	 * 	opinion：{"领导意见": "w", "部门意见": "r"}
	 * }
	 * </pre>
//	 * @param formKey 表单KEY 对应BPM_FROM key字段。
//	 * @param userId 用户ID
//	 * @param flowKey 流程KEY
//	 * @param nodeId 节点ID
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getPermission",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="获取表单权限")
	public String getPermission(@Schema(name="model",description="model") @RequestBody(required = true) ObjectNode param) ;

	/**
	 * 根据表单key获得权限列表。
	 * @param formId
	 * @return ObjectNode FormService.getByFormId
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getFormRigthListByFlowKey",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据表单key获得权限列表。")
	public List<ObjectNode> getFormRigthListByFlowKey(@Schema(name="formId",description="表单key")  @RequestParam(value = "formId", required = true) String formId) ;

	/**
	 * 删除表单权限
	 * 包括bpmFormRightManager.removeInst(flowKey);bpmFormRightManager.remove(flowKey, parentFlowKey);2个方法
	 * @param flowKey
	 * @param parentFlowKey
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/removeFormRights",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="删除表单权限")
	public void removeFormRights(@Schema(name="flowKey",description="流程KEY")@RequestParam String flowKey, @Schema(name="parentFlowKey",description="parentFlowKey")@RequestParam String  parentFlowKey);

	/**
	 * 通过别名获取bo定义  bODefManager.getByAlias(boDef.getKey());
	 * <pre>
	 * 获取bo定义并构建关联数据
	 * </pre>
	 *
	 * @param alias	别名
	 * @return		bo定义
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getBodefByAlias",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="通过别名获取bo定义")
	public ObjectNode getBodefByAlias(@Schema(name="alias",description="别名")  @RequestParam(value = "alias", required = true) String alias) ;

	/**
	 * 通过bo定义id获取bo的json格式定义 bODefManager.getBOJson(def.get("id"));
	 * @param id	bo定义id
	 * @return		json格式定义数据
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getBoJosn",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="通过bo定义id获取bo的json格式定义")
	public ObjectNode getBoJosn(@Schema(name="id",description="bo定义id") @RequestParam(value = "id", required = true) String id);

	/**
	 * 通过bo实例name获取boent  boDefService.getEntByName
//	 * @param 	bo实体name
	 * @return		json格式定义数据
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getBoEntByName",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="通过bo实例name获取boent")
	public ObjectNode getBoEntByName(@Schema(name="name",description="bo实例name") @RequestParam(value = "name", required = true) String name) ;

	/**
	 *删除表单权限
//	 * @param flowKey  流程定义key
//	 * @param parentFlowKey 父流程定义key
//	 * @param permissionType 权限类型
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/removeFormRightByFlowKey",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="删除表单权限")
	public void removeFormRightByFlowKey (@Schema(name="model",description="model") @RequestBody(required = true) ObjectNode param) ;

	/**
	 * 新增表单权限
	 * @param bpmFormRight
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/createFormRight",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="新增表单权限")
	public void createFormRight (@Schema(name="bpmFormRight",description="bpmFormRight") @RequestBody(required = true) ObjectNode bpmFormRight) ;

	/**
	 * 查询表单权限
	 * @param queryFilter
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/queryFormRightByJsonNode",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="查询表单权限")
	public  List<ObjectNode> queryFormRightByJsonNode (@Schema(name="queryFilter",description="通用查询对象") @RequestBody(required = true) JsonNode queryFilter) ;

	/**
	 * 根据表单key获取主版本的表单对象数据。  bpmFormManager.getMainByFormKey
	 * @param formKey	表单key
	 * @return		BpmForm的json格式数据
	 */

	@RequestMapping(value="/feign/form/formServiceController/v1/getFormBoLists",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="获取表单绑定的bo")
	public List<ObjectNode> getFormBoLists (@Schema(name="formKey",description="表单key") @RequestParam(value = "formKey", required = true) String formKey) ;
	/**
	 * 表单相关导出接口
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/getFormAndBoExportXml",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="获取表单导出的xml")
	public  Map<String,String> getFormAndBoExportXml (@Schema(name="obj",description="通用查询对象") @RequestBody(required = true) ObjectNode obj);

	/**
	 * 导入表单
	 * @param obj
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/importFormAndBo",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="导入表单和Bo")
	public  CommonResult<String> importFormAndBo (@Schema(name="obj",description="通用查询对象")@RequestBody ObjectNode obj);
	/**
	 * 根据业务数据关联对象清除流程相关数据
	 * @param links
	 * @return
	 */
	@RequestMapping(value="/feign/form/formServiceController/v1/removeDataByBusLink",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="根据业务数据关联对象清除流程相关数据")
	public void removeDataByBusLink (@Schema(name="links",description="业务数据关联对象列表") @RequestBody(required = true) JsonNode links) ;


	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/feign/form/customQuery/v1/getQueryPageForPageRowList", method = RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="")
	public PageList getQueryPageForPageRowList(@Schema(name = "alias",description="别名") @RequestParam(value = "alias", required = true) String alias);


	@RequestMapping(value="/feign/form/customQuery/v1/doQueryForPageRowList",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="")
	public PageList doQueryForPageRowList(
			@Schema(name = "alias",description="别名") @RequestParam(value = "alias", required = false) Optional<String> alias,
			@Schema(name = "queryData",description="") @RequestParam(value = "page", required = false) Optional<Integer> page,
			@Schema(name = "page",description="") @RequestBody(required = true) Optional<String> queryData) ;



	@RequestMapping(value = "/feign/form/customDialog/v1/getAll", method = RequestMethod.POST, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="查询所有的自定义对话框")
	public List getCustomDialogs();


	@RequestMapping(value="/feign/form/fieldAuth/v1/getByClassName",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description = "根据类名获取字段权限设置")
	public ObjectNode getByClassName(@Schema(name="className",description="类名", required = true) @RequestParam(value = "className", required = true) String className) ;

	@RequestMapping(value="/feign/form/form/v1/getFormData",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
	@ApiResponse(description="取得所有的表对象")
	public Map<String,Object> getFormData(@Schema(name = "pcAlias",description="pc别名")  @RequestParam(value = "pcAlias", required = false) String pcAlias, @RequestParam(value = "mobileAlias", required = false) String mobileAlias) ;

}   
