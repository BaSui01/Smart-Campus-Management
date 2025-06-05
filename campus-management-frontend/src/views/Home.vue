<template>
  <div class="home-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-brand" @click="scrollToTop">
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
      <!-- 英雄区域 -->
      <section class="hero-section">
        <!-- 背景动画 -->
        <div class="hero-background">
          <div class="gradient-overlay"></div>
          <div class="floating-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
            <div class="shape shape-4"></div>
            <div class="shape shape-5"></div>
          </div>
          <div class="grid-pattern"></div>
        </div>

        <div class="hero-container">
          <div class="hero-content">
            <div class="hero-badge">
              <el-icon><Star /></el-icon>
              <span>新一代智慧校园解决方案</span>
            </div>
            
            <h1 class="hero-title">
              <span class="title-line">
                <span class="gradient-text animate-text">智慧校园</span>
              </span>
              <span class="title-line">
                <span class="animate-text" style="animation-delay: 0.2s">连接学习</span>
                <span class="separator">·</span>
                <span class="animate-text" style="animation-delay: 0.4s">成就未来</span>
              </span>
            </h1>
            
            <p class="hero-subtitle">
              融合现代教育理念与前沿技术，为学生、教师、家长<br>
              打造全方位、智能化的校园生活与学习体验
            </p>
            
            <div class="hero-stats">
              <div class="stat-item">
                <div class="stat-number">10000+</div>
                <div class="stat-label">活跃用户</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">500+</div>
                <div class="stat-label">合作学校</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">99.9%</div>
                <div class="stat-label">系统稳定性</div>
              </div>
            </div>
            
            <div class="hero-actions">
              <el-button type="primary" size="large" class="cta-button primary" @click="goToLogin">
                <el-icon><Right /></el-icon>
                立即体验
              </el-button>
              <el-button size="large" class="cta-button secondary" @click="scrollToFeatures">
                <el-icon><VideoPlay /></el-icon>
                观看演示
              </el-button>
            </div>

            <div class="hero-features">
              <div class="feature-tag">
                <el-icon><Check /></el-icon>
                <span>免费试用</span>
              </div>
              <div class="feature-tag">
                <el-icon><Check /></el-icon>
                <span>24/7 技术支持</span>
              </div>
              <div class="feature-tag">
                <el-icon><Check /></el-icon>
                <span>数据安全保障</span>
              </div>
            </div>
          </div>
          
          <div class="hero-visual">
            <div class="visual-container">
              <div class="main-visual">
                <div class="visual-card dashboard">
                  <div class="card-header">
                    <div class="card-title">学习仪表盘</div>
                    <div class="card-status online"></div>
                  </div>
                  <div class="card-content">
                    <div class="progress-item">
                      <span>数学</span>
                      <div class="progress-bar">
                        <div class="progress-fill" style="width: 85%"></div>
                      </div>
                    </div>
                    <div class="progress-item">
                      <span>英语</span>
                      <div class="progress-bar">
                        <div class="progress-fill" style="width: 92%"></div>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="visual-card notification">
                  <el-icon class="notification-icon"><Bell /></el-icon>
                  <div class="notification-content">
                    <div class="notification-title">作业提醒</div>
                    <div class="notification-text">物理作业将于明天截止</div>
                  </div>
                </div>
                
                <div class="visual-card stats">
                  <div class="stats-grid">
                    <div class="stat-box">
                      <el-icon color="#667eea"><TrendCharts /></el-icon>
                      <span>成绩分析</span>
                    </div>
                    <div class="stat-box">
                      <el-icon color="#f56c6c"><Calendar /></el-icon>
                      <span>课程表</span>
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="floating-elements">
                <div class="floating-icon" style="top: 10%; left: 10%; animation-delay: 0s">
                  <el-icon><Reading /></el-icon>
                </div>
                <div class="floating-icon" style="top: 30%; right: 15%; animation-delay: 1s">
                  <el-icon><EditPen /></el-icon>
                </div>
                <div class="floating-icon" style="bottom: 20%; left: 20%; animation-delay: 2s">
                  <el-icon><Trophy /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 功能特性 -->
      <section id="features" class="features-section">
        <div class="container">
          <div class="section-header">
            <div class="section-badge">
            <div class="section-badge">
              <el-icon><Magic /></el-icon>
              <span>核心功能</span>
            </div>
            <h2>为每个角色量身定制的功能模块</h2>
            <p>深度理解教育场景，提供精准匹配的功能服务</p>
          </div>
          
          <div class="features-grid">
            <!-- 学生功能 -->
            <div class="feature-card student-card">
              <div class="card-background">
                <div class="bg-pattern"></div>
              </div>
              <div class="feature-header">
                <div class="feature-icon student">
                  <el-icon :size="40"><UserFilled /></el-icon>
                  <div class="icon-glow"></div>
                </div>
                <div class="feature-title">
                  <h3>学生端</h3>
                  <p>专注学习成长的智能助手</p>
                </div>
              </div>
              <ul class="feature-list">
                <li>
                  <el-icon><Document /></el-icon>
                  <span>课程学习与管理</span>
                </li>
                <li>
                  <el-icon><EditPen /></el-icon>
                  <span>在线作业提交</span>
                </li>
                <li>
                  <el-icon><TrendCharts /></el-icon>
                  <span>成绩查询分析</span>
                </li>
                <li>
                  <el-icon><Calendar /></el-icon>
                  <span>智能课程表</span>
                </li>
                <li>
                  <el-icon><Reading /></el-icon>
                  <span>数字图书馆</span>
                </li>
                <li>
                  <el-icon><Select /></el-icon>
                  <span>在线选课系统</span>
                </li>
              </ul>
              <div class="card-footer">
                <el-button type="primary" text @click="goToLogin">
                  体验学生端
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>

            <!-- 教师功能 -->
            <div class="feature-card teacher-card">
              <div class="card-background">
                <div class="bg-pattern"></div>
              </div>
              <div class="feature-header">
                <div class="feature-icon teacher">
                  <el-icon :size="40"><Reading /></el-icon>
                  <div class="icon-glow"></div>
                </div>
                <div class="feature-title">
                  <h3>教师端</h3>
                  <p>高效教学管理的智慧平台</p>
                </div>
              </div>
              <ul class="feature-list">
                <li>
                  <el-icon><UserFilled /></el-icon>
                  <span>学生信息管理</span>
                </li>
                <li>
                  <el-icon><Document /></el-icon>
                  <span>课程内容发布</span>
                </li>
                <li>
                  <el-icon><Check /></el-icon>
                  <span>智能作业批改</span>
                </li>
                <li>
                  <el-icon><DataAnalysis /></el-icon>
                  <span>教学数据分析</span>
                </li>
                <li>
                  <el-icon><Clock /></el-icon>
                  <span>课程安排管理</span>
                </li>
                <li>
                  <el-icon><Medal /></el-icon>
                  <span>考试成绩管理</span>
                </li>
              </ul>
              <div class="card-footer">
                <el-button type="primary" text @click="goToLogin">
                  体验教师端
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>

            <!-- 家长功能 -->
            <div class="feature-card parent-card">
              <div class="card-background">
                <div class="bg-pattern"></div>
              </div>
              <div class="feature-header">
                <div class="feature-icon parent">
                  <el-icon :size="40"><Management /></el-icon>
                  <div class="icon-glow"></div>
                </div>
                <div class="feature-title">
                  <h3>家长端</h3>
                  <p>全方位关注孩子成长轨迹</p>
                </div>
              </div>
              <ul class="feature-list">
                <li>
                  <el-icon><View /></el-icon>
                  <span>子女学习监控</span>
                </li>
                <li>
                  <el-icon><TrendCharts /></el-icon>
                  <span>成绩趋势分析</span>
                </li>
                <li>
                  <el-icon><ChatLineRound /></el-icon>
                  <span>家校即时沟通</span>
                </li>
                <li>
                  <el-icon><Money /></el-icon>
                  <span>便捷缴费服务</span>
                </li>
                <li>
                  <el-icon><Trophy /></el-icon>
                  <span>校园活动参与</span>
                </li>
                <li>
                  <el-icon><Calendar /></el-icon>
                  <span>出勤情况查看</span>
                </li>
              </ul>
              <div class="card-footer">
                <el-button type="primary" text @click="goToLogin">
                  体验家长端
                  <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 技术优势 -->
      <section class="tech-section">
        <div class="container">
          <div class="section-header">
            <div class="section-badge">
              <el-icon><Cpu /></el-icon>
              <span>技术优势</span>
            </div>
            <h2>先进技术栈，稳定可靠的系统架构</h2>
            <p>采用业界领先的技术方案，确保系统高效、安全、稳定运行</p>
          </div>
          
          <div class="tech-grid">
            <div class="tech-card">
              <div class="tech-icon">
                <el-icon :size="48" color="#67c23a"><Lightning /></el-icon>
              </div>
              <h4>极速性能</h4>
              <p>基于 Vue3 + Vite 构建<br>页面加载速度提升 300%</p>
              <div class="tech-stats">
                <div class="stat">
                  <span class="number">< 1s</span>
                  <span class="label">首屏加载</span>
                </div>
              </div>
            </div>

            <div class="tech-card">
              <div class="tech-icon">
                <el-icon :size="48" color="#409eff"><Lock /></el-icon>
              </div>
              <h4>安全防护</h4>
              <p>多层安全防护体系<br>保障用户数据绝对安全</p>
              <div class="tech-stats">
                <div class="stat">
                  <span class="number">SSL</span>
                  <span class="label">数据加密</span>
                </div>
              </div>
            </div>

            <div class="tech-card">
              <div class="tech-icon">
                <el-icon :size="48" color="#e6a23c"><Cellphone /></el-icon>
              </div>
              <h4>全端适配</h4>
              <p>响应式设计理念<br>完美适配各种终端设备</p>
              <div class="tech-stats">
                <div class="stat">
                  <span class="number">100%</span>
                  <span class="label">兼容性</span>
                </div>
              </div>
            </div>

            <div class="tech-card">
              <div class="tech-icon">
                <el-icon :size="48" color="#f56c6c"><Star /></el-icon>
              </div>
              <h4>用户体验</h4>
              <p>精心设计的交互界面<br>带来直观舒适的使用感受</p>
              <div class="tech-stats">
                <div class="stat">
                  <span class="number">4.9★</span>
                  <span class="label">用户评分</span>
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
            <div class="cta-text">
              <h2>准备好开启智慧校园之旅了吗？</h2>
              <p>加入我们，体验新一代教育管理平台带来的便捷与高效</p>
            </div>
            <div class="cta-actions">
              <el-button type="primary" size="large" class="cta-button" @click="goToLogin">
                <el-icon><Right /></el-icon>
                立即开始
              </el-button>
            </div>
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
              <div class="social-links">
                <el-button circle size="small"><el-icon><Platform /></el-icon></el-button>
                <el-button circle size="small"><el-icon><Share /></el-icon></el-button>
                <el-button circle size="small"><el-icon><Message /></el-icon></el-button>
              </div>
            </div>
            
            <div class="footer-links">
              <div class="link-group">
                <h4>产品服务</h4>
                <a href="#">功能介绍</a>
                <a href="#">价格方案</a>
                <a href="#">案例展示</a>
                <a href="#">产品更新</a>
              </div>
              <div class="link-group">
                <h4>技术支持</h4>
                <a href="#">帮助中心</a>
                <a href="#">技术文档</a>
                <a href="#">常见问题</a>
                <a href="#">联系支持</a>
              </div>
              <div class="link-group">
                <h4>关于我们</h4>
                <a href="#">公司介绍</a>
                <a href="#">团队成员</a>
                <a href="#">招贤纳士</a>
                <a href="#">联系我们</a>
              </div>
            </div>
          </div>
          
          <div class="footer-bottom">
            <div class="copyright">
              <p>&copy; 2024 智慧校园管理平台. 版权所有</p>
            </div>
            <div class="footer-nav">
              <a href="#">隐私政策</a>
              <a href="#">服务条款</a>
              <a href="#">法律声明</a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { onMounted } from 'vue'
