package com.nx.cloud.monitor.prometheus.health;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;


@Component
@Log4j2
public class ActiveProfilesHealthIndicator implements HealthIndicator {

    public ActiveProfilesHealthIndicator() {
    }

    /**
     *
     * UNKNOWN:未知状态，映射HTTP状态码为503
     * UP：正常，映射HTTP状态码为200
     * DOWN：失败，映射HTTP状态码为503
     * OUT_OF_SERVICE:不能对外提供服务，但是服务正常。映射HTTP状态码为200
     * */
    @Override
    public Health health() {
        log.info("云平台自定义健康检查!");
        return Health.up().build();
    }



}
