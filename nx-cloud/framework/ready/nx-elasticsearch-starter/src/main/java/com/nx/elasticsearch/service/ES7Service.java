package com.nx.elasticsearch.service;

import com.nx.elasticsearch.entity.Page;
import com.nx.elasticsearch.query.Query;
import com.nx.elasticsearch.entity.index_mapping.Mapping;
import com.nx.elasticsearch.query.impl.AggAggregate;
import com.nx.elasticsearch.constant.ESConstants;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: 王坤造
 * @date: 20/12/8 10:28
 * @comment: es7新接口
 * @return: 抛异常/具体对象(不会有null)
 * @notes: 新接口!新代码!新年快乐!
 */
public interface ES7Service {
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

    /***
     * 判断esid是否存在
     * @param index
     * @param esid
     * @return
     */
    boolean getExist(String index, String esid);

    /**
     * 查 根据esid查
     *
     * @param index
     * @param esid
     * @param showFields
     * @return
     */
    Map<String, Object> getOne(String index, String esid, String... showFields);

    /**
     * 根据查询条件查询一条
     *
     * @param index      根据查询条件查询一条(只显示第一条,底下的忽略)
     * @param queries    查询条件集合【查询的字段不存在则返回null,queries为null则查询第一条数据】
     * @param showFields 查询出来返回的字段
     * @return 有异常会抛出
     */
    Map<String, Object> getOne(String index, List<Query> queries, String... showFields);

    /**
     * 查 根据esid查
     *
     * @param index
     * @param esid
     * @param showFields
     * @return
     */
    List<Map<String, Object>> getMulti(String index, String[] esid, String... showFields);

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：查询(分页)
     * 备注：返回对象默认添加esid字段
     * index:索引名称
     * queries:集合/Query对象【查询的字段不存在则返回null,queries为null则查询全部数据】
     * page:分页对象【可以设置当前查询页数,每页条数,排序字段及排序方式】
     * showFields:查询出来返回的字段
     */
    Page<Map<String, Object>> getPage(String index, List<Query> queries, Page page, String... showFields);

    Page<Map<String, Object>> getPageCollapse(String index, List<Query> queries, Page page, String collapse, String... showFields);

    /**
     * 查 new
     *
     * @param index
     * @param queries
     * @param sorts      排序字段和排序方式(true,null:升序;false:降序)(esid禁止排序)
     * @param size       获取条数(size=-1时,获取全部;skip有值时,size+skip<=10000;after有值时,size<=1000)
     * @param skip       跳过条数(优先级比after低,禁止和after同时存在,大于1时size禁止=-1)
     * @param after      排序字段的对应的值(每次分页需要设置上一次分页最後一条数据的值,有值时size禁止=-1)
     * @param showFields 返回字段
     * @return
     */
    List<Map<String, Object>> getList(String index, List<Query> queries, LinkedHashMap<String, Boolean> sorts, int size, int skip, Object[] after, String... showFields);

    List<Map<String, Object>> getListCollapse(String index, String collapse, List<Query> queries, LinkedHashMap<String, Boolean> sorts, int size, int skip, Object[] after, String... showFields);


    /**
     * 分批查(获取10000+)
     *
     * @param index
     * @param queries
     * @param sorts      排序字段和排序方式(true,null:升序;false:降序)(esid禁止排序)
     * @param id         第一次null,第2次传返回的id,第3次传返回的id,第4次传返回的id...
     * @param size       每批返回的条数
     * @param showFields
     * @return 第1个元素是返回列表数据List<Map < String, Object>>;第2个元素是返回分批查的id
     */
    List<Object> getListSub(String index, List<Query> queries, LinkedHashMap<String, Boolean> sorts, String id, int size, String... showFields);

    /**
     * 配合 getListSub(分批 获取数据) 获取指定条数数据时使用【获取指定条数时需要手动执行】
     *
     * @param ids
     */
    void clearScrollID(String index, String... ids);