import {
  School,
  User,
  Star,
  Right,
  VideoPlay,
  Check,
  Bell,
  TrendCharts,
  Calendar,
  Reading,
  EditPen,
  Trophy,
  Magic,
  UserFilled,
  Management,
  Document,
  Select,
  ArrowRight,
  DataAnalysis,
  Clock,
  Medal,
  View,
  ChatLineRound,
  Money,
  Cpu,
  Lightning,
  Lock,
  Cellphone,
  Platform,
  Share,
  Message
} from '@element-plus/icons-vue'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const scrollToFeatures = () => {
  document.getElementById('features')?.scrollIntoView({ 
    behavior: 'smooth' 
  })
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  // 添加滚动监听，实现导航栏背景变化
  const navbar = document.querySelector('.navbar')
  const handleScroll = () => {
    if (window.scrollY > 50) {
      navbar?.classList.add('scrolled')
    } else {
      navbar?.classList.remove('scrolled')
    }
  }
  
  window.addEventListener('scroll', handleScroll)
  
  // 添加数字动画
  const animateNumbers = () => {
    const numbers = document.querySelectorAll('.stat-number')
    numbers.forEach(num => {
      const target = parseInt(num.textContent.replace(/[^\d]/g, ''))
      let current = 0
      const increment = target / 100
      const timer = setInterval(() => {
        current += increment
        if (current >= target) {
          current = target
          clearInterval(timer)
        }
        if (num.textContent.includes('%')) {
          num.textContent = Math.round(current) + '%'
        } else if (num.textContent.includes('+')) {
          num.textContent = Math.round(current) + '+'
        } else {
          num.textContent = Math.round(current)
        }
      }, 20)
    })
  }
  
  // 延迟执行数字动画
  setTimeout(animateNumbers, 1000)
})
</script>

