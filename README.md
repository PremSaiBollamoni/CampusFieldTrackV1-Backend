# 🚀 Campus FieldTrack - Spring Boot Backend

<div align="center">

<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=32&duration=2800&pause=2000&color=6DB33F&center=true&vCenter=true&width=940&lines=RESTful+API+Backend+%E2%9A%A1;JWT+Authentication+%F0%9F%94%90;Batch+Processing+%F0%9F%9A%80;MySQL+Database+%F0%9F%97%84%EF%B8%8F" alt="Typing SVG" />

<br/>
<br/>

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

<br/>

**⚡ RESTful API backend for GPS tracking with JWT authentication and batch processing**

<br/>

[![GitHub Stars](https://img.shields.io/github/stars/PremSaiBollamoni/CampusFieldTrackV1-Backend?style=social)](https://github.com/PremSaiBollamoni/CampusFieldTrackV1-Backend/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/PremSaiBollamoni/CampusFieldTrackV1-Backend?style=social)](https://github.com/PremSaiBollamoni/CampusFieldTrackV1-Backend/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/PremSaiBollamoni/CampusFieldTrackV1-Backend)](https://github.com/PremSaiBollamoni/CampusFieldTrackV1-Backend/issues)

<br/>

[Features](#-features) • [API Documentation](#-api-documentation) • [Installation](#-installation) • [Architecture](#-architecture) • [Database](#-database)

</div>

---

<div align="center">

## 🎯 **What Makes This Special?**

</div>

<table>
<tr>
<td width="33%" align="center">
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Locked.png" width="80" />
<h3>🔐 Security</h3>
<p>JWT authentication with BCrypt password hashing and token expiration</p>
</td>
<td width="33%" align="center">
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Chart%20Increasing.png" width="80" />
<h3>⚡ Performance</h3>
<p>Batch insert optimization for 1000+ route points per session</p>
</td>
<td width="33%" align="center">
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Card%20File%20Box.png" width="80" />
<h3>🗄️ Database</h3>
<p>Indexed queries with HikariCP connection pooling</p>
</td>
</tr>
</table>

---

## ✨ Features

<details open>
<summary><b>🔐 Authentication & Security</b></summary>
<br/>

- ✅ **JWT-based authentication** with BCrypt password hashing
- ✅ **Token expiration** (24 hours default)
- ✅ **Secure endpoints** with Spring Security
- ✅ **User registration** with email validation
- ✅ **Password encryption** using BCrypt (strength 12)

</details>

<details open>
<summary><b>📊 Session Management</b></summary>
<br/>

- ✅ **Full session sync** with batch insert optimization
- ✅ **1000+ route points** per session support
- ✅ **Checkpoint tracking** with arrival/departure times
- ✅ **User-specific sessions** with foreign key relationships
- ✅ **Automatic timestamp** management

</details>

<details open>
<summary><b>🗄️ Database Optimization</b></summary>
<br/>

- ✅ **Indexed queries** on session_id, user_id, timestamp
- ✅ **Batch inserts** for route points and checkpoints
- ✅ **Connection pooling** with HikariCP
- ✅ **Auto-create database** on first run
- ✅ **Schema migration** with Hibernate DDL

</details>

<details open>
<summary><b>🔄 API Design</b></summary>
<br/>

- ✅ **RESTful endpoints** following best practices
- ✅ **Consistent response format** with ApiResponse wrapper
- ✅ **Error handling** with GlobalExceptionHandler
- ✅ **CORS enabled** for cross-origin requests
- ✅ **JSON serialization** with Jackson

</details>

---

## 📋 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register User
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "user@example.com"
  },
  "timestamp": 1777530099442
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "email": "user@example.com"
  },
  "timestamp": 1777530099442
}
```

---

### Session Endpoints

> **Note:** All session endpoints require JWT token in Authorization header:
> ```
> Authorization: Bearer <your-jwt-token>
> ```

#### Get All Sessions
```http
GET /sessions
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Sessions retrieved",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "startTime": "2026-04-30T12:30:45Z",
      "endTime": "2026-04-30T13:45:20Z",
      "durationSeconds": 4475,
      "distanceKm": 5.23,
      "avgSpeedKmh": 4.2,
      "routePoints": [...],
      "checkpoints": [...]
    }
  ],
  "timestamp": 1777530099442
}
```

#### Get Session by ID
```http
GET /sessions/{id}
Authorization: Bearer <token>
```

#### Sync Full Session
```http
POST /sessions/full-sync
Authorization: Bearer <token>
Content-Type: application/json

