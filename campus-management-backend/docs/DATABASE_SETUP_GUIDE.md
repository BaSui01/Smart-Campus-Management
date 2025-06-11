# 智慧校园管理系统 - 数据库初始化指南

本指南介绍如何使用管道方式运行SQL脚本来初始化智慧校园管理系统的数据库。

## 📋 目录

- [概述](#概述)
- [SQL脚本文件说明](#sql脚本文件说明)
- [执行方式](#执行方式)
- [使用说明](#使用说明)
- [故障排除](#故障排除)
- [数据验证](#数据验证)

## 🎯 概述

智慧校园管理系统提供了完整的数据库初始化解决方案，包含6个SQL脚本文件，按顺序执行可以创建完整的数据库结构和测试数据。

### 系统特点

- ✅ **35张数据表** - 完整的校园管理业务表结构
- ✅ **15,000+用户** - 包含管理员、教师、学生、家长等角色
- ✅ **大量业务数据** - 课程、成绩、考勤、缴费等完整数据
- ✅ **多种执行方式** - 批处理脚本、Shell脚本、Java程序
- ✅ **数据验证** - 完整性检查和统计分析

## 📄 SQL脚本文件说明

| 序号 | 文件名 | 描述 | 数据量 |
|------|--------|------|--------|
| 1 | `01_create_tables.sql` | 创建数据库表结构 | 35张表 |
| 2 | `02_insert_basic_data.sql` | 插入基础数据 | 200个用户 + 基础配置 |
| 3 | `03_insert_large_data.sql` | 生成大规模测试数据 | 15,000用户 + 业务数据 |
| 4 | `04_complete_all_tables.sql` | 补充完整表数据 | 确保所有表都有数据 |
| 5 | `05_data_validation.sql` | 数据验证和统计 | 完整性检查 |
| 6 | `06_data_analysis.sql` | 数据分析和报告 | 性能分析 |

### 详细说明

#### 1. 01_create_tables.sql
- **功能**: 创建完整的数据库表结构
- **包含**: 35张业务表，包括用户管理、学院班级、课程教学、成绩考试、考勤财务等
- **特点**: 完整的索引、外键约束、触发器设置

#### 2. 02_insert_basic_data.sql
- **功能**: 插入系统基础数据
- **包含**: 
  - 50个角色 + 80个权限
  - 50个学院数据
  - 100个教室数据
  - 系统配置数据
  - 200个基础用户账号

#### 3. 03_insert_large_data.sql
- **功能**: 生成大规模测试数据
- **包含**:
  - 15,000个用户 (管理员、教师、学生、家长)
  - 200个班级
  - 1,000门课程
  - 30,000+选课记录
  - 20,000+考勤记录
  - 15,000+缴费记录

#### 4. 04_complete_all_tables.sql
- **功能**: 补充完整表数据
- **作用**: 确保所有35张表都有测试数据，方便系统测试

#### 5. 05_data_validation.sql
- **功能**: 数据验证和统计
- **检查**: 外键约束、数据一致性、业务规则验证

#### 6. 06_data_analysis.sql
- **功能**: 全面的数据分析
- **包含**: 表结构检查、性能分析、优化建议

## 🚀 执行方式

### 方式一：Windows批处理脚本（推荐）

```batch
# 进入脚本目录
cd campus-management-backend\scripts

# 运行初始化脚本
run-database-setup.bat
```

**特点**:
- ✅ 图形化界面，操作简单
- ✅ 自动错误检测和提示
- ✅ 实时进度显示
- ✅ 完成后显示统计信息

### 方式二：Linux/Mac Shell脚本

```bash
# 进入脚本目录
cd campus-management-backend/scripts

# 给脚本执行权限
chmod +x run-database-setup.sh

# 运行初始化脚本
./run-database-setup.sh
```

**特点**:
- ✅ 彩色输出，清晰易读
- ✅ 完整的错误处理
- ✅ 自动连接测试
- ✅ 可设置环境变量

### 方式三：Java程序执行

```bash
# 使用Spring Boot应用执行
java -jar campus-management-backend.jar --init-db

# 或使用Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--init-db"
```

**特点**:
- ✅ 集成在应用中
- ✅ 自动检测是否需要初始化
- ✅ 完整的日志记录
- ✅ 可编程控制

### 方式四：手动执行（高级用户）

```sql
-- 依次执行以下命令
mysql -u用户名 -p密码 < src/main/resources/db/01_create_tables.sql
mysql -u用户名 -p密码 < src/main/resources/db/02_insert_basic_data.sql
mysql -u用户名 -p密码 < src/main/resources/db/03_insert_large_data.sql
mysql -u用户名 -p密码 < src/main/resources/db/04_complete_all_tables.sql
mysql -u用户名 -p密码 < src/main/resources/db/05_data_validation.sql
mysql -u用户名 -p密码 < src/main/resources/db/06_data_analysis.sql
```

## 📖 使用说明

### 前置条件

1. **MySQL数据库** (版本 5.7+ 或 8.0+)
2. **足够的磁盘空间** (建议2GB以上)
3. **数据库权限** (创建数据库、表的权限)
4. **网络连接** (如果数据库在远程服务器)

### 环境变量设置

可以通过环境变量配置数据库连接信息：

```bash
# Linux/Mac
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=campus_management_db
export DB_USER=root
export DB_PASSWORD=your_password

# Windows
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=campus_management_db
set DB_USER=root
set DB_PASSWORD=your_password
```

### 执行步骤

1. **准备数据库环境**
   ```sql
   -- 确保MySQL服务运行
   -- 确保有足够的权限创建数据库和表
   ```

2. **选择执行方式**
   - 新手用户：推荐使用批处理脚本
   - Linux用户：使用Shell脚本
   - 开发者：使用Java程序

3. **执行初始化**
   - 按照上述执行方式运行脚本
   - 等待所有步骤完成
   - 查看执行结果

4. **验证结果**
   - 检查数据库是否创建成功
   - 验证表和数据是否正确
   - 测试管理员账号登录

### 默认账号信息

初始化完成后，系统将创建以下默认账号：

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 系统管理员 | admin | admin123 | 拥有所有权限 |
| 教师示例 | teacher001 | 123456 | 教师角色测试账号 |
| 学生示例 | student001 | 123456 | 学生角色测试账号 |
| 家长示例 | parent001 | 123456 | 家长角色测试账号 |

## ⚠️ 故障排除

### 常见问题及解决方案

#### 1. 数据库连接失败

**问题**: `Database connection failed`

**解决方案**:
```bash
# 检查MySQL服务状态
systemctl status mysql  # Linux
net start mysql         # Windows

# 检查网络连接
ping 数据库服务器IP

# 验证用户名密码
mysql -h主机 -u用户名 -p
```

#### 2. 权限不足

**问题**: `Access denied for user`

**解决方案**:
```sql
-- 授予足够的权限
GRANT ALL PRIVILEGES ON *.* TO '用户名'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

#### 3. 磁盘空间不足

**问题**: `No space left on device`

**解决方案**:
```bash
# 检查磁盘空间
df -h

# 清理临时文件
rm -rf /tmp/mysql*
```

#### 4. 字符编码问题

**问题**: 中文字符显示异常

**解决方案**:
```sql
-- 设置正确的字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
```

#### 5. 内存不足

**问题**: `Out of memory`

**解决方案**:
```sql
-- 调整MySQL配置
-- 在my.cnf中设置
innodb_buffer_pool_size = 1G
max_allowed_packet = 64M
```

### 日志分析

#### Windows批处理脚本
- 执行过程中会显示详细的进度信息
- 错误信息会用红色标注
- 成功信息会用绿色标注

#### Linux Shell脚本
- 支持彩色输出
- 错误信息会详细显示
- 可以重定向日志到文件

```bash
./run-database-setup.sh > setup.log 2>&1
```

#### Java程序
- 使用标准的SLF4J日志框架
- 日志级别可配置
- 支持文件和控制台输出

### 性能优化建议

1. **大规模数据生成时**
   ```sql
   -- 临时禁用约束检查
   SET FOREIGN_KEY_CHECKS = 0;
   SET UNIQUE_CHECKS = 0;
   SET AUTOCOMMIT = 0;
   ```

2. **执行完成后**
   ```sql
   -- 恢复约束检查
   SET FOREIGN_KEY_CHECKS = 1;
   SET UNIQUE_CHECKS = 1;
   SET AUTOCOMMIT = 1;
   ```

3. **MySQL配置优化**
   ```ini
   [mysql]
   innodb_buffer_pool_size = 2G
   innodb_log_file_size = 256M
   max_allowed_packet = 64M
   ```

## ✅ 数据验证

### 自动验证

执行脚本后，系统会自动进行以下验证：

1. **数据完整性检查**
   - 外键约束验证
   - 唯一性约束验证
   - 非空约束验证

2. **业务数据验证**
   - 用户角色分配检查
   - 学生班级关联检查
   - 课程选课数据检查

3. **统计信息验证**
   - 数据量统计
   - 分布情况检查
   - 性能指标分析

### 手动验证

#### 检查表结构
```sql
-- 查看所有表
SHOW TABLES;

-- 检查表结构
DESC tb_user;
DESC tb_student;
DESC tb_course;
```

#### 检查数据量
```sql
-- 用户统计
SELECT 
    '用户总数' as 统计项,
    COUNT(*) as 数量 
FROM tb_user WHERE deleted = 0;

-- 学生统计
SELECT 
    '学生总数' as 统计项,
    COUNT(*) as 数量 
FROM tb_student WHERE deleted = 0;

-- 课程统计
SELECT 
    '课程总数' as 统计项,
    COUNT(*) as 数量 
FROM tb_course WHERE deleted = 0;
```

#### 检查角色权限
```sql
-- 检查管理员权限
SELECT u.username, r.role_name 
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.username = 'admin';
```

### 预期结果

执行完成后，应该看到以下结果：

| 数据类型 | 预期数量 | 说明 |
|---------|----------|------|
| 数据表 | 35张 | 完整的业务表结构 |
| 用户总数 | 15,000+ | 包含各种角色用户 |
| 学生数量 | 10,000+ | 基本学生信息 |
| 教师数量 | 500+ | 教师和班主任 |
| 班级数量 | 200+ | 不同专业班级 |
| 课程数量 | 1,000+ | 各类课程信息 |
| 选课记录 | 30,000+ | 学生选课数据 |
| 成绩记录 | 30,000+ | 课程成绩数据 |
| 考勤记录 | 20,000+ | 考勤签到数据 |
| 缴费记录 | 15,000+ | 财务缴费数据 |

## 🔄 维护和更新

### 数据备份

```bash
# 备份完整数据库
mysqldump -u用户名 -p密码 campus_management_db > backup.sql

# 仅备份表结构
mysqldump -u用户名 -p密码 --no-data campus_management_db > schema.sql
```

### 增量更新

如果需要更新现有数据：

1. **增加新的SQL脚本文件**
2. **修改DatabaseInitializer.java中的文件列表**
3. **重新执行初始化程序**

### 重置数据库

```sql
-- 删除数据库
DROP DATABASE IF EXISTS campus_management_db;

-- 重新创建
CREATE DATABASE campus_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 📞 技术支持

如果在使用过程中遇到问题，请：

1. **查看日志文件** - 详细的错误信息
2. **检查环境配置** - 数据库连接、权限等
3. **参考故障排除** - 常见问题解决方案
4. **联系技术支持** - 提供详细的错误信息

---

## 📝 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2025-06-08 | 初始版本，包含完整的数据库初始化功能 |

---

**智慧校园管理系统开发团队**  
**版本**: 1.0.0  
**更新时间**: 2025年1月27日