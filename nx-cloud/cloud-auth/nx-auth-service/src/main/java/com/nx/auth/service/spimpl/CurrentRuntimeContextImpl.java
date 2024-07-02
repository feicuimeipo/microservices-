package com.nx.auth.service.spimpl;

import com.google.auto.service.AutoService;
import com.nx.api.context.AuthUser;
import com.nx.api.context.CurrentRuntimeContext;
import com.nx.api.context.GlobalConstants;
import com.nx.api.context.SpringAppUtils;
import com.nx.auth.context.BootConstant;
import com.nx.auth.context.ContextUtil;
import com.nx.auth.context.UserFacade;
import com.nx.boot.launch.NxSpringBootApplicationBuilder;
import com.nx.boot.support.AppUtil;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Properties;


@AutoService(CurrentRuntimeContext.class)
public class CurrentRuntimeContextImpl implements CurrentRuntimeContext {
    //TODO: 是否可以从内存中取，不需要直接从接口
    @Override
    public AuthUser getCurrentAuthUser() {
        if (CurrentRuntimeContext.getCurrentUser()!=null){
            return null;
        }
        UserFacade user = ContextUtil.getCurrentUser();
        AuthUser authUser = new AuthUser();
        authUser.setTenantId(BootConstant.PLATFORM_TENANT_ID);
        authUser.setStatus(user.getStatus());
        authUser.setFrom(BootConstant.FROM_RESTFUL);
        authUser.setId(user.getId());
        authUser.setDeptId(user.getGroupId());
        authUser.setEnabled(user.isEnabled());
        return authUser;
    }


    @Override
    public Boolean apiAuth(String s, String s1, String s2, GlobalConstants.REFERER referer, String s3, String s4) {
        return Boolean.TRUE;
    }

    private static ApplicationContext applicationContext;
    @Override
    public ApplicationContext getApplicationContext() {
        if (applicationContext==null){
            applicationContext = AppUtil.getApplicaitonContext();
        }
        return applicationContext;
    }

    @Override
    public Properties getBootstrapProperties() {
        return NxSpringBootApplicationBuilder.getBootstrap().getAllProperties();
    }

}
