/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager.impl;

import org.springframework.stereotype.Service;

import com.pharmcube.mybatis.support.manager.impl.BaseManagerImpl;
import com.hotent.portal.model.MySchedule;
import com.hotent.portal.persistence.dao.MyScheduleDao;
import com.hotent.portal.persistence.manager.MyScheduleManager;

/**
 * 
 * <pre> 
 * 描述：行程管理 处理实现类
 * 构建组：x7
 * 作者:heyf
 * 邮箱:heyf@jee-soft.cn
 * 日期:2018-09-10 09:51:04
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Service("myScheduleManager")
public class MyScheduleManagerImpl extends BaseManagerImpl<MyScheduleDao, MySchedule> implements MyScheduleManager{
}
