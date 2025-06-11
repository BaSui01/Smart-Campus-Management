<template>
  <div class="schedule-page">
    <div class="page-header">
      <h1>课程表</h1>
      <p>查看本学期课程安排</p>
    </div>
    
    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-select v-model="selectedWeek" placeholder="选择周次">
            <el-option
              v-for="week in weeks"
              :key="week.value"
              :label="week.label"
              :value="week.value"
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedSemester" placeholder="选择学期">
            <el-option label="2024春季学期" value="2024-spring" />
            <el-option label="2024秋季学期" value="2024-autumn" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="week">周视图</el-radio-button>
            <el-radio-button label="list">列表视图</el-radio-button>
          </el-radio-group>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="exportSchedule">
            <el-icon><Download /></el-icon>
            导出课程表
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 周视图 -->
    <el-card v-if="viewMode === 'week'" class="schedule-card" v-loading="loading">
      <div class="schedule-container">
        <div class="schedule-table">
          <!-- 表头 -->
          <div class="schedule-header">
            <div class="time-slot-header">时间</div>
            <div
              v-for="day in weekDays"
              :key="day.value"
              class="day-header"
            >
              <div class="day-name">{{ day.name }}</div>
              <div class="day-date">{{ day.date }}</div>
            </div>
          </div>
          
          <!-- 课程表格 -->
          <div class="schedule-body">
            <div
              v-for="slot in timeSlots"
              :key="slot.id"
              class="time-row"
            >
              <div class="time-slot">
                <div class="slot-number">第{{ slot.period }}节</div>
                <div class="slot-time">{{ slot.time }}</div>
              </div>
              
              <div
                v-for="day in weekDays"
                :key="day.value"
                class="course-cell"
                @click="handleCellClick(day.value, slot.id)"
              >
                <div
                  v-if="getCourseForSlot(day.value, slot.id)"
                  class="course-item"
                  :class="getCourseClass(getCourseForSlot(day.value, slot.id))"
                >
                  <div class="course-name">
                    {{ getCourseForSlot(day.value, slot.id).name }}
                  </div>
                  <div class="course-teacher">
                    {{ getCourseForSlot(day.value, slot.id).teacher }}
                  </div>
                  <div class="course-location">
                    {{ getCourseForSlot(day.value, slot.id).location }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 列表视图 -->
    <el-card v-else class="schedule-list-card" v-loading="loading">
      <template #header>
        <h3>课程列表</h3>
      </template>
      
      <div class="schedule-list">
        <div
          v-for="day in weekDays"
          :key="day.value"
          class="day-section"
        >
          <h4 class="day-title">{{ day.name }} ({{ day.date }})</h4>
          
          <div v-if="getDayCourses(day.value).length > 0" class="day-courses">
            <div
              v-for="course in getDayCourses(day.value)"
              :key="course.id"
              class="course-list-item"
            >
              <div class="course-time">{{ course.timeSlot }}</div>
              <div class="course-content">
                <h5>{{ course.name }}</h5>
                <p>{{ course.teacher }} | {{ course.location }}</p>
              </div>
              <div class="course-actions">
                <el-button size="small" @click="viewCourseDetail(course)">
                  详情
                </el-button>
              </div>
            </div>
          </div>
          
          <div v-else class="no-courses">
            <p>今天没有课程安排</p>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="课程详情"
      width="500px"
    >
      <div v-if="selectedCourse" class="course-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedCourse.name }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ selectedCourse.code }}
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            {{ selectedCourse.teacher }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ selectedCourse.timeSlot }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            {{ selectedCourse.location }}
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ selectedCourse.credits }}
          </el-descriptions-item>
          <el-descriptions-item label="课程类型">
            {{ selectedCourse.type }}
          </el-descriptions-item>
          <el-descriptions-item label="考核方式">
            {{ selectedCourse.assessment }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="course-description">
          <h4>课程简介</h4>
          <p>{{ selectedCourse.description }}</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import { courseApi } from '@/api/course'
import { studentApi } from '@/api/student'

const selectedWeek = ref(1)
const selectedSemester = ref('2024-spring')
const viewMode = ref('week')
const detailDialogVisible = ref(false)
const selectedCourse = ref(null)
const loading = ref(false)

// 当前学期信息
const currentSemester = ref({
  startDate: '2024-02-26',
  endDate: '2024-06-30'
})

// 生成周次选项
const weeks = computed(() => {
  const weekOptions = []
  for (let i = 1; i <= 20; i++) {
    weekOptions.push({
      label: `第${i}周`,
      value: i
    })
  }
  return weekOptions
})

// 工作日
const weekDays = computed(() => {
  const baseDate = dayjs().startOf('week').add(1, 'day') // 从周一开始
  return [
    { name: '周一', value: 'monday', date: baseDate.format('MM-DD') },
    { name: '周二', value: 'tuesday', date: baseDate.add(1, 'day').format('MM-DD') },
    { name: '周三', value: 'wednesday', date: baseDate.add(2, 'day').format('MM-DD') },
    { name: '周四', value: 'thursday', date: baseDate.add(3, 'day').format('MM-DD') },
    { name: '周五', value: 'friday', date: baseDate.add(4, 'day').format('MM-DD') }
  ]
})

// 时间段
const timeSlots = ref([
  { id: 1, period: '1-2', time: '08:00-09:40' },
  { id: 2, period: '3-4', time: '10:00-11:40' },
  { id: 3, period: '5-6', time: '14:00-15:40' },
  { id: 4, period: '7-8', time: '16:00-17:40' },
  { id: 5, period: '9-10', time: '19:00-20:40' }
])

// 课程数据
const courses = ref([])

// 加载课程表数据
const loadSchedule = async () => {
  loading.value = true

  try {
    const { data } = await studentApi.getStudentSchedule({
      semester: selectedSemester.value,
      week: selectedWeek.value
    })

    const scheduleList = Array.isArray(data) ? data : (data.schedules || data.courses || [])
    courses.value = scheduleList.map(schedule => ({
      id: schedule.id,
      name: schedule.courseName || schedule.name,
      code: schedule.courseCode || schedule.code,
      teacher: schedule.teacherName || schedule.teacher,
      location: schedule.location || schedule.classroom || schedule.classroomName,
      day: mapDayOfWeek(schedule.dayOfWeek),
      timeSlot: formatTimeSlot(schedule.startTime, schedule.endTime),
      slotId: mapTimeSlot(schedule.startTime),
      credits: schedule.credits || 0,
      type: schedule.courseType || schedule.type || '专业课',
      assessment: schedule.assessment || schedule.examType || '考试',
      description: schedule.description || schedule.courseDescription || '',
      startTime: schedule.startTime,
      endTime: schedule.endTime,
      dayOfWeek: schedule.dayOfWeek,
      weeks: schedule.weeks || [],
      semester: schedule.semester || selectedSemester.value
    }))
  } catch (error) {
    console.error('加载课程表失败:', error)
    ElMessage.error('加载课程表数据失败')
    courses.value = []
  } finally {
    loading.value = false
  }
}

// 映射星期几
const mapDayOfWeek = (dayOfWeek) => {
  const dayMap = {
    1: 'monday',
    2: 'tuesday',
    3: 'wednesday',
    4: 'thursday',
    5: 'friday',
    6: 'saturday',
    7: 'sunday'
  }
  return dayMap[dayOfWeek] || 'monday'
}

// 格式化时间段
const formatTimeSlot = (startTime, endTime) => {
  if (!startTime || !endTime) return '时间待定'
  return `${startTime}-${endTime}`
}

// 映射时间段到节次
const mapTimeSlot = (startTime) => {
  if (!startTime) return 1

  const hour = parseInt(startTime.split(':')[0])
  if (hour >= 8 && hour < 10) return 1
  if (hour >= 10 && hour < 12) return 2
  if (hour >= 14 && hour < 16) return 3
  if (hour >= 16 && hour < 18) return 4
  if (hour >= 19 && hour < 21) return 5
  return 1
}

// 获取指定时间段的课程
const getCourseForSlot = (day, slotId) => {
  return courses.value.find(course => course.day === day && course.slotId === slotId)
}

// 获取指定日期的所有课程
const getDayCourses = (day) => {
  return courses.value.filter(course => course.day === day)
}

// 获取课程样式类
const getCourseClass = (course) => {
  const typeClasses = {
    '必修课': 'course-required',
    '专业课': 'course-major',
    '选修课': 'course-elective'
  }
  return typeClasses[course.type] || 'course-default'
}

// 处理单元格点击
const handleCellClick = (day, slotId) => {
  const course = getCourseForSlot(day, slotId)
  if (course) {
    viewCourseDetail(course)
  }
}

// 查看课程详情
const viewCourseDetail = (course) => {
  selectedCourse.value = course
  detailDialogVisible.value = true
}

// 导出课程表
const exportSchedule = async () => {
  try {
    loading.value = true
    const { data } = await courseApi.exportSchedule({
      semester: selectedSemester.value,
      week: selectedWeek.value,
      format: 'pdf'
    })

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([data]))
    const link = document.createElement('a')
    link.href = url
    link.download = `课程表_${selectedSemester.value}_第${selectedWeek.value}周.pdf`
    link.click()
    window.URL.revokeObjectURL(url)

    ElMessage.success('课程表导出成功')
  } catch (error) {
    console.error('导出课程表失败:', error)
    ElMessage.error('导出课程表失败')
  } finally {
    loading.value = false
  }
}

