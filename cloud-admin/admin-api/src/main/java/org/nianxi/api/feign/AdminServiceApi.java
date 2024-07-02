/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pharmcube.api.conf.feign.FeignConfig;
import com.pharmcube.api.model.page.PageList;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.nianxi.api.feign.dto.*;
import org.nianxi.api.feign.fallback.AdminServiceApiFallbackFactory;
import org.nianxi.api.model.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 
 * @author liyg
 *
 */
@FeignClient(name= "${remote.feign.admin-service.name}",url="${remote.feign.admin-service.url}",contextId = "${remote.feign.admin-service.name}",fallbackFactory= AdminServiceApiFallbackFactory.class, configuration= FeignConfig.class)
public interface AdminServiceApi {



//	/**
//	 * 根据用户账号获取用户信息
//	 * @param account
//	 * @return
//	 */
//	@RequestMapping(value="/feign/api/user/v1/user/loadUserByUsername",method= RequestMethod.POST)
//	@ApiResponse(description = "根据账号获取userDetails信息")
//	public JsonNode loadUserByUsername(@RequestParam(value = "account", required = true) String account);

	@RequestMapping(value="/feign/api/user/v1/users/getAllUser",method=RequestMethod.GET)
	@ApiResponse(description = "获取系统中所有用户")
	public JsonNode getAllUser();


	/**
	 * 根据用户id获取用户信息
     * @return
     */
	@RequestMapping(value="/feign/api/user/v1/user/getUserById",method=RequestMethod.GET)
	@ApiResponse(description = "根据用户id获取用户信息")
	public CommonResult<UserDTO> getUserById(@RequestParam(value = "userId", required = true) String userId) ;

	/**
	 * 获取用户信息
	 * @param account
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getUser",method=RequestMethod.GET)
	@ApiResponse(description="根据用户标识获取用户信息，参数 （任传一个）{\"account\":\"用户账号\",\"userNumber\":\"用户工号\"}")
	public UserDTO getUser(@RequestParam(value = "account", required = true) String account, @RequestParam(value = "userNumber", required = false) String userNumber) ;

	/**
	 * 根据多个用户账号获取用户信息(以逗号隔开)
     * @return
     */
	@RequestMapping(value="/feign/api/user/v1/user/getUserByAccounts",method=RequestMethod.GET)
	@ApiResponse(description="根据多个用户账号获取用户信息(以逗号隔开)")
	public List<UserDTO> getUserByAccounts(@RequestParam(value = "accounts", required = true) String accounts);


	@RequestMapping(value="/feign/api/user/v1/users/postUserByAccount",method=RequestMethod.GET)
	@ApiResponse(description="根据用户账号获取用户信息并修改用户微信字段信息")
	public CommonResult<String> postUserByAccount(@RequestParam(value = "account", required = true) String accounts, @RequestParam(value = "openid", required = true) String openid) ;


	/**
	 * 获取所有组织人员（带分页信息）
	 * @param queryFilter
	 * @return
	 * @
	 */
	//@RequestMapping(value="/feign/api/user/v1/users/getAllOrgUsers",method=RequestMethod.POST)
	//public ObjectNode getAllOrgUsers(@RequestBody QueryFilter queryFilter);
	@RequestMapping(value="/feign/api/user/v1/users/getAllOrgUsersByJsonNode",method=RequestMethod.POST)
	@ApiResponse(description = "获取所有组织人员（带分页信息）")
	public PageList<UserDTO> getAllOrgUsersByJsonNode(@Schema(name="queryFilter",description="通用查询对象") @RequestBody JsonNode queryFilter) ;


