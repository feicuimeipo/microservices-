package com.nx.boot.launch.spimpl;

import com.google.auto.service.AutoService;
import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @ClassName BootProcessorListenerImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:14
 * @Version 1.0
 **/
@Slf4j
@AutoService(NxBootProcessor.class)
public class ProfileIncludeNxBootProcessorImpl implements NxBootProcessor {

    @Override
    public void launcher(String applicationName, NxBootstrap bootstrap, Properties props, Class mainClass) {

        /**
         * 组件
         */
        List<String> includes = new ArrayList<>();
        if ( StringUtils.hasLength(bootstrap.getProfileIncludes())){
            java.lang.String[] array =  bootstrap.getProfileIncludes().split(",");
            includes.addAll(Arrays.asList(array));
        }

        configIncludeProperties(props);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static Properties configIncludeProperties(Properties props){
        /**
         * 组件
         */
        List<String> includes = new ArrayList<>();
        try {
            Class.forName("com.nx.boot.NxBootAutoConfiguration");
            includes.add("boot");
        } catch (ClassNotFoundException e) {
            includes.add("autoconfigure");
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }

        try {
            Class.forName("com.nx.logging.actionlogger.ActionLoggerUtil");
            includes.add("logging");
        } catch (ClassNotFoundException e) {
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }

        try {
            Class.forName("com.nx.resilience4j.Resilience4jConfiguration");
            includes.add("resilience4j");
        } catch (ClassNotFoundException e) {
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }

        try {
            Class.forName("com.nx.prometheus.PrometheusConfiguration");
            includes.add("monitor");
        } catch (ClassNotFoundException e) {
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }


        //db的驱动选择-begin
        try {
            Class.forName("com.nx.cloud.NxCloudAutoConfiguration");
            includes.add("cloud");
        } catch (ClassNotFoundException e) {
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }

        try {
            Class.forName("com.nx.datasource.config.NxDataSourceAutoConfiguration");
            includes.add("mybatis");
        } catch (ClassNotFoundException e) {
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }


        //db的驱动选择-end

        try {
            Class.forName("com.nx.dubbo.SpringDubboBootstrap");
            includes.add("dubbo");
        } catch (ClassNotFoundException e) {
            log.warn( e.getClass().getName() + ":" + e.getMessage());
        }



        String includeString = StringUtils.arrayToDelimitedString(includes.toArray(new String[]{}),",");
        if (includeString.startsWith(",")){
            includeString = includeString.substring(1);
        }
        if (props.containsKey("spring.profiles.include")){
            String origin = props.getProperty("spring.profiles.include");
            props.setProperty("spring.profiles.include", origin.endsWith(",") ? origin + includeString:origin  + "," + includeString);
        }else{
            props.setProperty("spring.profiles.include",includeString);
        }
        return props;
    }
}
