import request from './request'

/**
 * 权限管理API
 * 基于后端 PermissionController、RoleController 等接口实现
 */
export const permissionApi = {
  // ==================== 权限基础管理 ====================
  
  /**
   * 获取权限列表
   * 对应后端: GET /api/v1/permissions
   */
  getPermissionList(params = {}) {
    return request({
      url: '/v1/permissions',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取权限详情
   * 对应后端: GET /api/v1/permissions/{id}
   */
  getPermissionById(id) {
    return request({
      url: `/v1/permissions/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建权限
   * 对应后端: POST /api/v1/permissions
   */
  createPermission(data) {
    return request({
      url: '/v1/permissions',
      method: 'post',
      data
    })
  },

  /**
   * 更新权限信息
   * 对应后端: PUT /api/v1/permissions/{id}
   */
  updatePermission(id, data) {
    return request({
      url: `/v1/permissions/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除权限
   * 对应后端: DELETE /api/v1/permissions/{id}
   */
  deletePermission(id) {
    return request({
      url: `/v1/permissions/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除权限
   * 对应后端: DELETE /api/v1/permissions/batch
   */
  batchDeletePermissions(permissionIds) {
    return request({
      url: '/v1/permissions/batch',
      method: 'delete',
      data: { permissionIds }
    })
  },

  /**
   * 获取权限树结构
   * 对应后端: GET /api/v1/permissions/tree
   */
  getPermissionTree(params = {}) {
    return request({
      url: '/v1/permissions/tree',
      method: 'get',
      params
    })
  },

  // ==================== 角色管理 ====================
  
  /**
   * 获取角色列表
   * 对应后端: GET /api/v1/roles
   */
  getRoleList(params = {}) {
    return request({
      url: '/v1/roles',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取角色详情
   * 对应后端: GET /api/v1/roles/{id}
   */
  getRoleById(id) {
    return request({
      url: `/v1/roles/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建角色
   * 对应后端: POST /api/v1/roles
   */
  createRole(data) {
    return request({
      url: '/v1/roles',
      method: 'post',
      data
    })
  },

  /**
   * 更新角色信息
   * 对应后端: PUT /api/v1/roles/{id}
   */
  updateRole(id, data) {
    return request({
      url: `/v1/roles/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除角色
   * 对应后端: DELETE /api/v1/roles/{id}
   */
  deleteRole(id) {
    return request({
      url: `/v1/roles/${id}`,
      method: 'delete'
    })
  },

  /**
   * 获取角色统计信息
   * 对应后端: GET /api/v1/roles/statistics
   */
  getRoleStatistics(params = {}) {
    return request({
      url: '/v1/roles/statistics',
      method: 'get',
      params
    })
  },

  // ==================== 角色权限关联 ====================
  
  /**
   * 获取角色权限列表
   * 对应后端: GET /api/v1/roles/{id}/permissions
   */
  getRolePermissions(roleId) {
    return request({
      url: `/v1/roles/${roleId}/permissions`,
      method: 'get'
    })
  },

  /**
   * 为角色分配权限
   * 对应后端: POST /api/v1/roles/{id}/permissions
   */
  assignPermissionsToRole(roleId, permissionIds) {
    return request({
      url: `/v1/roles/${roleId}/permissions`,
      method: 'post',
      data: { permissionIds }
    })
  },

  /**
   * 移除角色权限
   * 对应后端: DELETE /api/v1/roles/{id}/permissions
   */
  removePermissionsFromRole(roleId, permissionIds) {
    return request({
      url: `/v1/roles/${roleId}/permissions`,
      method: 'delete',
      data: { permissionIds }
    })
  },

  // ==================== 用户角色管理 ====================
  
  /**
   * 获取用户角色列表
   * 对应后端: GET /api/v1/users/{userId}/roles
   */
  getUserRoles(userId) {
    return request({
      url: `/users/${userId}/roles`,
      method: 'get'
    })
  },

  /**
   * 为用户分配角色
   * 对应后端: POST /api/v1/users/{userId}/roles
   */
  assignRolesToUser(userId, roleIds) {
    return request({
      url: `/users/${userId}/roles`,
      method: 'post',
      data: { roleIds }
    })
  },

  /**
   * 移除用户角色
   * 对应后端: DELETE /api/v1/users/{userId}/roles
   */
  removeRolesFromUser(userId, roleIds) {
    return request({
      url: `/users/${userId}/roles`,
      method: 'delete',
      data: { roleIds }
    })
  },

  /**
   * 获取用户权限列表
   * 对应后端: GET /api/v1/users/{userId}/permissions
   */
  getUserPermissions(userId) {
    return request({
      url: `/users/${userId}/permissions`,
      method: 'get'
    })
  },

  // ==================== 权限检查 ====================
  
  /**
   * 检查用户权限
   * 对应后端: POST /api/v1/auth/check-permission
   */
  checkUserPermission(permission, resource = null) {
    return request({
      url: '/auth/check-permission',
      method: 'post',
      data: { permission, resource }
    })
  },

  /**
   * 批量检查用户权限
   * 对应后端: POST /api/v1/auth/check-permissions
   */
  checkUserPermissions(permissions) {
    return request({
      url: '/auth/check-permissions',
      method: 'post',
      data: { permissions }
    })
  },

  /**
   * 获取当前用户菜单权限
   * 对应后端: GET /api/v1/auth/menu-permissions
   */
  getCurrentUserMenuPermissions() {
    return request({
      url: '/auth/menu-permissions',
      method: 'get'
    })
  },

  /**
   * 获取当前用户操作权限
   * 对应后端: GET /api/v1/auth/operation-permissions
   */
  getCurrentUserOperationPermissions() {
    return request({
      url: '/auth/operation-permissions',
      method: 'get'
    })
  },

  // ==================== 权限日志 ====================
  
  /**
   * 获取权限操作日志
   * 对应后端: GET /api/v1/permissions/logs
   */
  getPermissionLogs(params = {}) {
    return request({
      url: '/v1/permissions/logs',
      method: 'get',
      params
    })
  },

  /**
   * 记录权限操作日志
   * 对应后端: POST /api/v1/permissions/logs
   */
  logPermissionOperation(data) {
    return request({
      url: '/v1/permissions/logs',
      method: 'post',
      data
    })
  },

  // ==================== 数据权限 ====================
  
  /**
   * 获取数据权限配置
   * 对应后端: GET /api/v1/data-permissions
   */
  getDataPermissions(params = {}) {
    return request({
      url: '/v1/data-permissions',
      method: 'get',
      params
    })
  },

  /**
   * 设置数据权限
   * 对应后端: POST /api/v1/data-permissions
   */
  setDataPermission(data) {
    return request({
      url: '/v1/data-permissions',
      method: 'post',
      data
    })
  },

  /**
   * 更新数据权限
   * 对应后端: PUT /api/v1/data-permissions/{id}
   */
  updateDataPermission(id, data) {
    return request({
      url: `/v1/data-permissions/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除数据权限
   * 对应后端: DELETE /api/v1/data-permissions/{id}
   */
  deleteDataPermission(id) {
    return request({
      url: `/v1/data-permissions/${id}`,
      method: 'delete'
    })
  }
}

export default permissionApi
