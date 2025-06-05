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

            <!-- 验证码 -->
            <el-form-item prop="captcha" class="captcha-item">
              <div class="captcha-container">
                <el-input
                  v-model="loginForm.captcha"
                  placeholder="请输入验证码"
                  :prefix-icon="SafetyIcon"
                  clearable
                  style="flex: 1; margin-right: 12px;"
                />
                <div class="captcha-image" @click="refreshCaptcha" title="点击刷新验证码">
                  <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                  <span v-else-if="captchaCode" class="captcha-text">{{ captchaCode }}</span>
                  <el-icon v-else class="refresh-icon"><RefreshRight /></el-icon>
                </div>
              </div>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { adminAuthApi } from '@/api/adminAuth'
import {
  User,
  Lock,
  UserFilled,
  Reading,
  Management,
  Check,
  RefreshRight,
  Platform,
  Position,
  Promotion,
  School
} from '@element-plus/icons-vue'

// 使用Reading图标替代Shield
const SafetyIcon = Reading

const router = useRouter()

// 响应式数据
const loginFormRef = ref()
const loading = ref(false)
const captchaCode = ref('')
const captchaImage = ref('')

// 角色配置
const roles = [
  { value: 'STUDENT', label: '学生', icon: 'UserFilled' },
  { value: 'TEACHER', label: '教师', icon: 'Reading' },
  { value: 'PARENT', label: '家长', icon: 'Management' }
]

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  role: 'STUDENT',
  remember: false
})

// 表单验证规则
const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  captcha: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 4, message: '验证码为4位字符', trigger: 'blur' }
  ]
})

// 获取占位符文本
const getPlaceholder = (field) => {
  const roleMap = {
    STUDENT: { username: '请输入学号', password: '请输入密码' },
    TEACHER: { username: '请输入工号', password: '请输入密码' },
    PARENT: { username: '请输入手机号', password: '请输入密码' }
  }
  return roleMap[loginForm.role][field]
}

// 从后端获取验证码
const getCaptchaFromServer = async () => {
  try {
    const response = await adminAuthApi.getCaptcha()
    if (response.success) {
      captchaCode.value = response.data.code
      captchaImage.value = response.data.image
    } else {
      // 如果后端验证码服务不可用，使用前端生成
      generateCaptcha()
    }
  } catch (error) {
    console.warn('获取验证码失败，使用本地生成:', error)
    generateCaptcha()
  }
}

// 前端生成验证码的备用方案
const generateCaptcha = () => {
  const chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678'
  let result = ''
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  captchaCode.value = result
  captchaImage.value = '' // 清除图片，显示文本验证码
}

// 刷新验证码
const refreshCaptcha = () => {
  getCaptchaFromServer()
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 准备登录数据 - 适配后端期望的格式
    const loginData = {
      username: loginForm.username.trim(),
      password: loginForm.password,
      captcha: loginForm.captcha.trim(),
      remember: loginForm.remember
    }

    // 调用后端登录API
    try {
      const response = await adminAuthApi.login(loginData)
      
      // 后端登录成功后会重定向，我们需要处理这种情况
      // 如果是重定向响应，说明登录成功
      if (response && response.status >= 200 && response.status < 400) {
        // 登录成功，尝试获取用户信息
        try {
          const userInfo = await adminAuthApi.getCurrentUser()
          if (userInfo) {
            // 保存用户信息
            localStorage.setItem('userInfo', JSON.stringify(userInfo))
            
            // 获取并保存token状态
            const tokenStatus = await adminAuthApi.getTokenStatus()
            if (tokenStatus && tokenStatus.valid) {
              localStorage.setItem('userToken', 'admin-session') // 标记为管理员会话
            }
            
            if (loginForm.remember) {
              localStorage.setItem('rememberedUsername', loginForm.username)
              localStorage.setItem('rememberedRole', loginForm.role)
            } else {
              localStorage.removeItem('rememberedUsername')
              localStorage.removeItem('rememberedRole')
            }

            ElMessage.success('登录成功，正在跳转...')
            
            // 跳转到管理后台
            setTimeout(() => {
              router.push('/admin/dashboard')
            }, 1000)
            
            return
          }
        } catch (userError) {
          console.warn('获取用户信息失败:', userError)
        }
      }
      
      // 如果没有获取到用户信息，显示错误
      ElMessage.error('登录失败，请重试')
      refreshCaptcha()
      
    } catch (loginError) {
      console.error('登录请求失败:', loginError)
      
      if (loginError.message) {
        ElMessage.error(loginError.message)
      } else {
        ElMessage.error('登录失败，请检查用户名和密码')
      }
      
      refreshCaptcha()
    }

  } catch (validationError) {
    console.error('表单验证失败:', validationError)
  } finally {
    loading.value = false
  }
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
  
  // 获取验证码
  await getCaptchaFromServer()
  
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
