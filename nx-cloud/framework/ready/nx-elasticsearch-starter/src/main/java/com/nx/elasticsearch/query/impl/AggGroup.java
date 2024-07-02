package com.nx.elasticsearch.query.impl;

import com.nx.elasticsearch.constant.AggregationType;
import com.nx.elasticsearch.query.AbstractAggSize;
import com.nx.elasticsearch.query.AggBuilderService;
import com.nx.elasticsearch.query.AggFieldService;
import com.nx.elasticsearch.query.AggSubAggBuilderService;
import com.nx.elasticsearch.utils.EsUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 作者：王坤造
 * 时间：2018/1/27
 * 名称：分组
 * 备注：
 */
public class AggGroup extends AbstractAggSize implements Serializable, AggBuilderService, AggFieldService, AggSubAggBuilderService {

    //单字段分组字段名
    private String field;
    //单字段分组别名/多字段分组第1个别名
    private String alias;
    //排序方式(单字段字段排序或聚合函数排序,多字段聚合函数排序,复合方式无效)
    private Order order;
    //大小,默认获取全部
    private int size = DEFAULT_SIZE;
    //多字段分组对象
    private AggGroup[] aggGroups;
    //必须包含的分组值(单字段分组有效)
    private String[] includeFields;
    //默认多字段分组使用复合方式,True:原始多字段分组
    private boolean useMulTerms = false;
    //是否使用最大size(目前在单字段分组和多字段分组(复合查询)中使用)
    private boolean useMaxSize = false;
    //不存在时设置的默认值(原始多字段分组时使用)
    private Object missingValue = null;
    //聚合函数
    private List<AggBuilderService> aggBuilderServices = Collections.EMPTY_LIST;

