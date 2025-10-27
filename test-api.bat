@echo off
REM ========================================
REM Course Content Upload System - Test Script
REM ========================================

echo.
echo ========================================
echo Course Content Upload System - Testing
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo [1/5] Testing if server is running...
curl -s -o nul -w "%%{http_code}" %BASE_URL%/api/courses > temp.txt
set /p STATUS=<temp.txt
del temp.txt

if "%STATUS%"=="200" (
    echo ✓ Server is running
) else if "%STATUS%"=="000" (
    echo ✗ Server is NOT running. Please start the application first.
    echo    Run: mvnw.cmd spring-boot:run
    pause
    exit /b 1
) else (
    echo ✓ Server is responding
)

echo.
echo [2/5] Fetching all courses...
curl -s %BASE_URL%/api/courses
echo.

echo.
echo [3/5] Fetching all users...
curl -s %BASE_URL%/api/users
echo.

echo.
echo [4/5] Testing course creation...
curl -s -X POST %BASE_URL%/api/courses ^
  -H "Content-Type: application/json" ^
  -d "{\"courseName\":\"Test Course\",\"courseCode\":\"TEST101\",\"description\":\"Automated test course\",\"instructorId\":1}"
echo.

echo.
echo [5/5] Fetching courses again to verify creation...
curl -s %BASE_URL%/api/courses
echo.

echo.
echo ========================================
echo Testing Complete!
echo ========================================
echo.
echo To test file upload, use Postman or:
echo curl -X POST %BASE_URL%/api/course-content/upload ^
echo   -F "file=@path\to\your\file.pdf" ^
echo   -F "courseId=1" ^
echo   -F "userId=1"
echo.
echo For more detailed testing, import the Postman collection:
echo   Course-Content-API-Collection.postman_collection.json
echo.
echo Documentation available in:
echo   - README.md
echo   - QUICKSTART_GUIDE.md
echo   - COURSE_CONTENT_API.md
echo.
pause

