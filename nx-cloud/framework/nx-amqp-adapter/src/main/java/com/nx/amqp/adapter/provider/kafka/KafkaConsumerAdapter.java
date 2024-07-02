package com.nx.amqp.adapter.provider.kafka;

import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.provider.AbstractConsumer;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.MessageHandler;
import com.nx.common.banner.BannerUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;

@Slf4j
public class KafkaConsumerAdapter extends AbstractConsumer {
	private Map<TopicPartition, OffsetAndMetadataStat> uncommitOffsetStats = new ConcurrentHashMap<>();
	private KafkaConsumer<String, String> kafkaConsumer;
	private Duration timeoutDuration;
	private boolean offsetAutoCommit;
	private MQProviderConfig mqProviderConfig;

	public KafkaConsumerAdapter(Map<String, MessageHandler> messageHandlers) {
		super(messageHandlers);
	}

	@Override
	public void start() throws Exception {
		log.info(">>KafkaConsumer start Begin..");
		mqProviderConfig =  MQProviderConfigFactory.getConfig(MQProviderEnum.kafka,DEFAULT_GROUP_NAME);
		Properties configs = KafkaPropertiesUtils.buildConsumerConfigs(mqProviderConfig);

		timeoutDuration = mqProviderConfig.getFetchTimeout();
		offsetAutoCommit = Boolean.parseBoolean(configs.getProperty(ENABLE_AUTO_COMMIT_CONFIG));
		kafkaConsumer = new KafkaConsumer<>(configs);
		Set<String> topicNames = messageHandlers.keySet();
		if(offsetAutoCommit) {
			kafkaConsumer.subscribe(topicNames);
		}else {
			ConsumerRebalanceListener listener = new ConsumerRebalanceListener() {
				//准备重新负载均衡，停止消费消息
				@Override
				public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
					kafkaConsumer.commitSync(); //手动提交
				}
				//完成负载均衡，准备重新消费消息
				@Override
				public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
					uncommitOffsetStats.clear();
					for (TopicPartition partition : partitions) {
						uncommitOffsetStats.put(partition, new OffsetAndMetadataStat(0));
					}
				}
			};
			kafkaConsumer.subscribe(topicNames,listener);
		}

		super.startWorker();



		log.info(">>KafkaConsumer start End -> subscribeTopics:{}",configs,topicNames);
	}
	
	@Override
	public List<MQMessage> fetchMessages() {
		 //手动提交offset
		 trySubmitOffsets();
		 ConsumerRecords<String, String> records = kafkaConsumer.poll(timeoutDuration);
		 Iterator<ConsumerRecord<String, String>> iterator = records.iterator();
		 List<MQMessage> result = new ArrayList<>(records.count());
		 MQMessage message;
		 ConsumerRecord<String, String> item;
		 while(iterator.hasNext()) {
			 item = iterator.next();
			 message = new MQMessage(item.topic(), item.value());
			 message.setOriginMessage(item);
			 result.add(message);
		 }
		 return result;
	}
	
	@Override
	public String handleMessageConsumed(MQMessage message) {
		if(offsetAutoCommit)return null;
		ConsumerRecord<String, String> originMessage = message.getOriginMessage(ConsumerRecord.class);
		TopicPartition partition = new TopicPartition(originMessage.topic(), originMessage.partition());
		if(mqProviderConfig.getConsumer().isAsync()){
			uncommitOffsetStats.get(partition).updateOnConsumed(originMessage.offset());
		}else {
			Map<TopicPartition, OffsetAndMetadata> uncommitOffsets = new HashMap<>(1);
			uncommitOffsets.put(partition, new OffsetAndMetadata(originMessage.offset() + 1));
			submitOffsets(uncommitOffsets);
		}
		return null;
	}
	
	private void trySubmitOffsets() {
		if(offsetAutoCommit || !mqProviderConfig.getConsumer().isAsync()){
			return;
		}
		
		Map<TopicPartition, OffsetAndMetadata> uncommitOffsets = new HashMap<>(uncommitOffsetStats.size());
		uncommitOffsetStats.forEach( (k,v) -> {
			if(!v.isCommited()) {
				uncommitOffsets.put(k, new OffsetAndMetadata(v.getOffset() + 1));
			}
		} );
		
		submitOffsets(uncommitOffsets);
	}
	
	/**
	 * 手动提交offset
	 */
	private synchronized void submitOffsets(Map<TopicPartition, OffsetAndMetadata> uncommitOffsets) {
		if(uncommitOffsets.isEmpty())return;
		kafkaConsumer.commitAsync(uncommitOffsets, new OffsetCommitCallback() {
			@Override
			public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
				if(exception != null) {
					kafkaConsumer.commitSync(uncommitOffsets);
				}else {
					if(log.isDebugEnabled())log.debug("MQmessage_COMMIT_SUCCESS -> offsets:{}",offsets);
				}
				offsets.forEach( (k,v) -> {
					uncommitOffsetStats.get(k).setCommited(true);
				} );
			}
		});
	}
	


	@Override
	public void shutdown() {
		super.shutdown();
		kafkaConsumer.close();
	}
		
}
