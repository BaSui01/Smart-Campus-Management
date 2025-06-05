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
    <el-card v-if="viewMode === 'week'" class="schedule-card">
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
    <el-card v-else class="schedule-list-card">
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Download } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const selectedWeek = ref(1)
const selectedSemester = ref('2024-spring')
const viewMode = ref('week')
const detailDialogVisible = ref(false)
const selectedCourse = ref(null)

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
const courses = ref([
  {
    id: 1,
    name: '高等数学',
    code: 'MATH101',
    teacher: '张教授',
    location: '教学楼A101',
    day: 'monday',
    timeSlot: '08:00-09:40',
    slotId: 1,
    credits: 4,
    type: '必修课',
    assessment: '考试',
    description: '高等数学是理工科学生的重要基础课程。'
  },
  {
    id: 2,
    name: 'Java程序设计',
    code: 'CS201',
    teacher: '李老师',
    location: '实验楼B203',
    day: 'tuesday',
    timeSlot: '14:00-15:40',
    slotId: 3,
    credits: 3,
    type: '专业课',
    assessment: '考试+实验',
    description: 'Java程序设计课程教授面向对象编程思想。'
  },
  {
    id: 3,
    name: '数据结构',
    code: 'CS301',
    teacher: '王教授',
    location: '教学楼C302',
    day: 'wednesday',
    timeSlot: '10:00-11:40',
    slotId: 2,
    credits: 3,
    type: '专业课',
    assessment: '考试',
    description: '数据结构课程介绍各种数据组织方式。'
  },
  {
    id: 4,
    name: '计算机网络',
    code: 'CS401',
    teacher: '赵老师',
    location: '实验楼D401',
    day: 'thursday',
    timeSlot: '16:00-17:40',
    slotId: 4,
    credits: 3,
    type: '专业课',
    assessment: '考试+实验',
    description: '计算机网络课程涵盖网络协议等知识。'
  },
  {
    id: 5,
    name: '英语',
    code: 'ENG101',
    teacher: '刘老师',
    location: '外语楼E201',
    day: 'friday',
    timeSlot: '08:00-09:40',
    slotId: 1,
    credits: 2,
    type: '必修课',
    assessment: '考试',
    description: '大学英语课程提高学生英语能力。'
  }
])

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
const exportSchedule = () => {
  ElMessage.info('导出功能开发中...')
}

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.schedule-page {
  padding: 20px 0;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  color: #333;
  margin-bottom: 8px;
}

.page-header p {
  color: #666;
  font-size: 16px;
}

.toolbar-card {
  margin-bottom: 30px;
}

.schedule-card {
  margin-bottom: 30px;
}

.schedule-container {
  overflow-x: auto;
}

.schedule-table {
  min-width: 800px;
}

.schedule-header {
  display: grid;
  grid-template-columns: 120px repeat(5, 1fr);
  border-bottom: 2px solid #e4e7ed;
}

.time-slot-header {
  padding: 12px;
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  font-weight: 600;
  text-align: center;
}

.day-header {
  padding: 12px;
  background: #f5f7fa;
  border-right: 1px solid #e4e7ed;
  text-align: center;
}

.day-name {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.day-date {
  font-size: 12px;
  color: #666;
}

.schedule-body {
  display: grid;
  grid-template-columns: 120px repeat(5, 1fr);
}

.time-row {
  display: contents;
}

.time-slot {
  padding: 12px;
  background: #fafafa;
  border-right: 1px solid #e4e7ed;
  border-bottom: 1px solid #e4e7ed;
  text-align: center;
}

.slot-number {
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.slot-time {
  font-size: 12px;
  color: #666;
}

.course-cell {
  min-height: 80px;
  border-right: 1px solid #e4e7ed;
  border-bottom: 1px solid #e4e7ed;
  cursor: pointer;
  position: relative;
}

.course-cell:hover {
  background-color: #f0f9ff;
}

.course-item {
  height: 100%;
  padding: 8px;
  border-radius: 4px;
  margin: 4px;
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
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
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #333;
}

.course-name {
  font-weight: 600;
  font-size: 13px;
  margin-bottom: 4px;
}

.course-teacher {
  font-size: 11px;
  margin-bottom: 2px;
  opacity: 0.9;
}

.course-location {
  font-size: 11px;
  opacity: 0.8;
}

.schedule-list-card {
  margin-bottom: 30px;
}

.schedule-list {
  display: grid;
  gap: 20px;
}

.day-section {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}

.day-title {
  margin: 0;
  padding: 12px 16px;
  background: #f5f7fa;
  color: #333;
  border-bottom: 1px solid #e4e7ed;
}

.day-courses {
  padding: 16px;
}

.course-list-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.course-list-item:last-child {
  border-bottom: none;
}

.course-time {
  width: 120px;
  font-size: 14px;
  color: #666;
  font-weight: 600;
}

.course-content {
  flex: 1;
  margin: 0 16px;
}

.course-content h5 {
  margin: 0 0 4px 0;
  color: #333;
}

.course-content p {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.no-courses {
  padding: 40px;
  text-align: center;
  color: #999;
}

.no-courses p {
  margin: 0;
}

.course-detail {
  margin-bottom: 20px;
}

.course-description {
  margin-top: 20px;
}

.course-description h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.course-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}
</style>