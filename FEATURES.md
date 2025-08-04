# ESD Final Project - Enhanced Features

## Overview

This Spring Boot application now includes advanced features like caching, pagination, and proper DTO usage for better performance and maintainability.

## New Features Added

### 1. Caching

- **Framework**: Spring Boot Cache with Caffeine
- **Configuration**: Custom cache configuration with expiration policies
- **Cache Names**:
  - `students` - For student-related operations
  - `employees` - For employee-related operations
  - `student-search` - For search results
- **Benefits**: Improved response times and reduced database load

### 2. Pagination

- **Implementation**: Spring Data JPA Pageable
- **Default Settings**:
  - Page size: 10 items
  - Max page size: 100 items
- **Features**: Supports sorting, filtering, and navigation metadata

### 3. DTOs (Data Transfer Objects)

- **Purpose**: Proper separation between data persistence and API responses
- **New DTOs Added**:
  - `StudentResponse` - For student data responses
  - `EmployeeResponse` - For employee data responses
  - `StudentListResponse` - For student list views
  - `StudentSearchResponse` - For search results
  - `PagedResponse<T>` - Generic paginated response wrapper
  - `AuthResponse` - For authentication responses

## API Endpoints

### Student Endpoints (`/api/students`)

- `GET /` - Get all students (simple list)
- `GET /paginated` - Get paginated students with sorting
- `GET /search/{keyword}` - Search students by keyword
- `GET /{id}` - Get student by ID
- `POST /` - Create new student
- `PUT /{id}` - Update student
- `DELETE /{id}` - Delete student

### Employee Endpoints (`/api/employees`)

- `GET /` - Get paginated employees
- `GET /{id}` - Get employee by ID
- `GET /search?name={name}` - Search employees by name (paginated)
- `POST /` - Create new employee
- `PUT /{id}` - Update employee
- `DELETE /{id}` - Delete employee

### Authentication Endpoints (`/api/v1/auth`)

- `POST /login` - Employee login with JWT token response

### Cache Management (`/api/cache`)

- `GET /info` - Get cache information
- `DELETE /clear` - Clear all caches
- `DELETE /clear/{cacheName}` - Clear specific cache

## Pagination Parameters

All paginated endpoints support the following query parameters:

- `page` - Page number (0-based, default: 0)
- `size` - Page size (default: 10, max: 100)
- `sort` - Sort criteria (e.g., `firstName,asc` or `email,desc`)

Example:

```
GET /api/students/paginated?page=0&size=20&sort=firstName,asc
```

## Cache Configuration

The application uses Caffeine cache with the following settings:

- **Initial Capacity**: 100 entries
- **Maximum Size**: 500 entries
- **Expire After Access**: 10 minutes
- **Expire After Write**: 30 minutes
- **Statistics**: Enabled for monitoring

## Response Format

### Paginated Response Structure

```json
{
  "content": [...],
  "page_number": 0,
  "page_size": 10,
  "total_elements": 50,
  "total_pages": 5,
  "is_first": true,
  "is_last": false,
  "has_next": true,
  "has_previous": false
}
```

### Authentication Response

```json
{
  "access_token": "jwt_token_here",
  "token_type": "Bearer",
  "message": "Login successful"
}
```

## Dependencies Added

```xml
<!-- Caching -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

## Performance Benefits

1. **Caching**: Reduces database queries for frequently accessed data
2. **Pagination**: Prevents memory issues with large datasets
3. **DTOs**: Reduces data transfer size and provides clean API contracts
4. **Validation**: Enhanced input validation with proper error handling

## Usage Examples

### Creating a Student

```bash
POST /api/students/
Content-Type: application/json

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

### Getting Paginated Students

```bash
GET /api/students/paginated?page=0&size=5&sort=firstName,asc
```

### Search Students

```bash
GET /api/students/search/TechCorp
```

### Employee Login

```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "employee@example.com",
  "password": "password123"
}
```

## Configuration Properties

```properties
# Caching
spring.cache.type=caffeine
spring.cache.cache-names=students,employees,student-search

# Pagination
spring.data.web.pageable.default-page-size=10
spring.data.web.pageable.max-page-size=100
```

## Error Handling

The application includes proper error handling with:

- Input validation errors (400 Bad Request)
- Not found errors (404 Not Found)
- Internal server errors (500 Internal Server Error)
- Custom exception handling for business logic errors

## Future Enhancements

1. Cache statistics monitoring
2. Custom cache eviction policies
3. Advanced search with multiple criteria
4. Audit logging for cache operations
5. Cache warming strategies
