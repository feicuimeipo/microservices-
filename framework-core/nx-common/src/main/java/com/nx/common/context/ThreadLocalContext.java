package com.nx.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.nx.common.context.constant.NxRequestHeaders;

import java.util.*;


/**
 * 
 * <br>
 * Class Name   : ThreadLocalContext
 *
 * @author jiangwei
 * @version 1.0.0
 * @date 2020年6月30日
 */
public class ThreadLocalContext {
	protected static List<String> contextHeaders = Arrays.asList(
			NxRequestHeaders.HEADER_JWT_AUTHORITIES,
			NxRequestHeaders.HEADER_TENANT_ID,
			NxRequestHeaders.HEADER_I18N
	);

	private static ThreadLocal<Map<String, Object>> context = new TransmittableThreadLocal<>();
	

	public static void set(String key,Object value){
		if(value == null)return;
		getContextMap().put(key, value);
	}
	
	public static String getStringValue(String key){
		if(context.get() == null)return null;
		return Objects.toString(context.get().get(key), null);
	}
	
	public static boolean exists(String key){
		if(context.get() == null)return false;
		return context.get().containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key){
		if(context.get() == null)return null;
		return (T) context.get().get(key);
	}
	
	public static <T> T get(String key,T defaultVal){
		T value = get(key);
		return value == null ? defaultVal : value;
	}
	
	public static void remove(String...keys){
		if(context.get() == null)return;
		for (String key : keys) {
			context.get().remove(key);
		}
	}
	
	public static boolean isEmpty(){
		return context.get() == null || context.get().isEmpty();
	}
	
	public static void unset(){
		if(context.get() != null){
			context.get().clear();
			context.remove();
		}
	}
	
	private static Map<String, Object> getContextMap(){
		if(context.get() == null){
			context.set(new HashMap<>());
		}
		return context.get();
	}

}