{
  "start_time": "2026-04-30T12:30:45Z",
  "end_time": "2026-04-30T13:45:20Z",
  "duration_seconds": 4475,
  "distance_km": 5.23,
  "avg_speed_kmh": 4.2,
  "route_points": [
    {
      "lat": 12.9716,
      "lng": 77.5946,
      "altitude": 920.5,
      "speed": 1.25,
      "accuracy": 8.2,
      "timestamp": "2026-04-30T12:30:45Z"
    }
  ],
  "checkpoints": [
    {
      "lat": 12.9720,
      "lng": 77.5950,
      "arrived_at": "2026-04-30T12:45:00Z",
      "departed_at": "2026-04-30T12:50:00Z"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "Session synced successfully",
  "data": {
    "sessionId": 1,
    "routePointsCount": 1247,
    "checkpointsCount": 3
  },
  "timestamp": 1777530099442
}
```

---

## 🚀 Installation

<div align="center">

### 📋 Prerequisites

</div>

```bash
✅ Java: 17 or higher
✅ Maven: 3.9 or higher
✅ MySQL: 8.0 or higher
✅ Git: For cloning the repository
```

<div align="center">

### 🛠️ Setup Steps

</div>

<details>
<summary><b>1️⃣ Clone the repository</b></summary>

```bash
git clone https://github.com/PremSaiBollamoni/CampusFieldTrackV1-Backend.git
cd CampusFieldTrackV1-Backend
```

</details>

<details>
<summary><b>2️⃣ Configure environment variables</b></summary>

Create `.env` file in the root directory:

```env
DB_URL=jdbc:mysql://localhost:3306/CampusTrack?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
DB_USER=root
DB_PASS=YourPassword
JWT_SECRET=your-256-bit-secret-key-here-make-it-long-and-random
```

</details>

<details>
<summary><b>3️⃣ Install dependencies</b></summary>

```bash
mvn clean install -DskipTests
```

</details>

<details>
<summary><b>4️⃣ Run the application</b></summary>

**Option 1: Using Maven**
```bash
mvn spring-boot:run
```

**Option 2: Using shell script (loads .env)**
```bash
# Linux/Mac
chmod +x run.sh
./run.sh

# Windows
run.bat
```

**Option 3: Using JAR**
```bash
mvn clean package -DskipTests
java -jar target/backend-1.0.0.jar
```

</details>

<details>
<summary><b>5️⃣ Verify installation</b></summary>

```bash
curl http://localhost:8080/api/auth/login
```

✅ If you see a response, the server is running!

</details>

---

## 🏗️ Architecture

### Project Structure

```
src/main/java/com/campusfieldtrack/
├── CampusFieldTrackApplication.java   # Main application entry
├── config/
│   ├── SecurityConfig.java            # Spring Security configuration
│   └── GlobalExceptionHandler.java    # Centralized error handling
├── controller/
│   ├── AuthController.java            # Authentication endpoints
│   └── SessionController.java         # Session CRUD endpoints
├── dto/
│   ├── ApiResponse.java               # Standard response wrapper
│   ├── AuthRequest.java               # Login/register request
│   ├── AuthResponse.java              # JWT token response
│   ├── CheckpointDto.java             # Checkpoint data transfer
│   └── RoutePointDto.java             # Route point data transfer
├── entity/
│   ├── User.java                      # User entity (JPA)
│   ├── TrackingSession.java           # Session entity
│   ├── RoutePoint.java                # GPS point entity
│   └── Checkpoint.java                # Stop point entity
├── repository/
│   ├── UserRepository.java            # User data access
│   ├── SessionRepository.java         # Session data access
│   ├── RoutePointRepository.java      # Route point data access
│   └── CheckpointRepository.java      # Checkpoint data access
├── security/
│   ├── JwtUtil.java                   # JWT token generation/validation
│   └── JwtAuthenticationFilter.java   # JWT filter for requests
└── service/
    ├── AuthService.java               # Authentication business logic
    └── SessionSyncService.java        # Session sync business logic
```

### Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.2.0 | Application framework |
| **Spring Security** | 6.x | Authentication & authorization |
| **Spring Data JPA** | 3.x | Database ORM |
| **Hibernate** | 6.x | JPA implementation |
| **MySQL Connector** | 8.0.33 | Database driver |
| **JJWT** | 0.11.5 | JWT token handling |
| **Lombok** | 1.18.30 | Boilerplate code reduction |
| **BCrypt** | - | Password hashing |
| **HikariCP** | - | Connection pooling |

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌─────────────┐
│    User     │
├─────────────┤
│ id (PK)     │
│ email       │
│ password    │
│ created_at  │
└──────┬──────┘
       │ 1
       │
       │ N
┌──────┴──────────────┐
│  TrackingSession    │
├─────────────────────┤
│ id (PK)             │
│ user_id (FK)        │
│ start_time          │
│ end_time            │
│ duration_seconds    │
│ distance_km         │
│ avg_speed_kmh       │
└──────┬──────────────┘
       │ 1
       ├─────────────────┐
       │ N               │ N
┌──────┴──────────┐ ┌───┴──────────┐
│   RoutePoint    │ │  Checkpoint  │
├─────────────────┤ ├──────────────┤
│ id (PK)         │ │ id (PK)      │
│ session_id (FK) │ │ session_id   │
│ lat             │ │ lat          │
│ lng             │ │ lng          │
│ altitude        │ │ arrived_at   │
│ speed           │ │ departed_at  │
│ accuracy        │ └──────────────┘
│ timestamp       │
└─────────────────┘
```

### Table Definitions

#### users
```sql
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_email (email)
);
```

#### tracking_sessions
```sql
CREATE TABLE tracking_sessions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP,
  duration_seconds INT,
  distance_km DOUBLE,
  avg_speed_kmh DOUBLE,
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user_id (user_id),
  INDEX idx_start_time (start_time)
);
```

#### route_points
```sql
CREATE TABLE route_points (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  lat DOUBLE NOT NULL,
  lng DOUBLE NOT NULL,
  altitude DOUBLE,
  speed DOUBLE,
  accuracy DOUBLE,
  timestamp TIMESTAMP NOT NULL,
  FOREIGN KEY (session_id) REFERENCES tracking_sessions(id),
  INDEX idx_session_id (session_id),
  INDEX idx_timestamp (timestamp)
);
```

#### checkpoints
```sql
CREATE TABLE checkpoints (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  session_id BIGINT NOT NULL,
  lat DOUBLE NOT NULL,
  lng DOUBLE NOT NULL,
  arrived_at TIMESTAMP NOT NULL,
  departed_at TIMESTAMP,
  FOREIGN KEY (session_id) REFERENCES tracking_sessions(id),
  INDEX idx_session_id (session_id)
);
```

---

## 🔧 Configuration

### application.properties

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/api

# Database Configuration
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

### Environment Variables

| Variable | Description | Example |
|----------|-------------|---------|
| `DB_URL` | MySQL connection URL | `jdbc:mysql://localhost:3306/CampusTrack` |
| `DB_USER` | Database username | `root` |
| `DB_PASS` | Database password | `YourPassword` |
| `JWT_SECRET` | Secret key for JWT signing | `your-256-bit-secret` |

---

## 🔒 Security

### JWT Token Structure

```json
{
  "sub": "user@example.com",
  "userId": 1,
  "iat": 1777530099,
  "exp": 1777616499
}
```

### Password Hashing

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hashedPassword = encoder.encode(rawPassword);
```

### Endpoint Security

| Endpoint | Access |
|----------|--------|
| `/auth/register` | Public |
| `/auth/login` | Public |
| `/sessions/**` | Authenticated (JWT required) |

---

## 🐛 Troubleshooting

### Common Issues

**1. Database connection failed**
```
Error: Communications link failure
```
**Solution:**
- Verify MySQL is running: `systemctl status mysql`
- Check credentials in `.env` file
- Ensure database exists or `createDatabaseIfNotExist=true` is set

**2. JWT token invalid**
```
Error: JWT signature does not match
```
**Solution:**
- Verify `JWT_SECRET` is consistent across restarts
- Check token expiration (default 24 hours)
- Ensure token is sent in `Authorization: Bearer <token>` header

**3. Port already in use**
```
Error: Port 8080 is already in use
```
**Solution:**
```bash
# Find process using port 8080
lsof -i :8080  # Linux/Mac
netstat -ano | findstr :8080  # Windows

# Kill the process or change port in application.properties
server.port=8081
```

**4. Lombok not working**
```
Error: Cannot resolve symbol 'log'
```
**Solution:**
- Enable annotation processing in IDE
- Verify Lombok plugin is installed
- Rebuild project: `mvn clean install`

---

## 📈 Performance Optimization

### Batch Insert Configuration

```java
@Transactional
public void saveRoutePoints(List<RoutePoint> points) {
    int batchSize = 100;
    for (int i = 0; i < points.size(); i++) {
        routePointRepository.save(points.get(i));
        if (i % batchSize == 0 && i > 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }
}
```

### Database Indexes

- `idx_user_id` on `tracking_sessions.user_id`
- `idx_session_id` on `route_points.session_id`
- `idx_timestamp` on `route_points.timestamp`
- `idx_email` on `users.email`

### Connection Pooling

- Maximum pool size: 10 connections
- Minimum idle: 5 connections
- Connection timeout: 30 seconds

---

## 🧪 Testing

### Run Tests

```bash
mvn test
```

### API Testing with cURL

**Register:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123"}'
```

**Get Sessions:**
```bash
curl -X GET http://localhost:8080/api/sessions \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🚢 Deployment

### Docker Deployment

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Build and run:**
```bash
docker build -t campusfieldtrack-backend .
docker run -p 8080:8080 --env-file .env campusfieldtrack-backend
```

### Docker Compose

```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASS}
      MYSQL_DATABASE: CampusTrack
    ports:
      - "3306:3306"
  
  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://mysql:3306/CampusTrack
      DB_USER: ${DB_USER}
      DB_PASS: ${DB_PASS}
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - mysql
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## � Related Repositories

- **Flutter Frontend**: [Campus FieldTrack Flutter](FRONTEND_REPO_URL)

---

## 🙏 Acknowledgments

- **Spring Boot** for the amazing framework
- **JWT.io** for JWT implementation guidance
- **Hibernate** for ORM capabilities
- **MySQL** for reliable database

---

<div align="center">

**Built with ☕ using Spring Boot**

[⬆ Back to top](#-campus-fieldtrack---spring-boot-backend)

</div>
