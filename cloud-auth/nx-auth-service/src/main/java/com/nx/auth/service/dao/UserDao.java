/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nx.auth.service.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * <pre> 
 * 描述：用户表 DAO接口
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-30 10:26:50
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Mapper
public interface UserDao extends BaseMapper<User>{
	/**
	 * 根据account获取记录数
	 * @param account
	 * @return
	 */
	Integer getCountByAccount(@Param("account")String account);

	/**
	 * 根据mobile取定义对象。
	 * @param mobile
	 * @return
	 */
	User getByMobile(@Param("mobile") String mobile);


	/**
	 * 根据工号取定义对象。
	 * @param userNumber
	 * @return
	 */
	User getByNumber(@Param("userNumber") String userNumber);

	
	/**
	 * 根据Account取定义对象。
	 * @param account
	 * @return
	 */
	User getByAccount(@Param("account") String account);

	/**
	 * 根据openId查询用户信息
	 * @param openId
	 * @return
	 */
	User getUserByOpenId(@Param("openId") String openId);

}
