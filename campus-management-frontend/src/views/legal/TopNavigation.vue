<template>
  <div class="top-navigation" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <!-- 左侧区域 -->
    <div class="nav-left">
      <!-- 侧边栏切换按钮 -->
      <el-button
        type="text"
        class="sidebar-toggle"
        @click="toggleSidebar"
      >
        <el-icon size="20">
          <component :is="sidebarCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </el-button>

      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item
          v-for="breadcrumb in breadcrumbs"
          :key="breadcrumb.path"
          :to="breadcrumb.path"
          class="breadcrumb-item"
        >
          <el-icon v-if="breadcrumb.icon" class="breadcrumb-icon">
            <component :is="breadcrumb.icon" />
          </el-icon>
          {{ breadcrumb.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 中间区域 - 快捷操作 -->
    <div class="nav-center">
      <div class="quick-actions" v-if="quickActions.length > 0">
        <el-tooltip
          v-for="action in quickActions"
          :key="action.key"
          :content="action.tooltip"
          placement="bottom"
        >
          <el-button
            :type="action.type || 'text'"
            :class="['quick-action-btn', action.class]"
            @click="handleQuickAction(action)"
          >
            <el-icon>
              <component :is="action.icon" />
            </el-icon>
            <span v-if="!isCompact">{{ action.title }}</span>
          </el-button>
        </el-tooltip>
      </div>
    </div>

    <!-- 右侧区域 -->
    <div class="nav-right">
      <!-- 搜索框 -->
      <div class="search-box" v-if="showSearch">
        <el-input
          v-model="searchQuery"
          placeholder="搜索功能、内容..."
          class="search-input"
          @keyup.enter="handleSearch"
          @focus="showSearchSuggestions = true"
          @blur="hideSearchSuggestions"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <!-- 搜索建议 -->
        <div v-if="showSearchSuggestions && searchSuggestions.length > 0" class="search-suggestions">
          <div
            v-for="suggestion in searchSuggestions"
            :key="suggestion.key"
            class="suggestion-item"
            @click="selectSuggestion(suggestion)"
          >
            <el-icon class="suggestion-icon">
              <component :is="suggestion.icon" />
            </el-icon>
            <span class="suggestion-text">{{ suggestion.title }}</span>
            <el-tag size="small" :type="suggestion.type">{{ suggestion.category }}</el-tag>
          </div>
        </div>
      </div>

      <!-- 工具按钮组 -->
      <div class="tool-buttons">
        <!-- 搜索切换按钮 -->
        <el-tooltip content="搜索" placement="bottom">
          <el-button
            circle
            class="tool-btn search-toggle"
            @click="toggleSearch"
          >
            <el-icon><Search /></el-icon>
          </el-button>
        </el-tooltip>

        <!-- 通知按钮 -->
        <el-tooltip content="通知消息" placement="bottom">
          <el-badge :value="notificationCount" :hidden="notificationCount === 0" class="notification-badge">
            <el-button
              circle
              class="tool-btn notification-btn"
              @click="showNotifications"
            >
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-badge>
        </el-tooltip>

        <!-- 全屏切换 -->
        <el-tooltip content="全屏" placement="bottom">
          <el-button
            circle
            class="tool-btn fullscreen-btn"
            @click="toggleFullscreen"
          >
            <el-icon>
              <component :is="isFullscreen ? 'Aim' : 'FullScreen'" />
            </el-icon>
          </el-button>
        </el-tooltip>
      </div>

      <!-- 用户信息下拉菜单 -->
      <el-dropdown
        @command="handleUserAction"
        trigger="click"
        class="user-dropdown"
      >
        <div class="user-info">
          <el-avatar
            :size="36"
            :src="userInfo?.avatar"
            class="user-avatar"
          >
            <el-icon><User /></el-icon>
          </el-avatar>
          <div class="user-details" v-if="!isCompact">
            <div class="user-name">{{ userInfo?.name || '用户' }}</div>
            <div class="user-role">{{ roleDisplayName }}</div>
          </div>
          <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
        </div>

        <template #dropdown>
          <el-dropdown-menu class="user-dropdown-menu">
            <el-dropdown-item command="profile" class="dropdown-item">
              <el-icon><User /></el-icon>
              <span>个人信息</span>
            </el-dropdown-item>
            <el-dropdown-item command="settings" class="dropdown-item">
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </el-dropdown-item>
            <el-dropdown-item command="theme" class="dropdown-item">
              <el-icon><Sunny /></el-icon>
              <span>主题切换</span>
            </el-dropdown-item>
            <el-dropdown-item divided command="logout" class="dropdown-item logout-item">
              <el-icon><SwitchButton /></el-icon>
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Fold,
  Expand,
  Search,
  Bell,
  FullScreen,
  Aim,
  User,
  ArrowDown,
  Setting,
  Sunny,
  SwitchButton,
  House,
  Document,
  Edit,
  Plus,
  Download,
  Upload
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

// 组件属性
const props = defineProps({
  sidebarCollapsed: {
    type: Boolean,
    default: false
  }
})

// 组件事件
const emit = defineEmits(['toggle-sidebar', 'search', 'notification-click'])

// 响应式数据
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const searchQuery = ref('')
const showSearch = ref(false)
const showSearchSuggestions = ref(false)
const isFullscreen = ref(false)
const notificationCount = ref(3) // 示例通知数量
const isCompact = ref(false) // 紧凑模式

// 计算属性
const userInfo = computed(() => authStore.userInfo)
const userRole = computed(() => authStore.userRole)

// 角色显示名称
const roleDisplayName = computed(() => {
  const roleMap = {
    'STUDENT': '学生',
    'TEACHER': '教师',
    'PARENT': '家长',
    'ADMIN': '管理员'
  }
  return roleMap[userRole.value] || '用户'
})

// 面包屑导航
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  const breadcrumbList = matched.map(item => ({
    title: item.meta.title,
    path: item.path,
    icon: item.meta.icon
  }))
  
  // 添加首页
  if (breadcrumbList.length > 0 && breadcrumbList[0].path !== '/') {
    breadcrumbList.unshift({
      title: '首页',
      path: getRoleHomePath(),
      icon: 'House'
    })
  }
  
  return breadcrumbList
})

