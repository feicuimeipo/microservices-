package com.nx.common.model;


import com.nx.common.model.bo.PageParam;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public final class PageResult<T> implements Serializable {

    private List<T> list;

    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    public static <T> PageResult<T> empty() {
        return new PageResult(0L);
    }

    public static <T> PageResult<T> empty(Long total) {
        return new PageResult(total);
    }


    public static class PageUtils {

        public static int getStart(PageParam pageParam) {
            return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
        }

    }

}
