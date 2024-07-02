package com.alibaba.csp.sentinel.nx.dashboard.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;





/**
 * @ClassName JSONUtils
 * @Description TODO
 * @Author NIANXIAOLING
 * @Date 2022/6/15 16:37
 * @Version 1.0
 **/
public class JSONUtils {
    /**
     * 65-90  是A-Z的大写   97-122 是字母a-z的小写 ASCII码值
     * 类名首字母小写
     * @param clz
     * @return
     */
    public static String lowerFirst(Class clz){
        char[] chars = clz.getSimpleName().toCharArray();
        if (chars[0]<=90) {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    public static String upperFirst(Class clz){
        char[] chars = clz.getSimpleName().toCharArray();
        if (chars[0]>=97) {
            chars[0] -= 32;
        }
        return String.valueOf(chars);
    }

    public static <T> String toJSONString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return new ObjectMapper()
                .getTypeFactory()
                .constructParametricType(collectionClass, elementClasses);
    }

    public static <T> List<T> parseObject(Class<T> clazz, String string) {
        JavaType javaType = getCollectionType(ArrayList.class, clazz);
        try {
            return (List<T>) new ObjectMapper().readValue(string, javaType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}