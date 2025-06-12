<template>
  <div class="side-navigation" :class="{ 'collapsed': isCollapsed }">
    <!-- 导航栏头部 -->
    <div class="nav-header">
      <div class="logo-section" v-if="!isCollapsed">
        <div class="logo">
          <el-icon class="logo-icon"><School /></el-icon>
        </div>
        <div class="brand-info">
          <h3 class="brand-title">智慧校园</h3>
          <span class="role-badge" :class="roleClass">{{ roleDisplayName }}</span>
        </div>
      </div>
      <div class="logo-mini" v-else>
        <el-icon class="logo-icon"><School /></el-icon>
      </div>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card" v-if="!isCollapsed && userInfo">
      <div class="user-avatar">
        <el-avatar :size="40" :src="userInfo.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
      </div>
      <div class="user-info">
        <div class="user-name">{{ userInfo.name || '用户' }}</div>
        <div class="user-role">{{ roleDisplayName }}</div>
      </div>
    </div>

    <!-- 导航菜单 -->
    <div class="nav-menu">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :unique-opened="true"
        router
        class="sidebar-menu"
        background-color="transparent"
        text-color="#64748b"
        active-text-color="#667eea"
      >
        <!-- 动态渲染菜单项 -->
        <template v-for="item in currentMenuItems" :key="item.path">
          <!-- 单级菜单项 -->
          <el-menu-item
            v-if="!item.children || item.children.length === 0"
            :index="item.path"
            class="nav-menu-item"
          >
            <el-icon class="menu-icon">
              <component :is="item.icon" />
            </el-icon>
            <template #title>
              <span class="menu-title">{{ item.title }}</span>
            </template>
          </el-menu-item>

          <!-- 多级菜单项 -->
          <el-sub-menu
            v-else
            :index="item.path"
            class="nav-submenu"
          >
            <template #title>
              <el-icon class="menu-icon">
                <component :is="item.icon" />
              </el-icon>
              <span class="menu-title">{{ item.title }}</span>
            </template>
            
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
              class="nav-submenu-item"
            >
              <el-icon class="submenu-icon">
                <component :is="child.icon" />
              </el-icon>
              <template #title>
                <span class="submenu-title">{{ child.title }}</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </div>

    <!-- 底部操作区 -->
    <div class="nav-footer">
      <div class="footer-actions" v-if="!isCollapsed">
        <el-button
          type="text"
          class="footer-btn"
          @click="handleSettings"
        >
          <el-icon><Setting /></el-icon>
          <span>设置</span>
        </el-button>
        <el-button
          type="text"
          class="footer-btn"
          @click="handleHelp"
        >
          <el-icon><QuestionFilled /></el-icon>
          <span>帮助</span>
        </el-button>
      </div>
      
      <!-- 折叠按钮 -->
      <div class="collapse-btn" @click="toggleCollapse">
        <el-icon>
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  School,
  User,
  House,
  Reading,
  Document,
  Promotion,
  Calendar,
  UserFilled,
  Management,
  CreditCard,
  Setting,
  QuestionFilled,
  Fold,
  Expand,
  Edit,
  DataAnalysis,
  Monitor,
  ChatDotRound,
  Bell,
  Files,
  Trophy,
  Notebook,
  Plus,
  Download,
  Upload,
  Message
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import {
  getMenuItemsByRole,
  getThemeByRole,
  ROLE_DISPLAY_NAMES,
  filterMenuByPermissions
} from './navigationConfig.js'

// 组件属性
const props = defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

// 组件事件
const emit = defineEmits(['update:collapsed', 'menu-select'])

// 响应式数据
const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapsed = ref(props.collapsed)
const activeMenu = ref('')

// 计算属性
const userInfo = computed(() => authStore.userInfo)
const userRole = computed(() => authStore.userRole)

// 角色显示名称
const roleDisplayName = computed(() => {
  return ROLE_DISPLAY_NAMES[userRole.value] || '用户'
})

