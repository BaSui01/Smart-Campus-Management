<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="wave wave-1"></div>
      <div class="wave wave-2"></div>
    </div>

    <!-- 主要内容 -->
    <div class="login-content">
      <!-- 左侧信息面板 -->
      <div class="info-panel">
        <div class="logo-section">
          <div class="logo">
            <el-icon :size="60" color="#ffffff">
              <School />
            </el-icon>
          </div>
          <h1>智慧校园管理平台</h1>
          <p>连接学习，成就未来</p>
        </div>
        
        <div class="features">
          <div class="feature-item">
            <el-icon :size="24" color="#ffffff"><Check /></el-icon>
            <span>智能化教学管理</span>
          </div>
          <div class="feature-item">
            <el-icon :size="24" color="#ffffff"><Check /></el-icon>
            <span>实时数据分析</span>
          </div>
          <div class="feature-item">
            <el-icon :size="24" color="#ffffff"><Check /></el-icon>
            <span>全方位家校互动</span>
          </div>
          <div class="feature-item">
            <el-icon :size="24" color="#ffffff"><Check /></el-icon>
            <span>安全可靠的系统</span>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-panel">
        <div class="login-form-container">
          <div class="form-header">
            <h2>欢迎登录</h2>
            <p>请选择身份并输入您的登录信息</p>
          </div>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            size="large"
            @keyup.enter="handleLogin"
          >
            <!-- 身份选择 -->
            <div class="role-selector">
              <div class="role-tabs">
                <div
                  v-for="role in roles"
                  :key="role.value"
                  :class="['role-tab', { active: loginForm.role === role.value }]"
                  @click="loginForm.role = role.value"
                >
                  <el-icon :size="20">
                    <component :is="role.icon" />
                  </el-icon>
                  <span>{{ role.label }}</span>
                </div>
              </div>
            </div>

            <!-- 用户名 -->
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                :placeholder="getPlaceholder('username')"
                :prefix-icon="User"
                clearable
              />
            </el-form-item>

            <!-- 密码 -->
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                :placeholder="getPlaceholder('password')"
                :prefix-icon="Lock"
                show-password
                clearable
              />
            </el-form-item>


            <!-- 记住我 -->
            <div class="form-options">
              <el-checkbox v-model="loginForm.remember">
                记住我
              </el-checkbox>
              <el-link type="primary" underline="never">
                忘记密码？
              </el-link>
            </div>

            <!-- 登录按钮 -->
            <el-form-item class="login-button-item">
              <el-button
                :loading="loading"
                type="primary"
                class="login-button"
                @click="handleLogin"
              >
                <span v-if="!loading">立即登录</span>
                <span v-else>登录中...</span>
              </el-button>
            </el-form-item>

            <!-- 其他登录方式 -->
            <div class="other-login">
              <div class="divider">
                <span>其他登录方式</span>
              </div>
              <div class="social-login">
                <el-button circle :icon="Platform" />
                <el-button circle :icon="Position" />
                <el-button circle :icon="Promotion" />
              </div>
            </div>
          </el-form>

          <!-- 底部信息 -->
          <div class="footer-info">
            <p>© 2024 智慧校园管理平台. All rights reserved.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { adminAuthApi } from '@/api/adminAuth'
import { authApi } from '@/api/auth'
import { unifiedAuthApi } from '@/api/unifiedAuth'
import {
  User,
  Lock,
  UserFilled,
  Reading,
  Management,
  Check,
  Platform,
  Position,
  Promotion,
  School
} from '@element-plus/icons-vue'


const router = useRouter()

// 响应式数据
const loginFormRef = ref()
const loading = ref(false)

// 角色配置 - 前端显示用
const roles = [
  { value: 'STUDENT', label: '学生', icon: 'UserFilled' },
  { value: 'TEACHER', label: '教师', icon: 'Reading' },
  { value: 'PARENT', label: '家长', icon: 'Management' }
]

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  role: 'STUDENT',
  remember: false
})

