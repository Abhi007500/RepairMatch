# ==============================================================================
# Multi-Stage Dockerfile for RepairMatch Backend (Spring Boot 3 + Java 21)
# Optimized for Render Cloud Docker Runtime & Production Environments
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Application
# ------------------------------------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder
WORKDIR /app

# Copy Maven descriptor and wrapper files
COPY pom.xml ./
COPY .mvn/ .mvn/
COPY mvnw ./

# Pre-fetch project dependencies for Docker layer caching
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src ./src

# Build production executable JAR (skip unit tests in container packaging)
RUN mvn clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Minimal Production Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime
WORKDIR /app

# Create non-root system user and group for security
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup appuser

# Copy executable jar from builder stage
COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

# Run as non-root user
USER appuser

# Expose default port (overridden dynamically by Render via $PORT)
ENV PORT=8080
EXPOSE 8080

# Production JVM flags for container memory management
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Launch Spring Boot with dynamic port binding for Render
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT}"]
