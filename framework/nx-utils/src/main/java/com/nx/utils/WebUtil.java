package com.nx.utils;


import com.nx.httpclient.HttpClientConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;


public class WebUtil {
	private static final String HEADER_FORWARDED_HOST = "X-Forwarded-Host";
	private static final String HEADER_REQUESTED_WITH = "X-Requested-With";
	private static final String HEADER_FORWARDED_PREFIX = "X-FORWARDED-Prefix";
	private static final String HEADER_FORWARDED_PROTO = "X-Forwarded-Proto";
	private static final String HEADER_FORWARDED_PORT = "X-Forwarded-Port";
	public static final String HEADER_INTERNAL_REQUEST = "X-INTERNAL-REQUEST";
	public static final String HEADER_SERVICE_PROTOCOL = "service-protocol";

	public static final String HEADER_INVOKER_IS_GATEWAY = "INVOKER-IS-GATEWAY";

	public static final String HEADER_REAL_IP = "X-Real-IP";
//	public static final String DOT = ".";
//	public static final String COLON = ":";
	public static final String CUSTOMER_PROPERTY_PREFIX = "nx";

	private static final String POINT = ".";
	private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
	private static final String MULTIPART = "multipart/";
	private static List<String> doubleDomainSuffixs = Arrays.asList(".com.cn",".org.cn",".net.cn");
	//内网解析域名
	private static List<String> internalDomains  = HttpClientConfig.getDomainsList();// SpringUtils.getList("internal.dns.domains");
	
	
	public static boolean isAjax(HttpServletRequest request){
	    return  (request.getHeader(HEADER_REQUESTED_WITH) != null && XML_HTTP_REQUEST.equalsIgnoreCase(request.getHeader(HEADER_REQUESTED_WITH).toString())) ;

//		String requestType = request.getHeader("X-Requested-With");
//		//如果requestType能拿到值，并且值为 XMLHttpRequest ,表示客户端的请求为异步请求，那自然是ajax请求了，反之如果为null,则是普通的请求
//		if(requestType == null){
//			return false;
//		}
//		return true;
	}
	
	public static  void responseOutJson(HttpServletResponse response,String json) {  
	    //将实体对象转换为JSON Object转换  
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");  
	    PrintWriter out = null;  
	    try {  
	        out = response.getWriter();  
	        out.append(json);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    }  
	}  
	
	public static  void responseOutHtml(HttpServletResponse response,String html) {  
	    //将实体对象转换为JSON Object转换  
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("text/html; charset=utf-8");  
	    PrintWriter out = null;  
	    try {  
	        out = response.getWriter();  
	        out.append(html);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    }  
	}  
	
	public static  void responseOutJsonp(HttpServletResponse response,String callbackFunName,Object jsonObject) {  
	    //将实体对象转换为JSON Object转换  
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("text/plain; charset=utf-8");  
	    PrintWriter out = null;  
	    
	    String json = (jsonObject instanceof String) ? jsonObject.toString() : JsonUtil.toJson(jsonObject);
	    String content = callbackFunName + "("+json+")";
	    try {  
	        out = response.getWriter();  
	        out.append(content);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    }  
	} 


	
	/**
	 * 获取根域名
	 * @param request
	 * @return
	 */
	public static  String getRootDomain(HttpServletRequest request) {
		String host = request.getHeader(HEADER_FORWARDED_HOST);
		//StringBuffer url = request.getRequestURL();
		if(StringUtils.isBlank(host)){
			host = request.getServerName();
		}
		return parseHostRootDomain(host);
	}
	
	public static  String getRootDomain(String url) {
		String host = getDomain(url);
		return parseHostRootDomain(host);
	}
	
	private static String parseHostRootDomain(String host){
		if(IpUtil.isIp(host) || IpUtil.LOCAL_HOST.equals(host)){
			return host;
		}
		String[] segs = StringUtils.split(host, POINT);
		int len = segs.length;
		
		if(doubleDomainSuffixs.stream().anyMatch(e -> host.endsWith(e))){
			return segs[len - 3] + POINT + segs[len - 2] + POINT + segs[len - 1];
		}
		return segs[len - 2] + POINT + segs[len - 1];
	}
	
