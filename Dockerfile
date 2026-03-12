# Dockerfile for Davomat Application
# Build backend and frontend before running: ./deploy.sh

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy pre-built JAR file
COPY target/davomat-backend.jar /app/davomat-backend.jar

# Copy pre-built frontend to static resources
COPY frontend/dist /app/static

# Create logs directory
RUN mkdir -p /app/logs

# Expose port
EXPOSE 8080

# Set JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar davomat-backend.jar"]
