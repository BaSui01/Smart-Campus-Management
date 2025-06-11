# 作业管理系统使用指南

## 概述

作业管理系统为学生提供了完整的作业查看、提交和管理功能。系统包含以下主要功能：

- 作业列表查看和筛选
- 作业详情展示
- 在线作业提交
- 文件上传和下载
- 提交历史查看
- 成绩和反馈查看

## 功能模块

### 1. 作业列表页面 (`Assignments.vue`)

**主要功能：**
- 展示学生所有作业的列表
- 支持按状态、课程筛选作业
- 显示作业统计信息
- 提供作业操作入口

**状态说明：**
- `待提交` - 作业已发布，学生尚未提交
- `已提交` - 学生已提交作业，等待批改
- `已批改` - 教师已完成批改，显示成绩和反馈
- `已过期` - 超过截止时间的作业

**操作说明：**
- 点击作业卡片查看详情
- 点击"提交作业"按钮进行作业提交
- 点击"提交历史"查看历史记录

### 2. 作业详情组件 (`AssignmentDetail.vue`)

**显示内容：**
- 作业基本信息（标题、课程、教师、截止时间等）
- 作业要求和描述
- 作业附件下载
- 学生提交内容
- 教师反馈和评分

**交互功能：**
- 下载作业附件
- 查看提交内容
- 发起重新提交

### 3. 作业提交组件 (`AssignmentSubmission.vue`)

**提交方式：**
- **在线提交** - 直接在文本框中输入作业内容
- **文件上传** - 上传作业文件（支持多种格式）
- **混合提交** - 同时支持文本和文件提交

**支持的文件格式：**
- 文档类：`.doc`, `.docx`, `.pdf`, `.txt`
- 压缩包：`.zip`, `.rar`
- 图片类：`.jpg`, `.jpeg`, `.png`, `.gif`

**注意事项：**
- 单个文件大小限制：50MB
- 支持多文件上传
- 可预览部分文件类型

### 4. 提交历史组件 (`SubmissionHistory.vue`)

**功能特点：**
- 时间线方式展示所有提交记录
- 显示每次提交的内容和文件
- 展示教师反馈和评分
- 支持下载历史提交的文件

### 5. 文件预览组件 (`FilePreview.vue`)

**支持预览的文件类型：**
- PDF文档（iframe预览）
- 图片文件（直接显示）
- 文本文件（内容展示）

**不支持预览的文件：**
- 提供下载功能
- 显示文件基本信息

## API 接口使用

### 主要接口

```javascript
// 获取学生作业列表
assignmentApi.getStudentAssignments(params)

// 获取作业详情
assignmentApi.getStudentAssignmentDetail(assignmentId)

// 提交作业
assignmentApi.submitAssignment(assignmentId, data)

// 上传文件
assignmentApi.uploadSubmissionFile(file, assignmentId, onProgress)

// 获取提交历史
assignmentApi.getSubmissionHistory(assignmentId)
```

### 数据格式

**作业对象结构：**
```javascript
{
  id: 1,
  title: "作业标题",
  description: "作业描述",
  courseName: "课程名称",
  teacherName: "教师姓名",
  dueDate: "2024-01-01T23:59:59",
  maxScore: 100,
  status: "pending", // pending, submitted, graded
  submissionType: "both", // online, file, both
  allowResubmit: true,
  submission: { /* 提交信息 */ }
}
```

## 样式系统

### CSS 类命名规范

- `.assignment-*` - 作业相关样式
- `.submission-*` - 提交相关样式
- `.file-*` - 文件相关样式
- `.status-*` - 状态相关样式

### 主题色彩

- 主色调：`#409eff` (蓝色)
- 成功色：`#67c23a` (绿色)
- 警告色：`#e6a23c` (橙色)
- 危险色：`#f56c6c` (红色)

### 响应式断点

- 桌面端：`> 768px`
- 平板端：`768px - 480px`
- 手机端：`< 480px`

## 开发注意事项

### 1. 组件依赖

所有组件都依赖以下库：
- Vue 3 Composition API
- Element Plus UI 框架
- 自定义的 assignment API

### 2. 错误处理

- 网络请求失败会显示错误消息
- 文件上传失败会提供重试机制
- 表单验证失败会显示具体错误信息

### 3. 性能优化

- 使用虚拟滚动处理大量作业列表
- 图片懒加载减少初始加载时间
- 文件分块上传支持大文件

### 4. 安全考虑

- 文件类型白名单验证
- 文件大小限制
- XSS 防护（内容过滤）

## 测试建议

### 功能测试

1. **作业列表测试**
   - 验证筛选功能
   - 测试分页加载
   - 检查状态显示

2. **作业提交测试**
   - 测试各种文件格式上传
   - 验证表单验证规则
   - 测试网络异常情况

3. **文件操作测试**
   - 测试文件预览功能
   - 验证下载功能
   - 检查大文件处理

### 兼容性测试

- 现代浏览器兼容性（Chrome, Firefox, Safari, Edge）
- 移动端响应式布局
- 不同屏幕尺寸适配

## 部署配置

### 环境变量

```bash
# API 基础路径
VITE_API_BASE_URL=http://localhost:8080/api

# 文件上传路径
VITE_UPLOAD_URL=http://localhost:8080/api/v1/assignments/upload

# 最大文件大小 (字节)
VITE_MAX_FILE_SIZE=52428800
```

### Nginx 配置

```nginx
# 文件上传大小限制
client_max_body_size 50M;

# 静态文件缓存
location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
    expires 1y;
    add_header Cache-Control "public, immutable";
}
```

## 后续优化方向

1. **功能增强**
   - 支持协作作业
   - 添加作业评论功能
   - 实现作业模板系统

2. **用户体验**
   - 添加拖拽上传
   - 实现离线缓存
   - 优化加载动画

3. **技术升级**
   - 引入 WebRTC 实现实时协作
   - 使用 Service Worker 提升性能
   - 集成 AI 辅助功能

## 联系支持

如有问题或建议，请联系开发团队或查看项目文档。