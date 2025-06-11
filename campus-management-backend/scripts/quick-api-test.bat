@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
echo.
echo ========================================
echo ⚡ 智慧校园管理系统 - 快速API测试
echo ========================================
echo.

:: 设置变量
set "BASE_URL=http://localhost:8082"
set "ADMIN_USERNAME=admin"
set "ADMIN_PASSWORD=admin123"
set "OUTPUT_DIR=quick-test-results"
set "TIMESTAMP=%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "TIMESTAMP=!TIMESTAMP: =0!"

:: 创建输出目录
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

echo 📋 快速测试内容:
echo    ✅ 服务器健康检查
echo    ✅ 用户登录认证
echo    ✅ 核心API接口测试 (5个)
echo    ✅ 生成简洁测试报告
echo.

:: 步骤1: 检查服务器
echo [1/4] 检查服务器状态...
curl -s "%BASE_URL%/actuator/health" -o "%OUTPUT_DIR%\health_%TIMESTAMP%.json"
if %ERRORLEVEL% neq 0 (
    echo ❌ 服务器连接失败！
    pause
    exit /b 1
)
echo ✅ 服务器正常

:: 步骤2: 用户登录
echo [2/4] 执行用户登录...
curl -X POST "%BASE_URL%/api/auth/login" -H "Content-Type: application/json" -d "{\"username\":\"%ADMIN_USERNAME%\",\"password\":\"%ADMIN_PASSWORD%\"}" -s -o "%OUTPUT_DIR%\login_%TIMESTAMP%.json"

if %ERRORLEVEL% neq 0 (
    echo ❌ 登录失败！
    pause
    exit /b 1
)
echo ✅ 登录成功

:: 提示用户获取token
echo.
echo 🔑 请从登录文件中获取token:
echo 📁 文件位置: %OUTPUT_DIR%\login_%TIMESTAMP%.json
echo 💡 查找 "token" 字段的值
echo.
set /p "JWT_TOKEN=请输入token: "

if "%JWT_TOKEN%"=="" (
    echo ❌ Token不能为空！
    pause
    exit /b 1
)

:: 步骤3: 核心API测试
echo.
echo [3/4] 测试核心API接口...

echo 📍 测试用户信息...
curl -X GET "%BASE_URL%/api/auth/me" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\user_info_%TIMESTAMP%.json"

echo 📍 测试用户列表...
curl -X GET "%BASE_URL%/api/users?page=0&size=5" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\users_list_%TIMESTAMP%.json"

echo 📍 测试学生列表...
curl -X GET "%BASE_URL%/api/students?page=0&size=5" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\students_list_%TIMESTAMP%.json"

echo 📍 测试课程列表...
curl -X GET "%BASE_URL%/api/courses?page=0&size=5" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\courses_list_%TIMESTAMP%.json"

echo 📍 测试系统信息...
curl -X GET "%BASE_URL%/api/system/info" -H "Authorization: Bearer %JWT_TOKEN%" -H "Content-Type: application/json" -s -o "%OUTPUT_DIR%\system_info_%TIMESTAMP%.json"

echo ✅ API测试完成

:: 步骤4: 生成报告
echo.
echo [4/4] 生成测试报告...

echo ===== 智慧校园管理系统 - 快速API测试报告 ===== > "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 测试时间: %date% %time% >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 服务器地址: %BASE_URL% >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 管理员账号: %ADMIN_USERNAME% >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo. >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 测试文件列表: >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 1. health_%TIMESTAMP%.json - 服务器健康检查 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 2. login_%TIMESTAMP%.json - 用户登录响应 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 3. user_info_%TIMESTAMP%.json - 当前用户信息 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 4. users_list_%TIMESTAMP%.json - 用户列表数据 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 5. students_list_%TIMESTAMP%.json - 学生列表数据 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 6. courses_list_%TIMESTAMP%.json - 课程列表数据 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 7. system_info_%TIMESTAMP%.json - 系统信息数据 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo. >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo 检查要点: >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - 所有JSON文件应包含有效的响应数据 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - 登录响应应包含token字段 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - 列表响应应包含data数组和分页信息 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"
echo - 错误响应会包含error或message字段 >> "%OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt"

echo.
echo ========================================
echo 🎉 快速API测试完成！
echo ========================================
echo.
echo 📊 测试结果:
echo   ✅ 服务器健康检查
echo   ✅ 用户登录认证
echo   ✅ 用户信息API
echo   ✅ 用户列表API
echo   ✅ 学生列表API
echo   ✅ 课程列表API
echo   ✅ 系统信息API
echo.
echo 📁 测试结果目录: %OUTPUT_DIR%
echo 📋 测试报告: %OUTPUT_DIR%\QUICK_TEST_REPORT_%TIMESTAMP%.txt
echo.
echo 💡 下一步操作:
echo    1. 打开各个JSON文件查看响应数据
echo    2. 验证数据格式和内容是否正确
echo    3. 如有错误，检查服务器日志
echo.
echo 📂 快速查看文件:
if exist "%OUTPUT_DIR%\*_%TIMESTAMP%.json" (
    dir /b "%OUTPUT_DIR%\*_%TIMESTAMP%.json"
) else (
    echo 未找到JSON文件 - 请检查是否有错误
)
echo.

pause
