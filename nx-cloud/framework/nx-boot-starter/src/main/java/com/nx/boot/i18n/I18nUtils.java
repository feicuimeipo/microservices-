package com.nx.boot.i18n;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class I18nUtils {

    private final MessageSource messageSource;

    public I18nUtils(@Autowired @Qualifier("messageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    };


    /**
     * 获取key
     *
     * @param key
     * @return
     */
    public  String getMessage(String key,String defaultValue) {
        String name =   this.getMessage(key, null,defaultValue, LocaleContextHolder.getLocale());
        if (StringUtils.isEmpty(name)){
            return defaultValue;
        }
        return name;
    }

    /**
     * 获取key
     *
     * @param key
     * @return
     */
    public  String getMessage(String key,Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        String name =   this.getMessage(key, args,null, locale);
        if (StringUtils.isEmpty(name)){
            return key;
        }
        return name;
    }

    /**
     * 获取key
     *
     * @param key
     * @return
     */
    public  String getMessage(String key,Locale local,Object... args) {
        String name = this.getMessage(key,args,null,local);
        if (StringUtils.isEmpty(name)){
            return key;
        }
        return name;
    }


    /**
     * 获取指定哪个配置文件下的key
     *
     * @param key
     * @param local
     * @return
     */
    public  String getMessage(String key, Locale local) {
        String name = this.getMessage(key,null,null,local);
        if (StringUtils.isEmpty(name)){
            return key;
        }
        return name;
    }

    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }

}
