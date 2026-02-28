#!/bin/bash
set -e

echo "======================================"
echo "🚀 Davomat Deployment Script"
echo "======================================"
echo ""
echo "This script will:"
echo "  1. Build backend JAR"
echo "  2. Build frontend"
echo "  3. Build Docker image"
echo "  4. Start with Docker Compose"
echo ""
echo "======================================"
echo ""

# Step 1: Build Backend
echo "Step 1/4: Building Backend JAR..."
./build-backend.sh
echo ""

# Step 2: Build Frontend
echo "Step 2/4: Building Frontend..."
./build-frontend.sh
echo ""

# Step 3: Build Docker Image
echo "Step 3/4: Building Docker Image..."
./build-docker.sh
echo ""

# Step 4: Start with Docker Compose
echo "Step 4/4: Starting with Docker Compose..."
docker-compose up -d

echo ""
echo "======================================"
echo "✅ Deployment Complete!"
echo "======================================"
echo ""
echo "🌐 Application: http://localhost:8080"
echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"
echo "🔌 WebSocket Test: http://localhost:8080/websocket-test.html"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f"
echo ""
echo "To stop:"
echo "  docker-compose down"
echo ""
echo "======================================"
