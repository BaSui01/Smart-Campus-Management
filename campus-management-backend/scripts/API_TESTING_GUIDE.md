# 智慧校园管理系统 - API测试指南

## 📋 概述

本指南提供了三种不同的API测试方式，使用curl命令测试后端API接口，并将JSON响应数据保存到txt文件中进行分析。

## 🔧 前置条件

### 1. 环境要求
- ✅ Windows 10/11 操作系统
- ✅ PowerShell 5.0+ (用于自动化脚本)
- ✅ curl 命令行工具 (Windows 10+ 内置)
- ✅ 后端服务运行在 `http://localhost:8082`

### 2. 系统准备
```bash
# 1. 启动后端服务
cd campus-management-backend
mvn spring-boot:run

# 2. 确认服务运行
curl http://localhost:8082/actuator/health
```

### 3. 管理员账号
- **用户名**: `admin`
- **密码**: `admin123`

## 🚀 测试脚本说明

### 1. 快速测试脚本 (推荐新手)
**文件**: `quick-api-test.bat`

**特点**:
- ⚡ 快速测试核心API (5个接口)
- 📁 结果保存为JSON文件
- 🎯 适合快速验证系统功能

**使用方法**:
```bash
cd campus-management-backend/scripts
quick-api-test.bat
```

**测试内容**:
1. 服务器健康检查
2. 用户登录认证
3. 当前用户信息
4. 用户列表
5. 学生列表
6. 课程列表
7. 系统信息

### 2. 手动测试脚本 (推荐开发者)
**文件**: `test-api-endpoints.bat`

**特点**:
- 🔧 手动输入Token
- 📊 全面测试 (18个接口)
- 📝 详细的测试过程显示

**使用方法**:
```bash
cd campus-management-backend/scripts
test-api-endpoints.bat
# 按提示输入从登录响应中获取的token
```

**测试内容**:
- 认证API (2个)
- 用户管理API (2个)
- 学生管理API (2个)
- 课程管理API (3个)
- 系统管理API (2个)
- 权限管理API (2个)
- 学术管理API (3个)
- 特殊功能API (2个)

### 3. 全面自动化测试脚本 (推荐CI/CD) - **新增扩展版**
**文件**: `auto-api-test.bat`

**特点**:
- 🤖 全自动执行，无需手动干预
- 🔍 自动提取Token，智能错误处理
- 📊 全面覆盖36个API控制器，55个核心接口
- 🎯 按8大业务模块分组测试
- 📋 自动生成详细测试报告和映射文档

**使用方法**:
```bash
cd campus-management-backend/scripts
auto-api-test.bat
```

**测试覆盖**:
- 认证模块 (1个接口)
- 核心业务模块 (12个接口)
- 学术管理模块 (10个接口)
- 系统管理模块 (7个接口)
- 权限管理模块 (4个接口)
- 财务管理模块 (3个接口)
- 特殊功能模块 (8个接口)
- 扩展功能模块 (10个接口)

**注意**: 需要PowerShell支持用于JSON解析

## 📊 测试脚本功能对比

| 脚本名称 | 自动化程度 | 测试接口数 | 业务模块 | 适用场景 | 输出格式 |
|---------|-----------|-----------|----------|----------|----------|
| `quick-api-test.bat` | 半自动 | 7个核心接口 | 基础验证 | 快速验证 | JSON文件 |
| `auto-api-test.bat` | **全自动** | **55个接口** | **8大模块** | **全面测试/CI/CD** | JSON文件 |
| `test-api-endpoints.bat` | 手动 | 18个接口 | 核心功能 | 详细测试 | JSON文件 |

### 全面自动化测试脚本特色功能

**`auto-api-test.bat` 新增特性**:
- 🎯 **全覆盖**: 36个API控制器，55个核心接口
- 🏗️ **模块化**: 按8大业务模块分组测试
- 🤖 **智能化**: 自动token提取，无需手动干预
- 📊 **详细报告**: 生成完整的测试映射和统计报告
- 🔍 **错误诊断**: 智能错误处理和问题定位

