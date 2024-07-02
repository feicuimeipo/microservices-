package com.nx.amqp.adapter.provider.aliyum.mns;

import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.model.RawTopicMessage;
import com.aliyun.mns.model.TopicMessage;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.MQMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2019年3月18日
 */
public class MNSProducer  {

    private Map<String, CloudTopic> topics = new ConcurrentHashMap<>();

    public MNSProducer() {}

    public String publishMessage(String topicName,Object data){
        return publishMessage(topicName,data,DEFAULT_GROUP_NAME);
    }
    public String publishMessage(String topicName,Object data,String groupName){
        MQProviderConfig mnsmMQProperties =  MQProviderConfigFactory.getConfig(MQProviderEnum.aliyun_mns,groupName);

        CloudTopic topic = getTopic(topicName);
        TopicMessage tMessage = new RawTopicMessage();
        tMessage.setBaseMessageBody(new MQMessage(topicName, data).toMessageValue(false));
        topic.publishMessage(tMessage);

        return tMessage.getMessageId();
    }

    public CloudTopic getTopic(String topicName) {
        if(!topics.containsKey(topicName)){
            synchronized (this) {
                if(!topics.containsKey(topicName)){
                    CloudTopic topic = MNSClientInstance.createTopicIfAbsent(topicName, null);
                    topics.put(topicName, topic);
                }
            }
        }
        return topics.get(topicName);
    }
}