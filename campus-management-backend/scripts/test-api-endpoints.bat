@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 🚀 智慧校园管理系统 API 端点测试
echo ========================================
echo.

set BASE_URL=http://localhost:8082
set ADMIN_TOKEN=Bearer_admin_token_here

echo 📋 测试计划:
echo    1. 检查服务器状态
echo    2. 测试认证接口
echo    3. 测试核心API接口
echo    4. 生成测试报告
echo.

echo ⏳ 检查服务器状态...
curl -s -o nul -w "HTTP状态码: %%{http_code}" %BASE_URL%/actuator/health
if %ERRORLEVEL% neq 0 (
    echo.
    echo ❌ 服务器未启动，请先启动应用
    echo 💡 运行命令: mvn spring-boot:run
    pause
    exit /b 1
)
echo.
echo ✅ 服务器运行正常

echo.
echo ⏳ 测试认证接口...
echo 📍 POST /api/auth/login
curl -X POST "%BASE_URL%/api/auth/login" ^
     -H "Content-Type: application/json" ^
     -d "{\"username\":\"admin\",\"password\":\"admin123\"}" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/auth/me
curl -X GET "%BASE_URL%/api/auth/me" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试用户管理接口...
echo 📍 GET /api/users
curl -X GET "%BASE_URL%/api/users?page=1&size=5" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/users/stats
curl -X GET "%BASE_URL%/api/users/stats" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试学生管理接口...
echo 📍 GET /api/students
curl -X GET "%BASE_URL%/api/students?page=1&size=5" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/students/statistics
curl -X GET "%BASE_URL%/api/students/statistics" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试课程管理接口...
echo 📍 GET /api/courses
curl -X GET "%BASE_URL%/api/courses?page=1&size=5" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试系统管理接口...
echo 📍 GET /api/dashboard
curl -X GET "%BASE_URL%/api/dashboard" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/system/info
curl -X GET "%BASE_URL%/api/system/info" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试权限管理接口...
echo 📍 GET /api/roles
curl -X GET "%BASE_URL%/api/roles" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/permissions
curl -X GET "%BASE_URL%/api/permissions" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试学术管理接口...
echo 📍 GET /api/assignments
curl -X GET "%BASE_URL%/api/assignments" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/exams
curl -X GET "%BASE_URL%/api/exams" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/grades
curl -X GET "%BASE_URL%/api/grades" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ⏳ 测试特殊功能接口...
echo 📍 GET /api/auto-schedule
curl -X GET "%BASE_URL%/api/auto-schedule" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo 📍 GET /api/cache/stats
curl -X GET "%BASE_URL%/api/cache/stats" ^
     -H "Authorization: %ADMIN_TOKEN%" ^
     -w "\n状态码: %%{http_code}\n" ^
     -s

echo.
echo ========================================
echo 📊 API端点测试完成
echo ========================================
echo.
echo 💡 测试说明:
echo    - 200: 请求成功
echo    - 401: 未授权（需要登录）
echo    - 403: 权限不足
echo    - 404: 接口不存在
echo    - 500: 服务器错误
echo.
echo 📁 更多测试选项:
echo    - 使用Postman集合: src/test/resources/postman/
echo    - 运行单元测试: mvn test
echo    - 查看API文档: http://localhost:8082/swagger-ui.html
echo.

pause
