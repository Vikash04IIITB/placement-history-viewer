# API Testing Guide

## Overview

This guide provides comprehensive examples for testing all API endpoints of the ESD Final Project placement management system using various tools.

## Testing Tools

- **Postman**: GUI-based API testing
- **cURL**: Command-line testing
- **HTTPie**: User-friendly command-line tool
- **REST Client**: VS Code extension

## Base Configuration

### Base URL

```
http://localhost:8080
```

### Headers

```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <jwt_token>"
}
```

## Authentication Testing

### 1. Employee Login

#### POST /api/v1/auth/login

**Request**:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "employee@example.com",
    "password": "password123"
  }'
```

**Postman**:

```json
Method: POST
URL: http://localhost:8080/api/v1/auth/login
Headers:
  Content-Type: application/json
Body (JSON):
{
  "email": "employee@example.com",
  "password": "password123"
}
```

**Expected Response**:

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "message": "Login successful"
}
```

**HTTPie**:

```bash
http POST localhost:8080/api/v1/auth/login \
  email=employee@example.com \
  password=password123
```

## Student Management Testing

### 2. Get All Students (Simple List)

#### GET /api/students/

**cURL**:

```bash
curl -X GET http://localhost:8080/api/students/ \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**:

```json
[
  {
    "id": 1,
    "first_name": "John",
    "last_name": "Doe",
    "email": "john.doe@example.com",
    "placement_status": "Placed"
  },
  {
    "id": 2,
    "first_name": "Jane",
    "last_name": "Smith",
    "email": "jane.smith@example.com",
    "placement_status": "Unplaced"
  }
]
```

### 3. Get Paginated Students

#### GET /api/students/paginated

**cURL**:

```bash
curl -X GET "http://localhost:8080/api/students/paginated?page=0&size=5&sort=firstName,asc" \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Postman**:

```
Method: GET
URL: http://localhost:8080/api/students/paginated
Params:
  page: 0
  size: 5
  sort: firstName,asc
Headers:
  Authorization: Bearer <your_jwt_token>
```

**Expected Response**:

```json
{
  "content": [
    {
      "first_name": "Alice",
      "last_name": "Johnson",
      "email": "alice.johnson@example.com",
      "domain": {
        "id": 1,
        "program": "Computer Science",
        "batch": "2024"
      },
      "graduation_year": 2024,
      "org": "TechCorp",
      "specialisation": {
        "id": 1,
        "name": "AI/ML"
      }
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

### 4. Search Students by Keyword

#### GET /api/students/search/{keyword}

**cURL**:

```bash
curl -X GET http://localhost:8080/api/students/search/TechCorp \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**:

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

### 5. Get Student by ID

#### GET /api/students/{id}

**cURL**:

```bash
curl -X GET http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**:

```json
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john.doe@example.com",
  "domain": {
    "id": 1,
    "program": "Computer Science"
  },
  "graduation_year": 2024,
  "org": "TechCorp",
  "specialisation": {
    "id": 1,
    "name": "AI/ML"
  }
}
```

### 6. Create New Student

#### POST /api/students/

**cURL**:

```bash
curl -X POST http://localhost:8080/api/students/ \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "John",
    "last_name": "Doe",
    "email": "john.doe@example.com",
    "total_credits": 120.5,
    "cgpa": 85,
    "graduation_year": 2024,
    "org": "TechCorp",
    "rollno": "STU001",
    "photo_path": "/photos/john.jpg"
  }'
```

**Postman**:

```json
Method: POST
URL: http://localhost:8080/api/students/
Headers:
  Authorization: Bearer <your_jwt_token>
  Content-Type: application/json
