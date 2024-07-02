package com.nx.gateway.conf.context;


import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerAdapter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.HandlerResultHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ClassName WebFlexAppUtils
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/12 10:25
 * @Version 1.0
 **/
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean(ReactiveAppUtils.class)
@ConditionalOnClass(value={org.springframework.web.reactive.HandlerAdapter.class})
public class ReactiveAppUtils implements ApplicationContextAware {

    private List<HandlerMapping> handlerMappings;
    private ArrayList<HandlerResultHandler> resultHandlers;
    private ArrayList<HandlerAdapter> handlerAdapters;
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        //初始化策略
        context = applicationContext;
        initStrategies(applicationContext);
    }


    protected void initStrategies(ApplicationContext context) {
        Map<String, HandlerMapping> mappingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                context, HandlerMapping.class, true, false);

        ArrayList<HandlerMapping> mappings = new ArrayList<>(mappingBeans.values());
        AnnotationAwareOrderComparator.sort(mappings);

        this.handlerMappings = Collections.unmodifiableList(mappings);


        Map<String, HandlerAdapter> adapterBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                context, HandlerAdapter.class, true, false);

        this.handlerAdapters = new ArrayList<>(adapterBeans.values());
        AnnotationAwareOrderComparator.sort(this.handlerAdapters);


        Map<String, HandlerResultHandler> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(
                context, HandlerResultHandler.class, true, false);

        this.resultHandlers = new ArrayList<>(beans.values());
        AnnotationAwareOrderComparator.sort(this.resultHandlers);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}

