FROM amazoncorretto:21-alpine-jdk

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/product-service-1.0-SNAPSHOT.zip /app/product-service.zip

RUN unzip /app/product-service.zip && rm /app/product-service.zip

RUN mv /app/product-service-1.0-SNAPSHOT /app/product-service

RUN chmod +x /app/product-service/bin/product-service

CMD ["/bin/bash", "-c", "exec /app/product-service/bin/product-service"]