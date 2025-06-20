# 🔐 智慧校园管理系统前端安全架构

## 📋 概述

本文档详细描述了智慧校园管理系统前端的安全架构设计，包括加密传输、身份认证、数据保护、安全通信等核心安全功能的实现方案。

## 🏗️ 安全架构设计

### 1. 整体安全架构

```
┌─────────────────────────────────────────────────────────────┐
│                    前端安全架构层次                          │
├─────────────────────────────────────────────────────────────┤
│  应用层 │ Vue.js 组件 │ 路由守卫 │ 权限控制 │ 用户界面      │
├─────────────────────────────────────────────────────────────┤
│  安全层 │ 加密工具 │ 安全HTTP │ 身份认证 │ 数据验证       │
├─────────────────────────────────────────────────────────────┤
│  传输层 │ HTTPS/TLS │ 请求签名 │ 防重放 │ 数据完整性     │
├─────────────────────────────────────────────────────────────┤
│  存储层 │ 本地加密 │ 敏感数据保护 │ 安全清理 │ 密钥管理   │
└─────────────────────────────────────────────────────────────┘
```

### 2. 核心安全组件

#### 2.1 加密工具类 (`crypto.js`)
- **AES-256-CBC加密**: 对称加密算法，用于敏感数据加密
- **随机IV生成**: 每次加密使用不同的初始化向量
- **HMAC签名**: 数据完整性验证
- **密钥管理**: 会话密钥生成和轮换
- **密码强度验证**: 密码复杂度检查

#### 2.2 安全HTTP客户端 (`secureHttp.js`)
- **请求加密**: 敏感端点数据自动加密
- **响应解密**: 加密响应数据自动解密
- **签名验证**: API请求完整性验证
- **防重放攻击**: 时间戳和随机数机制
- **错误处理**: 安全的错误信息处理

#### 2.3 身份认证模块
- **JWT令牌管理**: 安全的令牌存储和刷新
- **权限控制**: 基于角色的访问控制
- **会话管理**: 安全的会话状态管理
- **自动登出**: 令牌过期自动处理

## 🔒 安全功能实现

### 1. 数据加密传输

#### 1.1 敏感数据识别
```javascript
// 敏感字段列表
const SENSITIVE_FIELDS = [
  'email',        // 邮箱地址
  'phone',        // 手机号码
  'idCard',       // 身份证号
  'password',     // 密码
  'bankAccount',  // 银行账号
  'address'       // 详细地址
]

// 敏感端点列表
const SENSITIVE_ENDPOINTS = [
  '/auth/login',
  '/auth/register',
  '/users/profile',
  '/students/create',
  '/teachers/create',
  '/payment/*'
]
```

#### 1.2 加密流程
```javascript
// 请求加密流程
1. 检测敏感端点 → 2. 生成会话密钥 → 3. AES加密数据 → 
4. 添加时间戳 → 5. 生成HMAC签名 → 6. 发送请求

// 响应解密流程
1. 接收响应 → 2. 验证签名 → 3. 检查时间戳 → 
4. AES解密数据 → 5. 返回明文数据
```

### 2. 身份认证与授权

#### 2.1 JWT令牌管理
```javascript
// 令牌存储策略
- 访问令牌: 内存存储 (2小时有效期)
- 刷新令牌: 安全存储 (7天有效期)
- 自动刷新: 令牌过期前30分钟自动刷新
```

#### 2.2 权限控制
```javascript
// 路由权限守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查登录状态
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
    return
  }
  
  // 检查角色权限
  if (to.meta.roles && !authStore.hasRole(to.meta.roles)) {
    next('/403')
    return
  }
  
  next()
})
```

### 3. 安全通信机制

#### 3.1 请求签名
```javascript
// API请求签名算法
function generateAPISignature(method, url, params, timestamp) {
  const signString = `${method}&${url}&${sortedParams}&${timestamp}`
  return HMAC-SHA256(signString, secretKey)
}
```

#### 3.2 防重放攻击
```javascript
// 时间戳验证
const MAX_REQUEST_AGE = 5 * 60 * 1000 // 5分钟
const requestTime = parseInt(headers['X-Timestamp'])
const currentTime = Date.now()

if (currentTime - requestTime > MAX_REQUEST_AGE) {
  throw new Error('请求已过期')
}
```

### 4. 数据保护策略

#### 4.1 敏感数据处理
```javascript
// 数据脱敏显示
function maskSensitiveData(data, field) {
  switch (field) {
    case 'phone':
      return data.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    case 'email':
      return data.replace(/(.{2}).*(@.*)/, '$1***$2')
    case 'idCard':
      return data.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
    default:
      return data
  }
}
```

#### 4.2 本地存储安全
```javascript
// 安全存储策略
- 敏感数据: 加密后存储到sessionStorage
- 配置信息: 明文存储到localStorage
- 临时数据: 仅内存存储
- 自动清理: 页面关闭时清理敏感数据
```

