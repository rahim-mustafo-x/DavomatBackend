# ⚡ Quick Start Guide

## 🔒 Security First!

Before deploying, configure your environment variables:

```bash
# 1. Copy the example file
cp .env.example .env

# 2. Edit with your credentials
nano .env
```

Required variables:
- `DB_PASSWORD` - Your secure database password
- `MAIL_USERNAME` - Your email address
- `MAIL_PASSWORD` - Gmail app password (get from https://myaccount.google.com/apppasswords)
- `JWT_SECRET` - Generate with: `openssl rand -base64 64`

See [SECURITY.md](SECURITY.md) for detailed security guide.

## 🚀 Deploy in One Command

```bash
./deploy.sh
```

That's it! This will:
1. ✅ Build backend JAR (`target/davomat-backend.jar`)
2. ✅ Build frontend (`frontend/dist/`)
3. ✅ Build Docker image (`davomat-app:latest`)
4. ✅ Start with Docker Compose (app + PostgreSQL)

## 🌐 Access

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **WebSocket Test**: http://localhost:8080/websocket-test.html

## 📊 Default Credentials

Default accounts are created on first run (change these immediately in production!):

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@davomat.uz | admin123 |
| Teacher | teacher@davomat.uz | teacher123 |
| Student | student@davomat.uz | student123 |

**⚠️ Security Warning**: Change these default passwords immediately after first login!

## 🔧 Manual Steps (Optional)

If you prefer to run each step manually:

```bash
# Step 1: Build backend
./build-backend.sh

# Step 2: Build frontend
./build-frontend.sh

# Step 3: Build Docker image
./build-docker.sh

# Step 4: Start services
docker-compose up -d
```

## 📝 Useful Commands

```bash
# View logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f davomat-app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Restart services
docker-compose restart

# Rebuild and restart
./deploy.sh
```

## 🐛 Troubleshooting

**Port 8080 already in use:**
```bash
# Find and kill process
lsof -i :8080
kill -9 <PID>
```

**Docker issues:**
```bash
# Clean everything
docker-compose down -v
docker system prune -a

# Rebuild from scratch
./deploy.sh
```

**Node.js not found:**
```bash
# Install Node.js
# Ubuntu/Debian: sudo apt install nodejs npm
# macOS: brew install node
# Or download from: https://nodejs.org/
```

## 🔄 Update Application

```bash
# Pull latest code
git pull

# Redeploy
./deploy.sh
```

## 📞 Support

- **Email**: support@davomat.uz
- **Documentation**: [README.md](README.md)

---

**That's all you need! Just run `./deploy.sh` and you're done! 🎉**
