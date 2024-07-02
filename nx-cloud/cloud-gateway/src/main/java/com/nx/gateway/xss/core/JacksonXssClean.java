
package com.nx.gateway.xss.core;

import com.nx.gateway.xss.config.PharmcubeXssProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * jackson xss 处理
 */
@Slf4j
@RequiredArgsConstructor
public class JacksonXssClean extends XssCleanDeserializerBase {
	private final PharmcubeXssProperties properties;
	private final XssCleaner xssCleaner;

	@Override
	public String clean(String text) throws IOException {
		if (XssHolder.isEnabled()) {
			String value = xssCleaner.clean(XssUtil.trim(text, properties.isTrimText()));
			log.debug("Json property value:{} cleaned up by mica-xss, current value is:{}.", text, value);
			return value;
		} else {
			return XssUtil.trim(text, properties.isTrimText());
		}
	}

}
