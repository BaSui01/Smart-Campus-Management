# 智慧校园管理系统 - 脚本工具集

本目录包含了智慧校园管理系统的数据库初始化脚本、API测试工具和相关文档。

## 📁 文件清单

### 🗄️ 数据库初始化脚本
| 文件名 | 类型 | 平台 | 描述 |
|--------|------|------|------|
| `run-database-setup.bat` | 完整脚本 | Windows | **改进版**数据库初始化脚本，支持模式选择 |
| `run-database-setup.sh` | 完整脚本 | Linux/Mac | 完整的数据库初始化脚本，支持彩色输出 |
| `quick-setup.bat` | 快速脚本 | Windows | 简化的快速初始化脚本 |
| `quick-setup.sh` | 快速脚本 | Linux/Mac | 简化的快速初始化脚本 |
| `test-system-settings.bat` | 测试脚本 | Windows | **新增**系统设置表测试脚本 |

### 🧪 API测试脚本
| 文件名 | 自动化程度 | 接口数 | 描述 |
|--------|-----------|--------|------|
| `quick-api-test.bat` | 半自动 | 7个 | **新增推荐**快速API测试，适合新手 |
| `auto-api-test.bat` | 全自动 | 13个 | **新增**自动化API测试，适合CI/CD |
| `test-api-endpoints.bat` | 手动 | 18个 | **改进版**手动API测试，适合开发者 |
| `run-api-tests.bat` | 半自动 | 基础 | 原API测试脚本 |
| `run-api-tests.sh` | 半自动 | 基础 | 原API测试脚本 (Linux/Mac) |
| `test-api-endpoints.sh` | 手动 | 基础 | API端点测试脚本 (Linux/Mac) |

### 📚 文档和指南
| 文件名 | 类型 | 描述 |
|--------|------|------|
| `API_TESTING_GUIDE.md` | 指南 | **新增**详细的API测试指南 |
| `DATABASE_FIX_GUIDE.md` | 指南 | **新增**数据库问题修复指南 |
| `README.md` | 说明 | 本文件，脚本工具集说明 |

## 🚀 快速开始

### 📋 推荐使用流程

#### 1. 数据库初始化 (首次部署)
```batch
# Windows - 改进版，支持模式选择
cd campus-management-backend\scripts
run-database-setup.bat
# 选择 1: 基础模式 (推荐开发环境)
# 选择 2: 完整模式 (包含大规模测试数据)
```

#### 2. API功能测试 (验证系统)
```batch
# 启动后端服务
mvn spring-boot:run

# 快速API测试 (推荐新手)
quick-api-test.bat
```

### 🗄️ 数据库初始化选项

#### 方式1：改进版初始化（推荐）
```batch
cd campus-management-backend\scripts
run-database-setup.bat
```

#### 方式2：快速初始化（有经验用户）
```batch
cd campus-management-backend\scripts
quick-setup.bat
```

### 🧪 API测试选项

#### 方式1：快速测试（推荐新手）
```batch
# 测试7个核心API接口
quick-api-test.bat
```

#### 方式2：全面测试（推荐开发者）
```batch
# 测试18个API接口，需要手动输入token
test-api-endpoints.bat
```

#### 方式3：自动化测试（推荐CI/CD）
```batch
# 全自动测试，需要PowerShell支持
auto-api-test.bat
```

### 🐧 Linux/Mac用户

#### 数据库初始化
```bash
cd campus-management-backend/scripts
chmod +x run-database-setup.sh
./run-database-setup.sh
```

#### API测试
```bash
chmod +x test-api-endpoints.sh
./test-api-endpoints.sh
```

## ⚙️ 环境变量配置

在执行脚本前，可以设置以下环境变量来避免交互输入：

### Windows
```batch
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=campus_management_db
set DB_USER=root
set DB_PASSWORD=your_password
```

