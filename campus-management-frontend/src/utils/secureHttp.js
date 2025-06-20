/**
 * 安全HTTP客户端
 * 集成加密传输、签名验证、防重放攻击等安全功能
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */

import axios from 'axios'
import CryptoUtil, { keyManager } from './crypto.js'
import { useAuthStore } from '@/stores/auth.js'
import { ElMessage } from 'element-plus'

/**
 * 安全配置
 */
const SECURITY_CONFIG = {
  // 请求超时时间
  timeout: 30000,
  
  // 重试配置
  retry: {
    times: 3,
    delay: 1000
  },
  
  // 加密配置
  encryption: {
    enabled: true,
    sensitiveEndpoints: [
      '/auth/login',
      '/auth/register',
      '/users/profile',
      '/students/create',
      '/teachers/create'
    ]
  },
  
  // 签名验证
  signature: {
    enabled: true,
    header: 'X-Signature',
    timestampHeader: 'X-Timestamp'
  }
}

/**
 * 创建安全的axios实例
 */
const createSecureHttpClient = () => {
  const client = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8889/api/v1',
    timeout: SECURITY_CONFIG.timeout,
    headers: {
      'Content-Type': 'application/json',
      'X-Requested-With': 'XMLHttpRequest'
    }
  })

  // 请求拦截器
  client.interceptors.request.use(
    (config) => {
      return enhanceRequestSecurity(config)
    },
    (error) => {
      console.error('请求拦截器错误:', error)
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  client.interceptors.response.use(
    (response) => {
      return handleSecureResponse(response)
    },
    (error) => {
      return handleResponseError(error)
    }
  )

  return client
}

/**
 * 增强请求安全性
 * @param {Object} config - axios配置
 * @returns {Object} 增强后的配置
 */
const enhanceRequestSecurity = (config) => {
  try {
    // 添加认证token
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }

    // 添加时间戳（防重放攻击）
    const timestamp = Date.now().toString()
    config.headers[SECURITY_CONFIG.signature.timestampHeader] = timestamp

    // 检查是否需要加密
    if (shouldEncryptRequest(config)) {
      config = encryptRequestData(config)
    }

    // 添加请求签名
    if (SECURITY_CONFIG.signature.enabled) {
      const signature = generateRequestSignature(config, timestamp)
      config.headers[SECURITY_CONFIG.signature.header] = signature
    }

    // 添加请求ID（用于追踪）
    config.headers['X-Request-ID'] = CryptoUtil.generateRandomString(16)

    // 添加客户端信息
    config.headers['X-Client-Version'] = '1.0.0'
    config.headers['X-Client-Platform'] = 'web'

    console.debug('请求安全增强完成:', {
      url: config.url,
      method: config.method,
      encrypted: !!config._encrypted,
      signed: !!config.headers[SECURITY_CONFIG.signature.header]
    })

    return config
  } catch (error) {
    console.error('请求安全增强失败:', error)
    throw error
  }
}

/**
 * 判断是否需要加密请求
 * @param {Object} config - axios配置
 * @returns {boolean} 是否需要加密
 */
const shouldEncryptRequest = (config) => {
  if (!SECURITY_CONFIG.encryption.enabled) {
    return false
  }

  // 检查是否为敏感端点
  const url = config.url || ''
  return SECURITY_CONFIG.encryption.sensitiveEndpoints.some(endpoint => 
    url.includes(endpoint)
  )
}

/**
 * 加密请求数据
 * @param {Object} config - axios配置
 * @returns {Object} 加密后的配置
 */
const encryptRequestData = (config) => {
  try {
    if (config.data) {
      // 加密请求体
      const encryptedData = CryptoUtil.encryptWithTimestamp(config.data)
      config.data = {
        encrypted: true,
        data: encryptedData
      }
      config._encrypted = true
    }

    return config
  } catch (error) {
    console.error('请求数据加密失败:', error)
    throw new Error('请求数据加密失败')
  }
}

/**
 * 生成请求签名
 * @param {Object} config - axios配置
 * @param {string} timestamp - 时间戳
 * @returns {string} 签名
 */
