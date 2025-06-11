<template>
  <div class="example-usage">
    <!-- 使用角色布局组件的示例 -->
    <RoleBasedLayout
      :show-sidebar="showSidebar"
      :show-footer="showFooter"
      :keep-alive-components="cacheComponents"
    >
      <!-- 这里的内容会通过 router-view 自动渲染 -->
      <!-- 但我们可以在这里展示一些示例内容 -->
      <template v-if="showExample">
        <div class="example-content">
          <div class="example-header">
            <h1>智慧校园管理系统</h1>
            <p>角色导航组件使用示例</p>
          </div>

          <div class="role-demo-section">
            <h2>当前用户角色：{{ roleDisplayName }}</h2>
            
            <!-- 角色切换演示（仅用于演示目的） -->
            <div class="role-switcher" v-if="isDemoMode">
              <h3>角色切换演示</h3>
              <el-radio-group v-model="demoRole" @change="handleRoleChange">
                <el-radio label="STUDENT">学生</el-radio>
                <el-radio label="TEACHER">教师</el-radio>
                <el-radio label="PARENT">家长</el-radio>
              </el-radio-group>
            </div>

            <!-- 功能展示 -->
            <div class="feature-showcase">
              <h3>主要功能</h3>
              <el-row :gutter="24">
                <el-col :span="8" v-for="feature in currentFeatures" :key="feature.key">
                  <el-card class="feature-card" @click="navigateToFeature(feature)">
                    <div class="feature-icon">
                      <el-icon size="32">
                        <component :is="feature.icon" />
                      </el-icon>
                    </div>
                    <h4>{{ feature.title }}</h4>
                    <p>{{ feature.description }}</p>
                  </el-card>
                </el-col>
              </el-row>
            </div>

            <!-- 快捷操作演示 -->
            <div class="quick-actions-demo">
              <h3>快捷操作</h3>
              <div class="action-buttons">
                <el-button
                  v-for="action in quickActionsList"
                  :key="action.key"
                  :type="action.type"
                  @click="handleQuickAction(action)"
                >
                  <el-icon><component :is="action.icon" /></el-icon>
                  {{ action.title }}
                </el-button>
              </div>
            </div>

            <!-- 组件配置演示 -->
            <div class="config-demo">
              <h3>组件配置</h3>
              <el-form :model="layoutConfig" label-width="120px">
                <el-form-item label="显示侧边栏">
                  <el-switch v-model="showSidebar" />
                </el-form-item>
                <el-form-item label="显示页脚">
                  <el-switch v-model="showFooter" />
                </el-form-item>
                <el-form-item label="演示模式">
                  <el-switch v-model="isDemoMode" />
                </el-form-item>
                <el-form-item label="缓存组件">
                  <el-select v-model="cacheComponents" multiple placeholder="选择需要缓存的组件">
                    <el-option label="Dashboard" value="Dashboard" />
                    <el-option label="Profile" value="Profile" />
                    <el-option label="Courses" value="Courses" />
                    <el-option label="Grades" value="Grades" />
                  </el-select>
                </el-form-item>
              </el-form>
            </div>

            <!-- API 使用示例 -->
            <div class="api-demo">
              <h3>API 使用示例</h3>
              <el-tabs v-model="activeTab">
                <el-tab-pane label="基本使用" name="basic">
                  <pre><code>{{ basicUsageCode }}</code></pre>
                </el-tab-pane>
                <el-tab-pane label="路由配置" name="router">
                  <pre><code>{{ routerConfigCode }}</code></pre>
                </el-tab-pane>
                <el-tab-pane label="权限验证" name="auth">
                  <pre><code>{{ authCode }}</code></pre>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </div>
      </template>
    </RoleBasedLayout>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import RoleBasedLayout from './RoleBasedLayout.vue'

// 响应式数据
const router = useRouter()
const authStore = useAuthStore()

const showSidebar = ref(true)
const showFooter = ref(true)
const showExample = ref(true)
const isDemoMode = ref(false)
const demoRole = ref('STUDENT')
const activeTab = ref('basic')
const cacheComponents = ref(['Dashboard', 'Profile'])

// 布局配置
const layoutConfig = ref({
  sidebarCollapsed: false,
  theme: 'light',
  language: 'zh-CN'
})

// 计算属性
const userRole = computed(() => isDemoMode.value ? demoRole.value : authStore.userRole)

const roleDisplayName = computed(() => {
  const roleMap = {
    'STUDENT': '学生',
    'TEACHER': '教师',
    'PARENT': '家长',
    'ADMIN': '管理员'
  }
  return roleMap[userRole.value] || '用户'
})