	/**
	 * 	@RequestMapping(value="/feign/api/user/v1/users/getAllOrgUsersByJsonNode",method=RequestMethod.POST)
	 * 源：public PageList<UserDTO> getAllOrgUsersByJsonNode(@Schema(name="queryFilter",description="通用查询对象") @RequestBody JsonNode queryFilter) ;
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getAllOrgUsersByOrgId",method=RequestMethod.POST)
	@ApiResponse(description = "获取所有组织人员（带分页信息）")
	public PageList<UserDTO> getAllOrgUsersByOrgId(String orgId) ;



	/**
	 * 获取角色（多个）中的用户
	 * @param codes
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/role/v1/role/getUsersByRoleCode",method=RequestMethod.POST)
	@ApiResponse(description = "获取角色（多个）中的用户")
	public List<UserDTO> getUsersByRoleCode(@Schema(name="codes",description="角色编码，多个用“,”号隔开", required = true) @RequestBody String codes) ;

	/**
	 * 通过岗位编码获取用户
	 * @param postCode
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getUserByPost",method=RequestMethod.GET)
	@ApiResponse(description = "通过岗位编码获取用户")
	public List<UserDTO> getUserByPost(@Schema(name="postCode",description="岗位编码",required=true) @RequestParam(value = "postCode", required = true) String postCode) ;

	/**
	 * 获取职务（多个）下的所有人员
	 * @param codes
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/job/v1/jobUser/getUsersByJob",method=RequestMethod.GET, produces = {
	"application/json; charset=utf-8" })
	@ApiResponse(description = "获取职务（多个）下的所有人员")
	public List<UserDTO> getUsersByJob(@Schema(name="codes",description="职务编码（多个用“,”号隔开）", required = true)  @RequestParam(value = "codes", required = true) String codes) ;

	/**
	 * 根据email查询用户信息
	 * @param email
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getUserByEmail",method=RequestMethod.GET)
	@ApiResponse(description = "根据email查询用户信息")
	public ArrayNode getUserByEmail(@Schema(name="email",description="用户账号")  @RequestParam(value = "email", required = true) String email) ;


	/** List<OrgUser> orgUsers = orgUserManager.getChargesByOrgId(id,isMain);
	 *根据组织ID获取组织的负责人组织关系
	 * @param orgId
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgUsers/getChargesByOrgId",method=RequestMethod.GET)
	@ApiResponse(description = "根据组织ID获取组织的负责人组织关系")
	public List<ObjectNode> getChargesByOrgId(@Schema(name = "orgId", description = "组织id", required = true)  @RequestParam(value = "orgId", required = true) String orgId,
											  @Schema(name = "isMain", description = "是否主组织", required = false) @RequestParam(value = "isMain", required = true) boolean isMain
	) ;

//	/** Org org =  orgManager.get(orgId);
//	 * 根据组织id获取组织
//	 * @return
//	 */
//	@RequestMapping(value="/feign/api/org/v1/org/get",method=RequestMethod.GET)
//	@ApiResponse(description = "根据组织id获取组织")
//	public OrgDTO getOrgByIdOrCode(@Schema(name = "id", description = "组织id", required = true) @RequestParam(value = "id", required = true) String orgId);

	/**
	 * 根据角色id或编码获取角色
	 * @return
	 */
	@RequestMapping(value="/feign/api/role/v1/role/getRole",method=RequestMethod.GET)
	@ApiResponse(description = "根据角色编码获取角色信息")
	public CommonResult<RoleDTO> getRoleByIdOrCode(@Schema(name="code",description="角色编码", required = true) @RequestParam(value = "code", required = true) String code);

	/**
	 * 根据岗位id或编码获取岗位
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgPost/getOrgPost",method=RequestMethod.GET)
	@ApiResponse(description = " 根据岗位id或编码获取岗位信息")
	public CommonResult<OrgPostDTO> getPostByIdOrCode(@Schema(name="postCode",description="岗位代码",required=true) @RequestParam(value = "postCode", required = true) String code);

	/**
	 * 根据职务id或编码获取职务(返回CommonResult<?>)
	 * @return
	 */
	@RequestMapping(value="/feign/api/job/v1/job/getOrgJob",method=RequestMethod.GET)
	@ApiResponse(description = "根据职务编码或id获取职务信息")
	public CommonResult<OrgJobDTO> getJobByOrgCode(@Schema(name="code",description="职务编码", required = true) @RequestParam(value = "code", required = true) String code);

	/**
	 * 根据职务id或编码获取职务
	 * @return
	 */
	@RequestMapping(value="/feign/api/job/v1/job/getJob",method=RequestMethod.GET)
	@ApiResponse(description = "根据职务编码或id获取职务信息")
	public OrgJobDTO getJobByIdOrCode(@Schema(name="code",description="职务编码", required = true) @RequestParam(value = "code", required = true) String code);

	/**
	 * OrgUser orgUser = orgUserManager.getOrgUserMaster(ContextUtil.getCurrentUserId());
	 * 获取当前用户的主部门
     * @return
     */
	@RequestMapping(value="/feign/api/org/v1/org/getSupOrgByCurrMain",method=RequestMethod.GET)
	@ApiResponse(description = "获取用户的主岗位组织关系")
	public OrgUserDTO getOrgUserMaster(@Schema(name = "userId", description = "用户id", required = true) @RequestParam(value = "userId", required = true) String userId,
                                       @Schema(name = "demId", description = "维度id", required = true)  @RequestParam(value = "demId", required = true) String demId);