<style scoped>
/* 全局样式 */
.home-page {
  min-height: 100vh;
  background: #fff;
  overflow-x: hidden;
}

/* 导航栏 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 1000;
  transition: all 0.3s ease;
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.95);
  border-bottom: 1px solid #eee;
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
  transition: transform 0.3s ease;
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
  transition: color 0.3s ease;
}

.navbar.scrolled .brand-text {
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

/* 主要内容 */
.main-content {
  padding-top: 70px;
}

/* 英雄区域 */
.hero-section {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  opacity: 0.9;
}

.floating-shapes {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  top: 30%;
  right: 30%;
  animation-delay: 4s;
}

.shape-4 {
  width: 80px;
  height: 80px;
  bottom: 20%;
  left: 30%;
  animation-delay: 1s;
}

.shape-5 {
  width: 120px;
  height: 120px;
  bottom: 40%;
  right: 20%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.grid-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    linear-gradient(rgba(255, 255, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.1) 1px, transparent 1px);
  background-size: 50px 50px;
  animation: gridMove 20s linear infinite;
}

@keyframes gridMove {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(50px, 50px);
  }
}

.hero-container {
  position: relative;
  z-index: 2;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
  color: white;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 30px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: fadeInUp 1s ease-out;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 30px;
}

.title-line {
  display: block;
  overflow: hidden;
}

