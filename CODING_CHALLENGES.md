# 💻 Live Coding Interview Challenges

## 🎯 Common Coding Scenarios for Spring Boot Interviews

### **Scenario 1: "Add a New Feature Live"**

**Challenge**: "Add a new endpoint to get students by graduation year with pagination"

#### Step-by-Step Solution:

**1. Add Repository Method**

```java
// In StudentRepo.java
@Query("SELECT s FROM Student s WHERE s.gradYear = :year")
Page<Student> findStudentsByGradYear(@Param("year") int year, Pageable pageable);
```

**2. Update Service Layer**

```java
// In StudentService.java
@Cacheable(value = "students", key = "'grad-year-' + #year + '-' + #pageable.pageNumber")
public PagedResponse<StudentResponse> getStudentsByGradYear(int year, Pageable pageable) {
    Page<Student> studentPage = studentRepo.findStudentsByGradYear(year, pageable);
    Page<StudentResponse> responsePage = studentPage.map(studentMapper::toStudentResponse);
    return PagedResponse.of(responsePage);
}
```

**3. Add Controller Endpoint**

```java
// In StudentController.java
@GetMapping("/graduation-year/{year}")
public ResponseEntity<PagedResponse<StudentResponse>> getStudentsByGradYear(
        @PathVariable int year,
        @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
    PagedResponse<StudentResponse> students = studentService.getStudentsByGradYear(year, pageable);
    return ResponseEntity.ok(students);
}
```

**Key Points to Mention:**

- Added caching for performance
- Used pagination for large result sets
- Followed existing naming conventions
- Added proper validation for year parameter

---

### **Scenario 2: "Debug This Code"**

**Challenge**: "This endpoint is returning 500 errors, find and fix the issue"

#### Common Issues to Look For:

**1. Null Pointer Exception**

```java
// Problematic code
public StudentResponse getStudentById(Long id) {
    Student student = studentRepo.findById(id).get(); // Can throw NoSuchElementException
    return studentMapper.toStudentResponse(student);
}

// Fixed code
public StudentResponse getStudentById(Long id) {
    Student student = studentRepo.findById(id)
        .orElseThrow(() -> new StudentNotFoundException("Student with id " + id + " not found"));
    return studentMapper.toStudentResponse(student);
}
```

**2. Missing Validation**

```java
// Problematic code
@PostMapping("/")
public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest request) {
    // Missing @Valid annotation
}

// Fixed code
@PostMapping("/")
public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
    // Now validation works
}
```

**3. Transaction Issues**

```java
// Problematic code - no transaction
public void updateMultipleStudents(List<StudentRequest> requests) {
    for (StudentRequest request : requests) {
        // Each save is a separate transaction
        studentRepo.save(studentMapper.toStudent(request));
    }
}

// Fixed code
@Transactional
public void updateMultipleStudents(List<StudentRequest> requests) {
    // All saves in single transaction
    for (StudentRequest request : requests) {
        studentRepo.save(studentMapper.toStudent(request));
    }
}
```

---

### **Scenario 3: "Optimize This Slow Query"**

**Challenge**: "This search endpoint is very slow, optimize it"

#### Current Slow Implementation:

```java
@GetMapping("/search/{keyword}")
public List<StudentSearchResponse> searchStudents(@PathVariable String keyword) {
    // This loads all students into memory - SLOW!
    List<Student> allStudents = studentRepo.findAll();
    return allStudents.stream()
        .filter(s -> s.getFirstName().contains(keyword) || s.getLastName().contains(keyword))
        .map(studentMapper::toStudentSearchResponse)
        .collect(Collectors.toList());
}
```

#### Optimized Solution:

```java
// 1. Add database-level query
@Query("SELECT s FROM Student s WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
       "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
Page<Student> searchStudentsByName(@Param("keyword") String keyword, Pageable pageable);

// 2. Add caching and pagination
@Cacheable(value = "student-search", key = "#keyword + '-' + #pageable.pageNumber")
public PagedResponse<StudentSearchResponse> searchStudents(String keyword, Pageable pageable) {
    Page<Student> studentPage = studentRepo.searchStudentsByName(keyword, pageable);
    Page<StudentSearchResponse> responsePage = studentPage.map(studentMapper::toStudentSearchResponse);
    return PagedResponse.of(responsePage);
}

// 3. Update controller
@GetMapping("/search")
public ResponseEntity<PagedResponse<StudentSearchResponse>> searchStudents(
        @RequestParam String keyword,
        @PageableDefault(size = 10) Pageable pageable) {
    PagedResponse<StudentSearchResponse> results = studentService.searchStudents(keyword, pageable);
    return ResponseEntity.ok(results);
}
```

**Optimization Techniques Demonstrated:**

- Database-level filtering instead of in-memory filtering
- Pagination to limit result size
- Caching for repeated searches
- Case-insensitive search
- Proper indexing (mention adding database index on name fields)

---

### **Scenario 4: "Implement Security"**

**Challenge**: "Add role-based access control to this endpoint"

#### Current Implementation:

```java
@GetMapping("/")
public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
    // Anyone with valid JWT can access
}
```

#### Enhanced Security Solution:

