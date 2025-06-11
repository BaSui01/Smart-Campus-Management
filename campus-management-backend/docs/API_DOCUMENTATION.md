# 智慧校园管理系统 API 文档

## 概述

本文档描述了智慧校园管理系统的REST API接口，包含36个API控制器和55个核心接口，涵盖认证、用户管理、角色管理、课程管理、学生管理、班级管理等8大功能模块。

## 🔐 认证和授权

### JWT Token认证

系统使用JWT Token进行认证，除了登录和注册接口外，所有API都需要在请求头中包含有效的Token：

```http
Authorization: Bearer <your-jwt-token>
```

### 权限控制

系统基于角色的访问控制(RBAC)，主要角色包括：

| 角色 | 权限级别 | 描述 |
|------|----------|------|
| `SUPER_ADMIN` | 最高权限 | 超级管理员，拥有所有权限 |
| `ADMIN` | 高级权限 | 系统管理员，管理用户和系统配置 |
| `SYSTEM_ADMIN` | 系统权限 | 系统管理员，管理系统功能 |
| `ACADEMIC_ADMIN` | 教务权限 | 教务管理员，管理学术相关功能 |
| `FINANCE_ADMIN` | 财务权限 | 财务管理员，管理费用和支付 |
| `TEACHER` | 教师权限 | 教师，管理课程和学生 |
| `STUDENT` | 学生权限 | 学生，查看个人信息和课程 |

### 权限注解说明

- `@PreAuthorize("hasAnyRole('ADMIN', 'SYSTEM_ADMIN')")`: 需要管理员权限
- `@PreAuthorize("hasRole('TEACHER')")`: 需要教师权限
- 无注解：公开接口或仅需登录

## 通用响应格式

所有API接口都使用统一的响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1640995200000
}
```

### 响应状态码

- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未授权访问（Token无效或过期）
- `403`: 禁止访问（权限不足）
- `404`: 资源未找到
- `500`: 服务器内部错误

## API 模块概览

系统共包含8大功能模块，36个API控制器：

| 模块 | 控制器数量 | 主要功能 |
|------|------------|----------|
| 认证模块 | 1个 | 用户登录、注册、Token管理 |
| 核心业务模块 | 5个 | 用户、学生、课程、班级、院系管理 |
| 学术管理模块 | 8个 | 作业、考试、成绩、选课管理 |
| 系统管理模块 | 6个 | 权限、角色、通知、配置管理 |
| 财务管理模块 | 2个 | 费用、支付管理 |
| 消息通信模块 | 3个 | 消息、通知模板管理 |
| 特殊功能模块 | 5个 | 排课、教室、缓存管理 |
| 扩展功能模块 | 6个 | 日志、评价、关系管理 |

---

## 1. 认证模块 API

### 基础路径: `/api/v1/auth`
**权限要求**: 大部分接口无需Token，获取用户信息需要Token

#### 1.1 用户登录 🔓
- **URL**: `POST /api/v1/auth/login`
- **权限**: 无需Token
- **请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **响应**: JWT Token和用户信息

#### 1.2 用户注册 🔓
- **URL**: `POST /api/v1/auth/register`
- **权限**: 无需Token
- **请求体**: 注册信息
- **响应**: JWT Token和用户信息

#### 1.3 获取当前用户信息 🔒
- **URL**: `GET /api/v1/auth/me`
- **权限**: 需要Token
- **响应**: 当前用户详细信息

#### 1.4 刷新Token 🔒
- **URL**: `POST /api/v1/auth/refresh`
- **权限**: 需要Token
- **参数**: `refreshToken` (string)
- **响应**: 新的JWT Token

#### 1.5 用户登出 🔒
- **URL**: `POST /api/v1/auth/logout`
- **权限**: 需要Token
- **响应**: 登出结果

#### 1.6 修改密码 🔒
- **URL**: `POST /api/v1/auth/change-password`
- **权限**: 需要Token
- **请求体**: 旧密码和新密码
- **响应**: 修改结果

#### 1.7 获取认证统计 🔒👑
- **URL**: `GET /api/v1/auth/stats`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`, `ACADEMIC_ADMIN`, `TEACHER`
- **响应**: 认证模块统计数据

#### 1.8 批量锁定用户 🔒👑
- **URL**: `PUT /api/v1/auth/batch/lock`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **请求体**: 用户ID列表
- **响应**: 批量锁定结果

#### 1.9 批量解锁用户 🔒👑
- **URL**: `PUT /api/v1/auth/batch/unlock`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **请求体**: 用户ID列表
- **响应**: 批量解锁结果

---

## 2. 用户管理 API

