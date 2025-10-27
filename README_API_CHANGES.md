# Course Management API - Instructor Edition

## 🎯 Overview

This is a Spring Boot application for **instructor-based course management**. Instructors can register, manage their profile, create courses with multimedia content, and manage course materials.

## ✨ Key Features

- ✅ **User Registration & Authentication** (JWT-based)
- ✅ **Profile Management** (View and edit own profile)
- ✅ **Course Creation** with multiple file uploads
- ✅ **Course Management** (View, Update courses)
- ✅ **Content Management** (Upload, Soft-delete course materials)
- ✅ **File Support** (PDF, MP4, JPG, JPEG, PNG up to 10MB)
- ✅ **Soft Delete** for course content (preserves data)

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE course_management;

-- Run initial setup
-- Use: database-setup-jwt.sql (includes JWT tables)

-- Run migration for soft delete feature
-- Use: database-migration-soft-delete.sql
```

### 2. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/course_management
spring.datasource.username=your_username
spring.datasource.password=your_password

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=50MB
file.upload-dir=uploads
file.max-size=10485760

# JWT
jwt.secret=your-secret-key-change-this-in-production
jwt.expiration=3600000
jwt.refresh-expiration=604800000
```

### 3. Build and Run

```bash
# Clean and build
mvn clean package

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Complete API Reference
See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for detailed endpoint documentation.

### API Summary

**Total Endpoints: 11**

#### Authentication (4)
- `POST /api/auth/register` - Register new instructor
- `POST /api/auth/login` - Login
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - Logout

#### User Profile (2)
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update profile

#### Courses (4)
- `POST /api/courses` - Create course with files (multipart)
- `GET /api/courses/{id}` - Get course with all content
- `GET /api/courses/instructor/{instructorId}` - Get instructor's courses
- `PUT /api/courses/{id}` - Update course with new files (multipart)

#### Course Content (1)
- `DELETE /api/course-content/{id}` - Soft delete content

## 🧪 Testing

### Using cURL
See [TESTING_GUIDE.md](TESTING_GUIDE.md) for comprehensive cURL examples.

### Using Postman
1. Import the API endpoints from documentation
2. Set up environment variables
3. Follow the test workflow in the testing guide

### Quick Test Flow
```bash
# 1. Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"firstName":"John","lastName":"Doe","email":"john@example.com","password":"Pass123"}'

# 2. Login (save the token)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"Pass123"}'

# 3. Get Profile
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"

# 4. Create Course with File
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F 'course={"courseName":"Java 101","courseCode":"CS101","description":"Learn Java","instructorId":1};type=application/json' \
  -F 'files=@/path/to/syllabus.pdf'
```

## 📁 Project Structure

```
src/main/java/org/example/samplespringboot/
├── controller/
│   ├── AuthController.java          # Authentication endpoints
│   ├── UserController.java          # User profile endpoints
│   ├── CourseController.java        # Course management
│   └── CourseContentController.java # Content soft-delete
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── CourseService.java
│   ├── CourseContentService.java
│   └── impl/                        # Service implementations
├── repository/
│   ├── UserRepository.java
│   ├── CourseRepository.java
│   └── CourseContentRepository.java
├── entity/
│   ├── User.java
│   ├── Course.java
│   ├── CourseContent.java           # Added: isDeleted, deletedAt
│   └── UserRole.java
├── dto/
│   ├── AuthResponseDTO.java
│   ├── LoginRequestDTO.java
│   ├── RegisterRequestDTO.java
│   ├── CourseRequestDTO.java
│   ├── CourseResponseDTO.java       # Modified: Added contents list
│   └── CourseContentResponseDTO.java
├── security/                        # JWT security configuration
└── exception/                       # Global exception handling
```

## 🔐 Security

- **JWT Authentication**: All endpoints (except register/login) require valid JWT token
- **Token Expiry**: Access tokens expire in 1 hour, refresh tokens in 7 days
- **Password Encryption**: BCrypt hashing for passwords
- **Role-Based Access**: Currently supports INSTRUCTOR role

## 📤 File Upload

### Supported Formats
- **Documents**: PDF
- **Videos**: MP4
- **Images**: JPG, JPEG, PNG

### Constraints
- Maximum file size: **10 MB**
- Maximum request size: **50 MB**
- Files are stored in `uploads/` directory

### File Naming
Files are automatically renamed with timestamp to avoid conflicts:
```
uploads/{courseId}/{originalName}_{timestamp}.{extension}
```

## 🗑️ Soft Delete Feature

Course content uses **soft delete** pattern:
- Content is marked as deleted (`isDeleted = true`)
- Deletion timestamp is recorded (`deletedAt`)
- Data remains in database for audit purposes
- Soft-deleted content is automatically excluded from queries

## 🔄 Recent Changes

See [REFACTORING_SUMMARY.md](REFACTORING_SUMMARY.md) for detailed change log.

### Major Updates
1. ✅ Added soft delete to CourseContent entity
2. ✅ Integrated file upload with course create/update
3. ✅ Simplified controller endpoints (removed admin-only features)
4. ✅ Enhanced CourseResponseDTO to include content list
5. ✅ Updated repositories to filter soft-deleted content

## 🛠️ Database Schema

### Main Tables
- **users** - User accounts (instructors)
- **user_roles** - User role assignments
- **courses** - Course information
- **course_content** - Course materials (with soft delete)
- **refresh_tokens** - JWT refresh tokens

### Migration Required
Run `database-migration-soft-delete.sql` to add soft delete columns:
```sql
ALTER TABLE course_content ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE course_content ADD COLUMN deleted_at TIMESTAMP NULL;
```

## 🐛 Troubleshooting

### Common Issues

**1. File Upload Fails**
- Check `file.upload-dir` exists and has write permissions
- Verify file size is under 10 MB
- Ensure file type is supported

**2. 401 Unauthorized**
- Token might be expired - use refresh endpoint
- Ensure `Authorization: Bearer TOKEN` format is correct

**3. Database Connection Failed**
- Verify MySQL is running
- Check credentials in application.properties
- Ensure database exists

**4. MultipartException**
- Check Content-Type is `multipart/form-data`
- Verify form field names match (`course`, `files`)
- Ensure course JSON is sent with proper content type

## 📖 Additional Resources

- [API Documentation](API_DOCUMENTATION.md) - Complete API reference
- [Testing Guide](TESTING_GUIDE.md) - cURL and Postman examples
- [Refactoring Summary](REFACTORING_SUMMARY.md) - Change details
- [JWT Authentication](JWT_AUTHENTICATION_COMPLETE.md) - JWT setup guide

## 🤝 Contributing

1. Follow existing code structure and naming conventions
2. Add tests for new features
3. Update documentation for API changes
4. Use proper commit messages

## 📝 License

This project is for educational/interview purposes.

## 👨‍💻 Author

Developed as part of SilverLineIT Interview Project

## 📞 Support

For issues or questions:
1. Check the documentation files
2. Review error responses in API documentation
3. Check application logs for detailed error messages

---

**Happy Coding! 🚀**

