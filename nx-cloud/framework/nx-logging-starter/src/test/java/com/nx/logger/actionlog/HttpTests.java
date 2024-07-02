package com.nx.logger.actionlog;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 * http状态测试
 * @ClassName HttpTests
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/5/31 21:13
 * @Version 1.0
 **/
public class HttpTests {
   // @Test
    void respondsWith204() throws Exception {
        String host = System.getProperty("http.server.host","localhost");
        String port = System.getProperty("http.server.port","8080");
        URL url = new URL("http://" + host + ":" + port + "/test");


        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();

        assertEquals(204, responseCode);
    }

}
