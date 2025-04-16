# Stage 1: Build with Maven
FROM maven:3.9.5-eclipse-temurin-17-alpine AS builder

# Set working directory inside the container
WORKDIR /app

# Copy the entire project with appropriate ownership
COPY . .

# Build the project, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM eclipse-temurin:17-jre-alpine

# Set working directory for the app
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the built JAR from the Maven build stage
COPY --from=builder /app/target/*.jar app.jar

# Set JVM options
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport -XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError"

# Health check using curl
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:9090/actuator/health || exit 1

# Entry point for running the application
ENTRYPOINT ["/bin/sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]

# Expose the port your app uses
EXPOSE 9090