const generateRequestSignature = (config, timestamp) => {
  try {
    const method = config.method.toUpperCase()
    const url = config.url
    const params = config.params || {}
    
    // 如果数据已加密，使用加密后的数据生成签名
    const data = config._encrypted ? config.data : (config.data || {})
    const allParams = { ...params, ...data }

    return CryptoUtil.generateAPISignature(method, url, allParams, timestamp)
  } catch (error) {
    console.error('请求签名生成失败:', error)
    return ''
  }
}

/**
 * 处理安全响应
 * @param {Object} response - axios响应
 * @returns {Object} 处理后的响应
 */
const handleSecureResponse = (response) => {
  try {
    // 验证响应签名（如果存在）
    if (response.headers[SECURITY_CONFIG.signature.header.toLowerCase()]) {
      const isValid = verifyResponseSignature(response)
      if (!isValid) {
        console.warn('响应签名验证失败')
      }
    }

    // 解密响应数据（如果需要）
    if (response.data && response.data.encrypted) {
      response.data = decryptResponseData(response.data)
    }

    // 处理敏感字段解密
    if (response.data && response.data.data) {
      response.data.data = CryptoUtil.decryptSensitiveFields(response.data.data)
    }

    console.debug('响应安全处理完成:', {
      status: response.status,
      encrypted: !!response.data?.encrypted,
      url: response.config.url
    })

    return response
  } catch (error) {
    console.error('响应安全处理失败:', error)
    return response
  }
}

/**
 * 验证响应签名
 * @param {Object} response - axios响应
 * @returns {boolean} 验证结果
 */
const verifyResponseSignature = (response) => {
  try {
    const signature = response.headers[SECURITY_CONFIG.signature.header.toLowerCase()]
    const timestamp = response.headers[SECURITY_CONFIG.signature.timestampHeader.toLowerCase()]
    
    if (!signature || !timestamp) {
      return false
    }

    const dataString = JSON.stringify(response.data)
    return CryptoUtil.verifyHMAC(dataString + timestamp, signature)
  } catch (error) {
    console.error('响应签名验证失败:', error)
    return false
  }
}

/**
 * 解密响应数据
 * @param {Object} encryptedResponse - 加密的响应数据
 * @returns {Object} 解密后的数据
 */
const decryptResponseData = (encryptedResponse) => {
  try {
    if (encryptedResponse.encrypted && encryptedResponse.data) {
      return CryptoUtil.decryptWithTimestamp(encryptedResponse.data)
    }
    return encryptedResponse
  } catch (error) {
    console.error('响应数据解密失败:', error)
    throw new Error('响应数据解密失败')
  }
}

/**
 * 处理响应错误
 * @param {Object} error - axios错误
 * @returns {Promise} 拒绝的Promise
 */
const handleResponseError = (error) => {
  console.error('HTTP请求错误:', error)

  // 网络错误
  if (!error.response) {
    ElMessage.error('网络连接失败，请检查网络设置')
    return Promise.reject(new Error('网络连接失败'))
  }

  // HTTP状态码错误
  const { status, data } = error.response

  switch (status) {
    case 401:
      // 未授权，清除token并跳转登录
      const authStore = useAuthStore()
      authStore.logout()
      ElMessage.error('登录已过期，请重新登录')
      break
    
    case 403:
      ElMessage.error('权限不足，无法访问该资源')
      break
    
    case 404:
      ElMessage.error('请求的资源不存在')
      break
    
    case 429:
      ElMessage.error('请求过于频繁，请稍后再试')
      break
    
    case 500:
      ElMessage.error('服务器内部错误，请稍后再试')
      break
    
    default:
      const message = data?.message || `请求失败 (${status})`
      ElMessage.error(message)
  }

  return Promise.reject(error)
}

// 创建全局安全HTTP客户端实例
const secureHttp = createSecureHttpClient()

// 导出便捷方法
export const secureGet = (url, config = {}) => secureHttp.get(url, config)
export const securePost = (url, data = {}, config = {}) => secureHttp.post(url, data, config)
export const securePut = (url, data = {}, config = {}) => secureHttp.put(url, data, config)
export const secureDelete = (url, config = {}) => secureHttp.delete(url, config)
export const securePatch = (url, data = {}, config = {}) => secureHttp.patch(url, data, config)

export default secureHttp
