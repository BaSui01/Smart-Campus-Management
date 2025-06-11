import request from './request'

/**
 * 班级管理API
 * 基于后端 SchoolClassController 等接口实现
 */
export const classApi = {
  // ==================== 班级基础管理 ====================
  
  /**
   * 获取班级列表
   * 对应后端: GET /api/v1/classes
   */
  getClassList(params = {}) {
    return request({
      url: '/classes',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取班级详情
   * 对应后端: GET /api/v1/classes/{id}
   */
  getClassById(id) {
    return request({
      url: `/classes/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建班级
   * 对应后端: POST /api/v1/classes
   */
  createClass(data) {
    return request({
      url: '/classes',
      method: 'post',
      data
    })
  },

  /**
   * 更新班级信息
   * 对应后端: PUT /api/v1/classes/{id}
   */
  updateClass(id, data) {
    return request({
      url: `/classes/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除班级
   * 对应后端: DELETE /api/v1/classes/{id}
   */
  deleteClass(id) {
    return request({
      url: `/classes/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除班级
   * 对应后端: DELETE /api/v1/classes/batch
   */
  batchDeleteClasses(classIds) {
    return request({
      url: '/classes/batch',
      method: 'delete',
      data: { classIds }
    })
  },

  /**
   * 获取班级统计信息
   * 对应后端: GET /api/v1/classes/statistics
   */
  getClassStatistics(params = {}) {
    return request({
      url: '/classes/statistics',
      method: 'get',
      params
    })
  },

  // ==================== 班级学生管理 ====================
  
  /**
   * 获取班级学生列表
   * 对应后端: GET /api/v1/classes/{id}/students
   */
  getClassStudents(classId, params = {}) {
    return request({
      url: `/classes/${classId}/students`,
      method: 'get',
      params
    })
  },

  /**
   * 添加学生到班级
   * 对应后端: POST /api/v1/classes/{id}/students
   */
  addStudentToClass(classId, studentId) {
    return request({
      url: `/classes/${classId}/students`,
      method: 'post',
      data: { studentId }
    })
  },

  /**
   * 从班级移除学生
   * 对应后端: DELETE /api/v1/classes/{id}/students/{studentId}
   */
  removeStudentFromClass(classId, studentId) {
    return request({
      url: `/classes/${classId}/students/${studentId}`,
      method: 'delete'
    })
  },

  /**
   * 批量添加学生到班级
   * 对应后端: POST /api/v1/classes/{id}/students/batch
   */
  batchAddStudentsToClass(classId, studentIds) {
    return request({
      url: `/classes/${classId}/students/batch`,
      method: 'post',
      data: { studentIds }
    })
  },

  /**
   * 转移学生到其他班级
   * 对应后端: PUT /api/v1/classes/{id}/students/{studentId}/transfer
   */
  transferStudent(classId, studentId, targetClassId) {
    return request({
      url: `/classes/${classId}/students/${studentId}/transfer`,
      method: 'put',
      data: { targetClassId }
    })
  },

  // ==================== 班级课程管理 ====================
  
  /**
   * 获取班级课程列表
   * 对应后端: GET /api/v1/classes/{id}/courses
   */
  getClassCourses(classId, params = {}) {
    return request({
      url: `/classes/${classId}/courses`,
      method: 'get',
      params
    })
  },

  /**
   * 为班级分配课程
   * 对应后端: POST /api/v1/classes/{id}/courses
   */
  assignCourseToClass(classId, courseId) {
    return request({
      url: `/classes/${classId}/courses`,
      method: 'post',
      data: { courseId }
    })
  },

  /**
   * 取消班级课程分配
   * 对应后端: DELETE /api/v1/classes/{id}/courses/{courseId}
   */
  unassignCourseFromClass(classId, courseId) {
    return request({
      url: `/classes/${classId}/courses/${courseId}`,
      method: 'delete'
    })
  },

  /**
   * 获取班级课程表
   * 对应后端: GET /api/v1/classes/{id}/schedule
   */
  getClassSchedule(classId, params = {}) {
    return request({
      url: `/classes/${classId}/schedule`,
      method: 'get',
      params
    })
  },

  // ==================== 班级考勤管理 ====================
  
  /**
   * 获取班级考勤统计
   * 对应后端: GET /api/v1/classes/{id}/attendance/statistics
   */
  getClassAttendanceStatistics(classId, params = {}) {
    return request({
      url: `/classes/${classId}/attendance/statistics`,
      method: 'get',
      params
    })
  },

  /**
   * 获取班级考勤记录
   * 对应后端: GET /api/v1/classes/{id}/attendance
   */
  getClassAttendanceRecords(classId, params = {}) {
    return request({
      url: `/classes/${classId}/attendance`,
      method: 'get',
      params
    })
  },

  // ==================== 班级成绩管理 ====================
  
  /**
   * 获取班级成绩统计
   * 对应后端: GET /api/v1/classes/{id}/grades/statistics
   */
  getClassGradeStatistics(classId, params = {}) {
    return request({
      url: `/classes/${classId}/grades/statistics`,
      method: 'get',
      params
    })
  },

  /**
   * 获取班级成绩排名
   * 对应后端: GET /api/v1/classes/{id}/grades/ranking
   */
  getClassGradeRanking(classId, params = {}) {
    return request({
      url: `/classes/${classId}/grades/ranking`,
      method: 'get',
      params
    })
  },

  // ==================== 班级活动管理 ====================
  
  /**
   * 获取班级活动列表
   * 对应后端: GET /api/v1/classes/{id}/activities
   */
  getClassActivities(classId, params = {}) {
    return request({
      url: `/classes/${classId}/activities`,
      method: 'get',
      params
    })
  },

  /**
   * 创建班级活动
   * 对应后端: POST /api/v1/classes/{id}/activities
   */
  createClassActivity(classId, data) {
    return request({
      url: `/classes/${classId}/activities`,
      method: 'post',
      data
    })
  },

  /**
   * 更新班级活动
   * 对应后端: PUT /api/v1/classes/{id}/activities/{activityId}
   */
  updateClassActivity(classId, activityId, data) {
    return request({
      url: `/classes/${classId}/activities/${activityId}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除班级活动
   * 对应后端: DELETE /api/v1/classes/{id}/activities/{activityId}
   */
  deleteClassActivity(classId, activityId) {
    return request({
      url: `/classes/${classId}/activities/${activityId}`,
      method: 'delete'
    })
  },

  // ==================== 班级通知管理 ====================
  
  /**
   * 获取班级通知列表
   * 对应后端: GET /api/v1/classes/{id}/notifications
   */
  getClassNotifications(classId, params = {}) {
    return request({
      url: `/classes/${classId}/notifications`,
      method: 'get',
      params
    })
  },

  /**
   * 发送班级通知
   * 对应后端: POST /api/v1/classes/{id}/notifications
   */
  sendClassNotification(classId, data) {
    return request({
      url: `/classes/${classId}/notifications`,
      method: 'post',
      data
    })
  },

  // ==================== 数据导出 ====================
  
  /**
   * 导出班级学生名单
   * 对应后端: GET /api/v1/classes/{id}/students/export
   */
  exportClassStudents(classId, format = 'excel') {
    return request({
      url: `/classes/${classId}/students/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  /**
   * 导出班级成绩
   * 对应后端: GET /api/v1/classes/{id}/grades/export
   */
  exportClassGrades(classId, format = 'excel') {
    return request({
      url: `/classes/${classId}/grades/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  }
}

export default classApi
