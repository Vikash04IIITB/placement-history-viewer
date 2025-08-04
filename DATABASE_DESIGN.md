# Database Design & Entity Relationships

## Overview

This document details the database design, entity relationships, and data model for the ESD Final Project placement management system.

## Entity Relationship Diagram

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│     Student     │         │    Employee     │         │   Departments   │
├─────────────────┤         ├─────────────────┤         ├─────────────────┤
│ id (PK)         │         │ id (PK)         │         │ id (PK)         │
│ first_name      │         │ first_name      │         │ name (UNIQUE)   │
│ last_name       │         │ last_name       │         │ capacity        │
│ email (UNIQUE)  │         │ title           │         └─────────────────┘
│ rollno (UNIQUE) │         │ photograph_path │
│ cgpa            │         │ email           │
│ graduation_year │         │ password        │
│ total_credits   │         │ department      │
│ org             │         └─────────────────┘
│ domain (FK)     │                 │
│ specialisation  │                 │
│ place_id (FK)   │                 │
│ photo_path      │                 │
└─────────────────┘                 │
         │                          │
         │ 1:N                      │ N:1
         ▼                          ▼
┌─────────────────┐         ┌─────────────────┐
│     Alumni      │         │    (Department) │
├─────────────────┤         │   Relationship  │
│ id (PK)         │         └─────────────────┘
│ sid (FK)        │
│ contact_number  │
│ address         │
│ company         │
│ position        │
│ joining_year    │
└─────────────────┘
         │ 1:N
         ▼
┌─────────────────┐
│  AlumniOrg      │
├─────────────────┤
│ id (PK)         │
│ alumni_id (FK)  │
│ org             │
│ position        │
│ joining_date    │
│ leaving_date    │
└─────────────────┘

         ┌─────────────────┐
         │    Placement    │
         ├─────────────────┤
         │ id (PK)         │
         │ org             │
         │ profile         │
         │ description     │
         │ intake          │
         │ min_grade       │
         │ domain (FK)     │
         └─────────────────┘
                  │ 1:N
                  ▼
         ┌─────────────────┐
         │ PlacementStudent│
         ├─────────────────┤
         │ id (PK)         │
         │ place_id (FK)   │
         │ sid (FK)        │
         │ date            │
         │ acceptance      │
         └─────────────────┘

┌─────────────────┐         ┌─────────────────┐
│     Domains     │         │ Specialisation  │
├─────────────────┤         ├─────────────────┤
│ id (PK)         │         │ id (PK)         │
│ program         │         │ name            │
│ batch           │         │ description     │
│ capacity        │         │ year            │
│ qualification   │         │ credits_required│
└─────────────────┘         └─────────────────┘
         │ 1:N                       │ 1:N
         │                           │
         └─────────┬─────────────────┘
                   ▼
            (Student Domain &
         Specialisation Relationships)
