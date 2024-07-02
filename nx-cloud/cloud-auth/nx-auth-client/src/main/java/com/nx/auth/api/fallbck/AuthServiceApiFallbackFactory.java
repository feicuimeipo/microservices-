package com.nx.auth.api.fallbck;

import com.nx.auth.api.AuthServiceApi;
import com.nx.auth.api.dto.ApiResourceDTO;
import com.nx.auth.api.dto.UserFacadeDTO;
import com.nx.auth.api.dto.SysRoleAuthDTO;
import com.nx.api.model.R;
import com.nx.api.exception.EmptyFeignException;
import com.nx.common.service.api.exception.EmptyFeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 授权服务
 */
@Slf4j
@Component
public class AuthServiceApiFallbackFactory implements FallbackFactory<AuthServiceApi> {
    @Override
    public AuthServiceApi create(Throwable e) {
        //e.printStackTrace();
        return new AuthServiceApi() {

            @Override
            public R<List<SysRoleAuthDTO>> getMethodRoleAuth() {
                throw new EmptyFeignException();
            }

            @Override
            public R<UserFacadeDTO> loadUserByUsername(String account) {
                throw new EmptyFeignException();
            }

            @Override
            public R<UserFacadeDTO> changePwd(String account, String oldPassword, String newPassword) {
                    throw new EmptyFeignException();
            }

            @Override
            public R<UserFacadeDTO> changePhoto(String account, String photoUrl) {
                    throw new EmptyFeignException();
            }

//            @Override
//            public R<String> apiEnabled(String appId, String accessToken, String encrypt,String protocol) {
//                throw new EmptyFeignException();
//            }
//
//            @Override
//            public R<List<ApiResourceDTO>> getApiResources(String appId, String protocol, String ticketId) {
//                throw new EmptyFeignException();
//            }
        };

    }
}
