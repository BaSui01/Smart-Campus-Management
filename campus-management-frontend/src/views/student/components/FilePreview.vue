<template>
  <div class="file-preview">
    <div class="preview-header">
      <h4>{{ file.fileName }}</h4>
      <div class="file-info">
        <span>文件大小：{{ formatFileSize(file.fileSize) }}</span>
        <span>文件类型：{{ getFileType(file.fileName) }}</span>
      </div>
    </div>

    <div class="preview-content">
      <!-- PDF预览 -->
      <div v-if="isPDF" class="pdf-preview">
        <iframe 
          :src="file.downloadUrl || file.fileUrl" 
          width="100%" 
          height="600px"
          frameborder="0"
        >
          您的浏览器不支持PDF预览，请<a :href="file.downloadUrl || file.fileUrl" target="_blank">点击下载</a>查看。
        </iframe>
      </div>

      <!-- 图片预览 -->
      <div v-else-if="isImage" class="image-preview">
        <img 
          :src="file.downloadUrl || file.fileUrl" 
          :alt="file.fileName"
          class="preview-image"
          @load="handleImageLoad"
          @error="handleImageError"
        />
      </div>

      <!-- 文本预览 -->
      <div v-else-if="isText" class="text-preview">
        <div v-loading="loading" class="text-content">
          <pre v-if="textContent">{{ textContent }}</pre>
          <div v-else class="no-preview">
            <p>无法预览文本内容</p>
            <el-button type="primary" @click="downloadFile">下载文件</el-button>
          </div>
        </div>
      </div>

      <!-- 不支持预览的文件类型 -->
      <div v-else class="unsupported-preview">
        <el-icon :size="80" color="#909399"><Document /></el-icon>
        <h3>无法预览此文件类型</h3>
        <p>{{ file.fileName }}</p>
        <el-button type="primary" @click="downloadFile">下载文件</el-button>
      </div>
    </div>

    <div class="preview-footer">
      <el-button type="primary" @click="downloadFile">
        <el-icon><Download /></el-icon>
        下载文件
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Download } from '@element-plus/icons-vue'

const props = defineProps({
  file: {
    type: Object,
    required: true
  }
})

// 响应式数据
const loading = ref(false)
const textContent = ref('')

// 计算属性
const fileExtension = computed(() => {
  return props.file.fileName.slice(props.file.fileName.lastIndexOf('.')).toLowerCase()
})

const isPDF = computed(() => {
  return fileExtension.value === '.pdf'
})

const isImage = computed(() => {
  const imageTypes = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp']
  return imageTypes.includes(fileExtension.value)
})

const isText = computed(() => {
  const textTypes = ['.txt', '.md', '.json', '.xml', '.csv']
  return textTypes.includes(fileExtension.value)
})

// 初始化
onMounted(() => {
  if (isText.value) {
    loadTextContent()
  }
})

// 加载文本内容
const loadTextContent = async () => {
  if (!props.file.downloadUrl && !props.file.fileUrl) return
  
  try {
    loading.value = true
    const response = await fetch(props.file.downloadUrl || props.file.fileUrl)
    if (response.ok) {
      const text = await response.text()
      textContent.value = text
    } else {
      throw new Error('文件加载失败')
    }
  } catch (error) {
    console.error('加载文本文件失败:', error)
    ElMessage.warning('无法加载文件内容，请下载查看')
  } finally {
    loading.value = false
  }
}

// 事件处理
const handleImageLoad = () => {
  // 图片加载成功
}

const handleImageError = () => {
  ElMessage.error('图片加载失败')
}

const downloadFile = () => {
  const url = props.file.downloadUrl || props.file.fileUrl
  if (url) {
    const link = document.createElement('a')
    link.href = url
    link.download = props.file.fileName
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } else {
    ElMessage.warning('下载链接不可用')
  }
}

// 工具函数
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
  const extension = fileName.slice(fileName.lastIndexOf('.')).toLowerCase()
  const typeMap = {
    '.pdf': 'PDF文档',
    '.doc': 'Word文档',
    '.docx': 'Word文档',
    '.txt': '文本文件',
    '.md': 'Markdown文件',
    '.json': 'JSON文件',
    '.xml': 'XML文件',
    '.csv': 'CSV文件',
    '.zip': '压缩文件',
    '.rar': '压缩文件',
    '.jpg': '图片文件',
    '.jpeg': '图片文件',
    '.png': '图片文件',
    '.gif': '图片文件',
    '.bmp': '图片文件',
    '.webp': '图片文件'
  }
  return typeMap[extension] || '未知类型'
}
</script>

<style scoped>
.file-preview {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-height: 70vh;
}

.preview-header {
  padding: 16px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 16px;
}

.preview-header h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  word-break: break-all;
}

.file-info {
  display: flex;
  gap: 16px;
  color: #909399;
  font-size: 14px;
}

.preview-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.pdf-preview,
.image-preview,
.text-preview {
  flex: 1;
  overflow: auto;
}

.pdf-preview iframe {
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-preview {
  text-align: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 100%;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.text-preview {
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 6px;
  overflow: auto;
}

.text-content {
  height: 100%;
  min-height: 400px;
}

.text-content pre {
  margin: 0;
  padding: 16px;
  color: #303133;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.no-preview,
.unsupported-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #909399;
  min-height: 300px;
}

.no-preview h3,
.unsupported-preview h3 {
  margin: 16px 0 8px;
  color: #606266;
  font-size: 18px;
}

.no-preview p,
.unsupported-preview p {
  margin: 0 0 20px 0;
  color: #909399;
  word-break: break-all;
}

.preview-footer {
  padding: 16px 0;
  border-top: 1px solid #e4e7ed;
  text-align: center;
  margin-top: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .file-info {
    flex-direction: column;
    gap: 4px;
  }
  
  .pdf-preview iframe {
    height: 400px;
  }
  
  .text-content {
    min-height: 300px;
  }
  
  .text-content pre {
    font-size: 12px;
    padding: 12px;
  }
}
</style>