```

## Detailed Entity Specifications

### 1. Student Entity

**Table Name**: `students`

| Column          | Type         | Constraints      | Description                       |
| --------------- | ------------ | ---------------- | --------------------------------- |
| id              | BIGINT       | PRIMARY KEY      | Unique student identifier         |
| first_name      | VARCHAR(255) | NOT NULL         | Student's first name              |
| last_name       | VARCHAR(255) |                  | Student's last name               |
| email           | VARCHAR(255) | UNIQUE, NOT NULL | Student's email address           |
| rollno          | VARCHAR(255) | UNIQUE, NOT NULL | Student roll number               |
| cgpa            | INT          |                  | Cumulative Grade Point Average    |
| graduation_year | INT          |                  | Year of graduation                |
| total_credits   | DOUBLE       |                  | Total credits earned              |
| org             | VARCHAR(255) |                  | Current organization              |
| domain          | BIGINT       | FOREIGN KEY      | Reference to Domains table        |
| specialisation  | BIGINT       | FOREIGN KEY      | Reference to Specialisation table |
| place_id        | BIGINT       | FOREIGN KEY      | Reference to Placement table      |
| photo_path      | VARCHAR(255) |                  | Path to student's photo           |

**Relationships**:

- `OneToMany` with Alumni
- `OneToMany` with PlacementStudent
- `ManyToOne` with Domains
- `ManyToOne` with Specialisation
- `ManyToOne` with Placement

**Business Rules**:

- Email must be unique across all students
- Roll number must be unique
- CGPA should be between 0-100
- Graduation year should be valid year

### 2. Employee Entity

**Table Name**: `employee`

| Column          | Type         | Constraints                 | Description                |
| --------------- | ------------ | --------------------------- | -------------------------- |
| id              | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Unique employee identifier |
| first_name      | VARCHAR(255) |                             | Employee's first name      |
| last_name       | VARCHAR(255) |                             | Employee's last name       |
| title           | VARCHAR(255) |                             | Job title/designation      |
| photograph_path | VARCHAR(255) |                             | Path to employee's photo   |
| email           | VARCHAR(255) | UNIQUE                      | Employee's email address   |
| password        | VARCHAR(255) | NOT NULL                    | Encrypted password         |
| department      | VARCHAR(255) |                             | Department name            |

**Relationships**:

- None (simplified for current implementation)

**Business Rules**:

- Email must be unique across all employees
- Password must be BCrypt encrypted
- Title should indicate position/role

### 3. Alumni Entity

**Table Name**: `alumni`

| Column         | Type         | Constraints | Description                |
| -------------- | ------------ | ----------- | -------------------------- |
| id             | BIGINT       | PRIMARY KEY | Unique alumni identifier   |
| sid            | BIGINT       | FOREIGN KEY | Reference to Student       |
| contact_number | VARCHAR(15)  |             | Alumni contact number      |
| address        | TEXT         |             | Alumni address             |
| company        | VARCHAR(255) |             | Current company            |
| position       | VARCHAR(255) |             | Current position           |
| joining_year   | INT          |             | Year joined alumni network |

**Relationships**:

- `ManyToOne` with Student
- `OneToMany` with AlumniOrganisation

### 4. Placement Entity

**Table Name**: `placement`

| Column      | Type         | Constraints | Description                 |
| ----------- | ------------ | ----------- | --------------------------- |
| id          | BIGINT       | PRIMARY KEY | Unique placement identifier |
| org         | VARCHAR(255) | NOT NULL    | Organization/Company name   |
| profile     | VARCHAR(255) |             | Job profile/role            |
| description | TEXT         |             | Job description             |
| intake      | INT          |             | Number of positions         |
| min_grade   | DOUBLE       |             | Minimum grade requirement   |
| domain      | BIGINT       | FOREIGN KEY | Reference to Domains        |

**Relationships**:

- `OneToMany` with PlacementStudent
- `ManyToOne` with Domains

### 5. PlacementStudent Entity (Junction Table)

**Table Name**: `placement_student`

| Column     | Type    | Constraints | Description              |
| ---------- | ------- | ----------- | ------------------------ |
| id         | BIGINT  | PRIMARY KEY | Unique record identifier |
| place_id   | BIGINT  | FOREIGN KEY | Reference to Placement   |
| sid        | BIGINT  | FOREIGN KEY | Reference to Student     |
| date       | DATE    |             | Placement date           |
| acceptance | BOOLEAN |             | Whether student accepted |

**Relationships**:

- `ManyToOne` with Placement
- `ManyToOne` with Student

### 6. Domains Entity

**Table Name**: `domains`

| Column        | Type         | Constraints | Description              |
| ------------- | ------------ | ----------- | ------------------------ |
| id            | BIGINT       | PRIMARY KEY | Unique domain identifier |
| program       | VARCHAR(255) | NOT NULL    | Program name             |
| batch         | VARCHAR(100) |             | Batch identifier         |
| capacity      | INT          |             | Maximum students         |
| qualification | VARCHAR(255) |             | Required qualification   |

**Relationships**:

- `OneToMany` with Student
- `OneToMany` with Placement

### 7. Specialisation Entity

**Table Name**: `specialisation`

| Column           | Type         | Constraints | Description                      |
| ---------------- | ------------ | ----------- | -------------------------------- |
| id               | BIGINT       | PRIMARY KEY | Unique specialisation identifier |
| name             | VARCHAR(255) | NOT NULL    | Specialisation name              |
| description      | TEXT         |             | Detailed description             |
| year             | INT          |             | Academic year                    |
| credits_required | INT          |             | Credits needed                   |

**Relationships**:

- `OneToMany` with Student

### 8. Departments Entity

**Table Name**: `departments`

| Column   | Type         | Constraints      | Description                  |
| -------- | ------------ | ---------------- | ---------------------------- |
| id       | BIGINT       | PRIMARY KEY      | Unique department identifier |
| name     | VARCHAR(255) | UNIQUE, NOT NULL | Department name              |
| capacity | INT          |                  | Department capacity          |

**Relationships**:

- Referenced by Employee.department (String field)

## Database Indexes

### Performance Optimization Indexes

```sql
-- Primary indexes (automatically created)
CREATE UNIQUE INDEX idx_students_email ON students(email);
CREATE UNIQUE INDEX idx_students_rollno ON students(rollno);
CREATE UNIQUE INDEX idx_employee_email ON employee(email);
CREATE UNIQUE INDEX idx_departments_name ON departments(name);

-- Search optimization indexes
CREATE INDEX idx_students_graduation_year ON students(graduation_year);
CREATE INDEX idx_students_domain ON students(domain);
CREATE INDEX idx_students_specialisation ON students(specialisation);
CREATE INDEX idx_placement_org ON placement(org);
CREATE INDEX idx_placement_student_sid ON placement_student(sid);
CREATE INDEX idx_placement_student_place_id ON placement_student(place_id);
CREATE INDEX idx_alumni_sid ON alumni(sid);

