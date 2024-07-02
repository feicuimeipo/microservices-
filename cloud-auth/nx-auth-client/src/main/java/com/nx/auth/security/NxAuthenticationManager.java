package com.nx.auth.security;

import com.nx.api.context.SpringAppUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnMissingBean(NxAuthenticationManager.class)
public class NxAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return authentication;
    }

    public static void setAuthenticate(Authentication authentication){
        SpringAppUtils.getBean(AuthenticationManager.class).authenticate(authentication);
    }
}
