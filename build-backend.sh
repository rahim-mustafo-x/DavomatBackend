#!/bin/bash
set -e

echo "======================================"
echo "🔨 Building Backend"
echo "======================================"

# Clean and build
echo "📦 Running Maven clean package..."
./mvnw clean package -DskipTests

# Check if JAR was created
if [ -f "target/davomat-backend.jar" ]; then
    echo "✅ Backend build successful!"
    echo "📦 Generated: target/davomat-backend.jar"
    echo "======================================"
else
    echo "❌ Error: davomat-backend.jar not found in target/"
    exit 1
fi