-- Composite indexes for complex queries
CREATE INDEX idx_students_domain_grad_year ON students(domain, graduation_year);
CREATE INDEX idx_placement_student_composite ON placement_student(place_id, sid);
```

## Complex Query Examples

### 1. Student Search Query (Used in StudentRepo)

```sql
SELECT
    s.first_name, s.last_name, d.program, p.org, ao.org, s.graduation_year,
    CASE
        WHEN EXISTS(SELECT 1 FROM alumni a WHERE s.id = a.sid) THEN 'Yes'
        ELSE 'No'
    END as isAlumni,
    CASE
        WHEN EXISTS(SELECT 1 FROM placement_student ps WHERE ps.sid = s.id) THEN 'Placed'
        ELSE 'Unplaced'
    END as is_placed
FROM students s
JOIN domains d ON s.domain = d.id
LEFT JOIN placement_student ps ON ps.sid = s.id
LEFT JOIN placement p ON p.id = ps.place_Id
LEFT JOIN alumni a ON a.sid = s.id
LEFT JOIN alumni_org ao ON a.id = ao.alumni_id
WHERE
    (LOWER(p.org) LIKE CONCAT('%', LOWER(?), '%')) OR
    (LOWER(ao.org) LIKE CONCAT('%', LOWER(?), '%')) OR
    (s.graduation_year = ?) OR
    (LOWER(d.program) LIKE CONCAT('%', LOWER(?), '%'))
ORDER BY s.first_name;
```

### 2. Student List with Placement Status

```sql
SELECT
    s.id, s.first_name, s.last_name, s.email,
    CASE
        WHEN EXISTS (SELECT p FROM placement_student p WHERE p.sid = s.id)
        THEN 'Placed'
        ELSE 'Unplaced'
    END AS placementStatus
FROM students s;
```

## Data Migration Scripts

### Initial Data Setup

```sql
-- Insert sample departments
INSERT INTO departments (id, name, capacity) VALUES
(1, 'Computer Science', 100),
(2, 'Information Technology', 80),
(3, 'Electronics', 60);

-- Insert sample domains
INSERT INTO domains (id, program, batch, capacity, qualification) VALUES
(1, 'Master of Technology', '2024', 50, 'BE/BTech'),
(2, 'Master of Science', '2024', 40, 'BSc/BTech'),
(3, 'PhD', '2024', 20, 'MTech/MSc');

-- Insert sample specialisations
INSERT INTO specialisation (id, name, description, year, credits_required) VALUES
(1, 'Artificial Intelligence', 'AI and Machine Learning', 2024, 64),
(2, 'Cyber Security', 'Information Security', 2024, 64),
(3, 'Data Science', 'Big Data Analytics', 2024, 64);
```

## Database Constraints & Triggers

### Foreign Key Constraints

```sql
-- Student foreign keys
ALTER TABLE students
ADD CONSTRAINT fk_student_domain
FOREIGN KEY (domain) REFERENCES domains(id);

ALTER TABLE students
ADD CONSTRAINT fk_student_specialisation
FOREIGN KEY (specialisation) REFERENCES specialisation(id);

-- Placement Student foreign keys
ALTER TABLE placement_student
ADD CONSTRAINT fk_ps_placement
FOREIGN KEY (place_id) REFERENCES placement(id);

ALTER TABLE placement_student
ADD CONSTRAINT fk_ps_student
FOREIGN KEY (sid) REFERENCES students(id);

-- Alumni foreign keys
ALTER TABLE alumni
ADD CONSTRAINT fk_alumni_student
FOREIGN KEY (sid) REFERENCES students(id);
```

### Data Validation Triggers

```sql
-- Validate CGPA range
DELIMITER $$
CREATE TRIGGER validate_cgpa
BEFORE INSERT ON students
FOR EACH ROW
BEGIN
    IF NEW.cgpa < 0 OR NEW.cgpa > 100 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CGPA must be between 0 and 100';
    END IF;
END$$
DELIMITER ;

-- Validate graduation year
DELIMITER $$
CREATE TRIGGER validate_graduation_year
BEFORE INSERT ON students
FOR EACH ROW
BEGIN
    IF NEW.graduation_year < 2000 OR NEW.graduation_year > YEAR(CURDATE()) + 5 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid graduation year';
    END IF;
END$$
DELIMITER ;
```

## Performance Considerations

### Query Optimization Tips

1. **Use appropriate indexes** for frequently queried columns
2. **Limit result sets** using pagination
3. **Use EXPLAIN** to analyze query performance
4. **Avoid N+1 queries** with proper JPA fetch strategies
5. **Use native queries** for complex joins

### Connection Pool Configuration

```properties
# HikariCP settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1200000
spring.datasource.hikari.connection-timeout=20000
```

### Database Monitoring

```sql
-- Monitor slow queries
SHOW PROCESSLIST;

-- Check table sizes
SELECT
    table_name,
    round(((data_length + index_length) / 1024 / 1024), 2) AS "DB Size in MB"
FROM information_schema.tables
WHERE table_schema = "placement_db"
ORDER BY (data_length + index_length) DESC;

-- Index usage statistics
SELECT
    TABLE_SCHEMA,
    TABLE_NAME,
    INDEX_NAME,
    SEQ_IN_INDEX,
    COLUMN_NAME
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = 'placement_db';
```

This database design ensures data integrity, performance, and scalability for the placement management system.