// 监听器
watch([selectedSemester, selectedWeek], () => {
  loadSchedule()
})

// 生命周期
onMounted(async () => {
  // 获取当前周次
  const currentWeek = getCurrentWeek()
  selectedWeek.value = currentWeek

  // 加载课程表数据
  await loadSchedule()
})

// 获取当前周次
const getCurrentWeek = () => {
  const now = dayjs()
  const semesterStart = dayjs(currentSemester.value.startDate)
  const diffWeeks = now.diff(semesterStart, 'week') + 1
  return Math.max(1, Math.min(20, diffWeeks))
}
</script>

<style scoped>
@import '@/styles/student.css';

.schedule-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.toolbar-card {
  margin-bottom: 20px;
}

.schedule-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.schedule-container {
  overflow-x: auto;
}

.schedule-table {
  min-width: 800px;
}

.schedule-header {
  display: flex;
  background-color: #f8f9fa;
  border-bottom: 2px solid #e9ecef;
}

.time-slot-header {
  width: 120px;
  padding: 15px 10px;
  text-align: center;
  font-weight: bold;
  color: #495057;
  border-right: 1px solid #dee2e6;
}

.day-header {
  flex: 1;
  padding: 15px 10px;
  text-align: center;
  border-right: 1px solid #dee2e6;
}

