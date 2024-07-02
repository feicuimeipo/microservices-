package com.nx.amqp.adapter;


public interface MQConsumer {

	public void start() throws Exception;
	
	public void shutdown();
}
