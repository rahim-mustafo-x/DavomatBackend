#!/bin/bash
set -e

echo "======================================"
echo "🚀 Starting Development Environment"
echo "======================================"
echo ""
echo "Backend will run on: http://localhost:8080"
echo "Frontend will run on: http://localhost:3000"
echo ""
echo "======================================"
echo ""

# Check if backend JAR exists
if [ ! -f "target/davomat-backend.jar" ]; then
    echo "⚠️  Backend JAR not found. Building..."
    ./build-backend.sh
fi

# Start backend in background
echo "📦 Starting Backend (port 8080)..."
java -jar target/davomat-backend.jar > logs/backend-dev.log 2>&1 &
BACKEND_PID=$!
echo "✅ Backend started (PID: $BACKEND_PID)"
echo ""

# Wait for backend to start
echo "⏳ Waiting for backend to be ready..."
sleep 5

# Check if backend is running
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✅ Backend is ready!"
else
    echo "⚠️  Backend might still be starting..."
fi
echo ""

# Start frontend
echo "🎨 Starting Frontend (port 3000)..."
cd frontend
npm run dev

# Cleanup on exit
trap "echo ''; echo 'Stopping backend...'; kill $BACKEND_PID 2>/dev/null; exit" INT TERM EXIT
