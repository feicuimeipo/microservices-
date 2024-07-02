package com.nx.logger.task.core;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class GeneralScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {

    public GeneralScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize,
                new BasicThreadFactory.Builder().namingPattern("scheduled-pool-%d").daemon(true).build()
        );
    }

    public GeneralScheduledThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize,threadFactory);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
        if (runnable instanceof GeneralTask) {
            ((GeneralTask) runnable).scheduledFuture = task;
        }
        return task;
    }
}
