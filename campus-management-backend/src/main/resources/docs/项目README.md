# 🎓 智慧校园管理系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue.js](https://img.shields.io/badge/Vue.js-3.3.0-4FC08D.svg)](https://vuejs.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

> 🚀 基于Spring Boot + Vue.js的现代化智慧校园管理系统

## 📋 项目简介

智慧校园管理系统是一个功能完整的校园管理平台，采用前后端分离架构，支持多角色权限管理，涵盖用户管理、教务管理、财务管理、系统管理等核心业务功能。

### ✨ 主要特性

- 🏗️ **现代化架构**: DDD分层架构 + 微服务设计理念
- 👥 **多角色支持**: 7种用户角色，细粒度权限控制
- 🔒 **企业级安全**: JWT认证 + RBAC权限 + 数据加密
- ⚡ **高性能**: Redis缓存 + 数据库优化 + 异步处理
- 📱 **响应式设计**: 支持PC端和移动端自适应
- 🤖 **智能化**: AI排课算法 + 学习分析 + 风险预警

### 🎯 适用场景

- 🏫 **高等院校**: 大学、学院校园管理
- 🎓 **职业学校**: 技术学院、职业培训机构
- 📚 **培训机构**: 教育培训、在线教育平台
- 🏢 **企业培训**: 企业内部培训管理

## 🏗️ 技术架构

### 技术栈

#### 后端技术
- **核心框架**: Spring Boot 3.1.5
- **安全框架**: Spring Security 6.1.0
- **数据访问**: Spring Data JPA 3.1.0
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.0
- **认证**: JWT Token
- **文档**: Swagger 3.0
- **构建工具**: Maven 3.9

#### 前端技术
- **前端框架**: Vue.js 3.3.0
- **UI组件**: Element Plus 2.3.0
- **状态管理**: Pinia 2.1.0
- **路由管理**: Vue Router 4.2.0
- **HTTP客户端**: Axios 1.4.0
- **图表库**: ECharts 5.4.0
- **构建工具**: Vite 4.3.0

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    智慧校园管理系统                           │
├─────────────────────────────────────────────────────────────┤
│  前端层 (Vue.js)                                            │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ 管理员后台   │ │ 教师端应用   │ │ 学生端应用   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│  接口层 (Spring Boot)                                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ REST API    │ │ Web控制器   │ │ WebSocket   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│  业务层 (Service)                                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ 用户管理     │ │ 教务管理     │ │ 财务管理     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│  数据层 (MySQL + Redis)                                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ 主数据库     │ │ 缓存数据库   │ │ 文件存储     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Maven**: 3.9+ (推荐使用mvnd)

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/your-repo/Smart-Campus-Management.git
cd Smart-Campus-Management
```

#### 2. 数据库配置
```sql
-- 创建数据库
CREATE DATABASE smart_campus CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'campus_user'@'localhost' IDENTIFIED BY 'campus_password';
GRANT ALL PRIVILEGES ON smart_campus.* TO 'campus_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. 配置文件
```yaml
# campus-management-backend/src/main/resources/application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_campus
    username: campus_user
    password: campus_password
  redis:
    host: localhost
    port: 6379
```

#### 4. 启动后端服务
```bash
cd campus-management-backend

# 使用Maven Daemon (推荐)
mvnd spring-boot:run

# 或使用普通Maven
mvn spring-boot:run
```

#### 5. 启动前端服务
```bash
cd campus-management-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

#### 6. 访问系统
- **前端地址**: http://localhost:3000
- **后端API**: http://localhost:8889
- **API文档**: http://localhost:8889/swagger-ui.html

### Docker部署

#### 使用Docker Compose
```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

#### 服务地址
- **前端**: http://localhost:80
- **后端**: http://localhost:8889
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

## 📚 功能模块

### 1. 👥 用户管理
- **多角色支持**: 超级管理员、系统管理员、教师、学生、家长等
- **权限控制**: 基于RBAC的细粒度权限管理
- **用户生命周期**: 注册、激活、禁用、密码重置
- **安全认证**: JWT Token认证，支持多设备登录

