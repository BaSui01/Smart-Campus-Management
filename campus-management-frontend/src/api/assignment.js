import request from './request'

/**
 * 作业管理API
 * 基于后端 AssignmentController、AssignmentSubmissionController 等接口实现
 */
export const assignmentApi = {
  // ==================== 作业基础管理 ====================

  /**
   * 获取作业列表
   * 对应后端: GET /api/v1/assignments
   */
  getAssignmentList(params = {}) {
    return request({
      url: '/v1/assignments',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取作业详情
   * 对应后端: GET /api/v1/assignments/{id}
   */
  getAssignmentById(id) {
    return request({
      url: `/v1/assignments/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建作业
   * 对应后端: POST /api/v1/assignments
   */
  createAssignment(data) {
    return request({
      url: '/v1/assignments',
      method: 'post',
      data
    })
  },

  /**
   * 更新作业信息
   * 对应后端: PUT /api/v1/assignments/{id}
   */
  updateAssignment(id, data) {
    return request({
      url: `/v1/assignments/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除作业
   * 对应后端: DELETE /api/v1/assignments/{id}
   */
  deleteAssignment(id) {
    return request({
      url: `/v1/assignments/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量发布作业
   * 对应后端: PUT /api/v1/assignments/batch/publish
   */
  batchPublishAssignments(assignmentIds) {
    return request({
      url: '/v1/assignments/batch/publish',
      method: 'put',
      data: assignmentIds
    })
  },

  /**
   * 发布作业
   * 对应后端: PUT /api/v1/assignments/{id}/publish
   */
  publishAssignment(id) {
    return request({
      url: `/v1/assignments/${id}/publish`,
      method: 'put'
    })
  },

  /**
   * 取消发布作业
   * 对应后端: PUT /api/v1/assignments/{id}/unpublish
   */
  unpublishAssignment(id) {
    return request({
      url: `/v1/assignments/${id}/unpublish`,
      method: 'put'
    })
  },

  // ==================== 作业提交管理 ====================

  /**
   * 获取作业提交列表
   * 对应后端: GET /api/v1/assignment-submissions
   */
  getAssignmentSubmissionList(params = {}) {
    return request({
      url: '/v1/assignment-submissions',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取作业提交详情
   * 对应后端: GET /api/v1/assignment-submissions/{id}
   */
  getAssignmentSubmissionById(id) {
    return request({
      url: `/v1/assignment-submissions/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建作业提交
   * 对应后端: POST /api/v1/assignment-submissions
   */
  createAssignmentSubmission(data) {
    return request({
      url: '/v1/assignment-submissions',
      method: 'post',
      data
    })
  },

  /**
   * 更新作业提交
   * 对应后端: PUT /api/v1/assignment-submissions/{id}
   */
  updateAssignmentSubmission(id, data) {
    return request({
      url: `/v1/assignment-submissions/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除作业提交
   * 对应后端: DELETE /api/v1/assignment-submissions/{id}
   */
  deleteAssignmentSubmission(id) {
    return request({
      url: `/v1/assignment-submissions/${id}`,
      method: 'delete'
    })
  },

  /**
   * 评分作业提交
   * 对应后端: PUT /api/v1/assignment-submissions/{id}/grade
   */
  gradeAssignmentSubmission(id, data) {
    return request({
      url: `/v1/assignment-submissions/${id}/grade`,
      method: 'put',
      data
    })
  },

  /**
   * 批量评分作业提交
   * 对应后端: PUT /api/v1/assignment-submissions/batch/grade
   */
  batchGradeSubmissions(data) {
    return request({
      url: '/v1/assignment-submissions/batch/grade',
      method: 'put',
      data
    })
  },

  /**
   * 提交作业
   * 对应后端: POST /api/v1/assignment-submissions/{assignmentId}/submit
   */
  submitAssignment(assignmentId, data) {
    return request({
      url: `/v1/assignment-submissions/${assignmentId}/submit`,
      method: 'post',
      data
    })
  },
  // ==================== 基础CRUD操作 ====================

  /**
   * 分页查询作业列表
   */
  getAssignments(params = {}) {
    return request({
      url: '/api/v1/assignments',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        sortBy: 'createdAt',
        sortDir: 'desc',
        ...params
      }
    })
  },

  /**
   * 根据ID查询作业详情
   */
  getAssignmentById(id) {
    return request({
      url: `/api/v1/assignments/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建作业
   */
  createAssignment(data) {
    return request({
      url: '/api/v1/assignments',
      method: 'post',
      data
    })
  },

  /**
   * 更新作业
   */
  updateAssignment(id, data) {
    return request({
      url: `/api/v1/assignments/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除作业
   */
  deleteAssignment(id) {
    return request({
      url: `/api/v1/assignments/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除作业
   */
  deleteAssignments(ids) {
    return request({
      url: '/api/v1/assignments/batch',
      method: 'delete',
      data: ids
    })
  },

  // ==================== 业务操作 ====================

  /**
   * 发布作业
   */
  publishAssignment(id) {
    return request({
      url: `/api/v1/assignments/${id}/publish`,
      method: 'post'
    })
  },

  /**
   * 取消发布作业
   */
  unpublishAssignment(id) {
    return request({
      url: `/api/v1/assignments/${id}/unpublish`,
      method: 'post'
    })
  },

  /**
   * 延长作业截止时间
   */
  extendDueDate(id, newDueDate) {
    return request({
      url: `/api/v1/assignments/${id}/extend`,
      method: 'post',
      params: { newDueDate }
    })
  },

  /**
   * 复制作业
   */
  copyAssignment(id, newCourseId) {
    return request({
      url: `/api/v1/assignments/${id}/copy`,
      method: 'post',
      params: { newCourseId }
    })
  },

  // ==================== 学生端操作 ====================

  /**
   * 获取学生的作业列表
   */
  getStudentAssignments(params = {}) {
    return request({
      url: '/api/v1/assignments/student',
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
   * 获取学生作业详情
   */
  getStudentAssignmentDetail(assignmentId) {
    return request({
      url: `/api/v1/assignments/${assignmentId}/student`,
      method: 'get'
    })
  },

  /**
   * 获取作业详情（通用）
   */
  getAssignmentDetail(assignmentId) {
    return request({
      url: `/api/v1/assignments/${assignmentId}`,
      method: 'get'
    })
  },

  /**
   * 提交作业
   */
  submitAssignment(assignmentId, data) {
    return request({
      url: `/api/v1/assignments/${assignmentId}/submit`,
      method: 'post',
      data
    })
  },

  /**
   * 更新作业提交
   */
  updateSubmission(submissionId, data) {
    return request({
      url: `/api/v1/assignments/submissions/${submissionId}`,
      method: 'put',
      data
    })
  },

  /**
   * 获取作业提交历史
   */
  getSubmissionHistory(assignmentId) {
    return request({
      url: `/api/v1/assignments/${assignmentId}/submissions/history`,
      method: 'get'
    })
  },

  // ==================== 教师端操作 ====================

  /**
   * 获取教师的作业列表
   */
  getTeacherAssignments(params = {}) {
    return request({
      url: '/api/v1/assignments/teacher',
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
   * 获取作业提交列表
   */
  getAssignmentSubmissions(assignmentId, params = {}) {
    return request({
      url: `/api/v1/assignments/${assignmentId}/submissions`,
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all', // all, pending, graded
        ...params
      }
    })
  },

  /**
   * 批改作业
   */
  gradeSubmission(submissionId, data) {
    return request({
      url: `/api/v1/assignments/submissions/${submissionId}/grade`,
      method: 'post',
      data: {
        score: data.score,
        feedback: data.feedback,
        ...data
      }
    })
  },

  /**
   * 批量批改作业
   */
  batchGradeSubmissions(submissions) {
    return request({
      url: '/api/v1/assignments/submissions/batch-grade',
      method: 'post',
      data: submissions
    })
  },

  /**
   * 导出作业提交
   */
  exportSubmissions(assignmentId, format = 'excel') {
    return request({
      url: `/api/v1/assignments/${assignmentId}/submissions/export`,
      method: 'get',
      params: { format },
      responseType: 'blob'
    })
  },

  // ==================== 统计分析 ====================

  /**
   * 获取作业统计信息
   */
  getAssignmentStatistics(assignmentId) {
    return request({
      url: `/api/v1/assignments/${assignmentId}/statistics`,
      method: 'get'
    })
  },

  /**
   * 获取课程作业统计
   */
  getCourseAssignmentStatistics(courseId, params = {}) {
    return request({
      url: `/api/v1/assignments/statistics/course/${courseId}`,
      method: 'get',
      params
    })
  },

  /**
   * 获取学生作业统计
   */
  getStudentAssignmentStatistics(studentId, params = {}) {
    return request({
      url: `/api/v1/assignments/statistics/student/${studentId}`,
      method: 'get',
      params
    })
  },

  /**
   * 获取教师作业统计
   */
  getTeacherAssignmentStatistics(teacherId, params = {}) {
    return request({
      url: `/api/v1/assignments/statistics/teacher/${teacherId}`,
      method: 'get',
      params
    })
  },

  // ==================== 文件上传 ====================

  /**
   * 上传作业附件
   */
  uploadAttachment(file, onProgress) {
    const formData = new FormData()
    formData.append('file', file)

    return request({
      url: '/api/v1/assignments/upload/attachment',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: onProgress
    })
  },

  /**
   * 上传作业提交文件
   */
  uploadSubmissionFile(file, assignmentId, onProgress) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('assignmentId', assignmentId)

    return request({
      url: '/api/v1/assignments/upload/submission',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: onProgress
    })
  },

  /**
   * 删除附件
   */
  deleteAttachment(fileId) {
    return request({
      url: `/api/v1/assignments/attachments/${fileId}`,
      method: 'delete'
    })
  },

  // ==================== 模板管理 ====================

  /**
   * 获取作业模板列表
   */
  getAssignmentTemplates(params = {}) {
    return request({
      url: '/api/v1/assignments/templates',
      method: 'get',
      params
    })
  },

  /**
   * 从模板创建作业
   */
  createFromTemplate(templateId, data) {
    return request({
      url: `/api/v1/assignments/templates/${templateId}/create`,
      method: 'post',
      data
    })
  },

  /**
   * 保存为模板
   */
  saveAsTemplate(assignmentId, templateData) {
    return request({
      url: `/api/v1/assignments/${assignmentId}/save-template`,
      method: 'post',
      data: templateData
    })
  }
}

export default assignmentApi
