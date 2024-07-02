
package com.nx.datasource.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.nx.common.context.SpringUtils;
import com.nx.common.crypt.util.SimpleCryptUtils;
import com.nx.common.exception.BaseException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class DataSourceGroups {
	public static final String CRYPT_PREFIX = "{Cipher}";
	private static DataSource defaultDataSource;
	private static Map<String, DataSource> groupDataSources = new HashMap<String, DataSource>();

	public static DataSource getDefaultDataSource() {
		if(defaultDataSource != null)return defaultDataSource;
		synchronized (DataSourceGroups.class) {
			Map<String, DataSource> map = SpringUtils.getBeansOfType(DataSource.class);
			if(map.isEmpty())return null;
			if(map.size() == 1) {
				defaultDataSource = map.values().stream().findFirst().get();
			}else {
				defaultDataSource = map.get("defaultDataSource");
			}
		}
		return defaultDataSource;
	}
	
	public static DataSource getDataSource(String group) {
		if(groupDataSources.containsKey(group)) {
			return groupDataSources.get(group);
		}
		if(!SpringUtils.Env.containsProperty(group + ".dataSource.url")) {
			throw new BaseException("未找到[" + group + "]数据库配置");
		}
		synchronized (groupDataSources) {
			if(groupDataSources.containsKey(group)) {
				return groupDataSources.get(group);
			}
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.setDriverClassName(SpringUtils.Env.getProperty(group + ".dataSource.driverClassName","com.mysql.cj.jdbc.Driver"));
			dataSource.setUrl(SpringUtils.Env.getProperty(group + ".dataSource.url"));
			dataSource.setUsername(SpringUtils.Env.getProperty(group + ".dataSource.username"));
			String password = SpringUtils.Env.getProperty(group + ".dataSource.password");
			if(password.startsWith(CRYPT_PREFIX)) {
				password = SimpleCryptUtils.decrypt(password.replace(CRYPT_PREFIX, ""));
			}
			dataSource.setPassword(password);
			dataSource.setMaxActive(SpringUtils.Env.getInt(group + ".dataSource.maxActive", 2));
			dataSource.setMinIdle(1);
			dataSource.setDefaultAutoCommit(true);
			dataSource.setTestOnBorrow(true);
			try {
				dataSource.init();
			} catch (SQLException e) {
				dataSource.close();
				throw new RuntimeException(e);
			}
			groupDataSources.put(group, dataSource);
			
			return dataSource;
		}
	}
	
	public static void close() {
		Collection<DataSource> dataSources = groupDataSources.values();
		for (DataSource dataSource : dataSources) {
			try {
				((DruidDataSource)dataSource).close();
			} catch (Exception e) {
				
			}
		}
	}
}
