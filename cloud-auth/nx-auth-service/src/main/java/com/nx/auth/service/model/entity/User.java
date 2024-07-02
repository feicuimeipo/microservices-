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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nx.auth.context.BootConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
*
* <pre>
* 描述：用户表 实体对象
* 构建组：x5-bpmx-platform
* 作者:ray
* 邮箱:zhangyg@jee-soft.cn
* 日期:2016-06-30 10:26:50
* 版权：广州宏天软件有限公司
* </pre>
*/
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName("uc_user")
@Data
public class User extends UcBaseModel<User> {
   private static final long serialVersionUID = -4165513977160986324L;

   public final static String FROM_RESTFUL = "restful";
   public final static String FROM_AD = "AD";
   public final static String FROM_EXCEL = "EXCEL";
   public final static String FROM_WEBSERVICE = "webservice";

   public final static int STATUS_NORMAL = 1;
   public final static int STATUS_DISABLED = 0;
   public final static int STATUS_NOT_ACTIVE = -1;
   public final static int STATUS_LEAVE = -2;

   //是否删除
   public final static String DELETE_YES = "1";
   public final static String DELETE_NO = "0";

   //是否已经同步微信
   public final static int HASSYNCTOWX_YEX = 1;
   public final static int HASSYNCTOWX_NO = 0;

   /**
   * id_
   */
   @TableId("ID_")
   @Schema(name="id",description="用户id")
   protected String id;

   /**
   * 姓名
   */
   @TableField("FULLNAME_")
   @Schema(name="fullname",description="姓名")
   protected String fullname;

   /**
   * 账号
   */
   @TableField("ACCOUNT_")
   @Schema(name="account",description="账号")
   protected String account;

   /**
   * 密码
   */
   @TableField("PASSWORD_")
   @Schema(name="password",description="密码")
   protected String password;

   /**
   * 邮箱
   */
   @TableField("EMAIL_")
   @Schema(name="email",description="邮箱")
   protected String email;

   /**
   * 手机号码
   */
   @TableField("MOBILE_")
   @Schema(name="mobile",description="手机号码")
   protected String mobile;



   /**
   * 地址
   */
   @TableField("ADDRESS_")
   @Schema(name="address",description="地址")
   protected String address;

   /**
   * 头像
   */
   @TableField("PHOTO_")
   @Schema(name="photo",description="头像")
   protected String photo;

   /**
   * 性别：男，女，未知
   */
   @TableField("SEX_")
   @Schema(name="sex",description="性别")
   protected String sex;

   /**
   * 来源
   */
   @TableField("FROM_")
   @Schema(name="from",description="来源")
   protected String from="system";

   /**
   * 0:禁用，1正常，-1未激活，-2离职
   */
   @TableField("STATUS_")
   @Schema(name="status",description="0:禁用，1正常，-1未激活，-2离职")
   protected Integer status;


   /**
    * 组织ID，用于在组织下添加用户。
    */
   @TableField(exist=false)
   @Schema(name="groupId",description="组织ID，用于在组织下添加用户")
   protected String groupId="";

   /**
    * 微信同步关注状态  0：未同步  1：已同步，尚未关注  2：已同步且已关注
    */
   @TableField("HAS_SYNC_TO_WX_")
   @Schema(name="hasSyncToWx",description="微信同步关注状态")
   protected Integer hasSyncToWx=0;

   /**
    * 微信号
    */
   @TableField("WEIXIN_")
   @Schema(name="weixin",description="微信号")
   protected String weixin;

    /**
   * 消息通知类型
   */
   @TableField("NOTIFY_TYPE_")
   @Schema(name="notifyType",description="消息通知类型")
   protected String notifyType;

   /**
   * 工号
   */
   @TableField("USER_NUMBER_")
   @Schema(name="userNumber",description="工号")
   protected String userNumber;
   /**
   * 身份证号
   */
   @TableField("ID_CARD_")
   @Schema(name="idCard",description="身份证号")
   protected String idCard;
   /**
   * 办公电话
   */
   @TableField("PHONE_")
   @Schema(name="phone",description="办公电话")
   protected String phone;
   /**
   * 生日
   */
   @TableField("BIRTHDAY_")
   @Schema(name="birthday",description="生日")
   protected LocalDate birthday;
   /**
   * 入职日期
   */
   @TableField("ENTRY_DATE_")
   @Schema(name="entryDate",description="入职日期")
   protected LocalDate entryDate;
   /**
   * 离职日期
   */
   @TableField("LEAVE_DATE_")
   @Schema(name="leaveDate",description="离职日期")
   protected LocalDate leaveDate;
   /**
   * 学历
   */
   @TableField("EDUCATION_")
   @Schema(name="education",description="学历")
   protected String education;

   @TableField("tenant_id_")
   @Schema(name="tenantId",description="租户id")
   protected String tenantId;

   @TableField("PWD_CREATE_TIME_")
   @Schema(name="pwdCreateTime",description="密码策略时间")
   protected LocalDateTime pwdCreateTime;


   public boolean isAdmin() {
       String tmp = BootConstant.SYSTEM_ACCOUNT;
       String[] split = tmp.split(",");
       for (String _account : split) {
           if(_account.equals(this.account)){
               return true;
           }
       }

       return false;
   }


}