/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.base.model;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class HtJsonNodeFactory extends JsonNodeFactory{
	private static final long serialVersionUID = -2528062905576193172L;

	public static HtJsonNodeFactory build(boolean bigDecimalExact) {
		return new HtJsonNodeFactory(bigDecimalExact);
	}
	
	public static HtJsonNodeFactory build() {
		return new HtJsonNodeFactory();
	}

	public HtJsonNodeFactory() {
		this(false);
	}
	
	public HtJsonNodeFactory(boolean bigDecimalExact) {
		super(bigDecimalExact);
	}

	public HtObjectNode htObjectNode(ObjectNode objectNode) {
		return new HtObjectNode(this, objectNode);
	}
}
