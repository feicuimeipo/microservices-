/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.redis.utils;

import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * 等待线程容器
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月18日
 */
public class AwaitThreadContainer {
    private final Map<String, Set<Thread>> threadMap = new ConcurrentHashMap<>();

    /**
     * 线程等待,最大等待100毫秒
     * @param key 缓存Key
     * @param milliseconds 等待时间
     * @throws InterruptedException {@link InterruptedException}
     */
    public final void await(String key, long milliseconds) throws InterruptedException {
        // 测试当前线程是否已经被中断
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Set<Thread> threadSet = threadMap.get(key);
        // 判断线程容器是否是null，如果是就新创建一个
        if (threadSet == null) {
            threadSet = new ConcurrentSkipListSet<>(Comparator.comparing(Thread::toString));
            threadMap.put(key, threadSet);
        }
        // 将线程放到容器
        threadSet.add(Thread.currentThread());
        // 阻塞一定的时间
        LockSupport.parkNanos(this, TimeUnit.MILLISECONDS.toNanos(milliseconds));
    }

    /**
     * 线程唤醒
     * @param key key
     */
    public final void signalAll(String key) {
        Set<Thread> threadSet = threadMap.get(key);
        // 判断key所对应的等待线程容器是否是null
        if (!CollectionUtils.isEmpty(threadSet)) {
            for (Thread thread : threadSet) {
                LockSupport.unpark(thread);
            }
            // 清空等待线程容器
            threadSet.clear();
        }
    }
}
