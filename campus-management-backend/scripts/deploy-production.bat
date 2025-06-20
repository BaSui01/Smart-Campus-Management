@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 智慧校园管理系统 - 生产环境部署脚本（Windows版本）
REM 用于自动化部署生产环境

echo.
echo ===== 智慧校园管理系统生产环境部署脚本 =====
echo.

REM 配置变量
set APP_NAME=campus-management-backend
set APP_VERSION=1.0.0
set DEPLOY_DIR=C:\opt\campus
set BACKUP_DIR=C:\opt\campus\backups
set LOG_DIR=C:\var\log\campus
set SERVICE_NAME=CampusManagement
set JAR_NAME=%APP_NAME%-%APP_VERSION%.jar
set CONFIG_DIR=C:\opt\campus\config

REM 数据库配置
if "%DB_HOST%"=="" set DB_HOST=localhost
if "%DB_PORT%"=="" set DB_PORT=3306
if "%DB_NAME%"=="" set DB_NAME=campus_management_db
if "%DB_USERNAME%"=="" set DB_USERNAME=campus_user

REM 服务端口
if "%SERVER_PORT%"=="" set SERVER_PORT=8443
if "%MANAGEMENT_PORT%"=="" set MANAGEMENT_PORT=8444

echo [信息] 应用名称: %APP_NAME%
echo [信息] 版本: %APP_VERSION%
echo [信息] 部署目录: %DEPLOY_DIR%
echo.

REM 检查管理员权限
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 此脚本需要管理员权限运行
    echo [提示] 请右键点击"以管理员身份运行"
    pause
    exit /b 1
)

REM 检查环境变量
call :check_environment
if %errorlevel% neq 0 exit /b 1

REM 检查系统依赖
call :check_dependencies
if %errorlevel% neq 0 exit /b 1

REM 创建必要的目录
call :create_directories

REM 备份当前版本
call :backup_current_version

REM 停止服务
call :stop_service

REM 部署应用
call :deploy_application
if %errorlevel% neq 0 exit /b 1

REM 创建Windows服务
call :create_windows_service

REM 启动服务
call :start_service

REM 健康检查
call :health_check

REM 显示部署信息
call :show_deployment_info

echo.
echo [✓] 部署完成！
pause
exit /b 0

REM ================================
REM 函数定义
REM ================================

:check_environment
echo [信息] 检查环境变量...

set missing_vars=

if "%DB_PASSWORD%"=="" set missing_vars=!missing_vars! DB_PASSWORD
if "%REDIS_PASSWORD%"=="" set missing_vars=!missing_vars! REDIS_PASSWORD
if "%JWT_SECRET%"=="" set missing_vars=!missing_vars! JWT_SECRET
if "%ENCRYPTION_SECRET_KEY%"=="" set missing_vars=!missing_vars! ENCRYPTION_SECRET_KEY
if "%SSL_KEYSTORE_PASSWORD%"=="" set missing_vars=!missing_vars! SSL_KEYSTORE_PASSWORD

if not "!missing_vars!"=="" (
    echo [错误] 缺少必要的环境变量: !missing_vars!
    echo.
    echo [提示] 请设置以下环境变量:
    echo set DB_PASSWORD=your-db-password
    echo set REDIS_PASSWORD=your-redis-password
    echo set JWT_SECRET=your-jwt-secret
    echo set ENCRYPTION_SECRET_KEY=your-encryption-key
    echo set SSL_KEYSTORE_PASSWORD=your-keystore-password
    exit /b 1
)

echo [✓] 环境变量检查通过
exit /b 0

:check_dependencies
echo [信息] 检查系统依赖...

REM 检查Java
where java >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到Java，请安装Java 17或更高版本
    exit /b 1
)

REM 检查Java版本
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr "version"') do (
    set java_version=%%i
    set java_version=!java_version:"=!
    for /f "tokens=1 delims=." %%j in ("!java_version!") do set java_major=%%j
)

if !java_major! lss 17 (
    echo [错误] Java版本过低，需要Java 17或更高版本
    exit /b 1
)

REM 检查NSSM（用于创建Windows服务）
where nssm >nul 2>&1
if %errorlevel% neq 0 (
    echo [警告] 未找到NSSM，将尝试下载...
    call :download_nssm
)

echo [✓] 系统依赖检查通过
exit /b 0

:create_directories
echo [信息] 创建部署目录...

if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
if not exist "%CONFIG_DIR%" mkdir "%CONFIG_DIR%"
if not exist "%DEPLOY_DIR%\uploads" mkdir "%DEPLOY_DIR%\uploads"
if not exist "%DEPLOY_DIR%\keystore" mkdir "%DEPLOY_DIR%\keystore"

echo [✓] 目录创建完成
exit /b 0

:backup_current_version
if exist "%DEPLOY_DIR%\%JAR_NAME%" (
    echo [信息] 备份当前版本...
    
    for /f "tokens=1-3 delims=/ " %%a in ('date /t') do set backup_date=%%c%%a%%b
    for /f "tokens=1-2 delims=: " %%a in ('time /t') do set backup_time=%%a%%b
    set backup_timestamp=!backup_date!_!backup_time!
    set backup_file=%BACKUP_DIR%\%APP_NAME%_!backup_timestamp!.jar
    
    copy "%DEPLOY_DIR%\%JAR_NAME%" "!backup_file!" >nul
    echo [✓] 当前版本已备份到: !backup_file!
)
exit /b 0

:stop_service
echo [信息] 停止服务...

