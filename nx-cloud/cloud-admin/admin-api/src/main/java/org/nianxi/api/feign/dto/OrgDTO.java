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
public class OrgDTO implements java.io.Serializable{


   /**
    * 主键
    */
   //@TableId("ID_")
   @Schema(name="id",description="组织id")
   protected String id;

   /**
    * name_
    */
   //@TableField("NAME_")
   @Schema(name="name",description="组织名称")
   protected String name;

   /**
    * prent_id_
    */
   //@TableField("PARENT_ID_")
   @Schema(name="parentId",description="组织父节点id")
   protected String parentId;

   /**
    * code_
    */
   //@TableField("CODE_")
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
   protected int isIsParent = 0;

   /**
    * 组织参数
    */
   @Schema(name="params",description="组织参数（获取单个组织时才会有值）")
   protected Map<String,Object> params;

   /**
    * 维度名称
    */
   //@TableField(exist=false)
   @Schema(name="demName",description="所属维度")
   protected String demName;

   //@TableField(exist=false)
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

   /**
    * 是否是叶子节点  true 是  false 不是
    */
   @Schema(name="isLeaf",description="是否是叶子节点  true 是  false 不是 ")
   protected boolean isLeaf = false;

   public Integer getExceedLimitNum() {
      return exceedLimitNum;
   }

   public void setExceedLimitNum(Integer exceedLimitNum) {
      this.exceedLimitNum = exceedLimitNum;
   }

   public Integer getLimitNum() {
      return limitNum;
   }

   public void setLimitNum(Integer limitNum) {
      this.limitNum = limitNum;
   }

   public Integer getNowNum() {
      return nowNum;
   }

   public void setNowNum(Integer nowNum) {
      this.nowNum = nowNum;
   }

   public String getOrgUserId() {
      return orgUserId;
   }

   public void setOrgUserId(String orgUserId) {
      this.orgUserId = orgUserId;
   }

   public String getPathName() {
      return pathName;
   }

   public boolean isIsParent() {
      return isIsParent==1;
   }

   public void setIsParent(int isIsParent) {
      this.isIsParent = isIsParent;
   }

   public void setPathName(String pathName) {
      this.pathName = pathName;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public void setParentOrgName(String parentOrgName) {
      this.parentOrgName = parentOrgName;
   }

   /**
    * 返回 主键
    * @return
    */
   public String getParentOrgName() {
      return this.parentOrgName;
   }

   public void setOrderNo(Long orderNo) {
      this.orderNo = orderNo;
   }


   public void setId(String id) {
      this.id = id;
   }

   /**
    * 返回 主键
    * @return
    */
   public String getId() {
      return this.id;
   }

   public void setName(String name) {
      this.name = name;
   }

   /**
    * 返回 name_
    * @return
    */
   public String getName() {
      return this.name;
   }

   public void setParentId(String parentId) {
      this.parentId = parentId;
   }

   /**
    * 返回 prent_id_
    * @return
    */
   public String getParentId() {
      return this.parentId;
   }

   public void setCode(String code) {
      this.code = code;
   }

   public String getDemId() {
      return demId;
   }

   public void setDemId(String demId) {
      this.demId = demId;
   }

   /**
    * 返回 code_
    * @return
    */
   public String getCode() {
      return this.code;
   }

   public void setGrade(String grade) {
      this.grade = grade;
   }

   /**
    * 返回 级别
    * @return
    */
   public String getGrade() {
      return this.grade;
   }

   protected String isDelete = "0";
   private String version;


   public String getGroupId() {
      return this.id;
   }

   public String getGroupCode() {
      return this.code;
   }

   public Long getOrderNo() {

      return this.orderNo;
   }

   public String getPath() {
      return this.path;
   }

   public int getIsMaster() {
      return isMaster;
   }

   public void setIsMaster(int isMaster) {
      this.isMaster = isMaster;
   }

   public Map<String, Object> getParams() {
      return params;
   }

   public void setParams(Map<String, Object> params) {
      this.params = params;
   }

   public String getDemName() {
      return demName;
   }

   public void setDemName(String demName) {
      this.demName = demName;
   }

   public String getRefId() {
      return refId;
   }

   public void setRefId(String refId) {
      this.refId = refId;
   }


   public String getIdentityType() {
      return IdentityType.GROUP;
   }


   public String getGroupType() {
      return GroupTypeConstant.ORG.key();
   }


   public GroupStructEnum getStruct() {
      return null;
   }

   public String getDemCode() {
      return demCode;
   }

   public void setDemCode(String demCode) {
      this.demCode = demCode;
   }

   public boolean isLeaf() {
      return this.isIsParent==0;
   }

   public void setLeaf(boolean isLeaf) {
      this.isLeaf = isLeaf;
   }


}