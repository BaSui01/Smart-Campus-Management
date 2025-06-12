/**
 * 性能优化工具函数
 */

/**
 * 防抖函数
 * @param {Function} func 要防抖的函数
 * @param {number} delay 延迟时间（毫秒）
 * @param {boolean} immediate 是否立即执行
 * @returns {Function} 防抖后的函数
 */
export function debounce(func, delay = 300, immediate = false) {
  let timeoutId
  let lastCallTime
  let lastInvokeTime = 0
  let lastArgs
  let lastThis
  let result

  function invokeFunc(time) {
    const args = lastArgs
    const thisArg = lastThis

    lastArgs = lastThis = undefined
    lastInvokeTime = time
    result = func.apply(thisArg, args)
    return result
  }

  function leadingEdge(time) {
    lastInvokeTime = time
    timeoutId = setTimeout(timerExpired, delay)
    return immediate ? invokeFunc(time) : result
  }

  function remainingWait(time) {
    const timeSinceLastCall = time - lastCallTime
    const timeSinceLastInvoke = time - lastInvokeTime
    const timeWaiting = delay - timeSinceLastCall

    return timeWaiting
  }

  function shouldInvoke(time) {
    const timeSinceLastCall = time - lastCallTime
    const timeSinceLastInvoke = time - lastInvokeTime

    return (lastCallTime === undefined || (timeSinceLastCall >= delay) ||
            (timeSinceLastCall < 0) || (timeSinceLastInvoke >= delay))
  }

  function timerExpired() {
    const time = Date.now()
    if (shouldInvoke(time)) {
      return trailingEdge(time)
    }
    timeoutId = setTimeout(timerExpired, remainingWait(time))
  }

  function trailingEdge(time) {
    timeoutId = undefined

    if (lastArgs) {
      return invokeFunc(time)
    }
    lastArgs = lastThis = undefined
    return result
  }

  function cancel() {
    if (timeoutId !== undefined) {
      clearTimeout(timeoutId)
    }
    lastInvokeTime = 0
    lastArgs = lastCallTime = lastThis = timeoutId = undefined
  }

  function flush() {
    return timeoutId === undefined ? result : trailingEdge(Date.now())
  }

  function debounced(...args) {
    const time = Date.now()
    const isInvoking = shouldInvoke(time)

    lastArgs = args
    lastThis = this
    lastCallTime = time

    if (isInvoking) {
      if (timeoutId === undefined) {
        return leadingEdge(lastCallTime)
      }
    }
    if (timeoutId === undefined) {
      timeoutId = setTimeout(timerExpired, delay)
    }
    return result
  }

  debounced.cancel = cancel
  debounced.flush = flush
  return debounced
}

/**
 * 节流函数
 * @param {Function} func 要节流的函数
 * @param {number} delay 延迟时间（毫秒）
 * @param {Object} options 选项
 * @returns {Function} 节流后的函数
 */
export function throttle(func, delay = 300, options = {}) {
  let timeoutId
  let lastCallTime
  let lastInvokeTime = 0
  let lastArgs
  let lastThis
  let result

  const { leading = true, trailing = true } = options

  function invokeFunc(time) {
    const args = lastArgs
    const thisArg = lastThis

    lastArgs = lastThis = undefined
    lastInvokeTime = time
    result = func.apply(thisArg, args)
    return result
  }

  function leadingEdge(time) {
    lastInvokeTime = time
    timeoutId = setTimeout(timerExpired, delay)
    return leading ? invokeFunc(time) : result
  }

  function remainingWait(time) {
    const timeSinceLastCall = time - lastCallTime
    const timeSinceLastInvoke = time - lastInvokeTime
    const timeWaiting = delay - timeSinceLastInvoke

    return timeWaiting
  }

  function shouldInvoke(time) {
    const timeSinceLastCall = time - lastCallTime
    const timeSinceLastInvoke = time - lastInvokeTime

    return (lastCallTime === undefined || (timeSinceLastInvoke >= delay) ||
            (timeSinceLastCall < 0))
  }

  function timerExpired() {
    const time = Date.now()
    if (shouldInvoke(time)) {
      return trailingEdge(time)
    }
    timeoutId = setTimeout(timerExpired, remainingWait(time))
  }

  function trailingEdge(time) {
    timeoutId = undefined

    if (trailing && lastArgs) {
      return invokeFunc(time)
    }
    lastArgs = lastThis = undefined
    return result
  }

  function cancel() {
    if (timeoutId !== undefined) {
      clearTimeout(timeoutId)
    }
    lastInvokeTime = 0
    lastArgs = lastCallTime = lastThis = timeoutId = undefined
  }

  function flush() {
    return timeoutId === undefined ? result : trailingEdge(Date.now())
  }

  function throttled(...args) {
    const time = Date.now()
    const isInvoking = shouldInvoke(time)

    lastArgs = args
    lastThis = this
    lastCallTime = time

    if (isInvoking) {
      if (timeoutId === undefined) {
        return leadingEdge(lastCallTime)
      }
      if (trailing) {
        timeoutId = setTimeout(timerExpired, delay)
        return invokeFunc(lastCallTime)
      }
    }
    if (timeoutId === undefined) {
      timeoutId = setTimeout(timerExpired, delay)
    }
    return result
  }

  throttled.cancel = cancel
  throttled.flush = flush
  return throttled
}

