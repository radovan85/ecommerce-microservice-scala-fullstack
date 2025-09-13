FROM openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/product-service-1.0-SNAPSHOT.zip /app/product-service.zip

RUN unzip /app/product-service.zip && rm /app/product-service.zip

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV PLAY_PORT=${PLAY_PORT}
ENV PLAY_SECRET=${PLAY_SECRET}

RUN mv /app/product-service-1.0-SNAPSHOT /app/product-service

RUN chmod +x /app/product-service/bin/product-service

CMD ["/bin/bash", "-c", "exec /app/product-service/bin/product-service"]