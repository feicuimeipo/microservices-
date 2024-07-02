/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto.bpm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;

import java.util.Map;

/**
 * 流程启动参数
 * 
 * @company 广州宏天软件股份有限公司
 * @author zhangxianwen
 * @email zhangxw@jee-soft.cn
 * @date 2018年6月28日
 */
@Schema(description="流程启动参数")
@ToString
public class StartFlowParamObjectDTO implements java.io.Serializable{
	
	@Schema(name="account",description="发起人帐号",example="admin")
	private String account;
	
	@Schema(name="defId",description="流程定义id，流程定义id与流程key必填其中一个")
	private String defId;
	
	@Schema(name="flowKey",description="流程key，流程定义id与流程key必填其中一个")
	private String flowKey;
	
	@Schema(name="subject",description="流程标题，不填则按流程定义中设置的标题规则生成")
	private String subject;
	
	@Schema(name="proInstId",description="流程实例id")
	private String proInstId;
	
	@Schema(name="vars",description="流程变量，变量名：变量值，如{\"var1\":\"val1\",\"var2\":\"val2\"...}")
	private Map<String,String> vars;
	
	@Schema(name="data",description="bo业务数据，以base64加密后的密文")
	private String data;
	
	@Schema(name="businessKey",description="业务主键KEY，只对URL表单形式有效")
	private String businessKey;
	
	@Schema(name="sysCode",description="业务系统编码，只对URL表单形式有效")
	private String sysCode;
	
	@Schema(name="formType",description="表单类型（inner,frame）")
	private String formType;
	
	@Schema(name="nodeUsers",description="下一节点人员")
	private String nodeUsers;
	
	@Schema(name="isSendNodeUsers",description="是否自由选择人员作为下一节点执行人")
	private int isSendNodeUsers;
	
	@Schema(name="destination",description="跳转目标节点")
	private String destination;
	
    @Schema(name="expression",description="审批意见")
    private String expression;
    
    @Schema(name="startOrgId",description="发起人组织id")
    private String startOrgId;
    
    @Schema(name="urgentStateValue",description="紧急状态的值")
    private ObjectNode urgentStateValue;
    
    @Schema(name="agentLeaderId",description="被代理的领导id")
    private String agentLeaderId;

    @Schema(name="isApproval",description="是否从待办审批页面点的保存")
    private Boolean isApproval = false;
    /**
     * 支持手机表单。
     */
    @Schema(name="supportMobile",description="是否支持手机表单 0：否，1：是")
    protected int supportMobile=0;
    
    @Schema(name="taskId",description="任务id")
    private String taskId;

    public int getSupportMobile() {
        return supportMobile;
    }

    public void setSupportMobile(int supportMobile) {
        this.supportMobile = supportMobile;
    }

    public Boolean getApproval() {
        return isApproval;
    }

    public void setApproval(Boolean approval) {
        isApproval = approval;
    }

    public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getIsSendNodeUsers() {
		return isSendNodeUsers;
	}

	public void setIsSendNodeUsers(int isSendNodeUsers) {
		this.isSendNodeUsers = isSendNodeUsers;
	}

	public String getNodeUsers() {
		return nodeUsers;
	}

	public void setNodeUsers(String nodeUsers) {
		this.nodeUsers = nodeUsers;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getDefId() {
		return defId;
	}

	public void setDefId(String defId) {
		this.defId = defId;
	}

	public String getFlowKey() {
		return flowKey;
	}

	public void setFlowKey(String flowKey) {
		this.flowKey = flowKey;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getProInstId() {
		return proInstId;
	}

	public void setProInstId(String proInstId) {
		this.proInstId = proInstId;
	}

	public Map<String, String> getVars() {
		return vars;
	}

	public void setVars(Map<String, String> vars) {
		this.vars = vars;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public String getSysCode() {
		return sysCode;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getStartOrgId() {
		return startOrgId;
	}

	public void setStartOrgId(String startOrgId) {
		this.startOrgId = startOrgId;
	}

	public ObjectNode getUrgentStateValue() {
		return urgentStateValue;
	}

	public void setUrgentStateValue(ObjectNode urgentStateValue) {
		this.urgentStateValue = urgentStateValue;
	}

	public String getAgentLeaderId() {
		return agentLeaderId;
	}

	public void setAgentLeaderId(String agentLeaderId) {
		this.agentLeaderId = agentLeaderId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	
}
