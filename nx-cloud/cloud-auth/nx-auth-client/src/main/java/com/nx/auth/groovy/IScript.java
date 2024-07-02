/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.groovy;

/**
 * 脚本接口
 * 
 * <pre>
 * 仅为一个标识接口，实现类会自动被注入到Groovy脚本引擎中直接使用。
 * 该接口的实现类被初始化到spring容器中以后，在Groovy脚本中可直接使用实现类的实例名来编写脚本，如：
 * &lt;bean id="scriptImpl" class="com.hotent.platform.service.bpm.ScriptImpl" />
 * 在脚本中可直接使用：
 * scriptImpl.getCurrentUser();
 * </pre>
 * 
 * 
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2018年4月11日
 */
public interface IScript {
}
