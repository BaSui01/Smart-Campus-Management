<template>
  <div class="course-selection-page">
    <div class="page-header">
      <h1>选课系统</h1>
      <p>选择您感兴趣的课程</p>
    </div>
    
    <!-- 选课状态提示 -->
    <el-alert
      :title="selectionStatus.title"
      :type="selectionStatus.type"
      :description="selectionStatus.description"
      show-icon
      :closable="false"
      class="status-alert"
    />
    
    <!-- 筛选和搜索 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="5">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程名称或代码"
            clearable
            :prefix-icon="Search"
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterDepartment" placeholder="选择院系" clearable>
            <el-option label="计算机学院" value="cs" />
            <el-option label="数学学院" value="math" />
            <el-option label="物理学院" value="physics" />
            <el-option label="外语学院" value="foreign" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterCredits" placeholder="学分范围" clearable>
            <el-option label="1-2学分" value="1-2" />
            <el-option label="3-4学分" value="3-4" />
            <el-option label="5-6学分" value="5-6" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterTime" placeholder="上课时间" clearable>
            <el-option label="周一至周三" value="mon-wed" />
            <el-option label="周四至周五" value="thu-fri" />
            <el-option label="周末" value="weekend" />
          </el-select>
        </el-col>
        <el-col :span="3">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </el-col>
        <el-col :span="4">
          <div class="selection-summary">
            已选：{{ selectedCourses.length }}/{{ maxCourses }} 门课程
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 选课统计 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ selectionStats.available }}</div>
            <div class="stat-label">可选课程</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ selectionStats.selected }}</div>
            <div class="stat-label">已选课程</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ selectionStats.credits }}</div>
            <div class="stat-label">已选学分</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ selectionStats.remaining }}</div>
            <div class="stat-label">剩余名额</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 选课列表 -->
    <el-card class="courses-card">
      <template #header>
        <div class="card-header">
          <h3>可选课程列表</h3>
          <div class="header-actions">
            <el-button 
              type="success" 
              :disabled="selectedCourses.length === 0"
              @click="batchConfirmSelection"
            >
              批量确认选课（{{ selectedCourses.length }}）
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table
        :data="filteredCourses"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column
          type="selection"
          width="55"
          :selectable="isSelectable"
        />
        
        <el-table-column prop="name" label="课程名称" min-width="120">
          <template #default="{ row }">
            <div class="course-name">
              <strong>{{ row.name }}</strong>
              <div class="course-code">{{ row.code }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="teacher" label="授课教师" width="100" />
        
        <el-table-column prop="department" label="开课院系" width="100" />
        
        <el-table-column prop="credits" label="学分" width="80" align="center" />
        
        <el-table-column prop="schedule" label="上课时间" width="120" />
        
        <el-table-column prop="location" label="上课地点" width="100" />
        
        <el-table-column prop="capacity" label="容量" width="80" align="center">
          <template #default="{ row }">
            {{ row.enrolled }}/{{ row.capacity }}
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button
              v-if="!row.isSelected"
              size="small"
              type="primary"
              :disabled="!isSelectable(row)"
              @click="selectCourse(row)"
            >
              选课
            </el-button>
            <el-button
              v-else
              size="small"
              type="danger"
              @click="dropCourse(row)"
            >
              退选
            </el-button>
            <el-button size="small" @click="viewCourseDetail(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 已选课程 -->
    <el-card v-if="selectedCourses.length > 0" class="selected-courses-card">
      <template #header>
        <div class="card-header">
          <h3>已选课程</h3>
          <el-button type="danger" size="small" @click="clearSelection">
            清空选择
          </el-button>
        </div>
      </template>
      
      <div class="selected-courses-list">
        <div
          v-for="course in selectedCourses"
          :key="course.id"
          class="selected-course-item"
        >
          <div class="course-info">
            <h4>{{ course.name }}</h4>
            <p>{{ course.teacher }} | {{ course.schedule }} | {{ course.credits }}学分</p>
          </div>
          <el-button size="small" type="danger" @click="dropCourse(course)">
            移除
          </el-button>
        </div>
      </div>
    </el-card>
    
    <!-- 课程详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="课程详情"
      width="600px"
    >
      <div v-if="selectedCourseDetail" class="course-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedCourseDetail.name }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ selectedCourseDetail.code }}
          </el-descriptions-item>
          <el-descriptions-item label="授课教师">
            {{ selectedCourseDetail.teacher }}
          </el-descriptions-item>
          <el-descriptions-item label="开课院系">
            {{ selectedCourseDetail.department }}
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ selectedCourseDetail.credits }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ selectedCourseDetail.schedule }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            {{ selectedCourseDetail.location }}
          </el-descriptions-item>
          <el-descriptions-item label="课程容量">
            {{ selectedCourseDetail.enrolled }}/{{ selectedCourseDetail.capacity }}
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="course-description">
          <h4>课程简介</h4>
          <p>{{ selectedCourseDetail.description }}</p>
        </div>
        
        <div class="course-syllabus">
          <h4>教学大纲</h4>
          <ul>
            <li v-for="item in selectedCourseDetail.syllabus" :key="item">
              {{ item }}
            </li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="!selectedCourseDetail.isSelected"
          type="primary"
          :disabled="!isSelectable(selectedCourseDetail)"
          @click="selectCourse(selectedCourseDetail)"
        >
          选择此课程
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { courseApi } from '@/api/course'
import { studentApi } from '@/api/student'

