import request from './request'

/**
 * 教师相关API
 * 基于后端 TeacherController 等接口实现
 */
export const teacherApi = {
  // ==================== 教师基础信息管理 ====================

  /**
   * 获取教师列表
   * 对应后端: GET /api/v1/teachers
   */
  getTeacherList(params = {}) {
    return request({
      url: '/teachers',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取教师详情
   * 对应后端: GET /api/v1/teachers/{id}
   */
  getTeacherById(id) {
    return request({
      url: `/teachers/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建教师
   * 对应后端: POST /api/v1/teachers
   */
  createTeacher(data) {
    return request({
      url: '/teachers',
      method: 'post',
      data
    })
  },

  /**
   * 更新教师信息
   * 对应后端: PUT /api/v1/teachers/{id}
   */
  updateTeacher(id, data) {
    return request({
      url: `/teachers/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除教师
   * 对应后端: DELETE /api/v1/teachers/{id}
   */
  deleteTeacher(id) {
    return request({
      url: `/teachers/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量更新教师状态
   * 对应后端: PUT /api/v1/teachers/batch/status
   */
  batchUpdateTeacherStatus(data) {
    return request({
      url: '/teachers/batch/status',
      method: 'put',
      data
    })
  },

  // ==================== 教师个人信息管理 ====================
  // 获取教师个人信息
  getProfile() {
    return request({
      url: '/teacher/profile',
      method: 'get'
    })
  },

  // 更新教师个人信息
  updateProfile(data) {
    return request({
      url: '/teacher/profile',
      method: 'put',
      data
    })
  },

  // 上传教师头像
  uploadAvatar(formData) {
    return request({
      url: '/teacher/profile/avatar',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取教师仪表盘统计
  getDashboardStats() {
    return request({
      url: '/teacher/dashboard/stats',
      method: 'get'
    })
  },

  // 获取教师统计数据
  getTeacherStats() {
    return request({
      url: '/teacher/stats',
      method: 'get'
    })
  },

  // 获取今日课程
  getTodayCourses() {
    return request({
      url: '/teacher/today-courses',
      method: 'get'
    })
  },

  // 获取待处理事项
  getPendingTasks() {
    return request({
      url: '/teacher/pending-tasks',
      method: 'get'
    })
  },

  // 获取最近成绩
  getRecentGrades() {
    return request({
      url: '/teacher/recent-grades',
      method: 'get'
    })
  },

  // 获取教师课程统计
  getCourseStats() {
    return request({
      url: '/teacher/course-stats',
      method: 'get'
    })
  },

  // 获取教师课程列表
  getCourses(params = {}) {
    return request({
      url: '/teacher/courses',
      method: 'get',
      params
    })
  },

  // 获取课程详情
  getCourseDetail(courseId) {
    return request({
      url: `/teacher/courses/${courseId}`,
      method: 'get'
    })
  },

  // 创建课程
  createCourse(data) {
    return request({
      url: '/teacher/courses',
      method: 'post',
      data
    })
  },

  // 更新课程
  updateCourse(courseId, data) {
    return request({
      url: `/teacher/courses/${courseId}`,
      method: 'put',
      data
    })
  },

  // 删除课程
  deleteCourse(courseId) {
    return request({
      url: `/teacher/courses/${courseId}`,
      method: 'delete'
    })
  },

  // ==================== 学生管理 ====================

  // 获取学生统计
  getStudentStats() {
    return request({
      url: '/teacher/student-stats',
      method: 'get'
    })
  },

  // 获取教师班级列表
  getTeacherClasses() {
    return request({
      url: '/teacher/classes',
      method: 'get'
    })
  },

  // 获取学生列表
  getStudents(params = {}) {
    return request({
      url: '/teacher/students',
      method: 'get',
      params
    })
  },

  // 获取学生详情
  getStudentDetail(studentId) {
    return request({
      url: `/teacher/students/${studentId}`,
      method: 'get'
    })
  },

  // 更新学生信息
  updateStudent(studentId, data) {
    return request({
      url: `/teacher/students/${studentId}`,
      method: 'put',
      data
    })
  },

  // 获取学生管理列表
  getStudentList(params = {}) {
    return request({
      url: '/teacher/students',
      method: 'get',
      params
    })
  },

  // 获取学生详细信息
  getStudentDetail(studentId) {
    return request({
      url: `/teacher/students/${studentId}`,
      method: 'get'
    })
  },

  // 更新学生信息
  updateStudentInfo(studentId, data) {
    return request({
      url: `/teacher/students/${studentId}`,
      method: 'put',
      data
    })
  },

  // 获取班级列表
  getClassList(params = {}) {
    return request({
      url: '/teacher/classes',
      method: 'get',
      params
    })
  },

  // 获取班级详情
  getClassDetail(classId) {
    return request({
      url: `/teacher/classes/${classId}`,
      method: 'get'
    })
  },

  // 创建班级
  createClass(data) {
    return request({
      url: '/teacher/classes',
      method: 'post',
      data
    })
  },

  // 更新班级信息
  updateClass(classId, data) {
    return request({
      url: `/teacher/classes/${classId}`,
      method: 'put',
      data
    })
  },

  // 删除班级
  deleteClass(classId) {
    return request({
      url: `/teacher/classes/${classId}`,
      method: 'delete'
    })
  },

  // 获取课程安排
  getCourseSchedule(params = {}) {
    return request({
      url: '/teacher/course-schedule',
      method: 'get',
      params
    })
  },

  // 创建课程安排
  createCourseSchedule(data) {
    return request({
      url: '/teacher/course-schedule',
      method: 'post',
      data
    })
  },

  // 更新课程安排
  updateCourseSchedule(scheduleId, data) {
    return request({
      url: `/teacher/course-schedule/${scheduleId}`,
      method: 'put',
      data
    })
  },

  // 删除课程安排
  deleteCourseSchedule(scheduleId) {
    return request({
      url: `/teacher/course-schedule/${scheduleId}`,
      method: 'delete'
    })
  },

  // 获取考勤记录
  getAttendanceRecords(params = {}) {
    return request({
      url: '/teacher/attendance',
      method: 'get',
      params
    })
  },

  // 记录考勤
  recordAttendance(data) {
    return request({
      url: '/teacher/attendance',
      method: 'post',
      data
    })
  },

  // 批量记录考勤
  batchRecordAttendance(data) {
    return request({
      url: '/teacher/attendance/batch',
      method: 'post',
      data
    })
  },

  // 更新考勤记录
  updateAttendance(attendanceId, data) {
    return request({
      url: `/teacher/attendance/${attendanceId}`,
      method: 'put',
      data
    })
  },

  // 获取作业列表
  getAssignmentList(params = {}) {
    return request({
      url: '/teacher/assignments',
      method: 'get',
      params
    })
  },

  // 创建作业
  createAssignment(data) {
    return request({
      url: '/teacher/assignments',
      method: 'post',
      data
    })
  },

  // 更新作业
  updateAssignment(assignmentId, data) {
    return request({
      url: `/teacher/assignments/${assignmentId}`,
      method: 'put',
      data
    })
  },

  // 删除作业
  deleteAssignment(assignmentId) {
    return request({
      url: `/teacher/assignments/${assignmentId}`,
      method: 'delete'
    })
  },

  // 获取作业提交列表
  getAssignmentSubmissions(assignmentId, params = {}) {
    return request({
      url: `/teacher/assignments/${assignmentId}/submissions`,
      method: 'get',
      params
    })
  },

  // 批改作业
  gradeAssignment(submissionId, data) {
    return request({
      url: `/teacher/assignment-submissions/${submissionId}/grade`,
      method: 'post',
      data
    })
  },

  // 批量批改作业
  batchGradeAssignments(data) {
    return request({
      url: '/teacher/assignment-submissions/batch-grade',
      method: 'post',
      data
    })
  },

  // 获取考试列表
  getExamList(params = {}) {
    return request({
      url: '/teacher/exams',
      method: 'get',
      params
    })
  },

  // 创建考试
  createExam(data) {
    return request({
      url: '/teacher/exams',
      method: 'post',
      data
    })
  },

  // 更新考试
  updateExam(examId, data) {
    return request({
      url: `/teacher/exams/${examId}`,
      method: 'put',
      data
    })
  },

  // 删除考试
  deleteExam(examId) {
    return request({
      url: `/teacher/exams/${examId}`,
      method: 'delete'
    })
  },

  // 获取试题库
  getQuestionBank(params = {}) {
    return request({
      url: '/teacher/question-bank',
      method: 'get',
      params
    })
  },

  // 创建试题
  createQuestion(data) {
    return request({
      url: '/teacher/question-bank',
      method: 'post',
      data
    })
  },

  // 更新试题
  updateQuestion(questionId, data) {
    return request({
      url: `/teacher/question-bank/${questionId}`,
      method: 'put',
      data
    })
  },

  // 删除试题
  deleteQuestion(questionId) {
    return request({
      url: `/teacher/question-bank/${questionId}`,
      method: 'delete'
    })
  },

  // 导入试题
  importQuestions(formData) {
    return request({
      url: '/teacher/question-bank/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 生成试卷
  generateExamPaper(data) {
    return request({
      url: '/teacher/exam-papers/generate',
      method: 'post',
      data
    })
  },

  // 获取教学计划
  getTeachingPlan(params = {}) {
    return request({
      url: '/teacher/teaching-plan',
      method: 'get',
      params
    })
  },

  // 创建教学计划
  createTeachingPlan(data) {
    return request({
      url: '/teacher/teaching-plan',
      method: 'post',
      data
    })
  },

  // 更新教学计划
  updateTeachingPlan(planId, data) {
    return request({
      url: `/teacher/teaching-plan/${planId}`,
      method: 'put',
      data
    })
  },

  // 获取课程资源
  getCourseResources(courseId, params = {}) {
    return request({
      url: `/teacher/courses/${courseId}/resources`,
      method: 'get',
      params
    })
  },

  // 上传课程资源
  uploadCourseResource(courseId, formData) {
    return request({
      url: `/teacher/courses/${courseId}/resources`,
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
      url: `/teacher/courses/${courseId}/resources/${resourceId}`,
      method: 'delete'
    })
  },

  // 获取学生评教结果
  getEvaluationResults(params = {}) {
    return request({
      url: '/teacher/evaluation-results',
      method: 'get',
      params
    })
  },

  // 获取详细评教报告
  getDetailedEvaluationReport(courseId, semester) {
    return request({
      url: `/teacher/evaluation-report/${courseId}/${semester}`,
      method: 'get'
    })
  },

  // 获取同事列表
  getColleagueList(params = {}) {
    return request({
      url: '/teacher/colleagues',
      method: 'get',
      params
    })
  },

  // 获取部门信息
  getDepartmentInfo() {
    return request({
      url: '/teacher/department',
      method: 'get'
    })
  },

  // 申请调课
  applyCourseAdjustment(data) {
    return request({
      url: '/teacher/course-adjustment',
      method: 'post',
      data
    })
  },

  // 获取调课申请列表
  getCourseAdjustmentList(params = {}) {
    return request({
      url: '/teacher/course-adjustments',
      method: 'get',
      params
    })
  },

  // 处理调课申请
  handleCourseAdjustment(adjustmentId, data) {
    return request({
      url: `/teacher/course-adjustments/${adjustmentId}`,
      method: 'put',
      data
    })
  },

  // 申请请假
  applyLeave(data) {
    return request({
      url: '/teacher/leave-application',
      method: 'post',
      data
    })
  },

  // 获取请假申请列表
  getLeaveApplicationList(params = {}) {
    return request({
      url: '/teacher/leave-applications',
      method: 'get',
      params
    })
  },

  // 获取工作量统计
  getWorkloadStats(params = {}) {
    return request({
      url: '/teacher/workload-stats',
      method: 'get',
      params
    })
  },

  // 获取薪资信息
  getSalaryInfo(params = {}) {
    return request({
      url: '/teacher/salary',
      method: 'get',
      params
    })
  },

  // 获取科研项目
  getResearchProjects(params = {}) {
    return request({
      url: '/teacher/research-projects',
      method: 'get',
      params
    })
  },

  // 创建科研项目
  createResearchProject(data) {
    return request({
      url: '/teacher/research-projects',
      method: 'post',
      data
    })
  },

  // 更新科研项目
  updateResearchProject(projectId, data) {
    return request({
      url: `/teacher/research-projects/${projectId}`,
      method: 'put',
      data
    })
  },

  // 获取学术论文
  getAcademicPapers(params = {}) {
    return request({
      url: '/teacher/academic-papers',
      method: 'get',
      params
    })
  },

  // 提交学术论文
  submitAcademicPaper(data) {
    return request({
      url: '/teacher/academic-papers',
      method: 'post',
      data
    })
  },

  // 获取培训记录
  getTrainingRecords(params = {}) {
    return request({
      url: '/teacher/training-records',
      method: 'get',
      params
    })
  },

  // 报名培训
  applyTraining(trainingId, data) {
    return request({
      url: `/teacher/trainings/${trainingId}/apply`,
      method: 'post',
      data
    })
  },

  // 获取教学评估
  getTeachingAssessment(params = {}) {
    return request({
      url: '/teacher/teaching-assessment',
      method: 'get',
      params
    })
  },

  // 提交自评报告
  submitSelfAssessment(data) {
    return request({
      url: '/teacher/self-assessment',
      method: 'post',
      data
    })
  },

  // 获取荣誉奖项
  getHonorsAndAwards(params = {}) {
    return request({
      url: '/teacher/honors-awards',
      method: 'get',
      params
    })
  },

  // 申请荣誉奖项
  applyHonorAward(data) {
    return request({
      url: '/teacher/honors-awards',
      method: 'post',
      data
    })
  },

  // 获取学生指导记录
  getStudentGuidanceRecords(params = {}) {
    return request({
      url: '/teacher/student-guidance',
      method: 'get',
      params
    })
  },

  // 添加学生指导记录
  addStudentGuidanceRecord(data) {
    return request({
      url: '/teacher/student-guidance',
      method: 'post',
      data
    })
  },

  // 修改密码
  changePassword(data) {
    return request({
      url: '/teacher/change-password',
      method: 'post',
      data
    })
  },

  // 导出教师数据
  exportTeacherData(format = 'pdf') {
    return request({
      url: '/teacher/export-data',
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // 生成教学报告
  generateTeachingReport(params = {}) {
    return request({
      url: '/teacher/teaching-report',
      method: 'get',
      params,
      responseType: 'blob'
    })
  }
}

export default teacherApi