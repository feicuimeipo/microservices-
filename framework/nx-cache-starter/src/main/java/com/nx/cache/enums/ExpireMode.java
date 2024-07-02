package com.nx.cache.enums;

/**
 * 缓存失效模式
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月15日
 */
public enum ExpireMode {
    /**
     * 每写入一次重新计算一次缓存的有效时间
     */
    WRITE("最后一次写入后到期失效"),

    /**
     * 每访问一次重新计算一次缓存的有效时间
     */
    ACCESS("最后一次访问后到期失效");

    private String label;

    ExpireMode(String label) {
    	this.label = label;
    }

	public String getLabel() {
		return label;
	}
}