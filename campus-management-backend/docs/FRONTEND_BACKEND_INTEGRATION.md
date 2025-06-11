# 前端页面与后端API集成说明

## 概述

本文档说明了智慧校园管理系统前端页面与后端API的集成情况，确保前端JavaScript代码与后端REST API接口完全匹配。

## 集成状态

### ✅ 已完成集成的模块

#### 1. 用户管理模块
- **前端页面**: `/admin/users/index.html`
- **JavaScript**: `/js/pages/users.js`
- **后端API**: `/api/v1/users/*`
- **集成状态**: ✅ 完成
- **主要功能**:
  - 用户列表分页查询
  - 用户创建、编辑、删除
  - 批量操作
  - 密码重置
  - 状态切换

#### 2. 角色管理模块
- **前端页面**: `/admin/system/roles.html`
- **JavaScript**: `/js/pages/roles.js` (新创建)
- **后端API**: `/api/v1/roles/*`
- **集成状态**: ✅ 完成
- **主要功能**:
  - 角色列表分页查询
  - 角色创建、编辑、删除
  - 权限配置（UI已准备，后端待完善）
  - 批量状态更新

#### 3. 课程管理模块
- **前端页面**: `/admin/courses/index.html`
- **JavaScript**: `/js/pages/courses.js`
- **后端API**: `/api/v1/courses/*`
- **集成状态**: ✅ 完成
- **主要功能**:
  - 课程列表分页查询
  - 课程创建、编辑、删除
  - 批量操作
  - 课程统计

#### 4. 学生管理模块
- **前端页面**: `/admin/students/index.html`
- **JavaScript**: `/js/pages/students.js`
- **后端API**: `/api/v1/students/*`
- **集成状态**: ✅ 完成
- **主要功能**:
  - 学生列表分页查询
  - 学生创建、编辑、删除
  - 批量导入
  - 级联选择（年级-专业-班级）

#### 5. 班级管理模块
- **前端页面**: `/admin/academic/classes.html`
- **JavaScript**: `/js/modules/class-management.js`
- **后端API**: `/api/v1/classes/*`
- **集成状态**: ✅ 完成
- **主要功能**:
  - 班级列表分页查询
  - 班级创建、编辑、删除
  - 批量状态更新
  - 班级统计

## API响应格式统一

所有模块都使用统一的API响应格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1640995200000
}
```

前端JavaScript统一处理响应：

```javascript
success: function(response) {
    if (response.code === 200) {
        // 成功处理
        showSuccess(response.message);
    } else {
        // 错误处理
        showError(response.message || '操作失败');
    }
}
```

## 分页查询统一

### 请求参数
- `page`: 页码（从1开始，后端转换为从0开始）
- `size`: 每页大小
- 其他筛选参数

### 响应格式
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": []
  }
}
```

## 表单数据处理

### 创建/编辑表单
1. 前端收集表单数据
2. 数据类型转换（字符串转数字等）
3. 发送JSON格式到后端
4. 统一错误处理和成功提示

### 级联选择
- 学生管理：年级 → 专业 → 班级
- 课程管理：院系 → 教师
- 使用 `/form-data` 接口获取选项数据

## 批量操作

### 统一流程
1. 复选框选择
2. 验证选择数量
3. 确认操作
4. 发送批量请求
5. 显示操作结果

### API格式
```javascript
// 批量删除
DELETE /api/v1/{module}/batch
Body: [1, 2, 3]

// 批量状态更新
PUT /api/v1/{module}/batch/status
Body: {
  "ids": [1, 2, 3],
  "status": 1
}
```

## 统计数据

每个模块都有统计接口：
- `/api/v1/users/stats`
- `/api/v1/roles/statistics`
- `/api/v1/courses/stats`
- `/api/v1/students/stats`
- `/api/v1/classes/stats`

前端统一更新统计卡片显示。

## 错误处理

### 统一错误处理函数
```javascript
function showError(message) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({ icon: 'error', title: '错误', text: message });
    } else { 
        alert(message); 
    }
}
```

### 常见错误码处理
- `400`: 参数错误
- `401`: 未授权
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器错误

## 权限控制

### 后端权限注解
```java
@PreAuthorize("hasAnyRole('ADMIN', 'ACADEMIC_ADMIN')")
```

### 前端权限控制
- 基于用户角色显示/隐藏功能按钮
- 页面级权限控制
- 操作级权限验证

## 数据验证

### 前端验证
- 必填字段验证
- 格式验证（邮箱、手机号等）
- 长度限制

### 后端验证
- 使用Bean Validation注解
- 自定义验证逻辑
- 统一异常处理

## 文件上传

### 学生批量导入
- 前端：文件选择 + FormData上传
- 后端：MultipartFile处理
- 进度显示和结果反馈

## 导出功能

### 统一导出流程
1. 构建查询参数
2. 创建下载链接
3. 触发浏览器下载
4. 显示导出提示

```javascript
function exportData() {
    const searchParams = new URLSearchParams($('#searchForm').serialize());
    const exportUrl = '/api/v1/{module}/export?' + searchParams.toString();
    
    const link = document.createElement('a');
    link.href = exportUrl;
    link.download = `{module}_${new Date().toISOString().split('T')[0]}.xlsx`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}
```

## 待完善功能

### 🔄 需要进一步开发的功能

1. **权限管理**
   - 权限树形结构显示
   - 角色权限分配
   - 动态权限验证

2. **文件上传安全**
   - 文件类型验证
   - 文件大小限制
   - 病毒扫描

3. **数据导入导出**
   - Excel模板下载
   - 导入数据验证
   - 导出格式优化

4. **实时通知**
   - WebSocket集成
   - 操作日志
   - 系统通知

## 测试建议

### 前端测试
1. 功能测试：所有CRUD操作
2. 兼容性测试：不同浏览器
3. 响应式测试：不同屏幕尺寸
4. 性能测试：大数据量处理

### 集成测试
1. API接口测试
2. 权限验证测试
3. 数据一致性测试
4. 错误处理测试

### 用户体验测试
1. 操作流程测试
2. 错误提示测试
3. 加载状态测试
4. 响应时间测试

## 部署注意事项

1. **静态资源**：确保CSS、JS文件正确加载
2. **API路径**：生产环境API地址配置
3. **权限配置**：CORS设置和安全配置
4. **缓存策略**：静态资源缓存配置

## 总结

前端页面与后端API已实现基本集成，主要功能模块都能正常工作。系统采用了统一的架构设计和编码规范，便于维护和扩展。后续可以根据实际需求继续完善权限管理、文件处理等高级功能。
