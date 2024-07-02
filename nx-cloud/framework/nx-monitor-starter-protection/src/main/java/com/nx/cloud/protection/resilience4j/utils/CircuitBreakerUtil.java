package com.nx.cloud.protection.resilience4j.utils;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;



@Slf4j
public class CircuitBreakerUtil {

    /**
     * @Description: 获取熔断器的状态
     */
    public static void getCircuitBreakerStatus(String time, CircuitBreaker circuitBreaker) {
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        // Returns the failure rate in percentage.
        float failureRate = metrics.getFailureRate();
        // Returns the current number of buffered calls.
        int bufferedCalls = metrics.getNumberOfBufferedCalls();
        // Returns the current number of failed calls.
        int failedCalls = metrics.getNumberOfFailedCalls();
        // Returns the current number of successed calls.
        int successCalls = metrics.getNumberOfSuccessfulCalls();
        // Returns the current number of not permitted calls.
        long notPermittedCalls = metrics.getNumberOfNotPermittedCalls();
        int slowCalls = metrics.getNumberOfSlowCalls();
        int slowFailedCalls = metrics.getNumberOfSlowFailedCalls();
        int slowSuccessFulCall = metrics.getNumberOfSlowSuccessfulCalls();
        float slowCallRate = metrics.getSlowCallRate();

        log.info(time +
                "state=" + circuitBreaker.getState() + " , metrics[ failureRate=" + failureRate +
                ", bufferedCalls=" + bufferedCalls +
                ", failedCalls=" + failedCalls +
                ", successCalls=" + successCalls +
                ", slowCalls=" + slowCalls +
                ", slowFailedCalls=" + slowFailedCalls +
                ", slowSuccessFulCall=" + slowSuccessFulCall +
                ", slowCallRate=" + slowCallRate +
                ", notPermittedCalls=" + notPermittedCalls +
                " ]"
        );
    }

    /**
     * @Description: 监听熔断器事件
     */
    public static void addCircuitBreakerListener(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> log.info("服务调用成功：" + event.toString()))
                .onError(event -> log.info("服务调用失败：" + event.toString()))
                .onIgnoredError(event -> log.info("服务调用失败，但异常被忽略：" + event.toString()))
                .onReset(event -> log.info("熔断器重置：" + event.toString()))
                .onStateTransition(event -> log.info("熔断器状态改变：" + event.toString()))
                .onCallNotPermitted(event -> log.info(" 熔断器不许可：" + event.toString()))
                .onFailureRateExceeded(event -> log.info("熔断器超时：" + event.toString()))
                .onSlowCallRateExceeded(event -> log.info("熔断器慢查率：" + event.toString()))
        ;
    }
}