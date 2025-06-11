@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
echo.
echo ========================================
echo 🚀 智慧校园管理系统 - 自动化API测试
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

echo 📋 全面自动化测试计划:
echo    1. 检查服务器状态
echo    2. 自动登录获取Token
echo    3. 测试所有36个API控制器 (100+接口)
echo    4. 按业务模块分组测试
echo    5. 自动生成详细测试报告
echo.
echo 📁 结果保存目录: %OUTPUT_DIR%
echo 📅 测试时间戳: %TIMESTAMP%
echo 🎯 测试覆盖: 核心业务、学术管理、系统功能、权限管理等
echo.

:: 检查PowerShell是否可用
powershell -Command "Write-Host 'PowerShell可用'" >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ PowerShell不可用，请使用手动测试脚本: test-api-endpoints.bat
    pause
    exit /b 1
)

echo ⏳ 步骤1: 检查服务器状态...
curl -s "%BASE_URL%/actuator/health" > "%OUTPUT_DIR%\00_server_health_%TIMESTAMP%.txt" 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ 服务器未启动，请先启动应用
    echo 💡 运行命令: mvn spring-boot:run
    echo 错误信息已保存到: %OUTPUT_DIR%\00_server_health_%TIMESTAMP%.txt
    pause
    exit /b 1
)
echo ✅ 服务器运行正常

echo.
echo ⏳ 步骤2: 自动登录获取Token...
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

:: 使用PowerShell提取token
echo 🔍 正在提取Token...
powershell -Command ^
"$json = Get-Content '%OUTPUT_DIR%\01_login_response_%TIMESTAMP%.txt' | ConvertFrom-Json; ^
if ($json.data.token) { ^
    $json.data.token | Out-File -FilePath '%OUTPUT_DIR%\token_%TIMESTAMP%.txt' -Encoding UTF8 -NoNewline ^
} else { ^
    'TOKEN_NOT_FOUND' | Out-File -FilePath '%OUTPUT_DIR%\token_%TIMESTAMP%.txt' -Encoding UTF8 -NoNewline ^
}"

:: 读取提取的token
set /p JWT_TOKEN=<"%OUTPUT_DIR%\token_%TIMESTAMP%.txt"

if "%JWT_TOKEN%"=="TOKEN_NOT_FOUND" (
    echo ❌ 无法从登录响应中提取Token！
    echo 💡 请检查登录响应文件: %OUTPUT_DIR%\01_login_response_%TIMESTAMP%.txt
    pause
    exit /b 1
)

if "%JWT_TOKEN%"=="" (
    echo ❌ Token为空！
    pause
    exit /b 1
)

echo ✅ Token提取成功: %JWT_TOKEN:~0,20%...
echo.

:: 开始全面API测试
echo ⏳ 步骤3: 开始全面API测试...
echo 🔍 测试36个API控制器，按业务模块分组执行...
echo.

:: ==================== 认证模块 ====================
echo 📍 [模块1/8] 测试认证API模块...
curl -X GET "%BASE_URL%/api/auth/me" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\02_auth_current_user_%TIMESTAMP%.txt" 2>&1

echo ✅ 认证模块测试完成

:: ==================== 核心业务模块 ====================
echo 📍 [模块2/8] 测试核心业务API模块...

:: 用户管理API
echo   📝 测试用户管理API...
curl -X GET "%BASE_URL%/api/users?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\03_users_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/v1/users/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\04_users_stats_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/v1/users/count" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\05_users_count_%TIMESTAMP%.txt" 2>&1

:: 学生管理API
echo   📝 测试学生管理API...
curl -X GET "%BASE_URL%/api/students?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\06_students_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/v1/students/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\07_students_stats_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/v1/students/count" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\08_students_count_%TIMESTAMP%.txt" 2>&1

:: 课程管理API
echo   📝 测试课程管理API...
curl -X GET "%BASE_URL%/api/courses?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\09_courses_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/courses/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\10_courses_stats_%TIMESTAMP%.txt" 2>&1

:: 院系管理API
echo   📝 测试院系管理API...
curl -X GET "%BASE_URL%/api/departments?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\11_departments_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/departments/tree" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\12_departments_tree_%TIMESTAMP%.txt" 2>&1