.day-name {
  font-weight: bold;
  color: #495057;
  margin-bottom: 4px;
}

.day-date {
  font-size: 12px;
  color: #6c757d;
}

.schedule-body {
  background: white;
}

.time-row {
  display: flex;
  border-bottom: 1px solid #dee2e6;
  min-height: 80px;
}

.time-slot {
  width: 120px;
  padding: 15px 10px;
  text-align: center;
  background-color: #f8f9fa;
  border-right: 1px solid #dee2e6;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.slot-number {
  font-weight: bold;
  color: #495057;
  margin-bottom: 4px;
}

.slot-time {
  font-size: 12px;
  color: #6c757d;
}

.course-cell {
  flex: 1;
  padding: 8px;
  border-right: 1px solid #dee2e6;
  cursor: pointer;
  transition: background-color 0.2s;
}

.course-cell:hover {
  background-color: #f8f9fa;
}

.course-item {
  height: 100%;
  padding: 8px;
  border-radius: 6px;
  color: white;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.course-required {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.course-major {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.course-elective {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.course-default {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.course-name {
  font-weight: bold;
  margin-bottom: 4px;
  font-size: 13px;
}

.course-teacher {
  margin-bottom: 2px;
  opacity: 0.9;
}

.course-location {
  opacity: 0.8;
  font-size: 11px;
}

.schedule-list-card {
  background: white;
  border-radius: 12px;
}

.day-section {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.day-title {
  margin: 0 0 16px 0;
  color: #495057;
  font-size: 18px;
  font-weight: 600;
}

.day-courses {
  space-y: 12px;
}

.course-list-item {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.2s;
}

.course-list-item:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.course-time {
  width: 120px;
  font-weight: bold;
  color: #495057;
  text-align: center;
  background: white;
  padding: 8px;
  border-radius: 6px;
  margin-right: 16px;
}

.course-content {
  flex: 1;
}

.course-content h5 {
  margin: 0 0 4px 0;
  color: #495057;
  font-size: 16px;
}

.course-content p {
  margin: 0;
  color: #6c757d;
  font-size: 14px;
}

.course-actions {
  margin-left: 16px;
}

.no-courses {
  text-align: center;
  padding: 40px;
  color: #6c757d;
}

.course-detail {
  padding: 16px 0;
}

.course-description {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.course-description h4 {
  margin: 0 0 12px 0;
  color: #495057;
}

.course-description p {
  margin: 0;
  color: #6c757d;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .schedule-page {
    padding: 10px;
  }

  .page-header {
    padding: 20px;
  }

  .page-header h1 {
    font-size: 24px;
  }

  .course-item {
    font-size: 10px;
  }

  .course-name {
    font-size: 11px;
  }

  .course-list-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .course-time {
    width: 100%;
    margin-right: 0;
    margin-bottom: 12px;
  }

  .course-actions {
    margin-left: 0;
    margin-top: 12px;
  }
}
</style>