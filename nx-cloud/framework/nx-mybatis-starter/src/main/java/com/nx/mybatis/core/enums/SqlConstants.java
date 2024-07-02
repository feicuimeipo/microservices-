package com.nx.mybatis.core.enums;

import com.baomidou.mybatisplus.annotation.DbType;

/**
 * SQL相关常量类
 *
 * @author 芋道源码
 */
public class SqlConstants {

    /**
     * 数据库的类型
     */
    public static DbType DB_TYPE;

    public static void init(DbType dbType) {
        DB_TYPE = dbType;
    }


    /**
     * 数据库类型
     * <p>平台通过mybatis-plus获取数据库类型，所以这里的数据库类型需要与com.baomidou.mybatisplus.annotation.DbType中保持一致。</p>
     */
    public static String DB_ORACLE = DbType.ORACLE.getDb();
    public static String DB_MYSQL = DbType.MYSQL.getDb();
    /**
     * SQLServer2012
     */
    public static String DB_SQLSERVER =  DbType.MYSQL.getDb();;


}
