package com.nx.amqp.adapter;

import com.aliyun.mns.client.CloudAccount;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.common.context.ResourceUtils;
import com.nx.common.context.SpringUtils;
import com.nx.logger.enums.LogStorageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;


@Data
public class MQProviderConfig {
    private volatile ThreadPoolExecutor logHandleExecutor;
    private volatile  MQLogHandler mqLogHandler;

    private String namespacePrefix;
    private String applicationName;
    private LogStorageType logStoreType;
    private String profile;

    protected MQProviderEnum provider;
    protected String Namespace;
    protected String mqGroup; //此为消息内部的group
    private String groupName;
    protected LogHandler logHandler;
    protected Producer producer;
    protected Consumer consumer;
    protected Duration fetchTimeout;


    private AliyumMnsProp aliyumMnsProps;
    private Kafka kafka;
    private RocketMQ rocketMQ;

    public String getMqGroup(){
        if (StringUtils.isEmpty(mqGroup)){
            mqGroup = Namespace;
        }
        return mqGroup;
    }



    public MQProviderConfig(String groupName, MQProviderEnum provider){
        this.groupName = groupName;
        this.provider = provider;
    }

    public MQProviderConfig(String groupName){
        String provider = SpringUtils.Env.getProperty(groupName+".amqp.provider");
        MQProviderEnum providerEnum = MQProviderEnum.valueByCode(provider);
        if (providerEnum==null){
            throw new RuntimeException(String.format("{}.amqp.provider不可以为空！",groupName));
        }
        this.provider = providerEnum;
    }


    private void init(){
        applicationName = SpringUtils.Env.getProperty("spring.application.name");
        profile = SpringUtils.Env.getProperty("spring.profiles.active");
        String type = SpringUtils.Env.getProperty("nx.logger.store-type");
        this.logStoreType = LogStorageType.valueOf(type);


        Namespace = SpringUtils.Env.getProperty(groupName+".amqp.namespace");
        this.groupName = SpringUtils.Env.getProperty(groupName+".amqp.groupName");
        this.logHandler = new LogHandler();
        logHandler.setEnabled(SpringUtils.Env.getBoolean(groupName+".amqp.log-handler.enabled",false));
        logHandler.setThreads(SpringUtils.Env.getInt(groupName+".amqp.log-handler.threads", 2));
        logHandler.setQueueSize(SpringUtils.Env.getInt(groupName+".amqp.log-handler.queueSize", 1000));
        String[] topics = SpringUtils.Env.getProperty(groupName+".amqp.log-handler.ignore-topics").split(",|;");
        logHandler.ignoreTopics = Arrays.asList(topics);
        mqGroup = SpringUtils.Env.getProperty(groupName+".amqp.group");


        this.producer = new Producer();
        producer.setEnabled(Boolean.parseBoolean(SpringUtils.Env.getProperty(groupName+".amqp.producer.enabled", "true")));




        consumer = new Consumer();
        consumer.fetchBatchSize =  SpringUtils.Env.getInt(groupName+".amqp.consumer.fetch.batchSize", 1);
        consumer.setEnabled(Boolean.parseBoolean(SpringUtils.Env.getProperty(groupName+".amqp.consumer.enabled", "true")));
        consumer.async = Boolean.parseBoolean(SpringUtils.Env.getProperty(groupName+".amqp.consumer.async.enabled", "true"));
        consumer.maxInterval = SpringUtils.Env.getLong(groupName+".amqp.consumer.maxInterval.ms",24 * 3600 * 1000);
        consumer.processThreads = SpringUtils.Env.getInt(groupName+".amqp.consumer.processThreads", 50);
        consumer.maxRetryTimes = SpringUtils.Env.getInt(groupName+".amqp.consumer.maxRetryTimes",10);
        consumer.queueName = ResourceUtils.getAndValidateProperty(groupName+".amqp.consumer.queueName");
        fetchTimeout = Duration.ofMillis(SpringUtils.Env.getLong(groupName+".amqp.fetch.timeout.ms",100));


        switch (provider){
            case aliyun_mns:
                aliyumMnsProps = new AliyumMnsProp(groupName);
                break;
            case kafka:
                kafka = new Kafka(groupName);
            case rocketmq:
                rocketMQ = new RocketMQ(groupName);
        }
    }






    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LogHandler {
        private boolean enabled=false;
        private int threads=2;
        private int queueSize=3;
        private List<String> ignoreTopics = new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Producer{
        private boolean enabled;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Consumer{
        private boolean enabled=false;
        private boolean async;
        private int processThreads;
        private long maxInterval;
        private int maxRetryTimes;
        private String queueName;
        private int fetchBatchSize;
    }

    @Data
    public static class Kafka {
        private String groupName;

        public Kafka(String groupName) {
            this.groupName = groupName;
        }

        public String getPropValue(String propName) {
            String propValue = SpringUtils.Env.getProperty(groupName + ".amqp.kafka." + propName);
            return propValue;
        }
    }



    @Data
    public static class RocketMQ{
        private String groupName;
        private String namesrvAddr;

        public RocketMQ(String groupName){
            this.groupName = groupName;

            String namesrvAddr = SpringUtils.Env.getProperty(groupName+".amqp.rocketmq.namesrv-addr");
            if (StringUtils.isEmpty(namesrvAddr)){
                throw new RuntimeException("namesrvAddr is null!");
            }

            this.namesrvAddr = namesrvAddr;
        }

    }

    @Data
    public static class AliyumMnsProp  {
        private String accessKeyId;
        private String  accessKeySecret;
        private String endpoint;

        public AliyumMnsProp(String groupName) {

            accessKeyId = SpringUtils.Env.getProperty(groupName + ".amqp.aliyun.mns.accessKeyId");
            accessKeySecret = ResourceUtils.getAndValidateProperty(groupName+".amqp.aliyun.mns.accessKeySecret");
            endpoint = ResourceUtils.getAndValidateProperty(groupName+".amqp.aliyun.mns.endpoint");
        }

        public CloudAccount getCloudAccount(){
            CloudAccount cloudAccount = new CloudAccount(accessKeyId, accessKeySecret, endpoint);
            if (cloudAccount==null){
                throw new RuntimeException("cloudAccount is null!");
            }
            return cloudAccount;
        }


    }



}
