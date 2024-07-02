package com.nx.boot;


import com.nx.boot.config.*;
import com.nx.boot.json.NxJacksonAutoConfiguration;
import com.nx.boot.support.AppUtil;
import com.nx.boot.tenant.TenantIgnoreAspect;
import com.nx.boot.undrtown.UndertowCustomizationBean;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.SpringUtils;
import com.nx.common.context.spi.TenantContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * com.nx.boot.nxBootAutoConfiguration
 */
@Configuration
@Import({
        WebProperties.class,
        NxWebAutoConfiguration.class,
        FilterAutoConfiguration.class,
        XxlJobConfig.class,
        AppUtil.class,
        Swagger3Config.class,
        NxJacksonAutoConfiguration.class,
        UndertowCustomizationBean.class
})
@EnableConfigurationProperties(ServerProperties.class)
public class NxBootAutoConfiguration {


}
