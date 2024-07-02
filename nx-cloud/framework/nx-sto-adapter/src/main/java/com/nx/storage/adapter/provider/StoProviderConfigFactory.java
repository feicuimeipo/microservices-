package com.nx.storage.adapter.provider;

import com.nx.common.context.SpringUtils;
import com.nx.storage.core.enums.StoProviderEnum;
import lombok.Data;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Data
public class StoProviderConfigFactory {

	private static Map<String, StoProviderConfig> stringS3FileClientConfigMap = new ConcurrentHashMap<>();
	private static StoProviderEnum defaultProvider= StoProviderEnum.minio;
	private static String defaultBucketName="default";

	private static String key(StoProviderConfig config){
		return config.getProvider().getCode() + "_" + config.getBucket();
	}

	private static String key(StoProviderEnum provider, String bucketName){
		String key = provider.getCode() + "_" + bucketName;
		return key;
	}

	public static void addConfig(StoProviderConfig config){
		String key = key(config);
		stringS3FileClientConfigMap.put(key,config);
	}

	public static StoProviderConfig getConfig(StoProviderEnum provider, String bucketName){
		String key = key(provider,bucketName);
		return stringS3FileClientConfigMap.get(key);
	}

	public static StoProviderConfig getDefautltConfig(){
		return getConfig(defaultProvider,defaultBucketName);
	}

	public static StoProviderConfig currentBucketConfig(StoProviderEnum provider, String bucketName) {
		StoProviderConfig config =  getConfig(provider,bucketName);
		if(config == null) {
			synchronized (stringS3FileClientConfigMap) {
				config = getConfig(provider,bucketName);
				if(config == null) {
					config = getBucketConfig(provider,bucketName);
				}
			}
		}
		if(config == null) {
			throw new IllegalArgumentException(provider.getCode() + "["+bucketName+"] not exists");
		}
		return config;
	}
	public static StoProviderConfig currentBucketConfig(String bucketName) {
		return currentBucketConfig(defaultProvider,bucketName);
	}

	public static StoProviderConfig getBucketConfig(StoProviderEnum provider, String bucketName) {
		Environment environment = SpringUtils.getBean(Environment.class);
		if (environment!=null){
			StoProviderConfig config = environment.getProperty(provider.getCode()+"."+bucketName, StoProviderConfig.class);
			config.setBucket(bucketName);
			config.setProvider(provider);
			addConfig(config);
			return config;
		}
		return null;
	}

}
