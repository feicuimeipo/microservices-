package com.hotent.base;


import com.hotent.base.conf.WebMvcConfiguration;
import com.hotent.base.swagger.Swagger3Config;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({Swagger3Config.class, WebMvcConfiguration.class})
public class AdminCommonAutoConfiguration {


}
