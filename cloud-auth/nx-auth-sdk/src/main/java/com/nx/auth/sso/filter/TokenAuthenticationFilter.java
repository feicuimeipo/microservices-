package com.nx.auth.sso.filter;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.security.config.SecurityProperties;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.web.core.handler.GlobalExceptionHandler;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.oauth2.OAuth2TokenApi;
import cn.iocoder.yudao.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import cn.iocoder.yudao.module.system.api.permission.DataLanguagePermission;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import com.nx.common.model.base.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.security.util.SecurityProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Token 过滤器，验证 token 的有效性
 * 验证通过后，获得 {@link LoginUser} 信息，并加入到 Spring Security 上下文
 *
 * @author 锐信视通
 */
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    private final GlobalExceptionHandler globalExceptionHandler;

    private final OAuth2TokenApi oauth2TokenApi;

    private final DataLanguagePermission dataLanguagePermission;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = SecurityFrameworkUtils.obtainAuthorization(request, securityProperties.getTokenHeader());
        if (StrUtil.isNotEmpty(token)) {
            Integer userType = WebFrameworkUtils.getLoginUserType(request);
            try {
                // 1.1 基于 token 构建登录用户
                LoginUser loginUser = buildLoginUserByToken(token, userType);

                // 1.2 模拟 Login 功能，方便日常开发调试
                if (loginUser == null) {
                    loginUser = mockLoginUser(request, token, userType);
                }

                // 2. 设置当前用户
                if (loginUser != null) {
                    SecurityFrameworkUtils.setLoginUser(loginUser, request);
                }
            } catch (Throwable ex) {
                CommonResult<?> result = globalExceptionHandler.allExceptionHandler(request, ex);
                ServletUtils.writeJSON(response, result);
                return;
            }
        }

        // 继续过滤链
        chain.doFilter(request, response);
    }



    private AuthUser buildLoginUserByToken(String token, Integer userType) {
        try {
            OAuth2AccessTokenCheckRespDTO accessToken = oauth2TokenApi.checkAccessToken(token);

            if (accessToken == null) {
                return null;
            }
            // 用户类型不匹配，无权限
            if (ObjectUtil.notEqual(accessToken.getUserType(), userType)) {
                throw new AccessDeniedException("错误的用户类型");
            }

            if (accessToken.getScopes()==null){
                accessToken.setScopes(new ArrayList<>());
            }

            List<DataLanguagePermission.DataLangKV> dataLangList = dataLanguagePermission.listByUserId(accessToken.getUserId());
            String currentDataLanguage = dataLanguagePermission.getCurrentDataLanguageByUserId(accessToken.getUserId());

            // 构建登录用户
            return new LoginUser()
                    .setId(accessToken.getUserId())
                    .setUserType(accessToken.getUserType())
                    .setTenantId(accessToken.getTenantId())
                    .setScopes(accessToken.getScopes())
                    .setCurrentDataLang(currentDataLanguage)
                    .setDataLanguageList(dataLangList)
                    ;
        } catch (ServiceException serviceException) {
            // 校验 Token 不通过时，考虑到一些接口是无需登录的，所以直接返回 null 即可
            return null;
        }
    }

    /**
     *
     * 模拟登录用户，方便日常开发调试
     * 注意，在线上环境下，一定要关闭该功能！！！
     *
     * @param request 请求
     * @param token 模拟的 token，格式为 {@link SecurityProperties#getMockSecret()} + 用户编号
     * @param userType 用户类型
     * @return 模拟的 LoginUser
     */
    private LoginUser mockLoginUser(HttpServletRequest request, String token, Integer userType) {
        if (!securityProperties.getMockEnable()) {
            return null;
        }
        // 必须以 mockSecret 开头
        if (!token.startsWith(securityProperties.getMockSecret())) {
            return null;
        }

        // 构建模拟用户
        Long userId = Long.valueOf(token.substring(securityProperties.getMockSecret().length()));

        List<DataLanguagePermission.DataLangKV> list = dataLanguagePermission.listByClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT);

        List<String> scopes = dataLanguagePermission.scopes(OAuth2ClientConstants.CLIENT_ID_DEFAULT);

        return new LoginUser()
                .setId(userId)
                .setUserType(userType)
                .setScopes(scopes)
                .setTenantId(WebFrameworkUtils.getTenantId(request))
                .setDataLanguageList(list)
                .setCurrentDataLang("zh_en")
                ;
    }

}
