import { ref, computed, onMounted, onUnmounted } from 'vue'

/**
 * 虚拟列表组合式函数
 * 用于优化大量数据的列表渲染性能
 */
export function useVirtualList(options = {}) {
  const {
    itemHeight = 80,
    containerHeight = 400,
    overscan = 5,
    getItemHeight = null
  } = options

  // 响应式数据
  const containerRef = ref()
  const scrollTop = ref(0)
  const containerSize = ref(containerHeight)
  const items = ref([])

  // 计算属性
  const visibleCount = computed(() => {
    return Math.ceil(containerSize.value / itemHeight) + overscan * 2
  })

  const startIndex = computed(() => {
    const index = Math.floor(scrollTop.value / itemHeight) - overscan
    return Math.max(0, index)
  })

  const endIndex = computed(() => {
    const index = startIndex.value + visibleCount.value
    return Math.min(items.value.length - 1, index)
  })

  const visibleItems = computed(() => {
    return items.value.slice(startIndex.value, endIndex.value + 1).map((item, index) => ({
      ...item,
      index: startIndex.value + index
    }))
  })

  const totalHeight = computed(() => {
    if (getItemHeight) {
      return items.value.reduce((total, item, index) => {
        return total + getItemHeight(item, index)
      }, 0)
    }
    return items.value.length * itemHeight
  })

  const offsetY = computed(() => {
    if (getItemHeight) {
      let offset = 0
      for (let i = 0; i < startIndex.value; i++) {
        offset += getItemHeight(items.value[i], i)
      }
      return offset
    }
    return startIndex.value * itemHeight
  })

  // 方法
  const handleScroll = (event) => {
    scrollTop.value = event.target.scrollTop
  }

  const scrollToIndex = (index) => {
    if (!containerRef.value) return
    
    let offset = 0
    if (getItemHeight) {
      for (let i = 0; i < index; i++) {
        offset += getItemHeight(items.value[i], i)
      }
    } else {
      offset = index * itemHeight
    }
    
    containerRef.value.scrollTop = offset
  }

  const scrollToTop = () => {
    if (containerRef.value) {
      containerRef.value.scrollTop = 0
    }
  }

  const scrollToBottom = () => {
    if (containerRef.value) {
      containerRef.value.scrollTop = totalHeight.value
    }
  }

  const updateContainerSize = () => {
    if (containerRef.value) {
      containerSize.value = containerRef.value.clientHeight
    }
  }

  const setItems = (newItems) => {
    items.value = newItems
  }

  // 生命周期
  onMounted(() => {
    updateContainerSize()
    window.addEventListener('resize', updateContainerSize)
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateContainerSize)
  })

  return {
    containerRef,
    visibleItems,
    totalHeight,
    offsetY,
    handleScroll,
    scrollToIndex,
    scrollToTop,
    scrollToBottom,
    setItems,
    startIndex,
    endIndex
  }
}
