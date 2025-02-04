package com.nx.logger.enums;

import com.nx.logger.model.api.dto.OperateLogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 查询
     *
     * 绝大多数情况下，不会记录查询动作，因为过于大量显得没有意义。
     * 在有需要的时候，通过声明 {@link OperateLogDTO} 注解来记录
     */
    GET(1),
    /**
     * 新增
     */
    CREATE(2),
    /**
     * 修改
     */
    UPDATE(3),
    /**
     * 删除
     */
    DELETE(4),
    /**
     * 导出
     */
    EXPORT(5),
    /**
     * 导入
     */
    IMPORT(6),
    /**
     * 其它
     *
     * 在无法归类时，可以选择使用其它。因为还有操作名可以进一步标识
     */
    OTHER(0),


            ;
    /**
     * 类型
     */
    private final Integer type;

}