import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import CryptoUtil from '@/utils/crypto'
import secureHttp, { secureGet, securePost, securePut, secureDelete, securePatch } from '@/utils/secureHttp'
import { API_CONFIG, HTTP_STATUS, ERROR_MESSAGES } from './config'

// 使用安全HTTP客户端作为默认请求实例
const request = secureHttp

// 加密通信配置 - 从环境变量读取
const ENABLE_ENCRYPTION = import.meta.env.VITE_ENCRYPTION_ENABLED === 'true'

// 注意：安全HTTP客户端已经包含了拦截器逻辑
// 这里保留一些额外的业务逻辑处理

/**
 * 响应数据标准化处理
 * @param {Object} response - 响应对象
 * @returns {Object} 标准化后的响应
 */
const normalizeResponse = (response) => {
  const { data: responseData } = response

  // 如果响应数据有标准格式
  if (responseData && typeof responseData === 'object') {
    const { code, message, data } = responseData

    // 请求成功
    if (code === 200 || code === 0) {
      return { data, message, success: true }
    }

    // 业务错误
    return {
      data: null,
      message: message || '请求失败',
      success: false,
      code
    }
  }

  // 直接返回数据
  return {
    data: responseData,
    message: '请求成功',
    success: true
  }
}

// 为安全HTTP客户端添加响应标准化
const originalThen = request.then
if (originalThen) {
  request.then = function(onFulfilled, onRejected) {
    const wrappedOnFulfilled = onFulfilled ? (response) => {
      const normalized = normalizeResponse(response)
      return onFulfilled(normalized)
    } : undefined

    return originalThen.call(this, wrappedOnFulfilled, onRejected)
  }
}

/**
 * 判断是否需要加密请求
 * @param {string} url - 请求URL
 * @param {string} method - 请求方法
 * @returns {boolean} 是否需要加密
 */
function shouldEncryptRequest(url, method) {
  // 敏感接口列表
  const sensitiveEndpoints = [
    '/auth/login',
    '/auth/change-password',
    '/auth/reset-password',
    '/student/profile',
    '/teacher/profile',
    '/parent/profile',
    '/payment/',
    '/grades'
  ]
  
  // POST、PUT、PATCH 请求需要加密
  const encryptMethods = ['POST', 'PUT', 'PATCH']
  
  return encryptMethods.includes(method?.toUpperCase()) && 
         sensitiveEndpoints.some(endpoint => url?.includes(endpoint))
}

/**
 * 创建带重试机制的请求
 * @param {object} config - 请求配置
 * @param {number} retries - 重试次数
 * @returns {Promise} 请求Promise
 */
export const requestWithRetry = async (config, retries = 3) => {
  for (let i = 0; i < retries; i++) {
    try {
      return await request(config)
    } catch (error) {
      if (i === retries - 1) throw error
      
      // 等待一段时间后重试
      await new Promise(resolve => setTimeout(resolve, 1000 * (i + 1)))
    }
  }
}

/**
 * 并发请求控制
 */
class ConcurrencyManager {
  constructor(maxConcurrent = 6) {
    this.maxConcurrent = maxConcurrent
    this.running = 0
    this.queue = []
  }
  
  async request(config) {
    return new Promise((resolve, reject) => {
      this.queue.push({ config, resolve, reject })
      this.process()
    })
  }
  
  async process() {
    if (this.running >= this.maxConcurrent || this.queue.length === 0) {
      return
    }
    
    this.running++
    const { config, resolve, reject } = this.queue.shift()
    
    try {
      const result = await request(config)
      resolve(result)
    } catch (error) {
      reject(error)
    } finally {
      this.running--
      this.process()
    }
  }
}

// 创建并发管理器实例
export const concurrencyManager = new ConcurrencyManager()

// 导出便捷方法
export { secureGet, securePost, securePut, secureDelete, securePatch }

// 导出请求实例
export default request