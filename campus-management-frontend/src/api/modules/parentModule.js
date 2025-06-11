import request from '../request'
import { PARENT_API_PATHS } from '../config'

/**
 * 家长端专用API模块
 * 包含家长监控子女学习和家校沟通的所有功能接口
 */
export const parentModule = {
  // ==================== 家长个人信息 ====================
  
  /**
   * 获取家长个人信息
   */
  getProfile() {
    return request({
      url: PARENT_API_PATHS.PROFILE,
      method: 'get'
    })
  },

  /**
   * 更新家长个人信息
   */
  updateProfile(data) {
    return request({
      url: PARENT_API_PATHS.PROFILE,
      method: 'put',
      data
    })
  },

  /**
   * 上传头像
   */
  uploadAvatar(file) {
    const formData = new FormData()
    formData.append('avatar', file)
    return request({
      url: `${PARENT_API_PATHS.PROFILE}/avatar`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // ==================== 家长仪表盘 ====================
  
  /**
   * 获取家长仪表盘数据
   */
  getDashboard() {
    return request({
      url: PARENT_API_PATHS.DASHBOARD,
      method: 'get'
    })
  },

  /**
   * 获取快速统计
   */
  getQuickStats() {
    return request({
      url: `${PARENT_API_PATHS.DASHBOARD}/quick-stats`,
      method: 'get'
    })
  },

  /**
   * 获取最近活动
   */
  getRecentActivities(limit = 10) {
    return request({
      url: `${PARENT_API_PATHS.DASHBOARD}/recent-activities`,
      method: 'get',
      params: { limit }
    })
  },

  // ==================== 子女管理 ====================
  
  /**
   * 获取子女列表
   */
  getChildren() {
    return request({
      url: PARENT_API_PATHS.CHILDREN,
      method: 'get'
    })
  },

  /**
   * 获取子女详细信息
   */
  getChildDetail(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}`,
      method: 'get'
    })
  },

  /**
   * 绑定子女
   */
  bindChild(data) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/bind`,
      method: 'post',
      data
    })
  },

  /**
   * 解绑子女
   */
  unbindChild(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/unbind`,
      method: 'post'
    })
  },

  // ==================== 子女成绩监控 ====================
  
  /**
   * 获取子女成绩
   */
  getChildGrades(childId, params = {}) {
    return request({
      url: PARENT_API_PATHS.CHILD_GRADES.replace('{childId}', childId),
      method: 'get',
      params: {
        page: 1,
        size: 20,
        semester: 'current',
        ...params
      }
    })
  },

  /**
   * 获取子女成绩统计
   */
  getChildGradeStats(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/grade-stats`,
      method: 'get',
      params
    })
  },

  /**
   * 获取子女成绩趋势
   */
  getChildGradeTrend(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/grade-trend`,
      method: 'get',
      params
    })
  },

  /**
   * 获取子女GPA信息
   */
  getChildGPA(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/gpa`,
      method: 'get',
      params
    })
  },

  // ==================== 子女考勤监控 ====================
  
  /**
   * 获取子女考勤记录
   */
  getChildAttendance(childId, params = {}) {
    return request({
      url: PARENT_API_PATHS.CHILD_ATTENDANCE.replace('{childId}', childId),
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 获取子女今日考勤状态
   */
  getChildTodayAttendance(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/attendance/today`,
      method: 'get'
    })
  },

  /**
   * 获取子女考勤统计
   */
  getChildAttendanceStats(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/attendance-stats`,
      method: 'get',
      params
    })
  },

  // ==================== 子女课程表 ====================
  
  /**
   * 获取子女课程表
   */
  getChildSchedule(childId, params = {}) {
    return request({
      url: PARENT_API_PATHS.CHILD_SCHEDULE.replace('{childId}', childId),
      method: 'get',
      params: {
        week: 'current',
        ...params
      }
    })
  },

  /**
   * 获取子女今日课程
   */
  getChildTodaySchedule(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/schedule/today`,
      method: 'get'
    })
  },

  // ==================== 子女作业监控 ====================
  
  /**
   * 获取子女作业情况
   */
  getChildAssignments(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/assignments`,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all',
        ...params
      }
    })
  },

  /**
   * 获取子女作业统计
   */
  getChildAssignmentStats(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/assignment-stats`,
      method: 'get',
      params
    })
  },

  // ==================== 子女考试安排 ====================
  
  /**
   * 获取子女考试安排
   */
  getChildExams(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/exams`,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all',
        ...params
      }
    })
  },

  /**
   * 获取子女即将到来的考试
   */
  getChildUpcomingExams(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/exams/upcoming`,
      method: 'get'
    })
  },

  // ==================== 家校沟通 ====================
  
  /**
   * 获取沟通记录
   */
  getCommunicationRecords(params = {}) {
    return request({
      url: PARENT_API_PATHS.COMMUNICATION,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 发起沟通
   */
  initiateCommunication(data) {
    return request({
      url: PARENT_API_PATHS.COMMUNICATION,
      method: 'post',
      data
    })
  },

  /**
   * 回复沟通
   */
  replyCommunication(recordId, data) {
    return request({
      url: `${PARENT_API_PATHS.COMMUNICATION}/${recordId}/reply`,
      method: 'post',
      data
    })
  },

  /**
   * 获取教师联系方式
   */
  getTeacherContacts(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/teacher-contacts`,
      method: 'get'
    })
  },

  // ==================== 家长会管理 ====================
  
  /**
   * 获取家长会列表
   */
  getParentMeetings(params = {}) {
    return request({
      url: PARENT_API_PATHS.MEETINGS,
      method: 'get',
      params
    })
  },

  /**
   * 预约家长会
   */
  bookParentMeeting(data) {
    return request({
      url: `${PARENT_API_PATHS.MEETINGS}/book`,
      method: 'post',
      data
    })
  },

  /**
   * 取消家长会预约
   */
  cancelParentMeeting(meetingId) {
    return request({
      url: `${PARENT_API_PATHS.MEETINGS}/${meetingId}/cancel`,
      method: 'post'
    })
  },

  /**
   * 获取家长会预约记录
   */
  getParentMeetingBookings(params = {}) {
    return request({
      url: `${PARENT_API_PATHS.MEETINGS}/bookings`,
      method: 'get',
      params
    })
  },

  // ==================== 通知管理 ====================
  
  /**
   * 获取通知列表
   */
  getNotifications(params = {}) {
    return request({
      url: PARENT_API_PATHS.NOTIFICATIONS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        read: 'all',
        ...params
      }
    })
  },

  /**
   * 标记通知为已读
   */
  markNotificationRead(notificationId) {
    return request({
      url: `${PARENT_API_PATHS.NOTIFICATIONS}/${notificationId}/read`,
      method: 'post'
    })
  },

  /**
   * 获取未读通知数量
   */
  getUnreadNotificationCount() {
    return request({
      url: `${PARENT_API_PATHS.NOTIFICATIONS}/unread-count`,
      method: 'get'
    })
  },

  // ==================== 缴费管理 ====================
  
  /**
   * 获取缴费记录
   */
  getPayments(params = {}) {
    return request({
      url: PARENT_API_PATHS.PAYMENTS,
      method: 'get',
      params
    })
  },

  /**
   * 获取子女缴费记录
   */
  getChildPayments(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/payments`,
      method: 'get',
      params
    })
  },

  /**
   * 获取未缴费项目
   */
  getUnpaidItems(childId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/unpaid-items`,
      method: 'get'
    })
  },

  /**
   * 代缴费用
   */
  makePayment(paymentId, data) {
    return request({
      url: `${PARENT_API_PATHS.PAYMENTS}/${paymentId}/pay`,
      method: 'post',
      data
    })
  },

  /**
   * 申请退费
   */
  applyRefund(paymentId, data) {
    return request({
      url: `${PARENT_API_PATHS.PAYMENTS}/${paymentId}/refund`,
      method: 'post',
      data
    })
  },

  // ==================== 活动管理 ====================
  
  /**
   * 获取学校活动
   */
  getSchoolActivities(params = {}) {
    return request({
      url: PARENT_API_PATHS.ACTIVITIES,
      method: 'get',
      params
    })
  },

  /**
   * 为子女报名活动
   */
  enrollChildActivity(childId, activityId, data = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/activities/${activityId}/enroll`,
      method: 'post',
      data
    })
  },

  /**
   * 获取子女活动报名记录
   */
  getChildActivityEnrollments(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/activity-enrollments`,
      method: 'get',
      params
    })
  },

  // ==================== 请假管理 ====================
  
  /**
   * 申请请假
   */
  applyLeave(childId, data) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/leave-application`,
      method: 'post',
      data
    })
  },

  /**
   * 获取请假申请记录
   */
  getLeaveApplications(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/leave-applications`,
      method: 'get',
      params
    })
  },

  /**
   * 撤销请假申请
   */
  cancelLeaveApplication(childId, applicationId) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/leave-applications/${applicationId}/cancel`,
      method: 'post'
    })
  },

  // ==================== 健康管理 ====================
  
  /**
   * 获取子女健康记录
   */
  getChildHealthRecords(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/health-records`,
      method: 'get',
      params
    })
  },

  /**
   * 添加健康记录
   */
  addHealthRecord(childId, data) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/health-records`,
      method: 'post',
      data
    })
  },

  /**
   * 获取疫苗接种记录
   */
  getVaccinationRecords(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/vaccinations`,
      method: 'get',
      params
    })
  },

  // ==================== 设置管理 ====================
  
  /**
   * 获取提醒偏好设置
   */
  getReminderPreferences() {
    return request({
      url: `${PARENT_API_PATHS.PROFILE}/reminder-preferences`,
      method: 'get'
    })
  },

  /**
   * 更新提醒偏好设置
   */
  updateReminderPreferences(data) {
    return request({
      url: `${PARENT_API_PATHS.PROFILE}/reminder-preferences`,
      method: 'put',
      data
    })
  },

  /**
   * 获取隐私设置
   */
  getPrivacySettings() {
    return request({
      url: `${PARENT_API_PATHS.PROFILE}/privacy-settings`,
      method: 'get'
    })
  },

  /**
   * 更新隐私设置
   */
  updatePrivacySettings(data) {
    return request({
      url: `${PARENT_API_PATHS.PROFILE}/privacy-settings`,
      method: 'put',
      data
    })
  },

  // ==================== 数据导出 ====================
  
  /**
   * 导出子女学习报告
   */
  exportChildLearningReport(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/learning-report`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  /**
   * 导出子女成绩单
   */
  exportChildGradeReport(childId, params = {}) {
    return request({
      url: `${PARENT_API_PATHS.CHILDREN}/${childId}/grade-report`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  }
}

export default parentModule