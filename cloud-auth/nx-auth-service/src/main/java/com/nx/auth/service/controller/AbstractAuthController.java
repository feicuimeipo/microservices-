package com.nx.auth.service.controller;

import com.nx.auth.security.jwt.JwtTokenHandler;
import com.nx.auth.service.constant.ExterUniEnum;
import com.nx.auth.service.constant.WeChatOffAccConsts;
import com.nx.auth.service.model.entity.PwdStrategy;
import com.nx.auth.service.service.PwdStrategyManager;
import com.nx.utils.BeanUtils;
import com.nx.auth.conf.AuthConfig;
import com.nx.auth.context.UserFacade;
import com.nx.api.context.AuthUser;
import com.nx.api.context.CurrentRuntimeContext;
import com.nx.api.context.SpringAppUtils;
import com.nx.auth.service.constant.DingTalkConsts;
import com.nx.auth.service.constant.WeChatWorkConsts;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public abstract class AbstractAuthController {


    protected abstract JwtTokenHandler jwtTokenHandler();
    protected abstract AuthConfig authConfig();


    /**
     *
     * @param type 第三方集成类型
     * @param code 应用code
     * @return
     * @throws Exception
     */
    public String getUserInfoUrl(String type,
                                 String code) throws Exception{
        String url="";
        if(ExterUniEnum.WeChatWork.getKey().equals(type)){
            url= WeChatWorkConsts.getQyWxUserInfo(code);
        }else if(ExterUniEnum.Dingtalk.getKey().equals(type)){
            url= DingTalkConsts.getUserInfo(code);
        }else if(ExterUniEnum.WeChatOfficialAccounts.getKey().equals(type)){
            url= WeChatOffAccConsts.getWxAccessToken(code);
        }
        return url;
    }

    /**
     * 处理单用户登录
     * @param isMobile
     * @param username
     * @param token
     */
    public void handleSingleLogin(boolean isMobile,String tenantId, String username,String token,long expiration){
        //如果是单用户登录
        if(authConfig().isSingle()){
            String userAgent = isMobile ? "mobile" : "pc";
            //以当前登录设备、租户ID、用户账号为key将token存放到缓存中
            jwtTokenHandler().putTokenInCache(userAgent, tenantId, username, expiration, token);
        }
    }


    protected void setCurrentUser(UserFacade user, boolean isSingle, boolean isMobile, String principalId, String principalType) {
        AuthUser authUser = new AuthUser();
        authUser.setAdmin(user.isAdmin());
        authUser.setId(user.getUserId());
        authUser.setDeptId(user.getGroupId());
        authUser.setStatus(user.getStatus());
        authUser.setAccount(user.getUsername());
        authUser.setFullName(user.getFullname());
        authUser.setType(user.getUserId());
        authUser.setTenantId(user.getTenantId());
        //TODO:
        //authUser.setType();


        if (!StringUtils.isEmpty(principalId)){
            authUser.setPrincipalId(principalId);
        }

        //TODO:
        if (!StringUtils.isEmpty(principalType)){
            authUser.setPrincipalType(principalType); //指定主体的安全凭据。
        }

        CurrentRuntimeContext.setTenantId(user.getTenantId());
        CurrentRuntimeContext.setCurrentUser(authUser);
    }

    //checkuser
    protected boolean chckPwdStragy(UserFacade user,String password) {
        if(!user.isAdmin()) {
            //非系统管理员
            PwdStrategyManager service= SpringAppUtils.getBean(PwdStrategyManager.class);
            PwdStrategy pwdStrategy = service.getDefault();
            if(pwdStrategy!=null) {
                // 初始化密码
                String initPwd = pwdStrategy.getInitPwd();// json.get("initPwd").asText();
                // 密码策略
                int pwdRule = pwdStrategy.getPwdRule();// json.get("pwdRule").asInt();
                // 密码长度
                int pwdLength =pwdStrategy.getPwdLength();// ;json.get("pwdLength").asInt();
                // 密码可用时长
                int duration =pwdStrategy.getDuration();// json.get("duration").asInt();
                // 启用策略	0:停用，1:启用
                int enable = pwdStrategy.getEnable();// json.get("enable").asInt();

                if(enable == 1) {
                    if(password.equals(initPwd)) {
                        return false;
                    }
                    if(password.length() < pwdLength) {
                        return false;
                    }

                    if(pwdRule!=1) {
                        if(pwdRule == 2) {//必须包含数字、字母
                            String regex ="^(?![a-zA-z]+$)(?!\\d+$)(?![!@#$%^&*]+$)[a-zA-Z\\d!@#$%^&*]+$";
                            boolean result=password.matches(regex);
                            if(!result) {
                                return false;
                            }
                        }else if(pwdRule == 3) {//必须包含数字、字母、特殊字符
                            String regex = "^(?=.*?[A-Za-z])(?=.*?\\d)(?=.*?[~!@#$%^&*()_+`\\-={}:\";'<>?,.\\/])[a-zA-Z\\d~!@#$%^&*()_+`\\-={}:\";'<>?,.\\/]*$";
                            boolean result=password.matches(regex);
                            if(!result) {
                                return false;
                            }
                        }else if(pwdRule == 4) {//必须包含数字、大小字母、特殊字符
                            String regex = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[~!@#$%^&*()_+`\\-={}:\";'<>?,.\\/])[a-zA-Z\\d~!@#$%^&*()_+`\\-={}:\";'<>?,.\\/]*$";
                            boolean result=password.matches(regex);
                            if(!result) {
                                return false;
                            }
                        }
                    }

                    //密码策略时间
                    LocalDateTime pwdCreateTime = user.getPwdCreateTime();
                    if(BeanUtils.isNotEmpty(pwdCreateTime)) {
                        LocalDateTime currenTime = LocalDateTime.now();
                        int size = (int)(currenTime.toLocalDate().toEpochDay() - pwdCreateTime.toLocalDate().toEpochDay());
                        if(size > duration) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }



    protected void setCurrentRuntimeContext(UserFacade user, boolean isSingle, boolean isMobile, String principalId, String principalType) {
        AuthUser authUser = new AuthUser();
        authUser.setAdmin(user.isAdmin());
        authUser.setId(user.getUserId());
        authUser.setDeptId(user.getGroupId());
        authUser.setStatus(user.getStatus());
        authUser.setAccount(user.getUsername());
        authUser.setFullName(user.getFullname());
        //authUser.setType(user.);
        authUser.setTenantId(user.getTenantId());


        if (!StringUtils.isEmpty(principalId)){
            authUser.setPrincipalId(principalId);
        }

        if (!StringUtils.isEmpty(principalType)){
            authUser.setPrincipalType(principalType);
        }

        CurrentRuntimeContext.setTenantId(user.getTenantId());
        CurrentRuntimeContext.setCurrentUser(authUser);
    }
}