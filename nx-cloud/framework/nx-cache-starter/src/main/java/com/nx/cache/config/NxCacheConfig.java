package com.nx.cache.config;


import com.nx.cache.enums.LocalCacheTypeEnum;
import com.nx.cache.utils.CacheExpiresUtils;
import com.nx.common.context.SpringUtils;
import com.nx.redis.RedisConfigFactory;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.nx.cache.CacheProviderFactory.DEFAULT_CACHE_GROUP_NAME;
import static com.nx.redis.RedisConfigFactory.DEFAULT_GROUP_NAME;

/**
 * CacheExpiresUtils.IN_1DAY
 */
@Data
public class NxCacheConfig {
    private LocalCacheConfig level1;
    private String redisGroupName = DEFAULT_GROUP_NAME;
    /**
     * redis
     */
    private boolean use2LevelCache;
    /**
     * 本地缓存
     */
    private boolean use1LevelCache;

    public static NxCacheConfig createNxCacheConfig(String groupName){
        NxCacheConfig nxCacheConfig = new NxCacheConfig();
        nxCacheConfig.redisGroupName = groupName;

        nxCacheConfig.use2LevelCache  = SpringUtils.Env.getBoolean(groupName+"cache.use2-level-cache.enabled",true);
        nxCacheConfig.use1LevelCache = SpringUtils.Env.getBoolean( groupName+"cache.use1-level-cache",true);



        nxCacheConfig.level1 = createLeveltNxCacheConfig(groupName);


        return nxCacheConfig;
    }

    private static LocalCacheConfig createLeveltNxCacheConfig(String groupName){
        int concurrencyLevel =  SpringUtils.Env.getInt(groupName + "cache.level1.concurrency-level",8);
        int initialCapacity =  SpringUtils.Env.getInt(groupName + "cache.initial-capacity",50);
        int maximumSize =  SpringUtils.Env.getInt(groupName + "cache.maximum-size",10000);
        long expireAfterWrite =  SpringUtils.Env.getLong(groupName + "cache.expire-after-write", CacheExpiresUtils.IN_1DAY);
        boolean recordStats = SpringUtils.Env.getBoolean(groupName + "cache.record-stats",false);

        String localType = SpringUtils.Env.getProperty( groupName+"cache.use1-level-cache.type","caffeine");
        LocalCacheTypeEnum localCacheType = LocalCacheTypeEnum.valueOfByCode(localType);
        LocalCacheConfig level1 = new LocalCacheConfig();
        level1.setConcurrencyLevel(concurrencyLevel);
        level1.setInitialCapacity(initialCapacity);
        level1.setMaximumSize(maximumSize);
        level1.setExpireAfterWrite(expireAfterWrite);
        level1.setRecordStats(recordStats);
        level1.setType(localCacheType);
        return level1;
    }


    public static String[] getCacheName(String... cacheNames){
        if (cacheNames==null || cacheNames.length==0){
            cacheNames = new String[]{RedisConfigFactory.DEFAULT_GROUP_NAME};
        }
        List<String> cacheNameList = new ArrayList<>();

        for (String cache : cacheNames) {
            if (!StringUtils.hasLength(cache)){
                cacheNameList.add(RedisConfigFactory.DEFAULT_GROUP_NAME);
            }else if (cache.equalsIgnoreCase(DEFAULT_CACHE_GROUP_NAME) && !DEFAULT_CACHE_GROUP_NAME.equals(RedisConfigFactory.DEFAULT_GROUP_NAME)){
                cacheNameList.add(RedisConfigFactory.DEFAULT_GROUP_NAME);
            }else{
                cacheNameList.add(cache);
            }
        }
        if (cacheNameList.size()==0){
            cacheNameList.add(RedisConfigFactory.DEFAULT_GROUP_NAME);
        }
        return cacheNameList.toArray(new String[]{});
    }
}
