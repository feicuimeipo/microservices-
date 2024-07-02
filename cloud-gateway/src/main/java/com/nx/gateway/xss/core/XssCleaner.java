
package com.nx.gateway.xss.core;

/**
 * xss 清理器
 */
public interface XssCleaner {

	/**
	 * 清理 html
	 *
	 * @param html html
	 * @return 清理后的数据
	 */
	String clean(String html);

}
