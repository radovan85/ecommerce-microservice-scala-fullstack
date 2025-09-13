FROM openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/customer-service-0.1.0-SNAPSHOT.zip /app/customer-service.zip

RUN unzip /app/customer-service.zip && rm /app/customer-service.zip

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}

RUN mv /app/customer-service-0.1.0-SNAPSHOT /app/customer-service

RUN chmod +x /app/customer-service/bin/customer-service

CMD ["/bin/bash", "-c", "exec /app/customer-service/bin/customer-service"]