# 智慧校园管理系统开发者指南

## 📋 概述

本文档为智慧校园管理系统的开发者提供详细的开发指南，包括项目结构、开发规范、调试技巧和贡献指南。

---

## 🏗️ 项目架构

### 整体架构

```
智慧校园管理系统
├── 前端 (Vue.js + Element Plus)
├── 后端 (Spring Boot + MySQL + Redis)
├── 数据库 (MySQL 8.0)
├── 缓存 (Redis 6.0)
└── 监控 (Micrometer + Prometheus)
```

### 后端架构分层

```
campus-management-backend/
├── src/main/java/com/campus/
│   ├── interfaces/          # 接口层 (REST API)
│   │   └── rest/v1/         # REST API v1
│   ├── application/         # 应用层
│   │   ├── service/         # 业务服务接口
│   │   └── Implement/       # 业务服务实现
│   ├── domain/              # 领域层
│   │   ├── entity/          # 实体类
│   │   ├── repository/      # 仓储接口
│   │   └── dto/             # 数据传输对象
│   ├── infrastructure/      # 基础设施层
│   │   ├── config/          # 配置类
│   │   ├── interceptor/     # 拦截器
│   │   └── repository/      # 仓储实现
│   └── shared/              # 共享组件
│       ├── security/        # 安全组件
│       ├── util/            # 工具类
│       └── exception/       # 异常处理
└── src/main/resources/
    ├── db/migration/        # 数据库迁移脚本
    ├── static/              # 静态资源
    └── application.yml      # 配置文件
```

---

## 🛠️ 开发环境搭建

### 1. 环境要求

- **JDK**: 21+
- **Maven**: 3.8+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **IDE**: IntelliJ IDEA 2023+ (推荐)

### 2. 克隆项目

```bash
git clone https://github.com/your-org/campus-management.git
cd campus-management/campus-management-backend
```

### 3. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE campus_management_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 导入数据
mysql -u root -p campus_management_db < src/main/resources/db/migration/01_create_tables.sql
mysql -u root -p campus_management_db < src/main/resources/db/migration/02_insert_basic_data.sql
```

### 4. 配置文件

复制并修改配置文件：

```bash
cp src/main/resources/application.yml src/main/resources/application-dev.yml
```

修改 `application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_management_db?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果有密码
```

### 5. 启动项目

```bash
# 使用Maven启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或者在IDE中运行 CampusManagementApplication.main()
```

---

## 📝 开发规范

### 1. 代码规范

#### Java 命名规范

```java
// 类名：大驼峰命名
public class UserService {}

// 方法名：小驼峰命名
public void createUser() {}

// 常量：全大写，下划线分隔
public static final String DEFAULT_PASSWORD = "123456";

// 变量：小驼峰命名
private String userName;
```

#### 包命名规范

```
com.campus.interfaces.rest.v1.auth     # 认证相关API
com.campus.application.service.auth    # 认证业务服务
com.campus.domain.entity.auth          # 认证实体
com.campus.infrastructure.config       # 配置类
```

### 2. 注释规范

```java
/**
 * 用户服务接口
 * 提供用户管理的核心业务功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */
public interface UserService {
    
    /**
     * 创建新用户
     * 
     * @param userCreateRequest 用户创建请求
     * @return 创建的用户信息
     * @throws BusinessException 当用户名已存在时
     */
    UserResponse createUser(UserCreateRequest userCreateRequest);
}
```

### 3. 异常处理规范

```java
// 业务异常
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// 资源不存在异常
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// 全局异常处理
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(e.getMessage()));
    }
}
```

### 4. API 设计规范

```java
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "用户管理", description = "用户管理相关API")
public class UserApiController {
    
    @GetMapping
    @Operation(summary = "获取用户列表", description = "分页获取用户列表")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String keyword
    ) {
        // 实现逻辑
    }
    
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
        @Valid @RequestBody UserCreateRequest request
    ) {
        // 实现逻辑
    }
}
```

---

## 🔧 开发工具和技巧

### 1. IDE 配置

#### IntelliJ IDEA 推荐插件

- **Lombok**: 简化代码
- **MyBatis Log Plugin**: SQL日志美化
- **RestfulTool**: API测试
- **SonarLint**: 代码质量检查
- **GitToolBox**: Git增强

#### 代码模板

创建 Live Template (File → Settings → Editor → Live Templates):

```java
// 创建Controller模板 (缩写: ctrl)
@RestController
@RequestMapping("/api/v1/$CLASS_NAME_LOWER$")
@Tag(name = "$CLASS_NAME$管理", description = "$CLASS_NAME$管理相关API")
public class $CLASS_NAME$ApiController {
    
    @Autowired
    private $CLASS_NAME$Service $CLASS_NAME_LOWER$Service;
    
    $END$
}

// 创建Service模板 (缩写: serv)
@Service
@Transactional
public class $CLASS_NAME$ServiceImpl implements $CLASS_NAME$Service {
    
    @Autowired
    private $CLASS_NAME$Repository $CLASS_NAME_LOWER$Repository;
    
    $END$
}
```

### 2. 调试技巧

#### 日志调试

```java
// 使用SLF4J日志
private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

