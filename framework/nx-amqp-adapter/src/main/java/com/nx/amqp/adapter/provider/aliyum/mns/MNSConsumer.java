package com.nx.amqp.adapter.provider.aliyum.mns;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.model.Message;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQProviderConfigFactory;
import com.nx.amqp.adapter.enums.MQProviderEnum;
import com.nx.amqp.adapter.utils.async.StandardThreadExecutor;
import com.nx.amqp.adapter.utils.async.StandardThreadFactory;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import static com.nx.amqp.adapter.MQProviderConfigFactory.DEFAULT_GROUP_NAME;



public class MNSConsumer implements InitializingBean,DisposableBean,PriorityOrdered{

    private static Logger logger = LoggerFactory.getLogger(MNSConsumer.class);
    private Map<String, MNSQueueProcessHanlder> queueHanlders = new HashMap<>();
    private StandardThreadExecutor fetchExecutor;
    private StandardThreadExecutor defaultProcessExecutor;
    private AtomicBoolean closed = new AtomicBoolean(false);
    private String queueName;
    private Semaphore semaphore;

    public MNSConsumer() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    private void start(){
        MQProviderConfig mnsmMQProperties =  MQProviderConfigFactory.getConfig(MQProviderEnum.aliyun_mns,DEFAULT_GROUP_NAME);
        this.queueName = mnsmMQProperties.getConsumer().getQueueName();

        CloudQueue queue = MNSClientInstance.createQueueIfAbsent(queueName);
        initTopicHanlders();
        fetchExecutor = new StandardThreadExecutor(1, 1,0, TimeUnit.SECONDS, 1,new StandardThreadFactory("mns-Fetch-Executor"));
        int maxThread = mnsmMQProperties.getConsumer().getProcessThreads();

        semaphore = new Semaphore(maxThread);
        defaultProcessExecutor = new StandardThreadExecutor(1, maxThread,60, TimeUnit.SECONDS, 1,new StandardThreadFactory("mns-defaultProcess-Executor"));
        fetchExecutor.submit(new Worker(queue));



        logger.info("start work for queue Ok -> queue:{}",queue.getQueueURL());
    }

    private void initTopicHanlders(){
        Map<String, MNSQueueProcessHanlder> interfaces = SpringUtils.getBeansOfType(MNSQueueProcessHanlder.class);
        if(interfaces == null || interfaces.isEmpty())return;
        for (MNSQueueProcessHanlder hanlder : interfaces.values()) {
            for (String topicName : hanlder.topicNames()) {
                if(queueHanlders.containsKey(topicName)){
                    throw new RuntimeException("ProcessHanlder for topicName ["+topicName+"] existed");
                }

                MNSClientInstance.createTopicIfAbsent(topicName, queueName);
                queueHanlders.put(topicName, hanlder);
                logger.info("registered MNSHanlder Ok -> queue:{},topic:{},hander:{}",queueName,topicName,hanlder.getClass().getName());
            }
        }
        if(queueHanlders.isEmpty())throw new RuntimeException("not any MNS TopicHanlder found");
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void destroy() throws Exception {
        closed.set(true);
        fetchExecutor.shutdown();
        defaultProcessExecutor.shutdown();
    }

    private class Worker implements Runnable{

        CloudQueue queue;
        public Worker(CloudQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while(!closed.get()){
                try {
                    Message message = queue.popMessage(5);
                    if(message != null){
                        String messageBody = message.getMessageBodyAsRawString();
                        JSONObject json = JSON.parseObject(messageBody);
                        final String topicName = json.getString("topic");
                        final String bodyString = json.getString("body");
                        MNSQueueProcessHanlder hanlder = queueHanlders.get(topicName);
                        if(hanlder == null)continue;
                        //信号量获取通行证
                        semaphore.acquire();

                        defaultProcessExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    logger.debug("processs_topic begin -> topicName:{},messageId:{}",topicName,message.getMessageId());
                                    hanlder.process(topicName,bodyString);
                                    queue.deleteMessage(message.getReceiptHandle());
                                    logger.debug("processs_topic end -> topicName:{},messageId:{},DequeueCount:{}",topicName,message.getMessageId(),message.getDequeueCount());
                                } finally {
                                    //释放信号量
                                    semaphore.release();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    logger.error("aliyum mns_unknow_error",e);
                }

            }
        }

    }


}