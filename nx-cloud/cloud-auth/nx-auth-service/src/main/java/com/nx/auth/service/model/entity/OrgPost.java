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
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


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
@TableName("UC_ORG_POST")
@Schema(description="组织岗位")
public class OrgPost extends UcBaseModel<OrgPost>  {

	private static final long serialVersionUID = 1233764588726416781L;
	/**
	* id_
	*/
	@TableId("ID_")
	@Schema(name="id",description="岗位id")
	protected String id; 
	
	/**
	* org_id_
	*/
	@TableField("ORG_ID_")
	@Schema(name="orgId",description="所属组织id")
	protected String orgId; 
	
	/**
	* job_id_
	*/
	@TableField("JOB_ID_")
	@Schema(name="relDefId",description="所属职务id")
	protected String relDefId;
	
	/**
	 * 主岗位
	 */
	@TableField("IS_CHARGE_")
	@Schema(name="isCharge",description="是否主岗位")
	protected Integer isCharge=0;
	
	
	
	/**
	* name
	*/
	@TableField("POS_NAME_")
	@Schema(name="name",description="岗位名称")
	protected String name; 
	
	/**
	* code_
	*/
	@TableField("CODE_")
	@Schema(name="code",description="岗位编码")
	protected String code; 
	/**
	 * job_code_
	 */
	@TableField(exist=false)
	@Schema(name = "jobCode",description = "所属职务Code")
	protected String jobCode;
	
	@TableField(exist=false)
	@Schema(name="orgName",description="所属组织名称")
	protected String orgName; 
	
	@TableField(exist=false)
	@Schema(name="jobName",description="所属职务名称")
	protected String jobName; 
	
	@TableField(exist=false)
	@Schema(name="demName",description="所属维度名称")
	protected String demName;
	
	@TableField(exist=false)
	@Schema(name="orgCode",description="所属组织编号")
	protected String orgCode;
	

	@TableField(exist=false)
	@Schema(name="pathName",description="岗位组织全路径")
    protected String pathName;

    public String getPathName() {
    	if (StringUtils.isNotEmpty(demName)) {
    		return demName+pathName;
		}
        return pathName;
    }



}
