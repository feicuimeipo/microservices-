package com.nx.prometheus.config.custom.aop;



import com.nx.prometheus.config.custom.annotation.Count;
import com.nx.prometheus.config.custom.annotation.Monitor;
import com.nx.prometheus.config.custom.annotation.TP;
import io.micrometer.core.instrument.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.function.Function;



/**
 * @ClassName MetricsAspect
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/21 18:53
 * @Version 1.0
 **/
@Aspect
@Component
public class CustomerMetricsAspect {

    /**
     * Prometheus指标管理
     */
    private MeterRegistry registry;

    private Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint;

    @Autowired
    public CustomerMetricsAspect(MeterRegistry registry) {
        this.init(registry, pjp -> Tags
                .of(new String[]{"class", pjp.getStaticPart().getSignature().getDeclaringTypeName(), "method",
                        pjp.getStaticPart().getSignature().getName()}));
    }

    public void init(MeterRegistry registry, Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint) {
        this.registry = registry;
        this.tagsBasedOnJoinPoint = tagsBasedOnJoinPoint;
    }

    /**
     * 针对@Tp指标配置注解的逻辑实现
     */
    @Around("@annotation(com.nx.prometheus.config.custom.annotation.TP)")
    public Object timedMethod(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        TP tp = method.getAnnotation(TP.class);
        Timer.Sample sample = Timer.start(this.registry);
        String exceptionClass = "none";
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                String finalExceptionClass = exceptionClass;
                //创建定义计数器，并设置指标的Tags信息（名称可以自定义）
                Timer timer =  Metrics.newTimer("tp.method.timed",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", tp.description())
                                .publishPercentileHistogram().register(this.registry));
                sample.stop(timer);
            } catch (Exception exception) {
            }
        }
    }

    /**
     * 针对@Count指标配置注解的逻辑实现
     */
    @Around("@annotation(com.nx.prometheus.config.custom.annotation.Count)")
    public Object countMethod(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        Count count = method.getAnnotation(Count.class);
        String exceptionClass = "none";
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                String finalExceptionClass = exceptionClass;
                //创建定义计数器，并设置指标的Tags信息（名称可以自定义）
                Counter counter = Metrics.newCounter("count.method.counted",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", count.description())
                                .register(this.registry));
                counter.increment();
            } catch (Exception exception) {
            }
        }
    }

    /**
     * 针对@Monitor通用指标配置注解的逻辑实现
     */
    @Around("@annotation(com.nx.prometheus.config.custom.annotation.Monitor)")
    public Object monitorMethod(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
        Monitor monitor = method.getAnnotation(Monitor.class);
        String exceptionClass = "none";
        try {
            return pjp.proceed();
        } catch (Exception ex) {
            exceptionClass = ex.getClass().getSimpleName();
            throw ex;
        } finally {
            try {
                String finalExceptionClass = exceptionClass;
                //计时器Metric
                Timer timer = Metrics.newTimer("tp.method.timed",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", monitor.description())
                                .publishPercentileHistogram().register(this.registry));
                Timer.Sample sample = Timer.start(this.registry);
                sample.stop(timer);

                //计数器Metric
                Counter counter = Metrics.newCounter("count.method.counted",
                        builder -> builder.tags(new String[]{"exception", finalExceptionClass})
                                .tags(this.tagsBasedOnJoinPoint.apply(pjp)).tag("description", monitor.description())
                                .register(this.registry));
                counter.increment();
            } catch (Exception exception) {
            }
        }
    }
}
