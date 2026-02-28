#!/bin/bash
set -e

echo "======================================"
echo "🎨 Building Frontend"
echo "======================================"

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Error: Node.js is not installed"
    echo "Please install Node.js from https://nodejs.org/"
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ Error: npm is not installed"
    echo "Please install npm (comes with Node.js)"
    exit 1
fi

echo "✅ Node.js version: $(node --version)"
echo "✅ npm version: $(npm --version)"
echo ""

# Navigate to frontend directory
cd frontend

# Install dependencies
echo "📦 Installing dependencies..."
npm install

# Build frontend
echo "🔨 Building React application..."
npm run build

# Check if build was successful
if [ -d "dist" ]; then
    echo "✅ Frontend build successful!"
    echo "📦 Generated: frontend/dist/"
    echo "======================================"
else
    echo "❌ Error: frontend/dist/ not found"
    exit 1
fi

cd ..