Body (JSON):
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john.doe@example.com",
  "total_credits": 120.5,
  "cgpa": 85,
  "graduation_year": 2024,
  "org": "TechCorp",
  "rollno": "STU001",
  "photo_path": "/photos/john.jpg"
}
```

**Expected Response**:

```json
{
  "first_name": "John",
  "last_name": "Doe",
  "email": "john.doe@example.com",
  "domain": null,
  "graduation_year": 2024,
  "org": "TechCorp",
  "specialisation": null
}
```

### 7. Update Student

#### PUT /api/students/{id}

**cURL**:

```bash
curl -X PUT http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "John Updated",
    "last_name": "Doe",
    "email": "john.doe@example.com",
    "total_credits": 125.0,
    "cgpa": 88,
    "graduation_year": 2024,
    "org": "NewTechCorp",
    "rollno": "STU001",
    "photo_path": "/photos/john_updated.jpg"
  }'
```

### 8. Delete Student

#### DELETE /api/students/{id}

**cURL**:

```bash
curl -X DELETE http://localhost:8080/api/students/1 \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**: `204 No Content`

## Employee Management Testing

### 9. Get Paginated Employees

#### GET /api/employees/

**cURL**:

```bash
curl -X GET "http://localhost:8080/api/employees/?page=0&size=10&sort=firstName,asc" \
  -H "Authorization: Bearer <your_jwt_token>"
```

### 10. Create New Employee

#### POST /api/employees/

**cURL**:

```bash
curl -X POST http://localhost:8080/api/employees/ \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "Jane",
    "last_name": "Smith",
    "title": "Professor",
    "email": "jane.smith@university.edu",
    "password": "securepass123",
    "departmentName": "Computer Science",
    "photograph_path": "/photos/jane.jpg"
  }'
```

**Expected Response**:

```json
{
  "id": 1,
  "first_name": "Jane",
  "last_name": "Smith",
  "title": "Professor",
  "email": "jane.smith@university.edu",
  "department": "Computer Science",
  "photograph_path": "/photos/jane.jpg"
}
```

### 11. Search Employees by Name

#### GET /api/employees/search

**cURL**:

```bash
curl -X GET "http://localhost:8080/api/employees/search?name=Jane&page=0&size=10" \
  -H "Authorization: Bearer <your_jwt_token>"
```

## Cache Management Testing

### 12. Get Cache Information

#### GET /api/cache/info

**cURL**:

```bash
curl -X GET http://localhost:8080/api/cache/info \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**:

```json
{
  "cache_names": ["students", "employees", "student-search"],
  "total_caches": 3
}
```

### 13. Clear All Caches

#### DELETE /api/cache/clear

**cURL**:

```bash
curl -X DELETE http://localhost:8080/api/cache/clear \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**:

```json
{
  "message": "All caches cleared successfully"
}
```

### 14. Clear Specific Cache

#### DELETE /api/cache/clear/{cacheName}

**cURL**:

```bash
curl -X DELETE http://localhost:8080/api/cache/clear/students \
  -H "Authorization: Bearer <your_jwt_token>"
```

## Error Testing Scenarios

### 15. Test Unauthorized Access

**cURL**:

```bash
curl -X GET http://localhost:8080/api/students/ \
  -H "Authorization: Bearer invalid_token"
```

**Expected Response**:

```json
{
  "error": "Unauthorized",
  "status": 401,
  "message": "Invalid or expired token"
}
```

### 16. Test Validation Errors

**cURL**:

```bash
curl -X POST http://localhost:8080/api/students/ \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "last_name": "Doe",
    "email": "invalid-email",
    "cgpa": 150
  }'
```

**Expected Response**:

```json
{
  "error_code": "VALIDATION_ERROR",
  "message": "Validation failed: first name is required, email should be in correct format",
  "status_code": 400,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 17. Test Not Found Errors

**cURL**:

```bash
curl -X GET http://localhost:8080/api/students/99999 \
  -H "Authorization: Bearer <your_jwt_token>"
