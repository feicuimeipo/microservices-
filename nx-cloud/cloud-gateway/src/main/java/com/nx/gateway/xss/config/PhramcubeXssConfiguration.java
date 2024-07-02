/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nx.gateway.xss.config;

import com.nx.gateway.conf.context.ReactiveAppUtils;
import com.nx.gateway.xss.core.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * jackson xss 配置
 */
//@AutoConfiguration
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(PharmcubeXssProperties.class)
public class PhramcubeXssConfiguration implements WebMvcConfigurer {
	private final PharmcubeXssProperties xssProperties;

	@Bean
	@ConditionalOnMissingBean(ReactiveAppUtils.class)
	@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public ReactiveAppUtils appUtil() {
		return new ReactiveAppUtils();
	}


	@Bean
	@ConditionalOnMissingBean(XssCleaner.class)
	@Autowired
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public XssCleaner xssCleaner(PharmcubeXssProperties properties) {
		return new DefaultXssCleaner(properties);
	}

	@Bean
	@Autowired
	@Order(Ordered.LOWEST_PRECEDENCE)
	public FormXssClean formXssClean(PharmcubeXssProperties properties,
                                     XssCleaner xssCleaner) {
		return new FormXssClean(properties, xssCleaner);
	}

	@Bean
	@Autowired
	public Jackson2ObjectMapperBuilderCustomizer xssJacksonCustomizer(PharmcubeXssProperties properties,
																	  XssCleaner xssCleaner) {
		return builder -> builder.deserializerByType(String.class, new JacksonXssClean(properties, xssCleaner));
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> patterns = xssProperties.getPathPatterns();
		if (patterns.isEmpty()) {
			patterns.add("/**");
		}
		XssCleanInterceptor interceptor = new XssCleanInterceptor(xssProperties);
		registry.addInterceptor(interceptor)
			.addPathPatterns(patterns)
			.excludePathPatterns(xssProperties.getPathExcludePatterns())
			.order(Ordered.LOWEST_PRECEDENCE);
	}

}
