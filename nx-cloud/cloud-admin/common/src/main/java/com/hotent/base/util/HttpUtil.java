/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.util;

import com.pharmcube.api.model.crypt.trust.MyX509TrustManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;


public class HttpUtil {
	
	// \b 是单词边界(连着的两个(字母字符 与 非字母字符) 之间的逻辑上的间隔),    
    // 字符串在编译时会被转码一次,所以是 "\\b"    
    // \B 是单词内部逻辑间隔(连着的两个字母字符之间的逻辑上的间隔)    
   private static final String phoneReg = "\\b(ip(hone|od)|android|opera m(ob|in)i"    
            +"|windows (phone|ce)|blackberry"    
            +"|s(ymbian|eries60|amsung)|p(laybook|alm|rofile/midp"    
            +"|laystation portable)|nokia|fennec|htc[-_]"    
            +"|mobile|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";    

   private static final String tabletReg = "\\b(ipad|tablet|(Nexus 7)|up.browser|[1-4][0-9]{2}x[1-4][0-9]{2})\\b";    

  //移动设备正则匹配：手机端、平板  
   private static Pattern phonePat = Pattern.compile(phoneReg, Pattern.CASE_INSENSITIVE);    
   private static Pattern tabletPat = Pattern.compile(tabletReg, Pattern.CASE_INSENSITIVE);
   
	/**
	 * 获取当前请求的request对象
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
	 * 获取当前请求的参数
	 * @param name
	 * @return
	 */
	public static String getRequestParameter(String name) {
		HttpServletRequest request = getRequest();
		if(request==null) {
			return null;
		}
		return request.getParameter(name);
	}
	

	

	

	
//	/**
//	 * 下载文件。
//	 *
//	 * @param response
//	 * @param fullPath
//	 *            下载文件路径
//	 * @param fileName
//	 *            下载文件名
//	 * @throws IOException
//	 *             void
//	 */
//	public static void downLoadFile(HttpServletResponse response, String fullPath, String fileName) throws IOException {
//		OutputStream outp = response.getOutputStream();
//		File file = new File(fullPath);
//		if (file.exists()) {
//			response.setContentType("application/x-download");
//			response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
//			if (System.getProperty("file.encoding").equals("GBK")) {
//				response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
//			} else {
//				response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//			}
//			try (FileInputStream in = new FileInputStream(fullPath);){
//				IOUtils.copy(in, outp);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (outp != null) {
//					outp.close();
//					outp = null;
//					response.flushBuffer();
//				}
//			}
//		} else {
//			outp.write("文件不存在!".getBytes("utf-8"));
//		}
//	}

//	/**
//	 * <pre>
//	 * 压缩多个文件到一个zip下然后提供到页面下载
//	 * 目前常用于导出xml
//	 * 里面进行了1：写一个临时文件；2：打包；3：导出打包好的文件；4：删除临时文件
//	 * </pre>
//	 *
//	 * @param request
//	 * @param response
//	 * @param fileContentMap
//	 *            ：{a:a的内容,b:b的内容,...}
//	 * @param zipName
//	 *            :压缩包的名字
//	 * @throws Exception
//	 *             void
//	 * @exception
//	 * @since 1.0.0
//	 */
//	public static void downLoadFile(HttpServletRequest request, HttpServletResponse response, Map<String, String> fileContentMap, String zipName) throws Exception {
//		String zipPath = (FileUtil.getIoTmpdir() + "attachFiles/tempZip/" + zipName).replace("/", File.separator);
//		String folderPath = (FileUtil.getIoTmpdir() + "attachFiles/tempZip/" + zipName+"/").replace("/", File.separator);
//		//建立临时文件夹，存放文件
//		File folder=new File(folderPath);
//		if(!folder.exists()) {
//			folder.mkdirs();
//		}
//
//		for (Map.Entry<String, String> ent : fileContentMap.entrySet()) {
//			String fileName = ent.getKey();
//			String content = ent.getValue();
//
//			String filePath = zipPath + File.separator + fileName;
//			FileUtil.writeFile(filePath, content);
//		}
//		// 打包
//		ZipUtil.zip(zipPath, true);
//		// 导出
//		HttpUtil.downLoadFile(response, zipPath + ".zip", zipName + ".zip");
//		// 删除导出的文件
//		FileUtil.deleteFile(zipPath + ".zip");
//	}

	/**
	 * <pre>
	 * 压缩一个文件到压缩包下然后提供到页面下载
	 * 目前常用于导出xml
	 * 里面进行了1：写一个临时文件；2：打包；3：导出打包好的文件；4：删除临时文件
	 * </pre>
	 * 
	 * @param request
	 * @param response
	 * @param content  :要导出的文本
	 * @param fileName ：文件名称
	 * @param zipName ：压缩包名称
	 * @throws Exception
	 *             void
	 */
//	public static void downLoadFile(HttpServletRequest request, HttpServletResponse response, String content, String fileName, String zipName) throws Exception {
//		Map<String, String> fileContentMap = new HashMap<String, String>();
//		fileContentMap.put(fileName, content);
//		downLoadFile(request, response, fileContentMap, zipName);
//	}



