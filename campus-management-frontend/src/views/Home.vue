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
              <el-icon><Star /></el-icon>
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
                  <el-icon><TrendCharts /></el-icon>
                  <span>教学数据分析</span>
                </li>
                <li>
                  <el-icon><Clock /></el-icon>
                  <span>课程安排管理</span>
                </li>
                <li>
                  <el-icon><Trophy /></el-icon>
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
  UserFilled,
  Management,
  Document,
  Select,
  ArrowRight,
  Clock,
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
<style>
/* 导入外部样式文件 */
@import '@/styles/Home.css';
</style>
