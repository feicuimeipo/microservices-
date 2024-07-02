package com.nx.cache;

import com.nx.cache.adapter.CacheAdapter;
import com.nx.cache.adapter.impl.CaffeineCacheAdapter;
import com.nx.cache.adapter.impl.GuavaCacheAdapter;
import com.nx.cache.adapter.impl.RedisCacheAdapter;
import com.nx.cache.enums.CacheModeEnum;
import com.nx.cache.config.NxCacheConfig;
import com.nx.cache.exception.LoaderCacheValueException;
import com.nx.cache.utils.NullValue;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * Cache 接口的抽象实现类，对公共的方法做了一些实现，如是否允许存NULL值
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月15日
 */
class CacheProviderImpl implements CacheProvider {
   private static final Logger log = LoggerFactory.getLogger(CacheProviderImpl.class);

   private final CacheAdapter localCache;
   private final RedisCacheAdapter redisCache;
   private NxCacheConfig config;
    /**
     * 是否使用二级缓存
     */
  private final boolean use2Level;
  private final boolean use1Level;


    /**
     * 缓存名称
     */
    @Getter
    private final String cacheGroupName;

    /**
     * 通过构造方法设置缓存配置
     */
    public CacheProviderImpl(String cacheGroupName, CacheAdapter localCache, RedisCacheAdapter redisCache, NxCacheConfig nxCacheConfig) {
        Assert.notNull(cacheGroupName, "缓存名称不能为NULL");
        this.cacheGroupName = cacheGroupName;
        this.localCache = localCache;
        this.redisCache = redisCache;
        this.config = nxCacheConfig;
        this.use1Level = config.isUse1LevelCache();
        this.use2Level = config.isUse2LevelCache();
    }

    /**
     * 本地缓存
     * @return
     */
    @Override
    public CacheAdapter get1LevelCache() {
        return localCache;
    }

    /**
     * redis
     * @return
     */
    @Override
    public RedisCacheAdapter getLevel2Cache() {
        return redisCache;
    }

    @Override
    public <T> T get(String key) {
        T obj = null;
        if (localCache!=null){
            obj = localCache.get(key);
        }
        if (obj==null && redisCache!=null){
            redisCache.get(key);
            localCache.set(key,obj);
        }
        return obj;
    }

    @Override
    public <T> T getFromLevel2(String  key) {
        T obj = null;
        if (redisCache!=null){
            redisCache.get(key);
        }
        return obj;
    }

    @Override
    public <T> T getFromLocalCache(String key) {
        T obj = null;
        if (localCache!=null){
            obj = localCache.get(key);
        }
        return obj;
    }

    @Override
    public <T> T getFromLevel1(String key) {
       return getFromLocalCache(key);
    }


    @Override
    public Map<String, Object> getAll(Iterable<String> keys) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (keys==null)
            return map;

