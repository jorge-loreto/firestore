# Use a lightweight OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy JAR built by Maven
COPY target/bot-iteci.jar app.jar
ENV SPRING_CLOUD_GCP_FIRESTORE_CREDENTIALS_LOCATION=classpath:nonexistent.json
# Expose the Cloud Run port
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
