<template>
  <div class="assignment-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>作业管理</h2>
        <p>管理课程作业，查看学生提交情况</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog = true">
          <el-icon><Plus /></el-icon>
          创建作业
        </el-button>
      </div>
    </div>

    <!-- 筛选搜索 -->
    <el-card class="filter-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="作业标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入作业标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="课程">
          <el-select v-model="searchForm.courseId" placeholder="选择课程" clearable style="width: 200px">
            <el-option
              v-for="course in courses"
              :key="course.id"
              :label="course.courseName"
              :value="course.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作业类型">
          <el-select v-model="searchForm.assignmentType" placeholder="选择类型" clearable style="width: 150px">
            <el-option label="作业" value="homework" />
            <el-option label="项目" value="project" />
            <el-option label="报告" value="report" />
            <el-option label="实验" value="experiment" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态">
          <el-select v-model="searchForm.isPublished" placeholder="选择状态" clearable style="width: 120px">
            <el-option label="已发布" :value="true" />
            <el-option label="未发布" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchAssignments">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#409eff"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.totalAssignments }}</div>
              <div class="stat-label">总作业数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#67c23a"><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.publishedAssignments }}</div>
              <div class="stat-label">已发布</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#e6a23c"><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.pendingGrade }}</div>
              <div class="stat-label">待批改</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon size="32" color="#f56c6c"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ statistics.overdueAssignments }}</div>
              <div class="stat-label">已过期</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 作业列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>作业列表</span>
          <div class="header-actions">
            <el-button size="small" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="assignments"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="title" label="作业标题" min-width="150">
          <template #default="{ row }">
            <div class="assignment-title">
              <span class="title-text">{{ row.title }}</span>
              <el-tag v-if="!row.isPublished" type="warning" size="small">未发布</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="courseName" label="课程" width="120" />

        <el-table-column prop="assignmentType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeColor(row.assignmentType)" size="small">
              {{ getTypeText(row.assignmentType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="maxScore" label="满分" width="80" align="center" />

        <el-table-column prop="dueDate" label="截止时间" width="150">
          <template #default="{ row }">
            <div :class="getDueDateClass(row.dueDate)">
              {{ formatDateTime(row.dueDate) }}
            </div>
          </template>
        </el-table-column>

        <el-table-column label="提交情况" width="120" align="center">
          <template #default="{ row }">
            <div class="submission-stats">
              <span>{{ row.totalSubmissions }} / {{ row.enrolledStudents }}</span>
              <el-progress
                :percentage="getSubmissionPercentage(row)"
                :stroke-width="4"
                :show-text="false"
                :color="getProgressColor(row)"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column label="批改进度" width="120" align="center">
          <template #default="{ row }">
            <div class="grading-stats">
              <span>{{ row.gradedCount }} / {{ row.totalSubmissions }}</span>
              <el-progress
                :percentage="getGradingPercentage(row)"
                :stroke-width="4"
                :show-text="false"
                color="#67c23a"
              />
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="averageScore" label="平均分" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.averageScore">{{ row.averageScore.toFixed(1) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="viewAssignment(row)">详情</el-button>
            <el-button size="small" type="primary" @click="viewSubmissions(row)">
              提交({{ row.totalSubmissions }})
            </el-button>
            <el-dropdown @command="handleCommand($event, row)">
              <el-button size="small">
                更多<el-icon><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="copy">复制</el-dropdown-item>
                  <el-dropdown-item v-if="!row.isPublished" command="publish">发布</el-dropdown-item>
                  <el-dropdown-item v-else command="unpublish">取消发布</el-dropdown-item>
                  <el-dropdown-item command="extend">延期</el-dropdown-item>
                  <el-dropdown-item command="export">导出</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 创建/编辑作业对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      :title="editingAssignment ? '编辑作业' : '创建作业'"
      width="800px"
      :before-close="handleDialogClose"
    >
      <assignment-form
        ref="assignmentFormRef"
        :assignment="editingAssignment"
        :courses="courses"
        @submit="handleSubmit"
        @cancel="handleCancel"
      />
    </el-dialog>

    <!-- 作业详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="作业详情"
      width="900px"
    >
      <assignment-detail
        v-if="selectedAssignment"
        :assignment="selectedAssignment"
        @edit="editAssignment"
        @publish="publishAssignment"
        @delete="deleteAssignment"
      />
    </el-dialog>

    <!-- 延期对话框 -->
    <el-dialog
      v-model="showExtendDialog"
      title="延长截止时间"
      width="400px"
    >
      <el-form :model="extendForm" label-width="100px">
        <el-form-item label="新截止时间">
          <el-date-picker
            v-model="extendForm.newDueDate"
            type="datetime"
            placeholder="选择新的截止时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="延期原因">
          <el-input
            v-model="extendForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入延期原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showExtendDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmExtend">确认延期</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Document, Check, Clock, Warning, Refresh, ArrowDown
} from '@element-plus/icons-vue'
import { assignmentApi } from '@/api/assignment'
import { courseApi } from '@/api/course'
import AssignmentForm from '@/components/AssignmentForm.vue'
import AssignmentDetail from '@/components/AssignmentDetail.vue'
import { formatDateTime } from '@/utils/date'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const assignments = ref([])
const courses = ref([])
const selectedAssignments = ref([])
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showExtendDialog = ref(false)
const editingAssignment = ref(null)
const selectedAssignment = ref(null)
const assignmentFormRef = ref(null)

// 搜索表单
const searchForm = reactive({
  title: '',
  courseId: '',
  assignmentType: '',
  isPublished: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 统计数据
const statistics = ref({
  totalAssignments: 0,
  publishedAssignments: 0,
  pendingGrade: 0,
  overdueAssignments: 0
})

// 延期表单
const extendForm = reactive({
  assignmentId: null,
  newDueDate: null,
  reason: ''
})

// 方法
const loadAssignments = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      ...searchForm
    }
    const response = await assignmentApi.getTeacherAssignments(params)
    assignments.value = response.data.list
    pagination.total = response.data.total
  } catch (error) {
    ElMessage.error('加载作业列表失败')
  } finally {
    loading.value = false
  }
}

const loadCourses = async () => {
  try {
    const response = await courseApi.getTeacherCourses()
    courses.value = response.data
  } catch (error) {
    ElMessage.error('加载课程列表失败')
  }
}

const loadStatistics = async () => {
  try {
    const response = await assignmentApi.getTeacherAssignmentStatistics()
    statistics.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const searchAssignments = () => {
  pagination.page = 1
  loadAssignments()
}

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = key === 'isPublished' ? null : ''
  })
  searchAssignments()
}

