<template>
  <div class="modern-navbar" :class="{ 'scrolled': isScrolled }">
    <div class="navbar-container">
      <!-- 品牌Logo -->
      <div class="brand-section" @click="scrollToTop">
        <div class="logo-container">
          <div class="logo-circle">
            <el-icon class="logo-icon"><School /></el-icon>
          </div>
          <div class="brand-text">
            <h1 class="brand-name">智慧校园</h1>
            <span class="brand-slogan">Smart Campus</span>
          </div>
        </div>
      </div>

      <!-- 中央导航区域 -->
      <div class="nav-center" :class="{ 'mobile-open': isMobileMenuOpen }">
        <!-- 主导航 -->
        <nav class="main-nav">
          <a href="#home" @click="scrollToSection('home')" class="nav-item">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </a>
          
          <a href="#features" @click="scrollToSection('features')" class="nav-item">
            <el-icon><Star /></el-icon>
            <span>功能</span>
          </a>
          
          <el-dropdown class="nav-dropdown" trigger="hover" placement="bottom-start">
            <div class="nav-item dropdown-trigger">
              <el-icon><Grid /></el-icon>
              <span>产品</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item @click="navigateTo('/features')" class="dropdown-item">
                  <el-icon><Document /></el-icon>
                  <div class="item-content">
                    <span class="item-title">功能介绍</span>
                    <span class="item-desc">详细功能展示</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item @click="navigateTo('/cases')" class="dropdown-item">
                  <el-icon><Trophy /></el-icon>
                  <div class="item-content">
                    <span class="item-title">案例展示</span>
                    <span class="item-desc">成功案例分享</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item @click="navigateTo('/pricing')" class="dropdown-item">
                  <el-icon><Money /></el-icon>
                  <div class="item-content">
                    <span class="item-title">价格方案</span>
                    <span class="item-desc">灵活定价策略</span>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <a href="#tech" @click="scrollToSection('tech')" class="nav-item">
            <el-icon><Cpu /></el-icon>
            <span>技术</span>
          </a>
          
          <el-dropdown class="nav-dropdown" trigger="hover" placement="bottom-start">
            <div class="nav-item dropdown-trigger">
              <el-icon><InfoFilled /></el-icon>
              <span>关于</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item @click="navigateTo('/about')" class="dropdown-item">
                  <el-icon><OfficeBuilding /></el-icon>
                  <div class="item-content">
                    <span class="item-title">公司介绍</span>
                    <span class="item-desc">了解我们的故事</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item @click="navigateTo('/team')" class="dropdown-item">
                  <el-icon><UserFilled /></el-icon>
                  <div class="item-content">
                    <span class="item-title">团队成员</span>
                    <span class="item-desc">优秀的团队</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item @click="navigateTo('/contact')" class="dropdown-item">
                  <el-icon><Message /></el-icon>
                  <div class="item-content">
                    <span class="item-title">联系我们</span>
                    <span class="item-desc">随时为您服务</span>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </nav>
      </div>

      <!-- 右侧操作区 -->
      <div class="nav-actions">
        <!-- 工具按钮 -->
        <div class="tool-buttons">
          <el-button
            circle
            class="tool-btn search-btn"
            @click="toggleSearch"
            title="搜索"
          >
            <el-icon><Search /></el-icon>
          </el-button>
        </div>

        <!-- CTA按钮 -->
        <div class="cta-buttons">
          <el-button class="contact-button" @click="navigateTo('/contact')">
            <el-icon><Phone /></el-icon>
            <span>联系我们</span>
          </el-button>
          
          <el-button type="primary" class="login-button" @click="goToLogin">
            <el-icon><User /></el-icon>
            <span>立即登录</span>
          </el-button>
        </div>

        <!-- 移动端菜单按钮 -->
        <el-button
          circle
          class="mobile-toggle"
          @click="toggleMobileMenu"
        >
          <el-icon><component :is="isMobileMenuOpen ? Close : Menu" /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 搜索面板 -->
    <transition name="search-slide">
      <div v-if="showSearch" class="search-overlay">
        <div class="search-content">
          <div class="search-header">
            <h3>搜索功能和内容</h3>
            <el-button text @click="toggleSearch" class="close-search">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          
          <div class="search-input-wrapper">
            <el-input
              v-model="searchQuery"
              placeholder="输入关键词搜索..."
              size="large"
              class="search-input"
              @keyup.enter="handleSearch"
              ref="searchInput"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          
          <div v-if="searchSuggestions.length > 0" class="search-suggestions">
            <h4>推荐搜索</h4>
            <div class="suggestion-grid">
              <div
                v-for="(suggestion, index) in searchSuggestions"
                :key="index"
                class="suggestion-card"
                @click="selectSuggestion(suggestion)"
              >
                <el-icon><component :is="suggestion.icon" /></el-icon>
                <span class="suggestion-title">{{ suggestion.title }}</span>
                <el-tag size="small" :type="suggestion.type || 'info'">{{ suggestion.category }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import {
  School,
  User,
  House,
  Star,
  Grid,
  ArrowDown,
  Document,
  Money,
  Trophy,
  Refresh,
  Cpu,
  InfoFilled,
  OfficeBuilding,
  UserFilled,
  Suitcase,
  Message,
  Search,
  Phone,
  Menu,
  Close
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式状态
const isScrolled = ref(false)
const isMobileMenuOpen = ref(false)
const showSearch = ref(false)
const searchQuery = ref('')
const searchInput = ref(null)
const isMobile = ref(false)

// 搜索建议数据
const searchSuggestions = ref([
  { title: '学生管理系统', url: '/features/student', icon: 'UserFilled', category: '核心功能', type: 'success' },
  { title: '教师管理功能', url: '/features/teacher', icon: 'User', category: '核心功能', type: 'success' },
  { title: '家长监督平台', url: '/features/parent', icon: 'View', category: '核心功能', type: 'success' },
  { title: '在线课程', url: '/features/courses', icon: 'Document', category: '教学模块', type: 'primary' },
  { title: '成绩管理', url: '/features/grades', icon: 'TrendCharts', category: '教学模块', type: 'primary' },
  { title: '缴费系统', url: '/features/payment', icon: 'Money', category: '财务模块', type: 'warning' },
  { title: '案例展示', url: '/cases', icon: 'Trophy', category: '产品展示', type: 'info' },
  { title: '联系我们', url: '/contact', icon: 'Message', category: '客户服务', type: 'info' }
])

// 计算过滤后的搜索建议
const filteredSuggestions = computed(() => {
  if (!searchQuery.value) return searchSuggestions.value
  
  return searchSuggestions.value.filter(item =>
    item.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    item.category.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

// 处理搜索选择
const selectSuggestion = (suggestion) => {
  navigateTo(suggestion.url)
  toggleSearch()
  searchQuery.value = ''
}

// 方法
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId === 'home' ? 'features' : sectionId)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
  closeMobileMenu()
}

const goToLogin = () => {
  router.push('/login')
}

const navigateTo = (path) => {
  router.push(path)
  closeMobileMenu()
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

const toggleSearch = async () => {
  showSearch.value = !showSearch.value
  if (showSearch.value) {
    await nextTick()
    searchInput.value?.focus()
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    console.log('搜索:', searchQuery.value)
    // 这里可以实现搜索逻辑
    toggleSearch()
  }
}



const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark', isDarkMode.value)
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
}

// 滚动监听
const handleScroll = () => {
  isScrolled.value = window.scrollY > 50
}

// 点击外部关闭移动菜单
const handleClickOutside = (event) => {
  const navbar = document.querySelector('.navbar')
  if (navbar && !navbar.contains(event.target)) {
    closeMobileMenu()
  }
}

onMounted(() => {
  // 监听滚动事件
  window.addEventListener('scroll', handleScroll)
  // 监听点击事件
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
/* 现代化导航栏 */
.modern-navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  transition: all 0.4s cubic-bezier(0.23, 1, 0.32, 1);
}

.modern-navbar.scrolled {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border-bottom-color: rgba(0, 0, 0, 0.1);
}

.navbar-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 32px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 品牌区域 */
.brand-section {
  flex: none;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.brand-section:hover {
  transform: translateY(-2px);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-circle {
  width: 52px;
  height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.logo-circle::before {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.logo-circle:hover::before {
  opacity: 0.2;
}

.logo-circle:hover {
  transform: scale(1.05) rotate(5deg);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

.logo-icon {
  color: white;
  font-size: 28px;
  z-index: 1;
}

.brand-text {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 24px;
  font-weight: 800;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

.brand-slogan {
  font-size: 11px;
  color: #64748b;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-top: 2px;
}

/* 中央导航区域 */
.nav-center {
  flex: 1;
  display: flex;
  justify-content: center;
  padding: 0 40px;
}

.main-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(248, 250, 252, 0.8);
  padding: 8px;
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border-radius: 12px;
  color: #64748b;
  font-weight: 600;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  position: relative;
  white-space: nowrap;
}

.nav-item:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.nav-item:active {
  transform: translateY(0);
}

.dropdown-trigger {
  cursor: pointer;
}

.arrow-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

.nav-dropdown:hover .arrow-icon {
  transform: rotate(180deg);
}

/* 下拉菜单美化 */
.custom-dropdown {
  background: white;
  border: none;
  border-radius: 16px;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  padding: 12px;
  margin-top: 8px;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 12px;
  margin: 4px 0;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background: rgba(102, 126, 234, 0.1);
  transform: translateX(4px);
}

.item-content {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.item-title {
  font-weight: 600;
  color: #1a202c;
  font-size: 14px;
}

.item-desc {
  font-size: 12px;
  color: #64748b;
  margin-top: 2px;
}

/* 右侧操作区 */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: none;
}

.tool-buttons {
  display: flex;
  gap: 8px;
}

.tool-btn {
  width: 44px;
  height: 44px;
  border: none;
  background: rgba(248, 250, 252, 0.9);
  color: #64748b;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tool-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.theme-btn:hover {
  background: rgba(245, 158, 11, 0.1);
  color: #f59e0b;
}

.search-btn:hover {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
}

.cta-buttons {
  display: flex;
  gap: 12px;
}

.contact-button {
  padding: 12px 24px;
  border: 1.5px solid #e2e8f0;
  background: white;
  color: #64748b;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.contact-button:hover {
  border-color: #667eea;
  color: #667eea;
  background: rgba(102, 126, 234, 0.05);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.login-button {
  padding: 12px 28px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  border-radius: 12px;
  font-weight: 700;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.login-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 30px rgba(102, 126, 234, 0.4);
}

.mobile-toggle {
  display: none;
  width: 44px;
  height: 44px;
  border: none;
  background: rgba(248, 250, 252, 0.9);
  color: #64748b;
  border-radius: 12px;
  transition: all 0.3s ease;
}

/* 搜索面板 */
.search-overlay {
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
  z-index: 999;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding-top: 80px;
}

.search-content {
  background: white;
  border-radius: 24px;
  padding: 32px;
  max-width: 600px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  transform: translateY(-20px);
}

.search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.search-header h3 {
  margin: 0;
  color: #1a202c;
  font-weight: 700;
  font-size: 20px;
}

.close-search {
  color: #64748b;
  padding: 8px;
}

.search-input-wrapper {
  margin-bottom: 32px;
}

.search-input {
  width: 100%;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 2px solid #f1f5f9;
  transition: all 0.3s ease;
  height: 56px;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.15);
}

.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.search-suggestions h4 {
  margin: 0 0 16px 0;
  color: #64748b;
  font-size: 14px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.1em;
}

.suggestion-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 12px;
}

.suggestion-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.suggestion-card:hover {
  background: rgba(102, 126, 234, 0.05);
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.suggestion-title {
  flex: 1;
  font-weight: 600;
  color: #1a202c;
  font-size: 14px;
}

/* 过渡动画 */
.search-slide-enter-active,
.search-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
}

.search-slide-enter-from,
.search-slide-leave-to {
  opacity: 0;
  transform: scale(0.9);
}


/* 响应式设计 */
@media (max-width: 1200px) {
  .navbar-container {
    padding: 0 24px;
  }
  
  .nav-center {
    padding: 0 24px;
  }
  
  .brand-slogan {
    display: none;
  }
}

@media (max-width: 968px) {
  .navbar-container {
    padding: 0 20px;
  }
  
  .nav-center {
    display: none;
  }
  
  .mobile-toggle {
    display: block;
  }
  
  .tool-buttons {
    gap: 4px;
  }
  
  .cta-buttons {
    gap: 8px;
  }
  
  .contact-button,
  .login-button {
    padding: 10px 16px;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .navbar-container {
    height: 70px;
    padding: 0 16px;
  }
  
  .logo-circle {
    width: 44px;
    height: 44px;
  }
  
  .logo-icon {
    font-size: 24px;
  }
  
  .brand-name {
    font-size: 20px;
  }
  
  .tool-buttons {
    display: none;
  }
  
  .cta-buttons {
    display: none;
  }
  
  .nav-center.mobile-open {
    position: fixed;
    top: 70px;
    left: 0;
    right: 0;
    bottom: 0;
    background: white;
    display: flex;
    flex-direction: column;
    padding: 32px 24px;
    z-index: 998;
    transform: translateX(0);
  }
  
  .nav-center.mobile-open .main-nav {
    flex-direction: column;
    background: transparent;
    padding: 0;
    border: none;
    gap: 16px;
    width: 100%;
  }
  
  .nav-center.mobile-open .nav-item {
    width: 100%;
    justify-content: center;
    padding: 16px 24px;
    background: rgba(248, 250, 252, 0.8);
    border: 1px solid rgba(0, 0, 0, 0.05);
  }
  
  .search-content {
    margin: 20px;
    padding: 24px;
  }
  
  .suggestion-grid {
    grid-template-columns: 1fr;
  }
}

</style>