public UserResponse createUser(UserCreateRequest request) {
    logger.debug("创建用户请求: {}", request);
    
    try {
        // 业务逻辑
        UserResponse response = doCreateUser(request);
        logger.info("用户创建成功: userId={}, username={}", response.getId(), response.getUsername());
        return response;
    } catch (Exception e) {
        logger.error("用户创建失败: {}", e.getMessage(), e);
        throw e;
    }
}
```

#### 性能调试

```java
// 使用@Timed注解监控方法性能
@Timed(name = "user.create", description = "用户创建耗时")
public UserResponse createUser(UserCreateRequest request) {
    // 实现逻辑
}

// 手动计时
public UserResponse createUser(UserCreateRequest request) {
    long startTime = System.currentTimeMillis();
    try {
        // 业务逻辑
        return result;
    } finally {
        long duration = System.currentTimeMillis() - startTime;
        logger.debug("用户创建耗时: {}ms", duration);
    }
}
```

### 3. 测试开发

#### 单元测试

```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void testCreateUser_Success() {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        UserResponse response = userService.createUser(request);
        
        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }
}
```

#### 集成测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class UserApiControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateUser() {
        // Given
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/v1/users", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo(200);
    }
}
```

---

## 📊 监控和调试

### 1. 应用监控

访问监控端点：

```bash
# 健康检查
curl http://localhost:8889/actuator/health

# 应用信息
curl http://localhost:8889/actuator/info

# JVM信息
curl http://localhost:8889/actuator/metrics/jvm.memory.used

# 自定义业务指标
curl http://localhost:8889/actuator/metrics/campus.user.login.count
```

### 2. 数据库监控

```sql
-- 查看慢查询
SELECT * FROM mysql.slow_log ORDER BY start_time DESC LIMIT 10;

-- 查看连接数
SHOW STATUS LIKE 'Threads_connected';

-- 查看表大小
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.tables 
WHERE table_schema = 'campus_management_db'
ORDER BY (data_length + index_length) DESC;
```

### 3. Redis 监控

```bash
# 连接Redis
redis-cli

# 查看信息
INFO memory
INFO stats

# 查看慢查询
SLOWLOG GET 10

# 监控命令
MONITOR
```

---

## 🚀 部署和发布

### 1. 构建流程

```bash
# 运行测试
mvn test

# 代码质量检查
mvn sonar:sonar

# 构建生产包
mvn clean package -Pprod

# 构建Docker镜像
docker build -t campus-management:latest .
```

### 2. 环境配置

#### 开发环境 (dev)
- 数据库：本地MySQL
- 缓存：本地Redis
- 日志级别：DEBUG

#### 测试环境 (test)
- 数据库：测试MySQL
- 缓存：测试Redis
- 日志级别：INFO

#### 生产环境 (prod)
- 数据库：生产MySQL集群
- 缓存：生产Redis集群
- 日志级别：WARN

### 3. 发布检查清单

- [ ] 代码审查通过
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 性能测试通过
- [ ] 安全扫描通过
- [ ] 文档更新完成
- [ ] 配置文件检查
- [ ] 数据库迁移脚本准备
- [ ] 回滚方案准备

---

## 🤝 贡献指南

### 1. 分支策略

```
main        # 主分支，生产环境代码
develop     # 开发分支，集成最新功能
feature/*   # 功能分支
hotfix/*    # 热修复分支
release/*   # 发布分支
```

### 2. 提交规范

```bash
# 提交格式
<type>(<scope>): <subject>

# 示例
feat(user): 添加用户头像上传功能
fix(auth): 修复JWT token过期时间计算错误
docs(api): 更新用户管理API文档
style(format): 统一代码格式
refactor(service): 重构用户服务层代码
test(unit): 添加用户服务单元测试
chore(deps): 升级Spring Boot版本
```

### 3. Pull Request 流程

1. Fork 项目到个人仓库
2. 创建功能分支：`git checkout -b feature/new-feature`
3. 提交代码：`git commit -m "feat: 添加新功能"`
4. 推送分支：`git push origin feature/new-feature`
5. 创建 Pull Request
6. 代码审查
7. 合并到主分支

### 4. 代码审查标准

- 代码风格符合规范
- 单元测试覆盖率 > 80%
- 没有明显的性能问题
- 安全漏洞检查通过
- 文档更新完整

---

## 📚 学习资源

### 官方文档

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Security 官方文档](https://spring.io/projects/spring-security)
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
- [Redis 官方文档](https://redis.io/documentation)

### 推荐书籍

- 《Spring Boot实战》
- 《Java并发编程实战》
- 《高性能MySQL》
- 《Redis设计与实现》

### 在线课程

- Spring Boot 微服务开发
- MySQL 性能优化
- Redis 实战应用
- 系统架构设计

---

## 📞 技术支持

### 联系方式

- 技术讨论群：[QQ群号]
- 邮件支持：dev-support@campus.edu
- 文档站点：https://docs.campus.edu
- 问题反馈：https://github.com/your-org/campus-management/issues

### 常见问题

1. **Q: 如何添加新的业务模块？**
   A: 参考现有模块结构，按照分层架构创建对应的Entity、Repository、Service、Controller等类。

2. **Q: 如何自定义异常处理？**
   A: 在GlobalExceptionHandler中添加对应的@ExceptionHandler方法。

3. **Q: 如何添加新的监控指标？**
   A: 在MonitoringConfig中添加对应的Counter、Gauge或Timer。

4. **Q: 如何优化数据库查询性能？**
   A: 使用@Query注解编写优化的SQL，添加合适的索引，使用分页查询。

---

**Happy Coding! 🎉**
