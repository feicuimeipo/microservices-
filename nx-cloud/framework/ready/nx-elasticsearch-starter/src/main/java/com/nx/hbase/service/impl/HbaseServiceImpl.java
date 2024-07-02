package com.nx.hbase.service.impl;

import com.nx.elasticsearch.utils.EsUtils;
import com.nx.hbase.service.HbaseService;
//import com.nx.es.api.ESClientHelper;
//import com.nx.es.api.HbaseTemplate;
//import elasticsearch.api.EsUtils;
import com.nx.elasticsearch.api.ESClientHelper;
import com.nx.hbase.api.HbaseTemplate;
import lombok.SneakyThrows;
//import org.apache.dubbo.config.annotation.DubboService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class HbaseServiceImpl implements HbaseService {

    //esid字段名
    public static final String ESID_FIELD = "esid";

    @SneakyThrows
    private static void saveOrUpdate(String index, Map<String, Object> map, List<Mutation> saveOrUpdates) {
        if (HbaseTemplate.checkHbaseTable(index)) {
            Object esid = map.remove(ESID_FIELD);
            checkESID(esid);
            Set<String> keys = map.keySet();
            for (String key : keys) {
                if (map.get(key) != null) {
                    Put put = new Put(Bytes.toBytes(esid.toString()));
                    put.addColumn(Bytes.toBytes("doc"), Bytes.toBytes(key), Bytes.toBytes(String.valueOf(map.get(key))));
                    saveOrUpdates.add(put);
                }
            }
            HbaseTemplate.saveOrUpdates(index, saveOrUpdates);
        } else {
            EsUtils.throwIllegalArgumentException("此表为空 " + index);
        }
    }

    @SneakyThrows
    private static void saveOrUpdateBulk(String index, List<Map<String, Object>> list, List<Mutation> saveOrUpdates) {
        if (HbaseTemplate.checkHbaseTable(index)) {
            for (Map<String, Object> map : list) {
                Object esid = map.remove(ESID_FIELD);
                checkESID(esid);
                Set<String> keys = map.keySet();
                for (String key : keys) {
                    if (map.get(key) != null) {
                        Put put = new Put(Bytes.toBytes(esid.toString()));
                        put.addColumn(Bytes.toBytes("doc"), Bytes.toBytes(key), Bytes.toBytes(String.valueOf(map.get(key))));
                        saveOrUpdates.add(put);
                    }
                }
            }
            HbaseTemplate.saveOrUpdates(index, saveOrUpdates);
        } else {
            EsUtils.throwIllegalArgumentException("此表为空 " + index);
        }
    }

    @SneakyThrows
    public static List<Map<String, Object>> query1(String index, String row, String field, String qualifier) {
        if (!HbaseTemplate.checkHbaseTable(index)) {
            return null;
        }
        List<Map<String, Object>> map = HbaseTemplate.get(index, row, field, qualifier);
        return map;
    }

    private static void checkESID(Object esid) {
        if (isEmptyESID(esid)) {
            EsUtils.throwIllegalArgumentException("esid禁止为null或空字符串!");
        }
    }

    private static boolean isEmptyESID(Object esid) {
        return esid == null || "".equals(esid);
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop1");
        conf.set("hbase.rootdir", "hdfs://hadoop1:8020/hbase/hbase_db");
        conf.set("zookeeper.znode.parent", "/hbase");
//		Connection connection = ConnectionFactory.createConnection(conf);
//        List<Mutation> saveOrUpdates = new ArrayList<>();
//        Put put = new Put(Bytes.toBytes("row002"));
//        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("sex"), Bytes.toBytes("female"));
//        saveOrUpdates.add(put);
//        HbaseTemplate.saveOrUpdates("phoenix", saveOrUpdates);
//        HbaseTemplate.checkHbaseTableCreate("test_wudi");
        System.out.println(query1("test_wudi", "rowkey1", "doc", "name").size());
    }

    @SneakyThrows
    @Override
    public void insert(String index, Map<String, Object> map) {
        if (!ESClientHelper.getInstance().isTest()) {
            HbaseTemplate.checkHbaseTableCreate(index);
            List<Mutation> saveOrUpdates = new ArrayList<>();
            saveOrUpdate(index, map, saveOrUpdates);
        }
    }

    @SneakyThrows
    @Override
    public void delete(String index, List<String> esids) {
        if (!ESClientHelper.getInstance().isTest()) {
            HbaseTemplate.checkHbaseTableCreate(index);
            if (HbaseTemplate.checkHbaseTable(index)) {
                List<Map<String, Object>> list = new ArrayList<>();
                for (String esid : esids) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(ESID_FIELD, esid);
                    map.put("deleted", "1");
                    list.add(map);
                }
                BulkInsert(index, list);
            } else {
                EsUtils.throwIllegalArgumentException("此表为空 " + index);
            }
        }
    }

    @SneakyThrows
    @Override
    public void BulkInsert(String index, List<Map<String, Object>> list) {
        if (!ESClientHelper.getInstance().isTest()) {
            HbaseTemplate.checkHbaseTableCreate(index);
            List<Mutation> saveOrUpdates = new ArrayList<>();
            saveOrUpdateBulk(index, list, saveOrUpdates);
        }
    }

    @Override
    public void truncateTable(String index) throws IOException {
        if (!ESClientHelper.getInstance().isTest()) {
            if (HbaseTemplate.checkHbaseTable(index)) {
                HbaseTemplate.truncateTable(index);
            } else {
                EsUtils.throwIllegalArgumentException("此表为空 " + index);
            }
        }
    }

    @Override
    public void createTable(String index) {
        if (!ESClientHelper.getInstance().isTest()) {
            try {
                HbaseTemplate.checkHbaseTableCreate(index);
            } catch (IOException e) {
                EsUtils.throwIllegalArgumentException("建表出现异常导致失败 :" + index);
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Map<String, Object>> query(String index, String row, String field) {
        if (!HbaseTemplate.checkHbaseTable(index)) {
            return null;
        }
        List<Map<String, Object>> map = HbaseTemplate.get(index, row, "doc", field);
        return map;
    }

    @Override
    @SneakyThrows
    public List<Map<String, Object>> query(String index, String row) {
        if (!HbaseTemplate.checkHbaseTable(index)) {
            return null;
        }
        List<Map<String, Object>> map = HbaseTemplate.get(index, row);
        return map;
    }

    @SneakyThrows
    @Override
    public List<Map<String, Object>> query(String index, List<String> row) {
        if (!HbaseTemplate.checkHbaseTable(index)) {
            return null;
        }
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        for (String r : row) {
            List<Map<String, Object>> map = HbaseTemplate.get(index, r);
            maps.addAll(map);
        }
        return maps;
    }

    @SneakyThrows
    @Override
    public void delete(String index, String esid) {
        if (!ESClientHelper.getInstance().isTest()) {
            HbaseTemplate.checkHbaseTableCreate(index);
            if (HbaseTemplate.checkHbaseTable(index)) {
                List<Map<String, Object>> resultHbase = query(index, esid);
                if (resultHbase != null) {
                    List<Mutation> deletes = new ArrayList<>();
                    Delete delete = new Delete(Bytes.toBytes(esid));
                    deletes.add(delete);
                    HbaseTemplate.saveOrUpdates(index, deletes);
                }
                Map<String, Object> map = new HashMap<>();
                map.put(ESID_FIELD, esid);
                map.put("deleted", "1");
                insert(index, map);
            } else {
                EsUtils.throwIllegalArgumentException("此表为空 " + index);
            }
        }
    }
}
