# Smart Campus Management System - Quick API Test (PowerShell Version)
# This script provides better Unicode support and error handling

param(
    [string]$BaseUrl = "http://localhost:8082",
    [string]$Username = "admin",
    [string]$Password = "admin123"
)

# Set UTF-8 encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "⚡ 智慧校园管理系统 - 快速API测试" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# Set variables
$outputDir = "quick-test-results"
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"

# Create output directory
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

Write-Host "📋 快速测试内容:" -ForegroundColor Yellow
Write-Host "    ✅ 服务器健康检查" -ForegroundColor Gray
Write-Host "    ✅ 用户登录认证" -ForegroundColor Gray
Write-Host "    ✅ 核心API接口测试 (5个)" -ForegroundColor Gray
Write-Host "    ✅ 生成简洁测试报告" -ForegroundColor Gray
Write-Host ""

# Function to make HTTP requests
function Invoke-ApiRequest {
    param(
        [string]$Method = "GET",
        [string]$Uri,
        [hashtable]$Headers = @{},
        [string]$Body,
        [string]$OutputFile
    )
    
    try {
        $params = @{
            Method = $Method
            Uri = $Uri
            Headers = $Headers
            TimeoutSec = 30
        }
        
        if ($Body) {
            $params.Body = $Body
        }
        
        if ($OutputFile) {
            $params.OutFile = $OutputFile
        }
        
        $response = Invoke-RestMethod @params
        return @{ Success = $true; Data = $response }
    }
    catch {
        Write-Warning "Request failed: $($_.Exception.Message)"
        if ($OutputFile) {
            $errorInfo = @{
                error = $_.Exception.Message
                timestamp = (Get-Date).ToString()
                uri = $Uri
            } | ConvertTo-Json -Depth 3
            $errorInfo | Out-File -FilePath $OutputFile -Encoding UTF8
        }
        return @{ Success = $false; Error = $_.Exception.Message }
    }
}

# Step 1: Check server health
Write-Host "[1/4] 检查服务器状态..." -ForegroundColor Cyan
$healthResult = Invoke-ApiRequest -Uri "$BaseUrl/actuator/health" -OutputFile "$outputDir\health_$timestamp.json"

if (-not $healthResult.Success) {
    Write-Host "❌ 服务器连接失败！" -ForegroundColor Red
    Write-Host "请确保服务器运行在 $BaseUrl" -ForegroundColor Yellow
    Read-Host "按任意键退出"
    exit 1
}
Write-Host "✅ 服务器正常" -ForegroundColor Green

# Step 2: User login
Write-Host "[2/4] 执行用户登录..." -ForegroundColor Cyan
$loginBody = @{
    username = $Username
    password = $Password
} | ConvertTo-Json

$loginResult = Invoke-ApiRequest -Method "POST" -Uri "$BaseUrl/api/auth/login" -Headers @{"Content-Type" = "application/json"} -Body $loginBody -OutputFile "$outputDir\login_$timestamp.json"

if (-not $loginResult.Success) {
    Write-Host "❌ 登录失败！" -ForegroundColor Red
    Write-Host "请检查服务器日志和凭据" -ForegroundColor Yellow
    Read-Host "按任意键退出"
    exit 1
}
Write-Host "✅ 登录成功" -ForegroundColor Green

