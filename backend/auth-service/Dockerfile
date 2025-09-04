<<<<<<< HEAD
FROM tomcat:10.1-jdk17

WORKDIR /app

COPY target/auth-service.war /app/app.war

RUN mv /app/app.war /usr/local/tomcat/webapps/ROOT.war

# Usklađene ENV varijable
=======
FROM docker.io/library/openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/auth-service-0.1.0-SNAPSHOT.zip /app/auth-service.zip

RUN unzip /app/auth-service.zip && rm /app/auth-service.zip

RUN mv /app/auth-service-0.1.0-SNAPSHOT /app/auth-service

RUN chmod +x /app/auth-service/bin/auth-service

EXPOSE 8081

>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
ENV DB_URL=jdbc:postgresql://postgres:5432/ecommerce-db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=1111

<<<<<<< HEAD
CMD ["catalina.sh", "run"]
=======
# Pokrećemo aplikaciju
CMD ["/bin/bash", "-c", "/app/auth-service/bin/auth-service"]
>>>>>>> 5b997bc28e9eae90ccd8ac621149bafaa1f732f4