	/**
	 * List<SysUserRel> sysUserRels = sysUserRelManager.getSuperUser(userId, level, sysType.get("id").asText());
	 * 根據分類編碼和用戶賬號獲取匯報線
	 * @return
	 */

	@RequestMapping(value="/feign/api/userRel/v1/userRel/getSuperUser",method=RequestMethod.POST)
	@ApiResponse(description = "获取直接上级用户")
	public List<UserDTO> getSuperUser(@Schema(name="userRelFilterObject",description="用户关系定义编码", required = true)  @RequestBody(required = true) ObjectNode obj) ;

	/**
	 * List<Role> listRole= roleManager.getAll();
	 * @return
	 */
	@RequestMapping(value="/feign/api/role/v1/roles/getAll",method=RequestMethod.GET)
	@ApiResponse(description = "获取所有角色")
	public List<RoleDTO> getAllRole();

//	/**
//	 * List<Role> listRole = roleManager.getListByUserId(userId);
//	 * 根據用戶賬號獲取角色
//     * @return
//     */
//	@RequestMapping(value="/feign/api/role/v1/role/getRolesByUser",method=RequestMethod.GET)
//	@ApiResponse(description = "获取用户所属角色列表")
//	public List<RoleDTO> getRoleListByAccount(@Schema(name="account",description="用户帐号", required = true)  @RequestParam(value = "account", required = true) String account) ;



//	/**
//	 * List<OrgRel> orgRels = orgRelManager.getListByUserId(ContextUtil.getCurrentUser().getUserId());
//	 * 獲取當前用戶所有崗位
//     * @return
//     */
//	@RequestMapping(value="/feign/api/org/v1/orgPost/getOrgPostByUserAccount",method=RequestMethod.GET)
//	@ApiResponse(description = " 根据用户账号获取所属岗位")
//	public List<OrgPostDTO> getPosListByAccount(@Schema(name="account",description="用户账号",required=true) @RequestParam(value = "account", required = true) String account);


	/**
	 * 获取当前用户的组织布局管理权限
	 */
	@RequestMapping(value="/feign/api/orgAuth/v1/orgAuths/getCurrentUserAuthOrgLayout",method=RequestMethod.GET)
	@ApiResponse(description = "获取当前用户的组织布局管理权限")
	public ArrayNode getCurrentUserAuthOrgLayout(@Schema(name="userId",description="用户id") @RequestParam(value = "userId", required = true) String userId) ;


	/**
	 * 根据职务编码、组织编码获取对应人员
	 * @param jobCode
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getByJobCodeAndOrgCode", method = RequestMethod.GET)
	@ApiResponse(description = "根据职务编码、组织编码获取对应人员")
	public List<ObjectNode> getByOrgRelDefCode(
			@Schema(name="jobCode",description="职务编码",required=true)  @RequestParam(value = "jobCode", required = true) String jobCode,
			@Schema(name="orgCode",description="组织编码",required=true) @RequestParam(value = "orgCode", required = true) String orgCode) ;

	/**
	 * 根据岗位编码、组织编码获取对应人员
	 * @param postCode
	 * @param orgCode
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getByPostCodeAndOrgCode", method = RequestMethod.GET)
	@ApiResponse(description = "根据岗位编码、组织编码获取对应人员")
	public List<ObjectNode> getByOrgRelCode(
			@Schema(name="postCode",description="岗位编码",required=true) @RequestParam(value = "postCode", required = true) String postCode,
			@Schema(name="orgCode",description="组织编码",required=true) @RequestParam(value = "orgCode", required = true) String orgCode) ;

	/**
	 * 获取用户主组织
	 */
	//@RequestMapping(value="/feign/api/org/v1/org/getMainGroup", method = RequestMethod.GET)
	//public ObjectNode getMainGroup(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "demId", required = true) String demId);

