package com.nx.boot.launch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Slf4j
@ConditionalOnMissingBean(Application4SmartInitializingSingleton.class)
public class Application4SmartInitializingSingleton implements SmartInitializingSingleton {

	@Override
	public void afterSingletonsInstantiated() {
	}
}
