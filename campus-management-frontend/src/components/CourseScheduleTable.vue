<template>
  <div class="course-schedule-table">
    <!-- 工具栏 -->
    <div class="schedule-toolbar">
      <div class="toolbar-left">
        <el-select v-model="currentWeek" placeholder="选择周次" style="width: 120px;" @change="loadSchedule">
          <el-option
            v-for="week in weekOptions"
            :key="week.value"
            :label="week.label"
            :value="week.value"
          />
        </el-select>
        <el-date-picker
          v-model="selectedDate"
          type="week"
          placeholder="选择周"
          format="YYYY年第WW周"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
          style="width: 180px; margin-left: 12px;"
        />
      </div>
      <div class="toolbar-right">
        <el-button @click="refreshSchedule" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button type="primary" @click="exportSchedule">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

    <!-- 课程表 -->
    <div class="schedule-container" v-loading="loading">
      <el-table
        :data="scheduleData"
        class="schedule-table"
        border
        height="600"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <!-- 时间列 -->
        <el-table-column prop="timeSlot" label="时间" width="100" fixed="left" align="center">
          <template #default="{ row }">
            <div class="time-slot">
              <div class="period">{{ row.period }}</div>
              <div class="time">{{ row.timeRange }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 星期列 -->
        <el-table-column
          v-for="day in weekDays"
          :key="day.value"
          :prop="day.value"
          :label="day.label"
          min-width="150"
          align="center"
        >
          <template #default="{ row }">
            <div
              v-if="row[day.value]"
              class="course-cell"
              :class="getCourseClass(row[day.value])"
              @click="viewCourseDetail(row[day.value])"
            >
              <div class="course-name">{{ row[day.value].courseName }}</div>
              <div class="course-info">
                <div class="teacher">{{ row[day.value].teacherName }}</div>
                <div class="classroom">{{ row[day.value].classroom }}</div>
              </div>
              <div v-if="row[day.value].courseType" class="course-type">
                <el-tag :type="getCourseTypeTag(row[day.value].courseType)" size="small">
                  {{ getCourseTypeText(row[day.value].courseType) }}
                </el-tag>
              </div>
            </div>
            <div v-else class="empty-cell">
              <span class="empty-text">无课</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 课程详情弹窗 -->
    <el-dialog
      v-model="courseDetailVisible"
      title="课程详情"
      width="600px"
      :before-close="closeCourseDetail"
    >
      <div v-if="selectedCourse" class="course-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedCourse.courseName }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ selectedCourse.courseCode }}
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            {{ selectedCourse.teacherName }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            {{ selectedCourse.classroom }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ formatCourseTime(selectedCourse) }}
          </el-descriptions-item>
          <el-descriptions-item label="课程类型">
            <el-tag :type="getCourseTypeTag(selectedCourse.courseType)">
              {{ getCourseTypeText(selectedCourse.courseType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ selectedCourse.credits || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="课程状态">
            <el-tag :type="getStatusTag(selectedCourse.status)">
              {{ getStatusText(selectedCourse.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">
            {{ selectedCourse.description || '暂无描述' }}
          </el-descriptions-item>
        </el-descriptions>

        <!-- 操作按钮 -->
        <div class="course-actions" style="margin-top: 20px; text-align: center;">
          <el-button v-if="showCourseActions" type="primary" @click="goToCourseDetail">
            查看课程详情
          </el-button>
          <el-button v-if="showCourseActions" @click="goToAssignments">
            查看作业
          </el-button>
          <el-button @click="closeCourseDetail">
            关闭
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 图例 -->
    <div class="schedule-legend">
      <div class="legend-title">图例：</div>
      <div class="legend-items">
        <div class="legend-item">
          <div class="legend-color required"></div>
          <span>必修课</span>
        </div>
        <div class="legend-item">
          <div class="legend-color elective"></div>
          <span>选修课</span>
        </div>
        <div class="legend-item">
          <div class="legend-color practical"></div>
          <span>实践课</span>
        </div>
        <div class="legend-item">
          <div class="legend-color exam"></div>
          <span>考试</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Download } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { useRouter } from 'vue-router'

const router = useRouter()

// Props
const props = defineProps({
  studentId: {
    type: [String, Number],
    default: null
  },
  teacherId: {
    type: [String, Number],
    default: null
  },
  classId: {
    type: [String, Number],
    default: null
  },
  showCourseActions: {
    type: Boolean,
    default: true
  }
})

// 响应式数据
const loading = ref(false)
const currentWeek = ref(1)
const selectedDate = ref('')
const scheduleData = ref([])
const courseDetailVisible = ref(false)
const selectedCourse = ref(null)

// 星期配置
const weekDays = [
  { label: '周一', value: 'monday' },
  { label: '周二', value: 'tuesday' },
  { label: '周三', value: 'wednesday' },
  { label: '周四', value: 'thursday' },
  { label: '周五', value: 'friday' },
  { label: '周六', value: 'saturday' },
  { label: '周日', value: 'sunday' }
]

// 时间段配置
const timeSlots = [
  { period: '第1节', timeRange: '08:00-08:45' },
  { period: '第2节', timeRange: '08:55-09:40' },
  { period: '第3节', timeRange: '10:00-10:45' },
  { period: '第4节', timeRange: '10:55-11:40' },
  { period: '第5节', timeRange: '14:00-14:45' },
  { period: '第6节', timeRange: '14:55-15:40' },
  { period: '第7节', timeRange: '16:00-16:45' },
  { period: '第8节', timeRange: '16:55-17:40' },
  { period: '第9节', timeRange: '19:00-19:45' },
  { period: '第10节', timeRange: '19:55-20:40' }
]

// 周次选项
const weekOptions = computed(() => {
  const options = []
  for (let i = 1; i <= 20; i++) {
    options.push({
      label: `第${i}周`,
      value: i
    })
  }
  return options
})

// 方法
const loadSchedule = async () => {
  loading.value = true
  
  try {
    const params = {
      week: currentWeek.value
    }
    
    if (props.studentId) {
      params.studentId = props.studentId
    } else if (props.teacherId) {
      params.teacherId = props.teacherId
    } else if (props.classId) {
      params.classId = props.classId
    }
    
    const { data } = await courseApi.getSchedule(params)
    
    // 初始化课程表数据结构
    const schedule = timeSlots.map(slot => ({
      timeSlot: slot.period,
      timeRange: slot.timeRange,
      period: slot.period,
      monday: null,
      tuesday: null,
      wednesday: null,
      thursday: null,
      friday: null,
      saturday: null,
      sunday: null
    }))
    
    // 填充课程数据
    if (data && data.courses) {
      data.courses.forEach(course => {
        const dayIndex = course.dayOfWeek - 1 // 1-7 转换为 0-6
        const periodIndex = course.period - 1 // 1-10 转换为 0-9
        
        if (dayIndex >= 0 && dayIndex < 7 && periodIndex >= 0 && periodIndex < 10) {
          const dayKey = weekDays[dayIndex].value
          schedule[periodIndex][dayKey] = {
            ...course,
            id: course.id || course.courseId,
            courseName: course.courseName || course.name,
            teacherName: course.teacherName || course.teacher,
            classroom: course.classroom || course.location,
            courseType: course.courseType || course.type,
            courseCode: course.courseCode || course.code,
            credits: course.credits,
            description: course.description,
            status: course.status || 'active'
          }
        }
      })
    }
    
    scheduleData.value = schedule
    
  } catch (error) {
    console.error('加载课程表失败:', error)
    ElMessage.error('加载课程表失败')
    
    // 显示空的课程表
    scheduleData.value = timeSlots.map(slot => ({
      timeSlot: slot.period,
      timeRange: slot.timeRange,
      period: slot.period,
      monday: null,
      tuesday: null,
      wednesday: null,
      thursday: null,
      friday: null,
      saturday: null,
      sunday: null
    }))
  } finally {
    loading.value = false
  }
}

const refreshSchedule = () => {
  loadSchedule()
}

const handleDateChange = (date) => {
  if (date) {
    // 根据选择的日期计算周次
    const selectedWeek = getWeekNumber(new Date(date))
    currentWeek.value = selectedWeek
    loadSchedule()
  }
}

const getWeekNumber = (date) => {
  // 简化的周次计算，实际应该根据学期开始时间计算
  const startOfYear = new Date(date.getFullYear(), 0, 1)
  const pastDaysOfYear = (date - startOfYear) / 86400000
  return Math.ceil((pastDaysOfYear + startOfYear.getDay() + 1) / 7)
}

const viewCourseDetail = (course) => {
  selectedCourse.value = course
  courseDetailVisible.value = true
}

const closeCourseDetail = () => {
  courseDetailVisible.value = false
  selectedCourse.value = null
}

const goToCourseDetail = () => {
  if (selectedCourse.value) {
    router.push(`/student/courses/${selectedCourse.value.id}`)
    closeCourseDetail()
  }
}

const goToAssignments = () => {
  router.push('/student/assignments')
  closeCourseDetail()
}

const exportSchedule = () => {
  ElMessage.info('导出功能开发中...')
}

// 样式相关方法
const getCourseClass = (course) => {
  if (!course) return ''
  
  const classes = ['course-item']
  
  if (course.courseType) {
    classes.push(`course-${course.courseType}`)
  }
  
  if (course.status === 'exam') {
    classes.push('course-exam')
  }
  
  return classes.join(' ')
}

const getCourseTypeTag = (type) => {
  const typeMap = {
    'required': 'primary',
    'elective': 'success',
    'practical': 'warning',
    'exam': 'danger'
  }
  return typeMap[type] || 'info'
}

const getCourseTypeText = (type) => {
  const textMap = {
    'required': '必修',
    'elective': '选修',
    'practical': '实践',
    'exam': '考试'
  }
  return textMap[type] || type
}

const getStatusTag = (status) => {
  const statusMap = {
    'active': 'success',
    'inactive': 'danger',
    'pending': 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'active': '进行中',
    'inactive': '已结束',
    'pending': '未开始'
  }
  return textMap[status] || status
}

const formatCourseTime = (course) => {
  if (!course) return '-'
  
  const dayNames = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const dayName = dayNames[course.dayOfWeek] || ''
  const timeSlot = timeSlots[course.period - 1]
  
  return `${dayName} ${timeSlot?.timeRange || ''}`
}

// 监听器
watch(() => props.studentId, () => {
  if (props.studentId) {
    loadSchedule()
  }
}, { immediate: true })

watch(() => props.teacherId, () => {
  if (props.teacherId) {
    loadSchedule()
  }
}, { immediate: true })

watch(() => props.classId, () => {
  if (props.classId) {
    loadSchedule()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  // 设置当前周
  currentWeek.value = getCurrentWeek()
  selectedDate.value = getCurrentWeekDate()
  
  // 如果有参数就加载数据
  if (props.studentId || props.teacherId || props.classId) {
    loadSchedule()
  }
})

const getCurrentWeek = () => {
  // 简化实现，返回当前周次
  const now = new Date()
  return getWeekNumber(now) % 20 || 1
}

const getCurrentWeekDate = () => {
  const now = new Date()
  const dayOfWeek = now.getDay() || 7 // 周日为0，转换为7
  const monday = new Date(now)
  monday.setDate(now.getDate() - dayOfWeek + 1)
  return monday.toISOString().split('T')[0]
}
</script>

<style scoped>
.course-schedule-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.schedule-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.toolbar-left {
  display: flex;
  align-items: center;
}

.toolbar-right {
  display: flex;
  gap: 12px;
}

.schedule-container {
  padding: 16px;
}

.schedule-table {
  width: 100%;
}

.time-slot {
  text-align: center;
}

.time-slot .period {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.time-slot .time {
  font-size: 12px;
  color: #909399;
}

.course-cell {
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  min-height: 60px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-cell:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.course-cell.course-required {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
}

.course-cell.course-elective {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  color: white;
}

.course-cell.course-practical {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
  color: white;
}

.course-cell.course-exam {
  background: linear-gradient(135deg, #f56c6c, #f78989);
  color: white;
}

.course-name {
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
  text-align: center;
}

.course-info {
  font-size: 12px;
  text-align: center;
  opacity: 0.9;
}

.course-info .teacher,
.course-info .classroom {
  margin: 2px 0;
}

.course-type {
  margin-top: 4px;
  text-align: center;
}

.empty-cell {
  padding: 20px;
  text-align: center;
  color: #c0c4cc;
  background: #f8f9fa;
  border-radius: 4px;
}

.empty-text {
  font-size: 12px;
}

.course-detail {
  padding: 16px 0;
}

.course-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.schedule-legend {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

.legend-title {
  font-weight: 600;
  color: #303133;
  margin-right: 16px;
}

.legend-items {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #606266;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.required {
  background: #409eff;
}

.legend-color.elective {
  background: #67c23a;
}

.legend-color.practical {
  background: #e6a23c;
}

.legend-color.exam {
  background: #f56c6c;
}

@media (max-width: 768px) {
  .schedule-toolbar {
    flex-direction: column;
    gap: 12px;
  }
  
  .toolbar-left,
  .toolbar-right {
    width: 100%;
    justify-content: center;
  }
  
  .schedule-container {
    padding: 8px;
    overflow-x: auto;
  }
  
  .schedule-table {
    min-width: 800px;
  }
  
  .legend-items {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>
