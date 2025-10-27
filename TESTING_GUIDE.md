# Quick Testing Guide

This guide provides step-by-step instructions to test all the APIs using cURL commands.

## Prerequisites
- Application running on `http://localhost:8080`
- cURL installed on your system
- Sample files for upload (test.pdf, lecture.mp4, etc.)

---

## 1. Register a New Instructor

```bash
curl -X POST http://localhost:8080/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"SecurePass123\"}"
```

**Expected Response:** 201 Created with access token and user details.

**Save the `accessToken` from the response for subsequent requests.**

---

## 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"john.doe@example.com\",\"password\":\"SecurePass123\"}"
```

**Expected Response:** 200 OK with access token and refresh token.

---

## 3. Get Current User Profile

Replace `YOUR_ACCESS_TOKEN` with the token from login response.

```bash
curl -X GET http://localhost:8080/api/users/me ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response:** 200 OK with user profile details.

---

## 4. Update User Profile

```bash
curl -X PUT http://localhost:8080/api/users/me ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John Updated\",\"lastName\":\"Doe Updated\",\"email\":\"john.updated@example.com\"}"
```

**Expected Response:** 200 OK with updated user profile.

---

## 5. Create Course (Without Files)

Replace `INSTRUCTOR_ID` with your user ID from profile response.

```bash
curl -X POST http://localhost:8080/api/courses ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" ^
  -F "course={\"courseName\":\"Introduction to Java\",\"courseCode\":\"CS101\",\"description\":\"Learn Java basics\",\"instructorId\":INSTRUCTOR_ID};type=application/json"
```

**Expected Response:** 201 Created with course details (contents array will be empty).

---

## 6. Create Course With Files

Prepare sample files in your directory (e.g., `C:\temp\syllabus.pdf`).

```bash
curl -X POST http://localhost:8080/api/courses ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" ^
  -F "course={\"courseName\":\"Advanced Java\",\"courseCode\":\"CS201\",\"description\":\"Advanced concepts\",\"instructorId\":INSTRUCTOR_ID};type=application/json" ^
  -F "files=@C:\temp\syllabus.pdf" ^
  -F "files=@C:\temp\lecture1.mp4"
```

**Expected Response:** 201 Created with course details and uploaded files in contents array.

---

## 7. Get Single Course with Content

Replace `COURSE_ID` with the ID from the create response.

```bash
curl -X GET http://localhost:8080/api/courses/COURSE_ID ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response:** 200 OK with course details and all non-deleted content.

---

## 8. Get All Instructor's Courses

Replace `INSTRUCTOR_ID` with your user ID.

```bash
curl -X GET http://localhost:8080/api/courses/instructor/INSTRUCTOR_ID ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response:** 200 OK with array of courses (contents field will be null in list view).

---

## 9. Update Course (Without Files)

```bash
curl -X PUT http://localhost:8080/api/courses/COURSE_ID ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" ^
  -F "course={\"courseName\":\"Intro to Java - Updated\",\"courseCode\":\"CS101\",\"description\":\"Updated description\",\"instructorId\":INSTRUCTOR_ID};type=application/json"
```

**Expected Response:** 200 OK with updated course details.

---

## 10. Update Course With New Files

```bash
curl -X PUT http://localhost:8080/api/courses/COURSE_ID ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" ^
  -F "course={\"courseName\":\"Intro to Java - Updated\",\"courseCode\":\"CS101\",\"description\":\"Updated with new files\",\"instructorId\":INSTRUCTOR_ID};type=application/json" ^
  -F "files=@C:\temp\assignment1.pdf" ^
  -F "files=@C:\temp\lecture2.mp4"
```

**Expected Response:** 200 OK with updated course and all content (old + new).

---

## 11. Soft Delete Course Content

Replace `CONTENT_ID` with the ID of a course content item.

```bash
curl -X DELETE http://localhost:8080/api/course-content/CONTENT_ID ^
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response:** 204 No Content

**Verify:** Get the course again (step 7) and the deleted content should not appear.

---

## 12. Refresh Access Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh ^
  -H "Content-Type: application/json" ^
  -d "{\"refreshToken\":\"YOUR_REFRESH_TOKEN\"}"
```

**Expected Response:** 200 OK with new access token and refresh token.

---

## 13. Logout

```bash
curl -X POST http://localhost:8080/api/auth/logout ^
  -H "Content-Type: application/json" ^
  -d "{\"refreshToken\":\"YOUR_REFRESH_TOKEN\"}"
```

**Expected Response:** 204 No Content

---

## Testing Workflow Example

1. **Register** → Save access token
2. **Get Profile** → Note your user ID
3. **Create Course with Files** → Note course ID
4. **Get Single Course** → Verify files are there
5. **Update Course with More Files** → Add more content
6. **Get Single Course** → Verify all files
7. **Soft Delete One Content** → Delete one file
8. **Get Single Course** → Verify deleted file is not shown
9. **Get All Instructor Courses** → See all your courses
10. **Update Profile** → Change your details
11. **Logout** → End session

---

## PowerShell Version (Windows)

If you're using PowerShell instead of CMD, use these formats:

### Register (PowerShell)
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" `
  -Method POST `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"firstName":"John","lastName":"Doe","email":"john.doe@example.com","password":"SecurePass123"}'
```

### Create Course with Files (PowerShell)
```powershell
$form = @{
    course = Get-Content -Raw -Path course.json
    files = Get-Item -Path "C:\temp\syllabus.pdf"
}
Invoke-WebRequest -Uri "http://localhost:8080/api/courses" `
  -Method POST `
  -Headers @{"Authorization"="Bearer YOUR_ACCESS_TOKEN"} `
  -Form $form
```

---

## Postman Collection

For easier testing, you can import the API documentation into Postman:

1. Open Postman
2. Click "Import"
3. Create requests based on the API_DOCUMENTATION.md file
4. Set up environment variables:
   - `baseUrl`: http://localhost:8080/api
   - `accessToken`: (set after login)
   - `instructorId`: (set after getting profile)
   - `courseId`: (set after creating course)

---

## Common Issues

### 1. 401 Unauthorized
- Check if access token is valid
- Token might have expired (use refresh endpoint)
- Token must be in format: `Bearer YOUR_TOKEN`

### 2. 413 Payload Too Large
- File size exceeds 10 MB limit
- Use smaller files or adjust `spring.servlet.multipart.max-file-size` in application.properties

### 3. 415 Unsupported Media Type
- Only PDF, MP4, JPG, JPEG, PNG allowed
- Check file extension and content type

### 4. 409 Conflict
- Course code already exists
- Try a different course code

---

## File Preparation

Create sample test files:

**course.json:**
```json
{
  "courseName": "Test Course",
  "courseCode": "TEST101",
  "description": "Test course description",
  "instructorId": 1
}
```

**Dummy Files:**
- Create a test.pdf (any small PDF)
- Create a test.mp4 (any small video)
- Create a test.jpg (any image)

Place them in `C:\temp\` or adjust paths in commands.

