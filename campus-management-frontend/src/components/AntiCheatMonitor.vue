<template>
  <div class="anti-cheat-monitor" v-if="enabled">
    <!-- 监控状态指示器 -->
    <div class="monitor-indicator" :class="{ 'warning': hasWarning }">
      <el-icon><VideoCamera /></el-icon>
      <span>考试监控中</span>
      <el-badge :value="warningCount" :hidden="warningCount === 0" type="danger" />
    </div>

    <!-- 警告提示 -->
    <el-alert
      v-if="currentWarning"
      :title="currentWarning.title"
      :description="currentWarning.description"
      type="warning"
      :closable="false"
      show-icon
      class="warning-alert"
    />

    <!-- 摄像头预览（可选） -->
    <div v-if="showCameraPreview" class="camera-preview">
      <video ref="videoRef" autoplay muted playsinline></video>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoCamera } from '@element-plus/icons-vue'

const props = defineProps({
  examRecordId: {
    type: [String, Number],
    required: true
  },
  enabled: {
    type: Boolean,
    default: true
  },
  showCameraPreview: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['cheat-warning'])

// 响应式数据
const videoRef = ref()
const warningCount = ref(0)
const hasWarning = ref(false)
const currentWarning = ref(null)

// 监控状态
const monitorState = reactive({
  isTabActive: true,
  isFullscreen: false,
  mouseLeaveCount: 0,
  keyboardSwitchCount: 0,
  copyAttempts: 0,
  lastActiveTime: Date.now()
})

// 监控配置
const config = {
  maxTabSwitches: 3,
  maxMouseLeaves: 5,
  maxCopyAttempts: 2,
  warningDuration: 5000,
  checkInterval: 1000
}

let monitorInterval = null
let mediaStream = null

// 方法
const initializeMonitoring = async () => {
  try {
    // 初始化摄像头（如果需要）
    if (props.showCameraPreview) {
      await initializeCamera()
    }

    // 添加事件监听器
    addEventListeners()

    // 开始监控循环
    startMonitoringLoop()

    ElMessage.success('考试监控已启动')
  } catch (error) {
    console.error('初始化监控失败:', error)
    ElMessage.error('监控初始化失败，请检查摄像头权限')
  }
}

const initializeCamera = async () => {
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({
      video: {
        width: { ideal: 320 },
        height: { ideal: 240 },
        facingMode: 'user'
      },
      audio: false
    })

    if (videoRef.value) {
      videoRef.value.srcObject = mediaStream
    }
  } catch (error) {
    console.error('摄像头初始化失败:', error)
    throw new Error('无法访问摄像头')
  }
}

const addEventListeners = () => {
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange)
  
  // 监听窗口焦点变化
  window.addEventListener('focus', handleWindowFocus)
  window.addEventListener('blur', handleWindowBlur)
  
  // 监听鼠标离开窗口
  document.addEventListener('mouseleave', handleMouseLeave)
  
  // 监听键盘事件
  document.addEventListener('keydown', handleKeyDown)
  
  // 监听复制粘贴
  document.addEventListener('copy', handleCopyAttempt)
  document.addEventListener('paste', handlePasteAttempt)
  
  // 监听右键菜单
  document.addEventListener('contextmenu', handleContextMenu)
  
  // 监听全屏变化
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  
  // 监听开发者工具
  setInterval(detectDevTools, 1000)
}

const removeEventListeners = () => {
  document.removeEventListener('visibilitychange', handleVisibilityChange)
  window.removeEventListener('focus', handleWindowFocus)
  window.removeEventListener('blur', handleWindowBlur)
  document.removeEventListener('mouseleave', handleMouseLeave)
  document.removeEventListener('keydown', handleKeyDown)
  document.removeEventListener('copy', handleCopyAttempt)
  document.removeEventListener('paste', handlePasteAttempt)
  document.removeEventListener('contextmenu', handleContextMenu)
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
}

const startMonitoringLoop = () => {
  monitorInterval = setInterval(() => {
    checkInactivity()
    reportMonitoringData()
  }, config.checkInterval)
}

const handleVisibilityChange = () => {
  if (document.hidden) {
    monitorState.isTabActive = false
    triggerWarning('页面切换警告', '检测到您切换了浏览器标签页，请保持在考试页面')
  } else {
    monitorState.isTabActive = true
    monitorState.lastActiveTime = Date.now()
  }
}

const handleWindowFocus = () => {
  monitorState.lastActiveTime = Date.now()
}

const handleWindowBlur = () => {
  monitorState.keyboardSwitchCount++
  if (monitorState.keyboardSwitchCount > config.maxTabSwitches) {
    triggerWarning('频繁切换警告', '检测到频繁切换窗口，请专注于考试')
  }
}

const handleMouseLeave = () => {
  monitorState.mouseLeaveCount++
  if (monitorState.mouseLeaveCount > config.maxMouseLeaves) {
    triggerWarning('鼠标离开警告', '鼠标频繁离开考试区域，请保持专注')
  }
}