### 基础路径: `/api/v1/users`
**权限要求**: 大部分接口需要管理员权限

#### 2.1 获取用户列表（分页）🔒
- **URL**: `GET /api/v1/users`
- **权限**: 需要Token
- **参数**:
  - `page` (int): 页码，默认1
  - `size` (int): 每页大小，默认10
  - `search` (string): 搜索关键词
  - `username` (string): 用户名
  - `realName` (string): 真实姓名
  - `role` (string): 角色
  - `status` (int): 状态
- **响应**: 分页用户列表

#### 2.2 获取用户详情 🔒
- **URL**: `GET /api/v1/users/{id}`
- **权限**: 需要Token
- **参数**: `id` (long): 用户ID
- **响应**: 用户详细信息

#### 2.3 创建用户 🔒
- **URL**: `POST /api/v1/users`
- **权限**: 需要Token
- **请求体**: User对象
- **响应**: 创建的用户信息

#### 2.4 更新用户 🔒
- **URL**: `PUT /api/v1/users/{id}`
- **权限**: 需要Token
- **参数**: `id` (long): 用户ID
- **请求体**: 用户更新数据
- **响应**: 更新结果

#### 2.5 删除用户 🔒
- **URL**: `DELETE /api/v1/users/{id}`
- **权限**: 需要Token
- **参数**: `id` (long): 用户ID
- **响应**: 删除结果

#### 2.6 批量删除用户 🔒
- **URL**: `DELETE /api/v1/users/batch`
- **权限**: 需要Token
- **请求体**: 用户ID列表
- **响应**: 批量删除结果

#### 2.7 重置密码 🔒
- **URL**: `POST /api/v1/users/{id}/reset-password`
- **权限**: 需要Token
- **参数**: `id` (long): 用户ID
- **响应**: 重置结果

#### 2.8 获取用户统计信息 🔒
- **URL**: `GET /api/v1/users/stats`
- **权限**: 需要Token
- **响应**: 用户统计数据

#### 2.9 切换用户状态 🔒
- **URL**: `POST /api/v1/users/{id}/toggle-status`
- **权限**: 需要Token
- **参数**: `id` (long): 用户ID
- **响应**: 状态切换结果

#### 2.10 导出用户数据 🔒
- **URL**: `GET /api/v1/users/export`
- **权限**: 需要Token
- **参数**: 筛选条件
- **响应**: 用户数据文件

---

## 3. 角色管理 API

### 基础路径: `/api/v1/roles`
**权限要求**: 大部分接口需要管理员权限

#### 3.1 获取角色列表（分页）🔒
- **URL**: `GET /api/v1/roles`
- **权限**: 需要Token
- **参数**:
  - `page` (int): 页码，默认0
  - `size` (int): 每页大小，默认20
  - `search` (string): 搜索关键词
  - `status` (string): 状态
- **响应**: 分页角色列表

#### 3.2 获取所有启用的角色 🔒
- **URL**: `GET /api/v1/roles/active`
- **权限**: 需要Token
- **响应**: 启用角色列表

#### 3.3 获取角色详情 🔒
- **URL**: `GET /api/v1/roles/{id}`
- **权限**: 需要Token
- **参数**: `id` (long): 角色ID
- **响应**: 角色详细信息

#### 3.4 创建角色 🔒👑
- **URL**: `POST /api/v1/roles`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **请求体**: Role对象
- **响应**: 创建的角色信息

#### 3.5 更新角色 🔒👑
- **URL**: `PUT /api/v1/roles/{id}`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **参数**: `id` (long): 角色ID
- **请求体**: Role对象
- **响应**: 更新的角色信息

#### 3.6 删除角色 🔒👑
- **URL**: `DELETE /api/v1/roles/{id}`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **参数**: `id` (long): 角色ID
- **响应**: 删除结果

#### 3.7 切换角色状态 🔒👑
- **URL**: `PATCH /api/v1/roles/{id}/toggle-status`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **参数**: `id` (long): 角色ID
- **响应**: 状态切换结果

#### 3.8 获取角色统计信息 🔒
- **URL**: `GET /api/v1/roles/statistics`
- **权限**: 需要Token
- **响应**: 角色统计数据

#### 3.9 批量更新角色状态 🔒👑
- **URL**: `PUT /api/v1/roles/batch/status`
- **权限**: `ADMIN`, `SYSTEM_ADMIN`
- **请求体**: 角色ID列表和状态
- **响应**: 批量更新结果

#### 3.10 导出角色数据 🔒
- **URL**: `GET /api/v1/roles/export`
- **权限**: 需要Token
- **响应**: 角色数据文件

---

## 4. 学生管理 API

