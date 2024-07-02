FROM openjdk:8-jre-slim
MAINTAINER pharmcube

ENV PARAMS=""

ENV TZ=PRC

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir /config
RUN mkdir /skywalking
RUN mkdir /

ADD target/xxx-service-*.jar /app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /app.jar $PARAMS"]