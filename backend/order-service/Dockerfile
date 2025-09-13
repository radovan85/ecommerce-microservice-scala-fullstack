FROM openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/order-service-1.0-SNAPSHOT.zip /app/order-service.zip

RUN unzip /app/order-service.zip && rm /app/order-service.zip

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV PLAY_PORT=${PLAY_PORT}
ENV PLAY_SECRET=${PLAY_SECRET}

RUN mv /app/order-service-1.0-SNAPSHOT /app/order-service

RUN chmod +x /app/order-service/bin/order-service

CMD ["/bin/bash", "-c", "exec /app/order-service/bin/order-service"]