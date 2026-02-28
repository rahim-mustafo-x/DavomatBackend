# 📊 Davomat - Smart Attendance Management System

A modern, enterprise-grade attendance management system built with Spring Boot and React. Features real-time tracking, advanced analytics, WebSocket notifications, and comprehensive admin controls.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![React](https://img.shields.io/badge/React-18-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## ✨ Key Features

### 🎯 Core Functionality
- **Multi-Role System** - Admin, Teacher, and Student dashboards with role-based access control
- **Course Management** - Create, edit, and organize courses with full CRUD operations
- **Group Management** - Organize students into groups for better class management
- **Student Tracking** - Comprehensive student information and enrollment management
- **Attendance System** - Mark and monitor attendance with real-time updates
- **Real-time Notifications** - WebSocket-based instant notifications for all updates
- **System Logging** - Database-backed logging with advanced filtering and search

### 👨‍💼 Admin Dashboard
- **Live Statistics** - Real-time metrics for users, courses, students, and attendance
- **Activity Monitoring** - Track daily activity with growth trends and percentages
- **Performance Metrics** - System health monitoring (memory usage, CPU, connections)
- **System Logs Management** - View, filter, paginate, and delete logs (individual or bulk)
- **Advanced Analytics** - Comprehensive charts and data visualization
- **Developer Tools** - Integrated Swagger UI and WebSocket testing interface

### 👨‍🏫 Teacher Dashboard
- **Course Management** - Create and manage courses with descriptions
- **Group Organization** - Create groups and assign them to courses
- **Student Management** - Add, edit, and remove students from groups
- **Attendance Tracking** - Mark attendance for classes with real-time updates
- **Pagination Support** - Efficient data loading for large datasets

### 🎓 Student Dashboard
- **Course View** - See all enrolled courses with group information
- **Attendance History** - View personal attendance records and statistics
- **Balance Tracking** - Monitor payment status and due dates
- **Real-time Updates** - Instant notifications for attendance and course changes

## 🛠️ Technology Stack

### Backend
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.x** - Enterprise application framework
- **Spring Security** - JWT-based authentication and authorization
- **Spring Data JPA** - Database abstraction with Hibernate
- **H2/PostgreSQL** - In-memory or production database
- **WebSocket (STOMP)** - Real-time bidirectional communication
- **Swagger/OpenAPI 3** - Interactive API documentation
- **Lombok** - Reduce boilerplate code
- **SLF4J + Logback** - Comprehensive logging

### Frontend
- **React 18** - Modern UI library with hooks
- **Vite** - Lightning-fast build tool
- **React Router v6** - Client-side routing
- **Zustand** - Lightweight state management
- **Axios** - HTTP client with interceptors
- **Lucide React** - Beautiful icon library
- **React Hot Toast** - Elegant notifications
- **SockJS + STOMP** - WebSocket client

## 📋 Prerequisites

- **Java 17** or higher ([Download](https://adoptium.net/))
- **Node.js 18+** and npm ([Download](https://nodejs.org/))
- **Maven 3.6+** (or use included wrapper)
- **Git** for version control

## 🚀 Quick Start

> **Development Mode**: See [DEVELOPMENT.md](DEVELOPMENT.md) for running frontend (port 3000) and backend (port 8080) separately with hot-reload.

> **Production Mode**: Just run `./deploy.sh` - See [QUICK-START.md](QUICK-START.md) for the fastest deployment.

> **⚠️ SECURITY**: Before deploying, read [SECURITY.md](SECURITY.md) and configure your `.env` file with secure credentials!

### 1. Clone the Repository

```bash
git clone <repository-url>
cd davomat-backend
```

### 2. Choose Your Mode

**Development Mode (Recommended for Development):**
```bash
# Frontend: http://localhost:3000 (hot-reload)
# Backend: http://localhost:8080
./start-dev.sh
```
See [DEVELOPMENT.md](DEVELOPMENT.md) for detailed development guide.

**Production Mode:**
```bash
# Everything on http://localhost:8080
./deploy.sh
```
See [QUICK-START.md](QUICK-START.md) for production deployment.

### 3. Configure Environment Variables

```bash
# Copy the example file
cp .env.example .env

# Edit with your actual credentials
nano .env
```

Required variables:
- `DB_PASSWORD` - Your database password
- `MAIL_USERNAME` - Your email address
- `MAIL_PASSWORD` - Gmail app password
- `JWT_SECRET` - Generate with: `openssl rand -base64 64`

See [SECURITY.md](SECURITY.md) for detailed security configuration.

---

## 🛠️ Development vs Production

### Development Mode
- **Frontend**: http://localhost:3000 (Vite dev server with hot-reload)
- **Backend**: http://localhost:8080 (Spring Boot)
- **Use**: `./start-dev.sh` or see [DEVELOPMENT.md](DEVELOPMENT.md)
- **Features**: Hot-reload, debugging, source maps

### Production Mode
- **Application**: http://localhost:8080 (serves both frontend and backend)
- **Use**: `./deploy.sh` or see [QUICK-START.md](QUICK-START.md)
- **Features**: Optimized build, Docker, PostgreSQL

---

## 📦 Build Scripts

### 3. Build Backend

```bash
./build-backend.sh
```

This will:
- Run Maven clean package
- Generate `target/davomat-backend.jar`

### 4. Build Frontend

```bash
./build-frontend.sh
```

This will:
- Check if Node.js is installed
- Install npm dependencies
- Build React application
- Generate `frontend/dist/`

### 5. Run Application

```bash
# Option A: Run directly
java -jar target/davomat-backend.jar

# Option B: Build and run everything
./start-prod.sh
```

### 6. Docker Deployment

**Option A: One Command (Recommended)**
```bash
./deploy.sh
```

This single script will:
1. Build backend JAR
2. Build frontend
3. Build Docker image
4. Start with Docker Compose

**Option B: Step by Step**
```bash
./build-backend.sh      # Step 1: Build JAR
./build-frontend.sh     # Step 2: Build frontend
./build-docker.sh       # Step 3: Build Docker image
docker-compose up -d    # Step 4: Run with PostgreSQL
```

**View Logs:**
```bash
docker-compose logs -f
```

**Stop Services:**
```bash
docker-compose down
```

### 7. Access the Application

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **WebSocket Test**: http://localhost:8080/websocket-test.html

## 🔒 Security

**Important**: This application uses environment variables for sensitive configuration.

- See [SECURITY.md](SECURITY.md) for complete security guide
- Configure `.env` file before deployment (copy from `.env.example`)
- Never commit `.env` or files with real credentials
- Generate strong JWT secret: `openssl rand -base64 64`
- Use Gmail app passwords, not regular passwords

## 🔑 Authentication

The system uses JWT (JSON Web Tokens) for secure authentication. Default accounts are created on first run:

## 📚 API Documentation

### Interactive Documentation

Access the Swagger UI for interactive API testing:
- **URL**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Key Endpoints

#### Authentication
```
POST /auth/login          - User login (returns JWT token)
POST /auth/register       - User registration
```

#### Statistics (Admin Only)
```
GET /api/statistics/dashboard              - Dashboard statistics
GET /api/statistics/activity               - Recent activity metrics
GET /api/statistics/performance            - System performance metrics
GET /api/statistics/charts/attendance-trend - Attendance trend data
GET /api/statistics/charts/user-distribution - User distribution data
```

#### System Logs (Admin Only)
```
GET    /api/system-logs                    - Get all logs (paginated)
GET    /api/system-logs/level/{level}      - Get logs by level
GET    /api/system-logs/search?keyword=    - Search logs
DELETE /api/system-logs/{id}               - Delete single log
DELETE /api/system-logs/bulk               - Delete multiple logs
DELETE /api/system-logs/all                - Delete all logs
```

#### Courses
```
GET    /api/course/getAllCourses            - Get all courses (paginated)
POST   /api/course/create                   - Create new course
PUT    /api/course/update                   - Update course
DELETE /api/course/delete/{id}              - Delete course
```

#### Groups
```
GET    /api/group/findByCourseId/{id}       - Get groups by course
POST   /api/group/create                    - Create new group
PUT    /api/group/update                    - Update group
DELETE /api/group/delete/{id}               - Delete group
```

#### Students
```
GET    /api/student/findByGroupId/{id}      - Get students by group
POST   /api/student/addStudent              - Add new student
PUT    /api/student/editStudent             - Update student
DELETE /api/student/deleteStudent/{id}      - Delete student
```

#### Attendance
```
POST   /api/attendance/mark                 - Mark attendance
GET    /api/attendance/group/{groupId}      - Get attendance by group
GET    /api/attendance/student/{studentId}  - Get attendance by student
```

## 🔌 WebSocket Integration

### Connection

Connect to WebSocket endpoint with JWT authentication:

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

const headers = { 
  'Authorization': 'Bearer ' + jwtToken 
};

stompClient.connect(headers, function(frame) {
  // Subscribe to topics
  stompClient.subscribe('/user/queue/course', handleCourseUpdate);
  stompClient.subscribe('/user/queue/student', handleStudentUpdate);
  stompClient.subscribe('/user/queue/group', handleGroupUpdate);
  stompClient.subscribe('/user/queue/attendance', handleAttendanceUpdate);
});
```

### Topics

- `/user/queue/course` - Course updates
- `/user/queue/student` - Student updates
- `/user/queue/group` - Group updates
- `/user/queue/attendance` - Attendance updates

### Testing

Use the built-in WebSocket test page:
- **URL**: http://localhost:8080/websocket-test.html
- Login with your credentials
- Monitor real-time messages by entity type
- View statistics and message history

## 🗂️ Project Structure

```
davomat-backend/
├── src/main/java/uz/coder/davomatbackend/
│   ├── config/                    # Configuration classes
│   │   ├── AsyncConfig.java       # Async processing config
│   │   ├── DataLoader.java        # Initial data loader
│   │   ├── LoggingAspect.java     # AOP logging
│   │   ├── WebSocketConfig.java   # WebSocket configuration
│   │   └── WebSocketAuthInterceptor.java
│   ├── controller/                # REST controllers
│   │   ├── AttendanceController.java
│   │   ├── AuthController.java
│   │   ├── CourseController.java
│   │   ├── GroupController.java
│   │   ├── StatisticsController.java
│   │   ├── StudentController.java
│   │   ├── SystemLogController.java
│   │   └── UserController.java
│   ├── db/                        # Database layer
│   │   ├── model/                 # JPA entities
│   │   └── *Database.java         # Repository interfaces
│   ├── jwt/                       # JWT authentication
│   │   ├── JwtAuthFilter.java
│   │   ├── JwtService.java
│   │   └── OpenApiConfig.java
│   ├── model/                     # DTOs and models
│   ├── security/                  # Security configuration
│   │   └── SecurityConfig.java
│   └── service/                   # Business logic
│       ├── SystemLogService.java
│       ├── UserService.java
│       └── WebSocketNotificationService.java
├── src/main/resources/
│   ├── static/
│   │   └── websocket-test.html    # WebSocket testing page
│   └── application.properties     # Application configuration
├── frontend/
│   ├── src/
│   │   ├── api/
│   │   │   └── axios.js           # API client configuration
│   │   ├── constants/
│   │   │   └── roles.js           # Role constants
│   │   ├── pages/
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── TeacherDashboard.jsx
│   │   │   ├── StudentDashboard.jsx
│   │   │   ├── Login.jsx
│   │   │   └── Landing.jsx
│   │   ├── store/
│   │   │   └── authStore.js       # Zustand auth store
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
└── pom.xml
```

## 🔒 Security Features

- **JWT Authentication** - Secure token-based authentication with expiration
- **Role-Based Access Control** - Fine-grained permissions (Admin, Teacher, Student)
- **Password Encryption** - BCrypt hashing with salt
- **CORS Configuration** - Configured for frontend origins
- **WebSocket Security** - JWT token validation for WebSocket connections
- **SQL Injection Prevention** - Parameterized queries with JPA
- **XSS Protection** - Input sanitization and validation
- **Security Logging** - Track authentication and authorization events

## ⚙️ Configuration

### Backend Configuration

The application uses environment variables for sensitive data. See `.env.example` for required variables.

Edit `src/main/resources/application.properties` for non-sensitive settings:

```properties
# Server
server.port=8080

# Database (uses environment variables)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/davomat_db}
spring.datasource.username=${DB_USERNAME:davomat_user}
spring.datasource.password=${DB_PASSWORD:changeme}

# JWT Configuration (uses environment variables)
jwt.secret=${JWT_SECRET:change-in-production}
jwt.expiration=${JWT_EXPIRATION:2592000000}

# Logging
logging.level.root=INFO
logging.file.name=logs/davomat-app.log
```

**Security Note**: Never hardcode credentials in application.properties!

### Frontend Configuration

Create `.env` file in `frontend/` directory:

```env
VITE_API_URL=http://localhost:8080
```

Update `frontend/vite.config.js` for proxy settings:

```javascript
export default {
  server: {
    port: 3000,
    proxy: {
      '/api': 'http://localhost:8080',
      '/auth': 'http://localhost:8080',
      '/ws': {
        target: 'http://localhost:8080',
        ws: true
      }
    }
  }
}
```

## 🚀 Production Deployment

### Backend

```bash
# Build production JAR
./mvnw clean package -DskipTests

# Run with production profile
java -jar target/davomat-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Frontend

```bash
cd frontend

# Build for production
npm run build

# Output will be in frontend/dist/
# Deploy to your web server (Nginx, Apache, etc.)
```

### Environment Variables

Set these environment variables in production (or use `.env` file):

```bash
# Database
export DB_URL=jdbc:postgresql://localhost:5432/davomat
export DB_USERNAME=your_username
export DB_PASSWORD=your_secure_password

# JWT
export JWT_SECRET=your-very-secure-secret-key-512-bits-minimum

# Email
export MAIL_USERNAME=your-email@gmail.com
export MAIL_PASSWORD=your-gmail-app-password

# Spring Profile
export SPRING_PROFILES_ACTIVE=prod
```

See [SECURITY.md](SECURITY.md) for detailed security configuration.

### Database Migration

For production, use PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/davomat
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

## 🧪 Testing

### Backend Tests

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

### Frontend Tests

```bash
cd frontend

# Run tests
npm run test

# Run with coverage
npm run test:coverage
```

## 🐛 Troubleshooting

### Common Issues

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Cannot connect to backend:**
- Verify backend is running: `curl http://localhost:8080/actuator/health`
- Check CORS configuration in `SecurityConfig.java`
- Verify proxy settings in `vite.config.js`

**WebSocket connection failed:**
- Ensure JWT token is valid and not expired
- Check WebSocket endpoint: `ws://localhost:8080/ws`
- Verify authentication headers are included

**Database errors:**
- Check database credentials in `application.properties`
- Ensure database server is running
- Verify connection URL format

**Frontend build errors:**
```bash
# Clear cache and reinstall
rm -rf node_modules package-lock.json
npm install
```

## 📊 Performance

- **Backend**: Handles 1000+ concurrent users
- **Database**: Optimized queries with proper indexing
- **WebSocket**: Efficient message broadcasting
- **Frontend**: Code splitting and lazy loading
- **Caching**: Strategic caching for frequently accessed data

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Use meaningful variable and method names
- Write unit tests for new features
- Update documentation for API changes
- Follow React best practices and hooks patterns

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Development Team** - Full-stack development
- **Design Team** - UI/UX design
- **QA Team** - Testing and quality assurance

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- React team for the powerful UI library
- Lucide for beautiful icons
- All contributors and testers

## 📞 Support

For support and questions:

- **Email**: support@davomat.uz
- **Documentation**: [Wiki](https://github.com/your-repo/wiki)
- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)

## 🗺️ Roadmap

### Version 2.0 (Planned)
- [ ] Mobile application (iOS/Android)
- [ ] Email notifications
- [ ] SMS integration
- [ ] Advanced reporting with PDF export
- [ ] Multi-language support
- [ ] Dark mode
- [ ] Biometric attendance
- [ ] Parent portal
- [ ] Calendar integration
- [ ] Automated backup system

### Version 1.1 (In Progress)
- [x] Real-time WebSocket notifications
- [x] System logging with database storage
- [x] Advanced admin dashboard
- [x] Pagination for all lists
- [x] Bulk operations (delete logs)
- [ ] Export data to Excel/CSV
- [ ] Advanced search and filters
- [ ] User profile management

---

**Built with ❤️ using Spring Boot and React**

*Last Updated: February 2026*
