package com.nx.elasticsearch.constant;

import java.util.HashSet;
import java.util.Set;


public class ESConstants {
	//region a常量
		//分页查询最大的条数(es最大限制10000)
	public static final int MAX_SIZE_PAGE = 10000;
	//分组每个bucket最多2000个
	public static final int SIZE_GROUP = 2000;
	//分组每个bucket最大10000
	public static final int MAX_SIZE_GROUP = 10000;
	public static final int MAX_SIZE_GROUP2 = 200000;
	//聚合函数count(distinct xxx)中准确计算最多条数,超过20000则有误差,es默认最大值:40000
	public static final int MAX_SIZE_CARDINALITY = 40000;
	//esid字段名
	public static final String ESID_FIELD = "esid";
	//es内部使用esid字段名
	public static final String ES_ID_FIELD = "_id";
	//查询时按匹配分数字段进行排序字段名
	public static final String SCORE_FIELD = "_score";
	//分页时,每页默认条数
	public static final int PAGE_SIZE = 20;
	//普通分页时,每页最大条数
	public static final int MAX_PAGE_SIZE = 1000;
	//滚动查询分页条数
	public static final int SCROLL_SIZE = 1000;
	private static final int[] SCROLL_SIZES = {1000, 900, 800, 700, 600, 500, 400, 300, 200};
	//别名後缀
	public static final String ALIAS_SUFFIX = "alias";
	//查询时不返回其它字段只返回esid时设置的字段
	public static final String NULL_VALUE = "None";
	private static final String[] STOREDFIELDS_NONE = {"_none_"};
	private static final Set<String> FIELDS_ESID_OR_NONE = new HashSet<String>() {{
		add(ES_ID_FIELD);
		add(ESID_FIELD);
		add(NULL_VALUE);
	}};

}
