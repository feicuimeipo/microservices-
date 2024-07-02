package com.nx.prometheus.health;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
/**
 * <b>@Title: ActiveProfilesHealthIndicator;<T> </b>
 * <p>@Description: {@link org.springframework.boot.actuate.info.InfoContributor} </p>
 * <p>1. sping boot health 可以通过暴露的接口来提供系统及其系统组件是否可用 </p>
 * <p>2. 实现 HealthIndicators 继承 AbstractHealthIndicator </p>
 * <p>Module ID: framework.core 包<p>
 * <p>Comments:<p>
 * <p>JDK version used:  JDK1.8;
 * <p>Namespace:<p>
 * <p>@author：      admin
 * <p>@ProjectName cloud-native-framework</p>
 * <p>@date： 2020-04-23</p>
 * <p>Modified By：
 * <p>Modified Date:
 * <p>Why & What is modified
 * <p>@version:  coreplatform1.0;
 * <p>CopyRright (c)2017-cloud:
 * <p> @mark: Talk is cheap , show me the code , change the world </p>
 * http://localhost:8013/actuator/health
 */
@Component
@Log4j2
public class ActiveProfilesHealthIndicator implements HealthIndicator {


    /**
     * <p>定义构造函数</p>
     * @param environment a {@link ConfigurableEnvironment} object.
     */
    public ActiveProfilesHealthIndicator(ConfigurableEnvironment environment) {

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
