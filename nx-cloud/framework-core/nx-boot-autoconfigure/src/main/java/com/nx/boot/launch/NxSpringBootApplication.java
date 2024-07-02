package com.nx.boot.launch;


import com.nx.boot.launch.env.NxEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationContextFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import java.util.Collection;
import java.util.Set;


/**
 * @ClassName SpringApplicationConstant
 * @Author NIANXIAOLING
 * @Date 2022/6/19 21:46
 * @Version 1.0
 **/
@Slf4j
public class NxSpringBootApplication<T> {

    private SpringApplication springApplication;
    private Class<?>  mainApplicationClass;
    private NxEnvironment bootstrap ;

    public SpringApplication getSpringApplication(){
        return springApplication;
    }

    public NxSpringBootApplication(Class<T> mainApplicationClass,String... args){
        this.mainApplicationClass = mainApplicationClass;
        springApplication = new SpringApplication();
        springApplication.setMainApplicationClass(mainApplicationClass);
     }

    public ConfigurableApplicationContext run(String... args){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        NxSpringBootApplicationBuilder.initBootstrap(mainApplicationClass,args);
        bootstrap = NxSpringBootApplicationBuilder.getNxEnvironment();

        NxSpringBootApplicationBuilder.getInstance().createSpringApplicationBuilder(
                NxSpringBootApplicationBuilder.getInstance().getAppName(),
                NxSpringBootApplicationBuilder.getInstance().getMainAppClass(),
                NxSpringBootApplicationBuilder.getInstance().getNxEnvironment(),springApplication);

        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);

        stopWatch.stop();
        log.info("应用加载总用时: " + stopWatch.formatTime());
        return configurableApplicationContext;
    }


    public void addPrimarySources(Collection<Class<?>> additionalPrimarySources) {
        springApplication.addPrimarySources(additionalPrimarySources);
    }

    public Set<String> getSources() {
        return springApplication.getSources();
    }


    public Set<ApplicationContextInitializer<?>> getInitializers() {
        return springApplication.getInitializers();
    }


    public void setWebApplicationType(WebApplicationType webApplicationType) {
        Assert.notNull(webApplicationType, "WebApplicationType must not be null");
        springApplication.setWebApplicationType(webApplicationType);
    }

    public void setApplicationContextFactory(ApplicationContextFactory applicationContextFactory) {
        springApplication.setApplicationContextFactory(applicationContextFactory);
    }

    public void setInitializers(Collection<? extends ApplicationContextInitializer<?>> initializers) {
       springApplication.setInitializers(initializers);
    }

    public void setEnvironment(ConfigurableEnvironment environment) {
        springApplication.setEnvironment(environment);
    }


    public void addListeners(ApplicationListener<?>... listeners) {
            springApplication.addListeners(listeners);
    }

    public ResourceLoader getResourceLoader() {
        return springApplication.getResourceLoader();
    }



}
