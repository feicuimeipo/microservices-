
package com.nx.gateway.xss.core;

import com.nx.gateway.conf.context.ReactiveAppUtils;
import com.nx.gateway.xss.config.PharmcubeXssProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * jackson xss 处理
 */
@Slf4j
public class XssCleanDeserializer extends XssCleanDeserializerBase {

	@Override
	public String clean(String text) throws IOException {
		// 读取 xss 配置
		PharmcubeXssProperties properties = ReactiveAppUtils.getBean(PharmcubeXssProperties.class);
		if (properties == null) {
			return text;
		}
		// 读取 XssCleaner bean
		XssCleaner xssCleaner = ReactiveAppUtils.getBean(XssCleaner.class);
		if (xssCleaner == null) {
			return XssUtil.trim(text, properties.isTrimText());
		}
		String value = xssCleaner.clean(XssUtil.trim(text, properties.isTrimText()));
		log.debug("Json property value:{} cleaned up by mica-xss, current value is:{}.", text, value);
		return value;
	}

}
