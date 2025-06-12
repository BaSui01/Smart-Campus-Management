<template>
  <div v-if="hasError" class="error-boundary">
    <div class="error-content">
      <div class="error-icon">
        <el-icon size="64" color="#f56c6c">
          <WarningFilled />
        </el-icon>
      </div>
      
      <div class="error-info">
        <h2>{{ errorTitle }}</h2>
        <p class="error-message">{{ errorMessage }}</p>
        
        <div v-if="showDetails" class="error-details">
          <el-collapse>
            <el-collapse-item title="错误详情" name="details">
              <pre>{{ errorDetails }}</pre>
            </el-collapse-item>
          </el-collapse>
        </div>
        
        <div class="error-actions">
          <el-button type="primary" @click="retry">
            <el-icon><Refresh /></el-icon>
            重试
          </el-button>
          <el-button @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
          <el-button v-if="!showDetails" type="text" @click="toggleDetails">
            查看详情
          </el-button>
        </div>
      </div>
    </div>
  </div>
  
  <slot v-else />
</template>

<script setup>
import { ref, onErrorCaptured, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WarningFilled, Refresh, HomeFilled } from '@element-plus/icons-vue'

const props = defineProps({
  fallbackTitle: {
    type: String,
    default: '页面出现错误'
  },
  fallbackMessage: {
    type: String,
    default: '抱歉，页面遇到了一些问题。请尝试刷新页面或联系技术支持。'
  },
  showRetry: {
    type: Boolean,
    default: true
  },
  onError: {
    type: Function,
    default: null
  }
})

const emit = defineEmits(['error', 'retry'])

const router = useRouter()

// 响应式数据
const hasError = ref(false)
const errorTitle = ref('')
const errorMessage = ref('')
const errorDetails = ref('')
const showDetails = ref(false)

// 错误处理
onErrorCaptured((error, instance, info) => {
  console.error('ErrorBoundary caught error:', error)
  console.error('Component instance:', instance)
  console.error('Error info:', info)
  
  handleError(error, info)
  
  // 阻止错误继续传播
  return false
})

// 全局错误处理
onMounted(() => {
  // 监听未捕获的Promise错误
  window.addEventListener('unhandledrejection', handleUnhandledRejection)
  
  // 监听JavaScript错误
  window.addEventListener('error', handleGlobalError)
})

const handleError = (error, info = '') => {
  hasError.value = true
  errorTitle.value = getErrorTitle(error)
  errorMessage.value = getErrorMessage(error)
  errorDetails.value = formatErrorDetails(error, info)
  
  // 发送错误事件
  emit('error', { error, info })
  
  // 调用自定义错误处理函数
  if (props.onError) {
    props.onError(error, info)
  }
  
  // 上报错误到监控系统
  reportError(error, info)
}

const handleUnhandledRejection = (event) => {
  console.error('Unhandled promise rejection:', event.reason)
  
  const error = event.reason instanceof Error ? event.reason : new Error(event.reason)
  handleError(error, 'Unhandled Promise Rejection')
  
  // 阻止默认的错误处理
  event.preventDefault()
}

const handleGlobalError = (event) => {
  console.error('Global error:', event.error)
  
  const error = event.error || new Error(event.message)
  handleError(error, `${event.filename}:${event.lineno}:${event.colno}`)
}

const getErrorTitle = (error) => {
  if (error.name === 'ChunkLoadError') {
    return '资源加载失败'
  }
  if (error.name === 'NetworkError') {
    return '网络连接错误'
  }
  if (error.response?.status === 401) {
    return '登录已过期'
  }
  if (error.response?.status === 403) {
    return '权限不足'
  }
  if (error.response?.status === 404) {
    return '页面不存在'
  }
  if (error.response?.status >= 500) {
    return '服务器错误'
  }
  
  return props.fallbackTitle
}

const getErrorMessage = (error) => {
  if (error.name === 'ChunkLoadError') {
    return '页面资源加载失败，请刷新页面重试。'
  }
  if (error.name === 'NetworkError') {
    return '网络连接异常，请检查网络设置后重试。'
  }
  if (error.response?.status === 401) {
    return '登录状态已过期，请重新登录。'
  }
  if (error.response?.status === 403) {
    return '您没有权限访问此页面。'
  }
  if (error.response?.status === 404) {
    return '请求的页面不存在。'
  }
  if (error.response?.status >= 500) {
    return '服务器暂时无法响应，请稍后重试。'
  }
  
  return error.message || props.fallbackMessage
}

const formatErrorDetails = (error, info) => {
  const details = {
    message: error.message,
    stack: error.stack,
    name: error.name,
    info: info,
    timestamp: new Date().toISOString(),
    userAgent: navigator.userAgent,
    url: window.location.href
  }
  
  if (error.response) {
    details.response = {
      status: error.response.status,
      statusText: error.response.statusText,
      data: error.response.data
    }
  }
  
  return JSON.stringify(details, null, 2)
}

const reportError = async (error, info) => {
  try {
    // 这里可以集成错误监控服务，如 Sentry、Bugsnag 等
    const errorReport = {
      message: error.message,
      stack: error.stack,
      name: error.name,
      info: info,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
      url: window.location.href,
      userId: localStorage.getItem('userId') || 'anonymous'
    }
    
    // 发送到错误监控服务
    // await fetch('/api/errors', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify(errorReport)
    // })
    
    console.log('Error reported:', errorReport)
  } catch (reportError) {
    console.error('Failed to report error:', reportError)
  }
}

const retry = () => {
  hasError.value = false
  errorTitle.value = ''
  errorMessage.value = ''
  errorDetails.value = ''
  showDetails.value = false
  
  emit('retry')
  
  // 刷新页面
  window.location.reload()
}

const goHome = () => {
  router.push('/')
}

const toggleDetails = () => {
  showDetails.value = !showDetails.value
}

// 清理事件监听器
const cleanup = () => {
  window.removeEventListener('unhandledrejection', handleUnhandledRejection)
  window.removeEventListener('error', handleGlobalError)
}

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(cleanup)
</script>

<style scoped>
.error-boundary {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background-color: #f5f5f5;
}

.error-content {
  max-width: 600px;
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.error-icon {
  margin-bottom: 24px;
}

.error-info h2 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.error-message {
  margin: 0 0 24px 0;
  color: #606266;
  font-size: 16px;
  line-height: 1.6;
}

.error-details {
  margin: 24px 0;
  text-align: left;
}

.error-details pre {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 6px;
  font-size: 12px;
  color: #606266;
  overflow-x: auto;
  max-height: 300px;
  overflow-y: auto;
}

.error-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

@media (max-width: 768px) {
  .error-content {
    padding: 24px;
    margin: 0 10px;
  }
  
  .error-info h2 {
    font-size: 20px;
  }
  
  .error-message {
    font-size: 14px;
  }
  
  .error-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error-actions .el-button {
    width: 200px;
  }
}
</style>
