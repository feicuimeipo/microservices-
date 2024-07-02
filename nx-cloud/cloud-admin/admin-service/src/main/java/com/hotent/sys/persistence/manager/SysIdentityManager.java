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

import org.nianxi.api.model.CommonResult;
import com.pharmcube.mybatis.support.manager.BaseManager;
import com.hotent.sys.persistence.model.SysIdentity;

public interface SysIdentityManager extends BaseManager<SysIdentity>{
	/**
	 * 判读流水号别名是否已经存在
	 * @param id  id为null 表明是新增的流水号，否则为更新流水号
	 * @param alias
	 * @return
	 */
	boolean isAliasExisted(Map<String,Object> params);

	/**
	 * 根据别名获取当前流水号
	 * @param alias
	 * @return
	 */
	public String getCurIdByAlias(String alias);

    CommonResult<String> getNextIdByAlias(String alias) throws Exception;

    /**
	 * 根据别名获取下一个流水号
	 * @param alias
	 * @return
	 */
	public String nextId(String alias);
	
	/**
	 * 根据别名预览前十条流水号
	 * @param alias
	 * @return
	 */
	public List<SysIdentity> getPreviewIden(String alias);
	
}
