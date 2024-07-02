
package com.nx.datasource.jdbc;

/**
 * 
 * @description <br>
 * @author <a href="mailto:vakinge@gmail.com">jiangwei</a>
 * @date 2022年6月14日
 */
public class JdbcExecParam {
	private String sql;
	private Object[] params;
	
	public JdbcExecParam(String sql, Object[] params) {
		super();
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

}
