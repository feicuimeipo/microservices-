package com.nx.elasticsearch.service.impl;

import com.nx.elasticsearch.service.MaxCoumputeService;
import com.nx.elasticsearch.utils.EsUtils;
import com.nx.elasticsearch.api.MaxComputeTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import lombok.SneakyThrows;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class MaxCoumputeServiceImpl implements MaxCoumputeService {

    @Override
    @SneakyThrows
    public List<Map<String, Object>> Query(String table, ArrayList<String> fields, String where, List<String> group, Map<String, List<String>> order, String limit) {
        if(StringUtils.isEmpty(table)){
            EsUtils.throwRuntimeException("指定表没有表名");
        }
        List<Map<String, Object>> list = MaxComputeTemplate.Query(table,fields,where,group,order,limit);
        return list;
    }

    @Override
    @SneakyThrows
    public List<String> Columns(String tableName) {
        if(StringUtils.isEmpty(tableName)){
            EsUtils.throwRuntimeException("指定表没有表名");
        }
        List<String> l = MaxComputeTemplate.Columns(tableName);
        return l;
    }

    @Override
    public Boolean Update(String table, String set, String where) {
        return MaxComputeTemplate.Update(table,set,where);
    }

    @Override
    public Boolean Insert(String table,String partition,String value) {
        return MaxComputeTemplate.Insert(table,partition,value);
    }


}