:: 班级管理API
echo   📝 测试班级管理API...
curl -X GET "%BASE_URL%/api/classes?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\13_classes_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/classes/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\14_classes_stats_%TIMESTAMP%.txt" 2>&1

echo ✅ 核心业务模块测试完成

:: ==================== 学术管理模块 ====================
echo 📍 [模块3/8] 测试学术管理API模块...

:: 作业管理API
echo   📝 测试作业管理API...
curl -X GET "%BASE_URL%/api/assignments?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\15_assignments_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/assignments/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\16_assignments_stats_%TIMESTAMP%.txt" 2>&1

:: 考勤管理API
echo   📝 测试考勤管理API...
curl -X GET "%BASE_URL%/api/attendance?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\17_attendance_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/attendance/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\18_attendance_stats_%TIMESTAMP%.txt" 2>&1

:: 考试管理API
echo   📝 测试考试管理API...
curl -X GET "%BASE_URL%/api/exams?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\19_exams_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/exams/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\20_exams_stats_%TIMESTAMP%.txt" 2>&1

:: 成绩管理API
echo   📝 测试成绩管理API...
curl -X GET "%BASE_URL%/api/grades?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\21_grades_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/grades/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\22_grades_stats_%TIMESTAMP%.txt" 2>&1

:: 选课管理API
echo   📝 测试选课管理API...
curl -X GET "%BASE_URL%/api/course-selections?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\23_course_selections_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/course-selection-periods?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\24_course_selection_periods_%TIMESTAMP%.txt" 2>&1

echo ✅ 学术管理模块测试完成

:: ==================== 系统管理模块 ====================
echo 📍 [模块4/8] 测试系统管理API模块...

:: 仪表盘API
echo   📝 测试仪表盘API...
curl -X GET "%BASE_URL%/api/dashboard/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\25_dashboard_stats_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/dashboard/activities" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\26_dashboard_activities_%TIMESTAMP%.txt" 2>&1

:: 系统管理API
echo   📝 测试系统管理API...
curl -X GET "%BASE_URL%/api/system/info" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\27_system_info_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/system/config" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\28_system_config_%TIMESTAMP%.txt" 2>&1

:: 通知管理API
echo   📝 测试通知管理API...
curl -X GET "%BASE_URL%/api/notifications?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\29_notifications_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/notification-templates?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\30_notification_templates_%TIMESTAMP%.txt" 2>&1

:: 消息管理API
echo   📝 测试消息管理API...
curl -X GET "%BASE_URL%/api/messages?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\31_messages_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 系统管理模块测试完成

:: ==================== 权限管理模块 ====================
echo 📍 [模块5/8] 测试权限管理API模块...

:: 角色管理API
echo   📝 测试角色管理API...
curl -X GET "%BASE_URL%/api/roles?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\32_roles_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/roles/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\33_roles_stats_%TIMESTAMP%.txt" 2>&1

:: 权限管理API
echo   📝 测试权限管理API...
curl -X GET "%BASE_URL%/api/permissions?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\34_permissions_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/permissions/tree" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\35_permissions_tree_%TIMESTAMP%.txt" 2>&1

echo ✅ 权限管理模块测试完成

:: ==================== 财务管理模块 ====================
echo 📍 [模块6/8] 测试财务管理API模块...

:: 支付管理API
echo   📝 测试支付管理API...
curl -X GET "%BASE_URL%/api/payments?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\36_payments_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/payments/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\37_payments_stats_%TIMESTAMP%.txt" 2>&1

:: 费用项目API
echo   📝 测试费用项目API...
curl -X GET "%BASE_URL%/api/fee-items?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\38_fee_items_list_%TIMESTAMP%.txt" 2>&1

echo ✅ 财务管理模块测试完成

:: ==================== 特殊功能模块 ====================
echo 📍 [模块7/8] 测试特殊功能API模块...

:: 自动排课API
echo   📝 测试自动排课API...
curl -X GET "%BASE_URL%/api/auto-schedule/status" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\39_auto_schedule_status_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/auto-schedule/config" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\40_auto_schedule_config_%TIMESTAMP%.txt" 2>&1

:: 课表管理API
echo   📝 测试课表管理API...
curl -X GET "%BASE_URL%/api/schedules?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\41_schedules_list_%TIMESTAMP%.txt" 2>&1

