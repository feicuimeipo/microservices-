/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.conf;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.pharmcube.api.model.crypt.Base64;
//import org.nianxi.utils.JsonUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//import java.util.HashMap;
//import java.util.Map;

///**
// *
// * @author liyg
// *
// */
//@Component
//@ConfigurationProperties(prefix="pharmcube.auth")
//public class SystemYmlConfig {
//
//	private Logger logger = LoggerFactory.getLogger(SystemYmlConfig.class);
//
//	private Map<String,String> basic = new HashMap<String, String>();
//
//	public Map<String, String> getBasic() {
//		return basic;
//	}
//
//	public void setBasic(Map<String, String> basic) {
//		this.basic = basic;
//	}
//
//	/**
//	 *
//	 * @return  {"Authorization":"Basic YWRtaW46MQ=="} 的base64编码
//	 */
//	public String getToken(){
//		if(basic.containsKey("token")){
//			return basic.get("token");
//		}
//		return "eyJBdXRob3JpemF0aW9uIjoiQmFzaWMgWVdSdGFXNDZNVEl6TkRVMiJ9";
//	}
//
//	/**
//	 *
//	 * @return Basic YWRtaW46MQ==
//	 */
//	public String getBasicToken(){
//		String token = getToken();
//		try {
//			String fromBase64 = Base64.getFromBase64(token);
//			JsonNode jsonNode = JsonUtil.toJsonNode(fromBase64);
//			return jsonNode.get("Authorization").asText();
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("设置的basic token 有问题");
//		}
//		return "";
//	}
//
//	private String getBasicTokenUsernameAndPassord(){
//		String result = "admin:123456";
//		try {
//			String basic = getBasicToken();
//			if(basic.startsWith("Basic ")){
//				basic = basic.substring(6);
//				result = Base64.getFromBase64(basic);
//			}
//		} catch (Exception e) {
//		}
//		return result;
//	}
//
//	/**
//	 * 返回设置basic token 中的用户名
//	 * @return
//	 */
//	public String getUsername(){
//		return getBasicTokenUsernameAndPassord().split(":")[0];
//	}
//
//	/**
//	 * 返回设置basic token 中的密码
//	 * @return
//	 */
//	public String getPassword(){
//		return getBasicTokenUsernameAndPassord().split(":")[1];
//	}
//
//
//}
