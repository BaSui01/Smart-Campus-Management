<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-content">
        <!-- 错误图标 -->
        <div class="error-icon">
          <div class="icon-wrapper">
            <el-icon :size="120" color="#f56c6c">
              <Lock />
            </el-icon>
          </div>
          <div class="error-code">403</div>
        </div>

        <!-- 错误信息 -->
        <div class="error-info">
          <h1>访问被拒绝</h1>
          <p>抱歉，您没有权限访问此页面。</p>
          <div class="error-suggestions">
            <ul>
              <li>您可能没有足够的权限访问此资源</li>
              <li>请联系管理员申请相应权限</li>
              <li>或者您可以切换到有权限的账户</li>
            </ul>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="error-actions">
          <el-button type="primary" size="large" @click="goHome">
            <el-icon style="margin-right: 8px;"><House /></el-icon>
            返回首页
          </el-button>
          <el-button size="large" @click="goBack">
            <el-icon style="margin-right: 8px;"><ArrowLeft /></el-icon>
            返回上页
          </el-button>
          <el-button type="warning" size="large" @click="logout">
            <el-icon style="margin-right: 8px;"><SwitchButton /></el-icon>
            切换账户
          </el-button>
        </div>

        <!-- 帮助链接 -->
        <div class="help-links">
          <el-link type="primary" href="/help">帮助中心</el-link>
          <el-divider direction="vertical" />
          <el-link type="primary" href="/contact">联系管理员</el-link>
        </div>
      </div>

      <!-- 装饰元素 -->
      <div class="decoration">
        <div class="circle circle-1"></div>
        <div class="circle circle-2"></div>
        <div class="circle circle-3"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Lock, House, ArrowLeft, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()

// 返回首页
const goHome = () => {
  router.push('/')
}

// 返回上一页
const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    goHome()
  }
}

// 退出登录
const logout = () => {
  // 清除本地存储的用户信息
  localStorage.removeItem('userToken')
  localStorage.removeItem('userInfo')
  
  // 跳转到登录页
  router.push('/login')
}
</script>

<style scoped>
.error-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.error-container {
  background: white;
  border-radius: 20px;
  padding: 60px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
  text-align: center;
  max-width: 600px;
  width: 100%;
  position: relative;
  z-index: 2;
}

.error-icon {
  margin-bottom: 40px;
}

.icon-wrapper {
  margin-bottom: 20px;
  animation: shake 2s infinite;
}

.error-code {
  font-size: 72px;
  font-weight: 700;
  color: #f56c6c;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  margin-top: 10px;
}

.error-info {
  margin-bottom: 40px;
}

.error-info h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 16px;
  font-weight: 600;
}

.error-info p {
  font-size: 18px;
  color: #666;
  margin-bottom: 24px;
  line-height: 1.6;
}

.error-suggestions {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  margin: 24px 0;
}

.error-suggestions ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.error-suggestions li {
  padding: 8px 0;
  color: #666;
  font-size: 14px;
  position: relative;
  padding-left: 20px;
}

.error-suggestions li::before {
  content: '•';
  color: #f56c6c;
  position: absolute;
  left: 0;
  font-weight: bold;
}

.error-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.help-links {
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.decoration {
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
  width: 100px;
  height: 100px;
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
  width: 80px;
  height: 80px;
  top: 30%;
  right: 30%;
  animation-delay: 4s;
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: translateX(-10px);
  }
  20%, 40%, 60%, 80% {
    transform: translateX(10px);
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .error-container {
    padding: 40px 20px;
    border-radius: 16px;
  }

  .error-code {
    font-size: 56px;
  }

  .error-info h1 {
    font-size: 24px;
  }

  .error-info p {
    font-size: 16px;
  }

  .error-actions {
    flex-direction: column;
    align-items: center;
  }

  .error-actions .el-button {
    width: 200px;
  }
}

@media (max-width: 480px) {
  .error-page {
    padding: 10px;
  }

  .error-container {
    padding: 30px 15px;
  }

  .error-code {
    font-size: 48px;
  }

  .error-info h1 {
    font-size: 20px;
  }

  .icon-wrapper :deep(.el-icon) {
    font-size: 80px !important;
  }
}
</style>