#!/bin/bash
#这里可替换为你自己的执行程序，其他代码无需更改


cd /eip/bin
APP_NAME=$1
Action=$2
Profile=$3
COLLECTOR_BACKEND_SERVICES=$4
JavaOpts=$5
PARAM=$6

if [ "$Profile" == "" ]; then
  Profile=dev
fi

if [ "$JavaOpts" == "" ]; then
  JavaOpts=" -Xms512m -Xmx1024m ";
fi



JAVA_OPTS="-javaagent:/skywalking/agent/skywalking-agent.jar -Dskywalking.agent.service_name=${APP_NAME} -Dskywalking.collector.backend_service=${COLLECTOR_BACKEND_SERVICES} "
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom ${JavaOpts}"
echo "JAVA_OPTS="${JAVA_OPTS}

PARAMS="${PARAM} --pharmcube.boobstrap.profile=${Profile} --pharmcube.boobstrap.application.name=${APP_NAME} --pharmcube.boobstrap.local-dev=false "


LOG_DIR=/var/logs/pharmcube
LOG_FILE=console.log

#使用说明，用来提示输入参数
usage() {
 echo "Usage: sh 脚本名.sh [start|stop|restart|status]"
 exit 1
}
  
#检查程序是否在运行
is_exist(){
 pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
 #如果不存在返回1，存在返回0 
 if [ -z "${pid}" ]; then
 return 1
 else
 return 0
 fi
}
  
#启动方法
start(){
 is_exist
 if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running. pid=${pid} ."
 else
    export JAVA_OPTS
    mkdir ${LOG_DIR} -p
    touch ${LOG_DIR}/${LOG_FILE}
    nohup java ${JAVA_OPTS} -jar ${APP_NAME}.jar ${PARAM} > ${LOG_DIR}/${LOG_FILE} 2>&1 &
    tail ${LOG_DIR}/${LOG_FILE} -f
    #echo "${APP_NAME} start success"
 fi
}

#停止方法
stop(){
 is_exist
 if [ $? -eq "0" ]; then
 kill -9 $pid
 else
 echo "${APP_NAME} is not running"
 fi
}
  
#输出运行状态
status(){
 is_exist
 if [ $? -eq "0" ]; then
 echo "${APP_NAME} is running. Pid is ${pid}"
 else
 echo "${APP_NAME} is NOT running."
 fi
}
  
#重启
restart(){
 stop
 start
}
  
#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "${Action}" in
 "start")
 start
 ;;
 "stop")
 stop
 ;;
 "status")
 status
 ;;
 "restart")
 restart
 ;;
 *)
 usage
 ;;
esac
