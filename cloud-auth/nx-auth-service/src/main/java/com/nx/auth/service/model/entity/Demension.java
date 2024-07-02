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
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
*
* <pre>
* 描述：维度管理 实体对象
* 构建组：x5-bpmx-platform
* 作者:liyg
* 邮箱:liyg@jee-soft.cn
* 日期:2017-07-19 15:30:09
* 版权：广州宏天软件有限公司
* </pre>
*/
@TableName("UC_DEMENSION")
@Schema(description="维度管理")
public class Demension extends UcBaseModel<Demension>{

   /**
    *
    */
   private static final long serialVersionUID = 3525497630012830993L;

   /**
   * 维度id
   */
   @TableId("ID_")
   @Schema(name="id",description="维度id")
   protected String id;

   /**
    * 维度编码
    */
   @TableField("CODE_")
   @Schema(name="demCode",description="维度编码")
   protected String demCode;

   /**
   * 维度名称
   */
   @TableField("DEM_NAME_")
   @Schema(name="demName",description="维度名称")
   protected String demName;

   /**
   * 描述
   */
   @TableField("DEM_DESC_")
   @Schema(name="demDesc",description="描述")
   protected String demDesc;


   /**
    * 是否默认
    */
   @TableField("IS_DEFAULT_")
   @Schema(name="isDefault",description="是否默认")
   protected int isDefault = 0;


   @TableField("ORGAN_ID_")
   @Schema(name="isDefault",description="机构ID")
   protected int organId ;


   public void setId(String id) {
       this.id = id;
   }

   /**
    * 返回 维度id
    * @return
    */
   public String getId() {
       return this.id;
   }

   public int getOrganId() {
       return organId;
   }

   public void setOrganId(int organId) {
       this.organId = organId;
   }

   /**
    * 返回 维度编码
    * @return
    */
   public String getDemCode() {
       return demCode;
   }

   public void setDemCode(String demCode) {
       this.demCode = demCode;
   }

   public void setDemName(String demName) {
       this.demName = demName;
   }

   /**
    * 返回 维度名称
    * @return
    */
   public String getDemName() {
       return this.demName;
   }

   public void setDemDesc(String demDesc) {
       this.demDesc = demDesc;
   }

   /**
    * 返回 描述
    * @return
    */
   public String getDemDesc() {
       return this.demDesc;
   }

   /**
    * 返回 是否默认
    * @return
    */
   public int getIsDefault() {
       return isDefault;
   }

   public void setIsDefault(int isDefault) {
       this.isDefault = isDefault;
   }


   public String getCode() {
       return this.demCode;
   }

   public String getName() {
       return this.demName;
   }

   /**
    * @see Object#toString()
    */
   public String toString() {
       return new ToStringBuilder(this)
       .append("id", this.id)
       .append("demCode", this.demCode)
       .append("demName", this.demName)
       .append("demDesc", this.demDesc)
       .append("name", this.demName)
       .append("code", this.demCode)
       .append("isDefault", this.isDefault)
       .append("delete",this.delete)
       .append("version",this.version)
       .toString();
   }
}