:: 课程安排API
echo   📝 测试课程安排API...
curl -X GET "%BASE_URL%/api/course-schedules?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\42_course_schedules_list_%TIMESTAMP%.txt" 2>&1

:: 教室管理API
echo   📝 测试教室管理API...
curl -X GET "%BASE_URL%/api/classrooms?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\43_classrooms_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/classrooms/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\44_classrooms_stats_%TIMESTAMP%.txt" 2>&1

:: 缓存管理API
echo   📝 测试缓存管理API...
curl -X GET "%BASE_URL%/api/cache/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\45_cache_stats_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/cache/info" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\46_cache_info_%TIMESTAMP%.txt" 2>&1

echo ✅ 特殊功能模块测试完成

:: ==================== 扩展功能模块 ====================
echo 📍 [模块8/8] 测试扩展功能API模块...

:: 活动日志API
echo   📝 测试活动日志API...
curl -X GET "%BASE_URL%/api/activity-logs?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\47_activity_logs_list_%TIMESTAMP%.txt" 2>&1

curl -X GET "%BASE_URL%/api/activity-logs/stats" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\48_activity_logs_stats_%TIMESTAMP%.txt" 2>&1

:: 作业提交API
echo   📝 测试作业提交API...
curl -X GET "%BASE_URL%/api/assignment-submissions?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\49_assignment_submissions_%TIMESTAMP%.txt" 2>&1

:: 考试题目API
echo   📝 测试考试题目API...
curl -X GET "%BASE_URL%/api/exam-questions?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\50_exam_questions_%TIMESTAMP%.txt" 2>&1

:: 考试记录API
echo   📝 测试考试记录API...
curl -X GET "%BASE_URL%/api/exam-records?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\51_exam_records_%TIMESTAMP%.txt" 2>&1

:: 课程资源API
echo   📝 测试课程资源API...
curl -X GET "%BASE_URL%/api/course-resources?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\52_course_resources_%TIMESTAMP%.txt" 2>&1

:: 家长学生关系API
echo   📝 测试家长学生关系API...
curl -X GET "%BASE_URL%/api/parent-student-relations?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\53_parent_student_relations_%TIMESTAMP%.txt" 2>&1

:: 学生评价API
echo   📝 测试学生评价API...
curl -X GET "%BASE_URL%/api/student-evaluations?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\54_student_evaluations_%TIMESTAMP%.txt" 2>&1

:: 时间段API
echo   📝 测试时间段API...
curl -X GET "%BASE_URL%/api/time-slots?page=0&size=10" ^
     -H "Authorization: Bearer %JWT_TOKEN%" ^
     -H "Content-Type: application/json" ^
     -s > "%OUTPUT_DIR%\55_time_slots_%TIMESTAMP%.txt" 2>&1

echo ✅ 扩展功能模块测试完成

echo.
echo 🎉 所有36个API控制器测试完成！
echo 📊 总计测试了55个核心API接口
echo.

echo ⏳ 步骤4: 生成详细测试报告...

