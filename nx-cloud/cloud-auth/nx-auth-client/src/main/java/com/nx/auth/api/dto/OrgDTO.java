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

import java.util.List;
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
@Schema(description="组织架构 ")
@Data
@ToString
public class OrgDTO extends BaseModel<OrgDTO> {



   /**
    * 主键
    */
   @Schema(name="id",description="组织id")
   protected String id;

   /**
    * name_
    */
   @Schema(name="name",description="组织名称")
   protected String name;

   /**
    * prent_id_
    */
   @Schema(name="parentId",description="组织父节点id")
   protected String parentId;

   /**
    * code_
    */
   @Schema(name="code",description="组织编码")
   protected String code;

   /**
    * 级别
    */
   @Schema(name="grade",description="组织级别")
   protected String grade;

   /**
    * 维度Id
    */
   @Schema(name="demId",description="维度id")
   protected String demId;

   @Schema(name="orderNo",description="序号")
   protected Long orderNo;

   /**
    * 上级组织名称
    */
   @Schema(name="parentOrgName",description="上级组织名称")
   protected  String parentOrgName;

   /**
    * 是否主组织。
    */
   @Schema(name="isMaster",description="是否主组织")
   private int isMaster=0;

   /**
    /**
    * 路径
    */
   @Schema(name="path",description="路径")
   protected String path;

   /**
    * 组织路径名
    */
   @Schema(name="pathName",description="组织路径名")
   protected String pathName;

   /**
    * 是否有子节点   否0  是1
    */
   @Schema(name="isIsParent",description="是否有子节点   否0  是1")
   protected int isParent = 0;

   /**
    * 组织参数
    */
   @Schema(name="params",description="组织参数（获取单个组织时才会有值）")
   protected Map<String,Object> params;

   /**
    * 维度名称
    */
   @Schema(name="demName",description="所属维度")
   protected String demName;


   @Schema(name = "demCode")
   protected String demCode;


   /**
    * OA关联ID
    */
   @Schema(name="refId",description="OA关联ID")
   protected String refId;

   /**
    * 组织用户关联id
    */
   @Schema(name="orgUserId",description="组织用户关联id")
   protected String orgUserId;


   @Schema(name="limitNum",description="组织限编用户数量(0:不受限制)")
   protected Integer limitNum=0;


   @Schema(name="nowNum",description="组织现编用户数量")
   protected Integer nowNum;


   @Schema(name="exceedLimitNum",description="是否允许超过限编(0:允许；1:不允许)")
   protected Integer exceedLimitNum=0;

   private String groupType = GroupTypeConstant.ORG.key();


   private List<OrgParamsDTO> orgParams;

   /**
    * 是否是叶子节点  true 是  false 不是
    */
   @Schema(name="isLeaf",description="是否是叶子节点  true 是  false 不是 ")
   protected boolean isLeaf = false;

   protected String isDelete = "0";


   public boolean isParent() {
      return isParent==1;
   }


   public boolean isLeaf() {
      return this.isParent==0;
   }




}