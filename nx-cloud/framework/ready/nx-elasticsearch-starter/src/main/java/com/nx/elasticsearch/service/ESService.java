package com.nx.elasticsearch.service;

import com.github.sd4324530.jtuple.Tuple;
import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.query.impl.AggAggregate;
import com.nx.elasticsearch.query.impl.AggGroup;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.entity.tree.TreeCube;
import com.nx.elasticsearch.constant.ESConstants;
//import com.nx.commons.utils.es.ESConstants;
//import com.nx.server.newsearch.entity.Page;
//import com.nx.server.newsearch.entity.query.AggAggregate;
//import com.nx.server.newsearch.entity.query.AggGroup;
//import com.nx.server.newsearch.entity.query.Query;
//import com.nx.server.newsearch.entity.tree.TreeCube;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 名称：王坤造
 * 时间：2017/1/19.
 * 名称：给cfda,临床库,美国库提供的es接口
 * 备注：
 */
public interface ESService {
    //region 常量
    //esid字段名
    String ESID_FIELD = ESConstants.ESID_FIELD;
    //查询时不返回其它字段只返回esid时设置的字段,EQ和NE查询空時使用(仅限str类型,其它类型会报错!)
    String NULL_VALUE = ESConstants.NULL_VALUE;
    String NULL_VALUE_NESTED = "None_Nested";
    //查询时按匹配分数字段进行排序字段名
    String SCORE_FIELD = ESConstants.SCORE_FIELD;
    //结构类型数据:索引和字段
    String INDEX_AUDIT_TREE_OPERATE_HISTORY = "audit_tree_operate_history";
    String FIELD_OPERATE_NAME = "operate_name";
    String VALUE_OPERATE_NAME_INDEX = "index";
    String VALUE_OPERATE_NAME_DELETE = "delete";
    String VALUE_OPERATE_NAME_UPDATE = "update";

    String INDEX_AUDIT_TREE = "audit_tree";
    String FIELD_CREATE_TIME = "create_time";
    String FIELD_INDEX_NAME = "index_name";
    String FIELD_CONTENT = "content";
    String FIELD_CONTENT_PRE = "content_pre";
    String FIELD_ID = "id";

