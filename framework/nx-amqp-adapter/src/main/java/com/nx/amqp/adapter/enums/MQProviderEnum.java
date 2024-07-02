package com.nx.amqp.adapter.enums;

import lombok.Getter;

public enum MQProviderEnum {
    kafka("kafka"),
    memoryqueue("memoryqueue"),
    redis("redis"),
    rocketmq("rocketmq"),
    rabbitmq("rabbitmq"),
    aliyun_mns("aliyun-mns"),
    aliyun_ons("aliyun-ons"),
    qcloud_cmq("qcloud-cmq"),
    eventbus("eventbus"),
    ;

    @Getter
    private String code;
     MQProviderEnum(String code) {
        this.code = code;
    }

    public static MQProviderEnum valueByCode(String code){
        for (MQProviderEnum value : MQProviderEnum.values()) {
            if (value.code.equalsIgnoreCase(code)){
                return value;
            }
        }
        return null;
    }
}
