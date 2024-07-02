package com.nx.boot.i18n;


import cn.hutool.extra.spring.SpringUtil;
import com.nx.common.banner.BannerUtils;
import com.nx.common.context.constant.NxRequestHeaders;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;


/**
 * <spring:message code="welcome" arguments="${requestScope.user.username }"/>
 */
@Slf4j
@Component("localeChangeInterceptor")
@ConditionalOnProperty(prefix = "nx.i18n", name = "enabled", havingValue = "true")
public class I18nInterceptor implements HandlerInterceptor {
    public I18nInterceptor(){
        BannerUtils.push(this.getClass(),"nx-boot-starter:i18n enabled");
    }

    @Nullable
    @Getter
    @Setter
    private String[] httpMethods;

    @Getter
    @Setter
    private boolean ignoreInvalidLocale;


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
        Locale locale = null;
        String customerAcceptLangHeader = request.getHeader(NxRequestHeaders.HEADER_I18N);
        if (StringUtils.isNotEmpty(customerAcceptLangHeader) ) {
          if(customerAcceptLangHeader.indexOf("_")>-1){
                String[] ary = customerAcceptLangHeader.split("_");
                locale = new Locale(ary[0], ary[1]);
            }else{
                locale = new Locale(customerAcceptLangHeader);
            }
            setLocale(request,response,locale);
        }
        return true;
    }


    /* 设置语言
     *
    * @param request: 请求对象
     * @param response: 返回对象
     * @param locale:  语言
     **/
    private void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        if (localeResolver == null) {
            if (SpringUtil.getBean(LocaleResolver.class)!=null){
                localeResolver = SpringUtil.getBean(LocaleResolver.class);
            };
            throw new IllegalStateException(
                    "No LocaleResolver found: not in a DispatcherServlet request?");
        }
        try {
            localeResolver.setLocale(request, response, locale);
        } catch (IllegalArgumentException ex) {
            if (isIgnoreInvalidLocale()) {
                System.out.println("Ignoring invalid locale value [" + locale.toString() + "]: " + ex.getMessage());
            } else {
                throw ex;
            }
        }
    }

    /**
     * 检查http请求
     *
     * @param currentMethod:当前请求
     * @return boolean
     **/
    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = getHttpMethods();
        if (ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        }
        for (String configuredMethod : configuredMethods) {
            if (configuredMethod.equalsIgnoreCase(currentMethod)) {
                return true;
            }
        }
        return false;
    }

}
