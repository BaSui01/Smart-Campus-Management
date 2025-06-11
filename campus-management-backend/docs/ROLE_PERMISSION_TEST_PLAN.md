# 智慧校园管理系统 - 50角色权限测试方案

## 📋 测试概述

本文档描述了基于50个角色的智慧校园管理系统API权限控制测试方案，确保权限控制的安全性和准确性。

## 🎯 测试目标

1. **权限隔离验证** - 确保不同角色只能访问其权限范围内的API
2. **层级权限验证** - 验证高层级角色能够访问低层级角色的权限
3. **功能权限验证** - 验证特定功能模块的权限控制正确性
4. **安全性验证** - 确保未授权访问被正确拒绝

## 🏗️ 角色层级结构

### 系统管理层级 (Level 1-4)
- **Level 1**: `ROLE_SUPER_ADMIN`, `ROLE_ADMIN` - 最高权限
- **Level 2**: `ROLE_PRINCIPAL` - 校长权限
- **Level 3**: `ROLE_VICE_PRINCIPAL` - 副校长权限
- **Level 4**: 各处长角色 - 部门管理权限

### 学院管理层级 (Level 5-9)
- **Level 5**: `ROLE_DEAN`, `ROLE_LIBRARY_DIRECTOR` - 院级管理
- **Level 6**: `ROLE_VICE_DEAN`, `ROLE_SPORTS_DIRECTOR` - 副院级管理
- **Level 7**: `ROLE_DEPARTMENT_HEAD` - 系主任
- **Level 8**: `ROLE_VICE_DEPARTMENT_HEAD` - 副系主任
- **Level 9**: `ROLE_TEACHING_GROUP_HEAD`, `ROLE_LAB_DIRECTOR` - 教研室主任

### 教学人员层级 (Level 10-17)
- **Level 10**: `ROLE_TEACHER`, `ROLE_PROFESSOR`, `ROLE_SUPERVISOR` - 高级教学人员
- **Level 11**: `ROLE_ASSOCIATE_PROFESSOR` - 副教授
- **Level 12**: `ROLE_LECTURER` - 讲师
- **Level 13**: `ROLE_ASSISTANT` - 助教
- **Level 14**: `ROLE_COUNSELOR` - 辅导员
- **Level 15**: `ROLE_CLASS_TEACHER` - 班主任
- **Level 16**: 各类专业技术人员
- **Level 17**: `ROLE_VISITING_TEACHER` - 外聘教师

### 学生层级 (Level 18-25)
- **Level 18**: `ROLE_PHD_STUDENT`, `ROLE_STUDENT_LEADER` - 博士生、学生干部
- **Level 19**: `ROLE_GRADUATE`, `ROLE_MASTER_STUDENT`, `ROLE_CLASS_MONITOR` - 研究生、班长
- **Level 20**: `ROLE_STUDENT`, `ROLE_UNDERGRADUATE` - 本科生
- **Level 21**: `ROLE_INTERNATIONAL_STUDENT` - 留学生
- **Level 22**: `ROLE_EXCHANGE_STUDENT` - 交换生
- **Level 25**: `ROLE_AUDITOR` - 旁听生

### 其他角色 (Level 30-50)
- **Level 30**: `ROLE_PARENT` - 家长
- **Level 50**: `ROLE_VISITOR` - 访客

## 🧪 测试用例设计

### 1. 考勤管理API权限测试

#### 测试场景1: 考勤记录查询权限
```http
GET /api/v1/attendance
```

**预期权限分配**:
- ✅ **允许访问**: 系统管理员、校级领导、教务相关角色、教学人员、学生工作人员
- ❌ **拒绝访问**: 学生、家长、访客、服务人员

**测试步骤**:
1. 使用`ROLE_SUPER_ADMIN`角色访问 → 应该成功
2. 使用`ROLE_PRINCIPAL`角色访问 → 应该成功
3. 使用`ROLE_TEACHER`角色访问 → 应该成功
4. 使用`ROLE_STUDENT`角色访问 → 应该被拒绝
5. 使用`ROLE_PARENT`角色访问 → 应该被拒绝

#### 测试场景2: 学生签到权限
```http
POST /api/v1/attendance/checkin
```

**预期权限分配**:
- ✅ **允许访问**: 所有学生角色 + 考勤管理权限角色
- ❌ **拒绝访问**: 家长、访客、服务人员

### 2. 课程管理API权限测试

#### 测试场景3: 课程创建权限
```http
POST /api/v1/courses
```

**预期权限分配**:
- ✅ **允许访问**: 系统管理员、校级领导、教务管理相关角色
- ❌ **拒绝访问**: 普通教师、学生、家长、访客

#### 测试场景4: 课程查看权限
```http
GET /api/v1/courses
```

**预期权限分配**:
- ✅ **允许访问**: 教务管理和教学人员
- ❌ **拒绝访问**: 学生、家长、访客、服务人员

### 3. 统计信息权限测试

#### 测试场景5: 统计数据查看权限
```http
GET /api/v1/attendance/stats
GET /api/v1/courses/stats
```

**预期权限分配**:
- ✅ **允许访问**: 管理层和相关工作人员
- ❌ **拒绝访问**: 普通教师、学生、家长、访客

### 4. 批量操作权限测试

#### 测试场景6: 批量删除权限
```http
DELETE /api/v1/attendance/batch
DELETE /api/v1/courses/batch
```

**预期权限分配**:
- ✅ **允许访问**: 仅高级管理员 (SUPER_ADMIN, ADMIN, PRINCIPAL)
- ❌ **拒绝访问**: 所有其他角色

## 🔧 测试工具和方法

### 1. 自动化测试脚本

