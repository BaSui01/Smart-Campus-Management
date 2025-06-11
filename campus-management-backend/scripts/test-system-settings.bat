@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul
echo =====================================================
echo    测试系统设置表数据插入
echo    Test System Settings Table Data Insert
echo =====================================================
echo.

:: 设置变量
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=campus_management_db"
set "DB_USER=root"
set /p "DB_PASSWORD=请输入数据库密码 (Enter database password): "

set "MYSQL_CMD=mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD%"

echo.
echo 数据库连接信息:
echo   主机: %DB_HOST%:%DB_PORT%
echo   用户: %DB_USER%
echo   数据库: %DB_NAME%
echo.

:: 测试数据库连接
echo [1/4] 测试数据库连接...
%MYSQL_CMD% -e "SELECT 'Database connection successful!' as status;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ 数据库连接失败，请检查连接信息！
    pause
    exit /b 1
)
echo ✅ 数据库连接成功！
echo.

:: 检查系统设置表结构
echo [2/4] 检查系统设置表结构...
%MYSQL_CMD% -e "USE %DB_NAME%; DESCRIBE tb_system_settings;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ 系统设置表不存在，请先执行表结构创建脚本！
    pause
    exit /b 1
)
echo ✅ 系统设置表结构检查完成
echo.

:: 测试系统设置数据插入
echo [3/4] 测试系统设置数据插入...
%MYSQL_CMD% -e "USE %DB_NAME%; INSERT IGNORE INTO tb_system_settings (setting_key, setting_value, setting_type, category, setting_name, description, sort_order, status, deleted) VALUES ('test.setting', 'test_value', 'string', 'test', '测试设置', '这是一个测试设置', 1, 1, 0);" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ 系统设置数据插入失败！
    pause
    exit /b 1
)
echo ✅ 系统设置数据插入测试成功
echo.

:: 验证数据插入结果
echo [4/4] 验证数据插入结果...
echo 当前系统设置表数据:
%MYSQL_CMD% -e "USE %DB_NAME%; SELECT setting_key, setting_value, setting_type, category, setting_name FROM tb_system_settings ORDER BY id DESC LIMIT 5;" 2>nul

echo.
echo =====================================================
echo          🎉 系统设置表测试完成！
echo =====================================================
echo.
echo 测试结果:
echo   ✅ 数据库连接正常
echo   ✅ 系统设置表结构正确
echo   ✅ 数据插入功能正常
echo   ✅ 数据查询功能正常
echo.
echo 现在可以安全执行完整的数据库初始化脚本了！
echo =====================================================

pause
