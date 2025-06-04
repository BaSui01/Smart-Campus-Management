# 智慧校园管理平台 - 后端服务

## 项目概述

智慧校园管理平台是一个基于Spring Boot 3.1.5 + Java 17的现代化教育管理系统，提供完整的教务管理、学生管理、财务管理等功能。

## 技术栈

### 核心框架
- **Spring Boot**: 3.1.5
- **Java**: 17
- **Maven**: 项目构建工具

### 数据层
- **MySQL**: 8.0.33 (主数据库)
- **Redis**: 缓存和会话管理
- **MyBatis Plus**: 3.0.2 (ORM框架)
- **Spring Data JPA**: 数据访问层

### 安全认证
- **Spring Security**: 安全框架
- **JWT**: JSON Web Token认证
- **BCrypt**: 密码加密

### API文档
- **SpringDoc OpenAPI**: 2.2.0 (Swagger文档)

### 其他组件
- **Spring Boot Quartz**: 定时任务
- **Spring Boot Mail**: 邮件服务
- **Thymeleaf**: 后台管理模板引擎
- **HuTool**: 5.8.21 (Java工具类库)

## 项目结构

```
campus-management-backend/
├── src/main/java/com/campus/
│   ├── CampusManagementApplication.java    # 应用启动类
│   ├── common/                             # 公共组件
│   │   └── ApiResponse.java               # 统一API响应类
│   ├── controller/                         # 控制器层
│   │   └── UserController.java           # 用户管理控制器
│   ├── entity/                            # 实体类
│   │   ├── User.java                     # 用户实体
│   │   ├── Role.java                     # 角色实体
│   │   ├── Permission.java               # 权限实体
│   │   ├── UserRole.java                 # 用户角色关联
│   │   ├── RolePermission.java           # 角色权限关联
│   │   ├── Student.java                  # 学生实体
│   │   └── Course.java                   # 课程实体
│   ├── service/                          # 服务层
│   ├── repository/                       # 数据访问层
│   ├── config/                           # 配置类
│   ├── security/                         # 安全配置
│   ├── scheduler/                        # 定时任务
│   └── utils/                            # 工具类
│       └── JwtUtil.java                  # JWT工具类
├── src/main/resources/
│   ├── application.yml                   # 主配置文件
│   ├── templates/                        # Thymeleaf模板
│   └── static/                          # 静态资源
└── pom.xml                              # Maven配置文件
```

## 核心功能模块

### 1. 用户权限管理
- ✅ 用户实体 (User)
- ✅ 角色实体 (Role)
- ✅ 权限实体 (Permission)
- ✅ 用户角色关联 (UserRole)
- ✅ 角色权限关联 (RolePermission)
- ✅ JWT认证工具类
- ✅ 用户管理API (登录、注册、密码修改等)

### 2. 教务管理
- ✅ 学生实体 (Student)
- ✅ 课程实体 (Course)
- ⏳ 班级管理
- ⏳ 课程安排
- ⏳ 选课管理
- ⏳ 成绩管理

### 3. 考试系统
- ⏳ 考试管理
- ⏳ 题库管理
- ⏳ 在线考试
- ⏳ 自动阅卷

### 4. 财务管理
- ⏳ 缴费项目管理
- ⏳ 缴费记录
- ⏳ 财务统计

### 5. 消息通知
- ⏳ 消息模板
- ⏳ 消息推送
- ⏳ 邮件通知

### 6. 系统管理
- ⏳ 系统配置
- ⏳ 操作日志
- ⏳ 数据备份

## 已完成的核心组件

### 1. 统一API响应 (ApiResponse)
提供标准化的API响应格式，包含：
- 响应码 (code)
- 响应消息 (message)
- 响应数据 (data)
- 时间戳 (timestamp)

### 2. JWT工具类 (JwtUtil)
完整的JWT令牌管理功能：
- 生成访问令牌和刷新令牌
- 令牌验证和解析
- 令牌刷新机制
- 从令牌提取用户信息

