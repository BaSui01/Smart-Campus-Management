# 🧪 智慧校园管理系统 API 测试指南

## 📋 概述

本文档提供了智慧校园管理系统后端API接口的完整测试方案，包括自动化测试、手动测试和性能测试。

## 🏗️ 测试架构

### 测试层次
```
测试金字塔
├── E2E测试 (端到端测试)
├── 集成测试 (API接口测试)
├── 单元测试 (Service/Repository测试)
└── 静态分析 (代码质量检查)
```

### 测试工具栈
- **Spring Boot Test** - 集成测试框架
- **MockMvc** - API接口测试
- **JUnit 5** - 单元测试框架
- **TestContainers** - 数据库集成测试
- **Postman/Newman** - API自动化测试
- **JaCoCo** - 代码覆盖率分析

## 🎯 测试范围

### API控制器覆盖 (26个)

#### 核心业务API
- ✅ **UserApiController** - 用户管理API
- ✅ **StudentApiController** - 学生管理API  
- ✅ **CourseApiController** - 课程管理API
- ✅ **ClassApiController** - 班级管理API
- ✅ **DepartmentApiController** - 院系管理API

#### 学术管理API
- ✅ **AssignmentApiController** - 作业管理API
- ✅ **AttendanceApiController** - 考勤管理API
- ✅ **ExamApiController** - 考试管理API
- ✅ **GradeApiController** - 成绩管理API
- ✅ **CourseSelectionApiController** - 选课管理API

#### 权限管理API
- ✅ **AuthApiController** - 认证API
- ✅ **RoleApiController** - 角色管理API
- ✅ **PermissionApiController** - 权限管理API

#### 系统管理API
- ✅ **SystemApiController** - 系统管理API
- ✅ **DashboardApiController** - 仪表盘API
- ✅ **NotificationApiController** - 通知管理API
- ✅ **MessageApiController** - 消息管理API

#### 特殊功能API
- ✅ **AutoScheduleApiController** - 自动排课API
- ✅ **ScheduleApiController** - 课表管理API
- ✅ **CacheManagementApiController** - 缓存管理API
- ✅ **PaymentApiController** - 支付管理API
- ✅ **FeeItemApiController** - 费用项目API

#### 优化版API
- ✅ **OptimizedUserApiController** - 优化用户API
- ✅ **OptimizedStudentApiController** - 优化学生API

#### 其他API
- ✅ **ClassroomApiController** - 教室管理API
- ✅ **CourseScheduleApiController** - 课程安排API
- ✅ **CourseSelectionPeriodApiController** - 选课时段API

## 🚀 快速开始

### 1. 环境准备

```bash
# 确保Java 17+和Maven已安装
java -version
mvn -version

# 进入项目目录
cd campus-management-backend
```

### 2. 运行所有API测试

#### Windows
```cmd
scripts\run-api-tests.bat
```

#### Linux/macOS
```bash
chmod +x scripts/run-api-tests.sh
./scripts/run-api-tests.sh
```

### 3. 运行特定测试

```bash
# 运行用户API测试
mvn test -Dtest=UserApiControllerTest

# 运行认证API测试
mvn test -Dtest=AuthApiControllerTest

# 运行所有API测试
mvn test -Dtest="com.campus.interfaces.rest.v1.*Test"
```

## 📊 测试类型详解

### 1. 单元测试
```bash
# 运行所有单元测试
mvn test

# 运行特定Service测试
mvn test -Dtest=UserServiceTest
```

### 2. 集成测试
```bash
# 运行API集成测试
mvn test -Dtest="*ApiControllerTest"

# 使用测试配置文件
mvn test -Dspring.profiles.active=test
```

### 3. Postman测试
```bash
# 安装Newman (Postman CLI)
npm install -g newman

# 运行Postman测试集合
newman run src/test/resources/postman/Smart-Campus-API-Tests.postman_collection.json \
  --environment src/test/resources/postman/test-environment.json
```

## 🔧 测试配置

### 测试数据库配置
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

### JWT测试配置
```yaml
jwt:
  secret: test-jwt-secret-key-for-testing-only
  expiration: 3600000  # 1小时
```

## 📈 测试报告

### 生成测试报告
```bash
# 生成Surefire测试报告
mvn surefire-report:report

# 生成JaCoCo覆盖率报告
mvn jacoco:report

# 查看报告
open target/site/surefire-report.html
open target/site/jacoco/index.html
```

### 报告内容
- **测试结果统计** - 通过/失败/跳过的测试数量
- **代码覆盖率** - 行覆盖率、分支覆盖率
- **性能指标** - 响应时间、吞吐量
- **错误详情** - 失败测试的详细信息

## 🎯 测试最佳实践

### 1. 测试命名规范
```java
@Test
@DisplayName("获取用户列表 - 成功")
void testGetUsers_Success() {
    // 测试实现
}

@Test
@DisplayName("创建用户 - 无权限")
void testCreateUser_Unauthorized() {
    // 测试实现
}
```

### 2. 测试数据管理
```java
@BeforeEach
void setUp() {
    // 准备测试数据
    testUser = createTestUser("testuser", "test@example.com");
}

@AfterEach
void tearDown() {
    // 清理测试数据
    userRepository.deleteAll();
}
```

### 3. 断言最佳实践
```java
// 使用具体的断言
mockMvc.perform(get("/api/v1/users"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.success").value(true))
    .andExpect(jsonPath("$.data.total").exists())
    .andExpect(jsonPath("$.data.records").isArray());
```

## 🔍 调试测试

### 1. 启用调试日志
```yaml
logging:
  level:
    com.campus: DEBUG
    org.springframework.web: DEBUG
```

### 2. 使用@Sql注解
```java
@Test
@Sql("/test-data/users.sql")
void testWithSpecificData() {
    // 测试实现
}
```

### 3. 模拟外部依赖
```java
@MockBean
private ExternalService externalService;

@Test
void testWithMockedService() {
    when(externalService.getData()).thenReturn("mocked data");
    // 测试实现
}
```

## 📋 测试检查清单

### API测试检查项
- [ ] 所有HTTP方法 (GET, POST, PUT, DELETE)
- [ ] 请求参数验证
- [ ] 响应格式检查
- [ ] 错误处理测试
- [ ] 权限控制测试
- [ ] 分页功能测试
- [ ] 搜索功能测试
- [ ] 数据验证测试

### 安全测试检查项
- [ ] 认证测试
- [ ] 授权测试
- [ ] JWT令牌验证
- [ ] 跨域请求测试
- [ ] SQL注入防护
- [ ] XSS防护测试

### 性能测试检查项
- [ ] 响应时间测试
- [ ] 并发访问测试
- [ ] 大数据量测试
- [ ] 内存使用测试
- [ ] 数据库连接池测试

## 🚨 常见问题

### 1. 测试数据库连接失败
```bash
# 检查H2数据库配置
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
```

### 2. JWT令牌生成失败
```java
// 使用模拟令牌
adminToken = "Bearer mock-admin-token";
```

### 3. 权限测试失败
```java
// 确保测试用户有正确的角色
@WithMockUser(roles = "ADMIN")
```

## 📞 技术支持

如果在测试过程中遇到问题，请：

1. 查看测试日志：`target/surefire-reports/`
2. 检查测试配置：`application-test.yml`
3. 参考示例测试：`UserApiControllerTest.java`
4. 联系开发团队获取支持

---

**最后更新**: 2025-06-08  
**文档版本**: 1.0.0  
**维护团队**: Campus Management Team