**测试模块覆盖**:
1. 认证模块 (1个接口)
2. 核心业务模块 (12个接口) - 用户、学生、课程、院系、班级
3. 学术管理模块 (10个接口) - 作业、考勤、考试、成绩、选课
4. 系统管理模块 (7个接口) - 仪表盘、系统、通知、消息
5. 权限管理模块 (4个接口) - 角色、权限
6. 财务管理模块 (3个接口) - 支付、费用项目
7. 特殊功能模块 (8个接口) - 排课、课表、教室、缓存
8. 扩展功能模块 (10个接口) - 日志、提交、题目等

## 📁 输出文件说明

### 文件命名规则
```
{序号}_{接口名称}_{时间戳}.{扩展名}
```

### 快速测试输出文件示例
```
quick-test-results/
├── health_20250611_143022.json          # 服务器健康检查
├── login_20250611_143022.json           # 登录响应 (包含token)
├── user_info_20250611_143022.json       # 当前用户信息
├── users_list_20250611_143022.json      # 用户列表
├── students_list_20250611_143022.json   # 学生列表
├── courses_list_20250611_143022.json    # 课程列表
├── system_info_20250611_143022.json     # 系统信息
└── QUICK_TEST_REPORT_20250611_143022.txt # 测试报告
```

### 全面自动化测试输出文件示例
```
api-test-results/
├── 00_server_health_20250611_143022.txt           # 服务器健康检查
├── 01_login_response_20250611_143022.txt          # 登录响应
├── 02_auth_current_user_20250611_143022.txt       # 当前用户信息
├── 03_users_list_20250611_143022.txt              # 用户列表
├── 04_users_stats_20250611_143022.txt             # 用户统计
├── 05_users_count_20250611_143022.txt             # 用户计数
├── 06_students_list_20250611_143022.txt           # 学生列表
├── 07_students_stats_20250611_143022.txt          # 学生统计
├── 08_students_count_20250611_143022.txt          # 学生计数
├── 09_courses_list_20250611_143022.txt            # 课程列表
├── 10_courses_stats_20250611_143022.txt           # 课程统计
├── 11_departments_list_20250611_143022.txt        # 院系列表
├── 12_departments_tree_20250611_143022.txt        # 院系树结构
├── 13_classes_list_20250611_143022.txt            # 班级列表
├── 14_classes_stats_20250611_143022.txt           # 班级统计
├── 15_assignments_list_20250611_143022.txt        # 作业列表
├── 16_assignments_stats_20250611_143022.txt       # 作业统计
├── 17_attendance_list_20250611_143022.txt         # 考勤列表
├── 18_attendance_stats_20250611_143022.txt        # 考勤统计
├── 19_exams_list_20250611_143022.txt              # 考试列表
├── 20_exams_stats_20250611_143022.txt             # 考试统计
├── 21_grades_list_20250611_143022.txt             # 成绩列表
├── 22_grades_stats_20250611_143022.txt            # 成绩统计
├── 23_course_selections_list_20250611_143022.txt  # 选课列表
├── 24_course_selection_periods_20250611_143022.txt # 选课时段
├── 25_dashboard_stats_20250611_143022.txt         # 仪表盘统计
├── 26_dashboard_activities_20250611_143022.txt    # 仪表盘活动
├── 27_system_info_20250611_143022.txt             # 系统信息
├── 28_system_config_20250611_143022.txt           # 系统配置
├── 29_notifications_list_20250611_143022.txt      # 通知列表
├── 30_notification_templates_20250611_143022.txt  # 通知模板
├── 31_messages_list_20250611_143022.txt           # 消息列表
├── 32_roles_list_20250611_143022.txt              # 角色列表
├── 33_roles_stats_20250611_143022.txt             # 角色统计
├── 34_permissions_list_20250611_143022.txt        # 权限列表
├── 35_permissions_tree_20250611_143022.txt        # 权限树结构
├── 36_payments_list_20250611_143022.txt           # 支付列表
├── 37_payments_stats_20250611_143022.txt          # 支付统计
├── 38_fee_items_list_20250611_143022.txt          # 费用项目列表
├── 39_auto_schedule_status_20250611_143022.txt    # 自动排课状态
├── 40_auto_schedule_config_20250611_143022.txt    # 自动排课配置
├── 41_schedules_list_20250611_143022.txt          # 课表列表
├── 42_course_schedules_list_20250611_143022.txt   # 课程安排列表
├── 43_classrooms_list_20250611_143022.txt         # 教室列表
├── 44_classrooms_stats_20250611_143022.txt        # 教室统计
├── 45_cache_stats_20250611_143022.txt             # 缓存统计
├── 46_cache_info_20250611_143022.txt              # 缓存信息
├── 47_activity_logs_list_20250611_143022.txt      # 活动日志列表
├── 48_activity_logs_stats_20250611_143022.txt     # 活动日志统计
├── 49_assignment_submissions_20250611_143022.txt  # 作业提交
├── 50_exam_questions_20250611_143022.txt          # 考试题目
├── 51_exam_records_20250611_143022.txt            # 考试记录
├── 52_course_resources_20250611_143022.txt        # 课程资源
├── 53_parent_student_relations_20250611_143022.txt # 家长学生关系
├── 54_student_evaluations_20250611_143022.txt     # 学生评价
├── 55_time_slots_20250611_143022.txt              # 时间段
└── API_COMPREHENSIVE_TEST_REPORT_20250611_143022.txt # 详细测试报告
```

