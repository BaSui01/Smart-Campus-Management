<template>
  <div class="student-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userInfo.name }}同学！</h1>
          <p class="welcome-subtitle">{{ getCurrentGreeting() }}</p>
          <div class="user-meta">
            <el-tag type="primary">{{ userInfo.className }}</el-tag>
            <el-tag type="info">学号：{{ userInfo.studentId }}</el-tag>
          </div>
        </div>
        <div class="banner-illustration">
          <el-image 
            src="/src/assets/welcome-illustration.svg" 
            fit="contain"
            class="illustration-image"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="(stat, index) in statistics" :key="index">
          <div class="stat-card" :class="`stat-${stat.type}`">
            <div class="stat-icon">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-trend" v-if="stat.trend">
                <el-icon :color="stat.trend > 0 ? '#67c23a' : '#f56c6c'">
                  <component :is="stat.trend > 0 ? 'ArrowUp' : 'ArrowDown'" />
                </el-icon>
                <span :class="stat.trend > 0 ? 'trend-up' : 'trend-down'">
                  {{ Math.abs(stat.trend) }}%
                </span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="main-content-section">
      <!-- 今日课程 -->
      <el-col :xs="24" :lg="12">
        <el-card class="today-courses-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#409eff"><Calendar /></el-icon>
                <h3>今日课程</h3>
              </div>
              <el-button type="text" @click="goToSchedule">
                查看完整课程表
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          
          <div v-loading="loading.todayCourses" class="courses-content">
            <div v-if="todayCourses.length > 0" class="course-list">
              <div
                v-for="course in todayCourses"
                :key="course.id"
                class="course-item"
                :class="{ 'current-course': course.isCurrent, 'completed-course': course.isCompleted }"
                @click="enterCourse(course)"
              >
                <div class="course-time">
                  <div class="time-range">{{ course.startTime }} - {{ course.endTime }}</div>
                  <div class="course-status">
                    <el-tag 
                      :type="getCourseStatusType(course.status)" 
                      size="small"
                      effect="plain"
                    >
                      {{ course.status }}
                    </el-tag>
                  </div>
                </div>
                <div class="course-info">
                  <h4>{{ course.courseName }}</h4>
                  <div class="course-details">
                    <span class="location">
                      <el-icon><Location /></el-icon>
                      {{ course.location }}
                    </span>
                    <span class="teacher">
                      <el-icon><User /></el-icon>
                      {{ course.teacherName }}
                    </span>
                  </div>
                </div>
                <div class="course-actions">
                  <el-button 
                    size="small" 
                    :type="course.isCurrent ? 'primary' : 'default'"
                    @click.stop="enterCourse(course)"
                  >
                    {{ course.isCurrent ? '进入课堂' : '查看详情' }}
                  </el-button>
                </div>
              </div>
            </div>
            
            <el-empty 
              v-else 
              description="今天没有课程安排" 
              :image-size="120"
              class="empty-courses"
            >
              <el-button type="primary" @click="goToSchedule">查看课程表</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
      
      <!-- 最新成绩 -->
      <el-col :xs="24" :lg="12">
        <el-card class="recent-grades-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#67c23a"><TrendCharts /></el-icon>
                <h3>最新成绩</h3>
              </div>
              <el-button type="text" @click="goToGrades">
                查看全部成绩
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          
          <div v-loading="loading.recentGrades" class="grades-content">
            <div v-if="recentGrades.length > 0" class="grade-list">
              <div
                v-for="grade in recentGrades"
                :key="grade.id"
                class="grade-item"
                @click="viewGradeDetail(grade)"
              >
                <div class="grade-subject">
                  <h4>{{ grade.courseName }}</h4>
                  <span class="exam-type">{{ grade.examType }}</span>
                </div>
                <div class="grade-score">
                  <div class="score-value" :class="getScoreClass(grade.score)">
                    {{ grade.score }}
                  </div>
                  <div class="score-rank" v-if="grade.rank">
                    第{{ grade.rank }}名
                  </div>
                </div>
                <div class="grade-trend">
                  <el-icon 
                    v-if="grade.trend" 
                    :color="getTrendColor(grade.trend)"
                    size="16"
                  >
                    <component :is="getTrendIcon(grade.trend)" />
                  </el-icon>
                </div>
              </div>
            </div>
            
            <el-empty 
              v-else 
              description="暂无成绩记录" 
              :image-size="120"
              class="empty-grades"
            >
              <el-button type="primary" @click="goToGrades">查看成绩</el-button>
            </el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 通知公告和快捷操作 -->
    <el-row :gutter="20" class="bottom-section">
      <!-- 通知公告 -->
      <el-col :xs="24" :lg="16">
        <el-card class="notifications-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#e6a23c"><Bell /></el-icon>
                <h3>通知公告</h3>
              </div>
              <el-badge :value="unreadNotifications" :hidden="unreadNotifications === 0">
                <el-button type="text" @click="viewAllNotifications">
                  查看全部
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </el-badge>
            </div>
          </template>
          
          <div v-loading="loading.notifications" class="notifications-content">
            <div v-if="notifications.length > 0" class="notification-list">
              <div
                v-for="notification in notifications"
                :key="notification.id"
                class="notification-item"
                :class="{ 'unread': !notification.isRead }"
                @click="readNotification(notification)"
              >
                <div class="notification-icon">
                  <el-icon :color="getNotificationColor(notification.type)">
                    <component :is="getNotificationIcon(notification.type)" />
                  </el-icon>
                </div>
                <div class="notification-content">
                  <h4>{{ notification.title }}</h4>
                  <p>{{ notification.content }}</p>
                  <div class="notification-meta">
                    <span class="time">{{ formatTime(notification.createTime) }}</span>
                    <el-tag 
                      :type="getNotificationTagType(notification.type)" 
                      size="small"
                      effect="plain"
                    >
                      {{ notification.typeName }}
                    </el-tag>
                  </div>
                </div>
                <div class="notification-actions" v-if="!notification.isRead">
                  <el-button size="small" type="primary" @click.stop="readNotification(notification)">
                    标记已读
                  </el-button>
                </div>
              </div>
            </div>
            
            <el-empty 
              v-else 
              description="暂无通知公告" 
              :image-size="120"
              class="empty-notifications"
            />
          </div>
        </el-card>
      </el-col>
      
      <!-- 快捷操作 -->
      <el-col :xs="24" :lg="8">
        <el-card class="quick-actions-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#f56c6c"><Lightning /></el-icon>
                <h3>快捷操作</h3>
              </div>
            </div>
          </template>
          
          <div class="quick-actions-grid">
            <div
              v-for="action in quickActions"
              :key="action.key"
              class="action-item"
              @click="handleQuickAction(action)"
            >
              <div class="action-icon" :style="{ backgroundColor: action.color }">
                <el-icon color="white" :size="20">
                  <component :is="action.icon" />
                </el-icon>
              </div>
              <div class="action-content">
                <h4>{{ action.title }}</h4>
                <p>{{ action.description }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Picture,
  Calendar, 
  TrendCharts, 
  Bell, 
  Lightning,
  Location,
  User,
  ArrowRight,
  ArrowUp,
  ArrowDown,
  Minus,
  Document,
  Reading,
  Promotion,
  Setting
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { studentApi } from '@/api/student'
import { courseApi } from '@/api/course'
import { gradeApi } from '@/api/grade'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loading = ref({
  dashboard: false,
  todayCourses: false,
  recentGrades: false,
  notifications: false
})

const userInfo = ref({
  name: '',
  studentId: '',
  className: '',
  avatar: ''
})

const statistics = ref([])
const todayCourses = ref([])
const recentGrades = ref([])
const notifications = ref([])
const unreadNotifications = ref(0)

// 计算属性
const quickActions = computed(() => [
  {
    key: 'courseSelection',
    title: '在线选课',
    description: '选择心仪课程',
    icon: 'Document',
    color: '#409eff',
    path: '/student/selection'
  },
  {
    key: 'viewGrades',
    title: '成绩查询',
    description: '查看考试成绩',
    icon: 'TrendCharts',
    color: '#67c23a',
    path: '/student/grades'
  },
  {
    key: 'courseTable',
    title: '课程表',
    description: '查看课程安排',
    icon: 'Calendar',
    color: '#e6a23c',
    path: '/student/schedule'
  },
  {
    key: 'profile',
    title: '个人中心',
    description: '管理个人信息',
    icon: 'Setting',
    color: '#f56c6c',
    path: '/student/profile'
  }
])

// 方法
const getCurrentGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，注意休息哦！'
  if (hour < 9) return '早上好，新的一天开始了！'
  if (hour < 12) return '上午好，学习状态怎么样？'
  if (hour < 14) return '中午好，记得午休哦！'
  if (hour < 18) return '下午好，继续加油！'
  if (hour < 22) return '晚上好，今天辛苦了！'
  return '夜深了，早点休息吧！'
}

