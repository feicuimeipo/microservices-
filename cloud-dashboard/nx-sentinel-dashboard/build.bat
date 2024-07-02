
rem curl -X POST "http://119.23.179.163:8848/nacos/v1/console/namespaces?customNamespaceId=test&namespaceName=test&namespaceDesc=test"

rem java -Dserver.port=18080 -Dnacos.address=119.23.179.163:8848 -Dnacos.namespace=pharmcube -Dcsp.sentinel.dashboard.server=localhost:18080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboard.jar -Dserver.servlet.session.timeout=7200 s-Dsentinel.dashboard.auth.username=sentinel -Dsentinel.dashboard.auth.password=123456

call mvn clean package -DskipTests