import CryptoJS from 'crypto-js'

/**
 * 前端加密工具类
 * 提供与后端对应的加密传输支持
 *
 * @author Campus Management Team
 * @version 2.0.0
 * @since 2025-06-20
 */

// 加密配置 - 与后端保持一致
const CRYPTO_CONFIG = {
  // AES加密配置 - 对应后端AES-256-GCM
  AES: {
    keySize: 256 / 32,
    ivSize: 128 / 32,
    mode: CryptoJS.mode.CBC, // 前端使用CBC模式
    padding: CryptoJS.pad.Pkcs7
  },

  // 哈希配置
  HASH: {
    algorithm: 'SHA256'
  },

  // 会话配置
  SESSION: {
    keyExpiry: 30 * 60 * 1000, // 30分钟
    maxAge: 5 * 60 * 1000      // 5分钟防重放
  }
}

// 从环境变量或配置获取密钥
const getSecretKey = () => {
  return import.meta.env.VITE_CRYPTO_SECRET_KEY || 'campus-management-2024-secret-key-enhanced'
}

/**
 * 密钥管理器
 */
class KeyManager {
  constructor() {
    this.sessionKey = null
    this.keyExpiry = null
    this.baseKey = getSecretKey()
  }

  /**
   * 生成会话密钥
   */
  generateSessionKey() {
    this.sessionKey = CryptoJS.lib.WordArray.random(256/8).toString()
    this.keyExpiry = Date.now() + CRYPTO_CONFIG.SESSION.keyExpiry
    return this.sessionKey
  }

  /**
   * 获取当前会话密钥
   */
  getSessionKey() {
    if (!this.sessionKey || Date.now() > this.keyExpiry) {
      return this.generateSessionKey()
    }
    return this.sessionKey
  }

  /**
   * 获取基础密钥
   */
  getBaseKey() {
    return this.baseKey
  }

  /**
   * 清除密钥
   */
  clearKeys() {
    this.sessionKey = null
    this.keyExpiry = null
  }
}

// 全局密钥管理器实例
const keyManager = new KeyManager()

/**
 * 数据加密工具类
 */
export class CryptoUtil {
  /**
   * AES加密 - 增强版，使用随机IV
   * @param {string|Object} data - 要加密的数据
   * @param {string} customKey - 自定义密钥（可选）
   * @returns {string} 加密后的数据（Base64格式，包含IV）
   */
  static encrypt(data, customKey = null) {
    try {
      if (typeof data !== 'string') {
        data = JSON.stringify(data)
      }

      const key = customKey || keyManager.getBaseKey()
      const keyWords = CryptoJS.enc.Utf8.parse(key)
      const iv = CryptoJS.lib.WordArray.random(CRYPTO_CONFIG.AES.ivSize)

      const encrypted = CryptoJS.AES.encrypt(data, keyWords, {
        iv: iv,
        mode: CRYPTO_CONFIG.AES.mode,
        padding: CRYPTO_CONFIG.AES.padding
      })

      // 将IV和加密数据组合
      const combined = iv.concat(encrypted.ciphertext)
      return CryptoJS.enc.Base64.stringify(combined)
    } catch (error) {
      console.error('数据加密失败:', error)
      throw new Error('数据加密失败')
    }
  }

