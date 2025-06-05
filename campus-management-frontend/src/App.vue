<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    
    <!-- 全局加载指示器 -->
    <div v-if="globalLoading" v-loading="true" :element-loading-text="loadingText" class="global-loading-mask"></div>
    
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
@import '@/styles/app.css';
</style>