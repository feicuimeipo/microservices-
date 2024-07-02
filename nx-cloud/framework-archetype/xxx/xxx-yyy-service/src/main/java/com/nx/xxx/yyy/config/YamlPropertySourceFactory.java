package com.nx.xxx.yyy.config;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName YamlPropertySourceFactory
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/8 15:04
 * @Version 1.0
 **/
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {
    @Override
    public PropertySource createPropertySource(String name, EncodedResource resource) throws IOException {
        if (resource == null) {
            return super.createPropertySource(name, resource);
        }
        List<PropertySource<?>> sources = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());

        return sources.get(0);
    }

//    public static String getPropertiesKey(String value) {
//        Pattern regex = Pattern.compile("\\$\\{([^}]*)\\}");
//        Matcher matcher = regex.matcher("value"); ;//regex.matcher("${aaa}借给${bbb}五毛钱");
//        while(matcher.find()) {
//            String variable = matcher.group(1);
//            String key = variable;
//            String defaultValue = null;
//            if (value.indexOf(":")>-1){
//                key = variable.substring(0,variable.indexOf(":"));
//                value = variable.substring(variable.indexOf(":")+1);
//                return System.getProperties().getProperty(key,value);
//            }
//        }
//        return value;
//    }
}
