package com.nx.common.context;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 资源文件加载工具类
 * @Author nianxiaoling
 * @Author vakinge
 */
@Slf4j
public class ResourceUtils {
    protected static List<String> sensitiveKeys = new ArrayList<>(Arrays.asList("password","key","secret","token","credentials"));
    protected static String CONFIG_DELIMITERS = ",; \t\n";
    protected static final String PLACEHOLDER_PREFIX = "${";
    protected static final String PLACEHOLDER_SUFFIX = "}";
    protected final static Properties allProperties = new Properties();


    /**
     * 获取所有配置的副本
     * @return
     */
    public static Properties getAllProperties() {
        return getAllProperties(null);
    }

    /**
     * 按前缀匹配配置列表
     * @param prefix
     * @return
     */
    public static Properties getAllProperties(String prefix) {
        return getAllProperties(prefix, true);
    }



    /**
     * 按key模糊匹配配置列表
     */
    public static Properties getAllProperties(String keyPattern,boolean matchPrefix) {

        Properties properties = new Properties();
        Set<Entry<Object, Object>> entrySet = allProperties.entrySet();
        boolean match = false;
        for (Entry<Object, Object> entry : entrySet) {
            match = isBlank(keyPattern) ;
            if(!match){
                if(matchPrefix){
                    match = entry.getKey().toString().startsWith(keyPattern);
                }else{
                    match = entry.getKey().toString().matches(keyPattern);
                }
            }
            if(match){
                String value = replaceRefValue(entry.getValue().toString());
                properties.put(entry.getKey(), value);
            }
        }
        return properties;
    }

