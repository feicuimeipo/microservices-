package com.nx.elasticsearch.utils;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @ClassName EsMyLocalizable
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/7/7 10:16
 * @Version 1.0
 **/
public enum EsMyLocalizable implements Localizable {

    MESSAGE("{0}");


    private final String sourceFormat;

    private EsMyLocalizable(String sourceFormat) {
        this.sourceFormat = sourceFormat;
    }

    public String getSourceString() {
        return this.sourceFormat;
    }

    public String getLocalizedString(Locale locale) {
        try {
            String path = LocalizedFormats.class.getName().replaceAll("\\.", "/");
            ResourceBundle bundle = ResourceBundle.getBundle("assets/" + path, locale);
            if (bundle.getLocale().getLanguage().equals(locale.getLanguage())) {
                return bundle.getString(this.toString());
            }
        } catch (MissingResourceException var4) {
        }

        return this.sourceFormat;
    }
}
