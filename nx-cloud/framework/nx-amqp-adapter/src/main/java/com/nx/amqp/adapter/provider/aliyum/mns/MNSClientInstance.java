package com.nx.amqp.adapter.provider.aliyum.mns;

import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import org.apache.commons.lang3.StringUtils;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.PagingListResult;
import com.aliyun.mns.model.QueueMeta;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.TopicMeta;

import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;


/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2019年3月18日
 */
public class MNSClientInstance {
    private static MNSClient client;
    public static MNSClient getClient() {
        return getClient(DEFAULT_GROUP_NAME);
    }

    public static MNSClient getClient(String groupName) {
        if(client != null) return client;
        if (groupName==null){
            groupName = DEFAULT_GROUP_NAME;
        }

        MQProviderConfig mnsmMQProperties =  MQProviderConfigFactory.getConfig(MQProviderEnum.aliyun_mns,groupName);
        if (mnsmMQProperties ==null || mnsmMQProperties.getAliyumMnsProps()==null){
            throw new RuntimeException(String.format("异步息配置provide:{}在gorupName:{}上的配置为空！",MQProviderEnum.aliyun_mns.getCode(),groupName));
        }
        synchronized (MNSClientInstance.class) {
            if(client != null)return client;
            CloudAccount account = mnsmMQProperties.getAliyumMnsProps().getCloudAccount();
            client = account.getMNSClient();
        }
        return client;
    }

    public static CloudQueue createQueueIfAbsent(String queueName){
        QueueMeta queueMeta = new QueueMeta();
        queueMeta.setQueueName(queueName);
        CloudQueue queue = getClient().getQueueRef(queueName);
        if(!queue.isQueueExist()){
            queue.create(queueMeta);
        }
        return queue;
    }

    public static CloudTopic createTopicIfAbsent(String topicName,String subForQueue){
        TopicMeta topicMeta = new TopicMeta();
        topicMeta.setTopicName(topicName);
        CloudTopic topic = getClient().getTopicRef(topicName);
        try {
            topic.getAttribute();
        } catch (ServiceException e) {
            if("TopicNotExist".equals(e.getErrorCode())){
                topic.create(topicMeta);
            }
        }

        if(StringUtils.isNotBlank(subForQueue)){
            String subscriptionName = "sub-for-queue-"+subForQueue;
            PagingListResult<SubscriptionMeta> topicSubscriptions = topic.listSubscriptions(subscriptionName, "", 1);
            if(topicSubscriptions == null || topicSubscriptions.getResult() == null || topicSubscriptions.getResult().isEmpty()){
                //创建订阅关系
                SubscriptionMeta subMeta = new SubscriptionMeta();
                subMeta.setSubscriptionName(subscriptionName);
                subMeta.setEndpoint(topic.generateQueueEndpoint(subForQueue));
                subMeta.setNotifyContentFormat(SubscriptionMeta.NotifyContentFormat.SIMPLIFIED);
                subMeta.setNotifyStrategy(SubscriptionMeta.NotifyStrategy.EXPONENTIAL_DECAY_RETRY);

                topic.subscribe(subMeta);
            }
        }
        return topic;
    }

}