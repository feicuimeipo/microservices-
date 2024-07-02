package cn.iocoder.yudao.module.system.dal.redis.datalang;

import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static cn.iocoder.yudao.module.system.dal.redis.RedisKeyConstants.DataLang;

/**
 * {@link OAuth2AccessTokenDO} 的 RedisDAO
 *
 * @author 锐信视通
 */
@Repository
public class DataLangRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String get(String userId) {
        String redisKey = formatKey(userId);
        return stringRedisTemplate.opsForValue().get(redisKey);
    }


    public void set(String userId,String dataLang) {
        if (StringUtils.isNotEmpty(dataLang)) {
            String redisKey = formatKey(userId);
            // 清理多余字段，避免缓存
            stringRedisTemplate.opsForValue().set(redisKey,dataLang,24, TimeUnit.HOURS);
        }
    }

    public void delete(String userId) {
        String redisKey = formatKey(userId);
        stringRedisTemplate.delete(redisKey);
    }

    public void deleteList(Collection<String> userId) {
        List<String> redisKeys = CollectionUtils.convertList(userId, DataLangRedisDAO::formatKey);
        stringRedisTemplate.delete(redisKeys);
    }

    private static String formatKey(String userId) {
        return String.format(DataLang.getKeyTemplate(), userId);
    }

}
