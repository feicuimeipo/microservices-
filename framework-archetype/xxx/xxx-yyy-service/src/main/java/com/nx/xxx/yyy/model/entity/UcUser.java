package com.nx.xxx.yyy.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nx.mybatis.support.model.DbBaseModel;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAttribute;
import java.time.LocalDateTime;


 /**
 * 用户管理
 * <pre> 
 * 描述：用户管理 实体对象
 * 构建组：x7
 * 作者:heyf
 * 邮箱:xlnian@163.com
 * 日期:2022-06-10 10:32:44
 * 版权：nx
 * </pre>
 */
 @Data
 @TableName("uc_user")
public class UcUser extends DbBaseModel<UcUser> {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name = "account")
	@TableField("ACCOUNT_")
	protected String account;
	

	@TableField("ADDRESS_")
	protected String address; 
	

	@TableField("BIRTHDAY_")
	protected LocalDateTime birthday; 
	

	@TableField("CREATE_BY_")
	protected String createBy; 
	

	@TableField("CREATE_ORG_ID_")
	protected String createOrgId; 
	

	@TableField("CREATE_TIME_")
	protected LocalDateTime createTime; 
	

	@TableField("EDUCATION_")
	protected String education; 
	

	@TableField("EMAIL_")
	protected String email; 
	

	@TableField("ENTRY_DATE_")
	protected LocalDateTime entryDate; 
	

	@TableField("FROM_")
	protected String from; 
	

	@TableField("FULLNAME_")
	protected String fullname; 
	

	@TableField("HAS_SYNC_TO_WX_")
	protected Integer hasSyncToWx; 

	@TableId("ID_")
	protected String id; 
	

	@TableField("ID_CARD_")
	protected String idCard; 
	

	@TableField("IS_DELE_")
	protected char isDele; 
	

	@TableField("LEAVE_DATE_")
	protected LocalDateTime leaveDate; 
	

	@TableField("MOBILE_")
	protected String mobile; 
	

	@TableField("NOTIFY_TYPE_")
	protected String notifyType; 
	

	@TableField("PASSWORD_")
	protected String password; 
	

	@TableField("PHONE_")
	protected String phone; 
	

	@TableField("PHOTO_")
	protected String photo; 
	

	@TableField("PWD_CREATE_TIME_")
	protected LocalDateTime pwdCreateTime; 
	

	@TableField("SEX_")
	protected String sex; 
	

	@TableField("STATUS_")
	protected Integer status; 
	

	@TableField("TENANT_ID_")
	protected String tenantId; 
	

	@TableField("UPDATE_BY_")
	protected String updateBy; 
	

	@TableField("UPDATE_TIME_")
	protected LocalDateTime updateTime; 
	

	@TableField("USER_NUMBER_")
	protected String userNumber; 
	

	@TableField("VERSION_")
	protected Integer version; 
	

	@TableField("WEIXIN_")
	protected String weixin; 

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this)
		.append("account", this.account) 
		.append("address", this.address) 
		.append("birthday", this.birthday) 
		.append("createBy", this.createBy) 
		.append("createOrgId", this.createOrgId) 
		.append("createTime", this.createTime) 
		.append("education", this.education) 
		.append("email", this.email) 
		.append("entryDate", this.entryDate) 
		.append("from", this.from) 
		.append("fullname", this.fullname) 
		.append("hasSyncToWx", this.hasSyncToWx) 
		.append("id", this.id) 
		.append("idCard", this.idCard) 
		.append("isDele", this.isDele) 
		.append("leaveDate", this.leaveDate) 
		.append("mobile", this.mobile) 
		.append("notifyType", this.notifyType) 
		.append("password", this.password) 
		.append("phone", this.phone) 
		.append("photo", this.photo) 
		.append("pwdCreateTime", this.pwdCreateTime) 
		.append("sex", this.sex) 
		.append("status", this.status) 
		.append("tenantId", this.tenantId) 
		.append("updateBy", this.updateBy) 
		.append("updateTime", this.updateTime) 
		.append("userNumber", this.userNumber) 
		.append("version", this.version) 
		.append("weixin", this.weixin) 
		.toString();
	}
}