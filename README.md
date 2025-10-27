# Sample Spring Boot Application - User Management API

A RESTful API for User CRUD operations built with Spring Boot, JPA, and MySQL.

## Project Structure

```
src/main/java/org/example/samplespringboot/
├── controller/          # REST API Controllers
│   └── UserController.java
├── service/            # Business Logic Layer
│   ├── UserService.java
│   └── impl/
│       └── UserServiceImpl.java
├── repository/         # Data Access Layer
│   └── UserRepository.java
├── entity/            # JPA Entities
│   └── User.java
├── dto/               # Data Transfer Objects
│   ├── UserRequestDTO.java
│   └── UserResponseDTO.java
├── exception/         # Custom Exceptions & Error Handling
│   ├── ResourceNotFoundException.java
│   ├── DuplicateResourceException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
└── SampleSpringBootApplication.java
```

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Database Setup

1. Install MySQL Server
2. Create a database named `userdb` (or it will be created automatically)
3. Update database credentials in `application.properties`:
   - Default username: `root`
   - Default password: `root`

## Configuration

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/userdb?createDatabaseIfNotExist=true
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

## Running the Application

```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/Sample-SpringBoot-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Base URL: `/api/users`

### 1. Create User
**POST** `/api/users`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St, City, Country"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St, City, Country",
  "createdAt": "2025-10-27T10:30:00",
  "updatedAt": "2025-10-27T10:30:00"
}
```

### 2. Get All Users
**GET** `/api/users`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St, City, Country",
    "createdAt": "2025-10-27T10:30:00",
    "updatedAt": "2025-10-27T10:30:00"
  }
]
```

### 3. Get User by ID
**GET** `/api/users/{id}`

**Response:** `200 OK`
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "1234567890",
  "address": "123 Main St, City, Country",
  "createdAt": "2025-10-27T10:30:00",
  "updatedAt": "2025-10-27T10:30:00"
}
```

### 4. Get User by Email
**GET** `/api/users/email/{email}`

**Response:** `200 OK`

### 5. Update User
**PUT** `/api/users/{id}`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "phoneNumber": "9876543210",
  "address": "456 New St, City, Country"
}
```

**Response:** `200 OK`

### 6. Delete User
**DELETE** `/api/users/{id}`

**Response:** `204 No Content`

## Testing with cURL

### Create a user:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "1234567890",
    "address": "123 Main St"
  }'
```

### Get all users:
```bash
curl http://localhost:8080/api/users
```

### Get user by ID:
```bash
curl http://localhost:8080/api/users/1
```

### Update user:
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane.doe@example.com",
    "phoneNumber": "9876543210",
    "address": "456 New St"
  }'
```

### Delete user:
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

## Error Responses

### 404 Not Found
```json
{
  "timestamp": "2025-10-27T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 1",
  "path": "/api/users/1"
}
```

### 409 Conflict (Duplicate Email)
```json
{
  "timestamp": "2025-10-27T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists: john.doe@example.com",
  "path": "/api/users"
}
```

### 400 Bad Request (Validation Error)
```json
{
  "timestamp": "2025-10-27T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/users",
  "validationErrors": {
    "email": "Email should be valid",
    "firstName": "First name is required"
  }
}
```

## Features

- ✅ RESTful API design
- ✅ CRUD operations for User entity
- ✅ MySQL database integration with JPA/Hibernate
- ✅ DTO pattern for request/response
- ✅ Service layer for business logic
- ✅ Repository pattern for data access
- ✅ Global exception handling
- ✅ Input validation
- ✅ Automatic timestamps (created_at, updated_at)
- ✅ Duplicate email prevention

## Technologies Used

- **Spring Boot 3.5.7** - Application framework
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **MySQL** - Database
- **Lombok** - Reduces boilerplate code
- **Jakarta Validation** - Input validation
- **Maven** - Dependency management

## Database Schema

### users table
| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| first_name | VARCHAR(100) | NOT NULL |
| last_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(150) | NOT NULL, UNIQUE |
| phone_number | VARCHAR(20) | |
| address | VARCHAR(255) | |
| created_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

## License

This project is open source and available under the MIT License.

