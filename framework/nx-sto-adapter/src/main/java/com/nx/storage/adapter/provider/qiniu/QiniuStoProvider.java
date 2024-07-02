package com.nx.storage.adapter.provider.qiniu;


import com.nx.common.exception.BaseException;
import com.nx.storage.adapter.provider.StoProviderConfig;
import com.nx.storage.adapter.provider.StoProviderConfigFactory;
import com.nx.storage.adapter.model.NxObjectMetadata;
import com.nx.storage.adapter.provider.AbstractStoProvider;
import com.nx.storage.core.enums.StoProviderEnum;
import com.nx.storage.adapter.model.NxUploadObject;
import com.nx.storage.adapter.model.NxUploadResult;
import com.nx.storage.utils.FilePathHelper;
import com.nx.storage.utils.StoInternalJsonUtils;
import com.nx.storage.adapter.model.NxUploadTokenParam;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BucketInfo;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 七牛文件服务
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2017年1月5日
 */
public class QiniuStoProvider extends AbstractStoProvider {

	private static OkHttpClient httpClient =
			new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
			.build();
	
	//public static final String NAME = "qiniu";
	private static final String DEFAULT_CALLBACK_BODY = "filename=${fname}&size=${fsize}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}";
	
	private Map<String, ExpireableUpToken> bucketUploadTokenCache = new HashMap<>();
	
	private static final String[] policyFields = new String[]{
            "callbackUrl",
            "callbackBody",
            "callbackHost",
            "callbackBodyType",
            "fileType",
            "saveKey",
            "mimeLimit",
            "fsizeLimit",
            "fsizeMin",
            "deleteAfterDays",
    };

	private static UploadManager uploadManager;
	private static BucketManager bucketManager;
	private Auth auth;

	public QiniuStoProvider(StoProviderConfig conf) {
		super(conf);
		auth = Auth.create(conf.getAccessKey(), conf.getSecretKey());

		Region region;
		
		if("huanan".equals(conf.getRegionName())){
			region = Region.huanan();
		}else if("huabei".equals(conf.getRegionName())){
			region = Region.huabei();
		}else if("huadong".equals(conf.getRegionName())){
			region = Region.huadong();
		}else if("beimei".equals(conf.getRegionName())){
			region = Region.beimei();
		}else {
			region = Region.autoRegion();
		}
		Configuration c = new Configuration(region);
		uploadManager = new UploadManager(c);
		bucketManager = new BucketManager(auth,c);
	}
	
	@Override
	public boolean existsBucket(String bucketName) {
		try {			
			BucketInfo bucketInfo = bucketManager.getBucketInfo(bucketName);
			return bucketInfo != null;
		} catch (Exception e) {
			return false;
		}
	}

	
	@Override
	public void createBucket(String bucketName,boolean isPrivate) {
		StoProviderConfig conf = StoProviderConfigFactory.getConfig(StoProviderEnum.qiniu,bucketName);
		try {
			bucketName = buildBucketName(bucketName);
			bucketManager.createBucket(bucketName, conf.getRegionName());
		} catch (QiniuException e) {
			processQiniuException(bucketName, e);
		}
	}

