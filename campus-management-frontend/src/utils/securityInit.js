/**
 * 前端安全初始化模块
 * 在应用启动时执行安全相关的初始化操作
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */

import { SECURITY_CONFIG, validateSecurityConfig } from '@/config/security.js'
import { keyManager } from '@/utils/crypto.js'

/**
 * 安全初始化类
 */
class SecurityInitializer {
  constructor() {
    this.initialized = false
    this.securityConfig = SECURITY_CONFIG
  }

  /**
   * 初始化安全模块
   */
  async initialize() {
    if (this.initialized) {
      return
    }

    console.log('🔐 正在初始化安全模块...')

    try {
      // 1. 验证安全配置
      await this.validateConfiguration()

      // 2. 初始化加密模块
      await this.initializeEncryption()

      // 3. 设置安全头
      await this.setupSecurityHeaders()

      // 4. 初始化监控
      await this.initializeMonitoring()

      // 5. 设置安全策略
      await this.setupSecurityPolicies()

      // 6. 清理敏感数据
      await this.setupDataCleanup()

      this.initialized = true
      console.log('✅ 安全模块初始化完成')

    } catch (error) {
      console.error('❌ 安全模块初始化失败:', error)
      throw error
    }
  }

  /**
   * 验证安全配置
   */
  async validateConfiguration() {
    console.log('🔍 验证安全配置...')

    const validation = validateSecurityConfig()
    
    if (!validation.isValid) {
      console.warn('⚠️ 安全配置存在问题:')
      validation.issues.forEach(issue => {
        console.warn(`  - ${issue}`)
      })

      // 在开发环境显示警告，生产环境抛出错误
      if (this.securityConfig.PRODUCTION.IS_PROD) {
        throw new Error('安全配置验证失败')
      }
    } else {
      console.log('✅ 安全配置验证通过')
    }
  }

  /**
   * 初始化加密模块
   */
  async initializeEncryption() {
    console.log('🔐 初始化加密模块...')

    if (this.securityConfig.ENCRYPTION.ENABLED) {
      // 生成初始会话密钥
      keyManager.generateSessionKey()
      console.log('✅ 加密模块已启用')
    } else {
      console.log('⚠️ 加密模块已禁用')
    }
  }

  /**
   * 设置安全头
   */
  async setupSecurityHeaders() {
    console.log('🛡️ 设置安全头...')

    // 设置CSP策略（仅生产环境）
    if (this.securityConfig.PRODUCTION.IS_PROD && this.securityConfig.PRODUCTION.ENABLE_CSP) {
      this.setupContentSecurityPolicy()
    }

    // 禁用开发者工具（仅生产环境）
    if (this.securityConfig.PRODUCTION.DISABLE_DEV_TOOLS) {
      this.disableDevTools()
    }

    console.log('✅ 安全头设置完成')
  }

  /**
   * 设置内容安全策略
   */
  setupContentSecurityPolicy() {
    const cspPolicy = this.securityConfig.PRODUCTION.CSP_POLICY
    const cspString = Object.entries(cspPolicy)
      .map(([directive, value]) => `${directive} ${value}`)
      .join('; ')

    // 创建meta标签设置CSP
    const meta = document.createElement('meta')
    meta.httpEquiv = 'Content-Security-Policy'
    meta.content = cspString
    document.head.appendChild(meta)

    console.log('✅ CSP策略已设置')
  }

  /**
   * 禁用开发者工具
   */
  disableDevTools() {
    // 禁用右键菜单
    document.addEventListener('contextmenu', (e) => {
      e.preventDefault()
      return false
    })

    // 禁用F12和开发者工具快捷键
    document.addEventListener('keydown', (e) => {
      // F12
      if (e.key === 'F12') {
        e.preventDefault()
        return false
      }
      
      // Ctrl+Shift+I
      if (e.ctrlKey && e.shiftKey && e.key === 'I') {
        e.preventDefault()
        return false
      }
      
      // Ctrl+Shift+J
      if (e.ctrlKey && e.shiftKey && e.key === 'J') {
        e.preventDefault()
        return false
      }
      
      // Ctrl+U
      if (e.ctrlKey && e.key === 'u') {
        e.preventDefault()
        return false
      }
    })

    // 检测开发者工具
    this.detectDevTools()

    console.log('✅ 开发者工具已禁用')
  }

  /**
   * 检测开发者工具
   */
  detectDevTools() {
    let devtools = { open: false, orientation: null }
    
    setInterval(() => {
      if (window.outerHeight - window.innerHeight > 200 || 
          window.outerWidth - window.innerWidth > 200) {
        if (!devtools.open) {
          devtools.open = true
          console.warn('检测到开发者工具被打开')
          // 可以在这里添加更多的安全措施
        }
      } else {
        devtools.open = false
      }
    }, 500)
  }

  /**
   * 初始化监控
   */
  async initializeMonitoring() {
    console.log('📊 初始化安全监控...')

    if (this.securityConfig.MONITORING.ENABLED) {
      // 设置错误监控
      this.setupErrorMonitoring()

      // 设置性能监控
      this.setupPerformanceMonitoring()

      // 设置安全事件监控
      this.setupSecurityEventMonitoring()

      console.log('✅ 安全监控已启用')
    } else {
      console.log('⚠️ 安全监控已禁用')
    }
  }

