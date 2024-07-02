package com.nx.elasticsearch.query;

import com.nx.elasticsearch.constant.AggregationType;

/**
 * 作者：王坤造
 * 时间：2018/1/27
 * 名称：
 * 备注：
 */
public interface AggBuilderService<T> {
    /**
     * @author: 王坤造
     * @date: 2018/1/27 17:14
     * @comment: 获取查询类型
     * @return:
     * @notes:
     */
    AggregationType getType();

    /**
     * @author: 王坤造
     * @date: 2018/1/30 10:18
     * @comment: 设置别名
     * @return: 返回实现类
     * @notes:
     */
    T setAlias(String alias);

    /**
     * @author: 王坤造
     * @date: 2018/1/30 10:19
     * @comment: 获取别名
     * @return: 返回字符串
     * @notes:
     */
    String getAlias();
}