	/**
	 * 获取组织（主）负责人
	 * @param userId 用户id
	 * @param isMain 是否主组织
	 * @param isP 是否上级
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getCharges", method = RequestMethod.GET)
	@ApiResponse(description = "获取用户主组织")
	public JsonNode getCharges(@Schema(name = "userId", description = "用户id", required = true) @RequestParam(value = "userId", required = true) String userId,
							   @Schema(name = "isMain", description = "是否主负责人", required = true)  @RequestParam(value = "isMain") Boolean isMain,
							   @Schema(name = "isP", description = "是否上级部门", required = true) @RequestParam(value = "isP") Boolean isP) ;

	/**
	 * 获取用户所有职务
     * @return
     */
	@RequestMapping(value="/feign/api/org/v1/orgJobs/getJobsByUserId", method = RequestMethod.GET)
	@ApiResponse(description = "获取用户所有职务")
	public List<OrgJobDTO> getJobsByUserId(@Schema(name = "userId", description = "用户id", required = true) @RequestParam(value = "userId", required = true) String userId);

	/**
	 * 判断当前用户主部门是否有上级
	 */
	@RequestMapping(value="/feign/api/org/v1/org/isSupOrgByCurrMain", method = RequestMethod.GET)
	@ApiResponse(description = "判断当前用户主部门是否有上级 ")
	public boolean isSupOrgByCurrMain(@Schema(name = "userId", description = "用户id", required = true)  @RequestParam(value = "userId", required = true) String userId,
									  @Schema(name = "demId", description = "维度id", required = true)   @RequestParam(value = "demId", required = true) String demId,
									  @Schema(name = "level", description = "级别", required = true) @RequestParam(value = "level", required = true) Integer level) ;

	/**
	 * 通过组织中的下属设置获取上级人员
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getSuperFromUnder", method = RequestMethod.GET)
	@ApiResponse(description = "通过组织中的下属设置获取上级人员")
	public ArrayNode getSuperFromUnder(@Schema(name = "userId", description = "用户id", required = true) @RequestParam(value = "userId", required = true) String userId,
									   @Schema(name = "orgId", description = "组织id或编码", required = false) @RequestParam(value = "orgId", required = false) String orgId,
									   @Schema(name = "demId", description = "维度id或编码", required = false)  @RequestParam(value = "demId", required = false) String demId) ;

	/**
	 * 获取发起人指定级别组织的负责人
	 * @param userId
	 * @param level  2
	 * @param isMainCharge true/false
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgusers/getCustomLevelCharge", method = RequestMethod.GET)
	@ApiResponse(description = "获取发起人指定级别组织的负责人")
	public ArrayNode getCustomLevelCharge(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "level", required = true) String level, @RequestParam(value = "isMainCharge", required = true) boolean isMainCharge) ;

	/**
	 * 获取发起人指定级别组织的指定岗位的用户
	 * @param userId
	 * @param level
	 * @param postCode
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgusers/getCustomLevelPost", method = RequestMethod.GET)
	@ApiResponse(description = "获取发起人指定级别组织的指定岗位的用户")
	public  Set<ObjectNode> getCustomLevelPost(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "level", required = true) String level, @RequestParam(value = "postCode", required = true) String postCode) ;

	/**
	 * 获取发起人指定级别组织的指定职务的用户
	 * @param userId
	 * @param level
	 * @param // STOPSHIP: 2022/4/19
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgusers/getCustomLevelJob", method = RequestMethod.GET)
	@ApiResponse(description = "获取发起人指定级别组织的指定职务的用")
	public  Set<ObjectNode> getCustomLevelJob(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "level", required = true) String level, @RequestParam(value = "jobCode", required = true) String jobCode) ;

	/**
	 * 获取发起人组织的指定扩展参数值
	 * @param userId
	 * @param param
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgusers/getStartOrgParam", method = RequestMethod.GET)
	@ApiResponse(description = "获取发起人组织的指定扩展参数值")
	public String getStartOrgParam(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "param", required = true) String param);

//	/**
//	 * 通过用户ID和参数代码获取用户参数
//	 * @param userId
//	 * @param code
//	 * @return
//	 */
//	@RequestMapping(value="/feign/api/params/v1/userParam/getUserParamsById", method = RequestMethod.GET)
//	@ApiResponse(description = "获取用户指定参数")
//	public ObjectNode getUserParamsById(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "code", required = true) String code) ;

//	/**
//	 * 通过组织ID和参数代码获取组织参数
//	 * @return
//	 */
//	@RequestMapping(value="/feign/api/params/v1/orgParam/getOrgParamsById", method = RequestMethod.GET)
//	@ApiResponse(description = "获取组织指定参数")
//	public ObjectNode getOrgParamsById(
//			@Schema(name="orgId",description="组织编码", required = true) @RequestParam(value = "orgId", required = true) String orgId,
//			@Schema(name="code",description="参数编码", required = true)  @RequestParam(value = "code", required = true) String code) ;

