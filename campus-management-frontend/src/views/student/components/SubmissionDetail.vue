<template>
  <div class="submission-detail">
    <div class="detail-header">
      <div class="submission-meta">
        <h3>提交详情</h3>
        <div class="meta-info">
          <el-tag :type="getStatusTagType(submission.status)" size="large">
            {{ getStatusText(submission.status) }}
          </el-tag>
          <span class="submission-time">
            提交时间：{{ formatDateTime(submission.submittedAt) }}
          </span>
        </div>
      </div>
      
      <div class="score-section" v-if="submission.score !== null">
        <div class="score-display">
          <div class="score-value">{{ submission.score }}</div>
          <div class="score-divider">/</div>
          <div class="score-max">{{ assignment.maxScore }}</div>
        </div>
        <div class="score-label">得分</div>
      </div>
    </div>

    <el-divider />

    <div class="detail-content">
      <!-- 基本信息 -->
      <div class="info-section">
        <h4>基本信息</h4>
        <div class="info-grid">
          <div class="info-item">
            <label>作业标题：</label>
            <span>{{ assignment.title }}</span>
          </div>
          <div class="info-item">
            <label>所属课程：</label>
            <span>{{ assignment.courseName }}</span>
          </div>
          <div class="info-item">
            <label>提交状态：</label>
            <el-tag :type="getStatusTagType(submission.status)" size="small">
              {{ getStatusText(submission.status) }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>提交时间：</label>
            <span>{{ formatDateTime(submission.submittedAt) }}</span>
          </div>
          <div class="info-item" v-if="submission.isLate">
            <label>提交状态：</label>
            <el-tag type="danger" size="small">逾期提交</el-tag>
          </div>
          <div class="info-item" v-if="submission.submissionCount">
            <label>提交次数：</label>
            <span>第 {{ submission.submissionCount }} 次</span>
          </div>
        </div>
      </div>

      <!-- 提交内容 -->
      <div class="content-section" v-if="submission.content">
        <h4>提交内容</h4>
        <div class="content-display">
          <div class="content-text" v-html="formatContent(submission.content)"></div>
        </div>
      </div>

      <!-- 提交文件 -->
      <div class="files-section" v-if="submission.files && submission.files.length > 0">
        <h4>提交文件</h4>
        <div class="files-list">
          <div 
            v-for="file in submission.files" 
            :key="file.id"
            class="file-card"
          >
            <div class="file-info">
              <div class="file-icon">
                <el-icon size="24"><Document /></el-icon>
              </div>
              <div class="file-details">
                <div class="file-name">{{ file.fileName }}</div>
                <div class="file-meta">
                  <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
                  <span class="file-type">{{ getFileType(file.fileName) }}</span>
                  <span class="upload-time">{{ formatDateTime(file.uploadTime) }}</span>
                </div>
              </div>
            </div>
            <div class="file-actions">
              <el-button 
                type="primary" 
                size="small"
                @click="downloadFile(file)"
              >
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              <el-button
                type="info"
                size="small"
                @click="handlePreviewFile(file)"
                v-if="canPreview(file)"
              >
                <el-icon><View /></el-icon>
                预览
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 评分和反馈 -->
      <div class="feedback-section" v-if="submission.status === 'graded'">
        <h4>评分反馈</h4>
        <div class="feedback-content">
          <div class="grade-info">
            <div class="grade-row">
              <label>评分：</label>
              <div class="grade-value">
                <span class="score">{{ submission.score }}</span>
                <span class="max-score">/ {{ assignment.maxScore }}</span>
                <el-tag 
                  :type="getGradeTagType(submission.score, assignment.maxScore)"
                  size="small"
                >
                  {{ getGradeText(submission.score, assignment.maxScore) }}
                </el-tag>
              </div>
            </div>
            <div class="grade-row" v-if="submission.gradedAt">
              <label>批改时间：</label>
              <span>{{ formatDateTime(submission.gradedAt) }}</span>
            </div>
            <div class="grade-row" v-if="submission.graderName">
              <label>批改教师：</label>
              <span>{{ submission.graderName }}</span>
            </div>
          </div>
          
          <div class="feedback-text" v-if="submission.feedback">
            <label>教师评语：</label>
            <div class="feedback-display">{{ submission.feedback }}</div>
          </div>
        </div>
      </div>

      <!-- 提交历史记录 -->
      <div class="history-section" v-if="submission.revisions && submission.revisions.length > 0">
        <h4>修改历史</h4>
        <div class="revision-list">
          <div 
            v-for="(revision, index) in submission.revisions" 
            :key="revision.id"
            class="revision-item"
          >
            <div class="revision-header">
              <span class="revision-title">修改 {{ index + 1 }}</span>
              <span class="revision-time">{{ formatDateTime(revision.modifiedAt) }}</span>
            </div>
            <div class="revision-content">{{ revision.changes }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 文件预览弹窗 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="文件预览"
      width="80%"
      :before-close="handlePreviewClose"
    >
      <FilePreview
        v-if="currentPreviewFile && previewDialogVisible"
        :file="currentPreviewFile"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Download, View } from '@element-plus/icons-vue'
import FilePreview from './FilePreview.vue'

const props = defineProps({
  submission: {
    type: Object,
    required: true
  },
  assignment: {
    type: Object,
    required: true
  }
})

// 响应式数据
const previewDialogVisible = ref(false)
const currentPreviewFile = ref(null)

// 计算属性
const submissionScore = computed(() => {
  return props.submission.score || 0
})

const maxScore = computed(() => {
  return props.assignment.maxScore || 100
})

// 文件操作
const downloadFile = (file) => {
  if (file.downloadUrl) {
    // 创建隐藏的下载链接
    const link = document.createElement('a')
    link.href = file.downloadUrl
    link.download = file.fileName
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } else {
    ElMessage.warning('文件下载链接不可用')
  }
}

const handlePreviewFile = (file) => {
  if (canPreview(file)) {
    currentPreviewFile.value = file
    previewDialogVisible.value = true
  } else {
    ElMessage.warning('该文件类型不支持预览')
  }
}

const canPreview = (file) => {
  const previewableTypes = ['.pdf', '.txt', '.jpg', '.jpeg', '.png', '.gif', '.bmp']
  const extension = getFileExtension(file.fileName).toLowerCase()
  return previewableTypes.includes(extension)
}

const handlePreviewClose = () => {
  previewDialogVisible.value = false
  currentPreviewFile.value = null
}

// 工具函数
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

const getGradeTagType = (score, maxScore) => {
  const percentage = (score / maxScore) * 100
  if (percentage >= 90) return 'success'
  if (percentage >= 80) return 'primary'
  if (percentage >= 70) return 'warning'
  if (percentage >= 60) return 'info'
  return 'danger'
}

const getGradeText = (score, maxScore) => {
  const percentage = (score / maxScore) * 100
  if (percentage >= 90) return '优秀'
  if (percentage >= 80) return '良好'
  if (percentage >= 70) return '中等'
  if (percentage >= 60) return '及格'
  return '不及格'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
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

const getFileType = (fileName) => {
  const extension = getFileExtension(fileName)
  const typeMap = {
    '.pdf': 'PDF文档',
    '.doc': 'Word文档',
    '.docx': 'Word文档',
    '.txt': '文本文件',
    '.zip': '压缩文件',
    '.rar': '压缩文件',
    '.jpg': '图片文件',
    '.jpeg': '图片文件',
    '.png': '图片文件',
    '.gif': '图片文件'
  }
  return typeMap[extension.toLowerCase()] || '未知类型'
}

const getFileExtension = (fileName) => {
  return fileName.slice(fileName.lastIndexOf('.'))
}

const formatContent = (content) => {
  // 简单的文本格式化，将换行符转换为<br>
  return content.replace(/\n/g, '<br>')
}
</script>

<style scoped>
.submission-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.submission-meta h3 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 20px;
  font-weight: 600;
}

.meta-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.submission-time {
  color: #909399;
  font-size: 14px;
}

.score-section {
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.score-display {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 4px;
  margin-bottom: 4px;
}

.score-value {
  font-size: 32px;
  font-weight: 700;
}

.score-divider {
  font-size: 24px;
  opacity: 0.8;
}

.score-max {
  font-size: 20px;
  opacity: 0.9;
}

.score-label {
  font-size: 14px;
  opacity: 0.9;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.info-section,
.content-section,
.files-section,
.feedback-section,
.history-section {
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.info-section h4,
.content-section h4,
.files-section h4,
.feedback-section h4,
.history-section h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  border-left: 3px solid #409eff;
  padding-left: 12px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item label {
  color: #909399;
  font-size: 14px;
  min-width: 80px;
  flex-shrink: 0;
}

.info-item span {
  color: #606266;
  font-size: 14px;
}

.content-display {
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 16px;
}

.content-text {
  color: #606266;
  line-height: 1.8;
  font-size: 14px;
}

.files-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.file-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.file-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.file-icon {
  color: #409eff;
}

.file-details {
  flex: 1;
}

.file-name {
  color: #303133;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.file-meta {
  display: flex;
  gap: 12px;
  color: #909399;
  font-size: 12px;
}

.file-actions {
  display: flex;
  gap: 8px;
}

.feedback-content {
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  padding: 16px;
}

.grade-info {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.grade-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.grade-row label {
  color: #909399;
  font-size: 14px;
  min-width: 80px;
}

.grade-value {
  display: flex;
  align-items: center;
  gap: 8px;
}

.grade-value .score {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.grade-value .max-score {
  color: #909399;
}

.feedback-text label {
  display: block;
  color: #909399;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.feedback-display {
  padding: 12px;
  background-color: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 4px;
  color: #856404;
  line-height: 1.6;
}

.revision-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.revision-item {
  padding: 12px;
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 6px;
}

.revision-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.revision-title {
  color: #303133;
  font-weight: 500;
}

.revision-time {
  color: #909399;
  font-size: 12px;
}

.revision-content {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .meta-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .file-card {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .file-info {
    justify-content: flex-start;
  }
  
  .file-actions {
    justify-content: center;
  }
  
  .grade-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>