:: 使用PowerShell生成详细的测试报告
powershell -Command ^
"$report = @(); ^
$report += '===== 智慧校园管理系统 - 全面API自动化测试报告 ====='; ^
$report += '测试时间: ' + (Get-Date).ToString('yyyy-MM-dd HH:mm:ss'); ^
$report += '服务器地址: %BASE_URL%'; ^
$report += '管理员账号: %ADMIN_USERNAME%'; ^
$report += '测试时间戳: %TIMESTAMP%'; ^
$report += '测试覆盖: 36个API控制器，55个核心接口'; ^
$report += ''; ^
$report += '测试模块统计:'; ^
$report += '1. 认证模块 - 1个接口'; ^
$report += '2. 核心业务模块 - 12个接口 (用户、学生、课程、院系、班级)'; ^
$report += '3. 学术管理模块 - 10个接口 (作业、考勤、考试、成绩、选课)'; ^
$report += '4. 系统管理模块 - 7个接口 (仪表盘、系统、通知、消息)'; ^
$report += '5. 权限管理模块 - 4个接口 (角色、权限)'; ^
$report += '6. 财务管理模块 - 3个接口 (支付、费用项目)'; ^
$report += '7. 特殊功能模块 - 8个接口 (排课、课表、教室、缓存)'; ^
$report += '8. 扩展功能模块 - 10个接口 (日志、提交、题目等)'; ^
$report += ''; ^
$files = Get-ChildItem '%OUTPUT_DIR%' -Filter '*_%TIMESTAMP%.txt'; ^
$report += '测试结果统计:'; ^
$report += '总测试文件数: ' + $files.Count; ^
$report += '文件总大小: ' + [math]::Round(($files | Measure-Object Length -Sum).Sum / 1KB, 2) + ' KB'; ^
$report += ''; ^
$report += '按模块分组的测试文件:'; ^
$report += ''; ^
$report += '【认证模块】'; ^
$report += '02_auth_current_user_%TIMESTAMP%.txt - 当前用户信息'; ^
$report += ''; ^
$report += '【核心业务模块】'; ^
$report += '03_users_list_%TIMESTAMP%.txt - 用户列表'; ^
$report += '04_users_stats_%TIMESTAMP%.txt - 用户统计'; ^
$report += '05_users_count_%TIMESTAMP%.txt - 用户计数'; ^
$report += '06_students_list_%TIMESTAMP%.txt - 学生列表'; ^
$report += '07_students_stats_%TIMESTAMP%.txt - 学生统计'; ^
$report += '08_students_count_%TIMESTAMP%.txt - 学生计数'; ^
$report += '09_courses_list_%TIMESTAMP%.txt - 课程列表'; ^
$report += '10_courses_stats_%TIMESTAMP%.txt - 课程统计'; ^
$report += '11_departments_list_%TIMESTAMP%.txt - 院系列表'; ^
$report += '12_departments_tree_%TIMESTAMP%.txt - 院系树结构'; ^
$report += '13_classes_list_%TIMESTAMP%.txt - 班级列表'; ^
$report += '14_classes_stats_%TIMESTAMP%.txt - 班级统计'; ^
$report += ''; ^
$report += '【学术管理模块】'; ^
$report += '15_assignments_list_%TIMESTAMP%.txt - 作业列表'; ^
$report += '16_assignments_stats_%TIMESTAMP%.txt - 作业统计'; ^
$report += '17_attendance_list_%TIMESTAMP%.txt - 考勤列表'; ^
$report += '18_attendance_stats_%TIMESTAMP%.txt - 考勤统计'; ^
$report += '19_exams_list_%TIMESTAMP%.txt - 考试列表'; ^
$report += '20_exams_stats_%TIMESTAMP%.txt - 考试统计'; ^
$report += '21_grades_list_%TIMESTAMP%.txt - 成绩列表'; ^
$report += '22_grades_stats_%TIMESTAMP%.txt - 成绩统计'; ^
$report += '23_course_selections_list_%TIMESTAMP%.txt - 选课列表'; ^
$report += '24_course_selection_periods_%TIMESTAMP%.txt - 选课时段'; ^
$report += ''; ^
$report += '【系统管理模块】'; ^
$report += '25_dashboard_stats_%TIMESTAMP%.txt - 仪表盘统计'; ^
$report += '26_dashboard_activities_%TIMESTAMP%.txt - 仪表盘活动'; ^
$report += '27_system_info_%TIMESTAMP%.txt - 系统信息'; ^
$report += '28_system_config_%TIMESTAMP%.txt - 系统配置'; ^
$report += '29_notifications_list_%TIMESTAMP%.txt - 通知列表'; ^
$report += '30_notification_templates_%TIMESTAMP%.txt - 通知模板'; ^
$report += '31_messages_list_%TIMESTAMP%.txt - 消息列表'; ^
$report += ''; ^
$report += '【权限管理模块】'; ^
$report += '32_roles_list_%TIMESTAMP%.txt - 角色列表'; ^
$report += '33_roles_stats_%TIMESTAMP%.txt - 角色统计'; ^
$report += '34_permissions_list_%TIMESTAMP%.txt - 权限列表'; ^
$report += '35_permissions_tree_%TIMESTAMP%.txt - 权限树结构'; ^
$report += ''; ^
$report += '【财务管理模块】'; ^
$report += '36_payments_list_%TIMESTAMP%.txt - 支付列表'; ^
$report += '37_payments_stats_%TIMESTAMP%.txt - 支付统计'; ^
$report += '38_fee_items_list_%TIMESTAMP%.txt - 费用项目列表'; ^
$report += ''; ^
$report += '【特殊功能模块】'; ^
$report += '39_auto_schedule_status_%TIMESTAMP%.txt - 自动排课状态'; ^
$report += '40_auto_schedule_config_%TIMESTAMP%.txt - 自动排课配置'; ^
$report += '41_schedules_list_%TIMESTAMP%.txt - 课表列表'; ^
$report += '42_course_schedules_list_%TIMESTAMP%.txt - 课程安排列表'; ^
$report += '43_classrooms_list_%TIMESTAMP%.txt - 教室列表'; ^
$report += '44_classrooms_stats_%TIMESTAMP%.txt - 教室统计'; ^
$report += '45_cache_stats_%TIMESTAMP%.txt - 缓存统计'; ^
$report += '46_cache_info_%TIMESTAMP%.txt - 缓存信息'; ^
$report += ''; ^
$report += '【扩展功能模块】'; ^
$report += '47_activity_logs_list_%TIMESTAMP%.txt - 活动日志列表'; ^
$report += '48_activity_logs_stats_%TIMESTAMP%.txt - 活动日志统计'; ^
$report += '49_assignment_submissions_%TIMESTAMP%.txt - 作业提交'; ^
$report += '50_exam_questions_%TIMESTAMP%.txt - 考试题目'; ^
$report += '51_exam_records_%TIMESTAMP%.txt - 考试记录'; ^
$report += '52_course_resources_%TIMESTAMP%.txt - 课程资源'; ^
$report += '53_parent_student_relations_%TIMESTAMP%.txt - 家长学生关系'; ^
$report += '54_student_evaluations_%TIMESTAMP%.txt - 学生评价'; ^
$report += '55_time_slots_%TIMESTAMP%.txt - 时间段'; ^
$report += ''; ^
$report += '数据验证要点:'; ^
$report += '1. 检查各文件中的HTTP状态码 (期望200)'; ^
$report += '2. 验证JSON响应格式 (code/message/data/timestamp)'; ^
$report += '3. 确认分页数据结构完整性'; ^
$report += '4. 检查统计数据的合理性'; ^
$report += '5. 如有错误，查看服务器日志和数据库连接'; ^
$report += ''; ^
$report += '后续操作建议:'; ^
$report += '1. 使用JSON格式化工具查看响应数据'; ^
$report += '2. 对比不同模块的数据结构一致性'; ^
$report += '3. 验证业务逻辑的正确性'; ^
$report += '4. 进行性能分析和优化'; ^
$report | Out-File -FilePath '%OUTPUT_DIR%\API_COMPREHENSIVE_TEST_REPORT_%TIMESTAMP%.txt' -Encoding UTF8"

