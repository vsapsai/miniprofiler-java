#!/bin/bash

set -e

CATALINA_BASE='/var/lib/tomcat8'
DEPLOY_TO_ROOT='true'
#CONTEXT_PATH='##CONTEXT_PATH##'
SERVER_HTTP_PORT='80'

TEMP_STAGING_DIR='/tmp/codedeploy-deployment-staging-area'
WAR_STAGED_LOCATION="$TEMP_STAGING_DIR/general.war"
HTTP_PORT_CONFIG_XSL_LOCATION="$TEMP_STAGING_DIR/configure_http_port.xsl"

# In Tomcat, ROOT.war maps to the server root
if [[ "$DEPLOY_TO_ROOT" = 'true' ]]; then
    CONTEXT_PATH='ROOT'
fi

# Remove unpacked application artifacts
if [[ -f $CATALINA_BASE/webapps/$CONTEXT_PATH.war ]]; then
    rm $CATALINA_BASE/webapps/$CONTEXT_PATH.war
fi
if [[ -d $CATALINA_BASE/webapps/$CONTEXT_PATH ]]; then
    rm -rfv $CATALINA_BASE/webapps/$CONTEXT_PATH
fi

# Copy the WAR file to the webapps directory
cp $WAR_STAGED_LOCATION $CATALINA_BASE/webapps/$CONTEXT_PATH.war

# Configure the Tomcat server HTTP connector
{ which xsltproc; } || { apt-get install -y xsltproc; }
cp $CATALINA_BASE/conf/server.xml $CATALINA_BASE/conf/server.xml.bak
xsltproc $HTTP_PORT_CONFIG_XSL_LOCATION $CATALINA_BASE/conf/server.xml.bak > $CATALINA_BASE/conf/server.xml

service tomcat8 start
