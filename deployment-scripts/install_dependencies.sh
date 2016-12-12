#!/bin/bash

set -e

apt-get install -y openjdk-8-jdk
apt-get install -y curl

if ! dpkg-query --status tomcat8 > /dev/null ; then
    apt-get install -y tomcat8
    # Make Tomcat work on privileged port 80.
    # Thanks to http://stackoverflow.com/questions/4756039/how-to-change-the-port-of-tomcat-from-8080-to-80
    echo "AUTHBIND=yes" >> /etc/default/tomcat8
    apt-get install -y authbind
    touch /etc/authbind/byport/80
    chmod 500 /etc/authbind/byport/80
    chown tomcat8 /etc/authbind/byport/80
fi

# System instance of Tomcat is installed with CATALINA_HOME in /usr/share/tomcat8
# and CATALINA_BASE in /var/lib/tomcat8, following the rules
# from /usr/share/doc/tomcat8-common/RUNNING.txt.gz
