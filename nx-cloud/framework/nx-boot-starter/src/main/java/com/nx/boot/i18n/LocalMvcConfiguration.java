package com.nx.boot.i18n;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import java.util.Locale;


@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@Configuration
public class LocalMvcConfiguration implements WebMvcConfigurer {


    /**
     * 解析区域的另一种方法是通过SessionLocaleResolver。它通过检验用户会话中预置的属性来解析区域。如果该会话属性不存在，它会根据accept-language HTTP头部确定默认区域。
     * @return
     */
    @Bean("localeResolver") // 默认方法名称就是bean名称，若方法名过于随性需要在@Bean注解中指定为localeResolver
    public LocaleResolver sessionLocaleResolver() {
        // 默认是简体中文
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }

    @Bean("messageSource")
    public MessageSource messageSource(){
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        //ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames("i18n/messages");// name of the resource bundle
        source.setDefaultEncoding("UTF-8");
        //source.setCacheSeconds(3600);
        //source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    @Override
    public  void addInterceptors(InterceptorRegistry registry) {
        // 实例化一个Locale拦截器
        I18nInterceptor interceptor  = new I18nInterceptor();
        interceptor.setIgnoreInvalidLocale(true);
        registry.addInterceptor(interceptor);//.addPathPatterns("/*");
    }

}
