FROM openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/cart-service-1.0-SNAPSHOT.zip /app/cart-service.zip

RUN unzip /app/cart-service.zip && rm /app/cart-service.zip

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV PLAY_PORT=${PLAY_PORT}

RUN mv /app/cart-service-1.0-SNAPSHOT /app/cart-service

RUN chmod +x /app/cart-service/bin/cart-service

CMD ["/bin/bash", "-c", "exec /app/cart-service/bin/cart-service"]