	@Override
	public void deleteBucket(String bucketName) {
		bucketName = buildBucketName(bucketName);
		String path = "/drop/"+bucketName+"\n";
        String accessToken = auth.sign(path);
        String url = "http://rs.qiniu.com/drop/"+bucketName;                

        Request request = new Request.Builder().url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "QBox " + accessToken).build();
        okhttp3.Response re = null;
        try {
            re = httpClient.newCall(request).execute();
            if (!re.isSuccessful()) {
            	throw new BaseException(re.code(), re.message());
            }
        } catch (IOException e) {
        	throw new BaseException(e.getMessage());
        }
	}
	
	@Override
	public boolean exists(String bucketName,String fileKey) {
		bucketName = buildBucketName(bucketName);
		fileKey = resolveFileKey(bucketName, fileKey);
		try {
			bucketManager.stat(bucketName, fileKey);
			return true;
		} catch (QiniuException e) {
			if(e.code() == 612)return false;
			throw new BaseException(e.code(), e.getMessage());
		}
	}

	@Override
	public NxUploadResult upload(NxUploadObject object) {
		String fileKey = object.getFileKey();
		String bucketName = buildBucketName(object.getBucketName());
		try {
			Response res = null;
			String upToken = getUpToken(bucketName);
			if(object.getFile() != null){
				res = uploadManager.put(object.getFile(), fileKey, upToken);
			}else if(object.getBytes() != null){
				res = uploadManager.put(object.getBytes(), fileKey, upToken);
			}else if(object.getInputStream() != null){
				res = uploadManager.put(object.getInputStream(), fileKey, upToken, null, object.getMimeType());
			}else{
				throw new IllegalArgumentException("upload object is NULL");
			}
			
			if (res.isOK()) {
				return new NxUploadResult(fileKey,getFullPath(object.getBucketName(),fileKey), null);
			}
			
		} catch (QiniuException e) {
			processQiniuException(object.getFileKey(), e);
		}
		return null;
	}



	@Override
	public boolean delete(String bucketName,String fileKey) {
		try {
			bucketName = buildBucketName(bucketName);
			bucketManager.delete(bucketName, fileKey);
			return true;
		} catch (QiniuException e) {
			//不存在
			if(e.code() == 612)return true;
			processQiniuException(fileKey, e);
		}
		return false;
	}
	
	@Override
	public byte[] getObjectBytes(String bucketName, String fileKey) {
		bucketName = buildBucketName(bucketName);
		String downloadUrl = getDownloadUrl(bucketName, fileKey,600);
		Request request = new Request.Builder().url(downloadUrl).build();
		okhttp3.Response re = null;
		try {
			re = httpClient.newCall(request).execute();
			if(re.isSuccessful()){
				return re.body().bytes();
			}
			throw new BaseException(re.code(),re.message());
		} catch (IOException e) {
			throw new BaseException(e.getMessage());
		}
	}

	@Override
	public InputStream getObjectInputStream(String bucketName, String fileKey) {
		bucketName = buildBucketName(bucketName);
		String downloadUrl = getDownloadUrl(bucketName, fileKey,600);
		Request request = new Request.Builder().url(downloadUrl).build();
		okhttp3.Response re = null;
		try {
			re = httpClient.newCall(request).execute();
			if(re.isSuccessful()){
				return re.body().byteStream();
			}
			throw new BaseException(re.code(),re.message());
		} catch (IOException e) {
			throw new BaseException(e.getMessage());
		}
	}

	@Override
	public NxObjectMetadata getObjectMetadata(String bucketName, String fileKey) {
		try {
			bucketName = buildBucketName(bucketName);
			FileInfo stat = bucketManager.stat(bucketName, fileKey);
			NxObjectMetadata objectMetadata = new NxObjectMetadata();
			objectMetadata.setCreateTime(new Date(stat.putTime));
			objectMetadata.setFilesize(stat.fsize);
			objectMetadata.setHash(stat.md5);
			objectMetadata.setMimeType(stat.mimeType);
			return objectMetadata;
		} catch (QiniuException e) {
			processQiniuException(bucketName, e);
			return null;
		}
	}
	
	@Override
	public Map<String, Object> createUploadToken(NxUploadTokenParam param) {
		
		if(StringUtils.isNotBlank(param.getCallbackUrl())){
			if(StringUtils.isBlank(param.getCallbackBody())){
				param.setCallbackBody(DEFAULT_CALLBACK_BODY);
			}
			if(StringUtils.isBlank(param.getCallbackHost())){
				param.setCallbackHost(param.getCallbackHost());
			}
		}
		
		Map<String, Object> result = new HashMap<>();
		StringMap policy = new StringMap();
		policy.putNotNull(policyFields[0], param.getCallbackUrl());
		policy.putNotNull(policyFields[1], param.getCallbackBody());
		policy.putNotNull(policyFields[2], param.getCallbackHost());
		policy.putNotNull(policyFields[3], param.getCallbackBodyType());
		policy.putNotNull(policyFields[4], param.getFileType());
		policy.putNotNull(policyFields[5], param.getFileKey());
		policy.putNotNull(policyFields[6], param.getMimeLimit());
		policy.putNotNull(policyFields[7], param.getFsizeMin());
		policy.putNotNull(policyFields[8], param.getFsizeMax());
		policy.putNotNull(policyFields[9], param.getDeleteAfterDays());

		String token = auth.uploadToken(param.getBucketName(), param.getFileKey(), param.getExpires(), policy, true);
		result.put("uptoken", token);
		result.put("dir", param.getUploadDir());
		
		return result;
	}

	@Override
	public void close() {}

	@Override
	public String name() {
		return StoProviderEnum.qiniu.getCode();
	}
	
	private void processQiniuException(String bucketName, QiniuException e) {
		Response r = e.response;
		if(e.code() == 631){
			throw new BaseException(404, "bucketName["+bucketName+"]不存在");
		}
		if(e.code() == 614){
			throw new BaseException(406, "bucketName["+bucketName+"]已存在");
		}
		if(e.code() == 612){
			throw new BaseException(404, "资源不存在");
		}
		String message;
		try {
			message = r.bodyString();
		} catch (Exception e2) {
			message = r.toString();
		}
		throw new BaseException(message);
	}


	private String getUpToken(String bucketName) {
		long currentTime = System.currentTimeMillis();
		ExpireableUpToken token = bucketUploadTokenCache.get(bucketName);
		if(token != null && token.expiredAt > currentTime){
			return token.token;
		}
		synchronized (bucketUploadTokenCache) {
			String uploadToken = auth.uploadToken(bucketName);
			bucketUploadTokenCache.put(bucketName, new ExpireableUpToken(uploadToken, currentTime + 3500));
			return uploadToken;
		}
	}

	@Override
	protected String getFullPath(String bucketName, String file) {
		bucketName = buildBucketName(bucketName);
		if(file.startsWith(HTTP_PREFIX) || file.startsWith(HTTPS_PREFIX)){
			return file;
		}
		return buildBucketUrlPrefix(bucketName).concat(file);
	}

	@Override
	protected String buildBucketUrlPrefix(String buketName){
		String rs = "";
		String path = "/v6/domain/list?tbl="+buketName+"\n";
        String accessToken = auth.sign(path);
        String url = "http://api.qiniu.com/v6/domain/list?tbl="+buketName;                
        
        OkHttpClient client = new OkHttpClient();       
        Request request = new Request.Builder().url(url)
        		 .addHeader("Host", "api.qiniu.com")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "QBox " + accessToken).build();
        okhttp3.Response re = null;
        try {
            re = client.newCall(request).execute();
            if (re.isSuccessful() == true) {
            	String[] reArr = StoInternalJsonUtils.toObject(re.body().string(), String[].class);
            	if(reArr.length > 0){
            		rs = reArr[0];
            		if(!rs.endsWith(FilePathHelper.DIR_SPLITER)){
            			rs = rs.concat(FilePathHelper.DIR_SPLITER);
            		}
            	}
            } else {
                throw new BaseException(re.message());
            }
        } catch (IOException e) {
        	throw new BaseException(e.getMessage());
        }
        return rs;
	}

	@Override
	protected String generatePreSignedUrl(String bucketName,String fileKey,int expireInSeconds) {
		bucketName = buildBucketName(bucketName);
		String path = getFullPath(bucketName,fileKey);
		return auth.privateDownloadUrl(path, expireInSeconds);
	}

	private class ExpireableUpToken {
		String token;
		long expiredAt;
		/**
		 * @param token
		 * @param expiredAt
		 */
		public ExpireableUpToken(String token, long expiredAt) {
			super();
			this.token = token;
			this.expiredAt = expiredAt;
		}
	}

}
