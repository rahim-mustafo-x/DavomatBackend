#!/bin/bash
set -e

echo "======================================"
echo "🐳 Building Docker Image"
echo "======================================"

# Check if backend JAR exists
if [ ! -f "target/davomat-backend.jar" ]; then
    echo "❌ Error: target/davomat-backend.jar not found"
    echo "Please run ./build-backend.sh first"
    exit 1
fi

# Check if frontend build exists
if [ ! -d "frontend/dist" ]; then
    echo "❌ Error: frontend/dist/ not found"
    echo "Please run ./build-frontend.sh first"
    exit 1
fi

echo "✅ Backend JAR found: target/davomat-backend.jar"
echo "✅ Frontend build found: frontend/dist/"
echo ""

# Image name and tag
IMAGE_NAME="davomat-app"
IMAGE_TAG="latest"

echo "📦 Building Docker image: $IMAGE_NAME:$IMAGE_TAG"
echo ""

# Build the Docker image
docker build -t $IMAGE_NAME:$IMAGE_TAG .

echo ""
echo "✅ Docker image built successfully!"
echo "======================================"
echo ""
echo "To run the application:"
echo "  docker run -p 8080:8080 $IMAGE_NAME:$IMAGE_TAG"
echo ""
echo "Or use docker-compose:"
echo "  docker-compose up -d"
echo ""
echo "======================================"
