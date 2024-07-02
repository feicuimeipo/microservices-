/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.cache.expression;

import org.springframework.expression.EvaluationException;

/**
 * A specific {@link EvaluationException} to mention that a given variable
 * used in the expression is not available in the context.
 *
 * 
 * @author 佚名
 * @email xlnian@163.com
 * @date 2020年6月16日
 */
@SuppressWarnings("serial")
class VariableNotAvailableException extends EvaluationException {

	private final String name;

	public VariableNotAvailableException(String name) {
		super("Variable '" + name + "' is not available");
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