const refreshData = () => {
  loadAssignments()
  loadStatistics()
}

const handleSelectionChange = (selection) => {
  selectedAssignments.value = selection
}

const viewAssignment = async (assignment) => {
  try {
    const response = await assignmentApi.getAssignmentById(assignment.id)
    selectedAssignment.value = response.data
    showDetailDialog.value = true
  } catch (error) {
    ElMessage.error('加载作业详情失败')
  }
}

const viewSubmissions = (assignment) => {
  router.push(`/teacher/assignment-submissions/${assignment.id}`)
}

const editAssignment = (assignment) => {
  editingAssignment.value = { ...assignment }
  showDetailDialog.value = false
  showCreateDialog.value = true
}

const handleCommand = (command, assignment) => {
  switch (command) {
    case 'edit':
      editAssignment(assignment)
      break
    case 'copy':
      copyAssignment(assignment)
      break
    case 'publish':
      publishAssignment(assignment)
      break
    case 'unpublish':
      unpublishAssignment(assignment)
      break
    case 'extend':
      extendAssignment(assignment)
      break
    case 'export':
      exportAssignment(assignment)
      break
    case 'delete':
      deleteAssignment(assignment)
      break
  }
}

const publishAssignment = async (assignment) => {
  try {
    await assignmentApi.publishAssignment(assignment.id)
    ElMessage.success('作业发布成功')
    refreshData()
  } catch (error) {
    ElMessage.error('发布作业失败')
  }
}

const unpublishAssignment = async (assignment) => {
  try {
    await ElMessageBox.confirm('确认取消发布此作业吗？', '确认操作', {
      type: 'warning'
    })
    
    await assignmentApi.unpublishAssignment(assignment.id)
    ElMessage.success('取消发布成功')
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消发布失败')
    }
  }
}

const copyAssignment = async (assignment) => {
  try {
    const response = await assignmentApi.copyAssignment(assignment.id, assignment.courseId)
    ElMessage.success('作业复制成功')
    refreshData()
  } catch (error) {
    ElMessage.error('复制作业失败')
  }
}

