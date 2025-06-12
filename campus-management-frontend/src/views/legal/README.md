# 智慧校园管理系统 - 角色导航组件

本目录包含了智慧校园管理系统的角色导航组件，支持根据用户角色（学生、教师、家长）动态显示不同的导航栏。

## 组件说明

### 1. SideNavigation.vue - 侧边导航栏组件

**功能特性：**
- 根据用户角色动态显示不同的菜单项
- 支持菜单折叠/展开
- 响应式设计，移动端友好
- 用户信息卡片显示
- 角色标识和权限验证

**角色菜单配置：**

#### 学生端菜单
- 首页概览
- 我的课程
- 选课系统
- 成绩查询
- 课程表
- 作业管理
- 考试安排
- 图书馆
- 个人信息

#### 教师端菜单
- 工作台
- 课程管理（支持子菜单）
- 学生管理
- 成绩录入
- 作业管理
- 考试管理
- 课程安排
- 个人信息

#### 家长端菜单
- 家长中心
- 子女信息
- 成绩查看
- 考勤记录
- 家校沟通
- 活动通知
- 缴费记录
- 学习监控
- 个人信息

### 2. TopNavigation.vue - 顶部导航栏组件

**功能特性：**
- 侧边栏切换按钮
- 面包屑导航
- 角色相关的快捷操作按钮
- 全局搜索功能
- 通知消息中心
- 全屏切换
- 用户信息下拉菜单
- 响应式设计

**快捷操作配置：**
- **学生端：** 新作业、选课
- **教师端：** 布置作业、录入成绩、导出数据
- **家长端：** 查看成绩、家校沟通

### 3. RoleBasedLayout.vue - 基于角色的布局组件

**功能特性：**
- 整合侧边栏和顶部栏
- 路由视图管理
- 页面缓存控制
- 通知抽屉
- 全局错误处理
- 移动端适配
- 角色特定样式

## 使用方法

### 1. 基本使用

```vue
<template>
  <RoleBasedLayout>
    <!-- 页面内容将通过 router-view 自动渲染 -->
  </RoleBasedLayout>
</template>

<script setup>
import RoleBasedLayout from '@/views/legal/RoleBasedLayout.vue'
</script>
```

### 2. 在路由中使用

```javascript
// router/index.js
import RoleBasedLayout from '@/views/legal/RoleBasedLayout.vue'

const routes = [
  {
    path: '/student',
    component: RoleBasedLayout,
    meta: { requiresAuth: true, roles: ['STUDENT'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/student/Dashboard.vue'),
        meta: { title: '学生首页', icon: 'House' }
      }
      // 其他学生页面...
    ]
  },
  {
    path: '/teacher',
    component: RoleBasedLayout,
    meta: { requiresAuth: true, roles: ['TEACHER'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/teacher/Dashboard.vue'),
        meta: { title: '教师工作台', icon: 'House' }
      }
      // 其他教师页面...
    ]
  },
  {
    path: '/parent',
    component: RoleBasedLayout,
    meta: { requiresAuth: true, roles: ['PARENT'] },
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/parent/Dashboard.vue'),
        meta: { title: '家长中心', icon: 'House' }
      }
      // 其他家长页面...
    ]
  }
]
```

### 3. 单独使用组件

```vue
<template>
  <div class="custom-layout">
    <!-- 单独使用侧边导航栏 -->
    <SideNavigation
      :collapsed="sidebarCollapsed"
      @update:collapsed="handleSidebarCollapse"
      @menu-select="handleMenuSelect"
    />
    
    <!-- 单独使用顶部导航栏 -->
    <TopNavigation
      :sidebar-collapsed="sidebarCollapsed"
      @toggle-sidebar="toggleSidebar"
      @search="handleSearch"
      @notification-click="handleNotificationClick"
    />
  </div>
</template>

<script setup>
import SideNavigation from '@/views/legal/SideNavigation.vue'
import TopNavigation from '@/views/legal/TopNavigation.vue'

// 组件逻辑...
</script>
```

## 权限验证

组件会自动与认证系统集成，通过 `useAuthStore` 获取用户角色信息：

```javascript
// 在组件中自动获取用户角色
const authStore = useAuthStore()
const userRole = computed(() => authStore.userRole)

// 根据角色显示不同的菜单
const currentMenuItems = computed(() => {
  const menuMap = {
    'STUDENT': studentMenuItems,
    'TEACHER': teacherMenuItems,
    'PARENT': parentMenuItems
  }
  return menuMap[userRole.value] || []
})
```

## 样式定制

### 1. 角色特定颜色

组件支持根据用户角色应用不同的主题色：

```css
.role-based-layout.role-student {
  --primary-color: #22c55e; /* 绿色 - 学生 */
}

.role-based-layout.role-teacher {
  --primary-color: #3b82f6; /* 蓝色 - 教师 */
}

.role-based-layout.role-parent {
  --primary-color: #a855f7; /* 紫色 - 家长 */
}
```

### 2. 响应式断点

```css
/* 移动端适配 */
@media (max-width: 768px) {
  .side-navigation {
    position: fixed;
    transform: translateX(-100%);
  }
  
  .side-navigation.mobile-open {
    transform: translateX(0);
  }
}
```

## 注意事项

1. **权限验证：** 确保在使用前用户已通过认证，组件会自动检查用户角色
2. **路由配置：** 需要在路由的 meta 中配置 title 和 icon 以支持面包屑导航
3. **移动端：** 在移动端会自动切换为抽屉式侧边栏
4. **缓存控制：** 可通过 keepAliveComponents 属性控制页面缓存
5. **错误处理：** 组件内置了全局错误处理机制

## 扩展功能

