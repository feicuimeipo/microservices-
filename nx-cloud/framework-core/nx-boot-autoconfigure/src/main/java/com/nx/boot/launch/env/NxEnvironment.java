package com.nx.boot.launch.env;


import static com.nx.boot.launch.env.NxValueProperties.PLACEHOLDER_PREFIX;
import static com.nx.boot.launch.env.NxValueProperties.PLACEHOLDER_SUFFIX;
import static com.nx.boot.launch.env.resources.NxBootResourceUtils.*;

import com.nx.boot.launch.env.resources.CmdLineArgs;
import com.nx.boot.launch.env.resources.NxBootResourceUtils;
import com.nx.boot.launch.env.resources.NxBootUrlResource;
import com.nx.boot.launch.exception.NxBootResourceException;
import com.nx.common.context.ResourceUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 属性文件
 */
@Slf4j
public class NxEnvironment extends NxBootstrap {
    public  static final String[] DEFAULT_SEARCH_LOCATIONS  = new String[]{"classpath:/","classpath:/config/","file:./","file:./config/*/","file:./config/"};
    public  static final String[]            BOOT_FILE_NAME = new String[]{"bootstrap.yaml","bootstrap.yml","bootstrap.properties"};
    public  static       String[]     APPLICATION_FILE_NAME = new String[]{"application.yml","application.yaml","application.properties"};
    public  static final String[]  DEFAULT_BOOT_FILE_NAME   = new String[]{"default-bootstrap.yml"};

    public static final String OPTIONAL_PREFIX = "optional:";
    public final static String KEY_PROFILES_ACTIVE = "spring.profiles.active";
    public final static String KEY_PROFILES_INCLUDE = "spring.profiles.include";
    public final static String KEY_CONFIG_LOCATION = "spring.config.location";
    public final static String KEY_ADDITION_CONFIG_LOCATION = "spring.config.additional-location";
    public final static String KEY_APPLICATION_NAME = "spring.application.name";
    public final static String KEY_SERVER_PORT  =     "server.port";
    public final static String KEY_MANAGEMENT_SERVER_PORT  = "management.server.port";


    @Getter
    private String[] cmdArgs;
    @Getter
    protected Properties allProperties = new Properties();

    private CmdLineArgs cmdLineArgs;
    private ClassLoader classLoader;
    private File rootPath;




    public static class Builder<T>{
        private NxEnvironment bootstrap = new NxEnvironment();

        public Builder(Class<T> source,String[] args) {
            bootstrap.cmdArgs = args;
            bootstrap.cmdLineArgs = CmdLineArgs.parse(args);

            bootstrap.classLoader = source!=null? source.getClassLoader():
                    (Thread.currentThread().getContextClassLoader()==null? NxBootResourceUtils.class.getClassLoader():
                            Thread.currentThread().getContextClassLoader());

            bootstrap.rootPath = new File(bootstrap.classLoader.getResource("").getPath());
            List<Object> keys = new ArrayList<>(bootstrap.allProperties.keySet());

            try {
                NxBootResourceUtils.loadLocalConfig(bootstrap.classLoader, null, false);
            }catch (IOException ex){
                throw new NxBootResourceException(ex);
            }
            Map<String, URL> classPathConfig = new HashMap<>();
            classPathConfig.putAll(NxBootUrlResource.getDefaultNxBootResourceUrl(bootstrap.classLoader));
            classPathConfig.putAll(NxBootUrlResource.getBootResourceUrl(bootstrap.classLoader));

            if (org.apache.commons.lang3.StringUtils.isNotEmpty(bootstrap.cmdLineArgs.getOptionArgsAsString(KEY_CONFIG_LOCATION))){
                String[] fileNames = bootstrap.cmdLineArgs.getOptionArgsAsString(KEY_CONFIG_LOCATION).split(",");
                classPathConfig.putAll(NxBootUrlResource.getResourceUrl(bootstrap.classLoader,fileNames));
            }else{
                invokeApplicationName();
                classPathConfig.putAll(NxBootUrlResource.getSpringApplicationResourceUrl(bootstrap.classLoader));

                String[] fileNames = bootstrap.cmdLineArgs.getOptionArgsAsString(KEY_ADDITION_CONFIG_LOCATION).split(",");
                classPathConfig.putAll(NxBootUrlResource.getResourceUrl(bootstrap.classLoader,fileNames));
            }
            try {
                NxBootResourceUtils.loadLocalConfig(bootstrap.classLoader,classPathConfig,true);
            }catch (IOException ex){
                throw new NxBootResourceException(ex);
            }


            bootstrap.allProperties.putAll(bootstrap.cmdLineArgs.getProperties());
            for (Object key : keys) {
                if(key == null || bootstrap.allProperties.getProperty(key.toString()) == null)continue;
                if(bootstrap.allProperties.getProperty(key.toString()).contains(PLACEHOLDER_PREFIX)){
                    String value = NxBootResourceUtils.replaceRefValue(bootstrap.allProperties.getProperty(key.toString()));
                    if(org.apache.commons.lang3.StringUtils.isNotBlank(value))bootstrap.allProperties.setProperty(key.toString(), value);
                }
            }



            //最后merge
            merge(bootstrap.cmdLineArgs.getProperties());
            bootstrap.parserValue();

            if (!StringUtils.hasText(bootstrap.getApplicationName())){
                bootstrap.setApplicationName(source.getSimpleName().toUpperCase());
            }
            if (!StringUtils.hasText(bootstrap.getConfigPrefix())){
                bootstrap.setConfigPrefix(bootstrap.getApplicationName());
            }
            Assert.hasText(bootstrap.getApplicationName(), "["+ KEY_APPLICATION_NAME  +"]服务名不能为空");
        }



