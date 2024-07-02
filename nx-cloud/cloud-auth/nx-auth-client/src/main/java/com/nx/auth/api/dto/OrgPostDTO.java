/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.api.dto;

import com.nx.api.model.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
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
public class OrgPostDTO extends BaseModel<OrgPostDTO> {

	/**
	* id_
	*/
	@Schema(name="id",description="岗位id")
	protected String id; 
	
	/**
	* org_id_
	*/
	@Schema(name="orgId",description="所属组织id")
	protected String orgId; 
	
	/**
	* job_id_
	*/
	@Schema(name="relDefId",description="所属职务id")
	protected String relDefId;
	
	/**
	 * 主岗位
	 */
	@Schema(name="isCharge",description="是否主岗位")
	protected Integer isCharge=0;
	
	
	
	/**
	* name
	*/
	@Schema(name="name",description="岗位名称")
	protected String name; 
	
	/**
	* code_
	*/
	@Schema(name="code",description="岗位编码")
	protected String code; 
	/**
	 * job_code_
	 */
	@Schema(name = "jobCode",description = "所属职务Code")
	protected String jobCode;
	
	@Schema(name="orgName",description="所属组织名称")
	protected String orgName; 
	
	@Schema(name="jobName",description="所属职务名称")
	protected String jobName; 
	
	@Schema(name="demName",description="所属维度名称")
	protected String demName;
	
	@Schema(name="orgCode",description="所属组织编号")
	protected String orgCode;
	

	@Schema(name="pathName",description="岗位组织全路径")
    protected String pathName;

    public String getPathName() {
    	if (demName!=null && demName.trim().length()>0) {
    		return demName+pathName;
		}
        return pathName;
    }




	public String getGroupId() {
		return this.id;
	}


	public Long getOrderNo() {
		return Long.valueOf(0);
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
