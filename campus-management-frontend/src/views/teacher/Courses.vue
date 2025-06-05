<template>
  <div class="teacher-courses">
    <div class="page-header">
      <h1>课程管理</h1>
      <p>管理您负责的课程信息</p>
    </div>
    
    <!-- 操作工具栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索课程名称或代码"
            clearable
            :prefix-icon="Search"
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterSemester" placeholder="选择学期" clearable>
            <el-option label="2024春季" value="2024-spring" />
            <el-option label="2024秋季" value="2024-autumn" />
            <el-option label="2023春季" value="2023-spring" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="filterStatus" placeholder="课程状态" clearable>
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="ended" />
            <el-option label="未开始" value="pending" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button type="success" @click="showCreateDialog">
            <el-icon><Plus /></el-icon>
            新建课程
          </el-button>
        </el-col>
        <el-col :span="4">
          <el-button @click="exportCourses">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 课程统计 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ courseStats.total }}</div>
            <div class="stat-label">总课程数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ courseStats.ongoing }}</div>
            <div class="stat-label">进行中</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ courseStats.totalStudents }}</div>
            <div class="stat-label">选课学生</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ courseStats.avgScore }}</div>
            <div class="stat-label">平均成绩</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 课程列表 -->
    <el-card class="courses-table-card">
      <template #header>
        <div class="card-header">
          <h3>课程列表</h3>
          <div class="header-actions">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="table">表格视图</el-radio-button>
              <el-radio-button label="card">卡片视图</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
      
      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'">
        <el-table
          :data="filteredCourses"
          style="width: 100%"
          @row-click="viewCourseDetail"
        >
          <el-table-column prop="name" label="课程名称" min-width="150">
            <template #default="{ row }">
              <div class="course-name">
                <strong>{{ row.name }}</strong>
                <div class="course-code">{{ row.code }}</div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="credits" label="学分" width="80" align="center" />
          
          <el-table-column prop="schedule" label="上课时间" width="150" />
          
          <el-table-column prop="location" label="上课地点" width="120" />
          
          <el-table-column prop="capacity" label="容量" width="100" align="center">
            <template #default="{ row }">
              {{ row.enrolled }}/{{ row.capacity }}
            </template>
          </el-table-column>
          
          <el-table-column prop="semester" label="学期" width="100" align="center" />
          
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click.stop="viewCourseDetail(row)">
                详情
              </el-button>
              <el-button size="small" @click.stop="manageStudents(row)">
                学生
              </el-button>
              <el-button size="small" type="primary" @click.stop="editCourse(row)">
                编辑
              </el-button>
              <el-button size="small" type="danger" @click.stop="deleteCourse(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 卡片视图 -->
      <div v-else class="courses-grid">
        <div
          v-for="course in filteredCourses"
          :key="course.id"
          class="course-card"
        >
          <div class="course-header">
            <div class="course-info">
              <h3>{{ course.name }}</h3>
              <p>{{ course.code }}</p>
            </div>
            <el-tag :type="getStatusType(course.status)">
              {{ course.status }}
            </el-tag>
          </div>
          
          <div class="course-details">
            <div class="detail-item">
              <el-icon><Clock /></el-icon>
              <span>{{ course.schedule }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Location /></el-icon>
              <span>{{ course.location }}</span>
            </div>
            <div class="detail-item">
              <el-icon><Star /></el-icon>
              <span>{{ course.credits }} 学分</span>
            </div>
            <div class="detail-item">
              <el-icon><UserFilled /></el-icon>
              <span>{{ course.enrolled }}/{{ course.capacity }} 人</span>
            </div>
          </div>
          
          <div class="course-actions">
            <el-button size="small" @click="viewCourseDetail(course)">
              详情
            </el-button>
            <el-button size="small" @click="manageStudents(course)">
              学生管理
            </el-button>
            <el-button size="small" type="primary" @click="editCourse(course)">
              编辑
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
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
      title="课程详情"
      width="600px"
    >
      <div v-if="selectedCourse" class="course-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="课程名称">
            {{ selectedCourse.name }}
          </el-descriptions-item>
          <el-descriptions-item label="课程代码">
            {{ selectedCourse.code }}
          </el-descriptions-item>
          <el-descriptions-item label="学分">
            {{ selectedCourse.credits }}
          </el-descriptions-item>
          <el-descriptions-item label="上课时间">
            {{ selectedCourse.schedule }}
          </el-descriptions-item>
          <el-descriptions-item label="上课地点">
            {{ selectedCourse.location }}
          </el-descriptions-item>
          <el-descriptions-item label="课程容量">
            {{ selectedCourse.enrolled }}/{{ selectedCourse.capacity }}
          </el-descriptions-item>
          <el-descriptions-item label="学期">
            {{ selectedCourse.semester }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(selectedCourse.status)">
              {{ selectedCourse.status }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        
        <div class="course-description">
          <h4>课程简介</h4>
          <p>{{ selectedCourse.description }}</p>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="editCourse(selectedCourse)">
          编辑课程
        </el-button>
      </template>
    </el-dialog>
    
    <!-- 创建/编辑课程对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEditing ? '编辑课程' : '创建课程'"
      width="500px"
    >
      <el-form
        ref="courseFormRef"
        :model="courseForm"
        :rules="courseRules"
        label-width="100px"
      >
        <el-form-item label="课程名称" prop="name">
          <el-input v-model="courseForm.name" placeholder="请输入课程名称" />
        </el-form-item>
        
        <el-form-item label="课程代码" prop="code">
          <el-input v-model="courseForm.code" placeholder="请输入课程代码" />
        </el-form-item>
        
        <el-form-item label="学分" prop="credits">
          <el-input-number
            v-model="courseForm.credits"
            :min="1"
            :max="6"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="上课时间" prop="schedule">
          <el-input v-model="courseForm.schedule" placeholder="如：周一 08:00-09:40" />
        </el-form-item>
        
        <el-form-item label="上课地点" prop="location">
          <el-input v-model="courseForm.location" placeholder="请输入上课地点" />
        </el-form-item>
        
        <el-form-item label="课程容量" prop="capacity">
          <el-input-number
            v-model="courseForm.capacity"
            :min="1"
            :max="200"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="学期" prop="semester">
          <el-select v-model="courseForm.semester" style="width: 100%">
            <el-option label="2024春季" value="2024-spring" />
            <el-option label="2024秋季" value="2024-autumn" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="课程简介" prop="description">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课程简介"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">
          {{ isEditing ? '更新' : '创建' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, 
  Plus, 
  Download, 
  Clock, 
  Location, 
  Star, 
  UserFilled 
} from '@element-plus/icons-vue'

const router = useRouter()

const searchQuery = ref('')
const filterSemester = ref('')
const filterStatus = ref('')
const viewMode = ref('table')
const detailDialogVisible = ref(false)
const formDialogVisible = ref(false)
const isEditing = ref(false)
const selectedCourse = ref(null)
const courseFormRef = ref()
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const courseStats = ref({
  total: 4,
  ongoing: 3,
  totalStudents: 156,
  avgScore: 85.2
})

const courses = ref([
  {
    id: 1,
    name: '数据结构',
    code: 'CS301',
    credits: 3,
    schedule: '周一 08:00-09:40',
    location: '教学楼A201',
    capacity: 50,
    enrolled: 42,
    semester: '2024-spring',
    status: '进行中',
    description: '数据结构是计算机科学的核心课程，主要讲解各种数据组织方式和相关算法。'
  },
  {
    id: 2,
    name: '算法设计',
    code: 'CS401',
    credits: 4,
    schedule: '周三 14:00-15:40',
    location: '教学楼B303',
    capacity: 45,
    enrolled: 38,
    semester: '2024-spring',
    status: '进行中',
    description: '算法设计课程教授算法分析与设计的基本方法和技巧。'
  },
  {
    id: 3,
    name: '数据库原理',
    code: 'CS501',
    credits: 3,
    schedule: '周五 10:00-11:40',
    location: '实验楼C201',
    capacity: 40,
    enrolled: 35,
    semester: '2024-spring',
    status: '进行中',
    description: '数据库原理课程介绍数据库系统的基本概念和设计方法。'
  },
  {
    id: 4,
    name: '软件工程',
    code: 'CS601',
    credits: 2,
    schedule: '周二 16:00-17:40',
    location: '教学楼D401',
    capacity: 60,
    enrolled: 0,
    semester: '2024-autumn',
    status: '未开始',
    description: '软件工程课程教授软件开发的方法论和最佳实践。'
  }
])

const courseForm = reactive({
  name: '',
  code: '',
  credits: 3,
  schedule: '',
  location: '',
  capacity: 50,
  semester: '',
  description: ''
})

const courseRules = {
  name: [
    { required: true, message: '请输入课程名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入课程代码', trigger: 'blur' }
  ],
  credits: [
    { required: true, message: '请选择学分', trigger: 'change' }
  ],
  schedule: [
    { required: true, message: '请输入上课时间', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入上课地点', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入课程容量', trigger: 'change' }
  ],
  semester: [
    { required: true, message: '请选择学期', trigger: 'change' }
  ]
}

const filteredCourses = computed(() => {
  return courses.value.filter(course => {
    const matchesSearch = !searchQuery.value || 
      course.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      course.code.toLowerCase().includes(searchQuery.value.toLowerCase())
    
    const matchesSemester = !filterSemester.value || 
      course.semester === filterSemester.value
    
    const matchesStatus = !filterStatus.value || 
      getStatusValue(course.status) === filterStatus.value
    
    return matchesSearch && matchesSemester && matchesStatus
  })
})

const getStatusType = (status) => {
  const statusMap = {
    '进行中': 'success',
    '已结束': 'info',
    '未开始': 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusValue = (status) => {
  const statusMap = {
    '进行中': 'ongoing',
    '已结束': 'ended',
    '未开始': 'pending'
  }
  return statusMap[status]
}

const handleSearch = () => {
  ElMessage.success('搜索完成')
}

const exportCourses = () => {
  ElMessage.info('导出功能开发中...')
}

const showCreateDialog = () => {
  isEditing.value = false
  resetForm()
  formDialogVisible.value = true
}

const viewCourseDetail = (course) => {
  selectedCourse.value = course
  detailDialogVisible.value = true
}

const editCourse = (course) => {
  isEditing.value = true
  Object.assign(courseForm, course)
  formDialogVisible.value = true
  detailDialogVisible.value = false
}

const deleteCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除课程《${course.name}》吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const index = courses.value.findIndex(c => c.id === course.id)
    if (index > -1) {
      courses.value.splice(index, 1)
      ElMessage.success('课程删除成功')
    }
  } catch {
    // 用户取消
  }
}

const manageStudents = (course) => {
  router.push(`/teacher/courses/${course.id}/students`)
}

const saveCourse = async () => {
  if (!courseFormRef.value) return
  
  await courseFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      if (isEditing.value) {
        const index = courses.value.findIndex(c => c.id === selectedCourse.value?.id)
        if (index > -1) {
          courses.value[index] = { ...courses.value[index], ...courseForm }
        }
        ElMessage.success('课程更新成功')
      } else {
        const newCourse = {
          id: Date.now(),
          ...courseForm,
          enrolled: 0,
          status: '未开始'
        }
        courses.value.push(newCourse)
        ElMessage.success('课程创建成功')
      }
      
      formDialogVisible.value = false
    } catch (error) {
      ElMessage.error('操作失败，请稍后重试')
    }
  })
}

const resetForm = () => {
  Object.assign(courseForm, {
    name: '',
    code: '',
    credits: 3,
    schedule: '',
    location: '',
    capacity: 50,
    semester: '',
    description: ''
  })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  // 重新加载数据
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  // 重新加载数据
}

onMounted(() => {
  total.value = courses.value.length
})
</script>


<style>
@import '@/styles/teacher.css';
</style>