  /**
   * AES解密 - 增强版，支持IV分离
   * @param {string} encryptedData - 加密的数据（Base64格式，包含IV）
   * @param {string} customKey - 自定义密钥（可选）
   * @returns {string|Object} 解密后的数据
   */
  static decrypt(encryptedData, customKey = null) {
    try {
      const key = customKey || keyManager.getBaseKey()
      const keyWords = CryptoJS.enc.Utf8.parse(key)
      const combined = CryptoJS.enc.Base64.parse(encryptedData)

      // 分离IV和加密数据
      const iv = CryptoJS.lib.WordArray.create(
        combined.words.slice(0, CRYPTO_CONFIG.AES.ivSize)
      )
      const encrypted = CryptoJS.lib.WordArray.create(
        combined.words.slice(CRYPTO_CONFIG.AES.ivSize)
      )

      const decrypted = CryptoJS.AES.decrypt(
        { ciphertext: encrypted },
        keyWords,
        {
          iv: iv,
          mode: CRYPTO_CONFIG.AES.mode,
          padding: CRYPTO_CONFIG.AES.padding
        }
      )

      const decryptedText = decrypted.toString(CryptoJS.enc.Utf8)

      // 尝试解析JSON，如果失败则返回原字符串
      try {
        return JSON.parse(decryptedText)
      } catch {
        return decryptedText
      }
    } catch (error) {
      console.error('数据解密失败:', error)
      throw new Error('数据解密失败')
    }
  }

  /**
   * MD5哈希
   * @param {string} data - 要哈希的数据
   * @returns {string} 哈希值
   */
  static md5(data) {
    return CryptoJS.MD5(data).toString()
  }

  /**
   * SHA256哈希
   * @param {string} data - 要哈希的数据
   * @returns {string} 哈希值
   */
  static sha256(data) {
    return CryptoJS.SHA256(data).toString()
  }