// 动态验证规则 - 根据角色调整用户名验证
const loginRules = computed(() => {
  // 根据选择的角色调整用户名验证规则
  const usernameRules = [
    { required: true, message: getUsernameRequiredMessage(), trigger: 'blur' }
  ]
  
  // 根据角色设置不同的验证规则
  if (loginForm.role === 'STUDENT') {
    usernameRules.push({
      pattern: /^[0-9]{8,12}$/,
      message: '学号应为8-12位数字',
      trigger: 'blur'
    })
  } else if (loginForm.role === 'TEACHER') {
    usernameRules.push({
      pattern: /^[A-Za-z0-9]{4,20}$/,
      message: '工号应为4-20位字母或数字',
      trigger: 'blur'
    })
  } else if (loginForm.role === 'PARENT') {
    usernameRules.push({
      pattern: /^1[3-9]\d{9}$/,
      message: '请输入正确的手机号码',
      trigger: 'blur'
    })
  }
  
  return {
    username: usernameRules,
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
    ]
  }
})

// 获取用户名验证失败消息
const getUsernameRequiredMessage = () => {
  const roleMap = {
    STUDENT: '请输入学号',
    TEACHER: '请输入工号',
    PARENT: '请输入手机号'
  }
  return roleMap[loginForm.role] || '请输入用户名'
}

// 获取占位符文本
const getPlaceholder = (field) => {
  const roleMap = {
    STUDENT: { username: '请输入学号', password: '请输入密码' },
    TEACHER: { username: '请输入工号', password: '请输入密码' },
    PARENT: { username: '请输入手机号', password: '请输入密码' }
  }
  return roleMap[loginForm.role][field]
}


// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 表单验证
    await loginFormRef.value.validate()

    loading.value = true

    // 准备登录数据 - 根据前端角色选择决定登录方式
    const loginData = {
      username: loginForm.username.trim(),
      password: loginForm.password,
      remember: loginForm.remember,
      role: loginForm.role // 传递角色信息给后端
    }

    console.log('开始登录，用户名:', loginData.username, '角色:', loginData.role)

    // 根据角色选择不同的登录API
    let loginResponse
    let loginSuccess = false

    try {
      // 使用统一认证API，它会根据后端实际情况选择合适的登录方式
      loginResponse = await unifiedAuthApi.simulateRoleLogin({
        username: loginData.username,
        password: loginData.password,
        role: loginForm.role,
        rememberMe: loginData.remember
      })
      loginSuccess = true

      if (loginSuccess && loginResponse) {
        console.log('登录响应:', loginResponse)

        // 等待一下让后端处理完成
        await new Promise(resolve => setTimeout(resolve, 500))

        // 尝试获取用户信息
        let userInfo
        try {
          // 优先尝试管理员API获取用户信息
          userInfo = await adminAuthApi.getCurrentUser()
          console.log('获取到用户信息:', userInfo)
        } catch (userError) {
          console.warn('获取用户信息失败，但登录可能成功:', userError)
          // 如果获取失败，创建基本用户信息
          userInfo = {
            username: loginData.username,
            role: loginForm.role,
            loginTime: new Date().toISOString()
          }
        }

        // 处理登录成功后的逻辑
        await handleLoginSuccess(userInfo || { username: loginData.username, role: loginForm.role })

        return
      }

    } catch (loginError) {
      console.error('登录请求失败:', loginError)
      handleLoginError(loginError)
    }

    // 如果走到这里说明登录失败
    ElMessage.error('登录失败，请重试')

  } catch (validationError) {
    console.error('表单验证失败:', validationError)
    ElMessage.error('请检查输入信息')
  } finally {
    loading.value = false
  }
}

// 判断是否使用统一认证API
const shouldUseUnifiedAuth = (role) => {
  // 学生、教师、家长使用统一认证API
  return ['STUDENT', 'TEACHER', 'PARENT'].includes(role)
}

