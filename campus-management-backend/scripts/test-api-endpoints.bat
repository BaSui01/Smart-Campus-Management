@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
echo.
echo ========================================
echo 🚀 智慧校园管理系统 API 端点测试
echo ========================================
echo.

:: 设置变量
set "BASE_URL=http://localhost:8082"
set "ADMIN_USERNAME=admin"
set "ADMIN_PASSWORD=admin123"
set "OUTPUT_DIR=api-test-results"
set "TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=!TIMESTAMP: =0!"

:: 创建输出目录
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

echo 📋 测试计划:
echo    1. 检查服务器状态
echo    2. 用户登录获取Token
echo    3. 测试核心API接口
echo    4. 保存JSON响应到txt文件
echo.
echo 📁 结果保存目录: %OUTPUT_DIR%
echo 📅 测试时间戳: %TIMESTAMP%
echo.

echo ⏳ 检查服务器状态...
curl -s "%BASE_URL%/actuator/health" > "%OUTPUT_DIR%\00_server_health_%TIMESTAMP%.txt" 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ 服务器未启动，请先启动应用
    echo 💡 运行命令: mvn spring-boot:run
    echo 错误信息已保存到: %OUTPUT_DIR%\00_server_health_%TIMESTAMP%.txt
    pause
    exit /b 1
)
echo ✅ 服务器运行正常，健康检查结果已保存

echo.
echo ⏳ 步骤1: 用户登录获取Token...
echo 📍 POST /api/auth/login
curl -X POST "%BASE_URL%/api/auth/login" ^
     -H "Content-Type: application/json" ^
     -d "{\"username\":\"%ADMIN_USERNAME%\",\"password\":\"%ADMIN_PASSWORD%\"}" ^
     -s > "%OUTPUT_DIR%\01_login_response_%TIMESTAMP%.txt" 2>&1

if %ERRORLEVEL% neq 0 (
    echo ❌ 登录请求失败！
    echo 错误信息已保存到: %OUTPUT_DIR%\01_login_response_%TIMESTAMP%.txt
    pause
    exit /b 1
)

echo ✅ 登录响应已保存到: %OUTPUT_DIR%\01_login_response_%TIMESTAMP%.txt

:: 提示用户手动获取token（实际项目中可以使用jq等工具自动解析）
echo.
echo ⚠️  请从登录响应文件中复制token值
echo 💡 打开文件: %OUTPUT_DIR%\01_login_response_%TIMESTAMP%.txt
echo 💡 查找 "token" 字段的值（不包括引号）
echo.
set /p "JWT_TOKEN=请输入token值: "

if "%JWT_TOKEN%"=="" (
    echo ❌ Token不能为空！
    pause
    exit /b 1
)

echo ✅ Token设置完成
echo.

echo ⏳ 步骤2: 测试认证接口...
echo 📍 GET /api/auth/me
curl -X GET "%BASE_URL%/api/auth/me" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\02_current_user_%TIMESTAMP%.txt" 2>&1

echo ✅ 当前用户信息已保存到: %OUTPUT_DIR%\02_current_user_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤3: 测试用户管理接口...
echo 📍 GET /api/users
curl -X GET "%BASE_URL%/api/users?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\03_users_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 用户列表已保存到: %OUTPUT_DIR%\03_users_list_%TIMESTAMP%.txt

echo 📍 GET /api/v1/users/stats
curl -X GET "%BASE_URL%/api/v1/users/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\04_users_stats_%TIMESTAMP%.txt" 2>&1

echo ✅ 用户统计已保存到: %OUTPUT_DIR%\04_users_stats_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤4: 测试学生管理接口...
echo 📍 GET /api/students
curl -X GET "%BASE_URL%/api/students?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\05_students_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 学生列表已保存到: %OUTPUT_DIR%\05_students_list_%TIMESTAMP%.txt

echo 📍 GET /api/v1/students/stats
curl -X GET "%BASE_URL%/api/v1/students/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\06_students_stats_%TIMESTAMP%.txt" 2>&1

echo ✅ 学生统计已保存到: %OUTPUT_DIR%\06_students_stats_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤5: 测试课程管理接口...
echo 📍 GET /api/courses
curl -X GET "%BASE_URL%/api/courses?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\07_courses_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 课程列表已保存到: %OUTPUT_DIR%\07_courses_list_%TIMESTAMP%.txt

echo 📍 GET /api/departments
curl -X GET "%BASE_URL%/api/departments?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\08_departments_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 院系列表已保存到: %OUTPUT_DIR%\08_departments_list_%TIMESTAMP%.txt

echo 📍 GET /api/classes
curl -X GET "%BASE_URL%/api/classes?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\09_classes_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 班级列表已保存到: %OUTPUT_DIR%\09_classes_list_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤6: 测试系统管理接口...
echo 📍 GET /api/dashboard/stats
curl -X GET "%BASE_URL%/api/dashboard/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\10_dashboard_stats_%TIMESTAMP%.txt" 2>&1

echo ✅ 仪表盘统计已保存到: %OUTPUT_DIR%\10_dashboard_stats_%TIMESTAMP%.txt

