# 智慧校园管理平台 - 数据库部署文档

## 📋 项目概述

智慧校园管理平台数据库是一个完整的教育管理系统数据库解决方案，支持用户管理、教务管理、考试管理、财务管理、消息通知等核心功能。

## 🗂️ 目录结构

```
database/
├── scripts/                    # SQL脚本目录
│   ├── 01_create_database.sql  # 数据库创建脚本
│   ├── 02_create_tables.sql    # 表结构创建脚本
│   ├── 03_create_indexes.sql   # 索引创建脚本
│   ├── 04_create_views.sql     # 视图创建脚本
│   ├── 05_init_data.sql        # 初始化数据脚本
│   └── 06_test_data.sql        # 测试数据脚本
├── deploy/                     # 部署配置
│   ├── docker-compose.yml      # Docker容器编排配置
│   ├── mysql.cnf               # MySQL配置文件
│   ├── redis.conf              # Redis配置文件
│   ├── backup.sh               # 数据库备份脚本
│   └── restore.sh              # 数据库恢复脚本
├── execute.sh                  # 一键部署脚本 (Linux/macOS)
├── execute.ps1                 # 一键部署脚本 (Windows PowerShell)
└── README.md                   # 项目说明文档
```

## 🚀 快速开始

### 方式一：Docker 部署（推荐）

#### Linux/macOS 用户
1. **确保已安装 Docker 和 Docker Compose**

2. **启动服务**
   ```bash
   cd database
   chmod +x execute.sh
   ./execute.sh --docker
   ```

#### Windows 用户
1. **确保已安装 Docker Desktop**

2. **以管理员身份运行 PowerShell**
   ```powershell
   cd database
   .\execute.ps1 -Docker
   ```

3. **访问服务**
   - MySQL: `localhost:3306`
   - Redis: `localhost:6379`
   - Adminer: `http://localhost:8080`

### 方式二：本地 MySQL 部署

#### Linux/macOS 用户
1. **确保已安装 MySQL 8.x**

2. **执行部署脚本**
   ```bash
   cd database
   chmod +x execute.sh
   ./execute.sh -p your_mysql_password
   ```

#### Windows 用户
1. **确保已安装 MySQL 8.x**

2. **以管理员身份运行 PowerShell**
   ```powershell
   cd database
   .\execute.ps1 -Password your_mysql_password
   ```

## 📊 数据库设计

### 核心表结构

| 模块 | 表数量 | 主要功能 |
|------|--------|----------|
| 用户管理 | 7张表 | 用户、角色、学生、教师、家长管理 |
| 教务管理 | 5张表 | 班级、课程、选课、成绩管理 |
| 考试管理 | 2张表 | 考试安排、题库管理 |
| 财务管理 | 2张表 | 缴费项目、缴费记录 |
| 消息通知 | 3张表 | 消息模板、消息、阅读状态 |
| 系统管理 | 2张表 | 系统配置、操作日志 |

### 重要视图

- `v_student_overview` - 学生信息概览
- `v_teacher_schedule` - 教师课程安排
- `v_student_grade_stats` - 学生成绩统计
- `v_user_message_overview` - 用户消息概览

## ⚙️ 配置说明

### 数据库连接信息

**Docker 部署：**
- 主机：`localhost`
- 端口：`3306`
- 数据库：`campus_management`
- 用户名：`campus_user`
- 密码：`campus_password`

**管理员账户：**
- 用户名：`admin`
- 密码：`admin123`

### 环境变量配置

可以通过修改 `docker-compose.yml` 中的环境变量来自定义配置：

```yaml
environment:
  MYSQL_ROOT_PASSWORD: your_root_password
  MYSQL_DATABASE: campus_management
  MYSQL_USER: campus_user
  MYSQL_PASSWORD: your_password
```

## 🛠️ 脚本使用指南

### Linux/macOS 一键部署脚本 (execute.sh)

```bash
# 基本用法
./execute.sh [选项]

# 常用选项
-p, --password      数据库密码
--docker           使用Docker部署
--init-only        只执行初始化（不含测试数据）
--clean            清理重新部署
-h, --help         显示帮助信息

# 示例
./execute.sh --docker                    # Docker部署
./execute.sh -p mypass                   # 本地部署
./execute.sh --init-only -p mypass       # 只部署基础数据
./execute.sh --clean --docker            # 清理重新部署
```

