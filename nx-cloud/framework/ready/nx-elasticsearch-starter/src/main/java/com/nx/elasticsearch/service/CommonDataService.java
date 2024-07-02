package com.nx.elasticsearch.service;

import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.entity.tuple.TwoTuple;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.impl.AggAggregate;
//import com.nx.commons.entity.TwoTuple;
//import com.nx.server.newsearch.entity.Page;
//import com.nx.server.newsearch.entity.query.AggAggregate;
//import com.nx.server.newsearch.entity.query.Query;

import java.util.List;
import java.util.Map;

/**
 * 名称：通用访问提供接口
 * 备注：
 */
public interface CommonDataService {
    /**
     * 名称：查询(分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * page:分页对象【可以设置当前查询页数,每页条数,排序字段及排序方式】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    Page<Map<String, Object>> getPage(String index, List<Query> querys, Page page, String... showFields);

    /**
     * 名称：查询(不分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * orderColumns:排序字段【为null,则不排序】
     * orderDirs:排序方式【为null或者长度小于排序字段,则默认为升序】
     * count:要获取总条数
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    List<Map<String, Object>> getList(String index, List<Query> querys, String[] orderColumns, String[] orderDirs, long count, String... showFields);

    /**
     * 名称：查询(不分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * orderColumns:排序字段【为null,则不排序】
     * orderDirs:排序方式【为null或者长度小于排序字段,则默认为升序】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    List<Map<String, Object>> getList(String index, List<Query> querys, String[] orderColumns, String[] orderDirs, String... showFields);

    /**
     * 名称：根据查询条件查询一条(只显示第一条,底下的忽略)
     * 备注：
     * index:索引名称
     * querys:查询条件集合【查询的字段不存在则返回null,list为null则查询全部数据】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    Map<String, Object> getOne(String index, List<Query> querys, String... showFields);

    /**
     * 名称：根据esid查询一条
     * 备注：
     * index:索引名称
     * esid:esid
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    Map<String, Object> getOne(String index, Object esid, String... showFields);

    /**
     * 名称：根据查询条件获取总条数
     * 备注：
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回0,list为null则查询全部数据条数】
     */
    long getCount(String index, List<Query> querys);


    /**
     * @comment: 新增/修改字段
     * @return: 参数1是否成功 2为操作的标识符
     * @notes: 1.必须传esid或整数标识
     * 2.需要修改的字段和值
     */
    TwoTuple<Boolean, Object> insertOrUpdateFields(String index, Object esid, Map<String, Object> map) ;//throws DuplicateKeyException;

    /**
     * 真正物理删除(根据esid删除)
     */
    List<String> deleteRealBulk(String index, List<String> list) throws Exception;

    /**
     * 直接删除数据,默认标识为id,es为esid
     *
     * @param index
     * @param id
     * @return
     */
    boolean delete(String index, String id);


    /**
     * @comment: 清空索引底下全部数据
     */
    boolean deleteIndexAllData(String index);

    /**
     * 【es是批量新增/替换】
     *
     * @param index
     * @param objs
     */
    List<Integer> insertOrUpdateBulk(String index, List<Map<String, Object>> objs);

    /**
     * @date: 2018/5/28 10:56
     * @comment: 批量修改部分字段【必须传esid字段】
     * @return:
     * @notes:
     */
    List<Map<String, Object>> updateFieldsBulk(String index, List<Map<String, Object>> list) throws Exception;

    /**
     * 仅数据库使用,统计总数
     *
     * @param sql
     * @param data
     * @return
     */
    int queryForInt(String sql, Map<String, Object> data);

    /**
     * 仅数据库使用,查询列表
     *
     * @param sql
     * @param data
     * @return
     */
    List<Map<String, Object>> queryForList(String sql, Map<String, Object> data);

    /**
     * @author: 王坤造
     * @date: 2017/4/21 14:03
     * @comment: 根据查询条件, 对一个字段分组
     * @return:
     * @notes: 最多显示10000条数据
     * index:索引名称
     * queries:查询条件集合【查询的字段不存在则返回null,queries为null则查询全部数据】
     * field:要分组的字段名称
     */
    List<String> getFieldGroup(String index, List<Query> queries, String field);

