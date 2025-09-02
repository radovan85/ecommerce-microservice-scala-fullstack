FROM docker.io/library/openjdk:17-alpine

RUN apk add --no-cache bash unzip

WORKDIR /app

COPY target/universal/auth-service-0.1.0-SNAPSHOT.zip /app/auth-service.zip

RUN unzip /app/auth-service.zip && rm /app/auth-service.zip

RUN mv /app/auth-service-0.1.0-SNAPSHOT /app/auth-service

RUN chmod +x /app/auth-service/bin/auth-service

EXPOSE 8081

ENV DB_URL=jdbc:postgresql://postgres:5432/ecommerce-db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=1111

# Pokrećemo aplikaciju
CMD ["/bin/bash", "-c", "/app/auth-service/bin/auth-service"]
