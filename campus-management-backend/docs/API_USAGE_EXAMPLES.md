# 智慧校园管理系统 API 使用示例

## 📋 概述

本文档提供了智慧校园管理系统 REST API 的详细使用示例，包括请求格式、响应格式、错误处理等。

**API 基础地址**: `http://localhost:8889/api/v1`  
**API 文档地址**: `http://localhost:8889/swagger-ui/index.html`

---

## 🔐 认证相关 API

### 1. 用户登录

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY4NzUwMDAwMCwiZXhwIjoxNjg3NTg2NDAwfQ.xxx",
    "refreshToken": "refresh_token_here",
    "user": {
      "id": 1,
      "username": "admin",
      "realName": "系统管理员",
      "email": "admin@campus.edu",
      "roles": ["ADMIN"]
    }
  },
  "timestamp": "2025-06-20T10:00:00"
}
```

### 2. 用户注册

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student001",
    "password": "123456",
    "realName": "张三",
    "email": "zhangsan@student.edu",
    "phone": "13800138000",
    "userType": "STUDENT"
  }'
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 2,
    "username": "student001",
    "realName": "张三",
    "email": "zhangsan@student.edu",
    "status": 1
  },
  "timestamp": "2025-06-20T10:05:00"
}
```

---

## 👥 用户管理 API

### 1. 获取用户列表

**请求示例**:
```bash
curl -X GET "http://localhost:8889/api/v1/users?page=0&size=10&sort=id,desc" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "admin",
        "realName": "系统管理员",
        "email": "admin@campus.edu",
        "phone": "13800138001",
        "status": 1,
        "createdAt": "2025-06-20T09:00:00",
        "roles": ["ADMIN"]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "orders": [{"property": "id", "direction": "DESC"}]
      }
    },
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2025-06-20T10:10:00"
}
```

### 2. 创建用户

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/users" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teacher001",
    "password": "123456",
    "realName": "李老师",
    "email": "liteacher@campus.edu",
    "phone": "13800138002",
    "userType": "TEACHER",
    "departmentId": 1
  }'
```

### 3. 更新用户信息

**请求示例**:
```bash
curl -X PUT "http://localhost:8889/api/v1/users/2" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "realName": "李教授",
    "email": "professor.li@campus.edu",
    "phone": "13800138003"
  }'
```

### 4. 删除用户

**请求示例**:
```bash
curl -X DELETE "http://localhost:8889/api/v1/users/2" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🎓 学生管理 API

### 1. 获取学生列表

**请求示例**:
```bash
curl -X GET "http://localhost:8889/api/v1/students?page=0&size=20&classId=1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 2. 创建学生

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/students" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "studentNumber": "2025001",
    "realName": "王小明",
    "gender": "MALE",
    "birthDate": "2005-03-15",
    "email": "wangxiaoming@student.edu",
    "phone": "13800138004",
    "classId": 1,
    "majorId": 1,
    "enrollmentYear": 2025
  }'
```

### 3. 获取学生详情

**请求示例**:
```bash
curl -X GET "http://localhost:8889/api/v1/students/1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "id": 1,
    "studentNumber": "2025001",
    "realName": "王小明",
    "gender": "MALE",
    "birthDate": "2005-03-15",
    "email": "wangxiaoming@student.edu",
    "phone": "13800138004",
    "status": 1,
    "enrollmentYear": 2025,
    "class": {
      "id": 1,
      "className": "计算机科学与技术1班",
      "grade": "2025级"
    },
    "major": {
      "id": 1,
      "majorName": "计算机科学与技术",
      "department": {
        "id": 1,
        "departmentName": "计算机学院"
      }
    }
  },
  "timestamp": "2025-06-20T10:15:00"
}
```

---

## 📚 课程管理 API

### 1. 获取课程列表

**请求示例**:
```bash
curl -X GET "http://localhost:8889/api/v1/courses?page=0&size=10&semester=2025-1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 2. 创建课程

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/courses" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "数据结构与算法",
    "courseCode": "CS101",
    "credits": 4,
    "courseType": "REQUIRED",
    "description": "计算机科学基础课程",
    "teacherId": 1,
    "semester": "2025-1",
    "maxStudents": 60
  }'
```

