package com.nx.storage.adapter.provider;

import com.nx.storage.adapter.model.NxUploadObject;
import com.nx.storage.adapter.model.NxUploadResult;
import com.nx.storage.adapter.model.NxUploadTokenParam;
import com.nx.storage.adapter.model.NxObjectMetadata;

import java.io.InputStream;
import java.util.Map;

/**
 * 上传接口
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2017年1月5日
 */
public interface StoProvider {

	String name();


	boolean existsBucket(String bucketName);
	
	void createBucket(String bucketName,boolean isPrivate);
	
	void deleteBucket(String bucketName);

	/**
	 * 文件上传
	 * @param object
	 * @return
	 */
	public NxUploadResult upload(NxUploadObject object);

	/**
	 * 获取文件下载地址
	 * @param bucketName
	 * @param fileKey 文件（全路径或者fileKey）
	 * @param expireInSeconds
	 * @return
	 */
	public String getDownloadUrl(String bucketName,String fileKey, int expireInSeconds);
	
	public boolean exists(String bucketName,String fileKey);
	/**
	 * 删除文件
	 * @return
	 */
	public boolean delete(String bucketName,String fileKey);
	
	byte[] getObjectBytes(String bucketName,String fileKey);
	
	InputStream getObjectInputStream(String bucketName,String fileKey);
	
	public String downloadAndSaveAs(String bucketName,String fileKey,String localSaveDir);
	
	public Map<String, Object> createUploadToken(NxUploadTokenParam param);
	
	NxObjectMetadata getObjectMetadata(String bucketName, String fileKey);
	
	void close();
}
