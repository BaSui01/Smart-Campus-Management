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

const availableCourses = ref([
  {
    id: 1,
    name: '数据库系统原理',
    code: 'CS401',
    teacher: '张教授',
    department: '计算机学院',
    credits: 3,
    schedule: '周二 10:00-11:40',
    location: '教学楼A201',
    capacity: 40,
    enrolled: 35,
    status: '可选',
    isSelected: false,
    description: '数据库系统原理是计算机专业的核心课程，主要介绍数据库的基本概念、设计原理和实现技术。',
    syllabus: [
      '数据库系统概述',
      '关系数据库理论',
      'SQL语言及应用',
      '数据库设计',
      '查询优化',
      '事务处理',
      '并发控制',
      '数据库恢复'
    ]
  },
  {
    id: 2,
    name: '人工智能导论',
    code: 'CS501',
    teacher: '李教授',
    department: '计算机学院',
    credits: 4,
    schedule: '周三 14:00-15:40',
    location: '实验楼B301',
    capacity: 30,
    enrolled: 28,
    status: '可选',
    isSelected: false,
    description: '人工智能导论课程介绍人工智能的基本概念、发展历史和主要技术。',
    syllabus: [
      '人工智能概述',
      '搜索策略',
      '知识表示',
      '机器学习基础',
      '神经网络',
      '专家系统',
      'AI应用实例'
    ]
  },
  {
    id: 3,
    name: '软件工程',
    code: 'CS301',
    teacher: '王老师',
    department: '计算机学院',
    credits: 3,
    schedule: '周四 8:00-9:40',
    location: '教学楼C101',
    capacity: 50,
    enrolled: 50,
    status: '已满',
    isSelected: false,
    description: '软件工程课程教授软件开发的方法论、过程和工具。',
    syllabus: [
      '软件工程概述',
      '软件过程模型',
      '需求工程',
      '系统设计',
      '编码实现',
      '软件测试',
      '项目管理'
    ]
  },
  {
    id: 4,
    name: '线性代数',
    code: 'MATH201',
    teacher: '赵教授',
    department: '数学学院',
    credits: 4,
    schedule: '周一 8:00-9:40',
    location: '教学楼D201',
    capacity: 60,
    enrolled: 45,
    status: '可选',
    isSelected: true,
    description: '线性代数是数学的重要分支，在计算机科学中有广泛应用。',
    syllabus: [
      '矩阵运算',
      '线性方程组',
      '向量空间',
      '特征值与特征向量',
      '二次型',
      '线性变换'
    ]
  }
])

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
    
    course.isSelected = true
    course.status = '已选'
    selectionStats.value.selected++
    selectionStats.value.credits += course.credits
    
    ElMessage.success(`成功选择课程《${course.name}》`)
    detailDialogVisible.value = false
  } catch {
    // 用户取消
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
    
    course.isSelected = false
    course.status = '可选'
    selectionStats.value.selected--
    selectionStats.value.credits -= course.credits
    
    // 从已选课程列表中移除
    const index = selectedCourses.value.findIndex(c => c.id === course.id)
    if (index > -1) {
      selectedCourses.value.splice(index, 1)
    }
    
    ElMessage.success(`成功退选课程《${course.name}》`)
  } catch {
    // 用户取消
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

onMounted(() => {
  // 初始化已选课程
  selectedCourses.value = availableCourses.value.filter(course => course.isSelected)
})
</script>

<style scoped>
.course-selection-page {
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

.status-alert {
  margin-bottom: 30px;
}

.filter-card {
  margin-bottom: 20px;
}

.selection-summary {
  display: flex;
  align-items: center;
  height: 32px;
  font-weight: 600;
  color: #409eff;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  text-align: center;
  height: 100px;
}

.stat-content {
  padding: 20px 0;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.courses-card {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #333;
}

.course-name strong {
  color: #333;
  font-size: 16px;
}

.course-code {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

.selected-courses-card {
  margin-bottom: 30px;
}

.selected-courses-list {
  display: grid;
  gap: 12px;
}

.selected-course-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.selected-course-item .course-info h4 {
  margin: 0 0 4px 0;
  color: #333;
}

.selected-course-item .course-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.course-detail {
  margin-bottom: 20px;
}

.course-description,
.course-syllabus {
  margin-top: 20px;
}

.course-description h4,
.course-syllabus h4 {
  margin: 0 0 12px 0;
  color: #333;
}

.course-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.course-syllabus ul {
  margin: 0;
  padding-left: 20px;
  color: #666;
}

.course-syllabus li {
  margin-bottom: 4px;
}
</style>