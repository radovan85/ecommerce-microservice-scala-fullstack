FROM docker.io/library/openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

# Kopiramo ZIP koji je sbt generisao za Scalatra
COPY target/universal/api-gateway-0.1.0-SNAPSHOT.zip /app/api-gateway.zip

RUN unzip /app/api-gateway.zip && \
    mv /app/api-gateway-0.1.0-SNAPSHOT /app/api-gateway && \
    rm /app/api-gateway.zip

RUN chmod +x /app/api-gateway/bin/api-gateway

EXPOSE 8080

CMD ["/bin/bash", "-c", "/app/api-gateway/bin/api-gateway"]
