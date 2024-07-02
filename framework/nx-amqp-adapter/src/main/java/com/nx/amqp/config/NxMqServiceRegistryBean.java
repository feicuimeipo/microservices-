 
package com.nx.amqp.config;

import com.nx.amqp.adapter.*;
import com.nx.amqp.adapter.annotation.MQTopicRef;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.provider.eventbus.EventbusProducerAdapter;
import com.nx.amqp.adapter.provider.kafka.KafkaConsumerAdapter;
import com.nx.amqp.adapter.provider.kafka.KafkaProducerAdapter;
import com.nx.amqp.adapter.provider.qcloud.cmq.CMQConsumerAdapter;
import com.nx.amqp.adapter.provider.qcloud.cmq.CMQProducerAdapter;
import com.nx.amqp.adapter.provider.redis.RedisConsumerAdapter;
import com.nx.amqp.adapter.provider.redis.RedisProducerAdapter;
import com.nx.amqp.adapter.provider.rocketmq.RocketProducerAdapter;
import com.nx.amqp.adapter.provider.rocketmq.RocketmqConsumerAdapter;
import com.nx.amqp.adapter.utils.MQContext;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.SpringUtils;
import com.nx.common.exception.BaseException;
import com.nx.redis.RedisTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.util.HashMap;
import java.util.Map;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;

@Configuration
@Slf4j
@ConditionalOnMissingBean(NxMqServiceRegistryBean.class)
@ConditionalOnProperty(prefix = "nx.amqp",value = "enabled",havingValue = "true",matchIfMissing = true)
@ConfigurationProperties(prefix = "nx.amqp")
public class NxMqServiceRegistryBean implements InitializingBean,DisposableBean,ApplicationContextAware,PriorityOrdered {

	 private String provider;

	 public NxMqServiceRegistryBean(){

	 }

	protected  static final Logger logger = LoggerFactory.getLogger(NxMqServiceRegistryBean.class);
	private ApplicationContext applicationContext;
	private MQConsumer consumer;
	private MQProducer producer;
	private MQProviderConfig providerConfig ;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (StringUtils.isNotEmpty(provider)){
			MQProviderConfigFactory.DEFAULT_GROUP_NAME = provider;
		}

		String enabled = SpringUtils.Env.getProperty(DEFAULT_GROUP_NAME+".enabled");
		if (enabled!=null && (enabled.equalsIgnoreCase("true") || enabled.equalsIgnoreCase("yes") || enabled.equalsIgnoreCase("on"))){
			String provider = SpringUtils.Env.getProperty(DEFAULT_GROUP_NAME+".provider");
			MQProviderEnum providerEnum = MQProviderEnum.valueByCode(provider);
			if (providerEnum==null){
				throw new BaseException(DEFAULT_GROUP_NAME+".provider，不可以为空！");
			}
			MQProviderConfigFactory.enabled = true;
			MQProviderConfigFactory.provider = providerEnum;
			providerConfig = MQProviderConfigFactory.getConfig(providerEnum,DEFAULT_GROUP_NAME);
		}

		if(providerConfig.getProducer().isEnabled()){
			startProducer(providerConfig.getProvider());
			MQInstanceDelegate.setProducer(producer);
		}
		//
		if(providerConfig.getConsumer().isEnabled()){
			startConsumer(providerConfig.getProvider());
		}

		BannerUtils.push(this.getClass(),"nx-amqp-adapter: "+ provider +" enabled");
	}

	private void startProducer(MQProviderEnum provider) throws Exception{

		switch (provider){
			case kafka:
				producer = new KafkaProducerAdapter();
				break;
			case rocketmq:
				producer = new RocketProducerAdapter();
				break;
			case aliyun_mns:
				//TODO
				break;
			case aliyun_ons:
				//TODO
				break;
			case qcloud_cmq:
				producer = new CMQProducerAdapter();
				break;
			case eventbus:
				producer = new EventbusProducerAdapter();
				break;
			case redis:

				producer = new RedisProducerAdapter(RedisTemplateFactory.getDefaultStringRedisTemplate());


				break;
			default:
				throw new BaseException("NOT_SUPPORT[providerName]:" + provider.getCode());
		}

		producer.start();
		logger.info("-->> MQ_PRODUCER started -> groupName:{},providerName:{}",providerConfig.getGroupName(),provider.getCode());
	}

	private void startConsumer(MQProviderEnum provider) throws Exception{
		Map<String, MessageHandler> messageHanlders = applicationContext.getBeansOfType(MessageHandler.class);
		if(messageHanlders != null && !messageHanlders.isEmpty()){
			Map<String, MessageHandler> messageHandlerMaps = new HashMap<>();
			messageHanlders.values().forEach(e -> {
				Object origin = e;
				try {origin = SpringAopHelper.getTarget(e);} catch (Exception ex) {ex.printStackTrace();}
				MQTopicRef topicRef = origin.getClass().getAnnotation(MQTopicRef.class);
				String topicName = MQContext.rebuildWithNamespace(topicRef.value(),providerConfig);
				messageHandlerMaps.put(topicName, e);
				logger.info("-->> ADD MQ_COMSUMER_HANDLER ->topic:{},handlerClass:{} ",topicName,e.getClass().getName());
			});


			switch (provider){
				case kafka:
					consumer = new KafkaConsumerAdapter(messageHandlerMaps);
					break;
				case rocketmq:
					consumer = new RocketmqConsumerAdapter(messageHandlerMaps);
					break;
				case aliyun_mns:
					//TODO
					break;
				case aliyun_ons:
					//TODO
					break;
				case qcloud_cmq:
					consumer = new CMQConsumerAdapter(messageHandlerMaps);
					break;
				case eventbus:
					EventbusProducerAdapter.setMessageHandlers(messageHandlerMaps);
					break;
				case redis:
					consumer = new RedisConsumerAdapter(messageHandlerMaps);
					break;
				default:
					throw new BaseException("NOT_SUPPORT[providerName]:" + provider.getCode());
			}

			if(consumer != null) {
				consumer.start();
			}
			logger.info("-->> MQ_COMSUMER started -> groupName:{},providerName:{}",providerConfig.getGroupName(),provider.getCode());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		SpringUtils.setContext(applicationContext);
	}

	@Override
	public void destroy() throws Exception {
		if(consumer != null) {
			consumer.shutdown();
		}
		if(producer != null) {
			producer.shutdown();
		}
		MQContext.close(providerConfig);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}


}
