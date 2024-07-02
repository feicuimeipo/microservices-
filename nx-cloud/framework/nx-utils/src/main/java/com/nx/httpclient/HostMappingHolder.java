package com.nx.httpclient;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;



/**
 * 
 * <br>
 * Class Name   : HostMappingHolder
 *
 * @author jiangwei
 * @version 1.0.0
 * @date 2018年12月06日
 */
public class HostMappingHolder {

	private static final String DOT = ".";
	private static final String PATH_SEPARATOR = "/";
	private static final String LOCALHOST = "localhost";
	public static ProxyResolver proxyResolver;

	private static Map<String, String> proxyUriMappings = new HashMap<>();
	private static Map<String, String> contextPathMappings = new HashMap<>();

	
	public static void setProxyResolver(ProxyResolver proxyResolver) {
		HostMappingHolder.proxyResolver = proxyResolver;
	}

	private static Map<String, String> getProxyUrlMappings() {
		if(!proxyUriMappings.isEmpty())return proxyUriMappings;
		synchronized (proxyUriMappings) {
			if(!proxyUriMappings.isEmpty())return proxyUriMappings;
			Map<String, String> mappings = HttpClientConfig.getLoadBalanceCustomizeMapping();
			//
			String proxyUrl;
			for (String serviceId : mappings.keySet()) {
				proxyUrl = formatProxyUrl(mappings.get(serviceId));
				proxyUriMappings.put(serviceId.toLowerCase(), proxyUrl);
			}
			if(proxyUriMappings.isEmpty()){
				proxyUriMappings.put("nx", "nx");
			}
		}
		return proxyUriMappings;
	}
	
	private static Map<String, String> getContextPathMappings() {
		if(!contextPathMappings.isEmpty())return contextPathMappings;
		synchronized (contextPathMappings) {
			if(!contextPathMappings.isEmpty())return contextPathMappings;
			Map<String, String> mappingValues = HttpClientConfig.getLoadBalanceContextPathMapping();
			contextPathMappings = new HashMap<>(mappingValues.size());
			String contextPath;
			for (String serviceId : mappingValues.keySet()) {
				contextPath = mappingValues.get(serviceId);
				if(!contextPath.startsWith(File.separator)) {
					contextPath = File.separator + contextPath;
				}
				contextPathMappings.put(serviceId, contextPath);
			}
			//
			if(contextPathMappings.isEmpty()) {
				contextPathMappings.put("nx", "nx");
			}
		}
		return contextPathMappings;
	}
	
	private static String formatProxyUrl(String mappingUrl) {
		if(!mappingUrl.startsWith("http://")
				&& !mappingUrl.startsWith("https://")) {
			mappingUrl = "http://" + mappingUrl;
		}
		if(mappingUrl.endsWith("/")) {
			mappingUrl = mappingUrl.substring(0,mappingUrl.length() - 1);
		}
		return mappingUrl;
	}
	
	public static String getProxyUrlMapping(String name){
		return getProxyUrlMappings().get(name.toLowerCase());
	}
	
	public static void addProxyUrlMapping(String name,String mappingUrl) {
		getProxyUrlMappings().put(name.toLowerCase(), mappingUrl);
	}
	
	public static boolean containsProxyUrlMapping(String name) {
		return getProxyUrlMappings().containsKey(name.toLowerCase());
	}
	
	public static String getContextPathMapping(String name){
		return getContextPathMappings().get(name.toLowerCase());
	}
	
	public static boolean containsContextPathMapping(String name) {
		return getContextPathMappings().containsKey(name.toLowerCase());
	}
	
	public static String resolveUrl(String url){
		String serviceId = StringUtils.split(url, PATH_SEPARATOR)[1].toLowerCase();
		if(serviceId.startsWith(LOCALHOST))return url;
		Map<String, String> baseNameMappings = getProxyUrlMappings();
		String realUrl = null;
		if(baseNameMappings.containsKey(serviceId)){
			realUrl = baseNameMappings.get(serviceId);
		}else if(proxyResolver != null && !serviceId.contains(DOT)) {//不是ip或者域名
			realUrl = proxyResolver.resolve(serviceId);	
		}
		
		if(realUrl != null) {
			if(containsContextPathMapping(serviceId)) {
				realUrl = realUrl + getContextPathMapping(serviceId);
			}
			String baseUrl = url.substring(0,url.indexOf(serviceId) + serviceId.length());
			return url.replace(baseUrl, realUrl);
		}

		return url;
	}
}
