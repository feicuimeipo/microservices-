package com.nx.httpclient.adapter;

import com.nx.httpclient.HttpClientConfig;
import com.nx.httpclient.HttpRequestEntity;
import com.nx.httpclient.HttpResponseEntity;
import java.io.IOException;


/**
 *
 * <br>
 * Class Name   : HttpClientProvider
 *
 * @version 1.0.0
 * @date Apr 29, 2021
 *
 */
public interface HttpClientProvider {
	
	String CHARSET_UTF8 = "utf-8";
	String CONTENT_ENCODING_GZIP = "gzip";
	
	String CONTENT_TYPE_JSON_PREFIX = "application/json; charset=";
	String CONTENT_TYPE_FROM_URLENCODED_PREFIX = "application/x-www-form-urlencoded; charset=";
	String CONTENT_TYPE_FROM_MULTIPART_PREFIX = "multipart/form-data;charset=";
	
	String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";
	String CONTENT_TYPE_FROM_URLENCODED_UTF8 = "application/x-www-form-urlencoded; charset=utf-8";
	String CONTENT_TYPE_FROM_MULTIPART_UTF8 = "multipart/form-data;charset=utf-8";
	

	HttpResponseEntity execute(HttpRequestEntity requestEntity) throws IOException;

	static int connectTimeout(){
		return HttpClientConfig.getConnectTimeout();
	}

	static int readTimeout(){
		return HttpClientConfig.getReadTimeout();
	}

}
