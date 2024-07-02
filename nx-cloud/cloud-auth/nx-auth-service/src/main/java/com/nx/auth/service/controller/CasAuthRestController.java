package com.nx.auth.service.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.nx.auth.conf.AuthConfig;
import com.nx.auth.security.jwt.JwtTokenHandler;
import com.nx.auth.service.model.bo.JwtAuthenticationRequest;
import com.nx.auth.service.model.bo.JwtAuthenticationResponse;
import com.nx.utils.FluentUtil;
import com.nx.utils.JsonUtil;
import com.nx.utils.MapUtil;
import com.nx.utils.XmlUtil;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.auth.exception.UsernameNotFoundException;
import com.nx.auth.support.AuthenticationUtil;
import com.nx.api.exception.BaseException;
import com.nx.auth.service.conf.SsoConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@RestController
@Schema(description = "认证接口")
public class CasAuthRestController extends AbstractAuthController {
    @Autowired
    protected AuthConfig authConfig;

    @Autowired
    protected SsoConfig ssoConfig;

    @Autowired
    protected JwtTokenHandler jwtTokenHandler;


    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    UserCache userCache;



    @RequestMapping(value = "/sso/auth", method = RequestMethod.GET, produces={"application/json; charset=utf-8" })
    @ApiResponse(description = "登录系统-单点登录")
    public ResponseEntity<JwtAuthenticationResponse> ssoAuth(@RequestParam Optional<String> ticket,
                                                             @RequestParam Optional<String> code,
                                                             @RequestParam Optional<String> ssoMode,
                                                             @RequestParam String service,
                                                             @RequestBody JwtAuthenticationRequest authenticationRequest,
                                                             HttpServletRequest request
    ) throws AuthenticationException, ClientProtocolException, IOException {
        Assert.isTrue(ssoConfig.isSsoEnabled(), "当前服务未开启单点登录");

        String username = null;
        String mode = authConfig.getMode();
        if (code.isPresent()){
            mode = code.get();
        }
        UserFacade userFacade = null;
        if(ssoMode.isPresent()) {
            mode = ssoMode.get();
        }
        // 使用cas认证

        if(ticket.isPresent() && SsoConfig.MODE_CAS.equals(mode)) {
            username = getUserNameWithCas(ticket.get(), service);
            userFacade = (UserFacade) userDetailsService.loadUserByUsername(username);
            if (userFacade!=null) {
                userCache.removeUserFromCache(username);
            }
        }
        // 使用oauth认证
        else if(SsoConfig.MODE_OAUTH2.equals(mode)) {
            username = getUserNameWithOauth(code.get(), service);
            userFacade = (UserFacade) userDetailsService.loadUserByUsername(username);
            if (userFacade!=null) {
                userCache.removeUserFromCache(username);
            }
        }
        // 使用jwt认证
        else if(SsoConfig.MODE_JWT.equals(mode)){
            username = jwtTokenHandler.getUsernameFromToken(ticket.get());
            userFacade = (UserFacade) userDetailsService.loadUserByUsername(username);
            if (userFacade!=null) {
                userCache.removeUserFromCache(username);
            }
        }else {
            username = authenticationRequest.getUsername();
            String password = authenticationRequest.getPassword();

            //清除用户缓存
            Objects.requireNonNull(username);
            Objects.requireNonNull(password);

            Authentication  authentication = AuthenticationUtil.login(request,username,password,false);

            if(Objects.isNull(authentication)){
                throw new BaseException(401,"用户名或密码错误");
            }

            userFacade = (UserFacade) authentication.getPrincipal();
            //userFacade = (UserFacade) userDetailsService.loadUserByUsername(username);
            if (userFacade==null){
                throw new UsernameNotFoundException("用户的未找到！");
            }
            //校验密码策略
            boolean  chckPwdStragy = super.chckPwdStragy(userFacade, password);
            if (!chckPwdStragy){
                throw new BaseException(401,"密码不符合规则!");
            }
        }

        final String token = jwtTokenHandler.generateToken(userFacade);
        if (userFacade==null){
            throw new UsernameNotFoundException("用户的找到！");
        }

        Map<String,Object> userAttrs = new HashMap<>();
        String fullname = userFacade.getFullname();
        String account = userFacade.getAccount();
        String userId = userFacade.getUserId();

        request.setAttribute("loginUser", String.format("%s[%s]",fullname,account));
        userAttrs.put("tenantId", userFacade.getTenantId());

        // 当前切中的方法
        boolean isMobile = ContextUtil.Request.isMobile(request);

        setCurrentRuntimeContext(userFacade, authConfig.isSingle(),isMobile,token,"sso.mode-"+authConfig.getMode());

        //获取超时时间

        log.debug("通过单点认证登录成功。");
        //处理单用户登录
        if(!(SsoConfig.MODE_JWT.equals(mode))){
            handleSingleLogin(isMobile, MapUtil.getString(userAttrs, "tenantId"), fullname, token, authConfig.getExpiration());
        }
        // Return the token
        JwtAuthenticationResponse response = new JwtAuthenticationResponse(token, fullname, account, userId, authConfig.getExpiration(),userAttrs);

        return ResponseEntity.ok(response);
    }

    // cas验证ticket并获取当前登录用户账号
    private String getUserNameWithCas(String ticket, String service) throws IOException {
        String casUserDetail = "";
        try{
            casUserDetail = FluentUtil.get(String.format("%s/p3/serviceValidate?ticket=%s&service=%s", ssoConfig.getCasUrl(), ticket, service), "");
        }catch(Exception e){
            e.printStackTrace();
            throw new BaseException("获取cas认证信息失败");
        }
        String json = XmlUtil.toJson(casUserDetail);
        JsonNode jsonNode = JsonUtil.toJsonNode(json);
        String username = jsonNode.get("authenticationSuccess").get("user").asText();
        return username;
    }

    // oauth验证code并获取当前登录用户账号
    private String getUserNameWithOauth(String code, String service) {
        String userName = null;
        String oauthTokenUrl = ssoConfig.getOauthTokenUrl();
        String stufix = String.format("&code=%s&redirect_uri=%s", code, service);
        try {
            String header = ssoConfig.getOauthBasicHeader();
            String tokenResult = FluentUtil.post(oauthTokenUrl + stufix, header, null);
            JsonNode jsonNode = JsonUtil.toJsonNode(tokenResult);
            if(jsonNode!=null && jsonNode.isObject()) {
                String token = jsonNode.get("value").asText();
                String oauthCheckUrl = ssoConfig.getOauthCheckUrl();
                String checkResult = FluentUtil.post(oauthCheckUrl + token, null, null);
                JsonNode checkJNode = JsonUtil.toJsonNode(checkResult);
                if(checkJNode!=null && checkJNode.isObject()) {
                    userName = checkJNode.get("user_name").asText();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new BaseException("获取oauth认证信息失败");
        }
        return userName;
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