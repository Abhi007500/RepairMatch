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

# Ensure common tools are in PATH (Node/NVM, Homebrew PostgreSQL, Maven)
if [ -d "$HOME/.nvm/versions/node" ]; then
    LATEST_NODE=$(ls "$HOME/.nvm/versions/node" 2>/dev/null | tail -n 1)
    if [ -n "$LATEST_NODE" ]; then
        export PATH="$HOME/.nvm/versions/node/$LATEST_NODE/bin:$PATH"
    fi
fi
if [ -d "/opt/homebrew/opt/postgresql@16/bin" ]; then
    export PATH="/opt/homebrew/opt/postgresql@16/bin:/opt/homebrew/bin:$PATH"
fi

echo "=================================================="
echo "🛠️  Starting RepairMatch Local Development Stack"
echo "=================================================="

# 1. Check and Start PostgreSQL
echo -n "🐘 Checking PostgreSQL status... "
if command -v pg_isready >/dev/null 2>&1 && pg_isready -q; then
    echo "Running."
else
    echo "Not running. Starting PostgreSQL 16..."
    if [ -f "/opt/homebrew/opt/postgresql@16/bin/pg_ctl" ]; then
        /opt/homebrew/opt/postgresql@16/bin/pg_ctl -D /opt/homebrew/var/postgresql@16 -l /opt/homebrew/var/log/postgresql@16.log start || true
        sleep 2
    else
        echo "⚠️  pg_ctl not found at default location. Please ensure PostgreSQL is running."
    fi
fi

# Ensure 'repairmatch' database exists
if command -v psql >/dev/null 2>&1; then
    if ! psql -lqt | cut -d \| -f 1 | grep -qw repairmatch; then
        echo "Creating 'repairmatch' database..."
        createdb repairmatch
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
echo "🐘 Database:     PostgreSQL (repairmatch on port 5432)"
echo "--------------------------------------------------"
echo "👥 Demo Logins:"
echo "   • Customer:   rahul@gmail.com / password123"
echo "   • Technician: rajesh.tech@repairmatch.com / password123"
echo "   • Admin:      admin@repairmatch.com / password123"
echo "=================================================="
echo "Press [Ctrl+C] to stop all services."

# Wait on background processes
wait "$BACKEND_PID" "$FRONTEND_PID"