    /**
     * 单字段分组
     *
     * @param indexTable 索引名称
     * @param queryList  查询条件
     * @param total      返回条数
     * @param field      分组字段
     * @param orderType  排序true(ASC),false(DESC)
     * @return
     */
    Object getSingleAggregationsObject(String indexTable, List<Query> queryList, int total, String field, Boolean orderType);

    /**
     * 多字段分组聚合
     *
     * @param indexTable  索引名称
     * @param queryList   查询条件
     * @param total       返回条数
     * @param aggNames    聚合字段 为空时，就是多字段分组
     * @param fieldsGroup 分组字段
     * @return
     */
    List<Map<String, Object>> getMutiAggregationsObject(String indexTable, List<Query> queryList, int total, String[] aggNames, boolean useMaxSize, String... fieldsGroup);


    List<Map<String, Object>> getMutiAggregationsObject(String indexTable, List<Query> queryList, int total, String[] aggNames, String... fieldsGroup);

    /**
     * 多字段分组聚合
     *
     * @param indexTable  索引名称
     * @param queryList   查询条件
     * @param total       返回条数
     * @param aggNames    聚合字段 为空时，就是多字段分组
     * @param fieldsGroup 分组字段
     * @return
     */
    List<Map<String, Object>> getMutiAggregationsCount(String indexTable, List<Query> queryList, int total, String[] aggNames, String... fieldsGroup);

    List<Map<String, Object>> getMutiAggregationsCount(String indexTable, List<Query> queryList, int total, String[] aggNames, boolean useMaxSize, String... fieldsGroup);


    /**
     * 多字段分组聚合分页
     *
     * @param indexTable  索引名称
     * @param queryList   查询条件
     * @param page        分页
     * @param aggNames    聚合字段
     * @param fieldsGroup 分组字段
     * @return
     */
    Page<List<Map<String, Object>>> getMutiAggregationsPage(String indexTable, List<Query> queryList, Page page, String[] aggNames, String... fieldsGroup);

    /**
     * 单字段聚合
     *
     * @param indexTable
     * @param queryList
     * @param aggs
     * @return
     */
    Map<String, Object> getAggAggregates(String indexTable, List<Query> queryList, AggAggregate... aggs);

    /**
     * 多字段分组，单字段聚合
     *
     * @param indexTable
     * @param queryList
     * @param page
     * @param aggName
     * @param fieldsGroup
     * @return
     */
    Page<Map<String, Object>> getAggAggregateGroupPage(String indexTable, List<Query> queryList, Page page, String aggName, String... fieldsGroup);

    /**
     * 单字段分组聚合
     *
     * @param indexTable
     * @param queryList
     * @param total
     * @param field
     * @return
     */
    List<Map<String, Object>> getAggAggregatesGroup(String indexTable, List<Query> queryList, int total, String field);

    List<Map<String, Object>> getAggGroupAgg(String indexTable, List<Query> queryList, int total, String field, boolean sortType, String aggName);

    List<String> getSingleAggregationsList(String indexTableName, List<Query> queries, String field);

    /**
     * WARNING:replace,慎用
     *
     * @param tableName
     * @param data
     * @return
     */
    @Deprecated
    void updateData(String tableName, Map<String, Object> data);

    /**
     * WARNING:批量replace,慎用
     *
     * @param tableName
     * @param dataList
     * @return
     */
    @Deprecated
    void updateDataList(String tableName, List<Map<String, Object>> dataList);

    void updateBySelect(String tableName, List<Query> queryList, Map<String, Object> updateMap);

    /**
     * 通过关联字段存入关联表对应字段值
     *
     * @param index           关联表
     * @param list            需要值得列表
     * @param queries         查询条件
     * @param field           字段
     * @param associatedField 关联字段
     * @param fields          需要字段
     * @return
     */
    List<Map<String, Object>> associateFields(String index, List<Map<String, Object>> list, List<Query> queries, String field, boolean isCollection, String associatedField, String... fields);
}
