package com.nx.amqp.adapter.utils;

import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.utils.async.StandardThreadFactory;
import com.nx.amqp.adapter.MQLogHandler;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.enums.ActionType;
import com.nx.amqp.logger.DefaultMQLogHandler;
import com.nx.common.context.SpringUtils;
import lombok.SneakyThrows;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MQContext {

    public static String rebuildWithNamespace(String name, MQProviderConfig config){
        if(config.getNamespacePrefix() == null)return name;
        if(name == null || name.startsWith(config.getNamespacePrefix())) return name;
        return config.getNamespacePrefix() + name;
    }


    private static ThreadPoolExecutor getLogHandleExecutor(MQProviderConfig config) {
        if(config.getLogHandleExecutor() != null)return config.getLogHandleExecutor();
        if(!config.getLogHandler().isEnabled()) return null;
        synchronized (config) {
            StandardThreadFactory threadFactory = new StandardThreadFactory("mqLogHandleExecutor");
            int nThread = config.getLogHandler().getThreads();
            int capacity = config.getLogHandler().getQueueSize();//  ResourceUtils.getInt("mendmix.amqp.loghandler.queueSize", 1000);
            ThreadPoolExecutor poolExecutor =  new ThreadPoolExecutor(nThread, nThread,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(capacity),
                    threadFactory);
            config.setLogHandleExecutor(poolExecutor);

            //忽略topic列表
            if(config.getLogHandler().getIgnoreTopics()!=null) {
                for (String topic : config.getLogHandler().getIgnoreTopics()) {
                    config.getLogHandler().getIgnoreTopics().add(rebuildWithNamespace(topic,config));
                }
            }
        }
        return config.getLogHandleExecutor();
    }

    public static void processMessageLog(MQProviderConfig config, MQMessage message, ActionType actionType, Throwable ex){
        if(!config.getLogHandler().isEnabled())return;

        ThreadPoolExecutor executor = getLogHandleExecutor(config);
        if(config.getLogHandler().getIgnoreTopics().contains(message.getTopic()))return;
        message.setProcessTime(System.currentTimeMillis());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(ex == null) {
                    getMQLogHandler(config).onSuccess(config.getGroupName(), actionType,message);
                }else {
                    getMQLogHandler(config).onError(config.getGroupName(),actionType, message, ex);
                }
            }
        });
    }




    @SneakyThrows
    private static MQLogHandler getMQLogHandler(MQProviderConfig config) {
        if(config.getLogHandler()!=null && config.getLogHandler().isEnabled()) {
            synchronized (config) {
                SpringUtils.getImplInstance(MQLogHandler.class);
                MQLogHandler handler = SpringUtils.getBean(MQLogHandler.class);
                if(handler == null) {
                    handler = new DefaultMQLogHandler(config);
                }
                config.setMqLogHandler(handler);
            }
        }
        return config.getMqLogHandler();
    }

    public static void close(MQProviderConfig config) {
        if(config.getLogHandleExecutor() != null) {
            config.getLogHandleExecutor().shutdown();
            config.setLogHandleExecutor(null);
        }
    }
}