  /**
   * 设置错误监控
   */
  setupErrorMonitoring() {
    if (!this.securityConfig.MONITORING.ERROR_REPORTING) {
      return
    }

    window.addEventListener('error', (event) => {
      this.reportSecurityEvent('javascript_error', {
        message: event.message,
        filename: event.filename,
        lineno: event.lineno,
        colno: event.colno,
        stack: event.error?.stack
      })
    })

    window.addEventListener('unhandledrejection', (event) => {
      this.reportSecurityEvent('unhandled_promise_rejection', {
        reason: event.reason,
        stack: event.reason?.stack
      })
    })
  }

  /**
   * 设置性能监控
   */
  setupPerformanceMonitoring() {
    if (!this.securityConfig.MONITORING.PERFORMANCE_MONITORING) {
      return
    }

    // 监控页面加载性能
    window.addEventListener('load', () => {
      setTimeout(() => {
        const perfData = performance.getEntriesByType('navigation')[0]
        const loadTime = perfData.loadEventEnd - perfData.loadEventStart

        if (loadTime > this.securityConfig.MONITORING.PERFORMANCE_THRESHOLDS.PAGE_LOAD_MAX_TIME) {
          this.reportSecurityEvent('performance_issue', {
            type: 'slow_page_load',
            loadTime: loadTime,
            threshold: this.securityConfig.MONITORING.PERFORMANCE_THRESHOLDS.PAGE_LOAD_MAX_TIME
          })
        }
      }, 0)
    })
  }

  /**
   * 设置安全事件监控
   */
  setupSecurityEventMonitoring() {
    // 监控可疑活动
    let clickCount = 0
    let lastClickTime = 0

    document.addEventListener('click', () => {
      const now = Date.now()
      if (now - lastClickTime < 100) { // 100ms内多次点击
        clickCount++
        if (clickCount > 10) { // 10次以上快速点击
          this.reportSecurityEvent('suspicious_activity', {
            type: 'rapid_clicking',
            count: clickCount,
            timespan: now - lastClickTime
          })
          clickCount = 0
        }
      } else {
        clickCount = 0
      }
      lastClickTime = now
    })
  }

  /**
   * 上报安全事件
   */
  reportSecurityEvent(eventType, details) {
    if (!this.securityConfig.MONITORING.ENABLED) {
      return
    }

    const event = {
      type: eventType,
      details: details,
      timestamp: Date.now(),
      userAgent: navigator.userAgent,
      url: window.location.href,
      referrer: document.referrer
    }

    // 在开发环境打印到控制台
    if (this.securityConfig.DEVELOPMENT.IS_DEV) {
      console.log('🚨 安全事件:', event)
    }

    // 发送到服务器（可以根据需要实现）
    // securePost('/security/events', event).catch(console.error)
  }

  /**
   * 设置安全策略
   */
  async setupSecurityPolicies() {
    console.log('📋 设置安全策略...')

    // 设置密码策略
    this.setupPasswordPolicy()

    // 设置会话策略
    this.setupSessionPolicy()

    console.log('✅ 安全策略设置完成')
  }

  /**
   * 设置密码策略
   */
  setupPasswordPolicy() {
    // 密码策略已在crypto.js中实现
    console.log('✅ 密码策略已设置')
  }

  /**
   * 设置会话策略
   */
  setupSessionPolicy() {
    // 设置空闲超时
    let idleTimer = null
    const idleTimeout = this.securityConfig.DATA_PROTECTION.AUTO_CLEANUP.IDLE_TIMEOUT

    const resetIdleTimer = () => {
      if (idleTimer) {
        clearTimeout(idleTimer)
      }
      
      idleTimer = setTimeout(() => {
        this.handleIdleTimeout()
      }, idleTimeout)
    }

    // 监听用户活动
    ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart'].forEach(event => {
      document.addEventListener(event, resetIdleTimer, true)
    })

    resetIdleTimer()
    console.log('✅ 会话策略已设置')
  }

  /**
   * 处理空闲超时
   */
  handleIdleTimeout() {
    console.log('⏰ 检测到用户空闲，执行安全清理')
    
    // 清理敏感数据
    this.cleanupSensitiveData()
    
    // 可以选择自动登出
    // const authStore = useAuthStore()
    // authStore.logout()
  }

  /**
   * 设置数据清理
   */
  async setupDataCleanup() {
    console.log('🧹 设置数据清理策略...')

    if (this.securityConfig.DATA_PROTECTION.AUTO_CLEANUP.ON_PAGE_UNLOAD) {
      window.addEventListener('beforeunload', () => {
        this.cleanupSensitiveData()
      })
    }

    console.log('✅ 数据清理策略设置完成')
  }

  /**
   * 清理敏感数据
   */
  cleanupSensitiveData() {
    const cleanupKeys = this.securityConfig.DATA_PROTECTION.AUTO_CLEANUP.CLEANUP_KEYS

    cleanupKeys.forEach(key => {
      try {
        localStorage.removeItem(key)
        sessionStorage.removeItem(key)
      } catch (error) {
        console.warn(`清理存储键失败: ${key}`, error)
      }
    })

    // 清理密钥管理器
    keyManager.clearKeys()

    console.log('✅ 敏感数据清理完成')
  }
}

// 创建全局实例
const securityInitializer = new SecurityInitializer()

/**
 * 初始化安全模块
 */
export async function initializeSecurity() {
  await securityInitializer.initialize()
}

/**
 * 上报安全事件
 */
export function reportSecurityEvent(eventType, details) {
  securityInitializer.reportSecurityEvent(eventType, details)
}

/**
 * 清理敏感数据
 */
export function cleanupSensitiveData() {
  securityInitializer.cleanupSensitiveData()
}

export default securityInitializer
