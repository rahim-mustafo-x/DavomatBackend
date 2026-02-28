# 🛠️ Development Guide

## Development Environment Setup

This guide explains how to run the application in development mode with hot-reload for both frontend and backend.

## 📋 Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.6+
- PostgreSQL (or use H2 for development)

## 🚀 Quick Start (Development Mode)

### Option 1: Automated Script

```bash
# Start both backend and frontend
./start-dev.sh
```

This will:
- Start backend on http://localhost:8080
- Start frontend on http://localhost:3000 (with hot-reload)
- Backend logs: `logs/backend-dev.log`

### Option 2: Manual Start

**Terminal 1 - Backend:**
```bash
# Build backend (first time only)
./mvnw clean package -DskipTests

# Run backend
java -jar target/davomat-backend.jar

# Or use Maven
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend

# Install dependencies (first time only)
npm install

# Start dev server with hot-reload
npm run dev
```

## 🌐 Access Points

### Development
- **Frontend**: http://localhost:3000 (React dev server with hot-reload)
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **WebSocket Test**: http://localhost:8080/websocket-test.html
- **Health Check**: http://localhost:8080/actuator/health

### Production
- **Application**: http://localhost:8080 (serves both frontend and backend)

## 🔧 Configuration

### Backend (Development)

Edit `src/main/resources/application.properties`:

```properties
# Use H2 in-memory database for development
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop

# Or use PostgreSQL
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/davomat_db}
spring.datasource.username=${DB_USERNAME:davomat_user}
spring.datasource.password=${DB_PASSWORD:changeme}
```

### Frontend (Development)

Create `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Davomat App
```

The frontend dev server (port 3000) proxies API requests to backend (port 8080).

## 🔄 Hot Reload

### Frontend
- Vite provides instant hot-reload
- Changes to React components update immediately
- No manual refresh needed

### Backend
- Use Spring Boot DevTools for automatic restart
- Or manually restart after changes

## 📦 Project Structure

```
davomat-backend/
├── src/main/java/              # Backend Java code
├── src/main/resources/         # Backend resources
│   ├── application.properties  # Backend config
│   └── static/                 # Production frontend build
├── frontend/                   # Frontend React app
│   ├── src/                    # React source code
│   ├── public/                 # Static assets
│   ├── package.json            # Frontend dependencies
│   └── vite.config.js          # Vite configuration
└── pom.xml                     # Backend dependencies
```

## 🧪 Testing

### Backend Tests
```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=UserServiceTest

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

# Run in watch mode
npm run test:watch
```

## 🐛 Debugging

### Backend (IntelliJ IDEA)
1. Open project in IntelliJ
2. Set breakpoints in Java code
3. Run in Debug mode (Shift+F9)

### Backend (VS Code)
1. Install Java Extension Pack
2. Set breakpoints
3. Press F5 to start debugging

### Frontend (Browser DevTools)
1. Open Chrome DevTools (F12)
2. Go to Sources tab
3. Set breakpoints in React code
4. Vite provides source maps for debugging

## 📝 Development Workflow

### Adding a New Feature

1. **Backend:**
   ```bash
   # Create entity, repository, service, controller
   # Example: src/main/java/uz/coder/davomatbackend/
   ```

2. **Frontend:**
   ```bash
   cd frontend/src
   # Create component, page, or service
   ```

3. **Test:**
   ```bash
   # Backend: ./mvnw test
   # Frontend: npm run test
   ```

4. **Commit:**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   ```

### Making API Changes

1. Update backend controller
2. Restart backend (or wait for DevTools)
3. Update Swagger annotations if needed
4. Update frontend API calls in `frontend/src/api/`
5. Test in browser

## 🔍 Common Issues

### Port Already in Use

**Backend (8080):**
```bash
# Find process
lsof -i :8080

# Kill process
kill -9 <PID>
```

**Frontend (3000):**
```bash
# Find process
lsof -i :3000

# Kill process
kill -9 <PID>
```

### CORS Errors

Check `SecurityConfig.java`:
```java
corsConfig.setAllowedOrigins(java.util.List.of(
    "http://localhost:3000",  // Frontend dev server
    "http://localhost:8080"   // Backend
));
```

### Database Connection Failed

1. Check PostgreSQL is running: `pg_isready`
2. Verify credentials in `application.properties`
3. Or use H2 for development (in-memory)

### Frontend Build Errors

```bash
cd frontend

# Clear cache
rm -rf node_modules package-lock.json

# Reinstall
npm install

# Rebuild
npm run build
```

## 🚀 Building for Production

### Full Build
```bash
# Build everything
./deploy.sh
```

### Backend Only
```bash
./build-backend.sh
# Output: target/davomat-backend.jar
```

### Frontend Only
```bash
./build-frontend.sh
# Output: frontend/dist/
```

## 📊 Performance Tips

### Backend
- Use pagination for large datasets
- Enable query caching
- Use async operations for heavy tasks
- Monitor with Actuator endpoints

### Frontend
- Use React.memo for expensive components
- Implement virtual scrolling for long lists
- Lazy load routes with React.lazy()
- Optimize images and assets

## 🔐 Security in Development

- Use `.env` files (never commit!)
- Default credentials are for development only
- Change passwords in production
- Review [SECURITY.md](SECURITY.md) before deploying

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests
5. Submit a pull request

---

**Happy Coding! 🎉**

*Last Updated: February 2026*
