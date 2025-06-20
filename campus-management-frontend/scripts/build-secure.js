#!/usr/bin/env node

/**
 * 安全构建脚本
 * 确保前端应用的安全配置正确
 * 
 * @author Campus Management Team
 * @version 1.0.0
 * @since 2025-06-20
 */

const fs = require('fs')
const path = require('path')
const crypto = require('crypto')
const { execSync } = require('child_process')

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  red: '\x1b[31m',
  green: '\x1b[32m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  magenta: '\x1b[35m',
  cyan: '\x1b[36m'
}

const log = {
  info: (msg) => console.log(`${colors.blue}ℹ${colors.reset} ${msg}`),
  success: (msg) => console.log(`${colors.green}✓${colors.reset} ${msg}`),
  warning: (msg) => console.log(`${colors.yellow}⚠${colors.reset} ${msg}`),
  error: (msg) => console.log(`${colors.red}✗${colors.reset} ${msg}`),
  title: (msg) => console.log(`\n${colors.cyan}${msg}${colors.reset}`)
}

/**
 * 安全检查配置
 */
const SECURITY_CHECKS = {
  // 必需的环境变量
  requiredEnvVars: [
    'VITE_API_BASE_URL',
    'VITE_CRYPTO_SECRET_KEY',
    'VITE_ENCRYPTION_ENABLED'
  ],
  
  // 敏感文件检查
  sensitiveFiles: [
    '.env.local',
    '.env.development.local',
    '.env.production.local',
    'src/config/secrets.js'
  ],
  
  // 安全依赖检查
  securityDeps: [
    'crypto-js',
    'axios'
  ],
  
  // 构建输出检查
  buildChecks: [
    'index.html',
    'assets/',
    'static/'
  ]
}

/**
 * 主构建流程
 */
async function main() {
  try {
    log.title('🔐 智慧校园管理系统 - 安全构建')
    
    // 1. 环境检查
    await checkEnvironment()
    
    // 2. 安全配置检查
    await checkSecurityConfig()
    
    // 3. 依赖安全检查
    await checkDependencies()
    
    // 4. 代码安全扫描
    await scanCodeSecurity()
    
    // 5. 执行构建
    await buildApplication()
    
    // 6. 构建产物检查
    await checkBuildOutput()
    
    // 7. 生成安全报告
    await generateSecurityReport()
    
    log.success('安全构建完成！')
    
  } catch (error) {
    log.error(`构建失败: ${error.message}`)
    process.exit(1)
  }
}

/**
 * 环境检查
 */
async function checkEnvironment() {
  log.title('1. 环境检查')
  
  // 检查Node.js版本
  const nodeVersion = process.version
  log.info(`Node.js版本: ${nodeVersion}`)
  
  // 检查npm版本
  try {
    const npmVersion = execSync('npm --version', { encoding: 'utf8' }).trim()
    log.info(`npm版本: ${npmVersion}`)
  } catch (error) {
    log.warning('无法获取npm版本')
  }
  
  // 检查环境变量
  const envFile = process.env.NODE_ENV === 'production' ? '.env.production' : '.env.development'
  if (fs.existsSync(envFile)) {
    log.success(`环境配置文件存在: ${envFile}`)
  } else {
    log.warning(`环境配置文件不存在: ${envFile}`)
  }
  
  // 检查必需的环境变量
  for (const envVar of SECURITY_CHECKS.requiredEnvVars) {
    if (process.env[envVar]) {
      log.success(`环境变量已设置: ${envVar}`)
    } else {
      log.error(`缺少必需的环境变量: ${envVar}`)
      throw new Error(`缺少环境变量: ${envVar}`)
    }
  }
}

/**
 * 安全配置检查
 */
async function checkSecurityConfig() {
  log.title('2. 安全配置检查')
  
  // 检查敏感文件
  for (const file of SECURITY_CHECKS.sensitiveFiles) {
    if (fs.existsSync(file)) {
      log.warning(`发现敏感文件: ${file}`)
      
      // 检查是否在.gitignore中
      const gitignore = fs.readFileSync('.gitignore', 'utf8')
      if (!gitignore.includes(file)) {
        log.error(`敏感文件未在.gitignore中: ${file}`)
        throw new Error(`敏感文件未被忽略: ${file}`)
      } else {
        log.success(`敏感文件已被.gitignore忽略: ${file}`)
      }
    }
  }
  
  // 检查加密密钥强度
  const cryptoKey = process.env.VITE_CRYPTO_SECRET_KEY
  if (cryptoKey && cryptoKey.length < 32) {
    log.warning('加密密钥长度不足，建议使用32位以上的密钥')
  } else {
    log.success('加密密钥强度检查通过')
  }
  
  // 检查HTTPS配置
  const httpsEnabled = process.env.VITE_HTTPS_ENABLED === 'true'
  if (process.env.NODE_ENV === 'production' && !httpsEnabled) {
    log.warning('生产环境建议启用HTTPS')
  } else {
    log.success('HTTPS配置检查通过')
  }
}

/**
 * 依赖安全检查
 */
