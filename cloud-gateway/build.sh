AppVersion=v1.0.0
Profile=$1
if [ "$Profile" == "" ]; then
 Profile=dev
fi
AppName=gateway



#--------------上述的参数需要修改-------------------
imageName=pharmcube/${AppName}
imageVersion=${AppVersion}
harborPrefix=harbor.pharmcube.com/architecture

cat /etc/docker_passwd | docker login --username admin --password-stdin http://harbor.pharmcube.com;
imageId=$(docker images | grep "${imageName}" | grep "${imageVersion}" | awk '{print $3}')
if [ "$imageIds" != "" ]; then
  docker rmi ${imageId} -f
fi

mvn clean package -Dmaven.test.skip=true


docker build --build-arg Profile=${Profile} AppName=${AppName}  -t ${imageName}:${imageVersion} .

docker tag ${imageName}:${imageVersion} ${harborPrefix}/${imageName}:${imageVersion}

docker push ${harborPrefix}/${imageName}:${imageVersion}

