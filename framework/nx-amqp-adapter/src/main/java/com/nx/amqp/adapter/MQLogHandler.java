package com.nx.amqp.adapter;


import com.nx.amqp.adapter.enums.ActionType;

public interface MQLogHandler {
    
	public void onSuccess(String groupName, ActionType actionType, MQMessage message);

	public void onError(String groupName, ActionType actionType, MQMessage message, Throwable e);

}