// 根据角色获取首页路径
const getRoleHomePath = () => {
  const rolePathMap = {
    'STUDENT': '/student/dashboard',
    'TEACHER': '/teacher/dashboard',
    'PARENT': '/parent/dashboard',
    'ADMIN': '/admin/dashboard'
  }
  return rolePathMap[userRole.value] || '/dashboard'
}

// 快捷操作按钮配置
const quickActions = computed(() => {
  const actionMap = {
    'STUDENT': [
      { key: 'new-assignment', title: '新作业', icon: 'Edit', tooltip: '查看新作业', type: 'primary' },
      { key: 'course-selection', title: '选课', icon: 'Plus', tooltip: '课程选择', type: 'success' }
    ],
    'TEACHER': [
      { key: 'create-assignment', title: '布置作业', icon: 'Edit', tooltip: '创建新作业', type: 'primary' },
      { key: 'grade-entry', title: '录入成绩', icon: 'Document', tooltip: '成绩录入', type: 'warning' },
      { key: 'export-data', title: '导出', icon: 'Download', tooltip: '导出数据', type: 'info' }
    ],
    'PARENT': [
      { key: 'view-grades', title: '查看成绩', icon: 'Document', tooltip: '查看子女成绩', type: 'primary' },
      { key: 'communication', title: '家校沟通', icon: 'Bell', tooltip: '与老师沟通', type: 'success' }
    ]
  }
  return actionMap[userRole.value] || []
})

// 搜索建议
const searchSuggestions = computed(() => {
  if (!searchQuery.value) return []
  
  const suggestions = [
    { key: 'courses', title: '课程管理', icon: 'Reading', category: '功能', type: 'primary' },
    { key: 'grades', title: '成绩查询', icon: 'Promotion', category: '功能', type: 'success' },
    { key: 'profile', title: '个人信息', icon: 'User', category: '设置', type: 'info' },
    { key: 'schedule', title: '课程表', icon: 'Calendar', category: '功能', type: 'warning' }
  ]
  
  return suggestions.filter(item =>
    item.title.toLowerCase().includes(searchQuery.value.toLowerCase())
  ).slice(0, 5)
})

// 方法
const toggleSidebar = () => {
  emit('toggle-sidebar')
}

const toggleSearch = () => {
  showSearch.value = !showSearch.value
  if (!showSearch.value) {
    searchQuery.value = ''
    showSearchSuggestions.value = false
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    emit('search', searchQuery.value)
    ElMessage.success(`搜索: ${searchQuery.value}`)
    showSearchSuggestions.value = false
  }
}

