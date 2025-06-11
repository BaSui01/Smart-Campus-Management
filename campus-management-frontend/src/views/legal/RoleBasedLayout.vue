<template>
  <div class="role-based-layout" :class="layoutClasses">
    <!-- 侧边导航栏 -->
    <transition name="sidebar-slide">
      <SideNavigation
        v-if="showSidebar"
        :collapsed="sidebarCollapsed"
        @update:collapsed="handleSidebarCollapse"
        @menu-select="handleMenuSelect"
        class="layout-sidebar"
      />
    </transition>

    <!-- 主内容区域 -->
    <div class="layout-main" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- 顶部导航栏 -->
      <TopNavigation
        :sidebar-collapsed="sidebarCollapsed"
        @toggle-sidebar="toggleSidebar"
        @search="handleSearch"
        @notification-click="handleNotificationClick"
        class="layout-header"
      />

      <!-- 页面内容区域 -->
      <div class="layout-content">
        <!-- 内容容器 -->
        <div class="content-container">
          <!-- 页面加载指示器 -->
          <div v-if="isLoading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <!-- 路由视图 -->
          <router-view v-else v-slot="{ Component, route }">
            <transition name="page-fade" mode="out-in">
              <keep-alive :include="keepAliveComponents">
                <component :is="Component" :key="route.path" />
              </keep-alive>
            </transition>
          </router-view>
        </div>

        <!-- 页面底部 -->
        <div class="layout-footer" v-if="showFooter">
          <div class="footer-content">
            <div class="footer-info">
              <span class="copyright">© 2024 智慧校园管理系统</span>
              <span class="version">版本 v1.0.0</span>
            </div>
            <div class="footer-links">
              <el-link type="primary" @click="showHelp">帮助文档</el-link>
              <el-link type="primary" @click="showAbout">关于我们</el-link>
              <el-link type="primary" @click="showPrivacy">隐私政策</el-link>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 移动端遮罩层 -->
    <transition name="mask-fade">
      <div
        v-if="isMobile && !sidebarCollapsed"
        class="mobile-mask"
        @click="collapseSidebar"
      ></div>
    </transition>

    <!-- 通知抽屉 -->
    <el-drawer
      v-model="showNotificationDrawer"
      title="通知消息"
      direction="rtl"
      size="400px"
      class="notification-drawer"
    >
      <div class="notification-content">
        <div class="notification-header">
          <h4>最新通知</h4>
          <el-button type="text" @click="markAllAsRead">全部已读</el-button>
        </div>
        
        <div class="notification-list">
          <div
            v-for="notification in notifications"
            :key="notification.id"
            class="notification-item"
            :class="{ 'unread': !notification.read }"
            @click="handleNotificationRead(notification)"
          >
            <div class="notification-icon">
              <el-icon :class="notification.type">
                <component :is="notification.icon" />
              </el-icon>
            </div>
            <div class="notification-body">
              <div class="notification-title">{{ notification.title }}</div>
              <div class="notification-desc">{{ notification.description }}</div>
              <div class="notification-time">{{ formatTime(notification.time) }}</div>
            </div>
            <div class="notification-actions">
              <el-button v-if="!notification.read" type="text" size="small">
                标记已读
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- 全局错误提示 -->
    <transition name="error-slide">
      <div v-if="globalError" class="global-error">
        <el-alert
          :title="globalError.title"
          :description="globalError.message"
          type="error"
          :closable="true"
          @close="clearGlobalError"
        />
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, provide } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import SideNavigation from './SideNavigation.vue'
import TopNavigation from './TopNavigation.vue'

// 组件属性
const props = defineProps({
  // 是否显示侧边栏
  showSidebar: {
    type: Boolean,
    default: true
  },
  // 是否显示页脚
  showFooter: {
    type: Boolean,
    default: true
  },
  // 需要缓存的组件
  keepAliveComponents: {
    type: Array,
    default: () => ['Dashboard', 'Profile']
  }
})

// 响应式数据
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const sidebarCollapsed = ref(false)
const isMobile = ref(false)
const isLoading = ref(false)
const showNotificationDrawer = ref(false)
const globalError = ref(null)

// 通知数据
const notifications = ref([
  {
    id: 1,
    title: '新作业通知',
    description: '数学老师布置了新的作业，请及时完成',
    time: new Date(Date.now() - 1000 * 60 * 30), // 30分钟前
    type: 'info',
    icon: 'Document',
    read: false
  },
  {
    id: 2,
    title: '成绩发布',
    description: '期中考试成绩已发布，请查看',
    time: new Date(Date.now() - 1000 * 60 * 60 * 2), // 2小时前
    type: 'success',
    icon: 'Trophy',
    read: false
  },
  {
    id: 3,
    title: '系统维护通知',
    description: '系统将于今晚22:00-24:00进行维护',
    time: new Date(Date.now() - 1000 * 60 * 60 * 24), // 1天前
    type: 'warning',
    icon: 'Warning',
    read: true
  }
])

// 计算属性
const userRole = computed(() => authStore.userRole)

// 布局样式类
const layoutClasses = computed(() => ({
  'mobile-layout': isMobile.value,
  'sidebar-collapsed': sidebarCollapsed.value,
  [`role-${userRole.value?.toLowerCase()}`]: userRole.value
}))

// 方法
const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
  // 在移动端，展开侧边栏时禁止页面滚动
  if (isMobile.value) {
    document.body.style.overflow = sidebarCollapsed.value ? 'auto' : 'hidden'
  }
}

const collapseSidebar = () => {
  sidebarCollapsed.value = true
  if (isMobile.value) {
    document.body.style.overflow = 'auto'
  }
}