### Windows PowerShell 部署脚本 (execute.ps1)

```powershell
# 基本用法
.\execute.ps1 [参数]

# 常用参数
-DBHost            数据库主机地址 (默认: localhost)
-Port              数据库端口 (默认: 3306)
-User              数据库用户名 (默认: root)
-Password          数据库密码
-Database          数据库名称 (默认: campus_management)
-InitOnly          只执行初始化（不含测试数据）
-TestOnly          只执行测试数据
-Docker            使用Docker部署
-Clean             清理重新部署
-Help              显示帮助信息

# 示例
.\execute.ps1 -Docker                    # Docker部署
.\execute.ps1 -Password mypass           # 本地部署
.\execute.ps1 -InitOnly -Password mypass # 只部署基础数据
.\execute.ps1 -Clean -Docker             # 清理重新部署
.\execute.ps1 -DBHost 192.168.1.100 -Password mypass  # 连接远程数据库
```

### 备份脚本 (backup.sh)

```bash
cd deploy
chmod +x backup.sh

# 基本用法
./backup.sh [选项]

# 常用选项
--compress          压缩备份文件
--structure-only    只备份表结构
--data-only         只备份数据
--docker           从Docker容器备份

# 示例
./backup.sh                              # 完整备份
./backup.sh --compress                   # 压缩备份
./backup.sh --docker                     # Docker容器备份
./backup.sh --structure-only             # 只备份结构
```

### 恢复脚本 (restore.sh)

```bash
cd deploy
chmod +x restore.sh

# 基本用法
./restore.sh [选项] [备份文件]

# 常用选项
--latest           使用最新备份
--list             列出可用备份
--docker           恢复到Docker容器
--force            强制恢复（不确认）
--drop-first       恢复前删除数据库

# 示例
./restore.sh --list                      # 列出备份文件
./restore.sh --latest                    # 使用最新备份恢复
./restore.sh -f backup.sql               # 从指定文件恢复
./restore.sh --docker --latest           # 恢复到Docker容器
```

## 🎯 使用场景

### 开发环境搭建

#### Linux/macOS
```bash
# 1. 快速搭建开发环境（包含测试数据）
./execute.sh --docker

# 2. 访问Adminer进行数据库管理
# 浏览器打开: http://localhost:8080
```

#### Windows
```powershell
# 1. 快速搭建开发环境（包含测试数据）
.\execute.ps1 -Docker

# 2. 访问Adminer进行数据库管理
# 浏览器打开: http://localhost:8080
```

**Adminer 连接信息：**
- 服务器: `mysql`
- 用户名: `campus_user`
- 密码: `campus_password`
- 数据库: `campus_management`

### 生产环境部署

#### Linux/macOS
```bash
# 1. 只部署基础数据（不含测试数据）
./execute.sh --init-only -p production_password

# 2. 设置定时备份
crontab -e
# 添加: 0 2 * * * /path/to/database/deploy/backup.sh --compress
```

#### Windows
```powershell
# 1. 只部署基础数据（不含测试数据）
.\execute.ps1 -InitOnly -Password production_password

# 2. 设置定时备份（使用任务计划程序）
# 控制面板 > 管理工具 > 任务计划程序
```

### 数据迁移

```bash
# 1. 在源环境备份
./backup.sh --compress

# 2. 在目标环境恢复
./restore.sh --latest --drop-first
```

## 📈 性能优化

### 索引策略

- **单列索引**：常用查询字段（用户名、邮箱、手机号等）
- **复合索引**：多条件查询场景（学生+学期+课程等）
- **唯一索引**：业务唯一性约束

### MySQL 配置优化

在 `mysql.cnf` 中已包含针对教育系统的优化配置：

- InnoDB缓冲池大小：512MB
- 查询缓存：64MB
- 最大连接数：200
- 慢查询日志：启用

## 🔒 安全配置

### 数据库安全

