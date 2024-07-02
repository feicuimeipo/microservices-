package com.nx.boot.launch.env;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.nx.boot.launch.env.NxEnvironment.*;


@Slf4j
@Data
public abstract class NxBootstrap {

    public  final static  String SPRING_BOOT_PREFIX = "spring";
    public  final static  String NX_PROPERTY_PREFIX = "nx";
    public  final static  String NX_BOOT_PREFIX = "nx.boot";
    public  final static  String DEV_PROFILE_CODE="dev";
    public  final static  String PROD_PROFILE_CODE="prod";
    public  final static  String DEFAULT_PROFILE_CODE=DEV_PROFILE_CODE;
    public  final static  boolean profileIsLocal = false;


    //两个参数名
    public static Map<String,String> DoubleKeyMap = new HashMap(){{
        put("nx.boot.tenant.enabled","nx.tenant.enabled");
    }};

    public boolean isProd(){
        try {
            return this.getProfile().equalsIgnoreCase(PROD_PROFILE_CODE);
        }catch (Exception e){
            log.warn(e.getMessage(),e);
            return false;
        }
    }

    /**
     * 外置的配置文件
     */
    @NxValue(value = "${"+ KEY_CONFIG_LOCATION +":''}")
    protected String configLocation;

    @NxValue(value = "${"+ KEY_PROFILES_ACTIVE +":"+DEFAULT_PROFILE_CODE+"}")
    protected String profile;

    @NxValue(value = "${"+ KEY_PROFILES_INCLUDE +"}")
    protected String profileIncludes;

    @NxValue(value = "${"+ KEY_APPLICATION_NAME +":''}")
    protected String applicationName;

    //直连地址
    @NxValue(value = "${"+ KEY_SERVER_PORT +":'8080'}")
    protected Integer serverPort;

    @NxValue(value = "${"+ KEY_MANAGEMENT_SERVER_PORT +":'8090'}")
    protected Integer managementServerPort;

    @NxValue(value = "${nx.boot.local-dev:"+ profileIsLocal +"}")
    protected boolean localDev;

    @NxValue(value = "${nx.boot.version:0.9.9}")
    protected String version;

    @NxValue(value = "${nx.boot.tenant.enabled:false}")
    protected boolean tenantEnabled;

    @NxValue(value = "${nx.boot.cloud.namespace:'nx'}")
    protected String cloudNamespace;

    @NxValue(value = "${nx.boot.cloud.group:'DEFAULT_GROUP'}")
    protected String cloudGroup;

    @NxValue(value = "${nx.boot.cloud.provider:nacos}")
    protected String provider;

    @NxValue(value = "${nx.boot.cloud.config.prefix:''}")
    protected String configPrefix;

    @NxValue(value = "${nx.boot.cloud.config.extension:yaml}")
    protected String configExentsion;

    @NxValue(value = "${nx.boot.cloud.dubbo.provider:nacos}")
    protected String dubboProvider;

    @NxValue(value = "${nx.boot.cloud.dubbo.port:20883}")
    protected Integer dubboPort;

    //协议
    @NxValue(value = "${nx.boot.cloud.dubbo.protocol.name:dubbo}")
    protected String dubboProtocolName;

    @NxValue(value = "${nx.boot.cloud.dubbo.scan.base-packages:''}")
    protected String dubboScanPackages;
    
    @NxValue(value = "${nx.boot.cloud.sentinel.port:8719}")
    protected Integer sentinelPort;

    @NxValue(value = "${nx.boot.cloud.discovery.enabled:false}")
    protected boolean discoveryEnabled;

    @NxValue(value = "${nx.boot.cloud.config.enabled:false}")
    protected boolean configEnabled;

    @NxValue(value = "${nx.boot.cloud.discovery.feign.enabled:true}")
    protected boolean discoveryFeignEnabled;

    //dubbo启动
    @NxValue(value = "${nx.boot.cloud.dubbo.enabled:true}")
    protected boolean dubboEnabled;

    //dubbo启动
    //如是为false为直联模式
    @NxValue(value = "${nx.boot.cloud.dubbo.discovery.enabled:false}")
    protected boolean dubboDiscoveryEnabled;

    @NxValue(value = "${nx.boot.cloud.skywalking.enabled:'true'}")
    protected boolean skywalkingEnabled;


    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.server-addr:localhost:8848}")
    protected String cloudServerAddr;

    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.username:nacos}")
    protected String cloudServerUserName;

    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.password:nacos}")
    protected String cloudServerPassword;

    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.dubbo.server-addr:localhost:8848}")
    protected String dubboServerAddr;

    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.dubbo.username:nacos}")
    protected String dubboServerUserName;

    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.dubbo.password:nacos}")
    protected String dubboServerPassword;

    @NxValue(value = "${nx.boot.'${"+SPRING_BOOT_PREFIX+".profiles.active}'.cloud.sentinel.server-addr:''}")
    protected String sentinelAddr;



    /**
     *
     * @param source  被复制对象
     * @param target  目标对象
     * 将src中的属性复制到target中
     * 假如 target中的字段String A=1,src中字段A未赋值即为null值
     * 在进行copy过程中,自动忽略src字段中的null值不进行操作,保留target的原有值
     */
    public static void configCmdLineArgToBootProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    private static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static class TypeUtils{
        public static Object stringToNullableTarget(String string, Class<?> t) throws Exception {
            return string == null ? null : t.getConstructor(String.class).newInstance(string);
        }

        public static Object stringToTarget(String string, Class<?> t)  {
            boolean nullOrEmpty = StringUtils.isEmpty(string);

            if (double.class.equals(t)) {
                return nullOrEmpty ? 0 : Double.parseDouble(string);
            } else if (long.class.equals(t)) {
                return nullOrEmpty ? 0 : Long.parseLong(string);
            } else if (int.class.equals(t)) {
                return nullOrEmpty ? 0 : Integer.parseInt(string);
            } else if (float.class.equals(t)) {
                return nullOrEmpty ? 0 : Float.parseFloat(string);
            } else if (short.class.equals(t)) {
                return nullOrEmpty ? 0 : Short.parseShort(string);
            } else if (boolean.class.equals(t)) {
                return nullOrEmpty ? 0 : Boolean.parseBoolean(string);
            }else if(String.class.equals(t)){
                return nullOrEmpty ? "" : string;
            }  else if (Number.class.isAssignableFrom(t)) {
                try {
                    return t.getConstructor(String.class).newInstance(nullOrEmpty ? "0" : string);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    return string;
                }
            }    else {
                try {
                    return nullOrEmpty ? "" : t.getConstructor(String.class).newInstance(string);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    return string;
                }
            }
        }

    }






}