const searchQuery = ref('')
const filterDepartment = ref('')
const filterCredits = ref('')
const filterTime = ref('')
const detailDialogVisible = ref(false)
const selectedCourseDetail = ref(null)
const selectedCourses = ref([])
const maxCourses = ref(8)

const selectionStatus = ref({
  title: '选课进行中',
  type: 'success',
  description: '选课时间：2024年6月1日 - 2024年6月15日，请及时完成选课。'
})

const selectionStats = ref({
  available: 156,
  selected: 3,
  credits: 9,
  remaining: 245
})

const availableCourses = ref([])
const loading = ref(false)

// 加载可选课程
const loadAvailableCourses = async () => {
  loading.value = true
  try {
    const { data } = await courseApi.getAvailableCourses({
      search: searchQuery.value,
      department: filterDepartment.value,
      credits: filterCredits.value,
      time: filterTime.value
    })

    const courseList = Array.isArray(data) ? data : (data.courses || data.list || [])
    availableCourses.value = courseList.map(course => ({
      id: course.id,
      name: course.courseName || course.name,
      code: course.courseCode || course.code,
      teacher: course.teacherName || course.teacher,
      department: course.departmentName || course.department,
      credits: course.credits || 0,
      schedule: course.schedule || formatSchedule(course),
      location: course.location || course.classroom || course.classroomName,
      capacity: course.capacity || course.maxStudents || 0,
      enrolled: course.enrolled || course.currentStudents || 0,
      status: getSelectionStatus(course),
      isSelected: course.isSelected || false,
      description: course.description || course.courseDescription,
      syllabus: course.syllabus || course.outline || [],
      selectionPeriodId: course.selectionPeriodId,
      prerequisites: course.prerequisites || []
    }))

    updateSelectionStats()
  } catch (error) {
    console.error('加载可选课程失败:', error)
    ElMessage.error('加载课程数据失败')
    availableCourses.value = []
  } finally {
    loading.value = false
  }
}

// 加载选课状态
const loadSelectionStatus = async () => {
  try {
    const { data } = await courseApi.getSelectionStatus()
    selectionStatus.value = {
      title: data.isOpen ? '选课进行中' : '选课已结束',
      type: data.isOpen ? 'success' : 'warning',
      description: data.description || `选课时间：${data.startTime} - ${data.endTime}`
    }
    maxCourses.value = data.maxCourses || 8
  } catch (error) {
    console.error('加载选课状态失败:', error)
    selectionStatus.value = {
      title: '选课状态未知',
      type: 'info',
      description: '无法获取选课状态信息'
    }
  }
}

// 格式化课程时间
const formatSchedule = (course) => {
  if (course.schedule) return course.schedule
  if (course.dayOfWeek && course.startTime && course.endTime) {
    const dayMap = ['日', '一', '二', '三', '四', '五', '六']
    return `周${dayMap[course.dayOfWeek]} ${course.startTime}-${course.endTime}`
  }
  return '时间待定'
}

// 获取选课状态
const getSelectionStatus = (course) => {
  if (course.isSelected) return '已选'
  if (course.enrolled >= course.capacity) return '已满'
  if (course.selectionStatus === 'closed') return '已关闭'
  return '可选'
}

