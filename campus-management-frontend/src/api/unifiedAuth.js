import request from './request'
import { API_CONFIG } from './config'

/**
 * 统一认证API
 * 支持学生、教师、家长的登录认证
 */

// 创建统一认证的axios实例
const authApi = request

export const unifiedAuthApi = {
  /**
   * 统一登录接口
   * 支持根据用户类型进行不同的认证处理
   */
  login(loginData) {
    // 根据用户类型调整登录参数
    const { username, password, captcha, userType, rememberMe } = loginData
    
    // 准备登录数据
    const authData = {
      username: username.trim(),
      password,
      captcha: captcha?.trim() || '',
      userType: userType?.toLowerCase() || 'student',
      rememberMe: rememberMe || false
    }

    console.log('统一认证登录:', authData)

    return authApi({
      url: '/auth/login',
      method: 'post',
      data: authData,
      headers: {
        'Content-Type': 'application/json'
      }
    })
  },

  // /**
  //  * 角色验证登录
  //  * 验证用户是否有指定角色的登录权限
  //  */
  // validateRoleLogin(username, role) {
  //   return authApi({
  //     url: '/auth/validate-role',
  //     method: 'post',
  //     data: { username, role }
  //   })
  // },

  /**
   * 获取用户角色信息
   */
  getUserRoles(username) {
    return authApi({
      url: '/auth/user-roles',
      method: 'get',
      params: { username }
    })
  },

  /**
   * 模拟统一登录 - 后端角色映射
   * 当后端只有统一用户系统时，前端通过角色选择来适配
   */
  simulateRoleLogin(loginData) {
    const { username, password, captcha, role, rememberMe } = loginData
    
    // 构建符合后端期望的登录请求
    const params = new URLSearchParams()
    params.append('username', username)
    params.append('password', password)
    params.append('captcha', captcha || '')
    params.append('rememberMe', rememberMe || false)
    
    // 添加角色标识，让后端知道前端选择的角色
    params.append('clientRole', role)
    params.append('redirect', this.getRoleRedirect(role))
    
    return authApi({
      url: '/admin/login', // 使用后端的统一登录接口
      method: 'post',
      data: params.toString(),
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      },
      maxRedirects: 0,
      validateStatus: function (status) {
        return (status >= 200 && status < 300) || status === 302
      }
    })
  },

  /**
   * 根据角色获取重定向路径
   */
  getRoleRedirect(role) {
    const redirectMap = {
      'STUDENT': 'student',
      'TEACHER': 'teacher',
      'PARENT': 'parent',
      'ADMIN': 'dashboard'
    }
    return redirectMap[role] || 'dashboard'
  },

  /**
   * 验证用户是否有指定角色权限
   */
  async validateUserRole(username, selectedRole) {
    try {
      // 这里可以调用后端API验证用户是否有对应角色
      // 暂时返回true，实际应该根据后端角色系统验证
      
      // 示例验证逻辑：
      // - 如果用户名是数字，可能是学生
      // - 如果用户名是字母+数字，可能是教师
      // - 如果用户名是手机号，可能是家长
      
      if (selectedRole === 'STUDENT' && /^\d+$/.test(username)) {
        return { valid: true, message: '学生身份验证通过' }
      }
      
      if (selectedRole === 'TEACHER' && /^[A-Za-z]\w+$/.test(username)) {
        return { valid: true, message: '教师身份验证通过' }
      }
      
      if (selectedRole === 'PARENT' && /^1[3-9]\d{9}$/.test(username)) {
        return { valid: true, message: '家长身份验证通过' }
      }
      
      // 默认允许，由后端最终验证
      return { valid: true, message: '身份验证通过' }
      
    } catch (error) {
      console.warn('角色验证失败:', error)
      return { valid: false, message: '身份验证失败' }
    }
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser() {
    return authApi({
      url: '/auth/me',
      method: 'get'
    })
  },

  /**
   * 登出
   */
  logout() {
    return authApi({
      url: '/auth/logout',
      method: 'post'
    })
  },

  /**
   * 刷新Token
   */
  refreshToken(refreshToken) {
    return authApi({
      url: '/auth/refresh-token',
      method: 'post',
      data: { refreshToken }
    })
  },

  /**
   * 获取验证码
   */
  getCaptcha() {
    return authApi({
      url: '/auth/captcha',
      method: 'get',
      responseType: 'blob'
    }).then(response => {
      return new Promise((resolve, reject) => {
        try {
          const reader = new FileReader()
          reader.onload = () => {
            resolve({
              success: true,
              data: {
                image: reader.result,
                code: null
              }
            })
          }
          reader.onerror = () => {
            reject(new Error('读取验证码图片失败'))
          }
          reader.readAsDataURL(response.data)
        } catch (error) {
          reject(error)
        }
      })
    }).catch(error => {
      console.error('获取统一验证码失败:', error)
      return {
        success: false,
        message: '获取验证码失败',
        error: error.message
      }
    })
  }
}

export default unifiedAuthApi