<template>
  <div class="assignments-page">
    <div class="page-header">
      <h1>作业管理</h1>
      <p>查看和提交课程作业</p>
    </div>
    
    <div class="main-content">
      <!-- 筛选和统计 -->
      <el-card class="filter-card" shadow="never">
        <div class="filter-section">
          <div class="filter-controls">
            <el-select
              v-model="filterParams.status"
              placeholder="作业状态"
              clearable
              @change="handleFilterChange"
              style="width: 150px"
            >
              <el-option label="全部" value="all" />
              <el-option label="待提交" value="pending" />
              <el-option label="已提交" value="submitted" />
              <el-option label="已批改" value="graded" />
              <el-option label="已过期" value="overdue" />
            </el-select>
            
            <el-select
              v-model="filterParams.courseId"
              placeholder="选择课程"
              clearable
              @change="handleFilterChange"
              style="width: 200px"
            >
              <el-option
                v-for="course in courseList"
                :key="course.id"
                :label="course.name"
                :value="course.id"
              />
            </el-select>
            
            <el-button
              type="primary"
              :icon="Refresh"
              @click="refreshAssignments"
              :loading="loading"
            >
              刷新
            </el-button>
          </div>
          
          <div class="stats-summary">
            <div class="stat-item">
              <span class="stat-number">{{ assignmentStats.total }}</span>
              <span class="stat-label">总作业</span>
            </div>
            <div class="stat-item pending">
              <span class="stat-number">{{ assignmentStats.pending }}</span>
              <span class="stat-label">待提交</span>
            </div>
            <div class="stat-item submitted">
              <span class="stat-number">{{ assignmentStats.submitted }}</span>
              <span class="stat-label">已提交</span>
            </div>
            <div class="stat-item graded">
              <span class="stat-number">{{ assignmentStats.graded }}</span>
              <span class="stat-label">已批改</span>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 作业列表 -->
      <el-card class="assignments-list-card" shadow="never">
        <template #header>
          <div class="card-header">
            <h3>作业列表</h3>
            <el-button
              type="text"
              :icon="Sort"
              @click="toggleSortOrder"
            >
              {{ sortOrder === 'desc' ? '最新优先' : '最早优先' }}
            </el-button>
          </div>
        </template>

        <div v-loading="loading" class="assignments-content">
          <div v-if="assignmentList.length === 0 && !loading" class="empty-state">
            <el-icon :size="60" color="#909399"><Document /></el-icon>
            <h3>暂无作业</h3>
            <p>当前没有找到符合条件的作业</p>
          </div>

          <div v-else class="assignment-items">
            <div
              v-for="assignment in assignmentList"
              :key="assignment.id"
              class="assignment-item"
              :class="getAssignmentStatusClass(assignment)"
              @click="viewAssignmentDetail(assignment)"
            >
              <div class="assignment-main">
                <div class="assignment-header">
                  <h4 class="assignment-title">{{ assignment.title }}</h4>
                  <el-tag
                    :type="getStatusTagType(assignment.status)"
                    class="status-tag"
                  >
                    {{ getStatusText(assignment.status) }}
                  </el-tag>
                </div>
                
                <div class="assignment-info">
                  <div class="info-row">
                    <span class="info-label">课程：</span>
                    <span class="info-value">{{ assignment.courseName }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">发布时间：</span>
                    <span class="info-value">{{ formatDateTime(assignment.createdAt) }}</span>
                  </div>
                  <div class="info-row">
                    <span class="info-label">截止时间：</span>
                    <span
                      class="info-value"
                      :class="{ 'overdue': isOverdue(assignment.dueDate) }"
                    >
                      {{ formatDateTime(assignment.dueDate) }}
                    </span>
                  </div>
                </div>

                <div class="assignment-description">
                  <p>{{ assignment.description || '暂无描述' }}</p>
                </div>
              </div>

              <div class="assignment-actions">
                <div class="score-info" v-if="assignment.status === 'graded' && assignment.score !== null">
                  <div class="score-value">{{ assignment.score }}/{{ assignment.maxScore }}</div>
                  <div class="score-label">得分</div>
                </div>
                
                <div class="action-buttons">
                  <el-button
                    type="primary"
                    size="small"
                    @click.stop="viewAssignmentDetail(assignment)"
                  >
                    查看详情
                  </el-button>
                  
                  <el-button
                    v-if="canSubmit(assignment)"
                    type="success"
                    size="small"
                    @click.stop="submitAssignment(assignment)"
                  >
                    {{ assignment.status === 'pending' ? '提交作业' : '重新提交' }}
                  </el-button>
                  
                  <el-button
                    v-if="assignment.status !== 'pending'"
                    type="info"
                    size="small"
                    @click.stop="viewSubmissionHistory(assignment)"
                  >
                    提交历史
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="total"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </el-card>
    </div>

    <!-- 作业详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="作业详情"
      width="70%"
      :before-close="handleDetailClose"
    >
      <div v-if="selectedAssignment" class="assignment-detail-content">
        <div class="detail-header">
          <h3>{{ selectedAssignment.title }}</h3>
          <el-tag :type="getStatusTagType(selectedAssignment.status)">
            {{ getStatusText(selectedAssignment.status) }}
          </el-tag>
        </div>

        <div class="detail-info">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <label>课程：</label>
                <span>{{ selectedAssignment.courseName }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <label>教师：</label>
                <span>{{ selectedAssignment.teacherName }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <label>发布时间：</label>
                <span>{{ formatDateTime(selectedAssignment.createdAt) }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <label>截止时间：</label>
                <span :class="{ 'overdue': isOverdue(selectedAssignment.dueDate) }">
                  {{ formatDateTime(selectedAssignment.dueDate) }}
                </span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <label>总分：</label>
                <span>{{ selectedAssignment.maxScore }}分</span>
              </div>
            </el-col>
            <el-col :span="12" v-if="selectedAssignment.score !== null">
              <div class="info-item">
                <label>得分：</label>
                <span class="score-highlight">{{ selectedAssignment.score }}分</span>
              </div>
            </el-col>
          </el-row>
        </div>

        <div class="detail-description">
          <h4>作业要求</h4>
          <p>{{ selectedAssignment.description || '暂无详细要求' }}</p>
        </div>

        <div class="detail-attachments" v-if="selectedAssignment.attachments && selectedAssignment.attachments.length > 0">
          <h4>附件</h4>
          <div class="attachment-list">
            <div v-for="attachment in selectedAssignment.attachments" :key="attachment.id" class="attachment-item">
              <el-icon><Document /></el-icon>
              <span>{{ attachment.name }}</span>
              <el-button type="text" size="small">下载</el-button>
            </div>
          </div>
        </div>

        <div class="detail-actions">
          <el-button v-if="canSubmit(selectedAssignment)" type="primary" @click="submitAssignment(selectedAssignment)">
            {{ selectedAssignment.status === 'pending' ? '提交作业' : '重新提交' }}
          </el-button>
          <el-button @click="handleDetailClose">关闭</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 作业提交弹窗 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="提交作业"
      width="70%"
      :before-close="handleSubmitClose"
    >
      <AssignmentSubmission
        v-if="submissionAssignment"
        :assignment="submissionAssignment"
        @submit="handleSubmissionComplete"
        @cancel="handleSubmitClose"
      />
    </el-dialog>

    <!-- 提交历史弹窗 -->
    <el-dialog
      v-model="historyDialogVisible"
      title="提交历史"
      width="60%"
    >
      <div v-if="historyAssignment" class="submission-history">
        <div v-if="historyAssignment.submissions && historyAssignment.submissions.length > 0">
          <div v-for="(submission, index) in historyAssignment.submissions" :key="submission.id" class="submission-item">
            <div class="submission-header">
              <span class="submission-time">第{{ index + 1 }}次提交</span>
              <span class="submission-date">{{ formatDateTime(submission.submitTime) }}</span>
            </div>
            <div class="submission-content">
              <p>{{ submission.content || '无提交内容' }}</p>
              <div v-if="submission.attachments && submission.attachments.length > 0" class="submission-attachments">
                <div v-for="file in submission.attachments" :key="file.id" class="attachment-item">
                  <el-icon><Document /></el-icon>
                  <span>{{ file.name }}</span>
                </div>
              </div>
            </div>
            <div v-if="submission.score !== null" class="submission-score">
              <span>得分：{{ submission.score }}/{{ historyAssignment.maxScore }}</span>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无提交记录" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Refresh,
  Sort,
  Calendar,
  Clock,
  User
} from '@element-plus/icons-vue'
import { assignmentApi } from '@/api/assignment'
import { courseApi } from '@/api/course'
import { studentApi } from '@/api/student'
// import AssignmentDetail from './components/AssignmentDetail.vue'
import AssignmentSubmission from './components/AssignmentSubmission.vue'
// import SubmissionHistory from './components/SubmissionHistory.vue'

// 响应式数据
const loading = ref(false)
const assignmentList = ref([])
const courseList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const sortOrder = ref('desc')

// 筛选参数
const filterParams = reactive({
  status: 'all',
  courseId: null
})

// 统计数据
const assignmentStats = reactive({
  total: 0,
  pending: 0,
  submitted: 0,
  graded: 0
})

// 弹窗控制
const detailDialogVisible = ref(false)
const submitDialogVisible = ref(false)
const historyDialogVisible = ref(false)
const selectedAssignment = ref(null)
const submissionAssignment = ref(null)
const historyAssignment = ref(null)

// 初始化
onMounted(() => {
  loadAssignments()
  loadCourseList()
})

// 加载作业列表
const loadAssignments = async () => {
  try {
    loading.value = true
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      sortBy: 'dueDate',
      sortDir: sortOrder.value,
      ...filterParams
    }

    // 移除空值参数
    Object.keys(params).forEach(key => {
      if (params[key] === null || params[key] === '' || params[key] === 'all') {
        delete params[key]
      }
    })

    const { data } = await assignmentApi.getStudentAssignments(params)
    const assignmentData = Array.isArray(data) ? data : (data.assignments || data.list || data.content || [])

    assignmentList.value = assignmentData.map(assignment => ({
      id: assignment.id,
      title: assignment.title || assignment.assignmentTitle || assignment.name,
      description: assignment.description || assignment.content,
      courseName: assignment.courseName || assignment.course?.courseName,
      courseId: assignment.courseId || assignment.course?.id,
      teacherName: assignment.teacherName || assignment.teacher?.realName,
      status: assignment.status || getAssignmentStatus(assignment),
      createdAt: assignment.createdAt || assignment.createTime || assignment.publishTime,
      dueDate: assignment.dueDate || assignment.deadline || assignment.endTime,
      maxScore: assignment.maxScore || assignment.totalScore || 100,
      score: assignment.score || assignment.studentScore,
      allowResubmit: assignment.allowResubmit || false,
      attachments: assignment.attachments || [],
      requirements: assignment.requirements || assignment.requirement
    }))

    total.value = data.total || data.totalElements || assignmentData.length

    // 计算统计数据
    calculateStats()
  } catch (error) {
    console.error('加载作业列表失败:', error)
    ElMessage.error('加载作业列表失败')
    assignmentList.value = []
  } finally {
    loading.value = false
  }
}

// 根据时间和提交状态判断作业状态
const getAssignmentStatus = (assignment) => {
  if (assignment.status) return assignment.status

  const now = new Date()
  const dueDate = new Date(assignment.dueDate || assignment.deadline)

  if (assignment.submittedAt || assignment.submitTime) {
    return assignment.score !== null ? 'graded' : 'submitted'
  }

  if (now > dueDate) {
    return 'overdue'
  }

  return 'pending'
}

// 加载课程列表
const loadCourseList = async () => {
  try {
    const { data } = await studentApi.getStudentCourses()
    const courseData = Array.isArray(data) ? data : (data.courses || data.list || [])

    courseList.value = courseData.map(course => ({
      id: course.id,
      name: course.courseName || course.name,
      code: course.courseCode || course.code
    }))
  } catch (error) {
    console.error('加载课程列表失败:', error)
    // 使用默认课程列表
    courseList.value = [
      { id: 1, name: '高等数学' },
      { id: 2, name: '大学英语' },
      { id: 3, name: '计算机基础' }
    ]
  }
}

// 计算统计数据
const calculateStats = () => {
  const stats = assignmentList.value.reduce((acc, assignment) => {
    acc.total++
    switch (assignment.status) {
      case 'pending':
        acc.pending++
        break
      case 'submitted':
        acc.submitted++
        break
      case 'graded':
        acc.graded++
        break
    }
    return acc
  }, { total: 0, pending: 0, submitted: 0, graded: 0 })
  
  Object.assign(assignmentStats, stats)
}

// 事件处理
const refreshAssignments = () => {
  currentPage.value = 1
  loadAssignments()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadAssignments()
}

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc'
  loadAssignments()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadAssignments()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadAssignments()
}

// 作业操作
const viewAssignmentDetail = async (assignment) => {
  try {
    const { data } = await assignmentApi.getAssignmentDetail(assignment.id)
    selectedAssignment.value = {
      ...assignment,
      ...data,
      attachments: data.attachments || [],
      submissions: data.submissions || []
    }
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载作业详情失败:', error)
    ElMessage.error('加载作业详情失败')
    // 如果API失败，使用基本信息显示详情
    selectedAssignment.value = assignment
    detailDialogVisible.value = true
  }
}

const submitAssignment = (assignment) => {
  submissionAssignment.value = assignment
  submitDialogVisible.value = true
}

const viewSubmissionHistory = async (assignment) => {
  try {
    const { data } = await assignmentApi.getSubmissionHistory(assignment.id)
    historyAssignment.value = {
      ...assignment,
      submissions: data.submissions || []
    }
    historyDialogVisible.value = true
  } catch (error) {
    console.error('加载提交历史失败:', error)
    ElMessage.error('加载提交历史失败')
  }
}

// 弹窗处理
const handleDetailClose = () => {
  detailDialogVisible.value = false
  selectedAssignment.value = null
}

const handleSubmitClose = () => {
  submitDialogVisible.value = false
  submissionAssignment.value = null
}

const handleSubmitFromDetail = (assignment) => {
  handleDetailClose()
  submitAssignment(assignment)
}

const handleSubmissionComplete = () => {
  handleSubmitClose()
  refreshAssignments()
  ElMessage.success('作业提交成功')
}

// 工具函数
const getAssignmentStatusClass = (assignment) => {
  const classes = [`status-${assignment.status}`]
  if (isOverdue(assignment.dueDate) && assignment.status === 'pending') {
    classes.push('overdue')
  }
  return classes
}

const getStatusTagType = (status) => {
  const typeMap = {
    pending: 'warning',
    submitted: 'info',
    graded: 'success',
    overdue: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待提交',
    submitted: '已提交',
    graded: '已批改',
    overdue: '已过期'
  }
  return textMap[status] || '未知'
}

const isOverdue = (dueDate) => {
  return new Date(dueDate) < new Date()
}

const canSubmit = (assignment) => {
  return assignment.status === 'pending' ||
         (assignment.status === 'submitted' && assignment.allowResubmit)
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
@import '@/styles/student.css';
@import '@/styles/assignments.css';

.assignments-page {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
  text-align: center;
}

.page-header h1 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 28px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 20px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.filter-controls {
  display: flex;
  gap: 12px;
  align-items: center;
}

.stats-summary {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 8px 12px;
  border-radius: 6px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
}

.stat-item.pending {
  background-color: #fff3cd;
  border-color: #ffeaa7;
}

.stat-item.submitted {
  background-color: #d1ecf1;
  border-color: #bee5eb;
}

.stat-item.graded {
  background-color: #d4edda;
  border-color: #c3e6cb;
}

.stat-number {
  display: block;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
}

/* 作业列表卡片 */
.assignments-list-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.assignments-content {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state h3 {
  margin: 16px 0 8px;
  font-size: 16px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

/* 作业条目 */
.assignment-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assignment-item {
  display: flex;
  padding: 20px;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.assignment-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.assignment-item.status-pending {
  border-left: 4px solid #e6a23c;
}

.assignment-item.status-submitted {
  border-left: 4px solid #909399;
}

.assignment-item.status-graded {
  border-left: 4px solid #67c23a;
}

.assignment-item.overdue {
  border-left: 4px solid #f56c6c;
  background-color: #fef0f0;
}

.assignment-main {
  flex: 1;
  min-width: 0;
}

.assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.assignment-title {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  line-height: 1.4;
}

.status-tag {
  flex-shrink: 0;
  margin-left: 12px;
}

.assignment-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 14px;
}

.info-label {
  color: #909399;
  width: 80px;
  flex-shrink: 0;
}

.info-value {
  color: #606266;
}

.info-value.overdue {
  color: #f56c6c;
  font-weight: 600;
}

.assignment-description {
  color: #909399;
  font-size: 14px;
  line-height: 1.5;
}

.assignment-description p {
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.assignment-actions {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  margin-left: 20px;
}

.score-info {
  text-align: center;
  padding: 8px 12px;
  background-color: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 6px;
}

.score-value {
  font-size: 18px;
  font-weight: 600;
  color: #0369a1;
}

.score-label {
  font-size: 12px;
  color: #0369a1;
  margin-top: 2px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-buttons .el-button {
  width: 100px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 24px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .assignments-page {
    padding: 12px;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-controls {
    justify-content: center;
  }
  
  .stats-summary {
    justify-content: center;
  }
  
  .assignment-item {
    flex-direction: column;
    gap: 16px;
  }
  
  .assignment-actions {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-left: 0;
  }
  
  .action-buttons {
    flex-direction: row;
  }
  
  .action-buttons .el-button {
    width: auto;
  }
}
</style>