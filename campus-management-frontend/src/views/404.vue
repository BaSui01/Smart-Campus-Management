<template>
  <div class="error-page">
    <div class="error-container">
      <div class="error-content">
        <!-- 错误图标 -->
        <div class="error-icon">
          <div class="icon-wrapper">
            <el-icon :size="120" color="#e6a23c">
              <WarningFilled />
            </el-icon>
          </div>
          <div class="error-code">404</div>
        </div>

        <!-- 错误信息 -->
        <div class="error-info">
          <h1>页面未找到</h1>
          <p>抱歉，您访问的页面不存在或已被移除。</p>
          <div class="error-suggestions">
            <ul>
              <li>请检查网址是否正确</li>
              <li>该页面可能已被移动或删除</li>
              <li>您可以返回首页继续浏览</li>
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
        </div>

        <!-- 帮助链接 -->
        <div class="help-links">
          <el-link type="primary" href="/help">帮助中心</el-link>
          <el-divider direction="vertical" />
          <el-link type="primary" href="/contact">联系客服</el-link>
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
import { WarningFilled, House, ArrowLeft } from '@element-plus/icons-vue'

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
</script>

<style scoped>
@import '@/styles/error.css';
</style>