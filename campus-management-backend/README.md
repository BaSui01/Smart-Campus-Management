# 智慧校园管理系统 - 后端服务

> 🚀 基于Spring Boot 3.x + JPA + MySQL + Redis构建的现代化校园管理系统

## 📋 项目概述

智慧校园管理系统是一个功能完善的校园信息化管理平台，支持学生管理、教师管理、课程管理、选课管理、成绩管理、缴费管理等核心业务功能。

### ✨ 主要特性

- 🏗️ **现代化架构**: 基于Spring Boot 3.x + JPA + MySQL + Redis
- 🔐 **安全认证**: JWT + Spring Security多层安全防护
- 📊 **数据缓存**: Redis缓存提升系统性能
- 🎯 **RESTful API**: 标准化API设计，支持前后端分离
- 📝 **接口文档**: 集成Swagger/OpenAPI自动生成文档
- 🔍 **数据验证**: 完善的参数校验和异常处理
- 📦 **容器化**: Docker支持，一键部署
- 🎨 **响应式**: 支持移动端和桌面端访问

## 🛠️ 技术栈

### 后端核心技术
- **框架**: Spring Boot 3.2.0
- **数据访问**: Spring Data JPA + Hibernate
- **数据库**: MySQL 8.0+
- **缓存**: Redis 7.0+
- **安全**: Spring Security + JWT
- **文档**: SpringDoc OpenAPI 3
- **工具**: Lombok + Hutool
- **构建**: Maven 3.8+

### 开发环境要求
- **JDK**: 17+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **IDE**: IntelliJ IDEA / Eclipse

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/your-org/campus-management-backend.git
cd campus-management-backend
```

### 2. 配置数据库
创建MySQL数据库：
```sql
CREATE DATABASE campus_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行数据库脚本：
```bash
# 1. 创建表结构
mysql -u root -p campus_management_db < database/01_create_tables_updated.sql

# 2. 插入初始数据
mysql -u root -p campus_management_db < database/02_insert_initial_data.sql
```

### 3. 配置应用
修改 `src/main/resources/application.yml` 中的数据库和Redis配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management_db?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true
    username: root
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### 4. 启动应用
```bash
# 使用Maven启动
mvn spring-boot:run

# 或者打包后启动
mvn clean package
java -jar target/campus-management-backend-1.0.0.jar
```

### 5. 访问应用
- **应用首页**: http://localhost:8080
- **API文档**: http://localhost:8080/api/swagger-ui.html
- **管理后台**: http://localhost:8080/admin

### 6. 默认账户
| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | admin123 | 系统管理员，拥有所有权限 |
| 教师 | teacher001 | teacher123 | 教师账户，可管理课程和成绩 |
| 学生 | student001 | student123 | 学生账户，可选课和查看成绩 |
| 教务员 | academic001 | staff123 | 教务管理员 |
| 财务员 | finance001 | finance123 | 财务管理员 |

## 📁 项目结构

```
campus-management-backend/
├── src/main/java/com/campus/
│   ├── CampusManagementApplication.java     # 启动类
│   ├── application/                         # 应用层
│   │   ├── controller/                      # 控制器
│   │   ├── service/                         # 服务接口
│   │   └── service/impl/                    # 服务实现
│   ├── domain/                              # 领域层
│   │   ├── entity/                          # 实体类
│   │   └── repository/                      # 数据访问接口
│   ├── shared/                              # 共享层
│   │   ├── config/                          # 配置类
│   │   ├── exception/                       # 异常处理
│   │   ├── security/                        # 安全配置
│   │   └── util/                            # 工具类
│   └── web/                                 # Web层
│       ├── controller/                      # Web控制器
│       └── dto/                             # 数据传输对象
├── src/main/resources/
│   ├── application.yml                      # 主配置文件
│   ├── static/                              # 静态资源
│   └── templates/                           # 模板文件
├── database/                                # 数据库脚本
│   ├── 01_create_tables_updated.sql         # 建表脚本
│   └── 02_insert_initial_data.sql           # 初始数据
├── docs/                                    # 文档目录
├── docker/                                  # Docker配置
├── pom.xml                                  # Maven配置
└── README.md                                # 项目说明
```

