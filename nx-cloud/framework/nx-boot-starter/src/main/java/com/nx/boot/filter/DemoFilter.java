package com.nx.boot.filter;

import cn.hutool.core.util.StrUtil;

import com.nx.boot.config.FilterAutoConfiguration;
import com.nx.boot.support.ServletUtils;
import com.nx.boot.support.WebFrameworkUtils;
import com.nx.common.banner.BannerUtils;
import com.nx.common.model.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.nx.common.model.constant.ResultCode.DEMO_DENY;


/**
 * 演示 Filter，禁止用户发起写操作，避免影响测试数据
 *
 * @author 芋道源码
 */
@WebFilter(filterName="demoFilter",urlPatterns="/**")
@ConditionalOnProperty(value = "nx.demo", havingValue = "true")
public class DemoFilter extends OncePerRequestFilter {

    public DemoFilter() {
        super();
        BannerUtils.push(this.getClass(),new String[]{"nx-boot-starter："+ this.getClass().getSimpleName() +" enabled"});
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        return !StrUtil.equalsAnyIgnoreCase(method, "POST", "PUT", "DELETE")  // 写操作时，不进行过滤率
                || WebFrameworkUtils.getLoginUserId(request) == null; // 非登录用户时，不进行过滤
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        // 直接返回 DEMO_DENY 的结果。即，请求不继续
        ServletUtils.writeJSON(response, Result.FAIL(DEMO_DENY));
    }

}
