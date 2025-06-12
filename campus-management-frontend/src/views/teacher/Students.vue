<template>
  <div class="teacher-students">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1>学生管理</h1>
          <p>查看和管理班级学生信息</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="exportStudents">
            <el-icon><Download /></el-icon>
            导出学生
          </el-button>
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>
    </div>

    <!-- 筛选工具栏 -->
    <el-card class="filter-card" shadow="never">
      <el-row :gutter="20" align="middle">
        <el-col :xs="24" :sm="8" :md="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索学生姓名或学号..."
            clearable
            :prefix-icon="Search"
            @keyup.enter="searchStudents"
            @clear="searchStudents"
          />
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterClass" placeholder="选择班级" clearable @change="searchStudents">
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.className"
              :value="cls.id"
            />
          </el-select>
        </el-col>
        <el-col :xs="12" :sm="6" :md="4">
          <el-select v-model="filterStatus" placeholder="学生状态" clearable @change="searchStudents">
            <el-option label="全部" value="" />
            <el-option label="在校" value="active" />
            <el-option label="休学" value="suspended" />
            <el-option label="毕业" value="graduated" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="24" :md="10">
          <div class="filter-actions">
            <el-button @click="searchStudents" :loading="loading">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetFilters">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon students">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ studentStats.total }}</h3>
              <p>总学生数</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon active">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ studentStats.active }}</h3>
              <p>在校学生</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon average">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ studentStats.avgGrade }}</h3>
              <p>平均成绩</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon attendance">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <h3>{{ studentStats.attendanceRate }}%</h3>
              <p>出勤率</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学生列表 -->
    <el-card class="students-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>学生列表</h3>
          <div class="header-controls">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="table">
                <el-icon><List /></el-icon>
                表格
              </el-radio-button>
              <el-radio-button label="card">
                <el-icon><Grid /></el-icon>
                卡片
              </el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <div v-loading="loading">
        <!-- 表格视图 -->
        <div v-if="viewMode === 'table'">
          <el-table
            :data="students"
            style="width: 100%"
            @row-click="viewStudentDetail"
          >
            <el-table-column type="selection" width="55" />

            <el-table-column label="学生信息" min-width="200">
              <template #default="{ row }">
                <div class="student-info">
                  <el-avatar :size="40" :src="row.avatar">
                    <el-icon><User /></el-icon>
                  </el-avatar>
                  <div class="info-text">
                    <div class="name">{{ row.name }}</div>
                    <div class="student-id">{{ row.studentId }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="className" label="班级" width="120" />

            <el-table-column prop="gender" label="性别" width="80" align="center">
              <template #default="{ row }">
                {{ getGenderText(row.gender) }}
              </template>
            </el-table-column>

            <el-table-column prop="phone" label="联系电话" width="130" />

            <el-table-column prop="email" label="邮箱" min-width="150" />

            <el-table-column label="平均成绩" width="100" align="center">
              <template #default="{ row }">
                <span :class="getGradeClass(row.avgGrade)">
                  {{ row.avgGrade || '-' }}
                </span>
              </template>
            </el-table-column>

            <el-table-column label="出勤率" width="100" align="center">
              <template #default="{ row }">
                <el-progress
                  :percentage="row.attendanceRate || 0"
                  :color="getAttendanceColor(row.attendanceRate)"
                  :stroke-width="6"
                  :show-text="false"
                />
                <div class="attendance-text">{{ row.attendanceRate || 0 }}%</div>
              </template>
            </el-table-column>

            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusTag(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button size="small" @click.stop="viewStudentDetail(row)">
                  详情
                </el-button>
                <el-button size="small" @click.stop="viewGrades(row)">
                  成绩
                </el-button>
                <el-button size="small" @click.stop="viewAttendance(row)">
                  考勤
                </el-button>
                <el-dropdown @command="handleCommand" trigger="click">
                  <el-button size="small">
                    更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item :command="{action: 'edit', student: row}">编辑信息</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'contact', student: row}">联系家长</el-dropdown-item>
                      <el-dropdown-item :command="{action: 'report', student: row}">生成报告</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 卡片视图 -->
        <div v-else class="students-grid">
          <div
            v-for="student in students"
            :key="student.id"
            class="student-card"
            @click="viewStudentDetail(student)"
          >
            <div class="card-header">
              <el-avatar :size="60" :src="student.avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <div class="student-basic">
                <h4>{{ student.name }}</h4>
                <p>{{ student.studentId }}</p>
                <el-tag :type="getStatusTag(student.status)" size="small">
                  {{ getStatusText(student.status) }}
                </el-tag>
              </div>
            </div>

            <div class="card-content">
              <div class="info-row">
                <span class="label">班级：</span>
                <span>{{ student.className }}</span>
              </div>
              <div class="info-row">
                <span class="label">性别：</span>
                <span>{{ getGenderText(student.gender) }}</span>
              </div>
              <div class="info-row">
                <span class="label">联系电话：</span>
                <span>{{ student.phone }}</span>
              </div>
              <div class="info-row">
                <span class="label">平均成绩：</span>
                <span :class="getGradeClass(student.avgGrade)">
                  {{ student.avgGrade || '-' }}
                </span>
              </div>
              <div class="info-row">
                <span class="label">出勤率：</span>
                <span>{{ student.attendanceRate || 0 }}%</span>
              </div>
            </div>

            <div class="card-actions">
              <el-button size="small" @click.stop="viewGrades(student)">
                成绩
              </el-button>
              <el-button size="small" @click.stop="viewAttendance(student)">
                考勤
              </el-button>
              <el-button size="small" type="primary" @click.stop="viewStudentDetail(student)">
                详情
              </el-button>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <el-empty v-if="!loading && students.length === 0" description="暂无学生数据">
          <el-button type="primary" @click="refreshData">刷新数据</el-button>
        </el-empty>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  User,
  Search,
  Download,
  Refresh,
  CircleCheck,
  TrendCharts,
  Calendar,
  List,
  Grid,
  ArrowDown
} from '@element-plus/icons-vue'
import { teacherApi } from '@/api/teacher'
import { studentApi } from '@/api/student'
import { useRouter } from 'vue-router'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const filterClass = ref('')
const filterStatus = ref('')
const viewMode = ref('table')
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const students = ref([])
const classList = ref([])

