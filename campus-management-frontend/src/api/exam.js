import request from './request'

/**
 * 考试管理API
 * 基于后端 ExamController、ExamRecordController、ExamQuestionController 等接口实现
 */
export const examApi = {
  // ==================== 考试基础管理 ====================

  /**
   * 获取考试列表
   * 对应后端: GET /api/v1/exams
   */
  getExamList(params = {}) {
    return request({
      url: '/v1/exams',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取考试详情
   * 对应后端: GET /api/v1/exams/{id}
   */
  getExamById(id) {
    return request({
      url: `/v1/exams/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建考试
   * 对应后端: POST /api/v1/exams
   */
  createExam(data) {
    return request({
      url: '/v1/exams',
      method: 'post',
      data
    })
  },

  /**
   * 更新考试信息
   * 对应后端: PUT /api/v1/exams/{id}
   */
  updateExam(id, data) {
    return request({
      url: `/v1/exams/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除考试
   * 对应后端: DELETE /api/v1/exams/{id}
   */
  deleteExam(id) {
    return request({
      url: `/v1/exams/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量发布考试
   * 对应后端: PUT /api/v1/exams/batch/publish
   */
  batchPublishExams(examIds) {
    return request({
      url: '/v1/exams/batch/publish',
      method: 'put',
      data: examIds
    })
  },

  /**
   * 发布考试
   * 对应后端: PUT /api/v1/exams/{id}/publish
   */
  publishExam(id) {
    return request({
      url: `/v1/exams/${id}/publish`,
      method: 'put'
    })
  },

  /**
   * 取消发布考试
   * 对应后端: PUT /api/v1/exams/{id}/unpublish
   */
  unpublishExam(id) {
    return request({
      url: `/v1/exams/${id}/unpublish`,
      method: 'put'
    })
  },

  // ==================== 考试记录管理 ====================

  /**
   * 获取考试记录列表
   * 对应后端: GET /api/v1/exam-records
   */
  getExamRecordList(params = {}) {
    return request({
      url: '/v1/exam-records',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取考试记录详情
   * 对应后端: GET /api/v1/exam-records/{id}
   */
  getExamRecordById(id) {
    return request({
      url: `/v1/exam-records/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建考试记录
   * 对应后端: POST /api/v1/exam-records
   */
  createExamRecord(data) {
    return request({
      url: '/v1/exam-records',
      method: 'post',
      data
    })
  },

  /**
   * 更新考试记录
   * 对应后端: PUT /api/v1/exam-records/{id}
   */
  updateExamRecord(id, data) {
    return request({
      url: `/v1/exam-records/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除考试记录
   * 对应后端: DELETE /api/v1/exam-records/{id}
   */
  deleteExamRecord(id) {
    return request({
      url: `/v1/exam-records/${id}`,
      method: 'delete'
    })
  },

  /**
   * 提交考试
   * 对应后端: PUT /api/v1/exam-records/{id}/submit
   */
  submitExam(id, data) {
    return request({
      url: `/v1/exam-records/${id}/submit`,
      method: 'put',
      data
    })
  },

  /**
   * 开始考试
   * 对应后端: POST /api/v1/exam-records/{examId}/start
   */
  startExam(examId, data = {}) {
    return request({
      url: `/v1/exam-records/${examId}/start`,
      method: 'post',
      data
    })
  },

  /**
   * 暂停考试
   * 对应后端: PUT /api/v1/exam-records/{id}/pause
   */
  pauseExam(id) {
    return request({
      url: `/v1/exam-records/${id}/pause`,
      method: 'put'
    })
  },

  /**
   * 恢复考试
   * 对应后端: PUT /api/v1/exam-records/{id}/resume
   */
  resumeExam(id) {
    return request({
      url: `/v1/exam-records/${id}/resume`,
      method: 'put'
    })
  },
  // ==================== 基础CRUD操作 ====================

  /**
   * 分页查询考试列表
   */
  getExams(params = {}) {
    return request({
      url: '/api/v1/exams',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        sortBy: 'startTime',
        sortDir: 'desc',
        ...params
      }
    })
  },

  /**
   * 根据ID查询考试详情
   */
  getExamById(id) {
    return request({
      url: `/api/v1/exams/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建考试
   */
  createExam(data) {
    return request({
      url: '/api/v1/exams',
      method: 'post',
      data
    })
  },

  /**
   * 更新考试
   */
  updateExam(id, data) {
    return request({
      url: `/api/v1/exams/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除考试
   */
  deleteExam(id) {
    return request({
      url: `/api/v1/exams/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除考试
   */
  deleteExams(ids) {
    return request({
      url: '/api/v1/exams/batch',
      method: 'delete',
      data: ids
    })
  },

  // ==================== 业务操作 ====================

  /**
   * 发布考试
   */
  publishExam(id) {
    return request({
      url: `/api/v1/exams/${id}/publish`,
      method: 'post'
    })
  },

  /**
   * 取消发布考试
   */
  unpublishExam(id) {
    return request({
      url: `/api/v1/exams/${id}/unpublish`,
      method: 'post'
    })
  },

  /**
   * 开始考试
   */
  startExam(id) {
    return request({
      url: `/api/v1/exams/${id}/start`,
      method: 'post'
    })
  },

  /**
   * 结束考试
   */
  endExam(id) {
    return request({
      url: `/api/v1/exams/${id}/end`,
      method: 'post'
    })
  },

  /**
   * 延长考试时间
   */
  extendExamTime(id, additionalMinutes) {
    return request({
      url: `/api/v1/exams/${id}/extend`,
      method: 'post',
      params: { additionalMinutes }
    })
  },

  /**
   * 复制考试
   */
  copyExam(id, newCourseId) {
    return request({
      url: `/api/v1/exams/${id}/copy`,
      method: 'post',
      params: { newCourseId }
    })
  },

  // ==================== 题目管理 ====================

  /**
   * 获取考试题目列表
   */
  getExamQuestions(examId, params = {}) {
    return request({
      url: `/api/v1/exams/${examId}/questions`,
      method: 'get',
      params: {
        page: 1,
        size: 50,
        ...params
      }
    })
  },

  /**
   * 添加考试题目
   */
  addExamQuestion(examId, questionData) {
    return request({
      url: `/api/v1/exams/${examId}/questions`,
      method: 'post',
      data: questionData
    })
  },

  /**
   * 更新考试题目
   */
  updateExamQuestion(questionId, questionData) {
    return request({
      url: `/api/v1/exams/questions/${questionId}`,
      method: 'put',
      data: questionData
    })
  },

  /**
   * 删除考试题目
   */
  deleteExamQuestion(questionId) {
    return request({
      url: `/api/v1/exams/questions/${questionId}`,
      method: 'delete'
    })
  },

  /**
   * 批量导入题目
   */
  importQuestions(examId, questions) {
    return request({
      url: `/api/v1/exams/${examId}/questions/import`,
      method: 'post',
      data: questions
    })
  },

  /**
   * 随机生成题目
   */
  generateRandomQuestions(examId, questionCount) {
    return request({
      url: `/api/v1/exams/${examId}/questions/generate`,
      method: 'post',
      params: { questionCount }
    })
  },

  /**
   * 调整题目顺序
   */
  reorderQuestions(examId, questionIds) {
    return request({
      url: `/api/v1/exams/${examId}/questions/reorder`,
      method: 'post',
      data: questionIds
    })
  },

  // ==================== 学生端操作 ====================

  /**
   * 获取学生的考试列表
   */
  getStudentExams(params = {}) {
    return request({
      url: '/api/v1/exams/student',
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
   * 获取学生考试详情
   */
  getStudentExamDetail(examId) {
    return request({
      url: `/api/v1/exams/${examId}/student`,
      method: 'get'
    })
  },

  /**
   * 开始学生考试
   */
  startStudentExam(examId) {
    return request({
      url: `/api/v1/exams/${examId}/student/start`,
      method: 'post'
    })
  },

  /**
   * 提交学生考试
   */
  submitStudentExam(recordId, answers) {
    return request({
      url: `/api/v1/exams/records/${recordId}/submit`,
      method: 'post',
      data: { answers }
    })
  },

  /**
   * 保存考试答案（临时保存）
   */
  saveExamAnswers(recordId, answers) {
    return request({
      url: `/api/v1/exams/records/${recordId}/save`,
      method: 'post',
      data: { answers }
    })
  },

  /**
   * 获取考试记录
   */
  getExamRecord(recordId) {
    return request({
      url: `/api/v1/exams/records/${recordId}`,
      method: 'get'
    })
  },

  /**
   * 获取考试结果
   */
  getExamResult(recordId) {
    return request({
      url: `/api/v1/exams/records/${recordId}/result`,
      method: 'get'
    })
  },

  // ==================== 教师端操作 ====================

  /**
   * 获取教师的考试列表
   */
  getTeacherExams(params = {}) {
    return request({
      url: '/api/v1/exams/teacher',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        courseId: null,
        status: 'all',
        ...params
      }
    })
  },

  /**
   * 获取考试记录列表
   */
  getExamRecords(examId, params = {}) {
    return request({
      url: `/api/v1/exams/${examId}/records`,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all', // all, submitted, graded
        ...params
      }
    })
  },

  /**
   * 批改考试
   */
  gradeExamRecord(recordId, data) {
    return request({
      url: `/api/v1/exams/records/${recordId}/grade`,
      method: 'post',
      data: {
        score: data.score,
        feedback: data.feedback,
        questionScores: data.questionScores,
        ...data
      }
    })
  },

  /**
   * 批量批改考试
   */
  batchGradeExamRecords(records) {
    return request({
      url: '/api/v1/exams/records/batch-grade',
      method: 'post',
      data: records
    })
  },

  /**
   * 导出考试记录
   */
  exportExamRecords(examId, format = 'excel') {
    return request({
      url: `/api/v1/exams/${examId}/records/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // ==================== 统计分析 ====================

  /**
   * 获取考试统计信息
   */
  getExamStatistics(examId) {
    return request({
      url: `/api/v1/exams/${examId}/statistics`,
      method: 'get'
    })
  },

  /**
   * 获取考试成绩分布
   */
  getExamScoreDistribution(examId) {
    return request({
      url: `/api/v1/exams/${examId}/score-distribution`,
      method: 'get'
    })
  },

  /**
   * 获取课程考试统计
   */
  getCourseExamStatistics(courseId, params = {}) {
    return request({
      url: `/api/v1/exams/statistics/course/${courseId}`,
      method: 'get',
      params
    })
  },

  /**
   * 获取学生考试统计
   */
  getStudentExamStatistics(studentId, params = {}) {
    return request({
      url: `/api/v1/exams/statistics/student/${studentId}`,
      method: 'get',
      params
    })
  },

  /**
   * 获取教师考试统计
   */
  getTeacherExamStatistics(teacherId, params = {}) {
    return request({
      url: `/api/v1/exams/statistics/teacher/${teacherId}`,
      method: 'get',
      params
    })
  },

  // ==================== 防作弊管理 ====================

  /**
   * 记录作弊警告
   */
  recordCheatWarning(recordId, warningData) {
    return request({
      url: `/api/v1/exams/records/${recordId}/cheat-warning`,
      method: 'post',
      data: warningData
    })
  },

  /**
   * 获取作弊记录
   */
  getCheatRecords(examId) {
    return request({
      url: `/api/v1/exams/${examId}/cheat-records`,
      method: 'get'
    })
  },

  /**
   * 标记可疑行为
   */
  markSuspiciousBehavior(recordId, behaviorData) {
    return request({
      url: `/api/v1/exams/records/${recordId}/suspicious-behavior`,
      method: 'post',
      data: behaviorData
    })
  },

  // ==================== 题库管理 ====================

  /**
   * 获取题库列表
   */
  getQuestionBank(params = {}) {
    return request({
      url: '/api/v1/question-bank',
      method: 'get',
      params
    })
  },

  /**
   * 从题库添加题目
   */
  addQuestionsFromBank(examId, questionIds) {
    return request({
      url: `/api/v1/exams/${examId}/questions/from-bank`,
      method: 'post',
      data: questionIds
    })
  }
}

export default examApi
