package com.nx.amqp.adapter.provider.redis;


import com.nx.amqp.adapter.provider.redis.message.AbstractRedisMessage;
import com.nx.amqp.adapter.provider.redis.pubsub.AbstractChannelMessage;
import com.nx.amqp.adapter.provider.redis.interceptor.RedisMessageEvent;
import com.nx.amqp.adapter.provider.redis.stream.AbstractStreamMessage;
import com.nx.amqp.adapter.utils.MQJsonUtils;
import lombok.*;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis MQ 操作模板类
 *
 * @author 念熹科技
 */
@Component
@RequiredArgsConstructor
@Data
public class RedisMQTemplate {

    private final   RedisTemplate<String, ?> redisTemplate;
    private final   List<RedisMessageEvent> interceptors;
    /**
     * 发送 Redis 消息，基于 Redis pub/sub 实现
     *
     * @param message 消息
     */
    public <T extends AbstractChannelMessage> void send(T message) {
        try {
            sendMessageBefore(message);
            // 发送消息

            if (redisTemplate!=null) {
                redisTemplate.convertAndSend(message.getChannel(), MQJsonUtils.toJsonString(message));
            }
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 发送 Redis 消息，基于 Redis Stream 实现
     *
     * @param message 消息
     * @return 消息记录的编号对象
     */
    public <T extends AbstractStreamMessage> RecordId send(T message) {
        try {
            sendMessageBefore(message);
            // 发送消息

            if (redisTemplate!=null) {
                return redisTemplate.opsForStream().add(StreamRecords.newRecord()
                        .ofObject(MQJsonUtils.toJsonString(message)) // 设置内容
                        .withStreamKey(message.getStreamKey())); // 设置 stream key
            }
           return null;
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     */
    public void addInterceptor(RedisMessageEvent interceptor) {
        interceptors.add(interceptor);
    }

    private void sendMessageBefore(AbstractRedisMessage message) {
        // 正序
        interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
    }

    private void sendMessageAfter(AbstractRedisMessage message) {
        // 倒序
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).sendMessageAfter(message);
        }
    }

}
