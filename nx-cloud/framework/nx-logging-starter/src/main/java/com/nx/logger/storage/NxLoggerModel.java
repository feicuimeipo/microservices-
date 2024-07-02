package com.nx.logger.storage;

import com.nx.logger.enums.NxLoggerType;
import com.nx.logger.utils.LogNetUtil;
import com.nx.logger.utils.LogWebUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

@Data
public class NxLoggerModel<T> implements Serializable{
    private NxLoggerType nxLoggerType;
    private String traceId;
    private String appId;
    private String profile;
    private Long   tenantId;
    private String bizId;
    private Long uid;
    private String action;
    private String description;
    protected ClassInfo classInfo;
    protected ServerInfo serverInfo;
    protected RequestInfo requestInfo;
    protected List<StackTraceElement> stackTraceElement;
    protected T data;

    @Slf4j
    @Data
    @NoArgsConstructor
    public static class ClassInfo {
        private String className;
        private String methodName;
        private Class clz;
        private String fileName;
        private int lineNumber;
        private boolean nativeMethod;
    }



    @Data
    public static class RequestInfo implements Serializable {
        private String remoteIP;
        private String userAgent;
        private String requireUri;
        private String method;
        private String param;


        public static RequestInfo build(HttpServletRequest request) {
            RequestInfo requestInfo = new RequestInfo();
            if (request != null) {
                requestInfo.remoteIP = LogWebUtil.getClientIP(request);
                requestInfo.userAgent = request.getHeader("user-agent");
                requestInfo.requireUri = LogWebUtil.getPath(request.getRequestURI());
                requestInfo.method = request.getMethod();
                requestInfo.param = LogWebUtil.getRequestParamString(request);
                if (requestInfo.param == null)
                    requestInfo.param = "";
            }
            return requestInfo;
        }
    }

    @Data
    @Slf4j
    public static class ServerInfo implements Serializable {
        private String hostName;
        private String ip;
        private Integer port;
        private String ipWithPort;

        public static ServerInfo build(String port) {
            ServerInfo serverInfo = new ServerInfo();
            try {
                serverInfo.hostName = LogNetUtil.getHostName();
                serverInfo.ip = LogNetUtil.getHostIp();
                serverInfo.port = Integer.valueOf(port==null||port.length()==0?"-1":port);
                serverInfo.ipWithPort = String.format("%s:%d", new Object[]{serverInfo.ip,serverInfo.getPort()});
                return serverInfo;
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage(),e);
            }
            return serverInfo;
        }
    }




}
