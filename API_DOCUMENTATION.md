# API Documentation

## Overview
This API is designed for an instructor-based course management system where instructors can register, login, manage their profile, create courses with content, and manage course materials.

## Base URL
```
http://localhost:8080/api
```

---

## Authentication APIs

### 1. Register User (Instructor)
Register a new instructor account.

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

**Response:** `201 Created`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "roles": ["ROLE_INSTRUCTOR"]
  }
}
```

---

### 2. Login
Authenticate and receive access tokens.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePassword123"
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "roles": ["ROLE_INSTRUCTOR"]
  }
}
```

---

### 3. Refresh Token
Get a new access token using refresh token.

**Endpoint:** `POST /api/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

### 4. Logout
Invalidate refresh token.

**Endpoint:** `POST /api/auth/logout`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `204 No Content`

---

## User Profile APIs

### 5. Get Current User Profile
Get the authenticated user's profile.

**Endpoint:** `GET /api/users/me`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "roles": ["ROLE_INSTRUCTOR"],
  "createdAt": "2025-10-27T10:00:00",
  "updatedAt": "2025-10-27T10:00:00"
}
```

---

### 6. Update Current User Profile
Update the authenticated user's profile.

**Endpoint:** `PUT /api/users/me`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Request Body:**
```json
{
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "email": "john.updated@example.com"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "firstName": "John Updated",
  "lastName": "Doe Updated",
  "email": "john.updated@example.com",
  "roles": ["ROLE_INSTRUCTOR"],
  "createdAt": "2025-10-27T10:00:00",
  "updatedAt": "2025-10-27T11:30:00"
}
```

---

## Course APIs

### 7. Create Course with Content
Create a new course and optionally upload course materials.

**Endpoint:** `POST /api/courses`

**Headers:**
```
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

**Request Body (multipart/form-data):**
- `course` (JSON):
```json
{
  "courseName": "Introduction to Java",
  "courseCode": "CS101",
  "description": "Learn the basics of Java programming",
  "instructorId": 1
}
```
- `files` (optional): Array of files (PDF, MP4, JPG, JPEG, PNG)

**Response:** `201 Created`
```json
{
  "id": 1,
  "courseName": "Introduction to Java",
  "courseCode": "CS101",
  "description": "Learn the basics of Java programming",
  "instructorId": 1,
  "instructorName": "John Doe",
  "createdAt": "2025-10-27T12:00:00",
  "updatedAt": "2025-10-27T12:00:00",
  "contents": [
    {
      "id": 1,
      "fileName": "syllabus.pdf",
      "fileType": "application/pdf",
      "fileSize": 102400,
      "uploadDate": "2025-10-27T12:00:00",
      "fileUrl": "1/syllabus_1730044800000.pdf",
      "courseId": 1,
      "courseName": "Introduction to Java",
      "uploadedByUserId": 1,
      "uploadedByUserName": "John Doe"
    }
  ]
}
```

---

### 8. Get Single Course with Content
Get course details including all non-deleted course content.

**Endpoint:** `GET /api/courses/{id}`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "courseName": "Introduction to Java",
  "courseCode": "CS101",
  "description": "Learn the basics of Java programming",
  "instructorId": 1,
  "instructorName": "John Doe",
  "createdAt": "2025-10-27T12:00:00",
  "updatedAt": "2025-10-27T12:00:00",
  "contents": [
    {
      "id": 1,
      "fileName": "syllabus.pdf",
      "fileType": "application/pdf",
      "fileSize": 102400,
      "uploadDate": "2025-10-27T12:00:00",
      "fileUrl": "1/syllabus_1730044800000.pdf",
      "courseId": 1,
      "courseName": "Introduction to Java",
      "uploadedByUserId": 1,
      "uploadedByUserName": "John Doe"
    }
  ]
}
```

---

### 9. Get All Courses by Instructor
Get all courses created by a specific instructor.

