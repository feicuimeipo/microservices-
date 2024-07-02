package com.nx.common.model.constant;

import com.nx.common.model.bo.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用状态枚举
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum implements IntArrayValuable {

    ENABLE(0, "开启"),
    DISABLE(1, "关闭");

    public static final int[] ARRAYS = Arrays.stream(CommonStatusEnum.values()).mapToInt(CommonStatusEnum::getStatus).toArray();


    /**
     * 状态值
     */
    private final Integer status;


    /**
     * 状态名
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
