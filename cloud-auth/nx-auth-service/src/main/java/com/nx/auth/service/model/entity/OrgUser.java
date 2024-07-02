/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@TableName("UC_ORG_USER")
@Schema(description="组织用户关系")
public class OrgUser extends UcBaseModel<OrgUser>  {

	private static final long serialVersionUID = 2541505317450158908L;
	/**
	 * 主关系
	 */
	public static final Integer MASTER_YES=1;
	
	/**
	 * 非主关系
	 */
	public static final Integer MASTER_NO=0;
	
	
	/**
	* id_
	*/
	@TableId("ID_")
	@Schema(name="id",description="用户组织关系id")
	protected String id; 
	
	/**
	* org_id_
	*/
	@TableField("ORG_ID_")
	@Schema(name="orgId",description="组织id")
	protected String orgId; 
	
	/**
	* user_id_
	*/
	@TableField("USER_ID_")
	@Schema(name="userId",description="用户id")
	protected String userId; 
	
	/**
	* 0:非主部门，1：主部门
	*/
	@TableField("IS_MASTER_")
	@Schema(name="isMaster",description="0:非主组织，1：主组织")
	protected Integer isMaster; 
	
	/**
	 * 0： 非负责人 ， 1: 负责人， 2 部门的主负责人
	 */
	@TableField("IS_CHARGE_")
	@Schema(name="isCharge",description="0： 非负责人 ， 1: 负责人， 2 部门的主负责人")
	protected Integer isCharge=0;
	
	/**
	* rel_id_
	*/
	@TableField("POS_ID_")
	@Schema(name="relId",description="岗位id")
	protected String relId; 
	
	/**
	 * 开始生效日期
	 */
	@TableField("START_DATE_")
	@Schema(name="startDate",description="开始生效日期")
	protected LocalDateTime startDate; 
	
	/**
	 * 结束日期
	 */
	@TableField("END_DATE_")
	@Schema(name="endDate",description="结束日期")
	protected LocalDateTime endDate; 
	
	/**
	 * 是否生效
	 */
	@TableField("IS_REL_ACTIVE_")
	@Schema(name="isRelActive",description="是否生效")
	protected int isRelActive = 1;
	
	/*扩展字段，仅用于关联查询时使用*/
	@TableField(exist=false)
	@Schema(name="orgCode",description="组织代码", hidden=true)
	protected String orgCode;
	
	@TableField(exist=false)
	@Schema(name="posCode",description="岗位代码", hidden=true)
	protected String posCode;



}
