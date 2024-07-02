package com.nx.boot.waf.request;

import com.nx.boot.waf.WAFHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Request请求过滤包装
 * <br>
 * Web应用防护系统(也称:网站应用级入侵防御系统.英文:Web Application Firewall, 简称:WAF)
 */
public class WAFRequestWrapper extends HttpServletRequestWrapper {

    private boolean filterXSS = true;

    private boolean filterSQL = true;

    public WAFRequestWrapper(HttpServletRequest request, boolean filterXSS, boolean filterSQL) {
        super(request);
        this.filterXSS = filterXSS;
        this.filterSQL = filterSQL;
    }

    public WAFRequestWrapper(HttpServletRequest request) {
        this(request, true, true);
    }

    /**
     * @Description 数组参数过滤
     */
    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = filterParamString(values[i]);
        }

        return encodedValues;
    }

    /**
     * @Description 参数过滤
     */
    @Override
    public String getParameter(String parameter) {
        return filterParamString(super.getParameter(parameter));
    }

    /**
     * @Description 请求头过滤
     */
    @Override
    public String getHeader(String name) {
        return filterParamString(super.getHeader(name));
    }

    /**
     * @Description Cookie内容过滤
     */
    @Override
    public Cookie[] getCookies() {
        Cookie[] existingCookies = super.getCookies();
        if (existingCookies != null) {
            for (int i = 0; i < existingCookies.length; ++i) {
                Cookie cookie = existingCookies[i];
                cookie.setValue(filterParamString(cookie.getValue()));
            }
        }
        return existingCookies;
    }

    /**
     * @Description 过滤字符串内容
     */
    protected String filterParamString(String rawValue) {
        if (rawValue == null) {
            return null;
        }
        String tmpStr = rawValue;
        if (this.filterXSS) {
            tmpStr = WAFHelper.stripXSS(rawValue);
        }
        if (this.filterSQL) {
            tmpStr = WAFHelper.stripSqlInjection(tmpStr);
        }
        return tmpStr;
    }
}
