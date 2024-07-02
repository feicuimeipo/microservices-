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
import org.nianxi.api.feign.constant.GroupStructEnum;
import org.nianxi.api.feign.constant.GroupTypeConstant;
import org.nianxi.api.feign.constant.IdentityType;

import java.util.Map;


/**
 * 
 * <pre> 
 * 描述：组织岗位  实体对象
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-30 10:26:10
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@Data
@ToString
@Schema(description="组织岗位")
public class OrgPostDTO  implements java.io.Serializable{

	/**
	* id_
	*/
	//@TableId("ID_")
	@Schema(name="id",description="岗位id")
	protected String id; 
	
	/**
	* org_id_
	*/
	//@TableField("ORG_ID_")
	@Schema(name="orgId",description="所属组织id")
	protected String orgId; 
	
	/**
	* job_id_
	*/
	//@TableField("JOB_ID_")
	@Schema(name="relDefId",description="所属职务id")
	protected String relDefId;
	
	/**
	 * 主岗位
	 */
	//@TableField("IS_CHARGE_")
	@Schema(name="isCharge",description="是否主岗位")
	protected Integer isCharge=0;
	
	
	
	/**
	* name
	*/
	//@TableField("POS_NAME_")
	@Schema(name="name",description="岗位名称")
	protected String name; 
	
	/**
	* code_
	*/
	//@TableField("CODE_")
	@Schema(name="code",description="岗位编码")
	protected String code; 
	/**
	 * job_code_
	 */
	//@TableField(exist=false)
	@Schema(name = "jobCode",description = "所属职务Code")
	protected String jobCode;
	
	//@TableField(exist=false)
	@Schema(name="orgName",description="所属组织名称")
	protected String orgName; 
	
	//@TableField(exist=false)
	@Schema(name="jobName",description="所属职务名称")
	protected String jobName; 
	
	//@TableField(exist=false)
	@Schema(name="demName",description="所属维度名称")
	protected String demName;
	
	//@TableField(exist=false)
	@Schema(name="orgCode",description="所属组织编号")
	protected String orgCode;
	

	//@TableField(exist=false)
	@Schema(name="pathName",description="岗位组织全路径")
    protected String pathName;

    public String getPathName() {
    	if (demName!=null && demName.trim().length()>0) {
    		return demName+pathName;
		}
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	
	
	/**
	 * 返回 id_
	 * @return
	 */
	public String getOrgName() {
		return this.orgName;
	}
	
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	/**
	 * 返回 id_
	 * @return
	 */
	public String getJobName() {
		return this.jobName;
	}
	
	public void setId(String id) {
		this.id = id;
	}


	public String getDemName() {
		return demName;
	}



	public void setDemName(String demName) {
		this.demName = demName;
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
	
	public void setRelDefId(String relDefId) {
		this.relDefId = relDefId;
	}
	
	/**
	 * 返回 rel_def_id_
	 * @return
	 */
	public String getRelDefId() {
		return this.relDefId;
	}
	
	
	
	public String getCode() {
		return code;
	}



	public void setCode(String code) {
		this.code = code;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getName() {
		return name;
	}



	/**
	 * 返回  是否主岗位
	 * @return
	 */
	public Integer getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(Integer isCharge) {
		this.isCharge = isCharge;
	}



	public String getGroupId() {
		return this.id;
	}


	public Long getOrderNo() {
		return Long.valueOf(0);
	}




	public String getIdentityType() {
		return IdentityType.GROUP;
	}




	public String getGroupCode() {
		return this.code;
	}




	public String getGroupType() {
		return GroupTypeConstant.POSITION.key();
	}




	public GroupStructEnum getStruct() {
		return null;
	}


	public String getParentId() {
		return null;
	}



	public String getPath() {
		return null;
	}


	public Map<String, Object> getParams() {
		return null;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}
}
