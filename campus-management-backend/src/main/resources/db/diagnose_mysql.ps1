# =====================================================
# MySQL连接诊断脚本
# =====================================================
# 文件名: diagnose_mysql.ps1
# 描述: 诊断MySQL连接问题并提供解决方案
# 版本: 1.0.0
# =====================================================

# 设置错误处理
$ErrorActionPreference = "Continue"

# 设置控制台编码
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# 颜色输出函数
function Write-ColorOutput {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

function Write-Success { param([string]$Message); Write-ColorOutput "✓ $Message" "Green" }
function Write-Info { param([string]$Message); Write-ColorOutput "ℹ $Message" "Cyan" }
function Write-Warning { param([string]$Message); Write-ColorOutput "⚠ $Message" "Yellow" }
function Write-Error { param([string]$Message); Write-ColorOutput "✗ $Message" "Red" }

# 显示标题
function Show-Header {
    Clear-Host
    Write-ColorOutput "=====================================================" "Magenta"
    Write-ColorOutput "MySQL连接诊断工具" "Magenta"
    Write-ColorOutput "=====================================================" "Magenta"
    Write-Host ""
}

# 检查MySQL服务状态
function Test-MySQLService {
    Write-Info "检查MySQL服务状态..."
    
    try {
        $service = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue
        if ($service) {
            foreach ($svc in $service) {
                if ($svc.Status -eq "Running") {
                    Write-Success "MySQL服务正在运行: $($svc.Name) - $($svc.Status)"
                    return $true
                } else {
                    Write-Warning "MySQL服务未运行: $($svc.Name) - $($svc.Status)"
                }
            }
        } else {
            Write-Warning "未找到MySQL服务"
        }
        return $false
    }
    catch {
        Write-Error "检查MySQL服务时出错: $($_.Exception.Message)"
        return $false
    }
}

# 测试不同的连接参数
function Test-MySQLConnections {
    Write-Info "测试不同的MySQL连接参数..."
    
    $testConfigs = @(
        @{ Username = "root"; Password = ""; Description = "root用户，空密码" },
        @{ Username = "root"; Password = "root"; Description = "root用户，密码root" },
        @{ Username = "root"; Password = "123456"; Description = "root用户，密码123456" },
        @{ Username = "root"; Password = "xiaoxiao123"; Description = "root用户，密码xiaoxiao123" },
        @{ Username = "root"; Password = "mysql"; Description = "root用户，密码mysql" }
    )
    
    $successCount = 0
    
    foreach ($config in $testConfigs) {
        Write-Host ""
        Write-Info "测试: $($config.Description)"
        
        try {
            $testQuery = "SELECT VERSION() as version"
            $result = & mysql -hlocalhost -P3306 -u$($config.Username) -p$($config.Password) -e $testQuery 2>$null
            
            if ($LASTEXITCODE -eq 0 -and $result) {
                Write-Success "连接成功！MySQL版本: $($result -join ' ')"
                Write-ColorOutput "  建议使用此配置：用户名=$($config.Username), 密码=$($config.Password)" "Green"
                $successCount++
            } else {
                Write-Error "连接失败 (退出代码: $LASTEXITCODE)"
            }
        }
        catch {
            Write-Error "连接异常: $($_.Exception.Message)"
        }
    }
    
    if ($successCount -eq 0) {
        Write-Warning "所有预设配置都连接失败"
        Show-TroubleshootingTips
    }
}

# 显示故障排除建议
function Show-TroubleshootingTips {
    Write-Host ""
    Write-ColorOutput "故障排除建议:" "Yellow"
    Write-ColorOutput "----------------------------------------" "Gray"
    
    Write-Host "1. 重置MySQL root密码:" -ForegroundColor White
    Write-Host "   - 停止MySQL服务" -ForegroundColor Gray
    Write-Host "   - 以安全模式启动MySQL: mysqld --skip-grant-tables" -ForegroundColor Gray
    Write-Host "   - 连接MySQL: mysql -u root" -ForegroundColor Gray
    Write-Host "   - 重置密码: ALTER USER 'root'@'localhost' IDENTIFIED BY 'xiaoxiao123';" -ForegroundColor Gray
    Write-Host "   - 刷新权限: FLUSH PRIVILEGES;" -ForegroundColor Gray
    
    Write-Host ""
    Write-Host "2. 检查MySQL配置文件 (my.ini 或 my.cnf):" -ForegroundColor White
    Write-Host "   - 确保端口设置为3306" -ForegroundColor Gray
    Write-Host "   - 检查bind-address设置" -ForegroundColor Gray
    
    Write-Host ""
    Write-Host "3. 使用MySQL Workbench或命令行工具测试连接" -ForegroundColor White
    
    Write-Host ""
    Write-Host "4. 检查防火墙设置，确保3306端口开放" -ForegroundColor White
}

# 检查MySQL安装路径
function Test-MySQLInstallation {
    Write-Info "检查MySQL安装..."
    
    try {
        $mysqlVersion = & mysql --version 2>$null
        if ($mysqlVersion -and $LASTEXITCODE -eq 0) {
            Write-Success "MySQL已安装: $mysqlVersion"
            
            # 尝试获取MySQL安装路径
            $mysqlPath = (Get-Command mysql -ErrorAction SilentlyContinue).Source
            if ($mysqlPath) {
                Write-Info "MySQL路径: $mysqlPath"
            }
            return $true
        }
    }
    catch {
        Write-Error "MySQL未安装或未添加到系统PATH中"
        return $false
    }
    
    return $false
}

# 主函数
function Start-Diagnosis {
    Show-Header
    
    Write-Info "开始MySQL连接诊断..."
    Write-Host ""
    
    # 1. 检查MySQL安装
    if (-not (Test-MySQLInstallation)) {
        Write-Error "MySQL安装检查失败，请先安装MySQL"
        return
    }
    
    # 2. 检查MySQL服务
    Test-MySQLService
    
    # 3. 测试连接
    Test-MySQLConnections
    
    Write-Host ""
    Write-ColorOutput "=====================================================" "Magenta"
    Write-ColorOutput "诊断完成" "Magenta"
    Write-ColorOutput "=====================================================" "Magenta"
}

# 执行诊断
try {
    Start-Diagnosis
}
catch {
    Write-Error "诊断过程中发生异常: $($_.Exception.Message)"
}
finally {
    Write-Host ""
    Read-Host "按任意键退出"
}
