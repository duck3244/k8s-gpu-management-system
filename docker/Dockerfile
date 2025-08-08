# Multi-stage build for K8s GPU Management System

# Build stage
FROM openjdk:11-jdk-slim AS builder

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Make gradle wrapper executable
RUN chmod +x gradlew

# Copy source code
COPY src/ src/

# Build application
RUN ./gradlew clean build -x test --no-daemon

# Runtime stage
FROM openjdk:11-jre-slim

# Install required packages for runtime
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create application user
RUN groupadd -r k8sgpu && useradd -r -g k8sgpu k8sgpu

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && chown -R k8sgpu:k8sgpu /app

# Copy built JAR from builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Copy any additional configuration files if needed
COPY --from=builder /app/src/main/resources/application-docker.yml application-docker.yml

# Change ownership
RUN chown -R k8sgpu:k8sgpu /app

# Switch to application user
USER k8sgpu

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=docker -jar app.jar"]