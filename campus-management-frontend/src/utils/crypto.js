import CryptoJS from 'crypto-js'

// 加密密钥 - 在实际项目中应该从环境变量获取
const SECRET_KEY = 'campus-management-2024-secret-key'

/**
 * 数据加密工具类
 */
export class CryptoUtil {
  /**
   * AES加密
   * @param {string} data - 要加密的数据
   * @returns {string} 加密后的数据
   */
  static encrypt(data) {
    try {
      if (typeof data !== 'string') {
        data = JSON.stringify(data)
      }
      
      const encrypted = CryptoJS.AES.encrypt(data, SECRET_KEY, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      })
      
      return encrypted.toString()
    } catch (error) {
      console.error('数据加密失败:', error)
      throw new Error('数据加密失败')
    }
  }

  /**
   * AES解密
   * @param {string} encryptedData - 加密的数据
   * @returns {string} 解密后的数据
   */
  static decrypt(encryptedData) {
    try {
      const decrypted = CryptoJS.AES.decrypt(encryptedData, SECRET_KEY, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
      })
      
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
  static decryptWithTimestamp(encryptedData, maxAge = 5 * 60 * 1000) {
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
}

// 默认导出
export default CryptoUtil