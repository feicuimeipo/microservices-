/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.conf;

import javax.annotation.Resource;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * 
 * @author jason
 *
 */
@Configuration
public class ValidatorConfig {

	/**
     * MessageSource
     */
    @Resource
    private MessageSource messageSource;

    /**
     * Validation message i18n
     * @return Validator
     */
    @NotNull
    @Bean
    public Validator getValidator() {
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(this.messageSource);
        return localValidatorFactoryBean;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor processor =
                new MethodValidationPostProcessor();
        processor.setValidator(getValidator());
        return new MethodValidationPostProcessor();
    }

}
