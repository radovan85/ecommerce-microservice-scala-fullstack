FROM tomcat:10.1-jdk17

WORKDIR /app

COPY target/auth-service.war /app/app.war

RUN mv /app/app.war /usr/local/tomcat/webapps/ROOT.war

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

EXPOSE 8080

CMD ["catalina.sh", "run"]