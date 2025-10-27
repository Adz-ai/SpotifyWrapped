# Backend Dockerfile for Spring Boot application
# Multi-stage build for optimal image size

# Stage 1: Build stage
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src
COPY config config

# Build the application (skip tests for faster builds, run tests separately in CI/CD)
RUN ./gradlew bootJar --no-daemon -x test

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Create non-root user for security
RUN groupadd -r spotify && useradd -r -g spotify spotify

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spotify:spotify /app

# Switch to non-root user
USER spotify

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# JVM options can be passed as environment variables
# Example: docker run -e JAVA_OPTS="-Xmx512m" ...
CMD []