// 角色样式类
const roleClass = computed(() => {
  const theme = getThemeByRole(userRole.value)
  return theme.class || 'role-default'
})

// 根据用户角色获取当前菜单项
const currentMenuItems = computed(() => {
  const menuItems = getMenuItemsByRole(userRole.value)

  // 如果用户有权限信息，则根据权限过滤菜单
  const userPermissions = authStore.permissions || []
  if (userPermissions.length > 0) {
    return filterMenuByPermissions(menuItems, userPermissions)
  }

  return menuItems
})

// 方法
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
  emit('update:collapsed', isCollapsed.value)
}

const handleSettings = () => {
  ElMessage.info('设置功能开发中...')
}

const handleHelp = () => {
  ElMessage.info('帮助功能开发中...')
}

// 监听路由变化，更新激活菜单
watch(
  () => route.path,
  (path) => {
    activeMenu.value = path
    emit('menu-select', path)
  },
  { immediate: true }
)

// 监听collapsed属性变化
watch(
  () => props.collapsed,
  (newVal) => {
    isCollapsed.value = newVal
  }
)

// 组件挂载时的初始化
onMounted(() => {
  // 验证用户角色权限
  if (!userRole.value) {
    ElMessage.warning('未检测到用户角色，请重新登录')
    router.push('/login')
  }
})
</script>

<style scoped>
/* 侧边导航栏样式 */
.side-navigation {
  width: 260px;
  height: 100vh;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  border-right: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.side-navigation.collapsed {
  width: 64px;
}

/* 导航栏头部 */
.nav-header {
  padding: 20px;
  border-bottom: 1px solid #f1f5f9;
  min-height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.logo-mini {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  color: white;
  font-size: 20px;
}

.brand-info {
  flex: 1;
  min-width: 0;
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 4px 0;
  line-height: 1.2;
}

.role-badge {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.role-student {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.role-teacher {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.role-parent {
  background: rgba(168, 85, 247, 0.1);
  color: #a855f7;
}

.role-admin {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

/* 用户信息卡片 */
.user-card {
  margin: 16px 20px;
  padding: 16px;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(102, 126, 234, 0.1);
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: #64748b;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  padding: 8px 12px;
  overflow-y: auto;
}

.sidebar-menu {
  border: none;
  background: transparent;
}

.nav-menu-item,
.nav-submenu-item {
  margin: 4px 0;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nav-menu-item:hover,
.nav-submenu-item:hover {
  background: rgba(102, 126, 234, 0.08) !important;
}

.nav-menu-item.is-active,
.nav-submenu-item.is-active {
  background: rgba(102, 126, 234, 0.1) !important;
  color: #667eea !important;
}

.menu-icon,
.submenu-icon {
  font-size: 18px;
  margin-right: 8px;
}

.menu-title,
.submenu-title {
  font-size: 14px;
  font-weight: 500;
}

/* 底部操作区 */
.nav-footer {
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
  background: rgba(248, 250, 252, 0.5);
}

.footer-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.footer-btn {
  flex: 1;
  padding: 8px 12px;
  font-size: 12px;
  color: #64748b;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.footer-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 0 auto;
}

.collapse-btn:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: scale(1.05);
}

/* 折叠状态下的样式调整 */
.side-navigation.collapsed .nav-menu {
  padding: 8px 4px;
}

.side-navigation.collapsed .nav-footer {
  padding: 16px 12px;
}

/* 滚动条样式 */
.nav-menu::-webkit-scrollbar {
  width: 4px;
}

.nav-menu::-webkit-scrollbar-track {
  background: transparent;
}

.nav-menu::-webkit-scrollbar-thumb {
  background: rgba(102, 126, 234, 0.2);
  border-radius: 2px;
}

.nav-menu::-webkit-scrollbar-thumb:hover {
  background: rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .side-navigation {
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .side-navigation.mobile-open {
    transform: translateX(0);
  }
}
</style>