    public static List<String>  getPropertyNames(String prefix){
        return allProperties.keySet()
                .stream() //
                .filter(key -> key.toString().startsWith(prefix)) //
                .map(key -> key.toString()) //
                .collect(Collectors.toList());
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getAnyProperty(String...keys) {
        String value;
        for (String key : keys) {
            value = getProperty(key);
            if(value != null)return value;
        }
        return null;
    }

    public static String getAndValidateProperty(String key) {
        String value = getProperty(key, null);
        if(isBlank(value)){
            throw new IllegalArgumentException(String.format("Property for key:%s not exists", key));
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        //优先环境变量
        String value = System.getProperty(key);
        if(!isBlank(value))return value;

        value = System.getenv(key);
        if(!isBlank(value))return value;

        value = allProperties.getProperty(key);
        if (!isBlank(value)) {
            value = replaceRefValue(value);
            return value;
        }

        return defaultValue;
    }

    public static int getInt(String key){
        return getInt(key,0);
    }

    public static int getInt(String key,int defaultValue){
        String v = getProperty(key);
        if(v != null)return Integer.parseInt(v);
        return defaultValue;
    }

    public static long getLong(String key){
        return getLong(key,0L);
    }

    public static long getLong(String key,long defalutValue){
        String v = getProperty(key);
        if(v != null)return Long.parseLong(v);
        return defalutValue;
    }

    public static boolean getBoolean(String key){
        return Boolean.parseBoolean(getProperty(key));
    }

    public static boolean getBoolean(String key,boolean defaultValue){
        return containsProperty(key) ? Boolean.parseBoolean(getProperty(key)) : defaultValue;
    }

    public static List<String> getList(String key){
        String value = getProperty(key);
        if(isBlank(value)) return new ArrayList<>(0);
        StringTokenizer st = new StringTokenizer(value, CONFIG_DELIMITERS);
        List<String> tokens = new ArrayList<>();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if(!isBlank(token)) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    public static Map<String, String> getMappingValues(String prefix){
        Properties properties = getAllProperties(prefix);
        Map<String, String> result = new HashMap<>(properties.size());
        properties.forEach( (k,v) -> {
            String[] arr = k.toString().split("[|]");
            result.put(arr[1], v.toString());
        } );
        return result;
    }

    public synchronized static void merge(Properties properties){
        if(properties == null || properties.isEmpty()) return;

        Set<Entry<Object, Object>> entrySet = properties.entrySet();
        for (Entry<Object, Object> entry : entrySet) {
            Object value = entry.getValue();
            if(value != null){
                String refValue = replaceRefValue(value.toString());
                if(!isBlank(refValue)){
                    allProperties.setProperty(entry.getKey().toString(), refValue);
                }
            }
        }
    }

    public synchronized static void merge(Map<String, Object> properties){
        for (String key : properties.keySet()) {
            String refValue = replaceRefValue(properties.get(key).toString());
            if(!isBlank(refValue)){
                allProperties.setProperty(key, refValue);
            }
        }
    }

    public synchronized static void add(String key,String value){
        if(isAnyBlank(key,value))return;
        value = replaceRefValue(value);
        if(!isBlank(value))allProperties.setProperty(key, value);
    }

    public static boolean  containsProperty(String key){
        if(System.getProperties().containsKey(key))return true;
        if(System.getenv().containsKey(key))return true;
        return allProperties.containsKey(key);
    }

    public static <T> T getBean(String prefix,Class<T> clazz) {
        try {
            T config = clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            String configKey;
            Object configValue;
            for (Field field : fields) {
                field.setAccessible(true);
                configKey = prefix + field.getName();
                if(containsProperty(configKey)){
                    if(field.getType() == int.class || field.getType() == Integer.class){
                        configValue = Integer.parseInt(getProperty(configKey));
                    }else if(field.getType() == long.class || field.getType() == Long.class){
                        configValue = Long.parseLong(getProperty(configKey));
                    }else if(field.getType() == boolean.class || field.getType() == Boolean.class){
                        configValue = Boolean.parseBoolean(getProperty(configKey));
                    }else{
                        configValue = getProperty(configKey);
                    }
                    try {field.set(config, configValue);} catch (Exception e) {}
                }
            }
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 如果替换包含占位符则替换占位符
     * @return
     */
    public static String replaceRefValue(Properties properties,String value ) {

        if(!value.contains(PLACEHOLDER_PREFIX)){
            return value;
        }

        String[] segments = value.split("\\$\\{");
        String seg;

        StringBuilder finalValue = new StringBuilder();
        for (int i = 0; i < segments.length; i++) {
            seg = trimToNull(segments[i]);
            if(isBlank(seg))continue;

            if(seg.contains(PLACEHOLDER_SUFFIX)){
                String refKey = seg.substring(0, seg.indexOf(PLACEHOLDER_SUFFIX)).trim();
                //其他非${}的占位符如：{{host}}
                String withBraceString = null;
                if(seg.contains("{")){
                    withBraceString = seg.substring(seg.indexOf(PLACEHOLDER_SUFFIX)+1);
                }

                //如果包含默认值，如：${host:127.0.0.1}
                String defaultValue = null;
                int defaultValSpliterIndex = refKey.indexOf(":");
                if(defaultValSpliterIndex > 0){
                    defaultValue = refKey.substring(defaultValSpliterIndex + 1);
                    refKey = refKey.substring(0,defaultValSpliterIndex);
                }

                String refValue = System.getProperty(refKey);
                if(isBlank(refValue))refValue = System.getenv(refKey);
                if(isBlank(refValue))refValue = properties.getProperty(refKey);
                if(isBlank(refValue)){
                    refValue = defaultValue;
                }

                if(isBlank(refValue)){
                    finalValue.append(PLACEHOLDER_PREFIX + refKey + PLACEHOLDER_SUFFIX);
                }else{
                    finalValue.append(refValue);
                }

                if(withBraceString != null){
                    finalValue.append(withBraceString);
                }else{
                    String[] segments2 = seg.split("\\}");
                    if(segments2.length == 2){
                        finalValue.append(segments2[1]);
                    }
                }
            }else{
                finalValue.append(seg);
            }
        }

        return finalValue.toString();
    }

    public static String replaceRefValue(String value){
        return replaceRefValue(allProperties, value);
    }


    public static void printConfigs(Properties properties){
        List<String> sortKeys = new ArrayList<>();
        Set<Entry<Object, Object>> entrySet = properties.entrySet();
        for (Entry<Object, Object> entry : entrySet) {
            String key = entry.getKey().toString();
            sortKeys.add(key);
        }
        Collections.sort(sortKeys);
        log.info("==================final config list start==================");
        String value;
        for (String key : sortKeys) {
            value = hideSensitive(key, properties.getProperty(key));
            log.info(String.format("%s = %s", key,value ));
        }
        log.info("==================final config list end====================");

    }

    //是否有敏感词汇
    protected static String hideSensitive(String key,String orign){
        if(isAnyBlank(key,orign)) return "";
        boolean is = false;
        for (String k : sensitiveKeys) {
            if(is = key.toLowerCase().contains(k))break;
        }
        int length = orign.length();
        if(is && length > 1)return orign.substring(0, length/2).concat("****");
        return orign;
    }


    protected static <T> String collection2String(List<T> list, String appendToken) {
        if (list==null) return null;
        StringBuffer sb = new StringBuffer("");
        int last = list.size() - 1;
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != last) {
                sb.append(appendToken);
            }
        }
        return sb.toString();
    }


    protected static String trimToNull(String value){
        return (value==null || value.trim().length()==0)?"":value.trim();
    }

    protected static boolean isBlank(String value){
        return value ==null || value.trim().length()==0;
    }

    protected static boolean isAnyBlank(String... values){
        for(int i=0;i<values.length;i++){
            boolean ret = values[i]==null || values[i].trim().length()==0;
            if (ret){
                return ret;
            }
        }
        return false;
    }



}
