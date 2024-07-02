package com.nx.boot.id;

import com.nx.common.context.SpringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 唯一ID获取类
 * 
 * @author nianxiaoling
 * @email xlnian@163.com
 * @date 2018年4月11日
 */
public class IdGeneratorUtils {
	private static Long defaultWorkId;
	private static Long defaultDataCenterId;
	private static IdGenerator defaultIdGenerator;

	public static Map<String,IdGenerator> IdGeneratorMap = new ConcurrentHashMap<>();

	private static  IdGenerator getIdGenerator(Long workId, Long dataCenterId) {
		if (defaultWorkId==null || defaultDataCenterId==null){
			synchronized (IdGeneratorUtils.class) {
				initDefaultInfo();
			}
		}
		String key = dataCenterId + "_" + workId;
		if (IdGeneratorMap.containsKey(key)){
			return IdGeneratorMap.get(key);
		}
		IdGenerator idGenerator = IdGeneratorMap.get(key);
		if (idGenerator==null){
			IdGeneratorMap.putIfAbsent(key, new IdGenerator(workId, dataCenterId));
		}
		return IdGeneratorMap.get(key);
	}

	private static IdGenerator getIdGenerator(Long workId) {
		if (defaultWorkId==null || defaultDataCenterId==null){
			synchronized (IdGeneratorUtils.class) {
				initDefaultInfo();
			}
		}
		return getIdGenerator(workId,defaultDataCenterId);
	}

	public static Long nextId(Long workId) {
		return getIdGenerator(workId).nextId();
	}

	public static Long nextId(Long workId,Long dataCenterId) {
		return getIdGenerator(workId,dataCenterId).nextId();
	}

	private static void initDefaultInfo(){
		Environment environment = SpringUtils.getBean(Environment.class);
		if (environment.containsProperty("nx.id.workerId")){
			defaultWorkId = Long.parseLong(environment.getProperty("nx.id.workerId","0"));
		}else{
			defaultWorkId = getRandom();
		}
		if (environment.containsProperty("nx.id.datacenterId")){
			defaultDataCenterId = Long.parseLong(environment.getProperty("nx.id.datacenterId","0"));
		}else{
			defaultDataCenterId = getRandom();
		}
	}

	private static Long getRandom(){
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			return secureRandom.nextLong();
		} catch (NoSuchAlgorithmException e) {

			SecureRandom secureRandom = null;
			try {
				secureRandom = SecureRandom.getInstance("NativePRNG");
				return  secureRandom.nextLong();
			} catch (NoSuchAlgorithmException ex) {
				ex.printStackTrace();
				return  Long.parseLong(String.valueOf(Math.random()*200));
			}
		}
	}

	public static Long nextId() {
		if (defaultIdGenerator==null){
			synchronized (IdGeneratorUtils.class) {
				if (defaultIdGenerator==null) {
					initDefaultInfo();
					defaultIdGenerator = getIdGenerator(defaultWorkId, defaultDataCenterId);
				}
			}
		}
		return defaultIdGenerator.nextId();
	}


}