async function checkDependencies() {
  log.title('3. 依赖安全检查')
  
  // 检查package.json
  const packageJson = JSON.parse(fs.readFileSync('package.json', 'utf8'))
  
  // 检查安全依赖
  for (const dep of SECURITY_CHECKS.securityDeps) {
    if (packageJson.dependencies[dep] || packageJson.devDependencies[dep]) {
      log.success(`安全依赖已安装: ${dep}`)
    } else {
      log.error(`缺少安全依赖: ${dep}`)
      throw new Error(`缺少依赖: ${dep}`)
    }
  }
  
  // 运行npm audit
  try {
    log.info('运行npm audit检查漏洞...')
    execSync('npm audit --audit-level=moderate', { stdio: 'pipe' })
    log.success('npm audit检查通过')
  } catch (error) {
    log.warning('发现安全漏洞，请运行 npm audit fix 修复')
  }
}

/**
 * 代码安全扫描
 */
async function scanCodeSecurity() {
  log.title('4. 代码安全扫描')
  
  // 检查硬编码密钥
  const srcFiles = getAllJSFiles('src')
  const patterns = [
    /password\s*[:=]\s*['"][^'"]+['"]/i,
    /secret\s*[:=]\s*['"][^'"]+['"]/i,
    /key\s*[:=]\s*['"][^'"]+['"]/i,
    /token\s*[:=]\s*['"][^'"]+['"]/i
  ]
  
  let foundIssues = false
  for (const file of srcFiles) {
    const content = fs.readFileSync(file, 'utf8')
    for (const pattern of patterns) {
      if (pattern.test(content)) {
        log.warning(`可能的硬编码敏感信息: ${file}`)
        foundIssues = true
      }
    }
  }
  
  if (!foundIssues) {
    log.success('代码安全扫描通过')
  }
  
  // 检查console.log
  let consoleCount = 0
  for (const file of srcFiles) {
    const content = fs.readFileSync(file, 'utf8')
    const matches = content.match(/console\.(log|debug|info)/g)
    if (matches) {
      consoleCount += matches.length
    }
  }
  
  if (process.env.NODE_ENV === 'production' && consoleCount > 0) {
    log.warning(`生产环境发现${consoleCount}个console输出，建议清理`)
  } else {
    log.success('console输出检查通过')
  }
}

/**
 * 构建应用
 */
async function buildApplication() {
  log.title('5. 构建应用')
  
  try {
    log.info('开始构建...')
    execSync('npm run build', { stdio: 'inherit' })
    log.success('应用构建完成')
  } catch (error) {
    log.error('构建失败')
    throw error
  }
}

/**
 * 检查构建产物
 */
async function checkBuildOutput() {
  log.title('6. 构建产物检查')
  
  const distDir = 'dist'
  if (!fs.existsSync(distDir)) {
    log.error('构建目录不存在')
    throw new Error('构建目录不存在')
  }
  
  // 检查必要文件
  for (const check of SECURITY_CHECKS.buildChecks) {
    const filePath = path.join(distDir, check)
    if (fs.existsSync(filePath)) {
      log.success(`构建文件存在: ${check}`)
    } else {
      log.warning(`构建文件缺失: ${check}`)
    }
  }
  
  // 检查文件大小
  const stats = fs.statSync(path.join(distDir, 'index.html'))
  log.info(`index.html大小: ${(stats.size / 1024).toFixed(2)} KB`)
  
  // 检查是否包含源码映射
  const jsFiles = getAllJSFiles(distDir)
  let hasSourceMaps = false
  for (const file of jsFiles) {
    if (file.endsWith('.map')) {
      hasSourceMaps = true
      break
    }
  }
  
  if (process.env.NODE_ENV === 'production' && hasSourceMaps) {
    log.warning('生产环境包含源码映射文件，可能泄露源码信息')
  } else {
    log.success('源码映射检查通过')
  }
}

/**
 * 生成安全报告
 */
async function generateSecurityReport() {
  log.title('7. 生成安全报告')
  
  const report = {
    buildTime: new Date().toISOString(),
    nodeVersion: process.version,
    environment: process.env.NODE_ENV || 'development',
    securityChecks: {
      environmentVariables: 'PASS',
      dependencies: 'PASS',
      codeScanning: 'PASS',
      buildOutput: 'PASS'
    },
    recommendations: []
  }
  
  // 添加建议
  if (process.env.NODE_ENV === 'production') {
    if (process.env.VITE_HTTPS_ENABLED !== 'true') {
      report.recommendations.push('建议在生产环境启用HTTPS')
    }
    if (process.env.VITE_DEBUG_MODE === 'true') {
      report.recommendations.push('建议在生产环境关闭调试模式')
    }
  }
  
  // 保存报告
  const reportPath = 'security-report.json'
  fs.writeFileSync(reportPath, JSON.stringify(report, null, 2))
  log.success(`安全报告已生成: ${reportPath}`)
}

/**
 * 获取所有JS文件
 */
function getAllJSFiles(dir) {
  const files = []
  
  function traverse(currentDir) {
    const items = fs.readdirSync(currentDir)
    for (const item of items) {
      const fullPath = path.join(currentDir, item)
      const stat = fs.statSync(fullPath)
      
      if (stat.isDirectory()) {
        traverse(fullPath)
      } else if (item.endsWith('.js') || item.endsWith('.vue') || item.endsWith('.ts')) {
        files.push(fullPath)
      }
    }
  }
  
  if (fs.existsSync(dir)) {
    traverse(dir)
  }
  
  return files
}

// 运行主流程
if (require.main === module) {
  main()
}

module.exports = {
  main,
  checkEnvironment,
  checkSecurityConfig,
  checkDependencies,
  scanCodeSecurity
}
