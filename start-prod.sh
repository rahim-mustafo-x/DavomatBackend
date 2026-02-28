#!/bin/bash
set -e

echo "======================================"
echo "🚀 Starting Production Build & Run"
echo "======================================"

# Step 1: Build Backend
echo ""
echo "Step 1/3: Building Backend..."
./build-backend.sh

# Step 2: Build Frontend
echo ""
echo "Step 2/3: Building Frontend..."
./build-frontend.sh

# Step 3: Start the application
echo ""
echo "Step 3/3: Starting Application..."
echo "======================================"

echo "🚀 Starting Spring Boot application..."
echo "📦 Running: java -jar target/davomat-backend.jar"
echo ""
echo "✅ Application starting..."
echo "🌐 Backend: http://localhost:8080"
echo "📚 Swagger: http://localhost:8080/swagger-ui.html"
echo "🔌 WebSocket Test: http://localhost:8080/websocket-test.html"
echo ""
echo "Press Ctrl+C to stop the application"
echo "======================================"

# Run the application
java -jar target/davomat-backend.jar
