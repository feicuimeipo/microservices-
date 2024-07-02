package com.nx.hbase.api;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.nx.elasticsearch.api.MutatorCallback;

import com.nx.elasticsearch.api.RowMapper;
import com.nx.elasticsearch.api.TableCallback;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.BufferedMutator;
import org.apache.hadoop.hbase.client.BufferedMutatorParams;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

public class HbaseTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseTemplate.class);
    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";
    private static final String HBASE_ROOTDIR = "hbase.rootdir";
    private static final String HBASE_ZNODE_PARENT = "zookeeper.znode.parent";
    private static Configuration configuration;
    private static volatile Connection connection;

    public HbaseTemplate() {
    }

    public static <T> T execute(String tableName, TableCallback<T> action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");
        StopWatch sw = new StopWatch();
        sw.start();
        Table table = null;

        Object var4;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            var4 = action.doInTable(table);
        } catch (Throwable var13) {
            throw new HbaseSystemException(var13);
        } finally {
            if (null != table) {
                try {
                    table.close();
                    sw.stop();
                } catch (IOException var12) {
                    LOGGER.error("hbase资源释放失败");
                }
            }

        }

        return (T) var4;
    }

    public static <T> List<T> find(String tableName, String family, RowMapper<T> action) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addFamily(Bytes.toBytes(family));
        return find(tableName, scan, action);
    }

    public static <T> List<T> find(String tableName, String family, String qualifier, RowMapper<T> action) {
        Scan scan = new Scan();
        scan.setCaching(5000);
        scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        return find(tableName, scan, action);
    }

    public static <T> List<T> find(String tableName, final Scan scan, final RowMapper<T> action) {
        return (List)execute(tableName, new TableCallback<List<T>>() {
            public List<T> doInTable(Table table) throws Throwable {
                int caching = scan.getCaching();
                if (caching == 1) {
                    scan.setCaching(5000);
                }

                ResultScanner scanner = table.getScanner(scan);

                try {
                    List<T> rs = new ArrayList();
                    int rowNum = 0;
                    Iterator var6 = scanner.iterator();

                    while(var6.hasNext()) {
                        Result result = (Result)var6.next();
                        rs.add(action.mapRow(result, rowNum++));
                    }

                    ArrayList var11 = (ArrayList) rs;
                    return var11;
                } finally {
                    scanner.close();
                }
            }
        });
    }

    public static <T> T get(String tableName, String rowName) {
        return get(tableName, rowName, (String)null, (String)null);
    }

    public static <T> T get(String tableName, String rowName, String familyName) {
        return get(tableName, rowName, familyName, (String)null);
    }

    public static <T> T get(String tableName, final String rowName, final String familyName, final String qualifier) {
        return execute(tableName, new TableCallback<T>() {
            public T doInTable(Table table) throws Throwable {
                Get get = new Get(Bytes.toBytes(rowName));
                if (StringUtils.isNotBlank(familyName)) {
                    byte[] family = Bytes.toBytes(familyName);
                    if (StringUtils.isNotBlank(qualifier)) {
                        get.addColumn(family, Bytes.toBytes(qualifier));
                    } else {
                        get.addFamily(family);
                    }
                }

                get.setMaxVersions(100);
                Result result = table.get(get);
                if (result.listCells() == null) {
                    return null;
                } else {
                    List<Map<String, Object>> map = new ArrayList();
                    Iterator var5 = result.listCells().iterator();

                    while(var5.hasNext()) {
                        Cell cell = (Cell)var5.next();
                        Map<String, Object> columnMap = new HashMap();
                        columnMap.put(Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()), Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
                        map.add(columnMap);
                    }

                    return (T) map;
                }
            }
        });
    }

    public static void execute(String tableName, MutatorCallback action) {
        Assert.notNull(action, "Callback object must not be null");
        Assert.notNull(tableName, "No table specified");
        StopWatch sw = new StopWatch();
        sw.start();
        BufferedMutator mutator = null;

        try {
            BufferedMutatorParams mutatorParams = new BufferedMutatorParams(TableName.valueOf(tableName));
            mutator = connection.getBufferedMutator(mutatorParams.writeBufferSize(3145728L));
            action.doInMutator(mutator);
        } catch (Throwable var12) {
            throw new HbaseSystemException(var12);
        } finally {
            if (null != mutator) {
                try {
                    mutator.flush();
                    mutator.close();
                    sw.stop();
                } catch (IOException var11) {
                    LOGGER.error("hbase mutator资源释放失败");
                }
            }

        }

    }

    public static void saveOrUpdate(String tableName, final Mutation mutation) {
        execute(tableName, new MutatorCallback() {
            public void doInMutator(BufferedMutator mutator) throws Throwable {
                mutator.mutate(mutation);
            }
        });
    }

    public static void saveOrUpdates(String tableName, final List<Mutation> mutations) {
        execute(tableName, new MutatorCallback() {
            public void doInMutator(BufferedMutator mutator) throws Throwable {
                mutator.mutate(mutations);
            }
        });
    }

    public static void getConnection() {
        if (null == connection) {
            Class var0 = HbaseTemplate.class;
            synchronized(HbaseTemplate.class) {
                if (null == connection) {
                    try {
                        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(200, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue());
                        poolExecutor.prestartCoreThread();
                        connection = ConnectionFactory.createConnection(configuration, poolExecutor);
                    } catch (IOException var3) {
                        LOGGER.error("hbase connection资源池创建失败");
                    }
                }
            }
        }

    }

    public static void deleteTable(String tableName) {
        Scan scan = new Scan();
        Table table = null;
        ResultScanner scanner = null;

        try {
            table = connection.getTable(TableName.valueOf(tableName));
            scanner = table.getScanner(scan);

            for(Result rr = scanner.next(); rr != null; rr = scanner.next()) {
                table.delete(new Delete(rr.getRow()));
            }
        } catch (Exception var13) {
            LOGGER.error("hbase删除表失败");
        } finally {
            if (scanner != null) {
                scanner.close();
            }

            try {
                if (null != table) {
                    table.close();
                }
            } catch (IOException var12) {
                LOGGER.error("hbase表关闭失败");
            }

        }

    }

    public static void truncateTable(String tableName) {
        TableName hTableName = TableName.valueOf(tableName);
        Admin admin = null;

        try {
            admin = connection.getAdmin();
            checkHbaseTable(tableName);
            admin.disableTable(hTableName);
            admin.truncateTable(hTableName, true);
        } catch (Exception var7) {
            LOGGER.error("hbase清空表失败");
        } finally {
            closeAdmin(admin);
        }

    }

    private static void closeAdmin(Admin admin) {
        try {
            if (null != admin) {
                admin.close();
            }
        } catch (IOException var2) {
            LOGGER.error("admin关闭失败");
        }

    }

    public static boolean checkHbaseTable(String tableName) throws IOException {
        tableName = tableName.replace("alias", "");
        Admin admin = connection.getAdmin();
        TableName hTableName = TableName.valueOf(tableName);
        if (!admin.tableExists(hTableName)) {
            LOGGER.error("HBase源头表" + hTableName.toString() + "不存在, 请检查您的配置 或者 联系 Hbase 管理员.");
            return false;
        } else if (!admin.isTableAvailable(hTableName)) {
            LOGGER.error("HBase源头表" + hTableName.toString() + "不存在, 请检查您的配置 或者 联系 Hbase 管理员.");
            return false;
        } else if (admin.isTableDisabled(hTableName)) {
            LOGGER.error("HBase源头表" + hTableName.toString() + "不存在, 请检查您的配置 或者 联系 Hbase 管理员.");
            return false;
        } else {
            return true;
        }
    }

    public static void checkHbaseTableCreate(String tableName) throws IOException {
        tableName = tableName.replace("alias", "");
        Admin admin = connection.getAdmin();
        TableName hTableName = TableName.valueOf(tableName);
        if (!admin.tableExists(hTableName)) {
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(Bytes.toBytes(tableName)));
            HColumnDescriptor columnDescriptor = new HColumnDescriptor(Bytes.toBytes("doc"));
            columnDescriptor.setVersions(180, 200);
            tableDescriptor.addFamily(columnDescriptor);
            admin.createTable(tableDescriptor);
        }

    }

    public static boolean checkTable(String index) {
        return index != null && !index.equals("") && !index.equals("pubmed_entrez") && !index.equals("epo_legal_status") && !index.equals("generic_drug") && !index.equals("import_bidding");
    }

    public void setConnection(Connection connection) {
        HbaseTemplate.connection = connection;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        HbaseTemplate.configuration = configuration;
    }

//    static {
//        PropertiesUtils prop = new PropertiesUtils("conf/application.properties");
//        String quorum = prop.getProperty("hbase.zookeeper.quorum");
//        String rootDir = prop.getProperty("hbase.rootdir");
//        String nodeParent = prop.getProperty("zookeeper.znode.parent");
//        Configuration conf = HBaseConfiguration.create();
//        conf.set("hbase.zookeeper.quorum", quorum);
//        conf.set("hbase.rootdir", rootDir);
//        conf.set("zookeeper.znode.parent", nodeParent);
//        configuration = conf;
//        getConnection();
//    }
}