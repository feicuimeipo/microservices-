package com.nx.logger.aop;


import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.SpringUtils;
import com.nx.common.model.Result;
import com.nx.common.model.constant.ResultCode;
import com.nx.common.tracing.NxTraceUtil;
import com.nx.logger.NxLoggerStorageProvider;
import com.nx.logger.config.NxLoggerConfig;
import com.nx.logger.model.api.dto.ApiAccessLogDTO;
import com.nx.logger.utils.LogWebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;




/**
 * API 访问日志 Filter

 */
@Aspect
@Slf4j
@ConditionalOnMissingBean(ApiAccessLogFilter.class)
@ConditionalOnProperty(prefix = "nx.logger.api-access",value = "enabled",havingValue = "true",matchIfMissing = true)
public class ApiAccessLogFilter extends OncePerRequestFilter {



    private List<String> excludePathPrefix=new ArrayList<>();
    public void addExcludePathPrefix(String urlPrefix){

        excludePathPrefix.add(urlPrefix);
    }
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String pathPrefix : excludePathPrefix) {
            boolean ret = request.getRequestURI().startsWith(pathPrefix);
            if (ret){
                return false;
            }
        }
        return true;
    }

    public ApiAccessLogFilter() {

        BannerUtils.push(new BannerUtils.BannerInfo(this.getClass(),"",new String[]{"nx-logging-starter："+ this.getClass().getSimpleName() +" enabled"}));
    }




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!shouldNotFilter(request)){
            return;
        }
        // 获得开始时间
        Date beginTim = new Date();
        // 提前获得参数，避免 XssFilter 过滤处理
        Map<String, String> queryString = ServletUtil.getParamMap(request);
        String requestBody = LogWebUtil.isJsonRequest(request) ? ServletUtil.getBody(request) : null;

        try {
            // 继续过滤器
            filterChain.doFilter(request, response);
            // 正常执行，记录日志
            createApiAccessLog(request, beginTim, queryString, requestBody, null);
        } catch (Exception ex) {
            // 异常执行，记录日志
            createApiAccessLog(request, beginTim, queryString, requestBody, ex);
            throw ex;
        }
    }

    private void createApiAccessLog(HttpServletRequest request, Date beginTime,
                                    Map<String, String> queryString, String requestBody, Exception ex) {

        ApiAccessLogDTO apiAccessLogDTO = new ApiAccessLogDTO();
        try {
            this.buildApiAccessLogDTO(apiAccessLogDTO, request, beginTime, queryString, requestBody, ex);
            NxLoggerStorageProvider.writeLog(apiAccessLogDTO);
            //apiAccessLogFrameworkService.createApiAccessLog(apiAccessLogDTO);
        } catch (Throwable th) {
            String json = JSONObject.toJSONString(apiAccessLogDTO);
            log.error("[createApiAccessLog][url({}) log({}) 发生异常]", request.getRequestURI(), json, th);
        }
    }

    private void buildApiAccessLogDTO(ApiAccessLogDTO apiAccessLogDTO, HttpServletRequest request, Date beginTime,
                                      Map<String, String> queryString, String requestBody, Exception ex) {
        // 处理用户信息
        apiAccessLogDTO.setUserId(CurrentRuntimeContext.getCurrentUserId());

        // 设置访问结果
        Result<?> result = CurrentRuntimeContext.getCommonResult(request);
        if (result != null) {
            apiAccessLogDTO.setResultCode(result.getCode());
            apiAccessLogDTO.setResultMsg(result.getMsg());
        } else if (ex != null) {
            apiAccessLogDTO.setResultCode(ResultCode.INTERNAL_SERVER_ERROR.getCode());
            apiAccessLogDTO.setResultMsg(ExceptionUtils.getRootCauseMessage(ex));
        } else {
            apiAccessLogDTO.setResultCode(0);
            apiAccessLogDTO.setResultMsg("");
        }
        // 设置其它字段
        apiAccessLogDTO.setTraceId(NxTraceUtil.getContextTraceId());

        NxLoggerConfig config = SpringUtils.getBean(NxLoggerConfig.class);;
        apiAccessLogDTO.setApplicationName(config.getApplicationName());

        apiAccessLogDTO.setRequestUrl(request.getRequestURI());
        Map<String, Object> requestParams = MapUtil.<String, Object>builder().put("query", queryString).put("body", requestBody).build();
        String json = JSONObject.toJSONString(requestParams);
        apiAccessLogDTO.setRequestParams(json);
        apiAccessLogDTO.setRequestMethod(request.getMethod());
        apiAccessLogDTO.setUserAgent(LogWebUtil.getUserAgent(request));
        apiAccessLogDTO.setUserIp(LogWebUtil.getClientIP(request));
        // 持续时间
        apiAccessLogDTO.setBeginTime(beginTime);
        apiAccessLogDTO.setEndTime(new Date());
        apiAccessLogDTO.setDuration((int) diff(apiAccessLogDTO.getEndTime(), apiAccessLogDTO.getBeginTime()));
    }

    public static long diff(Date endTime, Date startTime) {
        return endTime.getTime() - startTime.getTime();
    }
}
