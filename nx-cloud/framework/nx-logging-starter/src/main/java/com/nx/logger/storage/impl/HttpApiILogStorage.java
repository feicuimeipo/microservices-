
package com.nx.logger.storage.impl;


import com.alibaba.fastjson2.JSONObject;
import com.nx.logger.config.NxLoggerConfig;
import com.nx.logger.exception.NxLoggerException;
import com.nx.logger.storage.ILogStorage;
import com.nx.logger.storage.NxLoggerModel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class HttpApiILogStorage implements ILogStorage {
    @Setter
    private NxLoggerConfig.HttpApi httpApiConfig;
    private static ILogStorage fileAppender;


    public HttpApiILogStorage(NxLoggerConfig.HttpApi httpApiConfig, FileAppenderLogsStorage fileAppender) {
        this.httpApiConfig = httpApiConfig;
        if (fileAppender==null){
            throw new NxLoggerException("fileAppender is null!");
        }
        this.fileAppender = fileAppender;
    }

    @Override
    public void writeLog(String content) {
        String secret = httpApiConfig.getSecret();
        String webHookToken =  httpApiConfig.getPostUrl()+ "?accessToken=" + httpApiConfig.getAccessToken();
        try {
            if (secret != null && secret.trim().length()>0) {
                Long timestamp = Long.valueOf(System.currentTimeMillis());
                String stringToSign = timestamp + "\n" + secret;
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
                byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
                String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
                webHookToken = webHookToken + "&timestamp=" + timestamp + "&sign=" + sign;
            }
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }


        String finalWebHookToken = webHookToken;
        new Runnable(){
            @Override
            public void run() {
                try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                    HttpPost httppost = new HttpPost(finalWebHookToken);
                    httppost.addHeader("Content-Type", "application/json; charset=utf-8");
                    StringEntity se = new StringEntity(content, "utf-8");
                    httppost.setEntity((HttpEntity)se);
                    CloseableHttpResponse closeableHttpResponse = httpclient.execute((HttpUriRequest)httppost);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {}
            }
        };
    }



    @Override
    public void writeLog(NxLoggerModel createDTO){
        String content = JSONObject.toJSONString(createDTO);

        if (fileAppender!=null){
            fileAppender.writeLog(createDTO);
        }
        writeLog(content);
    }



}