.animate-text {
  display: inline-block;
  animation: slideInUp 1s ease-out;
}

.gradient-text {
  background: linear-gradient(45deg, #fff, #f0f8ff, #e6f3ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.separator {
  margin: 0 15px;
  opacity: 0.7;
}

@keyframes slideInUp {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeInUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.hero-subtitle {
  font-size: 20px;
  line-height: 1.6;
  opacity: 0.9;
  margin-bottom: 40px;
  animation: fadeInUp 1s ease-out 0.3s both;
}

.hero-stats {
  display: flex;
  gap: 40px;
  margin-bottom: 40px;
  animation: fadeInUp 1s ease-out 0.4s both;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  display: block;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.8;
}

.hero-actions {
  display: flex;
  gap: 20px;
  margin-bottom: 40px;
  animation: fadeInUp 1s ease-out 0.5s both;
}

.cta-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 32px;
  border-radius: 30px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.cta-button.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  color: white;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.cta-button.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.5);
}

.cta-button.secondary {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.cta-button.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-3px);
}

.hero-features {
  display: flex;
  gap: 20px;
  animation: fadeInUp 1s ease-out 0.6s both;
}

.feature-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  opacity: 0.9;
}

/* 视觉区域 */
.hero-visual {
  position: relative;
  animation: fadeInRight 1s ease-out 0.7s both;
}

@keyframes fadeInRight {
  from {
    transform: translateX(50px);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.visual-container {
  position: relative;
  height: 500px;
}

.main-visual {
  position: relative;
  width: 100%;
  height: 100%;
}

.visual-card {
  position: absolute;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #333;
  animation: cardFloat 3s ease-in-out infinite;
}

.visual-card.dashboard {
  top: 0;
  left: 0;
  right: 20px;
  height: 200px;
  animation-delay: 0s;
}

.visual-card.notification {
  top: 220px;
  left: 40px;
  right: 60px;
  height: 80px;
  animation-delay: 1s;
  display: flex;
  align-items: center;
  gap: 15px;
}

.visual-card.stats {
  bottom: 0;
  right: 0;
  left: 20px;
  height: 120px;
  animation-delay: 2s;
}

@keyframes cardFloat {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.card-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67c23a;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.progress-item span {
  width: 40px;
  font-size: 14px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
  animation: progressFill 2s ease-out;
}

@keyframes progressFill {
  from {
    width: 0 !important;
  }
}

.notification-icon {
  color: #f56c6c;
  font-size: 24px;
}

.notification-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.notification-text {
  font-size: 14px;
  color: #666;
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  height: 100%;
}

.stat-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(102, 126, 234, 0.1);
  border-radius: 12px;
  padding: 10px;
  text-align: center;
}

.stat-box span {
  font-size: 12px;
  margin-top: 5px;
  font-weight: 500;
}

.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.floating-icon {
  position: absolute;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  animation: iconFloat 4s ease-in-out infinite;
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-15px) rotate(180deg);
  }
}

