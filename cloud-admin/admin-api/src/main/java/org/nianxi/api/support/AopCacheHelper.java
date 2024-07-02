/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.support;


import com.hotent.ucapi.context.CacheKeyConst;
import com.pharmcube.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Deprecated
public class AopCacheHelper {
	@Resource
    CacheHelperService cacheHelper;


	@Cacheable(value = CacheKeyConst.DATA_PERMISSION_CACHENAME, key="#key")
	protected String getDataPermissionFromCache(String key) {
		return cacheHelper.getDataPermissionFromCache(key);
	}

	/**
	 * 由AdminConfig.sysLogsAspect代码
	 * @return
	 */
	protected Map<String, String> getSysLogsSettingStatusMap(){
		return cacheHelper.getSysLogsSettingStatusMap();
	}
}
