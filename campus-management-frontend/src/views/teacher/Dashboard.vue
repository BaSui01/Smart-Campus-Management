<template>
  <div class="teacher-dashboard">
    <div class="dashboard-header">
      <h1>教师首页</h1>
      <p>欢迎回来，{{ userName }}老师！</p>
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
import { ElMessage } from 'element-plus'
import { 
  Reading, 
  UserFilled, 
  Document, 
  Edit, 
  Calendar, 
  Select,
  Warning,
  Clock,
  Star
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const userName = computed(() => authStore.userName)

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

onMounted(() => {
  // 初始化数据
})
</script>


<style>
@import '@/styles/teacher.css';
</style>