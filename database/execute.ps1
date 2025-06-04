# =====================================================
# 智慧校园管理平台 - 数据库一键部署脚本 (PowerShell版本)
# 版本: 1.0.0
# 描述: 自动执行所有数据库脚本 (Windows PowerShell)
# =====================================================

param(
    [string]$DBHost = "localhost",
    [string]$Port = "3306",
    [string]$User = "root",
    [string]$Password = "",
    [string]$Database = "campus_management",
    [switch]$InitOnly,
    [switch]$TestOnly,
    [switch]$Docker,
    [switch]$Clean,
    [switch]$Help
)

# 设置UTF-8编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# 设置颜色输出
function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Type = "INFO"
    )

    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    switch ($Type) {
        "ERROR" { Write-Host "[$timestamp] [ERROR] $Message" -ForegroundColor Red }
        "WARNING" { Write-Host "[$timestamp] [WARNING] $Message" -ForegroundColor Yellow }
        "SUCCESS" { Write-Host "[$timestamp] [SUCCESS] $Message" -ForegroundColor Green }
        "STEP" { Write-Host "[$timestamp] [STEP] $Message" -ForegroundColor Blue }
        default { Write-Host "[$timestamp] [INFO] $Message" -ForegroundColor Green }
    }

    # 同时写入日志文件
    "$timestamp [$Type] $Message" | Out-File -FilePath "deployment.log" -Append -Encoding UTF8
}

# 显示横幅
function Show-Banner {
    Write-Host "=======================================================" -ForegroundColor Cyan
    Write-Host "           智慧校园管理平台 - 数据库部署工具 (Windows)" -ForegroundColor Cyan
    Write-Host "=======================================================" -ForegroundColor Cyan
    Write-Host ""
}

# 显示帮助信息
function Show-Help {
    Write-Host "使用方法: .\execute.ps1 [参数]"
    Write-Host ""
    Write-Host "参数:"
    Write-Host "  -DBHost         数据库主机地址 (默认: localhost)"
    Write-Host "  -Port           数据库端口 (默认: 3306)"
    Write-Host "  -User           数据库用户名 (默认: root)"
    Write-Host "  -Password       数据库密码"
    Write-Host "  -Database       数据库名称 (默认: campus_management)"
    Write-Host "  -InitOnly       只执行初始化脚本(不包含测试数据)"
    Write-Host "  -TestOnly       只执行测试数据脚本"
    Write-Host "  -Docker         使用Docker部署"
    Write-Host "  -Clean          清理并重新部署"
    Write-Host "  -Help           显示帮助信息"
    Write-Host ""
    Write-Host "示例:"
    Write-Host "  .\execute.ps1 -Password mypassword          # 使用密码部署"
    Write-Host "  .\execute.ps1 -Docker                       # 使用Docker部署"
    Write-Host "  .\execute.ps1 -InitOnly -Password mypass    # 只部署基础数据"
    Write-Host "  .\execute.ps1 -Clean -Password mypass       # 清理重新部署"
}

# 检查依赖
function Test-Dependencies {
    Write-ColorOutput "检查依赖..." "STEP"

    if ($Docker) {
        # 检查Docker
        try {
            $dockerVersion = & docker --version 2>$null
            if ($dockerVersion) {
                Write-ColorOutput "Docker检查通过: $dockerVersion"
            } else {
                Write-ColorOutput "Docker未安装，请先安装Docker Desktop" "ERROR"
                exit 1
            }
        } catch {
            Write-ColorOutput "Docker未安装，请先安装Docker Desktop" "ERROR"
            exit 1
        }

        # 检查Docker Compose
        try {
            $composeVersion = & docker-compose --version 2>$null
            if ($composeVersion) {
                Write-ColorOutput "Docker Compose检查通过: $composeVersion"
            } else {
                Write-ColorOutput "Docker Compose未安装" "ERROR"
                exit 1
            }
        } catch {
            Write-ColorOutput "Docker Compose未安装" "ERROR"
            exit 1
        }
    } else {
        # 检查MySQL客户端
        try {
            $mysqlVersion = & mysql --version 2>$null
            if ($mysqlVersion) {
                Write-ColorOutput "MySQL客户端检查通过: $mysqlVersion"
            } else {
                Write-ColorOutput "MySQL客户端未安装，请先安装MySQL" "ERROR"
                exit 1
            }
        } catch {
            Write-ColorOutput "MySQL客户端未安装，请先安装MySQL" "ERROR"
            exit 1
        }
    }
}

