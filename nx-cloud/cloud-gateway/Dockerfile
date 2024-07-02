FROM pharmcube/adoptopenjdk-openjdk8:latest

MAINTAINER pharmcube.com

ARG Profile
ARG AppName
ARG COLLECTOR_BACKEND_SERVICES=${Profile}.skywalking.pharmcube.com

ENV Profile=${Profile}
ENV SW_AGENT_NAM=${AppName}
ENV SW_AGENT_COLLECTOR_BACKEND_SERVICES=${COLLECTOR_BACKEND_SERVICES}
ENV PARAM=""
ENV JAVAOPTS=""

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mv /skywalking/agent/optional-plugins/apm-spring-cloud-gateway-*.jar /skywalking/agent/plugins/
RUN mv /skywalking/agent/optional-plugins/apm-spring-webflux-*.jar       /skywalking/agent/plugins/

ADD target/${AppName}-*.jar /${AppName}.jar

EXPOSE 18096
EXPOSE 19096

ENTRYPOINT ["sh","-c","/server.sh ${AppName} start ${Profile} ${COLLECTOR_BACKEND_SERVICES} ${JAVAOPTS} ${PARAM}"]