package com.nx.storage.adapter.provider.huawei;

import com.google.common.collect.Maps;
import com.nx.common.exception.BaseException;
import com.nx.storage.adapter.model.NxObjectMetadata;
import com.nx.storage.adapter.model.NxUploadObject;
import com.nx.storage.adapter.provider.StoProviderConfigFactory;
import com.nx.storage.adapter.provider.AbstractStoProvider;
import com.nx.storage.adapter.model.NxUploadResult;
import com.nx.storage.adapter.model.NxUploadTokenParam;
import com.nx.storage.core.enums.StoProviderEnum;
import com.obs.services.ObsClient;
import com.obs.services.ObsConfiguration;
import com.obs.services.model.*;

import com.nx.storage.adapter.provider.StoProviderConfig;
import com.qcloud.cos.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;


public class HuaweicloudStoProvider extends AbstractStoProvider {

    public static final String NAME = StoProviderEnum.huawei.getCode();

    private static Logger logger = LoggerFactory.getLogger(HuaweicloudStoProvider.class);
    private ObsClient obsClient;

    public HuaweicloudStoProvider(StoProviderConfig conf){
        super(conf);
        String endpoint=conf.getEndpoint();
        ObsConfiguration obsConfiguration = new ObsConfiguration();
        obsConfiguration.setEndPoint(endpoint);
        obsClient = new ObsClient(conf.getAccessKey(), conf.getSecretKey(), obsConfiguration);
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean existsBucket(String bucketName) {
        boolean exists = obsClient.headBucket(bucketName);
        return exists;
    }

    @Override
    public void createBucket(String bucketName, boolean isPrivate) {
        if (existsBucket(bucketName)) {
            throw new RuntimeException("bucket[" + bucketName + "] 已经存在");
        }
        CreateBucketRequest request = new CreateBucketRequest(bucketName, getConfig(StoProviderEnum.huawei,bucketName).getRegionName());
        ObsBucket bucket = new ObsBucket();
        bucket.setBucketName(bucketName);
        AccessControlList acl=null;
        if(isPrivate){
            acl=AccessControlList.REST_CANNED_PRIVATE;
        }else{
            acl=AccessControlList.REST_CANNED_PUBLIC_READ;
        }
        request.setAcl(acl);
        obsClient.createBucket(request);
    }

    @Override
    public void deleteBucket(String bucketName) {
        if (!existsBucket(bucketName)) {
            logger.info("桶[{}]不存在", bucketName);
            return ;
        }
        ObjectListing objectListing = obsClient.listObjects(bucketName);
        if (objectListing != null && !objectListing.getObjects().isEmpty()) {
            logger.error("桶[{}]不为空， 不能删除", bucketName);
            throw new RuntimeException("桶["+bucketName+"]不为空， 不能删除");
        }
        obsClient.deleteBucket(bucketName);
    }


    @Override
    public NxUploadResult upload(NxUploadObject object) {
        String bucketName = object.getBucketName();
        if (StringUtils.isBlank(bucketName)) {
            throw new BaseException("BucketName 不能为空");
        }
        InputStream inputStream = object.getInputStream();
        File file = object.getFile();
        String fileKey = object.getFileKey();
        byte[] bytes = object.getBytes();
        long size=0;
        logger.info("bucknetName={}, fileKey={}", bucketName, fileKey);
        PutObjectResult putObjectResult=null;
        try {
            if (file != null) {
                com.obs.services.model.ObjectMetadata metadata = new com.obs.services.model.ObjectMetadata();
                metadata.setContentType(object.getMimeType());
                putObjectResult = obsClient.putObject(bucketName, fileKey, file, metadata);
                size = file.length();
            } else if (bytes != null) {
                ByteArrayInputStream input = new ByteArrayInputStream(bytes);
                putObjectResult = obsClient.putObject(bucketName, fileKey, input);
                size=bytes.length;
                input.close();
            } else if (inputStream != null) {
                putObjectResult=obsClient.putObject(bucketName, fileKey, inputStream);
                size=inputStream.available();
            }else{
                throw new BaseException("upload object is NULL");
            }
            if (putObjectResult != null) {
                AccessControlList acl = new AccessControlList();
                if (!isBucketPrivate(bucketName)) {
                    acl=AccessControlList.REST_CANNED_PUBLIC_READ;
                }
                obsClient.setObjectAcl(bucketName, fileKey, acl);
                NxUploadResult uploadResult = new NxUploadResult(fileKey, getDownloadUrl(object.getBucketName(),fileKey, 300), null);
                uploadResult.setMimeType(object.getMimeType());
                uploadResult.setFileSize(size);
                return uploadResult;
            }
        } catch (Exception e) {
            logger.error("上传文件出错, bucketName={}, fileKey={}, e={}", bucketName, fileKey, ExceptionUtils.getMessage(e));
            throw new BaseException(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean exists(String bucketName, String fileKey) {
        if (!existsBucket(bucketName)) {
            return false;
        }
        ObsObject object = null;
        try {
            object = obsClient.getObject(bucketName, fileKey);
        } catch (Exception e) {
            logger.error("文件不存在, bucketName={}, fileKey={}, e={}", bucketName, fileKey, ExceptionUtils.getMessage(e));
        }
        return object!=null;
    }

    @Override
    public boolean delete(String bucketName, String fileKey) {
        if (!exists(bucketName, fileKey)) {
            return false;
        }
        DeleteObjectRequest request = new DeleteObjectRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(fileKey);
        DeleteObjectResult result = obsClient.deleteObject(request);
        return result.isDeleteMarker();
    }

    @Override
    public byte[] getObjectBytes(String bucketName, String fileKey) {
        if (!existsBucket(bucketName)) {
            logger.info("Bucket[{}]不存在", bucketName);
            return null;
        }
        try {
            ObsObject object = obsClient.getObject(bucketName, fileKey);
            InputStream inputStream = object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            return bytes;
        } catch (Exception e) {
            logger.error("获取字节, bucketName={}, fileKey={}, e={}", bucketName, fileKey, ExceptionUtils.getMessage(e));
        }
        return null;
    }

    @Override
    public InputStream getObjectInputStream(String bucketName, String fileKey) {
        if (!existsBucket(bucketName)) {
            logger.info("Bucket[{}]不存在", bucketName);
            return null;
        }
        try {
            ObsObject object = obsClient.getObject(bucketName, fileKey);
            InputStream inputStream = object.getObjectContent();
            return inputStream;
        }catch (Exception e){
            logger.error("获取流失败, bucketName={}, fileKey={}, e={}", bucketName, fileKey, ExceptionUtils.getMessage(e));
            throw new BaseException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> createUploadToken(NxUploadTokenParam param) {
        return null;
    }

    @Override
    public NxObjectMetadata getObjectMetadata(String bucketName, String fileKey) {
        com.obs.services.model.ObjectMetadata objectMetadata = obsClient.getObjectMetadata(bucketName, fileKey);
        if (objectMetadata == null) {
            return null;
        }
        NxObjectMetadata result = new NxObjectMetadata();
        Map<String, Object> customMetadata = objectMetadata.getMetadata();
        if (customMetadata != null) {
            Map<String, String> metadata= Maps.newHashMap();
            for (Map.Entry<String, Object> entry : customMetadata.entrySet()) {
                metadata.put(entry.getKey(), entry.getValue().toString());
            }
            result.setCustomMetadatas(metadata);
        }
        result.setMimeType(objectMetadata.getContentType());
        result.setFilesize(objectMetadata.getContentLength());

        return result;
    }

    @Override
    public void close() {
        try {
            if (obsClient!=null) {
                obsClient.close();
            }
        } catch (Exception e) {
            logger.error("obsClient关闭失败, e={}", ExceptionUtils.getMessage(e));
        }
    }

    @Override
    protected String buildBucketUrlPrefix(String bucketName) {
        StoProviderConfig config = StoProviderConfigFactory.currentBucketConfig(StoProviderEnum.huawei,bucketName);
        String baseUrl= config.getEndpoint();
        if (!baseUrl.endsWith(File.separator)) {
            baseUrl = baseUrl + File.separator;
        }
        String urlPrefix = baseUrl.replace("://", "://" + bucketName+".");
        return urlPrefix;
    }

    @Override
    protected String generatePreSignedUrl(String bucketName, String fileKey, int expireInSeconds) {
        //默认5分钟， 最长7天
        if (!exists(bucketName, fileKey)) {
            throw new BaseException("对象[bucketName=" + bucketName + ",fileKey=" + fileKey + "]不存在");
        }
        TemporarySignatureRequest req = new TemporarySignatureRequest(HttpMethodEnum.GET, expireInSeconds);
        req.setBucketName(bucketName);
        req.setObjectKey(fileKey);
        TemporarySignatureResponse res = obsClient.createTemporarySignature(req);
        String signedUrl = res.getSignedUrl();
        return signedUrl;
    }

    public boolean isBucketPrivate(String bucketName){
        if (!existsBucket(bucketName)) {
            throw new RuntimeException("bucket["+bucketName+"]不存在");
        }
        AccessControlList acl = obsClient.getBucketAcl(bucketName);
        Set<GrantAndPermission> grants = acl.getGrants();
        if (grants != null) {
            for (GrantAndPermission grant : grants) {
                if (grant.getGrantee().equals(GroupGrantee.ALL_USERS) && grant.getPermission().equals(Permission.PERMISSION_READ)) {
                    return false;
                }
            }
        }
        return true;
    }
}
