package com.nx.auth.service.apimpl;

import com.nx.api.model.R;
import com.nx.auth.api.AuthServiceApi;
import com.nx.auth.api.dto.SysRoleAuthDTO;
import com.nx.auth.api.dto.UserFacadeDTO;
import com.nx.auth.service.model.entity.SysRoleAuth;
import com.nx.auth.service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;


@RestController
@Primary
public class AuthServiceApiImpl implements AuthServiceApi {
    @Autowired
    AuthService authService;

//    @Autowired
//    APIService apiService;

    /**
     * 得到所有的权限
     * @return
     */
    @Override
    public R<List<SysRoleAuthDTO>> getMethodRoleAuth() {
        List<SysRoleAuth> sysRoleAuths = authService.getSysRoleAuthAll();

        List<SysRoleAuthDTO> result = new ArrayList<SysRoleAuthDTO>();
        for (SysRoleAuth sysRoleAuth : sysRoleAuths) {
            SysRoleAuthDTO dto = new SysRoleAuthDTO();
            dto.setRoleAlias(sysRoleAuth.getRoleAlias());
            dto.setMethodRequestUrl(sysRoleAuth.getMethodRequestUrl());
            dto.setDataPermission(sysRoleAuth.getDataPermission());
            result.add(dto);
        }
        return R.OK(result);
    }

    @Override
    public R<UserFacadeDTO> loadUserByUsername(String account) {
        return null;
    }

    @Override
    public R<UserFacadeDTO> changePwd(String account, String oldPassword, String newPassword) {
        return null;
    }

    @Override
    public R<UserFacadeDTO> changePhoto(String account, String photoUrl) {
        return null;
    }

//    @Override
//    public R<String> apiEnabled(String appId, String accessToken, String encrypt, String protocol) {
//        ApiAppInfo appInfo =  apiService.getAppById(appId);
//        if (appInfo==null) {
//            return R.FAIL(404, "appId不存在！");
//            //return R.OK(Boolean.FALSE);
//        }
//        accessToken = Base64.decodeBase64(accessToken);
//        encrypt = Base64.decodeBase64(encrypt);
//        if (appInfo.getAccessToken().equals(accessToken)
//            && encrypt.equals(encrypt)
//        ){
//            if (appInfo!=null){
//                ApiResourceDTO dto = new ApiResourceDTO();
//                BeanUtils.copyProperties(appInfo,dto);
//
//                String ticketId  = apiService.getTicketId(appId,protocol);
//
//                return R.OK(ticketId);
//            }
//        }
//
//        return R.FAIL(401, "认证不通过！");
//    }

//    @Override
//    public R<List<ApiResourceDTO>> getApiResources(String ticketId,String appId, String protocol) {
//        String providerTicket  = apiService.getTicketId(appId,protocol);
//        if (providerTicket.equals(toString())){
//            //TODO: 半列表可维护
//            ApiAppInfo appInfo =  apiService.getAppById(appId);
//            ApiResourceDTO dto = new ApiResourceDTO();
//            BeanUtils.copyProperties(appInfo,dto);
//            return R.OK(Arrays.asList(new ApiResourceDTO[]{dto}));
//        }
//        return R.OK(Collections.emptyList());
//    }

}
