package com.nx.xxx.yyy.controller;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.nx.api.model.R;
import com.nx.api.model.query.PageInfo;
import com.nx.api.model.query.PageList;
import com.nx.xxx.yyy.model.UserBo;
import com.nx.xxx.yyy.model.UserVo;
import com.nx.xxx.yyy.model.entity.UcUser;
import com.nx.xxx.yyy.service.UcUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 
 * <pre> 
 * 描述：用户管理 控制器类
 * 构建组：x7
 * 作者:heyf
 * 邮箱:xlnian@163.com
 * 日期:2022-06-10 10:32:45
 * 版权：nx
 * </pre>
 */
@RestController
@RequestMapping(value="/xxx/user/v1")
@Api(tags="userController")
public class UserController {

	@Autowired
	private UcUserService ucUserService;

	/**
	 * 用户管理列表(分页条件查询)数据
	 * @return
	 * @throws Exception 
	 * PageJson
	 * @exception 
	 */
	@PostMapping("/listPage")
	@ApiOperation(value="用户管理数据列表", httpMethod = "POST", notes = "获取用户管理列表")
	public PageList<UserVo> listPage(@ApiParam(name="pageInfo",value="查询对象")@RequestBody PageInfo pageInfo, UserBo userBo)  {
		return  ucUserService.findPageListByUserBo(pageInfo,userBo);
	}

	@PostMapping("/listPageCustom")
	@ApiOperation(value="用户管理数据列表", httpMethod = "POST", notes = "获取用户管理列表")
	public List<UserVo> listPageByCondition(@ApiParam(name="pageInfo",value="查询对象")@RequestBody PageInfo pageInfo, UserBo userBo) {
		return  ucUserService.listByCondition(pageInfo,userBo);
	}

	@PostMapping("/listCustom")
	@ApiOperation(value="用户管理数据列表", httpMethod = "POST", notes = "获取用户管理列表")
	public List<UserVo> listByCondition(@ApiParam(name="pageInfo",value="查询对象")@RequestBody PageInfo pageInfo, UserBo userBo)  {
		return  ucUserService.listByCondition(pageInfo,userBo);
	}

	/**
	 * 用户管理明细页面
	 * @param id
	 * @return
	 * @throws Exception 
	 * ModelAndView
	 */
	@GetMapping(value="/getJson")
	@ApiOperation(value="用户管理数据详情",httpMethod = "GET",notes = "用户管理数据详情")
	public UcUser get(@ApiParam(name="id",value="业务对象主键", required = true)@RequestParam String id)  {
		return ucUserService.get(id);
	}
	
    /**
	 * 新增用户管理
	 * @param ucUser
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@PostMapping(value="save")
	@ApiOperation(value = "新增,更新用户管理数据", httpMethod = "POST", notes = "新增,更新用户管理数据")
	public R<String> save(@ApiParam(name="ucUser",value="用户管理业务对象", required = true)@RequestBody UcUser ucUser)  {
		String msg = "添加用户管理成功";
		if(StringUtil.isEmpty(ucUser.getId())){
			ucUserService.create(ucUser);
		}else{
			ucUserService.update(ucUser);
			 msg = "更新用户管理成功";
		}
		return R.OK(msg);
	}
	
	/**
	 * 批量删除用户管理记录
	 * @param ids
	 * @throws Exception 
	 * @return
	 * @exception 
	 */
	@DeleteMapping(value="/remove")
	@ApiOperation(value = "批量删除用户管理记录", httpMethod = "DELETE", notes = "批量删除用户管理记录")
	public R<String> removes(@ApiParam(name="ids",value="业务主键数组,多个业务主键之间用逗号分隔", required = true)@RequestParam String...ids) {
		ucUserService.removeByIds(ids);
		return  R.OK("批量删除成功");
	}
}
