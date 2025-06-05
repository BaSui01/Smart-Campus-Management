import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const permissions = ref(JSON.parse(localStorage.getItem('permissions') || '[]'))
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))
  const isLoading = ref(false)
  const loginExpired = ref(false)

  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => userInfo.value.role || '')
  const userId = computed(() => userInfo.value.id || '')
  const userName = computed(() => userInfo.value.name || '')
  const userAvatar = computed(() => userInfo.value.avatar || '')

  // 检查是否有特定权限
  const hasPermission = computed(() => {
    return (permission) => {
      if (!permission) return true
      if (Array.isArray(permission)) {
        return permission.some(p => permissions.value.includes(p))
      }
      return permissions.value.includes(permission)
    }
  })

  // 检查是否有特定角色
  const hasRole = computed(() => {
    return (role) => {
      if (!role) return true
      if (Array.isArray(role)) {
        return role.some(r => roles.value.includes(r))
      }
      return roles.value.includes(role)
    }
  })

  // 检查是否是学生
  const isStudent = computed(() => userRole.value === 'student')
  // 检查是否是教师
  const isTeacher = computed(() => userRole.value === 'teacher')
  // 检查是否是家长
  const isParent = computed(() => userRole.value === 'parent')
  // 检查是否是管理员
  const isAdmin = computed(() => userRole.value === 'admin')

  // 方法
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setRefreshToken = (newRefreshToken) => {
    refreshToken.value = newRefreshToken
    localStorage.setItem('refreshToken', newRefreshToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const setPermissions = (perms) => {
    permissions.value = perms
    localStorage.setItem('permissions', JSON.stringify(perms))
  }

  const setRoles = (roleList) => {
    roles.value = roleList
    localStorage.setItem('roles', JSON.stringify(roleList))
  }

  // 登录
  const login = async (credentials) => {
    try {
      isLoading.value = true
      loginExpired.value = false
      
      const { data } = await authApi.login(credentials)
      
      setToken(data.accessToken)
      setRefreshToken(data.refreshToken)
      setUserInfo(data.user)
      setPermissions(data.permissions || [])
      setRoles(data.roles || [])
      
      ElMessage.success('登录成功')
      
      // 根据用户角色跳转到对应的首页
      const roleRouteMap = {
        student: '/student/dashboard',
        teacher: '/teacher/dashboard',
        parent: '/parent/dashboard',
        admin: '/admin/dashboard'
      }
      
      const targetRoute = roleRouteMap[data.user.role] || '/dashboard'
      router.push(targetRoute)
      
      return data
    } catch (error) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  // 注册
  const register = async (userData) => {
    try {
      isLoading.value = true
      
      const { data } = await authApi.register(userData)
      
      ElMessage.success('注册成功，请登录')
      router.push('/login')
      
      return data
    } catch (error) {
      console.error('注册失败:', error)
      ElMessage.error(error.message || '注册失败')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  // 登出
  const logout = async (showMessage = true) => {
    try {
      if (token.value) {
        await authApi.logout()
      }
    } catch (error) {
      console.error('登出失败:', error)
    } finally {
      clearAuthData()
      if (showMessage) {
        ElMessage.success('已安全退出')
      }
      router.push('/login')
    }
  }

  // 清除认证数据
  const clearAuthData = () => {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = {}
    permissions.value = []
    roles.value = []
    
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
    localStorage.removeItem('roles')
  }

  // 刷新token
  const refreshAuthToken = async () => {
    try {
      if (!refreshToken.value) {
        throw new Error('没有刷新令牌')
      }
      
      const { data } = await authApi.refreshToken(refreshToken.value)
      
      setToken(data.accessToken)
      if (data.refreshToken) {
        setRefreshToken(data.refreshToken)
      }
      
      return data.accessToken
    } catch (error) {
      console.error('刷新token失败:', error)
      // 刷新失败，清除认证信息并跳转到登录页
      await logout(false)
      loginExpired.value = true
      ElMessage.warning('登录已过期，请重新登录')
      throw error
    }
  }

  // 获取当前用户信息
  const getCurrentUser = async () => {
    try {
      const { data } = await authApi.getCurrentUser()
      setUserInfo(data)
      return data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 更新用户信息
  const updateUserInfo = async (updates) => {
    try {
      const updatedInfo = { ...userInfo.value, ...updates }
      setUserInfo(updatedInfo)
      return updatedInfo
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  // 修改密码
  const changePassword = async (passwordData) => {
    try {
      isLoading.value = true
      await authApi.changePassword(passwordData)
      ElMessage.success('密码修改成功，请重新登录')
      await logout(false)
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '修改密码失败')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  // 忘记密码
  const forgotPassword = async (email) => {
    try {
      isLoading.value = true
      await authApi.forgotPassword({ email })
      ElMessage.success('重置密码邮件已发送，请查收')
    } catch (error) {
      console.error('发送重置密码邮件失败:', error)
      ElMessage.error(error.message || '发送失败')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  // 重置密码
  const resetPassword = async (resetData) => {
    try {
      isLoading.value = true
      await authApi.resetPassword(resetData)
      ElMessage.success('密码重置成功，请登录')
      router.push('/login')
    } catch (error) {
      console.error('重置密码失败:', error)
      ElMessage.error(error.message || '重置密码失败')
      throw error
    } finally {
      isLoading.value = false
    }
  }

  // 检查认证状态
  const checkAuthStatus = async () => {
    if (!token.value) {
      return false
    }

    try {
      await getCurrentUser()
      return true
    } catch (error) {
      // 如果获取用户信息失败，尝试刷新token
      if (refreshToken.value) {
        try {
          await refreshAuthToken()
          await getCurrentUser()
          return true
        } catch (refreshError) {
          return false
        }
      }
      return false
    }
  }

  // 获取用户权限
  const loadUserPermissions = async () => {
    try {
      const { data } = await authApi.getUserPermissions()
      setPermissions(data)
      return data
    } catch (error) {
      console.error('获取用户权限失败:', error)
      throw error
    }
  }

  // 获取用户角色
  const loadUserRoles = async () => {
    try {
      const { data } = await authApi.getUserRoles()
      setRoles(data)
      return data
    } catch (error) {
      console.error('获取用户角色失败:', error)
      throw error
    }
  }

  // 初始化认证状态
  const initializeAuth = async () => {
    if (token.value) {
      const isValid = await checkAuthStatus()
      if (!isValid) {
        clearAuthData()
      }
    }
  }

  // 处理未授权错误
  const handleUnauthorized = () => {
    clearAuthData()
    loginExpired.value = true
    ElMessage.warning('登录已过期，请重新登录')
    router.push('/login')
  }

  // 监听localStorage变化（多标签页同步）
  const handleStorageChange = (event) => {
    if (event.key === 'token') {
      if (event.newValue !== token.value) {
        if (event.newValue) {
          // 其他标签页登录了，同步状态
          token.value = event.newValue
          userInfo.value = JSON.parse(localStorage.getItem('userInfo') || '{}')
          permissions.value = JSON.parse(localStorage.getItem('permissions') || '[]')
          roles.value = JSON.parse(localStorage.getItem('roles') || '[]')
        } else {
          // 其他标签页登出了，清除当前状态
          clearAuthData()
          router.push('/login')
        }
      }
    }
  }

  // 添加存储事件监听器
  if (typeof window !== 'undefined') {
    window.addEventListener('storage', handleStorageChange)
  }

  return {
    // 状态
    token,
    refreshToken,
    userInfo,
    permissions,
    roles,
    isLoading,
    loginExpired,
    
    // 计算属性
    isAuthenticated,
    userRole,
    userId,
    userName,
    userAvatar,
    hasPermission,
    hasRole,
    isStudent,
    isTeacher,
    isParent,
    isAdmin,
    
    // 方法
    login,
    register,
    logout,
    clearAuthData,
    refreshAuthToken,
    getCurrentUser,
    updateUserInfo,
    changePassword,
    forgotPassword,
    resetPassword,
    checkAuthStatus,
    loadUserPermissions,
    loadUserRoles,
    initializeAuth,
    handleUnauthorized,
    setToken,
    setRefreshToken,
    setUserInfo,
    setPermissions,
    setRoles
  }
})

export default useAuthStore