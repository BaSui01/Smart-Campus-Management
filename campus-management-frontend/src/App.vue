<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    
    <!-- 全局加载指示器 -->
    <el-loading-service v-if="globalLoading" :text="loadingText" />
    
    <!-- 网络状态提示 -->
    <transition name="slide-down">
      <div v-if="!isOnline" class="network-status">
        <el-alert
          title="网络连接已断开"
          type="warning"
          :closable="false"
          show-icon
        />
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const globalLoading = ref(false)
const loadingText = ref('加载中...')
const isOnline = ref(navigator.onLine)

// 网络状态监听
const handleOnline = () => {
  isOnline.value = true
  ElMessage.success('网络连接已恢复')
}

const handleOffline = () => {
  isOnline.value = false
  ElMessage.warning('网络连接已断开')
}

// 全局键盘快捷键
const handleKeydown = (event) => {
  // Ctrl/Cmd + K 快速搜索
  if ((event.ctrlKey || event.metaKey) && event.key === 'k') {
    event.preventDefault()
    // 触发全局搜索
    ElMessage.info('全局搜索功能开发中...')
  }
  
  // ESC 键关闭对话框
  if (event.key === 'Escape') {
    // 可以在这里处理全局ESC逻辑
  }
}

onMounted(() => {
  // 添加网络状态监听
  window.addEventListener('online', handleOnline)
  window.addEventListener('offline', handleOffline)
  
  // 添加键盘事件监听
  document.addEventListener('keydown', handleKeydown)
  
  // 设置全局错误处理
  window.addEventListener('unhandledrejection', (event) => {
    console.error('未处理的Promise拒绝:', event.reason)
    ElMessage.error('系统发生错误，请稍后重试')
  })
})

onUnmounted(() => {
  window.removeEventListener('online', handleOnline)
  window.removeEventListener('offline', handleOffline)
  document.removeEventListener('keydown', handleKeydown)
})

// 暴露给子组件使用的方法
defineExpose({
  showGlobalLoading: (text = '加载中...') => {
    globalLoading.value = true
    loadingText.value = text
  },
  hideGlobalLoading: () => {
    globalLoading.value = false
  }
})
</script>

<style>
/* 基础样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  background-color: #f0f2f5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  height: 100vh;
  position: relative;
}

/* 页面过渡动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 网络状态提示 */
.network-status {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
}

.slide-down-enter-active, .slide-down-leave-active {
  transition: transform 0.3s ease;
}

.slide-down-enter-from, .slide-down-leave-to {
  transform: translateY(-100%);
}

/* 通用布局类 */
.page-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  overflow: auto;
  padding: 0;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Element Plus 组件样式覆盖 */
.el-card {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  transition: box-shadow 0.3s ease;
}

.el-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.el-button {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
}

.el-input__wrapper {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #fafafa;
  font-weight: 600;
}

.el-table--striped .el-table__body tr.el-table__row--striped td {
  background-color: #fafbfc;
}

.el-pagination {
  justify-content: center;
}

.el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.el-dialog__header {
  background-color: #fafafa;
  border-bottom: 1px solid #e4e7ed;
  padding: 20px 24px 16px;
}

.el-dialog__body {
  padding: 24px;
}

.el-dialog__footer {
  background-color: #fafafa;
  border-top: 1px solid #e4e7ed;
  padding: 16px 24px 20px;
}

/* 表单样式优化 */
.el-form-item__label {
  font-weight: 500;
  color: #333;
}

.el-form-item__error {
  font-size: 12px;
  color: #f56c6c;
  padding-top: 4px;
}

/* 标签样式 */
.el-tag {
  border-radius: 4px;
  font-weight: 500;
}

/* 消息提示样式 */
.el-message {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 空状态样式 */
.el-empty {
  padding: 60px 0;
}

.el-empty__image {
  width: 120px;
  height: 120px;
}

.el-empty__description {
  color: #909399;
  font-size: 14px;
  margin-top: 16px;
}

/* 加载状态 */
.el-loading-mask {
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(2px);
}

.el-loading-spinner {
  font-size: 28px;
}

.el-loading-text {
  font-size: 14px;
  color: #409eff;
  margin-top: 12px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .el-card {
    margin: 8px;
    border-radius: 6px;
  }
  
  .el-dialog {
    width: 90% !important;
    margin: 5vh auto;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-button {
    padding: 8px 12px;
    font-size: 12px;
  }
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  html, body {
    background-color: #1a1a1a;
    color: #e4e4e4;
  }
  
  .el-card {
    background-color: #2d2d2d;
    border-color: #404040;
  }
  
  .el-table th {
    background-color: #2d2d2d;
  }
  
  .el-dialog__header,
  .el-dialog__footer {
    background-color: #2d2d2d;
    border-color: #404040;
  }
}

/* 打印样式 */
@media print {
  .el-button,
  .el-pagination,
  .el-dialog,
  .network-status {
    display: none !important;
  }
  
  .el-card {
    box-shadow: none;
    border: 1px solid #ddd;
  }
  
  .el-table {
    page-break-inside: avoid;
  }
}

/* 高对比度模式 */
@media (prefers-contrast: high) {
  .el-card {
    border-width: 2px;
    border-color: #000;
  }
  
  .el-button {
    border-width: 2px;
  }
}

/* 减少动画模式 */
@media (prefers-reduced-motion: reduce) {
  .fade-enter-active, .fade-leave-active,
  .slide-down-enter-active, .slide-down-leave-active,
  .el-card, .el-button, .el-input__wrapper {
    transition: none !important;
  }
  
  .el-button:hover {
    transform: none;
  }
}
</style>