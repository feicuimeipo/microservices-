package com.nx.amqp.adapter;


import com.nx.amqp.adapter.MQMessage;

public interface MQProducer {

	public void start() throws Exception;

	/**
	 * 
	 * @param message
	 * @param async  是否异步
	 * @return
	 */
	public String sendMessage(MQMessage message, boolean async);
	
	public void shutdown();
}
