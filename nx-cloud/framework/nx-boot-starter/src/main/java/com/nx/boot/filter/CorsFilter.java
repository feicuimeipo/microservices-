/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: ...
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.boot.filter;

import com.nx.boot.launch.NxLaunchTools;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.filter.WebFilterOrderEnum;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 *  客户端环境复杂，仅使用origin来获取，不一定会满足，未来可修改为了original
 * @Author 李昊
 * 跨域访问过滤器
 */
@Configuration
@Order(WebFilterOrderEnum.CORS_FILTER)
@WebFilter(filterName="crossFilter",urlPatterns="/**")
@ConditionalOnProperty(prefix = "nx.cors",value = {"enabled"},havingValue = "true",matchIfMissing = true)
@ConfigurationProperties(prefix = "nx.cors")
public class CorsFilter implements Filter{

	public static String whiteList="*";

	private String allowOrigin="*";

	private String whiteListDomain="localhost,127.0.0.1,47.106.126.30";

	private String whiteListPort="80,1080,1081,8082";
	
	@Override
	public void destroy() {
		System.out.println("跨域访问过滤器销毁");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter："+ this.getClass().getSimpleName() +" enabled"});
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;

		if (whiteList==null || whiteList.length()==0) {
			whiteList = getWhiteList(this.whiteListDomain, this.whiteListPort);
		}

		//请求的地址
		String originUrl = request.getHeader("origin");
		//查看是否在白名单里面
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "referer,origin, x-requested-with, content-type,accept, user-agent, cookie, authorization,tenant-code");
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		response.setHeader("Access-Control-Max-Age", "3600");

		boolean isAllow = isAllow(originUrl,whiteList);
		if (isAllow) {
			response.setHeader("Access-Control-Allow-Origin", originUrl);
		} else {
			response.setHeader("Access-Control-Allow-Origin", allowOrigin);
		}
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(request, response);
		}
	}

	private static String getWhiteList(String allowDomain,String port){
		if (allowDomain.equals("*")) return  "*";

		String[] urls = allowDomain.toLowerCase().split(",");
		String[] ports = port.split(",");
		StringBuilder allowOrigins = new StringBuilder();
		for (String url : urls) {
			for (String p:ports){
				allowOrigins.append(",http://" + url + ":" + p);
				allowOrigins.append(",https://"+ url + ":" + p);
				allowOrigins.append(",ws://"+ url + ":" + p);
			}
		}
		//System.out.println("==============|允许跨域:"+ allowOrigins.toString()+"======================");
		return allowOrigins.toString().substring(1);
	}

	private static boolean isAllow(String originUrl,String whiteList){
		if (whiteList.equalsIgnoreCase("*")) return true;

		String origin = originUrl.toLowerCase();
		origin = origin.replace("http://","");
		origin = origin.replace("https://","");
		//origin = origin.substring(0,origin.indexOf(":"));

		String[] urls = whiteList.split(",");
		for (String url : urls) {

			url = url.replace("http://","");
			url = url.replace("https://","");
			url = url.replace("ws://","");

			if (url.startsWith("*.") || url.startsWith(".")) {
				if (url.startsWith("*.")) url = url.substring(2);
				if (url.startsWith(".")) url = url.substring(1);

				if(origin.endsWith(url)){
					System.out.println("===============1.请求的URL来源:"+origin+"|允许跨域:"+ url +"|结果:true======================");
					return true;
				}
			}else{
				if(origin.equalsIgnoreCase(url)){
					System.out.println("===============2.请求的URL来源:"+origin+"|允许跨域:"+ url +"|结果:true======================");
					return true;
				}
			}
		}
		System.out.println("===============3.跨域请求，请求的URL来源:"+origin+"|结果:false======================");
		return false;

	}


}
