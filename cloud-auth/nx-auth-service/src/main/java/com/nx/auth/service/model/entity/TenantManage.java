/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.nx.mybatis.support.model.AutoFillModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.time.LocalDateTime;

import static com.nx.auth.context.BootConstant.TENANT_STATUS_ENABLE;


/**
 * UC_TENANT_MANAGE
 * <pre> 
 * 描述：租户管理实体对象
 * 构建组：x7
 * 作者:zhangxw
 * 邮箱:zhangxw@jee-soft.cn
 * 日期:2020-04-17 10:56:07
 * 版权：广州宏天软件股份有限公司
 * </pre>
 */
@Data
@TableName("UC_TENANT_MANAGE")
@Schema(description = "租户管理")
public class TenantManage extends AutoFillModel<TenantManage> {

	private static final long serialVersionUID = 1L;
	public final static String STATUS_ENABLE = TENANT_STATUS_ENABLE;
	public final static String STATUS_DISABLED = "disabled";
	public final static String STATUS_DRAFT = "draft";

	@XmlTransient
	@TableId("id_")
	@Schema(description="主键")
	protected String id; 

	@XmlAttribute(name = "typeId")
	@TableField("TYPE_ID_")
	@Schema(description="租户类型id")
	protected String typeId; 

	@XmlAttribute(name = "name")
	@TableField("NAME_")
	@Schema(description="租户名称")
	protected String name; 

	@XmlAttribute(name = "code")
	@TableField("CODE_")
	@Schema(description="租户别名（编码）")
	protected String code; 

	@XmlAttribute(name = "status")
	@TableField("STATUS_")
	@Schema(description="状态：启用（enable）、禁用（disabled）")
	protected String status; 

	@XmlAttribute(name = "shorthand")
	@TableField("SHORTHAND_")
	@Schema(description="租户简称")
	protected String shorthand; 

	@XmlAttribute(name = "domain")
	@TableField("DOMAIN_")
	@Schema(description="域名地址")
	protected String domain; 

	@XmlAttribute(name = "manageLogo")
	@TableField("MANAGE_LOGO_")
	@Schema(description="管理端logo附件id")
	protected String manageLogo; 

	@XmlAttribute(name = "frontLogo")
	@TableField("FRONT_LOGO_")
	@Schema(description="用户端logo附件id")
	protected String frontLogo; 

	@XmlAttribute(name = "ico")
	@TableField("ICO_")
	@Schema(description="地址栏ICO图标附件id")
	protected String ico; 

	@XmlAttribute(name = "desc")
	@TableField("DESC_")
	@Schema(description="描述")
	protected String desc; 

	@TableField(exist=false)
	@Schema(name="typeName",description="租户类型名称")
	protected String typeName="";
	
	@Schema(description = "创建人ID", hidden=true, accessMode=  Schema.AccessMode.READ_ONLY)
	@TableField(value="create_by_", fill=FieldFill.INSERT)
	private String createBy;

	@Schema(description = "创建人组织ID", hidden=true, accessMode= Schema.AccessMode.READ_ONLY)
	@TableField(value="create_org_id_", fill=FieldFill.INSERT)
	private String createOrgId;
	
	@Schema(description = "创建时间", hidden=true, accessMode= Schema.AccessMode.READ_ONLY)
	@TableField(value="create_time_", fill=FieldFill.INSERT)
	private LocalDateTime createTime;
	
	@Schema(description = "更新人ID", hidden=true, accessMode= Schema.AccessMode.READ_ONLY)
	@TableField(value="update_by_", fill=FieldFill.UPDATE)
	private String updateBy;

	@Schema(description = "更新时间", hidden=true, accessMode= Schema.AccessMode.READ_ONLY)
	@TableField(value="update_time_", fill=FieldFill.UPDATE)
	private LocalDateTime updateTime;

	/**
	 * 0: false
	 * 1: true
	 */
	@TableLogic
	@TableField("IS_DELE_")
	@Schema(name="isDelete",description="是否已删除 0：未删除 1：已删除（新增、更新数据时不需要传入）")
	protected boolean isDelete = false;


}