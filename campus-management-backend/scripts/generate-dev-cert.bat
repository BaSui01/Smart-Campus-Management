@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 智慧校园管理系统 - 开发环境SSL证书生成脚本（Windows版本）
REM 用于生成开发环境的自签名SSL证书

echo.
echo ===== 智慧校园管理系统 SSL证书生成工具 =====
echo.

REM 配置变量
set KEYSTORE_DIR=src\main\resources\keystore
set KEYSTORE_FILE=campus-keystore.p12
set KEYSTORE_PATH=%KEYSTORE_DIR%\%KEYSTORE_FILE%
set KEYSTORE_PASSWORD=campus-secure-2024
set CERT_ALIAS=campus-management
set VALIDITY_DAYS=365

REM 证书信息
set CERT_CN=localhost
set CERT_OU=Campus Management
set CERT_O=Smart Campus
set CERT_L=Beijing
set CERT_ST=Beijing
set CERT_C=CN

REM 检查Java环境
where keytool >nul 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到keytool命令，请确保已安装Java JDK并配置PATH环境变量
    pause
    exit /b 1
)

echo [✓] Java环境检查通过

REM 创建证书目录
if not exist "%KEYSTORE_DIR%" (
    echo [信息] 创建证书目录: %KEYSTORE_DIR%
    mkdir "%KEYSTORE_DIR%"
)

REM 检查是否已存在证书
if exist "%KEYSTORE_PATH%" (
    echo [警告] 证书文件已存在 (%KEYSTORE_PATH%)
    set /p OVERWRITE="是否覆盖现有证书? (y/N): "
    if /i not "!OVERWRITE!"=="y" (
        echo [信息] 操作已取消
        pause
        exit /b 0
    )
    del "%KEYSTORE_PATH%"
)

echo [信息] 开始生成SSL证书...

REM 生成自签名证书
keytool -genkeypair ^
    -alias "%CERT_ALIAS%" ^
    -keyalg RSA ^
    -keysize 2048 ^
    -storetype PKCS12 ^
    -keystore "%KEYSTORE_PATH%" ^
    -validity %VALIDITY_DAYS% ^
    -storepass "%KEYSTORE_PASSWORD%" ^
    -keypass "%KEYSTORE_PASSWORD%" ^
    -dname "CN=%CERT_CN%, OU=%CERT_OU%, O=%CERT_O%, L=%CERT_L%, ST=%CERT_ST%, C=%CERT_C%" ^
    -ext SAN=dns:localhost,dns:127.0.0.1,ip:127.0.0.1

if %errorlevel% equ 0 (
    echo [✓] SSL证书生成成功
) else (
    echo [✗] SSL证书生成失败
    pause
    exit /b 1
)

echo.
echo ===== 证书信息 =====
keytool -list -v -keystore "%KEYSTORE_PATH%" -storepass "%KEYSTORE_PASSWORD%" -alias "%CERT_ALIAS%"

REM 生成配置信息
echo.
echo ===== 配置信息 =====
echo [✓] 证书文件路径: %KEYSTORE_PATH%
echo [✓] 证书密码: %KEYSTORE_PASSWORD%
echo [✓] 证书别名: %CERT_ALIAS%
echo [✓] 有效期: %VALIDITY_DAYS% 天

REM 生成环境变量配置
set ENV_FILE=scripts\dev-ssl-env.bat
echo @echo off > "%ENV_FILE%"
echo REM 开发环境SSL配置环境变量 >> "%ENV_FILE%"
echo set SSL_KEYSTORE_PASSWORD=%KEYSTORE_PASSWORD% >> "%ENV_FILE%"
echo set SPRING_PROFILES_ACTIVE=dev >> "%ENV_FILE%"
echo set SERVER_SSL_ENABLED=true >> "%ENV_FILE%"
echo set SERVER_PORT=8443 >> "%ENV_FILE%"
echo. >> "%ENV_FILE%"
echo echo 开发环境SSL配置已加载 >> "%ENV_FILE%"
echo echo HTTPS端口: 8443 >> "%ENV_FILE%"
echo echo HTTP端口已禁用 >> "%ENV_FILE%"

echo.
echo ===== 使用说明 =====
echo [1] 加载环境变量:
echo     scripts\dev-ssl-env.bat
echo.
echo [2] 启动应用:
echo     mvn spring-boot:run
echo.
echo [3] 访问应用:
echo     https://localhost:8443/api/v1/health
echo.
echo [注意] 浏览器会显示安全警告，这是正常的（自签名证书）
echo [注意] 在浏览器中点击'高级' -^> '继续访问localhost'

REM 生成测试脚本
set TEST_SCRIPT=scripts\test-ssl.bat
echo @echo off > "%TEST_SCRIPT%"
echo chcp 65001 ^>nul >> "%TEST_SCRIPT%"
echo echo 测试SSL连接... >> "%TEST_SCRIPT%"
echo echo. >> "%TEST_SCRIPT%"
echo. >> "%TEST_SCRIPT%"
echo echo 1. 测试HTTPS健康检查端点: >> "%TEST_SCRIPT%"
echo curl -k -s https://localhost:8443/api/v1/health ^|^| echo 连接失败 >> "%TEST_SCRIPT%"
echo. >> "%TEST_SCRIPT%"
echo echo. >> "%TEST_SCRIPT%"
echo echo 2. 测试SSL证书信息: >> "%TEST_SCRIPT%"
echo echo ^| openssl s_client -connect localhost:8443 -servername localhost 2^>nul ^| openssl x509 -noout -dates >> "%TEST_SCRIPT%"
echo. >> "%TEST_SCRIPT%"
echo echo. >> "%TEST_SCRIPT%"
echo echo 测试完成 >> "%TEST_SCRIPT%"
echo pause >> "%TEST_SCRIPT%"

echo.
echo [✓] 证书生成完成！
echo [信息] 环境变量脚本: %ENV_FILE%
echo [信息] SSL测试脚本: %TEST_SCRIPT%
echo.
echo [下一步] 运行 '%ENV_FILE%' 加载SSL配置

pause
