/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.security;

import com.nx.auth.support.AuthenticationUtil;
import com.nx.auth.api.dto.SysRoleAuthDTO;
import com.nx.auth.api.service.IMethodAuthService;
import com.nx.auth.api.utils.InternalBeanUtils;
import com.nx.api.context.SpringAppUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 认证
 */
@Service
public class HtInvocationSecurityMetadataSourceService  implements FilterInvocationSecurityMetadataSource {
	// security认证对象的线程变量 Authentication
	private static ThreadLocal<HashMap<String, Collection<ConfigAttribute>>> mapThreadLocal = new ThreadLocal<HashMap<String, Collection<ConfigAttribute>>>();


    /**
     * 加载权限表中所有权限
     * 
     * 同一个方法可能多个角色都具有访问权限
     * 
     */
	private void loadResourceDefine(){
        IMethodAuthService methodAuthService =  SpringAppUtils.getBean(IMethodAuthService.class);
        List<SysRoleAuthDTO> methodAuth = methodAuthService.getMethodAuthFromCache();// AuthApiFactory.getMethodAuthService().getMethodAuthFromCache();

    	HashMap<String, Collection<ConfigAttribute>> map = getMapThreadLocal();
        for(SysRoleAuthDTO mapAuth : methodAuth) {
            String roleAlias = mapAuth.getRoleAlias();
            String key = mapAuth.getMethodRequestUrl();
            if(StringUtils.isEmpty( mapAuth.getRoleAlias()) || StringUtils.isEmpty(mapAuth.getMethodRequestUrl()) ) {
            	continue;
            }
            ConfigAttribute cfg = new SecurityConfig(roleAlias);
            Collection<ConfigAttribute> array = map.containsKey(key)?map.get(key):new ArrayList<>();
        	array.add(cfg);
            map.put(key, array);  
        }
    }


    /**
     * 此方法是为了判定用户请求的url 是否在权限表中，如果在权限表中，则返回给 decide 方法，用来判定用户是否有此权限。如果不在权限表中则放行。
     * 返回空 ， 则不用经过 decide 方法 判断权限， 直接具有访问权限了
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        // object 中包含用户请求的request 信息
        HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
        //AntPathRequestMatcher matcher;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 匿名访问不需要获取权限信息
        if(AuthenticationUtil.isAnonymous(authentication)) return null;

        if(object instanceof FilterInvocation){
            FilterInvocation filterInvocation = (FilterInvocation) object;
            String method = filterInvocation.getRequest().getMethod();
            //String resUrl = filterInvocation.getRequestUrl();
            // options 返回null
            if(HttpMethod.OPTIONS.matches(method)){
                return null;
            }
        }
        
        loadResourceDefine();
        // 从线程中获取保证线程安全
        HashMap<String, Collection<ConfigAttribute>> map = getMapThreadLocal();
        for(Iterator<String> iter = map.keySet().iterator(); iter.hasNext(); ) {
            String resUrl = iter.next();
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(resUrl);
            if(matcher.matches(request)) {
                return map.get(resUrl);
            }
        }

        return null;
    }
    
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
    
    public static HashMap<String, Collection<ConfigAttribute>> getMapThreadLocal(){
    	HashMap<String, Collection<ConfigAttribute>> hashMap = mapThreadLocal.get();
    	if(InternalBeanUtils.isEmpty(hashMap)){
    		hashMap = new HashMap<>();
    		mapThreadLocal.set(hashMap);
    	}
    	return hashMap;
    }
    
    /**
     * 清空线程变量中的权限数据
     */
    public static void clearMapThreadLocal() {
    	HashMap<String, Collection<ConfigAttribute>> hashMap = mapThreadLocal.get();
		if(hashMap!=null) {
			hashMap.clear();
		}
    }
}