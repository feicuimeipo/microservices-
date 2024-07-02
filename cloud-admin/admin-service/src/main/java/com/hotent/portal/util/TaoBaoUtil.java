/*
 * Copyright (c) 2020-2025, All rights reserved.
 * project name: eip
 * Date: 2020-03-22
 * Author: NianXiaoLing (xlnian@163.com)
 * Only use technical communication, please do not use it for business
 */
package com.hotent.portal.util;

import java.util.List;
import org.nianxi.jms.model.JmsMessage;
import org.nianxi.utils.BeanUtils;

/**
 * @author zhaoxy
 * @company 广州宏天软件股份有限公司
 * @email zhxy@jee-soft.cn
 * @date 2018-06-06 14:23
 */
public class TaoBaoUtil {
	
	/**
	 * 构建变量json
	 * @param jmsMessage
	 * @return
	 */
	public static String buildParams(JmsMessage jmsMessage){
		// 构建参数
		String parmString = "{";
		List<String> parmList = jmsMessage.getParms();
		if(BeanUtils.isEmpty(parmList)) return "";
		int n = parmList.size();
		int index = 0;
		for (String parm : parmList) {
			index++;
			if (jmsMessage.getExtendVars().containsKey(parm)) {
				parmString += parm + ":'" + jmsMessage.getExtendVars().get(parm).toString()+"'";
				if (index != n) {
					parmString += ",";
				}
			}
		}
		parmString += "}";
		
		return parmString;
	}
}