/**
 * 请求动画帧节流
 * @param {Function} func 要执行的函数
 * @returns {Function} 节流后的函数
 */
export function rafThrottle(func) {
  let isRunning = false
  
  return function(...args) {
    if (isRunning) return
    
    isRunning = true
    requestAnimationFrame(() => {
      func.apply(this, args)
      isRunning = false
    })
  }
}

/**
 * 延迟执行
 * @param {number} ms 延迟时间（毫秒）
 * @returns {Promise} Promise对象
 */
export function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}

/**
 * 批量执行任务
 * @param {Array} tasks 任务数组
 * @param {number} batchSize 批次大小
 * @param {number} batchDelay 批次间延迟（毫秒）
 * @returns {Promise} Promise对象
 */
export async function batchExecute(tasks, batchSize = 10, batchDelay = 100) {
  const results = []
  
  for (let i = 0; i < tasks.length; i += batchSize) {
    const batch = tasks.slice(i, i + batchSize)
    const batchResults = await Promise.all(batch.map(task => 
      typeof task === 'function' ? task() : task
    ))
    
    results.push(...batchResults)
    
    // 如果不是最后一批，则延迟
    if (i + batchSize < tasks.length) {
      await delay(batchDelay)
    }
  }
  
  return results
}

/**
 * 内存优化：清理大对象
 * @param {Object} obj 要清理的对象
 */
export function clearObject(obj) {
  if (obj && typeof obj === 'object') {
    Object.keys(obj).forEach(key => {
      delete obj[key]
    })
  }
}

/**
 * 图片懒加载
 * @param {HTMLElement} img 图片元素
 * @param {string} src 图片源
 * @param {Object} options 选项
 */
export function lazyLoadImage(img, src, options = {}) {
  const { placeholder = '', threshold = 0.1 } = options
  
  if (placeholder) {
    img.src = placeholder
  }
  
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const image = entry.target
        image.src = src
        image.onload = () => {
          image.classList.add('loaded')
        }
        observer.unobserve(image)
      }
    })
  }, { threshold })
  
  observer.observe(img)
  
  return () => observer.unobserve(img)
}

/**
 * 检测设备性能
 * @returns {Object} 性能信息
 */
export function detectPerformance() {
  const connection = navigator.connection || navigator.mozConnection || navigator.webkitConnection
  
  return {
    // 内存信息
    memory: performance.memory ? {
      used: Math.round(performance.memory.usedJSHeapSize / 1048576),
      total: Math.round(performance.memory.totalJSHeapSize / 1048576),
      limit: Math.round(performance.memory.jsHeapSizeLimit / 1048576)
    } : null,
    
    // 网络信息
    network: connection ? {
      effectiveType: connection.effectiveType,
      downlink: connection.downlink,
      rtt: connection.rtt,
      saveData: connection.saveData
    } : null,
    
    // 硬件并发
    hardwareConcurrency: navigator.hardwareConcurrency || 4,
    
    // 设备像素比
    devicePixelRatio: window.devicePixelRatio || 1,
    
    // 是否为移动设备
    isMobile: /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
  }
}

/**
 * 性能监控
 */
export class PerformanceMonitor {
  constructor() {
    this.marks = new Map()
    this.measures = new Map()
  }
  
  // 标记开始时间
  mark(name) {
    const markName = `${name}-start`
    performance.mark(markName)
    this.marks.set(name, markName)
  }
  
  // 测量执行时间
  measure(name) {
    const startMark = this.marks.get(name)
    if (!startMark) {
      console.warn(`No start mark found for: ${name}`)
      return 0
    }
    
    const endMark = `${name}-end`
    performance.mark(endMark)
    
    const measureName = `${name}-measure`
    performance.measure(measureName, startMark, endMark)
    
    const measure = performance.getEntriesByName(measureName)[0]
    const duration = measure.duration
    
    this.measures.set(name, duration)
    
    // 清理性能条目
    performance.clearMarks(startMark)
    performance.clearMarks(endMark)
    performance.clearMeasures(measureName)
    
    return duration
  }
  
  // 获取所有测量结果
  getResults() {
    return Object.fromEntries(this.measures)
  }
  
  // 清理所有数据
  clear() {
    this.marks.clear()
    this.measures.clear()
    performance.clearMarks()
    performance.clearMeasures()
  }
}
