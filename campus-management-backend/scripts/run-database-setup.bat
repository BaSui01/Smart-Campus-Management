@echo off
setlocal enabledelayedexpansion
chcp 65001 > nul
echo =====================================================
echo    智慧校园管理系统 - 数据库初始化脚本
echo    Smart Campus Management System - Database Setup
echo =====================================================
echo.

:: 设置变量
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_NAME=campus_management_db"
set "DB_USER=root"
set /p "DB_PASSWORD=请输入数据库密码 (Enter database password): "

set "MYSQL_CMD=mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD%"
set "SQL_DIR=..\src\main\resources\db"

echo.
echo 数据库连接信息:
echo   主机: %DB_HOST%:%DB_PORT%
echo   用户: %DB_USER%
echo   数据库: %DB_NAME%
echo.

:: 选择执行模式
echo 请选择执行模式:
echo   1. 执行基础SQL文件 (01,02,04,05,06) - 推荐
echo   2. 执行全部SQL文件 (01,02,03,04,05,06) - 包含大规模数据
echo.
set /p "EXEC_MODE=请输入选择 (1 或 2): "

if "%EXEC_MODE%"=="1" (
    set "EXEC_FILES=01_create_tables.sql 02_insert_basic_data.sql 04_complete_all_tables.sql 05_data_validation.sql 06_data_analysis.sql"
    echo ✅ 选择基础模式执行
) else if "%EXEC_MODE%"=="2" (
    set "EXEC_FILES=01_create_tables.sql 02_insert_basic_data.sql 03_insert_large_data.sql 04_complete_all_tables.sql 05_data_validation.sql 06_data_analysis.sql"
    echo ✅ 选择完整模式执行
) else (
    echo ❌ 无效选择，默认使用基础模式
    set "EXEC_FILES=01_create_tables.sql 02_insert_basic_data.sql 04_complete_all_tables.sql 05_data_validation.sql 06_data_analysis.sql"
)
echo.

:: 测试数据库连接
echo [1/6] 测试数据库连接...
%MYSQL_CMD% -e "SELECT 'Database connection successful!' as status;" 2>nul
if %ERRORLEVEL% neq 0 (
    echo ❌ 数据库连接失败，请检查连接信息！
    pause
    exit /b 1
)
echo ✅ 数据库连接成功！
echo.

:: 动态执行SQL脚本
set "STEP=2"
for %%f in (%EXEC_FILES%) do (
    echo [!STEP!/6] 执行 %%f...
    %MYSQL_CMD% < "%SQL_DIR%\%%f"
    if !ERRORLEVEL! neq 0 (
        echo ❌ %%f 执行失败！
        echo.
        echo 错误详情:
        if "%%f"=="01_create_tables.sql" echo   - 表结构创建失败，请检查数据库权限
        if "%%f"=="02_insert_basic_data.sql" echo   - 基础数据插入失败，可能存在数据冲突
        if "%%f"=="03_insert_large_data.sql" echo   - 大规模数据生成失败，可能是内存不足
        if "%%f"=="04_complete_all_tables.sql" echo   - 完整表数据补充失败，请检查表结构
        if "%%f"=="05_data_validation.sql" echo   - 数据验证失败，请检查数据完整性
        if "%%f"=="06_data_analysis.sql" echo   - 数据分析失败，请检查数据质量
        echo.
        pause
        exit /b 1
    )
    if "%%f"=="01_create_tables.sql" echo ✅ 表结构创建完成
    if "%%f"=="02_insert_basic_data.sql" echo ✅ 基础数据插入完成
    if "%%f"=="03_insert_large_data.sql" echo ✅ 大规模数据生成完成
    if "%%f"=="04_complete_all_tables.sql" echo ✅ 完整表数据补充完成
    if "%%f"=="05_data_validation.sql" echo ✅ 数据验证完成
    if "%%f"=="06_data_analysis.sql" echo ✅ 数据分析完成
    echo.
    set /a STEP+=1
)

echo =====================================================
echo          🎉 数据库初始化完成！
echo =====================================================
echo.
echo 执行结果:
if "%EXEC_MODE%"=="1" (
    echo   ✅ 1. 表结构创建 - 35张表
    echo   ✅ 2. 基础数据插入 - 角色权限、学院、教室等
    echo   ✅ 3. 完整表数据补充 - 确保所有表都有数据
    echo   ✅ 4. 数据验证 - 完整性和一致性检查
    echo   ✅ 5. 数据分析 - 性能和质量分析
    echo.
    echo 📊 数据统计 (基础模式):
    %MYSQL_CMD% -e "USE %DB_NAME%; SELECT '用户总数' as 项目, COUNT(*) as 数量 FROM tb_user WHERE deleted=0 UNION SELECT '学生总数', COUNT(*) FROM tb_student WHERE deleted=0 UNION SELECT '课程总数', COUNT(*) FROM tb_course WHERE deleted=0;" 2>nul
) else (
    echo   ✅ 1. 表结构创建 - 35张表
    echo   ✅ 2. 基础数据插入 - 角色权限、学院、教室等
    echo   ✅ 3. 大规模数据生成 - 15,000用户 + 完整业务数据
    echo   ✅ 4. 完整表数据补充 - 确保所有表都有数据
    echo   ✅ 5. 数据验证 - 完整性和一致性检查
    echo   ✅ 6. 数据分析 - 性能和质量分析
    echo.
    echo 📊 数据统计 (完整模式):
    %MYSQL_CMD% -e "USE %DB_NAME%; SELECT '用户总数' as 项目, COUNT(*) as 数量 FROM tb_user WHERE deleted=0 UNION SELECT '学生总数', COUNT(*) FROM tb_student WHERE deleted=0 UNION SELECT '课程总数', COUNT(*) FROM tb_course WHERE deleted=0 UNION SELECT '成绩记录', COUNT(*) FROM tb_grade WHERE deleted=0;" 2>nul
)
echo.
echo 🔑 默认管理员账号:
echo   用户名: admin
echo   密码: admin123
echo.
echo ✨ 系统现在可以正常使用了！
echo =====================================================

pause