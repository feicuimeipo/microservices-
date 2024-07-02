package com.nx.amqp.adapter.provider.redis.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nx.amqp.adapter.provider.redis.message.AbstractRedisMessage;

/**
 * Redis Stream Message 抽象类
 *
 * @author 念熹科技
 */
public abstract class AbstractStreamMessage extends AbstractRedisMessage {

    /**
     * 获得 Redis Stream Key
     *
     * @return Channel
     */
    @JsonIgnore // 避免序列化
    public abstract String getStreamKey();

}