# 检查MySQL连接
function Test-MySQLConnection {
    Write-ColorOutput "检查MySQL连接..." "STEP"

    try {
        if ($Password) {
            $result = & mysql -h $DBHost -P $Port -u $User "-p$Password" -e "SELECT 1;" 2>$null
        } else {
            $result = & mysql -h $DBHost -P $Port -u $User -e "SELECT 1;" 2>$null
        }

        if ($LASTEXITCODE -eq 0) {
            Write-ColorOutput "MySQL连接成功"
            return $true
        } else {
            Write-ColorOutput "MySQL连接失败，请检查配置" "ERROR"
            return $false
        }
    } catch {
        Write-ColorOutput "MySQL连接失败: $($_.Exception.Message)" "ERROR"
        return $false
    }
}

# 执行SQL脚本
function Invoke-SQLScript {
    param(
        [string]$ScriptPath,
        [bool]$ContinueOnError = $false
    )

    $scriptName = Split-Path $ScriptPath -Leaf
    Write-ColorOutput "执行脚本: $scriptName" "STEP"

    if (-not (Test-Path $ScriptPath)) {
        Write-ColorOutput "脚本文件不存在: $ScriptPath" "ERROR"
        return $false
    }

    try {
        # 读取SQL文件内容，使用UTF8编码
        $sqlContent = Get-Content -Path $ScriptPath -Raw -Encoding UTF8

        # 使用管道将SQL内容传递给mysql，设置字符集
        if ($Password) {
            $sqlContent | & mysql -h $DBHost -P $Port -u $User "-p$Password" --default-character-set=utf8mb4 2>>deployment.log
        } else {
            $sqlContent | & mysql -h $DBHost -P $Port -u $User --default-character-set=utf8mb4 2>>deployment.log
        }

        if ($LASTEXITCODE -eq 0) {
            Write-ColorOutput "脚本 $scriptName 执行成功" "SUCCESS"
            return $true
        } else {
            if ($ContinueOnError) {
                Write-ColorOutput "脚本 $scriptName 执行失败，但继续执行后续脚本" "WARNING"
                return $true
            } else {
                Write-ColorOutput "脚本 $scriptName 执行失败，详情请查看日志文件: deployment.log" "ERROR"
                return $false
            }
        }
    } catch {
        if ($ContinueOnError) {
            Write-ColorOutput "脚本执行异常，但继续执行: $($_.Exception.Message)" "WARNING"
            return $true
        } else {
            Write-ColorOutput "脚本执行异常: $($_.Exception.Message)" "ERROR"
            return $false
        }
    }
}

# Docker部署
function Start-DockerDeployment {
    Write-ColorOutput "使用Docker部署数据库环境..." "STEP"

    Push-Location "deploy"

    try {
        if ($Clean) {
            Write-ColorOutput "清理现有Docker容器和数据..." "INFO"
            & docker-compose down -v 2>$null
            & docker volume prune -f 2>$null
        }

        Write-ColorOutput "启动Docker容器..." "INFO"
        & docker-compose up -d

        if ($LASTEXITCODE -eq 0) {
            Write-ColorOutput "Docker容器启动成功" "SUCCESS"

            # 等待MySQL启动完成
            Write-ColorOutput "等待MySQL服务启动..." "INFO"
            $timeout = 60
            $count = 0

            do {
                Start-Sleep -Seconds 1
                & docker-compose exec mysql mysqladmin ping -h localhost --silent 2>$null
                $mysqlReady = $LASTEXITCODE -eq 0
                $count++
                Write-Host "." -NoNewline

                if ($count -ge $timeout) {
                    Write-Host ""
                    Write-ColorOutput "MySQL启动超时" "ERROR"
                    return $false
                }
            } while (-not $mysqlReady)

            Write-Host ""
            Write-ColorOutput "MySQL服务已就绪" "SUCCESS"

            Write-ColorOutput "数据库初始化脚本将自动执行..." "INFO"
            Write-ColorOutput "可以通过以下方式访问:" "INFO"
            Write-ColorOutput "  MySQL: localhost:3306" "INFO"
            Write-ColorOutput "  Redis: localhost:6379" "INFO"
            Write-ColorOutput "  Adminer: http://localhost:8080" "INFO"
            Write-ColorOutput "" "INFO"
            Write-ColorOutput "数据库连接信息:" "INFO"
            Write-ColorOutput "  Host: localhost" "INFO"
            Write-ColorOutput "  Port: 3306" "INFO"
            Write-ColorOutput "  Database: campus_management" "INFO"
            Write-ColorOutput "  Username: campus_user" "INFO"
            Write-ColorOutput "  Password: campus_password" "INFO"

            return $true
        } else {
            Write-ColorOutput "Docker容器启动失败" "ERROR"
            return $false
        }
    }
    finally {
        Pop-Location
    }
}