### 3. 课程选课

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/course-selections" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "courseId": 1,
    "semester": "2025-1"
  }'
```

---

## 📝 作业管理 API

### 1. 创建作业

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/assignments" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "数据结构实验1",
    "description": "实现链表的基本操作",
    "courseId": 1,
    "dueDate": "2025-07-01T23:59:59",
    "maxScore": 100,
    "requirements": "使用C++实现，提交源代码和实验报告"
  }'
```

### 2. 提交作业

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/assignment-submissions" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "assignmentId": 1,
    "studentId": 1,
    "content": "作业内容描述",
    "attachmentUrl": "/uploads/assignments/student1_assignment1.zip"
  }'
```

---

## 📊 成绩管理 API

### 1. 录入成绩

**请求示例**:
```bash
curl -X POST "http://localhost:8889/api/v1/grades" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 1,
    "courseId": 1,
    "examType": "FINAL",
    "score": 85.5,
    "semester": "2025-1"
  }'
```

### 2. 查询学生成绩

**请求示例**:
```bash
curl -X GET "http://localhost:8889/api/v1/grades/student/1?semester=2025-1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 🔍 监控和健康检查 API

### 1. 健康检查

**请求示例**:
```bash
curl -X GET "http://localhost:8889/actuator/health"
```

**响应示例**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "6.0.16"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 499963174912,
        "free": 123456789012,
        "threshold": 10485760,
        "exists": true
      }
    }
  }
}
```

### 2. 应用信息

**请求示例**:
```bash
curl -X GET "http://localhost:8889/actuator/info"
```

### 3. 监控指标

**请求示例**:
```bash
curl -X GET "http://localhost:8889/actuator/metrics"
curl -X GET "http://localhost:8889/actuator/metrics/campus.api.request.count"
curl -X GET "http://localhost:8889/actuator/prometheus"
```

---

## ❌ 错误处理

### 常见错误响应格式

```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "errors": [
    {
      "field": "email",
      "message": "邮箱格式不正确"
    }
  ],
  "timestamp": "2025-06-20T10:20:00"
}
```

### HTTP 状态码说明

| 状态码 | 说明 | 示例 |
|--------|------|------|
| 200 | 请求成功 | 数据获取成功 |
| 201 | 创建成功 | 用户创建成功 |
| 400 | 请求参数错误 | 必填字段缺失 |
| 401 | 未授权 | Token无效或过期 |
| 403 | 权限不足 | 无权限访问该资源 |
| 404 | 资源不存在 | 用户不存在 |
| 409 | 资源冲突 | 用户名已存在 |
| 500 | 服务器内部错误 | 数据库连接失败 |

---

## 🔧 开发工具

### Postman 集合

我们提供了完整的 Postman 集合文件，包含所有 API 的示例请求：

1. 导入集合文件：`docs/postman/Campus-Management-API.postman_collection.json`
2. 导入环境变量：`docs/postman/Campus-Management-Environment.postman_environment.json`
3. 设置环境变量中的 `baseUrl` 和 `token`

### cURL 脚本

我们还提供了常用操作的 cURL 脚本：

- `scripts/api-test.sh` - API 测试脚本
- `scripts/user-management.sh` - 用户管理操作
- `scripts/course-management.sh` - 课程管理操作

---

## 📝 注意事项

1. **认证**: 除了登录和注册接口，所有 API 都需要在请求头中包含有效的 JWT Token
2. **分页**: 列表查询接口支持分页，默认页大小为 10
3. **排序**: 支持多字段排序，格式为 `sort=field1,direction1&sort=field2,direction2`
4. **过滤**: 支持基本的字段过滤查询
5. **限流**: API 有访问频率限制，请合理控制请求频率
6. **版本**: 当前 API 版本为 v1，后续版本会保持向后兼容

更多详细信息请参考 [Swagger API 文档](http://localhost:8889/swagger-ui/index.html)。