const studentStats = reactive({
  total: 0,
  active: 0,
  avgGrade: 0,
  attendanceRate: 0
})

// 方法
const loadStudentStats = async () => {
  try {
    const { data } = await teacherApi.getStudentStats()
    studentStats.total = data.total || 0
    studentStats.active = data.active || 0
    studentStats.avgGrade = data.avgGrade || 0
    studentStats.attendanceRate = data.attendanceRate || 0
  } catch (error) {
    console.error('加载学生统计失败:', error)
  }
}

const loadClassList = async () => {
  try {
    const { data } = await teacherApi.getTeacherClasses()
    classList.value = data.classes || data.list || []
  } catch (error) {
    console.error('加载班级列表失败:', error)
  }
}

const loadStudents = async () => {
  loading.value = true

  try {
    const { data } = await teacherApi.getStudents({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchQuery.value,
      classId: filterClass.value,
      status: filterStatus.value
    })

    students.value = data.students || data.list || []
    total.value = data.total || data.totalElements || students.value.length

  } catch (error) {
    console.error('加载学生列表失败:', error)
    ElMessage.error('加载学生列表失败')
    students.value = []
  } finally {
    loading.value = false
  }
}

const searchStudents = async () => {
  currentPage.value = 1
  await loadStudents()
}

const resetFilters = () => {
  searchQuery.value = ''
  filterClass.value = ''
  filterStatus.value = ''
  currentPage.value = 1
  loadStudents()
}

