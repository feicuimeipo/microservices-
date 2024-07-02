package com.nx.auth.conf;

import com.nx.auth.api.service.IAuthUserService;
import com.nx.auth.api.service.IContextVar;
import com.nx.auth.api.service.IMethodAuthService;
import com.nx.auth.api.service.impl.AuthUserServiceImpl;
import com.nx.auth.api.service.impl.CurrentUserAccountVar;
import com.nx.auth.api.service.impl.CurrentUserIdVar;
import com.nx.auth.api.service.impl.DefaultMethodAuthService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;



@Configuration
public class AuthApiConfigure {


    @Bean
    @ConditionalOnMissingBean(IMethodAuthService.class)
    IMethodAuthService methodAuthService(){
        return new DefaultMethodAuthService();
    }

    @Bean
    @ConditionalOnMissingBean(IAuthUserService.class)
    IAuthUserService authUserService(){
        return new AuthUserServiceImpl();
    }


    @Bean
    @ConditionalOnMissingBean(AuthConfig.class)
    AuthConfig authConfig(){
        return new AuthConfig();
    }

    @Bean
    @ConditionalOnMissingBean(BaseContext.class)
    BaseContext baseContext(){
        return new BaseContext.BootContextImpl();
    }


    @Bean
    @ConditionalOnMissingBean(CurrentUserAccountVar.class)
    CurrentUserAccountVar currentUserAccountVar(){
        return new CurrentUserAccountVar();
    }

    @Bean
    @ConditionalOnMissingBean(CurrentUserIdVar.class)
    CurrentUserIdVar currentUserIdVar(){
        return new CurrentUserIdVar();
    }

    /**
     * 根据当前用户账号和用户ID获取用户信息
     * @param currentUserAccountVar 当前用户账号
     * @param currentUserIdVar 当前用户ID
     * @return 用户信息
     */
    @SuppressWarnings("rawtypes")
    @Bean(name="queryViewComVarList")
    @ConditionalOnMissingBean(name={"queryViewComVarList"})
    public ArrayList queryViewComVarList(
            CurrentUserAccountVar currentUserAccountVar,
            CurrentUserIdVar currentUserIdVar){
        ArrayList<IContextVar> list = new ArrayList<IContextVar >();
        list.add(currentUserAccountVar);
        list.add(currentUserIdVar);
        return list;
    }

}
