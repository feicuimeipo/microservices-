package com.nx.boot.launch.spimpl;


import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.auto.service.AutoService;
import com.nx.boot.launch.env.NxBootstrap;
import com.nx.boot.launch.spi.NxBootProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import static com.nx.boot.launch.env.NxBootstrap.NX_PROPERTY_PREFIX;

/**
 * @ClassName BootProcessorListenerImpl
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/7 1:14
 * @Version 1.0
 **/
@Slf4j
@AutoService(NxBootProcessor.class)
public class NacosNxBootProcessorImpl implements NxBootProcessor {

    ReentrantLock lock = new ReentrantLock();

    @Override
    public void launcher(String applicationName, NxBootstrap bootstrap, Properties props, Class mainClass) {
        if (bootstrap.isConfigEnabled() || bootstrap.isDiscoveryEnabled()){
            props.setProperty("spring.cloud.nacos.username",  bootstrap.getCloudServerUserName());
            props.setProperty("spring.cloud.nacos.password",  bootstrap.getCloudServerPassword());
        }
        //单例模式
        if (!bootstrap.isConfigEnabled()){
            props.setProperty("spring.cloud.nacos.config.enabled",  "false");
        }else{
            props.setProperty("spring.cloud.nacos.config.enabled",  "true");
            //props.setProperty("spring.cloud.nacos.config.refresh-enabled",  "true");
            props.setProperty("spring.cloud.nacos.config.server-addr", bootstrap.getCloudServerAddr());
            props.setProperty("spring.cloud.nacos.config.prefix", bootstrap.getConfigPrefix());
            props.setProperty("spring.cloud.nacos.config.file-extension", bootstrap.getConfigExentsion());
            props.setProperty("spring.cloud.nacos.config.namespace", bootstrap.getCloudNamespace());
            props.setProperty("spring.cloud.nacos.config.group", bootstrap.getCloudGroup());
        }

        if (!bootstrap.isDiscoveryEnabled()){
            props.setProperty("spring.cloud.nacos.discovery.enabled", "false");
        }else{
            props.setProperty("spring.cloud.nacos.discovery.enabled",  "true");
            //props.setProperty("spring.cloud.nacos.discovery.refresh-enabled",  "true");
            props.setProperty("spring.cloud.nacos.discovery.server-addr", bootstrap.getCloudServerAddr());
            props.setProperty("spring.cloud.nacos.discovery.namespace", bootstrap.getCloudNamespace());
            props.setProperty("spring.cloud.nacos.discovery.service", applicationName);
            props.setProperty("spring.cloud.nacos.discovery.group", bootstrap.getCloudGroup());
            props.setProperty("spring.cloud.nacos.discovery.watch.enabled", "true");
            props.setProperty("ribbon.nacos.enabled","true");
        }


        if ((bootstrap.isConfigEnabled() || bootstrap.isDiscoveryEnabled()) && StringUtils.hasLength(bootstrap.getCloudServerAddr()) && !bootstrap.isProd()){

            try{
                Class.forName("com.alibaba.nacos.api.config.ConfigService");
            }catch (ClassNotFoundException e){
                return;
            }


            try{
                lock.lock();

                String serverUrl = bootstrap.getCloudServerAddr();
                String url = "http://" + serverUrl + "/nacos/v1/console/namespaces";

                NacosNxBootConfig.syncNacosNamespace(url,bootstrap.getCloudNamespace());

                NacosNxBootConfig.syncNacosConfigService(bootstrap, applicationName);

            } catch (Exception e) {
            //} catch (JSONException | IOException | com.alibaba.nacos.api.exception.NacosException e) {
                //com.alibaba.nacos.api.exception.NacosException
                e.printStackTrace();
            } finally {
                lock.unlock();
            }


        }


    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE+2;
    }


    static class NacosNxBootConfig {

        private NacosNxBootConfig(){}
        private static boolean existNamespace(String uri,String namespace) throws IOException, JSONException {
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response = null;

            try {
                response = HttpClientBuilder.create().build().execute(httpGet);

                String resBody  = EntityUtils.toString(response.getEntity());
                JSONObject responseBody = new JSONObject(resBody);
                if (responseBody!=null && responseBody.get("code")!=null && (responseBody.get("code")=="200" || responseBody.get("code")=="0")){
                    JSONArray jsonArray = (JSONArray) responseBody.get("data");
                    for (int i=0;(jsonArray!=null && i< jsonArray.length());i++){
                        JSONObject json = (JSONObject) jsonArray.get(i);
                        if (json!=null && json.get("namespace")!=null && json.get("namespace").equals(namespace)){
                            return true;
                        }
                    }
                }
            }finally {
                try {
                    response.close();
                }catch (Exception e){

                }
            }
            return false;
        }


        protected static void syncNacosNamespace(String url,String namespace) throws IOException, JSONException {

            if (!existNamespace(url, namespace)) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("customNamespaceId", namespace));
                pairs.add(new BasicNameValuePair("namespaceName", namespace));
                pairs.add(new BasicNameValuePair("namespaceDesc", namespace));

                CloseableHttpClient httpClient=null;
                CloseableHttpResponse response =null;

                try {
                    HttpPost httpPost = new HttpPost(url);

                    UrlEncodedFormEntity requestEntity = new UrlEncodedFormEntity(pairs);
                    httpPost.setEntity(requestEntity);

                    httpClient = HttpClientBuilder.create().build();
                    response = httpClient.execute(httpPost);

                    String result = EntityUtils.toString(response.getEntity());
                    log.info("result-result-result-result:::" + result);
                } finally {
                    try{if (response!=null){response.close();}}catch (Exception e){};
                    try{if (httpClient!=null){httpClient.close();}}catch (Exception e){};
                }
            }
        }


        protected static void syncNacosConfigService(NxBootstrap bootstrap, String applicationName) throws NacosException {
            try{
                Class.forName("com.alibaba.nacos.api.config.ConfigService");
            }catch (Exception e){
                return;
            }
            if (!(bootstrap.isConfigEnabled()) || bootstrap.isDiscoveryEnabled() || !StringUtils.hasLength(bootstrap.getCloudServerAddr())){
                return;
            }


            Properties properties = new Properties();
            if (StringUtils.hasLength(bootstrap.getCloudNamespace())) {
                properties.put(PropertyKeyConst.NAMESPACE, bootstrap.getCloudNamespace());
            }
            properties.put(PropertyKeyConst.SERVER_ADDR, bootstrap.getCloudServerAddr());
            properties.put(PropertyKeyConst.USERNAME,bootstrap.getCloudServerUserName());
            properties.put(PropertyKeyConst.PASSWORD, bootstrap.getCloudServerPassword());

            ConfigService configService = ConfigFactory.createConfigService(properties);

            String dataId = bootstrap.getConfigPrefix();
            String groupId = bootstrap.getCloudGroup();

            String ext = bootstrap.getConfigExentsion();
            if (ext.startsWith(".")) {
                ext = ext.substring(1);
            }
            ConfigType configType = ConfigType.valueOf(ext.toUpperCase());
            if (configType == null) {
                configType = ConfigType.YAML;
            }

            String rules = configService.getConfig(dataId, groupId, 3000);
            if (!StringUtils.hasLength(rules)) {
                String content = String.format(NX_PROPERTY_PREFIX.toUpperCase() + ": \n  welcome: You are welcome!");
                configService.publishConfig(dataId, groupId, content, configType.getType());
            }

        }



    }



}
