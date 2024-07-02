package com.nx.skywalking.juc;


import com.alibaba.ttl.TtlCallable;
import com.alibaba.ttl.TtlRunnable;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EnhanceThreadPoolExecutor extends ThreadPoolExecutor {
    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public EnhanceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public void execute(Runnable command) {
        TtlRunnable ttlRunnable = TtlRunnable.get(command);
        super.execute((Runnable)ttlRunnable);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        TtlRunnable ttlRunnable = TtlRunnable.get(task);
        return super.submit((Runnable)ttlRunnable, result);
    }

    public Future<?> submit(Runnable task) {
        TtlRunnable ttlRunnable = TtlRunnable.get(task);
        return super.submit((Runnable)ttlRunnable);
    }

    public <T> Future<T> submit(Callable<T> task) {
        TtlCallable ttlCallable = TtlCallable.get(task);
        return super.submit((Callable<T>)ttlCallable);
    }

    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        TtlRunnable ttlRunnable = TtlRunnable.get(runnable);
        return super.newTaskFor((Runnable)ttlRunnable, value);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        TtlCallable ttlCallable = TtlCallable.get(callable);
        return super.newTaskFor((Callable<T>)ttlCallable);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        Collection<? extends Callable<T>> callables = TtlCallable.gets(tasks);
        return super.invokeAny(callables);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        Collection<? extends Callable<T>> callables = TtlCallable.gets(tasks);
        return super.invokeAny(callables, timeout, unit);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        Collection<? extends Callable<T>> callables = TtlCallable.gets(tasks);
        return super.invokeAll(callables);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        Collection<? extends Callable<T>> callables = TtlCallable.gets(tasks);
        return super.invokeAll(callables, timeout, unit);
    }
}