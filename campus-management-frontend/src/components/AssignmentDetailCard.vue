<template>
  <div class="assignment-detail-card">
    <!-- 作业头部信息 -->
    <div class="assignment-header">
      <div class="assignment-title">
        <h2>{{ assignment.title }}</h2>
        <div class="assignment-meta">
          <el-tag :type="getStatusTag(assignment.status)" size="large">
            {{ getStatusText(assignment.status) }}
          </el-tag>
          <el-tag v-if="assignment.priority" :type="getPriorityTag(assignment.priority)" size="small">
            {{ getPriorityText(assignment.priority) }}
          </el-tag>
        </div>
      </div>
      
      <div class="assignment-score" v-if="assignment.score !== null && assignment.score !== undefined">
        <div class="score-display">
          <span class="score-value" :class="getScoreClass(assignment.score, assignment.maxScore)">
            {{ assignment.score }}
          </span>
          <span class="score-total">/ {{ assignment.maxScore }}</span>
        </div>
        <div class="score-percentage">
          {{ getPercentage(assignment.score, assignment.maxScore) }}%
        </div>
      </div>
    </div>

    <!-- 作业基本信息 -->
    <el-divider />
    
    <el-descriptions :column="2" border>
      <el-descriptions-item label="课程名称">
        <div class="course-info">
          <span class="course-name">{{ assignment.courseName }}</span>
          <el-tag v-if="assignment.courseCode" size="small" type="info">
            {{ assignment.courseCode }}
          </el-tag>
        </div>
      </el-descriptions-item>
      
      <el-descriptions-item label="任课教师">
        {{ assignment.teacherName || '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="发布时间">
        {{ formatDateTime(assignment.publishTime || assignment.createTime) }}
      </el-descriptions-item>
      
      <el-descriptions-item label="截止时间">
        <span :class="getDeadlineClass(assignment.dueDate)">
          {{ formatDateTime(assignment.dueDate) }}
        </span>
      </el-descriptions-item>
      
      <el-descriptions-item label="作业类型">
        <el-tag :type="getTypeTag(assignment.type)" size="small">
          {{ getTypeText(assignment.type) }}
        </el-tag>
      </el-descriptions-item>
      
      <el-descriptions-item label="提交方式">
        <el-tag :type="getSubmissionTypeTag(assignment.submissionType)" size="small">
          {{ getSubmissionTypeText(assignment.submissionType) }}
        </el-tag>
      </el-descriptions-item>
      
      <el-descriptions-item label="最大分值">
        {{ assignment.maxScore || '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="权重">
        {{ assignment.weight ? `${assignment.weight}%` : '-' }}
      </el-descriptions-item>
      
      <el-descriptions-item label="允许迟交">
        <el-tag :type="assignment.allowLateSubmission ? 'success' : 'danger'" size="small">
          {{ assignment.allowLateSubmission ? '是' : '否' }}
        </el-tag>
      </el-descriptions-item>
      
      <el-descriptions-item label="迟交扣分">
        {{ assignment.latePenalty ? `${assignment.latePenalty}%/天` : '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <!-- 作业描述 -->
    <div class="assignment-description" v-if="assignment.description">
      <el-divider>
        <span class="section-title">作业要求</span>
      </el-divider>
      
      <div class="description-content" v-html="assignment.description"></div>
    </div>

    <!-- 附件资源 -->
    <div class="assignment-attachments" v-if="assignment.attachments && assignment.attachments.length > 0">
      <el-divider>
        <span class="section-title">相关资源</span>
      </el-divider>
      
      <div class="attachments-list">
        <div
          v-for="attachment in assignment.attachments"
          :key="attachment.id"
          class="attachment-item"
          @click="downloadAttachment(attachment)"
        >
          <el-icon class="attachment-icon">
            <component :is="getFileIcon(attachment.type)" />
          </el-icon>
          <div class="attachment-info">
            <div class="attachment-name">{{ attachment.name }}</div>
            <div class="attachment-meta">
              <span class="file-size">{{ formatFileSize(attachment.size) }}</span>
              <span class="upload-time">{{ formatDate(attachment.uploadTime) }}</span>
            </div>
          </div>
          <el-icon class="download-icon">
            <Download />
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 提交信息 -->
    <div class="submission-info" v-if="assignment.submission">
      <el-divider>
        <span class="section-title">提交信息</span>
      </el-divider>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="提交状态">
          <el-tag :type="getSubmissionStatusTag(assignment.submission.status)">
            {{ getSubmissionStatusText(assignment.submission.status) }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="提交时间">
          <span :class="getSubmissionTimeClass(assignment.submission.submitTime, assignment.dueDate)">
            {{ formatDateTime(assignment.submission.submitTime) }}
          </span>
        </el-descriptions-item>
        
        <el-descriptions-item label="提交次数">
          {{ assignment.submission.attemptCount || 1 }}
        </el-descriptions-item>
        
        <el-descriptions-item label="是否迟交">
          <el-tag :type="assignment.submission.isLate ? 'warning' : 'success'" size="small">
            {{ assignment.submission.isLate ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 提交内容 -->
      <div class="submission-content" v-if="assignment.submission.content">
        <h4>提交内容：</h4>
        <div class="content-text" v-html="assignment.submission.content"></div>
      </div>
      
      <!-- 提交文件 -->
      <div class="submission-files" v-if="assignment.submission.files && assignment.submission.files.length > 0">
        <h4>提交文件：</h4>
        <div class="files-list">
          <div
            v-for="file in assignment.submission.files"
            :key="file.id"
            class="file-item"
            @click="downloadFile(file)"
          >
            <el-icon class="file-icon">
              <component :is="getFileIcon(file.type)" />
            </el-icon>
            <div class="file-info">
              <div class="file-name">{{ file.name }}</div>
              <div class="file-meta">{{ formatFileSize(file.size) }}</div>
            </div>
            <el-icon class="download-icon">
              <Download />
            </el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 评分和反馈 -->
    <div class="grading-feedback" v-if="assignment.feedback || assignment.score !== null">
      <el-divider>
        <span class="section-title">评分反馈</span>
      </el-divider>
      
      <div class="score-breakdown" v-if="assignment.scoreBreakdown">
        <h4>评分详情：</h4>
        <el-table :data="assignment.scoreBreakdown" border>
          <el-table-column prop="criterion" label="评分标准" />
          <el-table-column prop="maxScore" label="满分" width="80" align="center" />
          <el-table-column prop="score" label="得分" width="80" align="center">
            <template #default="{ row }">
              <span :class="getScoreClass(row.score, row.maxScore)">
                {{ row.score }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="comment" label="评语" />
        </el-table>
      </div>
      
      <div class="teacher-feedback" v-if="assignment.feedback">
        <h4>教师反馈：</h4>
        <el-alert
          :title="assignment.feedback"
          type="info"
          :closable="false"
          show-icon
        />
      </div>
      
      <div class="grading-time" v-if="assignment.gradingTime">
        <p class="grading-meta">
          评分时间：{{ formatDateTime(assignment.gradingTime) }}
          <span v-if="assignment.graderName">，评分教师：{{ assignment.graderName }}</span>
        </p>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="assignment-actions" v-if="!readonly">
      <el-divider />
      <div class="actions-container">
        <el-button
          v-if="canSubmit"
          type="primary"
          @click="submitAssignment"
          :disabled="isOverdue && !assignment.allowLateSubmission"
        >
          <el-icon><Upload /></el-icon>
          {{ assignment.submission ? '重新提交' : '提交作业' }}
        </el-button>
        
        <el-button v-if="canEdit" @click="editSubmission">
          <el-icon><Edit /></el-icon>
          编辑提交
        </el-button>
        
        <el-button v-if="canWithdraw" type="warning" @click="withdrawSubmission">
          <el-icon><Close /></el-icon>
          撤回提交
        </el-button>
        
        <el-button @click="viewHistory">
          <el-icon><Clock /></el-icon>
          提交历史
        </el-button>
        
        <el-button @click="exportDetail">
          <el-icon><Download /></el-icon>
          导出详情
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Download,
  Upload,
  Edit,
  Close,
  Clock,
  Document,
  Picture,
  VideoPlay,
  Folder,
  Link
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  assignment: {
    type: Object,
    required: true
  },
  readonly: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits([
  'submit',
  'edit',
  'withdraw',
  'viewHistory',
  'export',
  'downloadAttachment',
  'downloadFile'
])

// 计算属性
const isOverdue = computed(() => {
  if (!props.assignment.dueDate) return false
  return new Date() > new Date(props.assignment.dueDate)
})

const canSubmit = computed(() => {
  if (props.readonly) return false
  if (props.assignment.status === 'closed') return false
  if (isOverdue.value && !props.assignment.allowLateSubmission) return false
  return true
})

const canEdit = computed(() => {
  if (props.readonly) return false
  if (!props.assignment.submission) return false
  if (props.assignment.submission.status === 'graded') return false
  return canSubmit.value
})

const canWithdraw = computed(() => {
  if (props.readonly) return false
  if (!props.assignment.submission) return false
  if (props.assignment.submission.status === 'graded') return false
  return true
})

// 方法
const getStatusTag = (status) => {
  const statusMap = {
    'published': 'success',
    'draft': 'info',
    'closed': 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'published': '已发布',
    'draft': '草稿',
    'closed': '已关闭'
  }
  return textMap[status] || status
}

const getPriorityTag = (priority) => {
  const priorityMap = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'success'
  }
  return priorityMap[priority] || 'info'
}

const getPriorityText = (priority) => {
  const textMap = {
    'high': '高优先级',
    'medium': '中优先级',
    'low': '低优先级'
  }
  return textMap[priority] || priority
}

const getScoreClass = (score, maxScore) => {
  const percentage = (score / maxScore) * 100
  if (percentage >= 90) return 'excellent'
  if (percentage >= 80) return 'good'
  if (percentage >= 70) return 'average'
  if (percentage >= 60) return 'pass'
  return 'fail'
}

const getPercentage = (score, maxScore) => {
  return Math.round((score / maxScore) * 100)
}

const getDeadlineClass = (dueDate) => {
  if (!dueDate) return ''
  const now = new Date()
  const due = new Date(dueDate)
  const diff = due - now
  
  if (diff < 0) return 'overdue'
  if (diff < 24 * 60 * 60 * 1000) return 'urgent'
  if (diff < 3 * 24 * 60 * 60 * 1000) return 'warning'
  return ''
}

const getTypeTag = (type) => {
  const typeMap = {
    'homework': 'primary',
    'project': 'success',
    'report': 'warning',
    'presentation': 'info'
  }
  return typeMap[type] || 'info'
}

const getTypeText = (type) => {
  const textMap = {
    'homework': '作业',
    'project': '项目',
    'report': '报告',
    'presentation': '演示'
  }
  return textMap[type] || type
}

const getSubmissionTypeTag = (type) => {
  const typeMap = {
    'online': 'success',
    'offline': 'warning',
    'both': 'primary'
  }
  return typeMap[type] || 'info'
}

const getSubmissionTypeText = (type) => {
  const textMap = {
    'online': '在线提交',
    'offline': '线下提交',
    'both': '线上线下'
  }
  return textMap[type] || type
}

const getSubmissionStatusTag = (status) => {
  const statusMap = {
    'submitted': 'success',
    'draft': 'warning',
    'graded': 'primary',
    'returned': 'info'
  }
  return statusMap[status] || 'info'
}

const getSubmissionStatusText = (status) => {
  const textMap = {
    'submitted': '已提交',
    'draft': '草稿',
    'graded': '已评分',
    'returned': '已退回'
  }
  return textMap[status] || status
}

const getSubmissionTimeClass = (submitTime, dueDate) => {
  if (!submitTime || !dueDate) return ''
  return new Date(submitTime) > new Date(dueDate) ? 'late-submission' : 'on-time'
}

const getFileIcon = (type) => {
  if (type?.includes('image')) return Picture
  if (type?.includes('video')) return VideoPlay
  if (type?.includes('pdf') || type?.includes('document')) return Document
  if (type?.includes('link')) return Link
  return Folder
}

const formatDateTime = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const formatFileSize = (size) => {
  if (!size) return '-'
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  while (size >= 1024 && index < units.length - 1) {
    size /= 1024
    index++
  }
  return `${size.toFixed(1)}${units[index]}`
}

// 操作方法
const submitAssignment = () => {
  emit('submit', props.assignment)
}

const editSubmission = () => {
  emit('edit', props.assignment)
}

const withdrawSubmission = () => {
  emit('withdraw', props.assignment)
}

const viewHistory = () => {
  emit('viewHistory', props.assignment)
}

const exportDetail = () => {
  emit('export', props.assignment)
}

const downloadAttachment = (attachment) => {
  emit('downloadAttachment', attachment)
}

const downloadFile = (file) => {
  emit('downloadFile', file)
}
</script>

<style scoped>
.assignment-detail-card {
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.assignment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.assignment-title h2 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 24px;
}

.assignment-meta {
  display: flex;
  gap: 8px;
  align-items: center;
}

.assignment-score {
  text-align: center;
}

.score-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  margin-bottom: 4px;
}

.score-value {
  font-size: 32px;
  font-weight: bold;
  margin-right: 4px;
}

.score-value.excellent { color: #67c23a; }
.score-value.good { color: #409eff; }
.score-value.average { color: #e6a23c; }
.score-value.pass { color: #f56c6c; }
.score-value.fail { color: #909399; }

.score-total {
  font-size: 18px;
  color: #909399;
}

.score-percentage {
  font-size: 14px;
  color: #606266;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.course-name {
  font-weight: 600;
}

.overdue {
  color: #f56c6c;
  font-weight: 600;
}

.urgent {
  color: #e6a23c;
  font-weight: 600;
}

.warning {
  color: #e6a23c;
}

.section-title {
  font-weight: 600;
  color: #303133;
}

.description-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
  line-height: 1.6;
}

.attachments-list,
.files-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item,
.file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.attachment-item:hover,
.file-item:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.attachment-icon,
.file-icon {
  font-size: 24px;
  color: #409eff;
}

.attachment-info,
.file-info {
  flex: 1;
}

.attachment-name,
.file-name {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.attachment-meta,
.file-meta {
  font-size: 12px;
  color: #909399;
}

.download-icon {
  color: #909399;
}

.submission-content {
  margin-top: 16px;
}

.content-text {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #67c23a;
}

.late-submission {
  color: #e6a23c;
  font-weight: 600;
}

.on-time {
  color: #67c23a;
}

.grading-meta {
  font-size: 14px;
  color: #606266;
  margin-top: 12px;
}

.actions-container {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .assignment-detail-card {
    padding: 16px;
  }
  
  .assignment-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .assignment-title h2 {
    font-size: 20px;
  }
  
  .score-value {
    font-size: 24px;
  }
  
  .actions-container {
    flex-direction: column;
  }
  
  .actions-container .el-button {
    width: 100%;
  }
}
</style>