/* 功能特性区域 */
.features-section {
  padding: 120px 0;
  background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
  position: relative;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 80px;
}

.section-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 20px;
  color: #333;
  line-height: 1.3;
}

.section-header p {
  font-size: 20px;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 40px;
}

.feature-card {
  background: white;
  border-radius: 24px;
  padding: 40px;
  position: relative;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.card-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.05;
  transition: opacity 0.3s ease;
}

.feature-card:hover .card-background {
  opacity: 0.1;
}

.bg-pattern {
  width: 100%;
  height: 100%;
  background-image: radial-gradient(circle, currentColor 1px, transparent 1px);
  background-size: 20px 20px;
}

.student-card .bg-pattern {
  color: #667eea;
}

.teacher-card .bg-pattern {
  color: #f093fb;
}

.parent-card .bg-pattern {
  color: #4facfe;
}

.feature-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 30px;
}

.feature-icon {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-icon.student {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.feature-icon.teacher {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  color: white;
}

.feature-icon.parent {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  color: white;
}

.icon-glow {
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 22px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover .icon-glow {
  opacity: 1;
  animation: glow 2s ease-in-out infinite alternate;
}

.student-card:hover .icon-glow {
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
}

.teacher-card:hover .icon-glow {
  box-shadow: 0 0 20px rgba(240, 147, 251, 0.5);
}

.parent-card:hover .icon-glow {
  box-shadow: 0 0 20px rgba(79, 172, 254, 0.5);
}

@keyframes glow {
  from {
    box-shadow: 0 0 10px currentColor;
  }
  to {
    box-shadow: 0 0 30px currentColor;
  }
}

.feature-title h3 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.feature-title p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

.feature-list {
  list-style: none;
  padding: 0;
  margin: 0 0 30px 0;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  color: #555;
  font-size: 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: all 0.3s ease;
}

.feature-list li:hover {
  background: #f9f9f9;
  padding-left: 10px;
  border-radius: 8px;
}

.feature-list li:last-child {
  border-bottom: none;
}

.feature-list .el-icon {
  color: #667eea;
  flex-shrink: 0;
}

.card-footer {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
  margin-top: 20px;
}

.card-footer .el-button {
  font-weight: 500;
  transition: all 0.3s ease;
}

/* 技术优势区域 */
.tech-section {
  padding: 120px 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.tech-card {
  background: white;
  padding: 40px 30px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.tech-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.12);
}

.tech-icon {
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.tech-card:hover .tech-icon {
  transform: scale(1.1);
}

.tech-card h4 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}

.tech-card p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.tech-stats {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat .number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.stat .label {
  font-size: 14px;
  color: #666;
}

/* CTA区域 */
.cta-section {
  padding: 80px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
}

.cta-content {
  max-width: 600px;
  margin: 0 auto;
}

.cta-text h2 {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
}

.cta-text p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.cta-actions .cta-button {
  background: white;
  color: #667eea;
  border: none;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.cta-actions .cta-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.3);
}

/* 页脚 */
.footer {
  background: #2c3e50;
  color: white;
  padding: 60px 0 20px;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
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
  margin-bottom: 30px;
}

.social-links {
  display: flex;
  gap: 10px;
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
  .hero-container {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-stats {
    justify-content: center;
  }

  .hero-actions {
    justify-content: center;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .tech-grid {
    grid-template-columns: repeat(2, 1fr);
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

@media (max-width: 480px) {
  .nav-container {
    padding: 0 15px;
  }

  .brand-text {
    font-size: 16px;
  }

  .hero-title {
    font-size: 28px;
  }

  .hero-subtitle {
    font-size: 16px;
  }

  .section-header h2 {
    font-size: 28px;
  }

  .tech-grid {
    grid-template-columns: 1fr;
  }

  .footer-nav {
    flex-direction: column;
    gap: 15px;
  }

  .cta-text h2 {
    font-size: 28px;
  }

  .hero-actions {
    flex-direction: column;
    align-items: center;
  }

  .hero-stats {
    flex-direction: column;
    gap: 20px;
  }
}
</style>