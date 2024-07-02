package com.nx.amqp.adapter.provider.aliyum.mns;

import java.util.List;


public interface MNSQueueProcessHanlder {

    void process(String topicName,String dataJson);

    List<String> topicNames();
}