package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import com.nx.boot.launch.spi.NxApplicationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.embedded.jetty.JettyWebServer;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowWebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;


/**
 * 项目启动事件通知
 */
@Slf4j
@Async
@Configuration
@ConditionalOnMissingBean(Application5WebServerInitializedEventListener.class)
public class Application5WebServerInitializedEventListener implements ApplicationListener<WebServerInitializedEvent> {

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {

		String webserverName = "unknown";
		if (event.getWebServer() instanceof TomcatWebServer){
			webserverName = TomcatWebServer.class.getSimpleName();
		}else if(event.getWebServer() instanceof UndertowWebServer){
			webserverName = UndertowWebServer.class.getSimpleName();
		}else if(event.getWebServer() instanceof JettyWebServer){
			webserverName = JettyWebServer.class.getSimpleName();
		}else if(event.getWebServer() instanceof NettyWebServer){
			webserverName = NettyWebServer.class.getSimpleName();
		}

		ApplicationEventManager.heartbeat(ApplicationEventManager.HeartBeatType.readyFail);

		//NxLaunchTools.printInfo(String.format("应用容器:[%s]",webserverName));
		Application7ReadyEventListener.WEB_SERVER_NAME = webserverName;

		List<NxApplicationListener> listener = new ArrayList<>();
		ServiceLoader.load(NxApplicationListener.class).forEach(listener::add);
		listener.stream().sorted(Comparator.comparing(NxApplicationListener::getOrder))
				.collect(Collectors.toList())
				.forEach(launcherService -> launcherService.onApplicationEvent(event));
	}
}