// 根据角色显示不同的功能
const currentFeatures = computed(() => {
  const featureMap = {
    'STUDENT': [
      {
        key: 'courses',
        title: '我的课程',
        description: '查看已选课程和课程详情',
        icon: 'Reading',
        path: '/student/courses'
      },
      {
        key: 'grades',
        title: '成绩查询',
        description: '查看各科目成绩和排名',
        icon: 'Promotion',
        path: '/student/grades'
      },
      {
        key: 'schedule',
        title: '课程表',
        description: '查看每日课程安排',
        icon: 'Calendar',
        path: '/student/schedule'
      }
    ],
    'TEACHER': [
      {
        key: 'course-management',
        title: '课程管理',
        description: '管理教授的课程和学生',
        icon: 'Reading',
        path: '/teacher/courses'
      },
      {
        key: 'grade-entry',
        title: '成绩录入',
        description: '录入和管理学生成绩',
        icon: 'Edit',
        path: '/teacher/grades'
      },
      {
        key: 'student-management',
        title: '学生管理',
        description: '查看和管理班级学生',
        icon: 'UserFilled',
        path: '/teacher/students'
      }
    ],
    'PARENT': [
      {
        key: 'children-info',
        title: '子女信息',
        description: '查看子女基本信息和状态',
        icon: 'UserFilled',
        path: '/parent/children'
      },
      {
        key: 'grade-view',
        title: '成绩查看',
        description: '查看子女各科成绩',
        icon: 'Promotion',
        path: '/parent/grades'
      },
      {
        key: 'communication',
        title: '家校沟通',
        description: '与老师进行沟通交流',
        icon: 'ChatDotRound',
        path: '/parent/communication'
      }
    ]
  }
  return featureMap[userRole.value] || []
})

// 快捷操作列表
const quickActionsList = computed(() => {
  const actionMap = {
    'STUDENT': [
      { key: 'new-assignment', title: '查看作业', icon: 'Document', type: 'primary' },
      { key: 'course-selection', title: '选课', icon: 'Plus', type: 'success' }
    ],
    'TEACHER': [
      { key: 'create-assignment', title: '布置作业', icon: 'Edit', type: 'primary' },
      { key: 'grade-entry', title: '录入成绩', icon: 'Promotion', type: 'warning' },
      { key: 'export-data', title: '导出数据', icon: 'Download', type: 'info' }
    ],
    'PARENT': [
      { key: 'view-grades', title: '查看成绩', icon: 'Promotion', type: 'primary' },
      { key: 'contact-teacher', title: '联系老师', icon: 'Message', type: 'success' }
    ]
  }
  return actionMap[userRole.value] || []
})

// 代码示例
const basicUsageCode = `<template>
  <RoleBasedLayout>
    <!-- 页面内容通过 router-view 自动渲染 -->
  </RoleBasedLayout>
</template>

<script setup>
import RoleBasedLayout from '@/views/legal/RoleBasedLayout.vue'
</script>`

const routerConfigCode = `// router/index.js
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
    ]
  }
]`

const authCode = `// 权限验证示例
const authStore = useAuthStore()
const userRole = computed(() => authStore.userRole)

// 检查用户权限
if (!authStore.isAuthenticated) {
  router.push('/login')
}

// 根据角色显示菜单
const menuItems = computed(() => {
  return getMenuByRole(userRole.value)
})`

// 方法
const handleRoleChange = (role) => {
  ElMessage.info(`切换到${roleDisplayName.value}角色`)
  // 在实际应用中，这里应该调用认证系统的角色切换方法
}

const navigateToFeature = (feature) => {
  if (isDemoMode.value) {
    ElMessage.info(`导航到: ${feature.title}`)
  } else {
    router.push(feature.path)
  }
}

const handleQuickAction = (action) => {
  ElMessage.success(`执行快捷操作: ${action.title}`)
  // 在实际应用中，这里应该执行具体的业务逻辑
}

// 监听角色变化
watch(userRole, (newRole) => {
  console.log('用户角色变更为:', newRole)
})
</script>

<style scoped>
.example-usage {
  min-height: 100vh;
}

.example-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.example-header {
  text-align: center;
  margin-bottom: 48px;
}

.example-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1a202c;
  margin-bottom: 8px;
}

.example-header p {
  font-size: 16px;
  color: #64748b;
}

.role-demo-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.role-demo-section h2 {
  color: #1a202c;
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
}

.role-switcher {
  background: rgba(102, 126, 234, 0.05);
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 32px;
}

.role-switcher h3 {
  margin: 0 0 16px 0;
  color: #667eea;
  font-size: 16px;
  font-weight: 600;
}

.feature-showcase {
  margin-bottom: 32px;
}

.feature-showcase h3 {
  margin-bottom: 16px;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.feature-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 100%;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.feature-icon {
  color: #667eea;
  margin-bottom: 16px;
}

.feature-card h4 {
  margin: 0 0 8px 0;
  color: #1a202c;
  font-size: 16px;
  font-weight: 600;
}

.feature-card p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.5;
}

.quick-actions-demo {
  margin-bottom: 32px;
}

.quick-actions-demo h3 {
  margin-bottom: 16px;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.config-demo {
  margin-bottom: 32px;
}

.config-demo h3 {
  margin-bottom: 16px;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.api-demo h3 {
  margin-bottom: 16px;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.api-demo pre {
  background: #f8fafc;
  border-radius: 8px;
  padding: 16px;
  overflow-x: auto;
  border: 1px solid #e2e8f0;
}

.api-demo code {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #1a202c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .example-content {
    padding: 16px;
  }
  
  .example-header h1 {
    font-size: 24px;
  }
  
  .action-buttons {
    justify-content: center;
  }
  
  .feature-card {
    margin-bottom: 16px;
  }
}
</style>