## 🔍 响应数据分析

### 1. 成功响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据内容
  },
  "timestamp": "2025-06-11T14:30:22"
}
```

### 2. 登录响应示例
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200000,
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "email": "admin@campus.com"
    }
  }
}
```

### 3. 分页数据格式
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0
  }
}
```

### 4. 错误响应格式
```json
{
  "code": 401,
  "message": "未授权访问",
  "timestamp": "2025-06-11T14:30:22"
}
```

## 🛠️ 故障排除

### 常见问题

#### 1. 服务器连接失败
**错误**: `curl: (7) Failed to connect to localhost port 8082`
**解决**:
```bash
# 检查服务是否启动
netstat -an | findstr :8082
# 启动后端服务
mvn spring-boot:run
```

#### 2. 登录失败
**错误**: `{"code":401,"message":"用户名或密码错误"}`
**解决**:
- 确认用户名密码: admin/admin123
- 检查数据库中用户数据是否存在

#### 3. Token无效
**错误**: `{"code":401,"message":"Token无效或已过期"}`
**解决**:
- 重新执行登录获取新token
- 检查token是否完整复制

#### 4. 权限不足
**错误**: `{"code":403,"message":"权限不足"}`
**解决**:
- 确认使用管理员账号登录
- 检查用户角色和权限配置

### 调试技巧

#### 1. 查看详细错误信息
```bash
# 添加详细输出
curl -v -X GET "http://localhost:8082/api/users" \
     -H "Authorization: Bearer YOUR_TOKEN"
```

#### 2. 检查HTTP状态码
```bash
# 只显示状态码
curl -s -o /dev/null -w "%{http_code}" \
     "http://localhost:8082/api/users"
```

#### 3. 格式化JSON输出
```bash
# 使用jq格式化 (需要安装jq)
curl -s "http://localhost:8082/api/users" | jq .
```

## 📊 测试最佳实践

### 1. 测试顺序
1. 🔍 **健康检查** - 确认服务可用
2. 🔐 **用户登录** - 获取认证token
3. 👤 **用户信息** - 验证token有效性
4. 📋 **列表接口** - 测试数据查询
5. 🔧 **管理接口** - 测试业务功能

### 2. 数据验证要点
- ✅ HTTP状态码为200
- ✅ 响应包含完整JSON结构
- ✅ 数据字段类型正确
- ✅ 分页信息完整
- ✅ 时间戳格式正确

### 3. 性能监控
```bash
# 测试响应时间
curl -w "时间: %{time_total}s\n" \
     -o /dev/null -s \
     "http://localhost:8082/api/users"
```

## 🔗 相关资源

- 📖 **API文档**: http://localhost:8082/api/swagger-ui.html
- 🧪 **单元测试**: `mvn test`
- 📊 **健康检查**: http://localhost:8082/actuator/health
- 📈 **监控指标**: http://localhost:8082/actuator/metrics

## 📞 技术支持

如遇到问题，请提供以下信息：
1. 使用的测试脚本名称
2. 完整的错误信息
3. 相关的JSON响应文件
4. 系统环境信息 (Windows版本、PowerShell版本等)
