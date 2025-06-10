# Smart Campus Management System - 数据库初始化脚本

## 概述

本目录包含Smart Campus Management System的完整数据库初始化脚本，采用高性能批量INSERT方法替代低效的游标操作，能够快速生成大规模测试数据。

## 文件说明

| 文件名 | 描述 | 数据规模 |
|--------|------|----------|
| `01_create_complete_tables.sql` | 创建完整的数据库表结构 | 30+张表 |
| `02_insert_large_scale_data.sql` | 插入基础数据和配置 | 基础数据 |
| `03_generate_users_and_classes.sql` | 生成用户和班级数据 | 77,900用户 + 800班级 |
| `04_generate_students_and_courses.sql` | 生成学生信息和课程数据 | 40,000学生 + 5,000+课程 |
| `05_generate_course_selections_and_grades.sql` | 生成选课记录和成绩数据 | 选课记录 + 成绩数据 |
| `06_generate_financial_data.sql` | 生成财务和关联数据 | 缴费记录 + 家长关系 |
| `07_data_validation_and_statistics.sql` | 数据验证和统计报告 | 完整性检查 |
| `execute_all_scripts.sql` | 主控制脚本 | 执行说明 |
| `Execute-All-Scripts.ps1` | PowerShell自动化脚本 | 自动执行 |

## 数据规模

### 用户数据 (77,900总用户)
- **管理员**: 100个
- **教师**: 2,000个
- **班主任**: 800个
- **学生**: 40,000个
- **家长**: 35,000个

### 学术数据
- **学院**: 25个 (涵盖理工文医各领域)
- **班级**: 800个 (每个学院每年级6-8个班)
- **课程**: 5,000+门 (每个学院200-250门课程)
- **选课记录**: 大量选课数据
- **成绩记录**: 高年级学生成绩数据

### 管理数据
- **缴费记录**: 学生×费用项目的完整记录
- **考勤记录**: 基于选课的考勤数据
- **通知消息**: 100条系统通知
- **家长关系**: 学生家长关联关系

## 性能优化特性

### 🚀 高性能批量INSERT
- 使用临时表和CROSS JOIN生成大量数据
- 避免低效的游标和循环操作
- 批量INSERT替代逐条插入
- 数学函数生成伪随机数据

### 📊 数据质量保证
- 真实的中文姓名数据
- 合理的数据分布和关联关系
- 完整的外键约束
- 数据完整性验证

### ⚡ 执行效率
- 单个脚本执行时间大幅缩短
- 内存使用优化
- 事务管理优化
- 自动清理临时资源

## 使用方法

### 方法一：PowerShell自动化执行 (推荐)

```powershell
# 在PowerShell中执行
cd campus-management-backend/src/main/resources/db
.\Execute-All-Scripts.ps1
```

### 方法二：手动执行SQL文件

```bash
# 按顺序执行以下命令
mysql -u root -pxiaoxiao123 < 01_create_complete_tables.sql
mysql -u root -pxiaoxiao123 < 02_insert_large_scale_data.sql
mysql -u root -pxiaoxiao123 < 03_generate_users_and_classes.sql
mysql -u root -pxiaoxiao123 < 04_generate_students_and_courses.sql
mysql -u root -pxiaoxiao123 < 05_generate_course_selections_and_grades.sql
mysql -u root -pxiaoxiao123 < 06_generate_financial_data.sql
mysql -u root -pxiaoxiao123 < 07_data_validation_and_statistics.sql
```

### 方法三：MySQL客户端执行

```sql
-- 在MySQL客户端中执行
SOURCE 01_create_complete_tables.sql;
SOURCE 02_insert_large_scale_data.sql;
SOURCE 03_generate_users_and_classes.sql;
SOURCE 04_generate_students_and_courses.sql;
SOURCE 05_generate_course_selections_and_grades.sql;
SOURCE 06_generate_financial_data.sql;
SOURCE 07_data_validation_and_statistics.sql;
```

## 数据库配置

### 连接信息
- **数据库名**: `campus_management_db`
- **用户名**: `root`
- **密码**: `xiaoxiao123`
- **主机**: `localhost`
- **端口**: `3306`

### Spring Boot配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: xiaoxiao123
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

## 默认账户

### 管理员账户
- **用户名**: `admin100001`
- **密码**: `123456`
- **角色**: 系统管理员
- **邮箱**: `admin100001@university.edu.cn`

### 其他用户
- **教师**: `teacher200001` - `teacher202000`
- **班主任**: `classteacher300001` - `classteacher300800`
- **学生**: `student400001` - `student440000`
- **家长**: `parent500001` - `parent535000`
- **统一密码**: `123456` (已加密存储)

## 技术特性

### 数据库设计
- UTF-8编码支持中文
- 统一的BaseEntity字段设计
- 完整的外键约束
- 软删除支持
- 创建和更新时间自动维护

### 数据生成算法
- 数学函数生成伪随机数据
- 循环分配避免数据倾斜
- 批量操作提高性能
- 临时表优化内存使用

## 重要提示

1. **默认管理员账户**: admin100001，密码: 123456
2. **数据库连接密码**: xiaoxiao123
3. **建议在生产环境中修改默认密码**
4. **这些脚本生成的是测试数据，请勿在生产环境中使用**

---

**系统已准备就绪，可以启动Spring Boot应用程序！**