### Linux/Mac
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=campus_management_db
export DB_USER=root
export DB_PASSWORD=your_password
```

## 📊 执行结果

成功执行后，您将得到：

- ✅ **35张数据表** - 完整的校园管理系统表结构
- ✅ **15,000+用户** - 包含管理员、教师、学生、家长等角色
- ✅ **完整业务数据** - 课程、班级、成绩、考勤、缴费等数据
- ✅ **系统配置** - 角色权限、系统参数等配置
- ✅ **测试账号** - 可立即使用的测试账号

### 默认账号信息

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 系统管理员 | admin | admin123 | 拥有所有权限的超级管理员 |
| 教师示例 | teacher001 | 123456 | 教师角色测试账号 |
| 学生示例 | student001 | 123456 | 学生角色测试账号 |
| 家长示例 | parent001 | 123456 | 家长角色测试账号 |

## 🔧 脚本功能对比

| 功能 | 完整脚本 | 快速脚本 |
|------|----------|----------|
| 详细进度显示 | ✅ | ❌ |
| 错误处理和提示 | ✅ | ✅ |
| 彩色输出 | ✅ | ✅ |
| 数据验证 | ✅ | ✅ |
| 统计信息显示 | ✅ | ✅ |
| 交互式配置 | ✅ | ❌ |
| 执行时间 | 较长 | 较短 |
| 适用场景 | 首次安装 | 快速重建 |

## ⚠️ 注意事项

1. **数据库权限** - 确保数据库用户有创建数据库和表的权限
2. **磁盘空间** - 建议至少2GB可用空间
3. **网络连接** - 如果数据库在远程，确保网络畅通
4. **MySQL版本** - 支持MySQL 5.7+和8.0+
5. **字符编码** - 系统使用UTF-8编码，确保数据库支持

## 🛠️ 故障排除

### 常见问题

#### 1. 权限问题
```sql
-- 如果遇到权限错误，可以尝试：
GRANT ALL PRIVILEGES ON *.* TO 'username'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```

#### 2. 连接问题
```bash
# 检查MySQL服务状态
# Windows:
net start mysql

# Linux:
systemctl status mysql
sudo systemctl start mysql
```

#### 3. 编码问题
```sql
-- 确保数据库使用正确的字符集
CREATE DATABASE campus_management_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

## 📋 执行检查清单

在执行脚本前，请确认：

- [ ] MySQL服务正在运行
- [ ] 数据库用户有足够权限
- [ ] 磁盘空间充足（推荐2GB+）
- [ ] 网络连接正常（如果数据库在远程）
- [ ] 已备份现有数据（如果有）

## 🔄 重新初始化

如果需要重新初始化数据库：

1. **删除现有数据库**
   ```sql
   DROP DATABASE IF EXISTS campus_management_db;
   ```

2. **重新执行初始化脚本**
   按照上述方式重新运行脚本即可。

## 📞 获取帮助

如果遇到问题：

1. 查看 `docs/DATABASE_SETUP_GUIDE.md` 获取详细文档
2. 检查脚本执行日志中的错误信息
3. 确认数据库配置和权限设置
4. 联系技术支持团队

## 🧪 API测试功能

### 测试脚本对比
| 脚本名称 | 自动化程度 | 测试接口数 | 适用场景 | 输出格式 |
|---------|-----------|-----------|----------|----------|
| `quick-api-test.bat` | 半自动 | 7个核心接口 | 快速验证 | JSON文件 |
| `auto-api-test.bat` | 全自动 | 13个接口 | CI/CD集成 | JSON文件 |
| `test-api-endpoints.bat` | 手动 | 18个接口 | 详细测试 | JSON文件 |

### 测试输出文件
```
api-test-results/          # 手动测试结果
quick-test-results/        # 快速测试结果
├── *.json                # API响应数据
└── *_REPORT_*.txt        # 测试报告
```

### 管理员账号信息
- **用户名**: `admin`
- **密码**: `admin123`
- **服务器**: `http://localhost:8082`

## 🔗 相关资源

- 📖 **API文档**: http://localhost:8082/api/swagger-ui.html
- 🏥 **健康检查**: http://localhost:8082/actuator/health
- 📊 **系统监控**: http://localhost:8082/actuator/metrics
- 🧪 **单元测试**: `mvn test`

## 📝 更新日志

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.1.0 | 2025-06-11 | 新增API测试脚本和详细指南 |
| 1.0.1 | 2025-06-11 | 修复系统设置表字段问题，改进数据库脚本 |
| 1.0.0 | 2025-01-27 | 初始版本，包含完整和快速两种初始化方式 |

---

**智慧校园管理系统开发团队**
**版本**: 1.1.0
**更新时间**: 2025年6月11日