<template>
  <div class="assignment-submission">
    <div class="submission-header">
      <h3>{{ assignment.title }}</h3>
      <el-tag type="warning">截止时间：{{ formatDateTime(assignment.dueDate) }}</el-tag>
    </div>

    <el-form 
      ref="submissionFormRef"
      :model="submissionForm"
      :rules="submissionRules"
      label-width="100px"
      class="submission-form"
    >
      <!-- 在线提交内容 -->
      <el-form-item 
        v-if="needTextSubmission"
        label="提交内容"
        prop="content"
      >
        <el-input
          v-model="submissionForm.content"
          type="textarea"
          :rows="8"
          placeholder="请输入作业内容..."
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>

      <!-- 文件上传 -->
      <el-form-item 
        v-if="needFileSubmission"
        label="上传文件"
        prop="files"
      >
        <el-upload
          ref="uploadRef"
          v-model:file-list="fileList"
          :action="uploadAction"
          :headers="uploadHeaders"
          :data="uploadData"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :on-remove="handleRemoveFile"
          :auto-upload="false"
          multiple
          drag
          class="upload-area"
        >
          <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              支持上传 doc/docx/pdf/txt/zip 格式文件，单个文件不超过 50MB
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <!-- 已有提交显示 -->
      <div v-if="assignment.submission" class="existing-submission">
        <h4>当前提交</h4>
        <div class="submission-info">
          <div v-if="assignment.submission.content" class="content-section">
            <label>提交内容：</label>
            <div class="content-display">{{ assignment.submission.content }}</div>
          </div>
          
          <div v-if="assignment.submission.files && assignment.submission.files.length > 0" class="files-section">
            <label>已提交文件：</label>
            <div class="file-list">
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
        </div>
      </div>
    </el-form>

    <div class="submission-footer">
      <div class="footer-tips">
        <el-alert
          v-if="isOverdue"
          title="作业已过期，无法提交"
          type="error"
          :closable="false"
        />
        <el-alert
          v-else-if="assignment.submission && !assignment.allowResubmit"
          title="该作业不允许重新提交"
          type="warning"
          :closable="false"
        />
        <el-alert
          v-else-if="assignment.submission"
          title="重新提交将覆盖之前的提交内容"
          type="info"
          :closable="false"
        />
      </div>
      
      <div class="footer-actions">
        <el-button @click="$emit('cancel')">取消</el-button>
        <el-button 
          type="primary"
          :loading="submitting"
          :disabled="!canSubmit"
          @click="handleSubmit"
        >
          {{ submitting ? '提交中...' : '提交作业' }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Document } from '@element-plus/icons-vue'
import { assignmentApi } from '@/api/assignment'