1. **修改默认密码**：部署后立即修改默认密码
2. **限制网络访问**：生产环境建议限制MySQL访问IP
3. **定期备份**：设置自动备份策略
4. **权限控制**：为不同用户分配最小必要权限

### 操作审计

系统包含完整的操作日志记录：

```sql
-- 查看操作日志
SELECT * FROM tb_operation_log
WHERE created_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)
ORDER BY created_time DESC;
```

## 🐛 故障排除

### 常见问题

1. **PowerShell 执行策略错误**
   ```powershell
   # 以管理员身份运行PowerShell，执行：
   Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
   ```

2. **PowerShell Host变量冲突**
   ```powershell
   # 如果遇到Host变量冲突，使用 -DBHost 参数：
   .\execute.ps1 -DBHost localhost -Password mypass
   ```

3. **MySQL连接失败**
   ```bash
   # 检查MySQL服务状态
   docker-compose logs mysql

   # 检查端口占用
   netstat -tulpn | grep 3306  # Linux/macOS
   netstat -an | findstr 3306  # Windows
   ```

4. **脚本执行权限错误**
   ```bash
   chmod +x execute.sh
   chmod +x deploy/backup.sh
   chmod +x deploy/restore.sh
   ```

5. **字符编码问题**
   ```sql
   -- 检查字符集配置
   SHOW VARIABLES LIKE 'character%';
   SHOW VARIABLES LIKE 'collation%';
   ```

6. **Docker 内存不足**
   ```bash
   # 调整Docker内存限制
   # 修改docker-compose.yml中的内存配置
   ```

7. **Windows Docker Desktop 未启动**
   ```powershell
   # 确保Docker Desktop正在运行
   # 检查系统托盘中的Docker图标
   ```

### 日志查看

- **Linux/macOS 部署日志**：`./deployment.log`
- **Windows 部署日志**：`.\deployment.log`
- **备份日志**：`./deploy/backups/backup.log`
- **恢复日志**：`./deploy/backups/restore.log`
- **MySQL日志**：`docker-compose logs mysql`

## 📚 开发指南

### 数据库连接示例

**Java (Spring Boot)**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management?useUnicode=true&characterEncoding=utf8mb4&useSSL=false
    username: campus_user
    password: campus_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**Python (Django)**
```python
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'campus_management',
        'USER': 'campus_user',
        'PASSWORD': 'campus_password',
        'HOST': 'localhost',
        'PORT': '3306',
        'OPTIONS': {
            'charset': 'utf8mb4',
        },
    }
}
```

**Node.js (MySQL2)**
```javascript
const mysql = require('mysql2/promise');

const connection = await mysql.createConnection({
  host: 'localhost',
  port: 3306,
  user: 'campus_user',
  password: 'campus_password',
  database: 'campus_management',
  charset: 'utf8mb4'
});
```

**C# (.NET Core)**
```csharp
"ConnectionStrings": {
  "DefaultConnection": "Server=localhost;Port=3306;Database=campus_management;Uid=campus_user;Pwd=campus_password;CharSet=utf8mb4;"
}
```

### 数据库迁移

如需修改表结构，建议：

1. 编写迁移脚本
2. 在测试环境验证
3. 备份生产数据
4. 执行迁移
5. 验证数据完整性

## 🤝 贡献指南

### 提交 SQL 脚本修改

1. 修改对应的SQL脚本文件
2. 更新版本号和变更日志
3. 在测试环境验证
4. 提交Pull Request

### 代码规范

- SQL关键字使用大写
- 表名和字段名使用下划线命名
- 添加详细的注释说明
- 遵循现有的格式风格

## 📞 技术支持

如遇到问题，请：

1. 查看本文档的故障排除部分
2. 检查相关日志文件
3. 在项目Issue中搜索类似问题
4. 提交新的Issue并提供详细信息

## 📄 许可证

本项目采用 MIT 许可证，详情请查看 LICENSE 文件。

---

**版本信息**
- 数据库版本：v1.0.0
- MySQL版本：8.0+
- 支持平台：Windows, Linux, macOS
- 文档更新：2025-06-03

**相关文档**
- [数据库设计文档](../数据库设计文档.md)
- [执行方案](../执行方案.md)
- [数据库脚本实施计划](../数据库脚本实施计划.md)
