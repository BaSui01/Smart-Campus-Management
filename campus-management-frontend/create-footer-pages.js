const fs = require('fs')
const path = require('path')

// 页面配置
const pages = [
  { path: 'about/About.vue', title: '公司介绍', name: 'About', breadcrumb: '关于我们 / 公司介绍' },
  { path: 'about/Team.vue', title: '团队成员', name: 'Team', breadcrumb: '关于我们 / 团队成员' },
  { path: 'about/Careers.vue', title: '招贤纳士', name: 'Careers', breadcrumb: '关于我们 / 招贤纳士' },
  { path: 'about/Contact.vue', title: '联系我们', name: 'Contact', breadcrumb: '关于我们 / 联系我们' },
  { path: 'product/Pricing.vue', title: '价格方案', name: 'Pricing', breadcrumb: '产品服务 / 价格方案' },
  { path: 'product/Cases.vue', title: '案例展示', name: 'Cases', breadcrumb: '产品服务 / 案例展示' },
  { path: 'product/Updates.vue', title: '产品更新', name: 'Updates', breadcrumb: '产品服务 / 产品更新' },
  { path: 'support/Help.vue', title: '帮助中心', name: 'Help', breadcrumb: '技术支持 / 帮助中心' },
  { path: 'support/Docs.vue', title: '技术文档', name: 'Docs', breadcrumb: '技术支持 / 技术文档' },
  { path: 'support/FAQ.vue', title: '常见问题', name: 'FAQ', breadcrumb: '技术支持 / 常见问题' },
  { path: 'support/Support.vue', title: '联系支持', name: 'Support', breadcrumb: '技术支持 / 联系支持' },
  { path: 'legal/Privacy.vue', title: '隐私政策', name: 'Privacy', breadcrumb: '法律 / 隐私政策' },
  { path: 'legal/Terms.vue', title: '服务条款', name: 'Terms', breadcrumb: '法律 / 服务条款' },
  { path: 'legal/Legal.vue', title: '法律声明', name: 'Legal', breadcrumb: '法律 / 法律声明' }
]