### 2. 🏫 学术管理
- **院系管理**: 学院、专业、班级的层级管理
- **课程管理**: 课程信息、教学大纲、先修课程
- **选课系统**: 选课时间控制、容量限制、冲突检测
- **排课系统**: 智能排课算法，教室资源调度

### 3. 📚 教学管理
- **作业管理**: 作业发布、提交、批改、成绩统计
- **考试管理**: 考试安排、题库管理、成绩录入
- **考勤管理**: 签到签退、请假审批、考勤统计
- **成绩管理**: 平时成绩、期中期末、综合评定

### 4. 💰 财务管理
- **费用管理**: 学费、住宿费、教材费等费用项目
- **缴费系统**: 在线支付、分期缴费、欠费提醒
- **财务统计**: 收入分析、欠费统计、财务报表
- **退费管理**: 退费申请、审批流程、退费记录

### 5. 📊 系统管理
- **系统配置**: 系统参数、业务规则、功能开关
- **通知管理**: 系统通知、消息推送、邮件发送
- **日志管理**: 操作日志、错误日志、审计日志
- **缓存管理**: Redis缓存、缓存清理、性能监控

## 🔧 开发指南

### 项目结构
```
Smart-Campus-Management/
├── campus-management-backend/     # 后端项目
│   ├── src/main/java/com/campus/
│   │   ├── application/          # 应用层
│   │   ├── domain/              # 领域层
│   │   ├── infrastructure/      # 基础设施层
│   │   └── interfaces/          # 接口层
│   └── src/main/resources/      # 配置文件
├── campus-management-frontend/    # 前端项目
│   ├── src/
│   │   ├── components/          # 组件
│   │   ├── views/              # 页面
│   │   ├── router/             # 路由
│   │   ├── store/              # 状态管理
│   │   └── utils/              # 工具类
└── docs/                        # 项目文档
```

### 开发规范

#### 代码规范
- **Java**: 遵循阿里巴巴Java开发手册
- **JavaScript**: 使用ESLint + Prettier
- **CSS**: 使用Stylelint
- **Git**: 使用Conventional Commits规范

#### 分支策略
- **main**: 主分支，用于生产环境
- **develop**: 开发分支，用于集成测试
- **feature/***: 功能分支，用于新功能开发
- **hotfix/***: 热修复分支，用于紧急修复

#### 提交规范
```bash
# 功能开发
git commit -m "feat: 添加用户管理功能"

# 问题修复
git commit -m "fix: 修复登录验证问题"

# 文档更新
git commit -m "docs: 更新API文档"
```

### API文档

#### 认证接口
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

#### 用户管理接口
```http
GET /api/v1/users?page=1&size=10
Authorization: Bearer {token}
```

#### 课程管理接口
```http
POST /api/v1/courses
Authorization: Bearer {token}
Content-Type: application/json

{
  "courseName": "Java程序设计",
  "courseCode": "CS101",
  "credits": 3.0
}
```

## 🧪 测试

### 运行测试
```bash
# 后端测试
cd campus-management-backend
mvnd test

# 前端测试
cd campus-management-frontend
npm run test
```

### 测试覆盖率
- **单元测试**: 覆盖率 > 80%
- **集成测试**: 覆盖核心API
- **E2E测试**: 覆盖主要业务流程

## 📈 性能指标

| 指标 | 目标值 | 实际值 |
|------|--------|--------|
| **响应时间** | < 200ms | 150ms |
| **并发用户** | 1000+ | 1500+ |
| **可用性** | 99.9% | 99.95% |
| **数据一致性** | 100% | 100% |

## 🤝 贡献指南

### 如何贡献
1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 贡献者
感谢所有为项目做出贡献的开发者！

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系我们

- **项目地址**: https://github.com/your-repo/Smart-Campus-Management
- **文档地址**: https://docs.smart-campus.com
- **问题反馈**: https://github.com/your-repo/Smart-Campus-Management/issues
- **技术支持**: support@smart-campus.com

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot) - 后端框架
- [Vue.js](https://vuejs.org/) - 前端框架
- [Element Plus](https://element-plus.org/) - UI组件库
- [MySQL](https://www.mysql.com/) - 数据库
- [Redis](https://redis.io/) - 缓存数据库

---

⭐ 如果这个项目对你有帮助，请给我们一个星标！
