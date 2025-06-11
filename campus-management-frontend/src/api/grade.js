import request from './request'

/**
 * 成绩管理API
 * 基于后端 GradeController 等接口实现
 */
export const gradeApi = {
  // ==================== 成绩基础管理 ====================

  /**
   * 获取成绩列表
   * 对应后端: GET /api/v1/grades
   */
  getGradeList(params = {}) {
    return request({
      url: '/grades',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取成绩详情
   * 对应后端: GET /api/v1/grades/{id}
   */
  getGradeById(id) {
    return request({
      url: `/grades/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建成绩记录
   * 对应后端: POST /api/v1/grades
   */
  createGrade(data) {
    return request({
      url: '/grades',
      method: 'post',
      data
    })
  },

  /**
   * 更新成绩信息
   * 对应后端: PUT /api/v1/grades/{id}
   */
  updateGrade(id, data) {
    return request({
      url: `/grades/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除成绩记录
   * 对应后端: DELETE /api/v1/grades/{id}
   */
  deleteGrade(id) {
    return request({
      url: `/grades/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量录入成绩
   * 对应后端: POST /api/v1/grades/batch
   */
  batchCreateGrades(data) {
    return request({
      url: '/grades/batch',
      method: 'post',
      data
    })
  },

  /**
   * 批量更新成绩
   * 对应后端: PUT /api/v1/grades/batch
   */
  batchUpdateGrades(data) {
    return request({
      url: '/grades/batch',
      method: 'put',
      data
    })
  },

  /**
   * 批量删除成绩
   * 对应后端: DELETE /api/v1/grades/batch
   */
  batchDeleteGrades(gradeIds) {
    return request({
      url: '/grades/batch',
      method: 'delete',
      data: { gradeIds }
    })
  },

  // ==================== 学生成绩查询 ====================
  // 获取学生成绩列表
  getStudentGrades(params = {}) {
    return request({
      url: '/grades/student',
      method: 'get',
      params
    })
  },

  // 获取教师管理的成绩列表
  getTeacherGrades(params = {}) {
    return request({
      url: '/grades/teacher',
      method: 'get',
      params
    })
  },

  // 获取成绩详情
  getGradeDetail(gradeId) {
    return request({
      url: `/grades/${gradeId}`,
      method: 'get'
    })
  },

  // 录入成绩
  submitGrade(data) {
    return request({
      url: '/grades',
      method: 'post',
      data
    })
  },

  // 批量录入成绩
  batchSubmitGrades(data) {
    return request({
      url: '/grades/batch',
      method: 'post',
      data
    })
  },

  // 更新成绩
  updateGrade(gradeId, data) {
    return request({
      url: `/grades/${gradeId}`,
      method: 'put',
      data
    })
  },

  // 删除成绩
  deleteGrade(gradeId) {
    return request({
      url: `/grades/${gradeId}`,
      method: 'delete'
    })
  },

  // 发布成绩
  publishGrades(courseId, examType) {
    return request({
      url: '/grades/publish',
      method: 'post',
      data: { courseId, examType }
    })
  },

  // 撤回成绩发布
  unpublishGrades(courseId, examType) {
    return request({
      url: '/grades/unpublish',
      method: 'post',
      data: { courseId, examType }
    })
  },

  // 获取最新成绩
  getRecentGrades(params = {}) {
    return request({
      url: '/grades/recent',
      method: 'get',
      params
    })
  },

  // 获取成绩统计
  getGradeStats(params = {}) {
    return request({
      url: '/grades/stats',
      method: 'get',
      params
    })
  },

  // 获取成绩趋势
  getGradeTrend(params = {}) {
    return request({
      url: '/grades/trend',
      method: 'get',
      params
    })
  },

  // 获取课程成绩统计
  getCourseGradeStats(courseId, params = {}) {
    return request({
      url: `/grades/course/${courseId}/stats`,
      method: 'get',
      params
    })
  },

  // 获取班级成绩统计
  getClassGradeStats(classId, params = {}) {
    return request({
      url: `/grades/class/${classId}/stats`,
      method: 'get',
      params
    })
  },

  // 获取年级成绩统计
  getGradeGradeStats(gradeLevel, params = {}) {
    return request({
      url: `/grades/grade-level/${gradeLevel}/stats`,
      method: 'get',
      params
    })
  },

  // 获取成绩分布
  getGradeDistribution(courseId, examType) {
    return request({
      url: '/grades/distribution',
      method: 'get',
      params: { courseId, examType }
    })
  },

  // 获取排名信息
  getRankingInfo(params = {}) {
    return request({
      url: '/grades/ranking',
      method: 'get',
      params
    })
  },

  // 获取GPA计算规则
  getGPACalculationRules() {
    return request({
      url: '/grades/gpa-rules',
      method: 'get'
    })
  },

  // 计算GPA
  calculateGPA(studentId, params = {}) {
    return request({
      url: `/grades/calculate-gpa/${studentId}`,
      method: 'get',
      params
    })
  },

  // 获取成绩分析报告
  getGradeAnalysisReport(studentId, params = {}) {
    return request({
      url: `/grades/analysis-report/${studentId}`,
      method: 'get',
      params
    })
  },

  // 获取课程成绩对比
  getCourseGradeComparison(courseId, params = {}) {
    return request({
      url: `/grades/course-comparison/${courseId}`,
      method: 'get',
      params
    })
  },

  // 获取学期成绩汇总
  getSemesterGradeSummary(studentId, semester) {
    return request({
      url: `/grades/semester-summary/${studentId}/${semester}`,
      method: 'get'
    })
  },

  // 获取毕业成绩汇总
  getGraduationGradeSummary(studentId) {
    return request({
      url: `/grades/graduation-summary/${studentId}`,
      method: 'get'
    })
  },

  // 申请成绩复查
  applyGradeReview(gradeId, data) {
    return request({
      url: `/grades/${gradeId}/review`,
      method: 'post',
      data
    })
  },

  // 获取成绩复查申请列表
  getGradeReviewApplications(params = {}) {
    return request({
      url: '/grades/review-applications',
      method: 'get',
      params
    })
  },

  // 处理成绩复查申请
  handleGradeReviewApplication(applicationId, data) {
    return request({
      url: `/grades/review-applications/${applicationId}`,
      method: 'put',
      data
    })
  },

  // 导出成绩单
  exportGradeReport(studentId, params = {}) {
    return request({
      url: `/grades/export-report/${studentId}`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 导出课程成绩
  exportCourseGrades(courseId, params = {}) {
    return request({
      url: `/grades/export-course/${courseId}`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 导出班级成绩
  exportClassGrades(classId, params = {}) {
    return request({
      url: `/grades/export-class/${classId}`,
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 导入成绩
  importGrades(formData) {
    return request({
      url: '/grades/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取成绩导入模板
  getGradeImportTemplate(courseId, examType) {
    return request({
      url: '/grades/import-template',
      method: 'get',
      params: { courseId, examType },
      responseType: 'blob'
    })
  },

  // 验证导入数据
  validateImportData(formData) {
    return request({
      url: '/grades/validate-import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取成绩等级标准
  getGradeStandards() {
    return request({
      url: '/grades/standards',
      method: 'get'
    })
  },

  // 更新成绩等级标准
  updateGradeStandards(data) {
    return request({
      url: '/grades/standards',
      method: 'put',
      data
    })
  },

  // 获取考试类型列表
  getExamTypes() {
    return request({
      url: '/grades/exam-types',
      method: 'get'
    })
  },

  // 创建考试类型
  createExamType(data) {
    return request({
      url: '/grades/exam-types',
      method: 'post',
      data
    })
  },

  // 更新考试类型
  updateExamType(examTypeId, data) {
    return request({
      url: `/grades/exam-types/${examTypeId}`,
      method: 'put',
      data
    })
  },

  // 删除考试类型
  deleteExamType(examTypeId) {
    return request({
      url: `/grades/exam-types/${examTypeId}`,
      method: 'delete'
    })
  },

  // 获取成绩预警设置
  getGradeWarningSettings() {
    return request({
      url: '/grades/warning-settings',
      method: 'get'
    })
  },

  // 更新成绩预警设置
  updateGradeWarningSettings(data) {
    return request({
      url: '/grades/warning-settings',
      method: 'put',
      data
    })
  },

  // 获取成绩预警列表
  getGradeWarnings(params = {}) {
    return request({
      url: '/grades/warnings',
      method: 'get',
      params
    })
  },

  // 处理成绩预警
  handleGradeWarning(warningId, data) {
    return request({
      url: `/grades/warnings/${warningId}`,
      method: 'put',
      data
    })
  },

  // 获取补考信息
  getMakeupExamInfo(params = {}) {
    return request({
      url: '/grades/makeup-exams',
      method: 'get',
      params
    })
  },

  // 申请补考
  applyMakeupExam(gradeId, data) {
    return request({
      url: `/grades/${gradeId}/makeup-exam`,
      method: 'post',
      data
    })
  },

  // 录入补考成绩
  submitMakeupGrade(gradeId, data) {
    return request({
      url: `/grades/${gradeId}/makeup-grade`,
      method: 'post',
      data
    })
  },

  // 获取重修信息
  getRetakeInfo(params = {}) {
    return request({
      url: '/grades/retakes',
      method: 'get',
      params
    })
  },

  // 申请重修
  applyRetake(courseId, data) {
    return request({
      url: `/grades/retakes/${courseId}`,
      method: 'post',
      data
    })
  },

  // 获取学位资格审查信息
  getDegreeEligibility(studentId) {
    return request({
      url: `/grades/degree-eligibility/${studentId}`,
      method: 'get'
    })
  },

  // 生成成绩证明
  generateGradeCertificate(studentId, data) {
    return request({
      url: `/grades/certificate/${studentId}`,
      method: 'post',
      data,
      responseType: 'blob'
    })
  }
}

export default gradeApi