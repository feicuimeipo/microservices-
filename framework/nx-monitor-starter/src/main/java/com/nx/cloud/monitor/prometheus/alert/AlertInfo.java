
package com.nx.cloud.monitor.prometheus.alert;

import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 告警模型
 */
@Data
public class AlertInfo implements Serializable {

	/**
	 * 状态 resolved|firing
	 */
	private String status;
	/**
	 * 标签集合
	 */
	private Map<String, String> labels;
	/**
	 * 注释集合
	 */
	private Map<String, String> annotations;
	/**
	 * 开始时间
	 */
	private OffsetDateTime startsAt;
	/**
	 * 结束时间
	 */
	private OffsetDateTime endsAt;
	/**
	 * identifies the entity that caused the alert
	 */
	private String generatorURL;
	/**
	 * fingerprint to identify the alert
	 */
	private String fingerprint;

}