// 处理登录成功
const handleLoginSuccess = async (userInfo) => {
  try {
    // 保存用户信息到localStorage
    const userToSave = {
      ...userInfo,
      selectedRole: loginForm.role, // 保存前端选择的角色
      loginTime: new Date().toISOString()
    }
    localStorage.setItem('userInfo', JSON.stringify(userToSave))

    // 获取token状态
    try {
      let tokenStatus
      if (shouldUseUnifiedAuth(loginForm.role)) {
        // 对于统一认证，可能需要从响应中获取token
        tokenStatus = { valid: true, token: 'auth-session' }
      } else {
        tokenStatus = await adminAuthApi.getTokenStatus()
      }
      
      if (tokenStatus && tokenStatus.valid) {
        localStorage.setItem('userToken', tokenStatus.token || 'auth-session')
      }
    } catch (tokenError) {
      console.warn('获取token状态失败:', tokenError)
      localStorage.setItem('userToken', 'auth-session')
    }

    // 保存记住的登录信息
    if (loginForm.remember) {
      localStorage.setItem('rememberedUsername', loginForm.username)
      localStorage.setItem('rememberedRole', loginForm.role)
    } else {
      localStorage.removeItem('rememberedUsername')
      localStorage.removeItem('rememberedRole')
    }

    ElMessage.success('登录成功，正在跳转...')

    // 根据角色跳转到相应页面
    const targetRoute = getTargetRoute(loginForm.role)
    setTimeout(() => {
      router.push(targetRoute)
    }, 1000)

  } catch (error) {
    console.error('处理登录成功逻辑失败:', error)
    ElMessage.warning('登录成功，但页面跳转异常')
  }
}

// 根据角色获取目标路由
const getTargetRoute = (role) => {
  const routeMap = {
    'STUDENT': '/student/dashboard',
    'TEACHER': '/teacher/dashboard',
    'PARENT': '/parent/dashboard',
    'ADMIN': '/admin/dashboard'
  }
  
  // 如果用户有管理员权限，默认跳转到管理后台
  return routeMap[role] || '/admin/dashboard'
}

// 处理登录错误
const handleLoginError = (loginError) => {
  let errorMessage = '登录失败，请检查用户名和密码'

  if (loginError.message) {
    errorMessage = loginError.message
  } else if (loginError.status === 401) {
    errorMessage = '用户名或密码错误'
  } else if (loginError.status === 403) {
    const roleText = roles.find(r => r.value === loginForm.role)?.label || '该身份'
    errorMessage = `无权限以${roleText}身份登录`
  } else if (loginError.status === 500) {
    errorMessage = '服务器内部错误，请稍后重试'
  } else if (loginError.status === 400) {
    errorMessage = '验证码错误或登录信息不正确'
  }

  ElMessage.error(errorMessage)
}

// 检查登录状态
const checkLoginStatus = async () => {
  const token = localStorage.getItem('userToken')
  const userInfo = localStorage.getItem('userInfo')
  
  if (token && userInfo) {
    try {
      // 检查后端登录状态
      const isLoggedIn = await adminAuthApi.checkLoginStatus()
      if (isLoggedIn) {
        const user = JSON.parse(userInfo)
        // 如果已经登录，直接跳转到管理后台
        router.push('/admin/dashboard')
        return
      }
    } catch (error) {
      console.warn('检查登录状态失败:', error)
    }
    
    // 清除无效的存储数据
    localStorage.removeItem('userToken')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('refreshToken')
  }
}

// 恢复记住的登录信息
const restoreRememberedLogin = () => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  const rememberedRole = localStorage.getItem('rememberedRole')
  
  if (rememberedUsername && rememberedRole) {
    loginForm.username = rememberedUsername
    loginForm.role = rememberedRole
    loginForm.remember = true
  }
}

// 组件挂载时的初始化
onMounted(async () => {
  // 检查是否已经登录
  await checkLoginStatus()
  
  // 恢复记住的登录信息
  restoreRememberedLogin()
  
  // 可选：检查系统健康状态
  try {
    const health = await adminAuthApi.healthCheck()
    console.log('系统状态:', health.message || '正常')
  } catch (error) {
    console.warn('系统健康检查失败:', error)
  }
})
</script>

<style>
/* 导入外部样式文件 */
@import '@/styles/login.css';
</style>
