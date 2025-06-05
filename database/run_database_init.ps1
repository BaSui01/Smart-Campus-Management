# ================================================
# 校园管理系统数据库初始化脚本（修复版）
# 创建时间: 2025-06-05
# 编码: UTF-8
# 功能: 修复密码传递问题的版本
# ================================================

# 设置控制台编码为UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "================================================" -ForegroundColor Green
Write-Host "校园管理系统数据库初始化脚本（修复版）" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host ""

# 数据库连接配置
$DB_HOST = "localhost"
$DB_PORT = "3306"
$DB_USER = "root"
$DB_PASSWORD = "xiaoxiao123"
$DB_NAME = "campus_management_db"

# MySQL客户端路径
$MYSQL_PATH = "mysql"

# 脚本文件列表
$SCRIPT_FILES = @(
    "00_create_database.sql",
    "01_create_tables.sql", 
    "02_create_indexes.sql",
    "03_insert_base_data.sql",
    "04_insert_user_student_data.sql",
    "05_insert_course_data.sql",
    "06_insert_selection_data.sql",
    "07_insert_grade_data.sql",
    "08_insert_payment_data.sql",
    "09_final_statistics.sql"
)

# 构建MySQL连接参数
$mysqlArgs = @(
    "-h", $DB_HOST,
    "-P", $DB_PORT,
    "-u", $DB_USER,
    "-p$DB_PASSWORD",
    "--default-character-set=utf8mb4"
)

Write-Host "检查MySQL客户端..." -ForegroundColor Yellow
try {
    $mysqlVersion = & $MYSQL_PATH --version 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ MySQL客户端检查通过: $mysqlVersion" -ForegroundColor Green
    } else {
        throw "MySQL客户端不可用"
    }
} catch {
    Write-Host "✗ 错误: 无法找到MySQL客户端" -ForegroundColor Red
    Write-Host "请确保MySQL已安装并且mysql命令在PATH中" -ForegroundColor Red
    exit 1
}

Write-Host "检查SQL脚本文件..." -ForegroundColor Yellow
$missingFiles = @()
foreach ($file in $SCRIPT_FILES) {
    if (-not (Test-Path $file)) {
        $missingFiles += $file
    }
}

if ($missingFiles.Count -gt 0) {
    Write-Host "✗ 错误: 以下SQL脚本文件不存在:" -ForegroundColor Red
    foreach ($file in $missingFiles) {
        Write-Host "  - $file" -ForegroundColor Red
    }
    exit 1
} else {
    Write-Host "✓ 所有SQL脚本文件检查通过" -ForegroundColor Green
}

Write-Host ""
Write-Host "开始执行数据库初始化..." -ForegroundColor Green
Write-Host "开始时间: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""

# 执行每个SQL脚本
$totalFiles = $SCRIPT_FILES.Count
$currentFile = 0

foreach ($file in $SCRIPT_FILES) {
    $currentFile++
    Write-Host "[$currentFile/$totalFiles] 执行脚本: $file" -ForegroundColor Yellow
    
    try {
        $startTime = Get-Date
        
        # 使用Start-Process来执行mysql命令
        $mysqlCmd = "mysql"
        $mysqlArgs = "-h", $DB_HOST, "-P", $DB_PORT, "-u", $DB_USER, "-p$DB_PASSWORD", "--default-character-set=utf8mb4"

        # 读取SQL文件内容并通过管道传递
        $sqlContent = Get-Content $file -Raw -Encoding UTF8
        $result = $sqlContent | & $mysqlCmd $mysqlArgs 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            $endTime = Get-Date
            $duration = ($endTime - $startTime).TotalSeconds
            Write-Host "✓ $file 执行成功 (耗时: $([math]::Round($duration, 2))秒)" -ForegroundColor Green
        } else {
            Write-Host "✗ $file 执行失败，退出码: $LASTEXITCODE" -ForegroundColor Red
            if ($result) {
                Write-Host "错误信息: $result" -ForegroundColor Red
            }
            $continue = Read-Host "是否继续执行下一个脚本? (y/n)"
            if ($continue -ne "y" -and $continue -ne "Y") {
                exit 1
            }
        }
    } catch {
        Write-Host "✗ 错误: $file 执行异常" -ForegroundColor Red
        Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
        $continue = Read-Host "是否继续执行下一个脚本? (y/n)"
        if ($continue -ne "y" -and $continue -ne "Y") {
            exit 1
        }
    }
    
    Write-Host ""
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "数据库初始化完成！" -ForegroundColor Green
Write-Host "结束时间: $(Get-Date)" -ForegroundColor Cyan
Write-Host ""
Write-Host "数据库信息:" -ForegroundColor Cyan
Write-Host "  数据库名: $DB_NAME" -ForegroundColor White
Write-Host "  主机地址: ${DB_HOST}:${DB_PORT}" -ForegroundColor White
Write-Host "  字符编码: UTF-8 (utf8mb4)" -ForegroundColor White
Write-Host ""
Write-Host "默认管理员账户:" -ForegroundColor Cyan
Write-Host "  用户名: admin" -ForegroundColor White
Write-Host "  密码: admin123" -ForegroundColor White
Write-Host ""
Write-Host "您现在可以启动校园管理系统应用程序了！" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# 询问是否打开数据库管理工具
$openTool = Read-Host "是否要打开MySQL命令行客户端查看数据库? (y/n)"
if ($openTool -eq "y" -or $openTool -eq "Y") {
    Write-Host "正在打开MySQL命令行客户端..." -ForegroundColor Yellow
    & $MYSQL_PATH -h $DB_HOST -P $DB_PORT -u $DB_USER -p$DB_PASSWORD $DB_NAME
}
