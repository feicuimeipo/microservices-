/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.model.bo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private String token;
    private String username;
    private String account;
    private String userId;
    private String openid;
    private Long expiration;
    private boolean loginStatus = true;
    
    private Map<String,Object> userAttrs = new HashMap<String, Object>();

    public JwtAuthenticationResponse(String token, String username,String account,String userId) {
        this.token = token;
        this.username = username;
        this.account = account;
        this.userId = userId;
    }

    public JwtAuthenticationResponse(String token, String username, String account, String userId, Long expiration, Map<String,Object> userAttrs) {
		this.token = token;
		this.username = username;
		this.account = account;
		this.userId = userId;
		this.expiration = expiration;
		this.userAttrs = userAttrs;
	}

	public JwtAuthenticationResponse(String token, String username, String account, String userId, Long expiration,
			boolean loginStatus, Map<String,Object> userAttrs) {
		this.token = token;
		this.username = username;
		this.account = account;
		this.userId = userId;
		this.expiration = expiration;
		this.loginStatus = loginStatus;
		this.userAttrs = userAttrs;
	}

	public JwtAuthenticationResponse(String openid) {
        this.openid = openid;
    }

    public String getOpenid() {
        return this.openid;
    }

    public String getToken() {
        return this.token;
    }
    
    public String getUsername() {
    	return this.username;
    }

	public String getAccount() {
		return account;
	}

	public String getUserId() {
		return userId;
	}

	public Long getExpiration() {
		return expiration;
	}

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public Map<String,Object> getUserAttrs() {
		return userAttrs;
	}

	public void setUserAttrs(Map<String,Object> userAttrs) {
		this.userAttrs = userAttrs;
	}
	
}
