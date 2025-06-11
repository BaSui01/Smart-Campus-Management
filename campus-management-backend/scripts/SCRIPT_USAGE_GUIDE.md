# 脚本使用指南 - Script Usage Guide

## 问题诊断 Problem Diagnosis

如果您遇到了类似以下的错误信息：
```
'接失败！' is not recognized as an internal or external command
'Bearer' is not recognized as an internal or external command
'MP.json" 2>&1' is not recognized as an internal or external command
```

这通常是由于以下原因造成的：
1. **字符编码问题** - 批处理脚本中的中文字符在某些系统配置下无法正确解析
2. **命令行参数解析问题** - curl命令的多行参数被错误拆分
3. **重定向语法问题** - `2>&1` 重定向语法在某些情况下被误解析

## 解决方案 Solutions

我们提供了三个版本的API测试脚本，请根据您的环境选择：

### 1. PowerShell版本 (推荐 Recommended) 
**文件**: `quick-api-test.ps1`

**优势**:
- ✅ 完美的Unicode/中文支持
- ✅ 更好的错误处理机制
- ✅ 自动token提取功能
- ✅ 彩色输出，用户体验更佳
- ✅ 跨平台兼容 (Windows/Linux/macOS)

**使用方法**:
```powershell
# 在PowerShell中运行
.\quick-api-test.ps1

# 或者指定参数
.\quick-api-test.ps1 -BaseUrl "http://localhost:8082" -Username "admin" -Password "admin123"
```

### 2. 修复版批处理文件
**文件**: `quick-api-test-fixed.bat`

**优势**:
- ✅ 移除了中文字符，避免编码问题
- ✅ 简化了curl命令格式
- ✅ 添加了curl可用性检查
- ✅ 改进了错误处理

**使用方法**:
```cmd
quick-api-test-fixed.bat
```

### 3. 原始修复版批处理文件
**文件**: `quick-api-test.bat` (已修复)

**修复内容**:
- ✅ 移除了多行curl命令的换行符
- ✅ 使用 `-o` 参数替代重定向
- ✅ 改进了文件存在性检查

**使用方法**:
```cmd
quick-api-test.bat
```

## 环境要求 Requirements

### 所有版本通用要求:
- ✅ 服务器运行在 `http://localhost:8082`
- ✅ 安装了 `curl` 工具
- ✅ 具有管理员账号 (`admin/admin123`)

### curl 安装检查:
```cmd
# 检查curl是否可用
curl --version

# 如果没有curl，可以：
# 1. 安装Git for Windows (包含curl)
# 2. 安装Windows的curl包
# 3. 使用WSL (Windows Subsystem for Linux)
```

## 故障排除 Troubleshooting

### 1. 字符编码问题
如果仍然遇到中文字符显示问题：
```cmd
# 设置控制台编码为UTF-8
chcp 65001
```

### 2. curl 不可用
```cmd
# 检查curl位置
where curl

# 如果找不到，安装Git for Windows或直接安装curl
```

### 3. 服务器连接问题
```cmd
# 手动测试服务器连接
curl http://localhost:8082/actuator/health

# 检查服务器是否运行
netstat -an | findstr :8082
```

### 4. 权限问题
如果PowerShell脚本无法执行：
```powershell
# 查看执行策略
Get-ExecutionPolicy

# 临时允许脚本执行
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process

# 然后运行脚本
.\quick-api-test.ps1
```

## 输出文件说明 Output Files

所有脚本都会在 `quick-test-results/` 目录下生成以下文件：

1. **health_TIMESTAMP.json** - 服务器健康检查结果
2. **login_TIMESTAMP.json** - 用户登录响应 (包含token)
3. **user_info_TIMESTAMP.json** - 当前用户信息
4. **users_list_TIMESTAMP.json** - 用户列表数据
5. **students_list_TIMESTAMP.json** - 学生列表数据
6. **courses_list_TIMESTAMP.json** - 课程列表数据
7. **system_info_TIMESTAMP.json** - 系统信息数据
8. **QUICK_TEST_REPORT_TIMESTAMP.txt** - 测试报告摘要

## 推荐使用顺序 Recommended Usage Order

1. **首选**: 使用PowerShell版本 (`quick-api-test.ps1`)
2. **备选**: 使用修复版批处理 (`quick-api-test-fixed.bat`)
3. **最后**: 使用原始修复版 (`quick-api-test.bat`)

## 技术支持 Technical Support

如果问题仍然存在，请：
1. 检查服务器日志
2. 确认API端点是否正确
3. 验证数据库连接状态
4. 查看防火墙设置

---

**更新时间**: 2025-06-11  
**适用版本**: Smart Campus Management System v1.0