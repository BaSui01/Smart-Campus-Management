import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import CryptoUtil from '@/utils/crypto'
import { API_CONFIG, HTTP_STATUS, ERROR_MESSAGES } from './config'

// 创建专门的管理员 axios 实例
const adminRequest = axios.create({
  baseURL: API_CONFIG.ADMIN_BASE_URL, // 使用 admin base URL
  timeout: API_CONFIG.TIMEOUT,
  headers: API_CONFIG.HEADERS
})

// 是否启用加密通信
const ENABLE_ENCRYPTION = true

// 请求拦截器
adminRequest.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    
    // 添加 token 到请求头
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    
    // 添加请求ID用于追踪
    config.headers['X-Request-ID'] = CryptoUtil.generateRandomString(16)
    
    // 加密请求数据
    if (ENABLE_ENCRYPTION && config.data) {
      try {
        // 对敏感数据进行加密
        if (shouldEncryptRequest(config.url, config.method)) {
          const encryptedData = CryptoUtil.encryptWithTimestamp(config.data)
          config.data = {
            encrypted: true,
            data: encryptedData
          }
          
          // 添加数据签名
          const signature = CryptoUtil.sha256(encryptedData + config.headers['X-Request-ID'])
          config.headers['X-Data-Signature'] = signature
        }
      } catch (error) {
        console.error('请求数据加密失败:', error)
      }
    }
    
    // 添加时间戳
    config.headers['X-Timestamp'] = Date.now()
    
    return config
  },
  (error) => {
    console.error('管理员请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
adminRequest.interceptors.response.use(
  (response) => {
    const { data: responseData } = response
    
    // 解密响应数据
    if (ENABLE_ENCRYPTION && responseData.encrypted) {
      try {
        const decryptedData = CryptoUtil.decryptWithTimestamp(responseData.data)
        response.data = decryptedData
      } catch (error) {
        console.error('响应数据解密失败:', error)
        ElMessage.error('数据解密失败，请刷新重试')
        return Promise.reject(new Error('数据解密失败'))
      }
    }
    
    // 对于管理员页面，可能返回HTML而不是JSON
    if (typeof responseData === 'string' && responseData.includes('<!DOCTYPE html>')) {
      // 如果返回HTML页面，直接返回
      return response
    }
    
    const { code, message, data } = response.data
    
    // 请求成功
    if (code === 200 || code === 0) {
      return { data, message }
    }
    
    // 业务错误
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message || '请求失败'))
  },
  (error) => {
    const { response } = error
    const authStore = useAuthStore()
    
    if (response) {
      const { status, data } = response
      
      switch (status) {
        case 401:
          // 未授权，清除登录状态
          authStore.logout()
          ElMessage.error('登录已过期，请重新登录')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 429:
          ElMessage.error('请求过于频繁，请稍后重试')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        case 502:
          ElMessage.error('网关错误，请稍后重试')
          break
        case 503:
          ElMessage.error('服务暂时不可用')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else if (error.code === 'ERR_NETWORK') {
      ElMessage.error('网络连接失败，请检查网络')
    } else if (error.message && error.message.includes('ERR_INCOMPLETE_CHUNKED_ENCODING')) {
      ElMessage.error('数据传输不完整，请刷新重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

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
    '/admin/login',
    '/admin/users',
    '/admin/roles',
    '/admin/permissions'
  ]
  
  // POST、PUT、PATCH 请求需要加密
  const encryptMethods = ['POST', 'PUT', 'PATCH']
  
  return encryptMethods.includes(method?.toUpperCase()) && 
         sensitiveEndpoints.some(endpoint => url?.includes(endpoint))
}

// 导出管理员请求实例
export default adminRequest