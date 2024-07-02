package com.nx.amqp.adapter.provider;

import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.MQProducer;
import com.nx.amqp.adapter.utils.MQContext;
import com.nx.amqp.adapter.enums.ActionType;

public abstract class AbstractProducer implements MQProducer {
	@Override
	public void start() throws Exception {}

	@Override
	public void shutdown() {}
	
	
	public void handleSuccess(MQMessage message) {
		MQContext.processMessageLog(MQProviderConfigFactory.getConfig(),message, ActionType.pub, null);
	}

	public void handleError(MQMessage message, Throwable e) {
		MQContext.processMessageLog(MQProviderConfigFactory.getConfig(),message,ActionType.pub, e);
	}

}
