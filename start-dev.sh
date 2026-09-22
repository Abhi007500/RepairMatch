#!/bin/bash
# ==============================================================================
# RepairMatch — Local Development Startup Script
# ==============================================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$PROJECT_ROOT/backend"
FRONTEND_DIR="$PROJECT_ROOT/frontend"

# Load local .env if present
if [ -f "$PROJECT_ROOT/.env" ]; then
    echo "📄 Loading environment variables from .env..."
    set -a
    # shellcheck disable=SC1091
    source "$PROJECT_ROOT/.env"
    set +a
fi

# Ensure common tools are in PATH (Node/NVM, MongoDB, Maven)
if [ -d "$HOME/.nvm/versions/node" ]; then
    LATEST_NODE=$(ls "$HOME/.nvm/versions/node" 2>/dev/null | tail -n 1)
    if [ -n "$LATEST_NODE" ]; then
        export PATH="$HOME/.nvm/versions/node/$LATEST_NODE/bin:$PATH"
    fi
fi
if [ -d "/opt/homebrew/opt/mongodb-community/bin" ]; then
    export PATH="/opt/homebrew/opt/mongodb-community/bin:/opt/homebrew/bin:$PATH"
fi

echo "=================================================="
echo "🛠️  Starting RepairMatch Local Development Stack"
echo "=================================================="

# 1. Check MongoDB Configuration / Connectivity
if [[ "$MONGODB_URI" == *"mongodb+srv://"* ]]; then
    echo "🍃 MongoDB Atlas remote connection detected via MONGODB_URI."
elif [ -n "$MONGODB_URI" ]; then
    echo "🍃 MongoDB configured via MONGODB_URI ($MONGODB_URI)."
else
    echo -n "🍃 Checking local MongoDB status (port 27017)... "
    if nc -z localhost 27017 >/dev/null 2>&1; then
        echo "Running."
    else
        echo "Not detected on port 27017."
        echo "ℹ️  Tip: If using local MongoDB, start it with 'brew services start mongodb-community' or set MONGODB_URI to your MongoDB Atlas connection string in .env"
    fi
fi

# Trap cleanup to cleanly stop child processes on script exit
cleanup() {
    echo ""
    echo "🛑 Shutting down RepairMatch services..."
    if [ -n "$BACKEND_PID" ]; then
        kill "$BACKEND_PID" 2>/dev/null || true
    fi
    if [ -n "$FRONTEND_PID" ]; then
        kill "$FRONTEND_PID" 2>/dev/null || true
    fi
    echo "All services stopped."
    exit 0
}

trap cleanup SIGINT SIGTERM EXIT

# 2. Start Spring Boot Backend
echo "☕ Starting Spring Boot backend on port 8080..."
cd "$BACKEND_DIR"
mvn spring-boot:run > "$BACKEND_DIR/backend.log" 2>&1 &
BACKEND_PID=$!

echo -n "Waiting for backend to be ready"
for i in {1..30}; do
    if curl -s http://localhost:8080/api/health | grep -q '"status":"UP"'; then
        echo " -> Backend is UP! (PID: $BACKEND_PID)"
        break
    fi
    echo -n "."
    sleep 1
    if [ $i -eq 30 ]; then
        echo ""
        echo "❌ Backend failed to start in 30 seconds. Check log: $BACKEND_DIR/backend.log"
        exit 1
    fi
done

# 3. Start Vite React Frontend
echo "⚛️  Starting React frontend on port 5173..."
cd "$FRONTEND_DIR"
npm run dev > "$FRONTEND_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!

echo -n "Waiting for frontend to be ready"
for i in {1..15}; do
    if curl -s http://localhost:5173 | grep -q '<title>'; then
        echo " -> Frontend is UP! (PID: $FRONTEND_PID)"
        break
    fi
    echo -n "."
    sleep 1
    if [ $i -eq 15 ]; then
        echo ""
        echo "❌ Frontend failed to start in 15 seconds. Check log: $FRONTEND_DIR/frontend.log"
        exit 1
    fi
done

echo ""
echo "=================================================="
echo "✨ RepairMatch is running successfully!"
echo "=================================================="
echo "🌐 Frontend URL: http://localhost:5173"
echo "🔌 Backend API:  http://localhost:8080"
echo "🩺 Health Check: http://localhost:8080/api/health"
echo "🍃 Database:     MongoDB Atlas / Local MongoDB (database: ${MONGODB_DATABASE:-repairmatch})"
echo "--------------------------------------------------"
echo "👥 Demo Logins:"
echo "   • Customer:   rahul@gmail.com / password123"
echo "   • Technician: rajesh.tech@repairmatch.com / password123"
echo "   • Admin:      admin@repairmatch.com / password123"
echo "=================================================="
echo "Press [Ctrl+C] to stop all services."

# Wait on background processes
wait "$BACKEND_PID" "$FRONTEND_PID"
