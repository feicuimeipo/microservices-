package com.nx.boot.i18n;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Component;

@Component
public class MessageSourceBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AbstractResourceBasedMessageSource) {
            AbstractResourceBasedMessageSource ms = (AbstractResourceBasedMessageSource) bean;
            ms.addBasenames("classpath:/i18n/messages");
        }
        return bean;
    }
}
