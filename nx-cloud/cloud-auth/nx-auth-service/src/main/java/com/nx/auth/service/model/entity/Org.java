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

import java.util.Map;


/**
 * 
 * <pre> 
 * 描述：组织架构 实体对象
 * 构建组：x5-bpmx-platform
 * 作者:ray
 * 邮箱:zhangyg@jee-soft.cn
 * 日期:2016-06-28 15:13:03
 * 版权：广州宏天软件有限公司
 * </pre>
 */
@TableName("UC_ORG")
@Schema(description="组织架构 ")
@Data
public class Org extends UcBaseModel<Org> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7138977532880036358L;
	//private static final long serialVersionUID = 7138977532880036358L;

	/**
	* 主键
	*/
	@TableId("ID_")
	@Schema(name="id",description="组织id")
	protected String id; 
	
	/**
	* name_
	*/
	@TableField("NAME_")
	@Schema(name="name",description="组织名称")
	protected String name; 

	/**
	* prent_id_
	*/
	@TableField("PARENT_ID_")
	@Schema(name="parentId",description="组织父节点id")
	protected String parentId; 
	
	/**
	* code_
	*/
	@TableField("CODE_")
	@Schema(name="code",description="组织编码")
	protected String code; 
	
	/**
	* 级别
	*/
	@TableField("GRADE_")
	@Schema(name="grade",description="组织级别")
	protected String grade; 
	
	/**
	 * 维度Id
	 */
	@TableField("DEM_ID_")
	@Schema(name="demId",description="维度id")
	protected String demId;


	@TableField("ORDER_NO_")
	@Schema(name="orderNo",description="序号")
	protected Long orderNo; 
	
	/**
	 * 上级组织名称
	 */
	@TableField(exist=false)
	@Schema(name="parentOrgName",description="上级组织名称")
	protected  String parentOrgName;
	
	/**
	 * 是否主组织。
	 */
	@TableField(exist=false)
	@Schema(name="isMaster",description="是否主组织")
	private int isMaster=0;
	
	/**
	/**
	 * 路径
	 */
	@TableField("PATH_")
	@Schema(name="path",description="路径")
	protected String path;
	
	/**
	 * 组织路径名
	 */
	@TableField("PATH_NAME_")
	@Schema(name="pathName",description="组织路径名")
	protected String pathName;
	/**
	 * 是否有子节点   否0  是1  
	 */
	@TableField(exist=false)
	@Schema(name="isIsParent",description="是否有子节点   否0  是1")
	protected int isIsParent = 0;
	
	/**
	 * 组织参数
	 */
	@TableField(exist=false)
	@Schema(name="params",description="组织参数（获取单个组织时才会有值）")
	protected Map<String,Object> params;
	
	/**
	 * 维度名称
	 */
	@TableField(exist=false)
	@Schema(name="demName",description="所属维度")
	protected String demName;
	
	@TableField(exist=false)
	@Schema(name = "demCode")
	protected String demCode;



	 /**
	 * OA关联ID
	 */
	@TableField(exist=false)
	@Schema(name="refId",description="OA关联ID")
	protected String refId;
	
	/**
	 * 组织用户关联id
	 */
	@TableField(exist=false)
	@Schema(name="orgUserId",description="组织用户关联id")
	protected String orgUserId;
	
	
	@TableField("LIMIT_NUM_")
	@Schema(name="limitNum",description="组织限编用户数量(0:不受限制)")
	protected Integer limitNum=0;
	
	
	@TableField("NOW_NUM_")
	@Schema(name="nowNum",description="组织现编用户数量")
    protected Integer nowNum;
	
	
	@TableField("EXCEED_LIMIT_NUM_")
	@Schema(name="exceedLimitNum",description="是否允许超过限编(0:允许；1:不允许)")
    protected Integer exceedLimitNum=0;
	
	/**
	 * 是否是叶子节点  true 是  false 不是  
	 */
	@TableField(exist=false)
	@Schema(name="isLeaf",description="是否是叶子节点  true 是  false 不是 ")
	protected boolean isLeaf = false;


}