# 本地部署
function Start-LocalDeployment {
    Write-ColorOutput "本地部署数据库..." "STEP"

    # 检查MySQL连接
    if (-not (Test-MySQLConnection)) {
        return $false
    }

    # 确定要执行的脚本
    $scripts = @()

    if ($TestOnly) {
        $scripts = @(
            @{Path = "06_test_data.sql"; ContinueOnError = $false}
        )
    } elseif ($InitOnly) {
        $scripts = @(
            @{Path = "01_create_database.sql"; ContinueOnError = $true},
            @{Path = "02_create_tables.sql"; ContinueOnError = $false},
            @{Path = "03_create_indexes.sql"; ContinueOnError = $false},
            @{Path = "04_create_views.sql"; ContinueOnError = $false},
            @{Path = "05_init_data.sql"; ContinueOnError = $false}
        )
    } else {
        $scripts = @(
            @{Path = "01_create_database.sql"; ContinueOnError = $true},
            @{Path = "02_create_tables.sql"; ContinueOnError = $false},
            @{Path = "03_create_indexes.sql"; ContinueOnError = $false},
            @{Path = "04_create_views.sql"; ContinueOnError = $false},
            @{Path = "05_init_data.sql"; ContinueOnError = $false},
            @{Path = "06_test_data.sql"; ContinueOnError = $false}
        )
    }

    # 按顺序执行脚本
    foreach ($script in $scripts) {
        $scriptPath = "scripts\$($script.Path)"
        if (-not (Invoke-SQLScript $scriptPath $script.ContinueOnError)) {
            Write-ColorOutput "部署失败，请检查错误信息" "ERROR"
            return $false
        }
    }

    return $true
}

# 验证部署结果
function Test-DeploymentResult {
    Write-ColorOutput "验证部署结果..." "STEP"

    try {
        if ($Docker) {
            # Docker环境验证
            $tableResult = & docker-compose -f "deploy\docker-compose.yml" exec mysql mysql -u campus_user -pcampus_password campus_management -e "SHOW TABLES;" 2>$null
            $tableCount = ($tableResult | Measure-Object -Line).Lines
        } else {
            # 本地环境验证
            if ($Password) {
                $tableResult = & mysql -h $DBHost -P $Port -u campus_user -pcampus_password $Database -e "SHOW TABLES;" --default-character-set=utf8mb4 2>$null
            } else {
                $tableResult = & mysql -h $DBHost -P $Port -u campus_user -pcampus_password $Database -e "SHOW TABLES;" --default-character-set=utf8mb4 2>$null
            }
            $tableCount = ($tableResult | Measure-Object -Line).Lines
        }

        if ($tableCount -gt 20) {
            Write-ColorOutput "数据库表创建验证通过 ($($tableCount-1)张表)" "SUCCESS"
        } else {
            Write-ColorOutput "数据库表数量异常: $tableCount" "WARNING"
        }

        Write-ColorOutput "部署验证完成" "SUCCESS"
        return $true
    } catch {
        Write-ColorOutput "验证过程中出现错误: $($_.Exception.Message)" "WARNING"
        return $false
    }
}

# 主函数
function Main {
    # 显示横幅
    Show-Banner

    # 显示帮助
    if ($Help) {
        Show-Help
        return
    }

    # 如果没有提供密码且不是Docker部署，提示输入
    if (-not $Password -and -not $Docker) {
        $securePassword = Read-Host "请输入数据库密码" -AsSecureString
        $Password = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword))
    }

    # 初始化日志
    "=== 智慧校园管理平台数据库部署日志 (PowerShell) ===" | Out-File -FilePath "deployment.log" -Encoding UTF8
    "开始时间: $(Get-Date)" | Out-File -FilePath "deployment.log" -Append -Encoding UTF8
    "配置信息: Host=$DBHost, Port=$Port, User=$User" | Out-File -FilePath "deployment.log" -Append -Encoding UTF8
    "" | Out-File -FilePath "deployment.log" -Append -Encoding UTF8

    # 检查依赖
    Test-Dependencies

    # 根据部署方式执行
    $deploymentSuccess = $false
    if ($Docker) {
        $deploymentSuccess = Start-DockerDeployment
    } else {
        $deploymentSuccess = Start-LocalDeployment
    }

    if ($deploymentSuccess) {
        # 验证部署结果
        Test-DeploymentResult

        Write-ColorOutput "数据库部署完成！" "SUCCESS"

        if (-not $Docker) {
            Write-ColorOutput "默认管理员账户:" "INFO"
            Write-ColorOutput "  用户名: admin" "INFO"
            Write-ColorOutput "  密码: admin123" "INFO"
            Write-ColorOutput "" "INFO"
            Write-ColorOutput "数据库连接信息:" "INFO"
            Write-ColorOutput "  Host: $DBHost" "INFO"
            Write-ColorOutput "  Port: $Port" "INFO"
            Write-ColorOutput "  Database: $Database" "INFO"
            Write-ColorOutput "  Username: campus_user" "INFO"
            Write-ColorOutput "  Password: campus_password" "INFO"
        }
    } else {
        Write-ColorOutput "数据库部署失败！" "ERROR"
        exit 1
    }

    Write-ColorOutput "日志文件: deployment.log" "INFO"
}

# 执行主函数
Main
