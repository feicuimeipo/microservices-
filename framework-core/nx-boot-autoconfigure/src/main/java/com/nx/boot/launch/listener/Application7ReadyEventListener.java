package com.nx.boot.launch.listener;

import com.nx.boot.launch.spi.NxApplicationListener;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


/**
 * 服务启动成功的监听器
 */
@Component
@Slf4j
@ConditionalOnMissingBean(Application7ReadyEventListener.class)
public class Application7ReadyEventListener implements  ApplicationListener<ApplicationReadyEvent> {

	public static String WEB_SERVER_NAME;
	//初始化开始时间
	public static long START_TIME=  System.currentTimeMillis();


	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {

		ApplicationEventManager.heartbeat(ApplicationEventManager.HeartBeatType.readySuccess);
		Environment environment = event.getApplicationContext().getEnvironment();
		SpringUtils.setContext(event.getApplicationContext());

		String appName = environment.getProperty("spring.application.name");
		String profile = environment.getProperty("spring.profiles.active");
		String port    = environment.getProperty("server.port");
		String webserverName =  WEB_SERVER_NAME;
		String localIP = ApplicationEventManager.IpUtil.getHostIp();
		long time = (System.currentTimeMillis() - START_TIME)/1000;

		String info = String.format("启动完成--应用名：" + "[%s] ,环境:[%s], ip:port [%s:%s], 容器 [%s], 总耗时 [%s] 秒 ", appName, profile,localIP,port,webserverName,time);
		BannerUtils.push(0,this.getClass(),  info);
		BannerUtils.push(0,this.getClass(),  "");

		BannerUtils.printEnabled();
		
		List<NxApplicationListener> listener = new ArrayList<>();
		ServiceLoader.load(NxApplicationListener.class).forEach(listener::add);
		listener.stream().sorted(Comparator.comparing(NxApplicationListener::getOrder)).collect(Collectors.toList())
				.forEach(launcherService -> launcherService.onApplicationEvent(event));

	}



}
