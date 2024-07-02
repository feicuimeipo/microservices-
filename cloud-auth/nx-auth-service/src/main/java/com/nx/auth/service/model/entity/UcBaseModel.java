/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.nx.mybatis.support.model.AutoFillModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * 基础实体类
 * @author Administrator
 *
 */
@Data
public abstract class UcBaseModel<T extends UcBaseModel<?>> extends AutoFillModel<T> implements Serializable{
	private static final long serialVersionUID = 3796984803158565007L;
	
	/**
	 * 0:false
	 * 1: true
	 * 是否已删除 0：未删除 1：已删除
	 */
	@TableLogic
	@TableField("IS_DELE_")
	@Schema(name="delete",description="是否已删除 0：未删除 1：已删除（新增、更新数据时不需要传入）")
	protected boolean delete =  false;
	
	/**
	 * 版本号
	 */
	@Version
	@TableField("VERSION_")
	@Schema(name="version",description="版本号（新增、更新数据时不需要传入）")
	protected String version;

	


}
