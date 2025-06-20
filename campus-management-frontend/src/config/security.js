/**
 * 前端安全配置
 * 集中管理所有安全相关的配置项
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */

/**
 * 安全配置常量
 */
export const SECURITY_CONFIG = {
  // 加密配置
  ENCRYPTION: {
    // 是否启用加密
    ENABLED: import.meta.env.VITE_ENCRYPTION_ENABLED === 'true',
    
    // 加密算法
    ALGORITHM: 'AES-256-CBC',
    
    // 密钥长度
    KEY_SIZE: 256,
    
    // IV长度
    IV_SIZE: 128,
    
    // 会话密钥过期时间（毫秒）
    SESSION_KEY_EXPIRY: 30 * 60 * 1000, // 30分钟
    
    // 敏感字段列表
    SENSITIVE_FIELDS: [
      'email',
      'phone',
      'idCard',
      'password',
      'bankAccount',
      'address',
      'parentPhone',
      'emergencyPhone'
    ],
    
    // 需要加密的API端点
    SENSITIVE_ENDPOINTS: [
      '/auth/login',
      '/auth/register',
      '/auth/change-password',
      '/auth/reset-password',
      '/users/profile',
      '/students/create',
      '/students/update',
      '/teachers/create',
      '/teachers/update',
      '/parents/create',
      '/parents/update',
      '/payment/',
      '/grades/',
      '/financial/'
    ]
  },

  // 签名验证配置
  SIGNATURE: {
    // 是否启用签名验证
    ENABLED: import.meta.env.VITE_SIGNATURE_ENABLED === 'true',
    
    // 签名算法
    ALGORITHM: 'HMAC-SHA256',
    
    // 签名头名称
    HEADER: 'X-Signature',
    
    // 时间戳头名称
    TIMESTAMP_HEADER: 'X-Timestamp',
    
    // 请求有效期（毫秒）
    MAX_REQUEST_AGE: 5 * 60 * 1000 // 5分钟
  },

  // 认证配置
  AUTHENTICATION: {
    // JWT令牌头名称
    TOKEN_HEADER: 'Authorization',
    
    // 令牌前缀
    TOKEN_PREFIX: 'Bearer ',
    
    // 令牌存储键名
    TOKEN_STORAGE_KEY: 'campus_access_token',
    
    // 刷新令牌存储键名
    REFRESH_TOKEN_STORAGE_KEY: 'campus_refresh_token',
    
    // 用户信息存储键名
    USER_INFO_STORAGE_KEY: 'campus_user_info',
    
    // 令牌自动刷新阈值（毫秒）
    AUTO_REFRESH_THRESHOLD: 30 * 60 * 1000, // 30分钟
    
    // 最大登录尝试次数
    MAX_LOGIN_ATTEMPTS: 5,
    
    // 登录锁定时间（毫秒）
    LOGIN_LOCKOUT_DURATION: 15 * 60 * 1000 // 15分钟
  },

  // HTTP请求配置
  HTTP: {
    // 请求超时时间
    TIMEOUT: parseInt(import.meta.env.VITE_API_TIMEOUT) || 30000,
    
    // 重试次数
    RETRY_TIMES: 3,
    
    // 重试延迟（毫秒）
    RETRY_DELAY: 1000,
    
    // 并发请求限制
    MAX_CONCURRENT_REQUESTS: 6,
    
    // 安全头配置
    SECURITY_HEADERS: {
      'X-Requested-With': 'XMLHttpRequest',
      'X-Client-Version': '1.0.0',
      'X-Client-Platform': 'web'
    }
  },

  // 数据保护配置
  DATA_PROTECTION: {
    // 本地存储加密
    LOCAL_STORAGE_ENCRYPTION: true,
    
    // 敏感数据脱敏
    DATA_MASKING: true,
    
    // 数据脱敏规则
    MASKING_RULES: {
      phone: (value) => value.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2'),
      email: (value) => value.replace(/(.{2}).*(@.*)/, '$1***$2'),
      idCard: (value) => value.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2'),
      bankAccount: (value) => value.replace(/(\d{4})\d+(\d{4})/, '$1****$2')
    },
    
    // 自动清理配置
    AUTO_CLEANUP: {
      // 页面关闭时清理
      ON_PAGE_UNLOAD: true,
      
      // 空闲时间清理（毫秒）
      IDLE_TIMEOUT: 60 * 60 * 1000, // 1小时
      
      // 清理的存储键
      CLEANUP_KEYS: [
        'campus_temp_data',
        'campus_form_cache',
        'campus_search_history'
      ]
    }
  },

  // 密码策略配置
  PASSWORD_POLICY: {
    // 最小长度
    MIN_LENGTH: 8,
    
    // 最大长度
    MAX_LENGTH: 128,
    
    // 必须包含小写字母
    REQUIRE_LOWERCASE: true,
    
    // 必须包含大写字母
    REQUIRE_UPPERCASE: true,
    
    // 必须包含数字
    REQUIRE_NUMBERS: true,
    
    // 必须包含特殊字符
    REQUIRE_SPECIAL_CHARS: true,
    
    // 特殊字符列表
    SPECIAL_CHARS: '!@#$%^&*(),.?":{}|<>',
    
    // 密码历史记录数量
    PASSWORD_HISTORY: 5,
    
    // 密码过期时间（天）
    PASSWORD_EXPIRY_DAYS: 90
  },

  // 安全监控配置
  MONITORING: {
    // 是否启用监控
    ENABLED: import.meta.env.VITE_MONITORING_ENABLED === 'true',
    
    // 错误上报
    ERROR_REPORTING: import.meta.env.VITE_ERROR_REPORTING_ENABLED === 'true',
    
    // 性能监控
    PERFORMANCE_MONITORING: true,
    
    // 安全事件类型
    SECURITY_EVENTS: {
      LOGIN_SUCCESS: 'login_success',
      LOGIN_FAILURE: 'login_failure',
      LOGOUT: 'logout',
      TOKEN_REFRESH: 'token_refresh',
      UNAUTHORIZED_ACCESS: 'unauthorized_access',
      ENCRYPTION_ERROR: 'encryption_error',
      DECRYPTION_ERROR: 'decryption_error',
      SIGNATURE_VERIFICATION_FAILED: 'signature_verification_failed',
      SUSPICIOUS_ACTIVITY: 'suspicious_activity'
    },
    
    // 性能阈值
    PERFORMANCE_THRESHOLDS: {
      // 加密操作最大耗时（毫秒）
      ENCRYPTION_MAX_TIME: 100,
      
      // API请求最大耗时（毫秒）
      API_REQUEST_MAX_TIME: 5000,
      
      // 页面加载最大耗时（毫秒）
      PAGE_LOAD_MAX_TIME: 3000
    }
  },

  // 开发环境配置
  DEVELOPMENT: {
    // 是否为开发环境
    IS_DEV: import.meta.env.DEV,
    
    // 调试模式
    DEBUG_MODE: import.meta.env.VITE_DEBUG_MODE === 'true',
    
    // 日志级别
    LOG_LEVEL: import.meta.env.VITE_LOG_LEVEL || 'info',
    
    // 是否显示安全警告
    SHOW_SECURITY_WARNINGS: true,
    
    // 是否启用开发者工具
    ENABLE_DEV_TOOLS: import.meta.env.DEV
  },

  // 生产环境配置
  PRODUCTION: {
    // 是否为生产环境
    IS_PROD: import.meta.env.PROD,
    
    // 是否启用HTTPS
    HTTPS_ENABLED: import.meta.env.VITE_HTTPS_ENABLED === 'true',
    
    // 是否禁用开发者工具
    DISABLE_DEV_TOOLS: import.meta.env.PROD,
    
    // 是否启用CSP
    ENABLE_CSP: true,
    
    // CSP策略
    CSP_POLICY: {
      'default-src': "'self'",
      'script-src': "'self' 'unsafe-inline'",
      'style-src': "'self' 'unsafe-inline'",
      'img-src': "'self' data: https:",
      'font-src': "'self' https:",
      'connect-src': "'self' https:",
      'frame-ancestors': "'none'"
    }
  }
}

