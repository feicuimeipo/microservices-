# 私仓-客户端配置
- /etc/hosts
60.205.229.175  harbor harbor.pharmcube.com

- daemon.json
```aidl
{
  "registry-mirrors": ["https://okrdqafi.mirror.aliyuncs.com","https://docker.mirrors.ustc.edu.cn","http://hub-mirror.c.163.com","https://reg-mirror.qiniu.com"],
  "insecure-registries": ["harbor.pharmcube.com"]
}
```
> 可配自己的加速器,去阿里云上找

- 登录
```aidl
docker login -u 用户 -p 密码 http://harbor.pharmcube.com
```

# 制作镜像
1. 编写Dockefile
2. 生成镜像
```
docker build  -t pharmcube/adoptopenjdk-openjdk8:latest .
```
3. 上传镜像
```aidl
docker tag pharmcube/adoptopenjdk-openjdk8:latest harbor.pharmcube.com/architecture/pharmcube/adoptopenjdk-openjdk8:latest
docker push ip/architecture/pharmcube/adoptopenjdk-openjdk8:latest
``` 

