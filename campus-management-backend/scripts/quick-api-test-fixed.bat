@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul 2>&1
echo.
echo ========================================
echo Smart Campus Management - Quick API Test
echo ========================================
echo.

:: Set variables
set "BASE_URL=http://localhost:8082"
set "ADMIN_USERNAME=admin"
set "ADMIN_PASSWORD=admin123"
set "OUTPUT_DIR=quick-test-results"
set "TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=!TIMESTAMP: =0!"

:: Create output directory
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

echo Quick test content:
echo    - Server health check
echo    - User login authentication
echo    - Core API interface testing (5 endpoints)
echo    - Generate concise test report
echo.

:: Check if curl is available
where curl >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: curl command not found. Please install curl or use Git Bash.
    pause
    exit /b 1
)

:: Step 1: Check server
echo [1/4] Checking server status...
curl -s "%BASE_URL%/actuator/health" -o "%OUTPUT_DIR%\health_%TIMESTAMP%.json"
if %ERRORLEVEL% neq 0 (
    echo ERROR: Server connection failed!
    echo Please ensure the server is running on %BASE_URL%
    pause
    exit /b 1
)
echo SUCCESS: Server is running

:: Step 2: User login
echo [2/4] Executing user login...
curl -X POST "%BASE_URL%/api/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"%ADMIN_USERNAME%\",\"password\":\"%ADMIN_PASSWORD%\"}" -s -o "%OUTPUT_DIR%\login_%TIMESTAMP%.json"

if %ERRORLEVEL% neq 0 (
    echo ERROR: Login failed!
    echo Please check server logs and credentials
    pause
    exit /b 1
)
echo SUCCESS: Login completed

:: Extract token from login response (Windows specific)
echo.
echo Please get token from login file:
echo File location: %OUTPUT_DIR%\login_%TIMESTAMP%.json
echo Look for "token" field value
echo.
set /p "JWT_TOKEN=Please enter token: "

if "%JWT_TOKEN%"=="" (
    echo ERROR: Token cannot be empty!
    pause
    exit /b 1
)

:: Step 3: Core API testing
echo.
echo [3/4] Testing core API interfaces...

echo Testing user info...
curl -X GET "%BASE_URL%/api/auth/me" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\user_info_%TIMESTAMP%.json"

echo Testing user list...
curl -X GET "%BASE_URL%/api/users?page=0&size=5" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\users_list_%TIMESTAMP%.json"

echo Testing student list...
curl -X GET "%BASE_URL%/api/students?page=0&size=5" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\students_list_%TIMESTAMP%.json"

echo Testing course list...
curl -X GET "%BASE_URL%/api/courses?page=0&size=5" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\courses_list_%TIMESTAMP%.json"

echo Testing system info...
curl -X GET "%BASE_URL%/api/system/info" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\system_info_%TIMESTAMP%.json"

echo SUCCESS: API testing completed

:: Step 4: Generate report
echo.
echo [4/4] Generating test report...

echo ===== Smart Campus Management - Quick API Test Report ===== > "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo Test Time: %date% %time% >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo Server Address: %BASE_URL% >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo Admin Account: %ADMIN_USERNAME% >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo. >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo Test File List: >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 1. health_%TIMESTAMP%.json - Server health check >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 2. login_%TIMESTAMP%.json - User login response >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 3. user_info_%TIMESTAMP%.json - Current user info >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 4. users_list_%TIMESTAMP%.json - Users list data >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 5. students_list_%TIMESTAMP%.json - Students list data >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 6. courses_list_%TIMESTAMP%.json - Courses list data >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 7. system_info_%TIMESTAMP%.json - System info data >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo. >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo Check Points: >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - All JSON files should contain valid response data >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - Login response should contain token field >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - List responses should contain data array and pagination info >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - Error responses will contain error or message field >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"

echo.
echo ========================================
echo Quick API Test Completed!
echo ========================================
echo.
echo Test Results:
echo   - Server health check
echo   - User login authentication
echo   - User info API
echo   - User list API
echo   - Student list API
echo   - Course list API
echo   - System info API
echo.
echo Test Results Directory: %OUTPUT_DIR%
echo Test Report: %OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt
echo.
echo Next Steps:
echo    1. Open each JSON file to view response data
echo    2. Verify data format and content correctness
echo    3. If errors exist, check server logs
echo.
echo Quick view files:
if exist "%OUTPUT_DIR%\*_%TIMESTAMP%.json" (
    dir /b "%OUTPUT_DIR%\*_%TIMESTAMP%.json"
) else (
    echo No JSON files found - check for errors
)
echo.

pause