	public static  String getDomain(String url) {
		String[] urlSegs = StringUtils.split(url,"/");
		return urlSegs[1];
	}
	
	public static  String getOriginDomain(HttpServletRequest request) {
		String originUrl = request.getHeader("Origin");
		if(originUrl == null) {
			originUrl = request.getHeader("Referer");
		}
		if(originUrl != null) {
			return getDomain(originUrl);
		}
		return request.getServerName();
	}
	
	public static  String getBaseUrl(String url) {
		String[] segs = StringUtils.split(url,"/");
		return segs[0] + "//" + segs[1];
	}
	
	public static String getBaseUrl(HttpServletRequest request){
		return getBaseUrl(request, true);
	}
	
	/**
	 * 获取baseurl<br>
	 * nginx转发需设置 proxy_set_header   X-Forwarded-Proto $scheme;
	 * @param request
	 * @param withContextPath
	 * @return
	 */
	public static String getBaseUrl(HttpServletRequest request,boolean withContextPath){
        String baseUrl = null;
        
        String schame = request.getHeader(HEADER_FORWARDED_PROTO);
		String host = request.getHeader(HEADER_FORWARDED_HOST);
		String prefix = request.getHeader(HEADER_FORWARDED_PREFIX);
		if(StringUtils.isBlank(host)){
			String[] segs = StringUtils.split(request.getRequestURL().toString(),"/");
			baseUrl = segs[0] + "//" + segs[1];
		}else{
			if(StringUtils.isBlank(schame)){
				String port = request.getHeader(HEADER_FORWARDED_PORT);
				schame = "443".equals(port) ? "https://" : "http://";
			}else{
				if(schame.contains(",")){
					schame = StringUtils.split(schame, ",")[0];
				}
				schame = schame + "://";
			}
			if(host.contains(",")){
				host = StringUtils.split(host, ",")[0];
			}
			baseUrl = schame + host + StringUtils.trimToEmpty(prefix);
		}
		
		if(withContextPath && StringUtils.isNotBlank(request.getContextPath())){
			baseUrl = baseUrl + request.getContextPath();
		}
		
		return baseUrl;
	}
	
	public static final boolean isMultipartContent(HttpServletRequest request) {
		if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
			return false;
		}
		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}
		contentType = contentType.toLowerCase(Locale.ENGLISH);
		if (contentType.startsWith(MULTIPART) 
				|| "application/octet-stream".equals(contentType)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 是否内网调用
	 * @param request
	 * @return
	 */
	public static boolean isInternalRequest(HttpServletRequest request){
		//
		String headerValue = request.getHeader(HEADER_INTERNAL_REQUEST);
		if(Boolean.parseBoolean(headerValue)){
			return true;
		}

		String serviceProtocol = request.getHeader(HEADER_SERVICE_PROTOCOL);
		if(serviceProtocol!=null || serviceProtocol.trim().length()==0){
			return true;
		}

		//从网关转发
		headerValue = request.getHeader(HEADER_INVOKER_IS_GATEWAY);
		if(Boolean.parseBoolean(headerValue)){
			return true;
		}

		String clientIp = request.getHeader(HEADER_REAL_IP);
		if(clientIp == null)clientIp = IpUtil.getIpAddr(request);
		if(IpUtil.isInnerIp(clientIp)) {
			return true;
		}
		
		boolean isInner = IpUtil.isInnerIp(request.getServerName());
		if(!isInner && !internalDomains.isEmpty()){
			isInner = internalDomains.stream().anyMatch(domain -> request.getServerName().endsWith(domain));
		}
		
		return isInner;
	}
	
	public static void printRequest(HttpServletRequest request) {
		System.out.println("============Request start==============");
		System.out.println("uri:" + request.getRequestURI());
		System.out.println("method:" + request.getMethod());
		System.out.println("clientIP:" + IpUtil.getIpAddr(request));
		Enumeration<String> headerNames = request.getHeaderNames();
		String headerName;
		while(headerNames.hasMoreElements()) {
			headerName = headerNames.nextElement();
			System.out.println(String.format("Header %s = %s", headerName,request.getHeader(headerName)));
		}
		System.out.println("============Request end==============");
	}
}
