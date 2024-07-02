/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.sys.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.nianxi.boot.annotation.ExcelColumn;




//@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDictExcelVo {
	
	////@ApiModelProperty(name = "pidKey", notes = "父节点key", required = false)
    @ExcelColumn(value = "pidKey", col = 0)
	protected String pidKey;
	
	////@ApiModelProperty(name = "key", notes = "节点key", required = true)
    @ExcelColumn(value = "key", col = 1)
	protected String key;
	
	////@ApiModelProperty(name = "name", notes = "节点名称", required = true)
    @ExcelColumn(value = "name", col = 2)
	protected String name;

	public DataDictExcelVo() {
	}

	public DataDictExcelVo(String pidKey, String key, String name) {
		this.pidKey = pidKey;
		this.key = key;
		this.name = name;
	}

	public String getPidKey() {
		return pidKey;
	}

	public void setPidKey(String pidKey) {
		this.pidKey = pidKey;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