// 更新选课统计
const updateSelectionStats = () => {
  const selectedList = availableCourses.value.filter(c => c.isSelected)
  selectionStats.value = {
    available: availableCourses.value.filter(c => c.status === '可选').length,
    selected: selectedList.length,
    credits: selectedList.reduce((sum, c) => sum + c.credits, 0),
    remaining: availableCourses.value.reduce((sum, c) => sum + (c.capacity - c.enrolled), 0)
  }
}

const filteredCourses = computed(() => {
  return availableCourses.value.filter(course => {
    const matchesSearch = !searchQuery.value || 
      course.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      course.code.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesDepartment = !filterDepartment.value || 
      getDepartmentValue(course.department) === filterDepartment.value
    
    const matchesCredits = !filterCredits.value || 
      isInCreditsRange(course.credits, filterCredits.value)
    
    const matchesTime = !filterTime.value || 
      matchesTimeFilter(course.schedule, filterTime.value)
    
    return matchesSearch && matchesDepartment && matchesCredits && matchesTime
  })
})

const getDepartmentValue = (department) => {
  const deptMap = {
    '计算机学院': 'cs',
    '数学学院': 'math',
    '物理学院': 'physics',
    '外语学院': 'foreign'
  }
  return deptMap[department]
}

const isInCreditsRange = (credits, range) => {
  const [min, max] = range.split('-').map(Number)
  return credits >= min && credits <= max
}

const matchesTimeFilter = (schedule, timeFilter) => {
  // 简单的时间匹配逻辑
  return true
}

const getStatusType = (status) => {
  const statusMap = {
    '可选': 'success',
    '已满': 'danger',
    '已选': 'info'
  }
  return statusMap[status] || 'info'
}

const isSelectable = (course) => {
  return course.status === '可选' && !course.isSelected && 
         selectedCourses.value.length < maxCourses.value
}

const handleSearch = () => {
  ElMessage.success('搜索完成')
}

const handleSelectionChange = (selection) => {
  selectedCourses.value = selection.filter(course => !course.isSelected)
}

const selectCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要选择课程《${course.name}》吗？`,
      '确认选课',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用后端API进行选课
    const { data } = await courseApi.selectCourse({
      courseId: course.id,
      selectionPeriodId: course.selectionPeriodId
    })

    if (data.success) {
      course.isSelected = true
      course.status = '已选'
      course.enrolled += 1
      updateSelectionStats()

      ElMessage.success(`成功选择课程《${course.name}》`)
      detailDialogVisible.value = false
    } else {
      ElMessage.error(data.message || '选课失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('选课失败:', error)
      ElMessage.error('选课失败，请重试')
    }
  }
}

const dropCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要退选课程《${course.name}》吗？`,
      '确认退选',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用后端API进行退选
    const { data } = await courseApi.dropCourse({
      courseId: course.id,
      selectionPeriodId: course.selectionPeriodId
    })

    if (data.success) {
      course.isSelected = false
      course.status = '可选'
      course.enrolled -= 1

      // 从已选课程列表中移除
      const index = selectedCourses.value.findIndex(c => c.id === course.id)
      if (index > -1) {
        selectedCourses.value.splice(index, 1)
      }

      updateSelectionStats()
      ElMessage.success(`成功退选课程《${course.name}》`)
    } else {
      ElMessage.error(data.message || '退选失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退选失败:', error)
      ElMessage.error('退选失败，请重试')
    }
  }
}

const batchConfirmSelection = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要确认选择这 ${selectedCourses.value.length} 门课程吗？确认后将无法更改。`,
      '批量确认选课',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('选课确认成功')
    selectedCourses.value = []
  } catch {
    // 用户取消
  }
}

const clearSelection = () => {
  selectedCourses.value = []
  ElMessage.info('已清空选择')
}

const viewCourseDetail = (course) => {
  selectedCourseDetail.value = course
  detailDialogVisible.value = true
}

onMounted(async () => {
  // 加载选课相关数据
  await Promise.all([
    loadSelectionStatus(),
    loadAvailableCourses()
  ])

  // 初始化已选课程
  selectedCourses.value = availableCourses.value.filter(course => course.isSelected)
})
</script>

<style scoped>
@import '@/styles/student.css';
</style>