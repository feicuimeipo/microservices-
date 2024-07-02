package com.nx.boot.filter;


import com.nx.boot.waf.request.WAFRequestWrapper;
import com.nx.common.banner.BannerUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * 防SQL注入与XSS攻击
 * waf && filter
 */
@WebFilter({"/*"})
@ConditionalOnProperty(prefix = "nx.waf",value = {"enabled"},havingValue = "true",matchIfMissing = true)
public class WafXssFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter："+ this.getClass().getSimpleName() +" enabled"});
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new WAFRequestWrapper((HttpServletRequest) servletRequest), servletResponse);
    }

    @Override
    public void destroy() {

    }

}
