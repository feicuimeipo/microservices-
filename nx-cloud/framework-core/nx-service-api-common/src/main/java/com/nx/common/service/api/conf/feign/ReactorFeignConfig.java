package com.nx.common.service.api.conf.feign;

import com.nx.common.context.constant.ServiceProtocol;
import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.crypt.Base64;
import com.nx.common.service.api.conf.NxServiceConfig;
import feign.Contract;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.nx.common.tracing.NxTraceUtil.TRACE_ID_NAME;
import static com.nx.common.context.constant.NxRequestHeaders.*;


/**
 * Reactive
 * @author liyg
 * @Date 2018-08-14
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnMissingBean({ReactorFeignConfig.class})
public class ReactorFeignConfig {
	/**
	 * 从请求中获取 Authorization 设置到feign 请求中
	 * @author liyanggui
	 */
	@Bean
	@Primary
	public RequestInterceptor requestTokenBearerInterceptor() {
		return getRequestInterceptor();
	}

	private RequestInterceptor getRequestInterceptor(){
		return new RequestInterceptor() {
			@SneakyThrows
			public void apply(RequestTemplate requestTemplate) {
				String token = getRequest().getHeaders().getFirst(HEADER_SERVICE_ACCESS_TOKEN);
				String appId = getRequest().getHeaders().getFirst(HEADER_SERVICE_APP_ID);
				String tenantId = getRequest().getHeaders().getFirst(HEADER_TENANT_ID);

				requestTemplate.header(HEADER_SERVICE_REFERER, ServiceProtocol.feign.code());
				requestTemplate.header(HEADER_SERVICE_APP_ID, isBlank(appId)? NxServiceConfig.appId():appId);
				requestTemplate.header(HEADER_SERVICE_ACCESS_TOKEN, Base64.encodeBase64(isBlank(token)? NxServiceConfig.accessToken():token));
				requestTemplate.header(HEADER_TENANT_ID, CurrentRuntimeContext.getTenantId()==null?null:tenantId);

				//跨服务调用场景和父子线程场景类似，需要将MDC中的request_id加入到header中，然后在另一个服务中的过滤器会取出它，加入到MDC中。
				requestTemplate.header(TRACE_ID_NAME,  MDC.get(TRACE_ID_NAME));

			}
		};
	}


	/**
	 * 获取当前请求的request对象s
	 * @return
	 */
	public static ServerHttpRequest getRequest() {
		return ReactiveContextHolder.getServerHttpRequest();
	}


	public static class ReactiveContextHolder  {

		public static ServerHttpRequest getServerHttpRequest() {
			return getServerWebExchange().getRequest();
		}


		public static Mono<ServerWebExchange> getExchange() {
			return Mono.subscriberContext().map(ctx -> ctx.get(ServerWebExchange.class));
		}

		public static ServerWebExchange getServerWebExchange() {
			Mono<ServerWebExchange> mono = getExchange();
			ServerWebExchange exchange =  mono.block();
			return exchange;
		}

		public static ServerHttpResponse getResponse() {
			return getServerWebExchange().getResponse();
		}


	}



	@Bean
	public Contract feignContract() {
		return new Contract.Default();
	}


	protected static boolean isBlank(String traceId){
		return traceId ==null || traceId.trim().length()==0;
	}

}
