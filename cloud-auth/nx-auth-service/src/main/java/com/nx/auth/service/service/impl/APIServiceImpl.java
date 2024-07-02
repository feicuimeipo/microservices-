package com.nx.auth.service.service.impl;

//import com.nx.api.context.SpringAppUtils;
//import com.nx.auth.service.constant.DingTalkConsts;
//import com.nx.auth.service.constant.WeChatOffAccConsts;
//import com.nx.auth.service.constant.WeChatWorkConsts;
//import com.nx.auth.service.model.entity.ApiAppInfo;
//import com.nx.auth.service.service.APIService;
//import com.nx.auth.service.constant.ExterUniEnum;
//import org.springframework.stereotype.Service;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class APIServiceImpl implements APIService {
//
//    /**
//     * todo： 从库中得到数据
//     * @param appId
//     * @return
//     */
//    @Override
//    public ApiAppInfo getAppById(String appId) {
//        Set<String> paths =new HashSet<>();
//        paths.add("/**");
//        ApiAppInfo apiAppInfo =  new ApiAppInfo(appId);
//        apiAppInfo.setAppId(SpringAppUtils.getProperty("nx.api.appId","basic"));
//        apiAppInfo.setAccessToken(SpringAppUtils.getProperty("nx.api.accessToken","accessToken"));
//        apiAppInfo.setEncrypt(SpringAppUtils.getProperty("nx.api.dubbo.encrytp","feignCallEncry"));
//        return apiAppInfo;
//    }
//
//
//    /**
//     *
//     * @param type 第三方集成类型
//     * @param code 应用code
//     * @return
//     * @throws Exception
//     */
//    public String getUserInfoUrl(String type, String code) throws Exception{
//        String url="";
//        if(ExterUniEnum.WeChatWork.getKey().equals(type)){
//            url= WeChatWorkConsts.getQyWxUserInfo(code);
//        }else if(ExterUniEnum.Dingtalk.getKey().equals(type)){
//            url= DingTalkConsts.getUserInfo(code);
//        }else if(ExterUniEnum.WeChatOfficialAccounts.getKey().equals(type)){
//            url= WeChatOffAccConsts.getWxAccessToken(code);
//        }
//        return url;
//    }
//
//
//}
