package com.nx.storage.adapter.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2018年4月3日
 */
@JsonInclude(Include.NON_NULL)
public class NxUploadResultParam {
	/**
	 * 业务应用id
	 */
	private String appId;
	
	private String bucketName;
	/**
	 * 文件名称
	 */
	private String fileName;
	/**
	 * 上传文件名称
	 */
	private String fileKey;
	/**
	 * 媒体类型
	 */
	private String mimeType;
	
	private boolean auth;
	
	private long fileSize;
	
    private String fileHash;
    
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the fileKey
	 */
	public String getFileKey() {
		return fileKey;
	}
	/**
	 * @param fileKey the fileKey to set
	 */
	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}
	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}
	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public boolean isAuth() {
		return auth;
	}
	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFileHash() {
		return fileHash;
	}
	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}
	
}
