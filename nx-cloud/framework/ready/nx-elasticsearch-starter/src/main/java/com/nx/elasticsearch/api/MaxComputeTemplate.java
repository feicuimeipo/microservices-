package com.nx.elasticsearch.api;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.odps.*;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.utils.StringUtils;
import com.nx.elasticsearch.utils.EsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.*;
import java.util.*;

public class MaxComputeTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(MaxComputeTemplate.class);
    private static final String PROJECT = "mc.project";
    private static final String DRIVER_NAME = "mc.driver_name";
    private static final String ENDPOINT = "mc.endpoint";
    private static final String URL = "mc.url";
//    private static  String ACCESSID;
//    private static  String ACCESSKEY;
    private static final Long LIFECYCLE = 1L;
//    private static  String JDBCURL;
    private static final Map<String, Object> mapValues = new LinkedHashMap();
    private static final List<Map<String, Object>> list = new ArrayList();
    private static Account account;
    private static Odps odps;

    public MaxComputeTemplate() {
    }

    @PostConstruct
    public void Init() {
        try {
            Class.forName("mc.driver_name");
        } catch (ClassNotFoundException var2) {
            var2.printStackTrace();
            System.exit(1);
        }

        account = new AliyunAccount(ACCESSID, ACCESSKEY);
        odps = new Odps(account);
        odps.setEndpoint("mc.endpoint");
        odps.setDefaultProject("mc.project");
    }

    public static List<Map<String, Object>> Query(String table, ArrayList<String> fields, String where, List<String> group, Map<String, List<String>> order, String limit) {
        String field = "";
        String orders = "";
        String ordersql = "";
        String groups = "";
        String groupsql = "";
        if (StringUtils.isEmpty(table)) {
            EsUtils.throwRuntimeException("指定表没有表名");
        }

        Iterator var11;
        String key;
        if (fields != null && fields.size() > 0) {
            for (var11 = fields.iterator(); var11.hasNext(); field = key + "," + field) {
                key = (String) var11.next();
            }

            field = field.substring(0, field.length() - 1);
        } else {
            field = "*";
        }

        if (!StringUtils.isEmpty(where)) {
            where = " where " + where;
        } else {
            where = "";
        }

        if (order != null && order.size() == 1) {
            var11 = order.keySet().iterator();

            label266:
            while (true) {
                while (true) {
                    if (!var11.hasNext()) {
                        break label266;
                    }

                    key = (String) var11.next();
                    List<String> fs = (List) order.get(key);
                    if (fs != null && fs.size() > 0) {
                        String s;
                        for (Iterator var14 = fs.iterator(); var14.hasNext(); orders = s + "," + orders) {
                            s = (String) var14.next();
                        }

                        orders = orders.substring(0, orders.length() - 1);
                        ordersql = " order by " + orders + " " + key;
                    } else {
                        EsUtils.throwRuntimeException("排序字段必须指定");
                    }
                }
            }
        } else {
            ordersql = "";
        }

        if (group != null && group.size() > 0) {
            for (var11 = group.iterator(); var11.hasNext(); groups = key + "," + groups) {
                key = (String) var11.next();
            }

            groups = groups.substring(0, groups.length() - 1);
            groupsql = " group by " + groups;
        }

        if (!StringUtils.isEmpty(limit)) {
            if (Integer.valueOf(limit) > 10000) {
                limit = "10000";
            }

            limit = " limit " + limit;
        } else {
            limit = " limit 100";
        }

        String SQL = "select " + field + " from nx." + table + where + groupsql + ordersql + limit;
        System.out.println(SQL);

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var30 = null;

            try {
                Statement st = con.createStatement();
                ResultSet results = st.executeQuery(SQL);

                while (results.next()) {
                    for (int i = 1; i <= results.getMetaData().getColumnCount(); ++i) {
                        mapValues.put(results.getMetaData().getColumnName(i), results.getString(i));
                    }

                    list.add(new JSONObject(mapValues));
                }
            } catch (Throwable var25) {
                var30 = var25;
                throw var25;
            } finally {
                if (con != null) {
                    if (var30 != null) {
                        try {
                            con.close();
                        } catch (Throwable var24) {
                            var30.addSuppressed(var24);
                        }
                    } else {
                        con.close();
                    }
                }

            }
        } catch (SQLException var27) {
            var27.printStackTrace();
        }

        return list;
    }

    public void TableCopy(String TABLE, String TARTGET_TABLE) throws OdpsException {
        Table table = odps.tables().get(TABLE);
        TableSchema targetSchema = table.getSchema();
        List<String> list = new ArrayList();

        for (int i = 0; i < targetSchema.getColumns().size(); ++i) {
            list.add(targetSchema.getColumn(i).getName());
        }

        System.out.println(list);
        TableSchema tableSchema = new TableSchema();
        List<Column> tableColumns = new ArrayList();
        tableColumns.addAll(targetSchema.getColumns());
        tableColumns.addAll(targetSchema.getPartitionColumns());
        tableSchema.setColumns(tableColumns);
        odps.tables().createTableWithLifeCycle("mc.project", TARTGET_TABLE, tableSchema, "Temporary Table", false, LIFECYCLE);
    }

    public static void QueryWhere(String SQL, ArrayList<String> pList) {
        SQL = "select * from nx.drug_patent_info_v2 where pt=? and family_id=?;";

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var3 = null;

            try {
                PreparedStatement ps = con.prepareStatement(SQL);
                Throwable var5 = null;

                try {
                    for (int i = 0; i < pList.size(); ++i) {
                        ps.setString(i + 1, (String) pList.get(i));
                    }

                    ResultSet results = ps.executeQuery();

                    while (results.next()) {
                        for (int i = 1; i <= results.getMetaData().getColumnCount(); ++i) {
                            mapValues.put(results.getMetaData().getColumnName(i), results.getString(i));
                        }

                        list.add(new JSONObject(mapValues));
                    }

                    System.out.println(list);
                } catch (Throwable var31) {
                    var5 = var31;
                    throw var31;
                } finally {
                    if (ps != null) {
                        if (var5 != null) {
                            try {
                                ps.close();
                            } catch (Throwable var30) {
                                var5.addSuppressed(var30);
                            }
                        } else {
                            ps.close();
                        }
                    }

                }
            } catch (Throwable var33) {
                var3 = var33;
                throw var33;
            } finally {
                if (con != null) {
                    if (var3 != null) {
                        try {
                            con.close();
                        } catch (Throwable var29) {
                            var3.addSuppressed(var29);
                        }
                    } else {
                        con.close();
                    }
                }

            }
        } catch (SQLException var35) {
            var35.printStackTrace();
        }

    }

    public static boolean Insert(String table, String partition, String value) {
        if (StringUtils.isEmpty(table)) {
            EsUtils.throwRuntimeException("指定表没有表名");
        }

        if (StringUtils.isEmpty(partition)) {
            EsUtils.throwRuntimeException("没有指定更新字段");
        }

        if (StringUtils.isEmpty(value)) {
            EsUtils.throwRuntimeException("没有指定where条件");
        }

        String SQL = "insert into " + table + " partition (" + partition + ") " + value;

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var5 = null;

            boolean var9;
            try {
                Statement stmt = con.createStatement();
                Throwable var7 = null;

                try {
                    Integer r = stmt.executeUpdate(SQL);
                    if (r == null || r <= 0) {
                        return false;
                    }

                    var9 = true;
                } catch (Throwable var37) {
                    var7 = var37;
                    throw var37;
                } finally {
                    if (stmt != null) {
                        if (var7 != null) {
                            try {
                                stmt.close();
                            } catch (Throwable var36) {
                                var7.addSuppressed(var36);
                            }
                        } else {
                            stmt.close();
                        }
                    }

                }
            } catch (Throwable var39) {
                var5 = var39;
                throw var39;
            } finally {
                if (con != null) {
                    if (var5 != null) {
                        try {
                            con.close();
                        } catch (Throwable var35) {
                            var5.addSuppressed(var35);
                        }
                    } else {
                        con.close();
                    }
                }

            }

            return var9;
        } catch (SQLException var41) {
            var41.printStackTrace();
            return false;
        }
    }

    public static boolean InsertOverWrite(String table, String partition, String value) {
        if (StringUtils.isEmpty(table)) {
            EsUtils.throwRuntimeException("指定表没有表名");
        }

        if (StringUtils.isEmpty(partition)) {
            EsUtils.throwRuntimeException("没有指定更新字段");
        }

        if (StringUtils.isEmpty(value)) {
            EsUtils.throwRuntimeException("没有指定where条件");
        }

        String SQL = "insert overwrite table " + table + value;

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var5 = null;

            boolean var10;
            try {
                PreparedStatement ps = con.prepareStatement(SQL);
                Throwable var7 = null;

                try {
                    ResultSet result = ps.executeQuery();
                    Integer r = result.getMetaData().getColumnCount();
                    if (r == null || r <= 0) {
                        System.out.println(result);
                        return false;
                    }

                    var10 = true;
                } catch (Throwable var38) {
                    var7 = var38;
                    throw var38;
                } finally {
                    if (ps != null) {
                        if (var7 != null) {
                            try {
                                ps.close();
                            } catch (Throwable var37) {
                                var7.addSuppressed(var37);
                            }
                        } else {
                            ps.close();
                        }
                    }

                }
            } catch (Throwable var40) {
                var5 = var40;
                throw var40;
            } finally {
                if (con != null) {
                    if (var5 != null) {
                        try {
                            con.close();
                        } catch (Throwable var36) {
                            var5.addSuppressed(var36);
                        }
                    } else {
                        con.close();
                    }
                }

            }

            return var10;
        } catch (SQLException var42) {
            var42.printStackTrace();
            return false;
        }
    }

    public static boolean Update(String table, String set, String where) {
        if (StringUtils.isEmpty(table)) {
            EsUtils.throwRuntimeException("指定表没有表名");
        }

        if (StringUtils.isEmpty(set)) {
            EsUtils.throwRuntimeException("没有指定更新字段");
        }

        if (StringUtils.isEmpty(where)) {
            EsUtils.throwRuntimeException("没有指定where条件");
        }

        String SQL = "update " + table + " set " + set + " where " + where;
        System.out.println(SQL);

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var5 = null;

            boolean var9;
            try {
                Statement stmt = con.createStatement();
                Throwable var7 = null;

                try {
                    Integer r = stmt.executeUpdate(SQL);
                    if (r == null || r <= 0) {
                        return false;
                    }

                    var9 = true;
                } catch (Throwable var37) {
                    var7 = var37;
                    throw var37;
                } finally {
                    if (stmt != null) {
                        if (var7 != null) {
                            try {
                                stmt.close();
                            } catch (Throwable var36) {
                                var7.addSuppressed(var36);
                            }
                        } else {
                            stmt.close();
                        }
                    }

                }
            } catch (Throwable var39) {
                var5 = var39;
                throw var39;
            } finally {
                if (con != null) {
                    if (var5 != null) {
                        try {
                            con.close();
                        } catch (Throwable var35) {
                            var5.addSuppressed(var35);
                        }
                    } else {
                        con.close();
                    }
                }

            }

            return var9;
        } catch (SQLException var41) {
            var41.printStackTrace();
            return false;
        }
    }

    public void InsertBatch(String SQL, String partition, ArrayList<ArrayList<Object>> lists) {
        SQL = "insert into test_t4 partition (pt=?) values (?, ?, ?);";

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var5 = null;

            try {
                PreparedStatement ps = con.prepareStatement(SQL);
                Throwable var7 = null;

                try {
                    //int batchSize = true;
                    int count = 0;
                    Iterator var10 = lists.iterator();

                    while (var10.hasNext()) {
                        ArrayList<Object> list = (ArrayList) var10.next();
                        ps.setString(1, "2020");
                        ps.setFloat(2, 3.14F);
                        ps.setDate(3, new Date(System.currentTimeMillis()));
                        ps.setDate(4, new Date(System.currentTimeMillis()));
                        ps.addBatch();
                        ++count;
                        if (count % 1000 == 0) {
                            ps.executeBatch();
                        }
                    }

                    int[] result = ps.executeBatch();
                    System.out.println(result);
                } catch (Throwable var35) {
                    var7 = var35;
                    throw var35;
                } finally {
                    if (ps != null) {
                        if (var7 != null) {
                            try {
                                ps.close();
                            } catch (Throwable var34) {
                                var7.addSuppressed(var34);
                            }
                        } else {
                            ps.close();
                        }
                    }

                }
            } catch (Throwable var37) {
                var5 = var37;
                throw var37;
            } finally {
                if (con != null) {
                    if (var5 != null) {
                        try {
                            con.close();
                        } catch (Throwable var33) {
                            var5.addSuppressed(var33);
                        }
                    } else {
                        con.close();
                    }
                }

            }
        } catch (SQLException var39) {
            var39.printStackTrace();
        }

    }

    public void AddBlob(String SQL, String SQL_1) throws Exception {
        String str = null;
        SQL = "select * from test_t7 where _partition='test';";

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var5 = null;

            try {
                Statement stmt = con.createStatement();
                stmt.executeQuery(SQL);

                for (ResultSet resultSet = stmt.getResultSet(); resultSet.next(); str = resultSet.getString(1)) {
                }
            } catch (Throwable var61) {
                var5 = var61;
                throw var61;
            } finally {
                if (con != null) {
                    if (var5 != null) {
                        try {
                            con.close();
                        } catch (Throwable var56) {
                            var5.addSuppressed(var56);
                        }
                    } else {
                        con.close();
                    }
                }

            }
        } catch (SQLException var63) {
            var63.printStackTrace();
        }

        InputStream stream = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        SQL_1 = "insert into test_t7 partition (year=?) values (?);";

        try {
            Connection con = DriverManager.getConnection(JDBCURL, ACCESSID, ACCESSKEY);
            Throwable var66 = null;

            try {
                PreparedStatement ps = con.prepareStatement(SQL_1);
                Throwable var8 = null;

                try {
                    ps.setString(1, "2020");
                    ps.setBinaryStream(2, stream);
                    ResultSet result = ps.executeQuery();
                    System.out.println(result);
                } catch (Throwable var55) {
                    var8 = var55;
                    throw var55;
                } finally {
                    if (ps != null) {
                        if (var8 != null) {
                            try {
                                ps.close();
                            } catch (Throwable var54) {
                                var8.addSuppressed(var54);
                            }
                        } else {
                            ps.close();
                        }
                    }

                }
            } catch (Throwable var58) {
                var66 = var58;
                throw var58;
            } finally {
                if (con != null) {
                    if (var66 != null) {
                        try {
                            con.close();
                        } catch (Throwable var53) {
                            var66.addSuppressed(var53);
                        }
                    } else {
                        con.close();
                    }
                }

            }
        } catch (SQLException var60) {
            var60.printStackTrace();
        }

    }

    public static List<String> Columns(String tableName) {
        Table table = odps.tables().get(tableName);
        TableSchema targetSchema = table.getSchema();
        List<Column> targetField = targetSchema.getColumns();
        List<String> list = new ArrayList();
        Iterator var5 = targetField.iterator();

        while (var5.hasNext()) {
            Column column = (Column) var5.next();
            list.add(column.getName());
        }

        return list;
    }

    public void createTableWithPartition(String createTableName) throws Exception {
        Tables tables = odps.tables();
        boolean exists = tables.exists(createTableName);
        if (exists) {
            LOGGER.error("指定表存在");
            EsUtils.throwRuntimeException("指定表存在:" + createTableName);
        } else {
            LOGGER.info("指定表不存在");
        }

        if (tables.exists(createTableName)) {
            LOGGER.error("指定表存在,无法创建");
        } else {
            LOGGER.info("指定表不存在,能够创建");
            TableSchema tableSchema = new TableSchema();
            Column col = new Column("id", OdpsType.STRING, "ID");
            tableSchema.addColumn(col);
            col = new Column("name", OdpsType.STRING, "姓名");
            tableSchema.addColumn(col);
            col = new Column("sex", OdpsType.BIGINT, "性别");
            tableSchema.addColumn(col);
            col = new Column("birthday", OdpsType.DATETIME, "生日");
            tableSchema.addColumn(col);
            col = new Column("pt", OdpsType.STRING, "分区");
            tableSchema.addPartitionColumn(col);
            tables.create(createTableName, tableSchema);
            LOGGER.info("表【" + createTableName + "】创建成功");
        }
    }
//
//    static {
//        PropertiesUtils prop = new PropertiesUtils("conf/application.properties");
//        JDBCURL = prop.getProperty("mc.url") + prop.getProperty("mc.project");
//        ACCESSID = prop.getProperty("mc.accessid");
//        ACCESSKEY = prop.getProperty("mc.accesskey");
//    }
}
