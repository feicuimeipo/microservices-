package com.nx.httpclient.adapter.impl;

import com.nx.httpclient.HttpRequestEntity;
import com.nx.httpclient.HttpResponseEntity;
import com.nx.httpclient.adapter.HttpClientProvider;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.nx.common.context.constant.NxRequestHeaders.HEADER_JWT_AUTHORITIES;


public class OkHttp3Client implements HttpClientProvider {

	private static Map<String, MediaType> mediaTypeMappings = new HashMap<>();
	private static OkHttpClient httpClient;
	
	static {
		mediaTypeMappings.put(CONTENT_TYPE_JSON_UTF8, MediaType.parse(CONTENT_TYPE_JSON_UTF8));
		mediaTypeMappings.put(CONTENT_TYPE_FROM_URLENCODED_UTF8, MediaType.parse(CONTENT_TYPE_FROM_URLENCODED_UTF8));
		httpClient = new OkHttpClient.Builder().connectionPool(new ConnectionPool(1, 60L, TimeUnit.SECONDS))
				.connectTimeout(HttpClientProvider.connectTimeout(), TimeUnit.MILLISECONDS).readTimeout(HttpClientProvider.readTimeout(), TimeUnit.MILLISECONDS)
				.writeTimeout(HttpClientProvider.readTimeout(), TimeUnit.MILLISECONDS).build();
	}
	
	@Override
	public HttpResponseEntity execute(HttpRequestEntity requestEntity) throws IOException {
		
		try {
			HttpUrl.Builder urlBuilder = HttpUrl.parse(requestEntity.getUri()).newBuilder();
			if (requestEntity.getQueryParams() != null) {
				for (String key : requestEntity.getQueryParams().keySet()) {
					urlBuilder.addQueryParameter(key, requestEntity.getQueryParams().get(key).toString());
				}
			}
			
			Headers.Builder headerBuilder = new Headers.Builder();
			if (requestEntity.getHeaders() != null) {
				for (String key : requestEntity.getHeaders().keySet()) {
					headerBuilder.add(key, requestEntity.getHeaders().get(key).toString());
				}
			}
			
			if(requestEntity.getBasicAuth() != null) {
				headerBuilder.add(HEADER_JWT_AUTHORITIES, requestEntity.getBasicAuth().getEncodeBasicAuth());
			}
			
			Request.Builder requestBuilder = new Request.Builder().headers(headerBuilder.build())
					.url(urlBuilder.build());
			
			if(HttpMethod.POST == requestEntity.getMethod()) {
				RequestBody body = null;
				if(requestEntity.getBody() != null) {
					body = FormBody.create(contentType2MediaType(requestEntity.getContentType()), requestEntity.getBody());
				} else if(requestEntity.getFormParams() != null) {
					Set<Entry<String, Object>> formEntries = requestEntity.getFormParams().entrySet();
					Object entryValue;
					if(requestEntity.isMultipart()) {
						MultipartBody.Builder builder = new MultipartBody.Builder();
						for (Entry<String, Object> entry : formEntries) {
							entryValue = entry.getValue();
							if(entryValue == null)continue;
							if(entryValue instanceof HttpRequestEntity.FileItem) {
								HttpRequestEntity.FileItem fileItem = (HttpRequestEntity.FileItem)entryValue;
								
								MediaType contentType = null;
								if(fileItem.getMimeType() != null) {
									contentType = MediaType.parse(fileItem.getMimeType());
								}
								RequestBody requestBody;
								requestBody = RequestBody.create(contentType, fileItem.getContent());
								builder.addFormDataPart(entry.getKey(), fileItem.getFileName(), requestBody);
							}else {
								builder.addFormDataPart(entry.getKey(), entryValue.toString());
							}
						}
						body = builder.build();
					}else {
						FormBody.Builder builder = new FormBody.Builder();
						for (Entry<String, Object> entry : formEntries) {
							if(entry.getValue() == null)continue;
							builder.add(entry.getKey(), entry.getValue().toString());
						}
						body = builder.build();
					}
				}
				
				if(body != null) {
					requestBuilder.post(body);
				}
			}
			
			HttpResponseEntity responseEntity = new HttpResponseEntity();
			
			Request request = requestBuilder.build();
			Response response = httpClient.newCall(request).execute();
			responseEntity.setStatusCode(response.code());
			if (response.body() != null) {
				responseEntity.setBody(response.body().string());
			}
			if (!response.isSuccessful()) {
				responseEntity.setMessage(StringUtils.defaultIfBlank(response.message(), responseEntity.getBody()));
			}
			
			return responseEntity;
		} finally {
			requestEntity.unset();
		}
	}
	
	private static MediaType contentType2MediaType(String contentType) {
		if(mediaTypeMappings.containsKey(contentType)) {
			return mediaTypeMappings.get(contentType);
		}
		return MediaType.parse(CONTENT_TYPE_FROM_URLENCODED_UTF8);
	}

}
