package com.nx.datasource.jdbc;

import com.baomidou.mybatisplus.annotation.DbType;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * JDBC 工具类
 *
 * @author 芋道源码
 */
public class JdbcUtils {

    /**
     * 判断连接是否正确
     *
     * @param url      数据源连接
     * @param username 账号
     * @param password 密码
     * @return 是否正确
     */
    public static boolean isConnectionOK(String url, String username, String password) {
        try (Connection ignored = DriverManager.getConnection(url, username, password)) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 获得 URL 对应的 DB 类型
     *
     * @param url URL
     * @return DB 类型
     */
    public static DbType getDbType(String url) {
        String name = com.alibaba.druid.util.JdbcUtils.getDbType(url, null);
        return DbType.getDbType(name);
    }


    public static class InternalStringUtils {
        public static String UNDER_LINE = "_";
        public static String toUnderlineCase(String camelCaseStr) {
            if (camelCaseStr == null) {
                return null;
            }
            // 将驼峰字符串转换成数组
            char[] charArray = camelCaseStr.toCharArray();
            StringBuffer buffer = new StringBuffer();
            // 处理字符串
            for (int i = 0, l = charArray.length; i < l; i++) {
                if (charArray[i] >= 65 && charArray[i] <= 90) {
                    buffer.append(UNDER_LINE).append(charArray[i] += 32);
                } else {
                    buffer.append(charArray[i]);
                }
            }
            return buffer.toString();
        }

        public static String toCamelCase(String underlineStr) {
            if (underlineStr == null) {
                return null;
            }
            if (!underlineStr.contains(UNDER_LINE)) {
                return underlineStr;
            }
            // 分成数组
            char[] charArray = underlineStr.toCharArray();
            // 判断上次循环的字符是否是"_"
            boolean underlineBefore = false;
            StringBuffer buffer = new StringBuffer();
            for (int i = 0, l = charArray.length; i < l; i++) {
                // 判断当前字符是否是"_",如果跳出本次循环
                if (charArray[i] == 95) {
                    underlineBefore = true;
                } else if (underlineBefore) {
                    // 如果为true，代表上次的字符是"_",当前字符需要转成大写
                    buffer.append(charArray[i] -= 32);
                    underlineBefore = false;
                } else {
                    // 不是"_"后的字符就直接追加
                    buffer.append(charArray[i]);
                }
            }
            return buffer.toString();
        }
    }

}
