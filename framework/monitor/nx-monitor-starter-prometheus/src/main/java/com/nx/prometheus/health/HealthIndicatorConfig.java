package com.nx.prometheus.health;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @see {@link org.springframework.boot.actuate.info.InfoContributor}
 *
 * */
@Configuration
@ConditionalOnClass(HealthContributor.class)
@Log4j2
public class HealthIndicatorConfig {

    /**
     * <p>activeProfilesInfoContributor.</p>
     *
     * @param environment a {@link ConfigurableEnvironment} object.
     * @return a {@link ...} object.
     */
    @Bean
    @ConditionalOnEnabledInfoContributor("active-profiles")
    public ActiveProfilesHealthIndicator activeProfilesHealthIndicator(ConfigurableEnvironment environment) {
        log.info("初始化自定义平台 actuate-health 信息!");
        return new ActiveProfilesHealthIndicator(environment);
    }
}
