FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/order-service-1.0-SNAPSHOT.zip /app/order-service.zip

RUN unzip /app/order-service.zip && rm /app/order-service.zip

RUN mv /app/order-service-1.0-SNAPSHOT /app/order-service

RUN chmod +x /app/order-service/bin/order-service

CMD ["/bin/bash", "-c", "exec /app/order-service/bin/order-service"]