package com.nx.amqp.adapter.provider.redis;

import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.provider.AbstractProducer;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;


public class RedisProducerAdapter extends AbstractProducer {

	private final Logger logger = LoggerFactory.getLogger(RedisProducerAdapter.class);

	private StringRedisTemplate redisTemplate;
	
	public RedisProducerAdapter(StringRedisTemplate redisTemplate) {
		Validate.notNull(redisTemplate, "can't load bean [redisTemplate]");
		this.redisTemplate = redisTemplate;

	}

	@Override
	public void start() throws Exception{
		super.start();
	}
	
	@Override
	public String sendMessage(MQMessage message, boolean async) {
		try {
			redisTemplate.convertAndSend(message.getTopic(), message.toMessageValue(false));
			handleSuccess(message);
		} catch (Exception e) {
			handleError(message, e);
			logger.warn("MQ_SEND_FAIL:"+message.getTopic(),e);
		}
		
		return null;
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}

	

}
