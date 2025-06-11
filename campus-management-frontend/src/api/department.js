import request from './request'

/**
 * 院系管理API
 * 基于后端 DepartmentController 等接口实现
 */
export const departmentApi = {
  // ==================== 院系基础管理 ====================
  
  /**
   * 获取院系列表
   * 对应后端: GET /api/v1/departments
   */
  getDepartmentList(params = {}) {
    return request({
      url: '/v1/departments',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取院系详情
   * 对应后端: GET /api/v1/departments/{id}
   */
  getDepartmentById(id) {
    return request({
      url: `/v1/departments/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建院系
   * 对应后端: POST /api/v1/departments
   */
  createDepartment(data) {
    return request({
      url: '/v1/departments',
      method: 'post',
      data
    })
  },

  /**
   * 更新院系信息
   * 对应后端: PUT /api/v1/departments/{id}
   */
  updateDepartment(id, data) {
    return request({
      url: `/v1/departments/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除院系
   * 对应后端: DELETE /api/v1/departments/{id}
   */
  deleteDepartment(id) {
    return request({
      url: `/v1/departments/${id}`,
      method: 'delete'
    })
  },

  /**
   * 启用院系
   * 对应后端: PUT /api/v1/departments/{id}/enable
   */
  enableDepartment(id) {
    return request({
      url: `/v1/departments/${id}/enable`,
      method: 'put'
    })
  },

  /**
   * 禁用院系
   * 对应后端: PUT /api/v1/departments/{id}/disable
   */
  disableDepartment(id) {
    return request({
      url: `/v1/departments/${id}/disable`,
      method: 'put'
    })
  },

  /**
   * 批量更新院系状态
   * 对应后端: PUT /api/v1/departments/batch/status
   */
  batchUpdateDepartmentStatus(data) {
    return request({
      url: '/v1/departments/batch/status',
      method: 'put',
      data
    })
  },

  /**
   * 获取院系统计信息
   * 对应后端: GET /api/v1/departments/statistics
   */
  getDepartmentStatistics(params = {}) {
    return request({
      url: '/v1/departments/statistics',
      method: 'get',
      params
    })
  },

  // ==================== 院系层级管理 ====================
  
  /**
   * 获取院系树结构
   * 对应后端: GET /api/v1/departments/tree
   */
  getDepartmentTree(params = {}) {
    return request({
      url: '/v1/departments/tree',
      method: 'get',
      params
    })
  },

  /**
   * 获取子院系列表
   * 对应后端: GET /api/v1/departments/{id}/children
   */
  getChildDepartments(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/children`,
      method: 'get',
      params
    })
  },

  /**
   * 获取父院系信息
   * 对应后端: GET /api/v1/departments/{id}/parent
   */
  getParentDepartment(id) {
    return request({
      url: `/v1/departments/${id}/parent`,
      method: 'get'
    })
  },

  /**
   * 移动院系到新的父级
   * 对应后端: PUT /api/v1/departments/{id}/move
   */
  moveDepartment(id, parentId) {
    return request({
      url: `/v1/departments/${id}/move`,
      method: 'put',
      data: { parentId }
    })
  },

  // ==================== 院系人员管理 ====================
  
  /**
   * 获取院系教师列表
   * 对应后端: GET /api/v1/departments/{id}/teachers
   */
  getDepartmentTeachers(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/teachers`,
      method: 'get',
      params
    })
  },

  /**
   * 获取院系学生列表
   * 对应后端: GET /api/v1/departments/{id}/students
   */
  getDepartmentStudents(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/students`,
      method: 'get',
      params
    })
  },

  /**
   * 添加教师到院系
   * 对应后端: POST /api/v1/departments/{id}/teachers
   */
  addTeacherToDepartment(id, teacherId) {
    return request({
      url: `/v1/departments/${id}/teachers`,
      method: 'post',
      data: { teacherId }
    })
  },

  /**
   * 从院系移除教师
   * 对应后端: DELETE /api/v1/departments/{id}/teachers/{teacherId}
   */
  removeTeacherFromDepartment(id, teacherId) {
    return request({
      url: `/v1/departments/${id}/teachers/${teacherId}`,
      method: 'delete'
    })
  },

  /**
   * 批量添加教师到院系
   * 对应后端: POST /api/v1/departments/{id}/teachers/batch
   */
  batchAddTeachersToDepartment(id, teacherIds) {
    return request({
      url: `/v1/departments/${id}/teachers/batch`,
      method: 'post',
      data: { teacherIds }
    })
  },

  /**
   * 转移教师到其他院系
   * 对应后端: PUT /api/v1/departments/{id}/teachers/{teacherId}/transfer
   */
  transferTeacher(id, teacherId, targetDepartmentId) {
    return request({
      url: `/v1/departments/${id}/teachers/${teacherId}/transfer`,
      method: 'put',
      data: { targetDepartmentId }
    })
  },

  // ==================== 院系课程管理 ====================
  
  /**
   * 获取院系课程列表
   * 对应后端: GET /api/v1/departments/{id}/courses
   */
  getDepartmentCourses(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/courses`,
      method: 'get',
      params
    })
  },

  /**
   * 为院系分配课程
   * 对应后端: POST /api/v1/departments/{id}/courses
   */
  assignCourseToDepartment(id, courseId) {
    return request({
      url: `/v1/departments/${id}/courses`,
      method: 'post',
      data: { courseId }
    })
  },

  /**
   * 取消院系课程分配
   * 对应后端: DELETE /api/v1/departments/{id}/courses/{courseId}
   */
  unassignCourseFromDepartment(id, courseId) {
    return request({
      url: `/v1/departments/${id}/courses/${courseId}`,
      method: 'delete'
    })
  },

  // ==================== 院系专业管理 ====================
  
  /**
   * 获取院系专业列表
   * 对应后端: GET /api/v1/departments/{id}/majors
   */
  getDepartmentMajors(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/majors`,
      method: 'get',
      params
    })
  },

  /**
   * 创建院系专业
   * 对应后端: POST /api/v1/departments/{id}/majors
   */
  createDepartmentMajor(id, data) {
    return request({
      url: `/v1/departments/${id}/majors`,
      method: 'post',
      data
    })
  },

  /**
   * 更新院系专业
   * 对应后端: PUT /api/v1/departments/{id}/majors/{majorId}
   */
  updateDepartmentMajor(id, majorId, data) {
    return request({
      url: `/v1/departments/${id}/majors/${majorId}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除院系专业
   * 对应后端: DELETE /api/v1/departments/{id}/majors/{majorId}
   */
  deleteDepartmentMajor(id, majorId) {
    return request({
      url: `/v1/departments/${id}/majors/${majorId}`,
      method: 'delete'
    })
  },

  // ==================== 院系班级管理 ====================
  
  /**
   * 获取院系班级列表
   * 对应后端: GET /api/v1/departments/{id}/classes
   */
  getDepartmentClasses(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/classes`,
      method: 'get',
      params
    })
  },

  /**
   * 创建院系班级
   * 对应后端: POST /api/v1/departments/{id}/classes
   */
  createDepartmentClass(id, data) {
    return request({
      url: `/v1/departments/${id}/classes`,
      method: 'post',
      data
    })
  },

  // ==================== 院系统计分析 ====================
  
  /**
   * 获取院系人员统计
   * 对应后端: GET /api/v1/departments/{id}/statistics/personnel
   */
  getDepartmentPersonnelStatistics(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/statistics/personnel`,
      method: 'get',
      params
    })
  },

  /**
   * 获取院系课程统计
   * 对应后端: GET /api/v1/departments/{id}/statistics/courses
   */
  getDepartmentCourseStatistics(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/statistics/courses`,
      method: 'get',
      params
    })
  },

  /**
   * 获取院系成绩统计
   * 对应后端: GET /api/v1/departments/{id}/statistics/grades
   */
  getDepartmentGradeStatistics(id, params = {}) {
    return request({
      url: `/v1/departments/${id}/statistics/grades`,
      method: 'get',
      params
    })
  },

  // ==================== 数据导出 ====================
  
  /**
   * 导出院系信息
   * 对应后端: GET /api/v1/departments/{id}/export
   */
  exportDepartmentInfo(id, format = 'excel') {
    return request({
      url: `/v1/departments/${id}/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  /**
   * 导出院系教师名单
   * 对应后端: GET /api/v1/departments/{id}/teachers/export
   */
  exportDepartmentTeachers(id, format = 'excel') {
    return request({
      url: `/v1/departments/${id}/teachers/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  /**
   * 导出院系学生名单
   * 对应后端: GET /api/v1/departments/{id}/students/export
   */
  exportDepartmentStudents(id, format = 'excel') {
    return request({
      url: `/v1/departments/${id}/students/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  }
}

export default departmentApi
