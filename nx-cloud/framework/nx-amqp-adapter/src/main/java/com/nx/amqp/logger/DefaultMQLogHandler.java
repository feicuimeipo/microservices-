package com.nx.amqp.logger;

import com.aliyun.openservices.shade.org.apache.commons.lang3.exception.ExceptionUtils;
import com.nx.amqp.adapter.MQProviderConfig;
import com.nx.amqp.adapter.MQLogHandler;
import com.nx.amqp.adapter.MQMessage;
import com.nx.amqp.adapter.enums.ActionType;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.spi.LoginUser;
import com.nx.common.tracing.NxTraceUtil;
import com.nx.logger.NxLoggerStorageProvider;
import com.nx.logger.enums.ActionLogType;
import com.nx.logger.model.api.dto.ActionLogDTO;
import java.util.Date;

import static com.nx.utils.IpUtil.getLocalIpAddr;

/**
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakinge</a>
 * @date Sep 17, 2022
 */
public class DefaultMQLogHandler implements MQLogHandler {
   MQProviderConfig config ;
    public DefaultMQLogHandler(MQProviderConfig config){
            this.config = config;
    }
    private boolean inited = false;


    @Override
    public void onSuccess(String groupName, ActionType actionType, MQMessage message) {
        ActionLogDTO actionLog = buildActionLogObject(groupName, actionType, message);
        actionLog.setSuccessed(true);
        NxLoggerStorageProvider.writeLog(actionLog,config.getLogStoreType());
    }


    @Override
    public void onError(String groupName, ActionType actionType, MQMessage message, Throwable e) {
        ActionLogDTO actionLog = buildActionLogObject(groupName, actionType, message);
        actionLog.setSuccessed(false);
        actionLog.setExceptions(ExceptionUtils.getStackTrace(e));
        NxLoggerStorageProvider.writeLog(actionLog,config.getLogStoreType());
    }

    private ActionLogDTO buildActionLogObject(String groupName, ActionType actionType, MQMessage message) {
        ActionLogDTO actionLog = new ActionLogDTO();
        actionLog.setLogType(ActionLogType.messageQueue.name());
        actionLog.setAppId(config.getApplicationName());
        actionLog.setEnv(config.getProfile());
        actionLog.setActionAt(new Date());
        actionLog.setTraceId(NxTraceUtil.getContextTraceId());
        actionLog.setActionName(actionType == ActionType.sub ? "messageConsume" : "messageProduce");
        actionLog.setActionKey(actionType.name() + "_" + message.getTopic());
        if(actionType == ActionType.sub) {
            actionLog.setInputData(message.toMessageValue(true));
        }
        actionLog.setFinishAt(actionLog.getActionAt());
        actionLog.setClientIp(getLocalIpAddr());
        actionLog.setUserId(groupName);
        actionLog.setUserName(groupName);
        LoginUser currentUser = CurrentRuntimeContext.getCurrentUser();
        if(currentUser != null){
            actionLog.setUserId(currentUser.getId().toString());
            actionLog.setUserName(currentUser.getUsername());
        }
        actionLog.setTenantId(CurrentRuntimeContext.getTenantId().toString());
        return actionLog;
    }

}