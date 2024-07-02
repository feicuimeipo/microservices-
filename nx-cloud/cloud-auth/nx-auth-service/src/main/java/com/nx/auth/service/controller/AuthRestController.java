/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.controller;

import com.nx.api.model.R;
import com.nx.auth.conf.AuthConfig;
import com.nx.auth.security.jwt.JwtTokenHandler;
import com.nx.auth.service.conf.SsoConfig;
import com.nx.auth.service.model.bo.JwtAuthenticationRequest;
import com.nx.auth.service.model.bo.JwtAuthenticationResponse;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.api.exception.BaseException;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


@RestController
@Schema(description = "认证接口")
public class AuthRestController extends AbstractAuthController {

    @Resource
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected  UserCache userCache;

    @Autowired
    protected UserDetailsService userDetailsService;

    @Autowired
    protected AuthConfig authConfig;

    @Autowired
    protected SsoConfig ssoConfig;

    @Autowired
    protected JwtTokenHandler jwtTokenHandler;


    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces={"application/json; charset=utf-8" })
    @ApiResponse(description = "登录系统")
    public R<JwtAuthenticationResponse> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest)  {


        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        //清除用户缓存
        userCache.removeUserFromCache(username);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        //UserFacade userFacade = (UserFacade) authenticate.getPrincipal();
        if(Objects.isNull(authenticate)){
            throw new BaseException(401,"用户名或密码错误");
        }

        final UserFacade userFacade = (UserFacade) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        if (!userFacade.getStatus().equals("1")){
            throw new BaseException(401,"账号被禁用或离职");
        }
        //校验密码策略
        boolean  chckPwdStragy = super.chckPwdStragy(userFacade, password);
        if (!chckPwdStragy){
            throw new BaseException(401,"密码不符合规则!");
        }
        // 当前切中的方法
        HttpServletRequest request = ContextUtil.Request.getContextRequest();
        boolean isMobile = ContextUtil.Request.isMobile(request);


        //赋值
        UserFacade finalUserFacade = userFacade;
        final String token = jwtTokenHandler.generateToken(userFacade);
        String userName = finalUserFacade.getAccount();
        String account = finalUserFacade.getAccount();
        String userId = finalUserFacade.getId();
        String tenantId = finalUserFacade.getTenantId();
        String fullName = finalUserFacade.getFullname();

        request.setAttribute("loginUser", String.format("%s[%s]",fullName,account));
        Map<String,Object> userAttrs = new HashMap<String, Object>(){{
            put("tenantId", finalUserFacade.getTenantId());
        }};

        setCurrentRuntimeContext(userFacade, authConfig.isSingle(),isMobile,token,"jwt");

        //处理单用户登录
        handleSingleLogin(isMobile, tenantId, account, token, authConfig.getExpiration());

        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, fullName, account, userId, authConfig.getExpiration(), chckPwdStragy, userAttrs);

        return ResponseEntity.ok(response);
    }


    /**
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/sso/info", method = RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @Schema(description = "单点登录配置")
    public ResponseEntity<Map<String,Object>> isUseCas(HttpServletRequest request, HttpServletResponse response) throws IOException{
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("enable", ssoConfig.isSsoEnabled());
        map.put("ssoUrl", ssoConfig.getSsoUrl());
        map.put("ssoLogoutUrl", ssoConfig.getSsoLogoutUrl());
        return ResponseEntity.ok(map);
    }

    @RequestMapping(value = "${jwt.route.refresh:/refresh}", method = RequestMethod.GET)
    @Schema(description = "刷新token")
    public ResponseEntity<JwtAuthenticationResponse> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(authConfig.getTokenHeader());

        final String token = authToken.substring(7);
        String tenantId = jwtTokenHandler.getTenantIdFromToken(token);
        String account = jwtTokenHandler.getUsernameFromToken(token);
        String refreshedToken = jwtTokenHandler.refreshToken(token);

        boolean isMobile = ContextUtil.Request.isMobile(request);
        // 处理单用户登录 更新单用户登录的token
        handleSingleLogin(isMobile, tenantId, account, refreshedToken, authConfig.getExpiration());
        return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken, "", "", ""));
    }


    @Override
    protected JwtTokenHandler jwtTokenHandler() {
        return jwtTokenHandler;
    }

    @Override
    protected AuthConfig authConfig() {
        return authConfig;
    }

}