# 智慧校园管理系统 (Smart Campus Management System)

[![CI/CD](https://github.com/BaSui01/Smart-Campus-Management/actions/workflows/ci.yml/badge.svg)](https://github.com/BaSui01/Smart-Campus-Management/actions/workflows/ci.yml)
[![Deploy](https://github.com/BaSui01/Smart-Campus-Management/actions/workflows/deploy.yml/badge.svg)](https://github.com/BaSui01/Smart-Campus-Management/actions/workflows/deploy.yml)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📖 项目简介

智慧校园管理系统是一个现代化的校园综合管理平台，采用前后端分离架构，为学校提供完整的数字化管理解决方案。

### 🎯 核心功能

- **👥 用户管理** - 学生、教师、家长、管理员多角色管理
- **🏫 学术管理** - 课程管理、班级管理、成绩管理
- **📚 教学管理** - 课程安排、考勤管理、作业管理
- **💰 财务管理** - 学费管理、缴费记录、财务统计
- **📊 数据分析** - 多维度报表、数据可视化
- **🔐 权限控制** - 基于RBAC的细粒度权限管理
- **📱 多端支持** - Web端、移动端适配

## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0+
- **缓存**: Redis 6.0+
- **安全**: Spring Security + JWT
- **数据访问**: JPA + Hibernate
- **任务调度**: Quartz
- **文档**: Swagger 3.0

### 前端技术栈
- **框架**: Vue 3.x + TypeScript
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **构建工具**: Vite
- **HTTP客户端**: Axios

### 开发工具
- **版本控制**: Git + GitHub
- **CI/CD**: GitHub Actions
- **容器化**: Docker + Docker Compose
- **代码质量**: ESLint + Prettier + SonarQube

## 🚀 快速开始

### 环境要求

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/BaSui01/Smart-Campus-Management.git
cd Smart-Campus-Management
```

2. **后端启动**
```bash
cd campus-management-backend
# 配置数据库连接信息
cp src/main/resources/application-example.yml src/main/resources/application.yml
# 启动后端服务
mvn spring-boot:run
```

3. **前端启动**
```bash
cd campus-management-frontend
# 安装依赖
npm install
# 启动开发服务器
npm run dev
```

4. **访问应用**
- 前端地址: http://localhost:3000
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

## 📁 项目结构

```
Smart-Campus-Management/
├── campus-management-backend/     # 后端项目
│   ├── src/main/java/
│   │   └── com/campus/
│   │       ├── application/       # 应用层
│   │       ├── domain/           # 领域层
│   │       ├── infrastructure/   # 基础设施层
│   │       └── interfaces/       # 接口层
│   └── src/main/resources/       # 配置文件
├── campus-management-frontend/    # 前端项目
│   ├── src/
│   │   ├── components/          # 组件
│   │   ├── views/              # 页面
│   │   ├── router/             # 路由
│   │   ├── store/              # 状态管理
│   │   └── utils/              # 工具类
├── docs/                        # 项目文档
├── scripts/                     # 脚本文件
└── docker-compose.yml          # Docker编排文件
```

## 🔧 配置说明

### 数据库配置

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_campus?useUnicode=true&characterEncoding=utf8&useSSL=false
    username: root
    password: xiaoxiao123
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Redis配置

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
```

## 🧪 测试

### 后端测试
```bash
cd campus-management-backend
mvn test
```

### 前端测试
```bash
cd campus-management-frontend
npm run test
```

## 📦 部署

### Docker部署

```bash
# 构建镜像
docker-compose build

# 启动服务
docker-compose up -d
```

### 生产环境部署

1. 构建前端项目
```bash
cd campus-management-frontend
npm run build
```

2. 打包后端项目
```bash
cd campus-management-backend
mvn clean package -DskipTests
```

3. 部署到服务器
```bash
# 上传jar包和前端dist目录到服务器
# 配置nginx反向代理
# 启动应用
java -jar campus-management-backend.jar
```

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 团队

- **项目负责人**: Campus Management Team
- **技术架构**: Backend Team
- **前端开发**: Frontend Team
- **测试团队**: QA Team

## 📞 联系我们

- 项目地址: https://github.com/BaSui01/Smart-Campus-Management
- 问题反馈: [Issues](https://github.com/BaSui01/Smart-Campus-Management/issues)
- 功能建议: [Discussions](https://github.com/BaSui01/Smart-Campus-Management/discussions)

## 🙏 致谢

感谢所有为本项目做出贡献的开发者和用户！

---

⭐ 如果这个项目对您有帮助，请给我们一个星标！