	/**
	 * 根据id或者账号串获取用户
     * @return
     */
	@RequestMapping(value="/feign/api/user/v1/users/postUserByIds", method = RequestMethod.POST)
	@ApiResponse(description = "根据多个用户id获取用户信息(以逗号隔开)")
	public List<UserDTO> getUserByIdsOrAccounts(@Schema(name="ids",description="用户id") @RequestBody(required = false) String ids);

	/**
	 * 获取子组织
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgs/getByParentId",method=RequestMethod.GET)
	public List<OrgDTO> getChildOrg(@RequestParam(value = "parentId", required = true) String parentId);

	/**
	 * 获取子组织（只获取底下一层子组织）
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgs/getOrgsByparentId",method=RequestMethod.GET)
	@ApiResponse(description = "获取子组织(只获取底下一层子组织)")
	public List<OrgDTO> getOrgsByparentId(@Schema(name = "parentId", description = "父组织id", required = true) @RequestParam(value = "parentId", required = true) String parentId);

	/**
	 * 根据用户id组获取主组织路径
	 */
	@RequestMapping(value="/feign/api/org/v1/org/getPathNames", method = RequestMethod.POST)
	@ApiResponse(description = "根据用户id组获取组织全路径")
	public List<Map<String, String>> getPathNames(@Schema(name = "userIds", description = "用户id组", required = false)  @RequestParam(value = "userIds", required = true) List<String> userIds);

	/**
	 * 根据用户id获取用户所在部门负责人
	 * @param userId
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getDepHeader",method=RequestMethod.GET)
	@ApiResponse(description = "根据用户id获取用户所在部门负责人")
	public List<UserDTO> getDepHeader(@Schema(name = "userId", description = "用户id", required = false) @RequestParam(value = "userId", required = false) String userId,
                                      @Schema(name = "isMain", description = "是否只取主负责人", required = false)  @RequestParam(value = "isMain", required = true) Boolean isMain) ;


	/**
	 * 根据部门id获取部门负责人
	 * @param orgId
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getDepHeaderByOrg",method=RequestMethod.GET)
	@ApiResponse(description = "根据部门id获取部门负责人")
	public List<UserDTO> getDepHeaderByOrg(@Schema(name = "orgId", description = "部门id", required = false)  @RequestParam(value = "orgId", required = true) String orgId,
                                           @RequestParam(value = "isMain", required = true) Boolean isMain) ;


	/**
	 * 根据用户id获取其相关用户组id
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getGroupsByUidAndTypeForGroupDTO",method=RequestMethod.GET)
	@ApiResponse(description = "根据用户id和用户组类型获取其相关用户组")
	public  List<GroupDTO>  getGroupsByUidAndTypeForGroupDTO(
			@Schema(name = "userId", description = "用户id", required = false) @RequestParam(value = "userId", required = true) String userId,
			@Schema(name = "type", description = "类型", required = false) @RequestParam(value = "type", required = true) String type);


	/**
	 * 获取子组织ids
	 */
	@RequestMapping(value="/feign/api/org/v1/org/getChildrenIds",method=RequestMethod.POST)
	@ApiResponse(description = "获取子组织ID（包含自己）")
	public Map<String, Set<String>> getChildrenIds(@Schema(name = "ids", description = "组织id", required = true) @RequestParam(value = "ids", required = true) Map<String, String> ids);

	/**
	 * 查询用户组织关系
	 * @param queryFilter
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/user/v1/user/queryOrgUserRelByJsonNode",method=RequestMethod.POST)
	@ApiResponse(description = "根据用户id和用户组类型获取其相关用户组")
	public List queryOrgUserRelByJsonNode(@Schema(name="queryFilter",description="通用查询对象") @RequestBody JsonNode queryFilter) ;
	//@RequestMapping(value="/feign/api/user/v1/user/queryOrgUserRel",method=RequestMethod.POST)
	//public List queryOrgUserRel(@RequestBody QueryFilter queryFilter);


	/**
	 * 模糊查询用户列表
	 */
	@RequestMapping(value="/feign/api/user/v1/users/getUserByNameaAndEmal",method=RequestMethod.GET)
	@ApiResponse(  description="模糊查询获取用户列表")
	public List<ObjectNode> getUserByNameaAndEmal(@Schema(name="key",description="查询条件", required = false) @RequestParam(value = "query", required = true) String query);

