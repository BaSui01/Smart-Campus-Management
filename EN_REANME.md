# Smart Campus Management System

A comprehensive, modern campus management system built with Spring Boot and Vue.js, providing full-featured management capabilities for students, teachers, parents, and administrators.

[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.x-green.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🌟 Key Features

- **Modern Architecture**: Built with Spring Boot 3.x + Vue 3 + Element Plus technology stack
- **Multi-Role Support**: Students, Teachers, Parents, and Administrators with distinct functionalities
- **Real-time Communication**: WebSocket-based real-time notification system
- **High Performance**: Redis caching + optimized database indexing
- **Security**: JWT authentication + RBAC (Role-Based Access Control)
- **Responsive Design**: Support for both PC and mobile access
- **Comprehensive Management**: Academic, financial, and administrative features

## 🏗️ Project Structure

```
Smart-Campus-Management/
├── campus-management-backend/     # Backend Service (Spring Boot)
│   ├── src/main/java/            # Java source code
│   ├── src/main/resources/       # Configuration and static resources
│   └── pom.xml                   # Maven configuration
├── campus-management-frontend/    # Frontend Application (Vue.js)
│   ├── src/                      # Vue.js source code
│   ├── package.json              # npm dependencies
│   └── vite.config.js            # Vite configuration
└── database/                     # Database scripts and migrations
    ├── *.sql                     # SQL scripts
    └── run_all_scripts.sql       # Database initialization script
```

## 🛠️ Technology Stack

### Backend Technologies
- **Spring Boot 3.x** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **MySQL 8.0** - Primary database
- **Redis** - Caching and session management
- **JWT** - Token-based authentication
- **Quartz** - Job scheduling
- **Swagger/OpenAPI** - API documentation
- **Maven** - Dependency management

### Frontend Technologies
- **Vue 3** - Progressive JavaScript framework
- **Vue Router** - Client-side routing
- **Pinia** - State management
- **Element Plus** - UI component library
- **Axios** - HTTP client
- **Vite** - Build tool and development server

## 🚀 Quick Start

### Prerequisites

- **JDK 17+** - Java Development Kit
- **Node.js 16+** - JavaScript runtime
- **MySQL 8.0+** - Database server
- **Redis 6+** - Cache server
- **Maven 3.6+** - Build tool (or use included wrapper)

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/Smart-Campus-Management.git
cd Smart-Campus-Management
```

### 2. Database Setup

```bash
# Navigate to database directory
cd database

# Create database and run initialization scripts
mysql -u root -p < run_all_scripts.sql
```

The database setup includes:
- Database creation
- Table structure creation
- Initial data insertion
- Index optimization
- System settings configuration

### 3. Backend Configuration

```bash
cd campus-management-backend

# Copy and modify configuration file
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

Edit `application.yml` with your database and Redis configurations:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management?useSSL=false&serverTimezone=UTC
    username: your_db_username
    password: your_db_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0

jwt:
  secret: your_jwt_secret_key_here
  expiration: 86400  # 24 hours in seconds
```

### 4. Start Backend Service

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using installed Maven
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

### 5. Frontend Setup

```bash
cd campus-management-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

The frontend will start on `http://localhost:5173`

### 6. Access the System

- **Frontend Application**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Admin Panel**: http://localhost:8080/admin/login

### Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Teacher | teacher1 | password |
| Student | student1 | password |
| Parent | parent1 | password |

## 👥 User Roles & Features

### 🎓 Student Features
- **Course Management**: Course selection, withdrawal, and schedule viewing
- **Academic Tracking**: Grade inquiry, assignment submission, exam schedules
- **Personal Management**: Profile management, password changes
- **Communication**: Message system, announcements

### 👨‍🏫 Teacher Features
- **Course Administration**: Course management, student enrollment
- **Academic Management**: Grade entry, assignment publishing, exam scheduling
- **Student Tracking**: Attendance tracking, performance analysis
- **Communication**: Student interaction, parent communication

### 👨‍👩‍👧‍👦 Parent Features
- **Child Monitoring**: Child information viewing, academic progress tracking
- **Financial Management**: Fee payment, payment history
- **Communication**: Teacher communication, school announcements
- **Activity Participation**: School event participation

### 👨‍💼 Administrator Features
- **User Management**: User creation, role assignment, permission control
- **System Configuration**: System settings, feature toggles
- **Data Analytics**: Usage statistics, performance monitoring
- **Content Management**: Course management, system announcements

## 📊 Core Modules

### Academic Management
- **Course Management**: CRUD operations, scheduling, enrollment management
- **Grade Management**: Grade entry, statistical analysis, transcript generation
- **Exam Management**: Exam scheduling, result publication, makeup exam management

### User Management
- **Multi-Role Support**: Students, teachers, parents, administrators
- **Permission Control**: Fine-grained RBAC permission management
- **Profile Management**: Information updates, password management

### Financial Management
- **Fee Management**: Tuition, miscellaneous fees, and other fee categories
- **Payment Records**: Payment history, overdue reminders
- **Financial Reports**: Revenue statistics, overdue analysis

### Notification System
- **Real-time Notifications**: WebSocket-based real-time push notifications
- **Email Notifications**: Important information email reminders
- **System Announcements**: School-wide notification publishing

### Data Analytics
- **Learning Analytics**: Grade trends, learning effectiveness analysis
- **Usage Statistics**: System usage analytics
- **Performance Monitoring**: Real-time system performance monitoring

## 🗄️ Database Design

### Core Tables
- `users` - User basic information
- `students` - Student information
- `courses` - Course information
- `course_selections` - Course enrollment records
- `grades` - Grade records
- `schedules` - Course scheduling
- `payments` - Payment records
- `notifications` - Notification messages

### System Tables
- `roles` - Role definitions
- `permissions` - Permission definitions
- `system_settings` - System configuration
- `activity_logs` - Operation logs

## 📝 API Documentation

### Authentication Endpoints
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/logout` - User logout
- `GET /api/v1/auth/profile` - Get user information
- `PUT /api/v1/auth/profile` - Update user profile

### Student Management
- `GET /api/v1/students` - Get student list
- `POST /api/v1/students` - Create student
- `PUT /api/v1/students/{id}` - Update student information
- `DELETE /api/v1/students/{id}` - Delete student

### Course Management
- `GET /api/v1/courses` - Get course list
- `POST /api/v1/courses` - Create course
- `GET /api/v1/courses/{id}/students` - Get course students
- `POST /api/v1/courses/{id}/enroll` - Enroll in course

### Grade Management
- `GET /api/v1/grades` - Get grade records
- `POST /api/v1/grades` - Add grade entry
- `PUT /api/v1/grades/{id}` - Update grade
- `GET /api/v1/grades/statistics` - Get grade statistics

For complete API documentation, visit: http://localhost:8080/swagger-ui.html

## 🔧 Configuration

### Backend Configuration (application.yml)

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: campus-management-backend
  
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    timeout: 2000ms
    
  cache:
    type: redis
    redis:
      time-to-live: 600000

jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: ${JWT_EXPIRATION:86400}

logging:
  level:
    com.campus: DEBUG
    org.springframework.security: DEBUG
```

### Frontend Configuration (vite.config.js)

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'],
          elementPlus: ['element-plus']
        }
      }
    }
  }
})
```

## 🧪 Testing

### Backend Tests
```bash
cd campus-management-backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=StudentServiceTest

