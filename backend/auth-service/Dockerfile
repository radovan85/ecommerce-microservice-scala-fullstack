FROM tomcat:10.1-jdk17

WORKDIR /app

COPY target/auth-service.war /app/app.war

RUN sed -i 's/port="8080"/port="8081"/' /usr/local/tomcat/conf/server.xml

RUN mv /app/app.war /usr/local/tomcat/webapps/ROOT.war

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

CMD ["catalina.sh", "run"]
