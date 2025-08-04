# ESD Final Project - Spring Boot Backend Documentation

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture & Design](#architecture--design)
3. [Database Schema](#database-schema)
4. [API Documentation](#api-documentation)
5. [Security Implementation](#security-implementation)
6. [Caching Strategy](#caching-strategy)
7. [Data Flow & Business Logic](#data-flow--business-logic)
8. [Configuration](#configuration)
9. [Error Handling](#error-handling)
10. [Deployment Guide](#deployment-guide)

---

## Project Overview

### Description

This is a comprehensive Spring Boot backend application for a **Placement Management System** designed for educational institutions. The system manages students, employees, placements, and alumni data with advanced features including caching, pagination, and JWT authentication.

### Key Features

- 🎓 **Student Management**: Complete CRUD operations for student records
- 👨‍💼 **Employee Management**: Employee authentication and management
- 🏢 **Placement Tracking**: Track student placements and job opportunities
- 👥 **Alumni Network**: Manage alumni and their organizations
- 🔐 **JWT Authentication**: Secure API endpoints with JWT tokens
- ⚡ **Caching**: High-performance caching with Caffeine
- 📄 **Pagination**: Efficient data retrieval with pagination
- 🔄 **DTOs**: Clean API contracts with Data Transfer Objects

### Technology Stack

- **Framework**: Spring Boot 3.3.5
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Security**: JWT Tokens, BCrypt
- **Caching**: Spring Cache + Caffeine
- **Documentation**: OpenAPI 3.0
- **Build Tool**: Maven

---

## Architecture & Design

### Project Structure

```
src/main/java/com/himanshu/esd_final_project/
├── config/           # Configuration classes
├── controller/       # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA Entities
├── exception/       # Custom Exceptions
├── helper/          # Utility classes
├── mapper/          # Entity-DTO Mappers
├── repo/            # JPA Repositories
└── service/         # Business Logic Layer
```

### Layered Architecture

```
┌─────────────────────────────────────┐
│           Controller Layer          │ ← REST API Endpoints
├─────────────────────────────────────┤
│            Service Layer            │ ← Business Logic + Caching
├─────────────────────────────────────┤
│          Repository Layer           │ ← Data Access
├─────────────────────────────────────┤
│            Entity Layer             │ ← JPA Entities
└─────────────────────────────────────┘
```

### Design Patterns Used

- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer between layers
- **Builder Pattern**: Entity construction (via Lombok)
- **Dependency Injection**: Spring IoC container
- **Aspect-Oriented Programming**: Caching aspects

---

## Database Schema

### Core Entities

#### 1. Student Entity

```sql
CREATE TABLE students (
    id BIGINT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE NOT NULL,
    rollno VARCHAR(255) UNIQUE NOT NULL,
    cgpa INT,
    graduation_year INT,
    total_credits DOUBLE,
    org VARCHAR(255),
    domain BIGINT,
    specialisation BIGINT,
    place_id BIGINT,
    photo_path VARCHAR(255)
);
```

#### 2. Employee Entity

```sql
CREATE TABLE employee (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    title VARCHAR(255),
    photograph_path VARCHAR(255),
    department VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255)
);
```

#### 3. Departments Entity

```sql
CREATE TABLE departments (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) UNIQUE,
    capacity INT
);
```

### Relationship Mapping

#### Entity Relationships

```
Student ──┐
          ├── OneToMany → Alumni
          ├── OneToMany → PlacementStudent
          ├── ManyToOne → Domains
          ├── ManyToOne → Specialisation
          └── ManyToOne → Placement

Employee ──┐
           └── ManyToOne → Departments

Alumni ──┐
         ├── ManyToOne → Student
         └── OneToMany → AlumniOrganisation

Placement ──┐
            └── OneToMany → PlacementStudent
```

### Database Schema Diagram

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Student   │    │  Employee   │    │ Departments │
├─────────────┤    ├─────────────┤    ├─────────────┤
│ id (PK)     │    │ id (PK)     │    │ id (PK)     │
│ first_name  │    │ first_name  │    │ name        │
│ last_name   │    │ last_name   │    │ capacity    │
│ email       │    │ title       │    └─────────────┘
│ rollno      │    │ email       │
│ cgpa        │    │ password    │
│ grad_year   │    │ department  │
│ credits     │    └─────────────┘
│ domain (FK) │
│ spec (FK)   │
│ place (FK)  │
└─────────────┘
```

---

## API Documentation

### Base URL

```
http://localhost:8080
```

### Authentication Endpoints

#### POST /api/v1/auth/login

**Description**: Authenticate employee and get JWT token

**Request Body**:

```json
{
  "email": "employee@example.com",
  "password": "password123"
}
```

**Response**:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "message": "Login successful"
}
```

### Student Management Endpoints

#### GET /api/students/

**Description**: Get all students (simple list)

**Response**:

```json
[
  {
    "id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "email": "john.doe@example.com",
    "placement_status": "Placed"
  }
]
```

#### GET /api/students/paginated

**Description**: Get paginated students with sorting

**Query Parameters**:

- `page`: Page number (0-based, default: 0)
- `size`: Page size (default: 10, max: 100)
- `sort`: Sort criteria (e.g., `firstName,asc`)

**Example Request**:

```
GET /api/students/paginated?page=0&size=5&sort=firstName,asc
```

**Response**:

```json
{
  "content": [
    {
      "first_name": "John",
      "last_name": "Doe",
      "email": "john.doe@example.com",
      "domain": {...},
      "graduation_year": 2024,
      "org": "TechCorp",
      "specialisation": {...}
    }
  ],
  "page_number": 0,
  "page_size": 5,
  "total_elements": 50,
  "total_pages": 10,
  "is_first": true,
  "is_last": false,
  "has_next": true,
  "has_previous": false
}
```

#### GET /api/students/search/{keyword}

**Description**: Search students by keyword (organization, program, graduation year)

**Response**:

```json
[
  {
    "first_name": "John",
    "last_name": "Doe",
    "program": "Computer Science",
    "placement_org": "TechCorp",
    "alumni_org": "TechCorp Alumni",
    "graduation_year": 2024,
    "is_alumni": "Yes",
    "placement_status": "Placed"
  }
]
```

#### POST /api/students/

**Description**: Create new student

**Request Body**:

```json
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john.doe@example.com",
  "domain": {...},
  "total_credits": 120.5,
  "cgpa": 85,
  "graduation_year": 2024,
  "org": "TechCorp",
  "specialisation": {...},
  "rollno": "STU001",
  "photo_path": "/photos/john.jpg",
  "place_id": {...}
}
```

#### PUT /api/students/{id}

**Description**: Update existing student

#### DELETE /api/students/{id}

**Description**: Delete student by ID

### Employee Management Endpoints

#### GET /api/employees/

**Description**: Get paginated employees

#### GET /api/employees/{id}

**Description**: Get employee by ID

#### GET /api/employees/search?name={name}

**Description**: Search employees by name (paginated)

#### POST /api/employees/

**Description**: Create new employee

#### PUT /api/employees/{id}

**Description**: Update employee

#### DELETE /api/employees/{id}

**Description**: Delete employee

### Cache Management Endpoints

#### GET /api/cache/info

**Description**: Get cache information

**Response**:

```json
{
  "cache_names": ["students", "employees", "student-search"],
  "total_caches": 3
}
```

#### DELETE /api/cache/clear

**Description**: Clear all caches

#### DELETE /api/cache/clear/{cacheName}

**Description**: Clear specific cache

---

## Security Implementation

### JWT Token Authentication

#### Token Structure

```
Header.Payload.Signature
```

#### Token Validation Flow

```
Client Request → JWT Filter → Token Validation → Controller
                     ↓
                 Unauthorized (401) ← Invalid/Expired Token
```

#### Security Configuration

```java
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/auth/login");
    }
}
```

#### Password Encryption

- **Algorithm**: BCrypt
- **Rounds**: Default (10)
- **Salt**: Auto-generated per password

### Request Interceptor

```java
@Component
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        // JWT token validation logic
        String token = extractToken(request);
        if (jwtHelper.validateToken(token)) {
            return true;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return false;
    }
}
```

---

## Caching Strategy

### Cache Configuration

```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(500)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats();
    }
}
```

### Cache Regions

| Cache Name       | Purpose        | TTL    | Max Size |
| ---------------- | -------------- | ------ | -------- |
| `students`       | Student data   | 30 min | 200      |
| `employees`      | Employee data  | 30 min | 200      |
| `student-search` | Search results | 10 min | 100      |

### Caching Annotations

#### @Cacheable

```java
@Cacheable(value = "students", key = "#id")
public StudentResponse getStudentById(Long id) {
    // Cache miss: fetch from database
    // Cache hit: return from cache
}
```

#### @CacheEvict

```java
@CacheEvict(value = {"students", "student-search"}, allEntries = true)
public StudentResponse createStudent(StudentRequest request) {
    // Clear cache after data modification
}
```

#### Cache Key Strategies

- **Simple Keys**: `#id`, `#email`
- **Composite Keys**: `'paginated-' + #pageable.pageNumber + '-' + #pageable.pageSize`
- **Search Keys**: `#keyword`

### Cache Performance Metrics

```java
// Cache hit ratio monitoring
CacheStats stats = cache.stats();
double hitRate = stats.hitRate();
long evictionCount = stats.evictionCount();
```

---

## Data Flow & Business Logic

### Student Management Flow

#### 1. Student Creation Flow

```
POST /api/students/
        ↓
   Validation (DTO)
        ↓
   Mapper (DTO → Entity)
        ↓
   Business Logic (Service)
        ↓
   Database Save (Repository)
        ↓
   Cache Eviction
        ↓
   Response (Entity → DTO)
```

#### 2. Student Search Flow

```
GET /api/students/search/{keyword}
        ↓
   Cache Check (@Cacheable)
        ↓
   Cache Hit? → Return Cached Result
        ↓
   Cache Miss → Database Query
        ↓
   Complex JOIN Query Execution
        ↓
   Result Mapping (Object[] → DTO)
        ↓
   Cache Storage + Response
```

### Authentication Flow

#### Login Process

```
POST /api/v1/auth/login
        ↓
   Email Validation
        ↓
   Password Verification (BCrypt)
        ↓
   JWT Token Generation
        ↓
   Token Response
```

#### Request Authorization

```
API Request with JWT Token
        ↓
   RequestInterceptor
        ↓
   Token Extraction (Header)
        ↓
   Token Validation (Signature + Expiry)
        ↓
   Valid? → Continue to Controller
        ↓
   Invalid? → 401 Unauthorized
```

### Pagination Flow

#### Paginated Request Processing

```
GET /api/students/paginated?page=0&size=10&sort=firstName,asc
        ↓
   Pageable Parameter Binding
        ↓
   Cache Key Generation
        ↓
   Cache Lookup
        ↓
   Cache Miss → Database Query (LIMIT + OFFSET)
        ↓
   Page<Entity> → Page<DTO> Mapping
        ↓
   PagedResponse<DTO> Creation
        ↓
   Cache Storage + Response
```

### Error Handling Flow

#### Exception Processing

```
Controller Method Execution
        ↓
   Business Logic Exception
        ↓
   @ControllerAdvice Handler
        ↓
   Error Response DTO Creation
        ↓
   HTTP Status Code Mapping
        ↓
   JSON Error Response
```

---

## Configuration

### Application Properties

```properties
# Application Configuration
spring.application.name=ESD_Final_Project

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/placement_db?createDatabaseIfNotExist=true
spring.datasource.username=himanshu
spring.datasource.password=Aryabhatta@0000
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Caching Configuration
spring.cache.type=caffeine
spring.cache.cache-names=students,employees,student-search

# Pagination Configuration
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=100
```

### Maven Dependencies

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-cache</artifactId>
    </dependency>

    <!-- JWT Dependencies -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Caching -->
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
    </dependency>

    <!-- Utilities -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>8.0.1.Final</version>
    </dependency>
</dependencies>
```

---

## Error Handling

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(StudentNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "STUDENT_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(EmployeeNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            "EMPLOYEE_NOT_FOUND",
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Validation failed: " + String.join(", ", errors),
            HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(error);
    }
}
```

### Error Response Format

```json
{
  "error_code": "STUDENT_NOT_FOUND",
  "message": "Student with id 123 not found",
  "status_code": 404,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### HTTP Status Codes Used

| Status Code | Description           | Usage                      |
| ----------- | --------------------- | -------------------------- |
| 200         | OK                    | Successful GET requests    |
| 201         | Created               | Successful POST requests   |
| 204         | No Content            | Successful DELETE requests |
| 400         | Bad Request           | Validation errors          |
| 401         | Unauthorized          | Authentication failures    |
| 404         | Not Found             | Resource not found         |
| 500         | Internal Server Error | Unexpected errors          |

---

## Deployment Guide

### Prerequisites

- ☑️ Java 17 or higher
- ☑️ MySQL 8.0 or higher
- ☑️ Maven 3.6 or higher
- ☑️ 2GB RAM minimum

### Database Setup

```sql
-- Create database
CREATE DATABASE placement_db;

-- Create user (optional)
CREATE USER 'himanshu'@'localhost' IDENTIFIED BY 'Aryabhatta@0000';
GRANT ALL PRIVILEGES ON placement_db.* TO 'himanshu'@'localhost';
FLUSH PRIVILEGES;
```

### Build & Run

#### Option 1: Maven Wrapper

```bash
# Build the project
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw clean package

# Run application
./mvnw spring-boot:run
```

#### Option 2: Java JAR

```bash
# Build JAR file
./mvnw clean package

# Run JAR
java -jar target/ESD_Final_Project-0.0.1-SNAPSHOT.jar
```

### Environment Variables

```bash
# Database Configuration
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/placement_db
export SPRING_DATASOURCE_USERNAME=himanshu
export SPRING_DATASOURCE_PASSWORD=Aryabhatta@0000

# JWT Configuration
export JWT_SECRET=your-jwt-secret-key

# Cache Configuration
export SPRING_CACHE_TYPE=caffeine
```

### Production Configuration

```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

  cache:
    caffeine:
      spec: maximumSize=1000,expireAfterAccess=30m

logging:
  level:
    com.himanshu.esd_final_project: INFO
    org.springframework.cache: DEBUG
```

### Docker Deployment

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ESD_Final_Project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

```yaml
# docker-compose.yml
version: "3.8"
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/placement_db
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=password
    depends_on:
      - mysql

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=placement_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Cache statistics
curl http://localhost:8080/api/cache/info

# Database connectivity
curl http://localhost:8080/actuator/health/db
```

### Performance Monitoring

```yaml
# Add to application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,caches
  endpoint:
    health:
      show-details: always
```

---

## Performance Optimizations

### Database Optimizations

- **Indexes**: Added on frequently queried columns (email, rollno)
- **Connection Pooling**: HikariCP with optimized settings
- **Query Optimization**: Native queries for complex searches
- **Lazy Loading**: Configured for optional relationships

### Caching Optimizations

- **Cache Sizing**: Appropriate cache sizes based on data volume
- **TTL Strategy**: Different expiration times for different data types
- **Cache Warming**: Pre-load frequently accessed data
- **Eviction Policy**: LRU eviction for memory management

### API Optimizations

- **Pagination**: Prevents large data transfers
- **DTO Optimization**: Minimal data transfer
- **Compression**: Gzip compression for responses
- **Connection Keep-Alive**: Persistent connections

---

## Security Considerations

### Data Protection

- **Password Hashing**: BCrypt with salt
- **SQL Injection Prevention**: Parameterized queries
- **Input Validation**: Comprehensive validation rules
- **HTTPS**: Force HTTPS in production

### Access Control

- **JWT Tokens**: Stateless authentication
- **Role-Based Access**: Employee-only access patterns
- **CORS Configuration**: Restricted origins
- **Request Rate Limiting**: Prevent abuse

### Monitoring & Auditing

- **Security Logs**: Authentication attempts
- **Access Logs**: API access patterns
- **Error Tracking**: Security exception monitoring
- **Health Checks**: System status monitoring

---

This documentation provides a comprehensive overview of the Spring Boot backend system, covering all major aspects from architecture to deployment. The system is designed for scalability, maintainability, and security, making it suitable for production use in educational institutions.
