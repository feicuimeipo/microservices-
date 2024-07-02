package com.nx.elasticsearch.api;


import com.nx.elasticsearch.conf.EsProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.nx.elasticsearch.conf.EsProperties.DEFAULT_GROUP_NAME;

public class ESClientHelper {
    private static final char split = '|';
    private static final String schemeHttps = "https";
    private static final String schemeHttp = "http";
    private static final int CONNECT_TIME_OUT = 10000;
    private static final int SOCKET_TIME_OUT = 300000;
    private static final int CONNECTION_REQUEST_TIME_OUT = 2000;
    private static final int MAX_CONNECT_NUM = 100;
    private static final int MAX_CONNECT_PER_ROUTE = 100;
    private static final int MAX_RETRY_TIMEOUT_MILLIS = 300000;
    private static Set<String> hsLogs=new HashSet<>();
    private static final boolean IS_TEST = true;
    private static final String esnode8_host = "esnode8.cubees.com";
    private static final String esnode8_ip = "47.95.239.120";
    private static RestHighLevelClient restHighLevelClientHttps;
    private static RestHighLevelClient restHighLevelClientHttp;
    private static RestHighLevelClient restHighLevelClientHttpsRead;

    private ESClientHelper() {
    }

    private static void getESCluster(String groupName) {
        EsProperties properties = EsProperties.getEsProperties(groupName);
        String clusterName = properties.getClusterName();
        String ip = properties.getIp();// prop.getProperty("es.host");
        int port = properties.getPort();//prop.getPropertyWithInteger("es.urlport");
        int nodeport =properties.getNodeport();// prop.getPropertyWithInteger("es.nodeport");
        hsLogs = properties.getHsLogs();
        String protocol = ip.contains("test") ? "http" : "https";

        HttpHost[] hosts = getHosts(ip, port, protocol);
        String user =properties.getUser();
        String pass = properties.getPass();
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));

        SSLContext sslContext;
        try {
            sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, (chain, authType) -> {
                return true;
            }).build();
        } catch (Exception var12) {
            sslContext = null;
            var12.printStackTrace();
        }

        RestClientBuilder restClientBuilder = getRestClientBuilder(hosts, basicCredentialsProvider, sslContext);
        restHighLevelClientHttps = new RestHighLevelClient(restClientBuilder);
        System.out.println(Arrays.toString(hosts));
    }

    private static void getESCluster2(String groupName) {


        if (restHighLevelClientHttps != null && restHighLevelClientHttps.getLowLevelClient().getNodes().stream().map(Node::getHost).anyMatch((o) -> {
            return o.getHostName().contains("test");
        })) {
            restHighLevelClientHttp = restHighLevelClientHttps;
        } else {
            EsProperties prop = EsProperties.getEsProperties("log.es");
            String clusterName = prop.getClusterName();
            hsLogs = prop.getHsLogs();
            String ip = prop.getIp();// prop.getProperty("es.host");
            int port = prop.getPort();//prop.getPropertyWithInteger("es.urlport");
            int nodeport =prop.getNodeport();// prop.getPropertyWithInteger("es.nodeport");

            String protocol = ip.contains("test") ? "http" : "https";
            protocol = "http";
//            String clusterName = prop.getProperty("log.es.clusterName");
//            String ip = prop.getProperty("log.es.host");
//            int port = prop.getPropertyWithInteger("es.urlport");
//            int nodeport = prop.getPropertyWithInteger("es.nodeport");

            HttpHost[] hosts = getHosts(ip, port, protocol);
            RestClientBuilder restClientBuilder = getRestClientBuilder(hosts, (CredentialsProvider)null, (SSLContext)null);
            restHighLevelClientHttp = new RestHighLevelClient(restClientBuilder);
        }
    }

    public static boolean isTest() {
        return restHighLevelClientHttps != null && restHighLevelClientHttps.getLowLevelClient().getNodes().stream().map(Node::getHost).anyMatch((o) -> {
            return o.getHostName().contains("test");
        });
    }

    private static void getESClusterRead() {
        EsProperties prop = EsProperties.getEsProperties("read.es");
        String clusterName = prop.getClusterName();
        String ip = prop.getIp();// prop.getProperty("es.host");
        int port = prop.getPort();//prop.getPropertyWithInteger("es.urlport");
        int nodeport =prop.getNodeport();// prop.getPropertyWithInteger("es.nodeport");
        hsLogs = prop.getHsLogs();

        String s = ip.contains("test") ? "http" : "https";
        HttpHost[] hosts = getHosts(ip, port, s);
        String user = prop.getUser();
        String pass = prop.getPass();
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass));

        SSLContext sslContext;
        try {
            sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, (chain, authType) -> {
                return true;
            }).build();
        } catch (Exception var12) {
            sslContext = null;
            var12.printStackTrace();
        }

        RestClientBuilder restClientBuilder = getRestClientBuilder(hosts, basicCredentialsProvider, sslContext);
        restHighLevelClientHttpsRead = new RestHighLevelClient(restClientBuilder);
        System.out.println(Arrays.toString(hosts));
    }

    private static HttpHost[] getHosts(String ip, int port, String scheme) {
        return (HttpHost[])Arrays.stream(StringUtils.split(ip, '|')).map((o) -> {
            return getHttpHost(o, port, scheme);
        }).toArray((x$0) -> {
            return new HttpHost[x$0];
        });
    }

    private static Object getHttpHost(String name, int port, String scheme) {
        try {
            if (StringUtils.equals(name, "esnode8.cubees.com") && Arrays.stream(InetAddress.getAllByName(name)).anyMatch((o) -> {
                return StringUtils.equals(o.getHostAddress(), "47.95.239.120");
            })) {
                throw new UnknownHostException(name);
            } else {
                return new HttpHost(name, port, scheme);
            }
        } catch (Throwable var4) {
            throw new RuntimeException(var4);
        }
    }

    private static RestClientBuilder getRestClientBuilder(HttpHost[] hosts, CredentialsProvider basicCredentialsProvider, SSLContext sslContext) {
        RestClientBuilder builder = RestClient.builder(hosts);
        setConnectTimeOutConfig(builder);
        setMutiConnectConfig(builder, basicCredentialsProvider, sslContext);
        return builder;
    }

    private static void setConnectTimeOutConfig(RestClientBuilder builder) {
        builder.setRequestConfigCallback((o) -> {
            return o.setConnectTimeout(10000).setSocketTimeout(300000).setConnectionRequestTimeout(2000);
        });
    }

    private static void setMutiConnectConfig(RestClientBuilder builder, CredentialsProvider basicCredentialsProvider, SSLContext sslContext) {
        builder.setHttpClientConfigCallback((o) -> {
            o.setMaxConnTotal(100).setMaxConnPerRoute(100).setKeepAliveStrategy((o1, o2) -> {
                return TimeUnit.MINUTES.toMillis(1L);
            });
            if (basicCredentialsProvider != null) {
                o.setDefaultCredentialsProvider(basicCredentialsProvider);
            }

            if (sslContext != null) {
                o.setSSLContext(sslContext);
            }

            return o;
        });
    }

    public static boolean resetESClient(String groupName) {
        EsProperties prop = EsProperties.getEsProperties(groupName);
        if (prop == null) {
            closeHttps();
            getESCluster(DEFAULT_GROUP_NAME);
        } else {
            closeHttp();
            getESCluster2(groupName);
        }

        return true;
    }

    public static void closeHttps() {
        try {
            if (restHighLevelClientHttps != null) {
                restHighLevelClientHttps.close();
            }
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void closeHttp() {
        try {
            if (restHighLevelClientHttp != null) {
                restHighLevelClientHttp.close();
            }
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void closeHttpsRead() {
        try {
            if (restHighLevelClientHttpsRead != null) {
                restHighLevelClientHttpsRead.close();
            }
        } catch (IOException var1) {
            var1.printStackTrace();
        }

    }

    public static void close(RestHighLevelClient restHighLevelClient) {
    }

    public static final ESClientHelper getInstance() {
        return ESClientHelper.ClientHolder.INSTANCE;
    }

    public RestHighLevelClient getClient(String index) {
        return hsLogs.contains(index) ? restHighLevelClientHttp : restHighLevelClientHttps;
    }

    public RestHighLevelClient getClientRead(String index) {
        return hsLogs.contains(index) ? restHighLevelClientHttp : restHighLevelClientHttpsRead;
    }

    public void close(RestClient client) {
        try {
            client.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }


    private static class ClientHolder {
        private static final ESClientHelper INSTANCE = new ESClientHelper();

        private ClientHolder() {
        }
    }
}