/**
 * 获取当前环境的安全配置
 * @returns {Object} 安全配置对象
 */
export function getSecurityConfig() {
  const config = { ...SECURITY_CONFIG }
  
  // 根据环境调整配置
  if (config.DEVELOPMENT.IS_DEV) {
    // 开发环境：放宽一些限制
    config.HTTP.TIMEOUT = 60000 // 增加超时时间
    config.SIGNATURE.ENABLED = false // 可选择性禁用签名
  } else if (config.PRODUCTION.IS_PROD) {
    // 生产环境：加强安全措施
    config.ENCRYPTION.ENABLED = true // 强制启用加密
    config.SIGNATURE.ENABLED = true // 强制启用签名
    config.DEVELOPMENT.DEBUG_MODE = false // 禁用调试模式
  }
  
  return config
}

/**
 * 验证安全配置
 * @returns {Object} 验证结果
 */
export function validateSecurityConfig() {
  const config = getSecurityConfig()
  const issues = []
  
  // 检查必需的环境变量
  if (!import.meta.env.VITE_API_BASE_URL) {
    issues.push('缺少API基础URL配置')
  }
  
  if (!import.meta.env.VITE_CRYPTO_SECRET_KEY) {
    issues.push('缺少加密密钥配置')
  }
  
  // 检查密钥强度
  const cryptoKey = import.meta.env.VITE_CRYPTO_SECRET_KEY
  if (cryptoKey && cryptoKey.length < 32) {
    issues.push('加密密钥长度不足，建议使用32位以上')
  }
  
  // 生产环境检查
  if (config.PRODUCTION.IS_PROD) {
    if (!config.PRODUCTION.HTTPS_ENABLED) {
      issues.push('生产环境建议启用HTTPS')
    }
    
    if (config.DEVELOPMENT.DEBUG_MODE) {
      issues.push('生产环境不应启用调试模式')
    }
  }
  
  return {
    isValid: issues.length === 0,
    issues: issues
  }
}

// 默认导出
export default SECURITY_CONFIG
