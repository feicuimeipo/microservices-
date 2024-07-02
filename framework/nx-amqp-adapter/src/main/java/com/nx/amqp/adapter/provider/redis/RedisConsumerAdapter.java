package com.nx.amqp.adapter.provider.redis;

import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.utils.async.StandardThreadExecutor;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.MQConsumer;
import com.nx.amqp.adapter.MessageHandler;
import com.nx.amqp.adapter.utils.async.StandardThreadFactory;
import com.nx.redis.RedisTemplateFactory;
import org.apache.commons.lang3.Validate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;


public class RedisConsumerAdapter implements MQConsumer {

	private RedisConnectionFactory connectionFactory;
	private RedisMessageListenerContainer container = new RedisMessageListenerContainer();
	private ThreadPoolExecutor fetchExecutor;
	private StandardThreadExecutor asyncProcessExecutor;
	private MQProviderConfig mqProviderConfig;

	private Map<String, MessageHandler> messageHandlers = new HashMap<>();
	/**
	 * @param messageHandlers
	 */
	public RedisConsumerAdapter(Map<String, MessageHandler> messageHandlers) {
		this.messageHandlers = messageHandlers;
		mqProviderConfig = MQProviderConfigFactory.getConfig(MQProviderEnum.redis,DEFAULT_GROUP_NAME);
	}

	@Override
	public void start() throws Exception {
		StringRedisTemplate redisTemplate = RedisTemplateFactory.getDefaultStringRedisTemplate();
		Validate.notNull(redisTemplate, "can't load bean [redisTemplate]");
		this.connectionFactory = redisTemplate.getConnectionFactory();
		int maxThread = mqProviderConfig.getConsumer().getProcessThreads();//.getMaxProcessThreads();
		this.fetchExecutor = new ThreadPoolExecutor(1, 1,0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),new StandardThreadFactory("messageFetcher"));
		this.asyncProcessExecutor = new StandardThreadExecutor(1, maxThread,60, TimeUnit.SECONDS,1000,new StandardThreadFactory("messageAsyncProcessor"));
		container.setConnectionFactory(connectionFactory);
		container.setSubscriptionExecutor(fetchExecutor);
		container.setTaskExecutor(asyncProcessExecutor);
		//
		Set<String> topics = messageHandlers.keySet();
		MessageListenerAdapter listener;
		for (String topic : topics) {
			MessageHandlerDelegate delegate = new MessageHandlerDelegate(messageHandlers.get(topic));
			listener = new MessageListenerAdapter(delegate, "onMessage");
			listener.afterPropertiesSet();
			container.addMessageListener(listener, new PatternTopic(topic));
		}

		container.afterPropertiesSet();
		container.start();
	}

	@Override
	public void shutdown() {
		fetchExecutor.shutdown();
		asyncProcessExecutor.shutdown();
		container.stop();
		try {container.destroy();} catch (Exception e) {}
	}




}
