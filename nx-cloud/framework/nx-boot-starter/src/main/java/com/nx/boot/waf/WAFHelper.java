package com.nx.boot.waf;


import com.nx.boot.waf.attack.SqlInjection;
import com.nx.boot.waf.attack.XSS;

/**
 * Web防火墙工具类
 * <br>
 * Web应用防护系统(也称:网站应用级入侵防御系统.英文:Web Application Firewall, 简称:WAF)
 */
public class WAFHelper {

    /**
     * @Description 过滤XSS脚本内容
     */
    public static String stripXSS(String value) {
        if (value == null) {
            return null;
        }
        return new XSS().strip(value);
    }

    /**
     * @Description 过滤SQL注入内容
     */
    public static String stripSqlInjection(String value) {
        if (value == null) {
            return null;
        }
        return new SqlInjection().strip(value);
    }

    /**
     * @Description 过滤SQL/XSS注入内容
     */
    public static String stripSqlXSS(String value) {
        if (value == null) {
            return null;
        }
        return stripXSS(stripSqlInjection(value));
    }

}
