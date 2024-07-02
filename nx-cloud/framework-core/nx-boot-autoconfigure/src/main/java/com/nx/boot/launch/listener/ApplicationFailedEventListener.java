package com.nx.boot.launch.listener;

import com.nx.boot.launch.NxLaunchTools;
import com.nx.boot.launch.spi.NxApplicationListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Component
@Slf4j
@ConditionalOnMissingBean(ApplicationFailedEventListener.class)
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent>{


	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		NxLaunchTools.printInfo(this.getClass(),"应用启动失败！！！");


		ApplicationEventManager.heartbeat(ApplicationEventManager.HeartBeatType.readyFail);

		List<NxApplicationListener> list = new ArrayList<>();

		ServiceLoader.load(NxApplicationListener.class).forEach(list::add);

		list.stream().sorted(Comparator.comparing(NxApplicationListener::getOrder))
				.collect(Collectors.toList())
				.forEach(item -> {
					item.onApplicationEvent(event);
				});

	}
}
