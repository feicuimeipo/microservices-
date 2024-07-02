package com.nx.auth.context;

import com.nx.auth.api.dto.UserFacadeDTO;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserFacade extends UserFacadeDTO implements UserDetails {

    private static final long serialVersionUID = -47458854186013987L;

    /**
     *	用户授权信息
     */
    private Collection<SimpleGrantedAuthority> authorities;


    @Override
    public String getUsername() {
        return this.account;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isEnabled() {
        int status=this.getStatus();
        if(status==1){
            return true;
        }
        return false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    public String getUserId() {
        return getId();
    }
}
