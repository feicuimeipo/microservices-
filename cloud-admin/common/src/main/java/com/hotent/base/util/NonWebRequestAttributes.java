/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.util;

import org.springframework.web.context.request.RequestAttributes;

public class NonWebRequestAttributes implements RequestAttributes {

	@Override
	public Object getAttribute(String name, int scope) {
		return null;
	}

	@Override
	public void setAttribute(String name, Object value, int scope) {
		
	}

	@Override
	public void removeAttribute(String name, int scope) {
	}

	@Override
	public String[] getAttributeNames(int scope) {
		return null;
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback, int scope) {
		
	}

	@Override
	public Object resolveReference(String key) {
		return null;
	}

	@Override
	public String getSessionId() {
		return null;
	}

	@Override
	public Object getSessionMutex() {
		return null;
	}

}
