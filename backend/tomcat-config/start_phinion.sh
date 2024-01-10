#!/usr/bin/env bash
set -eux

export CATALINA_OPTS="-DPROXY_NAME=$PROXY_NAME -DPROXY_PORT=$PROXY_PORT -Dfile.encoding=UTF-8"
export UMASK=002

exec "/usr/libexec/tomcat9/tomcat-start.sh"
