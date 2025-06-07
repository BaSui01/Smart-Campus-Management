# Smart Campus Management - 测试指南

## 概述

本文档描述了智慧校园管理系统的测试策略和实现。我们采用了多层次的测试方法，包括单元测试、集成测试和端到端测试。

## 测试架构

### 1. 测试层次

```
┌─────────────────────────────────────┐
│           端到端测试 (E2E)            │
├─────────────────────────────────────┤
│           集成测试 (Integration)      │
├─────────────────────────────────────┤
│           单元测试 (Unit)            │
└─────────────────────────────────────┘
```

### 2. 测试类型

- **单元测试**: 测试单个类或方法的功能
- **集成测试**: 测试多个组件之间的交互
- **Repository测试**: 测试数据访问层
- **Service测试**: 测试业务逻辑层
- **Controller测试**: 测试Web层

## 测试配置

### 测试环境配置

测试使用独立的配置文件：
- `application-test.yml`: 测试环境配置
- `schema-test.sql`: 测试数据库结构
- `data-test.sql`: 测试初始数据

### 数据库配置

测试环境使用H2内存数据库：

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: 
```

## 测试实现

### 1. Repository层测试

**示例**: `UserRepositoryTest`

```java
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void testFindByUsername() {
        // 测试根据用户名查找用户
        User user = createTestUser();
        entityManager.persistAndFlush(user);
        
        Optional<User> result = userRepository.findByUsername("testuser");
        
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }
}
```

**特点**:
- 使用`@DataJpaTest`注解
- 自动配置JPA测试环境
- 使用`TestEntityManager`管理测试数据
- 测试数据库操作的正确性

### 2. Service层测试

**示例**: `SimpleUserServiceTest`

```java
@ExtendWith(MockitoExtension.class)
class SimpleUserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void testFindByUsername() {
        // 模拟Repository返回
        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        
        // 执行测试
        Optional<User> result = userService.findByUsername("testuser");
        
        // 验证结果
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }
}
```

**特点**:
- 使用Mockito进行Mock测试
- 隔离外部依赖
- 专注于业务逻辑测试
- 快速执行

### 3. 集成测试

**示例**: `UserServiceIntegrationTest`

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    void testCreateUserIntegration() {
        // 测试完整的用户创建流程
        User newUser = new User();
        newUser.setUsername("integrationtest");
        newUser.setPassword("password123");
        
        User result = userService.createUser(newUser);
        
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
    }
}
```

**特点**:
- 使用完整的Spring Context
- 测试组件间的真实交互
- 使用真实的数据库操作
- 验证端到端功能

## 测试数据管理

### 1. 测试数据初始化

使用SQL脚本初始化测试数据：

```sql
-- 插入测试用户
INSERT INTO users (username, password, real_name, email, status, deleted) VALUES
('admin', '$2a$10$...', '系统管理员', 'admin@test.com', 1, 0),
('teacher', '$2a$10$...', '测试教师', 'teacher@test.com', 1, 0),
('student', '$2a$10$...', '测试学生', 'student@test.com', 1, 0);
```

### 2. 测试数据清理

- 使用`@Transactional`注解自动回滚
- 使用`@DirtiesContext`重置Spring Context
- 手动清理特定测试数据

## 运行测试

### 1. 运行所有测试

```bash
mvn test
```

### 2. 运行特定测试类

```bash
mvn test -Dtest=SimpleUserServiceTest
```

### 3. 运行特定测试方法

```bash
mvn test -Dtest=SimpleUserServiceTest#testFindByUsername
```

### 4. 跳过测试

```bash
mvn install -DskipTests
```

## 测试覆盖率

### 配置JaCoCo

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### 生成覆盖率报告

```bash
mvn clean test jacoco:report
```

报告位置: `target/site/jacoco/index.html`

## 最佳实践

### 1. 测试命名

- 使用描述性的测试方法名
- 遵循`should_ExpectedBehavior_When_StateUnderTest`模式
- 例如: `should_ReturnUser_When_ValidUsernameProvided`

### 2. 测试结构

使用AAA模式：
- **Arrange**: 准备测试数据和环境
- **Act**: 执行被测试的操作
- **Assert**: 验证结果

### 3. 测试隔离

- 每个测试应该独立运行
- 不依赖其他测试的执行顺序
- 使用适当的清理机制

### 4. Mock使用

- 只Mock外部依赖
- 避免过度Mock
- 使用真实对象进行集成测试

## 持续集成

### GitHub Actions配置

```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Run tests
      run: mvn clean test
```

## 故障排除

### 常见问题

1. **测试数据库连接失败**
   - 检查H2依赖是否正确添加
   - 验证测试配置文件

2. **Spring Context加载失败**
   - 检查测试注解配置
   - 验证依赖注入配置

3. **Mock对象不工作**
   - 确认使用正确的Mock注解
   - 检查Mock设置是否正确

### 调试技巧

- 使用`@Sql`注解执行特定SQL
- 使用`@TestPropertySource`覆盖配置
- 启用DEBUG日志查看详细信息

## 总结

通过完善的测试策略，我们确保了智慧校园管理系统的质量和可靠性。测试覆盖了从数据访问层到业务逻辑层的各个方面，为系统的持续开发和维护提供了坚实的保障。
