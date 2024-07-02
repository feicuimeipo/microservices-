package com.nx.dubbo.spimpl;


import com.google.auto.service.AutoService;
import com.nx.boot.launch.spi.NxApplicationListener;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName BootProcessorListenerImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:14
 * @Version 1.0
 **/
@AutoService(NxApplicationListener.class)
public class NxDubboStart implements NxApplicationListener {
    private static Class mainSource;


    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {

    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Map<String, Object> annotatedBeans = event.getApplicationContext().getBeansWithAnnotation(SpringBootApplication.class);
        mainSource = annotatedBeans==null || annotatedBeans.size()==0 ? null : annotatedBeans.values().toArray()[0].getClass();

        Environment environment = event.getApplicationContext().getEnvironment();


        List<String> files = new ArrayList<>();
        files.add("application-dubbo.yml");//第1优先级


        YamlPropertySourceFactory.setClassLoader(mainSource.getClassLoader());
        YamlPropertySourceFactory.setResoruceFiles(files.toArray(new String[]{}));
        NxDubboStartConfig.start();
    }


    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {

    }
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


}
