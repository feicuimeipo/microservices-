package com.nx.amqp.adapter.provider.kafka;


import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.provider.AbstractProducer;
import com.nx.amqp.adapter.MQMessage;
//import com.pharmcube.api.context.SpringUtils;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;


public class KafkaProducerAdapter extends AbstractProducer {

	private final static Logger logger = LoggerFactory.getLogger(KafkaProducerAdapter.class);
	
	private KafkaProducer<String, Object> kafkaProducer;
	private MQProviderConfig mqProviderConfig;

	@Override
	public void start() throws Exception {
		mqProviderConfig = MQProviderConfigFactory.getConfig(MQProviderEnum.kafka,DEFAULT_GROUP_NAME);
		Properties configs = KafkaPropertiesUtils.buildProductConfigs(mqProviderConfig);
        kafkaProducer = new KafkaProducer<String, Object>(configs);
	}

	@Override
	public String sendMessage(MQMessage message, boolean async) {
		String topic = message.getTopic();
		Integer partition = null; //
		String key = message.getBizKey();
		String value = message.toMessageValue(true);
		List<Header> headers = null;
		
		ProducerRecord<String,Object> producerRecord = new ProducerRecord<String, Object>(topic, partition, key, value, headers);

		if (async) {
			kafkaProducer.send(producerRecord, new Callback(){
				@Override
				public void onCompletion(RecordMetadata recordMetadata, Exception e) {
					message.onProducerFinished(null,recordMetadata.partition(), recordMetadata.offset());
                    if (e == null) {//成功发送
                        handleSuccess(message);
                        logger.debug("发送成功, topic:{}, partition:{}, offset:{}", topic, recordMetadata.partition(), recordMetadata.offset());
                    }else{
                        //发送失败
                        handleError(message, e);
                        logger.warn("发送失败, topic:{}, partition:{}, offset:{}, exception:{}", topic, recordMetadata.partition(), recordMetadata.offset(), e);
                    }
				}
			});
        } else {
            try {
                Future<RecordMetadata> future= kafkaProducer.send(producerRecord);
                RecordMetadata recordMetadata = future.get();
                message.onProducerFinished(null,recordMetadata.partition(), recordMetadata.offset());
                this.handleSuccess(message);
            } catch (Exception e) {
                this.handleError(message, e);
            }
        }
		return null;
	}

	@Override
	public void shutdown() {
		kafkaProducer.close();
	}
	


}
