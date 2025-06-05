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
              <el-link type="primary" :underline="false">
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

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 30%;
  right: 30%;
  animation-delay: 4s;
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50% 50% 0 0;
  animation: wave 8s ease-in-out infinite;
}

.wave-1 {
  animation-delay: 0s;
}

.wave-2 {
  animation-delay: 4s;
  height: 80px;
  opacity: 0.7;
}

.login-content {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 600px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;
  z-index: 2;
}

.info-panel {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
}

.logo-section {
  text-align: center;
  margin-bottom: 60px;
}

.logo {
  margin-bottom: 20px;
}

.logo-section h1 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.logo-section p {
  font-size: 18px;
  opacity: 0.9;
  margin: 0;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  opacity: 0.9;
}

.login-panel {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-form-container {
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.form-header p {
  color: #666;
  margin: 0;
}

.role-selector {
  margin-bottom: 30px;
}

.role-tabs {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.role-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px 12px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.role-tab:hover {
  border-color: #409eff;
  background: #f0f8ff;
}

.role-tab.active {
  border-color: #409eff;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
}

.role-tab .el-icon {
  margin-bottom: 6px;
}

.role-tab span {
  font-size: 14px;
  font-weight: 500;
}

.login-form {
  margin-bottom: 20px;
}

.captcha-item {
  margin-bottom: 20px;
}

.captcha-container {
  display: flex;
  align-items: center;
}

.captcha-image {
  width: 100px;
  height: 40px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-family: 'Courier New', monospace;
  font-weight: bold;
  font-size: 18px;
  color: #409eff;
  user-select: none;
}

.captcha-image:hover {
  background: #eeeeee;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.login-button-item {
  margin-bottom: 30px;
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.3);
}

.other-login {
  text-align: center;
}

.divider {
  position: relative;
  margin-bottom: 20px;
}

.divider span {
  background: white;
  padding: 0 15px;
  color: #999;
  font-size: 14px;
}

.divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e0e0e0;
  z-index: 1;
}

.social-login {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.footer-info {
  text-align: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.footer-info p {
  color: #999;
  font-size: 12px;
  margin: 0;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes wave {
  0%, 100% {
    transform: translateX(0px);
  }
  50% {
    transform: translateX(-50px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-content {
    flex-direction: column;
    margin: 20px;
    max-width: none;
  }

  .info-panel {
    padding: 40px 30px;
  }

  .logo-section h1 {
    font-size: 24px;
  }

  .features {
    display: none;
  }

  .login-panel {
    padding: 40px 30px;
  }

  .role-tabs {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .role-tab {
    padding: 12px 8px;
  }

  .role-tab span {
    font-size: 12px;
  }
}
</style>