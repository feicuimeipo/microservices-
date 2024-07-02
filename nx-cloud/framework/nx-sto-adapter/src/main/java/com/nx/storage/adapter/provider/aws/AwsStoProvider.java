package com.nx.storage.adapter.provider.aws;

import com.nx.common.exception.BaseException;
import com.nx.storage.adapter.model.NxObjectMetadata;
import com.nx.storage.adapter.model.NxUploadObject;
import com.nx.storage.adapter.model.NxUploadResult;
import com.nx.storage.adapter.model.NxUploadTokenParam;
import com.nx.storage.adapter.provider.StoProviderConfig;
import com.nx.storage.adapter.provider.AbstractStoProvider;
import com.nx.storage.core.enums.StoProviderEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static com.nx.storage.adapter.provider.StoProviderConfigFactory.currentBucketConfig;


public class AwsStoProvider extends AbstractStoProvider {

    public static final String NAME = StoProviderEnum.aws.getCode();;

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsStoProvider.class);

    private S3Client s3Client = null;
    private S3Presigner s3Presigner = null;
    private Region region = null;

    private final String publicPolicyTemplate ="{\n" +
            "    \"Version\": \"2012-10-17\",\n" +
            "    \"Statement\": [\n" +
            "        {\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Principal\": {\n" +
            "                \"AWS\": [\n" +
            "                    \"*\"\n" +
            "                ]\n" +
            "            },\n" +
            "            \"Action\": [\n" +
            "                \"s3:GetBucketLocation\",\n" +
            "                \"s3:ListBucket\",\n" +
            "                \"s3:ListBucketMultipartUploads\"\n" +
            "            ],\n" +
            "            \"Resource\": [\n" +
            "                \"arn:aws:s3:::%s\"\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"Effect\": \"Allow\",\n" +
            "            \"Principal\": {\n" +
            "                \"AWS\": [\n" +
            "                    \"*\"\n" +
            "                ]\n" +
            "            },\n" +
            "            \"Action\": [\n" +
            "                \"s3:ListMultipartUploadParts\",\n" +
            "                \"s3:PutObject\",\n" +
            "                \"s3:AbortMultipartUpload\",\n" +
            "                \"s3:DeleteObject\",\n" +
            "                \"s3:GetObject\"\n" +
            "            ],\n" +
            "            \"Resource\": [\n" +
            "                \"arn:aws:s3:::%s/*\"\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    public AwsStoProvider(StoProviderConfig conf) {
        super(conf);
        String regionName = conf.getRegionName();
        if(StringUtils.isBlank(regionName)) {
            conf.setRegionName("china-south-1");
        }
        region=Region.of(regionName);
        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(new AwsCredentialsProvider() {
                    @Override
                    public AwsCredentials resolveCredentials() {
                        return new AwsCredentials() {
                            @Override
                            public String accessKeyId() {
                                return conf.getAccessKey();
                            }

                            @Override
                            public String secretAccessKey() {
                                return conf.getSecretKey();
                            }
                        };
                    }
                }).build();
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public boolean existsBucket(String bucketName) {
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(ListBucketsRequest.builder().build());
        List<Bucket> buckets = listBucketsResponse.buckets();
        if (buckets != null) {
            for (Bucket bucket : buckets) {
                if (bucket.name().equals(bucketName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void createBucket(String bucketName, boolean isPrivate) {
        try {
            BucketCannedACL acl=null;
            if (isPrivate) {
                acl=BucketCannedACL.PRIVATE;
            }else{
                acl=BucketCannedACL.PUBLIC_READ;
            }
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName)
                    .acl(acl).build());
            s3Client.putBucketPolicy(PutBucketPolicyRequest.builder().bucket(bucketName).policy(String.format(publicPolicyTemplate, bucketName,bucketName)).build());
        } catch (Exception e) {
            LOGGER.error("创建Bucket[{}]出错, e={}", bucketName, ExceptionUtils.getMessage(e), e);
        }
    }

    @Override
    public void deleteBucket(String bucketName) {
        try {
            s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build());
        } catch (Exception e) {
            LOGGER.error("删除Bucket[{}]出错, e={}", bucketName, ExceptionUtils.getMessage(e), e);
        }
    }



    @Override
    public NxUploadResult upload(NxUploadObject object) {
        try {
            String bucketName = object.getBucketName();
            if (StringUtils.isEmpty(bucketName)) {
                throw new BaseException("BucketName 不能为空");
            }
            String fileKey = object.getFileKey();
            PutObjectResponse putObjectResponse = null;
            long size=0;
            if (object.getFile() != null) {
                size=object.getFile().length();
                PutObjectRequest putRequest = PutObjectRequest
                        .builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .contentType(object.getMimeType())
                        .build();
                putObjectResponse = s3Client.putObject(putRequest, object.getFile().toPath());
            } else if (object.getInputStream() != null) {
                size=object.getInputStream().available();
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .contentType(object.getMimeType())
                        .build();
                putObjectResponse = s3Client.putObject(putRequest, RequestBody.fromInputStream(object.getInputStream(), object.getInputStream().available()));
            } else if (object.getBytes() != null) {
                size=object.getBytes().length;
                PutObjectRequest putRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .contentType(object.getMimeType())
                        .build();
                putObjectResponse = s3Client.putObject(putRequest, RequestBody.fromBytes(object.getBytes()));
            }
            if (putObjectResponse != null) {
                NxUploadResult uploadResult=new NxUploadResult(fileKey, getDownloadUrl(bucketName, fileKey, 300), null);
                uploadResult.setMimeType(object.getMimeType());
                uploadResult.setFileSize(size);
                return uploadResult;
            }
        } catch (BaseException e){
            throw e;
        } catch (Exception e) {
            LOGGER.warn("上传失败, e={}", ExceptionUtils.getMessage(e), e);
            throw new BaseException(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean exists(String bucketName, String fileKey) {
        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        HeadObjectResponse headObjectResponse = s3Client.headObject(headObjectRequest);
        if (headObjectResponse != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String bucketName, String fileKey) {
        try {
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            DeleteBucketResponse deleteBucketResponse = s3Client.deleteBucket(deleteBucketRequest);
            return deleteBucketResponse.sdkHttpResponse().isSuccessful();
        } catch (Exception e) {
            LOGGER.error("删除Bucket[{}]出错, e={}", bucketName, ExceptionUtils.getMessage(e), e);
        }
        return false;
    }

    @Override
    public byte[] getObjectBytes(String bucketName, String fileKey) {
        byte[] bytes = new byte[0];
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(getObjectRequest);
            bytes = objectAsBytes.asByteArray();
        } catch (Exception e) {
            LOGGER.error("getObjectBytes出错, bucketName={}, fileKey={}, e={}", bucketName, fileKey, ExceptionUtils.getMessage(e), e);
        }
        return bytes;
    }

    @Override
    public InputStream getObjectInputStream(String bucketName, String fileKey) {
        byte[] bytes = getObjectBytes(bucketName, fileKey);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return inputStream;
    }

    @Override
    public Map<String, Object> createUploadToken(NxUploadTokenParam param) {
        return null;
    }

    @Override
    public NxObjectMetadata getObjectMetadata(String bucketName, String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        ResponseInputStream<GetObjectResponse> object = s3Client.getObject(getObjectRequest);
        Map<String, String> metadata = object.response().metadata();
        if (metadata != null) {
            NxObjectMetadata cObjectMetadata=new NxObjectMetadata();
            cObjectMetadata.setCustomMetadatas(metadata);
            return cObjectMetadata;
        }
        return null;
    }

    @Override
    public void close() {
        s3Client.close();
        s3Presigner.close();
    }

    @Override
    protected String buildBucketUrlPrefix(String bucketName) {
        StoProviderConfig conf = currentBucketConfig(StoProviderEnum.aws,bucketName);
        String baseUrl=conf.getEndpoint();
        if (!baseUrl.endsWith(File.separator)) {
            baseUrl = baseUrl + File.separator;
        }
        String urlPrefix = baseUrl.replace("://", "://" + bucketName+".");
        return urlPrefix;
    }

    @Override
    protected String generatePreSignedUrl(String bucketName, String fileKey, int expireInSeconds) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();
        GetObjectPresignRequest getObjectPresignRequest =  GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        String url = presignedGetObjectRequest.url().toString();
        return url;
    }
}
