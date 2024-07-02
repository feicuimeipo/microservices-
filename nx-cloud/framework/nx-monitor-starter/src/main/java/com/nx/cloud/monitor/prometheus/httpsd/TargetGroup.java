package com.nx.cloud.monitor.prometheus.httpsd;

import java.util.List;
import java.util.Map;

/**
 *
 * prometheus http sd 模型
 */
public class TargetGroup {
	private final List<String> targets;
	private final Map<String, String> labels;

	public TargetGroup(List<String> targets, Map<String, String> labels) {
		this.targets = targets;
		this.labels = labels;
	}

	public List<String> getTargets() {
		return targets;
	}

	public Map<String, String> getLabels() {
		return labels;
	}
}