    String INDEX_TREE_DATA_SUFFIX = "_tree_data";
    String INDEX_TREE_RELATIONSHIP_SUFFIX = "_tree_relationship";
    String FIELD_UNIQUE_ESID = "unique_esid";
    String FIELD_PARENT_ESID = "parent_esid";
    String FIELD_CHILDREN_ESIDS = "children_esids";
    //String[] FIELDS_TREE_SHOW_FIELDS = {"topic", "expanded", "direction"};
    //中标数据范围查询使用
    String[] DEFAULT_AREA = {"4+7城市", "安徽", "北京", "重庆", "福建", "甘肃", "广东", "广西", "贵州", "海南", "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西", "陕西", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江", "军区"};
    //endregion

    //region 查询

    //region 查询

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：查询(分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:集合/Query对象【查询的字段不存在则返回null,queries为null则查询全部数据】
     * page:分页对象【可以设置当前查询页数,每页条数,排序字段及排序方式】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    Page<Map<String, Object>> getPage(String index, List<Query> queries, Page page, String... showFields);

    /**
     * @author: 王坤造
     * @date: 2018/7/26 10:40
     * @comment: 适用于获取分页在10000条+
     * @return:
     * @notes: size:每页条数(<=1000)
     * sorts:排序字段和排序方式(true,null:升序;false:降序)
     * searchValues:排序字段的对应的值(每次分页需要设置上一次分页最後一条数据的值)
     */
    Page<Map<String, Object>> getPage(String index, List<Query> queries, int size, LinkedHashMap<String, Boolean> sorts, Object[] after, String... showFields);

    Page<Map<String, Object>> getPageCollapse(String index, String collapse, List<Query> queries, int size, LinkedHashMap<String, Boolean> sorts, Object[] after, String... showFields);

    /**
     * 推荐使用 ES7Service.getList
     *
     * @param index
     * @param queries
     * @param orderColumns
     * @param orderDirs
     * @param size
     * @param showFields
     * @return
     */
    @Deprecated
    List<Map<String, Object>> getList(String index, List<Query> queries, String[] orderColumns, String[] orderDirs, long size, String... showFields);

    List<Map<String, Object>> getListCollapse(String index, String collapse, List<Query> queries, String[] orderColumns, String[] orderDirs, long size, String... showFields);

    /**
     * 作者: 王坤造
     * 日期: 2017/1/20 16:34
     * 名称：根据esid查询一条
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * esid:esid必须有
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    Map<String, Object> getOne(String index, Object esid, String... showFields);

    /**
     * 作者: 王坤造
     * 日期: 2017/1/20 16:34
     * 名称：根据查询条件查询一条(只显示第一条,底下的忽略)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:查询条件集合【查询的字段不存在则返回null,queries为null则查询第一条数据】
     * showFields:查询出来返回的字段【为null,则显示全部字段】
     */
    Map<String, Object> getOne(String index, List<Query> queries, String... showFields);

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：根据查询条件获取总条数
     * 备注：
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回0,list为null则查询全部数据条数】
     */
    long getCount(String index, List<Query> queries);


    Map<String, Object> getPriceMedian(String index, List<Query> queries, String beginTimeField, String endTimeField, int showType);

    /***
     * 判断esid是否存在
     * @param index
     * @param esid
     * @return
     */
    boolean getExist(String index, String esid);
    //endregion

    //region 分组聚合相关

    /***
     *
     * @param index
     * @param queries
     * @param agg 分组聚合
     * @param beginTimeField 发布時间字段
     * @param endTimeField    失效時间字段
     * @param showType 显示的类型:0:年份+月份,1:年份,2:月份
     * @return
     * 返回值含有null, 则数据不存在这条纪录;
     * 返回值含有Double类型Infinity,则数据中这条纪录存储null;
     * NaN
     */
    Map<String, Map<String, Object>> getAggregateGroupAndDateRange(String index, List<Query> queries, AggBuilderService agg, String beginTimeField, String endTimeField, int showType);

    Map<String, Map<String, Object>> getAggregateGroupAndDateRange2(String index, List<Query> queries, List<AggAggregate> agg, String beginTimeField, String endTimeField, int showType);

    /**
     * 分组聚合相关接口【分组聚合推荐使用ES7Service.getAggregationsGroup和ES7Service.getAggregationsGroupsAgg】
     *
     * @param index
     * @param queries
     * @param total
     * @param aggBuilders
     * @return 聚合:Map<String,Object>;单字段分组:List<String>;其它:List<Map<String,Object>>
     */
    @Deprecated
    Object getAggregations(String index, List<Query> queries, int total, AggBuilderService... aggBuilders);

    @Deprecated
    Object getAggregationsTmp(String index, List<Query> queries, int total, AggBuilderService... aggBuilders);

    @Deprecated
    Object getAggregations(String index, List<Query> queries, int total, AggGroup aggGroup);

    /**
     * 分页 分组聚合相关接口【分组聚合相关推荐使用getAggregationsGroup和getAggregationsGroupsAgg】
     *
     * @param index
     * @param queries
     * @param page
     * @param aggBuilder
     * @return
     */
    Page<Object> getAggregations(String index, List<Query> queries, Page page, AggBuilderService aggBuilder);
    //endregion

    //region 增删改

    /**
     * @author: 王坤造
     * @date: 2017/2/22 14:43
     * @comment: 新增/替换
     * @return:
     * @notes: 修改操作也要传入全部字段
     */
    String insertOrReplace(String index, Object esid, Map<String, Object> map);

    /**
     * @author: 王坤造
     * @date: 2017/3/15 16:25
     * @comment: 新增/修改字段
     * @return:
     * @notes: 1.必须传esid
     * 2.需要修改的字段和值
     */
    boolean insertOrUpdateFields(String index, Object esid, Map<String, Object> map);

    boolean insertOrUpdateFields(String index, Object esid, Map<String, Object> map, boolean retryOnConflict);

    /**
     * @author: 王坤造
     * @date: 2017/3/22 14:27
     * @comment: 真正物理删除
     * @return:
     * @notes:
     */
    <T> boolean deleteReal(String index, T esid);

    /**
     * @author: 王坤造
     * @date: 2017/4/21 13:45
     * @comment: 清空索引底下全部数据【慎用】
     * @return:
     * @notes: 未來会增加一个新接口, 到时用新接口替换
     */
    @Deprecated
    boolean deleteIndexAllData(String index);
    //endregion

    //region 批量

    /**
     * @author: 王坤造
     * @date: 2018/3/27 15:09
     * @comment: 批量新增/替换【要传入全部字段,包括esid】
     * @return: 如果有新增/替换失败,返回新增/替换失败对象
     * @notes:
     */
    List<Map<String, Object>> insertOrReplaceBulk(String index, List<Map<String, Object>> list);

    /**
     * 批量新增/替换【要传入全部字段,包括esid】【只能newsearch项目中使用,不能在dubbo服务中调用】
     *
     * @param index
     * @param list
     * @param supplierError
     * @param consumerError
     * @param supplierRight
     * @param consumerRight
     * @param <E>
     * @param <R>
     * @return 如果有新增/替换失败,返回自定义对象
     */
    <E, R> Tuple insertOrReplaceBulk(String index, List<Map<String, Object>> list, Supplier<E> supplierError, BiConsumer<E, ? super Tuple> consumerError, Supplier<R> supplierRight, BiConsumer<R, ? super Tuple> consumerRight);

    /**
     * @author: 王坤造
     * @date: 2018/5/16 10:56
     * @comment: 批量修改部分字段【必须传esid字段】
     * @return:
     * @notes:
     */
    List<Map<String, Object>> updateFieldsBulk(String index, List<Map<String, Object>> list);

    /**
     * @author: 王坤造
     * @date: 2017/3/22 14:27
     * @comment: 真正物理删除(根据esid删除)
     * @return:
     * @notes:
     */
    <T> List<T> deleteRealBulk(String index, List<T> list);
    //endregion

    //region 查询更新/删除

    /**
     * @author: 王坤造
     * @date: 2017/10/21 16:41
     * @comment: 查询更新
     * @return: 更新失败的对象
     * @notes:
     */
    List<Map<String, Object>> updateBySelect(String index, List<Query> queries, Map<String, Object> updateFields);

    /**
     * 查询後操作【只能newsearch项目中使用,不能在dubbo服务中调用】
     *
     * @param index
     * @param queries
     * @param consumer
     * @param showFields
     */
    @Deprecated
    void updateBySelect(String index, List<Query> queries, Consumer<List<Map<String, Object>>> consumer, String... showFields);

    @Deprecated
    void updateBySelect(String index, List<Query> queries, Consumer<List<Map<String, Object>>> consumer, LinkedHashMap<String, Boolean> sorts, String... showFields);


    /**
     * 查询删除【後面可能会优化】
     *
     * @param index
     * @param queries
     */
    @Deprecated
    void deleteBySelect(String index, List<Query> queries);
    //endregion

    //region 树相关

    /**
     * 获取所有根结点
     *
     * @param index
     * @param queries
     * @param page       至多获取10000条
     * @param showFields
     * @return
     */
    Page<Map<String, Object>> treeGetRoots(String index, List<Query> queries, Page<Map<String, Object>> page, String... showFields);

    Page<Map<String, Object>> treeGetRootsAudit(String index, Page<Map<String, Object>> page);

    /**
     * 获取所有节点
     * 至多获取10000条数据
     *
     * @param index
     * @param uniqueESID
     * @return
     */
    TreeCube treeGetAll(String index, String uniqueESID, String... showFields);

    TreeCube treeGetAllAudit(String index, String uniqueESID);

    /**
     * 根据查询条件获取节点所在的树
     *
     * @param index
     * @param queries
     * @param showFields
     * @return
     */
    List<TreeCube> treeGetTrees(String index, List<Query> queries, String... showFields);

    boolean treeAdd(String index, TreeCube tc);

    /**
     * 删除所有节点
     *
     * @param index
     * @param uniqueESID
     * @return
     */
    void treeDeleteAll(String index, String uniqueESID);

    /**
     * 保存整颗树
     *
     * @param index
     * @param uniqueESID
     * @param tcAfter    data中必须含有:FIELD_TOPIC和ESID_FIELD,如果含有子节点,则必须设置childrenTreeCubes
     */
    void treeSaveAll(String index, String uniqueESID, TreeCube tcAfter, String... topicFields);

    boolean treeSaveAllAudit(String index, String uniqueESID, TreeCube tcAfter, String... topicFields);

    void treeAuditPass(String uniqueESID, long createTime);

    void treeAuditCancel(String uniqueESID, long createTime);

    List<Map<String, Object>> treeGetAuditOperateHistory(String uniqueESID);
    //endregion
}