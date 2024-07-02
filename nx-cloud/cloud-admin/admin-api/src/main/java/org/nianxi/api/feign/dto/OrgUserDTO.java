/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;


//@TableName("UC_ORG_USER")
@Schema(description="组织用户关系")
@Data
@ToString
public class OrgUserDTO implements java.io.Serializable {

	/**
	 * 主关系
	 */
	public static final Integer MASTER_YES=1;
	
	/**
	 * 非主关系
	 */
	public static final Integer MASTER_NO=0;

	/**
	 * 是否已删除 0：未删除 1：已删除
	 */
	@Schema(name="isDelete",description="是否已删除 0：未删除 1：已删除（新增、更新数据时不需要传入）")
	protected String isDelete = "0";

	/**
	 * 版本号
	 */
	@Schema(name="version",description="版本号（新增、更新数据时不需要传入）")
	protected Integer version;



	/**
	* id_
	*/
	// @TableId("ID_")
	@Schema(name="id",description="用户组织关系id")
	protected String id; 
	
	/**
	* org_id_
	*/
	// @TableId("ORG_ID_")
	@Schema(name="orgId",description="组织id")
	protected String orgId; 
	
	/**
	* user_id_
	*/
	// @TableId("USER_ID_")
	@Schema(name="userId",description="用户id")
	protected String userId; 
	
	/**
	* 0:非主部门，1：主部门
	*/
	// @TableId("IS_MASTER_")
	@Schema(name="isMaster",description="0:非主组织，1：主组织")
	protected Integer isMaster; 
	
	/**
	 * 0： 非负责人 ， 1: 负责人， 2 部门的主负责人
	 */
	// @TableId("IS_CHARGE_")
	@Schema(name="isCharge",description="0： 非负责人 ， 1: 负责人， 2 部门的主负责人")
	protected Integer isCharge=0;
	
	/**
	* rel_id_
	*/
	// @TableId("POS_ID_")
	@Schema(name="relId",description="岗位id")
	protected String relId; 
	
	/**
	 * 开始生效日期
	 */
	// @TableId("START_DATE_")
	@Schema(name="startDate",description="开始生效日期")
	protected LocalDateTime startDate; 
	
	/**
	 * 结束日期
	 */
	// @TableId("END_DATE_")
	@Schema(name="endDate",description="结束日期")
	protected LocalDateTime endDate; 
	
	/**
	 * 是否生效
	 */
	// @TableId("IS_REL_ACTIVE_")
	@Schema(name="isRelActive",description="是否生效")
	protected int isRelActive = 1;
	
	/*扩展字段，仅用于关联查询时使用*/
	// @TableId(exist=false)
	@Schema(name="orgCode",description="组织代码", hidden=true)
	protected String orgCode;
	
	// @TableId(exist=false)
	@Schema(name="posCode",description="岗位代码", hidden=true)
	protected String posCode;

	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 返回 id_
	 * @return
	 */
	public String getId() {
		return this.id;
	}
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	/**
	 * 返回 org_id_
	 * @return
	 */
	public String getOrgId() {
		return this.orgId;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	/**
	 * 返回 user_id_
	 * @return
	 */
	public String getUserId() {
		return this.userId;
	}
	
	public void setIsMaster(Integer isMaster) {
		this.isMaster = isMaster;
	}
	
	/**
	 * 返回 0:非主部门，1：主部门
	 * @return
	 */
	public Integer getIsMaster() {
		return this.isMaster;
	}
	
	public Integer getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(Integer isCharge) {
		this.isCharge = isCharge;
	}

	public void setRelId(String relId) {
		this.relId = relId;
	}
	
	/**
	 * 返回 rel_id_
	 * @return
	 */
	public String getRelId() {
		return this.relId;
	}
	
	/**
	 * 返回 开始生效日期
	 * @return
	 */
	public LocalDateTime getStartDate() {
		return startDate;
	}

	
	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	/**
	 * 返回 结束日期
	 * @return
	 */
	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	/**
	 * 返回 是否生效
	 * @return
	 */
	public int getIsRelActive() {
		return isRelActive;
	}

	public void setIsRelActive(int isRelActive) {
		this.isRelActive = isRelActive;
	}
	
	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getPosCode() {
		return posCode;
	}

	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}


}
