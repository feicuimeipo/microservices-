/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.persistence.manager;

import java.util.List;
import java.util.Map;

import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.sys.persistence.model.SysLogs;

/**
 * 
 * <pre> 
 * 描述：系统操作日志 处理接口
 * 构建组：x7
 * 作者:liyg
 * 邮箱:liygui@jee-soft.cn
 * 日期:2018-08-31 10:59:25
 * 版权：广州宏天软件有限公司
 * </pre>
 */
public interface SysLogsManager extends BaseManager<SysLogs>{

	void removeByEexcutionTime(List<Map<String, Object>> params);
	
}
