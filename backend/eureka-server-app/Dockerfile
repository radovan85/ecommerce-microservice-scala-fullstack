FROM eclipse-temurin:21-jdk-alpine

# Install curl (required for health check)
RUN apk add --no-cache curl

WORKDIR /app
COPY target/eureka-server-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "-Dspring.main.lazy-initialization=true", "app.jar"]
