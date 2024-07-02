package com.nx.logger.task.core;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

public abstract class GeneralTask implements Runnable {
    volatile ScheduledFuture<?> scheduledFuture = null;
    public GeneralTask() { }

    /**
     * 执行
     */
    public abstract void doRun();

    /**
     * 取消定时任务
     */
    public void cancel(boolean mayInterruptIfRunning) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(mayInterruptIfRunning);
        }
    }

    /**
     * 获取定时任务名称
     */
    public String getTaskName() {
        return this.getClass().getName();
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        doRun();
        long endTime = System.currentTimeMillis();
        System.out.printf("currentTime: [%s] ,task: [%s] ,cost: [%s] millis%n", new Date(), getTaskName(), (endTime - startTime));
    }
}