    /**
     * @author: 王坤造
     * @date: 2018/1/28 16:34
     * @comment: dubbo调用的时候必须提供默认构造函数
     * @return:
     * @notes:
     */
    public AggGroup() {
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/29 15:15
     * @comment: 单字段分组的构造函数
     * @return:
     * @notes:
     */
    private AggGroup(String field, String alias) {
        EsUtils.judgeStringNotNull("field", field);
        this.field = field;
        if (StringUtils.isEmpty(alias)) {
            this.alias = field;
        } else {
            this.alias = alias;
        }
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/29 15:15
     * @comment: 多字段分组的构造函数, 以单字段分组的构造函数为元素
     */
    private AggGroup(AggGroup... aggGroups) {
        EsUtils.judgeIterLen2AndENotNull("aggGroups", aggGroups);
        this.aggGroups = aggGroups;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggGroup getInstance(String field) {
        return new AggGroup(field, null);
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggGroup getInstance(String field, String alias) {
        return new AggGroup(field, alias);
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/31 10:47
     * @comment: 获取当前实例对象(非单例对象)
     * @return:
     * @notes:
     */
    public static AggGroup getInstance(AggGroup... aggGroups) {
        return new AggGroup(aggGroups);
    }

    public AggGroup[] getAggGroups() {
        return aggGroups;
    }

    public Order getOrder() {
        return order;
    }

    public AggGroup setOrder(Order order) {
        this.order = order;
        return this;
    }

    public String[] getIncludeFields() {
        return includeFields;
    }

    public AggGroup setIncludeFields(String... includeFields) {
        this.includeFields = includeFields;
        return this;
    }

    public boolean isUseMulTerms() {
        return useMulTerms;
    }

    public AggGroup setUseMulTerms(boolean useMulTerms) {
        this.useMulTerms = useMulTerms;
        return this;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/28 21:06
     * @comment: 获取查询类型
     * @return:
     * @notes:
     */
    @Override
    public AggregationType getType() {
        if (CollectionUtils.isEmpty(aggBuilderServices)) {
            if (ArrayUtils.isEmpty(aggGroups)) {
                return AggregationType.SINGLE_GROUP;
            }
            return AggregationType.MULTIPLE_GROUP;
        }
        if (ArrayUtils.isEmpty(aggGroups)) {
            return AggregationType.SINGLE_GROUP_AGGREGATE;
        }
        return AggregationType.MULTIPLE_GROUP_AGGREGATE;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public AggGroup setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public AggGroup setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean isUseMaxSize() {
        return useMaxSize;
    }

    @Override
    public AggGroup setUseMaxSize(boolean useMaxSize) {
        this.useMaxSize = useMaxSize;
        return this;
    }

    public Object getMissingValue() {
        return missingValue;
    }

    public AggGroup setMissingValue(Object missingValue) {
        this.missingValue = missingValue;
        return this;
    }

    @Override
    public AggGroup addAggBuilders(AggBuilderService... aggBuilderServices) {
        if (ArrayUtils.isNotEmpty(aggBuilderServices)) {
            if (CollectionUtils.isEmpty(this.aggBuilderServices)) {
                this.aggBuilderServices = new ArrayList<>(aggBuilderServices.length);
            }
            this.aggBuilderServices.addAll(Arrays.asList(aggBuilderServices));
        }
        return this;
    }

    @Override
    public List<AggBuilderService> getAggBuilders() {
        return aggBuilderServices;
    }

    @Override
    public AggBuilderService[] getAggBuilders2() {
        return aggBuilderServices.toArray(new AggBuilderService[aggBuilderServices.size()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (ArrayUtils.isNotEmpty(aggGroups)) {
            sb.append(String.format("AggGroup.getInstance(%s)", StringUtils.join(aggGroups, ", ")));
        } else if (field.equals(alias)) {
            sb.append(String.format("AggGroup.getInstance(\"%s\")", field));
        } else {
            sb.append(String.format("AggGroup.getInstance(\"%s\", \"%s\")", field, alias));
        }
        if (order != null) {
            sb.append(String.format(".setOrder(%s)", order));
        }
        if (size != DEFAULT_SIZE) {
            sb.append(String.format(".setSize(%s)", size));
        }
        if (ArrayUtils.isNotEmpty(includeFields)) {
            sb.append(String.format(".setIncludeFields(%s)", StringUtils.join(Arrays.stream(includeFields).map(o -> String.format("\"%s\"", o)).iterator(), ", ")));
        }
        if (useMulTerms) {
            sb.append(String.format(".setUseMulTerms(%s)", useMulTerms));
        }
        if (useMaxSize) {
            sb.append(String.format(".setUseMaxSize(%s)", useMaxSize));
        }
        if (CollectionUtils.isNotEmpty(aggBuilderServices)) {
            sb.append(String.format(".addAggBuilders(%s)", StringUtils.join(aggBuilderServices, ", ")));
        }
        return sb.toString();
    }

    /**
     * @author: 王坤造
     * @date: 2018/2/1 14:41
     * @comment: 分组的排序对象
     * @return:
     * @notes:
     */
    public static class Order implements Serializable {
        private static final byte one = 1;
        private static final byte two = 2;
        private static final byte three = 3;
        private static final byte four = 4;
        //用來判断排序方式
        private byte typeValue;
        private boolean asc;
        private String name;
        private Order[] orders;
        //private static Order orderCount1;
        //private static Order orderCount0;
        //private static Order orderField1;
        //private static Order orderField0;
        //
        //static {
        //	orderCount1 = new Order(one, true, null);
        //	orderCount0 = new Order(one, false, null);
        //	orderField1 = new Order(two, true, null);
        //	orderField0 = new Order(two, false, null);
        //}

        public Order() {
        }

        private Order(byte typeValue, boolean asc, String name) {
            this.typeValue = typeValue;
            this.asc = asc;
            if (typeValue == three) {
                EsUtils.judgeStringNotNull("name", name);
                this.name = name;
            }
        }

        private Order(Order... orders) {
            EsUtils.judgeIterLen2AndENotNull("orders", orders);
            this.orders = orders;
            this.typeValue = four;
        }

        /**
         * @author: 王坤造
         * @date: 2018/2/1 19:51
         * @comment: 多种排序方式【默认:添加当前分组字段升序】
         * @return:
         * @notes:
         */
        public static Order orderCompound(Order... orders) {
            return new Order(orders);
        }

        /**
         * @author: 王坤造
         * @date: 2018/2/1 19:50
         * @comment: 以分组後对应的count条数进行排序
         * @return:
         * @notes:
         */
        public static Order orderCount(boolean asc) {
            return new Order(one, asc, null);
        }

        /**
         * @author: 王坤造
         * @date: 2018/2/1 19:49
         * @comment: 以当前分组字段进行排序【排序结果为全局有序】
         * @return:
         * @notes:
         */
        public static Order orderField(boolean asc) {
            return new Order(two, asc, null);
        }

        /**
         * @author: 王坤造
         * @date: 2018/2/1 19:50
         * @comment: 以子聚合进行排序【排序结果为部分有序,并非全局有序】
         * @return:
         * @notes:
         */
        public static Order orderAggregation(boolean asc, String name) {
            return new Order(three, asc, name);
        }

        public byte getTypeValue() {
            return typeValue;
        }

        public boolean isAsc() {
            return asc;
        }

        public String getName() {
            return name;
        }

        public Order[] getOrders() {
            return orders;
        }

        @Override
        public String toString() {
            if (typeValue == one) {
                return String.format("AggGroup.Order.orderCount(%s)", asc);
            } else if (typeValue == two) {
                return String.format("AggGroup.Order.orderField(%s)", asc);
            } else if (typeValue == three) {
                return String.format("AggGroup.Order.orderAggregation(%s, \"%s\")", asc, name);
            }
            return String.format("AggGroup.Order.orderCompound(%s)", StringUtils.join(orders, ", "));
        }
    }
}