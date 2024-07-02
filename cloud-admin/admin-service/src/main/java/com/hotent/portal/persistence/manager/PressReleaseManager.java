/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.persistence.manager;

import java.util.List;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.portal.model.PressRelease;

/**
 * 
 * <pre> 
 * 描述：新闻公告 处理接口
 * 构建组：x7
 * 作者:heyf
 * 邮箱:heyf@jee-soft.cn
 * 日期:2020-04-02 18:17:27
 * 版权：广州宏天软件股份有限公司
 * </pre>
 */
public interface PressReleaseManager extends BaseManager<PressRelease>{

	List<PressRelease> getByType(String fLbtssfl,String FFbfs);

	List<Object> getNews();
	
}