# Run tests with coverage
./mvnw test jacoco:report
```

### Frontend Tests
```bash
cd campus-management-frontend

# Run unit tests
npm run test

# Run tests with coverage
npm run test:coverage

# Run e2e tests
npm run test:e2e
```

## 📦 Deployment

### Docker Deployment (Recommended)

1. **Create docker-compose.yml**:
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: campus_management
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./database:/docker-entrypoint-initdb.d

  redis:
    image: redis:6-alpine
    ports:
      - "6379:6379"

  backend:
    build: ./campus-management-backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      - DB_HOST=mysql
      - REDIS_HOST=redis

  frontend:
    build: ./campus-management-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

2. **Build and start services**:
```bash
docker-compose up -d
```

### Traditional Deployment

#### Backend Deployment
```bash
cd campus-management-backend

# Build application
./mvnw clean package -DskipTests

# Run with production profile
java -jar target/campus-management-backend-1.0.0.jar --spring.profiles.active=prod
```

#### Frontend Deployment
```bash
cd campus-management-frontend

# Build for production
npm run build

# Deploy to web server (e.g., Nginx)
cp -r dist/* /var/www/html/
```

### Nginx Configuration
```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # Frontend
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }
    
    # Backend API
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🔍 Monitoring & Observability

### Health Checks
- **Application Health**: http://localhost:8080/actuator/health
- **Database Health**: http://localhost:8080/actuator/health/db
- **Redis Health**: http://localhost:8080/actuator/health/redis

### Metrics
- **Application Metrics**: http://localhost:8080/actuator/metrics
- **JVM Metrics**: http://localhost:8080/actuator/metrics/jvm.memory.used
- **Custom Metrics**: http://localhost:8080/actuator/metrics/custom.*

### Logging
- **Application Logs**: `logs/application.log`
- **Error Logs**: `logs/error.log`
- **Access Logs**: `logs/access.log`

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add some amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) for Java code
- Follow [Vue.js Style Guide](https://vuejs.org/style-guide/) for Vue.js code
- Write comprehensive tests for new features
- Update documentation for any API changes
- Ensure all tests pass before submitting PR

### Code Review Process

1. All submissions require review from maintainers
2. Automated tests must pass
3. Code coverage should not decrease
4. Documentation must be updated for user-facing changes

## 🐛 Issue Reporting

When reporting issues, please include:

- **Environment details** (OS, Java version, Node.js version)
- **Steps to reproduce** the issue
- **Expected behavior** vs actual behavior
- **Screenshots** if applicable
- **Log files** for backend issues

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support & Contact

- **Project Repository**: [GitHub](https://github.com/your-username/Smart-Campus-Management)
- **Issue Tracker**: [Issues](https://github.com/your-username/Smart-Campus-Management/issues)
- **Documentation**: [Wiki](https://github.com/your-username/Smart-Campus-Management/wiki)
- **Email**: support@campus-management.com

## 🎯 Roadmap

### Version 1.0 (Current)
- ✅ Core functionality complete
- ✅ User role management
- ✅ Course management system
- ✅ Grade management system
- ✅ Basic reporting features

### Version 1.1 (Planned)
- 🔄 Mobile app development
- 🔄 Enhanced notification system
- 🔄 Advanced reporting and analytics
- 🔄 Performance optimizations
- 🔄 Multi-language support

### Version 2.0 (Future)
- 📅 AI-powered recommendations
- 📅 Advanced analytics and insights
- 📅 Microservices architecture
- 📅 Multi-tenant support
- 📅 Integration with external systems

## 🏆 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) - The foundational framework
- [Vue.js](https://vuejs.org/) - The progressive JavaScript framework
- [Element Plus](https://element-plus.org/) - Vue 3 UI library
- [MySQL](https://www.mysql.com/) - Database management system
- [Redis](https://redis.io/) - In-memory data structure store

## 📊 Project Statistics

- **Total Lines of Code**: 50,000+
- **Test Coverage**: 85%+
- **Documentation Coverage**: 90%+
- **Active Contributors**: 5+

---

⭐ **If this project helps you, please give us a star!**

For more information, please visit our [documentation](https://github.com/your-username/Smart-Campus-Management/wiki) or [contact us](mailto:support@campus-management.com).