```java
// 1. Add Role enum
public enum Role {
    ADMIN, HR, EMPLOYEE
}

// 2. Enhance JWT to include roles
public String generateToken(String username, Set<Role> roles) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", roles.stream().map(Enum::name).collect(Collectors.toList()));
    return createToken(claims, username);
}

// 3. Create security annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    Role[] value();
}

// 4. Update interceptor to check roles
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    // Extract and validate JWT
    String token = extractToken(request);
    if (!jwtHelper.validateToken(token)) {
        return false;
    }

    // Check method-level role requirements
    if (handler instanceof HandlerMethod) {
        HandlerMethod method = (HandlerMethod) handler;
        RequireRole roleAnnotation = method.getMethodAnnotation(RequireRole.class);
        if (roleAnnotation != null) {
            Set<Role> userRoles = jwtHelper.extractRoles(token);
            Set<Role> requiredRoles = Set.of(roleAnnotation.value());
            if (Collections.disjoint(userRoles, requiredRoles)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
        }
    }
    return true;
}

// 5. Apply to controller
@GetMapping("/")
@RequireRole({Role.ADMIN, Role.HR})
public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
    // Only ADMIN and HR can access
}
```

---

### **Scenario 5: "Handle Concurrent Updates"**

**Challenge**: "Prevent race conditions when updating student data"

#### Problem:

```java
// Two users updating same student simultaneously can cause data corruption
public StudentResponse updateStudent(Long id, StudentRequest request) {
    Student student = studentRepo.findById(id).orElseThrow(...);
    // Time gap here - another thread might modify the student
    studentMapper.updateStudentFromRequest(request, student);
    return studentMapper.toStudentResponse(studentRepo.save(student));
}
```

#### Solution Options:

**Option 1: Optimistic Locking**

```java
@Entity
public class Student {
    @Version
    private Long version;
    // other fields...
}

@Transactional
public StudentResponse updateStudent(Long id, StudentRequest request) {
    try {
        Student student = studentRepo.findById(id).orElseThrow(...);
        studentMapper.updateStudentFromRequest(request, student);
        Student saved = studentRepo.save(student);
        return studentMapper.toStudentResponse(saved);
    } catch (OptimisticLockingFailureException e) {
        throw new ConflictException("Student was modified by another user. Please refresh and try again.");
    }
}
```

**Option 2: Pessimistic Locking**

```java
@Query("SELECT s FROM Student s WHERE s.id = :id")
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Student> findByIdWithLock(@Param("id") Long id);

@Transactional
public StudentResponse updateStudent(Long id, StudentRequest request) {
    Student student = studentRepo.findByIdWithLock(id).orElseThrow(...);
    studentMapper.updateStudentFromRequest(request, student);
    return studentMapper.toStudentResponse(studentRepo.save(student));
}
```

---

### **Scenario 6: "Add Input Validation"**

**Challenge**: "Add comprehensive validation to prevent SQL injection and invalid data"

#### Enhanced DTO with Validation:

```java
public record StudentRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
    String lastName,

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,

    @NotBlank(message = "Roll number is required")
    @Pattern(regexp = "^[A-Z]{3}\\d{7}$", message = "Roll number must be in format ABC1234567")
    String rollNo,

    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    @Digits(integer = 2, fraction = 2, message = "CGPA must have at most 2 decimal places")
    Double cgpa,

    @Min(value = 2020, message = "Graduation year cannot be before 2020")
    @Max(value = 2030, message = "Graduation year cannot be after 2030")
    Integer graduationYear
) {}
```

#### Custom Validation:

```java
@Component
public class StudentValidator {

    @Autowired
    private StudentRepo studentRepo;

    public void validateUniqueEmail(String email, Long excludeId) {
        boolean emailExists = (excludeId == null)
            ? studentRepo.existsByEmail(email)
            : studentRepo.existsByEmailAndIdNot(email, excludeId);

        if (emailExists) {
            throw new ValidationException("Email already exists");
        }
    }

    public void validateUniqueRollNo(String rollNo, Long excludeId) {
        boolean rollNoExists = (excludeId == null)
            ? studentRepo.existsByRollNo(rollNo)
            : studentRepo.existsByRollNoAndIdNot(rollNo, excludeId);

        if (rollNoExists) {
            throw new ValidationException("Roll number already exists");
        }
    }
}
```

---

## 🎯 General Coding Interview Tips

### **1. Problem-Solving Approach**

1. **Understand**: Ask clarifying questions
2. **Plan**: Outline your approach before coding
3. **Implement**: Write clean, readable code
4. **Test**: Consider edge cases and error scenarios
5. **Optimize**: Discuss performance improvements

### **2. Code Quality Checklist**

- ✅ Proper error handling
- ✅ Input validation
- ✅ Consistent naming conventions
- ✅ Appropriate logging
- ✅ Transaction management
- ✅ Performance considerations

### **3. Common Gotchas to Avoid**

- Missing `@Valid` annotation for validation
- Not handling `Optional.empty()` cases
- Forgetting `@Transactional` for multi-step operations
- Using `findAll()` without pagination for large datasets
- Hardcoding values instead of using configuration
- Not considering concurrent access scenarios

### **4. Questions to Ask**

- "Should I consider performance optimization?"
- "What about error handling for this scenario?"
- "Do you want me to add validation?"
- "Should I implement caching for this?"
- "What about security considerations?"

Remember: **Think out loud, explain your reasoning, and write production-quality code!**
