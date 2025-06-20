# 🎓 智慧校园管理系统后端

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0-red.svg)](https://redis.io/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

基于Spring Boot 3.2的现代化校园管理系统后端服务，提供完整的校园信息化管理解决方案。

## ✨ 功能特性

### 🔐 认证授权
- JWT Token认证
- 基于角色的权限控制(RBAC)
- 多端登录支持
- 密码安全策略

### 👥 用户管理
- 学生信息管理
- 教师信息管理
- 管理员权限管理
- 用户状态控制

### 📚 教务管理
- 课程信息管理
- 课程表安排
- 选课系统
- 自动排课算法

### 📝 考试管理
- 考试安排
- 成绩录入
- 成绩查询
- 统计分析

### 💰 缴费管理
- 学费管理
- 缴费记录
- 财务统计
- 欠费提醒

### 📢 通知公告
- 系统通知
- 消息推送
- 公告管理
- 邮件提醒

### 📊 数据统计
- 学生统计
- 课程统计
- 成绩分析
- 财务报表

### 🔧 系统管理
- 系统配置
- 日志管理
- 数据备份
- 监控告警

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.2.x
- **安全**: Spring Security 6.x + JWT
- **数据库**: MySQL 8.0 + MyBatis Plus
- **缓存**: Redis 6.0 + Spring Cache
- **文档**: SpringDoc OpenAPI 3 (Swagger)
- **监控**: Micrometer + Prometheus
- **构建**: Maven 3.8+
- **Java**: JDK 21

### 开发工具
- **IDE**: IntelliJ IDEA 2023+
- **版本控制**: Git
- **API测试**: Postman
- **数据库工具**: MySQL Workbench
- **Redis工具**: RedisInsight

## 🚀 快速开始

### 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| Java | JDK 21+ | 推荐使用 OpenJDK 21 |
| Maven | 3.8+ | 构建工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存和会话存储 |

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-org/campus-management.git
cd campus-management/campus-management-backend
```

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE campus_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'campus_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON campus_management_db.* TO 'campus_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **导入数据**
```bash
# 导入数据库结构
mysql -u root -p campus_management_db < src/main/resources/db/migration/01_create_tables.sql

# 导入基础数据
mysql -u root -p campus_management_db < src/main/resources/db/migration/02_insert_basic_data.sql

# 导入测试数据（可选）
mysql -u root -p campus_management_db < src/main/resources/db/migration/03_insert_large_data.sql
```

4. **修改配置文件**
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root  # 或 campus_user
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果Redis设置了密码
```

5. **启动项目**
```bash
# 使用Maven启动
mvn spring-boot:run

# 或者编译后启动
mvn clean package
java -jar target/campus-management-backend-1.0.0.jar
```

6. **验证启动**
- 应用地址: http://localhost:8889
- API文档: http://localhost:8889/swagger-ui/index.html
- 健康检查: http://localhost:8889/actuator/health

## 📖 文档

### 📚 完整文档
- [📋 API使用示例](docs/API_USAGE_EXAMPLES.md) - 详细的API调用示例
- [🚀 部署指南](docs/DEPLOYMENT_GUIDE.md) - 生产环境部署指南
- [👨‍💻 开发者指南](docs/DEVELOPER_GUIDE.md) - 开发规范和最佳实践

### 🔗 在线文档
- **Swagger API文档**: http://localhost:8889/swagger-ui/index.html
- **应用监控**: http://localhost:8889/actuator
- **健康检查**: http://localhost:8889/actuator/health

## 🏗️ 项目结构

```
campus-management-backend/
├── src/main/java/com/campus/
│   ├── interfaces/              # 🌐 接口层 (REST API)
│   │   └── rest/v1/            # REST API v1版本
│   │       ├── auth/           # 认证相关API
│   │       ├── user/           # 用户管理API
│   │       ├── student/        # 学生管理API
│   │       ├── course/         # 课程管理API
│   │       └── ...
│   ├── application/            # 🔧 应用层 (业务逻辑)
│   │   ├── service/           # 业务服务接口
│   │   └── Implement/         # 业务服务实现
│   ├── domain/                # 🏛️ 领域层 (核心业务)
│   │   ├── entity/           # 实体类
│   │   ├── repository/       # 仓储接口
│   │   └── dto/              # 数据传输对象
│   ├── infrastructure/        # 🔨 基础设施层
│   │   ├── config/           # 配置类
│   │   ├── interceptor/      # 拦截器
│   │   └── repository/       # 仓储实现
│   └── shared/               # 🔄 共享组件
│       ├── security/         # 安全组件
│       ├── util/             # 工具类
│       └── exception/        # 异常处理
├── src/main/resources/
│   ├── db/migration/         # 📊 数据库迁移脚本
│   ├── static/               # 📁 静态资源
│   ├── application.yml       # ⚙️ 主配置文件
│   └── logback-spring.xml    # 📝 日志配置
├── docs/                     # 📖 项目文档
├── scripts/                  # 🔧 脚本文件
└── pom.xml                   # 📦 Maven配置
```

## 🔌 API接口

### 认证相关
- `POST /api/v1/auth/login` - 用户登录
- `POST /api/v1/auth/register` - 用户注册
- `POST /api/v1/auth/logout` - 用户登出
- `POST /api/v1/auth/refresh` - 刷新Token

### 用户管理
- `GET /api/v1/users` - 获取用户列表
- `POST /api/v1/users` - 创建用户
- `PUT /api/v1/users/{id}` - 更新用户
- `DELETE /api/v1/users/{id}` - 删除用户

### 学生管理
- `GET /api/v1/students` - 获取学生列表
- `POST /api/v1/students` - 创建学生
- `GET /api/v1/students/{id}` - 获取学生详情
- `PUT /api/v1/students/{id}` - 更新学生信息

### 课程管理
- `GET /api/v1/courses` - 获取课程列表
- `POST /api/v1/courses` - 创建课程
- `GET /api/v1/courses/{id}` - 获取课程详情
- `POST /api/v1/course-selections` - 学生选课

更多API详情请查看 [API使用示例](docs/API_USAGE_EXAMPLES.md)

## 🧪 测试

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest="*Test"

# 运行集成测试
mvn test -Dtest="*IntegrationTest"

# 生成测试报告
mvn test jacoco:report
```

### 测试覆盖率
- 目标覆盖率: 80%+
- 报告位置: `target/site/jacoco/index.html`

## 📊 监控

### 应用监控
- **健康检查**: `/actuator/health`
- **应用信息**: `/actuator/info`
- **监控指标**: `/actuator/metrics`
- **Prometheus指标**: `/actuator/prometheus`

### 自定义指标
- `campus.user.login.count` - 用户登录次数
- `campus.api.request.count` - API请求次数
- `campus.api.response.time` - API响应时间
- `campus.user.active.count` - 活跃用户数

## 🔧 配置

### 环境配置
- **开发环境**: `application-dev.yml`
- **测试环境**: `application-test.yml`
- **生产环境**: `application-prod.yml`

### 关键配置项
```yaml
# JWT配置
campus:
  jwt:
    secret: your-jwt-secret-key
    expiration: 7200000  # 2小时
    refresh-expiration: 604800000  # 7天

# 文件上传配置
  upload:
    path: uploads/
    max-file-size: 10MB
    allowed-types: [jpg, jpeg, png, pdf, doc, docx]

# 安全配置
  security:
    password:
      min-length: 6
      max-attempts: 5
    session:
      timeout: 1800
```

## 🚀 部署

### Docker部署
```bash
# 构建镜像
docker build -t campus-management:latest .

# 运行容器
docker run -d \
  --name campus-management \
  -p 8889:8889 \
  -e SPRING_PROFILES_ACTIVE=prod \
  campus-management:latest
```

### Docker Compose部署
```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

详细部署指南请查看 [部署文档](docs/DEPLOYMENT_GUIDE.md)

## 🤝 贡献

我们欢迎所有形式的贡献！请查看 [开发者指南](docs/DEVELOPER_GUIDE.md) 了解详细信息。

### 贡献流程
1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范
- 遵循 Java 编码规范
- 单元测试覆盖率 > 80%
- 提交信息使用约定式提交格式
- 代码审查通过后方可合并

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 支持

### 获取帮助
- 📧 邮件支持: support@campus.edu
- 💬 技术讨论: [GitHub Discussions](https://github.com/your-org/campus-management/discussions)
- 🐛 问题反馈: [GitHub Issues](https://github.com/your-org/campus-management/issues)
- 📖 文档站点: https://docs.campus.edu

### 常见问题
1. **启动失败**: 检查Java版本和数据库连接
2. **端口冲突**: 修改`server.port`配置
3. **数据库连接失败**: 检查数据库服务和配置
4. **Redis连接失败**: 检查Redis服务状态

---

<div align="center">

**⭐ 如果这个项目对你有帮助，请给我们一个星标！**

Made with ❤️ by Campus Management Team

</div>