  /**
   * Base64编码
   * @param {string} data - 要编码的数据
   * @returns {string} 编码后的数据
   */
  static base64Encode(data) {
    return CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(data))
  }

  /**
   * Base64解码
   * @param {string} data - 要解码的数据
   * @returns {string} 解码后的数据
   */
  static base64Decode(data) {
    return CryptoJS.enc.Base64.parse(data).toString(CryptoJS.enc.Utf8)
  }

  /**
   * 生成随机字符串
   * @param {number} length - 字符串长度
   * @returns {string} 随机字符串
   */
  static generateRandomString(length = 16) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    let result = ''
    for (let i = 0; i < length; i++) {
      result += chars.charAt(Math.floor(Math.random() * chars.length))
    }
    return result
  }

  /**
   * 时间戳加密（防重放攻击）
   * @param {any} data - 要加密的数据
   * @returns {string} 带时间戳的加密数据
   */
  static encryptWithTimestamp(data) {
    const timestamp = Date.now()
    const dataWithTimestamp = {
      data: data,
      timestamp: timestamp,
      nonce: this.generateRandomString(8)
    }
    return this.encrypt(dataWithTimestamp)
  }

  /**
   * 时间戳解密（防重放攻击）
   * @param {string} encryptedData - 加密的数据
   * @param {number} maxAge - 最大有效时间（毫秒），默认5分钟
   * @returns {any} 解密后的数据
   */
  static decryptWithTimestamp(encryptedData, maxAge = CRYPTO_CONFIG.SESSION.maxAge) {
    try {
      const decryptedData = this.decrypt(encryptedData)
      const { data, timestamp } = decryptedData

      const now = Date.now()
      if (now - timestamp > maxAge) {
        throw new Error('数据已过期')
      }

      return data
    } catch (error) {
      console.error('时间戳解密失败:', error)
      throw error
    }
  }

  /**
   * 敏感数据加密（对应后端字段级加密）
   * @param {Object} data - 包含敏感数据的对象
   * @param {Array} sensitiveFields - 敏感字段列表
   * @returns {Object} 加密后的数据对象
   */
  static encryptSensitiveFields(data, sensitiveFields = ['email', 'phone', 'idCard']) {
    if (!data || typeof data !== 'object') {
      return data
    }

    const encryptedData = { ...data }
    const sessionKey = keyManager.getSessionKey()

    sensitiveFields.forEach(field => {
      if (encryptedData[field] && typeof encryptedData[field] === 'string') {
        try {
          encryptedData[field] = this.encrypt(encryptedData[field], sessionKey)
          encryptedData[`${field}_encrypted`] = true
        } catch (error) {
          console.warn(`字段 ${field} 加密失败:`, error)
        }
      }
    })

    return encryptedData
  }

  /**
   * 敏感数据解密
   * @param {Object} data - 包含加密数据的对象
   * @param {Array} sensitiveFields - 敏感字段列表
   * @returns {Object} 解密后的数据对象
   */
  static decryptSensitiveFields(data, sensitiveFields = ['email', 'phone', 'idCard']) {
    if (!data || typeof data !== 'object') {
      return data
    }

    const decryptedData = { ...data }
    const sessionKey = keyManager.getSessionKey()

    sensitiveFields.forEach(field => {
      if (decryptedData[field] && decryptedData[`${field}_encrypted`]) {
        try {
          decryptedData[field] = this.decrypt(decryptedData[field], sessionKey)
          delete decryptedData[`${field}_encrypted`]
        } catch (error) {
          console.warn(`字段 ${field} 解密失败:`, error)
        }
      }
    })

    return decryptedData
  }

  /**
   * HMAC签名验证（用于API请求完整性验证）
   * @param {string} data - 数据
   * @param {string} signature - 签名
   * @param {string} key - 密钥
   * @returns {boolean} 验证结果
   */
  static verifyHMAC(data, signature, key = null) {
    try {
      const secretKey = key || keyManager.getBaseKey()
      const computedSignature = CryptoJS.HmacSHA256(data, secretKey).toString()
      return computedSignature === signature
    } catch (error) {
      console.error('HMAC验证失败:', error)
      return false
    }
  }

  /**
   * 生成API请求签名
   * @param {string} method - HTTP方法
   * @param {string} url - 请求URL
   * @param {Object} params - 请求参数
   * @param {string} timestamp - 时间戳
   * @returns {string} 签名
   */
  static generateAPISignature(method, url, params = {}, timestamp = null) {
    try {
      const ts = timestamp || Date.now().toString()
      const sortedParams = Object.keys(params)
        .sort()
        .map(key => `${key}=${params[key]}`)
        .join('&')

      const signString = `${method.toUpperCase()}&${url}&${sortedParams}&${ts}`
      return CryptoJS.HmacSHA256(signString, keyManager.getBaseKey()).toString()
    } catch (error) {
      console.error('API签名生成失败:', error)
      throw new Error('API签名生成失败')
    }
  }

  /**
   * 密码强度验证
   * @param {string} password - 密码
   * @returns {Object} 验证结果
   */
  static validatePasswordStrength(password) {
    const result = {
      isValid: false,
      score: 0,
      feedback: [],
      strength: 'weak'
    }

    if (!password) {
      result.feedback.push('密码不能为空')
      return result
    }

    // 长度检查
    if (password.length < 8) {
      result.feedback.push('密码长度至少8位')
    } else {
      result.score += 1
    }

    // 包含小写字母
    if (/[a-z]/.test(password)) {
      result.score += 1
    } else {
      result.feedback.push('密码应包含小写字母')
    }

    // 包含大写字母
    if (/[A-Z]/.test(password)) {
      result.score += 1
    } else {
      result.feedback.push('密码应包含大写字母')
    }

    // 包含数字
    if (/\d/.test(password)) {
      result.score += 1
    } else {
      result.feedback.push('密码应包含数字')
    }

    // 包含特殊字符
    if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
      result.score += 1
    } else {
      result.feedback.push('密码应包含特殊字符')
    }

    // 确定强度等级
    if (result.score >= 4) {
      result.strength = 'strong'
      result.isValid = true
    } else if (result.score >= 3) {
      result.strength = 'medium'
      result.isValid = true
    } else {
      result.strength = 'weak'
    }

    return result
  }

  /**
   * 清除敏感数据
   */
  static clearSensitiveData() {
    keyManager.clearKeys()
  }
}

// 导出密钥管理器和配置
export { keyManager, CRYPTO_CONFIG, KeyManager }

// 默认导出
export default CryptoUtil