### 3. 用户管理API (UserController)
基础的用户管理接口：
- POST `/api/users/login` - 用户登录
- POST `/api/users/refresh` - 刷新令牌
- GET `/api/users/profile` - 获取用户信息
- POST `/api/users/register` - 用户注册
- PUT `/api/users/password` - 修改密码
- POST `/api/users/logout` - 用户登出

### 4. 后台管理界面 (Thymeleaf模板)
完整的后台管理系统：
- **登录认证**: 支持验证码的安全登录页面
- **仪表盘**: 数据统计、图表展示、快速操作
- **系统管理**: 用户管理、角色管理、权限管理、系统设置
- **教务管理**: 课程管理、班级管理、课程安排、学生管理
- **财务管理**: 学费管理、缴费记录、财务报表
- **系统测试**: 功能测试页面，验证系统各项功能

### 5. 权限控制系统
基于拦截器的权限验证：
- 登录状态检查
- 角色权限验证
- 页面访问控制
- 会话管理

### 6. 验证码功能
图形验证码系统：
- 动态验证码生成
- 防暴力破解
- 自动刷新机制

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management
    username: campus_user
    password: campus_password
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      database: 0
```

### JWT配置
```yaml
jwt:
  secret: campus-management-jwt-secret-key-2024
  expiration: 86400000  # 24小时
  refresh-expiration: 604800000  # 7天
  header: Authorization
  prefix: Bearer
```

## API文档

项目集成了SpringDoc OpenAPI，启动项目后可访问：
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- API文档: http://localhost:8080/api/v3/api-docs

## 开发进度

### 已完成 ✅
- [x] 项目基础架构搭建
- [x] Maven依赖配置
- [x] 应用配置文件
- [x] 核心实体类设计
- [x] JWT认证工具类
- [x] 统一API响应类
- [x] 用户管理基础API
- [x] **验证码功能** - 图形验证码生成和验证
- [x] **后台管理界面** - 完整的Thymeleaf模板系统
- [x] **权限控制系统** - 基于拦截器的权限验证
- [x] **登录认证页面** - 支持验证码的安全登录
- [x] **管理后台仪表盘** - 数据统计和快速操作
- [x] **系统管理模块** - 用户、角色、权限、系统设置
- [x] **教务管理模块** - 课程、班级、课程安排、学生管理
- [x] **财务管理模块** - 学费、缴费记录、财务报表

### 进行中 ⏳
- [ ] 数据访问层实现 (Repository/Service)
- [ ] Spring Security安全配置
- [ ] 完善实体类关联关系
- [ ] 业务逻辑实现
- [ ] 后端API与前端模板集成

### 待开发 📋
- [ ] 考试系统API
- [ ] 消息通知系统
- [ ] 定时任务实现
- [ ] 邮件服务集成
- [ ] 数据导入导出功能
- [ ] 单元测试
- [ ] 性能优化
- [ ] 部署配置

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 运行步骤
1. 克隆项目到本地
2. 配置数据库连接信息
3. 启动Redis服务
4. 执行数据库初始化脚本
5. 运行 `mvn spring-boot:run`
6. 访问系统：
   - **系统测试页面**: http://localhost:8080/admin/test
   - **管理员登录**: http://localhost:8080/admin/login
   - **管理后台**: http://localhost:8080/admin/dashboard
   - **API接口**: http://localhost:8080/api
   - **API文档**: http://localhost:8080/api/swagger-ui.html

### 默认登录账户
- **用户名**: admin
- **密码**: admin123
- **角色**: 超级管理员

## 数据库集成

本项目配套完整的数据库设计方案，位于 `../database/` 目录：
- 数据库设计文档
- SQL初始化脚本
- 测试数据脚本
- 一键部署工具

## 下一步计划

1. **完善数据访问层**: 实现Repository和Service层
2. **Spring Security配置**: 完整的认证授权机制
3. **业务API开发**: 教务、考试、财务等核心功能
4. **前端对接准备**: 完善API接口和文档
5. **系统集成测试**: 确保各模块协同工作

## 贡献指南

欢迎提交Issue和Pull Request来改进项目！

## 许可证

MIT License
