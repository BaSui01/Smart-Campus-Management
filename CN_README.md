<div align="center">

# Smart Campus Management System
## 智慧校园管理系统

<p>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.x-4fc08d.svg" alt="Vue.js">
  <img src="https://img.shields.io/badge/MySQL-8.0+-blue.svg" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-6.0+-red.svg" alt="Redis">
  <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

一个基于Spring Boot和Vue.js的现代化校园管理系统，提供学生、教师、家长和管理员的全方位管理功能。

[快速开始](#-快速开始) •
[功能特色](#-项目特色) •
[技术架构](#-技术栈) •
[API文档](#-api文档) •
[部署指南](#-部署)

</div>

---

## 📋 目录

- [项目特色](#-项目特色)
- [系统架构](#️-系统架构)
- [技术栈](#-技术栈)
- [快速开始](#-快速开始)
- [用户角色](#-用户角色)
- [核心功能模块](#-核心功能模块)
- [数据库设计](#️-数据库设计)
- [配置说明](#-配置说明)
- [API文档](#-api文档)
- [系统监控](#-系统监控)
- [测试](#-测试)
- [部署](#-部署)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)
- [联系我们](#-联系我们)
- [路线图](#-路线图)

---

## 🌟 项目特色

- **🏗️ 现代化架构**: 采用Spring Boot 3.x + Vue 3 + Element Plus技术栈
- **👥 多角色支持**: 学生、教师、家长、管理员四种角色，权限分离
- **⚡ 实时通信**: 基于WebSocket的实时通知系统
- **🚀 高性能**: Redis缓存 + 数据库索引优化，响应速度优异
- **🔒 安全可靠**: JWT认证 + RBAC权限控制，数据安全有保障
- **📱 响应式设计**: 支持PC和移动端访问，自适应各种屏幕尺寸
- **📊 数据分析**: 内置强大的数据分析和报表功能
- **🔧 易于扩展**: 模块化设计，支持功能扩展和定制

---

## 🏗️ 系统架构

```
Smart-Campus-Management/
├── campus-management-backend/     # 后端服务 (Spring Boot)
│   ├── src/main/java/
│   │   ├── application/          # 应用服务层
│   │   ├── domain/              # 领域模型层
│   │   ├── infrastructure/      # 基础设施层
│   │   ├── interfaces/          # 接口层
│   │   └── shared/              # 共享组件
│   └── src/main/resources/
├── campus-management-frontend/    # 前端应用 (Vue.js)
│   ├── src/
│   │   ├── components/          # 公共组件
│   │   ├── views/               # 页面视图
│   │   ├── router/              # 路由配置
│   │   ├── store/               # 状态管理
│   │   └── utils/               # 工具函数
│   └── public/
└── database/                     # 数据库脚本
    ├── migrations/              # 数据库迁移
    └── seeds/                   # 初始数据
```

---

## 💻 技术栈

### 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 主框架 |
| Spring Security | 6.x | 安全框架 |
| Spring Data JPA | 3.x | 数据访问层 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存数据库 |
| JWT | - | 认证令牌 |
| Quartz | 2.x | 定时任务 |
| Swagger | 3.x | API文档 |

### 前端技术
[text](campus-management-backend)
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Vue Router | 4.x | 路由管理 |
| Pinia | 2.x | 状态管理 |
| Element Plus | 2.x | UI组件库 |
| Axios | 1.x | HTTP客户端 |
| Vite | 4.x | 构建工具 |

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Node.js**: 16+
- **MySQL**: 8.0+
- **Redis**: 6+
- **Maven**: 3.6+

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/Smart-Campus-Management.git
cd Smart-Campus-Management
```

### 2. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE campus_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 进入数据库目录
cd database

# 执行初始化脚本
mysql -u root -p campus_management < run_all_scripts.sql
```

### 3. 配置环境变量

创建 `campus-management-backend/src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0

jwt:
  secret: your_jwt_secret_key_here
  expiration: 86400

logging:
  level:
    com.campus: DEBUG
```

### 4. 启动后端服务

```bash
cd campus-management-backend

# 安装依赖并启动
./mvnw clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 5. 启动前端应用

```bash
cd campus-management-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 6. 访问系统

- **前端应用**: http://localhost:5173
- **后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **数据库管理**: http://localhost:8080/h2-console (开发环境)

### 默认账户

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员 |
| 教师 | teacher001 | teacher123 | 示例教师 |
| 学生 | student001 | student123 | 示例学生 |
| 家长 | parent001 | parent123 | 示例家长 |

---

## 👥 用户角色

### 🎓 学生 (Student)

- ✅ 课程选择与退选
- ✅ 个人课程表查看
- ✅ 成绩查询与分析
- ✅ 作业提交与查看
- ✅ 考试安排查看
- ✅ 个人信息管理
- ✅ 通知消息接收

### 👨‍🏫 教师 (Teacher)

- ✅ 课程管理与安排
- ✅ 学生信息管理
- ✅ 成绩录入与统计
- ✅ 作业发布与批改
- ✅ 考试安排与管理
- ✅ 出勤统计与分析
- ✅ 班级管理

### 👨‍👩‍👧‍👦 家长 (Parent)

- ✅ 子女信息查看
- ✅ 成绩监控与分析
- ✅ 出勤情况查看
- ✅ 费用缴纳管理
- ✅ 与教师在线沟通
- ✅ 学校活动参与
- ✅ 家校互动

### 👨‍💼 管理员 (Admin)

- ✅ 用户管理与权限分配
- ✅ 课程管理与维护
- ✅ 系统配置与设置
- ✅ 数据统计与分析
- ✅ 权限管理与控制
- ✅ 系统监控与维护
- ✅ 日志管理

---

## 📊 核心功能模块

### 🎓 学术管理

- **课程管理**: 课程CRUD、课程安排、选课管理、课程评价
- **成绩管理**: 成绩录入、统计分析、成绩单生成、绩点计算
- **考试管理**: 考试安排、监考安排、成绩发布、补考管理

### 👤 用户管理

- **多角色支持**: 学生、教师、家长、管理员四种角色
- **权限控制**: 基于RBAC的细粒度权限管理
- **个人中心**: 信息修改、密码管理、头像上传

### 💰 财务管理

- **费用管理**: 学费、杂费、住宿费等各类费用项目管理
- **缴费记录**: 缴费历史查询、欠费提醒、收据管理
- **财务报表**: 收入统计、欠费分析、财务报表生成

### 📢 通知系统

- **实时通知**: 基于WebSocket的实时消息推送
- **邮件通知**: 重要信息邮件提醒功能
- **系统公告**: 全校通知发布与管理

### 📈 数据分析

- **学习分析**: 成绩趋势分析、学习效果评估
- **使用统计**: 系统使用情况统计分析
- **性能监控**: 系统性能实时监控与预警

---

## 🗄️ 数据库设计

### 核心数据表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 用户基础信息 | id, username, password, email, status |
| `students` | 学生信息 | id, user_id, student_no, class_id, major |
| `teachers` | 教师信息 | id, user_id, teacher_no, department_id |
| `courses` | 课程信息 | id, course_code, name, credits, department_id |
| `course_selections` | 选课记录 | id, student_id, course_id, status, score |
| `grades` | 成绩记录 | id, student_id, course_id, score, grade_type |
| `schedules` | 课程安排 | id, course_id, teacher_id, classroom, time_slot |
| `payments` | 缴费记录 | id, student_id, fee_item_id, amount, status |

### 系统表

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `roles` | 角色定义 | id, name, description, permissions |
| `permissions` | 权限定义 | id, name, resource, action |
| `system_settings` | 系统配置 | id, setting_key, setting_value, description |
| `activity_logs` | 操作日志 | id, user_id, action, resource, timestamp |

---

## 🔧 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: campus-management
  
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
  
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: 0
    jedis:
      pool:
        max-active: 10
        max-wait: -1ms
        max-idle: 8
        min-idle: 0

jwt:
  secret: ${JWT_SECRET:campus_management_secret_key}
  expiration: ${JWT_EXPIRATION:86400}

logging:
  level:
    com.campus: INFO
    org.springframework.security: DEBUG
```

### 前端配置 (vite.config.js)

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    rollupOptions: {
      output: {
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: '[ext]/[name]-[hash].[ext]'
      }
    }
  }
})
```

---

## 📝 API文档

### 认证相关

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/auth/login` | 用户登录 |
| POST | `/api/v1/auth/logout` | 用户登出 |
| GET | `/api/v1/auth/profile` | 获取用户信息 |
| PUT | `/api/v1/auth/profile` | 更新用户信息 |

### 学生相关

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/students` | 获取学生列表 |
| POST | `/api/v1/students` | 创建学生 |
| GET | `/api/v1/students/{id}` | 获取学生详情 |
| PUT | `/api/v1/students/{id}` | 更新学生信息 |
| DELETE | `/api/v1/students/{id}` | 删除学生 |

### 课程相关

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/courses` | 获取课程列表 |
| POST | `/api/v1/courses` | 创建课程 |
| GET | `/api/v1/courses/{id}` | 获取课程详情 |
| GET | `/api/v1/courses/{id}/students` | 获取课程学生列表 |
| POST | `/api/v1/courses/{id}/select` | 学生选课 |

### 成绩相关

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/grades` | 获取成绩列表 |
| POST | `/api/v1/grades` | 录入成绩 |
| PUT | `/api/v1/grades/{id}` | 更新成绩 |
| GET | `/api/v1/grades/statistics` | 成绩统计 |

> 📖 **完整API文档**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔍 系统监控

### 性能监控

- **响应时间**: 实时监控API接口响应时间
- **内存使用**: JVM内存使用情况监控
- **数据库性能**: 慢查询监控与优化建议
- **缓存命中率**: Redis缓存效率统计

### 业务监控

- **用户活跃度**: 登录统计、操作频率分析
- **功能使用**: 各模块使用情况统计
- **错误追踪**: 异常日志记录与分析
- **系统负载**: 服务器资源使用监控

### 监控面板

访问 `http://localhost:8080/actuator` 查看系统健康状态

---

## 🧪 测试

### 后端测试

```bash
cd campus-management-backend

# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=UserServiceTest

# 生成测试报告
./mvnw test jacoco:report
```

### 前端测试

```bash
cd campus-management-frontend

# 运行单元测试
npm run test

# 运行端到端测试
npm run test:e2e

# 生成测试覆盖率报告
npm run test:coverage
```

### 测试覆盖率

- **后端测试覆盖率**: > 80%
- **前端测试覆盖率**: > 75%

---

## 📦 部署

### Docker部署 (推荐)

1. **构建镜像**

```bash
# 构建所有服务
docker-compose build

# 构建指定服务
docker-compose build backend
docker-compose build frontend
```

2. **启动服务**

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

3. **环境变量配置** (创建 `.env` 文件)

```env
# 数据库配置
DB_HOST=mysql
DB_PORT=3306
DB_NAME=campus_management
DB_USERNAME=campus_user
DB_PASSWORD=campus_password

# Redis配置
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=redis_password

# JWT配置
JWT_SECRET=your_super_secret_jwt_key
JWT_EXPIRATION=86400

# 应用配置
BACKEND_PORT=8080
FRONTEND_PORT=80
```

### 传统部署

#### 后端部署

```bash
cd campus-management-backend

# 构建生产包
./mvnw clean package -Pprod

# 启动应用
java -jar target/campus-management-backend-1.0.0.jar
```

#### 前端部署

```bash
cd campus-management-frontend

# 构建生产版本
npm run build

# 部署到Nginx
sudo cp -r dist/* /var/www/html/
```

#### Nginx配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/html;
    index index.html;
    
    # 前端路由
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 🤝 贡献指南

我们欢迎所有形式的贡献！请遵循以下步骤：

### 开发流程

1. **Fork 项目** 到你的GitHub账户
2. **创建特性分支** (`git checkout -b feature/AmazingFeature`)
3. **提交更改** (`git commit -m 'Add some AmazingFeature'`)
4. **推送到分支** (`git push origin feature/AmazingFeature`)
5. **开启 Pull Request**

### 代码规范

- **Java代码**: 遵循Google Java Style Guide
- **Vue.js代码**: 遵循Vue官方风格指南
- **提交信息**: 使用约定式提交规范

### 开发环境设置

```bash
# 安装开发工具
npm install -g @vue/cli
npm install -g eslint
npm install -g prettier

# 配置Git钩子
npm install husky --save-dev
npx husky install
```

---

## 📄 许可证

本项目基于 [MIT 许可证](LICENSE) 开源 - 查看 LICENSE 文件了解详情

```
MIT License

Copyright (c) 2024 Smart Campus Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 📞 联系我们

- **项目地址**: [GitHub Repository](https://github.com/your-repo/Smart-Campus-Management)
- **问题反馈**: [GitHub Issues](https://github.com/your-repo/Smart-Campus-Management/issues)
- **讨论交流**: [GitHub Discussions](https://github.com/your-repo/Smart-Campus-Management/discussions)
- **邮箱联系**: [campus-management@example.com](mailto:campus-management@example.com)
- **技术支持**: 工作日 9:00-18:00

### 技术交流群

- **QQ群**: 123456789
- **微信群**: 扫描二维码加入
- **钉钉群**: 搜索群号 XXXX

---

## 🎯 路线图

### v1.0 (当前版本) ✅

- ✅ 基础功能完成
- ✅ 用户角色管理
- ✅ 课程管理系统
- ✅ 成绩管理系统
- ✅ 基础权限控制
- ✅ 简单报表功能

### v1.1 (开发中) 🚧

- 🔄 移动端UI优化
- 🔄 消息推送增强
- 🔄 报表系统完善
- 🔄 性能优化
- 🔄 国际化支持
- 🔄 离线模式支持

### v1.2 (计划中) 📋

- 📅 文件管理系统
- 📅 在线考试功能
- 📅 视频会议集成
- 📅 智能排课算法
- 📅 家校沟通增强
- 📅 数据导入导出

### v2.0 (未来版本) 🚀

- 📅 AI智能推荐
- 📅 大数据分析平台
- 📅 微服务架构重构
- 📅 多租户支持
- 📅 区块链学历认证
- 📅 IoT设备集成

---

## 🏆 致谢

感谢所有为这个项目做出贡献的开发者们！

### 核心贡献者

- [@contributor1](https://github.com/contributor1) - 项目创始人
- [@contributor2](https://github.com/contributor2) - 后端架构师
- [@contributor3](https://github.com/contributor3) - 前端开发者

### 特别感谢

- Spring Boot 社区
- Vue.js 社区
- Element Plus 团队
- 所有测试用户和反馈者

---


### ⭐ 如果这个项目对你有帮助，请给我们一个Star！

**让我们一起打造更好的智慧校园管理系统！**

[⬆ 回到顶部](#smart-campus-management-system)


