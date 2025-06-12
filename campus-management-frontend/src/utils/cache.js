/**
 * 缓存管理工具
 */

/**
 * 内存缓存类
 */
export class MemoryCache {
  constructor(options = {}) {
    this.cache = new Map()
    this.timers = new Map()
    this.maxSize = options.maxSize || 100
    this.defaultTTL = options.defaultTTL || 5 * 60 * 1000 // 5分钟
  }

  /**
   * 设置缓存
   * @param {string} key 缓存键
   * @param {any} value 缓存值
   * @param {number} ttl 过期时间（毫秒）
   */
  set(key, value, ttl = this.defaultTTL) {
    // 如果缓存已满，删除最旧的条目
    if (this.cache.size >= this.maxSize && !this.cache.has(key)) {
      const firstKey = this.cache.keys().next().value
      this.delete(firstKey)
    }

    // 清除旧的定时器
    if (this.timers.has(key)) {
      clearTimeout(this.timers.get(key))
    }

    // 设置缓存
    this.cache.set(key, {
      value,
      timestamp: Date.now(),
      ttl
    })

    // 设置过期定时器
    if (ttl > 0) {
      const timer = setTimeout(() => {
        this.delete(key)
      }, ttl)
      this.timers.set(key, timer)
    }
  }

  /**
   * 获取缓存
   * @param {string} key 缓存键
   * @returns {any} 缓存值
   */
  get(key) {
    const item = this.cache.get(key)
    if (!item) return undefined

    // 检查是否过期
    if (item.ttl > 0 && Date.now() - item.timestamp > item.ttl) {
      this.delete(key)
      return undefined
    }

    return item.value
  }

  /**
   * 检查缓存是否存在
   * @param {string} key 缓存键
   * @returns {boolean} 是否存在
   */
  has(key) {
    return this.get(key) !== undefined
  }

  /**
   * 删除缓存
   * @param {string} key 缓存键
   */
  delete(key) {
    this.cache.delete(key)
    if (this.timers.has(key)) {
      clearTimeout(this.timers.get(key))
      this.timers.delete(key)
    }
  }

  /**
   * 清空所有缓存
   */
  clear() {
    this.cache.clear()
    this.timers.forEach(timer => clearTimeout(timer))
    this.timers.clear()
  }

  /**
   * 获取缓存大小
   * @returns {number} 缓存大小
   */
  size() {
    return this.cache.size
  }

  /**
   * 获取所有缓存键
   * @returns {Array} 缓存键数组
   */
  keys() {
    return Array.from(this.cache.keys())
  }
}

/**
 * LocalStorage缓存类
 */
export class LocalStorageCache {
  constructor(prefix = 'cache_') {
    this.prefix = prefix
  }

  /**
   * 生成缓存键
   * @param {string} key 原始键
   * @returns {string} 带前缀的键
   */
  _getKey(key) {
    return `${this.prefix}${key}`
  }

  /**
   * 设置缓存
   * @param {string} key 缓存键
   * @param {any} value 缓存值
   * @param {number} ttl 过期时间（毫秒）
   */
  set(key, value, ttl = 0) {
    try {
      const item = {
        value,
        timestamp: Date.now(),
        ttl
      }
      localStorage.setItem(this._getKey(key), JSON.stringify(item))
    } catch (error) {
      console.warn('LocalStorage cache set failed:', error)
    }
  }

  /**
   * 获取缓存
   * @param {string} key 缓存键
   * @returns {any} 缓存值
   */
  get(key) {
    try {
      const itemStr = localStorage.getItem(this._getKey(key))
      if (!itemStr) return undefined

      const item = JSON.parse(itemStr)
      
      // 检查是否过期
      if (item.ttl > 0 && Date.now() - item.timestamp > item.ttl) {
        this.delete(key)
        return undefined
      }

      return item.value
    } catch (error) {
      console.warn('LocalStorage cache get failed:', error)
      return undefined
    }
  }

  /**
   * 检查缓存是否存在
   * @param {string} key 缓存键
   * @returns {boolean} 是否存在
   */
  has(key) {
    return this.get(key) !== undefined
  }

  /**
   * 删除缓存
   * @param {string} key 缓存键
   */
  delete(key) {
    try {
      localStorage.removeItem(this._getKey(key))
    } catch (error) {
      console.warn('LocalStorage cache delete failed:', error)
    }
  }

  /**
   * 清空所有缓存
   */
  clear() {
    try {
      const keys = Object.keys(localStorage)
      keys.forEach(key => {
        if (key.startsWith(this.prefix)) {
          localStorage.removeItem(key)
        }
      })
    } catch (error) {
      console.warn('LocalStorage cache clear failed:', error)
    }
  }
}

/**
 * 请求缓存装饰器
 * @param {Object} cache 缓存实例
 * @param {Function} keyGenerator 键生成函数
 * @param {number} ttl 过期时间
 * @returns {Function} 装饰器函数
 */
export function cacheRequest(cache, keyGenerator, ttl) {
  return function(target, propertyKey, descriptor) {
    const originalMethod = descriptor.value

    descriptor.value = async function(...args) {
      const cacheKey = keyGenerator ? keyGenerator(...args) : `${propertyKey}_${JSON.stringify(args)}`
      
      // 尝试从缓存获取
      const cachedResult = cache.get(cacheKey)
      if (cachedResult !== undefined) {
        return cachedResult
      }

      // 执行原方法
      const result = await originalMethod.apply(this, args)
      
      // 缓存结果
      cache.set(cacheKey, result, ttl)
      
      return result
    }

    return descriptor
  }
}

/**
 * 创建缓存包装函数
 * @param {Function} fn 原函数
 * @param {Object} cache 缓存实例
 * @param {Function} keyGenerator 键生成函数
 * @param {number} ttl 过期时间
 * @returns {Function} 包装后的函数
 */
export function createCachedFunction(fn, cache, keyGenerator, ttl) {
  return async function(...args) {
    const cacheKey = keyGenerator ? keyGenerator(...args) : `${fn.name}_${JSON.stringify(args)}`
    
    // 尝试从缓存获取
    const cachedResult = cache.get(cacheKey)
    if (cachedResult !== undefined) {
      return cachedResult
    }

    // 执行原函数
    const result = await fn.apply(this, args)
    
    // 缓存结果
    cache.set(cacheKey, result, ttl)
    
    return result
  }
}

/**
 * LRU缓存类
 */
export class LRUCache {
  constructor(capacity = 50) {
    this.capacity = capacity
    this.cache = new Map()
  }

  get(key) {
    if (this.cache.has(key)) {
      // 移动到最前面
      const value = this.cache.get(key)
      this.cache.delete(key)
      this.cache.set(key, value)
      return value
    }
    return undefined
  }

  set(key, value) {
    if (this.cache.has(key)) {
      // 更新现有键
      this.cache.delete(key)
    } else if (this.cache.size >= this.capacity) {
      // 删除最旧的键
      const firstKey = this.cache.keys().next().value
      this.cache.delete(firstKey)
    }
    
    this.cache.set(key, value)
  }

  has(key) {
    return this.cache.has(key)
  }

  delete(key) {
    return this.cache.delete(key)
  }

  clear() {
    this.cache.clear()
  }

  size() {
    return this.cache.size
  }
}

// 创建全局缓存实例
export const memoryCache = new MemoryCache({
  maxSize: 200,
  defaultTTL: 10 * 60 * 1000 // 10分钟
})

export const localStorageCache = new LocalStorageCache('campus_')

export const lruCache = new LRUCache(100)
