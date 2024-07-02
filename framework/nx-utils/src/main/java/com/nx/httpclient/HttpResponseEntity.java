package com.nx.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.nx.common.exception.BaseException;
import com.nx.common.model.PageResult;
import com.nx.common.model.Result;
import com.nx.utils.HttpClientJsonUtil;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * <br>
 * Class Name   : HttpResponseEntity
 *
 */
public class HttpResponseEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private static final String msgAlias = "message";
	private static final List<String> successCodes = Arrays.asList("200","0000","0");

	private int statusCode;
	private String body;
	private Map<String, String> headers;
	private String bizCode;
	private String message;
	private JsonNode bodyJsonObject;
	
	private Boolean successed;
	private Boolean isJson;
	
	
	public HttpResponseEntity() {}
	
	public HttpResponseEntity(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}


	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getBody() {
		return body;
	}
	
	public String getBizCode() {
		return bizCode;
	}

	public boolean isJson() {
		if(isJson == null) {
			isJson = body != null && HttpClientJsonUtil.isJsonString(body);
		}
		return isJson;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}
	
	public void addHeader(String name,String value) {
		if(headers == null)headers = new HashMap<>();
		headers.put(name, value);
	}
	

	public JsonNode getBodyJsonObject() {
		if(bodyJsonObject == null) {
			handleBizException();
		}
		return bodyJsonObject;
	}

	public String getUnwrapBody() {
		if(!isSuccessed()) {
			throw new BaseException(getStatusCode(),getMessage());
		}
		return body;
		
	}
	
	public <T> T toObject(Class<T> clazz) {
		String json = getUnwrapBody();
		if(!isJson)return null;
		return HttpClientJsonUtil.toObject(json, clazz);
	}
	
	public <T> List<T> toList(Class<T> clazz) {
		String json = getUnwrapBody();
		if(!isJson)return null;
		return HttpClientJsonUtil.toList(json, clazz);
	}
	
	public String toValue(String selectNode) {
		String value = HttpClientJsonUtil.getJsonNodeValue(getUnwrapBody(), selectNode);
		return value;
	}
	
	public <T> T toObject(Class<T> clazz,String selectNode) {
		String json = getUnwrapBody();
		if(!isJson)return null;
		json = HttpClientJsonUtil.getJsonNodeValue(json, selectNode);
		return HttpClientJsonUtil.toObject(json, clazz);
	}
	
	public <T> List<T> toList(Class<T> clazz,String selectNode) {
		String json = getUnwrapBody();
		if(!isJson)return null;
		json = HttpClientJsonUtil.getJsonNodeValue(json, selectNode);
		return HttpClientJsonUtil.toList(json, clazz);
	}
	
	public <T> PageResult<T> toPage(Class<T> clazz) {
		String json = getUnwrapBody();
		JsonNode jsonNode = HttpClientJsonUtil.selectJsonNode(json, null);
		PageResult<T> page = new PageResult<>();
		page.setTotal(jsonNode.get("total").asLong());
		page.setList(HttpClientJsonUtil.toList(jsonNode.get("records").toString(), clazz));
		return page;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public boolean isSuccessed(){
		handleBizException();
		return successed;
	}
	

	public String getMessage() {
		handleBizException();
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public BaseException buildException() {
		return new BaseException(getStatusCode(), getMessage());
	}
	
	public void appendResponseLog(StringBuilder builder) {
		builder.append("\nresponse");
		builder.append("\n - statusCode:").append(statusCode);
		if(body != null)builder.append("\n - body:").append(body);
		builder.append("\n---------------backend request trace end--------------------");
	}
	
	
	private void handleBizException() {
		if(successed != null)return;
		successed = statusCode == HttpURLConnection.HTTP_OK  || (statusCode >= 200 && statusCode <= 210);
		if(successed && isJson()) {
			bodyJsonObject = HttpClientJsonUtil.toJsonNode(body);
			//
			if(!bodyJsonObject.has(Result.PARAM_CODE)) {
				return;
			}

			//
			String code = bodyJsonObject.get(Result.PARAM_CODE).asText();
			if (code.equals("0") || code.equals("200")){
				successed = true;
			}

			if(bodyJsonObject.size() > 1
					&& !bodyJsonObject.has(Result.PARAM_DATA)
					&& !bodyJsonObject.has(Result.PARAM_MSG)
					&& !bodyJsonObject.has(msgAlias)) {
				return;
			}

			if(successCodes.contains(Result.PARAM_DATA)) {
				bodyJsonObject = bodyJsonObject.get(Result.PARAM_DATA);
				if(bodyJsonObject instanceof NullNode) {
					body = null;
				}else {
					body = bodyJsonObject.toString();
				}
			}
			
			if(!successed && message == null) {
				message = "http请求错误";
			}
		}
	}

	@Override
	public String toString() {
		return "[statusCode=" + statusCode + ", body=" + body + ", message=" + message + "]";
	}

}
