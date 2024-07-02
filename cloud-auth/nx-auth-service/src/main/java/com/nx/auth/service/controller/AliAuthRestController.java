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
import com.nx.auth.service.model.bo.JwtAuthenticationResponse;
import com.nx.utils.JsonUtil;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.api.context.SpringAppUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
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
public class AliAuthRestController extends AbstractAuthController {

    @Autowired
    protected AuthConfig authConfig;

    @Autowired
    protected JwtTokenHandler jwtTokenHandler;


    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UserCache userCache;

    @RequestMapping(value = "/sso/dingTalk", method = RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @Schema(description= "进入钉钉手机端")
    public ResponseEntity<JwtAuthenticationResponse> dingTalk(@RequestParam Optional<String> code, HttpServletRequest request) throws Exception {
        String url = getUserInfoUrl("dingtalk",code.get());
        String resultJson = SpringAppUtils.Request.sendHttpsRequest(url, "", "GET");
        ObjectNode result=null;
        try{
            result = (ObjectNode) JsonUtil.toJsonNode(resultJson);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        if(result.has("userid")) {
            String userid = result.get("userid").asText();
            final UserFacade userDetails = (UserFacade) userDetailsService.loadUserByUsername(userid);
            if(userDetails!=null) {
                String account = userid;
                //清除用户缓存
                userCache.removeUserFromCache(account);
                //this.deleteUserDetailsCache(account);

                // Reload password post-security so we can generate the token
                // 当前切中的方法

                boolean isMobile = ContextUtil.Request.isMobile(request);
                final String token = jwtTokenHandler.generateToken(userDetails);
                String fullName = userDetails.getUsername();
                String userId = userDetails.getId();
                String tenantId = userDetails.getTenantId();


                setCurrentRuntimeContext(userDetails, authConfig.isSingle(),isMobile,userid,"dingTalk");
                //处理单用户登录
                handleSingleLogin(isMobile, tenantId, fullName, token, authConfig.getExpiration());
                // Return the token
                JwtAuthenticationResponse response =  new JwtAuthenticationResponse(token, fullName, account, userId);
                return ResponseEntity.ok(response);
            }else {
                throw new RuntimeException("钉钉登录失败！"+userid+"账号不存在");
            }
        }
        throw new RuntimeException("钉钉登录失败 ： " + result.get("errmsg").asText());

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