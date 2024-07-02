/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤
 * <pre>
 * 数据权限过滤
 * </pre>
 *
 * @company 广州宏天软件股份有限公司
 * @author liygui
 * @email liygui@jee-soft.cn
 * @date 2018年9月28日
 */
@Target({ElementType.METHOD}) 
@Retention(RetentionPolicy.RUNTIME) 
public @interface DataPermission {
    //按组件机构
    boolean baseOrgId() default false;
    //按创建者
    boolean baseCreateUser() default false;

}