```bash
# 权限测试脚本示例
#!/bin/bash

# 测试不同角色的API访问权限
test_role_permission() {
    local role=$1
    local endpoint=$2
    local expected_status=$3
    
    token=$(get_token_for_role $role)
    response=$(curl -s -w "%{http_code}" -H "Authorization: Bearer $token" $endpoint)
    
    if [ "$response" = "$expected_status" ]; then
        echo "✅ $role -> $endpoint: PASS"
    else
        echo "❌ $role -> $endpoint: FAIL (Expected: $expected_status, Got: $response)"
    fi
}

# 执行测试
test_role_permission "ROLE_SUPER_ADMIN" "/api/v1/attendance" "200"
test_role_permission "ROLE_STUDENT" "/api/v1/attendance" "403"
test_role_permission "ROLE_PARENT" "/api/v1/courses/batch" "403"
```

### 2. 单元测试

```java
@Test
@WithMockUser(roles = {"SUPER_ADMIN"})
public void testSuperAdminCanAccessAllEndpoints() {
    // 测试超级管理员可以访问所有端点
}

@Test
@WithMockUser(roles = {"STUDENT"})
public void testStudentCanOnlyAccessStudentEndpoints() {
    // 测试学生只能访问学生相关端点
}

@Test
@WithMockUser(roles = {"PARENT"})
public void testParentCanOnlyViewStudentInfo() {
    // 测试家长只能查看学生信息
}
```

### 3. 集成测试

使用TestContainers进行完整的权限集成测试：

```java
@SpringBootTest
@Testcontainers
public class RolePermissionIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");
    
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine");
    
    @Test
    public void testCompleteRoleHierarchy() {
        // 测试完整的角色层级权限
    }
}
```

## 📊 测试报告模板

### 权限测试结果矩阵

| API端点 | SUPER_ADMIN | PRINCIPAL | DEAN | TEACHER | STUDENT | PARENT | VISITOR |
|---------|-------------|-----------|------|---------|---------|--------|---------|
| GET /api/v1/attendance | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| POST /api/v1/attendance/checkin | ✅ | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ |
| GET /api/v1/courses | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ |
| POST /api/v1/courses | ✅ | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ |
| DELETE /api/v1/courses/batch | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

## 🚀 执行测试

### 1. 准备测试环境
```bash
# 启动测试数据库
docker-compose -f docker-compose.test.yml up -d

# 初始化测试数据
mvn test-compile exec:java -Dexec.mainClass="TestDataInitializer"
```

### 2. 运行权限测试
```bash
# 运行所有权限测试
mvn test -Dtest="*PermissionTest"

# 运行特定角色测试
mvn test -Dtest="RoleHierarchyTest"
```

### 3. 生成测试报告
```bash
# 生成详细的权限测试报告
mvn surefire-report:report
```

## ✅ 验收标准

1. **100%权限覆盖** - 所有API端点都有明确的权限控制
2. **0权限泄露** - 没有角色能访问超出其权限范围的API
3. **层级一致性** - 高层级角色能访问低层级角色的所有权限
4. **功能完整性** - 每个角色都能完成其职责范围内的所有操作
5. **安全性保证** - 所有未授权访问都被正确拒绝

## 🔄 持续监控

1. **自动化回归测试** - 每次代码变更后自动运行权限测试
2. **权限审计日志** - 记录所有权限检查和访问尝试
3. **异常监控** - 监控权限拒绝和异常访问模式
4. **定期权限审查** - 定期审查和更新权限配置

通过这个全面的测试方案，我们可以确保智慧校园管理系统的50角色权限控制系统的安全性和正确性。

## 📋 权限配置完成总结

### ✅ 已完成的工作

1. **角色层级工具类** - 创建了`RoleHierarchyUtil`类，支持50个角色的层级管理
2. **权限常量类** - 创建了`RolePermissions`类，定义了标准化的权限表达式
3. **权限工具类增强** - 更新了`PermissionUtil`类，支持新的角色分组
4. **API控制器更新** - 更新了`AttendanceApiController`和`CourseApiController`的权限注解
5. **用户服务增强** - 更新了`UserServiceImpl`中的菜单权限检查逻辑
6. **安全拦截器修复** - 修复了所有安全拦截器中的角色键不一致问题

### 🎯 权限控制特点

1. **层级化权限** - 基于角色层级(1-50)的权限继承机制
2. **功能化分组** - 按职能分组的权限管理(教学、学工、财务等)
3. **细粒度控制** - 精确到API端点级别的权限控制
4. **安全性保证** - 多层次的权限验证和安全检查

### 🔧 使用方式

```java
// 使用权限常量
@PreAuthorize(RolePermissions.ATTENDANCE_MANAGEMENT)
public ResponseEntity<ApiResponse<List<Attendance>>> getAttendanceRecords() {
    // 考勤管理权限
}

@PreAuthorize(RolePermissions.STUDENT_CHECKIN + " || " + RolePermissions.ATTENDANCE_MANAGEMENT)
public ResponseEntity<ApiResponse<Attendance>> checkIn() {
    // 学生签到权限或考勤管理权限
}

// 使用角色层级工具
@Autowired
private RoleHierarchyUtil roleHierarchyUtil;

public boolean hasRequiredPermission(Set<String> userRoles) {
    return roleHierarchyUtil.hasAnyRoleInGroup(userRoles, "TEACHING_STAFF");
}
```

### 📈 下一步工作

1. **完善其他API控制器** - 更新剩余的API控制器权限注解
2. **编写单元测试** - 为权限控制编写完整的单元测试
3. **集成测试** - 使用TestContainers进行权限集成测试
4. **性能优化** - 优化权限检查的性能
5. **文档完善** - 完善API文档中的权限说明
