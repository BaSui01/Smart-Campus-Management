@echo off
chcp 65001 >nul
echo ================================================
echo 校园管理系统数据库初始化脚本
echo ================================================
echo.

REM 数据库连接配置
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=xiaoxiao123
set DB_NAME=campus_management_db

REM MySQL客户端路径（请根据实际安装路径修改）
set MYSQL_PATH=mysql
REM set MYSQL_PATH="C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"

echo 检查MySQL客户端...
%MYSQL_PATH% --version >nul 2>&1
if errorlevel 1 (
    echo ✗ 错误: 无法找到MySQL客户端
    echo 请确保MySQL已安装并且mysql命令在PATH中
    pause
    exit /b 1
) else (
    echo ✓ MySQL客户端检查通过
)

echo.
echo 测试数据库连接...
echo SELECT 1; | %MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% >nul 2>&1
if errorlevel 1 (
    echo ✗ 错误: 无法连接到数据库
    echo 请检查数据库服务是否启动，以及连接参数是否正确
    echo 主机: %DB_HOST%, 端口: %DB_PORT%, 用户: %DB_USER%
    pause
    exit /b 1
) else (
    echo ✓ 数据库连接测试通过
)

echo.
echo 开始执行数据库初始化...
echo 开始时间: %date% %time%
echo.

echo [1/9] 执行脚本: 00_create_database.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 00_create_database.sql
if errorlevel 1 goto error
echo ✓ 00_create_database.sql 执行成功
echo.

echo [2/9] 执行脚本: 01_create_tables.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 01_create_tables.sql
if errorlevel 1 goto error
echo ✓ 01_create_tables.sql 执行成功
echo.

echo [3/9] 执行脚本: 02_create_indexes.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 02_create_indexes.sql
if errorlevel 1 goto error
echo ✓ 02_create_indexes.sql 执行成功
echo.

echo [4/9] 执行脚本: 03_insert_base_data.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 03_insert_base_data.sql
if errorlevel 1 goto error
echo ✓ 03_insert_base_data.sql 执行成功
echo.

echo [5/9] 执行脚本: 04_insert_user_student_data.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 04_insert_user_student_data.sql
if errorlevel 1 goto error
echo ✓ 04_insert_user_student_data.sql 执行成功
echo.

echo [6/9] 执行脚本: 05_insert_course_data.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 05_insert_course_data.sql
if errorlevel 1 goto error
echo ✓ 05_insert_course_data.sql 执行成功
echo.

echo [7/9] 执行脚本: 06_insert_selection_data.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 06_insert_selection_data.sql
if errorlevel 1 goto error
echo ✓ 06_insert_selection_data.sql 执行成功
echo.

echo [8/9] 执行脚本: 07_insert_grade_data.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 07_insert_grade_data.sql
if errorlevel 1 goto error
echo ✓ 07_insert_grade_data.sql 执行成功
echo.

echo [9/10] 执行脚本: 08_insert_payment_data.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 08_insert_payment_data.sql
if errorlevel 1 goto error
echo ✓ 08_insert_payment_data.sql 执行成功
echo.

echo [10/10] 执行脚本: 09_final_statistics.sql
%MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --default-character-set=utf8mb4 < 09_final_statistics.sql
if errorlevel 1 goto error
echo ✓ 09_final_statistics.sql 执行成功
echo.

echo 执行最终数据统计...
echo USE campus_management_db; SELECT 'tb_user' AS table_name, COUNT(*) AS record_count FROM tb_user UNION ALL SELECT 'tb_student', COUNT(*) FROM tb_student UNION ALL SELECT 'tb_course', COUNT(*) FROM tb_course UNION ALL SELECT 'tb_course_schedule', COUNT(*) FROM tb_course_schedule UNION ALL SELECT 'tb_course_selection', COUNT(*) FROM tb_course_selection UNION ALL SELECT 'tb_grade', COUNT(*) FROM tb_grade UNION ALL SELECT 'tb_payment_record', COUNT(*) FROM tb_payment_record; | %MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% --table

echo.
echo ================================================
echo 数据库初始化完成！
echo 结束时间: %date% %time%
echo.
echo 数据库信息:
echo   数据库名: %DB_NAME%
echo   主机地址: %DB_HOST%:%DB_PORT%
echo   字符编码: UTF-8 (utf8mb4)
echo.
echo 默认管理员账户:
echo   用户名: admin
echo   密码: admin123
echo.
echo 您现在可以启动校园管理系统应用程序了！
echo ================================================
echo.

set /p openTool="是否要打开MySQL命令行客户端查看数据库? (y/n): "
if /i "%openTool%"=="y" (
    echo 正在打开MySQL命令行客户端...
    %MYSQL_PATH% -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p%DB_PASSWORD% %DB_NAME%
)

pause
exit /b 0

:error
echo.
echo ✗ 错误: 脚本执行失败
echo 请检查错误信息并重新运行脚本
pause
exit /b 1
