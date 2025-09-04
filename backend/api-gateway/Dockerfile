FROM openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/api-gateway-0.1.0-SNAPSHOT.zip /app/api-gateway.zip

RUN unzip /app/api-gateway.zip && rm /app/api-gateway.zip

RUN mv /app/api-gateway-0.1.0-SNAPSHOT /app/api-gateway

RUN chmod +x /app/api-gateway/bin/api-gateway

CMD ["/bin/bash", "-c", "/app/api-gateway/bin/api-gateway"]