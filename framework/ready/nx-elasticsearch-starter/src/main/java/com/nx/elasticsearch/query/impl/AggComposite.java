package com.nx.elasticsearch.query.impl;//package com.nx.server.newsearch.entity.query;

import com.nx.elasticsearch.constant.AggregationType;
import com.nx.elasticsearch.query.AbstractAggSize;
import com.nx.elasticsearch.query.AggBuilderService;
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
 * 时间：2018/4/12
 * 名称：
 * 备注：
 */
public class AggComposite extends AbstractAggSize implements Serializable, AggBuilderService, AggSubAggBuilderService {
	//别名
	private String alias;
	//大小,默认获取全部
	private int size = DEFAULT_SIZE;
	//多字段分组对象
	private AggBuilderService[] chiAggBuilders;
	//聚合函数或者其它
	private List<AggBuilderService> aggBuilderServices = Collections.EMPTY_LIST;
	//是否使用最大size
	private boolean useMaxSize = false;

	public AggComposite() {
	}

	private AggComposite(String alias, AggBuilderService... chiAggBuilders) {
		EsUtils.judgeStringNotNull("alias", alias);
		EsUtils.judgeIterLen2AndENotNull("chiAggBuilders", chiAggBuilders);
		this.alias = alias;
		this.chiAggBuilders = chiAggBuilders;
	}

	/**
	 * @author: 王坤造
	 * @date: 2018/1/31 10:47
	 * @comment: 获取当前实例对象(非单例对象)
	 * @return:
	 * @notes:
	 */
	public static AggComposite getInstance(String alias, AggBuilderService... chiAggBuilders) {
		return new AggComposite(alias, chiAggBuilders);
	}

	@Override
	public AggComposite setSize(int size) {
		this.size = size;
		return this;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean isUseMaxSize() {
		return useMaxSize;
	}

	@Override
	public AggComposite setUseMaxSize(boolean useMaxSize) {
		this.useMaxSize = useMaxSize;
		return this;
	}

	@Override
	public AggregationType getType() {
		return AggregationType.COMPOSITE;
	}

	@Override
	public AggComposite setAlias(String alias) {
		this.alias = alias;
		return this;
	}

	@Override
	public String getAlias() {
		return this.alias;
	}

	@Override
	public AggComposite addAggBuilders(AggBuilderService... aggBuilderServices) {
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
		sb.append(String.format("AggComposite.getInstance(\"%s\", %s)", alias, StringUtils.join(chiAggBuilders, ", ")));
		if (size != DEFAULT_SIZE) {
			sb.append(String.format(".setSize(%s)", size));
		}
		if (useMaxSize) {
			sb.append(String.format(".setUseMaxSize(%s)", useMaxSize));
		}
		if (CollectionUtils.isNotEmpty(aggBuilderServices)) {
			sb.append(String.format(".addAggBuilders(%s)", StringUtils.join(aggBuilderServices, ", ")));
		}
		return sb.toString();
	}
}
