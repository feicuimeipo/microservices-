package com.nx.datasource.core.filter;

import com.alibaba.druid.util.Utils;


import javax.servlet.*;

import java.io.IOException;

/**
 *
 * OncePerRequestFilter vs Filter
 * Druid 底部广告过滤器
 *
 * @author 芋道源码
 */
public class DruidAdRemoveFilter implements Filter {

    /**
     * common.js 的路径
     */
    private static final String COMMON_JS_ILE_PATH = "support/http/resources/js/common.js";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
        // 重置缓冲区，响应头不会被重置
        response.resetBuffer();
        // 获取 common.js
        String text = Utils.readFromResource(COMMON_JS_ILE_PATH);
        // 正则替换 banner, 除去底部的广告信息
        text = text.replaceAll("<a.*?banner\"></a><br/>", "");
        text = text.replaceAll("powered.*?shrek.wang</a>", "");
        response.getWriter().write(text);
    }
}
