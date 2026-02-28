# 📊 Davomat - Smart Attendance Management System

A modern attendance management system built with Spring Boot and React, featuring real-time tracking, WebSocket notifications, and role-based dashboards.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![React](https://img.shields.io/badge/React-18-blue)

## ✨ Features

- **Multi-Role System** - Admin, Teacher, and Student dashboards
- **Real-time Updates** - WebSocket notifications for instant updates
- **Course Management** - Full CRUD operations for courses and groups
- **Attendance Tracking** - Mark and monitor attendance with analytics
- **System Logging** - Database-backed logging with filtering
- **JWT Authentication** - Secure token-based authentication

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Docker & Docker Compose (for production)

### 1. Clone & Configure

```bash
git clone <repository-url>
cd davomat-backend

# Create environment file
cp .env.example .env
# Edit .env with your credentials
```

### 2. Deploy

**Production Mode (Docker):**
```bash
./deploy.sh prod
# or simply
./deploy.sh
```

**Development Mode (Hot-reload):**
```bash
./deploy.sh dev
```

### 3. Access

**Production Mode:**
- **Application**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **WebSocket Test**: http://localhost:8080/websocket-test.html

**Development Mode:**
- **Frontend**: http://localhost:3000 (hot-reload)
- **Backend**: http://localhost:8080

## 🔑 Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@davomat.uz | admin123 |
| Teacher | teacher@davomat.uz | teacher123 |
| Student | student@davomat.uz | student123 |

⚠️ **Change these immediately in production!**

## 🛠️ Development Mode

The deploy script supports both development and production modes:

**Development Mode (Hot-reload):**
```bash
./deploy.sh dev
```

This will:
- Build backend JAR (if not exists)
- Start backend on port 8080
- Start frontend dev server on port 3000 with hot-reload

Access:
- Frontend: http://localhost:3000 (hot-reload)
- Backend: http://localhost:8080

**Production Mode (Docker):**
```bash
./deploy.sh prod
# or simply
./deploy.sh
```

This will:
- Build backend and frontend
- Create Docker image
- Start with Docker Compose
- Everything runs on port 8080

## 🔒 Security Configuration

### Required Environment Variables

Create `.env` file:

```env
# Database
DB_NAME=davomat_db
DB_USERNAME=davomat_user
DB_PASSWORD=your_secure_password
DB_PORT_HOST=5433
DB_PORT_CONTAINER=5432

# Application
APP_PORT=8080

# Email (Gmail)
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password

# JWT Secret (generate with: openssl rand -base64 64)
JWT_SECRET=your_generated_secret_here
```

### Gmail App Password

1. Enable 2FA on your Google account
2. Visit: https://myaccount.google.com/apppasswords
3. Generate app password for "Mail"
4. Use this in `.env` (not your regular password)

### Generate JWT Secret

```bash
openssl rand -base64 64
```

## 📚 API Documentation

Interactive API documentation available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Key Endpoints

**Authentication:**
```
POST /auth/login          - User login
POST /auth/register       - User registration
```

**Courses:**
```
GET    /api/course/getAllCourses
POST   /api/course/create
PUT    /api/course/update
DELETE /api/course/delete/{id}
```

**Groups:**
```
GET    /api/group/findByCourseId/{id}
POST   /api/group/create
PUT    /api/group/update
DELETE /api/group/delete/{id}
```

**Students:**
```
GET    /api/student/findByGroupId/{id}
POST   /api/student/addStudent
PUT    /api/student/editStudent
DELETE /api/student/deleteStudent/{id}
```

**Attendance:**
```
POST   /api/attendance/mark
GET    /api/attendance/group/{groupId}
GET    /api/attendance/student/{studentId}
```

**Statistics (Admin):**
```
GET /api/statistics/dashboard
GET /api/statistics/activity
GET /api/statistics/performance
```

## 🔌 WebSocket Integration

Connect with JWT authentication:

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({ 
  'Authorization': 'Bearer ' + jwtToken 
}, function(frame) {
  stompClient.subscribe('/user/queue/course', handleUpdate);
  stompClient.subscribe('/user/queue/student', handleUpdate);
  stompClient.subscribe('/user/queue/group', handleUpdate);
  stompClient.subscribe('/user/queue/attendance', handleUpdate);
});
```

Test WebSocket: http://localhost:8080/websocket-test.html

## 🗂️ Project Structure

```
davomat-backend/
├── src/main/java/uz/coder/davomatbackend/
│   ├── config/           # Configuration
│   ├── controller/       # REST controllers
│   ├── db/              # Database layer
│   ├── jwt/             # JWT authentication
│   ├── model/           # DTOs
│   ├── security/        # Security config
│   └── service/         # Business logic
├── frontend/
│   └── src/
│       ├── api/         # API client
│       ├── pages/       # React pages
│       └── store/       # State management
├── docker-compose.yml   # Docker configuration
├── deploy.sh           # Deployment script
└── pom.xml             # Maven dependencies
```

## 🐳 Docker Commands

```bash
# Deploy production mode
./deploy.sh prod

# Deploy development mode
./deploy.sh dev

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Restart
docker-compose restart

# Clean rebuild
docker-compose down -v
./deploy.sh prod
```

## 🧪 Testing

**Backend:**
```bash
./mvnw test
```

**Frontend:**
```bash
cd frontend
npm run test
```

## 🐛 Troubleshooting

**Port 8080 in use:**
```bash
lsof -i :8080
kill -9 <PID>
```

**Database connection failed:**
- Check PostgreSQL is running
- Verify credentials in `.env`
- Ensure `DB_URL` uses service name `postgres` in docker-compose

**CORS errors:**
- Check `SecurityConfig.java` allowed origins
- Verify frontend proxy in `vite.config.js`

**Docker issues:**
```bash
docker-compose down -v
docker system prune -a
./deploy.sh
```

## 🔐 Security Best Practices

- ✅ Use environment variables for secrets
- ✅ Generate strong JWT secret (512-bit minimum)
- ✅ Change default passwords immediately
- ✅ Use Gmail app passwords, not regular passwords
- ✅ Enable HTTPS in production
- ✅ Keep dependencies updated
- ❌ Never commit `.env` file
- ❌ Never hardcode credentials
- ❌ Never use default passwords in production

## 📊 Performance

- Handles 1000+ concurrent users
- Optimized database queries with indexing
- Efficient WebSocket message broadcasting
- Code splitting and lazy loading in frontend

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/name`)
3. Commit changes (`git commit -m 'Add feature'`)
4. Push to branch (`git push origin feature/name`)
5. Open Pull Request

## 📄 License

MIT License - see [LICENSE](LICENSE) file

## 📞 Support

- **Email**: support@davomat.uz
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)

---

**Built with ❤️ using Spring Boot and React**
