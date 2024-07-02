package com.nx.skywalking.juc;

import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class EnhanceThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    public void execute(Runnable command) {
        TtlRunnable ttlRunnable = TtlRunnable.get(command);
        super.execute((Runnable)ttlRunnable);
    }

    public <T> Future<T> submit(Callable<T> task) {
        TtlCallable ttlCallable = TtlCallable.get(task);
        return super.submit((Callable)ttlCallable);
    }

    public Future<?> submit(Runnable task) {
        TtlRunnable ttlRunnable = TtlRunnable.get(task);
        return super.submit((Runnable)ttlRunnable);
    }

    public ListenableFuture<?> submitListenable(Runnable task) {
        TtlRunnable ttlRunnable = TtlRunnable.get(task);
        return super.submitListenable((Runnable)ttlRunnable);
    }

    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        TtlCallable ttlCallable = TtlCallable.get(task);
        return super.submitListenable((Callable)ttlCallable);
    }
}
