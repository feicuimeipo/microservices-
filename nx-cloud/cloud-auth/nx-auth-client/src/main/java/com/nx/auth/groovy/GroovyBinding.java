/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.groovy;

import groovy.lang.Binding;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Groovy 绑定
 * 
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年4月11日
 */
public class GroovyBinding extends Binding {
	@SuppressWarnings("unused")
	private Map<?, ?> variables;
	private static ThreadLocal<Map<String, Object>> localVars = new ThreadLocal<Map<String, Object>>();

	private static Map<String, Object> propertyMap = new HashMap<String, Object>();

	public GroovyBinding() {
	}

	public GroovyBinding(Map<String, Object> variables) {
		localVars.set(variables);
	}

	public GroovyBinding(String[] args) {
		this();
		setVariable("args", args);
	}

	public Object getVariable(String name) {
		Map<String, Object> map = localVars.get();
		Object result = null;
		if (map != null && map.containsKey(name)) {
			result = map.get(name);
		} else {
			result = propertyMap.get(name);
		}

		return result;
	}

	public void setVariable(String name, Object value) {
		if (localVars.get() == null) {
			Map<String, Object> vars = new LinkedHashMap<String, Object>();
			vars.put(name, value);
			localVars.set(vars);
		} else {
			localVars.get().put(name, value);
		}
	}

	@SuppressWarnings("rawtypes")
	public Map<?, ?> getVariables() {
		if (localVars.get() == null) {
			return new LinkedHashMap();
		}
		return localVars.get();
	}

	public void clearVariables() {
		localVars.remove();
	}

	public Object getProperty(String property) {
		return propertyMap.get(property);
	}

	public void setProperty(String property, Object newValue) {
		propertyMap.put(property, newValue);
	}
}
