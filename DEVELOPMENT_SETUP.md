# Development Setup Guide

## Prerequisites

### System Requirements

- **Operating System**: Windows 10/11, macOS 10.14+, or Linux
- **Memory**: Minimum 4GB RAM (8GB recommended)
- **Disk Space**: At least 2GB free space
- **Network**: Internet connection for downloading dependencies

### Required Software

#### 1. Java Development Kit (JDK)

- **Version**: Java 17 or higher
- **Download**: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

**Verification**:

```bash
java -version
javac -version
```

#### 2. MySQL Database

- **Version**: MySQL 8.0 or higher
- **Download**: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)

**Alternative**: MySQL via Docker

```bash
docker run --name mysql-placement -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=placement_db -p 3306:3306 -d mysql:8.0
```

#### 3. Maven (Optional - Project includes Maven Wrapper)

- **Version**: Maven 3.6 or higher
- **Download**: [Apache Maven](https://maven.apache.org/download.cgi)

#### 4. IDE (Recommended)

- **IntelliJ IDEA**: [Download](https://www.jetbrains.com/idea/)
- **Eclipse**: [Download](https://www.eclipse.org/downloads/)
- **VS Code**: [Download](https://code.visualstudio.com/) with Java extensions

## Project Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ESD_Final_Project
```

### 2. Database Configuration

#### Create Database

```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE placement_db;

-- Create user (optional)
CREATE USER 'himanshu'@'localhost' IDENTIFIED BY 'Aryabhatta@0000';
GRANT ALL PRIVILEGES ON placement_db.* TO 'himanshu'@'localhost';
FLUSH PRIVILEGES;

-- Exit MySQL
EXIT;
```

#### Configure Connection

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/placement_db?createDatabaseIfNotExist=true
spring.datasource.username=himanshu
spring.datasource.password=Aryabhatta@0000
```

### 3. Install Dependencies

```bash
# Using Maven Wrapper (recommended)
./mvnw clean install

# Using Maven (if installed)
mvn clean install
```

### 4. Run the Application

#### Option 1: Maven Wrapper

```bash
./mvnw spring-boot:run
```

#### Option 2: IDE

- Open project in your IDE
- Run `EsdFinalProjectApplication.java`

#### Option 3: JAR File

```bash
./mvnw clean package
java -jar target/ESD_Final_Project-0.0.1-SNAPSHOT.jar
```

### 5. Verify Installation

- Application starts on: `http://localhost:8080`
- Health check: `http://localhost:8080/actuator/health`

## Development Environment Configuration

### IDE Setup

#### IntelliJ IDEA Configuration

1. **Import Project**:

   - File → Open → Select project folder
   - Choose "Import project from external model" → Maven

2. **Configure JDK**:

   - File → Project Structure → Project → Project SDK → Java 17

3. **Enable Annotation Processing**:

   - File → Settings → Build → Compiler → Annotation Processors
   - Check "Enable annotation processing"

4. **Install Lombok Plugin**:
   - File → Settings → Plugins → Search "Lombok" → Install

#### VS Code Configuration

1. **Install Extensions**:

   - Extension Pack for Java
   - Spring Boot Extension Pack
   - Lombok Annotations Support

2. **Configure Settings** (`.vscode/settings.json`):

```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "spring-boot.ls.problem.application-properties.unknown-property": "ignore"
}
```

### Environment Variables

Create `.env` file in project root:

```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/placement_db
DB_USERNAME=himanshu
DB_PASSWORD=Aryabhatta@0000

# JWT Configuration
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000

# Cache Configuration
CACHE_TTL=1800
CACHE_MAX_SIZE=1000
```

### Development Profiles

#### application-dev.properties

```properties
# Development-specific settings
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.com.himanshu.esd_final_project=DEBUG
logging.level.org.springframework.cache=DEBUG

# Hot reload
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Database pool settings for development
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
```

#### application-test.properties

```properties
# Test database (H2 in-memory)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA settings for tests
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Disable caching in tests
spring.cache.type=none
```

## Database Development Setup

### Sample Data Script

Create `src/main/resources/data.sql`:

```sql
-- Insert sample departments
INSERT INTO departments (id, name, capacity) VALUES
(1, 'Computer Science', 100),
(2, 'Information Technology', 80),
(3, 'Electronics', 60);

-- Insert sample domains
INSERT INTO domains (id, program, batch, capacity, qualification) VALUES
(1, 'Master of Technology', '2024', 50, 'BE/BTech'),
(2, 'Master of Science', '2024', 40, 'BSc/BTech');

-- Insert sample specialisations
INSERT INTO specialisation (id, name, description, year, credits_required) VALUES
(1, 'Artificial Intelligence', 'AI and Machine Learning', 2024, 64),
(2, 'Cyber Security', 'Information Security', 2024, 64);

-- Insert sample employee
INSERT INTO employee (id, first_name, last_name, title, email, password, department) VALUES
(1, 'Admin', 'User', 'Administrator', 'admin@university.edu', '$2a$10$encrypted_password_here', 'Computer Science');

-- Insert sample students
INSERT INTO students (id, first_name, last_name, email, rollno, cgpa, graduation_year, total_credits, org, domain, specialisation) VALUES
(1, 'John', 'Doe', 'john.doe@student.edu', 'STU001', 85, 2024, 120.0, 'TechCorp', 1, 1),
(2, 'Jane', 'Smith', 'jane.smith@student.edu', 'STU002', 90, 2024, 125.0, 'DataCorp', 1, 2);
```

### Database Migration Scripts

Create `src/main/resources/db/migration/`:

```
V1__Create_initial_schema.sql
V2__Insert_sample_data.sql
V3__Add_indexes.sql
```

### Database Tools

1. **MySQL Workbench**: GUI for database management
2. **DBeaver**: Universal database tool
3. **phpMyAdmin**: Web-based MySQL administration

## Development Workflow

### Git Configuration

```bash
# Configure Git
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Set up Git hooks (optional)
cp .githooks/* .git/hooks/
chmod +x .git/hooks/*
```

### Branch Strategy

```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "feat: add new feature"

# Push to remote
git push origin feature/your-feature-name
```

### Code Style Configuration

#### .editorconfig

```ini
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

[*.java]
indent_style = space
indent_size = 4

[*.{yml,yaml}]
indent_style = space
indent_size = 2

[*.properties]
indent_style = space
indent_size = 4
```

#### Checkstyle Configuration (checkstyle.xml)

```xml
<?xml version="1.0"?>
<!DOCTYPE module PUBLIC
    "-//Checkstyle//DTD Checkstyle Configuration 1.3//EN"
    "https://checkstyle.org/dtds/configuration_1_3.dtd">
<module name="Checker">
    <module name="TreeWalker">
        <module name="UnusedImports"/>
        <module name="RedundantImport"/>
        <module name="ImportOrder"/>
        <module name="NeedBraces"/>
        <module name="LeftCurly"/>
        <module name="RightCurly"/>
    </module>
</module>
```

## Testing Setup

### Unit Testing Dependencies

Already included in `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Test Configuration

Create `src/test/resources/application-test.properties`:

```properties
# Test-specific configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.jpa.hibernate.ddl-auto=create-drop
logging.level.org.springframework.web=DEBUG
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=StudentServiceTest

# Run with coverage
./mvnw test jacoco:report
```

## Debugging Configuration

### Application Debugging

```bash
# Debug mode with specific port
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### IDE Debug Configuration

**IntelliJ IDEA**:

1. Run → Edit Configurations
2. Add → Remote JVM Debug
3. Host: localhost, Port: 5005

### Logging Configuration

Add to `application-dev.properties`:

```properties
# Root logging level
logging.level.root=INFO

# Package-specific logging
logging.level.com.himanshu.esd_final_project=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.cache=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Log pattern
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## Docker Development Setup

### Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/ESD_Final_Project-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
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
    volumes:
      - ./logs:/app/logs

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=placement_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d

volumes:
  mysql_data:
```

### Development with Docker

```bash
# Build and run
docker-compose up --build

# Run in background
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

## Performance Monitoring Setup

### Actuator Configuration

Add to `application.properties`:

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=health,metrics,caches,info
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

### JVM Monitoring

```bash
# Enable JVM metrics
-Dmanagement.metrics.export.jvm.enabled=true

# Remote monitoring
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=9999 \
-Dcom.sun.management.jmxremote.authenticate=false \
-Dcom.sun.management.jmxremote.ssl=false
```

## Troubleshooting

### Common Issues

#### 1. Port Already in Use

```bash
# Find process using port 8080
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # macOS/Linux

# Kill process
taskkill /PID <PID> /F        # Windows
kill -9 <PID>                 # macOS/Linux
```

#### 2. Database Connection Issues

```bash
# Test database connection
mysql -h localhost -u himanshu -p placement_db

# Check if MySQL is running
brew services list | grep mysql    # macOS
systemctl status mysql             # Linux
net start mysql                    # Windows
```

#### 3. Maven Dependencies Issues

```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository

# Reimport dependencies
./mvnw clean install -U
```

#### 4. Lombok Issues

- Ensure Lombok plugin is installed in IDE
- Enable annotation processing
- Restart IDE after installation

### Debug Logs

Enable debug logging in `application-dev.properties`:

```properties
logging.level.com.himanshu.esd_final_project=DEBUG
logging.level.org.springframework.boot.autoconfigure=DEBUG
```

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database health
curl http://localhost:8080/actuator/health/db

# Cache health
curl http://localhost:8080/actuator/caches
```

This development setup guide provides everything needed to get the project running in a development environment with proper tooling and configuration.
