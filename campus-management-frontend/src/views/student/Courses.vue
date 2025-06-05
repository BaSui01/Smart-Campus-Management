<template>
  <div class="student-courses">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>我的课程</h1>
          <p>管理和查看您的课程信息</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-number">{{ courseStats.total }}</span>
            <span class="stat-label">总课程</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ courseStats.ongoing }}</span>
            <span class="stat-label">进行中</span>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ courseStats.completed }}</span>
            <span class="stat-label">已完成</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选工具栏 -->
    <el-card class="filter-toolbar" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程名称或代码"
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
          <el-select v-model="filterStatus" placeholder="状态" clearable @change="handleFilter">
            <el-option label="进行中" value="ongoing" />
            <el-option label="已完成" value="completed" />
            <el-option label="未开始" value="pending" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterType" placeholder="课程类型" clearable @change="handleFilter">
            <el-option label="必修课" value="required" />
            <el-option label="选修课" value="elective" />
            <el-option label="实践课" value="practical" />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="8" :md="6">
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button @click="exportCourses">
            <el-icon><Download /></el-icon>
            导出
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 视图切换和课程列表 -->
    <el-card class="courses-content" shadow="never">
      <template #header>
        <div class="content-header">
          <div class="view-controls">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="grid">
                <el-icon><Grid /></el-icon>
                卡片视图
              </el-radio-button>
              <el-radio-button label="list">
                <el-icon><List /></el-icon>
                列表视图
              </el-radio-button>
            </el-radio-group>
          </div>
          <div class="sort-controls">
            <el-select v-model="sortBy" size="small" placeholder="排序方式" @change="handleSort">
              <el-option label="按名称" value="name" />
              <el-option label="按学分" value="credits" />
              <el-option label="按时间" value="time" />
              <el-option label="按进度" value="progress" />
            </el-select>
          </div>
        </div>
      </template>

      <!-- 加载状态 -->
      <div v-loading="loading" :style="{ minHeight: '400px' }">
        <!-- 卡片视图 -->
        <div v-if="viewMode === 'grid'" class="courses-grid">
          <div
            v-for="course in filteredCourses"
            :key="course.id"
            class="course-card"
            :class="{ 'course-current': course.isCurrent }"
            @click="viewCourseDetail(course)"
          >
            <!-- 课程封面 -->
            <div class="course-cover" :style="{ backgroundColor: course.coverColor }">
              <div class="course-type-tag">
                <el-tag :type="getCourseTypeTag(course.type)" size="small">
                  {{ getCourseTypeName(course.type) }}
                </el-tag>
              </div>
              <div class="course-icon">
                <el-icon :size="32" color="white">
                  <component :is="getCourseIcon(course.category)" />
                </el-icon>
              </div>
            </div>

            <!-- 课程信息 -->
            <div class="course-info">
              <div class="course-title">
                <h3>{{ course.name }}</h3>
                <span class="course-code">{{ course.code }}</span>
              </div>
              
              <div class="course-meta">
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ course.teacherName }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Clock /></el-icon>
                  <span>{{ course.schedule }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ course.location }}</span>
                </div>
              </div>

              <!-- 课程进度 -->
              <div class="course-progress">
                <div class="progress-label">
                  <span>学习进度</span>
                  <span>{{ course.progress }}%</span>
                </div>
                <el-progress 
                  :percentage="course.progress" 
                  :show-text="false" 
                  :stroke-width="6"
                  :color="getProgressColor(course.progress)"
                />
              </div>

              <!-- 课程状态和操作 -->
              <div class="course-footer">
                <el-tag :type="getStatusType(course.status)" size="small">
                  {{ getStatusName(course.status) }}
                </el-tag>
                <div class="course-actions">
                  <el-button size="small" @click.stop="enterCourse(course)">
                    {{ course.isCurrent ? '进入课堂' : '查看详情' }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else class="courses-table">
          <el-table 
            :data="filteredCourses" 
            style="width: 100%"
            @row-click="viewCourseDetail"
            :row-class-name="getRowClassName"
          >
            <el-table-column label="课程信息" min-width="250">
              <template #default="{ row }">
                <div class="table-course-info">
                  <div class="course-icon-small" :style="{ backgroundColor: row.coverColor }">
                    <el-icon color="white" :size="16">
                      <component :is="getCourseIcon(row.category)" />
                    </el-icon>
                  </div>
                  <div class="course-details">
                    <div class="course-name">{{ row.name }}</div>
                    <div class="course-code">{{ row.code }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="teacherName" label="授课教师" width="120" />
            
            <el-table-column prop="credits" label="学分" width="80" align="center" />
            
            <el-table-column prop="schedule" label="上课时间" width="150" />
            
            <el-table-column prop="location" label="上课地点" width="120" />
            
            <el-table-column label="学习进度" width="150">
              <template #default="{ row }">
                <div class="table-progress">
                  <el-progress 
                    :percentage="row.progress" 
                    :stroke-width="8"
                    :color="getProgressColor(row.progress)"
                  />
                </div>
              </template>
            </el-table-column>
            
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusName(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="150" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click.stop="enterCourse(row)">
                  {{ row.isCurrent ? '进入课堂' : '查看详情' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && filteredCourses.length === 0" description="暂无课程数据">
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="filteredCourses.length > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 48]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="selectedCourse?.name"
      width="700px"
      :before-close="handleCloseDetail"
    >
      <div v-if="selectedCourse" class="course-detail-content">
        <!-- 课程基本信息 -->
        <div class="detail-section">
          <h4>基本信息</h4>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="课程代码">
              {{ selectedCourse.code }}
            </el-descriptions-item>
            <el-descriptions-item label="课程类型">
              <el-tag :type="getCourseTypeTag(selectedCourse.type)">
                {{ getCourseTypeName(selectedCourse.type) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="学分">
              {{ selectedCourse.credits }}
            </el-descriptions-item>
            <el-descriptions-item label="授课教师">
              {{ selectedCourse.teacherName }}
            </el-descriptions-item>
            <el-descriptions-item label="上课时间">
              {{ selectedCourse.schedule }}
            </el-descriptions-item>
            <el-descriptions-item label="上课地点">
              {{ selectedCourse.location }}
            </el-descriptions-item>
            <el-descriptions-item label="课程状态">
              <el-tag :type="getStatusType(selectedCourse.status)">
                {{ getStatusName(selectedCourse.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="学期">
              {{ selectedCourse.semester }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 课程进度详情 -->
        <div class="detail-section">
          <h4>学习进度</h4>
          <div class="progress-detail">
            <div class="progress-chart">
              <el-progress 
                type="circle" 
                :percentage="selectedCourse.progress"
                :width="120"
                :color="getProgressColor(selectedCourse.progress)"
              />
            </div>
            <div class="progress-stats">
              <div class="stat-item">
                <span class="label">已完成章节</span>
                <span class="value">{{ selectedCourse.completedChapters || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">总章节数</span>
                <span class="value">{{ selectedCourse.totalChapters || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">出勤次数</span>
                <span class="value">{{ selectedCourse.attendanceCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="label">总课时</span>
                <span class="value">{{ selectedCourse.totalHours || 0 }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 课程简介 -->
        <div class="detail-section">
          <h4>课程简介</h4>
          <div class="course-description">
            {{ selectedCourse.description || '暂无课程简介' }}
          </div>
        </div>

        <!-- 最近活动 -->
        <div class="detail-section" v-if="selectedCourse.recentActivities?.length">
          <h4>最近活动</h4>
          <el-timeline>
            <el-timeline-item
              v-for="activity in selectedCourse.recentActivities"
              :key="activity.id"
              :timestamp="activity.time"
              :type="getActivityType(activity.type)"
            >
              {{ activity.description }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="enterCourse(selectedCourse)">
            {{ selectedCourse?.isCurrent ? '进入课堂' : '查看课程' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  Search, 
  Refresh, 
  Download, 
  Grid, 
  List,
  User, 
  Clock, 
  Location,
  Reading,
  Monitor,
  Tools,
  Star
} from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { studentApi } from '@/api/student'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const filterSemester = ref('')
const filterStatus = ref('')
const filterType = ref('')
const viewMode = ref('grid')
const sortBy = ref('name')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const courses = ref([])
const semesters = ref([])
const courseStats = ref({
  total: 0,
  ongoing: 0,
  completed: 0
})

const detailDialogVisible = ref(false)
const selectedCourse = ref(null)

// 计算属性
const filteredCourses = computed(() => {
  let result = courses.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(course => 
      course.name.toLowerCase().includes(query) ||
      course.code.toLowerCase().includes(query) ||
      course.teacherName.toLowerCase().includes(query)
    )
  }

  // 学期过滤
  if (filterSemester.value) {
    result = result.filter(course => course.semester === filterSemester.value)
  }

  // 状态过滤
  if (filterStatus.value) {
    result = result.filter(course => course.status === filterStatus.value)
  }

  // 类型过滤
  if (filterType.value) {
    result = result.filter(course => course.type === filterType.value)
  }

  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'credits':
        return b.credits - a.credits
      case 'time':
        return new Date(b.createTime) - new Date(a.createTime)
      case 'progress':
        return b.progress - a.progress
      default:
        return 0
    }
  })

  return result
})

// 方法
const loadCourses = async () => {
  loading.value = true
  
  try {
    const { data } = await courseApi.getStudentCourses({
      page: currentPage.value,
      size: pageSize.value,
      semester: filterSemester.value,
      status: filterStatus.value,
      type: filterType.value,
      search: searchQuery.value
    })
    
    courses.value = data.courses.map(course => ({
      ...course,
      isCurrent: isCurrentCourse(course),
      coverColor: generateCoverColor(course.category)
    }))
    
    total.value = data.total
    updateStats()
  } catch (error) {
    console.error('加载课程失败:', error)
    ElMessage.error('加载课程数据失败')
    courses.value = []
  } finally {
    loading.value = false
  }
}

const loadSemesters = async () => {
  try {
    const { data } = await studentApi.getSemesters()
    semesters.value = data
  } catch (error) {
    console.error('加载学期数据失败:', error)
    semesters.value = [
      { label: '2024年春季', value: '2024-spring' },
      { label: '2024年秋季', value: '2024-autumn' }
    ]
  }
}

const updateStats = () => {
  const stats = courses.value.reduce((acc, course) => {
    acc.total++
    if (course.status === 'ongoing') acc.ongoing++
    if (course.status === 'completed') acc.completed++
    return acc
  }, { total: 0, ongoing: 0, completed: 0 })
  
  courseStats.value = stats
}

const isCurrentCourse = (course) => {
  if (course.status !== 'ongoing') return false
  
  const now = new Date()
  const currentDay = now.getDay() // 0=Sunday, 1=Monday, etc.
  const currentTime = now.getHours() * 60 + now.getMinutes()
  
  // 解析课程时间 (假设格式: "周一 08:00-09:40")
  if (course.schedule) {
    const scheduleMatch = course.schedule.match(/周([一二三四五六日])\s+(\d{2}):(\d{2})-(\d{2}):(\d{2})/)
    if (scheduleMatch) {
      const dayMap = { '一': 1, '二': 2, '三': 3, '四': 4, '五': 5, '六': 6, '日': 0 }
      const courseDay = dayMap[scheduleMatch[1]]
      const startTime = parseInt(scheduleMatch[2]) * 60 + parseInt(scheduleMatch[3])
      const endTime = parseInt(scheduleMatch[4]) * 60 + parseInt(scheduleMatch[5])
      
      return currentDay === courseDay && currentTime >= startTime && currentTime <= endTime
    }
  }
  
  return false
}

const generateCoverColor = (category) => {
  const colors = {
    'computer': '#409eff',
    'math': '#67c23a',
    'physics': '#e6a23c',
    'chemistry': '#f56c6c',
    'biology': '#909399',
    'literature': '#9c27b0',
    'art': '#ff5722',
    'music': '#795548',
    'sports': '#607d8b'
  }
  return colors[category] || '#409eff'
}

const getCourseIcon = (category) => {
  const icons = {
    'computer': 'Monitor',
    'math': 'Tools',
    'physics': 'Tools',
    'chemistry': 'Tools',
    'biology': 'Tools',
    'literature': 'Reading',
    'art': 'Star',
    'music': 'Star',
    'sports': 'Star'
  }
  return icons[category] || 'Reading'
}

const getCourseTypeTag = (type) => {
  const typeMap = {
    'required': 'danger',
    'elective': 'primary',
    'practical': 'success'
  }
  return typeMap[type] || 'info'
}

const getCourseTypeName = (type) => {
  const typeMap = {
    'required': '必修课',
    'elective': '选修课',
    'practical': '实践课'
  }
  return typeMap[type] || type
}

const getStatusType = (status) => {
  const statusMap = {
    'ongoing': 'success',
    'completed': 'info',
    'pending': 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusName = (status) => {
  const statusMap = {
    'ongoing': '进行中',
    'completed': '已完成',
    'pending': '未开始'
  }
  return statusMap[status] || status
}

const getProgressColor = (progress) => {
  if (progress >= 80) return '#67c23a'
  if (progress >= 60) return '#409eff'
  if (progress >= 40) return '#e6a23c'
  return '#f56c6c'
}

const getRowClassName = ({ row }) => {
  if (row.isCurrent) return 'current-course-row'
  if (row.status === 'completed') return 'completed-course-row'
  return ''
}

const getActivityType = (type) => {
  const typeMap = {
    'assignment': 'primary',
    'exam': 'warning',
    'attendance': 'success',
    'grade': 'info'
  }
  return typeMap[type] || 'info'
}

// 事件处理
const handleSearch = () => {
  currentPage.value = 1
  // 实时搜索，不需要额外处理
}

const handleFilter = () => {
  currentPage.value = 1
  loadCourses()
}

const handleSort = () => {
  // 排序在计算属性中处理，不需要重新请求数据
}

const refreshData = async () => {
  await Promise.all([
    loadCourses(),
    loadSemesters()
  ])
  ElMessage.success('数据刷新成功')
}

const exportCourses = () => {
  ElMessage.info('导出功能开发中...')
}

const viewCourseDetail = async (course) => {
  try {
    const { data } = await courseApi.getCourseDetail(course.id)
    selectedCourse.value = data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载课程详情失败:', error)
    ElMessage.error('加载课程详情失败')
  }
}

const enterCourse = (course) => {
  if (course.isCurrent) {
    ElMessage.success(`进入课堂：${course.name}`)
    // 这里可以跳转到在线课堂
  } else {
    router.push(`/student/courses/${course.id}`)
  }
}

const handleCloseDetail = () => {
  detailDialogVisible.value = false
  selectedCourse.value = null
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadCourses()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadCourses()
}

// 监听器
watch([filterSemester, filterStatus, filterType], () => {
  handleFilter()
}, { deep: true })

// 生命周期
onMounted(async () => {
  await nextTick()
  await Promise.all([
    loadCourses(),
    loadSemesters()
  ])
})
</script>

<style scoped>
@import '@/styles/student.css';
</style>