package com.nx.dubbo.spimpl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

/**
 *
 */
@Slf4j
@Component
public class NxDubboStartConfig {

    protected static AnnotationConfigApplicationContext annotationConfigApplicationContext;

    @Autowired(required = false)
    private static ApplicationContext applicationContext;


    public NxDubboStartConfig() {

    }

    public static <T> T getComponentBean(Class<T> beanClz) {
        return annotationConfigApplicationContext.getBean(beanClz);
    }

    public static void start() {
        start(null);
    }

    public static void start(Class mainClass) {

        if (annotationConfigApplicationContext == null) {
            synchronized (NxDubboStartConfig.class) {
                if (annotationConfigApplicationContext == null) {

                    try{
                        Class clz = Class.forName("com.nx.boot.launch.NxSpringBootApplicationBuilder");
                        java.lang.reflect.Method method = clz.getMethod("getMainClass",null);
                        mainClass = (Class) method.invoke(null,null);
                    } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |InvocationTargetException e) {

                    }

                    if (mainClass == null) {
                        annotationConfigApplicationContext = new AnnotationConfigApplicationContext(NxDubboStartConfig.class, DubboConfiguration.class);
                    } else {
                        YamlPropertySourceFactory.setClassLoader(mainClass.getClassLoader());
                        annotationConfigApplicationContext = new AnnotationConfigApplicationContext(mainClass, NxDubboStartConfig.class, DubboConfiguration.class);
                    }
                    annotationConfigApplicationContext.start();

                    printInfo("Dubbo is ready! ");

                    try {
                        new CountDownLatch(33).await();
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void printInfo(String... msg){
        //int length=100;
        log.info("");
        log.info("********************************************************************************");
        for (String s : msg) {
            log.info("*"+s);
        }
        log.info("********************************************************************************");
        log.info("");
    }


    @Configuration
    @PropertySource(value = YamlPropertySourceFactory.DUBBO_CONFIG_FILE, factory = YamlPropertySourceFactory.class)
    class DubboConfiguration {

    }
}
