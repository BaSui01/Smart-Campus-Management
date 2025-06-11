@echo off
chcp 65001 > nul
echo =====================================================
echo    智慧校园管理系统 - 快速初始化
echo    Quick Database Setup for Smart Campus System
echo =====================================================
echo.

:: 快速设置（使用默认配置）
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=campus_management_db"
set "DB_USER=root"

echo 🚀 快速初始化模式
echo.
echo 使用默认配置:
echo   主机: %DB_HOST%:%DB_PORT%
echo   数据库: %DB_NAME%
echo   用户: %DB_USER%
echo.

set /p "DB_PASSWORD=请输入数据库密码: "

set "MYSQL_CMD=mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD%"
set "SQL_DIR=..\src\main\resources\db"

echo.
echo 🔍 测试数据库连接...
%MYSQL_CMD% -e "SELECT 'Connection OK' as status;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ 数据库连接失败！请检查配置。
    pause
    exit /b 1
)
echo ✅ 数据库连接成功！

echo.
echo 📦 开始快速初始化...
echo.

:: 执行所有SQL脚本
for %%f in (01_create_tables.sql 02_insert_basic_data.sql 03_insert_large_data.sql 04_complete_all_tables.sql 05_data_validation.sql 06_data_analysis.sql) do (
    echo 📝 执行 %%f...
    %MYSQL_CMD% < "%SQL_DIR%\%%f" 2>nul
    if !ERRORLEVEL! neq 0 (
        echo ❌ %%f 执行失败！
        pause
        exit /b 1
    )
    echo ✅ %%f 完成
)

echo.
echo 🎉 快速初始化完成！
echo.
echo 📊 数据库统计:
%MYSQL_CMD% -e "USE %DB_NAME%; SELECT '用户总数' as 项目, COUNT(*) as 数量 FROM tb_user WHERE deleted=0 UNION SELECT '学生总数', COUNT(*) FROM tb_student WHERE deleted=0 UNION SELECT '课程总数', COUNT(*) FROM tb_course WHERE deleted=0;" 2>nul

echo.
echo 🔑 默认管理员账号:
echo   用户名: admin
echo   密码: admin123
echo.
echo ✨ 系统已准备就绪！
pause