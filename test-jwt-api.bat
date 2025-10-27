@echo off
REM JWT Authentication API Test Script
REM This script helps test the JWT authentication endpoints

echo ========================================
echo JWT Authentication API Test Script
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo Choose an option:
echo 1. Register Instructor
echo 2. Register Student
echo 3. Register Admin
echo 4. Login
echo 5. Test Create Course (requires token)
echo 6. Get My Courses (requires token)
echo 7. Get All Courses
echo 8. Exit
echo.

set /p choice="Enter your choice (1-8): "

if "%choice%"=="1" goto register_instructor
if "%choice%"=="2" goto register_student
if "%choice%"=="3" goto register_admin
if "%choice%"=="4" goto login
if "%choice%"=="5" goto create_course
if "%choice%"=="6" goto my_courses
if "%choice%"=="7" goto all_courses
if "%choice%"=="8" goto end

:register_instructor
echo.
echo Registering Instructor...
curl -X POST %BASE_URL%/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Jane\",\"lastName\":\"Smith\",\"email\":\"jane.smith@example.com\",\"password\":\"password123\",\"roles\":[\"INSTRUCTOR\"]}"
echo.
echo Save the accessToken and refreshToken from the response!
goto menu

:register_student
echo.
echo Registering Student...
curl -X POST %BASE_URL%/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"John\",\"lastName\":\"Doe\",\"email\":\"john.doe@example.com\",\"password\":\"password123\",\"roles\":[\"STUDENT\"]}"
echo.
echo Save the accessToken and refreshToken from the response!
goto menu

:register_admin
echo.
echo Registering Admin...
curl -X POST %BASE_URL%/api/auth/register ^
  -H "Content-Type: application/json" ^
  -d "{\"firstName\":\"Admin\",\"lastName\":\"User\",\"email\":\"admin@example.com\",\"password\":\"admin123\",\"roles\":[\"ADMIN\"]}"
echo.
echo Save the accessToken and refreshToken from the response!
goto menu

:login
echo.
set /p email="Enter email: "
set /p password="Enter password: "
echo.
echo Logging in...
curl -X POST %BASE_URL%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"%email%\",\"password\":\"%password%\"}"
echo.
echo Save the accessToken and refreshToken from the response!
goto menu

:create_course
echo.
set /p token="Enter your access token: "
echo.
echo Creating course...
curl -X POST %BASE_URL%/api/courses ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer %token%" ^
  -d "{\"courseName\":\"Introduction to Java\",\"courseCode\":\"CS101\",\"description\":\"Learn Java from scratch\",\"contentIds\":[]}"
echo.
goto menu

:my_courses
echo.
set /p token="Enter your access token: "
echo.
echo Getting your courses...
curl -X GET %BASE_URL%/api/courses/my-courses ^
  -H "Authorization: Bearer %token%"
echo.
goto menu

:all_courses
echo.
set /p token="Enter your access token (or press Enter to skip): "
echo.
echo Getting all courses...
if "%token%"=="" (
  curl -X GET %BASE_URL%/api/courses
) else (
  curl -X GET %BASE_URL%/api/courses ^
    -H "Authorization: Bearer %token%"
)
echo.
goto menu

:menu
echo.
echo Press any key to return to menu...
pause >nul
cls
goto start

:end
echo.
echo Goodbye!
echo.
exit

:start
goto :eof

