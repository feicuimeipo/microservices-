package com.nx.amqp.adapter.provider.rocketmq;

import com.nx.amqp.adapter.utils.MQContext;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.enums.ActionType;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.MessageHandler;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.MQConsumer;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.ThreadLocalContext;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;


public class RocketmqConsumerAdapter implements MQConsumer {
	
	private final static Logger logger = LoggerFactory.getLogger(RocketmqConsumerAdapter.class);
	private MQProviderConfig mqProviderConfig;
	private MQProviderConfig.Consumer consumerConfig;
	private String namesrvAddr;
	private Map<String, MessageHandler> messageHandlers = new HashMap<>();
	private DefaultMQPushConsumer consumer;

	/**
	 * @param messageHandlers
	 */
	public RocketmqConsumerAdapter(Map<String, MessageHandler> messageHandlers) {
		this.namesrvAddr = mqProviderConfig.getRocketMQ().getNamesrvAddr();
		this.messageHandlers = messageHandlers;
	}


	/**
	 * @param namesrvAddr the namesrvAddr to set
	 */
	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	@Override
	public void start() throws Exception {
		mqProviderConfig =  MQProviderConfigFactory.getConfig(MQProviderEnum.qcloud_cmq,DEFAULT_GROUP_NAME);
		consumerConfig = mqProviderConfig.getConsumer();
		int consumeThreads = mqProviderConfig.getConsumer().getProcessThreads();
		String groupName = mqProviderConfig.getMqGroup();
		consumer = new DefaultMQPushConsumer(groupName);
		consumer.setNamesrvAddr(namesrvAddr);
		consumer.setConsumeMessageBatchMaxSize(1); //每次拉取一条
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		consumer.setConsumeThreadMin(consumeThreads);
		consumer.setConsumeThreadMax(consumeThreads);
		consumer.setPullThresholdForQueue(1000);
		consumer.setConsumeConcurrentlyMaxSpan(500);
		for (String topic : messageHandlers.keySet()) {
			consumer.subscribe(topic, "*");
		}
		consumer.registerMessageListener(new CustomMessageListener());
		consumer.start();
	}



	private class CustomMessageListener implements MessageListenerConcurrently{
		@Override
		public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
			if(msgs.isEmpty())return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			MessageExt msg = msgs.get(0); //每次只拉取一条
			if(!messageHandlers.containsKey(msg.getTopic())) {
				logger.warn("log->> not messageHandler found for:{}",msg.getTopic());
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			if(consumerConfig.getMaxRetryTimes() > 0 && msg.getReconsumeTimes() > consumerConfig.getMaxRetryTimes()) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}

			if(consumerConfig.getMaxRetryTimes() > 0 && msg.getReconsumeTimes() > 1 && System.currentTimeMillis() - msg.getBornTimestamp() > consumerConfig.getMaxRetryTimes()) {
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			}
			MQMessage message = new MQMessage(msg.getTopic(),msg.getTags(),msg.getKeys(), msg.getBody());
			message.setOriginMessage(msg);
			message.setHeaders(msg.getProperties());
			//上下文
			if(message.getHeaders() != null) {
				message.getHeaders().keySet().forEach( key->{
					CurrentRuntimeContext.addContextHeader(key,message.getHeaders().get(key));
				});
			}
			//消息状态检查
			if(!message.originStatusCompleted() && message.getConsumeTimes() <= 1) {
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}
			try {
				messageHandlers.get(message.getTopic()).process(message);
				if(logger.isDebugEnabled())logger.debug("log->> MQ_MESSAGE_CONSUME_SUCCESS ->message:{}",message);
				//
				MQContext.processMessageLog(mqProviderConfig,message, ActionType.sub,null);
				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
			} catch (Exception e) {
				logger.error(String.format("log->> MQ_MESSAGE_CONSUME_ERROR ->message:%s",message.toString()),e);
				MQContext.processMessageLog(mqProviderConfig,message,ActionType.sub, e);
				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
			}finally{
				ThreadLocalContext.unset();
			}
		}

	}

	@Override
	public void shutdown() {
		consumer.shutdown();
	}
	
}
