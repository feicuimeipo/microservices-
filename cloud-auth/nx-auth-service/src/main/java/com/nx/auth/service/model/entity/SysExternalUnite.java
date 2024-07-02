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
import com.nx.mybatis.support.model.AutoFillModel;
import io.swagger.v3.oas.annotations.media.Schema;


@TableName("portal_sys_external_unite")
@Schema(description = "附件分类信息")
public class SysExternalUnite extends AutoFillModel<SysExternalUnite> {


    @Schema(name="id", description="主键")
    @TableId("id_")
    protected String id;

    @Schema(description = "企业名称")
    @TableField("CORP_NAME_")
    protected String corpName;

    @Schema(description="企业id")
    @TableField("CORP_ID_")
    protected String corpId;

    @Schema(description="企业secret")
    @TableField("CORP_SECRET_")
    protected String corpSecret;

    @Schema(description="集成类型")
    @TableField("TYPE_")
    protected String type;



    @Schema(description="本系统地址")
    @TableField("BASE_URL_")
    protected String baseUrl;

    @Schema(description="应用id")
    @TableField("AGENT_ID_")
    protected String agentId;

    @Schema(description="应用key")
    @TableField("AGENT_KEY_")
    protected String agentKey;

    @Schema(description="菜单名称")
    @TableField("MENU_NAME_")
    protected String menuName;

    @Schema(description="应用secret")
    @TableField("AGENT_SECRET_")
    protected String agentSecret;


    @Schema(description="菜单url")
    @TableField("MENU_URL_")
    protected String menuUrl;
    
    @Schema(description="公众号模板消息id")
    @TableField("TEMP_MSG_ID_")
    protected String tempMsgId;

    @Schema(description="是否发布")
    @TableField(exist=false)
    protected String isPublish;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getCorpSecret() {
        return corpSecret;
    }

    public void setCorpSecret(String corpSecret) {
        this.corpSecret = corpSecret;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentKey() {
        return agentKey;
    }

    public void setAgentKey(String agentKey) {
        this.agentKey = agentKey;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getAgentSecret() {
        return agentSecret;
    }

    public void setAgentSecret(String agentSecret) {
        this.agentSecret = agentSecret;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getTempMsgId() {
		return tempMsgId;
	}

	public void setTempMsgId(String tempMsgId) {
		this.tempMsgId = tempMsgId;
	}

	public String getIsPublish() {
        return isPublish;
    }

    public void setIsPublish(String isPublish) {
        this.isPublish = isPublish;
    }
}
