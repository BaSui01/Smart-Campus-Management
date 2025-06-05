<template>
  <div class="parent-dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1>您好，{{ userInfo.name }}家长！</h1>
          <p class="welcome-subtitle">{{ getCurrentGreeting() }}</p>
          <div class="children-info">
            <el-tag 
              v-for="child in children" 
              :key="child.id" 
              type="primary" 
              class="child-tag"
              @click="switchCurrentChild(child)"
            >
              {{ child.name }} - {{ child.className }}
            </el-tag>
          </div>
        </div>
        <div class="banner-actions">
          <el-button type="primary" @click="addChild">
            <el-icon><Plus /></el-icon>
            添加子女
          </el-button>
          <el-button @click="viewAllChildren">
            <el-icon><User /></el-icon>
            管理子女
          </el-button>
        </div>
      </div>
    </div>

    <!-- 子女选择器 -->
    <el-card class="child-selector" shadow="hover" v-if="children.length > 1">
      <template #header>
        <div class="card-header">
          <h3>选择子女</h3>
          <span class="subtitle">查看不同子女的信息</span>
        </div>
      </template>
      
      <div class="children-grid">
        <div 
          v-for="child in children" 
          :key="child.id"
          class="child-card"
          :class="{ active: currentChild?.id === child.id }"
          @click="switchCurrentChild(child)"
        >
          <div class="child-avatar">
            <el-avatar :size="60" :src="child.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="child-info">
            <h4>{{ child.name }}</h4>
            <p>{{ child.className }}</p>
            <p>{{ child.grade }}</p>
          </div>
          <div class="child-status">
            <el-tag :type="getStatusType(child.status)" size="small">
              {{ child.status }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <div class="stats-section" v-if="currentChild">
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
    <el-row :gutter="20" class="main-content-section" v-if="currentChild">
      <!-- 学习概况 -->
      <el-col :xs="24" :lg="12">
        <el-card class="learning-overview-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#409eff"><TrendCharts /></el-icon>
                <h3>学习概况</h3>
              </div>
              <el-button type="text" @click="viewDetailedGrades">
                查看详细成绩
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          
          <div v-loading="loading.grades" class="learning-content">
            <!-- 最新成绩 -->
            <div class="recent-grades">
              <h4>最新成绩</h4>
              <div class="grade-list">
                <div
                  v-for="grade in recentGrades"
                  :key="grade.id"
                  class="grade-item"
                >
                  <div class="grade-subject">
                    <span class="subject-name">{{ grade.courseName }}</span>
                    <span class="exam-type">{{ grade.examType }}</span>
                  </div>
                  <div class="grade-score">
                    <span class="score" :class="getScoreClass(grade.score)">
                      {{ grade.score }}
                    </span>
                    <span class="grade-level">{{ grade.grade }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- GPA趋势图 -->
            <div class="gpa-trend">
              <h4>GPA趋势</h4>
              <div ref="gpaTrendChartRef" class="chart-container"></div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 出勤情况 -->
      <el-col :xs="24" :lg="12">
        <el-card class="attendance-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#67c23a"><Calendar /></el-icon>
                <h3>出勤情况</h3>
              </div>
              <el-button type="text" @click="viewAttendanceDetail">
                查看详细记录
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          
          <div v-loading="loading.attendance" class="attendance-content">
            <!-- 出勤统计 -->
            <div class="attendance-stats">
              <div class="stat-item">
                <div class="stat-circle excellent">
                  <span>{{ attendanceStats.present }}</span>
                </div>
                <p>出勤</p>
              </div>
              <div class="stat-item">
                <div class="stat-circle warning">
                  <span>{{ attendanceStats.late }}</span>
                </div>
                <p>迟到</p>
              </div>
              <div class="stat-item">
                <div class="stat-circle danger">
                  <span>{{ attendanceStats.absent }}</span>
                </div>
                <p>缺勤</p>
              </div>
              <div class="stat-item">
                <div class="stat-circle info">
                  <span>{{ attendanceStats.leave }}</span>
                </div>
                <p>请假</p>
              </div>
            </div>

            <!-- 本周出勤 -->
            <div class="weekly-attendance">
              <h4>本周出勤</h4>
              <div class="attendance-calendar">
                <div 
                  v-for="(day, index) in weeklyAttendance" 
                  :key="index"
                  class="day-item"
                  :class="day.status"
                >
                  <div class="day-name">{{ day.name }}</div>
                  <div class="day-status">
                    <el-icon>
                      <component :is="getAttendanceIcon(day.status)" />
                    </el-icon>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 下方区域 -->
    <el-row :gutter="20" class="bottom-section" v-if="currentChild">
      <!-- 最新通知 -->
      <el-col :xs="24" :lg="8">
        <el-card class="notifications-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#e6a23c"><Bell /></el-icon>
                <h3>最新通知</h3>
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
                  <h5>{{ notification.title }}</h5>
                  <p>{{ notification.content }}</p>
                  <span class="time">{{ formatTime(notification.createTime) }}</span>
                </div>
              </div>
            </div>
            
            <el-empty 
              v-else 
              description="暂无通知" 
              :image-size="100"
            />
          </div>
        </el-card>
      </el-col>
      
      <!-- 缴费提醒 -->
      <el-col :xs="24" :lg="8">
        <el-card class="payment-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#f56c6c"><Money /></el-icon>
                <h3>缴费提醒</h3>
              </div>
              <el-button type="text" @click="viewPaymentRecords">
                查看记录
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          
          <div v-loading="loading.payments" class="payment-content">
            <div v-if="pendingPayments.length > 0" class="payment-list">
              <div
                v-for="payment in pendingPayments"
                :key="payment.id"
                class="payment-item"
                :class="{ 'urgent': payment.isUrgent }"
              >
                <div class="payment-info">
                  <h5>{{ payment.title }}</h5>
                  <p class="amount">¥{{ payment.amount }}</p>
                  <p class="due-date">截止: {{ formatDate(payment.dueDate) }}</p>
                </div>
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="makePayment(payment)"
                >
                  立即缴费
                </el-button>
              </div>
            </div>
            
            <div v-else class="no-payments">
              <el-icon size="48" color="#67c23a"><CircleCheck /></el-icon>
              <p>暂无待缴费用</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <!-- 快捷操作 -->
      <el-col :xs="24" :lg="8">
        <el-card class="quick-actions-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <div class="header-title">
                <el-icon color="#9c27b0"><Lightning /></el-icon>
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
                <h5>{{ action.title }}</h5>
                <p>{{ action.description }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <div v-if="!currentChild && children.length === 0" class="empty-state">
      <el-empty description="您还没有添加子女信息">
        <el-button type="primary" @click="addChild">添加子女</el-button>
      </el-empty>
    </div>

    <!-- 添加子女对话框 -->
    <el-dialog
      v-model="addChildDialogVisible"
      title="添加子女"
      width="500px"
      :before-close="handleCloseAddChild"
    >
      <el-form
        ref="addChildFormRef"
        :model="addChildForm"
        :rules="addChildRules"
        label-width="80px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="addChildForm.name" placeholder="请输入子女姓名" />
        </el-form-item>
        
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="addChildForm.studentId" placeholder="请输入学号" />
        </el-form-item>
        
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="addChildForm.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        
        <el-form-item label="关系" prop="relationship">
          <el-select v-model="addChildForm.relationship" placeholder="请选择关系">
            <el-option label="父亲" value="father" />
            <el-option label="母亲" value="mother" />
            <el-option label="监护人" value="guardian" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addChildDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            :loading="addChildLoading"
            @click="handleAddChild"
          >
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Plus,
  User, 
  TrendCharts, 
  Calendar, 
  Bell, 
  Money,
  Lightning,
  ArrowRight,
  ArrowUp,
  ArrowDown,
  CircleCheck,
  Document,
  ChatLineRound,
  Setting
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { parentApi } from '@/api/parent'
import * as echarts from 'echarts'

const router = useRouter()
const authStore = useAuthStore()

// 响应式数据
const loading = ref({
  children: false,
  grades: false,
  attendance: false,
  notifications: false,
  payments: false
})

const userInfo = ref({
  name: '',
  avatar: ''
})

const children = ref([])
const currentChild = ref(null)
const statistics = ref([])
const recentGrades = ref([])
const attendanceStats = ref({
  present: 0,
  late: 0,
  absent: 0,
  leave: 0
})
const weeklyAttendance = ref([])
const notifications = ref([])
const unreadNotifications = ref(0)
const pendingPayments = ref([])

const addChildDialogVisible = ref(false)
const addChildLoading = ref(false)
const addChildFormRef = ref()
const addChildForm = ref({
  name: '',
  studentId: '',
  idCard: '',
  relationship: ''
})

// 图表引用
const gpaTrendChartRef = ref()
let gpaTrendChart = null

// 计算属性
const quickActions = computed(() => [
  {
    key: 'grades',
    title: '成绩查看',
    description: '查看子女成绩',
    icon: 'TrendCharts',
    color: '#409eff',
    path: '/parent/grades'
  },
  {
    key: 'communication',
    title: '家校沟通',
    description: '与老师交流',
    icon: 'ChatLineRound',
    color: '#67c23a',
    path: '/parent/communication'
  },
  {
    key: 'leave',
    title: '请假申请',
    description: '为子女请假',
    icon: 'Document',
    color: '#e6a23c',
    action: 'applyLeave'
  },
  {
    key: 'settings',
    title: '个人设置',
    description: '管理账户信息',
    icon: 'Setting',
    color: '#f56c6c',
    path: '/parent/profile'
  }
])

// 验证规则
const addChildRules = {
  name: [
    { required: true, message: '请输入子女姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  studentId: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { 
      pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, 
      message: '请输入正确的身份证号', 
      trigger: 'blur' 
    }
  ],
  relationship: [
    { required: true, message: '请选择关系', trigger: 'change' }
  ]
}

// 方法
const getCurrentGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了，关注孩子的休息时间哦！'
  if (hour < 9) return '早上好，新的一天开始了！'
  if (hour < 12) return '上午好，孩子在学校的表现如何？'
  if (hour < 14) return '中午好，关注孩子的午餐情况！'
  if (hour < 18) return '下午好，孩子快要放学了！'
  if (hour < 22) return '晚上好，陪伴孩子完成作业了吗？'
  return '夜深了，和孩子一起早点休息吧！'
}

const loadDashboardData = async () => {
  try {
    await Promise.all([
      loadUserInfo(),
      loadChildren()
    ])
    
    if (currentChild.value) {
      await Promise.all([
        loadChildStatistics(),
        loadRecentGrades(),
        loadAttendanceData(),
        loadNotifications(),
        loadPendingPayments()
      ])
    }
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
    ElMessage.error('加载数据失败，请刷新重试')
  }
}

const loadUserInfo = async () => {
  try {
    const { data } = await parentApi.getProfile()
    userInfo.value = {
      name: data.name,
      avatar: data.avatar
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const loadChildren = async () => {
  loading.value.children = true
  
  try {
    const { data } = await parentApi.getChildren()
    children.value = data
    
    if (data.length > 0 && !currentChild.value) {
      currentChild.value = data[0]
    }
  } catch (error) {
    console.error('加载子女信息失败:', error)
    children.value = []
  } finally {
    loading.value.children = false
  }
}

const loadChildStatistics = async () => {
  if (!currentChild.value) return
  
  try {
    const { data } = await parentApi.getDashboardStats()
    statistics.value = [
      {
        type: 'gpa',
        icon: 'TrendCharts',
        label: '平均成绩',
        value: data.averageGrade,
        trend: data.gradeTrend
      },
      {
        type: 'attendance',
        icon: 'Calendar',
        label: '出勤率',
        value: `${data.attendanceRate}%`,
        trend: data.attendanceTrend
      },
      {
        type: 'homework',
        icon: 'Document',
        label: '作业完成率',
        value: `${data.homeworkRate}%`,
        trend: data.homeworkTrend
      },
      {
        type: 'rank',
        icon: 'Trophy',
        label: '班级排名',
        value: data.classRank,
        trend: null
      }
    ]
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentGrades = async () => {
  if (!currentChild.value) return
  
  loading.value.grades = true
  
  try {
    const { data } = await parentApi.getChildGrades(currentChild.value.id, { limit: 5 })
    recentGrades.value = data
    
    // 渲染GPA趋势图
    nextTick(() => {
      renderGpaTrendChart()
    })
  } catch (error) {
    console.error('加载成绩数据失败:', error)
    recentGrades.value = []
  } finally {
    loading.value.grades = false
  }
}

const loadAttendanceData = async () => {
  if (!currentChild.value) return
  
  loading.value.attendance = true
  
  try {
    const { data } = await parentApi.getChildAttendance(currentChild.value.id)
    attendanceStats.value = data.stats
    weeklyAttendance.value = data.weeklyData
  } catch (error) {
    console.error('加载出勤数据失败:', error)
  } finally {
    loading.value.attendance = false
  }
}

const loadNotifications = async () => {
  loading.value.notifications = true
  
  try {
    const { data } = await parentApi.getNotifications({ limit: 5 })
    notifications.value = data.notifications
    unreadNotifications.value = data.unreadCount
  } catch (error) {
    console.error('加载通知失败:', error)
    notifications.value = []
  } finally {
    loading.value.notifications = false
  }
}

const loadPendingPayments = async () => {
  loading.value.payments = true
  
  try {
    const { data } = await parentApi.getPaymentRecords({ status: 'pending' })
    pendingPayments.value = data.slice(0, 3) // 只显示前3个
  } catch (error) {
    console.error('加载缴费信息失败:', error)
    pendingPayments.value = []
  } finally {
    loading.value.payments = false
  }
}

const renderGpaTrendChart = () => {
  if (!gpaTrendChartRef.value) return
  
  if (gpaTrendChart) {
    gpaTrendChart.dispose()
  }
  
  gpaTrendChart = echarts.init(gpaTrendChartRef.value)
  
  // 模拟GPA趋势数据
  const data = recentGrades.value.map((grade, index) => ({
    name: grade.courseName.substring(0, 4),
    value: grade.gpa || 0
  }))
  
  const option = {
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisLabel: {
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 4,
      axisLabel: {
        fontSize: 12
      }
    },
    series: [{
      data: data.map(item => item.value),
      type: 'line',
      smooth: true,
      lineStyle: {
        color: '#409eff',
        width: 2
      },
      itemStyle: {
        color: '#409eff'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    tooltip: {
      trigger: 'axis'
    }
  }
  
  gpaTrendChart.setOption(option)
}

const switchCurrentChild = async (child) => {
  currentChild.value = child
  
  // 重新加载当前子女的数据
  await Promise.all([
    loadChildStatistics(),
    loadRecentGrades(),
    loadAttendanceData()
  ])
}

const addChild = () => {
  addChildDialogVisible.value = true
}

const handleAddChild = async () => {
  try {
    await addChildFormRef.value.validate()
    addChildLoading.value = true
    
    await parentApi.bindChild(addChildForm.value)
    
    ElMessage.success('添加子女成功')
    addChildDialogVisible.value = false
    resetAddChildForm()
    await loadChildren()
  } catch (error) {
    console.error('添加子女失败:', error)
    ElMessage.error(error.message || '添加子女失败')
  } finally {
    addChildLoading.value = false
  }
}

const handleCloseAddChild = () => {
  addChildDialogVisible.value = false
  resetAddChildForm()
}

const resetAddChildForm = () => {
  Object.assign(addChildForm.value, {
    name: '',
    studentId: '',
    idCard: '',
    relationship: ''
  })
  addChildFormRef.value?.clearValidate()
}

const readNotification = async (notification) => {
  if (!notification.isRead) {
    try {
      await parentApi.markNotificationRead(notification.id)
      notification.isRead = true
      unreadNotifications.value = Math.max(0, unreadNotifications.value - 1)
    } catch (error) {
      console.error('标记通知已读失败:', error)
    }
  }
}

const handleQuickAction = (action) => {
  if (action.path) {
    router.push(action.path)
  } else if (action.action) {
    switch (action.action) {
      case 'applyLeave':
        // 跳转到请假申请
        ElMessage.info('请假功能开发中...')
        break
      default:
        ElMessage.info('功能开发中...')
    }
  }
}

const makePayment = (payment) => {
  router.push({
    path: '/parent/payments',
    query: { paymentId: payment.id }
  })
}

// 辅助方法
const getStatusType = (status) => {
  const statusMap = {
    '在校': 'success',
    '请假': 'warning',
    '缺勤': 'danger'
  }
  return statusMap[status] || 'info'
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-fair'
  return 'score-poor'
}

const getAttendanceIcon = (status) => {
  const iconMap = {
    'present': 'CircleCheck',
    'late': 'Warning',
    'absent': 'CircleClose',
    'leave': 'Document'
  }
  return iconMap[status] || 'Minus'
}

const getNotificationIcon = (type) => {
  const iconMap = {
    'system': 'Bell',
    'grade': 'TrendCharts',
    'attendance': 'Calendar',
    'payment': 'Money'
  }
  return iconMap[type] || 'Bell'
}

const getNotificationColor = (type) => {
  const colorMap = {
    'system': '#409eff',
    'grade': '#67c23a',
    'attendance': '#e6a23c',
    'payment': '#f56c6c'
  }
  return colorMap[type] || '#909399'
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

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

// 路由导航方法
const viewAllChildren = () => {
  router.push('/parent/children')
}

const viewDetailedGrades = () => {
  router.push('/parent/grades')
}

const viewAttendanceDetail = () => {
  router.push('/parent/attendance')
}

const viewAllNotifications = () => {
  ElMessage.info('通知中心功能开发中...')
}

const viewPaymentRecords = () => {
  router.push('/parent/payments')
}

// 生命周期
onMounted(async () => {
  await nextTick()
  await loadDashboardData()
  
  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (gpaTrendChart) gpaTrendChart.resize()
  })
})
</script>

<style scoped>
@import '@/styles/parent.css';
</style>