	/**
	 * 结算节点人员
	 */
	@RequestMapping(value="/feign/api/user/v1/users/calculateNodeUser", method = RequestMethod.POST)
	@ApiResponse(description = "根据节点处理人对象抽取处理人")
	public Map<String, Object> calculateNodeUser(@Schema(name="nodeMap",description="通用查询对象") @RequestBody(required = true) Map<String, Object> result);

	/**
	 * 根据维度获取组织
	 * @param demId
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/org/getOrgListByDemId",method=RequestMethod.GET)
	@ApiResponse(description = "获取组织列表")
	public List<OrgDTO> getOrgListByDemId(@Schema(name = "demId", description = "维度id", required = true)  @RequestParam(value = "demId", required = true) String demId);

	/**
	 * 根据微信公众号openId获取用户信息
	 * @param openId
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getUserByOpenId",method=RequestMethod.GET)
	@ApiResponse(description="根据微信公众号openId获取用户信息")
	public CommonResult<UserDTO> getUserByOpenId(@Schema(name="openId",description="公众号openId", required = false) @RequestParam(value = "openId", required = true) String openId);


	/**
	 * 根据传入的用户id集合，获取用户的权限集合
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getUserRightMapByIds", method = RequestMethod.GET)
	@ApiResponse( description="根据传入的用户id集合，获取用户的权限集合")
	public Map<String, Map<String, String>> getUserRightMapByIds(@Schema(name="ids",description="用户id集合", required = true) @RequestParam(value = "ids", required = true) Set<String> ids);

	/**
	 * 获取默认维度信息
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/demension/v1/dem/getDefaultDem",method=RequestMethod.GET)
	@ApiResponse(description = "获取默认维度信息")
	public ObjectNode getDefaultDem();

	/**
	 * 获取用户的主岗位(优先获取默认维度的主岗位、主组织，没有时获取其他维度主岗位、主组织)
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/org/getMainPostOrOrgByUserId",method=RequestMethod.GET)
	@ApiResponse(description = "获取用户的主岗位、主组织(优先获取默认维度的，没有时获取其他维度的)")
	public OrgUserDTO getMainPostOrOrgByUserId(@Schema(name = "userId", description = "用户id", required = true) @RequestParam(value = "userId", required = true) String userId);

	/**
	 * 获取用户的主岗位组织关系
	 * @param userId
	 * @param demId
	 * @return
	 */
	//@RequestMapping(value="/feign/api/org/v1/org/getSupOrgByCurrMain",method=RequestMethod.GET)
	//public ObjectNode getSupOrgByCurrMain(@RequestParam(value = "userId", required = true) String userId, @RequestParam(value = "demId", required = true) String demId);
	/*@Deprecated
	@ApiResponse(description = "move to: getOrgUserMaster方法")
	ObjectNode getSupOrgByCurrMain(String userId, String demId);
	*/
	/**
	 * 获取所有维度列表
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/demension/v1/dems/getAll",method=RequestMethod.GET)
	@ApiResponse(description = "获取所有维度列表")
	public List<ObjectNode> getAllDems();

	/**
	 * 根据用户账号获取用户组织岗位相关信息
	 * @param account
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getDetailByAccountOrId",method=RequestMethod.GET)
	@ApiResponse(description = "根据用户账号获取用户信息")
	public Map<String, Object> getDetailByAccountOrId(@Schema(name="account",description="用户账号") @RequestParam(value = "account", required = true) String account);

    /**
     * 根据多个用户id获取用户信息(以逗号隔开)
     * @return
     */
    @RequestMapping(value="/feign/api/user/v1/user/getUserByIds",method=RequestMethod.GET)
	@ApiResponse(description = "根据多个用户id获取用户信息(以逗号隔开)")
    public List<UserDTO> getUserByIds(@Schema(name="ids",description="用户id")  @RequestParam(value = "ids", required = true) String ids);

    /**
	 * 将第三方通讯录组织框架拉取至本系统
	 * @return
	 */

	//public CommonResult<String> addOrgFromExterUni(@RequestBody ObjectNode org);

	@RequestMapping(value="/feign/api/org/v1/org/addOrgFromExterUni",method=RequestMethod.POST)
	@ApiResponse(description = "从第三方获取组织数据添加到本系统")
	CommonResult<String> addOrgFromExterUni(@Schema(name = "org", description = "组织", required = true) @RequestBody OrgDTO orgVo);