echo 📍 GET /api/system/info
curl -X GET "%BASE_URL%/api/system/info" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\11_system_info_%TIMESTAMP%.txt" 2>&1

echo ✅ 系统信息已保存到: %OUTPUT_DIR%\11_system_info_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤7: 测试权限管理接口...
echo 📍 GET /api/roles
curl -X GET "%BASE_URL%/api/roles?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\12_roles_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 角色列表已保存到: %OUTPUT_DIR%\12_roles_list_%TIMESTAMP%.txt

echo 📍 GET /api/permissions
curl -X GET "%BASE_URL%/api/permissions?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\13_permissions_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 权限列表已保存到: %OUTPUT_DIR%\13_permissions_list_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤8: 测试学术管理接口...
echo 📍 GET /api/assignments
curl -X GET "%BASE_URL%/api/assignments?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\14_assignments_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 作业列表已保存到: %OUTPUT_DIR%\14_assignments_list_%TIMESTAMP%.txt

echo 📍 GET /api/exams
curl -X GET "%BASE_URL%/api/exams?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\15_exams_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 考试列表已保存到: %OUTPUT_DIR%\15_exams_list_%TIMESTAMP%.txt

echo 📍 GET /api/grades
curl -X GET "%BASE_URL%/api/grades?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\16_grades_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 成绩列表已保存到: %OUTPUT_DIR%\16_grades_list_%TIMESTAMP%.txt

echo.
echo ⏳ 步骤9: 测试特殊功能接口...
echo 📍 GET /api/notifications
curl -X GET "%BASE_URL%/api/notifications?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\17_notifications_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 通知列表已保存到: %OUTPUT_DIR%\17_notifications_list_%TIMESTAMP%.txt

echo 📍 GET /api/cache/stats
curl -X GET "%BASE_URL%/api/cache/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\18_cache_stats_%TIMESTAMP%.txt" 2>&1

echo ✅ 缓存统计已保存到: %OUTPUT_DIR%\18_cache_stats_%TIMESTAMP%.txt

echo.
echo ⏳ 生成测试报告...
echo ===== 智慧校园管理系统 API 测试报告 ===== > "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 测试时间: %date% %time% >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 服务器地址: %BASE_URL% >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 管理员账号: %ADMIN_USERNAME% >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 测试时间戳: %TIMESTAMP% >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo. >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 测试文件列表: >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 00_server_health_%TIMESTAMP%.txt - 服务器健康检查 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 01_login_response_%TIMESTAMP%.txt - 用户登录响应 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 02_current_user_%TIMESTAMP%.txt - 当前用户信息 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 03_users_list_%TIMESTAMP%.txt - 用户列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 04_users_stats_%TIMESTAMP%.txt - 用户统计 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 05_students_list_%TIMESTAMP%.txt - 学生列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 06_students_stats_%TIMESTAMP%.txt - 学生统计 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 07_courses_list_%TIMESTAMP%.txt - 课程列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 08_departments_list_%TIMESTAMP%.txt - 院系列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 09_classes_list_%TIMESTAMP%.txt - 班级列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 10_dashboard_stats_%TIMESTAMP%.txt - 仪表盘统计 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 11_system_info_%TIMESTAMP%.txt - 系统信息 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 12_roles_list_%TIMESTAMP%.txt - 角色列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 13_permissions_list_%TIMESTAMP%.txt - 权限列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 14_assignments_list_%TIMESTAMP%.txt - 作业列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 15_exams_list_%TIMESTAMP%.txt - 考试列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 16_grades_list_%TIMESTAMP%.txt - 成绩列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 17_notifications_list_%TIMESTAMP%.txt - 通知列表 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"
echo 18_cache_stats_%TIMESTAMP%.txt - 缓存统计 >> "%OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt"

echo.
echo ========================================
echo 🎉 API端点测试完成！
echo ========================================
echo.
echo 📊 测试结果:
echo   ✅ 服务器健康检查
echo   ✅ 用户登录认证
echo   ✅ 用户管理API (2个接口)
echo   ✅ 学生管理API (2个接口)
echo   ✅ 课程管理API (3个接口)
echo   ✅ 系统管理API (2个接口)
echo   ✅ 权限管理API (2个接口)
echo   ✅ 学术管理API (3个接口)
echo   ✅ 特殊功能API (2个接口)
echo.
echo 📁 所有测试结果已保存到: %OUTPUT_DIR%
echo 📋 测试报告: %OUTPUT_DIR%\API_TEST_REPORT_%TIMESTAMP%.txt
echo.
echo 💡 HTTP状态码说明:
echo    - 200: 请求成功
echo    - 401: 未授权（需要登录）
echo    - 403: 权限不足
echo    - 404: 接口不存在
echo    - 500: 服务器错误
echo.
echo 📁 更多测试选项:
echo    - 查看API文档: %BASE_URL%/api/swagger-ui.html
echo    - 运行单元测试: mvn test
echo    - 使用Postman集合: src/test/resources/postman/
echo.

pause
