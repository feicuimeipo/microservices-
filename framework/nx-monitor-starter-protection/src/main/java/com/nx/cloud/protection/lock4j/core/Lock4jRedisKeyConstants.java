package com.nx.cloud.protection.lock4j.core;

import com.nx.redis.utils.RedisKeyDefine;
import org.redisson.api.RLock;

import static com.nx.redis.utils.RedisKeyDefine.KeyTypeEnum.HASH;

/**
 * Lock4j Redis Key 枚举类
 *
 * @author 念熹科技
 */
public interface Lock4jRedisKeyConstants {

    RedisKeyDefine LOCK4J = new RedisKeyDefine("分布式锁",
            "lock4j:%s", // 参数来自 DefaultLockKeyBuilder 类
            HASH, RLock.class, RedisKeyDefine.TimeoutTypeEnum.DYNAMIC); // Redisson 的 Lock 锁，使用 Hash 数据结构

}
