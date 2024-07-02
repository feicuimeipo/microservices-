package com.nx.amqp.adapter.utils.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakinge</a>
 * @date 2016年7月20日
 */
public class DelayRetryExecutor {

    private static final Logger logger = LoggerFactory.getLogger(DelayRetryExecutor.class);

    // 重试时间间隔单元（毫秒）
    private long retryPeriodUnit;
    private int maxReties;
    private int queueCapacity;

    private final PriorityBlockingQueue<PriorityTask<?>> taskQueue = new PriorityBlockingQueue<PriorityTask<?>>(1000);

    private ExecutorService executor;

    private AtomicBoolean closed = new AtomicBoolean(false);

    public int getRetryTaskNums() {
        return taskQueue.size();
    }

    public DelayRetryExecutor(int poolSize,int queueCapacity, int retryPeriodUnitMs, int maxReties) {

        this.queueCapacity = queueCapacity;
        this.retryPeriodUnit = retryPeriodUnitMs;
        this.maxReties = maxReties;
        executor = Executors.newFixedThreadPool(poolSize, new StandardThreadFactory("DelayRetryExecutor"));
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (!closed.get()) {
                    try {
                        PriorityTask<?> task = taskQueue.take();
                        // 空任务跳出循环
                        if (task.getConsumer() == null)
                            break;
                        if (task.nextFireTime - System.currentTimeMillis() > 0) {
                            TimeUnit.MILLISECONDS.sleep(1000);
                            taskQueue.put(task);
                            continue;
                        }
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public <T> void submit(String traceId, ICaller<T> caller) {
        int taskCount;
        if ((taskCount = taskQueue.size()) > queueCapacity) {
            logger.warn("ZVOS-FRAMEWORK-TRACE-LOGGGING-->> Retry queue task count:{} over max queueCapacity:{}", taskCount,queueCapacity);
            return;
        }
        taskQueue.add(new PriorityTask<>(traceId,caller));
    }

    public void close() {
        closed.set(true);
        // taskQueue里面没有任务会一直阻塞，所以先add一个新任务保证执行
        taskQueue.add(new PriorityTask<>(null,null));
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }
        executor.shutdown();
        logger.info("ZVOS-FRAMEWORK-TRACE-LOGGGING-->> DelayRetryExecutor closed");
    }

    class PriorityTask<T> implements Runnable, Comparable<PriorityTask<T>> {

        String traceId;
        final ICaller<T> caller;

        int retryCount = 0;
        long nextFireTime;

        public PriorityTask(String traceId,ICaller<T> caller) {
            this(traceId,caller, System.currentTimeMillis() + retryPeriodUnit);
        }

        public PriorityTask(String traceId,ICaller<T> caller, long nextFireTime) {
            this.traceId = traceId;
            this.caller = caller;
            this.nextFireTime = nextFireTime;
        }

        public ICaller<T> getConsumer() {
            return caller;
        }

        @Override
        public void run() {
            try {
                logger.debug("ZVOS-FRAMEWORK-TRACE-LOGGGING-->> DelayRetry begin traceId:" + traceId);
                caller.call();
                logger.debug("ZVOS-FRAMEWORK-TRACE-LOGGGING-->> DelayRetry successed traceId:" + traceId);
            } catch (Exception e) {
                retryCount++;
                if (retryCount == maxReties) {
                    logger.error(String.format("ZVOS-FRAMEWORK-TRACE-LOGGGING-->> DelayRetry maxReties over %s,traceId:%s ", maxReties,traceId),e);
                    return;
                }
                nextFireTime = nextFireTime + retryCount * retryPeriodUnit;
                // 重新放入任务队列
                taskQueue.add(this);
                logger.debug("ZVOS-FRAMEWORK-TRACE-LOGGGING-->> DelayRetry error ,reAdd to queue traceId:{},retryCount:{}" + traceId,retryCount);
            }
        }

        @Override
        public int compareTo(PriorityTask<T> o) {
            return (int) (this.nextFireTime - o.nextFireTime);
        }

    }
}