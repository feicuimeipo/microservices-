package com.nx.elasticsearch.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送Http请求工具类
 */
public class EsHttpUtils {

    /**
     * 默认编码
     */
    public static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * 最大连接数
     */
    public final static int MAX_TOTAL_CONNECTIONS = 800;
    /**
     * 每个路由最大连接数
     */
    public final static int MAX_ROUTE_CONNECTIONS = 400;
    /**
     * 连接超时时间
     */
    public final static int CONNECT_TIMEOUT = 10000;
    /**
     * 读取超时时间
     */
    public final static int READ_TIMEOUT = 10000;
    private static final String JSON_SUFFIX = ".json";
    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR",
            "X-Real-IP"};
    private static Logger logger = LoggerFactory.getLogger(EsHttpUtils.class);
    private static HttpParams httpParams;
    private static PoolingClientConnectionManager connectionManager;
    private static HttpClient client;

    static {
        httpParams = new BasicHttpParams();
        // 设置连接超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
        // 设置读取超时时间
        HttpConnectionParams.setSoTimeout(httpParams, READ_TIMEOUT);

        connectionManager = new PoolingClientConnectionManager();
        // 设置最大连接数
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        // 设置每个路由最大连接数
        connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        client = new DefaultHttpClient(connectionManager, httpParams);
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            // set up a TrustManager that trusts everything

            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }}, new SecureRandom());
            SSLSocketFactory ssf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", 443, ssf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String post(String url, String content) throws ClientProtocolException, IOException {
        HttpEntity httpEntity = new StringEntity(content, DEFAULT_ENCODING);
        return post(url, httpEntity, DEFAULT_ENCODING);
    }

    public static String post(String url, Map<String, Object> data, String encoding) throws ClientProtocolException, IOException {
        List<NameValuePair> list = new ArrayList<>();
        for (Map.Entry<String, Object> me : data.entrySet()) {
            if (me.getValue() != null) {
                list.add(new BasicNameValuePair(me.getKey(), me.getValue().toString()));
            }
        }
        return post(url, new UrlEncodedFormEntity(list, DEFAULT_ENCODING), encoding);
    }

    public static String post(String url, HttpEntity httpEntity, String encoding) throws ClientProtocolException, IOException {
        HttpPost postMethod = new HttpPost(url);
        postMethod.setEntity(httpEntity);
        // 发送请求
        try {
            HttpResponse httpResponse = client.execute(postMethod);
            return EntityUtils.toString(httpResponse.getEntity(), encoding);// 取出应答字符串
        } finally {
            postMethod.releaseConnection();
        }
    }

    public static HttpResponse post(String url, Map<String, String> headers, Map<String, String> querys, String body) throws Exception {
        HttpPost request = new HttpPost(buildUrl(url, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            request.addHeader(e.getKey(), e.getValue());
        }

        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }
        client.getParams().setIntParameter("http.socket.timeout", 30000);
        return client.execute(request);
    }

    public static String post(String url, Map<String, String> headers, Map<String, String> querys, String body, Integer retryTimes) throws Exception {
        HttpPost request = new HttpPost(buildUrl(url, querys));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (StringUtils.isNotBlank(body)) {
            request.setEntity(new StringEntity(body, "utf-8"));
        }
        try {
            int count = 1;
            boolean flag;
            do {
                try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request)) {
                    flag = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                    if (flag) {
                        return EntityUtils.toString(response.getEntity(), "UTF-8");
                    }
                }
                count++;
            } while (retryTimes != null && count <= retryTimes);
            throw new RuntimeException("访问POST请求异常");
        } finally {
            request.releaseConnection();
        }
    }

    public static boolean postAsJson(String url, String body) {
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        StringEntity se = new StringEntity(body, "utf-8");
        httppost.setEntity(se);
        HttpResponse response = null;
        try {
            response = client.execute(httppost);
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
        } catch (IOException e) {
            return false;
        }
    }


    public static String put(String url, Map<String, String> headers, Map<String, String> querys, String body, Integer retryTimes) throws Exception {
        HttpPut request = new HttpPut(buildUrl(url, querys));
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        if (StringUtils.isNotBlank(body)) {
            request.setHeader("Content-Type", "application/json");
            StringEntity s = new StringEntity(body, "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(s);
        }
        try {
            int count = 1;
            boolean flag;
            do {
                try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request)) {
                    flag = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                    if (flag) {
                        return EntityUtils.toString(response.getEntity(), "UTF-8");
                    }
                }
                count++;
            } while (retryTimes != null && count <= retryTimes);
            throw new RuntimeException("访问PUT请求异常");
        } finally {
            request.releaseConnection();
        }
    }

    public static String get(String url, Map<String, String> headers, Map<String, String> querys, Integer retryTimes) throws Exception {
        HttpGet request = new HttpGet(buildUrl(url, querys));
        if(headers!=null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                request.addHeader(e.getKey(), e.getValue());
            }
        }
        try {
            int count = 1;
            boolean flag;
            do {
                try (CloseableHttpResponse response = (CloseableHttpResponse) client.execute(request)) {
                    flag = response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                    if (flag) {
                        return EntityUtils.toString(response.getEntity(), "UTF-8");
                    }
                }
                count++;
            } while (retryTimes != null && count <= retryTimes);
            throw new RuntimeException("访问GET请求异常");
        } finally {
            request.releaseConnection();
        }
    }

    public static String getValue(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    public static String post(String url, String data, File file) {
        String result = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            builder.setCharset(StandardCharsets.UTF_8).addBinaryBody("media", new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getName());
            Map<String, Object> map = JSONObject.parseObject(data);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.addTextBody(getValue(entry.getKey()), getValue(entry.getValue()));
            }
            HttpEntity httpEntity = builder.build();
            httpPost.setEntity(httpEntity);
            HttpResponse httpResponse = client.execute(httpPost);
            HttpEntity responseEntity = httpResponse.getEntity();
            if (responseEntity != null) { // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
                logger.info("上传文件返回结果:{}", result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 调用 python 接口 post 请求
     *
     * @param url
     * @param list
     * @return
     */
    public static JSONObject post(String url, List<NameValuePair> list) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(url);
            //url格式编码
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(list, "UTF-8");
            post.setEntity(uefEntity);
            //执行请求
            try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    return JSON.parseObject(stream2Json(entity.getContent()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 得到请求的根目录
     *
     * @param request
     * @return
     */
    public static String getBasePath(HttpServletRequest request) {
        String path = request.getContextPath();
        return request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + path;
    }

    /**
     * 得到结构目录
     *
     * @param request
     * @return
     */
    public static String getContextPath(HttpServletRequest request) {
        return request.getContextPath();
    }


    /**
     * @param ip           目标ip,一般在局域网内
     * @param sourceString 命令处理的结果字符串
     * @param macSeparator mac分隔符号
     * @return mac地址，用上面的分隔符号表示
     */

    private static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {
        String result = "";
        String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            result = matcher.group(1);
            if (sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
                break; // 如果有多个IP,只匹配本IP对应的Mac.
            }
        }
        return result;
    }

    /**
     * @param ip 目标ip
     * @return Mac Address
     */

    private static String getMacInWindows(final String ip) {
        String result = "";
        String[] cmd = {"cmd", "/c", "ping " + ip};
        String[] another = {"cmd", "/c", "arp -a"};
        String cmdResult = callCmd(cmd, another);
        result = filterMacAddress(ip, cmdResult, "-");
        return result;
    }

    /**
     * @param ip 目标ip
     * @return Mac Address
     */
    private static String getMacInLinux(final String ip) {
        String result = "";
        String[] cmd = {"/bin/sh", "-c", "ping " + ip + " -c 2 && arp -a"};
        String cmdResult = callCmd(cmd);
        result = filterMacAddress(ip, cmdResult, ":");
        return result;
    }


    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * @param cmd     第一个命令
     * @param another 第二个命令
     * @return 第二个命令的执行结果
     */
    private static String callCmd(String[] cmd, String[] another) {
        StringBuilder result = new StringBuilder();
        String line;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd);
            proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
            proc = rt.exec(another);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 命令获取mac地址
     *
     * @param cmd
     * @return
     */
    private static String callCmd(String[] cmd) {
        StringBuilder result = new StringBuilder();
        String line;
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static String getUri(HttpServletRequest request) throws UnsupportedEncodingException {
        String requestUri = request.getRequestURI();
        Map<String, String[]> params = request.getParameterMap();
        String queryString = "";
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                queryString += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
            }
        }
        if (!"".equals(queryString)) {
            requestUri += "?" + queryString.substring(0, queryString.length() - 1);
        }
        return requestUri;
    }

    private static String stream2Json(InputStream inputStream) {
        String jsonStr = "";
        // ByteArrayOutputStream相当于内存输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        // 将输入流转移到内存输出流中
        try {
            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, len);
            }
            // 将内存流转换为字符串
            jsonStr = new String(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    public static String buildUrl(String url, Map<String, String> querys) throws UnsupportedEncodingException {
        StringBuilder sbUrl = new StringBuilder();
        sbUrl.append(url);
        if (null != querys) {
            StringBuilder sbQuery = new StringBuilder();
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (0 < sbQuery.length()) {
                    sbQuery.append("&");
                }
                if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
                    sbQuery.append(query.getValue());
                }
                if (!StringUtils.isBlank(query.getKey())) {
                    sbQuery.append(query.getKey());
                    if (!StringUtils.isBlank(query.getValue())) {
                        sbQuery.append("=");
                        sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
                    }
                }
            }
            if (0 < sbQuery.length()) {
                sbUrl.append("?").append(sbQuery);
            }
        }
        return sbUrl.toString();
    }


    /**
     * //todo：非通用方法找时间移出去
     **/
    @Deprecated
    public static JSONArray httpPostJson(String url, String json) {
        return httpPostJson(url, json, "cubelin_java", "cubelin666abc_java.qwe.");
    }

    @Deprecated
    public static JSONArray httpPostJson(String url, String json, String username, String password) {


        //第一步：创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URL aUrl = null;
        try {
            aUrl = new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
        HttpHost targetHost = new HttpHost(aUrl.getHost(), aUrl.getPort(), "http");
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(targetHost.getHostName(),
                targetHost.getPort()), new UsernamePasswordCredentials(
                username, password));

        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);
        //第二步：创建httpPost对象
        HttpPost httpPost = new HttpPost(url);
        String s = "";
        try {
            //第三步：给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json, "utf-8");
            httpPost.setEntity(requestEntity);

            //第四步：发送HttpPost请求，获取返回值
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost, context);
            try {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    s = stream2Json(entity.getContent());
                    logger.info(s);
                    if (StringUtils.isNotEmpty(s)) {
                        s = s.replaceAll("\\\\", "|");
                    }
                    return resultHandler(url, json, s);
                }
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //发邮件
            String content = "报错信息：" + s + "<br/>接口：" + url + "<br/>参数：" + json;
            logger.error(content);
        }
        return null;
    }


    /**
     * //todo：非通用方法找时间移出去
     **/
    @Deprecated
    private static JSONArray resultHandler(String url, String json, String s) {
        JSONArray jsonArr = JSON.parseArray(s);
        if (jsonArr != null && jsonArr.size() > 0) {
            JSONObject jsonObj = jsonArr.getJSONObject(0);
            if (jsonObj != null) {
                String msg_error = jsonObj.getString("msg_error");
                if (StringUtils.isNotEmpty(msg_error)) {
                    //发邮件
                    String content = "报错信息：" + msg_error + "<br/>接口：" + url + "<br/>参数：" + json;
                    logger.error(content);
                } else {
                    return jsonArr;
                }
            }
        }
        return null;
    }

    /**
     * //todo：非通用方法找时间移出去
     **/
    @Deprecated
    public static JSONObject postAsJson(String url, File file, List<String> esidList) {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            FileBody fileBody = new FileBody(file);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addPart("file", fileBody);//file 为请求后台的普通参数;属性
            HttpEntity httpEntity = multipartEntityBuilder.build();

            HttpPost post = new HttpPost(url);
            post.setEntity(httpEntity);
            RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(30 * 1000)
                    .setSocketTimeout(8 * 60 * 1000).setConnectTimeout(30 * 1000).build();
            post.setConfig(config);
            //执行请求
            try (CloseableHttpResponse httpResponse = client.execute(post)) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    return JSON.parseObject(stream2Json(entity.getContent()));
                }
            }
        } catch (Exception e) {
            //发邮件
            String content = "接口：" + url + "<br/>报错信息：" + e.getMessage() + "<br/>参数信息：" + StringUtils.join(esidList, ",");
            logger.error(content);
        }
        return null;
    }

    /*
     * @Description: 判断请求是否为ajax请求
     * @Param: request
     * @Return: boolean
     **/
    public static boolean isAjax(HttpServletRequest request) {
        return (request.getServletPath().endsWith(JSON_SUFFIX))
                || request.getHeader("accept").contains("application/json")
                || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").contains("XMLHttpRequest"));
    }
}
