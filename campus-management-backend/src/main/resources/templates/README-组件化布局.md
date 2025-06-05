# 智慧校园管理系统 - 组件化布局使用说明

## 概述

本项目已将HTML模板进行了组件化重构，将左侧导航栏和顶部导航栏提取为独立的可复用组件，并将内联样式分离到独立的CSS文件中。

## 文件结构

```
templates/
├── components/                 # 组件目录
│   ├── sidebar.html           # 左侧导航栏组件
│   └── topbar.html            # 顶部导航栏组件
├── layout/                    # 布局目录
│   ├── base.html              # 基础布局模板
│   └── admin.html             # 管理后台布局模板
└── admin/                     # 管理页面
    ├── dashboard.html         # 仪表盘页面（已重构示例）
    ├── classes.html
    ├── courses.html
    └── ...

static/
└── css/
    └── admin.css              # 管理后台样式文件
```

## 组件说明

### 1. 左侧导航栏组件 (`components/sidebar.html`)

**功能特性：**
- 响应式设计，移动端自动收起
- 支持菜单项高亮显示
- 分组显示（系统管理、教务管理、财务管理等）
- 包含侧边栏切换JavaScript功能

**使用方法：**
```html
<!-- 引入左侧导航栏 -->
<th:block th:replace="~{components/sidebar :: sidebar}"></th:block>

<!-- 引入侧边栏脚本 -->
<th:block th:replace="~{components/sidebar :: sidebar-script}"></th:block>
```

**菜单项配置：**
- 通过 `currentPage` 变量控制当前页面高亮
- 菜单链接使用 Thymeleaf 的 `@{/admin/...}` 语法

### 2. 顶部导航栏组件 (`components/topbar.html`)

**功能特性：**
- 用户信息下拉菜单
- 通知和消息中心
- 退出登录确认模态框
- 响应式设计

**使用方法：**
```html
<!-- 引入顶部导航栏 -->
<th:block th:replace="~{components/topbar :: topbar}"></th:block>

<!-- 引入退出登录模态框 -->
<th:block th:replace="~{components/topbar :: logout-modal}"></th:block>

<!-- 引入顶部导航栏样式 -->
<th:block th:replace="~{components/topbar :: topbar-style}"></th:block>
```

## 布局模板使用

### 方式一：使用基础布局模板 (`layout/base.html`)

适用于需要完全自定义页面结构的情况：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head>
    <!-- 基础head内容 -->
    <th:block th:replace="~{layout/base :: head}"></th:block>
    
    <!-- 页面特定的head内容 -->
    <title>页面标题</title>
</head>
<body>
    <!-- 使用基础布局 -->
    <th:block th:replace="~{layout/base :: body}">
        <!-- 页面内容将替换这里 -->
        <div th:fragment="content">
            <!-- 你的页面内容 -->
        </div>
    </th:block>
</body>
</html>
```

### 方式二：使用管理后台布局模板 (`layout/admin.html`)

适用于标准的管理页面：

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>页面标题 - 智慧校园管理系统</title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <!-- 自定义CSS -->
    <link th:href="@{/css/admin.css}" rel="stylesheet">
    
    <!-- 引入组件样式 -->
    <th:block th:replace="~{components/topbar :: topbar-style}"></th:block>
</head>
<body>
    <!-- 布局容器 -->
    <div class="wrapper">
        <!-- 左侧导航栏 -->
        <th:block th:replace="~{components/sidebar :: sidebar}"></th:block>

        <!-- 主内容区域 -->
        <div class="content" id="content">
            <!-- 顶部导航栏 -->
            <th:block th:replace="~{components/topbar :: topbar}"></th:block>

            <!-- 页面内容包装器 -->
            <div class="content-wrapper">
                <!-- 页面标题 -->
                <h1 class="h3 mb-4 text-gray-800">页面标题</h1>

                <!-- 你的页面内容开始 -->
                <div class="row">
                    <!-- 页面具体内容 -->
                </div>
                <!-- 你的页面内容结束 -->
            </div>
        </div>
    </div>

    <!-- 退出登录确认模态框 -->
    <th:block th:replace="~{components/topbar :: logout-modal}"></th:block>

    <!-- 脚本部分 -->
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <!-- jQuery -->
    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <!-- 退出登录确认模态框 -->
    <th:block th:replace="~{components/topbar :: logout-modal}"></th:block>
    <!-- 引入侧边栏脚本 -->
    <th:block th:replace="~{components/sidebar :: sidebar-script}"></th:block>

    <!-- 页面特定的JavaScript -->
    <script>
        // 设置当前页面标识
        document.addEventListener('DOMContentLoaded', function() {
            document.body.setAttribute('data-current-page', '页面标识');
        });
    </script>
</body>
</html>
```

## 样式文件说明

### CSS变量定义
```css
:root {
    --primary-color: #4e73df;
    --secondary-color: #858796;
    --success-color: #1cc88a;
    --info-color: #36b9cc;
    --warning-color: #f6c23e;
    --danger-color: #e74a3b;
    --light-color: #f8f9fc;
    --dark-color: #5a5c69;
    --sidebar-width: 250px;
    --sidebar-collapsed-width: 80px;
}
```

### 主要CSS类
- `.wrapper` - 主布局容器
- `.sidebar` - 左侧导航栏
- `.content` - 主内容区域
- `.content-wrapper` - 内容包装器
- `.topbar` - 顶部导航栏

## 迁移现有页面

要将现有页面迁移到新的组件化布局，请按以下步骤操作：

1. **替换页面头部**：
   - 移除原有的布局引用
   - 添加新的CSS和组件引用

2. **重构页面结构**：
   - 使用新的布局容器结构
   - 引入导航栏组件

3. **移除内联样式**：
   - 将页面特定的样式移到CSS文件或style标签中

4. **更新JavaScript**：
   - 引入组件脚本
   - 设置正确的页面标识

## 注意事项

1. **页面标识**：确保在每个页面的JavaScript中设置正确的 `data-current-page` 属性，用于导航菜单高亮显示。

2. **响应式设计**：新布局已包含响应式设计，在移动端会自动调整布局。

3. **浏览器兼容性**：使用了现代CSS特性，建议在现代浏览器中使用。

4. **性能优化**：CSS文件已分离，可以被浏览器缓存，提高页面加载性能。

## 扩展和自定义

### 添加新的导航菜单项
在 `components/sidebar.html` 中添加新的菜单项：

```html
<li th:classappend="${currentPage == 'new-page'} ? 'active'">
    <a th:href="@{/admin/new-page}">
        <i class="fas fa-new-icon"></i>
        <span>新页面</span>
    </a>
</li>
```

### 自定义样式
在 `static/css/admin.css` 中添加或修改样式，或在页面中添加 `<style>` 标签。

### 添加新的组件
在 `templates/components/` 目录下创建新的组件文件，按照现有组件的结构编写。
