# 智慧校园管理系统 API 测试脚本

$baseUrl = "http://localhost:8889/api/v1"

Write-Host "=== 智慧校园管理系统 API 测试 ===" -ForegroundColor Green

# 测试健康检查
Write-Host "`n1. 测试健康检查..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/health" -Method GET
    Write-Host "✅ 健康检查成功: $($response | ConvertTo-Json)" -ForegroundColor Green
} catch {
    Write-Host "❌ 健康检查失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 测试用户注册
Write-Host "`n2. 测试用户注册..." -ForegroundColor Yellow
$registerData = @{
    username = "admin"
    password = "admin123"
    realName = "Administrator"
    email = "admin@campus.edu"
    phone = "13800138000"
    gender = "其他"
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST -Body $registerData -ContentType "application/json; charset=utf-8"
    Write-Host "✅ 用户注册成功: $($response | ConvertTo-Json)" -ForegroundColor Green
    $global:token = $response.data.token
} catch {
    Write-Host "❌ 用户注册失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorResponse = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorResponse)
        $errorBody = $reader.ReadToEnd()
        Write-Host "错误详情: $errorBody" -ForegroundColor Red
    }
}

# 测试用户登录
Write-Host "`n3. 测试用户登录..." -ForegroundColor Yellow
$loginData = @{
    username = "admin"
    password = "admin123"
    userType = "ADMIN"
} | ConvertTo-Json -Depth 10

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginData -ContentType "application/json; charset=utf-8"
    Write-Host "✅ 用户登录成功: $($response | ConvertTo-Json)" -ForegroundColor Green
    $global:token = $response.data.token
} catch {
    Write-Host "❌ 用户登录失败: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $errorResponse = $_.Exception.Response.GetResponseStream()
        $reader = New-Object System.IO.StreamReader($errorResponse)
        $errorBody = $reader.ReadToEnd()
        Write-Host "错误详情: $errorBody" -ForegroundColor Red
    }
}

# 测试获取用户信息
if ($global:token) {
    Write-Host "`n4. 测试获取用户信息..." -ForegroundColor Yellow
    try {
        $headers = @{
            "Authorization" = "Bearer $global:token"
        }
        $response = Invoke-RestMethod -Uri "$baseUrl/auth/me" -Method GET -Headers $headers
        Write-Host "✅ 获取用户信息成功: $($response | ConvertTo-Json)" -ForegroundColor Green
    } catch {
        Write-Host "❌ 获取用户信息失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
