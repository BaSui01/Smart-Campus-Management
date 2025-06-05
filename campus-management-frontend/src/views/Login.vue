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
                <div class="captcha-image" @click="refreshCaptcha">
                  <span v-if="captchaCode">{{ captchaCode }}</span>
                  <el-icon v-else><RefreshRight /></el-icon>
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
import { ElMessage } from 'element-plus'
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

// 角色配置 - 移除管理员选项
const roles = [
  { value: 'student', label: '学生', icon: 'UserFilled' },
  { value: 'teacher', label: '教师', icon: 'Reading' },
  { value: 'parent', label: '家长', icon: 'Management' }
]

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  captcha: '',
  role: 'student',
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
    student: { username: '请输入学号', password: '请输入密码' },
    teacher: { username: '请输入工号', password: '请输入密码' },
    parent: { username: '请输入手机号', password: '请输入密码' }
  }
  return roleMap[loginForm.role][field]
}

// 生成验证码
const generateCaptcha = () => {
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789'
  let result = ''
  for (let i = 0; i < 4; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

// 刷新验证码
const refreshCaptcha = () => {
  captchaCode.value = generateCaptcha()
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    
    // 验证码检查
    if (loginForm.captcha.toLowerCase() !== captchaCode.value.toLowerCase()) {
      ElMessage.error('验证码错误')
      refreshCaptcha()
      return
    }

    loading.value = true

    // 模拟登录API调用
    await new Promise(resolve => setTimeout(resolve, 1500))

    // 根据角色跳转到不同的页面
    const roleRoutes = {
      student: '/student',
      teacher: '/teacher', 
      parent: '/parent'
    }

    // 保存用户信息到本地存储
    const userInfo = {
      username: loginForm.username,
      role: loginForm.role,
      token: 'mock-token-' + Date.now()
    }
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
    localStorage.setItem('userToken', userInfo.token)

    ElMessage.success('登录成功')
    
    // 跳转到对应的角色页面
    router.push(roleRoutes[loginForm.role])

  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查用户名和密码')
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

// 组件挂载时生成验证码
onMounted(() => {
  refreshCaptcha()
})
</script>

<style>
/* 导入外部样式文件 */
@import '@/styles/login.css';
</style>
