<template>
  <div class="assignment-detail">
    <div class="detail-header">
      <div class="title-section">
        <h2>{{ assignment.title }}</h2>
        <el-tag 
          :type="getStatusTagType(assignment.status)"
          size="large"
        >
          {{ getStatusText(assignment.status) }}
        </el-tag>
      </div>
      
      <div class="meta-info">
        <div class="meta-item">
          <el-icon><Calendar /></el-icon>
          <span>发布时间：{{ formatDateTime(assignment.createdAt) }}</span>
        </div>
        <div class="meta-item">
          <el-icon><Clock /></el-icon>
          <span 
            class="due-date"
            :class="{ 'overdue': isOverdue(assignment.dueDate) }"
          >
            截止时间：{{ formatDateTime(assignment.dueDate) }}
          </span>
        </div>
        <div class="meta-item">
          <el-icon><User /></el-icon>
          <span>任课教师：{{ assignment.teacherName }}</span>
        </div>
      </div>
    </div>

    <el-divider />

    <div class="detail-content">
      <div class="content-section">
        <h3>作业要求</h3>
        <div class="requirement-content">
          <p v-if="assignment.description">{{ assignment.description }}</p>
          <p v-else class="no-content">暂无详细要求</p>
        </div>
      </div>

      <div class="content-section" v-if="assignment.attachments && assignment.attachments.length > 0">
        <h3>作业附件</h3>
        <div class="attachments-list">
          <div 
            v-for="attachment in assignment.attachments" 
            :key="attachment.id"
            class="attachment-item"
          >
            <el-icon><Document /></el-icon>
            <span class="attachment-name">{{ attachment.fileName }}</span>
            <el-button 
              type="primary" 
              size="small" 
              link
              @click="downloadAttachment(attachment)"
            >
              下载
            </el-button>
          </div>
        </div>
      </div>

      <div class="content-section">
        <h3>作业信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>所属课程：</label>
            <span>{{ assignment.courseName }}</span>
          </div>
          <div class="info-item">
            <label>总分：</label>
            <span>{{ assignment.maxScore }}分</span>
          </div>
          <div class="info-item">
            <label>提交方式：</label>
            <span>{{ getSubmissionTypeText(assignment.submissionType) }}</span>
          </div>
          <div class="info-item">
            <label>允许重新提交：</label>
            <span>{{ assignment.allowResubmit ? '是' : '否' }}</span>
          </div>
        </div>
      </div>

      <div class="content-section" v-if="assignment.submission">
        <h3>我的提交</h3>
        <div class="submission-info">
          <div class="submission-header">
            <div class="submission-status">
              <el-tag :type="getSubmissionStatusTagType(assignment.submission.status)">
                {{ getSubmissionStatusText(assignment.submission.status) }}
              </el-tag>
              <span class="submission-time">
                提交时间：{{ formatDateTime(assignment.submission.submittedAt) }}
              </span>
            </div>
            <div class="submission-score" v-if="assignment.submission.score !== null">
              <span class="score">{{ assignment.submission.score }}/{{ assignment.maxScore }}</span>
            </div>
          </div>

          <div class="submission-content">
            <div class="content-item" v-if="assignment.submission.content">
              <label>提交内容：</label>
              <div class="content-text">{{ assignment.submission.content }}</div>
            </div>

            <div class="content-item" v-if="assignment.submission.files && assignment.submission.files.length > 0">
              <label>提交文件：</label>
              <div class="submission-files">
                <div 
                  v-for="file in assignment.submission.files" 
                  :key="file.id"
                  class="file-item"
                >
                  <el-icon><Document /></el-icon>
                  <span>{{ file.fileName }}</span>
                  <el-button type="primary" size="small" link @click="downloadFile(file)">
                    下载
                  </el-button>
                </div>
              </div>
            </div>

            <div class="content-item" v-if="assignment.submission.feedback">
              <label>教师反馈：</label>
              <div class="feedback-content">{{ assignment.submission.feedback }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="detail-footer">
      <el-button @click="$emit('close')">关闭</el-button>
      <el-button 
        v-if="canSubmit"
        type="primary"
        @click="$emit('submit', assignment)"
      >
        {{ assignment.submission ? '重新提交' : '提交作业' }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Clock, User, Document } from '@element-plus/icons-vue'

const props = defineProps({
  assignment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'submit'])

// 计算属性
const canSubmit = computed(() => {
  const { assignment } = props
  if (isOverdue(assignment.dueDate)) return false
  return !assignment.submission || assignment.allowResubmit
})

// 工具函数
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

const getSubmissionStatusTagType = (status) => {
  const typeMap = {
    submitted: 'info',
    graded: 'success',
    returned: 'warning'
  }
  return typeMap[status] || 'info'
}

const getSubmissionStatusText = (status) => {
  const textMap = {
    submitted: '已提交',
    graded: '已批改',
    returned: '已退回'
  }
  return textMap[status] || '未知'
}

const getSubmissionTypeText = (type) => {
  const textMap = {
    online: '在线提交',
    file: '文件上传',
    both: '在线+文件'
  }
  return textMap[type] || '未指定'
}

const isOverdue = (dueDate) => {
  return new Date(dueDate) < new Date()
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

// 文件下载
const downloadAttachment = (attachment) => {
  // 实现文件下载逻辑
  window.open(attachment.downloadUrl)
}

const downloadFile = (file) => {
  // 实现文件下载逻辑
  window.open(file.downloadUrl)
}
</script>

<style scoped>
.assignment-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-header {
  margin-bottom: 20px;
}

.title-section {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.title-section h2 {
  margin: 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
  line-height: 1.3;
  flex: 1;
  margin-right: 16px;
}

.meta-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.meta-item .el-icon {
  color: #909399;
}

.due-date.overdue {
  color: #f56c6c;
  font-weight: 600;
}

.detail-content {
  margin-bottom: 24px;
}

.content-section {
  margin-bottom: 24px;
}

.content-section h3 {
  margin: 0 0 12px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  border-left: 3px solid #409eff;
  padding-left: 12px;
}

.requirement-content {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.requirement-content p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.no-content {
  color: #909399;
  font-style: italic;
}

.attachments-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.attachment-name {
  flex: 1;
  color: #606266;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item label {
  color: #909399;
  font-size: 14px;
  margin-right: 8px;
  min-width: 80px;
}

.info-item span {
  color: #606266;
  font-size: 14px;
}

.submission-info {
  background-color: #f8f9fa;
  padding: 16px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.submission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.submission-status {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submission-time {
  color: #909399;
  font-size: 14px;
}

.submission-score .score {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.submission-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.content-item label {
  color: #909399;
  font-size: 14px;
  font-weight: 600;
}

.content-text,
.feedback-content {
  color: #606266;
  line-height: 1.6;
  padding: 12px;
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.submission-files {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.file-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.file-item span {
  flex: 1;
  color: #606266;
}

.detail-footer {
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.detail-footer .el-button {
  margin-left: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .title-section {
    flex-direction: column;
    gap: 12px;
  }

  .title-section h2 {
    margin-right: 0;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .submission-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>