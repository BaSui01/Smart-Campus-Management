import request from './request'

/**
 * 课程相关API
 */
export const courseApi = {
  // 获取学生课程列表
  getStudentCourses(params = {}) {
    return request({
      url: '/courses/student',
      method: 'get',
      params
    })
  },

  // 获取教师课程列表
  getTeacherCourses(params = {}) {
    return request({
      url: '/courses/teacher',
      method: 'get',
      params
    })
  },

  // 获取所有课程列表
  getAllCourses(params = {}) {
    return request({
      url: '/courses',
      method: 'get',
      params
    })
  },

  // 获取课程详情
  getCourseDetail(courseId) {
    return request({
      url: `/courses/${courseId}`,
      method: 'get'
    })
  },

  // 创建课程
  createCourse(data) {
    return request({
      url: '/courses',
      method: 'post',
      data
    })
  },

  // 更新课程信息
  updateCourse(courseId, data) {
    return request({
      url: `/courses/${courseId}`,
      method: 'put',
      data
    })
  },

  // 删除课程
  deleteCourse(courseId) {
    return request({
      url: `/courses/${courseId}`,
      method: 'delete'
    })
  },

  // 获取今日课程
  getTodayCourses() {
    return request({
      url: '/courses/today',
      method: 'get'
    })
  },

  // 获取本周课程
  getWeekCourses(params = {}) {
    return request({
      url: '/courses/week',
      method: 'get',
      params
    })
  },

  // 获取课程表
  getCourseSchedule(params = {}) {
    return request({
      url: '/courses/schedule',
      method: 'get',
      params
    })
  },

  // 获取课程学生列表
  getCourseStudents(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/students`,
      method: 'get',
      params
    })
  },

  // 添加学生到课程
  addStudentToCourse(courseId, studentId) {
    return request({
      url: `/courses/${courseId}/students/${studentId}`,
      method: 'post'
    })
  },

  // 从课程中移除学生
  removeStudentFromCourse(courseId, studentId) {
    return request({
      url: `/courses/${courseId}/students/${studentId}`,
      method: 'delete'
    })
  },

  // 批量添加学生到课程
  batchAddStudents(courseId, studentIds) {
    return request({
      url: `/courses/${courseId}/students/batch`,
      method: 'post',
      data: { studentIds }
    })
  },

  // 选课
  enrollCourse(courseId, data = {}) {
    return request({
      url: `/courses/${courseId}/enroll`,
      method: 'post',
      data
    })
  },

  // 退课
  withdrawCourse(courseId, data = {}) {
    return request({
      url: `/courses/${courseId}/withdraw`,
      method: 'post',
      data
    })
  },

  // 获取选课历史
  getEnrollmentHistory(params = {}) {
    return request({
      url: '/courses/enrollment-history',
      method: 'get',
      params
    })
  },

  // 获取可选课程列表
  getAvailableCourses(params = {}) {
    return request({
      url: '/courses/available',
      method: 'get',
      params
    })
  },

  // 搜索课程
  searchCourses(params = {}) {
    return request({
      url: '/courses/search',
      method: 'get',
      params
    })
  },

  // 获取课程分类
  getCourseCategories() {
    return request({
      url: '/courses/categories',
      method: 'get'
    })
  },

  // 获取课程统计信息
  getCourseStats(courseId) {
    return request({
      url: `/courses/${courseId}/stats`,
      method: 'get'
    })
  },

  // 获取课程资源
  getCourseResources(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/resources`,
      method: 'get',
      params
    })
  },

  // 上传课程资源
  uploadCourseResource(courseId, formData) {
    return request({
      url: `/courses/${courseId}/resources`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 删除课程资源
  deleteCourseResource(courseId, resourceId) {
    return request({
      url: `/courses/${courseId}/resources/${resourceId}`,
      method: 'delete'
    })
  },

  // 下载课程资源
  downloadCourseResource(courseId, resourceId) {
    return request({
      url: `/courses/${courseId}/resources/${resourceId}/download`,
      method: 'get',
      responseType: 'blob'
    })
  },

  // 获取课程章节
  getCourseChapters(courseId) {
    return request({
      url: `/courses/${courseId}/chapters`,
      method: 'get'
    })
  },

  // 创建课程章节
  createCourseChapter(courseId, data) {
    return request({
      url: `/courses/${courseId}/chapters`,
      method: 'post',
      data
    })
  },

  // 更新课程章节
  updateCourseChapter(courseId, chapterId, data) {
    return request({
      url: `/courses/${courseId}/chapters/${chapterId}`,
      method: 'put',
      data
    })
  },

  // 删除课程章节
  deleteCourseChapter(courseId, chapterId) {
    return request({
      url: `/courses/${courseId}/chapters/${chapterId}`,
      method: 'delete'
    })
  },

  // 获取课程作业
  getCourseAssignments(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/assignments`,
      method: 'get',
      params
    })
  },

  // 创建课程作业
  createCourseAssignment(courseId, data) {
    return request({
      url: `/courses/${courseId}/assignments`,
      method: 'post',
      data
    })
  },

  // 更新课程作业
  updateCourseAssignment(courseId, assignmentId, data) {
    return request({
      url: `/courses/${courseId}/assignments/${assignmentId}`,
      method: 'put',
      data
    })
  },

  // 删除课程作业
  deleteCourseAssignment(courseId, assignmentId) {
    return request({
      url: `/courses/${courseId}/assignments/${assignmentId}`,
      method: 'delete'
    })
  },

  // 获取课程考试
  getCourseExams(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/exams`,
      method: 'get',
      params
    })
  },

  // 创建课程考试
  createCourseExam(courseId, data) {
    return request({
      url: `/courses/${courseId}/exams`,
      method: 'post',
      data
    })
  },

  // 更新课程考试
  updateCourseExam(courseId, examId, data) {
    return request({
      url: `/courses/${courseId}/exams/${examId}`,
      method: 'put',
      data
    })
  },

  // 删除课程考试
  deleteCourseExam(courseId, examId) {
    return request({
      url: `/courses/${courseId}/exams/${examId}`,
      method: 'delete'
    })
  },

  // 获取课程公告
  getCourseAnnouncements(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/announcements`,
      method: 'get',
      params
    })
  },

  // 发布课程公告
  createCourseAnnouncement(courseId, data) {
    return request({
      url: `/courses/${courseId}/announcements`,
      method: 'post',
      data
    })
  },

  // 更新课程公告
  updateCourseAnnouncement(courseId, announcementId, data) {
    return request({
      url: `/courses/${courseId}/announcements/${announcementId}`,
      method: 'put',
      data
    })
  },

  // 删除课程公告
  deleteCourseAnnouncement(courseId, announcementId) {
    return request({
      url: `/courses/${courseId}/announcements/${announcementId}`,
      method: 'delete'
    })
  },

  // 获取课程讨论
  getCourseDiscussions(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/discussions`,
      method: 'get',
      params
    })
  },

  // 创建课程讨论
  createCourseDiscussion(courseId, data) {
    return request({
      url: `/courses/${courseId}/discussions`,
      method: 'post',
      data
    })
  },

  // 回复课程讨论
  replyCourseDiscussion(courseId, discussionId, data) {
    return request({
      url: `/courses/${courseId}/discussions/${discussionId}/reply`,
      method: 'post',
      data
    })
  },

  // 获取课程出勤记录
  getCourseAttendance(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/attendance`,
      method: 'get',
      params
    })
  },

  // 记录课程出勤
  recordCourseAttendance(courseId, data) {
    return request({
      url: `/courses/${courseId}/attendance`,
      method: 'post',
      data
    })
  },

  // 更新出勤记录
  updateCourseAttendance(courseId, attendanceId, data) {
    return request({
      url: `/courses/${courseId}/attendance/${attendanceId}`,
      method: 'put',
      data
    })
  },

  // 获取课程评价
  getCourseEvaluations(courseId, params = {}) {
    return request({
      url: `/courses/${courseId}/evaluations`,
      method: 'get',
      params
    })
  },

  // 提交课程评价
  submitCourseEvaluation(courseId, data) {
    return request({
      url: `/courses/${courseId}/evaluations`,
      method: 'post',
      data
    })
  },

  // 获取课程推荐
  getCourseRecommendations(params = {}) {
    return request({
      url: '/courses/recommendations',
      method: 'get',
      params
    })
  },

  // 收藏课程
  favoriteCourse(courseId) {
    return request({
      url: `/courses/${courseId}/favorite`,
      method: 'post'
    })
  },

  // 取消收藏课程
  unfavoriteCourse(courseId) {
    return request({
      url: `/courses/${courseId}/favorite`,
      method: 'delete'
    })
  },

  // 获取收藏的课程
  getFavoriteCourses(params = {}) {
    return request({
      url: '/courses/favorites',
      method: 'get',
      params
    })
  },

  // 导出课程数据
  exportCourseData(courseId, format = 'excel') {
    return request({
      url: `/courses/${courseId}/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // 导入课程数据
  importCourseData(formData) {
    return request({
      url: '/courses/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取课程模板
  getCourseTemplate() {
    return request({
      url: '/courses/template',
      method: 'get',
      responseType: 'blob'
    })
  },

  // 复制课程
  copyCourse(courseId, data) {
    return request({
      url: `/courses/${courseId}/copy`,
      method: 'post',
      data
    })
  },

  // 归档课程
  archiveCourse(courseId) {
    return request({
      url: `/courses/${courseId}/archive`,
      method: 'post'
    })
  },

  // 恢复课程
  restoreCourse(courseId) {
    return request({
      url: `/courses/${courseId}/restore`,
      method: 'post'
    })
  }
}

export default courseApi