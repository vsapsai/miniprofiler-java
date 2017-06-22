#!/bin/bash

set -e

CATALINA_BASE='/var/lib/tomcat8'
SERVER_HTTP_PORT='80'

TEMP_STAGING_DIR='/tmp/codedeploy-deployment-staging-area'
HTTP_PORT_CONFIG_XSL_LOCATION="$TEMP_STAGING_DIR/configure_http_port.xsl"

# Parameters are:
#   $1 - .war file name
#   $2 - context name to use
copy_war_file_to_context() {
    WAR_STAGED_LOCATION="$TEMP_STAGING_DIR/$1"
    CONTEXT_PATH="$2"

    # Remove unpacked application artifacts
    if [[ -f $CATALINA_BASE/webapps/$CONTEXT_PATH.war ]]; then
        rm $CATALINA_BASE/webapps/$CONTEXT_PATH.war
    fi
    if [[ -d $CATALINA_BASE/webapps/$CONTEXT_PATH ]]; then
        rm -rfv $CATALINA_BASE/webapps/$CONTEXT_PATH
    fi

    # Copy the WAR file to the webapps directory
    cp $WAR_STAGED_LOCATION $CATALINA_BASE/webapps/$CONTEXT_PATH.war
}

copy_war_file_to_context "general.war" "ROOT"
copy_war_file_to_context "spring-web-mvc.war" "spring-web-mvc"

# Configure the Tomcat server HTTP connector
{ which xsltproc; } || { apt-get install -y xsltproc; }
cp $CATALINA_BASE/conf/server.xml $CATALINA_BASE/conf/server.xml.bak
xsltproc $HTTP_PORT_CONFIG_XSL_LOCATION $CATALINA_BASE/conf/server.xml.bak > $CATALINA_BASE/conf/server.xml

service tomcat8 start