const props = defineProps({
  assignment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['submit', 'cancel'])

// 响应式数据
const submissionFormRef = ref()
const uploadRef = ref()
const submitting = ref(false)
const fileList = ref([])
const uploadedFiles = ref([])

// 表单数据
const submissionForm = reactive({
  content: '',
  files: []
})

// 表单验证规则
const submissionRules = reactive({
  content: [
    { 
      validator: (rule, value, callback) => {
        if (needTextSubmission.value && !value.trim()) {
          callback(new Error('请输入作业内容'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  files: [
    { 
      validator: (rule, value, callback) => {
        if (needFileSubmission.value && fileList.value.length === 0 && uploadedFiles.value.length === 0) {
          callback(new Error('请上传作业文件'))
        } else {
          callback()
        }
      }, 
      trigger: 'change' 
    }
  ]
})

// 上传配置
const uploadAction = '/api/v1/assignments/upload/submission'
const uploadHeaders = {
  'Authorization': `Bearer ${localStorage.getItem('token')}`
}
const uploadData = {
  assignmentId: props.assignment.id
}

// 计算属性
const needTextSubmission = computed(() => {
  return props.assignment.submissionType === 'online' || props.assignment.submissionType === 'both'
})

const needFileSubmission = computed(() => {
  return props.assignment.submissionType === 'file' || props.assignment.submissionType === 'both'
})

const isOverdue = computed(() => {
  return new Date(props.assignment.dueDate) < new Date()
})

const canSubmit = computed(() => {
  if (isOverdue.value) return false
  if (props.assignment.submission && !props.assignment.allowResubmit) return false
  
  // 检查必要的内容是否已填写
  if (needTextSubmission.value && !submissionForm.content.trim()) return false
  if (needFileSubmission.value && fileList.value.length === 0 && uploadedFiles.value.length === 0) return false
  
  return true
})

// 初始化
onMounted(() => {
  // 如果已有提交，填充表单
  if (props.assignment.submission) {
    submissionForm.content = props.assignment.submission.content || ''
    uploadedFiles.value = props.assignment.submission.files || []
  }
})

// 文件上传处理
const beforeUpload = (file) => {
  const allowedTypes = [
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/pdf',
    'text/plain',
    'application/zip',
    'application/x-zip-compressed'
  ]
  
  const isAllowedType = allowedTypes.includes(file.type) || 
    file.name.toLowerCase().match(/\.(doc|docx|pdf|txt|zip)$/)
  
  if (!isAllowedType) {
    ElMessage.error('只能上传 doc/docx/pdf/txt/zip 格式的文件')
    return false
  }
  
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过 50MB')
    return false
  }
  
  return true
}

const handleUploadSuccess = (response, file) => {
  if (response.success) {
    uploadedFiles.value.push({
      id: response.data.id,
      fileName: file.name,
      fileUrl: response.data.fileUrl
    })
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.message || '文件上传失败')
  }
}

const handleUploadError = (error, file) => {
  console.error('文件上传失败:', error)
  ElMessage.error('文件上传失败')
}

const handleRemoveFile = (file, fileList) => {
  // 如果是已上传的文件，从uploadedFiles中移除
  const index = uploadedFiles.value.findIndex(f => f.fileName === file.name)
  if (index > -1) {
    uploadedFiles.value.splice(index, 1)
  }
}

const downloadFile = (file) => {
  window.open(file.downloadUrl || file.fileUrl)
}

// 提交处理
const handleSubmit = async () => {
  try {
    // 表单验证
    await submissionFormRef.value.validate()
    
    // 确认提交
    const confirmText = props.assignment.submission 
      ? '确定要重新提交作业吗？这将覆盖之前的提交内容。'
      : '确定要提交作业吗？'
    
    await ElMessageBox.confirm(confirmText, '确认提交', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    submitting.value = true
    
    // 先上传文件
    if (fileList.value.length > 0) {
      for (const file of fileList.value) {
        if (file.status !== 'success') {
          await uploadFile(file)
        }
      }
    }
    
    // 准备提交数据
    const submissionData = {
      content: submissionForm.content.trim(),
      fileIds: uploadedFiles.value.map(f => f.id)
    }
    
    // 提交作业
    await assignmentApi.submitAssignment(props.assignment.id, submissionData)
    
    ElMessage.success('作业提交成功')
    emit('submit')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交作业失败:', error)
      ElMessage.error('提交作业失败')
    }
  } finally {
    submitting.value = false
  }
}

const uploadFile = (file) => {
  return new Promise((resolve, reject) => {
    const formData = new FormData()
    formData.append('file', file.raw)
    formData.append('assignmentId', props.assignment.id)
    
    assignmentApi.uploadSubmissionFile(file.raw, props.assignment.id, (progressEvent) => {
      const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
      file.percentage = percentCompleted
    }).then(response => {
      if (response.success) {
        uploadedFiles.value.push({
          id: response.data.id,
          fileName: file.name,
          fileUrl: response.data.fileUrl
        })
        file.status = 'success'
        resolve(response)
      } else {
        reject(new Error(response.message))
      }
    }).catch(error => {
      file.status = 'exception'
      reject(error)
    })
  })
}

// 工具函数
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
.assignment-submission {
  max-height: 70vh;
  overflow-y: auto;
}

.submission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e7ed;
}

.submission-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.submission-form {
  margin-bottom: 20px;
}

.upload-area {
  width: 100%;
}

.upload-area :deep(.el-upload-dragger) {
  width: 100%;
  padding: 40px;
}

.existing-submission {
  margin-top: 20px;
  padding: 16px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
}

.existing-submission h4 {
  margin: 0 0 12px 0;
  color: #606266;
  font-size: 14px;
  font-weight: 600;
}

.submission-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-section,
.files-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.content-section label,
.files-section label {
  color: #909399;
  font-size: 14px;
  font-weight: 600;
}

.content-display {
  padding: 12px;
  background-color: white;
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
  background-color: white;
  border: 1px solid #e9ecef;
  border-radius: 4px;
}

.file-item span {
  flex: 1;
  color: #606266;
}

.submission-footer {
  border-top: 1px solid #e4e7ed;
  padding-top: 16px;
}

.footer-tips {
  margin-bottom: 16px;
}

.footer-actions {
  text-align: right;
}

.footer-actions .el-button {
  margin-left: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .submission-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .upload-area :deep(.el-upload-dragger) {
    padding: 20px;
  }
}
</style>