const refreshData = async () => {
  await Promise.all([
    loadStudentStats(),
    loadStudents()
  ])
  ElMessage.success('数据刷新成功')
}

const handleSizeChange = async (size) => {
  pageSize.value = size
  currentPage.value = 1
  await loadStudents()
}

const handleCurrentChange = async (page) => {
  currentPage.value = page
  await loadStudents()
}

const viewStudentDetail = (student) => {
  router.push(`/teacher/students/${student.id}`)
}

const viewGrades = (student) => {
  router.push(`/teacher/grades?studentId=${student.id}`)
}

const viewAttendance = (student) => {
  router.push(`/teacher/attendance?studentId=${student.id}`)
}

const handleCommand = (command) => {
  const { action, student } = command

  switch (action) {
    case 'edit':
      ElMessage.info(`编辑学生 ${student.name} 的信息`)
      break
    case 'contact':
      ElMessage.info(`联系学生 ${student.name} 的家长`)
      break
    case 'report':
      ElMessage.info(`生成学生 ${student.name} 的报告`)
      break
  }
}

const exportStudents = () => {
  ElMessage.info('导出学生功能开发中...')
}

// 辅助方法
const getGenderText = (gender) => {
  const genderMap = {
    'male': '男',
    'female': '女'
  }
  return genderMap[gender] || '-'
}

const getGradeClass = (grade) => {
  if (!grade) return ''
  if (grade >= 90) return 'excellent'
  if (grade >= 80) return 'good'
  if (grade >= 70) return 'average'
  if (grade >= 60) return 'pass'
  return 'fail'
}

const getAttendanceColor = (rate) => {
  if (rate >= 95) return '#67c23a'
  if (rate >= 85) return '#409eff'
  if (rate >= 75) return '#e6a23c'
  return '#f56c6c'
}

const getStatusTag = (status) => {
  const statusMap = {
    'active': 'success',
    'suspended': 'warning',
    'graduated': 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'active': '在校',
    'suspended': '休学',
    'graduated': '毕业'
  }
  return textMap[status] || status
}

// 生命周期
onMounted(async () => {
  await Promise.all([
    loadStudentStats(),
    loadClassList(),
    loadStudents()
  ])
})
</script>

<style scoped>
@import '@/styles/teacher.css';

.teacher-students {
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

.header-actions {
  display: flex;
  gap: 12px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.students {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-icon.active {
  background: linear-gradient(135deg, #67c23a, #85ce61);
}

.stat-icon.average {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.stat-icon.attendance {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.stat-info h3 {
  margin: 0 0 4px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.students-card {
  background: white;
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #303133;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.info-text .name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.info-text .student-id {
  font-size: 12px;
  color: #909399;
}

.attendance-text {
  font-size: 12px;
  color: #606266;
  text-align: center;
  margin-top: 4px;
}

.excellent { color: #67c23a; font-weight: 600; }
.good { color: #409eff; font-weight: 600; }
.average { color: #e6a23c; font-weight: 600; }
.pass { color: #f56c6c; font-weight: 600; }
.fail { color: #909399; font-weight: 600; }

.students-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.student-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.student-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.student-card .card-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.student-basic h4 {
  margin: 0 0 4px 0;
  color: #303133;
}

.student-basic p {
  margin: 0 0 8px 0;
  color: #909399;
  font-size: 12px;
}

.card-content {
  margin-bottom: 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row .label {
  color: #606266;
}

.card-actions {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

@media (max-width: 768px) {
  .teacher-students {
    padding: 10px;
  }

  .header-content {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }

  .header-actions {
    align-self: stretch;
    justify-content: center;
  }

  .filter-actions {
    justify-content: center;
    margin-top: 16px;
  }

  .students-grid {
    grid-template-columns: 1fr;
  }

  .student-card .card-header {
    flex-direction: column;
    text-align: center;
  }

  .card-actions {
    flex-direction: column;
  }
}
</style>
