package com.nx.auth.api.service;

//import com.nx.auth.api.context.AuthCache;
//import com.nx.auth.api.dto.ApiResourceDTO;
//import com.nx.cache.annotation.Cacheable;
//
//
//import java.util.List;
//
//public interface ApiService {
//
//    @Cacheable(cacheName= AuthCache.AUTH_CACHE_NAME, keyPrefix = "app-api",value="'enabled'#appId'-'#protocol")
//    String apiEnabled(String appId, String accessToken, String encrypt,String protocol);
//
//    @Cacheable(cacheName= AuthCache.AUTH_CACHE_NAME,keyPrefix = "app-api",value="'resource'#appId'-'#protocol")
//    List<ApiResourceDTO> getApiResources(String appId, String protocol, String ticketId);
//}
