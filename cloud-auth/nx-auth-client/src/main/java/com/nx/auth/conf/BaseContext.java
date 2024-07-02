/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.conf;


import com.nx.auth.context.ContextUtil;
import com.nx.api.context.CurrentRuntimeContext;
import com.nx.auth.context.BootConstant;
import org.springframework.stereotype.Service;

/**
 *
 *  * public class UcContext implements BaseContext {
 *  * public class EmptyBaseContext implements BaseContext{
 *  * public class UcimplContext implements BaseContext {
 *
 * 当前登录用户的上下文数据
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月9日
 */
public interface BaseContext {

	String contextTemplateIdName = "tempTenantId";


	default String getCurrentUserId(){
		if(ContextUtil.authenticationEmpty()) {
			return BootConstant.PLATFORM_TENANT_ID;
		}
		return ContextUtil.getCurrentUserId();
	}

	//getCurrentTenantId
	default String getCurrentTenantId(){
		return ContextUtil.getCurrentTenantId();
	}



	default String getCurrentOrgId() {
		if(ContextUtil.authenticationEmpty()) {
			return BootConstant.PLATFORM_TENANT_ID;
		}
		return ContextUtil.getCurrentGroupId();
	}

	default String getCurrentUserAccout(){
		if(ContextUtil.authenticationEmpty()) {
			return BootConstant.PLATFORM_TENANT_ID;
		}
		return ContextUtil.getCurrentUser().getAccount();
	}


	default void setTempTenantId(String tenantId){
		CurrentRuntimeContext.addContextHeader(contextTemplateIdName,tenantId);
	}

	default void clearTempTenantId(){
		CurrentRuntimeContext.removeContextHeader(contextTemplateIdName);
	}

	@Service
	public class BootContextImpl implements BaseContext {}


}
