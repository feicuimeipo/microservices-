#docker login -u admin -p Harbor12345 http://harbor.pharmcube.com;
cat /etc/docker_passwd | docker login --username admin --password-stdin http://harbor.pharmcube.com;
docker pull harbor.pharmcube.com/architecture/pharmcube/adoptopenjdk-openjdk8;