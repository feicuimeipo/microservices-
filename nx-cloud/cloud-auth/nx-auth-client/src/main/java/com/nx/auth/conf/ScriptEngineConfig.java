/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.nx.auth.conf;

import com.nx.auth.groovy.GroovyScriptEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScriptEngineConfig {
	@Bean
	public GroovyScriptEngine getGroovyScriptEngine(){
		return new GroovyScriptEngine();
	}
}
