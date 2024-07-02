package com.nx.httpclient.adapter.impl;


import com.nx.httpclient.HttpRequestEntity;
import com.nx.httpclient.adapter.HttpClientProvider;
import com.nx.httpclient.HttpResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPInputStream;


/**
 * 
 * 
 * <br>
 * Class Name : JdkHttpClient
 *
 * @author vakinge@gmail.com
 * @version 1.0.0
 * @date Apr 29, 2021
 */
public class JdkHttpClient implements HttpClientProvider {

	public static final String CHARSET_UTF8 = "utf-8";
	private static final String CONTENT_TYPE_JSON_PREFIX = "application/json; charset="+CHARSET_UTF8;
	private static final String CONTENT_TYPE_FROM_URLENCODED_PREFIX = "application/x-www-form-urlencoded; charset=";
	public static final String CONTENT_TYPE_JSON_UTF8 = CONTENT_TYPE_JSON_PREFIX + CHARSET_UTF8;
	public static final String CONTENT_TYPE_FROM_URLENCODED_UTF8 = CONTENT_TYPE_FROM_URLENCODED_PREFIX + CHARSET_UTF8;

	@Override
	public HttpResponseEntity execute(HttpRequestEntity requestEntity) throws IOException {

		HttpURLConnection connection = null;
		OutputStream out = null;

		String charset = requestEntity.getCharset();
		String queryParam = buildQuery(requestEntity.getQueryParams(), charset);

		URL url = buildQueryParamUrl(requestEntity.getUri(), queryParam);

		try {
			connection = buildConnection(url, requestEntity);
			if (HttpMethod.POST == requestEntity.getMethod()) {
				if (requestEntity.getBody() != null) {
					byte[] data = requestEntity.getBody().getBytes();
					connection.setRequestProperty("Content-Length", String.valueOf(data.length));
					out = connection.getOutputStream();
					out.write(data);
					out.flush();
				} else if (requestEntity.getFormParams() != null) {
					if (requestEntity.isMultipart()) {
						byte[] entryBoundaryBytes = ("\r\n--" + requestEntity.getBoundary() + "\r\n").getBytes(charset);
						// 组装请求参数
						Set<Entry<String, Object>> formEntries = requestEntity.getFormParams().entrySet();

						out = connection.getOutputStream();

						Object entryValue;
						HttpRequestEntity.FileItem curFileItem;
						for (Entry<String, Object> entry : formEntries) {
							entryValue = entry.getValue();
							if (entryValue == null)
								continue;
							byte[] textBytes;
							if (entryValue instanceof HttpRequestEntity.FileItem) {
								curFileItem = (HttpRequestEntity.FileItem) entryValue;
								if (curFileItem.getSize() == 0) {
									continue;
								}
								textBytes = getFileEntry(entry.getKey(), curFileItem.getFileName(),
										curFileItem.getMimeType(), charset);
							} else {
								curFileItem = null;
								textBytes = getTextEntry(entry.getKey(), entry.getValue().toString(), charset);
							}
							out.write(entryBoundaryBytes);
							out.write(textBytes);
							if (curFileItem != null) {
								for (int i = 0; i < curFileItem.getChunkNum(); i++) {
									out.write(curFileItem.getContent());
								}
							}
						}
						// 添加请求结束标志
						byte[] endBoundaryBytes = ("\r\n--" + requestEntity.getBoundary() + "--\r\n").getBytes(charset);
						out.write(endBoundaryBytes);
					} else {
						String query = buildQuery(requestEntity.getFormParams(), charset);
						byte[] data = {};
						if (query != null) {
							data = query.getBytes(charset);
						}
						connection.setRequestProperty("Content-Length", String.valueOf(data.length));
						out = connection.getOutputStream();
						out.write(data);
					}
				}
			}
			return getResponseAsResponseEntity(connection, requestEntity.getCharset());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e2) {
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
			requestEntity.unset();
		}
	}

	private static URL buildQueryParamUrl(String strUrl, String query) throws IOException {
		URL url = new URL(strUrl);
		if (StringUtils.isEmpty(query)) {
			return url;
		}

		if (StringUtils.isEmpty(url.getQuery())) {
			if (strUrl.endsWith("?")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "?" + query;
			}
		} else {
			if (strUrl.endsWith("&")) {
				strUrl = strUrl + query;
			} else {
				strUrl = strUrl + "&" + query;
			}
		}

		return new URL(strUrl);
	}

