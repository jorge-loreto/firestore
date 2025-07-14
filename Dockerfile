# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR file into the container
COPY target/bot-iteci.jar app.jar

# Expose the default Cloud Run port
EXPOSE 8080

# Run the JAR and use PORT environment variable
#CMD ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]

ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-jar", "app.jar"]
