package com.nx.gateway.route.core;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;

import java.sql.SQLException;

/**
 * @ClassName H2DBServerStartListener
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/19 18:47
 * @Version 1.0
 **/
@Slf4j
public class H2DBServerListener {
    //H2数据库服务器启动实例
    private static Server server;
    /*
     * Web应用初始化时启动H2数据库
     */
    public static synchronized void start() {
        if (server==null) {
            try {
                log.info("正在启动h2数据库...");
                //使用org.h2.tools.Server这个类创建一个H2数据库的服务并启动服务，由于没有指定任何参数，那么H2数据库启动时默认占用的端口就是8082
                //"-tcpAllowOthers","-webAllowOthers"
                //默认：9092
                server = Server.createTcpServer("-tcpAllowOthers","-webAllowOthers").start();
                log.info("h2数据库启动成功...");
            } catch (SQLException e) {;
                log.error("启动h2数据库出错："+e.getMessage(),e);
                throw new RuntimeException(e);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    /*
     * Web应用销毁时停止H2数据库
     */
    public static void stop() {
        try {
            if (server != null) {
                // 停止H2数据库
                server.stop();
                server = null;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}
