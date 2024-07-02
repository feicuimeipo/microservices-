package com.nx.logger.task.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时器管理类
 */
public class GeneralTimerMgr {

    // 注册异常停止定时器
    // Runtime.getRuntime().addShutdownHook(new Thread(GeneralTimerMgr::stop));

    private static ScheduledExecutorService timerExecutorService;

    private static Set<GeneralTask> tasks = new CopyOnWriteArraySet<>();

    /**
     * 初始化
     */
    public static synchronized void init() {
        // 初始化定时器线程池
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        corePoolSize = Math.min(8, Math.max(4, corePoolSize));
        timerExecutorService = new GeneralScheduledThreadPoolExecutor(corePoolSize);
        //initTask();
    }



    /**
     * 添加任务到定时器
     */
    public static void addTask(GeneralTask task, long initialDelay, long delay, TimeUnit unit) {
        if (!tasks.contains(task)){
            //BasicThreadFactory
            if (task != null) {
                // 循环任务，按照上一次任务的发起时间计算下一次任务的开始时间
                System.out.println("main thread time : " + formatDateToString(new Date()));
                // 循环任务，按照上一次任务的发起时间计算下一次任务的开始时间
                // 第一个参数为执行体，第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间。
                timerExecutorService.scheduleWithFixedDelay(task, initialDelay, delay, unit);
            }
            tasks.add(task);
        }
    }



    /**
     * 取消任务
     */
    private static void cancelTask(GeneralTask task) {
        if (task != null) {
            task.cancel(false);
        }
    }

    /**
     * 停止任务
     */
    public static void stop() {
        if (!tasks.isEmpty()){
            tasks.stream().forEach(task->{
                cancelTask(task);
            });
        }
    }


    public static String formatDateToString(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(time);
    }
}