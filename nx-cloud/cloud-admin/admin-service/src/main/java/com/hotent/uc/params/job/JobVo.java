/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.params.job;

import com.hotent.uc.manager.OrgJobManager;
import com.hotent.uc.model.OrgJob;
import com.hotent.uc.util.OperateLogUtil;
import com.hotent.uc.util.UpdateCompare;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.utils.BeanUtils;




/**
 * 职务VO类
 * @author zhangxw
 *
 */
//@ApiModel
public class JobVo implements UpdateCompare {

	////@ApiModelProperty(name="name",notes="职务名称",required=true)
	private String name;
	
	////@ApiModelProperty(name="code",notes="职务编码",required=true)
	private String code;
	
	////@ApiModelProperty(name="postLevel",notes="职务级别")
	private String postLevel;
	
	////@ApiModelProperty(name="description",notes="职务描述")
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPostLevel() {
		return postLevel;
	}

	public void setPostLevel(String postLevel) {
		this.postLevel = postLevel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String compare() throws Exception {
	    OrgJobManager orgJobService =	AppUtil.getBean(OrgJobManager.class);
	    OrgJob job=orgJobService.getByCode(this.code);
		return OperateLogUtil.compare(this,changeVo(job));
	}


	public JobVo changeVo(OrgJob job) {
		JobVo newVo=new JobVo();
		if (BeanUtils.isEmpty(newVo)) return newVo;
		newVo.setCode(job.getCode());
		newVo.setDescription(job.getDescription());
		newVo.setName(job.getName());
		newVo.setPostLevel(job.getPostLevel());
		return newVo;
	}
	
}
