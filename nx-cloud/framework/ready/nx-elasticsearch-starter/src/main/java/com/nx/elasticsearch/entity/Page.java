package com.nx.elasticsearch.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = -6334409153410283668L;

    //默认每页条数
    private static final Integer DEFAULT_PAGE_LIMIT = 20;
    //总条数
    private Long totalCount;
    //总页数
    private Integer pages;
    //每页条数
    private Integer limit = DEFAULT_PAGE_LIMIT;
    //当前页数
    private Integer currentPage = 1;
    //分页跳过的条数
    private Long start = 0L;
    //分页的内容
    private List<T> itemList = Collections.emptyList();
    //排序字段(根据esid字段排序,则使用:ESService.ESID_FIELD)
    private String[] orderColumns;
    //排序方式
    private String[] orderDirs;
    //排序(好用!!!用它!!!不用亏大发!!!)
    private LinkedHashMap<String, Boolean> sorts;

    /**
     * 推荐用getSorts
     */
    public String[] getOrderColumns() {
        return orderColumns;
    }

    /**
     * 作者: 王坤造
     * 日期: 2016/12/19 14:20
     * 名称：多个排序字段【推荐用setSorts】
     * 备注：
     */
    public void setOrderColumns(String... orderColumns) {
        this.orderColumns = orderColumns;
    }

    /**
     * 推荐用getSorts
     */
    public String[] getOrderDirs() {
        return orderDirs;
    }

    /**
     * 作者: 王坤造
     * 日期: 2016/12/19 14:21
     * 名称：多个排序方式(asc,desc)【多个排序方式的个数必须和多个排序字段的个数相等】【推荐用setSorts】
     * 备注：
     */
    public void setOrderDirs(String... orderDirs) {
        this.orderDirs = orderDirs;
    }

    /**
     * @author: 王坤造
     * @date: 20/12/25 16:51
     * @comment: 排序
     * @return:
     * @notes:
     */
    public LinkedHashMap<String, Boolean> getSorts() {
        return sorts;
    }

    /**
     * @author: 王坤造
     * @date: 20/12/25 16:51
     * @comment: 排序
     * @return:
     * @notes:
     */
    public Page<T> setSorts(LinkedHashMap<String, Boolean> sorts) {
        this.sorts = sorts;
        return this;
    }

    /**
     * @author: 王坤造
     * @date: 2017/10/25 13:20
     * @comment: 判断是否有下一页
     * @return:
     * @notes:
     */
    public boolean getHasNext() {
        return pages != null && currentPage != null && pages > currentPage;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
        if (this.limit == 0) {
            // 设置limit=0是为了得到分页信息而不查询记录
            this.pages = (totalCount.intValue() + this.limit - 1) / DEFAULT_PAGE_LIMIT;
        } else {
            this.pages = (totalCount.intValue() + this.limit - 1) / this.limit;
        }
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getLimit() {
        return limit;
    }

    /**
     * @author: 王坤造
     * @date: 2018/1/15 11:55
     * @comment: limit:<0则获取全部,=0只获取总条数,>0则获取分页列表
     * @return:
     * @notes:
     */
    public void setLimit(int limit) {
        this.limit = limit;
        if (limit < 1) {
            start = 0L;
        } else {
            start = (long) (currentPage - 1) * this.limit;
        }
    }

    public void setRows(int rows) {
        setLimit(rows);
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = Math.max(currentPage, 1);
        start = (long) (this.currentPage - 1) * limit;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public List<T> getItemList() {
        return itemList;
    }

    public void setItemList(List<T> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "Page{" +
                "totalCount=" + totalCount +
                ", pages=" + pages +
                ", limit=" + limit +
                ", currentPage=" + currentPage +
                ", start=" + start +
                ", itemList=" + itemList +
                ", sorts=" + sorts +
                '}';
    }

    public static class Builder<T> {
        private final Page<T> page;

        public Builder() {
            this.page = new Page<>();
        }

        public Builder<T> currentPage(Integer currentPage) {
            this.page.setCurrentPage(currentPage);
            return this;
        }

        public Builder<T> totalCount(Long totalCount) {
            this.page.setTotalCount(totalCount);
            return this;
        }

        public Builder<T> limit(Integer limit) {
            this.page.setLimit(limit);
            return this;
        }

        public Builder<T> orderColumns(String... orderColumns) {
            this.page.setOrderColumns(orderColumns);
            return this;
        }

        public Builder<T> orderDirs(String... orderDirs) {
            this.page.setOrderDirs(orderDirs);
            return this;
        }

        public Builder<T> itemList(List<T> itemList) {
            this.page.setItemList(itemList);
            return this;
        }

        public Page<T> build() {
            return this.page;
        }
    }
}