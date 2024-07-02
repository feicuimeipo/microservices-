package com.nx.logger.model.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String IGNORE_FLAG = "[ignore]";

    private String logType;
    private String appId;
    private String env;
    private String tenantId;
    private String clientType;
    private String actionName;
    private String actionKey;
    private String userId;
    private String userName;
    private String moduleId;
    private String clientIp;
    private Date actionAt;
    private String inputData;
    private String outputData;
    private Boolean successed;
    private Integer useTime;
    private String bizId;
    private String traceId;
    private String exceptions;
    private Date finishAt;
}