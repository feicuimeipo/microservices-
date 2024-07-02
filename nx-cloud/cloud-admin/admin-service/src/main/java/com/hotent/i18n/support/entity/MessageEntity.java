/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.i18n.support.entity;

import java.io.Serializable;
import java.util.Map;

/**
 * 资源对象
 * @author zhaoxy
 * @company 广州宏天软件股份有限公司
 * @email zhxy@jee-soft.cn
 * @date 2018-06-06 14:20
 */
public class MessageEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	private String key;						/*资源KEY*/
	private Map<String, String> values;		/*资源类型和资源值的键值对集合*/
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Map<String, String> getValues() {
		return values;
	}
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
}