const loadDashboardData = async () => {
  loading.value.dashboard = true
  
  try {
    // 并行加载所有数据
    await Promise.all([
      loadUserInfo(),
      loadStatistics(),
      loadTodayCourses(),
      loadRecentGrades(),
      loadNotifications()
    ])
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败，请刷新重试')
  } finally {
    loading.value.dashboard = false
  }
}

const loadUserInfo = async () => {
  try {
    const { data } = await studentApi.getProfile()
    userInfo.value = {
      name: data.name,
      studentId: data.studentId,
      className: data.className,
      avatar: data.avatar
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const loadStatistics = async () => {
  try {
    const { data } = await studentApi.getDashboardStats()
    statistics.value = [
      {
        type: 'courses',
        icon: 'Reading',
        label: '选修课程',
        value: data.courseCount,
        trend: data.courseCountTrend
      },
      {
        type: 'grades',
        icon: 'TrendCharts',
        label: '平均成绩',
        value: data.avgGrade,
        trend: data.avgGradeTrend
      },
      {
        type: 'attendance',
        icon: 'Calendar',
        label: '出勤率',
        value: `${data.attendanceRate}%`,
        trend: data.attendanceTrend
      },
      {
        type: 'credits',
        icon: 'Promotion',
        label: '已获学分',
        value: data.totalCredits,
        trend: null
      }
    ]
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 设置默认值
    statistics.value = [
      { type: 'courses', icon: 'Reading', label: '选修课程', value: 0, trend: null },
      { type: 'grades', icon: 'TrendCharts', label: '平均成绩', value: 0, trend: null },
      { type: 'attendance', icon: 'Calendar', label: '出勤率', value: '0%', trend: null },
      { type: 'credits', icon: 'Promotion', label: '已获学分', value: 0, trend: null }
    ]
  }
}

const loadTodayCourses = async () => {
  loading.value.todayCourses = true
  
  try {
    const { data } = await courseApi.getTodayCourses()
    todayCourses.value = data.map(course => ({
      ...course,
      isCurrent: isCurrentCourse(course),
      isCompleted: isCompletedCourse(course)
    }))
  } catch (error) {
    console.error('加载今日课程失败:', error)
    todayCourses.value = []
  } finally {
    loading.value.todayCourses = false
  }
}

const loadRecentGrades = async () => {
  loading.value.recentGrades = true
  
  try {
    const { data } = await gradeApi.getRecentGrades({ limit: 5 })
    recentGrades.value = data
  } catch (error) {
    console.error('加载最新成绩失败:', error)
    recentGrades.value = []
  } finally {
    loading.value.recentGrades = false
  }
}

const loadNotifications = async () => {
  loading.value.notifications = true
  
  try {
    const { data } = await studentApi.getNotifications({ limit: 5 })
    notifications.value = data.notifications
    unreadNotifications.value = data.unreadCount
  } catch (error) {
    console.error('加载通知公告失败:', error)
    notifications.value = []
    unreadNotifications.value = 0
  } finally {
    loading.value.notifications = false
  }
}

const isCurrentCourse = (course) => {
  const now = new Date()
  const currentTime = now.getHours() * 60 + now.getMinutes()
  const startTime = parseTimeToMinutes(course.startTime)
  const endTime = parseTimeToMinutes(course.endTime)
  
  return currentTime >= startTime && currentTime <= endTime
}

const isCompletedCourse = (course) => {
  const now = new Date()
  const currentTime = now.getHours() * 60 + now.getMinutes()
  const endTime = parseTimeToMinutes(course.endTime)
  
  return currentTime > endTime
}

const parseTimeToMinutes = (timeStr) => {
  const [hours, minutes] = timeStr.split(':').map(Number)
  return hours * 60 + minutes
}

const getCourseStatusType = (status) => {
  const statusMap = {
    '进行中': 'success',
    '即将开始': 'warning',
    '已结束': 'info',
    '未开始': 'primary'
  }
  return statusMap[status] || 'info'
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-fair'
  return 'score-poor'
}

const getTrendIcon = (trend) => {
  if (trend > 0) return 'ArrowUp'
  if (trend < 0) return 'ArrowDown'
  return 'Minus'
}

const getTrendColor = (trend) => {
  if (trend > 0) return '#67c23a'
  if (trend < 0) return '#f56c6c'
  return '#909399'
}

const getNotificationIcon = (type) => {
  const iconMap = {
    'system': 'Bell',
    'academic': 'Document',
    'course': 'Reading',
    'grade': 'TrendCharts'
  }
  return iconMap[type] || 'Bell'
}

const getNotificationColor = (type) => {
  const colorMap = {
    'system': '#409eff',
    'academic': '#67c23a',
    'course': '#e6a23c',
    'grade': '#f56c6c'
  }
  return colorMap[type] || '#909399'
}

const getNotificationTagType = (type) => {
  const tagMap = {
    'system': 'primary',
    'academic': 'success',
    'course': 'warning',
    'grade': 'danger'
  }
  return tagMap[type] || 'info'
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  if (minutes > 0) return `${minutes}分钟前`
  return '刚刚'
}

// 事件处理
const enterCourse = (course) => {
  ElMessage.success(`进入课程：${course.courseName}`)
  // 这里可以跳转到课程详情或在线课堂
}

const viewGradeDetail = (grade) => {
  router.push({
    path: '/student/grades',
    query: { courseId: grade.courseId }
  })
}

const readNotification = async (notification) => {
  try {
    await studentApi.markNotificationRead(notification.id)
    notification.isRead = true
    unreadNotifications.value = Math.max(0, unreadNotifications.value - 1)
    ElMessage.success('已标记为已读')
  } catch (error) {
    console.error('标记通知已读失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleQuickAction = (action) => {
  router.push(action.path)
}

const goToSchedule = () => {
  router.push('/student/schedule')
}

const goToGrades = () => {
  router.push('/student/grades')
}

const viewAllNotifications = () => {
  ElMessage.info('通知中心功能开发中...')
}

// 生命周期
onMounted(async () => {
  await nextTick()
  await loadDashboardData()
})
</script>

<style scoped>
@import '@/styles/student.css';
</style>