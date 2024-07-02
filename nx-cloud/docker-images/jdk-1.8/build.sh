imageName=pharmcube/adoptopenjdk-openjdk8
imageVersion=latest
harborPrefix=harbor.pharmcube.com/architecture

cat /etc/docker_passwd | docker login --username admin --password-stdin http://harbor.pharmcube.com
#docker login -u admin -p  Harbor12345 http://harbor.pharmcube.com
imageIds=$(docker images | grep "${imageName}" | grep "latest" | awk '{print $3}')
if [ "$imageIds" != "" ]; then
  docker rmi ${imageIds} -f
fi
docker build  -t ${imageName}:${imageVersion} .
docker tag ${imageName}:latest ${harborPrefix}/${imageName}:${imageVersion}
docker push ${harborPrefix}/${imageName}:${imageVersion}

# pull
#docker pull harbor.pharmcube.com/architecture/pharmcube/adoptopenjdk-openjdk8:latest