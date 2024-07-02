package com.nx.elasticsearch.query;

/**
 * 作者：王坤造
 * 时间：2018/1/31
 * 名称：
 * 备注：
 */
public abstract class AbstractAggSize<T> {

    //大小,默认获取全部
    protected static final int DEFAULT_SIZE = -1;

    /**
     * @author: 王坤造
     * @date: 2018/1/31 14:25
     * @comment: 设置大小
     * @return:
     * @notes:
     */
    public abstract T setSize(int size);

    /**
     * @author: 王坤造
     * @date: 2018/1/31 14:25
     * @comment: 获取大小
     * @return:
     * @notes:
     */
    public abstract int getSize();

    /**
     * @author: 王坤造
     * @date: 2018/1/31 14:25
     * @comment: 设置大小是否限制
     * @return:
     * @notes:
     */
    public abstract T setUseMaxSize(boolean useMaxSize);

    /**
     * @author: 王坤造
     * @date: 2018/1/31 14:25
     * @comment: 获取大小是否限制
     * @return:
     * @notes:
     */
    public abstract boolean isUseMaxSize();

}
