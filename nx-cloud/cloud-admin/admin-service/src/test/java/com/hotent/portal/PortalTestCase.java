/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal;

import javax.annotation.Resource;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import org.nianxi.id.IdGenerator;




@SpringBootTest(classes=Application.class)
@Transactional
public class PortalTestCase {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	protected IdGenerator idGenerator;
	
	@Test
	public void Test(){
		logger.debug("X7 bo test initialize.");
	}
}
