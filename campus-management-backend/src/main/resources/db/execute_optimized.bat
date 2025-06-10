@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM =====================================================
REM Smart Campus Management System - Database Initialization
REM File: execute_optimized.bat
REM Description: Batch script to execute optimized SQL scripts
REM Version: 1.0.0
REM Created: 2025-01-27
REM =====================================================

echo.
echo =====================================================
echo Smart Campus Management System - Database Init
echo =====================================================
echo.

REM Database connection configuration
set DB_HOST=localhost
set DB_PORT=3306
set DB_USER=root
set DB_PASSWORD=xiaoxiao123

REM Check if MySQL is available
echo Checking MySQL connection...
mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% -e "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Cannot connect to MySQL database
    echo Please check:
    echo   1. MySQL service is running
    echo   2. Connection parameters are correct
    echo   3. User password is correct
    pause
    exit /b 1
)
echo [OK] MySQL connection successful
echo.

REM Display execution options
echo Please select execution plan:
echo 1. Optimized version ^(Recommended^) - Fast batch data generation
echo 2. Original version ^(Backup^) - Step-by-step data generation
echo 3. Exit
echo.

set /p choice="Enter your choice (1-3): "

if "%choice%"=="1" (
    set VERSION_NAME=Optimized Version
    set SQL_FILES=01_create_complete_tables.sql 02_insert_large_scale_data.sql 11_optimized_data_generation.sql 12_data_validation_and_statistics.sql
) else if "%choice%"=="2" (
    set VERSION_NAME=Original Version
    set SQL_FILES=01_create_complete_tables.sql 02_insert_large_scale_data.sql 08_complete_data_generation_fixed.sql 09_business_data_generation_fixed.sql 10_financial_and_other_data_fixed.sql 07_data_validation_and_statistics.sql
) else if "%choice%"=="3" (
    echo Execution cancelled
    exit /b 0
) else (
    echo Invalid choice, please run the script again
    pause
    exit /b 1
)

echo.
echo Starting execution of %VERSION_NAME% scripts
echo.

REM Display files to be executed
echo About to execute the following script files:
for %%f in (%SQL_FILES%) do (
    echo   • %%f
)
echo.

set /p confirm="Confirm execution? (y/N): "
if /i not "%confirm%"=="y" (
    echo Execution cancelled
    exit /b 0
)

echo.
echo =====================================================
echo Executing SQL Scripts
echo =====================================================
echo.

REM Initialize counters
set SUCCESS_COUNT=0
set FAIL_COUNT=0
set START_TIME=%time%

REM Execute each SQL file
for %%f in (%SQL_FILES%) do (
    echo Executing: %%f
    
    if not exist "%%f" (
        echo [ERROR] File not found: %%f
        set /a FAIL_COUNT+=1
    ) else (
        mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASSWORD% < "%%f"
        if errorlevel 1 (
            echo [ERROR] %%f execution failed
            set /a FAIL_COUNT+=1
            
            echo.
            set /p continue="Script execution failed, continue with remaining scripts? (y/N): "
            if /i not "!continue!"=="y" (
                echo User chose to stop execution
                goto :show_results
            )
        ) else (
            echo [OK] %%f executed successfully
            set /a SUCCESS_COUNT+=1
        )
    )
    echo.
)

:show_results
set END_TIME=%time%

echo.
echo =====================================================
echo Execution Statistics Report
echo =====================================================
echo Total Files: %SUCCESS_COUNT% + %FAIL_COUNT%
echo Successful: %SUCCESS_COUNT%
echo Failed: %FAIL_COUNT%
echo Start Time: %START_TIME%
echo End Time: %END_TIME%
echo.

if %FAIL_COUNT%==0 (
    echo =====================================================
    echo Database Initialization Completed!
    echo =====================================================
    echo Smart Campus Management System is ready for use.
    echo.
    echo Default admin account:
    echo   Username: admin001
    echo   Password: 123456
    echo   Email: admin001@university.edu.cn
) else (
    echo =====================================================
    echo Database Initialization Partially Completed
    echo =====================================================
    echo Some scripts failed to execute, please check error messages.
)

echo.
echo Press any key to exit...
pause >nul
