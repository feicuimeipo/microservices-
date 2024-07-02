package com.nx.common.service.api.conf.dubbo;

import com.nx.common.context.constant.ServiceProtocol;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.context.spi.ServiceVerification;
import com.nx.common.crypt.Base64;
import com.nx.common.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import static com.nx.common.context.constant.NxRequestHeaders.*;

/**
 *  把信息放进MDC里面
 *  整合 Dubbo Provider端过滤器
 */
@Slf4j
@Activate(group = {CommonConstants.PROVIDER}, order = -30000)
public class DubboProviderFilter extends AbstractDubboFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        // 1.处理 Trace 信息
        printRequest(invocation);
        // 2.执行前
        handleTraceId();
        long start = System.currentTimeMillis();

        String tenantId = invocation.getAttachment(HEADER_TENANT_ID);
        CurrentRuntimeContext.setTenantId(tenantId==null?null:Long.parseLong(tenantId));

        //-检测
        apiAccessCheck(invocation);

        //处理请求
        Result result = invoker.invoke(invocation);
        long end = System.currentTimeMillis();
        // 4.执行后
        final long executionTime = end - start;
        printResponse(invocation, result, executionTime);

        CurrentRuntimeContext.removeCurrentTenantId();
        return result;
    }


    private void apiAccessCheck(Invocation invocation){
        // RPC 调用鉴权
        //过滤芯请求
        String appId = invocation.getAttachment(HEADER_SERVICE_APP_ID);
        String accessToken =  Base64.encodeBase64(invocation.getAttachment(HEADER_SERVICE_ACCESS_TOKEN));

        //TODO: 校验, 不能过抛出异常
        if (!ServiceVerification.apiAccessCheck(appId, accessToken,  ServiceProtocol.dubbo)){
            String msg =  String.format("dubbo接口认证未通过(appId:%s,accessToken:%s,encrypt:%s)！",appId,accessToken);
            throw new UnauthorizedException(403,msg);
        };

    }

}