### 基础路径: `/api/v1/students`
**权限要求**: 大部分接口需要教务管理员权限

#### 4.1 获取学生列表（分页）🔒
- **URL**: `GET /api/v1/students`
- **权限**: 需要Token
- **参数**:
  - `page` (int): 页码，默认1
  - `size` (int): 每页大小，默认10
  - `studentNo` (string): 学号
  - `grade` (string): 年级
  - `classId` (long): 班级ID
  - `status` (int): 状态
- **响应**: 分页学生列表

#### 4.2 获取学生详情 🔒
- **URL**: `GET /api/v1/students/{id}`
- **权限**: 需要Token
- **参数**: `id` (long): 学生ID
- **响应**: 学生详细信息

#### 4.3 创建学生 🔒👑
- **URL**: `POST /api/v1/students`
- **权限**: `ADMIN`, `ACADEMIC_ADMIN`
- **请求体**: Student对象
- **响应**: 创建的学生信息

#### 4.4 更新学生 🔒👑
- **URL**: `PUT /api/v1/students/{id}`
- **权限**: `ADMIN`, `ACADEMIC_ADMIN`
- **参数**: `id` (long): 学生ID
- **请求体**: Student对象
- **响应**: 更新结果

#### 4.5 删除学生 🔒👑
- **URL**: `DELETE /api/v1/students/{id}`
- **权限**: `ADMIN`, `ACADEMIC_ADMIN`
- **参数**: `id` (long): 学生ID
- **响应**: 删除结果

#### 4.6 获取学生表单数据 🔒
- **URL**: `GET /api/v1/students/form-data`
- **权限**: 需要Token
- **响应**: 创建/编辑学生所需的表单数据

#### 4.7 批量删除学生 🔒👑
- **URL**: `DELETE /api/v1/students/batch`
- **权限**: `ADMIN`, `ACADEMIC_ADMIN`
- **请求体**: 学生ID列表
- **响应**: 批量删除结果

#### 4.8 批量导入学生 🔒👑
- **URL**: `POST /api/v1/students/batch/import`
- **权限**: `ADMIN`, `ACADEMIC_ADMIN`
- **请求体**: 学生数据列表
- **响应**: 批量导入结果

#### 4.9 获取学生统计信息 🔒
- **URL**: `GET /api/v1/students/stats`
- **权限**: 需要Token
- **响应**: 学生统计数据

#### 4.10 导出学生数据 🔒
- **URL**: `GET /api/v1/students/export`
- **权限**: 需要Token
- **参数**: 筛选条件
- **响应**: 学生数据文件

## 3. 课程管理 API

### 基础路径: `/api/v1/courses`

#### 3.1 获取课程列表（分页）
- **URL**: `GET /api/v1/courses`
- **参数**:
  - `page` (int): 页码，默认1
  - `size` (int): 每页大小，默认10
  - `courseName` (string): 课程名称
  - `courseType` (string): 课程类型
  - `semester` (string): 开课学期
  - `status` (int): 状态
- **响应**: 分页课程列表

#### 3.2 获取课程详情
- **URL**: `GET /api/v1/courses/{id}`
- **参数**: `id` (long): 课程ID
- **响应**: 课程详细信息

#### 3.3 创建课程
- **URL**: `POST /api/v1/courses`
- **请求体**: Course对象
- **响应**: 创建的课程信息

#### 3.4 更新课程
- **URL**: `PUT /api/v1/courses/{id}`
- **参数**: `id` (long): 课程ID
- **请求体**: Course对象
- **响应**: 更新结果

#### 3.5 删除课程
- **URL**: `DELETE /api/v1/courses/{id}`
- **参数**: `id` (long): 课程ID
- **响应**: 删除结果

#### 3.6 批量删除课程
- **URL**: `DELETE /api/v1/courses/batch`
- **请求体**: 课程ID列表
- **响应**: 批量删除结果

#### 3.7 获取课程统计信息
- **URL**: `GET /api/v1/courses/stats`
- **响应**: 课程统计数据

#### 3.8 获取课程表单数据
- **URL**: `GET /api/v1/courses/form-data`
- **响应**: 创建/编辑课程所需的表单数据

## 4. 学生管理 API

### 基础路径: `/api/v1/students`

#### 4.1 获取学生列表（分页）
- **URL**: `GET /api/v1/students`
- **参数**:
  - `page` (int): 页码，默认1
  - `size` (int): 每页大小，默认10
  - `studentNo` (string): 学号
  - `grade` (string): 年级
  - `classId` (long): 班级ID
  - `status` (int): 状态
- **响应**: 分页学生列表