// 通用页面模板
const createPageTemplate = (config) => `<template>
  <div class="page-container">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-brand" @click="goHome">
          <div class="logo-wrapper">
            <el-icon :size="32" class="logo-icon"><School /></el-icon>
            <div class="logo-ripple"></div>
          </div>
          <span class="brand-text">智慧校园管理平台</span>
        </div>
        <div class="nav-actions">
          <el-button type="primary" class="login-btn" @click="goToLogin">
            <el-icon><User /></el-icon>
            立即登录
          </el-button>
        </div>
      </div>
    </nav>

    <!-- 主要内容 -->
    <main class="main-content">
      <!-- 标题区域 -->
      <section class="hero-section">
        <div class="hero-background">
          <div class="gradient-overlay"></div>
        </div>
        <div class="container">
          <div class="page-header">
            <div class="breadcrumb">
              <el-breadcrumb separator="/">
                <el-breadcrumb-item @click="goHome">首页</el-breadcrumb-item>
                <el-breadcrumb-item>${config.breadcrumb}</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <h1>${config.title}</h1>
            <p>智慧校园管理平台 - ${config.title}</p>
          </div>
        </div>
      </section>

      <!-- 内容区域 -->
      <section class="content-section">
        <div class="container">
          <div class="content-card">
            <h2>${config.title}</h2>
            <p>此页面正在建设中，敬请期待...</p>
            <div class="placeholder-content">
              <el-empty description="内容即将上线" />
            </div>
          </div>
        </div>
      </section>

      <!-- CTA区域 -->
      <section class="cta-section">
        <div class="container">
          <div class="cta-content">
            <h2>了解更多信息</h2>
            <p>如有任何问题，欢迎联系我们的客服团队</p>
            <el-button type="primary" size="large" @click="goToLogin">
              <el-icon><Right /></el-icon>
              立即体验
            </el-button>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-main">
            <div class="footer-brand">
              <div class="brand-logo">
                <el-icon :size="32" color="#667eea"><School /></el-icon>
                <span>智慧校园管理平台</span>
              </div>
              <p class="brand-desc">
                致力于为教育行业提供最专业、最智能的数字化解决方案，
                让教育更简单，让学习更高效。
              </p>
            </div>
            
            <div class="footer-links">
              <div class="link-group">
                <h4>产品服务</h4>
                <router-link to="/features">功能介绍</router-link>
                <router-link to="/pricing">价格方案</router-link>
                <router-link to="/cases">案例展示</router-link>
                <router-link to="/updates">产品更新</router-link>
              </div>
              <div class="link-group">
                <h4>技术支持</h4>
                <router-link to="/help">帮助中心</router-link>
                <router-link to="/docs">技术文档</router-link>
                <router-link to="/faq">常见问题</router-link>
                <router-link to="/support">联系支持</router-link>
              </div>
              <div class="link-group">
                <h4>关于我们</h4>
                <router-link to="/about">公司介绍</router-link>
                <router-link to="/team">团队成员</router-link>
                <router-link to="/careers">招贤纳士</router-link>
                <router-link to="/contact">联系我们</router-link>
              </div>
            </div>
          </div>
          
          <div class="footer-bottom">
            <div class="copyright">
              <p>&copy; 2024 智慧校园管理平台. 版权所有</p>
            </div>
            <div class="footer-nav">
              <router-link to="/privacy">隐私政策</router-link>
              <router-link to="/terms">服务条款</router-link>
              <router-link to="/legal">法律声明</router-link>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { School, User, Right } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* 基础布局 */
.page-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding-top: 70px;
}

/* 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid #eee;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.nav-brand:hover {
  transform: translateY(-2px);
}

.logo-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  color: #667eea;
}

.logo-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border: 2px solid #667eea;
  border-radius: 50%;
  opacity: 0;
  animation: ripple 2s infinite;
}

@keyframes ripple {
  0% {
    transform: translate(-50%, -50%) scale(0.8);
    opacity: 1;
  }
  100% {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 0;
  }
}

.brand-text {
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

/* 英雄区域 */
.hero-section {
  position: relative;
  padding: 60px 0;
  color: white;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 2;
}

.page-header {
  text-align: center;
}

.breadcrumb {
  margin-bottom: 20px;
}

.breadcrumb :deep(.el-breadcrumb__item) {
  color: rgba(255, 255, 255, 0.8);
}

.breadcrumb :deep(.el-breadcrumb__item:last-child) {
  color: white;
}

.page-header h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
}

.page-header p {
  font-size: 20px;
  opacity: 0.9;
}

/* 内容区域 */
.content-section {
  padding: 80px 0;
  background: #f8f9fa;
}

.content-card {
  background: white;
  padding: 60px;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.content-card h2 {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #333;
}

.content-card p {
  font-size: 18px;
  color: #666;
  margin-bottom: 40px;
}

.placeholder-content {
  padding: 40px 0;
}

/* CTA区域 */
.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.cta-content h2 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

.cta-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 32px;
}

.cta-content .el-button {
  background: white;
  color: #667eea;
  border: none;
  padding: 16px 32px;
  border-radius: 30px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cta-content .el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* 页脚 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 60px 0 20px;
}

.footer-main {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 60px;
  margin-bottom: 40px;
}

.footer-brand {
  max-width: 300px;
}

.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.brand-logo span {
  font-size: 20px;
  font-weight: 600;
}

.brand-desc {
  color: #bdc3c7;
  line-height: 1.6;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
}

.link-group h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  color: white;
}

.link-group a {
  display: block;
  color: #bdc3c7;
  text-decoration: none;
  padding: 5px 0;
  transition: color 0.3s ease;
}

.link-group a:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid #34495e;
  padding-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.copyright p {
  margin: 0;
  color: #95a5a6;
}

.footer-nav {
  display: flex;
  gap: 30px;
}

.footer-nav a {
  color: #95a5a6;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

.footer-nav a:hover {
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header h1 {
    font-size: 32px;
  }

  .content-card {
    padding: 40px 20px;
  }

  .footer-main {
    grid-template-columns: 1fr;
    gap: 40px;
  }

  .footer-links {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .footer-bottom {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
}
</style>
`

// 创建目录
const ensureDir = (dirPath) => {
  if (!fs.existsSync(dirPath)) {
    fs.mkdirSync(dirPath, { recursive: true })
  }
}

// 创建页面文件
pages.forEach(page => {
  const fullPath = path.join('src', 'views', page.path)
  const dirPath = path.dirname(fullPath)
  
  // 确保目录存在
  ensureDir(dirPath)
  
  // 创建文件
  const content = createPageTemplate(page)
  fs.writeFileSync(fullPath, content, 'utf8')
  
  console.log(`Created: ${fullPath}`)
})

console.log('All footer pages created successfully!')