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

<style >
@import '@/styles/error.css';
</style>