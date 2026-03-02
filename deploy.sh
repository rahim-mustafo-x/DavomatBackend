#!/bin/bash
set -e

# Function to display usage
usage() {
    echo "Usage: $0 [dev|prod]"
    echo ""
    echo "  dev   - Start development mode (backend:8080, frontend:3000 with hot-reload)"
    echo "  prod  - Deploy production mode with Docker (everything on :8080)"
    echo ""
    echo "If no argument provided, defaults to production mode."
    exit 1
}

# Development mode
dev_mode() {
    echo "======================================"
    echo "🚀 Starting Development Environment"
    echo "======================================"
    echo ""
    echo "Backend: http://localhost:8080"
    echo "Frontend: http://localhost:3000 (hot-reload)"
    echo "Database: localhost:5433 (PostgreSQL)"
    echo ""
    echo "======================================"
    echo ""

    # Load .env file if exists
    if [ -f ".env" ]; then
        set -a
        source .env
        set +a
        echo "✅ Loaded environment variables from .env"
    else
        echo "⚠️  .env file not found, using defaults"
    fi

    # Check if Maven wrapper exists
    if [ ! -f "mvnw" ]; then
        echo "❌ Maven wrapper not found"
        exit 1
    fi

    # Check if backend JAR exists, if not build it
    if [ ! -f "target/davomat-backend.jar" ]; then
        echo "⚠️  Backend JAR not found. Building..."
        ./mvnw clean package -DskipTests
        if [ ! -f "target/davomat-backend.jar" ]; then
            echo "❌ Backend build failed"
            exit 1
        fi
    fi

    # Create logs directory if it doesn't exist
    mkdir -p logs

    # Start backend in background
    echo "📦 Starting Backend (port 8080)..."
    
    # Set DB_URL for development (using host port 5433)
    export DB_URL="jdbc:postgresql://localhost:${DB_PORT_HOST:-5433}/${DB_NAME:-davomat_db}"
    
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
}

# Production mode
prod_mode() {
    echo "======================================"
    echo "🚀 Davomat Production Deployment"
    echo "======================================"

    # Check prerequisites
    command -v java >/dev/null 2>&1 || { echo "❌ Java not found. Install Java 17+"; exit 1; }
    command -v node >/dev/null 2>&1 || { echo "❌ Node.js not found. Install Node.js 18+"; exit 1; }
    command -v docker >/dev/null 2>&1 || { echo "❌ Docker not found. Install Docker"; exit 1; }

    # Check .env file
    if [ ! -f ".env" ]; then
        echo "❌ .env file not found"
        echo "Run: cp .env.example .env"
        echo "Then edit .env with your credentials"
        exit 1
    fi

    echo "✅ Prerequisites check passed"
    echo ""

    # Check if Maven wrapper exists
    if [ ! -f "mvnw" ]; then
        echo "❌ Maven wrapper not found"
        exit 1
    fi

    # Build backend
    echo "📦 Building backend..."
    ./mvnw clean package -DskipTests
    if [ ! -f "target/davomat-backend.jar" ]; then
        echo "❌ Backend build failed"
        exit 1
    fi
    echo "✅ Backend built: target/davomat-backend.jar"
    echo ""

    # Create dist directory if it doesn't exist
    mkdir -p frontend/dist

    # Build frontend
    echo "🎨 Building frontend..."
    cd frontend
    npm install --silent
    npm run build
    if [ ! -d "dist" ]; then
        echo "❌ Frontend build failed"
        exit 1
    fi
    cd ..
    echo "✅ Frontend built: frontend/dist/"
    echo ""

    # Build and start Docker
    echo "🐳 Building Docker image..."
    docker-compose build
    echo ""

    echo "🚀 Starting services..."
    docker-compose up -d

    echo ""
    echo "======================================"
    echo "✅ Deployment Complete!"
    echo "======================================"
    echo ""
    echo "🌐 Application: http://localhost:8080"
    echo "📚 API Docs: http://localhost:8080/swagger-ui.html"
    echo "🔌 WebSocket Test: http://localhost:8080/websocket-test.html"
    echo ""
    echo "📊 View logs: docker-compose logs -f"
    echo "🛑 Stop: docker-compose down"
    echo ""
    echo "======================================"
}

# Main script logic
MODE="${1:-prod}"

case "$MODE" in
    dev)
        dev_mode
        ;;
    prod)
        prod_mode
        ;;
    *)
        usage
        ;;
esac
