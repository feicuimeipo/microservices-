package com.nx.storage.adapter.provider;

import com.nx.storage.core.enums.StoProviderEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StoProviderFactory {
    protected static Map<String, StoProvider> providerMap = new ConcurrentHashMap<>();

    public static void addStoProvider(StoProvider provider){
        String key = StoProviderEnum.valueByCode(provider.name()).getCode();
        providerMap.put(key,provider);
    }

    public static StoProvider getStoProvider(StoProvider provider){
        String key = StoProviderEnum.valueByCode(provider.name()).getCode();
        return providerMap.get(key);
    }
}
