#!/bin/bash

set -e

mkdir -p /var/codedeploy/tomcat

cat <<EOF >/var/codedeploy/tomcat/env.properties
APPLICATION_NAME=$APPLICATION_NAME
DEPLOYMENT_GROUP_NAME=$DEPLOYMENT_GROUP_NAME
DEPLOYMENT_ID=$DEPLOYMENT_ID
EOF
