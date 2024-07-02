## 介绍
对象存储服务，本项目作为各云存储服务的中间适配层，只需要修改配置即可轻松切换云存储服务提供商。目前支持：七牛、阿里云、腾讯云、minio

##  快速使用
如果服务只存在一套文件服务配置，我们提供了一个默认客户端。只需要提供如下配置：

```
#可选：aliyun,qcloud,qiniu,minio
pharmcube.storage.adapter.type=qiniu
pharmcube.storage.adapter.accessKey=
pharmcube.storage.adapter.xxx= 
```

# 用法：
```java
public void example() {
    PharmcubeStoProvider provider = StorageProviderServiceFacade.getProvider(); 
    String bucketName = "pharmcube";
    //创建bucket
    provider.createBucket(bucketName);

    StorageUploadObject uploadObject = new CUploadObject(new File("/Users/jiangwei/Desktop/1.txt")).bucketName(bucketName).folderPath("2020/01/13");
    //上传
    StorageUploadResult result = provider.upload(uploadObject);
    //是否存在
    boolean exists = provider.exists("pharmcube", result.getFileKey());
    //元信息
    StorageObjectMetadata metadata = provider.getObjectMetadata(bucketName, result.getFileKey());
    //删除
    provider.delete(null, result.getFileKey());
    
    provider.close();
	}
```

# 使用说明 
### 添加依赖‘
```aidl
<dependency>
	<groupId>com.pharmcube.cloud</groupId>
	<artifactId>pharmcube-storage-adapter</artifactId>
	<version>1.0.0</version>
</dependency>

<!--接入七牛需加入-->
<dependency>
	<groupId>com.qiniu</groupId>
	<artifactId>qiniu-java-sdk</artifactId>
	<version>[7.0.0, 7.2.99]</version>
</dependency>

<!--接入阿里云需加入-->
<dependency>
	<groupId>com.aliyun.oss</groupId>
	<artifactId>aliyun-sdk-oss</artifactId>
	<version>3.11.3</version>
</dependency>

<!--接入腾讯云需加入-->
<dependency>
	<groupId>com.qcloud</groupId>
	<artifactId>cos_api</artifactId>
	<version>5.6.39</version>
</dependency>
```

# 未来计划
 
```aidl
- mongodb
- 本地文件 

```
 
