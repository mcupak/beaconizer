FROM jboss/wildfly:8.2.1.Final
MAINTAINER Miro Cupak <mirocupak@gmail.com>

# prepare wildfly
ENV JBOSS_HOME /opt/jboss/wildfly

USER root

# install necessary packages
RUN yes | yum update
RUN yes | yum install patch wget tree curl zip unzip

# customize wildfly
USER jboss
RUN $JBOSS_HOME/bin/add-user.sh admin admin --silent
ADD *.war $JBOSS_HOME/standalone/deployments/beaconizer.war

# expose ports
EXPOSE 8080
