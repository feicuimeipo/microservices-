/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package org.nianxi.api.feign.dto.form;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "表单服务")
public class FormRestfulModelDTO implements java.io.Serializable{
	@Schema(name="boid",description="boid")
	protected String boid;
	@Schema(name="defId",description="defId")
	protected String defId;
	@Schema(name="boData",description="boDataJson数据")
	protected ObjectNode boData;
	@Schema(name="saveType",description="保存方式")
	protected String saveType;
	@Schema(name="bocode",description="bo别名")
	protected String code;
	@Schema(name="formkey",description="表单key")
	protected String formkey;
	@Schema(name="userId",description="用户ID")
	protected String userId;
	@Schema(name="flowKey",description="流程key")
	protected String flowKey;
    @Schema(name="flowDefId",description="流程定义id")
    protected String flowDefId;
	@Schema(name="nodeId",description="节点ID")
	protected String nodeId;
	@Schema(name="nextNodeId",description="下一个节点ID")
	protected String nextNodeId;
	@Schema(name="parentFlowKey",description="parentFlowKey")
	protected String parentFlowKey;
	@Schema(name="permissionType",description="权限类型")
	protected int permissionType;
	@Schema(name="isGlobalPermission",description="是否获取全局表单权限")
	protected boolean isGlobalPermission;
	protected String parentDefKey;
    
}
