package com.nx.logger.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LogWebUtil {

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return (requestAttributes == null) ? null : ((ServletRequestAttributes)requestAttributes).getRequest();
    }

    public static String getClientIP(HttpServletRequest request) {
        LogOperatorUtil.notNull(request, "HttpServletRequest is null");
        String ip = request.getHeader("X-Requested-For");
        if (LogOperatorUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("X-Forwarded-For");}
        if (LogOperatorUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");}
        if (LogOperatorUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");}
        if (LogOperatorUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_CLIENT_IP");}
        if (LogOperatorUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");}
        if (LogOperatorUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();}
        return LogOperatorUtil.isBlank(ip) ? null : ip.split(",")[0];
    }

    public static String getPath(String uriStr) {
        URI uri;
        try {
            uri = new URI(uriStr);
        } catch (URISyntaxException var3) {
            throw new RuntimeException(var3);
        }
        return uri.getPath();
    }

    public static String getRequestParamString(HttpServletRequest request) {
        try {
            return getRequestStr(request);
        } catch (Exception ex) {
            return LogOperatorUtil.EMPTY;
        }
    }

    public static String getRequestStr(HttpServletRequest request) throws IOException {
        String queryString = request.getQueryString();
        if (LogOperatorUtil.isNotBlank(queryString))
            return (new String(queryString.getBytes(LogOperatorUtil.ISO_8859_1), LogOperatorUtil.UTF_8)).replaceAll("&amp;", "&").replaceAll("%22", "\"");
        return getRequestStr(request, getRequestBytes(request));
    }

    public static byte[] getRequestBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0)
            return null;
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1)
                break;
            i += readlen;
        }
        return buffer;
    }

    public static String getRequestStr(HttpServletRequest request, byte[] buffer) throws IOException {
        String charEncoding = request.getCharacterEncoding();
        if (charEncoding == null)
            charEncoding = LogOperatorUtil.UTF_8_NAME;
        String str = (new String(buffer, charEncoding)).trim();
        if (LogOperatorUtil.isBlank(str)) {
            StringBuilder sb = new StringBuilder();
            Enumeration<String> parameterNames = request.getParameterNames();
            while (parameterNames.hasMoreElements()) {
                String key = parameterNames.nextElement();
                String value = request.getParameter(key);
                appendBuilder(sb, new CharSequence[] { key, "=", value, "&" });
            }
            str = LogOperatorUtil.removeSuffix(sb.toString(), "&");
        }
        return str.replaceAll("&amp;", "&");
    }

    public static StringBuilder appendBuilder(StringBuilder stringBuilder, CharSequence... charSequences) {
        for (CharSequence str : charSequences)
            stringBuilder.append(str);
        return stringBuilder;
    }



    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }


//    public static String getDeviceType(HttpServletRequest request) {
//        // ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String agentString = request.getHeader("User-Agent");
//        UserAgent userAgent = UserAgent.parseUserAgentString(agentString);
//        OperatingSystem operatingSystem = userAgent.getOperatingSystem(); // 操作系统信息
//        eu.bitwalker.useragentutils.DeviceType deviceType = operatingSystem.getDeviceType(); // 设备类型
//
//        switch (deviceType) {
//            case COMPUTER:
//                return "PC";
//            case TABLET: {
//                if (agentString.contains("Android")) return "Android Pad";
//                if (agentString.contains("iOS")) return "iPad";
//                return "Unknown";
//            }
//            case MOBILE: {
//                if (agentString.contains("Android")) return "Android";
//                if (agentString.contains("iOS")) return "IOS";
//                return "Unknown";
//            }
//            default:
//                return "Unknown";
//        }
//
//    }

    public static boolean isJsonRequest(ServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

}
