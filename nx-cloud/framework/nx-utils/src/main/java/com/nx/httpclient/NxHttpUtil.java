package com.nx.httpclient;

import com.nx.common.exception.BaseException;
import com.nx.httpclient.adapter.HttpClientProvider;
import com.nx.httpclient.adapter.impl.ApacheHttpClient;
import com.nx.httpclient.adapter.impl.JdkHttpClient;
import com.nx.httpclient.adapter.impl.OkHttp3Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * http操作工具类
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @date 2017年8月24日
 */
@Slf4j
public class NxHttpUtil {

	private static HttpClientProvider provider;

	static {
		String providerType =  HttpClientConfig.getHttpClientProvider();//SpringUtils.getProperty("nx.http-client.provider");
		try {
			if(providerType == null || providerType.equals("okHttp3")) {
				Class.forName("okhttp3.OkHttpClient");
				provider = new OkHttp3Client();
			}
		} catch (Exception e) {}
		if(provider == null) {
			try {
				if(providerType == null || providerType.equals("httpClient")) {
					Class.forName("org.apache.http.impl.client.CloseableHttpClient");
					Class.forName("org.apache.http.entity.mime.MultipartEntityBuilder");
					provider = new ApacheHttpClient();
				}
			} catch (Exception e) {}
		}

		if(provider == null) {
			provider = new JdkHttpClient();
		}
		System.out.println("init HttpClientProvider:"+provider.getClass().getSimpleName());
	}

	private NxHttpUtil() {}
	
	public static HttpResponseEntity get(String url) {
		return execute(HttpRequestEntity.get(url));
	}
	
	public static HttpResponseEntity postJson(String url,String json) {
		HttpRequestEntity requestEntity = HttpRequestEntity.post(url).body(json);
		return execute(requestEntity);
	}
	
	public static HttpResponseEntity postJson(String url,String json,String charset) {
		String contentType = HttpClientProvider.CONTENT_TYPE_JSON_UTF8;//HttpClientProvider.CONTENT_TYPE_JSON_PREFIX + charset;
		HttpRequestEntity requestEntity = HttpRequestEntity.post(url).body(json).contentType(contentType);
		return execute(requestEntity);
	}
	
	public static HttpResponseEntity execute(HttpRequestEntity requestEntity) {
		StringBuilder logBuilder = null;
		try {
			if(StringUtils.isBlank(requestEntity.getUri())) {
				throw new IllegalArgumentException("request uri is missing");
			}
			requestEntity.uri(HostMappingHolder.resolveUrl(requestEntity.getUri()));
			if(log.isDebugEnabled()) {
				logBuilder = requestEntity.buildRequestLog();
			}
			HttpResponseEntity resp = provider.execute(requestEntity);
			if(logBuilder != null) {
				resp.appendResponseLog(logBuilder);
			}
			return resp;
		} catch (IOException e) {
			if(logBuilder != null) {
				logBuilder.append("\nexception:").append(e.getMessage());
				logBuilder.append("\n---------------backend request trace end--------------------");
			}
			if(e instanceof java.net.ConnectException) {
				return new HttpResponseEntity(503, "ConnectException:" + e.getMessage());
			}else if(e instanceof java.net.UnknownHostException) {
				return new HttpResponseEntity(400, "UnknownHostException:" + e.getMessage());
			}
			return new HttpResponseEntity(400, e.getMessage());
		}
	}

	
	public static HttpResponseEntity uploadFile(String url,String fieldName,File file){
		HttpRequestEntity requestEntity = HttpRequestEntity.post(url);
		requestEntity.fileParam(fieldName, file);
		return execute(requestEntity);
	}
	
	public static String downloadFile(String fileURL, String saveDir){
		HttpURLConnection httpConn = null;
		FileOutputStream outputStream = null;
		try {
			URL url = new URL(fileURL);
	        httpConn = (HttpURLConnection) url.openConnection();
	        int responseCode = httpConn.getResponseCode();
	 
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            String fileName = "";
	            String disposition = httpConn.getHeaderField("Content-Disposition");
	 
	            if (disposition != null) {
	                int index = disposition.indexOf("filename=");
	                if (index > 0) {
	                    fileName = disposition.substring(index + 10,
	                            disposition.length() - 1);
	                }
	            } else {
	                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
	                        fileURL.length());
	            }
	            InputStream inputStream = httpConn.getInputStream();
	            String saveFilePath = saveDir + File.separator + fileName;
	             
	            outputStream = new FileOutputStream(saveFilePath);
	 
	            int bytesRead = -1;
	            byte[] buffer = new byte[2048];
	            while ((bytesRead = inputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }
	 
	            outputStream.close();
	            inputStream.close();
	            
	            return saveFilePath;
	        } else {
	        	throw new BaseException(responseCode, "下载失败");
	        }
		} catch (IOException e) {
			throw new BaseException(500, "下载失败", e);
		}finally {
			try {if( outputStream!= null) outputStream.close();} catch (Exception e2) {}
			try {if( httpConn!= null) httpConn.disconnect();} catch (Exception e2) {}
		}
        
       
    }

}