```

**Expected Response**:

```json
{
  "error_code": "STUDENT_NOT_FOUND",
  "message": "Student with id 99999 not found",
  "status_code": 404,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Load Testing with cURL

### 18. Stress Test Pagination

```bash
#!/bin/bash
# Stress test pagination endpoints
for i in {0..10}
do
  curl -X GET "http://localhost:8080/api/students/paginated?page=$i&size=20" \
    -H "Authorization: Bearer <your_jwt_token>" \
    -w "Response time: %{time_total}s\n" \
    -o /dev/null -s
  sleep 1
done
```

### 19. Cache Performance Test

```bash
#!/bin/bash
# Test cache performance
echo "First request (cache miss):"
time curl -X GET http://localhost:8080/api/students/ \
  -H "Authorization: Bearer <your_jwt_token>" \
  -o /dev/null -s

echo "Second request (cache hit):"
time curl -X GET http://localhost:8080/api/students/ \
  -H "Authorization: Bearer <your_jwt_token>" \
  -o /dev/null -s
```

## Postman Collection Structure

### Environment Variables

```json
{
  "baseUrl": "http://localhost:8080",
  "jwtToken": "",
  "studentId": "1",
  "employeeId": "1"
}
```

### Pre-request Scripts

**For Authentication**:

```javascript
// Auto-login script
pm.sendRequest(
  {
    url: pm.environment.get("baseUrl") + "/api/v1/auth/login",
    method: "POST",
    header: {
      "Content-Type": "application/json",
    },
    body: {
      mode: "raw",
      raw: JSON.stringify({
        email: "employee@example.com",
        password: "password123",
      }),
    },
  },
  function (err, res) {
    if (res.code === 200) {
      var responseJson = res.json();
      pm.environment.set("jwtToken", responseJson.access_token);
    }
  }
);
```

### Test Scripts

**For Response Validation**:

```javascript
pm.test("Status code is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("Response has required fields", function () {
  var jsonData = pm.response.json();
  pm.expect(jsonData).to.have.property("content");
  pm.expect(jsonData).to.have.property("page_number");
  pm.expect(jsonData).to.have.property("total_elements");
});

pm.test("Response time is less than 500ms", function () {
  pm.expect(pm.response.responseTime).to.be.below(500);
});
```

## Integration Testing Scenarios

### 20. Complete Student Lifecycle Test

```bash
#!/bin/bash
# Complete student lifecycle test

BASE_URL="http://localhost:8080"
JWT_TOKEN="<your_jwt_token>"

echo "1. Creating student..."
STUDENT_ID=$(curl -X POST $BASE_URL/api/students/ \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "Test",
    "last_name": "Student",
    "email": "test.student@example.com",
    "rollno": "TEST001",
    "cgpa": 85,
    "graduation_year": 2024
  }' | jq -r '.id')

echo "Created student with ID: $STUDENT_ID"

echo "2. Getting student..."
curl -X GET $BASE_URL/api/students/$STUDENT_ID \
  -H "Authorization: Bearer $JWT_TOKEN"

echo "3. Updating student..."
curl -X PUT $BASE_URL/api/students/$STUDENT_ID \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "first_name": "Updated Test",
    "last_name": "Student",
    "email": "test.student@example.com",
    "rollno": "TEST001",
    "cgpa": 90,
    "graduation_year": 2024
  }'

echo "4. Searching for student..."
curl -X GET $BASE_URL/api/students/search/2024 \
  -H "Authorization: Bearer $JWT_TOKEN"

echo "5. Deleting student..."
curl -X DELETE $BASE_URL/api/students/$STUDENT_ID \
  -H "Authorization: Bearer $JWT_TOKEN"

echo "Test completed!"
```

## Performance Benchmarking

### 21. Apache Bench (ab) Testing

```bash
# Test authentication endpoint
ab -n 100 -c 10 -p login_data.json -T application/json \
  http://localhost:8080/api/v1/auth/login

# Test paginated endpoint (with JWT token)
ab -n 1000 -c 50 -H "Authorization: Bearer <token>" \
  http://localhost:8080/api/students/paginated?page=0&size=10
```

### 22. wrk Load Testing

```bash
# Install wrk first, then:
wrk -t12 -c400 -d30s --script=auth.lua \
  http://localhost:8080/api/students/

# auth.lua script:
wrk.method = "GET"
wrk.headers["Authorization"] = "Bearer <your_jwt_token>"
```

This comprehensive testing guide covers all API endpoints with practical examples for different testing tools and scenarios.
