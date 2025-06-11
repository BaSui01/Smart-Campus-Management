<template>
  <div class="submission-history">
    <div class="history-header">
      <h3>{{ assignment.title }} - 提交历史</h3>
      <el-button 
        type="primary" 
        size="small"
        :icon="Refresh"
        @click="loadSubmissionHistory"
        :loading="loading"
      >
        刷新
      </el-button>
    </div>

    <div v-loading="loading" class="history-content">
      <div v-if="historyList.length === 0 && !loading" class="empty-state">
        <el-icon :size="60" color="#909399"><DocumentCopy /></el-icon>
        <h3>暂无提交记录</h3>
        <p>该作业还没有提交记录</p>
      </div>

      <div v-else class="history-timeline">
        <el-timeline>
          <el-timeline-item
            v-for="(submission, index) in historyList"
            :key="submission.id"
            :timestamp="formatDateTime(submission.submittedAt)"
            :type="getTimelineType(submission, index)"
            placement="top"
            :hollow="index !== 0"
          >
            <el-card class="submission-card">
              <div class="submission-header">
                <div class="submission-info">
                  <h4>第 {{ historyList.length - index }} 次提交</h4>
                  <el-tag 
                    :type="getStatusTagType(submission.status)"
                    size="small"
                  >
                    {{ getStatusText(submission.status) }}
                  </el-tag>
                </div>
                <div class="submission-score" v-if="submission.score !== null">
                  <span class="score-label">得分：</span>
                  <span class="score-value">{{ submission.score }}/{{ assignment.maxScore }}</span>
                </div>
              </div>

              <div class="submission-content">
                <!-- 提交内容 -->
                <div v-if="submission.content" class="content-section">
                  <h5>提交内容：</h5>
                  <div class="content-text">{{ submission.content }}</div>
                </div>

                <!-- 提交文件 -->
                <div v-if="submission.files && submission.files.length > 0" class="files-section">
                  <h5>提交文件：</h5>
                  <div class="file-list">
                    <div 
                      v-for="file in submission.files" 
                      :key="file.id"
                      class="file-item"
                    >
                      <el-icon><Document /></el-icon>
                      <span class="file-name">{{ file.fileName }}</span>
                      <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
                      <el-button 
                        type="primary" 
                        size="small" 
                        link
                        @click="downloadFile(file)"
                      >
                        下载
                      </el-button>
                    </div>
                  </div>
                </div>

                <!-- 教师反馈 -->
                <div v-if="submission.feedback" class="feedback-section">
                  <h5>教师反馈：</h5>
                  <div class="feedback-content">
                    <div class="feedback-text">{{ submission.feedback }}</div>
                    <div v-if="submission.gradedAt" class="feedback-time">
                      批改时间：{{ formatDateTime(submission.gradedAt) }}
                    </div>
                    <div v-if="submission.graderName" class="feedback-grader">
                      批改教师：{{ submission.graderName }}
                    </div>
                  </div>
                </div>

                <!-- 提交统计 -->
                <div class="submission-stats">
                  <div class="stat-item">
                    <span class="stat-label">提交时间：</span>
                    <span class="stat-value">{{ formatDateTime(submission.submittedAt) }}</span>
                  </div>
                  <div v-if="submission.submissionCount" class="stat-item">
                    <span class="stat-label">提交次数：</span>
                    <span class="stat-value">第 {{ submission.submissionCount }} 次</span>
                  </div>
                  <div v-if="submission.isLate" class="stat-item late">
                    <span class="stat-label">状态：</span>
                    <span class="stat-value">逾期提交</span>
                  </div>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="submission-actions" v-if="index === 0">
                <el-button 
                  v-if="canResubmit"
                  type="primary"
                  size="small"
                  @click="handleResubmit"
                >
                  重新提交
                </el-button>
                <el-button 
                  type="info"
                  size="small"
                  @click="viewDetails(submission)"
                >
                  查看详情
                </el-button>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        small
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="提交详情"
      width="60%"
    >
      <SubmissionDetail
        v-if="selectedSubmission"
        :submission="selectedSubmission"
        :assignment="assignment"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, 
  DocumentCopy, 
  Document, 
  Download 
} from '@element-plus/icons-vue'
import assignmentApi from '@/api/assignment.js'
import SubmissionDetail from './SubmissionDetail.vue'