sc query "%SERVICE_NAME%" >nul 2>&1
if %errorlevel% equ 0 (
    sc stop "%SERVICE_NAME%" >nul 2>&1
    timeout /t 5 /nobreak >nul
    echo [✓] 服务已停止
) else (
    echo [信息] 服务未运行
)
exit /b 0

:deploy_application
echo [信息] 部署应用...

REM 检查JAR文件是否存在
if not exist "target\%JAR_NAME%" (
    echo [错误] 未找到JAR文件 target\%JAR_NAME%
    echo [提示] 请先运行: mvn clean package -Dmaven.test.skip=true
    exit /b 1
)

REM 复制JAR文件
copy "target\%JAR_NAME%" "%DEPLOY_DIR%\" >nul
if %errorlevel% neq 0 (
    echo [错误] 复制JAR文件失败
    exit /b 1
)

REM 复制配置文件
if exist "src\main\resources\application-prod.yml" (
    copy "src\main\resources\application-prod.yml" "%CONFIG_DIR%\" >nul
)

REM 复制SSL证书（如果存在）
if exist "src\main\resources\keystore\campus-keystore.p12" (
    copy "src\main\resources\keystore\campus-keystore.p12" "%DEPLOY_DIR%\keystore\" >nul
)

echo [✓] 应用部署完成
exit /b 0

:create_windows_service
echo [信息] 创建Windows服务...

REM 删除现有服务（如果存在）
sc query "%SERVICE_NAME%" >nul 2>&1
if %errorlevel% equ 0 (
    sc delete "%SERVICE_NAME%" >nul 2>&1
)

REM 使用NSSM创建服务
nssm install "%SERVICE_NAME%" java >nul 2>&1
nssm set "%SERVICE_NAME%" AppParameters "-Xms512m -Xmx2g -XX:+UseG1GC -Dspring.profiles.active=prod -Dspring.config.location=classpath:/application.yml,%CONFIG_DIR%\application-prod.yml -Dlogging.file.name=%LOG_DIR%\campus-management.log -jar %DEPLOY_DIR%\%JAR_NAME%" >nul 2>&1
nssm set "%SERVICE_NAME%" AppDirectory "%DEPLOY_DIR%" >nul 2>&1
nssm set "%SERVICE_NAME%" DisplayName "Smart Campus Management System" >nul 2>&1
nssm set "%SERVICE_NAME%" Description "智慧校园管理系统后端服务" >nul 2>&1
nssm set "%SERVICE_NAME%" Start SERVICE_AUTO_START >nul 2>&1

REM 设置环境变量
nssm set "%SERVICE_NAME%" AppEnvironmentExtra "DB_HOST=%DB_HOST%" "DB_PORT=%DB_PORT%" "DB_NAME=%DB_NAME%" "DB_USERNAME=%DB_USERNAME%" "DB_PASSWORD=%DB_PASSWORD%" "REDIS_PASSWORD=%REDIS_PASSWORD%" "JWT_SECRET=%JWT_SECRET%" "ENCRYPTION_SECRET_KEY=%ENCRYPTION_SECRET_KEY%" "SSL_KEYSTORE_PASSWORD=%SSL_KEYSTORE_PASSWORD%" "SERVER_PORT=%SERVER_PORT%" >nul 2>&1

echo [✓] Windows服务创建完成
exit /b 0

:start_service
echo [信息] 启动服务...

sc start "%SERVICE_NAME%" >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 服务启动失败
    exit /b 1
)

echo [信息] 等待服务启动...
timeout /t 15 /nobreak >nul

sc query "%SERVICE_NAME%" | findstr "RUNNING" >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] 服务启动成功
) else (
    echo [错误] 服务启动失败
    exit /b 1
)
exit /b 0

:health_check
echo [信息] 执行健康检查...

set max_attempts=30
set attempt=1

:health_check_loop
curl -k -s "https://localhost:%SERVER_PORT%/api/v1/system/health" >nul 2>&1
if %errorlevel% equ 0 (
    echo [✓] 健康检查通过
    exit /b 0
)

echo [信息] 健康检查尝试 !attempt!/%max_attempts%...
timeout /t 5 /nobreak >nul
set /a attempt+=1

if !attempt! leq %max_attempts% goto health_check_loop

echo [警告] 健康检查失败，请检查应用状态
exit /b 1

:show_deployment_info
echo.
echo ===== 部署完成 =====
echo [信息] 应用名称: %APP_NAME%
echo [信息] 版本: %APP_VERSION%
echo [信息] 部署目录: %DEPLOY_DIR%
echo [信息] 配置目录: %CONFIG_DIR%
echo [信息] 日志目录: %LOG_DIR%
echo [信息] 服务名称: %SERVICE_NAME%
echo.
echo [访问地址]
echo   HTTPS: https://localhost:%SERVER_PORT%
echo   健康检查: https://localhost:%SERVER_PORT%/api/v1/system/health
echo   API文档: https://localhost:%SERVER_PORT%/api/v1/swagger-ui.html
echo.
echo [常用命令]
echo   查看服务状态: sc query %SERVICE_NAME%
echo   重启服务: sc stop %SERVICE_NAME% ^&^& sc start %SERVICE_NAME%
echo   停止服务: sc stop %SERVICE_NAME%
echo   删除服务: sc delete %SERVICE_NAME%
echo.
exit /b 0

:download_nssm
echo [信息] 下载NSSM...
REM 这里可以添加下载NSSM的逻辑
echo [提示] 请手动下载NSSM并添加到PATH环境变量中
echo [下载地址] https://nssm.cc/download
exit /b 0
