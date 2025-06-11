import request from './request'

/**
 * 系统管理API
 * 基于后端 SystemConfigController、SystemController 等接口实现
 */
export const systemApi = {
  // ==================== 系统配置管理 ====================
  
  /**
   * 获取系统配置列表
   * 对应后端: GET /api/v1/system-configs
   */
  getSystemConfigList(params = {}) {
    return request({
      url: '/v1/system-configs',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取系统配置详情
   * 对应后端: GET /api/v1/system-configs/{id}
   */
  getSystemConfigById(id) {
    return request({
      url: `/v1/system-configs/${id}`,
      method: 'get'
    })
  },

  /**
   * 根据配置键获取配置值
   * 对应后端: GET /api/v1/system-configs/key/{key}
   */
  getSystemConfigByKey(key) {
    return request({
      url: `/v1/system-configs/key/${key}`,
      method: 'get'
    })
  },

  /**
   * 创建系统配置
   * 对应后端: POST /api/v1/system-configs
   */
  createSystemConfig(data) {
    return request({
      url: '/v1/system-configs',
      method: 'post',
      data
    })
  },

  /**
   * 更新系统配置
   * 对应后端: PUT /api/v1/system-configs/{id}
   */
  updateSystemConfig(id, data) {
    return request({
      url: `/v1/system-configs/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除系统配置
   * 对应后端: DELETE /api/v1/system-configs/{id}
   */
  deleteSystemConfig(id) {
    return request({
      url: `/v1/system-configs/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量更新配置
   * 对应后端: PUT /api/v1/system-configs/batch
   */
  batchUpdateConfigs(configs) {
    return request({
      url: '/v1/system-configs/batch',
      method: 'put',
      data: configs
    })
  },

  /**
   * 批量删除系统配置
   * 对应后端: DELETE /api/v1/system-configs/batch
   */
  batchDeleteConfigs(configIds) {
    return request({
      url: '/v1/system-configs/batch',
      method: 'delete',
      data: configIds
    })
  },

  // ==================== 系统信息管理 ====================
  
  /**
   * 获取系统信息
   * 对应后端: GET /api/v1/system/info
   */
  getSystemInfo() {
    return request({
      url: '/system/info',
      method: 'get'
    })
  },

  /**
   * 获取系统状态
   * 对应后端: GET /api/v1/system/status
   */
  getSystemStatus() {
    return request({
      url: '/system/status',
      method: 'get'
    })
  },

  /**
   * 获取系统健康检查
   * 对应后端: GET /api/v1/system/health
   */
  getSystemHealth() {
    return request({
      url: '/system/health',
      method: 'get'
    })
  },

  /**
   * 获取系统版本信息
   * 对应后端: GET /api/v1/system/version
   */
  getSystemVersion() {
    return request({
      url: '/system/version',
      method: 'get'
    })
  },

  /**
   * 获取系统统计信息
   * 对应后端: GET /api/v1/system/statistics
   */
  getSystemStatistics() {
    return request({
      url: '/system/statistics',
      method: 'get'
    })
  },

  // ==================== 系统监控 ====================
  
  /**
   * 获取系统性能监控数据
   * 对应后端: GET /api/v1/system/monitor/performance
   */
  getSystemPerformance(params = {}) {
    return request({
      url: '/system/monitor/performance',
      method: 'get',
      params
    })
  },

  /**
   * 获取系统资源使用情况
   * 对应后端: GET /api/v1/system/monitor/resources
   */
  getSystemResources() {
    return request({
      url: '/system/monitor/resources',
      method: 'get'
    })
  },

  /**
   * 获取系统日志
   * 对应后端: GET /api/v1/system/logs
   */
  getSystemLogs(params = {}) {
    return request({
      url: '/system/logs',
      method: 'get',
      params
    })
  },

  /**
   * 清理系统日志
   * 对应后端: DELETE /api/v1/system/logs/clean
   */
  cleanSystemLogs(params = {}) {
    return request({
      url: '/system/logs/clean',
      method: 'delete',
      params
    })
  },

  // ==================== 缓存管理 ====================
  
  /**
   * 获取缓存信息
   * 对应后端: GET /api/v1/cache/info
   */
  getCacheInfo() {
    return request({
      url: '/v1/cache/info',
      method: 'get'
    })
  },

  /**
   * 清理所有缓存
   * 对应后端: DELETE /api/v1/cache/clear-all
   */
  clearAllCache() {
    return request({
      url: '/v1/cache/clear-all',
      method: 'delete'
    })
  },

  /**
   * 清理指定缓存
   * 对应后端: DELETE /api/v1/cache/clear/{key}
   */
  clearCacheByKey(key) {
    return request({
      url: `/v1/cache/clear/${key}`,
      method: 'delete'
    })
  },

  /**
   * 预热缓存
   * 对应后端: POST /api/v1/cache/warm-up
   */
  warmUpCache(data = {}) {
    return request({
      url: '/v1/cache/warm-up',
      method: 'post',
      data
    })
  },

  // ==================== 数据库管理 ====================
  
  /**
   * 获取数据库连接状态
   * 对应后端: GET /api/v1/system/database/status
   */
  getDatabaseStatus() {
    return request({
      url: '/system/database/status',
      method: 'get'
    })
  },

  /**
   * 执行数据库备份
   * 对应后端: POST /api/v1/system/database/backup
   */
  backupDatabase(data = {}) {
    return request({
      url: '/system/database/backup',
      method: 'post',
      data
    })
  },

  /**
   * 获取数据库备份列表
   * 对应后端: GET /api/v1/system/database/backups
   */
  getDatabaseBackups(params = {}) {
    return request({
      url: '/system/database/backups',
      method: 'get',
      params
    })
  },

  /**
   * 恢复数据库
   * 对应后端: POST /api/v1/system/database/restore
   */
  restoreDatabase(backupId) {
    return request({
      url: '/system/database/restore',
      method: 'post',
      data: { backupId }
    })
  },

  // ==================== 系统维护 ====================
  
  /**
   * 系统重启
   * 对应后端: POST /api/v1/system/restart
   */
  restartSystem() {
    return request({
      url: '/system/restart',
      method: 'post'
    })
  },

  /**
   * 系统关闭
   * 对应后端: POST /api/v1/system/shutdown
   */
  shutdownSystem() {
    return request({
      url: '/system/shutdown',
      method: 'post'
    })
  },

  /**
   * 进入维护模式
   * 对应后端: POST /api/v1/system/maintenance/enable
   */
  enableMaintenanceMode(data = {}) {
    return request({
      url: '/system/maintenance/enable',
      method: 'post',
      data
    })
  },

  /**
   * 退出维护模式
   * 对应后端: POST /api/v1/system/maintenance/disable
   */
  disableMaintenanceMode() {
    return request({
      url: '/system/maintenance/disable',
      method: 'post'
    })
  },

  // ==================== 系统更新 ====================
  
  /**
   * 检查系统更新
   * 对应后端: GET /api/v1/system/update/check
   */
  checkSystemUpdate() {
    return request({
      url: '/system/update/check',
      method: 'get'
    })
  },

  /**
   * 执行系统更新
   * 对应后端: POST /api/v1/system/update/execute
   */
  executeSystemUpdate(data = {}) {
    return request({
      url: '/system/update/execute',
      method: 'post',
      data
    })
  },

  /**
   * 获取更新历史
   * 对应后端: GET /api/v1/system/update/history
   */
  getUpdateHistory(params = {}) {
    return request({
      url: '/system/update/history',
      method: 'get',
      params
    })
  }
}

export default systemApi
