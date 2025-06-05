# 智慧校园管理系统架构重构方案

## 一、当前架构问题分析

### 1. Controller层混乱
- 混合了API控制器和页面控制器
- 缺乏统一的REST API设计规范
- 权限控制分散，没有统一的认证拦截

### 2. 包结构不清晰
- controller包下同时存在admin、api子包和直接的控制器类
- 服务层和数据层缺乏明确的分层架构
- 缺乏领域模型的清晰划分

### 3. 技术债务
- 使用了JPA和MyBatis Plus双ORM框架
- 缺乏统一的异常处理机制
- 没有API版本控制策略

### 4. 安全架构
- JWT工具类重复（JwtUtil和JwtTokenUtil）
- 认证逻辑分散在多个控制器中
- 缺乏统一的权限验证机制

## 二、重构目标

### 1. 清晰的分层架构
- 明确的领域驱动设计(DDD)架构
- 统一的API设计规范
- 清晰的包结构划分

### 2. 微服务预备架构
- 模块化设计，便于后续拆分微服务
- 统一的配置管理
- 完善的监控和日志体系

### 3. 现代化技术栈
- 统一使用JPA作为ORM
- 完善的缓存策略
- 统一的异常处理和响应格式

## 三、重构实施方案

### 阶段一：包结构重组 (立即执行)

#### 1. 新的包结构设计
```
com.campus/
├── application/           # 应用服务层
│   ├── service/          # 应用服务
│   ├── dto/              # 数据传输对象
│   └── command/          # 命令对象
├── domain/               # 领域层
│   ├── entity/           # 领域实体
│   ├── repository/       # 仓储接口
│   ├── service/          # 领域服务
│   └── event/            # 领域事件
├── infrastructure/       # 基础设施层
│   ├── persistence/      # 持久化实现
│   ├── cache/            # 缓存实现
│   ├── messaging/        # 消息实现
│   └── external/         # 外部服务
├── interfaces/           # 接口层
│   ├── rest/            # REST API
│   │   ├── v1/          # API版本1
│   │   └── admin/       # 管理端API
│   ├── web/             # Web页面控制器
│   └── dto/             # 接口DTO
├── shared/               # 共享层
│   ├── common/          # 通用组件
│   ├── config/          # 配置类
│   ├── security/        # 安全配置
│   ├── exception/       # 异常处理
│   └── util/            # 工具类
└── modules/              # 业务模块
    ├── user/            # 用户模块
    ├── academic/        # 教务模块
    ├── finance/         # 财务模块
    └── system/          # 系统模块
```

#### 2. API版本控制策略
- 采用URL版本控制：`/api/v1/`
- 管理端API独立：`/admin/api/`
- Web页面控制器：`/web/`

### 阶段二：核心组件重构

#### 1. 统一响应格式
```java
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;
    private String requestId;
}
```

#### 2. 统一异常处理
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 业务异常
    // 系统异常
    // 参数验证异常
    // 权限异常
}
```

#### 3. 统一认证授权
```java
@Component
public class JwtAuthenticationProvider {
    // 统一JWT处理
    // 权限验证
    // 用户信息获取
}
```

### 阶段三：模块化重构

#### 1. 用户管理模块
- 用户注册/登录
- 权限管理
- 角色管理

#### 2. 教务管理模块
- 课程管理
- 排课管理
- 成绩管理

#### 3. 财务管理模块
- 缴费管理
- 财务报表
- 退费管理

#### 4. 系统管理模块
- 系统配置
- 数据字典
- 操作日志

### 阶段四：性能优化

#### 1. 缓存策略
- Redis缓存热点数据
- 本地缓存配置信息
- 分布式锁实现

#### 2. 数据库优化
- 统一使用jpa
- 数据库连接池优化
- SQL性能监控

#### 3. 监控告警
- 接口性能监控
- 业务指标监控
- 系统资源监控

## 四、技术栈选择

### 1. 保留的技术
- Spring Boot 3.1.5
- Spring Security
- Redis
- MySQL
- Quartz

### 2. 优化的技术
- 统一使用JPAM（移除myBatis Plus）
- 使用Spring Cache抽象层
- 集成OpenAPI 3.0文档

### 3. 新增的技术
- Mapstruct（对象映射）
- Caffeine（本地缓存）
- SkyWalking（链路追踪）

## 五、执行计划

### 第1周：包结构重组
1. 创建新的包结构
2. 移动现有类到新包
3. 更新import引用

### 第2周：API重构
1. 统一API设计规范
2. 实现版本控制
3. 完善异常处理

### 第3周：安全重构
1. 统一认证机制
2. 完善权限控制
3. 安全配置优化

### 第4周：模块化重构
1. 按业务领域拆分模块
2. 实现领域服务
3. 优化数据访问层

### 第5周：性能优化
1. 缓存策略实施
2. 数据库优化
3. 监控系统集成

### 第6周：测试和部署
1. 单元测试完善
2. 集成测试
3. 生产环境部署

## 六、风险控制

### 1. 向下兼容
- 保留原有API接口
- 渐进式迁移策略
- 充分的测试覆盖

### 2. 数据安全
- 数据库备份策略
- 迁移脚本测试
- 回滚方案准备

### 3. 性能影响
- 性能基准测试
- 负载测试验证
- 监控指标对比