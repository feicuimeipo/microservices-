/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.controller;

import java.util.List;

import org.nianxi.x7.api.constant.ApiGroupConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.nianxi.boot.annotation.ApiGroup;
import com.hotent.base.controller.BaseController;
import com.hotent.uc.exception.RequiredException;
import com.hotent.uc.manager.RelAuthManager;
import com.hotent.uc.manager.UserManager;
import com.hotent.uc.manager.UserRelManager;
import com.pharmcube.mybatis.support.query.FieldRelation;
import com.pharmcube.mybatis.support.query.PageList;
import com.pharmcube.mybatis.support.query.QueryFilter;
import com.pharmcube.mybatis.support.query.QueryOP;
import com.hotent.uc.model.RelAuth;
import com.hotent.uc.model.User;
import com.hotent.uc.model.UserRel;
import org.nianxi.api.model.CommonResult;
import com.hotent.uc.params.common.OrgExportObject;
import com.hotent.uc.params.org.RelAuthVo;
import com.hotent.uc.util.OrgUtil;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 汇报线分级管理接口
 * @author liangqf
 *
 */
@RestController
@RequestMapping("/api/relAuth/v1/")
@Api(tags="汇报线分级管理")
@ApiGroup(group= {ApiGroupConsts.GROUP_UC})
public class RelAuthController extends BaseController<RelAuthManager, RelAuth> {
	
	@Autowired
	RelAuthManager relAuthService;
	@Autowired
	UserRelManager userRelService;
	@Autowired
	UserManager userService;
	
	/**
	 * 获取汇报线分级列表
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuths/getRelAuthPage",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "获取汇报线分级列表（带分页信息）", httpMethod = "POST", notes = "获取汇报线分级列表")
	public PageList<RelAuth> getRelAuthPage(@ApiParam(name="filter",value="查询对象")
	 @RequestBody QueryFilter filter,@ApiParam(name="relCode",value="汇报线编码",required=true) @RequestParam String relCode,@ApiParam(name="account",value="用户账号") @RequestParam(required=false) String account) throws Exception{
		UserRel rel = userRelService.getByAlias(relCode);
		if(BeanUtils.isEmpty(rel)){
			throw new RequiredException("汇报线编码【"+relCode+"】不存在！");
		}
		filter.addFilter("a.REL_ID_", rel.getId(), QueryOP.EQUAL, FieldRelation.AND);
		if(StringUtil.isNotEmpty(account)){
			User user = userService.getByAccount(account);
			if(BeanUtils.isEmpty(user)){
				throw new RequiredException("用户账号【"+account+"】不存在！");
			}
			//filter.addFilter("a.USER_ID_", user.getId(), QueryOP.EQUAL, FieldRelation.AND);
		}
		return  relAuthService.queryRelAuth(filter);
	}
	
	/**
	 * 添加汇报线分级
	 * @param relAuthVo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuth/addRelAuth",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "添加汇报线分级", httpMethod = "POST", notes = "添加汇报线分级")
	public CommonResult<String> addRelAuth(@ApiParam(name="relAuthVo",value="汇报线分级对象",required=true) @RequestBody RelAuthVo relAuthVo) throws Exception{
		relAuthService.addRelAuth(relAuthVo);
		return new CommonResult<String>(true, "添加成功", "");
	}
	
	/**
	 * 分配管理员
	 * @param Role
	 * @return
	 * @throws Exception
	 * 
	 */
	@RequestMapping(value="relAuths/addRelAuths",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "分配管理员（多个）", httpMethod = "POST", notes = "分配管理员（多个）")
	public CommonResult<String> addRelAuths(@ApiParam(name="code",value="汇报线节点编码", required = true) @RequestParam String code,
			@ApiParam(name="accounts",value="用户账号，多个用“,”号隔开", required = true) @RequestParam String accounts) throws Exception{
		return relAuthService.addRelAuths(code,accounts);
	}
	
	/**
	 * 修改汇报线分级
	 * @param relAuthVo
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuth/updateRelAuth",method=RequestMethod.PUT, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "修改汇报线分级", httpMethod = "PUT", notes = "修改汇报线分级")
	public CommonResult<String> updateRelAuth(@ApiParam(name="relAuthVo",value="汇报线分级对象",required=true) @RequestBody RelAuthVo relAuthVo) throws Exception{
		relAuthService.updateRelAuth(relAuthVo);
		return new CommonResult<String>(true, "修改成功", "");
	}
	
	/**
	 * 删除汇报线分级
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuth/delRelAuth",method=RequestMethod.DELETE, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "删除汇报线分级", httpMethod = "DELETE", notes = "删除汇报线分级")
	public CommonResult<String> delRelAuth(@ApiParam(name="relCode",value="汇报线编码",required=true) @RequestParam String relCode,@ApiParam(name="accounts",value="用户账号（多个用“,”号隔开）",required=true) @RequestBody String accounts) throws Exception{
		return relAuthService.delRelAuth(relCode,accounts);
	}
	
	/**
	 * 获取汇报线分级
	 * @param code
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuth/getRelAuth",method=RequestMethod.GET, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "获取汇报线分级", httpMethod = "GET", notes = "获取汇报线分级")
	public RelAuth getRelAuth(@ApiParam(name="account",value="用户账号",required=true) @RequestParam String account,@ApiParam(name="relCode",value="汇报线编码",required=true) @RequestParam String relCode) throws Exception{
		return relAuthService.getRelAuth(account,relCode);
	}
	
	/**
	 * 物理删除所有逻辑删除了的分级汇报线数据
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuth/deleteRelAuthPhysical",method=RequestMethod.DELETE, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "物理删除所有逻辑删除了的分级汇报线数据", httpMethod = "DELETE", notes = "物理删除所有逻辑删除了的分级汇报线数据")
	public CommonResult<Integer> deleteRelAuthPhysical() throws Exception{
		Integer num = relAuthService.removePhysical();
		return OrgUtil.getRemovePhysiMsg(num);
	}
	
	
	/**
	 * 根据时间获取分级汇报线数据（数据同步）
	 * @param btime
	 * @param etime
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="relAuths/getRelAuthByTime",method=RequestMethod.POST, produces = {
	"application/json; charset=utf-8" })
	@ApiOperation(value = "根据时间获取分级汇报线数据（数据同步）", httpMethod = "POST", notes = "根据时间获取分级汇报线数据（数据同步）")
	public List<RelAuth> getRelAuthByTime(@ApiParam(name="exportObject",value="获取数据参数类",required=true) @RequestBody OrgExportObject exportObject) throws Exception{
		return relAuthService.getRelAuthByTime(exportObject);
	}
}
