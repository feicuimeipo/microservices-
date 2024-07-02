package com.nx.storage.adapter.provider;

import com.nx.httpclient.NxHttpUtil;
import com.nx.storage.core.enums.StoProviderEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import static com.nx.storage.adapter.provider.StoProviderConfigFactory.currentBucketConfig;

/**
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2017年1月7日
 */
public abstract class AbstractStoProvider implements StoProvider {

	protected static final String URL_PREFIX_PATTERN = "(http).*\\.(com|cn)\\/";
	protected static final String HTTP_PREFIX = "http://";
	protected static final String HTTPS_PREFIX = "https://";
	protected static final String DIR_SPLITER = "/";

	
	public AbstractStoProvider(StoProviderConfig conf) {
		Validate.notBlank(conf.getAccessKey(), "[accessKey] not defined");
		Validate.notBlank(conf.getAccessSecret(), "[secretKey] not defined");
		StoProviderConfigFactory.addConfig(conf);
	}


	

	protected String getFullPath(String bucketName,String file) {
		if(file.startsWith(HTTP_PREFIX) || file.startsWith(HTTPS_PREFIX)){
			return file;
		}
		return buildBucketUrlPrefix(bucketName) + file;
	}
	
	protected String resolveFileKey(String bucketName,String fileUrl) {
		if(!fileUrl.startsWith(HTTP_PREFIX) && !fileUrl.startsWith(HTTPS_PREFIX)){
			return fileUrl;
		}
		String urlprefix = buildBucketUrlPrefix(bucketName);
		return fileUrl.replace(urlprefix, StringUtils.EMPTY);
	}

	@Override
	public String downloadAndSaveAs(String bucketName,String file, String localSaveDir) {
		return NxHttpUtil.downloadFile(getDownloadUrl(bucketName,file,300), localSaveDir);
	}
	
	@Override
	public String getDownloadUrl(String bucketName,String fileKey, int expireInSeconds) {
		StoProviderConfig config = currentBucketConfig(bucketName);
		String url;
		if(config.isAuth()){
			fileKey = resolveFileKey(bucketName, fileKey);
			String presignedUrl = generatePreSignedUrl(bucketName,fileKey,expireInSeconds);
			String urlprefix = buildBucketUrlPrefix(bucketName);
			url = presignedUrl.replaceFirst(URL_PREFIX_PATTERN, urlprefix);
		}else{
			url = getFullPath(bucketName,fileKey);
		}
		return url;
	}


	protected String buildBucketName(String bucketName) {
		if(StringUtils.isBlank(bucketName)){
			throw new IllegalArgumentException("[bucketName] not defined");
		}
		return bucketName;
	}

	protected StoProviderConfig getConfig(StoProviderEnum provider, String bucketName) {
		StoProviderConfig config = StoProviderConfigFactory.currentBucketConfig(provider,bucketName);
		if (config==null){
			throw new IllegalArgumentException(provider.getCode() + ":" + bucketName + " not defined");
		}
		return config;
	}



	public void addBucketConfig(StoProviderConfig bucketConfig) {
		StoProviderConfigFactory.addConfig(bucketConfig);
	}


	protected abstract String buildBucketUrlPrefix(String bucketName);
	protected abstract String generatePreSignedUrl(String bucketName,String fileKey, int expireInSeconds);

	
}
