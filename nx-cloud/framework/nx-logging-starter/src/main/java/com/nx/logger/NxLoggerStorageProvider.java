package com.nx.logger;


import com.nx.common.context.SpringUtils;
import com.nx.logger.enums.NxLoggerType;
import com.nx.logger.enums.LogStorageType;
import com.nx.logger.model.api.dto.*;
import com.nx.logger.storage.NxLoggerModel;
import com.nx.logger.storage.NxLoggerStorageManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NxLoggerStorageProvider {

    public static Map<String, NxLoggerStorageManager> actionLogMap = new ConcurrentHashMap<>();
    final static LogStorageType DEFAULT_STORAGE_TYPE = LogStorageType.spi;


    private static NxLoggerStorageManager getActionLogger(LogStorageType storeType){
        String loggerName = SpringUtils.Env.getProperty("spring.application.name","");
        loggerName = loggerName + "_" + "nx_logger";

        if (!actionLogMap.containsKey(loggerName)){
            NxLoggerStorageManager logger = new NxLoggerStorageManager(loggerName,storeType);
            actionLogMap.put(loggerName,logger);
            return logger;
        }
        return actionLogMap.get(loggerName);
    }

    public  static void writeLog(AccessLogRpcDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.OperateLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }

    public  static void writeLog(AccessLogRpcDTO logDTO) {
        writeLog(logDTO, LogStorageType.spi);
    }

    public  static void writeLog(AccessLogOpenApiDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.ApiAccessLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }

    public  static void writeLog(AccessLogOpenApiDTO logDTO) {
        writeLog(logDTO, LogStorageType.spi);
    }

    public  static void writeLog(ActionLogDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.ApiAccessLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }

    public  static void writeLog(ActionLogDTO logDTO) {
        writeLog(logDTO, LogStorageType.spi);
    }



    public  static void writeLog(ApiAccessLogDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.ApiAccessLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }
    public  static void writeLog(ApiAccessLogDTO logDTO) {
        writeLog(logDTO,DEFAULT_STORAGE_TYPE);
    }

    public  static void writeLog(ApiErrorLogDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.ApiErrorLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }

    public  static void writeLog(ApiErrorLogDTO logDTO) {
            writeLog(logDTO,DEFAULT_STORAGE_TYPE);
    }
    public  static void writeLog(LoginLogDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.LoginLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }

    public static void writeLog(LoginLogDTO logDTO) {
        writeLog(logDTO,DEFAULT_STORAGE_TYPE);
    }



    public static void writeLog(OperateLogDTO logDTO) {
        writeLog(logDTO,DEFAULT_STORAGE_TYPE);
    }
    public static void writeLog(OperateLogDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.OperateLog);
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }

    public  static void writeLog(BuryingPointLogDTO logDTO) {
        writeLog(logDTO,DEFAULT_STORAGE_TYPE);
    }
    public  static void writeLog(BuryingPointLogDTO logDTO, LogStorageType storeType) {
        NxLoggerModel nxLoggerModel = new NxLoggerModel();
        nxLoggerModel.setData(logDTO);
        nxLoggerModel.setNxLoggerType(NxLoggerType.BuriedPointLog);
        getActionLogger(DEFAULT_STORAGE_TYPE).writeLog(nxLoggerModel);
    }

    public  static void writeLog(NxLoggerModel nxLoggerModel, LogStorageType storeType) {
        getActionLogger(storeType).writeLog(nxLoggerModel);
    }
    public  static void writeLog(NxLoggerModel nxLoggerModel){
        getActionLogger(DEFAULT_STORAGE_TYPE).writeLog(nxLoggerModel);
    }



    public static <T> void writeLog(String id,String action, Long userId,String description,T data) {
        NxLoggerModel logDTO = new NxLoggerModel();
        logDTO.setNxLoggerType(NxLoggerType.Other);
        logDTO.setBizId(id);
        logDTO.setAction(action);
        logDTO.setUid(userId);
        logDTO.setDescription(description);
        logDTO.setData(data);

        writeLog(logDTO);
    }


    public static <T>  void writeLog(String id,String action, Long userId,String description,T data, NxLoggerModel.ClassInfo classInfo) {
        NxLoggerModel logDTO = new NxLoggerModel();
        logDTO.setNxLoggerType(NxLoggerType.Other);
        logDTO.setBizId(id);
        logDTO.setAction(action);
        logDTO.setUid(userId);
        logDTO.setDescription(description);
        if (classInfo!=null){
            logDTO.setClassInfo(classInfo);
        }

        writeLog(logDTO);
    }
}