## 🛡️ 安全配置

### 1. 环境配置

#### 1.1 开发环境 (`.env.development`)
```bash
# 加密配置
VITE_CRYPTO_SECRET_KEY=campus-dev-key-2024
VITE_ENCRYPTION_ENABLED=true
VITE_SIGNATURE_ENABLED=true

# 安全配置
VITE_HTTPS_ENABLED=false
VITE_DEBUG_MODE=true
```

#### 1.2 生产环境 (`.env.production`)
```bash
# 加密配置
VITE_CRYPTO_SECRET_KEY=your-production-secret-key
VITE_ENCRYPTION_ENABLED=true
VITE_SIGNATURE_ENABLED=true

# 安全配置
VITE_HTTPS_ENABLED=true
VITE_DEBUG_MODE=false
```

### 2. 安全头配置
```javascript
// HTTP安全头
const SECURITY_HEADERS = {
  'X-Content-Type-Options': 'nosniff',
  'X-Frame-Options': 'DENY',
  'X-XSS-Protection': '1; mode=block',
  'Referrer-Policy': 'strict-origin-when-cross-origin',
  'Content-Security-Policy': "default-src 'self'"
}
```

## 🔍 安全监控与审计

### 1. 安全事件监控
```javascript
// 安全事件类型
const SECURITY_EVENTS = {
  LOGIN_SUCCESS: 'login_success',
  LOGIN_FAILURE: 'login_failure',
  UNAUTHORIZED_ACCESS: 'unauthorized_access',
  DATA_ENCRYPTION_ERROR: 'encryption_error',
  SIGNATURE_VERIFICATION_FAILED: 'signature_failed'
}

// 事件上报
function reportSecurityEvent(eventType, details) {
  securePost('/security/events', {
    type: eventType,
    details: details,
    timestamp: Date.now(),
    userAgent: navigator.userAgent,
    url: window.location.href
  })
}
```

### 2. 性能监控
```javascript
// 加密性能监控
function monitorEncryptionPerformance() {
  const start = performance.now()
  // 执行加密操作
  const end = performance.now()
  
  if (end - start > 100) { // 超过100ms
    console.warn('加密操作耗时过长:', end - start)
  }
}
```

## 🚀 部署安全

### 1. 构建安全
```bash
# 生产构建
npm run build

# 安全检查
npm audit
npm run security-check
```

### 2. 运行时安全
```javascript
// CSP策略
<meta http-equiv="Content-Security-Policy" 
      content="default-src 'self'; 
               script-src 'self' 'unsafe-inline'; 
               style-src 'self' 'unsafe-inline';">

// 禁用开发者工具 (生产环境)
if (process.env.NODE_ENV === 'production') {
  document.addEventListener('keydown', (e) => {
    if (e.key === 'F12' || 
        (e.ctrlKey && e.shiftKey && e.key === 'I')) {
      e.preventDefault()
    }
  })
}
```

## 📊 安全测试

### 1. 单元测试
```javascript
// 加密功能测试
describe('CryptoUtil', () => {
  test('should encrypt and decrypt data correctly', () => {
    const data = 'sensitive information'
    const encrypted = CryptoUtil.encrypt(data)
    const decrypted = CryptoUtil.decrypt(encrypted)
    expect(decrypted).toBe(data)
  })
})
```

### 2. 安全扫描
```bash
# 依赖漏洞扫描
npm audit

# 代码安全扫描
npm run security-scan

# HTTPS配置检查
npm run ssl-check
```

## 🎯 最佳实践

### 1. 开发规范
- ✅ 敏感数据必须加密传输
- ✅ 所有API请求必须包含签名
- ✅ 用户输入必须进行验证和清理
- ✅ 错误信息不能泄露敏感信息
- ✅ 定期更新依赖包和安全补丁

### 2. 运维规范
- ✅ 生产环境必须启用HTTPS
- ✅ 定期轮换加密密钥
- ✅ 监控安全事件和异常
- ✅ 定期进行安全审计
- ✅ 建立应急响应机制

## 📈 安全指标

### 1. 关键指标
- 🔐 数据加密覆盖率: 100%
- 🛡️ API签名验证率: 100%
- ⚡ 加密操作平均耗时: <50ms
- 🚫 安全事件响应时间: <5min
- 📊 漏洞修复时间: <24h

### 2. 监控告警
- 🚨 加密失败率 > 1%
- 🚨 签名验证失败率 > 5%
- 🚨 未授权访问尝试 > 10次/分钟
- 🚨 异常登录行为检测
- 🚨 敏感数据泄露检测

---

**注意**: 本文档包含系统安全架构的核心信息，请妥善保管，不要泄露给未授权人员。