	public static String buildQuery(Map<String, Object> params, String charset) throws IOException {
		if (params == null || params.isEmpty()) {
			return null;
		}

		if (charset == null)
			charset = CHARSET_UTF8;

		StringBuilder query = new StringBuilder();
		Set<Entry<String, Object>> entries = params.entrySet();
		boolean hasParam = false;

		for (Entry<String, Object> entry : entries) {
			String name = entry.getKey();
			String value = Objects.toString(entry.getValue(), null);
			if (StringUtils.isAnyEmpty(name, value))
				continue;
			if (hasParam) {
				query.append("&");
			} else {
				hasParam = true;
			}
			query.append(name).append("=").append(URLEncoder.encode(value, charset));
		}

		return query.toString();
	}

	private static HttpResponseEntity getResponseAsResponseEntity(HttpURLConnection conn, String charset)
			throws IOException {
		HttpResponseEntity responseEntity = new HttpResponseEntity();
		InputStream es = conn.getErrorStream();

		responseEntity.setStatusCode(conn.getResponseCode());
		if (es == null) {
			String contentEncoding = conn.getContentEncoding();
			if (CONTENT_ENCODING_GZIP.equalsIgnoreCase(contentEncoding)) {
				responseEntity.setBody(getStreamAsString(new GZIPInputStream(conn.getInputStream()), charset));
			} else {
				responseEntity.setBody(getStreamAsString(conn.getInputStream(), charset));
			}
		} else {
			String msg = getStreamAsString(es, charset);
			if (StringUtils.isEmpty(msg)) {
				responseEntity.setBody(conn.getResponseCode() + ":" + conn.getResponseMessage());
			} else {
				responseEntity.setBody(msg);
			}
		}

		return responseEntity;
	}

	private static String getStreamAsString(InputStream stream, String charset) throws IOException {
		try {
			Reader reader = new InputStreamReader(stream, charset);
			StringBuilder response = new StringBuilder();

			final char[] buff = new char[1024];
			int read = 0;
			while ((read = reader.read(buff)) > 0) {
				response.append(buff, 0, read);
			}

			return response.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private static byte[] getTextEntry(String fieldName, String fieldValue, String charset) throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
		entry.append(fieldValue);
		return entry.toString().getBytes(charset);
	}

	private static byte[] getFileEntry(String fieldName, String fileName, String mimeType, String charset)
			throws IOException {
		StringBuilder entry = new StringBuilder();
		entry.append("Content-Disposition:form-data;name=\"");
		entry.append(fieldName);
		entry.append("\";filename=\"");
		entry.append(fileName);
		entry.append("\"\r\nContent-Type:");
		entry.append(mimeType);
		entry.append("\r\n\r\n");
		return entry.toString().getBytes(charset);
	}

	private static HttpURLConnection buildConnection(URL url, HttpRequestEntity requestEntity) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if("https".equals(url.getProtocol())) {
			((HttpsURLConnection)conn).setSSLSocketFactory(createSSL());
		}
		conn.setRequestMethod(requestEntity.getMethod().name());
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

		String contentType = requestEntity.getContentType();
		if (contentType != null) {
			conn.setRequestProperty("Content-Type", requestEntity.getContentType());
		}
		conn.setConnectTimeout(conn.getConnectTimeout());
		conn.setReadTimeout(conn.getReadTimeout());
		if (requestEntity.getHeaders() != null) {
			for (Entry<String, String> entry : requestEntity.getHeaders().entrySet()) {
				conn.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		if (requestEntity.getBasicAuth() != null) {
			conn.setRequestProperty("Authorization", requestEntity.getBasicAuth().getEncodeBasicAuth());
		}
		return conn;
	}

	private static javax.net.ssl.SSLSocketFactory createSSL() {
		TrustManager[] tm = new TrustManager[] { myX509TrustManager };
		// TLSv1.1以及以上的需要JDK1.7以上
		try {
			SSLContext sslContext = SSLContext.getInstance("TLSv1.1");
			sslContext.init(null, tm, null);
			javax.net.ssl.SSLSocketFactory ssf = sslContext.getSocketFactory();
			return ssf;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static TrustManager myX509TrustManager = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
	};

}