### 1. 添加新的菜单项

在对应的菜单配置数组中添加新项：

```javascript
const studentMenuItems = [
  // 现有菜单项...
  { path: '/student/new-feature', title: '新功能', icon: 'Star' }
]
```

### 2. 自定义快捷操作

在 TopNavigation 组件中的 quickActions 计算属性中添加：

```javascript
const quickActions = computed(() => {
  const actionMap = {
    'STUDENT': [
      // 现有操作...
      { key: 'custom-action', title: '自定义操作', icon: 'Plus', tooltip: '自定义功能' }
    ]
  }
  return actionMap[userRole.value] || []
})
```

### 3. 添加新的通知类型

在 notifications 数据中添加新的通知项，支持不同的图标和样式。

## 技术栈

- **Vue 3** - 组合式 API
- **Element Plus** - UI 组件库
- **Vue Router** - 路由管理
- **Pinia** - 状态管理
- **CSS3** - 样式和动画

## 安装和配置

### 1. 文件结构

确保以下文件已正确放置在项目中：

```
src/views/legal/
├── SideNavigation.vue          # 侧边导航栏组件
├── TopNavigation.vue           # 顶部导航栏组件
├── RoleBasedLayout.vue         # 基于角色的布局组件
├── navigationConfig.js         # 导航配置文件
├── router-integration-example.js  # 路由集成示例
├── ExampleUsage.vue           # 使用示例组件
└── README.md                  # 说明文档
```

### 2. 依赖检查

确保项目已安装以下依赖：

```json
{
  "dependencies": {
    "vue": "^3.3.0",
    "vue-router": "^4.2.0",
    "element-plus": "^2.3.0",
    "pinia": "^2.1.0",
    "@element-plus/icons-vue": "^2.1.0"
  }
}
```

### 3. 认证Store配置

确保 `@/stores/auth.js` 包含以下必要的状态和方法：

```javascript
// stores/auth.js
export const useAuthStore = defineStore('auth', () => {
  const userInfo = ref({})
  const userRole = ref('')
  const permissions = ref([])
  const isAuthenticated = computed(() => !!userInfo.value.id)

  // 其他认证相关方法...

  return {
    userInfo,
    userRole,
    permissions,
    isAuthenticated,
    // 其他方法...
  }
})
```

### 4. 路由配置

在 `router/index.js` 中集成角色导航：

```javascript
import RoleBasedLayout from '@/views/legal/RoleBasedLayout.vue'

const routes = [
  // 使用角色布局的路由
  {
    path: '/student',
    component: RoleBasedLayout,
    meta: { requiresAuth: true, roles: ['STUDENT'] },
    children: [
      // 学生页面路由...
    ]
  }
  // 其他路由...
]
```

## 快速开始

### 1. 基本集成

在主布局中使用角色导航组件：

```vue
<template>
  <RoleBasedLayout>
    <!-- 页面内容会通过 router-view 自动渲染 -->
  </RoleBasedLayout>
</template>

<script setup>
import RoleBasedLayout from '@/views/legal/RoleBasedLayout.vue'
</script>
```

### 2. 自定义配置

修改 `navigationConfig.js` 来自定义菜单和权限：

```javascript
// 添加新的菜单项
export const STUDENT_MENU_ITEMS = [
  // 现有菜单...
  {
    path: '/student/new-feature',
    title: '新功能',
    icon: 'Star',
    permission: 'student:new-feature:view',
    meta: { title: '新功能' }
  }
]
```

### 3. 权限验证

在页面组件中检查权限：

```vue
<script setup>
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { hasPermission } from '@/views/legal/navigationConfig.js'

const authStore = useAuthStore()

const canEdit = computed(() => {
  return hasPermission(authStore.permissions, 'student:assignments:edit')
})
</script>
```

## 故障排除

### 常见问题

1. **菜单不显示**
   - 检查用户是否已登录
   - 验证用户角色是否正确
   - 确认权限配置是否正确

2. **路由跳转失败**
   - 检查路由配置是否正确
   - 验证路由守卫是否正常工作
   - 确认目标页面组件是否存在

3. **样式显示异常**
   - 检查Element Plus是否正确安装
   - 确认图标组件是否正确导入
   - 验证CSS样式是否冲突

### 调试技巧

1. **开启调试模式**

```javascript
// 在组件中添加调试信息
console.log('当前用户角色:', authStore.userRole)
console.log('用户权限:', authStore.permissions)
console.log('当前菜单:', currentMenuItems.value)
```

2. **检查权限验证**

```javascript
// 在navigationConfig.js中添加调试
export function hasPermission(userPermissions, requiredPermission) {
  console.log('权限检查:', { userPermissions, requiredPermission })
  // 权限验证逻辑...
}
```

## 性能优化

### 1. 组件懒加载

```javascript
// 使用动态导入优化页面加载
const StudentDashboard = () => import('@/views/student/Dashboard.vue')
```

### 2. 菜单缓存

```javascript
// 在navigationConfig.js中缓存菜单配置
const menuCache = new Map()

export function getMenuItemsByRole(role) {
  if (menuCache.has(role)) {
    return menuCache.get(role)
  }

  const menu = generateMenuByRole(role)
  menuCache.set(role, menu)
  return menu
}
```

### 3. 权限预加载

```javascript
// 在用户登录时预加载权限信息
const authStore = useAuthStore()
await authStore.loadUserPermissions()
```

## 更新日志

- **v1.0.0** - 初始版本，支持学生、教师、家长三种角色的导航
- 支持响应式设计和移动端适配
- 集成权限验证和角色管理
- 提供完整的通知和搜索功能
- 包含配置文件和路由集成示例
- 添加完整的文档和故障排除指南
