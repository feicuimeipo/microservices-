package com.nx.boot.launch;


import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.env.NxEnvironment;
import com.nx.boot.launch.spi.NxBootProcessor;
import com.nx.common.banner.BannerUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.util.StringUtils;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Getter
public class NxSpringBootApplicationBuilder {
    private static NxSpringBootApplicationBuilder nxSpringBootApplicationBuilder;
    private ConfigurableApplicationContext applicationContext;
    private Class<?> mainAppClass;
    private String[] cmdLineArg;
    private NxEnvironment nxEnvironment;

    public static NxEnvironment getNxEnvironment(){
        if (nxSpringBootApplicationBuilder==null){
             initBootstrap(nxSpringBootApplicationBuilder.getMainAppClass(),nxSpringBootApplicationBuilder.cmdLineArg);
        }
        return nxSpringBootApplicationBuilder.nxEnvironment;
    }


    protected static void initBootstrap(Class<?> mainAppClass,String[] cmdLineArg){
        if (nxSpringBootApplicationBuilder!=null) return;
        synchronized (NxSpringBootApplicationBuilder.class){
            if (nxSpringBootApplicationBuilder==null){
                nxSpringBootApplicationBuilder = SingletonApplicationBuilder.builder;
                nxSpringBootApplicationBuilder.mainAppClass = mainAppClass;
                nxSpringBootApplicationBuilder.cmdLineArg = cmdLineArg;
                nxSpringBootApplicationBuilder.nxEnvironment = new NxEnvironment.Builder(mainAppClass,cmdLineArg).build();
                nxSpringBootApplicationBuilder.appName  = nxSpringBootApplicationBuilder.nxEnvironment.getApplicationName();
            }
        }
    }

    public static NxSpringBootApplicationBuilder getInstance(){
        return nxSpringBootApplicationBuilder;
    }

    public static ConfigurableApplicationContext getApplicationContext(){
        return nxSpringBootApplicationBuilder.applicationContext;
    }

    public static Class getMainClass(){
        return nxSpringBootApplicationBuilder.mainAppClass;
    }

    @Getter
    private String appName;

    private NxSpringBootApplicationBuilder(){
        super();
    };

    //方法同步，调用效率低
    private static class SingletonApplicationBuilder{
        private static final NxSpringBootApplicationBuilder builder=new NxSpringBootApplicationBuilder();
    }

    /**
     *
     * @param
     * @param args
     * @param <T>
     * @return
     * @throws RuntimeException
     */
    public static <T> ConfigurableApplicationContext run(Class<T> mainApplicationClass, String... args) throws RuntimeException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        initBootstrap(mainApplicationClass,args);

        if (nxSpringBootApplicationBuilder.applicationContext==null){
            synchronized (NxSpringBootApplicationBuilder.class){
                if (nxSpringBootApplicationBuilder.applicationContext==null) {
                    SpringApplicationBuilder springApplicationBuilder = nxSpringBootApplicationBuilder.createSpringApplicationBuilder(nxSpringBootApplicationBuilder.appName, mainApplicationClass, nxSpringBootApplicationBuilder.nxEnvironment, null);
                    nxSpringBootApplicationBuilder.applicationContext = springApplicationBuilder.run(args);
                }
            }
        }

        stopWatch.stop();
        log.info("应用加载总用时: " + stopWatch.formatTime());

        BannerUtils.push(NxSpringBootApplicationBuilder.class,"nx-boot-autoconfigure enabled!");
        return  nxSpringBootApplicationBuilder.applicationContext;
    }



    /**
     * --spring.profiles.active=test --nacos.addr=loclahost --sentinel.addr=localhost
     * 优先级高于内置的Commonstant参数
     * @param appName
     * @param source
     * @param bootstrap
     * @return
     */
    protected SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class source,
                                                                           NxEnvironment bootstrap,
                                                                           SpringApplication springApplication
        ) {

        ConfigurableEnvironment environment = new StandardEnvironment();
        //2.获取配置的环境变量
        List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        if (bootstrap.getProfile().length() > 0) {
            profiles = new ArrayList<>();
            profiles.add(bootstrap.getProfile());
        }

        //3.set
        environment.getPropertySources().addFirst(new SimpleCommandLinePropertySource(bootstrap.getCmdArgs()));
        environment.getPropertySources().addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
        environment.getPropertySources().addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));


        SpringApplicationBuilder applicationBuilder = null;
        if (springApplication==null) {
            applicationBuilder = new SpringApplicationBuilder(source);
        }
        String profile;

        // 3.当前使用
        List<String> activeProfileList = new ArrayList<>(profiles);
        if (profiles.isEmpty()) {
            // 默认dev开发
            profile = NxBootstrap.DEFAULT_PROFILE_CODE;
            activeProfileList.add(profile);
            if (springApplication==null) {
                applicationBuilder.profiles(profile);
            }else{
                springApplication.setAdditionalProfiles(profile);
            }
        } else if (activeProfileList.size() == 1) {
            profile = activeProfileList.get(0);
        } else {
            //同时存在dev、test、prod环境时
            throw new RuntimeException("同时存在环境变量:[" + StringUtils.arrayToCommaDelimitedString(activeProfileList.toArray(new String[]{})) + "]");
        }

        Properties props = buildEnvironmentContext(appName,profile,bootstrap);
        // 加载自定义组件
        List<NxBootProcessor> launcherList = new ArrayList<>();
        ServiceLoader.load(NxBootProcessor.class).forEach(launcherList::add);
        launcherList.stream().sorted(Comparator.comparing(NxBootProcessor::getOrder)).collect(Collectors.toList())
                .forEach(launcherService -> launcherService.launcher(appName, bootstrap, props,source));


        String activePros = props.getProperty("spring.profiles.active");

        NxLaunchTools.printInfo(this.getClass(),String.format("启动中，读取到的环境变量:[%s]", activePros));

        return applicationBuilder;
    }


    private Properties buildEnvironmentContext(String applicationName, String profile, NxEnvironment bootstrap){
        Properties props = System.getProperties();
        props.setProperty("spring.application.name", applicationName);
        props.setProperty("spring.profiles.active", profile);
        props.setProperty("application.name", applicationName);
        props.setProperty("server.port", bootstrap.getServerPort().toString());
        props.setProperty("spring.mvc.pathmatch.matching-strategy", "ant_path_matcher");
        props.setProperty("spring.mvc.throw-exception-if-no-handler-found", "true");
        props.setProperty("spring.main.allow-bean-definition-overriding", "true");

        //单例模式
        if (!bootstrap.isConfigEnabled()){
            props.setProperty("spring.cloud.nacos.config.enabled",  "false");
        }else{
            props.setProperty("spring.cloud.nacos.config.enabled",  "true");
            props.setProperty("spring.cloud.nacos.config.server-addr", bootstrap.getCloudServerAddr());
        }

        if (!bootstrap.isDiscoveryEnabled()){
            props.setProperty("spring.cloud.nacos.discovery.enabled", "false");
        }else{
            props.setProperty("spring.cloud.nacos.discovery.enabled",  "true");
            props.setProperty("spring.cloud.nacos.discovery.server-addr",bootstrap.getCloudServerAddr());
            props.setProperty("ribbon.nacos.enabled","true");
        }

        return props;
    }


}
