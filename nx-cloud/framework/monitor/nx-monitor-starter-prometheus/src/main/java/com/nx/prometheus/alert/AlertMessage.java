
package com.nx.prometheus.alert;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * alert hook
 */
@Data
public class AlertMessage implements Serializable {

	/**
	 * 版本号
	 */
	private String version;
	/**
	 * 由于 “max_alerts” 而截断了多少警报
	 */
	private Integer truncatedAlerts;
	/**
	 * 分组 key
	 */
	private String groupKey;
	/**
	 * 状态 resolved|firing
	 */
	private String status;
	/**
	 * 接收者
	 */
	private String receiver;
	/**
	 * 分组 labels
	 */
	private Map<String, String> groupLabels;
	/**
	 * 通用 label
	 */
	private Map<String, String> commonLabels;
	/**
	 * 通用注解
	 */
	private Map<String, String> commonAnnotations;
	/**
	 * 扩展 url 地址
	 */
	private String externalURL;
	/**
	 * alerts
	 */
	private List<AlertInfo> alerts;

}
