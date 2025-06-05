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
                <el-breadcrumb-item>功能介绍</el-breadcrumb-item>
              </el-breadcrumb>
            </div>
            <h1>功能介绍</h1>
            <p>全面了解智慧校园管理平台的强大功能特性</p>
          </div>
        </div>
      </section>

      <!-- 功能详情 -->
      <section class="features-detail-section">
        <div class="container">
          <!-- 学生功能 -->
          <div class="feature-category">
            <div class="category-header">
              <div class="category-icon student">
                <el-icon :size="48"><UserFilled /></el-icon>
              </div>
              <div class="category-info">
                <h2>学生端功能</h2>
                <p>为学生提供完整的学习管理和生活服务</p>
              </div>
            </div>
            <div class="features-grid">
              <div class="feature-item">
                <el-icon class="feature-icon" color="#667eea"><Document /></el-icon>
                <h3>课程学习管理</h3>
                <p>在线观看课程视频、下载学习资料、参与课堂讨论，全方位支持线上线下混合学习模式。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#667eea"><EditPen /></el-icon>
                <h3>作业提交系统</h3>
                <p>支持多种格式的作业上传，实时查看提交状态，自动提醒作业截止时间。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#667eea"><TrendCharts /></el-icon>
                <h3>成绩分析统计</h3>
                <p>可视化成绩报告，学习进度跟踪，智能分析学习薄弱环节，提供个性化学习建议。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#667eea"><Calendar /></el-icon>
                <h3>智能课程表</h3>
                <p>个性化课程安排，支持课程提醒、教室导航、课程变更通知等智能服务。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#667eea"><Reading /></el-icon>
                <h3>数字图书馆</h3>
                <p>海量电子图书资源，在线阅读，智能检索，个人书架管理，支持离线下载。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#667eea"><Select /></el-icon>
                <h3>在线选课系统</h3>
                <p>便捷的选课流程，课程容量实时显示，选课冲突检测，退选改选灵活操作。</p>
              </div>
            </div>
          </div>

          <!-- 教师功能 -->
          <div class="feature-category">
            <div class="category-header">
              <div class="category-icon teacher">
                <el-icon :size="48"><Reading /></el-icon>
              </div>
              <div class="category-info">
                <h2>教师端功能</h2>
                <p>高效的教学管理工具，提升教学质量</p>
              </div>
            </div>
            <div class="features-grid">
              <div class="feature-item">
                <el-icon class="feature-icon" color="#f093fb"><UserFilled /></el-icon>
                <h3>学生信息管理</h3>
                <p>完整的学生档案管理，学习轨迹跟踪，个性化标签分类，便于因材施教。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#f093fb"><Document /></el-icon>
                <h3>课程内容发布</h3>
                <p>多媒体课件上传，课程大纲编辑，学习资料分享，支持实时直播授课。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#f093fb"><Check /></el-icon>
                <h3>智能作业批改</h3>
                <p>AI辅助批改，快速反馈，批注工具丰富，支持语音评价和视频讲解。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#f093fb"><TrendCharts /></el-icon>
                <h3>教学数据分析</h3>
                <p>课堂参与度统计，学习效果评估，教学质量报告，数据驱动教学优化。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#f093fb"><Clock /></el-icon>
                <h3>课程安排管理</h3>
                <p>灵活的排课系统，教室资源调配，课程变更通知，考勤管理一体化。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#f093fb"><Trophy /></el-icon>
                <h3>考试成绩管理</h3>
                <p>在线组卷出题，自动阅卷评分，成绩统计分析，多维度评价体系。</p>
              </div>
            </div>
          </div>

          <!-- 家长功能 -->
          <div class="feature-category">
            <div class="category-header">
              <div class="category-icon parent">
                <el-icon :size="48"><Management /></el-icon>
              </div>
              <div class="category-info">
                <h2>家长端功能</h2>
                <p>全方位关注孩子成长，促进家校协作</p>
              </div>
            </div>
            <div class="features-grid">
              <div class="feature-item">
                <el-icon class="feature-icon" color="#4facfe"><View /></el-icon>
                <h3>子女学习监控</h3>
                <p>实时了解孩子学习状态，作业完成情况，在线时长统计，学习习惯分析。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#4facfe"><TrendCharts /></el-icon>
                <h3>成绩趋势分析</h3>
                <p>详细的成绩变化趋势，学科优势劣势分析，与同龄人对比，成长轨迹记录。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#4facfe"><ChatLineRound /></el-icon>
                <h3>家校即时沟通</h3>
                <p>与老师实时交流，班级群聊，学校通知推送，预约家长会便捷服务。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#4facfe"><Money /></el-icon>
                <h3>便捷缴费服务</h3>
                <p>在线缴纳学费、活动费用，电子发票开具，缴费记录查询，多种支付方式。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#4facfe"><Trophy /></el-icon>
                <h3>校园活动参与</h3>
                <p>活动报名参与，精彩瞬间分享，获奖记录查看，培养孩子全面发展。</p>
              </div>
              <div class="feature-item">
                <el-icon class="feature-icon" color="#4facfe"><Calendar /></el-icon>
                <h3>出勤情况查看</h3>
                <p>实时考勤通知，请假申请审批，迟到早退统计，保障孩子安全到校。</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- CTA区域 -->
      <section class="cta-section">
        <div class="container">
          <div class="cta-content">
            <h2>体验完整功能</h2>
            <p>立即注册，开启智慧校园管理新体验</p>
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
import {
  School,
  User,
  UserFilled,
  Reading,
  Management,
  Document,
  EditPen,
  TrendCharts,
  Calendar,
  Select,
  Check,
  Clock,
  Trophy,
  View,
  ChatLineRound,
  Money,
  Right
} from '@element-plus/icons-vue'

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