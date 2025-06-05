import request from './request'

/**
 * 家长相关API
 */
export const parentApi = {
  // 获取家长个人信息
  getProfile() {
    return request({
      url: '/parent/profile',
      method: 'get'
    })
  },

  // 更新家长个人信息
  updateProfile(data) {
    return request({
      url: '/parent/profile',
      method: 'put',
      data
    })
  },

  // 上传家长头像
  uploadAvatar(formData) {
    return request({
      url: '/parent/profile/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取家长仪表盘统计
  getDashboardStats() {
    return request({
      url: '/parent/dashboard/stats',
      method: 'get'
    })
  },

  // 获取子女列表
  getChildren() {
    return request({
      url: '/parent/children',
      method: 'get'
    })
  },

  // 获取子女详细信息
  getChildDetail(childId) {
    return request({
      url: `/parent/children/${childId}`,
      method: 'get'
    })
  },

  // 绑定子女
  bindChild(data) {
    return request({
      url: '/parent/children/bind',
      method: 'post',
      data
    })
  },

  // 解绑子女
  unbindChild(childId) {
    return request({
      url: `/parent/children/${childId}/unbind`,
      method: 'post'
    })
  },

  // 获取子女成绩
  getChildGrades(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/grades`,
      method: 'get',
      params
    })
  },

  // 获取子女成绩统计
  getChildGradeStats(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/grade-stats`,
      method: 'get',
      params
    })
  },

  // 获取子女成绩趋势
  getChildGradeTrend(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/grade-trend`,
      method: 'get',
      params
    })
  },

  // 获取子女课程表
  getChildSchedule(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/schedule`,
      method: 'get',
      params
    })
  },

  // 获取子女出勤记录
  getChildAttendance(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/attendance`,
      method: 'get',
      params
    })
  },

  // 获取子女作业情况
  getChildAssignments(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/assignments`,
      method: 'get',
      params
    })
  },

  // 获取子女考试安排
  getChildExams(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/exams`,
      method: 'get',
      params
    })
  },

  // 获取缴费记录
  getPaymentRecords(params = {}) {
    return request({
      url: '/parent/payments',
      method: 'get',
      params
    })
  },

  // 获取缴费详情
  getPaymentDetail(paymentId) {
    return request({
      url: `/parent/payments/${paymentId}`,
      method: 'get'
    })
  },

  // 在线支付
  makePayment(paymentId, data) {
    return request({
      url: `/parent/payments/${paymentId}/pay`,
      method: 'post',
      data
    })
  },

  // 获取支付方式
  getPaymentMethods() {
    return request({
      url: '/parent/payment-methods',
      method: 'get'
    })
  },

  // 验证支付结果
  verifyPayment(paymentId, data) {
    return request({
      url: `/parent/payments/${paymentId}/verify`,
      method: 'post',
      data
    })
  },

  // 申请退费
  applyRefund(paymentId, data) {
    return request({
      url: `/parent/payments/${paymentId}/refund`,
      method: 'post',
      data
    })
  },

  // 获取退费申请列表
  getRefundApplications(params = {}) {
    return request({
      url: '/parent/refund-applications',
      method: 'get',
      params
    })
  },

  // 下载缴费凭证
  downloadPaymentReceipt(paymentId) {
    return request({
      url: `/parent/payments/${paymentId}/receipt`,
      method: 'get',
      responseType: 'blob'
    })
  },

  // 获取通知公告
  getNotifications(params = {}) {
    return request({
      url: '/parent/notifications',
      method: 'get',
      params
    })
  },

  // 标记通知为已读
  markNotificationRead(notificationId) {
    return request({
      url: `/parent/notifications/${notificationId}/read`,
      method: 'post'
    })
  },

  // 获取家校沟通记录
  getCommunicationRecords(params = {}) {
    return request({
      url: '/parent/communication-records',
      method: 'get',
      params
    })
  },

  // 发起家校沟通
  initiateCommunication(data) {
    return request({
      url: '/parent/communication-records',
      method: 'post',
      data
    })
  },

  // 回复家校沟通
  replyCommunication(recordId, data) {
    return request({
      url: `/parent/communication-records/${recordId}/reply`,
      method: 'post',
      data
    })
  },

  // 预约家长会
  bookParentMeeting(data) {
    return request({
      url: '/parent/parent-meetings/book',
      method: 'post',
      data
    })
  },

  // 获取家长会预约记录
  getParentMeetingBookings(params = {}) {
    return request({
      url: '/parent/parent-meetings',
      method: 'get',
      params
    })
  },

  // 取消家长会预约
  cancelParentMeeting(bookingId) {
    return request({
      url: `/parent/parent-meetings/${bookingId}/cancel`,
      method: 'post'
    })
  },

  // 获取教师联系方式
  getTeacherContacts(childId) {
    return request({
      url: `/parent/children/${childId}/teacher-contacts`,
      method: 'get'
    })
  },

  // 获取班级信息
  getClassInfo(childId) {
    return request({
      url: `/parent/children/${childId}/class-info`,
      method: 'get'
    })
  },

  // 获取同班学生家长信息
  getClassmateParents(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/classmate-parents`,
      method: 'get',
      params
    })
  },

  // 申请请假
  applyLeave(childId, data) {
    return request({
      url: `/parent/children/${childId}/leave-application`,
      method: 'post',
      data
    })
  },

  // 获取请假申请记录
  getLeaveApplications(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/leave-applications`,
      method: 'get',
      params
    })
  },

  // 撤销请假申请
  cancelLeaveApplication(childId, applicationId) {
    return request({
      url: `/parent/children/${childId}/leave-applications/${applicationId}/cancel`,
      method: 'post'
    })
  },

  // 获取校车信息
  getSchoolBusInfo(childId) {
    return request({
      url: `/parent/children/${childId}/school-bus`,
      method: 'get'
    })
  },

  // 申请校车服务
  applySchoolBus(childId, data) {
    return request({
      url: `/parent/children/${childId}/school-bus/apply`,
      method: 'post',
      data
    })
  },

  // 获取校车实时位置
  getSchoolBusLocation(busId) {
    return request({
      url: `/parent/school-bus/${busId}/location`,
      method: 'get'
    })
  },

  // 获取食堂菜单
  getCafeteriaMenu(params = {}) {
    return request({
      url: '/parent/cafeteria/menu',
      method: 'get',
      params
    })
  },

  // 订餐
  orderMeal(childId, data) {
    return request({
      url: `/parent/children/${childId}/meal-order`,
      method: 'post',
      data
    })
  },

  // 获取订餐记录
  getMealOrders(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/meal-orders`,
      method: 'get',
      params
    })
  },

  // 取消订餐
  cancelMealOrder(childId, orderId) {
    return request({
      url: `/parent/children/${childId}/meal-orders/${orderId}/cancel`,
      method: 'post'
    })
  },

  // 获取子女健康记录
  getChildHealthRecords(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/health-records`,
      method: 'get',
      params
    })
  },

  // 添加健康记录
  addHealthRecord(childId, data) {
    return request({
      url: `/parent/children/${childId}/health-records`,
      method: 'post',
      data
    })
  },

  // 获取疫苗接种记录
  getVaccinationRecords(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/vaccinations`,
      method: 'get',
      params
    })
  },

  // 获取体检报告
  getMedicalExamReports(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/medical-exams`,
      method: 'get',
      params
    })
  },

  // 下载体检报告
  downloadMedicalExamReport(childId, reportId) {
    return request({
      url: `/parent/children/${childId}/medical-exams/${reportId}/download`,
      method: 'get',
      responseType: 'blob'
    })
  },

  // 获取学校活动
  getSchoolActivities(params = {}) {
    return request({
      url: '/parent/school-activities',
      method: 'get',
      params
    })
  },

  // 报名学校活动
  enrollSchoolActivity(childId, activityId, data) {
    return request({
      url: `/parent/children/${childId}/school-activities/${activityId}/enroll`,
      method: 'post',
      data
    })
  },

  // 获取活动报名记录
  getActivityEnrollments(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/activity-enrollments`,
      method: 'get',
      params
    })
  },

  // 获取兴趣班信息
  getInterestClasses(params = {}) {
    return request({
      url: '/parent/interest-classes',
      method: 'get',
      params
    })
  },

  // 报名兴趣班
  enrollInterestClass(childId, classId, data) {
    return request({
      url: `/parent/children/${childId}/interest-classes/${classId}/enroll`,
      method: 'post',
      data
    })
  },

  // 获取兴趣班报名记录
  getInterestClassEnrollments(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/interest-class-enrollments`,
      method: 'get',
      params
    })
  },

  // 获取家长评价
  getParentEvaluations(params = {}) {
    return request({
      url: '/parent/evaluations',
      method: 'get',
      params
    })
  },

  // 提交家长评价
  submitParentEvaluation(data) {
    return request({
      url: '/parent/evaluations',
      method: 'post',
      data
    })
  },

  // 获取学校新闻
  getSchoolNews(params = {}) {
    return request({
      url: '/parent/school-news',
      method: 'get',
      params
    })
  },

  // 获取学校政策
  getSchoolPolicies(params = {}) {
    return request({
      url: '/parent/school-policies',
      method: 'get',
      params
    })
  },

  // 提交意见建议
  submitFeedback(data) {
    return request({
      url: '/parent/feedback',
      method: 'post',
      data
    })
  },

  // 获取意见建议记录
  getFeedbackRecords(params = {}) {
    return request({
      url: '/parent/feedback-records',
      method: 'get',
      params
    })
  },

  // 设置提醒偏好
  setReminderPreferences(data) {
    return request({
      url: '/parent/reminder-preferences',
      method: 'put',
      data
    })
  },

  // 获取提醒偏好
  getReminderPreferences() {
    return request({
      url: '/parent/reminder-preferences',
      method: 'get'
    })
  },

  // 修改密码
  changePassword(data) {
    return request({
      url: '/parent/change-password',
      method: 'post',
      data
    })
  },

  // 绑定手机号
  bindPhone(data) {
    return request({
      url: '/parent/bind-phone',
      method: 'post',
      data
    })
  },

  // 绑定邮箱
  bindEmail(data) {
    return request({
      url: '/parent/bind-email',
      method: 'post',
      data
    })
  },

  // 导出家长数据
  exportParentData(format = 'pdf') {
    return request({
      url: '/parent/export-data',
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // 生成子女学习报告
  generateChildLearningReport(childId, params = {}) {
    return request({
      url: `/parent/children/${childId}/learning-report`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  }
}

export default parentApi