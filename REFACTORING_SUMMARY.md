# API Refactoring Summary

## Overview
The application has been refactored to support an **instructor-only course management system** with the following capabilities:
- User registration and authentication
- Profile management
- Course creation with file uploads
- Course viewing and updating
- Soft delete for course content

---

## Changes Made

### 1. **Entity Changes**

#### CourseContent.java
- **Added soft delete support:**
  - `isDeleted` (Boolean, default: false)
  - `deletedAt` (LocalDateTime)
- These fields allow marking content as deleted without physical removal from the database.

---

### 2. **DTO Changes**

#### CourseResponseDTO.java
- **Added field:**
  - `contents` (List<CourseContentResponseDTO>)
- This allows returning course details along with all associated content in a single response.

#### New DTO: CourseWithContentRequestDTO.java
- Created for future extensibility (currently uses CourseRequestDTO).

---

### 3. **Repository Changes**

#### CourseContentRepository.java
- **Added methods:**
  - `findByCourseIdAndIsDeletedFalse(Long courseId)` - Get non-deleted content by course
  - `findByUploadedByIdAndIsDeletedFalse(Long userId)` - Get non-deleted content by user

---

### 4. **Service Changes**

#### CourseService.java (Interface)
- **Added methods:**
  - `createCourseWithContent(CourseRequestDTO, List<MultipartFile>)` - Create course with files
  - `getCourseByIdWithContents(Long id)` - Get course including content list
  - `updateCourseWithContent(Long id, CourseRequestDTO, List<MultipartFile>)` - Update course and add files

#### CourseServiceImpl.java
- **Implemented new methods:**
  - `createCourseWithContent()` - Creates course, uploads files, returns course with content
  - `getCourseByIdWithContents()` - Fetches course and populates content list
  - `updateCourseWithContent()` - Updates course, uploads new files, returns updated course with content
- **Added dependency:** `CourseContentService` for handling file operations

#### CourseContentServiceImpl.java
- **Modified methods:**
  - `getContentsByCourseId()` - Now filters out soft-deleted content
  - `getContentsByUserId()` - Now filters out soft-deleted content
  - `deleteContent()` - Changed from hard delete to soft delete (sets isDeleted=true, deletedAt=now)

---

### 5. **Controller Changes**

#### AuthController.java
- **Kept endpoints:**
  - `POST /api/auth/register` - Register new instructor
  - `POST /api/auth/login` - Login
  - `POST /api/auth/refresh` - Refresh access token
  - `POST /api/auth/logout` - Logout and invalidate refresh token
- All endpoints remain unchanged with proper documentation

#### UserController.java
- **Removed endpoints:**
  - All admin-only endpoints (create user, get all users, get by ID, get by email, update by ID, delete by ID)
- **Kept endpoints:**
  - `GET /api/users/me` - Get current user profile
  - `PUT /api/users/me` - Update current user profile

#### CourseController.java
- **Modified endpoints:**
  - `POST /api/courses` - Now accepts multipart/form-data with course JSON and optional files
  - `GET /api/courses/{id}` - Now returns course with all non-deleted content
  - `PUT /api/courses/{id}` - Now accepts multipart/form-data for updating course and adding files
- **Removed endpoints:**
  - `GET /api/courses` - Get all courses (removed)
  - `DELETE /api/courses/{id}` - Delete course (removed, as per requirements)
- **Kept endpoints:**
  - `GET /api/courses/instructor/{instructorId}` - Get all courses by instructor

#### CourseContentController.java
- **Removed endpoints:**
  - `POST /api/course-content/upload` - Upload moved to course creation/update
  - `GET /api/course-content/{id}` - Get content by ID
  - `GET /api/course-content/course/{courseId}` - Get contents by course (now included in course detail)
  - `GET /api/course-content/user/{userId}` - Get contents by user
  - `GET /api/course-content/download/{id}` - Download file
- **Kept endpoints:**
  - `DELETE /api/course-content/{id}` - Soft delete course content

---

## API Endpoints Summary

### Authentication (4 endpoints)
1. `POST /api/auth/register` - Register instructor
2. `POST /api/auth/login` - Login
3. `POST /api/auth/refresh` - Refresh token
4. `POST /api/auth/logout` - Logout

### User Profile (2 endpoints)
5. `GET /api/users/me` - Get profile
6. `PUT /api/users/me` - Update profile

### Courses (4 endpoints)
7. `POST /api/courses` - Create course with content (multipart)
8. `GET /api/courses/{id}` - Get single course with content
9. `GET /api/courses/instructor/{instructorId}` - Get all instructor's courses
10. `PUT /api/courses/{id}` - Update course with content (multipart)

### Course Content (1 endpoint)
11. `DELETE /api/course-content/{id}` - Soft delete content

**Total: 11 API endpoints**

---

## Database Schema Updates Required

When running the application, the following schema changes will be auto-applied (if using Hibernate auto-update):

### course_content table
```sql
ALTER TABLE course_content 
ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE NOT NULL,
ADD COLUMN deleted_at TIMESTAMP NULL;
```

If using manual schema management, run this migration before deploying.

---

## Key Features

### ✅ Implemented
1. **User Registration** - Instructors can register
2. **User Login** - JWT-based authentication
3. **Profile Management** - View and edit own profile
4. **Course Creation** - Create course with multiple file uploads
5. **Course Viewing** - View single course with all content, view all instructor courses
6. **Course Update** - Update course details and add new content
7. **Content Soft Delete** - Mark content as deleted without physical removal

### 🔒 Security
- All endpoints except register/login require JWT authentication
- Users can only access/modify their own profile
- Course operations are scoped to authenticated instructors

### 📁 File Handling
- Supports PDF, MP4, JPG, JPEG, PNG
- Maximum file size: 10 MB
- Files stored with unique names to prevent conflicts
- Multiple files can be uploaded during course creation/update

---

## Testing the APIs

Use the provided `test-api.bat` or `test-jwt-api.bat` scripts, or use tools like:
- Postman
- cURL
- Thunder Client (VS Code)
- Insomnia

Refer to `API_DOCUMENTATION.md` for detailed endpoint documentation with request/response examples.

---

## Migration Notes

1. **Database:** Ensure the `is_deleted` and `deleted_at` columns are added to `course_content` table
2. **File Storage:** Ensure the upload directory has proper write permissions
3. **Configuration:** Check `application.properties` for file upload settings
4. **Testing:** Test multipart/form-data endpoints with proper content-type headers

---

## Future Enhancements (Optional)

1. Add download endpoint for course content
2. Add pagination for course listings
3. Add search/filter functionality for courses
4. Add course content ordering/sorting
5. Add file preview functionality
6. Add bulk operations for content management
7. Add course analytics/statistics

