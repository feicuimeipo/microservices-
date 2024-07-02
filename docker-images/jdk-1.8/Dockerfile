FROM adoptopenjdk/openjdk8
MAINTAINER nianxiaoling


ENV LANG='zh_CN.UTF-8'
ENV LANGUAGE='zh_CN:zh'
ENV LC_ALL='zh_CN.UTF-8'
ENV TIME_ZONE Asia/Shanghai
ENV TZ=PRC


RUN mkdir /skywalking/agent/ -p
RUN mkdir /config -p
RUN mkdir /var/logs/pharmcube -p
COPY skywalking/agent/* /skywalking/agent/
COPY shell/server.sh /server.sh

RUN chmod +x /server.sh

WORKDIR /
