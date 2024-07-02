/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.util;

import com.google.common.base.Joiner;
import com.hotent.base.id.UniqueIdUtil;
import com.pharmcube.boot.support.AppUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nianxi.boot.constants.BootConstant;
import org.nianxi.utils.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.Map.Entry;

/**
 * <pre>
 * 
 * 租户相关工具类
 * </pre>
 * @author jason
 * @Date 2020-04-23
 */
public class TenantUtil {
	
	private static Log logger = LogFactory.getLog(TenantUtil.class);
	
	/**
	 * <pre>
	 * 初始化租户数据
	 * </pre>
	 * @param tenantId
	 * @param tableNames
	 */
	public static void initData(String tenantId, List<String> tableNames) {
		JdbcTemplate jdbcTemplate = AppUtil.getBean(JdbcTemplate.class);
		logger.debug("开始初始化租户的数据");
		for (String tableName : tableNames) {
			String sql = String.format("select * from %s where tenant_id_ = %s", tableName, BootConstant.PLATFORM_TENANT_ID);
			
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql);
			List<String> updateSqls = new ArrayList<String>();
 			for (Map<String,Object> map : queryForList) {
 				
 				Iterator<Entry<String,Object>> iter = map.entrySet().iterator();
 				List<String> fields = new ArrayList<String>();
 				List<Object> params = new ArrayList<Object>();
 				Object id = null;
 				while(iter.hasNext()){
 				   Entry<String,Object> entry = iter.next();
 				   String key = entry.getKey();
 				   
 				   Object value = entry.getValue();
 				   String lowerCase = key.toLowerCase();
 				   switch (lowerCase) {
					case "id_":
						value = UniqueIdUtil.getSuid();
						id = value;
						break;
					case "id":
						value = UniqueIdUtil.getSuid();						
						break;
					case "tenant_id_":
						value = tenantId;
						break;
					case "path_":
						if(tableName.equals("portal_sys_type")) {							
							value = String.valueOf(value).split("\\.")[0]+"."+id+".";
						}
						break;
					}
 				  if(BeanUtils.isNotEmpty(value)) {
 					 fields.add(key);
 	 				 if(value instanceof String || value instanceof Date  ) {
 	 					 value = "\'"+value + "\'";
 	 				 }
 					 params.add(value);
 				  }
 				  
 				}


 				String updateSql = String.format("insert into %s(%s) values(%s)", tableName,Joiner.on(",").join(fields),Joiner.on(",").join(params));
 				updateSqls.add(updateSql);
			}
 			if(BeanUtils.isNotEmpty(updateSqls)&& updateSqls.size()>0){
 				// 分布式事务不支持多sql执行  ONLY SUPPORT SAME TYPE (UPDATE OR DELETE) MULTI SQL 
 				for (String insertSql : updateSqls) {					
 					jdbcTemplate.update(insertSql);
				}
// 				jdbcTemplate.batchUpdate(updateSqls.toArray(new String[updateSqls.size()]));
 			}
		}
		
	
	}
	

}
