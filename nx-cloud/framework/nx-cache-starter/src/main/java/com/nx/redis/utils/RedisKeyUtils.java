package com.nx.redis.utils;

import com.nx.common.context.CurrentRuntimeContext;
import com.nx.common.exception.BaseException;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.Map;

public class RedisKeyUtils {
    private RedisSerializer keySerializer;
    public  static final String TENANT_KEY_PREFIX = "_tenant@";
    private static final String KEY_SPLITER = ":";
    private static final String TENANT_KEY_TEMPLATE= TENANT_KEY_PREFIX+"%s:%s";
    private String groupName;
    private String prefix;
    private String keySpEl;

    public RedisKeyUtils(String groupName, String prefix, Object key, RedisSerializer<?> keySerializer) {
        this.groupName  = groupName;
        this.prefix = prefix;
        this.keySpEl = key.toString();
        this.keySerializer = keySerializer;
    }

    private static String buildNameSpaceKey(){
        if (CurrentRuntimeContext.getTenantId()!=null){
            Long tenantId = CurrentRuntimeContext.getTenantId();
            return String.format(TENANT_KEY_TEMPLATE, tenantId);
        }
        return "";
    }


    public static String buildNameSpaceKey(String key) {
        StringBuffer keys = new StringBuffer(buildNameSpaceKey());
        append(keys,key,KEY_SPLITER);
        return key.toString();
    };


    private static void append(StringBuffer key,String suffix){
        append(key,suffix,KEY_SPLITER);
    }
    private static void append(StringBuffer key,String suffix,String split){
        if (key==null) return;

        if (!StringUtils.hasLength(suffix)){
            return;
        }
        if (StringUtils.hasLength(key) && StringUtils.hasLength(suffix)) {
            key.append(split);
        }
        if (StringUtils.hasLength(suffix)){
            key.append(suffix);
        }
    }

    /**
     * #号后为变量
     * @param prefix
     * @param keySpEl
     * @param args
     * @return
     */
    public static String generateKey(String groupName, String prefix, String keySpEl, Map<String,String> args){

        if (!StringUtils.hasText(keySpEl)){
            return null;
        }

        StringBuffer key = new StringBuffer(buildNameSpaceKey());
        append(key,groupName);
        append(key,prefix);

        String keyPrefix = keySpEl.substring(0,keySpEl.indexOf("#"));
        String[] aryKeys = keySpEl.split("#");
        if (args.size()<aryKeys.length-1){
            throw new BaseException("key参数个数不对！");
        }
        append(key,keyPrefix,"_");
        for (String k : aryKeys) {
            if (args.containsKey(k)){
                append(key,"_"+k+"="+args.get(k),"");
            }
        }
        return key.toString();
    }

    public String getKey(){
        return generateKey(groupName,prefix,keySpEl,new HashMap<>());
    }

    public String getKey(Map<String,String> args){
        return generateKey(groupName,prefix,keySpEl,args);
    }


}
