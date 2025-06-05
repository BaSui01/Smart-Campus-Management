import request from './request'

/**
 * 认证相关API
 */
export const authApi = {
  // 用户登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data,
      encrypt: true // 敏感数据加密
    })
  },

  // 用户注册
  register(data) {
    return request({
      url: '/auth/register',
      method: 'post',
      data,
      encrypt: true // 敏感数据加密
    })
  },

  // 用户登出
  logout() {
    return request({
      url: '/auth/logout',
      method: 'post'
    })
  },

  // 刷新token
  refreshToken(refreshToken) {
    return request({
      url: '/auth/refresh-token',
      method: 'post',
      data: { refreshToken }
    })
  },

  // 获取当前用户信息
  getCurrentUser() {
    return request({
      url: '/auth/me',
      method: 'get'
    })
  },

  // 发送验证码
  sendVerificationCode(data) {
    return request({
      url: '/auth/send-verification-code',
      method: 'post',
      data
    })
  },

  // 验证验证码
  verifyCode(data) {
    return request({
      url: '/auth/verify-code',
      method: 'post',
      data
    })
  },

  // 忘记密码 - 发送重置链接
  forgotPassword(data) {
    return request({
      url: '/auth/forgot-password',
      method: 'post',
      data
    })
  },

  // 重置密码
  resetPassword(data) {
    return request({
      url: '/auth/reset-password',
      method: 'post',
      data,
      encrypt: true // 敏感数据加密
    })
  },

  // 修改密码
  changePassword(data) {
    return request({
      url: '/auth/change-password',
      method: 'post',
      data,
      encrypt: true // 敏感数据加密
    })
  },

  // 验证旧密码
  verifyOldPassword(data) {
    return request({
      url: '/auth/verify-old-password',
      method: 'post',
      data,
      encrypt: true // 敏感数据加密
    })
  },

  // 检查用户名是否可用
  checkUsernameAvailability(username) {
    return request({
      url: '/auth/check-username',
      method: 'get',
      params: { username }
    })
  },

  // 检查邮箱是否可用
  checkEmailAvailability(email) {
    return request({
      url: '/auth/check-email',
      method: 'get',
      params: { email }
    })
  },

  // 检查手机号是否可用
  checkPhoneAvailability(phone) {
    return request({
      url: '/auth/check-phone',
      method: 'get',
      params: { phone }
    })
  },

  // 绑定邮箱
  bindEmail(data) {
    return request({
      url: '/auth/bind-email',
      method: 'post',
      data
    })
  },

  // 解绑邮箱
  unbindEmail() {
    return request({
      url: '/auth/unbind-email',
      method: 'post'
    })
  },

  // 绑定手机号
  bindPhone(data) {
    return request({
      url: '/auth/bind-phone',
      method: 'post',
      data
    })
  },

  // 解绑手机号
  unbindPhone() {
    return request({
      url: '/auth/unbind-phone',
      method: 'post'
    })
  },

  // 启用两步验证
  enableTwoFactorAuth() {
    return request({
      url: '/auth/enable-2fa',
      method: 'post'
    })
  },

  // 禁用两步验证
  disableTwoFactorAuth(data) {
    return request({
      url: '/auth/disable-2fa',
      method: 'post',
      data
    })
  },

  // 生成两步验证二维码
  generateTwoFactorQR() {
    return request({
      url: '/auth/generate-2fa-qr',
      method: 'get'
    })
  },

  // 验证两步验证码
  verifyTwoFactorCode(data) {
    return request({
      url: '/auth/verify-2fa-code',
      method: 'post',
      data
    })
  },

  // 获取备用验证码
  getBackupCodes() {
    return request({
      url: '/auth/backup-codes',
      method: 'get'
    })
  },

  // 重新生成备用验证码
  regenerateBackupCodes() {
    return request({
      url: '/auth/regenerate-backup-codes',
      method: 'post'
    })
  },

  // 获取登录历史
  getLoginHistory(params = {}) {
    return request({
      url: '/auth/login-history',
      method: 'get',
      params
    })
  },

  // 获取活跃会话
  getActiveSessions() {
    return request({
      url: '/auth/active-sessions',
      method: 'get'
    })
  },

  // 终止指定会话
  terminateSession(sessionId) {
    return request({
      url: `/auth/sessions/${sessionId}/terminate`,
      method: 'post'
    })
  },

  // 终止所有其他会话
  terminateAllOtherSessions() {
    return request({
      url: '/auth/terminate-all-sessions',
      method: 'post'
    })
  },

  // 账户锁定/解锁
  lockAccount() {
    return request({
      url: '/auth/lock-account',
      method: 'post'
    })
  },

  unlockAccount(data) {
    return request({
      url: '/auth/unlock-account',
      method: 'post',
      data
    })
  },

  // 账户注销申请
  requestAccountDeletion(data) {
    return request({
      url: '/auth/request-account-deletion',
      method: 'post',
      data
    })
  },

  // 取消账户注销申请
  cancelAccountDeletion() {
    return request({
      url: '/auth/cancel-account-deletion',
      method: 'post'
    })
  },

  // 获取用户权限
  getUserPermissions() {
    return request({
      url: '/auth/permissions',
      method: 'get'
    })
  },

  // 获取用户角色
  getUserRoles() {
    return request({
      url: '/auth/roles',
      method: 'get'
    })
  },

  // 检查权限
  checkPermission(permission) {
    return request({
      url: '/auth/check-permission',
      method: 'get',
      params: { permission }
    })
  },

  // 获取安全设置
  getSecuritySettings() {
    return request({
      url: '/auth/security-settings',
      method: 'get'
    })
  },

  // 更新安全设置
  updateSecuritySettings(data) {
    return request({
      url: '/auth/security-settings',
      method: 'put',
      data
    })
  },

  // 获取隐私设置
  getPrivacySettings() {
    return request({
      url: '/auth/privacy-settings',
      method: 'get'
    })
  },

  // 更新隐私设置
  updatePrivacySettings(data) {
    return request({
      url: '/auth/privacy-settings',
      method: 'put',
      data
    })
  },

  // OAuth登录 - 第三方登录
  oauthLogin(provider, data) {
    return request({
      url: `/auth/oauth/${provider}`,
      method: 'post',
      data
    })
  },

  // 绑定第三方账号
  bindOauthAccount(provider, data) {
    return request({
      url: `/auth/bind-oauth/${provider}`,
      method: 'post',
      data
    })
  },

  // 解绑第三方账号
  unbindOauthAccount(provider) {
    return request({
      url: `/auth/unbind-oauth/${provider}`,
      method: 'post'
    })
  },

  // 获取绑定的第三方账号
  getBoundOauthAccounts() {
    return request({
      url: '/auth/bound-oauth-accounts',
      method: 'get'
    })
  },

  // 单点登录 (SSO)
  ssoLogin(data) {
    return request({
      url: '/auth/sso-login',
      method: 'post',
      data
    })
  },

  // 获取SSO配置
  getSSOConfig() {
    return request({
      url: '/auth/sso-config',
      method: 'get'
    })
  },

  // 账户激活
  activateAccount(data) {
    return request({
      url: '/auth/activate-account',
      method: 'post',
      data
    })
  },

  // 重新发送激活邮件
  resendActivationEmail(email) {
    return request({
      url: '/auth/resend-activation',
      method: 'post',
      data: { email }
    })
  },

  // 验证token有效性
  validateToken(token) {
    return request({
      url: '/auth/validate-token',
      method: 'post',
      data: { token }
    })
  },

  // 获取用户偏好设置
  getUserPreferences() {
    return request({
      url: '/auth/preferences',
      method: 'get'
    })
  },

  // 更新用户偏好设置
  updateUserPreferences(data) {
    return request({
      url: '/auth/preferences',
      method: 'put',
      data
    })
  },

  // 获取通知设置
  getNotificationSettings() {
    return request({
      url: '/auth/notification-settings',
      method: 'get'
    })
  },

  // 更新通知设置
  updateNotificationSettings(data) {
    return request({
      url: '/auth/notification-settings',
      method: 'put',
      data
    })
  },

  // 导出用户数据
  exportUserData(format = 'json') {
    return request({
      url: '/auth/export-data',
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // 数据迁移申请
  requestDataMigration(data) {
    return request({
      url: '/auth/request-data-migration',
      method: 'post',
      data
    })
  }
}

export default authApi