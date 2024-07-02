package com.nx.amqp.adapter.utils.async;


import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;

class ExecutorQueue extends LinkedTransferQueue<Runnable> {
    private static final long serialVersionUID = -265236426751004839L;
    StandardThreadExecutor threadPoolExecutor;

    public ExecutorQueue() {
        super();
    }

    public void setStandardThreadExecutor(StandardThreadExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public boolean force(Runnable o) {
        if (threadPoolExecutor.isShutdown()) {
            throw new RejectedExecutionException("Executor not running, can't force a command into the queue");
        }
        return super.offer(o);
    }

    public boolean offer(Runnable o) {
        int poolSize = threadPoolExecutor.getPoolSize();

        if (poolSize == threadPoolExecutor.getMaximumPoolSize()) {
            return super.offer(o);
        }
        if (threadPoolExecutor.getSubmittedTasksCount() <= poolSize) {
            return super.offer(o);
        }

        if (poolSize < threadPoolExecutor.getMaximumPoolSize()) {
            return false;
        }
        return super.offer(o);
    }
}