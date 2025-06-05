import axios from 'axios'

/**
 * 管理后台认证API
 * 专门用于与后端 AuthController 通信
 */

// 创建专用的axios实例用于管理后台认证
const adminApi = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
adminApi.interceptors.request.use(
  config => {
    const token = localStorage.getItem('userToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
adminApi.interceptors.response.use(
  response => {
    // 对于管理后台API，直接返回响应数据
    return response.data
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      const errorMessage = data?.message || data?.error || '请求失败'
      
      // 根据状态码处理不同的错误情况
      switch (status) {
        case 401:
          // 清除过期的认证信息
          localStorage.removeItem('userToken')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('refreshToken')
          break
        case 403:
          console.warn('访问权限不足')
          break
        case 500:
          console.error('服务器内部错误')
          break
      }
      
      // 返回格式化的错误信息
      return Promise.reject({
        status,
        message: errorMessage,
        data: data
      })
    } else if (error.code === 'ECONNABORTED') {
      return Promise.reject({
        status: 408,
        message: '请求超时，请检查网络连接'
      })
    } else {
      return Promise.reject({
        status: 0,
        message: '网络连接失败，请检查网络设置'
      })
    }
  }
)

export const adminAuthApi = {
  /**
   * 用户登录
   * 对应后端: POST /admin/login
   */
  login(loginData) {
    // 将前端格式转换为后端期望的URL编码格式
    const params = new URLSearchParams()
    params.append('username', loginData.username)
    params.append('password', loginData.password)
    params.append('captcha', loginData.captcha || '')
    params.append('rememberMe', loginData.remember || false)
    params.append('redirect', 'dashboard')
    
    return adminApi({
      url: '/admin/login',
      method: 'post',
      data: params.toString(),
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      // 处理重定向响应 - 后端登录成功会返回302重定向
      maxRedirects: 0, // 不自动跟随重定向
      validateStatus: function (status) {
        return (status >= 200 && status < 300) || status === 302
      }
    })
  },

  /**
   * 用户登出
   * 对应后端: POST /admin/logout
   */
  logout() {
    return adminApi({
      url: '/admin/logout',
      method: 'post'
    })
  },

  /**
   * 获取验证码
   * 对应后端: GET /admin/captcha
   */
  getCaptcha() {
    return adminApi({
      url: '/admin/captcha',
      method: 'get',
      responseType: 'blob'
    }).then(blob => {
      // 将blob转换为base64图片
      return new Promise((resolve) => {
        const reader = new FileReader()
        reader.onload = () => {
          resolve({
            success: true,
            data: {
              image: reader.result,
              code: null // 后端不返回验证码文本，仅返回图片
            }
          })
        }
        reader.readAsDataURL(blob)
      })
    })
  },

  /**
   * 刷新Token
   * 对应后端: POST /admin/refresh-token
   */
  refreshToken() {
    return adminApi({
      url: '/admin/refresh-token',
      method: 'post'
    })
  },

  /**
   * 获取Token状态
   * 对应后端: GET /admin/token-status
   */
  getTokenStatus() {
    return adminApi({
      url: '/admin/token-status',
      method: 'get'
    })
  },

  /**
   * 获取当前用户信息
   * 对应后端: GET /admin/current-user
   */
  getCurrentUser() {
    return adminApi({
      url: '/admin/current-user',
      method: 'get'
    })
  },

  /**
   * 检查登录状态
   * 对应后端: GET /admin/check-login
   */
  checkLoginStatus() {
    return adminApi({
      url: '/admin/check-login',
      method: 'get'
    })
  },

  /**
   * 系统健康检查
   * 对应后端: GET /admin/api/health
   */
  healthCheck() {
    return adminApi({
      url: '/admin/api/health',
      method: 'get'
    })
  },

  /**
   * 获取系统版本信息
   * 对应后端: GET /admin/api/version
   */
  getVersionInfo() {
    return adminApi({
      url: '/admin/api/version',
      method: 'get'
    })
  },

  /**
   * 数据库连接测试
   * 对应后端: GET /admin/api/database
   */
  testDatabase() {
    return adminApi({
      url: '/admin/api/database',
      method: 'get'
    })
  },

  /**
   * 修改密码
   * 对应后端: POST /admin/change-password
   */
  changePassword(passwordData) {
    const formData = new FormData()
    formData.append('oldPassword', passwordData.oldPassword)
    formData.append('newPassword', passwordData.newPassword)
    formData.append('confirmPassword', passwordData.confirmPassword)
    
    return adminApi({
      url: '/admin/change-password',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  }
}

export default adminAuthApi