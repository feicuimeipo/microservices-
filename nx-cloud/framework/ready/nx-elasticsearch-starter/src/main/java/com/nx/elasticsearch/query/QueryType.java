package com.nx.elasticsearch.query;

/**
 * 名称：王坤造
 * 时间：2017/1/18.
 * 名称：
 * 备注：
 */
public enum QueryType {
    //SQL使用:FIND_IN_SET ('%s', `%s`)
    EQ_LIST,
    //其它处理
    OTHER,

    //region 後面带1个参数
    //大于【不可以有null】
    GT,
    //小于【不可以有null】
    LT,
    //大于等于【不可以有null】
    GE,
    //小于等于【不可以有null】
    LE,
    //endregion

    //region 後面带1+个参数
    //类似于sql中的between范围查询【左右都闭合,不可以有null】
    BN,
    //类似于sql中的not in关键字【都是Or的关系,值都是精确匹配,不可以有null】
    NI,
    //类似于sql中的in关键字【都是Or的关系,值都是精确匹配,不可以有null】
    IN,
    //类似于sql中的in关键字【都是Or的关系,但是值是模糊匹配,不可以有null】
    IN_LIKE,
    //endregion

    //region 後面带(0,1)个参数
    //类似于sql模糊查询【not like '%xxx%'】
    NL,
    //类似于sql模糊查询【like '%xxx%'】
    LIKE,
    //模糊查询不需要顺序一致
    MATCH,
    //不相等
    NE,
    //等于【NULL_VALUE:查询 普通字段类型 不存在/为空字符串("")/为空数组([]);NULL_VALUE_NESTED:查询 json字段类型 不存在】
    EQ,
    //前缀查询
    PREFIX,
    //特殊字符查询【like '%|%'】
    WILDCARD
    //endregion
}