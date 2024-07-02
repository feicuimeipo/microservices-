package com.nx.redis;

import com.nx.cache.exception.LoaderCacheException;
import com.nx.redis.enums.RedisMode;
import com.nx.common.context.SpringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import static com.nx.cache.CacheProviderFactory.DEFAULT_CACHE_GROUP_NAME;

public class RedisConfigFactory {
    private volatile static Map<String, RedisConfig> redisConfigMap = new ConcurrentHashMap<>();
    public  volatile static String    DEFAULT_GROUP_NAME = DEFAULT_CACHE_GROUP_NAME;
    public final static String DEFAULT_SESSION_NAME = "session";

    public static String[] getGroupNames(){
        return redisConfigMap.keySet().toArray(new String[]{});
    }
    public static RedisConfig[] getRedisConfigs(){
        return redisConfigMap.values().toArray(new RedisConfig[]{});
    }
    private static Pattern pattern = Pattern.compile("^.+[:]\\d{1,5}\\s*$");


    public static RedisConfig getRedisConfig(String groupName){
        RedisConfig redisConfig = redisConfigMap.get(groupName);
        if (redisConfig!=null) {
            return redisConfig;
        }

        synchronized (RedisConfigFactory.class) {
            Environment environment = SpringUtils.getContextAndNullCheck().getEnvironment();
            redisConfig = new RedisConfig(groupName);
            getRedisConfig("" ,redisConfig, environment);

            RedisMode redisMode = RedisMode.valueOfCode(redisConfig.getMode());
            switch (redisMode) {
                case standalone:
                    break;
                case cluster:
                    if (!StringUtils.hasLength(redisConfig.getCluster().getNodes())){
                        redisConfig.getCluster().setNodes(redisConfig.getServers());
                    }
                    break;
                case sentinel:
                    if (!StringUtils.hasLength(redisConfig.getSentinel().getNodes())){
                        redisConfig.getCluster().setNodes(redisConfig.getServers());
                    }
                    break;
                default:
                    throw new RuntimeException("参数mode："+ redisMode==null?"null":redisMode.getCode() +"不支持");
            }

            if (redisConfig==null) {
                redisConfigMap.put(groupName, redisConfig);
            }
            if (redisConfig!=null && redisConfigMap.containsKey(DEFAULT_GROUP_NAME)){
                DEFAULT_GROUP_NAME = RedisConfigFactory.getRedisConfigs()[0].getGroupName();
            }
        }
        if (redisConfig==null){
            throw new LoaderCacheException("groupName="+groupName+".redis的配置信息不存在");
        }

        if (redisConfig.getPool()!=null){
            redisConfig.setPool(new JedisPoolConfig());
        }


        if (redisConfig.getPool().getMinIdle()<=0){
            redisConfig.getPool().setMinIdle(0);
        }

        if (redisConfig.getPool().getMinIdle()<=0){
            redisConfig.getPool().setMaxIdle(8);
        }

        if (redisConfig.getPool().getMaxTotal()<=0){
            redisConfig.getPool().setMaxTotal(8);
        }

       return redisConfig;
    }

    public static void checkServers(String servers){
        String[] serversAry = StringUtils.tokenizeToStringArray(servers, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        //检查ip和port格式
        for (String server : serversAry) {
            if (!pattern.matcher(server).matches()) {
                throw new RuntimeException("参数servers：" + servers + "错误");
            }
        }
    }


    public static boolean isPrimitive(Field field){
        if (ClassUtils.isPrimitiveOrWrapper(field.getType())){
            return true;
        }

        String fieldTypeName = field.getType().getSimpleName();
        if (fieldTypeName.equals("int")
                || fieldTypeName.equals("string")
                || fieldTypeName.equals("boolean")
                || fieldTypeName.equals("double")
                || fieldTypeName.equals("float")
                || fieldTypeName.equals(java.lang.Integer.class.getSimpleName())
                || fieldTypeName.equals(java.lang.String.class.getSimpleName())
                || fieldTypeName.equals(java.lang.Boolean.class.getSimpleName())
                || fieldTypeName.equals(java.lang.Long.class.getSimpleName())
                || fieldTypeName.equals(java.math.BigDecimal.class.getSimpleName())
                || fieldTypeName.equals(java.math.BigInteger.class.getSimpleName())
                || fieldTypeName.equals(java.lang.Float.class.getSimpleName())
                || fieldTypeName.equals(java.lang.Double.class.getSimpleName())

        ){
            return true;
        }
        return false;
    }

    private static List<Field> getInheritedPrivateFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = type;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }

        return result;
    }


    public static void getRedisConfig(String prefix,  Object target,Environment  environment) {
        //ConfigurableEnvironment  configurableEnvironment = (ConfigurableEnvironment) environment;
        List<Field> list = getInheritedPrivateFields(target.getClass());
        if (list==null || list.size()==0){
            return;
        }
        Field[] fields = list.toArray(new Field[]{});

        if (target instanceof RedisConfig){
            RedisConfig redisConfig = (RedisConfig) target;
            prefix = redisConfig.getGroupName() + ".redis";
        }



        for (Field field : fields) {
            String name = field.getName();
            if (name.equals("maxTotal") || name.equals("minIdel") || name.equals("maxIdel")){
                System.out.println("host="+field.getName());
            }

           field.setAccessible(true);
           String key = prefix + "." +name;
           if (Modifier.isStatic(field.getModifiers()) || field.getType()==Void.TYPE){
               continue;
           }
           boolean isPrimitive = isPrimitive(field);
            Object fieldValue = null;
           if (environment.containsProperty(key)){
               fieldValue = environment.getProperty(key,field.getType());
               if (isPrimitive && fieldValue!=null) {
                   ReflectionUtils.setField(field, target, fieldValue);
               }
           }else if(!isPrimitive){
               prefix = prefix + "." + field.getName();
               fieldValue = ReflectionUtils.getField(field,target);
               try {
                   if (fieldValue==null){
                       fieldValue = field.getType().newInstance();
                   }
                   if (fieldValue!=null) {
                       ReflectionUtils.setField(field, target, fieldValue);
                       getRedisConfig(prefix,fieldValue,environment);
                   }
               } catch (Exception e) {
               }
           }
        }
    }


}
