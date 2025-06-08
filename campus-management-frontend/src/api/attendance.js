import request from './request'

/**
 * 考勤管理API
 */
export const attendanceApi = {
  // ==================== 基础CRUD操作 ====================

  /**
   * 分页查询考勤记录列表
   */
  getAttendanceRecords(params = {}) {
    return request({
      url: '/api/v1/attendance',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        sortBy: 'attendanceDate',
        sortDir: 'desc',
        ...params
      }
    })
  },

  /**
   * 根据ID查询考勤记录详情
   */
  getAttendanceRecordById(id) {
    return request({
      url: `/api/v1/attendance/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建考勤记录
   */
  createAttendanceRecord(data) {
    return request({
      url: '/api/v1/attendance',
      method: 'post',
      data
    })
  },

  /**
   * 更新考勤记录
   */
  updateAttendanceRecord(id, data) {
    return request({
      url: `/api/v1/attendance/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除考勤记录
   */
  deleteAttendanceRecord(id) {
    return request({
      url: `/api/v1/attendance/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除考勤记录
   */
  deleteAttendanceRecords(ids) {
    return request({
      url: '/api/v1/attendance/batch',
      method: 'delete',
      data: ids
    })
  },

  // ==================== 学生端操作 ====================

  /**
   * 学生签到
   */
  checkIn(data) {
    return request({
      url: '/api/v1/attendance/checkin',
      method: 'post',
      params: {
        studentId: data.studentId,
        courseId: data.courseId,
        location: data.location
      }
    })
  },

  /**
   * 学生签退
   */
  checkOut(data) {
    return request({
      url: '/api/v1/attendance/checkout',
      method: 'post',
      params: {
        studentId: data.studentId,
        courseId: data.courseId
      }
    })
  },

  /**
   * 获取学生考勤记录
   */
  getStudentAttendance(studentId, params = {}) {
    return request({
      url: `/api/v1/attendance/student/${studentId}`,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 获取学生今日考勤状态
   */
  getStudentTodayAttendance(studentId) {
    return request({
      url: `/api/v1/attendance/student/${studentId}/today`,
      method: 'get'
    })
  },

  /**
   * 学生请假申请
   */
  applyLeave(data) {
    return request({
      url: '/api/v1/attendance/leave/apply',
      method: 'post',
      data: {
        studentId: data.studentId,
        courseId: data.courseId,
        leaveDate: data.leaveDate,
        leaveReason: data.leaveReason,
        proofAttachment: data.proofAttachment,
        ...data
      }
    })
  },

  /**
   * 获取学生请假记录
   */
  getStudentLeaveRecords(studentId, params = {}) {
    return request({
      url: `/api/v1/attendance/student/${studentId}/leave-records`,
      method: 'get',
      params
    })
  },

  // ==================== 教师端操作 ====================

  /**
   * 获取课程考勤记录
   */
  getCourseAttendance(courseId, params = {}) {
    return request({
      url: `/api/v1/attendance/course/${courseId}`,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 教师手动考勤
   */
  manualAttendance(data) {
    return request({
      url: '/api/v1/attendance/manual',
      method: 'post',
      data: {
        studentId: data.studentId,
        courseId: data.courseId,
        attendanceDate: data.attendanceDate,
        attendanceStatus: data.attendanceStatus,
        remarks: data.remarks,
        ...data
      }
    })
  },

  /**
   * 批量录入考勤
   */
  batchAttendance(data) {
    return request({
      url: '/api/v1/attendance/batch-record',
      method: 'post',
      data: {
        courseId: data.courseId,
        attendanceDate: data.attendanceDate,
        attendanceRecords: data.attendanceRecords,
        ...data
      }
    })
  },

  /**
   * 审核请假申请
   */
  approveLeave(leaveId, data) {
    return request({
      url: `/api/v1/attendance/leave/${leaveId}/approve`,
      method: 'post',
      data: {
        approved: data.approved,
        approvalReason: data.approvalReason,
        ...data
      }
    })
  },

  /**
   * 获取待审核请假申请
   */
  getPendingLeaveApplications(teacherId, params = {}) {
    return request({
      url: `/api/v1/attendance/teacher/${teacherId}/pending-leaves`,
      method: 'get',
      params
    })
  },

  /**
   * 导出考勤记录
   */
  exportAttendanceRecords(params = {}, format = 'excel') {
    return request({
      url: '/api/v1/attendance/export',
      method: 'get',
      params: { ...params, format },
      responseType: 'blob'
    })
  },

  // ==================== 批量操作 ====================

  /**
   * 批量导入考勤记录
   */
  batchImportAttendance(data) {
    return request({
      url: '/api/v1/attendance/batch-import',
      method: 'post',
      data
    })
  },

  /**
   * 上传考勤文件
   */
  uploadAttendanceFile(file, onProgress) {
    const formData = new FormData()
    formData.append('file', file)

    return request({
      url: '/api/v1/attendance/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: onProgress
    })
  },

  /**
   * 下载考勤模板
   */
  downloadAttendanceTemplate() {
    return request({
      url: '/api/v1/attendance/template',
      method: 'get',
      responseType: 'blob'
    })
  },

  // ==================== 统计分析 ====================

  /**
   * 获取学生考勤统计
   */
  getStudentAttendanceStatistics(studentId, params = {}) {
    return request({
      url: `/api/v1/attendance/statistics/student/${studentId}`,
      method: 'get',
      params: {
        startDate: params.startDate,
        endDate: params.endDate,
        courseId: params.courseId,
        ...params
      }
    })
  },

  /**
   * 获取课程考勤统计
   */
  getCourseAttendanceStatistics(courseId, params = {}) {
    return request({
      url: `/api/v1/attendance/statistics/course/${courseId}`,
      method: 'get',
      params: {
        startDate: params.startDate,
        endDate: params.endDate,
        ...params
      }
    })
  },

  /**
   * 获取班级考勤统计
   */
  getClassAttendanceStatistics(classId, params = {}) {
    return request({
      url: `/api/v1/attendance/statistics/class/${classId}`,
      method: 'get',
      params: {
        startDate: params.startDate,
        endDate: params.endDate,
        ...params
      }
    })
  },

  /**
   * 获取教师考勤统计
   */
  getTeacherAttendanceStatistics(teacherId, params = {}) {
    return request({
      url: `/api/v1/attendance/statistics/teacher/${teacherId}`,
      method: 'get',
      params: {
        startDate: params.startDate,
        endDate: params.endDate,
        courseId: params.courseId,
        ...params
      }
    })
  },

  /**
   * 获取院系考勤统计
   */
  getDepartmentAttendanceStatistics(departmentId, params = {}) {
    return request({
      url: `/api/v1/attendance/statistics/department/${departmentId}`,
      method: 'get',
      params: {
        startDate: params.startDate,
        endDate: params.endDate,
        ...params
      }
    })
  },

  /**
   * 获取考勤趋势分析
   */
  getAttendanceTrend(params = {}) {
    return request({
      url: '/api/v1/attendance/statistics/trend',
      method: 'get',
      params: {
        type: params.type || 'monthly', // daily, weekly, monthly
        startDate: params.startDate,
        endDate: params.endDate,
        targetType: params.targetType, // student, course, class, department
        targetId: params.targetId,
        ...params
      }
    })
  },

  /**
   * 获取考勤排行榜
   */
  getAttendanceRanking(params = {}) {
    return request({
      url: '/api/v1/attendance/statistics/ranking',
      method: 'get',
      params: {
        type: params.type || 'student', // student, class, course
        period: params.period || 'month', // week, month, semester, year
        limit: params.limit || 10,
        ...params
      }
    })
  },

  // ==================== 考勤规则管理 ====================

  /**
   * 获取考勤规则
   */
  getAttendanceRules(params = {}) {
    return request({
      url: '/api/v1/attendance/rules',
      method: 'get',
      params
    })
  },

  /**
   * 创建考勤规则
   */
  createAttendanceRule(data) {
    return request({
      url: '/api/v1/attendance/rules',
      method: 'post',
      data
    })
  },

  /**
   * 更新考勤规则
   */
  updateAttendanceRule(id, data) {
    return request({
      url: `/api/v1/attendance/rules/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除考勤规则
   */
  deleteAttendanceRule(id) {
    return request({
      url: `/api/v1/attendance/rules/${id}`,
      method: 'delete'
    })
  },

  // ==================== 考勤设备管理 ====================

  /**
   * 获取考勤设备列表
   */
  getAttendanceDevices(params = {}) {
    return request({
      url: '/api/v1/attendance/devices',
      method: 'get',
      params
    })
  },

  /**
   * 同步设备考勤数据
   */
  syncDeviceData(deviceId) {
    return request({
      url: `/api/v1/attendance/devices/${deviceId}/sync`,
      method: 'post'
    })
  },

  /**
   * 获取设备状态
   */
  getDeviceStatus(deviceId) {
    return request({
      url: `/api/v1/attendance/devices/${deviceId}/status`,
      method: 'get'
    })
  }
}

export default attendanceApi