	/**
	 * 发送请求。
	 * 
	 * @param url
	 *            URL地址
	 * @param params
	 *            发送参数
	 * @param requestMethod
	 *            GET,POST
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws IOException
	 */
	public static String sendHttpsRequest(String url, String params,
			String requestMethod) {
		HttpsURLConnection conn;
		String str = null;
		try {
			conn = getHttpsConnection(url);
			conn.setRequestMethod(requestMethod);

			conn.setDoInput(true);
			conn.setDoOutput(true);

			if (StringUtils.isNotEmpty(params)) {
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(params.getBytes("utf-8"));
				outputStream.close();
			}
			str = getOutPut(conn);
		} catch (KeyManagementException e) {
			throw new RuntimeException("远程服务器请求失败！"+e.getMessage(),e);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("远程服务器请求失败！"+e.getMessage(),e);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("远程服务器请求失败！"+e.getMessage(),e);
		} catch (IOException e) {
			throw new RuntimeException("远程服务器请求失败！"+e.getMessage(),e);
		}

		return str;
	}

	/**
	 * 获取https连接。
	 * @param accessUrl
	 * @return
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws IOException
	 */
	public static HttpsURLConnection getHttpsConnection(String accessUrl)
			throws KeyManagementException, NoSuchAlgorithmException,
			NoSuchProviderException, IOException {
		URL url = new URL(accessUrl);
		HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();

		TrustManager[] tm = { new MyX509TrustManager() };

		SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
		sslContext.init(null, tm, new java.security.SecureRandom());
		SSLSocketFactory ssf = sslContext.getSocketFactory();
		connection.setSSLSocketFactory(ssf);
		return connection;
	}


	/**
	 * 读取返回数据。
	 * 
	 * @param conn
	 * @return
	 * @throws IOException
	 */
	public static String getOutPut(HttpsURLConnection conn) throws IOException {
		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(
				inputStream, "utf-8");
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		StringBuffer buffer = new StringBuffer();
		String str = null;
		while ((str = bufferedReader.readLine()) != null) {
			buffer.append(str);
		}
		bufferedReader.close();
		inputStreamReader.close();
		inputStream.close();
		conn.disconnect();
		return buffer.toString();
	}


	/**
	 * 根据url取得数据，支持gzip类网站
	 * @param url
	 * @param charset
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String getContentByUrl(String url,String charset) throws  IOException{
		HttpClient httpclient = HttpClientBuilder.create().build();////获取DefaultHttpClient请求
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);


		if(StringUtils.isEmpty(charset)){
			String defaultCharset="iso-8859-1";
			Header contentTypeHeader=response.getFirstHeader("Content-Type");
			String contentType=contentTypeHeader.getValue().toLowerCase();
			if(contentType.indexOf("gbk")>-1 || contentType.indexOf("gb2312") >-1 || contentType.indexOf("gb18030")>-1){
				defaultCharset="gb18030";
			}
			else if(contentType.indexOf("utf-8")>-1){
				defaultCharset="utf-8";
			}
			else if(contentType.indexOf("big5")>-1){
				defaultCharset="big5";
			}
			charset=defaultCharset;
		}
		Header contentEncoding=response.getFirstHeader("Content-Encoding");
		StatusLine line=response.getStatusLine();
		if(line.getStatusCode()==200){
			HttpEntity entity = response.getEntity();
			InputStream is=null;
			if(contentEncoding!=null && contentEncoding.getValue().toLowerCase().equals("gzip")){
				is=new GZIPInputStream( entity.getContent());
			}
			else{
				is=entity.getContent();
			}
			String str= inputStream2String(is, charset);
			is.close();
			return str;

		}
		return "";
	}

	public static String inputStream2String(InputStream input, String charset) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(input, charset));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = in.readLine()) != null) {
			buffer.append(line + "\n");
		}
		return buffer.toString();

	}

	public static String sendData(String url,String data){
		return sendData( url, data, "utf-8");
	}

	/**
	 * 发送数据到指定的URL并读取返回结果。
	 * @param url
	 * @param data
	 * @return
	 */
	public static String sendData(String url,String data,String charset){
		URL uRL;
		URLConnection conn;

		BufferedReader bufferedReader = null;
		try {
			uRL = new URL(url);
			conn = uRL.openConnection();
			conn.setDoOutput(true);
			if(StringUtils.isNotEmpty(data)){
				OutputStream stream=conn.getOutputStream();
				stream.write(data.getBytes(charset));
				stream.flush();
				stream.close();
			}


			// Get the response
			bufferedReader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				response.append(line);
			}

			bufferedReader.close();

			return response.toString();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/** 
     * 检测是否是移动设备访问 
     * 
     * @param request 浏览器标识
     * @return true:移动设备接入，false:pc端接入 
     */  
    public static boolean isMobile(HttpServletRequest request){
    	String userAgent = request.getHeader("user-agent");
        if(null == userAgent){    
            userAgent = "";    
        }   
        return phonePat.matcher(userAgent).find() || tabletPat.matcher(userAgent).find(); 
    }
}
