<template>
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
                <el-breadcrumb-item>关于我们</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <h1>关于我们</h1>
            <p>致力于为教育行业提供最专业、最智能的数字化解决方案</p>
          </div>
        </div>
      </section>

      <!-- 内容区域 -->
      <section class="content-section">
        <div class="container">
          <div class="content-card">
            <h2>公司简介</h2>
            <p>智慧校园管理平台是一家专注于教育科技的创新企业，致力于为学校、教师、学生和家长提供全面的数字化教育解决方案。</p>
            
            <div class="company-info">
              <div class="info-grid">
                <div class="info-item">
                  <h3>我们的愿景</h3>
                  <p>成为全球领先的智慧教育平台，让每个人都能享受到优质的教育资源和服务。</p>
                </div>
                <div class="info-item">
                  <h3>我们的使命</h3>
                  <p>通过技术创新，打破传统教育的时空限制，构建更加开放、互动、个性化的学习环境。</p>
                </div>
                <div class="info-item">
                  <h3>核心价值观</h3>
                  <p>以用户为中心，追求卓越品质，持续创新发展，共创教育未来。</p>
                </div>
                <div class="info-item">
                  <h3>发展历程</h3>
                  <p>自2020年成立以来，我们已服务超过500所学校，帮助10000+用户实现智慧教育转型。</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA区域 -->
      <section class="cta-section">
        <div class="container">
          <div class="cta-content">
            <h2>加入我们</h2>
            <p>让我们一起推动教育行业的数字化变革</p>
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
@import '@/styles/about.css';
</style>