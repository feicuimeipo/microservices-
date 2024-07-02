/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.support;

@Service
public class CacheHelperService {
	@Resource
	AdminServiceApi portalFeignService;

	@Cacheable(value = CacheKeyConst.DATA_PERMISSION_CACHENAME, key="#key")
	protected String getDataPermissionFromCache(String key) {
		return key;
	}

	/**
	 * 由AdminConfig.sysLogsAspect代码
	 * @return
	 */
	@Cacheable(value = CacheKeyConst.LOGS_SETTING_STATUS_CACHENAME, key=CacheKeyConst.SYS_LOGS_SETTING_STATUS, pureKey = true)
	protected Map<String, String> getSysLogsSettingStatusMap(){
		return portalFeignService.getSysLogsSettingStatusMap();
	}

	@CacheEvict(value = "user:details", key = "#userAccount")
	public void delUserDetailsCache(String userAccount) {}

}