#### 4.2 获取学生详情
- **URL**: `GET /api/v1/students/{id}`
- **参数**: `id` (long): 学生ID
- **响应**: 学生详细信息

#### 4.3 创建学生
- **URL**: `POST /api/v1/students`
- **请求体**: Student对象
- **响应**: 创建的学生信息

#### 4.4 获取学生表单数据
- **URL**: `GET /api/v1/students/form-data`
- **响应**: 创建/编辑学生所需的表单数据

#### 4.5 批量删除学生
- **URL**: `DELETE /api/v1/students/batch`
- **请求体**: 学生ID列表
- **响应**: 批量删除结果

#### 4.6 批量导入学生
- **URL**: `POST /api/v1/students/batch/import`
- **请求体**: 学生数据列表
- **响应**: 批量导入结果

#### 4.7 获取学生统计信息
- **URL**: `GET /api/v1/students/stats`
- **响应**: 学生统计数据

## 5. 班级管理 API

### 基础路径: `/api/v1/classes`

#### 5.1 获取班级列表（分页）
- **URL**: `GET /api/v1/classes`
- **参数**:
  - `page` (int): 页码，默认1
  - `size` (int): 每页大小，默认10
  - `className` (string): 班级名称
  - `grade` (string): 年级
  - `major` (string): 专业
  - `status` (int): 状态
- **响应**: 分页班级列表

#### 5.2 获取班级详情
- **URL**: `GET /api/v1/classes/{id}`
- **参数**: `id` (long): 班级ID
- **响应**: 班级详细信息

#### 5.3 创建班级
- **URL**: `POST /api/v1/classes`
- **请求体**: SchoolClass对象
- **响应**: 创建的班级信息

#### 5.4 更新班级
- **URL**: `PUT /api/v1/classes/{id}`
- **参数**: `id` (long): 班级ID
- **请求体**: SchoolClass对象
- **响应**: 更新结果

#### 5.5 删除班级
- **URL**: `DELETE /api/v1/classes/{id}`
- **参数**: `id` (long): 班级ID
- **响应**: 删除结果

#### 5.6 获取班级表单数据
- **URL**: `GET /api/v1/classes/form-data`
- **响应**: 创建/编辑班级所需的表单数据

#### 5.7 获取班级统计信息
- **URL**: `GET /api/v1/classes/stats`
- **响应**: 班级统计数据

#### 5.8 批量更新班级状态
- **URL**: `PUT /api/v1/classes/batch/status`
- **请求体**: 
```json
{
  "ids": [1, 2, 3],
  "status": 1
}
```
- **响应**: 批量更新结果

## 数据模型

### User (用户)
```json
{
  "id": 1,
  "username": "admin",
  "password": "encrypted_password",
  "realName": "管理员",
  "email": "admin@example.com",
  "phone": "13800138000",
  "status": 1,
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Role (角色)
```json
{
  "id": 1,
  "roleName": "ADMIN",
  "roleKey": "admin",
  "description": "系统管理员",
  "status": 1,
  "isSystem": true,
  "createdAt": "2024-01-01T00:00:00"
}
```

### Course (课程)
```json
{
  "id": 1,
  "courseName": "Java程序设计",
  "courseCode": "CS101",
  "courseType": "REQUIRED",
  "credits": 3,
  "hours": 48,
  "semester": "2024-1",
  "status": 1,
  "createdAt": "2024-01-01T00:00:00"
}
```

### Student (学生)
```json
{
  "id": 1,
  "userId": 2,
  "studentNo": "2024001",
  "grade": "2024级",
  "major": "计算机科学与技术",
  "classId": 1,
  "enrollmentYear": 2024,
  "status": 1,
  "createdAt": "2024-01-01T00:00:00"
}
```

### SchoolClass (班级)
```json
{
  "id": 1,
  "className": "计算机2024-1班",
  "classCode": "CS2024-1",
  "grade": "2024级",
  "major": "计算机科学与技术",
  "maxStudents": 50,
  "currentStudents": 45,
  "headTeacherId": 3,
  "status": 1,
  "createdAt": "2024-01-01T00:00:00"
}
```

## 认证和授权

系统使用JWT Token进行认证，需要在请求头中包含：

```
Authorization: Bearer <token>
```

权限控制基于角色（RBAC），主要角色包括：
- `ADMIN`: 系统管理员
- `ACADEMIC_ADMIN`: 教务管理员
- `TEACHER`: 教师
- `STUDENT`: 学生

## 错误处理

API遵循RESTful规范，使用HTTP状态码表示请求结果，错误响应格式：

```json
{
  "code": 400,
  "message": "参数错误：用户名不能为空",
  "timestamp": 1640995200000
}
```