        private void invokeApplicationName(){
            String[] activeKeys = bootstrap.cmdLineArgs.getOptionArgsAsString(KEY_PROFILES_ACTIVE).split(",");
            String[] includeKeys = bootstrap.cmdLineArgs.getOptionArgsAsString(KEY_PROFILES_INCLUDE).split(",");;
            List<String> applicationNames = new ArrayList<>();
            Arrays.stream(activeKeys).forEach(key->{
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(key)) {
                    applicationNames.addAll(Arrays.stream(APPLICATION_FILE_NAME).map(mainName->{return mainName.substring(0,mainName.indexOf(".")) + "-" + key + mainName.substring(mainName.indexOf(".")+1);}).collect(Collectors.toList()));
                }
            });
            Arrays.stream(includeKeys).forEach(key->{
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(key)) {
                    applicationNames.addAll(Arrays.stream(APPLICATION_FILE_NAME).map(mainName->{return mainName.substring(0,mainName.indexOf(".")) + "-" + key + mainName.substring(mainName.indexOf(".")+1);}).collect(Collectors.toList()));
                }
            });
            APPLICATION_FILE_NAME = applicationNames.toArray(new String[]{});
        }

        private synchronized void merge(Properties properties){
            if(properties == null || properties.isEmpty()) return;

            Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
            for (Map.Entry<Object, Object> entry : entrySet) {
                Object value = entry.getValue();
                if(value != null){
                    String refValue = replaceRefValue(value.toString());
                    if(StringUtils.hasText(refValue)){
                        bootstrap.allProperties.setProperty(entry.getKey().toString(), refValue);
                    }
                }
            }
        }

        public NxEnvironment build(){
            ResourceUtils.merge(bootstrap.allProperties);
            return bootstrap;
        }
    }


    /**
     *
     * @param
     * @param
     * @return
     */
    protected void parserValue(){

        String replaceKey =  DoubleKeyMap.containsKey(KEY_PROFILES_ACTIVE)?DoubleKeyMap.get(KEY_PROFILES_ACTIVE):"null";// PROFILE_PROPERTIES_NAME.replace(CUSTOMER_PROPERTY_BOOT_PREFIX,SPRING_BOOT_PREFIX);
        if (!NxBootResourceUtils.containsProperty(KEY_PROFILES_ACTIVE) && NxBootResourceUtils.containsProperty(replaceKey)){
            allProperties.setProperty(KEY_PROFILES_ACTIVE, NxBootResourceUtils.getProperty(replaceKey,DEFAULT_PROFILE_CODE));
        }
        this.profile = NxBootResourceUtils.getProperty(KEY_PROFILES_ACTIVE,DEFAULT_PROFILE_CODE);

        Field[] fields = NxBootstrap.class.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(NxValue.class)==null) continue;;
            NxValue nxValue = field.getAnnotation(NxValue.class);
            String key = nxValue.value();
            Object value = getValue(key,field.getType());
            if (value!=null) {
                field.setAccessible(true);
                try {
                    ReflectionUtils.setField(field, this, value);
                }catch (Exception e){
                    log.error("[" + field.getName() + "]"+e.getMessage(),e);
                }
            }
        }
    }


    private  Object getValue(String oriKey,Class<?> fieldClz){
        if (oriKey.startsWith("${") &&  oriKey.endsWith("}") && oriKey.length()>2){
            oriKey= oriKey.substring(2,oriKey.length()-1);
        }

        NxValueProperties valueParse = new NxValueProperties(oriKey);
        List<String> keys = valueParse.getKeys();
        List<Object> assemble = new LinkedList<>();
        for (String subKey : keys) {
            if (subKey.startsWith(PLACEHOLDER_PREFIX) && subKey.endsWith(PLACEHOLDER_SUFFIX)){
                subKey = subKey.substring(2,subKey.length()-1);

                String replaceSubKey = DoubleKeyMap.containsKey(subKey)? DoubleKeyMap.get(subKey):"null";
                if (!NxBootResourceUtils.containsProperty(subKey) && NxBootResourceUtils.containsProperty(replaceSubKey)){
                    allProperties.setProperty(subKey, NxBootResourceUtils.getProperty(replaceSubKey,""));
                }

                String value = NxBootResourceUtils.getProperty(subKey,"");
                value = value.replace("\"","");
                value = value.replace("\'","");
                assemble.add(value);
            }else{
                assemble.add(subKey);
            }
        }

        StringBuffer pkey = new StringBuffer();
        for (Object item : assemble) {
            pkey.append(item);
        }
        String replaceSubKey = DoubleKeyMap.containsKey(pkey)? DoubleKeyMap.get(pkey):"null";
        if (!NxBootResourceUtils.containsProperty(pkey.toString()) && NxBootResourceUtils.containsProperty(replaceSubKey)){
            allProperties.setProperty(pkey.toString(), NxBootResourceUtils.getProperty(replaceSubKey,valueParse.getDefaultValue()));
        }

        String value = NxBootResourceUtils.getProperty(pkey.toString(),valueParse.getDefaultValue());
        return TypeUtils.stringToTarget(value, fieldClz);
    }


}