**Endpoint:** `GET /api/courses/instructor/{instructorId}`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "courseName": "Introduction to Java",
    "courseCode": "CS101",
    "description": "Learn the basics of Java programming",
    "instructorId": 1,
    "instructorName": "John Doe",
    "createdAt": "2025-10-27T12:00:00",
    "updatedAt": "2025-10-27T12:00:00",
    "contents": null
  },
  {
    "id": 2,
    "courseName": "Advanced Java",
    "courseCode": "CS201",
    "description": "Advanced Java programming concepts",
    "instructorId": 1,
    "instructorName": "John Doe",
    "createdAt": "2025-10-27T13:00:00",
    "updatedAt": "2025-10-27T13:00:00",
    "contents": null
  }
]
```

---

### 10. Update Course with Content
Update course details and optionally add new course materials.

**Endpoint:** `PUT /api/courses/{id}`

**Headers:**
```
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
```

**Request Body (multipart/form-data):**
- `course` (JSON):
```json
{
  "courseName": "Introduction to Java - Updated",
  "courseCode": "CS101",
  "description": "Updated description",
  "instructorId": 1
}
```
- `files` (optional): Array of new files to add

**Response:** `200 OK`
```json
{
  "id": 1,
  "courseName": "Introduction to Java - Updated",
  "courseCode": "CS101",
  "description": "Updated description",
  "instructorId": 1,
  "instructorName": "John Doe",
  "createdAt": "2025-10-27T12:00:00",
  "updatedAt": "2025-10-27T14:00:00",
  "contents": [
    {
      "id": 1,
      "fileName": "syllabus.pdf",
      "fileType": "application/pdf",
      "fileSize": 102400,
      "uploadDate": "2025-10-27T12:00:00",
      "fileUrl": "1/syllabus_1730044800000.pdf",
      "courseId": 1,
      "courseName": "Introduction to Java - Updated",
      "uploadedByUserId": 1,
      "uploadedByUserName": "John Doe"
    },
    {
      "id": 2,
      "fileName": "lecture1.mp4",
      "fileType": "video/mp4",
      "fileSize": 10485760,
      "uploadDate": "2025-10-27T14:00:00",
      "fileUrl": "1/lecture1_1730051600000.mp4",
      "courseId": 1,
      "courseName": "Introduction to Java - Updated",
      "uploadedByUserId": 1,
      "uploadedByUserName": "John Doe"
    }
  ]
}
```

---

## Course Content APIs

### 11. Soft Delete Course Content
Soft delete (mark as deleted) course content. The content will be excluded from course content lists.

**Endpoint:** `DELETE /api/course-content/{id}`

**Headers:**
```
Authorization: Bearer {accessToken}
```

**Response:** `204 No Content`

---

## Error Responses

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-10-27T12:00:00",
  "errors": {
    "courseName": "Course name is required",
    "courseCode": "Course code is required"
  }
}
```

### 401 Unauthorized
```json
{
  "status": 401,
  "message": "Unauthorized - Invalid or missing token",
  "timestamp": "2025-10-27T12:00:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Course not found with id: 999",
  "timestamp": "2025-10-27T12:00:00"
}
```

### 409 Conflict
```json
{
  "status": 409,
  "message": "Course with code CS101 already exists",
  "timestamp": "2025-10-27T12:00:00"
}
```

### 413 Payload Too Large
```json
{
  "status": 413,
  "message": "File size exceeds maximum limit of 10 MB",
  "timestamp": "2025-10-27T12:00:00"
}
```

### 415 Unsupported Media Type
```json
{
  "status": 415,
  "message": "Invalid file type. Only PDF, MP4, JPG, JPEG, and PNG files are allowed",
  "timestamp": "2025-10-27T12:00:00"
}
```

---

## File Upload Constraints

- **Maximum file size:** 10 MB
- **Allowed file types:**
  - PDF (application/pdf)
  - MP4 (video/mp4)
  - JPG/JPEG (image/jpeg, image/jpg)
  - PNG (image/png)

---

## Notes

1. All protected endpoints require a valid JWT access token in the Authorization header.
2. Access tokens expire after 1 hour (3600 seconds).
3. Use the refresh token endpoint to get a new access token without re-authenticating.
4. Course content is soft-deleted, meaning it's marked as deleted but not physically removed from the database.
5. When retrieving courses with content, only non-deleted content is returned.
6. The `instructorId` in course create/update requests should match the authenticated user's ID for security.

