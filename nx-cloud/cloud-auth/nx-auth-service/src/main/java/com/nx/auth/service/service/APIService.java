package com.nx.auth.service.service;

//import com.nx.auth.context.AuthCache;
//import com.nx.api.id.GUID;
//import com.nx.auth.service.model.entity.ApiAppInfo;
//import com.nx.cache.annotation.Cacheable;
//
//
//public interface APIService {
//    @Cacheable(cacheName= AuthCache.AUTH_CACHE_NAME, key="'appinfo'#appId'-'#protocol")
//    ApiAppInfo getAppById(String appId);
//
//
//
//    /**
//     * 如果不存在，则生成一个新的
//     * @param appId
//     * @param protocol
//     * @return
//     */
//    @Cacheable(cacheName= AuthCache.AUTH_CACHE_NAME, key="'appinfo'#appId'-'#protocol")
//    default String getTicketId(String appId,String protocol){
//        return GUID.uuid();
//    }
//
//}
