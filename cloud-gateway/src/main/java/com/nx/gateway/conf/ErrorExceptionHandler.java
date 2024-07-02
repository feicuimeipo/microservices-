package com.nx.gateway.conf;

import com.nx.api.model.R;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常处理
 */
public class ErrorExceptionHandler extends DefaultErrorWebExceptionHandler {

	public ErrorExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ErrorProperties errorProperties, ApplicationContext applicationContext) {
		super(errorAttributes, resources, errorProperties, applicationContext);
	}

	/**
	 * 获取异常属性
	 */
	@Override
	protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
		int code = 500;
		Throwable error = super.getError(request);
		if (error instanceof NotFoundException) {
			code = 404;
		}
		if (error instanceof ResponseStatusException) {
			code = ((ResponseStatusException) error).getStatus().value();
		}
		return response(code, this.buildMessage(request, error));
	}

	/**
	 * 指定响应处理方法为JSON处理的方法
	 *
	 * @param request
	 */
	@Override
	protected Mono<ServerResponse> renderErrorView(ServerRequest request) {
		Map<String, Object> error = getErrorAttributes(request, ErrorAttributeOptions.defaults());
		int errorStatus = getHttpStatus(error);
		if (404 == errorStatus) {
			return ServerResponse.status(HttpStatus.NOT_FOUND)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(R.FAIL(404,"未找到资源"));
		}
		return super.renderErrorView(request);
	}

	/**
	 * 根据code获取对应的HttpStatus
	 *
	 * @param errorAttributes
	 * @return
	 */
	@Override
	protected int getHttpStatus(Map<String, Object> errorAttributes) {
		int statusCode = (int) errorAttributes.get("code");
		return HttpStatus.valueOf(statusCode).value();
	}

	/**
	 * 构建异常信息
	 *
	 * @param request
	 * @param ex
	 * @return
	 */
	private String buildMessage(ServerRequest request, Throwable ex) {
		StringBuilder message = new StringBuilder("Failed to handle request [");
		message.append(request.methodName());
		message.append(" ");
		message.append(request.uri());
		message.append("]");
		if (ex != null) {
			message.append(": ");
			message.append(ex.getMessage());
		}
		return message.toString();
	}

	/**
	 * 构建返回的JSON数据格式
	 *
	 * @param status       状态码
	 * @param errorMessage 异常信息
	 * @return
	 */
	public static Map<String, Object> response(int status, String errorMessage) {
		Map<String, Object> map = new HashMap<>(16);
		map.put("code", status);
		map.put("message", errorMessage);
		map.put("data", null);
		return map;
	}

}
