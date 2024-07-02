package com.nx.amqp.adapter.provider.rocketmq;

import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.provider.AbstractProducer;
import com.nx.common.context.CurrentRuntimeContext;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;


public class RocketProducerAdapter extends AbstractProducer {

	private final Logger logger = LoggerFactory.getLogger(RocketProducerAdapter.class);

	private String productGroup;
	private String namesrvAddr;
	private MQProviderConfig MQProviderConfig;
	private DefaultMQProducer producer;
	
	/**

	 */
	public RocketProducerAdapter() {
		this.MQProviderConfig =  MQProviderConfigFactory.getConfig(MQProviderEnum.qcloud_cmq,DEFAULT_GROUP_NAME);
		this.productGroup = MQProviderConfig.getMqGroup();
		this.namesrvAddr = MQProviderConfig.getRocketMQ().getNamesrvAddr();
	}

	@Override
	public void start() throws Exception{
		super.start();

		producer = new DefaultMQProducer(productGroup);
		producer.setNamesrvAddr(namesrvAddr);
		producer.start();
	}

	@Override
	public String sendMessage(MQMessage message,boolean async) {
		Message _message = new Message(message.getTopic(), message.getTag(), message.getBizKey(), message.bodyAsBytes());

		CurrentRuntimeContext.getContextHeaders().forEach( (k, v) -> {
			_message.putUserProperty(k, v);
		} );

		try {
			if(async){
				producer.send(_message, new SendCallback() {
					@Override
					public void onSuccess(SendResult sendResult) {
						if(logger.isDebugEnabled())logger.debug("MENDMIX-TRACE-LOGGGING-->> MQ_SEND_SUCCESS:{} -> msgId:{},status:{},offset:{}",message.getTopic(),sendResult.getMsgId(),sendResult.getSendStatus().name(),sendResult.getQueueOffset());
						message.setMsgId(sendResult.getMsgId());
						handleSuccess(message);
					}

					@Override
					public void onException(Throwable e) {
						handleError(message, e);
						logger.warn("MENDMIX-TRACE-LOGGGING-->> MQ_SEND_FAIL:"+message.getTopic(),e);
					}
				});
			}else{
				SendResult sendResult = producer.send(_message);
				message.setMsgId(sendResult.getMsgId());
				if(sendResult.getSendStatus() == SendStatus.SEND_OK) {
					handleSuccess(message);
				}else {
					handleError(message, new MQClientException(0, sendResult.getSendStatus().name()));
				}
			}
		} catch (Exception e) {
			handleError(message, e);
			throw new RuntimeException("rocketMQ_Producer_error",e);
		}

		return null;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		producer.shutdown();
	}



}
