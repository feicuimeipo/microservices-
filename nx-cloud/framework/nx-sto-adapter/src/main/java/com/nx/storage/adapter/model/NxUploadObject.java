package com.nx.storage.adapter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.nx.storage.utils.FilePathHelper;
import com.nx.storage.utils.MimeTypeFileExtensionConvert;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @description <br>
 * @author <a href="mailto:xlnian@163.com">nianxi</a>
 * @date 2017年1月5日
 */
@JsonInclude(Include.NON_NULL)
public class NxUploadObject {

	private String bucketName;
	private String fileKey;
	private String extension;
	private String mimeType;
	@JsonIgnore
	private String folderPath;
	@JsonIgnore
	private byte[] bytes;
	@JsonIgnore
	private File file;
	private long fileSize;
	@JsonIgnore
	private InputStream inputStream;
	private Map<String, Object> metadata;

	public NxUploadObject(String filePath) {
		this(new File(filePath));
	}

	public NxUploadObject(File file) {
		this.file = file;
		this.fileSize = file.length();
		
		if(file.getName().contains(FilePathHelper.DOT)){
			this.extension = file.getName().substring(file.getName().lastIndexOf(FilePathHelper.DOT) + 1);
			this.mimeType = MimeTypeFileExtensionConvert.getFileMimeType(extension);
		}
		
		try {
			if(this.mimeType == null) {
				MagicMatch magicMatch = Magic.getMagicMatch(file, true, true);
				this.mimeType = StringUtils.trimToNull(magicMatch.getMimeType());
				if(StringUtils.isNotBlank(magicMatch.getExtension())) {
					extension = magicMatch.getExtension();
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public NxUploadObject(InputStream inputStream, long fileSize, String mimeType, String originFileName) {
		this.inputStream = inputStream;
		this.mimeType = mimeType;
		this.fileSize = fileSize;
		if(StringUtils.isNotBlank(originFileName) && originFileName.contains(FilePathHelper.DOT)){
			extension = originFileName.substring(originFileName.lastIndexOf(FilePathHelper.DOT));
		}else if(mimeType != null) {
			this.extension = MimeTypeFileExtensionConvert.getFileExtension(mimeType);
		}
	}

	public NxUploadObject(byte[] bytes, String mimeType) {
		this.bytes = bytes;
		this.mimeType = mimeType;
		this.fileSize = bytes.length;
		if(mimeType != null) {
			this.extension = MimeTypeFileExtensionConvert.getFileExtension(mimeType);
		}
		try {
			if(this.extension == null) {
				MagicMatch magicMatch = Magic.getMagicMatch(bytes);
				extension = StringUtils.trimToNull(magicMatch.getExtension());
				if(StringUtils.isNotBlank(magicMatch.getMimeType())) {
					this.mimeType = magicMatch.getMimeType();
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	

	/**
	 * @return the bucketName
	 */
	public String getBucketName() {
		return bucketName;
	}
	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public File getFile() {
		return file;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setString(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public NxUploadObject addMetaData(String key, Object value) {
		if(metadata == null)metadata = new HashMap<>();
		metadata.put(key, value);
		return this;
	}
	
	public String getMimeType(){
		return mimeType;
	}

	public NxUploadObject folderPath(String folderPath) {
		this.folderPath = folderPath;
		return this;
	}
	
	public NxUploadObject fileKey(String fileKey) {
		this.fileKey = fileKey;
		return this;
	}
	
	public NxUploadObject bucketName(String bucketName) {
		this.bucketName = bucketName;
		return this;
	}

	public String getFileKey() {
		if(StringUtils.isBlank(fileKey)) {
			StringBuilder builder = new StringBuilder();
			if(StringUtils.isNotBlank(this.folderPath)){
				builder.append(FilePathHelper.formatDirectoryPath(folderPath));
			}
			builder.append(FilePathHelper.genTimePathRandomFilePath(extension));
			fileKey = builder.toString();
		}
		
		if(!fileKey.startsWith(FilePathHelper.DIR_SPLITER)) {
			fileKey = FilePathHelper.DIR_SPLITER + fileKey;
		}
		
		return fileKey;
	}

}
