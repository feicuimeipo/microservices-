package com.nx.amqp.adapter;


import com.nx.amqp.adapter.MQMessage;

public interface MessageHandler {

	default boolean retrieable() {return true;}
	/**
	 * 预处理消息（同步）
	 * @param message
	 */
	default void prepare(MQMessage message) {}

	/**
	 * 处理消息（异步）
	 * @param message
	 * @throws Exception
	 */
	void process(MQMessage message) throws Exception;
}