const props = defineProps({
  assignment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['resubmit'])

// 响应式数据
const loading = ref(false)
const historyList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const detailDialogVisible = ref(false)
const selectedSubmission = ref(null)

// 计算属性
const canResubmit = computed(() => {
  return props.assignment.allowResubmit && 
         new Date(props.assignment.dueDate) > new Date()
})

// 初始化
onMounted(() => {
  loadSubmissionHistory()
})

// 加载提交历史
const loadSubmissionHistory = async () => {
  try {
    loading.value = true
    const response = await assignmentApi.getSubmissionHistory(props.assignment.id)
    
    if (response.success) {
      historyList.value = response.data.content || []
      total.value = response.data.totalElements || 0
    } else {
      ElMessage.error(response.message || '加载提交历史失败')
    }
  } catch (error) {
    console.error('加载提交历史失败:', error)
    ElMessage.error('加载提交历史失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  loadSubmissionHistory()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadSubmissionHistory()
}

// 操作处理
const handleResubmit = () => {
  emit('resubmit', props.assignment)
}

const viewDetails = (submission) => {
  selectedSubmission.value = submission
  detailDialogVisible.value = true
}

const downloadFile = (file) => {
  if (file.downloadUrl) {
    window.open(file.downloadUrl)
  } else {
    ElMessage.warning('文件下载链接不可用')
  }
}

// 工具函数
const getTimelineType = (submission, index) => {
  if (index === 0) {
    return submission.status === 'graded' ? 'success' : 'primary'
  }
  return 'info'
}

const getStatusTagType = (status) => {
  const typeMap = {
    submitted: 'info',
    graded: 'success',
    returned: 'warning',
    rejected: 'danger'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    submitted: '已提交',
    graded: '已批改',
    returned: '已退回',
    rejected: '已拒绝'
  }
  return textMap[status] || '未知'
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

const formatFileSize = (size) => {
  if (!size) return '-'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  let fileSize = size
  
  while (fileSize >= 1024 && index < units.length - 1) {
    fileSize /= 1024
    index++
  }
  
  return `${fileSize.toFixed(1)} ${units[index]}`
}
</script>

<style scoped>
.submission-history {
  max-height: 70vh;
  overflow-y: auto;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.history-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.history-content {
  min-height: 300px;
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

.history-timeline {
  padding: 0 20px;
}

.submission-card {
  margin-bottom: 0;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.submission-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.submission-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submission-info h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.submission-score {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background-color: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 4px;
}

.score-label {
  color: #0369a1;
  font-size: 14px;
}

.score-value {
  color: #0369a1;
  font-size: 16px;
  font-weight: 600;
}

.submission-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.content-section,
.files-section,
.feedback-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.content-section h5,
.files-section h5,
.feedback-section h5 {
  margin: 0;
  color: #606266;
  font-size: 14px;
  font-weight: 600;
}

.content-text {
  padding: 12px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  color: #606266;
  line-height: 1.6;
  max-height: 120px;
  overflow-y: auto;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.file-name {
  flex: 1;
  color: #606266;
}

.file-size {
  color: #909399;
  font-size: 12px;
}

.feedback-content {
  padding: 12px;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
}

.feedback-text {
  color: #856404;
  line-height: 1.6;
  margin-bottom: 8px;
}

.feedback-time,
.feedback-grader {
  color: #856404;
  font-size: 12px;
  margin-bottom: 4px;
}

.submission-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 12px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-item.late {
  color: #f56c6c;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.stat-value {
  color: #606266;
  font-size: 14px;
}

.stat-item.late .stat-value {
  color: #f56c6c;
  font-weight: 600;
}

.submission-actions {
  margin-top: 16px;
  text-align: right;
  border-top: 1px solid #e4e7ed;
  padding-top: 12px;
}

.submission-actions .el-button {
  margin-left: 8px;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: center;
}

/* 时间轴样式自定义 */
:deep(.el-timeline-item__timestamp) {
  font-size: 12px;
  color: #909399;
}

:deep(.el-timeline-item__node--primary) {
  background-color: #409eff;
}

:deep(.el-timeline-item__node--success) {
  background-color: #67c23a;
}

:deep(.el-timeline-item__node--info) {
  background-color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .history-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .submission-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .submission-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .history-timeline {
    padding: 0 10px;
  }
}
</style>