	/**
	 * 根据手机号获取用户
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/user/getUserByMobile",method=RequestMethod.GET)
    CommonResult<UserDTO> getUserByMobile(String mobile);

	/**
	 * 将第三方通讯录成员拉取至本系统
	 * @return
	 */
	//@RequestMapping(value="/feign/api/user/v1/user/addUserFromExterUni",method=RequestMethod.POST)
	//public CommonResult<String> addUserFromExterUni(@RequestBody ObjectNode newUser);

	@RequestMapping(value="/feign/api/user/v1/user/addUserFromExterUni",method=RequestMethod.POST)
	@ApiResponse(description = "添加用户")
	public CommonResult<String> addUserFromExterUni(@Schema(name = "newUser", description = "用户", required = true) @RequestBody UserDTO newUser) ;

	/**
	 * 组织批量加入用户
	 * @return
	 */
	@RequestMapping(value="/feign/api/org/v1/orgUsers/addUsersForOrg",method=RequestMethod.POST)
	@ApiResponse(description="组织批量加入用户")
	public CommonResult<String> addUsersForOrg(@Schema(name = "orgCode", description = "组织代码", required = true) @RequestParam(value = "orgCode", required = true) String orgCode,
											   @Schema(name = "accounts", description = "用户帐号", required = true) @RequestParam(value = "accounts", required = true) String accounts) ;

    /**
	 * 根据签署任务信息获取审批用户的姓名，账号， 主部门等信息
	 * @param customSignDatas
	 * @return
	 */
	@RequestMapping(value="/feign/api/user/v1/user/getUserInfoBySignData", method = RequestMethod.POST)
	@ApiResponse(description = "根据签署数据获取用户信息")
	public ArrayNode getUserInfoBySignData(@RequestBody ArrayNode customSignDatas) ;

	/**
	 * 根据租户id获取其被禁用菜单别名
	 * @param tenantId
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/uc/tenantIgnoreMenu/v1/getIgnoreMenuCodes",method=RequestMethod.GET)
	@ApiResponse(description="租户禁用菜单数据详情")
	public List<String> getIgnoreMenuCodes(@Schema(name="tenantId",description="租户id", required = true) @RequestParam(value = "tenantId", required = true) String tenantId) ;

	/**
	 * 通过租户ID获取租户信息
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/feign/uc/tenantManage/v1/getJson",method=RequestMethod.GET)
	@ApiResponse(description="租户管理 数据详情")
	public JsonNode getTenantById(@Schema(name="id",description="业务对象主键", required = true) @RequestParam(value = "id", required = true) String id);

	/**
	 * 获取当前用户填制单位
	 * @param demId
	 * @return
	 * @
	 */
	@RequestMapping(value="/feign/api/org/v1/org/getFillOrg",method=RequestMethod.GET)
	@ApiResponse(description = "获取当前用户填制单位")
	public CommonResult<OrgDTO> getFillOrg(@Schema(name = "demId", description = "维度Id", required = true)  @RequestParam(value = "demId", required = false) String demId) ;



//    //PortalApi
//    @ApiResponse(description="获取角色跟请求地址的关系")
//    @RequestMapping(value="/feign/sys/sysRoleAuth/v1/getMethodRoleAuth",method=RequestMethod.GET)
//    public List<Map<String,String>> getMethodRoleAuth();


    @RequestMapping(value="/feign/sys/sysProperties/v1/getByAlias",method=RequestMethod.GET)
    @ApiResponse(description="获取系统属性参数值")
    public String getByAlias(@Schema(name="alias", description="属性别名", required = true) @RequestParam String alias,
                             @Schema(name="defaultValue", description="默认值")@RequestParam Optional<String> defaultValue);

    /**
     * 根据别名获取系统分类
     *
     */
    @RequestMapping(value="/feign/sys/sysType/v1/getJson",method=RequestMethod.GET)
    @ApiResponse(description="系统分类")
    public ObjectNode getSysTypeById(@Schema(name="id", description="主键", required = false) @RequestParam(value = "id", required = true) String id);

    /**
     * 根据queryFilter获取系统分类。不带分页
     * @return
     */
    @RequestMapping(value="/feign/sys/sysType/v1/listByJsonNode",method=RequestMethod.POST)
    @ApiResponse(description="系统分类")
    public ObjectNode getAllSysTypeByJsonNode(@Schema(name="queryFilter",description="通用查询对象") @RequestBody JsonNode queryFilter);



    /**
     * 根据用户，指定工时，指定开始时间,计算任务实际完成时间
     * @return				完成时间
     * @throws Exception
     */
    @RequestMapping(value="/feign/portal/calendar/v1/getEndTimeByUser",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="根据用户开始时间和结束时间，获取这段时间的有效工时")
    public Long getEndTimeByUser(@Schema(name="calendarVo",description="日历操作参数") @RequestBody(required = true) CalendarDTO param);


