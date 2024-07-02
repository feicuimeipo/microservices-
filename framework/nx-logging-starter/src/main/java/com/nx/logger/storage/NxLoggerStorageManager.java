package com.nx.logger.storage;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.filter.Filter;
import com.alibaba.fastjson2.JSONObject;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.SpringUtils;
import com.nx.logger.config.NxLoggerConfig;
import com.nx.logger.enums.LogStorageType;
import com.nx.logger.storage.impl.FileAppenderLogsStorage;
import com.nx.logger.storage.impl.HttpApiILogStorage;
import com.nx.logger.storage.impl.SpiServiceImpl;
import com.nx.logger.utils.LogExceptionUtils;
import com.nx.logger.utils.LogWebUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.impl.StaticLoggerBinder;

@Data
@Slf4j
public class NxLoggerStorageManager extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private Logger logger;

    private Layout<ILoggingEvent> layout;
    private Filter filter;
    private String loggerName;
    private static LoggerContext loggerContext;
    private String applicationName;
    private String profile;
    private com.nx.logger.storage.ILogStorage ILogStorage;
    private LogStorageType logStorageType;
    private NxLoggerConfig config;

    private NxLoggerConfig.HttpApi currentHttpApi;

    public NxLoggerStorageManager(){
        config = SpringUtils.getBean(NxLoggerConfig.class);
        if (config==null){
            config = new NxLoggerConfig();
        }
        init();
    }

    public NxLoggerStorageManager(String loggerName, LogStorageType logStorageType){
        if (config==null){
            config = new NxLoggerConfig();
        }
        this.applicationName = config.getApplicationName();
        this.loggerName = loggerName;
        this.profile = config.getProfile();
        this.logStorageType = logStorageType;
        init();
    }

    public void init(){
        this.loggerContext = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
        super.setContext(loggerContext);

        String name = applicationName +'-' + loggerName + "-" +profile;
        super.setName(name);

        ch.qos.logback.classic.Logger mylogger = loggerContext.getLogger(name);
        mylogger.addAppender(this);
        mylogger.setLevel(Level.ALL);
        mylogger.setAdditive(true);
        this.logger = mylogger;
        this.ILogStorage = getILogStorage();

        this.start();
    }

    @Override
    public void start(){
        if(layout == null) {
            addWarn("Layout was not defined");
        }
        /**
         * 或者写入数据库 或者redis时 初始化连接等等
         */
        super.start();
    }

    @Override
    public void stop()
    {
        /**
         * 释放相关资源，如数据库连接，redis线程池等等
         */
        System.out.println("logback-stop方法被调用");
        if(!isStarted()) {
            return;
        }
        super.stop();
    }


    @Override
    protected void append(ILoggingEvent event) {
        if (event == null || !isStarted()){
            return;
        }

        NxLoggerModel logModel = null;
        String message = "";
        Object[] objList = event.getArgumentArray();
        for (Object o : objList) {
            if (o instanceof NxLoggerModel){
                logModel = (NxLoggerModel) o;
            }
        }
        if (logModel ==null){
            message = event.getFormattedMessage();
        }
        if (StringUtils.isEmpty(message) && logModel ==null){
            return;
        }

        if (logModel !=null){
            ILogStorage.writeLog(logModel);
        }else{
            ILogStorage.writeLog(message);
        }
    }

    private ILogStorage getILogStorage(){
        FileAppenderLogsStorage fileAppender = new FileAppenderLogsStorage(config.getFileLog(),applicationName,loggerName,profile);
        switch (logStorageType) {
//            case mongodb:
//                return new MongodbILogStorage(config.getMongodb(),appId,loggerName,profile);
            case httpApi:
                return new HttpApiILogStorage(config.getHttpApi(),fileAppender);
            case spi:
                return new SpiServiceImpl(fileAppender);
            default:
                return fileAppender;
        }
    }



    public  void writeLog(NxLoggerModel logDTO) {
        if (logDTO==null){
            logDTO = new NxLoggerModel();
        }
        logDTO.setAppId(applicationName);
        logDTO.setProfile(profile);

        if (logDTO.getRequestInfo() == null) {
            logDTO.setRequestInfo(NxLoggerModel.RequestInfo.build(LogWebUtil.getRequest()));
        }

        if (logDTO.getServerInfo()==null){
            String port = SpringUtils.Env.getProperty("server.port","-1");
            logDTO.setServerInfo(NxLoggerModel.ServerInfo.build(port==null?"-1":port));
        }

        logDTO.setStackTraceElement(LogExceptionUtils.getStackTraceElement());

        if (logDTO.getUid() == null) {
            Long uid = CurrentRuntimeContext.getCurrentUser()==null?null:CurrentRuntimeContext.getCurrentUserId();
            if (uid!=null) {
                logDTO.setUid(CurrentRuntimeContext.getCurrentUserId());
                logDTO.setTenantId(CurrentRuntimeContext.getTenantId());
            }else{
                logDTO.setTenantId(CurrentRuntimeContext.getTenantId());
            }
        }

        String json = JSONObject.toJSONString(logDTO);
        logger.info(json, logDTO);
    }

}