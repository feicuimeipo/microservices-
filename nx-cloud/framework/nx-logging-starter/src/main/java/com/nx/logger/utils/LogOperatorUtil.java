package com.nx.logger.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public class LogOperatorUtil {

    public static void notNull(Object object, String message) {
        if (object == null)
            throw new RuntimeException(message);
        if (object instanceof String && (
                (String)object).trim().length() == 0)
            throw new RuntimeException(message);
    }

    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    public static final Charset GBK = Charset.forName("GBK");

    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    public static final String UTF_8_NAME = UTF_8.name();

    public static Charset charset(String charsetName) throws UnsupportedCharsetException {
        return isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }


    public static boolean isBlankChar(char c) {
        return isBlankChar(c);
    }


    public static final String NEWLINE = "\n";


    public static String EMPTY = "";

    public static boolean isNotBlank(String str) {
        if (str != null && str.length() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isBlank(String str) {
        return !isNotBlank(str);
    }

    public static String removePrefix(CharSequence str, CharSequence prefix) {
        if (isEmpty(str) || isEmpty(prefix)) {
            return "";
        }
        String str2 = str.toString();
        if (str2.startsWith(prefix.toString()))
            return subSuf(str2, prefix.length());
        return str2;
    }

    public static String subSuf(CharSequence string, int fromIndex) {
        if (isEmpty(string)) {
            return null;
        }
        return sub(string, fromIndex, string.length());
    }


    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    public static String removeSuffix(CharSequence str, CharSequence suffix) {
        if (isEmpty(str) || isEmpty(suffix)) {
            return str(str);
        }
        String str2 = str.toString();
        if (str2.endsWith(suffix.toString())) {
            return subPre(str2, str2.length() - suffix.length());
        }
        return str2;
    }

    public static String subPre(CharSequence string, int toIndexExclude) {
        return sub(string, 0, toIndexExclude);
    }

    public static String sub(CharSequence str, int fromIndexInclude, int toIndexExclude) {
        if (isEmpty(str)) {
            return str(str);
        }
        int len = str.length();
        if (fromIndexInclude < 0) {
            fromIndexInclude = len + fromIndexInclude;
            if (fromIndexInclude < 0)
                fromIndexInclude = 0;
        } else if (fromIndexInclude > len) {
            fromIndexInclude = len;
        }
        if (toIndexExclude < 0) {
            toIndexExclude = len + toIndexExclude;
            if (toIndexExclude < 0)
                toIndexExclude = len;
        } else if (toIndexExclude > len) {
            toIndexExclude = len;
        }
        if (toIndexExclude < fromIndexInclude) {
            int tmp = fromIndexInclude;
            fromIndexInclude = toIndexExclude;
            toIndexExclude = tmp;
        }
        if (fromIndexInclude == toIndexExclude) {
            return EMPTY;
        }
        return str.toString().substring(fromIndexInclude, toIndexExclude);
    }

    public static String str(CharSequence cs) {
        return (null == cs) ? null : cs.toString();
    }


}
