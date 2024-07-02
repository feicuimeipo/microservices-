package com.nx.amqp.adapter.provider.redis;

import com.nx.amqp.adapter.utils.MQContext;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.enums.ActionType;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.MQMessage;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.amqp.adapter.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;

public class MessageHandlerDelegate {

	private final static Logger logger = LoggerFactory.getLogger(MessageHandlerDelegate.class);

	private MessageHandler   messageHandler;
	private MQProviderConfig mqProviderConfig;

	public MessageHandlerDelegate(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		mqProviderConfig = MQProviderConfigFactory.getConfig(MQProviderEnum.redis,DEFAULT_GROUP_NAME);
	}


	public void onMessage(String body, String topic) {
		MQMessage message = MQMessage.build(body);
		try {
			//上下文
			if(message.getHeaders() != null) {
				message.getHeaders().keySet().forEach(key->{
					CurrentRuntimeContext.addContextHeader(key,message.getHeaders().get(key));
				});

			}
			messageHandler.process(message);
			if(logger.isDebugEnabled())logger.debug("-->> MQ_MESSAGE_CONSUME_SUCCESS ->message:{}",message.toString());
			MQContext.processMessageLog(mqProviderConfig,message, ActionType.sub,null);
		} catch (Exception e) {
			MQContext.processMessageLog(mqProviderConfig,message, ActionType.sub,e);
			logger.error(String.format("-->> MQ_MESSAGE_CONSUME_ERROR ->message:%s",body),e);
		}

	}

	
}
