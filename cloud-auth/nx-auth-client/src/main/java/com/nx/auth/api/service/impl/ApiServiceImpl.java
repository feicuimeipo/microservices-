package com.nx.auth.api.service.impl;
//
//import com.nx.auth.api.AuthServiceApi;
//import com.nx.auth.api.dto.ApiResourceDTO;
//import com.nx.auth.api.ucapi.service.ApiService;
//import com.nx.api.model.R;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import java.util.Collections;
//import java.util.List;
//
//@Component
//public class ApiServiceImpl implements ApiService {
//
//    @Autowired
//    AuthServiceApi authServiceApi;
//
//
//    @Override
//    public String apiEnabled(String appId, String accessToken, String encrypt,String protocol){
//        R<String> r = authServiceApi.apiEnabled(appId,accessToken,encrypt,protocol);
//        if (r.isOK()){
//           r.getData();
//        }
//        return StringUtils.EMPTY;
//    }
//
//
//
//    @Override
//    public List<ApiResourceDTO> getApiResources(String appId,String protocol,String ticketId){
//        R<List<ApiResourceDTO>> r = authServiceApi.getApiResources(appId,protocol,ticketId);
//        if (r.isOK()){
//            return r.getData();
//        }
//        return Collections.EMPTY_LIST;
//    }
//
//}