const handleSidebarCollapse = (collapsed) => {
  sidebarCollapsed.value = collapsed
}

const handleMenuSelect = (path) => {
  // 在移动端选择菜单后自动收起侧边栏
  if (isMobile.value) {
    collapseSidebar()
  }
}

const handleSearch = (query) => {
  ElMessage.info(`搜索: ${query}`)
  // 这里可以实现全局搜索逻辑
}

const handleNotificationClick = () => {
  showNotificationDrawer.value = true
}

const handleNotificationRead = (notification) => {
  notification.read = true
  ElMessage.success('通知已标记为已读')
}

const markAllAsRead = () => {
  notifications.value.forEach(item => {
    item.read = true
  })
  ElMessage.success('所有通知已标记为已读')
}

const formatTime = (time) => {
  const now = new Date()
  const diff = now - time
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else {
    return `${days}天前`
  }
}

const showHelp = () => {
  ElMessage.info('帮助文档功能开发中...')
}

const showAbout = () => {
  ElMessage.info('关于我们功能开发中...')
}

const showPrivacy = () => {
  router.push('/legal/privacy')
}

const clearGlobalError = () => {
  globalError.value = null
}

// 检查是否为移动设备
const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  // 在移动端默认收起侧边栏
  if (isMobile.value && !sidebarCollapsed.value) {
    sidebarCollapsed.value = true
  }
}

// 处理窗口大小变化
const handleResize = () => {
  checkMobile()
}

// 处理路由变化
const handleRouteChange = () => {
  // 页面切换时显示加载状态
  isLoading.value = true
  setTimeout(() => {
    isLoading.value = false
  }, 300)
}

// 全局错误处理
const handleGlobalError = (error) => {
  globalError.value = {
    title: '系统错误',
    message: error.message || '发生未知错误，请稍后重试'
  }
}

// 提供给子组件的方法
provide('layout', {
  toggleSidebar,
  showNotification: handleNotificationClick,
  setLoading: (loading) => { isLoading.value = loading },
  showError: handleGlobalError
})

// 监听路由变化
watch(
  () => route.path,
  () => {
    handleRouteChange()
  }
)

// 生命周期
onMounted(() => {
  checkMobile()
  window.addEventListener('resize', handleResize)
  
  // 检查用户权限
  if (!authStore.isAuthenticated) {
    ElMessage.warning('请先登录')
    router.push('/login')
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  // 恢复页面滚动
  document.body.style.overflow = 'auto'
})
</script>

<style scoped>
/* 布局容器 */
.role-based-layout {
  display: flex;
  height: 100vh;
  background: #f8fafc;
  overflow: hidden;
}

/* 侧边栏 */
.layout-sidebar {
  flex-shrink: 0;
  z-index: 200;
}

/* 主内容区域 */
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: all 0.3s ease;
}

.layout-main.sidebar-collapsed {
  margin-left: 0;
}

/* 顶部导航栏 */
.layout-header {
  flex-shrink: 0;
  z-index: 100;
}

/* 页面内容区域 */
.layout-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.content-container {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #f8fafc;
}

/* 加载状态 */
.loading-container {
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 页面底部 */
.layout-footer {
  flex-shrink: 0;
  background: white;
  border-top: 1px solid #e2e8f0;
  padding: 16px 24px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.footer-info {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: #64748b;
}

.footer-links {
  display: flex;
  gap: 16px;
}

/* 移动端遮罩层 */
.mobile-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 150;
}

/* 通知抽屉 */
.notification-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 16px;
}

.notification-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
}

.notification-list {
  flex: 1;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.notification-item:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: rgba(102, 126, 234, 0.1);
}

.notification-item.unread {
  background: rgba(102, 126, 234, 0.02);
  border-color: rgba(102, 126, 234, 0.1);
}

.notification-icon {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-icon .info {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.notification-icon .success {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.notification-icon .warning {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.notification-body {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 4px;
}

.notification-desc {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 8px;
  line-height: 1.4;
}

.notification-time {
  font-size: 12px;
  color: #94a3b8;
}

.notification-actions {
  flex-shrink: 0;
}

/* 全局错误提示 */
.global-error {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 2000;
  width: 90%;
  max-width: 500px;
}

/* 过渡动画 */
.sidebar-slide-enter-active,
.sidebar-slide-leave-active {
  transition: transform 0.3s ease;
}

.sidebar-slide-enter-from,
.sidebar-slide-leave-to {
  transform: translateX(-100%);
}

.mask-fade-enter-active,
.mask-fade-leave-active {
  transition: opacity 0.3s ease;
}

.mask-fade-enter-from,
.mask-fade-leave-to {
  opacity: 0;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.3s ease;
}

.page-fade-enter-from,
.page-fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

.error-slide-enter-active,
.error-slide-leave-active {
  transition: all 0.3s ease;
}

.error-slide-enter-from,
.error-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 移动端适配 */
@media (max-width: 768px) {
  .layout-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 200;
  }
  
  .layout-main {
    margin-left: 0;
  }
  
  .content-container {
    padding: 16px;
  }
  
  .footer-content {
    flex-direction: column;
    gap: 12px;
    text-align: center;
  }
  
  .footer-links {
    justify-content: center;
  }
}

/* 角色特定样式 */
.role-based-layout.role-student {
  --primary-color: #22c55e;
}

.role-based-layout.role-teacher {
  --primary-color: #3b82f6;
}

.role-based-layout.role-parent {
  --primary-color: #a855f7;
}

.role-based-layout.role-admin {
  --primary-color: #ef4444;
}
</style>