    /**
     * 作者: 王坤造
     * 日期: 16-11-15 下午5:34
     * 名称：根据查询条件获取总条数
     * 备注：
     * index:索引名称
     * list:查询条件集合【查询的字段不存在则返回0,list为null则查询全部数据条数】
     */
    long getCount(String index, List<Query> queries);



    /**
     * 分页 单字段分组(获取10000+数据)
     *
     * @param index
     * @param queries
     * @param after
     * @param size
     * @param field   esid禁止分组
     * @return
     */
    List<Object> getAggregationsGroup(String index, List<Query> queries, int size, Object after, String field);

    List<Object> getAggregationsGroup(String index, List<Query> queries, int size, Object after, String field, boolean asc);

    /**
     * 分页 多字段分组[聚合] 或 单字段分组聚合(获取10000+数据)
     *
     * @param index
     * @param queries
     * @param after
     * @param size
     * @param fields      esid禁止分组
     * @param aggBuilders
     * @return
     */
    List<Map<String, Object>> getAggregationsGroupsAgg(String index, List<Query> queries, int size, Map<String, Object> after, List<String> fields, AggAggregate... aggBuilders);

    List<Map<String, Object>> getAggregationsGroupsAgg(String index, List<Query> queries, int size, Map<String, Object> after, LinkedHashMap<String, Boolean> sorts, AggAggregate... aggBuilders);
    //endregion

    //region 增删改
    void delete(String index, String esid);

    /**
     * 删除字段
     *
     * @param index
     * @param esid
     * @param fields
     */
    void deleteField(String index, String esid, String... fields);

    /**
     * 新增
     *
     * @param index
     * @param map   必须要有esid
     * @return
     */
    void insert(String index, Map<String, Object> map);

    /**
     * 新增或替换
     *
     * @param index
     * @param map   必须要有esid
     * @return
     */
    void insertOrReplace(String index, Map<String, Object> map);

    /**
     * 新增
     *
     * @param index
     * @param map   必须要有esid
     * @return
     */
    void update(String index, Map<String, Object> map);

    void updateOrInsert(String index, Map<String, Object> map);



    //region 批量
    Map<String, String> bulkDelete(String index, Set<String> list);

    Map<String, String> BulkInsert(String index, List<Map<String, Object>> list);

    Map<String, String> BulkInsertOrReplace(String index, List<Map<String, Object>> list);

    Map<String, String> BulkUpdate(String index, List<Map<String, Object>> list);

    Map<String, String> BulkUpdateOrInsert(String index, List<Map<String, Object>> list);

    /**
     * 全部批量操作後要执行刷新操作
     *
     * @param index
     * @return
     */
    boolean freshes(String... index);
    //endregion

    //region 批量(全量)
    Map<String, String> bulkDeleteAll(String index, Set<String> list);

    Map<String, String> BulkInsertAll(String index, List<Map<String, Object>> list);

    Map<String, String> BulkInsertOrReplaceAll(String index, List<Map<String, Object>> list);

    Map<String, String> BulkUpdateAll(String index, List<Map<String, Object>> list);

    Map<String, String> BulkUpdateOrInsertAll(String index, List<Map<String, Object>> list);
    //endregion


    /**
     * 创建新索引(2021.05.24)
     *
     * @param index   索引名称
     * @param list    excel解析list
     * @param isExist 索引是否存在 存在:修改 不存在:新建索引
     * @return
     */
    boolean indexCreate(String index, List<Mapping> list, boolean isExist);

    /**
     * 增 索引字段(2021.05.24)
     *
     * @param index
     * @param list
     * @return
     */
    boolean indexAdd(String index, List<Mapping> list);
    //endregion

    /**
     * 重导数据后旧索引别名处理(2021.06.01)
     *
     * @param indexName
     * @return
     */
    boolean dealAlias(String indexName);

    /**
     * 获取索引Mapping(2021.06.17)
     *
     * @param indexName
     * @return
     */
    String getMapping(String indexName);

    /**
     * ccr索引重载(2021.08.16)
     *
     * @param indexName
     * @return
     */
    void reLoadCcrIndex(String indexName);

}