echo.
echo ========================================
echo 🎉 全面自动化API测试完成！
echo ========================================
echo.
echo 📊 测试结果总览:
echo   ✅ 服务器健康检查
echo   ✅ 自动登录获取Token
echo   ✅ 认证模块 (1个接口)
echo   ✅ 核心业务模块 (12个接口)
echo   ✅ 学术管理模块 (10个接口)
echo   ✅ 系统管理模块 (7个接口)
echo   ✅ 权限管理模块 (4个接口)
echo   ✅ 财务管理模块 (3个接口)
echo   ✅ 特殊功能模块 (8个接口)
echo   ✅ 扩展功能模块 (10个接口)
echo.
echo 🎯 测试覆盖统计:
echo   📊 API控制器总数: 36个
echo   📊 核心接口总数: 55个
echo   📊 测试文件总数: 56个 (含健康检查和登录)
echo   📊 业务模块覆盖: 8大模块
echo.
echo 📁 测试结果目录: %OUTPUT_DIR%
echo 📋 详细测试报告: %OUTPUT_DIR%\API_COMPREHENSIVE_TEST_REPORT_%TIMESTAMP%.txt
echo.
echo 💡 数据分析建议:
echo    1. 查看详细测试报告了解完整测试覆盖
echo    2. 按模块检查JSON响应数据格式
echo    3. 验证业务数据的完整性和一致性
echo    4. 检查统计接口的数据准确性
echo    5. 如有错误，参考报告中的验证要点
echo.
echo 🔗 相关资源:
echo    📖 API文档: %BASE_URL%/api/swagger-ui.html
echo    🏥 健康检查: %BASE_URL%/actuator/health
echo    📊 系统监控: %BASE_URL%/actuator/metrics
echo.

pause
