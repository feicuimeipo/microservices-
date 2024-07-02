package com.nx.logger.storage.impl;

import com.alibaba.fastjson2.JSONObject;
import com.nx.common.context.SpringUtils;
import com.nx.logger.enums.NxLoggerType;
import com.nx.logger.exception.NxLoggerException;
import com.nx.logger.model.api.*;
import com.nx.logger.model.api.dto.*;
import com.nx.logger.storage.ILogStorage;
import com.nx.logger.storage.NxLoggerModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public class SpiServiceImpl implements ILogStorage {
    private static  LoginLogApi loginLogApi;
    private static  OperateLogApi operateLogApi;
    private static  ApiErrorLogApi apiErrorLogApi;
    private static  ApiAccessLogApi apiAccessLogApi;
    private static  BuryingPointLogApi buryingPointLogApi;
    private static  ActionLogApi actionLogApi;
    private static  AccessLogRpcApi accessLogRpcApi;
    private static  ApiAccessLogOpenApi apiAccessLogOpenApi;
    private static  ILogStorage fileAppender;

    public SpiServiceImpl(FileAppenderLogsStorage fileAppender){
        operateLogApi = SpringUtils.getBean(OperateLogApi.class);
        apiErrorLogApi = SpringUtils.getBean(ApiErrorLogApi.class);
        loginLogApi = SpringUtils.getBean(LoginLogApi.class);
        apiAccessLogApi = SpringUtils.getBean(ApiAccessLogApi.class);
        buryingPointLogApi = SpringUtils.getBean(BuryingPointLogApi.class);
        actionLogApi = SpringUtils.getBean(ActionLogApi.class);
        accessLogRpcApi = SpringUtils.getBean(AccessLogRpcApi.class);
        apiAccessLogOpenApi = SpringUtils.getBean(ApiAccessLogOpenApi.class);
        if (fileAppender==null){
            throw new NxLoggerException("fileAppender is null!");
        }
        this.fileAppender = fileAppender;
    }


    @Async
    @Override
    public void writeLog(NxLoggerModel nxLoggerModel) {
        if (nxLoggerModel==null){
            log.warn("nxLoggerModel is nul!");
            return;
        }
        NxLoggerType nxLoggerType =  nxLoggerModel.getNxLoggerType();
        if (nxLoggerType==null){
            log.warn("nxLoggerType is null!");
            nxLoggerType = NxLoggerType.Unknown;
        }
        switch (nxLoggerType){
            case OperateLog:
                if (operateLogApi !=null) {
                    log.warn("operateLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                OperateLogDTO logDTO = new OperateLogDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(), logDTO);
                operateLogApi.createOperateLog(logDTO);
                break;
            case ApiErrorLog:
                if (apiErrorLogApi ==null) {
                    log.warn("operateLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                ApiErrorLogDTO errorLogDTO = new ApiErrorLogDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(), errorLogDTO);
                apiErrorLogApi.createApiErrorLog(errorLogDTO);
                break;
            case ApiAccessLog:
                if (apiAccessLogApi ==null) {
                    log.warn("operateLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                ApiAccessLogDTO apiAccessLogDTO  = new ApiAccessLogDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(),apiAccessLogDTO);
                apiAccessLogApi.createApiAccessLog(apiAccessLogDTO);
                break;
            case LoginLog:
                if (loginLogApi ==null) {
                    log.warn("operateLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                LoginLogDTO loginLogDTO  = new LoginLogDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(),loginLogDTO);
                loginLogApi.createLoginLog(loginLogDTO);
                break;
            case BuriedPointLog:
                if (buryingPointLogApi ==null) {
                    log.warn("buryingPointLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                BuryingPointLogDTO buryingPointLogDTO = new BuryingPointLogDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(), buryingPointLogDTO);
                buryingPointLogApi.createBuryingPoint(buryingPointLogDTO);
                break;
            case ActionLog:
                if (actionLogApi ==null) {
                    log.warn("actionLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                ActionLogDTO dto = new ActionLogDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(), dto);
                actionLogApi.createActionLog(dto);
                break;
            case ApiAccessRpcLog:
                if (accessLogRpcApi ==null) {
                    log.warn("accessLogRpcApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                AccessLogRpcDTO accessLogRpcDTO = new AccessLogRpcDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(), accessLogRpcDTO);
                accessLogRpcApi.createRpcAccessLog(accessLogRpcDTO);
                break;
            case ApiAccessOpenApiLog:
                if (apiAccessLogOpenApi ==null) {
                    log.warn("operateLogApi is null!");
                    fileAppender.writeLog(nxLoggerModel);
                    break;
                }
                AccessLogOpenApiDTO accessLogOpenApiDTO = new AccessLogOpenApiDTO();
                BeanUtils.copyProperties(nxLoggerModel.getData(), accessLogOpenApiDTO);
                apiAccessLogOpenApi.createOpenApiAccessLog(accessLogOpenApiDTO);
                break;
            default:
                fileAppender.writeLog(nxLoggerModel);
        }
    }

    //@Async
    @Override
    public void writeLog(String jsonMessage) {
        //TODO
        try {
            NxLoggerModel loggerModel = JSONObject.parseObject(jsonMessage, NxLoggerModel.class);
            writeLog(loggerModel);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}
