/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: nianxiMicro
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.logger.storage.impl;


import com.nx.logger.config.NxLoggerConfig;
import com.nx.logger.storage.ILogStorage;
import com.nx.logger.storage.NxLoggerModel;
import com.nx.logger.storage.dao.MongoDBDao;
import com.nx.logger.storage.dao.impl.MongoDBDaoImpl;


public class MongodbILogStorage implements ILogStorage {

    private MongoDBDao mongoLogsDao;
    private String appId;
    private String loggerName;
    private String profile;

    public MongodbILogStorage(NxLoggerConfig.MongodbConfig config, String appId, String loggerName, String profile){
        mongoLogsDao = new MongoDBDaoImpl(config.getHost(),config.getPort(),config.getUsername(),config.getPassword(),config.getDbName());
        this.appId = appId;
        this.loggerName = loggerName;
        this.profile = profile;
    }


    @Override
    public void writeLog(NxLoggerModel nxLoggerModel) {
        nxLoggerModel.setAppId(appId);
        nxLoggerModel.setProfile(profile);
        mongoLogsDao.insert(loggerName, nxLoggerModel);
    }


    public void writeLog(String content) {
        mongoLogsDao.insert(loggerName,content);
    }
}