const handleKeyDown = (event) => {
  // 检测常见的作弊快捷键
  const forbiddenKeys = [
    'F12', // 开发者工具
    'F5',  // 刷新
    'F11', // 全屏
  ]
  
  const forbiddenCombinations = [
    { ctrl: true, key: 'c' }, // 复制
    { ctrl: true, key: 'v' }, // 粘贴
    { ctrl: true, key: 'a' }, // 全选
    { ctrl: true, key: 'f' }, // 查找
    { ctrl: true, key: 'r' }, // 刷新
    { ctrl: true, key: 'w' }, // 关闭标签
    { ctrl: true, key: 't' }, // 新标签
    { ctrl: true, shift: true, key: 'i' }, // 开发者工具
    { ctrl: true, shift: true, key: 'j' }, // 控制台
    { alt: true, key: 'Tab' }, // 切换窗口
  ]
  
  if (forbiddenKeys.includes(event.key)) {
    event.preventDefault()
    triggerWarning('禁用快捷键', `禁止使用 ${event.key} 键`)
    return
  }
  
  for (const combo of forbiddenCombinations) {
    if (
      (combo.ctrl && event.ctrlKey) &&
      (combo.shift ? event.shiftKey : !event.shiftKey) &&
      (combo.alt ? event.altKey : !event.altKey) &&
      event.key.toLowerCase() === combo.key.toLowerCase()
    ) {
      event.preventDefault()
      triggerWarning('禁用快捷键', '检测到禁用的快捷键组合')
      return
    }
  }
}

const handleCopyAttempt = (event) => {
  event.preventDefault()
  monitorState.copyAttempts++
  
  if (monitorState.copyAttempts > config.maxCopyAttempts) {
    triggerWarning('复制警告', '禁止复制考试内容')
  }
}

const handlePasteAttempt = (event) => {
  event.preventDefault()
  triggerWarning('粘贴警告', '禁止粘贴外部内容')
}

const handleContextMenu = (event) => {
  event.preventDefault()
  triggerWarning('右键菜单警告', '考试期间禁用右键菜单')
}

const handleFullscreenChange = () => {
  monitorState.isFullscreen = !!document.fullscreenElement
  if (!monitorState.isFullscreen) {
    triggerWarning('全屏警告', '请保持全屏模式进行考试')
  }
}

const detectDevTools = () => {
  const threshold = 160
  
  if (
    window.outerHeight - window.innerHeight > threshold ||
    window.outerWidth - window.innerWidth > threshold
  ) {
    triggerWarning('开发者工具警告', '检测到开发者工具，请关闭后继续考试')
  }
}

const checkInactivity = () => {
  const inactiveTime = Date.now() - monitorState.lastActiveTime
  const maxInactiveTime = 5 * 60 * 1000 // 5分钟
  
  if (inactiveTime > maxInactiveTime) {
    triggerWarning('长时间无操作', '检测到长时间无操作，请确认是否在正常考试')
    monitorState.lastActiveTime = Date.now()
  }
}

const triggerWarning = (title, description) => {
  warningCount.value++
  hasWarning.value = true
  
  currentWarning.value = { title, description }
  
  // 发送警告事件
  emit('cheat-warning', {
    type: title,
    description,
    timestamp: new Date().toISOString(),
    examRecordId: props.examRecordId
  })
  
  // 自动清除警告提示
  setTimeout(() => {
    currentWarning.value = null
    hasWarning.value = false
  }, config.warningDuration)
  
  // 记录到后端
  reportWarning(title, description)
}

const reportWarning = async (type, description) => {
  try {
    // 这里应该调用后端API记录警告
    console.log('报告警告:', { type, description, examRecordId: props.examRecordId })
  } catch (error) {
    console.error('报告警告失败:', error)
  }
}

const reportMonitoringData = async () => {
  try {
    // 定期向后端报告监控数据
    const data = {
      examRecordId: props.examRecordId,
      isActive: monitorState.isTabActive,
      isFullscreen: monitorState.isFullscreen,
      warningCount: warningCount.value,
      timestamp: new Date().toISOString()
    }
    
    // 这里应该调用后端API
    console.log('监控数据:', data)
  } catch (error) {
    console.error('报告监控数据失败:', error)
  }
}

const cleanup = () => {
  // 清理事件监听器
  removeEventListeners()
  
  // 清理定时器
  if (monitorInterval) {
    clearInterval(monitorInterval)
  }
  
  // 关闭摄像头
  if (mediaStream) {
    mediaStream.getTracks().forEach(track => track.stop())
  }
}

// 生命周期
onMounted(() => {
  if (props.enabled) {
    initializeMonitoring()
  }
})

onUnmounted(() => {
  cleanup()
})
</script>

<style scoped>
.anti-cheat-monitor {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.monitor-indicator {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.monitor-indicator.warning {
  background: rgba(245, 108, 108, 0.9);
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.warning-alert {
  position: fixed;
  top: 80px;
  right: 20px;
  width: 300px;
  z-index: 10000;
}

.camera-preview {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 160px;
  height: 120px;
  border: 2px solid #409eff;
  border-radius: 8px;
  overflow: hidden;
  background: #000;
}

.camera-preview video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .monitor-indicator {
    top: 10px;
    right: 10px;
    padding: 6px 12px;
    font-size: 12px;
  }
  
  .warning-alert {
    top: 60px;
    right: 10px;
    left: 10px;
    width: auto;
  }
  
  .camera-preview {
    bottom: 10px;
    right: 10px;
    width: 120px;
    height: 90px;
  }
}
</style>
