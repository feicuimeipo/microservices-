package com.nx.storage.adapter.provider.qcloud;


import com.nx.common.exception.BaseException;
import com.nx.storage.adapter.provider.StoProviderConfig;
import com.nx.storage.adapter.provider.StoProviderConfigFactory;
import com.nx.storage.adapter.model.NxObjectMetadata;
import com.nx.storage.adapter.provider.AbstractStoProvider;
import com.nx.storage.utils.StandardThreadExecutor;
import com.nx.storage.core.enums.StoProviderEnum;
import com.nx.storage.adapter.model.NxUploadObject;
import com.nx.storage.adapter.model.NxUploadResult;
import com.nx.storage.utils.FilePathHelper;
import com.nx.storage.adapter.model.NxUploadTokenParam;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.Upload;
import com.tencent.cloud.CosStsClient;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 
 * <br>
 * Class Name   : QcloudStoProvider
 *
 * @author nianxiaoling
 * @version 1.0.0
 * @date 2020年1月2日
 */
public class QcloudStoProvider extends AbstractStoProvider {

	public static final String NAME = StoProviderEnum.qcloud.getCode();;
	private static Pattern bucketNamePattern = Pattern.compile("\\w+\\-[0-9]{5,}");
	
	private COSClient cosclient;
	private TransferManager transferManager;
	private StandardThreadExecutor transferExecutor;

	
	/**
	 * @param conf
	 */
	public QcloudStoProvider(StoProviderConfig conf) {
		super(conf);
		Validate.notBlank(conf.getAppId(), "[appId] not defined");
		//设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
		if(StringUtils.isBlank(conf.getRegionName())){
			conf.setRegionName("ap-guangzhou");
		}

		COSCredentials cred = new BasicCOSCredentials(conf.getAccessKey(), conf.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(conf.getRegionName()));
        clientConfig.setMaxConnectionsCount(conf.getMaxConnections());//getMaxConnectionsCount());
        //生成cos客户端
        cosclient = new COSClient(cred, clientConfig);
        //
        transferExecutor = new StandardThreadExecutor(1, 5,0, TimeUnit.SECONDS, 1,new StandardThreadExecutor.StandardThreadFactory("storage-transfer-executor"));
        transferManager = new TransferManager(cosclient, transferExecutor);
	}

	@Override
	public String name() {
		return NAME;
	}
	
	@Override
	public boolean existsBucket(String bucketName) {
		bucketName = buildBucketName(bucketName);
		return cosclient.doesBucketExist(bucketName);
	}
	


	@Override
	public void createBucket(String bucketName,boolean isPrivate) {
		bucketName = buildBucketName(bucketName);
		if(cosclient.doesBucketExist(bucketName)){
			throw new BaseException(406, "bucketName["+bucketName+"]已存在");
		}
		CreateBucketRequest request = new CreateBucketRequest(bucketName);
		if(isPrivate) {
			request.setCannedAcl(CannedAccessControlList.Private);
		}else {
			request.setCannedAcl(CannedAccessControlList.PublicRead);
		}
		cosclient.createBucket(request);
	}

	@Override
	public void deleteBucket(String bucketName) {
		bucketName = buildBucketName(bucketName);
		cosclient.deleteBucket(bucketName);
	}
	
	@Override
	public boolean exists(String bucketName,String fileKey) {
		fileKey = resolveFileKey(bucketName, fileKey);
		bucketName = buildBucketName(bucketName);
		return cosclient.doesObjectExist(bucketName, fileKey);
	}
	
