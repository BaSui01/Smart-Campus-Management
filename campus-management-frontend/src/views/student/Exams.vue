<template>
  <div class="exams-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>考试安排</h1>
          <p>查看考试时间安排和考试成绩</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="exportExamSchedule">
            <el-icon><Download /></el-icon>
            导出考试安排
          </el-button>
          <el-button @click="refreshExams">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 考试统计概览 -->
    <div class="stats-overview">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="(stat, index) in examStats" :key="index">
          <div class="stat-card" :class="`stat-${stat.type}`">
            <div class="stat-icon">
              <el-icon :size="24" :color="stat.color">
                <component :is="stat.icon" />
              </el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
              <div class="stat-description">{{ stat.description }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选和搜索 -->
    <el-card class="filter-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程名称"
            clearable
            :prefix-icon="Search"
            @input="handleSearch"
          />
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterSemester" placeholder="学期" clearable @change="handleFilter">
            <el-option
              v-for="semester in semesters"
              :key="semester.value"
              :label="semester.label"
              :value="semester.value"
            />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterStatus" placeholder="考试状态" clearable @change="handleFilter">
            <el-option label="即将开始" value="upcoming" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="completed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterType" placeholder="考试类型" clearable @change="handleFilter">
            <el-option label="期中考试" value="midterm" />
            <el-option label="期末考试" value="final" />
            <el-option label="随堂测验" value="quiz" />
            <el-option label="补考" value="makeup" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="12" :md="6">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="list">
              <el-icon><List /></el-icon>
              列表视图
            </el-radio-button>
            <el-radio-button label="calendar">
              <el-icon><Calendar /></el-icon>
              日历视图
            </el-radio-button>
          </el-radio-group>
        </el-col>
      </el-row>
    </el-card>

    <!-- 考试内容 -->
    <el-card class="exams-content" shadow="hover">
      <template #header>
        <div class="content-header">
          <h3>考试安排</h3>
          <div class="header-controls">
            <el-button size="small" @click="refreshExams">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div v-loading="loading.exams">
        <!-- 列表视图 -->
        <div v-if="viewMode === 'list'" class="exams-list">
          <el-table
            :data="filteredExams"
            style="width: 100%"
            :row-class-name="getRowClassName"
            @row-click="viewExamDetail"
          >
            <el-table-column label="课程信息" min-width="200" fixed="left">
              <template #default="{ row }">
                <div class="course-info">
                  <div class="course-name">{{ row.courseName }}</div>
                  <div class="course-code">{{ row.courseCode }}</div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="examType" label="考试类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getExamTypeTag(row.examType)" size="small">
                  {{ getExamTypeName(row.examType) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="examDate" label="考试日期" width="120" align="center">
              <template #default="{ row }">
                {{ formatDate(row.examDate) }}
              </template>
            </el-table-column>

            <el-table-column prop="examTime" label="考试时间" width="140" align="center">
              <template #default="{ row }">
                {{ row.startTime }} - {{ row.endTime }}
              </template>
            </el-table-column>

            <el-table-column prop="location" label="考试地点" width="120" align="center" />

            <el-table-column prop="duration" label="考试时长" width="100" align="center">
              <template #default="{ row }">
                {{ row.duration }}分钟
              </template>
            </el-table-column>

            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="score" label="成绩" width="80" align="center">
              <template #default="{ row }">
                <span v-if="row.score !== null" :class="getScoreClass(row.score)">
                  {{ row.score }}
                </span>
                <span v-else class="text-muted">-</span>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="150" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click.stop="viewExamDetail(row)">
                  详情
                </el-button>
                <el-button
                  v-if="canTakeExam(row)"
                  size="small"
                  type="primary"
                  @click.stop="startExam(row)"
                >
                  开始考试
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 日历视图 -->
        <div v-else class="exams-calendar">
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div class="calendar-cell">
                <div class="date-number">{{ data.day.split('-').pop() }}</div>
                <div class="exam-items">
                  <div
                    v-for="exam in getExamsForDate(data.day)"
                    :key="exam.id"
                    class="exam-item"
                    :class="`exam-${exam.examType}`"
                    @click="viewExamDetail(exam)"
                  >
                    <div class="exam-time">{{ exam.startTime }}</div>
                    <div class="exam-course">{{ exam.courseName }}</div>
                  </div>
                </div>
              </div>
            </template>
          </el-calendar>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading.exams && filteredExams.length === 0" description="暂无考试安排">
          <el-button type="primary" @click="refreshExams">刷新数据</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="viewMode === 'list' && filteredExams.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Download,
  Refresh,
  Search,
  List,
  Calendar,
  Clock,
  Trophy,
  Warning,
  CircleCheck
} from '@element-plus/icons-vue'
import { examApi } from '@/api/exam'
import { studentApi } from '@/api/student'

// 响应式数据
const loading = ref({
  exams: false
})

const searchQuery = ref('')
const filterSemester = ref('')
const filterStatus = ref('')
const filterType = ref('')
const viewMode = ref('list')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const calendarDate = ref(new Date())

const exams = ref([])
const semesters = ref([])
const examStats = ref([])

// 计算属性
const filteredExams = computed(() => {
  let result = exams.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(exam =>
      exam.courseName.toLowerCase().includes(query) ||
      exam.courseCode.toLowerCase().includes(query)
    )
  }

  // 学期过滤
  if (filterSemester.value) {
    result = result.filter(exam => exam.semester === filterSemester.value)
  }

  // 状态过滤
  if (filterStatus.value) {
    result = result.filter(exam => exam.status === filterStatus.value)
  }

  // 类型过滤
  if (filterType.value) {
    result = result.filter(exam => exam.examType === filterType.value)
  }

  return result
})

// 方法
const loadExams = async () => {
  loading.value.exams = true

  try {
    const { data } = await examApi.getStudentExams({
      page: currentPage.value,
      size: pageSize.value,
      semester: filterSemester.value,
      status: filterStatus.value,
      type: filterType.value,
      search: searchQuery.value
    })

    const examList = Array.isArray(data) ? data : (data.exams || data.list || [])
    exams.value = examList.map(exam => ({
      id: exam.id,
      courseName: exam.courseName || exam.course?.courseName,
      courseCode: exam.courseCode || exam.course?.courseCode,
      examType: exam.examType || exam.type || 'final',
      examDate: exam.examDate || exam.date,
      startTime: exam.startTime || exam.beginTime,
      endTime: exam.endTime || exam.finishTime,
      location: exam.location || exam.classroom || exam.venue,
      duration: exam.duration || calculateDuration(exam.startTime, exam.endTime),
      status: exam.status || getTimeBasedStatus(exam),
      score: exam.score || exam.totalScore,
      maxScore: exam.maxScore || exam.fullScore || 100,
      semester: exam.semester || exam.semesterName,
      teacherName: exam.teacherName || exam.teacher?.realName,
      description: exam.description || exam.examDescription,
      requirements: exam.requirements || exam.examRequirements
    }))

    total.value = data.total || examList.length
    await loadExamStats()
  } catch (error) {
    console.error('加载考试安排失败:', error)
    ElMessage.error('加载考试数据失败')
    exams.value = []
  } finally {
    loading.value.exams = false
  }
}

const loadExamStats = async () => {
  try {
    const { data } = await examApi.getExamStats()
    examStats.value = [
      {
        type: 'total',
        icon: 'Calendar',
        color: '#409eff',
        label: '总考试数',
        value: data.totalExams || exams.value.length,
        description: `本学期共 ${data.totalExams || exams.value.length} 场考试`
      },
      {
        type: 'upcoming',
        icon: 'Clock',
        color: '#e6a23c',
        label: '即将开始',
        value: data.upcomingExams || exams.value.filter(e => e.status === 'upcoming').length,
        description: `${data.upcomingExams || 0} 场考试即将开始`
      },
      {
        type: 'completed',
        icon: 'CircleCheck',
        color: '#67c23a',
        label: '已完成',
        value: data.completedExams || exams.value.filter(e => e.status === 'completed').length,
        description: `已完成 ${data.completedExams || 0} 场考试`
      },
      {
        type: 'average',
        icon: 'Trophy',
        color: '#f56c6c',
        label: '平均分',
        value: data.averageScore || calculateAverageScore(),
        description: `当前平均成绩 ${data.averageScore || 0} 分`
      }
    ]
  } catch (error) {
    console.error('加载考试统计失败:', error)
    // 设置默认统计数据
    examStats.value = [
      { type: 'total', icon: 'Calendar', color: '#409eff', label: '总考试数', value: exams.value.length, description: '本学期考试总数' },
      { type: 'upcoming', icon: 'Clock', color: '#e6a23c', label: '即将开始', value: 0, description: '暂无即将开始的考试' },
      { type: 'completed', icon: 'CircleCheck', color: '#67c23a', label: '已完成', value: 0, description: '暂无已完成的考试' },
      { type: 'average', icon: 'Trophy', color: '#f56c6c', label: '平均分', value: '0', description: '暂无成绩数据' }
    ]
  }
}

const loadSemesters = async () => {
  try {
    const { data } = await studentApi.getSemesters()
    semesters.value = Array.isArray(data) ? data : (data.semesters || [])
  } catch (error) {
    console.error('加载学期数据失败:', error)
    semesters.value = [
      { label: '2024年春季', value: '2024-spring' },
      { label: '2024年秋季', value: '2024-autumn' }
    ]
  }
}

// 辅助函数
const calculateDuration = (startTime, endTime) => {
  if (!startTime || !endTime) return 120

  const start = new Date(`2000-01-01 ${startTime}`)
  const end = new Date(`2000-01-01 ${endTime}`)
  return Math.round((end - start) / (1000 * 60))
}

const getTimeBasedStatus = (exam) => {
  if (exam.status) return exam.status

  const now = new Date()
  const examDateTime = new Date(`${exam.examDate} ${exam.startTime}`)
  const examEndTime = new Date(`${exam.examDate} ${exam.endTime}`)

  if (now < examDateTime) return 'upcoming'
  if (now >= examDateTime && now <= examEndTime) return 'ongoing'
  return 'completed'
}

const calculateAverageScore = () => {
  const scoredExams = exams.value.filter(e => e.score !== null && e.score !== undefined)
  if (scoredExams.length === 0) return '0'

  const total = scoredExams.reduce((sum, e) => sum + e.score, 0)
  return (total / scoredExams.length).toFixed(1)
}

const getExamsForDate = (date) => {
  return exams.value.filter(exam => exam.examDate === date)
}

const getExamTypeTag = (type) => {
  const typeMap = {
    'midterm': 'warning',
    'final': 'primary',
    'quiz': 'success',
    'makeup': 'info'
  }
  return typeMap[type] || 'info'
}

const getExamTypeName = (type) => {
  const typeMap = {
    'midterm': '期中考试',
    'final': '期末考试',
    'quiz': '随堂测验',
    'makeup': '补考'
  }
  return typeMap[type] || type
}

const getStatusType = (status) => {
  const statusMap = {
    'upcoming': 'warning',
    'ongoing': 'primary',
    'completed': 'success',
    'cancelled': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusName = (status) => {
  const statusMap = {
    'upcoming': '即将开始',
    'ongoing': '进行中',
    'completed': '已结束',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-fair'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

const getRowClassName = ({ row }) => {
  if (row.status === 'upcoming') return 'upcoming-row'
  if (row.status === 'ongoing') return 'ongoing-row'
  if (row.status === 'cancelled') return 'cancelled-row'
  return ''
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const canTakeExam = (exam) => {
  return exam.status === 'ongoing' || exam.status === 'upcoming'
}

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
}

const handleFilter = () => {
  currentPage.value = 1
  loadExams()
}

const refreshExams = async () => {
  await loadExams()
  ElMessage.success('数据刷新成功')
}

const exportExamSchedule = () => {
  ElMessage.info('导出功能开发中...')
}

const viewExamDetail = (exam) => {
  ElMessage.info(`查看考试详情: ${exam.courseName}`)
}

const startExam = (exam) => {
  ElMessage.info(`开始考试: ${exam.courseName}`)
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadExams()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadExams()
}

// 生命周期
onMounted(async () => {
  await nextTick()
  await Promise.all([
    loadExams(),
    loadSemesters()
  ])
})
</script>

<style scoped>
@import '@/styles/student.css';

.exams-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.title-section h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.title-section p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.stats-overview {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-icon {
  margin-right: 16px;
  padding: 12px;
  border-radius: 8px;
  background-color: rgba(64, 158, 255, 0.1);
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 2px;
}

.stat-description {
  font-size: 12px;
  color: #909399;
}

.filter-card {
  margin-bottom: 20px;
}

.exams-content {
  background: white;
  border-radius: 12px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.content-header h3 {
  margin: 0;
  color: #303133;
}

.course-info {
  text-align: left;
}

.course-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.course-code {
  font-size: 12px;
  color: #909399;
}

.score-excellent { color: #67c23a; font-weight: bold; }
.score-good { color: #409eff; font-weight: bold; }
.score-fair { color: #e6a23c; font-weight: bold; }
.score-pass { color: #909399; font-weight: bold; }
.score-fail { color: #f56c6c; font-weight: bold; }

.upcoming-row { background-color: #fdf6ec; }
.ongoing-row { background-color: #ecf5ff; }
.cancelled-row { background-color: #fef0f0; }

.calendar-cell {
  height: 100px;
  padding: 4px;
}

.date-number {
  font-weight: bold;
  margin-bottom: 4px;
}

.exam-items {
  max-height: 70px;
  overflow-y: auto;
}

.exam-item {
  background: #f0f9ff;
  border-radius: 4px;
  padding: 2px 4px;
  margin-bottom: 2px;
  cursor: pointer;
  font-size: 10px;
}

.exam-item:hover {
  background: #e0f2fe;
}

.exam-midterm { border-left: 3px solid #e6a23c; }
.exam-final { border-left: 3px solid #409eff; }
.exam-quiz { border-left: 3px solid #67c23a; }
.exam-makeup { border-left: 3px solid #909399; }

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

.text-muted {
  color: #909399;
}
</style>