    /**
     * 根据用户开始时间和结束时间，获取这段时间的有效工时
     * @return
     * @throws IOException
     */
    @RequestMapping(value="/feign/portal/calendar/v1/getWorkTimeByUser",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="根据用户开始时间和结束时间，获取这段时间的有效工时")
    public Long getWorkTimeByUser(@Schema(name="calendarVo",description="日历操作参数") @RequestBody(required = true) CalendarDTO calendar);


    /**
     * 根据id或别名获取系统分类
     *
     */
    @RequestMapping(value="/feign/sys/sysType/v1/getJson",method=RequestMethod.GET, produces = { "application/json; charset=utf-8" })
    @ApiResponse(description="系统分类")
    public ObjectNode getSysTypeByIdOrKey(@RequestParam(value = "userId", required = true) String id);

    /**
     * 获取用户已读未读消息
     * @param account
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/feign/innermsg/messageReceiver/v1/getMessBoxInfo",method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="获取用户已读未读消息")
    public ObjectNode getMessBoxInfo(@Schema(name="account", description="用户账号", required = true) String account);


    /**
     * 根据数据源别名，获取数据源的设置信息
     * @param alias
     * @return
     */
    @RequestMapping(value="/feign/sys/sysDataSource/v1/getBeanByAlias", method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="根据别名，获取数据库类型")
    public JsonNode getBeanByAlias(@Schema(name="alias", description="数据源别名", required = true) String alias);

    /**
     * 根据流水号别名获取下一个流水号
     * @param alias
     * @return
     */
    @RequestMapping(value="/feign/sys/identity/v1/getNextIdByAlias", method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="获取下一个流水号")
    public  String getNextIdByAlias(@Schema(name="alias",description="流水号别名", required = true) @RequestParam(value = "alias", required = true) String alias);

    /**
     * 根据queryFilter获取新闻公告
     */
    @RequestMapping(value="/feign/portal/messageNews/v1/listByJsonNode",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="获取新闻公告列表")
    public ObjectNode getMessageNewsByJsonNode(@Schema(name="queryFilter",description="查询对象") @RequestBody(required = true) JsonNode queryFilter);

    /**
     * 根据新闻公告id发布新闻公告
     */
    @RequestMapping(value="/feign/portal/messageNews/v1/publicMsgNews",method=RequestMethod.POST, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="发布新闻公告记录")
    public ObjectNode publicMsgNews(@Schema(name="idStrs",description="业务主键（列表）", required = true) @RequestBody(required = true) String array);

	@RequestMapping(value="/feign/sys/sysLogsSettings/v1/getSysLogsSettingStatusMap",method=RequestMethod.GET)
	public Map<String, String> getSysLogsSettingStatusMap();

    @RequestMapping(value="/feign/sys/authUser/v1/getAuthorizeIdsByUserMap", method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="通过objType获取当前用户权限")
    public List<String> getAuthorizeIdsByUserMap(@Schema(name="objType",description="objType") @RequestParam(value = "objType", required = true) String objType) ;

    @RequestMapping(value="/feign/portal/sysExternalUnite/v1/getToken", method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="获取getToken")
    public String getToken(@RequestParam(value = "type", required = true) String type);

    /**
     * 创建租户时 初始化租户数据
     * @param tenantId
     * @return
     */
    @RequestMapping(value="/feign/portal/tenantInitData/v1/initData", method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description = "初始化数据")
    public CommonResult<String> initData(@RequestParam(value = "tenantId", required = true) String tenantId);

    @RequestMapping(value="/feign/portal/sysExternalUnite/v1/getUserInfoUrl", method=RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description="获取getUserInfoUrl")
    public String getUserInfoUrl(@Schema(name="type",description="第三方集成类型") @RequestParam(value = "type",required = true) String type,
                                 @Schema(name="code",description="应用code")@RequestParam(value = "code",required = true) String code);
    //public String getUserInfoUrl(@RequestParam(value = "type", required = true) String type, @RequestParam(value = "code", required = true) String code);

    @RequestMapping(value="/feign/system/file/v1/wordPrint",method=RequestMethod.POST, produces={"application/json; charset=utf-8"})
    @ApiResponse(description="word模板打印")
    public String wordPrint(@Schema(name="objectNode",description="Json对象") @RequestBody ObjectNode objectNode);





}