	@Override
	public NxUploadResult upload(NxUploadObject object) {
		String bucketName = buildBucketName(object.getBucketName());
		StoProviderConfig conf = StoProviderConfigFactory.getConfig(StoProviderEnum.qcloud,bucketName);

		PutObjectRequest request;
		String fileKey = object.getFileKey();
		if(object.getFile() != null){
			request = new PutObjectRequest(bucketName, fileKey, object.getFile());
		}else if(object.getBytes() != null){
			ByteArrayInputStream inputStream = new ByteArrayInputStream(object.getBytes());
			com.qcloud.cos.model.ObjectMetadata objectMetadata = new com.qcloud.cos.model.ObjectMetadata();
	        objectMetadata.setContentLength(object.getFileSize());
			request = new PutObjectRequest(bucketName, fileKey, inputStream, objectMetadata);
		}else if(object.getInputStream() != null){
			com.qcloud.cos.model.ObjectMetadata objectMetadata = new com.qcloud.cos.model.ObjectMetadata();
	        objectMetadata.setContentLength(object.getFileSize());
			request = new PutObjectRequest(bucketName, fileKey, object.getInputStream(), objectMetadata);
		}else{
			throw new IllegalArgumentException("upload object is NULL");
		}
		
		try {
			if(object.getFileSize() > conf.getMaxAllowdSingleFileSize()){
				Upload upload = transferManager.upload(request);
				com.qcloud.cos.model.UploadResult result = upload.waitForUploadResult();
				return new NxUploadResult(fileKey, getFullPath(object.getBucketName(),fileKey), result.getCrc64Ecma());
			}else{
				PutObjectResult result = cosclient.putObject(request);
				return new NxUploadResult(fileKey,getFullPath(object.getBucketName(),fileKey), result.getContentMd5());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BaseException(500, buildMessage(bucketName,e));
		}
	}



	@Override
	public boolean delete(String bucketName,String fileKey) {
		try {
			bucketName = buildBucketName(bucketName);
			cosclient.deleteObject(bucketName, fileKey);
		} catch (Exception e) {
			throw new BaseException(500, buildMessage(bucketName,e));
		} 
		return true;
	}
	
	@Override
	public byte[] getObjectBytes(String bucketName, String fileKey) {
		try {
			InputStream inputStream = getObjectInputStream(bucketName, fileKey);
			return IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			throw new BaseException(e.getMessage());
		}
	}

	@Override
	public InputStream getObjectInputStream(String bucketName, String fileKey) {
		try {
			String _bucketName = buildBucketName(bucketName);
			String _fileKey = resolveFileKey(bucketName, fileKey);
			COSObject cosObject = cosclient.getObject(_bucketName, _fileKey);
			return cosObject.getObjectContent();
		} catch (Exception e) {
			throw new BaseException(500, buildMessage(bucketName,e));
		}
	}

	@Override
	public NxObjectMetadata getObjectMetadata(String bucketName, String fileKey) {
		try {
			String _bucketName = buildBucketName(bucketName);
			String _fileKey = resolveFileKey(bucketName, fileKey);
			com.qcloud.cos.model.ObjectMetadata metadata = cosclient.getObjectMetadata(_bucketName, _fileKey);
			NxObjectMetadata objectMetadata = new NxObjectMetadata();
			objectMetadata.setCreateTime(metadata.getLastModified());
			objectMetadata.setMimeType(metadata.getContentType());
			objectMetadata.setFilesize(metadata.getContentLength());
			objectMetadata.setHash(metadata.getContentMD5());
			objectMetadata.setExpirationTime(metadata.getExpirationTime());
			objectMetadata.setCustomMetadatas(metadata.getUserMetadata());
			return objectMetadata;
		} catch (Exception e) {
			throw new BaseException(500, buildMessage(bucketName,e));
		}
	}

	//https://github.com/tencentyun/qcloud-cos-sts-sdk/tree/master/java
	@Override
	public Map<String, Object> createUploadToken(NxUploadTokenParam param) {
		StoProviderConfig conf = StoProviderConfigFactory.getConfig(StoProviderEnum.qcloud,param.getBucketName());
		TreeMap<String, Object> config = new TreeMap<String, Object>();
		config.put("SecretId", conf.getAccessKey());
		config.put("SecretKey", conf.getSecretKey());
		config.put("durationSeconds", param.getExpires());
		config.put("bucket", buildBucketName(param.getBucketName()));
		config.put("region", conf.getRegionName());
		//config.put("allowPrefix", "a.jpg");

		// 密钥的权限列表。简单上传、表单上传和分片上传需要以下的权限，其他权限列表请看
		// https://cloud.tencent.com/document/product/436/31923
		String[] allowActions = new String[] {
				// 简单上传
				"name/cos:PutObject",
				// 表单上传、小程序上传
				"name/cos:PostObject",
				// 分片上传
				"name/cos:InitiateMultipartUpload", "name/cos:ListMultipartUploads", "name/cos:ListParts",
				"name/cos:UploadPart", "name/cos:CompleteMultipartUpload" };
		config.put("allowActions", allowActions);

		try {
			org.json.JSONObject json = CosStsClient.getCredential(config);
			return json.toMap();
		} catch (IOException e) {
			throw new BaseException("生成临时凭证错误:"+e.getMessage());
		}
	}


	@Override
	public void close() {
		cosclient.shutdown();
		transferExecutor.shutdown();
	}
	
	protected String buildBucketName(String bucketName){

		StoProviderConfig conf = StoProviderConfigFactory.getConfig(StoProviderEnum.qcloud,bucketName);
		if (StringUtils.isEmpty(conf.getAppId())){
			throw new IllegalArgumentException(StoProviderEnum.qcloud.getCode() + ":" + bucketName + " appId not defined");
		}
		bucketName = super.buildBucketName(bucketName);
		if(bucketName.endsWith(conf.getAppId())) {
			return bucketName;
		}
		if(bucketName.contains(FilePathHelper.MID_LINE) && bucketNamePattern.matcher(bucketName).matches()) {
			return bucketName;
		}
		return new StringBuilder(bucketName).append(FilePathHelper.MID_LINE).append(conf.getAppId()).toString();
	}

	@Override
	protected String buildBucketUrlPrefix(String bucketName) {
		StoProviderConfig config = StoProviderConfigFactory.getConfig(StoProviderEnum.qcloud,bucketName);
		StringBuilder urlBuilder = new StringBuilder()
				   .append("http://") //
				   .append(buildBucketName(bucketName)) //
				   .append(".cos.") //
				   .append(config.getRegionName()) //
				   .append(".myqcloud.com");
		return urlBuilder.toString();
	}

	@Override
	protected String generatePreSignedUrl(String bucketName,String fileKey, int expireInSeconds) {
		bucketName = buildBucketName(bucketName);
		try {
			URL url = cosclient.generatePresignedUrl(bucketName, fileKey, DateUtils.addSeconds(new Date(), expireInSeconds));
			return url.toString();
		} catch (Exception e) {
			throw new BaseException(500, buildMessage(bucketName,e));
		}
	}

	private static String buildMessage(String bucketName,Exception e){
		if(e instanceof CosServiceException){
			if("NoSuchBucket".equals(((CosServiceException)e).getErrorCode())){
				throw new BaseException(404, "bucketName["+bucketName+"]不存在"); 
			}else if("AccessDenied".equals(((CosServiceException)e).getErrorCode())){
				throw new BaseException(403, "appId与bucketName["+bucketName+"]不匹配"); 
			}else if("InvalidAccessKeyId".equals(((CosServiceException)e).getErrorCode())){
				throw new BaseException(40, "AccessKey配置错误"); 
			}
			return ((CosServiceException)e).getErrorMessage();
		}else{
			return e.getMessage();
		}
	}

}
