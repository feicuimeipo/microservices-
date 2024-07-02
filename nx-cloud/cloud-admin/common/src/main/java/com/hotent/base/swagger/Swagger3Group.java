/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.swagger;

import org.nianxi.api.feign.constant.ApiGroupConsts;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Swagger接口分组枚举
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年3月31日
 */

@Component
public class Swagger3Group implements ApiGroupConsts {

	@Override
	public Map<String, String> getGroups() {
		Map hash = new HashMap();
		hash.put(GROUP_BPM,"工作流");
		hash.put(GROUP_FORM,"表单");
		hash.put(GROUP_UC,"用户中心");
		hash.put(GROUP_PORTAL,"门户");
		hash.put(FEIGN_BPM,"工作流接口");
		hash.put(FEIGN_FORM,"表单接口");
		hash.put(FEIGN_UC,"用户接口");
		hash.put(FEIGN_PORTAL,"门户接口");
		return hash;
	}
}