## 📊 数据库设计

### 核心实体关系
```
用户 (tb_user) 1:N 学生 (tb_student)
用户 (tb_user) 1:N 教师课程 (tb_course)
院系 (tb_department) 1:N 班级 (tb_class)
班级 (tb_class) 1:N 学生 (tb_student)
课程 (tb_course) 1:N 课程表 (tb_course_schedule)
学生 (tb_student) N:M 课程 (tb_course) -> 选课表 (tb_course_selection)
学生 (tb_student) 1:N 成绩 (tb_grade)
学生 (tb_student) 1:N 缴费记录 (tb_payment_record)
```

### 主要数据表
- **tb_user**: 用户基础信息表
- **tb_department**: 院系信息表
- **tb_class**: 班级信息表
- **tb_student**: 学生信息表
- **tb_course**: 课程信息表
- **tb_course_schedule**: 课程安排表
- **tb_course_selection**: 选课记录表
- **tb_grade**: 成绩记录表
- **tb_payment_record**: 缴费记录表
- **tb_role**: 角色表
- **tb_permission**: 权限表

## 🔌 API接口

### 认证相关
```http
POST /api/auth/login          # 用户登录
POST /api/auth/logout         # 用户登出
POST /api/auth/refresh        # 刷新令牌
GET  /api/auth/profile        # 获取用户信息
```

### 用户管理
```http
GET    /api/users             # 获取用户列表
POST   /api/users             # 创建用户
GET    /api/users/{id}        # 获取用户详情
PUT    /api/users/{id}        # 更新用户信息
DELETE /api/users/{id}        # 删除用户
```

### 院系管理
```http
GET    /api/departments       # 获取院系列表
POST   /api/departments       # 创建院系
GET    /api/departments/{id}  # 获取院系详情
PUT    /api/departments/{id}  # 更新院系信息
DELETE /api/departments/{id}  # 删除院系
GET    /api/departments/tree  # 获取院系树结构
```

### 班级管理
```http
GET    /api/classes           # 获取班级列表
POST   /api/classes           # 创建班级
GET    /api/classes/{id}      # 获取班级详情
PUT    /api/classes/{id}      # 更新班级信息
DELETE /api/classes/{id}      # 删除班级
```

### 学生管理
```http
GET    /api/students          # 获取学生列表
POST   /api/students          # 创建学生
GET    /api/students/{id}     # 获取学生详情
PUT    /api/students/{id}     # 更新学生信息
DELETE /api/students/{id}     # 删除学生
```

### 课程管理
```http
GET    /api/courses           # 获取课程列表
POST   /api/courses           # 创建课程
GET    /api/courses/{id}      # 获取课程详情
PUT    /api/courses/{id}      # 更新课程信息
DELETE /api/courses/{id}      # 删除课程
```

### 选课管理
```http
GET    /api/course-selections        # 获取选课列表
POST   /api/course-selections        # 学生选课
DELETE /api/course-selections/{id}   # 学生退课
GET    /api/course-selections/my     # 获取我的选课
```

### 成绩管理
```http
GET    /api/grades            # 获取成绩列表
POST   /api/grades            # 录入成绩
GET    /api/grades/{id}       # 获取成绩详情
PUT    /api/grades/{id}       # 更新成绩
DELETE /api/grades/{id}       # 删除成绩
GET    /api/grades/transcript # 获取成绩单
```

### 缴费管理
```http
GET    /api/payments          # 获取缴费列表
POST   /api/payments          # 记录缴费
GET    /api/payments/{id}     # 获取缴费详情
PUT    /api/payments/{id}     # 更新缴费记录
GET    /api/fee-items         # 获取缴费项目
```

## 🔒 安全机制

### 认证授权
- **JWT Token**: 无状态认证，支持令牌刷新
- **角色权限**: 基于RBAC的细粒度权限控制
- **接口保护**: 所有API均需认证，敏感操作需授权

