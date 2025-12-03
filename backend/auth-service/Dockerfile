FROM tomcat:10.1-jdk21

WORKDIR /app

COPY target/auth-service.war /app/app.war

# Promena porta iz default 8080 u 8081
RUN sed -i 's/port="8080"/port="8081"/' /usr/local/tomcat/conf/server.xml

RUN sed -i 's/port="8005"/port="-1"/' /usr/local/tomcat/conf/server.xml

# Deploy WAR kao ROOT aplikaciju
RUN mv /app/app.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]