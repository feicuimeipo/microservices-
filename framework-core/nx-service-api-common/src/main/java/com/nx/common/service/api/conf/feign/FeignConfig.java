package com.nx.common.service.api.conf.feign;

import com.nx.common.context.constant.ServiceProtocol;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.crypt.Base64;
import com.nx.common.service.api.conf.NxServiceConfig;
import feign.Contract;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

import static com.nx.common.tracing.NxTraceUtil.TRACE_ID_NAME;
import static com.nx.common.context.constant.NxRequestHeaders.*;


/**
 * @author nianxiaoling
 * @version v0.0.1
 * @apiNote feign传递token拦截统一处理
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "nx.feign")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnMissingBean({FeignConfig.class})
public class FeignConfig {



//    @Value("${nx.feign.encry.key:feignCallEncry}")
//    private String feignEncry= "feignCallEncry";


    /**
     * return new BasicAuthRequestInterceptor("user", "password");
     * 从请求中获取 Authorization设置到feign请求中
     * @return
     */
    @Bean
    public RequestInterceptor requestTokenBearerInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                String token =  getRequest().getParameter(HEADER_SERVICE_ACCESS_TOKEN);
                String appId = getRequest().getParameter(HEADER_SERVICE_APP_ID);
                String tenantId = getRequest().getParameter(HEADER_TENANT_ID);

                requestTemplate.header(HEADER_SERVICE_REFERER, ServiceProtocol.feign.code());
                requestTemplate.header(HEADER_SERVICE_APP_ID, isBlank(appId)? NxServiceConfig.appId():appId);
                requestTemplate.header(HEADER_SERVICE_ACCESS_TOKEN, Base64.encodeBase64(isBlank(token)? NxServiceConfig.accessToken():token));
                requestTemplate.header(HEADER_TENANT_ID, CurrentRuntimeContext.getTenantId()==null?null: CurrentRuntimeContext.getTenantId().toString());

                //跨服务调用场景和父子线程场景类似，需要将MDC中的request_id加入到header中，然后在另一个服务中的过滤器会取出它，加入到MDC中。
                requestTemplate.header(TRACE_ID_NAME,  MDC.get(TRACE_ID_NAME));
            }
        };
    }



    /**
     * 获取当前请求的request对象s
     * @return
     */
    public static HttpServletRequest getRequest() {

        RequestAttributes requestAttributes = null;
        try{
            requestAttributes = RequestContextHolder.currentRequestAttributes();

        }catch (IllegalStateException e){
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }



    /**
     * 设置请求超时时间
     * 默认
     */
    @Bean
    Request.Options feignOptions() {
       // Request.Options
        return new Request.Options(5 * 1000,TimeUnit.SECONDS, 60 * 1000, TimeUnit.SECONDS,true);
    }

    @Bean
    public Contract feignContract() {
        return new Contract.Default();
    }


    protected static boolean isBlank(String traceId){
        return traceId ==null || traceId.trim().length()==0;
    }

}