### 数据安全
- **参数验证**: 完善的输入参数校验
- **SQL注入防护**: 使用JPA预编译语句
- **XSS防护**: 输出编码和CSP策略
- **密码加密**: BCrypt强加密算法

### 系统安全
- **访问控制**: IP白名单和访问频率限制
- **审计日志**: 完整的操作日志记录
- **异常处理**: 统一异常处理，避免信息泄露

## 📈 性能优化

### 缓存策略
- **Redis缓存**: 用户信息、权限数据、常用查询结果
- **查询优化**: 合理使用索引，避免N+1查询
- **连接池**: HikariCP高性能连接池

### 系统监控
- **应用监控**: Spring Boot Actuator健康检查
- **性能指标**: 接口响应时间、吞吐量统计
- **日志管理**: 结构化日志，支持ELK Stack

## 🐳 容器化部署

### Docker部署
```bash
# 构建镜像
docker build -t campus-management-backend .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e MYSQL_HOST=mysql \
  -e REDIS_HOST=redis \
  campus-management-backend
```

### Docker Compose
```bash
# 一键启动所有服务
docker-compose up -d
```

## 🧪 测试

### 单元测试
```bash
# 运行单元测试
mvn test

# 生成测试报告
mvn test jacoco:report
```

### 集成测试
```bash
# 运行集成测试
mvn verify -P integration-test
```

### API测试
推荐使用Postman或Insomnia导入API文档进行测试。

## 📚 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok减少样板代码
- 统一异常处理和响应格式
- 完善的代码注释和文档

### 扩展开发
1. **添加新模块**: 按照DDD分层架构添加新的业务模块
2. **数据库变更**: 使用Flyway进行数据库版本管理
3. **接口开发**: 遵循RESTful设计原则
4. **测试编写**: 为新功能编写单元测试和集成测试

### 最佳实践
- **事务管理**: 合理使用@Transactional注解
- **异常处理**: 使用自定义业务异常
- **日志记录**: 记录关键业务操作和异常信息
- **性能监控**: 关注慢查询和高频接口

## 🔧 故障排查

### 常见问题
1. **数据库连接失败**: 检查数据库配置和网络连接
2. **Redis连接异常**: 确认Redis服务状态和配置
3. **JWT令牌失效**: 检查令牌配置和时钟同步
4. **权限访问拒绝**: 确认用户角色和权限配置

### 日志查看
```bash
# 查看应用日志
tail -f logs/campus-management.log

# 查看错误日志
grep "ERROR" logs/campus-management.log
```

## 🤝 贡献指南

### 提交代码
1. Fork项目到个人仓库
2. 创建功能分支: `git checkout -b feature/new-feature`
3. 提交变更: `git commit -am 'Add new feature'`
4. 推送分支: `git push origin feature/new-feature`
5. 创建Pull Request

### 代码审查
- 确保代码符合项目规范
- 添加必要的测试用例
- 更新相关文档
- 通过CI/CD检查

## 📄 许可证

本项目采用MIT许可证，详情请查看 [LICENSE](LICENSE) 文件。

## 📞 联系我们

- **项目主页**: https://github.com/your-org/campus-management-backend
- **问题反馈**: https://github.com/your-org/campus-management-backend/issues
- **技术支持**: support@campus.com
- **开发团队**: Campus Management Team

---

## 🎯 版本历史

### v2.0.0 (2025-06-06) - 重大更新
- ✨ 全新的实体设计和数据库架构
- 🚀 基于Spring Boot 3.x重构
- 🔐 增强的安全认证机制
- 📊 完善的院系管理功能
- 🎯 优化的API设计
- 📝 完整的接口文档

### v1.0.0 (2025-06-03) - 初始版本
- 🎉 项目初始发布
- 📚 基础功能实现
- 🛠️ 核心模块开发

---

**⭐ 如果这个项目对你有帮助，请给我们一个Star！**