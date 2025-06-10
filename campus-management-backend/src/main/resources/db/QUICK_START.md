# Smart Campus Management System - 快速启动指南

## 🚀 快速执行数据库初始化

### 方法一：PowerShell自动化脚本（推荐）

```powershell
# 进入数据库脚本目录
cd E:\code\java_springboot\Smart-Campus-Management\campus-management-backend\src\main\resources\db

# 执行自动化脚本
powershell -ExecutionPolicy Bypass -File Execute-All-Scripts.ps1
```

**操作步骤：**
1. 脚本会自动检查MySQL连接
2. 选择执行方案：
   - `1` - 优化版本（推荐）：高效批量数据生成
   - `2` - 原版本（备用）：分步骤数据生成
   - `3` - 退出
3. 确认执行：输入 `y` 确认，`N` 取消
4. 等待执行完成，查看统计报告

### 方法二：批处理文件（备用）

```cmd
# 进入数据库脚本目录
cd E:\code\java_springboot\Smart-Campus-Management\campus-management-backend\src\main\resources\db

# 执行批处理文件
execute_optimized.bat
```

### 方法三：手动执行（高级用户）

#### 优化版本（推荐）：
```bash
mysql -u root -pxiaoxiao123 < 01_create_complete_tables.sql
mysql -u root -pxiaoxiao123 < 02_insert_large_scale_data.sql
mysql -u root -pxiaoxiao123 < 11_optimized_data_generation.sql
mysql -u root -pxiaoxiao123 < 12_data_validation_and_statistics.sql
```

#### 原版本（备用）：
```bash
mysql -u root -pxiaoxiao123 < 01_create_complete_tables.sql
mysql -u root -pxiaoxiao123 < 02_insert_large_scale_data.sql
mysql -u root -pxiaoxiao123 < 08_complete_data_generation_fixed.sql
mysql -u root -pxiaoxiao123 < 09_business_data_generation_fixed.sql
mysql -u root -pxiaoxiao123 < 10_financial_and_other_data_fixed.sql
mysql -u root -pxiaoxiao123 < 07_data_validation_and_statistics.sql
```

## ⚙️ 系统要求

### 必需条件：
- **MySQL 8.0+** 已安装并运行
- **数据库连接信息**：
  - 主机：localhost
  - 端口：3306
  - 用户：root
  - 密码：xiaoxiao123
- **系统资源**：
  - 内存：至少4GB可用
  - 存储：至少2GB可用空间

### 可选条件：
- **PowerShell 5.0+**（Windows自带）
- **命令行工具**（cmd或PowerShell）

## 📊 执行时间预估

| 执行方案 | 预估时间 | 内存占用 | 数据规模 |
|----------|----------|----------|----------|
| 优化版本 | 8-12分钟 | ~1GB | 15,000用户 |
| 原版本 | 35-50分钟 | ~2.5GB | 15,000用户 |

## 🔍 故障排除

### 常见问题及解决方案：

#### 1. MySQL连接失败
```
错误：Cannot connect to MySQL database
```
**解决方案：**
- 检查MySQL服务是否启动：`services.msc` → 查找MySQL服务
- 验证连接参数：主机、端口、用户名、密码
- 测试连接：`mysql -u root -pxiaoxiao123 -e "SELECT 1;"`

#### 2. PowerShell执行策略限制
```
错误：execution of scripts is disabled on this system
```
**解决方案：**
```powershell
# 临时允许脚本执行
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process

# 或者使用参数执行
powershell -ExecutionPolicy Bypass -File Execute-All-Scripts.ps1
```

#### 3. 文件路径问题
```
错误：File not found
```
**解决方案：**
- 确保在正确的目录下执行脚本
- 检查文件是否存在：`ls *.sql`
- 使用完整路径执行

#### 4. 内存不足
```
错误：Out of memory
```
**解决方案：**
- 关闭其他应用程序释放内存
- 使用原版本分步执行
- 增加MySQL内存配置

#### 5. 脚本执行中断
**解决方案：**
- 重新执行脚本（会自动清理现有数据）
- 检查MySQL错误日志
- 手动执行失败的脚本

## ✅ 验证安装

### 执行完成后验证：

1. **检查数据规模：**
```sql
USE campus_management_db;
SELECT 
    (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) as '用户总数',
    (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) as '学生数',
    (SELECT COUNT(*) FROM tb_class WHERE deleted = 0) as '班级数',
    (SELECT COUNT(*) FROM tb_course WHERE deleted = 0) as '课程数';
```

2. **测试登录账户：**
- 用户名：`admin001`
- 密码：`123456`
- 邮箱：`admin001@university.edu.cn`

3. **运行验证脚本：**
```bash
mysql -u root -pxiaoxiao123 < test_optimization.sql
```

## 📞 技术支持

### 预期结果：
- ✅ 用户总数：~15,000
- ✅ 学生数：~10,000
- ✅ 班级数：200
- ✅ 课程数：1,000
- ✅ 选课记录：~50,000
- ✅ 成绩记录：~30,000

### 如果遇到问题：
1. 查看执行日志和错误信息
2. 检查MySQL错误日志
3. 运行测试脚本验证数据完整性
4. 参考 `README_Database_Optimization.md` 详细文档

---

**快速启动完成！** 🎉

系统现在已准备就绪，可以开始使用Smart Campus Management System进行开发和测试。
