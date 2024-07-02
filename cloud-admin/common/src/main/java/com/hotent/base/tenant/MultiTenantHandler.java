/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.tenant;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotent.ucapi.constant.BaseContext;
import com.pharmcube.api.context.SaaSConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.StringValue;
import org.nianxi.boot.constants.BootConstant;
import org.nianxi.boot.support.AppUtil;
import org.nianxi.mybatis.db.constant.SQLConst;
import org.nianxi.utils.BeanUtils;
import org.nianxi.utils.JsonUtil;
import org.nianxi.utils.StringUtil;
import org.nianxi.x7.api.UCApi;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 多租户处理器
 *
 * @company 广州宏天软件股份有限公司
 * @author heyifan
 * @email heyf@jee-soft.cn
 * @date 2020年4月6日
 */
@Slf4j
@Component
public class MultiTenantHandler implements TenantLineHandler{//TenantHandler {

   final private SaaSConfig saaSConfig;

   final private BaseContext apiContext;


    @Getter
    @Setter
    private volatile static List<String> ignoreTableNames = new ArrayList<>();

    /**
     * 多租户标识
     */
    private String tenantIdColumn = "tenant_id_";


    private static ThreadLocal<Boolean> threadLocalIgnore = new ThreadLocal<Boolean>();

    public MultiTenantHandler(SaaSConfig saaSConfig, BaseContext apiContext) {
        this.saaSConfig = saaSConfig;
        this.apiContext = apiContext;
    }


    /**
     * 线程变量 临时忽略 自动添加租户id的操作
     */
    public static MultiTenantIgnoreResult setThreadLocalIgnore() {
    	threadLocalIgnore.set(true);
    	return new MultiTenantIgnoreResult();
    }
    
    public static Boolean getThreadLocalIgnore() {
    	Boolean ignoreTable = threadLocalIgnore.get();
    	if(BeanUtils.isNotEmpty(ignoreTable) && ignoreTable) {
    		return true;
    	}
    	return false;
    }
    
    public static void removeThreadLocalIgnore() {
    	threadLocalIgnore.remove();
    }
    
    /**	
     * 租户Id
     *
     * @return
     */
    @Override
    public Expression getTenantId() {
        // 从当前系统上下文中取出当前请求的服务商ID，通过解析器注入到SQL中。
        String tenantId = apiContext.getCurrentTenantId();
        //log.debug("当前租户为{}", tenantId);
        if (tenantId == null) {
            return new NullValue();
        }
        return new StringValue(tenantId);
    }
    
    /**
     * 获取当前用户所属租户的租户CODE
     * <pre>
     * 如果当前是平台管理用户则返回null
     * </pre>
     * @return
     */
    public String getTenantCode() {
    	// 租户模式下生成物理表时需要在表名中追加租户别名
		if(saaSConfig.isEnable()) {
			String currentTenantId = apiContext.getCurrentTenantId();
			// 非平台管理用户
			if(!BootConstant.PLATFORM_TENANT_ID.equals(currentTenantId)) {
				UCApi ucFeign = AppUtil.getBean(UCApi.class);
				JsonNode tenantManage = ucFeign.getTenantById(currentTenantId);
				Assert.notNull(tenantManage, "未获取到当前用户所属的租户信息");
				String tenantCode = JsonUtil.getString((ObjectNode)tenantManage, "code");
				Assert.isTrue(StringUtil.isNotEmpty(tenantCode), "租户中的租户别名为空");
				return tenantCode;
			}
		}
		return null;
    }
    
    /**
     * 设置租户字段名
     * @param tenantId
     */
    public void setTenantId(String tenantId) {
    	this.tenantIdColumn = tenantId;
    }
    

    /**
     * 获取忽略的表名列表
     * @return
     */
    public List<String> getIgnoreTableNames() {
    	return saaSConfig.getIgnoreTables();
    }

    /**
     * 根据表名判断是否忽略拼接多租户条件
     *
     * @param tableName
     * @return
     */
    @Override
    public boolean ignoreTable(String tableName) {
        return doTableFilter(tableName);
    }

    /**
     * 租户字段名
     *
     * @return
     */
    @Override
    public String getTenantIdColumn() {
        return tenantIdColumn;
    }

    /**
     * 根据表名判断是否进行过滤
     * 忽略掉一些表：如租户表（sys_tenant）本身不需要执行这样的处理
     *
     * @param tableName
     * @return
     */
    public boolean doTableFilter(String tableName) {
    	if(getThreadLocalIgnore()) {
    		return true;
    	}
    	if(StringUtil.isNotEmpty(tableName) && tableName.toUpperCase().startsWith(SQLConst.CUSTOMER_TABLE_PREFIX)){
            return true;
        }
        return saaSConfig.getIgnoreTables().stream().anyMatch((e) -> e.equalsIgnoreCase(tableName));
    }

    public void setIgnoreTableNames(List<String> ignoreTables) {
        this.ignoreTableNames = ignoreTables;
    }
}