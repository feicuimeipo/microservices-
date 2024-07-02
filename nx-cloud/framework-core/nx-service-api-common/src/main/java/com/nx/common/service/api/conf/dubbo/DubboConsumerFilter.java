package com.nx.common.service.api.conf.dubbo;

import com.nx.common.service.api.conf.NxServiceConfig;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.crypt.Base64;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import static com.nx.common.context.constant.NxRequestHeaders.*;


/**
 *
 * 使用 Logback 的 MDC 机制，在日志模板中加入 traceId 标识，取值方式为 %X{traceId}
 * 系统入口（api网关）创建 traceId 的值
 * 使用 MDC 保存 traceId
 * 修改 logback 配置文件模板格式添加标识 %X{traceId}
 *
 * <dubbo:provider filter="providerFilter" />
 * <dubbo:consumer filter="consumerFilter" />
 * 1
 *
 * Sa-Token 整合 Dubbo Consumer端过滤器
 */
@Slf4j
@Activate(group = {CommonConstants.CONSUMER}, order = -30000)
public class DubboConsumerFilter extends AbstractDubboFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        // 追加 Id-Token 参数
        RpcContext.getClientAttachment().setAttachment(HEADER_SERVICE_APP_ID, NxServiceConfig.appId());
        RpcContext.getClientAttachment().setAttachment(HEADER_TENANT_ID, CurrentRuntimeContext.getTenantId());
        RpcContext.getClientAttachment().setAttachment(HEADER_SERVICE_ACCESS_TOKEN, Base64.encodeBase64(NxServiceConfig.accessToken()));

        // 1.处理 Trace 信息
        printRequest(invocation);
        // 2.执行前
        handleTraceId();
        long start = System.currentTimeMillis();


        Result result = invoker.invoke(invocation);
        long end = System.currentTimeMillis();
        // 3.执行后
        final long executionTime = end - start;
        printResponse(invocation, result, executionTime);
        return result;
    }




}
