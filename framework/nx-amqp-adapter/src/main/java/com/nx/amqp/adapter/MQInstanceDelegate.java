package com.nx.amqp.adapter;

import com.nx.amqp.adapter.utils.MQContext;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class MQInstanceDelegate {
	
	private static MQProducer producer;
	
	private MQInstanceDelegate() {}

	public static void setProducer(MQProducer producer) {
		MQInstanceDelegate.producer = producer;
	}

	public static void send(MQMessage message){
		MQProviderConfig providerConfig = MQProviderConfigFactory.getConfig(MQProviderConfigFactory.provider,MQProviderConfigFactory.DEFAULT_GROUP_NAME);
		if(producer == null){
			log.info("MQProducer did not Initialization,Please check config[mq.provider] AND [mq.producer.enabled]");
			return;
		}
		message.setTopic(MQContext.rebuildWithNamespace(message.getTopic(),providerConfig));
		producer.sendMessage(message, false);
	}
	
    public static void asyncSend(MQMessage message){
    	if(producer == null){
			log.info("MQProducer did not Initialization,Please check config[mq.provider] AND [mq.producer.enabled]");
    		return;
		}
    	producer.sendMessage(message, true);
	}
}
