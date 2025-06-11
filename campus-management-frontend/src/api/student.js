import request from './request'

/**
 * 学生相关API
 * 基于后端 StudentController 和相关接口实现
 */
export const studentApi = {
  // ==================== 学生基础信息管理 ====================

  /**
   * 获取学生列表
   * 对应后端: GET /api/v1/students
   */
  getStudentList(params = {}) {
    return request({
      url: '/v1/students',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取学生详情
   * 对应后端: GET /api/v1/students/{id}
   */
  getStudentById(id) {
    return request({
      url: `/v1/students/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建学生
   * 对应后端: POST /api/v1/students
   */
  createStudent(data) {
    return request({
      url: '/v1/students',
      method: 'post',
      data
    })
  },

  /**
   * 更新学生信息
   * 对应后端: PUT /api/v1/students/{id}
   */
  updateStudent(id, data) {
    return request({
      url: `/v1/students/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除学生
   * 对应后端: DELETE /api/v1/students/{id}
   */
  deleteStudent(id) {
    return request({
      url: `/v1/students/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量更新学生状态
   * 对应后端: PUT /api/v1/students/batch/status
   */
  batchUpdateStudentStatus(data) {
    return request({
      url: '/v1/students/batch/status',
      method: 'put',
      data
    })
  },
  // ==================== 学生个人信息管理 ====================

  /**
   * 获取学生个人信息
   * 对应后端: GET /api/v1/student/profile
   */
  getProfile() {
    return request({
      url: '/student/profile',
      method: 'get'
    })
  },

  /**
   * 更新学生个人信息
   * 对应后端: PUT /api/v1/student/profile
   */
  updateProfile(data) {
    return request({
      url: '/student/profile',
      method: 'put',
      data
    })
  },

  /**
   * 上传头像
   * 对应后端: POST /api/v1/student/profile/avatar
   */
  uploadAvatar(formData) {
    return request({
      url: '/student/profile/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // ==================== 学生仪表盘数据 ====================

  /**
   * 获取仪表盘统计数据
   * 对应后端: GET /api/v1/dashboard/stats
   */
  getDashboardStats() {
    return request({
      url: '/dashboard/stats',
      method: 'get'
    })
  },

  /**
   * 获取快速统计数据
   * 对应后端: GET /api/v1/dashboard/quick-stats
   */
  getQuickStats() {
    return request({
      url: '/dashboard/quick-stats',
      method: 'get'
    })
  },

  /**
   * 获取系统通知
   * 对应后端: GET /api/v1/dashboard/system-notifications
   */
  getSystemNotifications(params = {}) {
    return request({
      url: '/dashboard/system-notifications',
      method: 'get',
      params
    })
  },

  /**
   * 获取最近活动
   * 对应后端: GET /api/v1/dashboard/recent-activities
   */
  getRecentActivities(params = {}) {
    return request({
      url: '/dashboard/recent-activities',
      method: 'get',
      params
    })
  },

  /**
   * 获取图表数据
   * 对应后端: GET /api/v1/dashboard/chart-data
   */
  getChartData(type, params = {}) {
    return request({
      url: `/dashboard/chart-data/${type}`,
      method: 'get',
      params
    })
  },

  // 获取通知公告
  getNotifications(params = {}) {
    return request({
      url: '/student/notifications',
      method: 'get',
      params
    })
  },

  // 标记通知为已读
  markNotificationRead(notificationId) {
    return request({
      url: `/student/notifications/${notificationId}/read`,
      method: 'post'
    })
  },

  // 批量标记通知为已读
  markNotificationsRead(notificationIds) {
    return request({
      url: '/student/notifications/batch-read',
      method: 'post',
      data: { notificationIds }
    })
  },

  // 获取学期列表
  getSemesters() {
    return request({
      url: '/student/semesters',
      method: 'get'
    })
  },

  // 获取班级信息
  getClassInfo() {
    return request({
      url: '/student/class-info',
      method: 'get'
    })
  },

  // 获取同班同学列表
  getClassmates(params = {}) {
    return request({
      url: '/student/classmates',
      method: 'get',
      params
    })
  },

  // 获取出勤记录
  getAttendanceRecords(params = {}) {
    return request({
      url: '/student/attendance',
      method: 'get',
      params
    })
  },

  // 获取学习进度
  getLearningProgress(courseId) {
    return request({
      url: `/student/learning-progress/${courseId}`,
      method: 'get'
    })
  },

  // 更新学习进度
  updateLearningProgress(courseId, data) {
    return request({
      url: `/student/learning-progress/${courseId}`,
      method: 'post',
      data
    })
  },

  // 获取作业列表
  getAssignments(params = {}) {
    return request({
      url: '/student/assignments',
      method: 'get',
      params
    })
  },

  // 提交作业
  submitAssignment(assignmentId, data) {
    return request({
      url: `/student/assignments/${assignmentId}/submit`,
      method: 'post',
      data
    })
  },

  // 获取考试安排
  getExamSchedule(params = {}) {
    return request({
      url: '/student/exams',
      method: 'get',
      params
    })
  },

  // 获取图书借阅记录
  getLibraryRecords(params = {}) {
    return request({
      url: '/student/library/records',
      method: 'get',
      params
    })
  },

  // 预约图书
  reserveBook(bookId, data) {
    return request({
      url: `/student/library/reserve/${bookId}`,
      method: 'post',
      data
    })
  },

  // 续借图书
  renewBook(recordId) {
    return request({
      url: `/student/library/renew/${recordId}`,
      method: 'post'
    })
  },

  // 获取校园卡信息
  getCampusCardInfo() {
    return request({
      url: '/student/campus-card',
      method: 'get'
    })
  },

  // 获取校园卡消费记录
  getCampusCardRecords(params = {}) {
    return request({
      url: '/student/campus-card/records',
      method: 'get',
      params
    })
  },

  // 校园卡充值
  rechargeCampusCard(data) {
    return request({
      url: '/student/campus-card/recharge',
      method: 'post',
      data
    })
  },

  // 校园卡挂失
  reportLostCard() {
    return request({
      url: '/student/campus-card/report-lost',
      method: 'post'
    })
  },

  // 获取奖学金信息
  getScholarshipInfo(params = {}) {
    return request({
      url: '/student/scholarships',
      method: 'get',
      params
    })
  },

  // 申请奖学金
  applyScholarship(scholarshipId, data) {
    return request({
      url: `/student/scholarships/${scholarshipId}/apply`,
      method: 'post',
      data
    })
  },

  // 获取实习信息
  getInternshipInfo(params = {}) {
    return request({
      url: '/student/internships',
      method: 'get',
      params
    })
  },

  // 申请实习
  applyInternship(internshipId, data) {
    return request({
      url: `/student/internships/${internshipId}/apply`,
      method: 'post',
      data
    })
  },

  // 获取社团活动
  getClubActivities(params = {}) {
    return request({
      url: '/student/club-activities',
      method: 'get',
      params
    })
  },

  // 参加社团活动
  joinClubActivity(activityId) {
    return request({
      url: `/student/club-activities/${activityId}/join`,
      method: 'post'
    })
  },

  // 获取志愿服务活动
  getVolunteerActivities(params = {}) {
    return request({
      url: '/student/volunteer-activities',
      method: 'get',
      params
    })
  },

  // 报名志愿服务
  applyVolunteerActivity(activityId, data) {
    return request({
      url: `/student/volunteer-activities/${activityId}/apply`,
      method: 'post',
      data
    })
  },

  // 获取评教列表
  getEvaluationList(params = {}) {
    return request({
      url: '/student/evaluations',
      method: 'get',
      params
    })
  },

  // 提交课程评教
  submitEvaluation(courseId, data) {
    return request({
      url: `/student/evaluations/${courseId}`,
      method: 'post',
      data
    })
  },

  // 获取毕业要求
  getGraduationRequirements() {
    return request({
      url: '/student/graduation-requirements',
      method: 'get'
    })
  },

  // 获取选课建议
  getCourseRecommendations(params = {}) {
    return request({
      url: '/student/course-recommendations',
      method: 'get',
      params
    })
  },

  // 获取学业预警信息
  getAcademicWarnings() {
    return request({
      url: '/student/academic-warnings',
      method: 'get'
    })
  },

  // 获取导师信息
  getAdvisorInfo() {
    return request({
      url: '/student/advisor',
      method: 'get'
    })
  },

  // 预约导师
  bookAdvisorMeeting(data) {
    return request({
      url: '/student/advisor/book-meeting',
      method: 'post',
      data
    })
  },

  // 修改密码
  changePassword(data) {
    return request({
      url: '/student/change-password',
      method: 'post',
      data
    })
  },

  // 绑定邮箱
  bindEmail(data) {
    return request({
      url: '/student/bind-email',
      method: 'post',
      data
    })
  },

  // 绑定手机号
  bindPhone(data) {
    return request({
      url: '/student/bind-phone',
      method: 'post',
      data
    })
  },

  // 获取隐私设置
  getPrivacySettings() {
    return request({
      url: '/student/privacy-settings',
      method: 'get'
    })
  },

  // 更新隐私设置
  updatePrivacySettings(data) {
    return request({
      url: '/student/privacy-settings',
      method: 'put',
      data
    })
  },

  // 导出学生数据
  exportStudentData(format = 'pdf') {
    return request({
      url: '/student/export-data',
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // 反馈建议
  submitFeedback(data) {
    return request({
      url: '/student/feedback',
      method: 'post',
      data
    })
  },

  // ==================== 学生课程相关 ====================

  /**
   * 获取学生已选课程列表
   * 对应后端: GET /api/v1/student/courses
   */
  getStudentCourses(params = {}) {
    return request({
      url: '/student/courses',
      method: 'get',
      params
    })
  },

  /**
   * 获取学生课程详情
   * 对应后端: GET /api/v1/student/courses/{id}
   */
  getStudentCourseDetail(courseId) {
    return request({
      url: `/student/courses/${courseId}`,
      method: 'get'
    })
  },

  /**
   * 获取学生成绩列表
   * 对应后端: GET /api/v1/student/grades
   */
  getStudentGrades(params = {}) {
    return request({
      url: '/student/grades',
      method: 'get',
      params
    })
  },

  /**
   * 获取学生考试安排
   * 对应后端: GET /api/v1/student/exam-schedule
   */
  getStudentExamSchedule(params = {}) {
    return request({
      url: '/student/exam-schedule',
      method: 'get',
      params
    })
  },

  /**
   * 获取学生课程表
   * 对应后端: GET /api/v1/student/schedule
   */
  getStudentSchedule(params = {}) {
    return request({
      url: '/student/schedule',
      method: 'get',
      params
    })
  },

  // ==================== 学生个人资料相关 ====================

  /**
   * 更新学生个人资料
   * 对应后端: PUT /api/v1/student/profile
   */
  updateProfile(data) {
    return request({
      url: '/student/profile',
      method: 'put',
      data
    })
  },

  /**
   * 上传学生头像
   * 对应后端: POST /api/v1/student/avatar
   */
  uploadAvatar(formData) {
    return request({
      url: '/student/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 获取学习统计数据
   * 对应后端: GET /api/v1/student/learning-stats
   */
  getLearningStats() {
    return request({
      url: '/student/learning-stats',
      method: 'get'
    })
  },

  /**
   * 导出个人信息
   * 对应后端: GET /api/v1/student/export-profile
   */
  exportProfile(params = {}) {
    return request({
      url: '/student/export-profile',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  /**
   * 获取学期列表
   * 对应后端: GET /api/v1/student/semesters
   */
  getSemesters() {
    return request({
      url: '/student/semesters',
      method: 'get'
    })
  },

  /**
   * 获取学生课程详情
   * 对应后端: GET /api/v1/student/courses/{id}
   */
  getStudentCourseDetail(courseId) {
    return request({
      url: `/student/courses/${courseId}`,
      method: 'get'
    })
  },

  /**
   * 获取课程学习进度
   * 对应后端: GET /api/v1/student/courses/{id}/progress
   */
  getCourseProgress(courseId) {
    return request({
      url: `/student/courses/${courseId}/progress`,
      method: 'get'
    })
  }
}

export default studentApi