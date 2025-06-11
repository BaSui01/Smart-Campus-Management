import request from '../request'
import { TEACHER_API_PATHS } from '../config'

/**
 * 教师端专用API模块
 * 包含教师日常教学和管理功能的所有接口
 */
export const teacherModule = {
  // ==================== 教师个人信息 ====================
  
  /**
   * 获取教师个人信息
   */
  getProfile() {
    return request({
      url: TEACHER_API_PATHS.PROFILE,
      method: 'get'
    })
  },

  /**
   * 更新教师个人信息
   */
  updateProfile(data) {
    return request({
      url: TEACHER_API_PATHS.PROFILE,
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
      url: `${TEACHER_API_PATHS.PROFILE}/avatar`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // ==================== 教师仪表盘 ====================
  
  /**
   * 获取教师仪表盘数据
   */
  getDashboard() {
    return request({
      url: TEACHER_API_PATHS.DASHBOARD,
      method: 'get'
    })
  },

  /**
   * 获取教学统计
   */
  getTeachingStats() {
    return request({
      url: `${TEACHER_API_PATHS.DASHBOARD}/teaching-stats`,
      method: 'get'
    })
  },

  /**
   * 获取最近教学活动
   */
  getRecentTeachingActivities(limit = 10) {
    return request({
      url: `${TEACHER_API_PATHS.DASHBOARD}/recent-activities`,
      method: 'get',
      params: { limit }
    })
  },

  // ==================== 课程管理 ====================
  
  /**
   * 获取教师课程列表
   */
  getCourses(params = {}) {
    return request({
      url: TEACHER_API_PATHS.COURSES,
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
      url: `${TEACHER_API_PATHS.COURSES}/${courseId}`,
      method: 'get'
    })
  },

  /**
   * 创建课程
   */
  createCourse(data) {
    return request({
      url: TEACHER_API_PATHS.COURSES,
      method: 'post',
      data
    })
  },

  /**
   * 更新课程信息
   */
  updateCourse(courseId, data) {
    return request({
      url: `${TEACHER_API_PATHS.COURSES}/${courseId}`,
      method: 'put',
      data
    })
  },

  /**
   * 获取课程学生列表
   */
  getCourseStudents(courseId, params = {}) {
    return request({
      url: `${TEACHER_API_PATHS.COURSES}/${courseId}/students`,
      method: 'get',
      params
    })
  },

  // ==================== 班级管理 ====================
  
  /**
   * 获取班级列表
   */
  getClasses(params = {}) {
    return request({
      url: TEACHER_API_PATHS.CLASSES,
      method: 'get',
      params
    })
  },

  /**
   * 获取班级详情
   */
  getClassDetail(classId) {
    return request({
      url: `${TEACHER_API_PATHS.CLASSES}/${classId}`,
      method: 'get'
    })
  },

  /**
   * 获取班级学生列表
   */
  getClassStudents(classId, params = {}) {
    return request({
      url: `${TEACHER_API_PATHS.CLASSES}/${classId}/students`,
      method: 'get',
      params
    })
  },

  // ==================== 学生管理 ====================
  
  /**
   * 获取学生列表
   */
  getStudents(params = {}) {
    return request({
      url: TEACHER_API_PATHS.STUDENTS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 获取学生详情
   */
  getStudentDetail(studentId) {
    return request({
      url: `${TEACHER_API_PATHS.STUDENTS}/${studentId}`,
      method: 'get'
    })
  },

  /**
   * 更新学生信息
   */
  updateStudent(studentId, data) {
    return request({
      url: `${TEACHER_API_PATHS.STUDENTS}/${studentId}`,
      method: 'put',
      data
    })
  },

  // ==================== 课程表管理 ====================
  
  /**
   * 获取教师课程表
   */
  getSchedule(params = {}) {
    return request({
      url: TEACHER_API_PATHS.SCHEDULE,
      method: 'get',
      params: {
        week: 'current',
        ...params
      }
    })
  },

  /**
   * 获取今日课程安排
   */
  getTodaySchedule() {
    return request({
      url: `${TEACHER_API_PATHS.SCHEDULE}/today`,
      method: 'get'
    })
  },

  /**
   * 创建课程安排
   */
  createSchedule(data) {
    return request({
      url: TEACHER_API_PATHS.SCHEDULE,
      method: 'post',
      data
    })
  },

  /**
   * 更新课程安排
   */
  updateSchedule(scheduleId, data) {
    return request({
      url: `${TEACHER_API_PATHS.SCHEDULE}/${scheduleId}`,
      method: 'put',
      data
    })
  },

  // ==================== 作业管理 ====================
  
  /**
   * 获取作业列表
   */
  getAssignments(params = {}) {
    return request({
      url: TEACHER_API_PATHS.ASSIGNMENTS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 创建作业
   */
  createAssignment(data) {
    return request({
      url: TEACHER_API_PATHS.ASSIGNMENTS,
      method: 'post',
      data
    })
  },

  /**
   * 更新作业
   */
  updateAssignment(assignmentId, data) {
    return request({
      url: `${TEACHER_API_PATHS.ASSIGNMENTS}/${assignmentId}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除作业
   */
  deleteAssignment(assignmentId) {
    return request({
      url: `${TEACHER_API_PATHS.ASSIGNMENTS}/${assignmentId}`,
      method: 'delete'
    })
  },

  /**
   * 获取作业提交列表
   */
  getAssignmentSubmissions(assignmentId, params = {}) {
    return request({
      url: `${TEACHER_API_PATHS.ASSIGNMENTS}/${assignmentId}/submissions`,
      method: 'get',
      params
    })
  },

  /**
   * 批改作业
   */
  gradeAssignment(submissionId, data) {
    return request({
      url: `${TEACHER_API_PATHS.ASSIGNMENTS}/submissions/${submissionId}/grade`,
      method: 'post',
      data
    })
  },

  /**
   * 批量批改作业
   */
  batchGradeAssignments(data) {
    return request({
      url: `${TEACHER_API_PATHS.ASSIGNMENTS}/batch-grade`,
      method: 'post',
      data
    })
  },

  // ==================== 考试管理 ====================
  
  /**
   * 获取考试列表
   */
  getExams(params = {}) {
    return request({
      url: TEACHER_API_PATHS.EXAMS,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 创建考试
   */
  createExam(data) {
    return request({
      url: TEACHER_API_PATHS.EXAMS,
      method: 'post',
      data
    })
  },

  /**
   * 更新考试
   */
  updateExam(examId, data) {
    return request({
      url: `${TEACHER_API_PATHS.EXAMS}/${examId}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除考试
   */
  deleteExam(examId) {
    return request({
      url: `${TEACHER_API_PATHS.EXAMS}/${examId}`,
      method: 'delete'
    })
  },

  /**
   * 发布考试
   */
  publishExam(examId) {
    return request({
      url: `${TEACHER_API_PATHS.EXAMS}/${examId}/publish`,
      method: 'post'
    })
  },

  // ==================== 成绩管理 ====================
  
  /**
   * 获取成绩列表
   */
  getGrades(params = {}) {
    return request({
      url: TEACHER_API_PATHS.GRADES,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 录入成绩
   */
  submitGrades(data) {
    return request({
      url: TEACHER_API_PATHS.GRADES,
      method: 'post',
      data
    })
  },

  /**
   * 批量录入成绩
   */
  batchSubmitGrades(data) {
    return request({
      url: `${TEACHER_API_PATHS.GRADES}/batch`,
      method: 'post',
      data
    })
  },

  /**
   * 更新成绩
   */
  updateGrade(gradeId, data) {
    return request({
      url: `${TEACHER_API_PATHS.GRADES}/${gradeId}`,
      method: 'put',
      data
    })
  },

  /**
   * 发布成绩
   */
  publishGrades(courseId, examType) {
    return request({
      url: `${TEACHER_API_PATHS.GRADES}/publish`,
      method: 'post',
      data: { courseId, examType }
    })
  },

  // ==================== 考勤管理 ====================
  
  /**
   * 获取考勤记录
   */
  getAttendance(params = {}) {
    return request({
      url: TEACHER_API_PATHS.ATTENDANCE,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 记录考勤
   */
  recordAttendance(data) {
    return request({
      url: TEACHER_API_PATHS.ATTENDANCE,
      method: 'post',
      data
    })
  },

  /**
   * 批量记录考勤
   */
  batchRecordAttendance(data) {
    return request({
      url: `${TEACHER_API_PATHS.ATTENDANCE}/batch`,
      method: 'post',
      data
    })
  },

  /**
   * 更新考勤记录
   */
  updateAttendance(attendanceId, data) {
    return request({
      url: `${TEACHER_API_PATHS.ATTENDANCE}/${attendanceId}`,
      method: 'put',
      data
    })
  },

  // ==================== 教学资源管理 ====================
  
  /**
   * 获取课程资源列表
   */
  getCourseResources(courseId, params = {}) {
    return request({
      url: `${TEACHER_API_PATHS.RESOURCES}/course/${courseId}`,
      method: 'get',
      params
    })
  },

  /**
   * 上传课程资源
   */
  uploadCourseResource(courseId, file, data = {}) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('courseId', courseId)
    Object.keys(data).forEach(key => {
      formData.append(key, data[key])
    })
    
    return request({
      url: `${TEACHER_API_PATHS.RESOURCES}/upload`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 删除课程资源
   */
  deleteCourseResource(resourceId) {
    return request({
      url: `${TEACHER_API_PATHS.RESOURCES}/${resourceId}`,
      method: 'delete'
    })
  },

  // ==================== 题库管理 ====================
  
  /**
   * 获取题库列表
   */
  getQuestionBank(params = {}) {
    return request({
      url: TEACHER_API_PATHS.QUESTION_BANK,
      method: 'get',
      params
    })
  },

  /**
   * 创建题目
   */
  createQuestion(data) {
    return request({
      url: TEACHER_API_PATHS.QUESTION_BANK,
      method: 'post',
      data
    })
  },

  /**
   * 更新题目
   */
  updateQuestion(questionId, data) {
    return request({
      url: `${TEACHER_API_PATHS.QUESTION_BANK}/${questionId}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除题目
   */
  deleteQuestion(questionId) {
    return request({
      url: `${TEACHER_API_PATHS.QUESTION_BANK}/${questionId}`,
      method: 'delete'
    })
  },

  /**
   * 导入题目
   */
  importQuestions(file) {
    const formData = new FormData()
    formData.append('file', file)
    
    return request({
      url: `${TEACHER_API_PATHS.QUESTION_BANK}/import`,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // ==================== 教学评价管理 ====================
  
  /**
   * 获取教学评价结果
   */
  getEvaluationResults(params = {}) {
    return request({
      url: TEACHER_API_PATHS.EVALUATIONS,
      method: 'get',
      params
    })
  },

  /**
   * 获取详细评价报告
   */
  getDetailedEvaluationReport(courseId, semester) {
    return request({
      url: `${TEACHER_API_PATHS.EVALUATIONS}/${courseId}/${semester}`,
      method: 'get'
    })
  },

  // ==================== 科研管理 ====================
  
  /**
   * 获取科研项目列表
   */
  getResearchProjects(params = {}) {
    return request({
      url: TEACHER_API_PATHS.RESEARCH,
      method: 'get',
      params
    })
  },

  /**
   * 创建科研项目
   */
  createResearchProject(data) {
    return request({
      url: TEACHER_API_PATHS.RESEARCH,
      method: 'post',
      data
    })
  },

  /**
   * 更新科研项目
   */
  updateResearchProject(projectId, data) {
    return request({
      url: `${TEACHER_API_PATHS.RESEARCH}/${projectId}`,
      method: 'put',
      data
    })
  }
}

export default teacherModule