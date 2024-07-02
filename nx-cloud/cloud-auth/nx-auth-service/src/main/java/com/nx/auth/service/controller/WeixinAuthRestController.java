/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.service.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nx.auth.conf.AuthConfig;
import com.nx.auth.security.jwt.JwtTokenHandler;
import com.nx.auth.service.conf.SsoConfig;
import com.nx.auth.service.model.bo.JwtAuthenticationResponse;
import com.nx.auth.service.model.entity.User;
import com.nx.auth.service.service.AuthService;
import com.nx.utils.JsonUtil;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.api.context.SpringAppUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@RestController
@Schema(description = "认证接口")
@Slf4j
public class WeixinAuthRestController extends AbstractAuthController {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UserCache userCache;


    @Autowired
    protected AuthConfig authConfig;

    @Autowired
    protected SsoConfig ssoConfig;

    @Autowired
    protected JwtTokenHandler jwtTokenHandler;

    @RequestMapping(value = "/sso/weixin", method = RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description = "企业微信应用进入手机端-单点登录")
    public ResponseEntity<JwtAuthenticationResponse> ssoWeixin(@RequestParam Optional<String> code, HttpServletRequest request) throws Exception {



        String username = null;
        String userInfoUrl = getUserInfoUrl("weChatWork",code.get());
        String resultJson = SpringAppUtils.Request.sendHttpsRequest(userInfoUrl, "", "POST");
        log.error("企业微信登录返回结果："+resultJson);
        ObjectNode result=null;
        try{
            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        String errcode = result.get("errcode").asText();
        if("0".equals(errcode)) {
            username = result.get("UserId").asText();
            try {
                //清除用户缓存
                //deleteUserDetailsCache(username);
                userCache.removeUserFromCache(username);

                // 当前切中的方法

                boolean isMobile = ContextUtil.Request.isMobile(request);
                // Reload password post-security so we can generate the token
                final UserFacade userDetails = (UserFacade) userDetailsService.loadUserByUsername(username);
                final String token = jwtTokenHandler.generateToken(userDetails);
                //String userName = userDetails.getUsername();
                String fullName = userDetails.getFullname();
                String account = userDetails.getAccount();
                String userId = userDetails.getId();
                String tenantId = userDetails.getTenantId();

                request.setAttribute("loginUser", String.format("%s[%s]", fullName, account));

                setCurrentRuntimeContext(userDetails, authConfig.isSingle(),isMobile,username,"ssoWeixin");


                log.debug("通过单点认证登录成功。");
                //处理单用户登录
                handleSingleLogin(isMobile, tenantId, fullName, token, authConfig.getExpiration());
                // Return the token
                JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse(token, fullName, account, userId);
                return ResponseEntity.ok(jwtAuthenticationResponse);
            } catch (Exception e) {
                throw new RuntimeException("企业微信登录失败 ,用户账号:"+ username);
            }
        }
        throw new RuntimeException("企业微信登录失败 ： " + result.get("errmsg").asText());



    }

    @Autowired
    AuthService authService;

    @RequestMapping(value = "/sso/weixinPublic", method = RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @Schema(description = "微信公众号进入手机端")
    public ResponseEntity<JwtAuthenticationResponse> weixinPublic( @RequestParam Optional<String> code,HttpServletRequest request) throws Exception {


        String userInfoUrl = getUserInfoUrl("weChatOffAcc", code.get());
        String resultJson = SpringAppUtils.Request.sendHttpsRequest(userInfoUrl, "", "POST");

        ObjectNode result = null;
        try {
            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (result.has("openid") && StringUtils.isNotEmpty(result.get("openid").asText())) {
            String openid = result.get("openid").asText();

            User node = authService.getUserByOpenId(openid);
            if (node == null) {
                return ResponseEntity.ok(new JwtAuthenticationResponse(openid));
            }

            // UserDTO node = r.getValue();
            String account = node.getAccount();
            //清除用户缓存
            userCache.removeUserFromCache(account);
            //this.deleteUserDetailsCache(account);
            // 当前切中的方法

            boolean isMobile = ContextUtil.Request.isMobile(request);
            // Reload password post-security so we can generate the token
            final UserFacade userDetails = (UserFacade) userDetailsService.loadUserByUsername(account);
            final String token = jwtTokenHandler.generateToken(userDetails);
            //String userName = userDetails.getUsername();
            String fullname = userDetails.getFullname();
            String userId = userDetails.getId();
            String tenantId = userDetails.getTenantId();

            request.setAttribute("loginUser", String.format("%s[%s]", fullname, account));

            setCurrentRuntimeContext(userDetails, authConfig.isSingle(), isMobile, openid, "weixinPublic");

            //处理单用户登录
            handleSingleLogin(isMobile, tenantId, fullname, token, authConfig.getExpiration());

            // Return the token
            JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, fullname, account, userId);
            return ResponseEntity.ok(response);
        }
        throw new RuntimeException("微信登录失败 ： " + result.get("errmsg").asText());
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