@echo off
chcp 65001 >nul
echo.
echo ========================================
echo 🚀 智慧校园管理系统 API 测试执行器
echo ========================================
echo.

cd /d "%~dp0.."

echo 📋 测试执行计划:
echo    1. 编译项目
echo    2. 运行单元测试
echo    3. 运行集成测试
echo    4. 生成测试报告
echo.

echo ⏳ 正在编译项目...
call mvn clean compile -q
if %ERRORLEVEL% neq 0 (
    echo ❌ 项目编译失败！
    pause
    exit /b 1
)
echo ✅ 项目编译成功

echo.
echo ⏳ 正在运行API测试...
call mvn test -Dtest="com.campus.interfaces.rest.v1.*Test" -Dspring.profiles.active=test
if %ERRORLEVEL% neq 0 (
    echo ⚠️  部分测试失败，请查看详细报告
) else (
    echo ✅ 所有API测试通过
)

echo.
echo ⏳ 正在生成测试报告...
call mvn surefire-report:report -q
if %ERRORLEVEL% neq 0 (
    echo ⚠️  测试报告生成失败
) else (
    echo ✅ 测试报告生成成功
    echo 📊 报告位置: target\site\surefire-report.html
)

echo.
echo ⏳ 正在生成代码覆盖率报告...
call mvn jacoco:report -q
if %ERRORLEVEL% neq 0 (
    echo ⚠️  覆盖率报告生成失败
) else (
    echo ✅ 覆盖率报告生成成功
    echo 📊 报告位置: target\site\jacoco\index.html
)

echo.
echo ========================================
echo 📊 测试执行完成
echo ========================================
echo.
echo 📁 查看测试报告:
echo    - 测试结果: target\site\surefire-report.html
echo    - 覆盖率: target\site\jacoco\index.html
echo    - 详细日志: target\surefire-reports\
echo.

set /p choice="是否打开测试报告? (y/n): "
if /i "%choice%"=="y" (
    if exist "target\site\surefire-report.html" (
        start "" "target\site\surefire-report.html"
    )
    if exist "target\site\jacoco\index.html" (
        start "" "target\site\jacoco\index.html"
    )
)

pause
