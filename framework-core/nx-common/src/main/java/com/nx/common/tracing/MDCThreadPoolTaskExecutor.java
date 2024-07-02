package com.nx.common.tracing;

import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @description 封装线程池任务执行器，在任务提交时，会将父线程的request_id，带入子线程
 * @author
 * @date 2022/3/3 17:35
 */
public class MDCThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {


    public MDCThreadPoolTaskExecutor() {
        super();
    }


    @Override
    public void execute(Runnable task) {
        super.execute(NxTraceUtil.wrap(task, MDC.getCopyOfContextMap()));
    }


    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(NxTraceUtil.wrap(task, MDC.getCopyOfContextMap()));
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(NxTraceUtil.wrap(task, MDC.getCopyOfContextMap()));
    }
}