import adminRequest from './adminRequest'

const adminAuthApi = {
  // 管理员登录
  login(loginData) {
    return adminRequest.post('/auth/login', loginData)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return adminRequest.get('/auth/user')
  },

  // 获取Token状态
  getTokenStatus() {
    return adminRequest.get('/auth/token-status')
  },

  // 检查登录状态
  checkLoginStatus() {
    return adminRequest.get('/auth/status')
  },

  // 系统健康检查
  healthCheck() {
    return adminRequest.get('/health')
  },

  // 退出登录
  logout() {
    return adminRequest.post('/auth/logout')
  },

  // 刷新Token
  refreshToken() {
    return adminRequest.post('/auth/refresh')
  },

  // 角色相关API
  getRoles(params = {}) {
    return adminRequest.get('/roles', { params })
  },

  getRoleById(id) {
    return adminRequest.get(`/roles/${id}`)
  },

  createRole(roleData) {
    return adminRequest.post('/roles', roleData)
  },

  updateRole(id, roleData) {
    return adminRequest.put(`/roles/${id}`, roleData)
  },

  deleteRole(id) {
    return adminRequest.delete(`/roles/${id}`)
  },

  getRoleStatistics() {
    return adminRequest.get('/roles/statistics')
  }
}

export { adminAuthApi }
export default adminAuthApi