const selectSuggestion = (suggestion) => {
  searchQuery.value = suggestion.title
  showSearchSuggestions.value = false
  handleSearch()
}

const hideSearchSuggestions = () => {
  setTimeout(() => {
    showSearchSuggestions.value = false
  }, 200)
}

const handleQuickAction = (action) => {
  ElMessage.info(`执行快捷操作: ${action.title}`)
  // 这里可以根据action.key执行具体的操作
}

const showNotifications = () => {
  emit('notification-click')
  ElMessage.info('通知功能开发中...')
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    document.exitFullscreen()
    isFullscreen.value = false
  }
}

const handleUserAction = async (command) => {
  switch (command) {
    case 'profile':
      const rolePath = userRole.value.toLowerCase()
      router.push(`/${rolePath}/profile`)
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'theme':
      ElMessage.info('主题切换功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await authStore.logout()
        ElMessage.success('已退出登录')
        router.push('/login')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}

// 监听窗口大小变化
const handleResize = () => {
  isCompact.value = window.innerWidth < 768
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 生命周期
onMounted(() => {
  window.addEventListener('resize', handleResize)
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  handleResize() // 初始化检查
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
/* 顶部导航栏样式 */
.top-navigation {
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  position: sticky;
  top: 0;
  z-index: 100;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.top-navigation.sidebar-collapsed {
  margin-left: 0;
}

/* 左侧区域 */
.nav-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  min-width: 0;
}

.sidebar-toggle {
  padding: 8px;
  color: #64748b;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.sidebar-toggle:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.breadcrumb {
  flex: 1;
  min-width: 0;
}

.breadcrumb-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #64748b;
}

.breadcrumb-icon {
  font-size: 16px;
}

/* 中间区域 */
.nav-center {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
  margin: 0 24px;
}

.quick-actions {
  display: flex;
  gap: 8px;
}

.quick-action-btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.quick-action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 右侧区域 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 0 0 auto;
}

/* 搜索框 */
.search-box {
  position: relative;
  width: 280px;
  transition: all 0.3s ease;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  background: rgba(248, 250, 252, 0.8);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  background: white;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.search-suggestions {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid #e2e8f0;
  margin-top: 4px;
  max-height: 300px;
  overflow-y: auto;
  z-index: 1000;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f1f5f9;
}

.suggestion-item:last-child {
  border-bottom: none;
}

.suggestion-item:hover {
  background: rgba(102, 126, 234, 0.05);
}

.suggestion-icon {
  font-size: 16px;
  color: #667eea;
}

.suggestion-text {
  flex: 1;
  font-size: 14px;
  color: #1a202c;
}

/* 工具按钮组 */
.tool-buttons {
  display: flex;
  gap: 8px;
}

.tool-btn {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(248, 250, 252, 0.8);
  color: #64748b;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
}

.tool-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateY(-2px);
}

.notification-badge {
  position: relative;
}

/* 用户下拉菜单 */
.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(102, 126, 234, 0.05);
}

.user-avatar {
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.user-info:hover .user-avatar {
  border-color: #667eea;
}

.user-details {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: #64748b;
}

.dropdown-arrow {
  font-size: 12px;
  color: #64748b;
  transition: transform 0.3s ease;
}

.user-dropdown.is-opened .dropdown-arrow {
  transform: rotate(180deg);
}

/* 用户下拉菜单样式 */
.user-dropdown-menu {
  background: white;
  border: none;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  padding: 8px;
  margin-top: 8px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  margin: 2px 0;
  transition: all 0.3s ease;
  font-size: 14px;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.1);
}

.logout-item {
  color: #ef4444;
}

.logout-item:hover {
  background: rgba(239, 68, 68, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .top-navigation {
    padding: 0 16px;
  }
  
  .nav-center {
    display: none;
  }
  
  .search-box {
    width: 200px;
  }
  
  .user-details {
    display: none;
  }
  
  .tool-buttons {
    gap: 4px;
  }
  
  .tool-btn {
    width: 36px;
    height: 36px;
  }
}

@media (max-width: 480px) {
  .search-box {
    display: none;
  }
  
  .breadcrumb {
    display: none;
  }
}
</style>
