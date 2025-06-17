# 页面重构计划 - Page Refactor Plan

## 📋 重构概述

按模块逐个重构现有页面，统一使用新的模板继承体系，集成JavaScript组件。

## 🎯 重构目标

1. **统一模板继承** - 所有页面使用 `layout/admin.html` 作为基础模板
2. **组件化设计** - 使用可复用的fragments组件
3. **JavaScript集成** - 集成新开发的JavaScript组件
4. **响应式优化** - 确保所有页面在各种设备上正常显示
5. **权限控制** - 基于角色的页面访问控制

## 📂 重构模块划分

### 模块1: 系统管理 (system/)   
**优先级: 高** | **预计时间: 1天**

- [x] `system/users.html` - 用户管理
- [x] `system/roles.html` - 角色管理  
- [x] `system/permissions.html` - 权限管理
- [x] `system/settings.html` - 系统设置
- [x] `system/notifications.html` - 通知管理
- [x] `system/notification-form.html` - 通知表单
- [x] `system/notification-detail.html` - 通知详情
- [x] `system/activity-log.html` - 活动日志

### 模块2: 学生管理 (students/)
**优先级: 高** | **预计时间: 1天**

- [x] `students/index.html` - 学生列表
- [x] `students/form.html` - 学生表单 (新建)
- [x] `students/detail.html` - 学生详情 (新建)
- [x] `students/import.html` - 批量导入 (新建)

### 模块3: 教务管理 (academic/)
**优先级: 高** | **预计时间: 1天**

- [x] `academic/courses.html` - 课程管理
- [x] `academic/course-form.html` - 课程表单
- [x] `academic/course-detail.html` - 课程详情
- [x] `academic/classes.html` - 班级管理
- [x] `academic/schedules.html` - 课程安排

### 模块4: 财务管理 (finance/)
**优先级: 中** | **预计时间: 1天**

- [x] `finance/payments.html` - 缴费管理
- [x] `finance/payment-records.html` - 缴费记录
- [x] `finance/fee-items.html` - 收费项目
- [x] `finance/reports.html` - 财务报表
- [x] `finance/finance.html` - 财务概览

### 模块5: 人事管理 (staff/ & teachers/)
**优先级: 中** | **预计时间: 0.5天** | **状态: ✅ 已完成**

- [x] `staff/index.html` - 员工管理 ✅ 已重构
- [x] `teachers/index.html` - 教师管理 ✅ 已重构

### 模块6: 其他页面
**优先级: 低** | **预计时间: 0.5天**

- [x] `attendance/index.html` - 考勤管理
- [x] `grades/index.html` - 成绩管理
- [x] `departments/index.html` - 部门管理
- [x] `profile.html` - 个人资料
- [x] `access-denied.html` - 访问拒绝

## 🏗️ 重构模板结构

### 新的页面模板结构
```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org"
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout"
      layout:decorate="~{layout/admin}">

<head>
    <!-- 页面特定的CSS -->
    <th:block layout:fragment="styles">
        <link rel="stylesheet" th:href="@{/css/components/data-table.css}">
        <link rel="stylesheet" th:href="@{/css/admin/module-name.css}">
    </th:block>
</head>

<body>
    <!-- 页面标题 -->
    <th:block layout:fragment="page-title">页面标题</th:block>
    
    <!-- 面包屑导航 -->
    <th:block layout:fragment="breadcrumb">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb">
                <li class="breadcrumb-item"><a th:href="@{/admin/dashboard}">首页</a></li>
                <li class="breadcrumb-item active">当前页面</li>
            </ol>
        </nav>
    </th:block>

    <!-- 页面内容 -->
    <div layout:fragment="content">
        <!-- 页面具体内容 -->
    </div>

    <!-- 页面特定的JavaScript -->
    <th:block layout:fragment="scripts">
        <script th:src="@{/js/components/data-table.js}"></script>
        <script th:src="@{/js/admin/module-name.js}"></script>
    </th:block>
</body>
</html>
```

## 🧩 组件集成计划

### JavaScript组件集成
1. **DataTable组件** - 用于所有列表页面
2. **FormBuilder组件** - 用于表单页面
3. **Toast组件** - 用于消息提示
4. **Dashboard组件** - 用于仪表盘
5. **ThemeSwitcher组件** - 用于主题切换

### CSS组件集成
1. **stats-cards.css** - 统计卡片
2. **data-table.css** - 数据表格
3. **charts.css** - 图表组件
4. **toast.css** - 通知组件
5. **components.css** - 通用组件

## 📋 重构检查清单

### 每个页面重构后需要检查:
- [ ] 使用正确的模板继承
- [ ] 包含必要的CSS和JavaScript文件
- [ ] 响应式设计正常工作
- [ ] 权限控制正确实现
- [ ] 表单验证功能正常
- [ ] 数据表格功能完整
- [ ] 动画效果流畅
- [ ] 主题切换正常
- [ ] 无控制台错误
- [ ] 符合可访问性标准

## 🔄 重构流程

### 单个页面重构步骤:
1. **分析现有页面** - 了解页面功能和结构
2. **设计新结构** - 规划使用的组件和布局
3. **重构HTML** - 使用新的模板继承体系
4. **集成CSS** - 应用新的样式系统
5. **集成JavaScript** - 添加交互功能
6. **测试验证** - 确保功能正常
7. **优化调整** - 性能和用户体验优化

### 模块重构步骤:
1. **模块分析** - 分析模块内页面的共同特点
2. **创建模块样式** - 如需要，创建模块特定的CSS
3. **创建模块脚本** - 如需要，创建模块特定的JavaScript
4. **逐页重构** - 按优先级重构模块内页面
5. **模块测试** - 整体测试模块功能
6. **文档更新** - 更新相关文档

## 📊 进度跟踪

### 重构进度
- [x] **模板继承体系** - 已完成
- [x] **CSS样式系统** - 已完成
- [x] **JavaScript组件** - 已完成
- [x] **系统管理模块** - 已完成
- [ ] **学生管理模块** - 待开始
- [ ] **教务管理模块** - 待开始
- [ ] **财务管理模块** - 待开始
- [x] **人事管理模块** - 已完成 ✅
- [ ] **其他页面** - 待开始

### 质量指标
- **代码复用率**: 目标 >80%
- **页面加载时间**: 目标 <2秒
- **移动端适配**: 目标 100%
- **浏览器兼容**: 目标 Chrome 90+, Firefox 88+, Safari 14+
- **可访问性**: 目标 WCAG 2.1 AA级

## 🚀 预期成果

### 重构完成后的效果:
1. **统一的用户体验** - 所有页面风格一致
2. **更好的性能** - 优化的CSS和JavaScript
3. **更强的可维护性** - 模块化的代码结构
4. **更好的响应式** - 完美适配各种设备
5. **更丰富的交互** - 流畅的动画和反馈
6. **更好的可访问性** - 符合无障碍标准

---

*本计划将根据实际进展动态调整*
