import request from '../request'
import { STUDENT_API_PATHS } from '../config'

/**
 * 学生端专用API模块
 * 包含学生日常使用的所有功能接口
 */
export const studentModule = {
  // ==================== 学生个人信息 ====================
  
  /**
   * 获取学生个人信息
   */
  getProfile() {
    return request({
      url: STUDENT_API_PATHS.PROFILE,
      method: 'get'
    })
  },

  /**
   * 更新学生个人信息
   */
  updateProfile(data) {
    return request({
      url: STUDENT_API_PATHS.PROFILE,
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
      url: `${STUDENT_API_PATHS.PROFILE}/avatar`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // ==================== 学生仪表盘 ====================
  
  /**
   * 获取学生仪表盘数据
   */
  getDashboard() {
    return request({
      url: STUDENT_API_PATHS.DASHBOARD,
      method: 'get'
    })
  },

  /**
   * 获取快速统计
   */
  getQuickStats() {
    return request({
      url: `${STUDENT_API_PATHS.DASHBOARD}/quick-stats`,
      method: 'get'
    })
  },

  /**
   * 获取最近活动
   */
  getRecentActivities(limit = 10) {
    return request({
      url: `${STUDENT_API_PATHS.DASHBOARD}/recent-activities`,
      method: 'get',
      params: { limit }
    })
  },

  // ==================== 课程管理 ====================
  
  /**
   * 获取学生课程列表
   */
  getCourses(params = {}) {
    return request({
      url: STUDENT_API_PATHS.COURSES,
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
   * 获取课程详情
   */
  getCourseDetail(courseId) {
    return request({
      url: `${STUDENT_API_PATHS.COURSES}/${courseId}`,
      method: 'get'
    })
  },

  /**
   * 获取课程表
   */
  getSchedule(params = {}) {
    return request({
      url: STUDENT_API_PATHS.SCHEDULE,
      method: 'get',
      params: {
        week: 'current',
        ...params
      }
    })
  },

  /**
   * 获取今日课程
   */
  getTodaySchedule() {
    return request({
      url: `${STUDENT_API_PATHS.SCHEDULE}/today`,
      method: 'get'
    })
  },

  // ==================== 选课管理 ====================
  
  /**
   * 获取可选课程
   */
  getAvailableCourses(params = {}) {
    return request({
      url: `${STUDENT_API_PATHS.COURSE_SELECTION}/available`,
      method: 'get',
      params
    })
  },

  /**
   * 选课
   */
  selectCourse(courseId, data = {}) {
    return request({
      url: `${STUDENT_API_PATHS.COURSE_SELECTION}/${courseId}`,
      method: 'post',
      data
    })
  },

  /**
   * 退课
   */
  dropCourse(courseId, reason = '') {
    return request({
      url: `${STUDENT_API_PATHS.COURSE_SELECTION}/${courseId}`,
      method: 'delete',
      data: { reason }
    })
  },

  /**
   * 获取选课历史
   */
  getSelectionHistory(params = {}) {
    return request({
      url: `${STUDENT_API_PATHS.COURSE_SELECTION}/history`,
      method: 'get',
      params
    })
  },

  // ==================== 作业管理 ====================
  
  /**
   * 获取作业列表
   */
  getAssignments(params = {}) {
    return request({
      url: STUDENT_API_PATHS.ASSIGNMENTS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all', // all, pending, submitted, graded
        ...params
      }
    })
  },

  /**
   * 获取作业详情
   */
  getAssignmentDetail(assignmentId) {
    return request({
      url: `${STUDENT_API_PATHS.ASSIGNMENTS}/${assignmentId}`,
      method: 'get'
    })
  },

  /**
   * 提交作业
   */
  submitAssignment(assignmentId, data) {
    return request({
      url: `${STUDENT_API_PATHS.ASSIGNMENTS}/${assignmentId}/submit`,
      method: 'post',
      data
    })
  },

  /**
   * 上传作业文件
   */
  uploadAssignmentFile(assignmentId, file) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('assignmentId', assignmentId)
    return request({
      url: `${STUDENT_API_PATHS.ASSIGNMENTS}/upload`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // ==================== 考试管理 ====================
  
  /**
   * 获取考试列表
   */
  getExams(params = {}) {
    return request({
      url: STUDENT_API_PATHS.EXAMS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all', // all, upcoming, ongoing, finished
        ...params
      }
    })
  },

  /**
   * 获取考试详情
   */
  getExamDetail(examId) {
    return request({
      url: `${STUDENT_API_PATHS.EXAMS}/${examId}`,
      method: 'get'
    })
  },

  /**
   * 开始考试
   */
  startExam(examId) {
    return request({
      url: `${STUDENT_API_PATHS.EXAMS}/${examId}/start`,
      method: 'post'
    })
  },

  /**
   * 提交考试答案
   */
  submitExam(examId, answers) {
    return request({
      url: `${STUDENT_API_PATHS.EXAMS}/${examId}/submit`,
      method: 'post',
      data: { answers }
    })
  },

  /**
   * 保存考试答案（临时保存）
   */
  saveExamAnswers(examId, answers) {
    return request({
      url: `${STUDENT_API_PATHS.EXAMS}/${examId}/save`,
      method: 'post',
      data: { answers }
    })
  },

  // ==================== 成绩管理 ====================
  
  /**
   * 获取成绩列表
   */
  getGrades(params = {}) {
    return request({
      url: STUDENT_API_PATHS.GRADES,
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
   * 获取成绩详情
   */
  getGradeDetail(gradeId) {
    return request({
      url: `${STUDENT_API_PATHS.GRADES}/${gradeId}`,
      method: 'get'
    })
  },

  /**
   * 获取GPA信息
   */
  getGPA(params = {}) {
    return request({
      url: `${STUDENT_API_PATHS.GRADES}/gpa`,
      method: 'get',
      params
    })
  },

  /**
   * 获取成绩统计
   */
  getGradeStatistics(params = {}) {
    return request({
      url: `${STUDENT_API_PATHS.GRADES}/statistics`,
      method: 'get',
      params
    })
  },

  /**
   * 申请成绩复查
   */
  applyGradeReview(gradeId, reason) {
    return request({
      url: `${STUDENT_API_PATHS.GRADES}/${gradeId}/review`,
      method: 'post',
      data: { reason }
    })
  },

  // ==================== 考勤管理 ====================
  
  /**
   * 获取考勤记录
   */
  getAttendance(params = {}) {
    return request({
      url: STUDENT_API_PATHS.ATTENDANCE,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 学生签到
   */
  checkIn(courseId, location) {
    return request({
      url: `${STUDENT_API_PATHS.ATTENDANCE}/checkin`,
      method: 'post',
      data: { courseId, location }
    })
  },

  /**
   * 学生签退
   */
  checkOut(courseId) {
    return request({
      url: `${STUDENT_API_PATHS.ATTENDANCE}/checkout`,
      method: 'post',
      data: { courseId }
    })
  },

  /**
   * 申请请假
   */
  applyLeave(data) {
    return request({
      url: `${STUDENT_API_PATHS.ATTENDANCE}/leave`,
      method: 'post',
      data
    })
  },

  // ==================== 通知管理 ====================
  
  /**
   * 获取通知列表
   */
  getNotifications(params = {}) {
    return request({
      url: STUDENT_API_PATHS.NOTIFICATIONS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        read: 'all', // all, read, unread
        ...params
      }
    })
  },

  /**
   * 标记通知为已读
   */
  markNotificationRead(notificationId) {
    return request({
      url: `${STUDENT_API_PATHS.NOTIFICATIONS}/${notificationId}/read`,
      method: 'post'
    })
  },

  /**
   * 获取未读通知数量
   */
  getUnreadNotificationCount() {
    return request({
      url: `${STUDENT_API_PATHS.NOTIFICATIONS}/unread-count`,
      method: 'get'
    })
  },

  // ==================== 缴费管理 ====================
  
  /**
   * 获取缴费记录
   */
  getPayments(params = {}) {
    return request({
      url: STUDENT_API_PATHS.PAYMENTS,
      method: 'get',
      params
    })
  },

  /**
   * 获取未缴费项目
   */
  getUnpaidItems() {
    return request({
      url: `${STUDENT_API_PATHS.PAYMENTS}/unpaid`,
      method: 'get'
    })
  },

  /**
   * 在线支付
   */
  makePayment(paymentId, paymentMethod) {
    return request({
      url: `${STUDENT_API_PATHS.PAYMENTS}/${paymentId}/pay`,
      method: 'post',
      data: { paymentMethod }
    })
  },

  // ==================== 图书馆管理 ====================
  
  /**
   * 搜索图书
   */
  searchBooks(keyword, params = {}) {
    return request({
      url: `${STUDENT_API_PATHS.LIBRARY}/search`,
      method: 'get',
      params: {
        keyword,
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 借阅图书
   */
  borrowBook(bookId) {
    return request({
      url: `${STUDENT_API_PATHS.LIBRARY}/borrow/${bookId}`,
      method: 'post'
    })
  },

  /**
   * 归还图书
   */
  returnBook(borrowId) {
    return request({
      url: `${STUDENT_API_PATHS.LIBRARY}/return/${borrowId}`,
      method: 'post'
    })
  },

  /**
   * 续借图书
   */
  renewBook(borrowId) {
    return request({
      url: `${STUDENT_API_PATHS.LIBRARY}/renew/${borrowId}`,
      method: 'post'
    })
  },

  /**
   * 获取借阅记录
   */
  getBorrowRecords(params = {}) {
    return request({
      url: `${STUDENT_API_PATHS.LIBRARY}/records`,
      method: 'get',
      params
    })
  }
}

export default studentModule