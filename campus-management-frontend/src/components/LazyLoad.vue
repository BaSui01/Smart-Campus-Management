<template>
  <div ref="containerRef" class="lazy-load-container">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <slot name="loading">
        <div class="default-loading">
          <el-icon class="loading-icon" size="32">
            <Loading />
          </el-icon>
          <p>{{ loadingText }}</p>
        </div>
      </slot>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <slot name="error" :error="error" :retry="retry">
        <div class="default-error">
          <el-icon size="32" color="#f56c6c">
            <WarningFilled />
          </el-icon>
          <p>{{ errorText }}</p>
          <el-button type="primary" size="small" @click="retry">
            重试
          </el-button>
        </div>
      </slot>
    </div>
    
    <!-- 内容 -->
    <div v-else-if="visible" class="content">
      <slot :data="data" />
    </div>
    
    <!-- 占位符 -->
    <div v-else class="placeholder">
      <slot name="placeholder">
        <div class="default-placeholder" :style="placeholderStyle">
          <el-icon size="24" color="#c0c4cc">
            <Picture />
          </el-icon>
        </div>
      </slot>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'
import { Loading, WarningFilled, Picture } from '@element-plus/icons-vue'

const props = defineProps({
  // 懒加载函数
  loadFunction: {
    type: Function,
    required: true
  },
  
  // 触发距离（像素）
  threshold: {
    type: Number,
    default: 100
  },
  
  // 占位符高度
  height: {
    type: [String, Number],
    default: 200
  },
  
  // 占位符宽度
  width: {
    type: [String, Number],
    default: '100%'
  },
  
  // 加载文本
  loadingText: {
    type: String,
    default: '加载中...'
  },
  
  // 错误文本
  errorText: {
    type: String,
    default: '加载失败'
  },
  
  // 是否立即加载
  immediate: {
    type: Boolean,
    default: false
  },
  
  // 是否只加载一次
  once: {
    type: Boolean,
    default: true
  },
  
  // 延迟加载时间（毫秒）
  delay: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['load', 'error', 'visible'])

// 响应式数据
const containerRef = ref()
const visible = ref(false)
const loading = ref(false)
const error = ref(null)
const data = ref(null)
const hasLoaded = ref(false)

// 计算属性
const placeholderStyle = computed(() => ({
  height: typeof props.height === 'number' ? `${props.height}px` : props.height,
  width: typeof props.width === 'number' ? `${props.width}px` : props.width,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  backgroundColor: '#f5f7fa',
  border: '1px solid #e4e7ed',
  borderRadius: '4px'
}))

let observer = null

// 方法
const load = async () => {
  if (loading.value || (hasLoaded.value && props.once)) {
    return
  }
  
  loading.value = true
  error.value = null
  
  try {
    // 延迟加载
    if (props.delay > 0) {
      await new Promise(resolve => setTimeout(resolve, props.delay))
    }
    
    const result = await props.loadFunction()
    data.value = result
    hasLoaded.value = true
    
    emit('load', result)
  } catch (err) {
    error.value = err
    emit('error', err)
  } finally {
    loading.value = false
  }
}

const retry = () => {
  error.value = null
  load()
}

const handleIntersection = (entries) => {
  const entry = entries[0]
  
  if (entry.isIntersecting) {
    visible.value = true
    emit('visible')
    
    // 触发加载
    load()
    
    // 如果只需要加载一次，停止观察
    if (props.once && observer) {
      observer.unobserve(containerRef.value)
    }
  } else {
    visible.value = false
  }
}

const startObserving = () => {
  if (!containerRef.value || !window.IntersectionObserver) {
    // 如果不支持 IntersectionObserver，直接加载
    visible.value = true
    load()
    return
  }
  
  observer = new IntersectionObserver(handleIntersection, {
    rootMargin: `${props.threshold}px`,
    threshold: 0.1
  })
  
  observer.observe(containerRef.value)
}

const stopObserving = () => {
  if (observer && containerRef.value) {
    observer.unobserve(containerRef.value)
    observer = null
  }
}

// 监听器
watch(() => props.loadFunction, () => {
  if (visible.value) {
    load()
  }
})

// 生命周期
onMounted(() => {
  if (props.immediate) {
    visible.value = true
    load()
  } else {
    startObserving()
  }
})

onUnmounted(() => {
  stopObserving()
})

// 暴露方法
defineExpose({
  load,
  retry,
  reload: () => {
    hasLoaded.value = false
    load()
  }
})
</script>

<style scoped>
.lazy-load-container {
  position: relative;
  overflow: hidden;
}

.loading-state,
.error-state,
.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}

.default-loading {
  text-align: center;
  color: #606266;
}

.loading-icon {
  animation: rotate 2s linear infinite;
  margin-bottom: 12px;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.default-loading p {
  margin: 0;
  font-size: 14px;
}

.default-error {
  text-align: center;
  color: #606266;
}

.default-error p {
  margin: 12px 0;
  font-size: 14px;
}

.default-placeholder {
  color: #c0c4cc;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.default-placeholder:hover {
  background-color: #f0f2f5;
}

.content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .default-loading,
  .default-error {
    padding: 20px;
  }
  
  .default-loading p,
  .default-error p {
    font-size: 12px;
  }
}
</style>
