FROM eclipse-temurin:21-jdk-alpine

# Instaliramo bash i unzip
RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/eureka-server-app-0.1.0-SNAPSHOT.zip /app/eureka-server.zip

RUN unzip /app/eureka-server.zip && rm /app/eureka-server.zip

RUN mv /app/eureka-server-app-0.1.0-SNAPSHOT /app/eureka-server-app

RUN chmod +x /app/eureka-server-app/bin/eureka-server-app

EXPOSE 8761

CMD ["/bin/bash", "-c", "exec /app/eureka-server-app/bin/eureka-server-app"]