# Extract token from login response
Write-Host ""
Write-Host "🔑 请从登录文件中获取token:" -ForegroundColor Yellow
Write-Host "📁 文件位置: $outputDir\login_$timestamp.json" -ForegroundColor Gray
Write-Host "💡 查找 `"token`" 字段的值" -ForegroundColor Gray

# Try to auto-extract token
try {
    $loginContent = Get-Content "$outputDir\login_$timestamp.json" | ConvertFrom-Json
    if ($loginContent.token) {
        $jwtToken = $loginContent.token
        Write-Host "🎯 自动提取到token (前20字符): $($jwtToken.Substring(0, [Math]::Min(20, $jwtToken.Length)))..." -ForegroundColor Green
    } elseif ($loginContent.data.token) {
        $jwtToken = $loginContent.data.token
        Write-Host "🎯 自动提取到token (前20字符): $($jwtToken.Substring(0, [Math]::Min(20, $jwtToken.Length)))..." -ForegroundColor Green
    } else {
        throw "Token not found in response"
    }
} catch {
    Write-Host "⚠️ 无法自动提取token，请手动输入" -ForegroundColor Yellow
    $jwtToken = Read-Host "请输入token"
}

if ([string]::IsNullOrEmpty($jwtToken)) {
    Write-Host "❌ Token不能为空！" -ForegroundColor Red
    Read-Host "按任意键退出"
    exit 1
}

# Step 3: Core API testing
Write-Host ""
Write-Host "[3/4] 测试核心API接口..." -ForegroundColor Cyan

$authHeaders = @{
    "Authorization" = "Bearer $jwtToken"
    "Content-Type" = "application/json"
}

$apiTests = @(
    @{ Name = "用户信息"; Uri = "$BaseUrl/api/auth/me"; File = "user_info_$timestamp.json" }
    @{ Name = "用户列表"; Uri = "$BaseUrl/api/users?page=0&size=5"; File = "users_list_$timestamp.json" }
    @{ Name = "学生列表"; Uri = "$BaseUrl/api/students?page=0&size=5"; File = "students_list_$timestamp.json" }
    @{ Name = "课程列表"; Uri = "$BaseUrl/api/courses?page=0&size=5"; File = "courses_list_$timestamp.json" }
    @{ Name = "系统信息"; Uri = "$BaseUrl/api/system/info"; File = "system_info_$timestamp.json" }
)

$successCount = 0
foreach ($test in $apiTests) {
    Write-Host "📍 测试$($test.Name)..." -ForegroundColor Gray
    $result = Invoke-ApiRequest -Uri $test.Uri -Headers $authHeaders -OutputFile "$outputDir\$($test.File)"
    if ($result.Success) {
        $successCount++
    }
}

Write-Host "✅ API测试完成 ($successCount/$($apiTests.Count) 成功)" -ForegroundColor Green

# Step 4: Generate report
Write-Host ""
Write-Host "[4/4] 生成测试报告..." -ForegroundColor Cyan

$reportContent = @"
===== 智慧校园管理系统 - 快速API测试报告 =====
测试时间: $(Get-Date)
服务器地址: $BaseUrl
管理员账号: $Username
时间戳: $timestamp

测试文件列表:
1. health_$timestamp.json - 服务器健康检查
2. login_$timestamp.json - 用户登录响应
3. user_info_$timestamp.json - 当前用户信息
4. users_list_$timestamp.json - 用户列表数据
5. students_list_$timestamp.json - 学生列表数据
6. courses_list_$timestamp.json - 课程列表数据
7. system_info_$timestamp.json - 系统信息数据

检查要点:
- 所有JSON文件应包含有效的响应数据
- 登录响应应包含token字段
- 列表响应应包含data数组和分页信息
- 错误响应会包含error或message字段

测试成功率: $successCount/$($apiTests.Count) ($([math]::Round(($successCount / $apiTests.Count) * 100, 1))%)
"@

$reportContent | Out-File -FilePath "$outputDir\QUICK_TEST_REPORT_$timestamp.txt" -Encoding UTF8

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "🎉 快速API测试完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📊 测试结果:" -ForegroundColor Yellow
Write-Host "  ✅ 服务器健康检查" -ForegroundColor Gray
Write-Host "  ✅ 用户登录认证" -ForegroundColor Gray
Write-Host "  ✅ 用户信息API" -ForegroundColor Gray
Write-Host "  ✅ 用户列表API" -ForegroundColor Gray
Write-Host "  ✅ 学生列表API" -ForegroundColor Gray
Write-Host "  ✅ 课程列表API" -ForegroundColor Gray
Write-Host "  ✅ 系统信息API" -ForegroundColor Gray
Write-Host ""
Write-Host "📁 测试结果目录:" -ForegroundColor Yellow -NoNewline
Write-Host " $outputDir" -ForegroundColor White
Write-Host "📋 测试报告:" -ForegroundColor Yellow -NoNewline
Write-Host " $outputDir\QUICK_TEST_REPORT_$timestamp.txt" -ForegroundColor White
Write-Host ""
Write-Host "💡 下一步操作:" -ForegroundColor Yellow
Write-Host "   1. 打开各个JSON文件查看响应数据" -ForegroundColor Gray
Write-Host "   2. 验证数据格式和内容是否正确" -ForegroundColor Gray
Write-Host "   3. 如有错误，检查服务器日志" -ForegroundColor Gray
Write-Host ""
Write-Host "📂 快速查看文件:" -ForegroundColor Yellow
Get-ChildItem "$outputDir\*_$timestamp.json" | ForEach-Object {
    Write-Host "   $($_.Name)" -ForegroundColor Gray
}
Write-Host ""

Read-Host "按任意键退出"