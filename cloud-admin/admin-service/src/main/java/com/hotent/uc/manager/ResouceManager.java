/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.manager;


import com.pharmcube.mybatis.support.manager.BaseManager;
import org.nianxi.api.model.CommonResult;
import com.hotent.uc.params.resouce.ResouceVo;
import com.hotent.uc.model.Resouce;

/**
 * 
 * <pre> 
 * 描述：维度管理 处理接口
 * 构建组：x5-bpmx-platform
 * 作者:liyg
 * 邮箱:liyg@jee-soft.cn
 * 日期:2017-07-19 15:30:09
 * 版权：广州宏天软件有限公司
 * </pre>
 */
public interface ResouceManager extends   BaseManager<Resouce>{
	
	/**
	 * 删除所有已逻辑删除的实体（物理删除）
	 * @param entityId 实体对象ID
	 */
	Integer removePhysical();
	
	

	CommonResult<String> saveResouce(ResouceVo resouceVo);

	Resouce getByRoleCode(String roleCode);
	
	/**
	 * 根据用户账号查询所有的权限
	 * @param code
	 * @return
	 * @throws Exception
	 */
	String getResouceByAccount(String account);

}