const extendAssignment = (assignment) => {
  extendForm.assignmentId = assignment.id
  extendForm.newDueDate = new Date(assignment.dueDate)
  extendForm.reason = ''
  showExtendDialog.value = true
}

const confirmExtend = async () => {
  try {
    await assignmentApi.extendDueDate(extendForm.assignmentId, extendForm.newDueDate.toISOString())
    ElMessage.success('延期成功')
    showExtendDialog.value = false
    refreshData()
  } catch (error) {
    ElMessage.error('延期失败')
  }
}

const exportAssignment = async (assignment) => {
  try {
    const response = await assignmentApi.exportSubmissions(assignment.id)
    // 处理文件下载
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${assignment.title}_submissions.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const deleteAssignment = async (assignment) => {
  try {
    await ElMessageBox.confirm(
      `确认删除作业"${assignment.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        type: 'warning',
        confirmButtonText: '确认删除',
        confirmButtonClass: 'el-button--danger'
      }
    )
    
    await assignmentApi.deleteAssignment(assignment.id)
    ElMessage.success('删除成功')
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async (formData) => {
  try {
    if (editingAssignment.value) {
      await assignmentApi.updateAssignment(editingAssignment.value.id, formData)
      ElMessage.success('作业更新成功')
    } else {
      await assignmentApi.createAssignment(formData)
      ElMessage.success('作业创建成功')
    }
    
    showCreateDialog.value = false
    editingAssignment.value = null
    refreshData()
  } catch (error) {
    ElMessage.error(editingAssignment.value ? '更新失败' : '创建失败')
  }
}

const handleCancel = () => {
  showCreateDialog.value = false
  editingAssignment.value = null
}

const handleDialogClose = () => {
  if (assignmentFormRef.value?.hasUnsavedChanges()) {
    ElMessageBox.confirm('有未保存的更改，确认关闭吗？', '确认关闭', {
      type: 'warning'
    }).then(() => {
      handleCancel()
    }).catch(() => {})
  } else {
    handleCancel()
  }
}

const handleSizeChange = (size) => {
  pagination.size = size
  loadAssignments()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadAssignments()
}

// 工具方法
const getTypeColor = (type) => {
  const colorMap = {
    homework: 'primary',
    project: 'success',
    report: 'warning',
    experiment: 'info'
  }
  return colorMap[type] || 'info'
}

const getTypeText = (type) => {
  const textMap = {
    homework: '作业',
    project: '项目',
    report: '报告',
    experiment: '实验'
  }
  return textMap[type] || type
}

const getDueDateClass = (dueDate) => {
  const now = new Date()
  const due = new Date(dueDate)
  const diffHours = (due - now) / (1000 * 60 * 60)
  
  if (diffHours < 0) return 'overdue'
  if (diffHours < 24) return 'urgent'
  if (diffHours < 72) return 'warning'
  return 'normal'
}

const getSubmissionPercentage = (assignment) => {
  if (assignment.enrolledStudents === 0) return 0
  return Math.round((assignment.totalSubmissions / assignment.enrolledStudents) * 100)
}

const getGradingPercentage = (assignment) => {
  if (assignment.totalSubmissions === 0) return 0
  return Math.round((assignment.gradedCount / assignment.totalSubmissions) * 100)
}

const getProgressColor = (assignment) => {
  const percentage = getSubmissionPercentage(assignment)
  if (percentage >= 80) return '#67c23a'
  if (percentage >= 60) return '#e6a23c'
  return '#f56c6c'
}

// 生命周期
onMounted(() => {
  loadAssignments()
  loadCourses()
  loadStatistics()
})
</script>

<style scoped>
.assignment-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0 0 5px 0;
  color: #303133;
}

.header-left p {
  margin: 0;
  color: #606266;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.assignment-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-weight: 500;
}

.submission-stats,
.grading-stats {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.submission-stats span,
.grading-stats span {
  font-size: 12px;
  color: #606266;
}

.overdue {
  color: #f56c6c;
  font-weight: bold;
}

.urgent {
  color: #e6a23c;
  font-weight: bold;
}

.warning {
  color: #e6a23c;
}

.normal {
  color: #606266;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}
</style>
