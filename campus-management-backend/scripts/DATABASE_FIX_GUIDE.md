# 数据库设置脚本修复指南

## 问题描述

在执行数据库初始化脚本时遇到以下错误：
```
ERROR 1054 (42S22) at line 996: Unknown column 'setting_group' in 'field list'
```

## 问题原因

1. **字段名不匹配**：`04_complete_all_tables.sql` 中使用了 `setting_group` 字段，但表结构定义中使用的是 `category` 字段
2. **字段缺失**：INSERT语句使用了表结构中不存在的字段：
   - `setting_group` → 应为 `category`
   - `is_editable` → 表结构中不存在
   - `display_order` → 应为 `sort_order`

## 修复内容

### 1. 修复系统设置表数据插入 (04_complete_all_tables.sql)

**修复前：**
```sql
INSERT IGNORE INTO tb_system_settings (
    setting_key, setting_value, setting_type, setting_group, setting_name,
    description, is_editable, display_order, status, deleted
)
```

**修复后：**
```sql
INSERT IGNORE INTO tb_system_settings (
    setting_key, setting_value, setting_type, category, setting_name,
    description, sort_order, status, deleted
)
```

### 2. 改进数据库初始化脚本 (run-database-setup.bat)

**新增功能：**
- 添加执行模式选择（基础模式/完整模式）
- 基础模式：跳过大规模数据生成，适合开发测试
- 完整模式：包含所有数据，适合压力测试
- 改进错误处理和提示信息
- 添加数据统计显示

### 3. 新增测试脚本 (test-system-settings.bat)

**功能：**
- 独立测试系统设置表的数据插入功能
- 验证表结构和字段匹配
- 提供详细的错误诊断信息

## 使用方法

### 方法1：使用改进的初始化脚本

```bash
cd campus-management-backend/scripts
run-database-setup.bat
```

选择执行模式：
- 输入 `1`：基础模式（推荐，快速初始化）
- 输入 `2`：完整模式（包含大规模测试数据）

### 方法2：先测试系统设置表

```bash
cd campus-management-backend/scripts
test-system-settings.bat
```

确认测试通过后，再执行完整初始化：
```bash
run-database-setup.bat
```

### 方法3：手动执行SQL文件

```bash
# 1. 创建表结构
mysql -u root -p < ../src/main/resources/db/01_create_tables.sql

# 2. 插入基础数据
mysql -u root -p < ../src/main/resources/db/02_insert_basic_data.sql

# 3. 补充完整表数据（已修复）
mysql -u root -p < ../src/main/resources/db/04_complete_all_tables.sql

# 4. 数据验证
mysql -u root -p < ../src/main/resources/db/05_data_validation.sql

# 5. 数据分析
mysql -u root -p < ../src/main/resources/db/06_data_analysis.sql
```

## 验证修复结果

执行以下SQL验证系统设置表数据：

```sql
USE campus_management_db;

-- 检查表结构
DESCRIBE tb_system_settings;

-- 查看系统设置数据
SELECT setting_key, setting_value, setting_type, category, setting_name 
FROM tb_system_settings 
ORDER BY category, sort_order;

-- 统计各分类的设置数量
SELECT category, COUNT(*) as count 
FROM tb_system_settings 
WHERE deleted = 0 
GROUP BY category;
```

## 预期结果

修复后应该看到以下系统设置分类：
- `appearance`：外观设置（主题色、辅助色）
- `pagination`：分页设置（默认大小、选项）
- `security`：安全设置（会话超时、密码复杂度）
- `system`：系统设置（自动备份、备份间隔）
- `notification`：通知设置（邮件、短信通知）

## 注意事项

1. **备份数据**：在执行修复脚本前，建议备份现有数据库
2. **权限检查**：确保数据库用户有足够的权限执行DDL和DML操作
3. **字符编码**：确保数据库和客户端使用UTF-8编码
4. **内存限制**：如果选择完整模式，确保系统有足够内存处理大规模数据

## 故障排除

如果仍然遇到问题，请检查：

1. **MySQL版本兼容性**：建议使用MySQL 8.0+
2. **字符集设置**：数据库应使用utf8mb4字符集
3. **SQL模式**：检查MySQL的sql_mode设置
4. **文件路径**：确保SQL文件路径正确
5. **网络连接**：检查数据库连接参数

## 联系支持

如果问题仍然存在，请提供以下信息：
- MySQL版本
- 操作系统版本
- 完整的错误日志
- 数据库配置信息