        for (String key : keys) {
            map.put(key, get(key));
        }
        return map;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        T result = get(key);
        return result;
    }

    /**
     * 加载数据
     */
    private <T> T loaderValue(String key, Callable<T> valueLoader) {
        try {
            T t = valueLoader.call();
            log.debug("caffeine缓存 key={} 从库加载缓存", key);
            return t;
        } catch (Exception e) {
            throw new LoaderCacheValueException(key, e);
        }
    }

    @Override
    public <T> T get(String key, Callable<T> valueLoader) {
        log.debug("caffeine缓存 key={} 获取缓存， 如果没有命中就走库加载缓存", key);
        T result = get(key);
        if (result==null){
            result = (T) loaderValue(key,valueLoader);
        }
        if (result instanceof NullValue) {
            return null;
        }
        return result;
    }


    @Override
    public <T> T get(String key, Callable<T> valueLoader,Class<T> retrunClass) {
        T result = get(key,valueLoader);
        return result;
    }


    @Override
    public void set(String key, Object value, boolean allowNullValue) {
        set(key,value,-1,-1,allowNullValue);
    }

    @Override
    public void set(String key, Object value) {
        set(key,value,-1,-1, TimeUnit.MILLISECONDS,false);
    }

    @Override
    public void set(String key, Object value, long expire) {
        set(key,value,expire,expire, TimeUnit.MILLISECONDS,false);
    }

    @Override
    public void set(String key, Object value, long expire, TimeUnit timeUnit){
        set(key,value,expire,expire,timeUnit,false);
    }

    @Override
    public void set(String key, Object value, long firstExpire, long secondExpire, boolean allowNullValue) {
        set(key,value,firstExpire,secondExpire, TimeUnit.MILLISECONDS,allowNullValue);
    }


    @Override
    public void set(String key, Object value, long expire, boolean allowNullValue) {
        set(key,value,expire,expire, TimeUnit.MILLISECONDS,allowNullValue);
    }

    @Override
    public void set(String key, Object value, long firstExpire, long secondExpire){
        set(key,value,firstExpire,secondExpire, TimeUnit.MILLISECONDS,false);
    }

    @Override
    public void set(String key, Object value, long firstExpire, long secondExpire, TimeUnit timeUnit, boolean allowNullValue) {

        if (use1Level && localCache != null) {
            localCache.set(key,value,allowNullValue,firstExpire, timeUnit);
        }

        if (use2Level && redisCache!=null){
            redisCache.set(key,value,allowNullValue,secondExpire, timeUnit);
        }
    }

    @Override
    public void set(String key, Object value, long level1Expire, long level2Expire, CacheModeEnum cacheModeEnum, boolean level1allowNullValue, boolean level2AllowNullValue){
        if ((cacheModeEnum == CacheModeEnum.Both || cacheModeEnum == CacheModeEnum.Use1LevelCache) && localCache != null) {
            localCache.set(key,value,level1allowNullValue,level1Expire, TimeUnit.MILLISECONDS);
        }

        if ((cacheModeEnum == CacheModeEnum.Both || cacheModeEnum == CacheModeEnum.Use2LevelCache) && redisCache!=null){
            redisCache.set(key,value,level2AllowNullValue,level2Expire, TimeUnit.MILLISECONDS);
        }
    }


    @Override
    public void evict(String key) {
        localCache.remove(key);
        redisCache.remove(key);
    }

    @Override
    public void hdel(String key, String field) {
        if (use1Level && localCache!=null){
            try {
                localCache.get(key,Map.class).remove(field);
            }catch (Exception e){
                log.warn(e.getMessage());
            }
        }

        if (use2Level && redisCache != null){
            if (redisCache instanceof RedisCacheAdapter){
                RedisTemplate<String, Object> redisTemplate =  ((RedisCacheAdapter) redisCache).getRedisTemplate();
                redisTemplate.opsForHash().delete(key,field);
            }
        }
    }

    @Override
    public void evictAll(Iterable<String> keys) {
        List<String> keyList = new ArrayList<>();
        keys.forEach(key -> keyList.add(key));

        if (use1Level && localCache!=null){
            localCache.remove(keyList.toArray(new String[]{}));
        }

        if (use2Level && redisCache!=null){
            redisCache.remove(keyList.toArray(new String[]{}));
        }
    }

    @Override
    public void clearLocalCache() {
        if (localCache!=null){
            if (localCache instanceof GuavaCacheAdapter){
                ((GuavaCacheAdapter) localCache).getCache().invalidateAll();
            }else{
                ((CaffeineCacheAdapter) localCache).getCache().invalidateAll();
            }
        }
    }



    @Override
    public void remove(String... keys) {
        if (redisCache!=null){
            redisCache.remove(keys);
        }
        if (localCache!=null){
            localCache.remove(keys);
        }
    }

    @Override
    public boolean exists(String key) {
        if (redisCache!=null){
            redisCache.exists(key);
        }
        if (localCache!=null){
            localCache.exists(key);
        }
        return true;
    }

    @Override
    public void addListItems(String key, String... items) {
        if (redisCache!=null){
            redisCache.addListItems(key,items);
        }
        if (localCache!=null){
            localCache.addListItems(key,items);
        }
    }

    @Override
    public List<Object> getListItems(String key, int start, int end) {
        List<Object> list = null;
        if (localCache!=null){
            localCache.getListItems(key,start,end);
        }
        if ((list==null || list.size()==0) && redisCache!=null) {
            list = redisCache.getListItems(key,start,end);
        }
        return list;
    }


    @Override
    public void setMapItem(String key, String field, Object value) {

        if (localCache!=null){
            localCache.setMapItem(key,field,value);
        }
        if (redisCache!=null){
            redisCache.setMapItem(key,field,value);
        }
    }




}
