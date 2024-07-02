rem 下载jacoco文件后解压
rem jacocoagent.jar  mini.jar


rem 使用命令启动SpringBoot，启动参数添加-javaagent：
java -javaagent:jacoco/jacocoagent.jar -jar target/mini-0.0.1-SNAPSHOT.jar

rem 此时会生成1个jacoco.exec文件，这个文件就是覆盖率数据文件，采用以下命令根据数据文件生成HTML报告：
java -jar jacoco/jacococli.jar report jacoco.exec --classfiles ./target/classes/ --sourcefiles ./src/main/java/ --html ./report