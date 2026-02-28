# Dockerfile for Davomat Application
# Build backend and frontend before running: ./deploy.sh

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy pre-built JAR file
COPY target/davomat-backend.jar /app/davomat-backend.jar

# Copy pre-built frontend (create empty dir if not exists)
COPY frontend/dist /app/static

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar davomat-backend.jar"]
