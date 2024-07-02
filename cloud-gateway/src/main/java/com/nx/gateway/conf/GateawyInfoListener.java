package com.nx.gateway.conf;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @ClassName GateawyInfoListener
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/19 18:50
 * @Version 1.0
 **/
public class GateawyInfoListener implements ServletContextListener {
    /**
     * 服务启动
     * @param sce
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("ServletContextEvent initialized.");
//        ServletContext sc = sce.getServletContext();
//        sc.setAttribute("admin", "Krishna");
    }

    /**
     * 服务销毁
     * @param sce
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
//        sc.removeAttribute("admin");
//        System.out.println("ServletContextEvent destroyed.");
    }



}
