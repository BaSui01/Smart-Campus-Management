<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="sidebarWidth">
        <div class="sidebar">
          <div class="logo">
            <h2>智慧校园</h2>
          </div>
          
          <el-menu
            :default-active="activeMenu"
            class="sidebar-menu"
            :collapse="isCollapse"
            :unique-opened="true"
            router
          >
            <template v-for="item in menuItems" :key="item.path">
              <el-menu-item
                v-if="!item.children"
                :index="item.path"
              >
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <template #title>{{ item.title }}</template>
              </el-menu-item>
              
              <el-sub-menu
                v-else
                :index="item.path"
              >
                <template #title>
                  <el-icon>
                    <component :is="item.icon" />
                  </el-icon>
                  <span>{{ item.title }}</span>
                </template>
                
                <el-menu-item
                  v-for="child in item.children"
                  :key="child.path"
                  :index="child.path"
                >
                  <el-icon>
                    <component :is="child.icon" />
                  </el-icon>
                  <template #title>{{ child.title }}</template>
                </el-menu-item>
              </el-sub-menu>
            </template>
          </el-menu>
        </div>
      </el-aside>
      
      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-button
              type="text"
              @click="toggleSidebar"
            >
              <el-icon size="20">
                <Fold v-if="!isCollapse" />
                <Expand v-else />
              </el-icon>
            </el-button>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item
                v-for="breadcrumb in breadcrumbs"
                :key="breadcrumb.path"
                :to="breadcrumb.path"
              >
                {{ breadcrumb.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown @command="handleUserAction">
              <div class="user-info">
                <el-avatar
                  :size="32"
                  :src="userAvatar"
                >
                  <el-icon><User /></el-icon>
                </el-avatar>
                <span class="username">{{ userName }}</span>
                <el-icon class="arrow-down"><ArrowDown /></el-icon>
              </div>
              
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人信息
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    系统设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Fold,
  Expand,
  User,
  ArrowDown,
  Setting,
  SwitchButton,
  House,
  Reading,
  Document,
  Promotion,
  Calendar,
  UserFilled,
  Management,
  CreditCard
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const isCollapse = ref(false)
const activeMenu = ref('')

// 计算属性
const sidebarWidth = computed(() => isCollapse.value ? '64px' : '200px')
const userName = computed(() => authStore.userName)
const userAvatar = computed(() => authStore.user?.avatar || '')

// 根据用户角色生成菜单
const menuItems = computed(() => {
  const role = authStore.userRole
  const menus = {
    STUDENT: [
      { path: '/student/dashboard', title: '首页', icon: 'House' },
      { path: '/student/courses', title: '我的课程', icon: 'Reading' },
      { path: '/student/selection', title: '选课系统', icon: 'Document' },
      { path: '/student/grades', title: '成绩查询', icon: 'Promotion' },
      { path: '/student/schedule', title: '课程表', icon: 'Calendar' },
      { path: '/student/profile', title: '个人信息', icon: 'User' }
    ],
    TEACHER: [
      { path: '/teacher/dashboard', title: '首页', icon: 'House' },
      { path: '/teacher/courses', title: '课程管理', icon: 'Reading' },
      { path: '/teacher/grades', title: '成绩录入', icon: 'Promotion' },
      { path: '/teacher/students', title: '学生管理', icon: 'UserFilled' },
      { path: '/teacher/schedule', title: '课程安排', icon: 'Calendar' },
      { path: '/teacher/profile', title: '个人信息', icon: 'User' }
    ],
    PARENT: [
      { path: '/parent/dashboard', title: '首页', icon: 'House' },
      { path: '/parent/children', title: '子女信息', icon: 'UserFilled' },
      { path: '/parent/grades', title: '成绩查看', icon: 'Promotion' },
      { path: '/parent/payments', title: '缴费记录', icon: 'CreditCard' },
      { path: '/parent/profile', title: '个人信息', icon: 'User' }
    ]
  }
  
  return menus[role] || []
})

// 生成面包屑
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  return matched.map(item => ({
    title: item.meta.title,
    path: item.path
  }))
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 处理用户操作
const handleUserAction = async (command) => {
  switch (command) {
    case 'profile':
      const role = authStore.userRole.toLowerCase()
      router.push(`/${role}/profile`)
      break
    case 'settings':
      ElMessage.info('功能开发中...')
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

// 监听路由变化，更新激活菜单
watch(
  () => route.path,
  (path) => {
    activeMenu.value = path
  },
  { immediate: true }
)
</script>

<style scoped>
@import '@/styles/layout.css';
</style>