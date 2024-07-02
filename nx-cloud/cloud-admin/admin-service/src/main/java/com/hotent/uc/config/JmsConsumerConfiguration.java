/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.uc.config;

import org.nianxi.jms.handler.JmsConsumer;
import com.hotent.base.jms.impl.DefaultApplyConsumerImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConsumerConfiguration {
    @Bean
    @ConditionalOnMissingBean(DefaultApplyConsumerImpl.class)
    JmsConsumer defaultApplyConsumerImpl(){
        return new DefaultApplyConsumerImpl();
    }
}
