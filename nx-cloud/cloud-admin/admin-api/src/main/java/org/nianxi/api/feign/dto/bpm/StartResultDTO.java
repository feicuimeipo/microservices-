/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto.bpm;

//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;
import org.nianxi.api.model.CommonResult;
//import org.nianxi.api.model.CommonResult;

/**
 * 启动流程的结果
 * 
 * @company 广州宏天软件股份有限公司
 * @author zhangxianwen
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月28日
 */
@Schema(description="启动流程的结果")
@ToString
public class StartResultDTO extends CommonResult implements java.io.Serializable{

	@Schema(name="message",description="流程实例ID",example="10000000000001",required=true)
	private String instId;

	public StartResultDTO(String instId){
		this.instId = instId;
	}

	public StartResultDTO(String message, String instId){
		super(message);
		this.instId = instId;
	}
	
	public String getInstId() {
		return instId;
	}
	public void setInstId(String instId) {
		this.instId = instId;
	}
}
