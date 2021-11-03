FROM tomcat:9.0.54-jre8-openjdk

ARG WAR_FILE=target/*.war

COPY ${WAR_FILE} /usr/local/tomcat/webapps/app.war