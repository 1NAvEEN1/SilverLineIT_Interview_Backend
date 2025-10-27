# Quick Start Guide

## Setup Instructions

### 1. Prerequisites
- Install Java 17 or higher
- Install MySQL 8.0+
- Ensure MySQL server is running

### 2. Database Configuration
Update `src/main/resources/application.properties` with your MySQL credentials:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run

#### Option A: Using Maven Wrapper (Recommended)
```bash
# On Windows
mvnw.cmd spring-boot:run

# On Linux/Mac
./mvnw spring-boot:run
```

#### Option B: Using Maven
```bash
mvn spring-boot:run
```

#### Option C: Build JAR and Run
```bash
# Build
mvnw.cmd clean package

# Run
java -jar target/Sample-SpringBoot-0.0.1-SNAPSHOT.jar
```

### 4. Verify Application is Running
Open browser and navigate to: `http://localhost:8080/api/users`

You should see an empty array `[]` if no users exist.

## Testing the API

### Using cURL

#### 1. Create a User
```bash
curl -X POST http://localhost:8080/api/users ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john@example.com\",\"phoneNumber\":\"1234567890\",\"address\":\"123 Main St\"}"
```

#### 2. Get All Users
```bash
curl http://localhost:8080/api/users
```

#### 3. Get User by ID
```bash
curl http://localhost:8080/api/users/1
```

#### 4. Update User
```bash
curl -X PUT http://localhost:8080/api/users/1 ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"email\":\"jane@example.com\",\"phoneNumber\":\"9876543210\",\"address\":\"456 Oak Ave\"}"
```

#### 5. Delete User
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### Using Postman
Import the `User-API-Collection.postman_collection.json` file into Postman.

### Using Browser (GET requests only)
- Get all users: http://localhost:8080/api/users
- Get user by ID: http://localhost:8080/api/users/1
- Get user by email: http://localhost:8080/api/users/email/john@example.com

## Project Structure Overview

```
Sample-SpringBoot/
├── src/
│   ├── main/
│   │   ├── java/org/example/samplespringboot/
│   │   │   ├── controller/       # REST Controllers
│   │   │   ├── service/          # Business Logic
│   │   │   ├── repository/       # Data Access
│   │   │   ├── entity/           # Database Entities
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   └── exception/        # Error Handling
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── README.md
└── database-setup.sql
```

## Common Issues

### Issue 1: Port 8080 already in use
**Solution:** Change the port in `application.properties`:
```properties
server.port=8081
```

### Issue 2: Cannot connect to MySQL
**Solution:** 
- Ensure MySQL service is running
- Verify credentials in `application.properties`
- Check if MySQL is running on port 3306

### Issue 3: Build fails
**Solution:** 
- Ensure Java 17+ is installed: `java -version`
- Clean and rebuild: `mvnw.cmd clean install`

## Next Steps

1. ✅ Application is running
2. ✅ Test API endpoints using Postman or cURL
3. ✅ Check MySQL database to see the created table
4. ✅ Add custom business logic as needed
5. ✅ Deploy to production server

## Support

For detailed API documentation, see `README.md`

