<template>
  <div class="teacher-dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userName }}老师！</h1>
          <p>今天是 {{ currentDate }}，{{ currentWeekday }}</p>
          <p class="weather-info" v-if="weatherInfo">
            <el-icon><Sunny /></el-icon>
            {{ weatherInfo.weather }} {{ weatherInfo.temperature }}°C
          </p>
        </div>
        <div class="welcome-actions">
          <el-button type="primary" @click="quickCreateAssignment">
            <el-icon><Plus /></el-icon>
            快速布置作业
          </el-button>
          <el-button @click="goToSchedule">
            <el-icon><Calendar /></el-icon>
            查看课程表
          </el-button>
        </div>
      </div>
    </div>
    
    <!-- 数据统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon courses">
              <el-icon size="24"><Reading /></el-icon>
            </div>
            <div class="stats-info">
              <h3>{{ teacherStats.courseCount }}</h3>
              <p>授课门数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon students">
              <el-icon size="24"><UserFilled /></el-icon>
            </div>
            <div class="stats-info">
              <h3>{{ teacherStats.studentCount }}</h3>
              <p>学生总数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon grades">
              <el-icon size="24"><Document /></el-icon>
            </div>
            <div class="stats-info">
              <h3>{{ teacherStats.pendingGrades }}</h3>
              <p>待录成绩</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon assignments">
              <el-icon size="24"><Edit /></el-icon>
            </div>
            <div class="stats-info">
              <h3>{{ teacherStats.assignments }}</h3>
              <p>作业数量</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 主要内容区域 -->
    <el-row :gutter="20" class="content-row">
      <!-- 今日课程 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>今日课程</h3>
              <el-button type="text" @click="goToSchedule">查看更多</el-button>
            </div>
          </template>
          
          <div v-if="todayCourses.length > 0" class="course-list">
            <div
              v-for="course in todayCourses"
              :key="course.id"
              class="course-item"
            >
              <div class="course-time">
                {{ course.startTime }} - {{ course.endTime }}
              </div>
              <div class="course-info">
                <h4>{{ course.courseName }}</h4>
                <p>{{ course.location }} | {{ course.studentCount }}人</p>
              </div>
              <div class="course-actions">
                <el-button size="small" type="primary" @click="enterClass(course)">
                  进入课堂
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state">
            <el-icon size="48" color="#ccc"><Calendar /></el-icon>
            <p>今天没有课程安排</p>
          </div>
        </el-card>
      </el-col>
      
      <!-- 待处理事项 -->
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>待处理事项</h3>
              <el-badge :value="pendingTasks.length" class="badge">
                <el-button type="text">全部</el-button>
              </el-badge>
            </div>
          </template>
          
          <div v-if="pendingTasks.length > 0" class="task-list">
            <div
              v-for="task in pendingTasks"
              :key="task.id"
              class="task-item"
            >
              <div class="task-icon">
                <el-icon :color="getTaskIconColor(task.type)">
                  <component :is="getTaskIcon(task.type)" />
                </el-icon>
              </div>
              <div class="task-content">
                <h5>{{ task.title }}</h5>
                <p>{{ task.description }}</p>
                <span class="task-time">{{ task.createTime }}</span>
              </div>
              <div class="task-actions">
                <el-button size="small" @click="handleTask(task)">
                  处理
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-else class="empty-state">
            <el-icon size="48" color="#ccc"><Select /></el-icon>
            <p>暂无待处理事项</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近成绩录入 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>最近成绩录入</h3>
              <el-button type="text" @click="goToGrades">查看更多</el-button>
            </div>
          </template>
          
          <el-table :data="recentGrades" style="width: 100%">
            <el-table-column prop="studentName" label="学生姓名" width="120" />
            <el-table-column prop="courseName" label="课程名称" width="150" />
            <el-table-column prop="examType" label="考试类型" width="120" />
            <el-table-column prop="score" label="成绩" width="80" align="center">
              <template #default="{ row }">
                <span :class="getScoreClass(row.score)">
                  {{ row.score }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="recordTime" label="录入时间" width="150" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center">
              <template #default="{ row }">
                <el-button size="small" @click="editGrade(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteGrade(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Reading,
  UserFilled,
  Document,
  Edit,
  Calendar,
  Select,
  Sunny,
  Plus,
  TrendCharts,
  Bell,
  Timer,
  Star
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import { teacherApi } from '@/api/teacher'
import { formatDate, getWeekday } from '@/utils/date'

// 定义页面元信息
defineOptions({
  name: 'TeacherDashboard'
})

const router = useRouter()
const authStore = useAuthStore()

// 计算属性
const userName = computed(() => authStore.userName || '老师')
const currentDate = computed(() => formatDate(new Date()))
const currentWeekday = computed(() => getWeekday(new Date()))

// 响应式数据
const loading = ref(false)
const weatherInfo = ref({
  weather: '晴',
  temperature: 22
})

const teacherStats = ref({
  courseCount: 4,
  studentCount: 156,
  pendingGrades: 23,
  assignments: 8
})

const todayCourses = ref([
  {
    id: 1,
    courseName: '数据结构',
    startTime: '08:00',
    endTime: '09:40',
    location: '教学楼A201',
    studentCount: 42,
    status: '进行中'
  },
  {
    id: 2,
    courseName: '算法设计',
    startTime: '14:00',
    endTime: '15:40',
    location: '教学楼B303',
    studentCount: 38,
    status: '未开始'
  }
])

const pendingTasks = ref([
  {
    id: 1,
    title: '期中考试成绩录入',
    description: '数据结构课程期中考试成绩待录入',
    type: 'grade',
    createTime: '2024-06-01 10:30',
    priority: 'high'
  },
  {
    id: 2,
    title: '作业批改',
    description: '算法设计课程作业3待批改',
    type: 'assignment',
    createTime: '2024-06-01 09:15',
    priority: 'medium'
  },
  {
    id: 3,
    title: '课程资料更新',
    description: '更新下周课程的教学资料',
    type: 'material',
    createTime: '2024-05-31 16:45',
    priority: 'low'
  }
])

const recentGrades = ref([
  {
    id: 1,
    studentName: '张三',
    courseName: '数据结构',
    examType: '期中考试',
    score: 92,
    recordTime: '2024-06-01 14:30',
    status: '已确认'
  },
  {
    id: 2,
    studentName: '李四',
    courseName: '数据结构',
    examType: '期中考试',
    score: 85,
    recordTime: '2024-06-01 14:25',
    status: '已确认'
  },
  {
    id: 3,
    studentName: '王五',
    courseName: '算法设计',
    examType: '平时作业',
    score: 88,
    recordTime: '2024-06-01 11:20',
    status: '待确认'
  }
])

const getTaskIcon = (type) => {
  const iconMap = {
    'grade': 'Document',
    'assignment': 'Edit',
    'material': 'Star'
  }
  return iconMap[type] || 'Warning'
}

const getTaskIconColor = (type) => {
  const colorMap = {
    'grade': '#409eff',
    'assignment': '#67c23a',
    'material': '#e6a23c'
  }
  return colorMap[type] || '#909399'
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-fair'
  return 'score-poor'
}

const getStatusType = (status) => {
  const statusMap = {
    '已确认': 'success',
    '待确认': 'warning',
    '已驳回': 'danger'
  }
  return statusMap[status] || 'info'
}

const enterClass = (course) => {
  ElMessage.success(`进入课堂：${course.courseName}`)
}

const handleTask = (task) => {
  ElMessage.info(`处理任务：${task.title}`)
}

const editGrade = (grade) => {
  ElMessage.info(`编辑成绩：${grade.studentName}`)
}

const deleteGrade = (grade) => {
  ElMessage.warning(`删除成绩：${grade.studentName}`)
}

const goToSchedule = () => {
  router.push('/teacher/schedule')
}

const goToGrades = () => {
  router.push('/teacher/grades')
}

// 新增方法
const quickCreateAssignment = () => {
  router.push('/teacher/assignments?action=create')
}

const loadDashboardData = async () => {
  try {
    loading.value = true
    // 加载教师统计数据
    const statsResponse = await teacherApi.getTeacherStats()
    teacherStats.value = statsResponse.data

    // 加载今日课程
    const coursesResponse = await teacherApi.getTodayCourses()
    todayCourses.value = coursesResponse.data

    // 加载待处理事项
    const tasksResponse = await teacherApi.getPendingTasks()
    pendingTasks.value = tasksResponse.data

    // 加载最近成绩
    const gradesResponse = await teacherApi.getRecentGrades()
    recentGrades.value = gradesResponse.data

  } catch (error) {
    console.error('加载仪表板数据失败:', error)
    ElMessage.error('加载数据失败，请刷新页面重试')
  } finally {
    loading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadDashboardData()
})
</script>

</script>

<style scoped>
/* 教师仪表板样式 */
.teacher-dashboard {
  padding: 0;
  background: #f5f7fa;
  min-height: calc(100vh - 64px);
}

/* 欢迎区域 */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 32px 24px;
  margin-bottom: 24px;
  border-radius: 12px;
  margin: 24px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-text h1 {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.welcome-text p {
  margin: 4px 0;
  opacity: 0.9;
  font-size: 16px;
}

.weather-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.welcome-actions {
  display: flex;
  gap: 12px;
}

.welcome-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
}

.welcome-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.welcome-actions .el-button--primary {
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
  border-color: transparent;
}

.welcome-actions .el-button--primary:hover {
  background: white;
}

/* 统计卡片 */
.stats-row {
  margin: 0 24px 24px 24px;
}

.stats-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.stats-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stats-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px;
}

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stats-icon.courses {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stats-icon.students {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stats-icon.grades {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stats-icon.assignments {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-info h3 {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px 0;
  color: #1a202c;
}

.stats-info p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}

/* 内容区域 */
.content-row {
  margin: 0 24px 24px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1a202c;
}

/* 课程列表 */
.course-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.course-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.course-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.course-time {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
  min-width: 120px;
}

.course-info {
  flex: 1;
}

.course-info h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1a202c;
}

.course-info p {
  margin: 0;
  font-size: 14px;
  color: #64748b;
}

/* 任务列表 */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.task-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
}

.task-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #f1f5f9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.task-content {
  flex: 1;
}

.task-content h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
}

.task-content p {
  margin: 0 0 4px 0;
  font-size: 13px;
  color: #64748b;
  line-height: 1.4;
}

.task-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #94a3b8;
}

.empty-state p {
  margin: 12px 0 0 0;
  font-size: 14px;
}

/* 成绩相关样式 */
.score-excellent {
  color: #22c55e;
  font-weight: 600;
}

.score-good {
  color: #3b82f6;
  font-weight: 600;
}

.score-fair {
  color: #f59e0b;
  font-weight: 600;
}

.score-poor {
  color: #ef4444;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .teacher-dashboard {
    padding: 0;
  }

  .welcome-section {
    margin: 16px;
    padding: 24px 16px;
  }

  .welcome-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .welcome-actions {
    justify-content: center;
  }

  .stats-row,
  .content-row {
    margin: 0 16px 16px 16px;
  }

  .course-item,
  .task-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .course-time {
    min-width: auto;
  }
}
</style>