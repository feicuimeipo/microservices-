
```aidl
mvn test -Dtest=类名         //执行单个测试类

mvn test -Dtest=类名,类名　　//逗号或* 分割执行多个测试类

命令：mvn test surefire-report:report -DskipTests=true  执行生成html文件

-Dmaven.test.failure.ignore=true

mvn cobertura:cobertura  -Dmaven.test.failure.ignore=true 生成覆盖率报告
```

http://t.zoukankan.com/yan-sh-p-13932161.html
https://www.wangt.cc/2021/09/jacoco%E5%8D%95%E5%85%83%E6%B5%8B%E8%AF%95%E5%B7%A5%E5%85%B7%E7%9A%84%E4%BD%BF%E7%94%A8-maven%E9%A1%B9%E7%9B%AE%E9%85%8D%E7%BD%AE/
