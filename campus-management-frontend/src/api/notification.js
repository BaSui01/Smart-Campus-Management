import request from './request'

/**
 * 通知管理API
 * 基于后端 NotificationController、NotificationTemplateController、MessageController 等接口实现
 */
export const notificationApi = {
  // ==================== 通知基础管理 ====================

  /**
   * 获取通知列表
   * 对应后端: GET /api/v1/notifications
   */
  getNotificationList(params = {}) {
    return request({
      url: '/v1/notifications',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取通知详情
   * 对应后端: GET /api/v1/notifications/{id}
   */
  getNotificationById(id) {
    return request({
      url: `/v1/notifications/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建通知
   * 对应后端: POST /api/v1/notifications
   */
  createNotification(data) {
    return request({
      url: '/v1/notifications',
      method: 'post',
      data
    })
  },

  /**
   * 更新通知信息
   * 对应后端: PUT /api/v1/notifications/{id}
   */
  updateNotification(id, data) {
    return request({
      url: `/v1/notifications/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除通知
   * 对应后端: DELETE /api/v1/notifications/{id}
   */
  deleteNotification(id) {
    return request({
      url: `/v1/notifications/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量发布通知
   * 对应后端: PUT /api/v1/notifications/batch/publish
   */
  batchPublishNotifications(notificationIds) {
    return request({
      url: '/v1/notifications/batch/publish',
      method: 'put',
      data: notificationIds
    })
  },

  /**
   * 发布通知
   * 对应后端: PUT /api/v1/notifications/{id}/publish
   */
  publishNotification(id) {
    return request({
      url: `/v1/notifications/${id}/publish`,
      method: 'put'
    })
  },

  /**
   * 撤回通知
   * 对应后端: PUT /api/v1/notifications/{id}/withdraw
   */
  withdrawNotification(id) {
    return request({
      url: `/v1/notifications/${id}/withdraw`,
      method: 'put'
    })
  },

  /**
   * 标记通知为已读
   * 对应后端: PUT /api/v1/notifications/{id}/read
   */
  markNotificationAsRead(id) {
    return request({
      url: `/v1/notifications/${id}/read`,
      method: 'put'
    })
  },

  /**
   * 批量标记通知为已读
   * 对应后端: PUT /api/v1/notifications/batch/read
   */
  batchMarkNotificationsAsRead(notificationIds) {
    return request({
      url: '/v1/notifications/batch/read',
      method: 'put',
      data: notificationIds
    })
  },

  // ==================== 通知模板管理 ====================

  /**
   * 获取通知模板列表
   * 对应后端: GET /api/v1/notification-templates
   */
  getNotificationTemplateList(params = {}) {
    return request({
      url: '/v1/notification-templates',
      method: 'get',
      params
    })
  },

  /**
   * 根据ID获取通知模板详情
   * 对应后端: GET /api/v1/notification-templates/{id}
   */
  getNotificationTemplateById(id) {
    return request({
      url: `/v1/notification-templates/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建通知模板
   * 对应后端: POST /api/v1/notification-templates
   */
  createNotificationTemplate(data) {
    return request({
      url: '/v1/notification-templates',
      method: 'post',
      data
    })
  },

  /**
   * 更新通知模板
   * 对应后端: PUT /api/v1/notification-templates/{id}
   */
  updateNotificationTemplate(id, data) {
    return request({
      url: `/v1/notification-templates/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除通知模板
   * 对应后端: DELETE /api/v1/notification-templates/{id}
   */
  deleteNotificationTemplate(id) {
    return request({
      url: `/v1/notification-templates/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量更新模板状态
   * 对应后端: PUT /api/v1/notification-templates/batch-status
   */
  batchUpdateTemplateStatus(templateIds, newStatus) {
    return request({
      url: '/v1/notification-templates/batch-status',
      method: 'put',
      params: {
        templateIds,
        newStatus
      }
    })
  },

  // ==================== 消息管理 ====================

  /**
   * 获取消息列表
   * 对应后端: GET /api/v1/messages
   */
  getMessageList(params = {}) {
    return request({
      url: '/v1/messages',
      method: 'get',
      params
    })
  },

  /**
   * 发送消息
   * 对应后端: POST /api/v1/messages
   */
  sendMessage(data) {
    return request({
      url: '/v1/messages',
      method: 'post',
      data
    })
  },

  /**
   * 批量标记消息为已读
   * 对应后端: PUT /api/v1/messages/batch/read
   */
  batchMarkMessagesAsRead(messageIds) {
    return request({
      url: '/v1/messages/batch/read',
      method: 'put',
      data: messageIds
    })
  },

  /**
   * 删除消息
   * 对应后端: DELETE /api/v1/messages/{id}
   */
  deleteMessage(id) {
    return request({
      url: `/v1/messages/${id}`,
      method: 'delete'
    })
  },
  // ==================== 基础CRUD操作 ====================

  /**
   * 分页查询通知列表
   */
  getNotifications(params = {}) {
    return request({
      url: '/api/v1/notifications',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        sortBy: 'publishTime',
        sortDir: 'desc',
        ...params
      }
    })
  },

  /**
   * 根据ID查询通知详情
   */
  getNotificationById(id) {
    return request({
      url: `/api/v1/notifications/${id}`,
      method: 'get'
    })
  },

  /**
   * 创建通知
   */
  createNotification(data) {
    return request({
      url: '/api/v1/notifications',
      method: 'post',
      data
    })
  },

  /**
   * 更新通知
   */
  updateNotification(id, data) {
    return request({
      url: `/api/v1/notifications/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除通知
   */
  deleteNotification(id) {
    return request({
      url: `/api/v1/notifications/${id}`,
      method: 'delete'
    })
  },

  /**
   * 批量删除通知
   */
  deleteNotifications(ids) {
    return request({
      url: '/api/v1/notifications/batch',
      method: 'delete',
      data: ids
    })
  },

  // ==================== 业务操作 ====================

  /**
   * 发布通知
   */
  publishNotification(id) {
    return request({
      url: `/api/v1/notifications/${id}/publish`,
      method: 'post'
    })
  },

  /**
   * 取消发布通知
   */
  unpublishNotification(id) {
    return request({
      url: `/api/v1/notifications/${id}/unpublish`,
      method: 'post'
    })
  },

  /**
   * 置顶通知
   */
  topNotification(id) {
    return request({
      url: `/api/v1/notifications/${id}/top`,
      method: 'post'
    })
  },

  /**
   * 取消置顶通知
   */
  untopNotification(id) {
    return request({
      url: `/api/v1/notifications/${id}/untop`,
      method: 'post'
    })
  },

  /**
   * 延长通知有效期
   */
  extendExpireTime(id, newExpireTime) {
    return request({
      url: `/api/v1/notifications/${id}/extend`,
      method: 'post',
      params: { newExpireTime }
    })
  },

  /**
   * 复制通知
   */
  copyNotification(id) {
    return request({
      url: `/api/v1/notifications/${id}/copy`,
      method: 'post'
    })
  },

  // ==================== 用户通知操作 ====================

  /**
   * 获取当前用户的通知
   */
  getMyNotifications(params = {}) {
    return request({
      url: '/api/v1/notifications/my',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 获取当前用户的未读通知
   */
  getMyUnreadNotifications(params = {}) {
    return request({
      url: '/api/v1/notifications/my/unread',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        ...params
      }
    })
  },

  /**
   * 获取未读通知数量
   */
  getUnreadCount() {
    return request({
      url: '/api/v1/notifications/my/unread-count',
      method: 'get'
    })
  },

  /**
   * 标记通知为已读
   */
  markAsRead(id) {
    return request({
      url: `/api/v1/notifications/${id}/read`,
      method: 'post'
    })
  },

  /**
   * 批量标记通知为已读
   */
  markMultipleAsRead(ids) {
    return request({
      url: '/api/v1/notifications/mark-read',
      method: 'post',
      data: ids
    })
  },

  /**
   * 标记所有通知为已读
   */
  markAllAsRead() {
    return request({
      url: '/api/v1/notifications/mark-all-read',
      method: 'post'
    })
  },

  // ==================== 通知发送 ====================

  /**
   * 发送系统通知
   */
  sendSystemNotification(data) {
    return request({
      url: '/api/v1/notifications/send/system',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        targetType: data.targetType,
        targetIds: data.targetIds,
        priority: data.priority || 'normal',
        ...data
      }
    })
  },

  /**
   * 发送课程通知
   */
  sendCourseNotification(data) {
    return request({
      url: '/api/v1/notifications/send/course',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        courseId: data.courseId,
        ...data
      }
    })
  },

  /**
   * 发送考试通知
   */
  sendExamNotification(data) {
    return request({
      url: '/api/v1/notifications/send/exam',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        examId: data.examId,
        ...data
      }
    })
  },

  /**
   * 发送作业通知
   */
  sendAssignmentNotification(data) {
    return request({
      url: '/api/v1/notifications/send/assignment',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        assignmentId: data.assignmentId,
        ...data
      }
    })
  },

  /**
   * 发送个人通知
   */
  sendPersonalNotification(data) {
    return request({
      url: '/api/v1/notifications/send/personal',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        targetUserId: data.targetUserId,
        ...data
      }
    })
  },

  /**
   * 发送角色通知
   */
  sendRoleNotification(data) {
    return request({
      url: '/api/v1/notifications/send/role',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        roleName: data.roleName,
        ...data
      }
    })
  },

  /**
   * 发送班级通知
   */
  sendClassNotification(data) {
    return request({
      url: '/api/v1/notifications/send/class',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        classId: data.classId,
        ...data
      }
    })
  },

  /**
   * 发送院系通知
   */
  sendDepartmentNotification(data) {
    return request({
      url: '/api/v1/notifications/send/department',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        departmentId: data.departmentId,
        ...data
      }
    })
  },

  /**
   * 发送广播通知
   */
  sendBroadcastNotification(data) {
    return request({
      url: '/api/v1/notifications/send/broadcast',
      method: 'post',
      data: {
        title: data.title,
        content: data.content,
        ...data
      }
    })
  },

  // ==================== 模板通知 ====================

  /**
   * 使用模板发送通知
   */
  sendTemplateNotification(data) {
    return request({
      url: '/api/v1/notifications/send/template',
      method: 'post',
      data: {
        templateCode: data.templateCode,
        variables: data.variables,
        targetType: data.targetType,
        targetIds: data.targetIds,
        ...data
      }
    })
  },

  /**
   * 发送选课提醒
   */
  sendCourseSelectionReminder(studentId, deadline) {
    return request({
      url: '/api/v1/notifications/reminders/course-selection',
      method: 'post',
      data: { studentId, deadline }
    })
  },

  /**
   * 发送缴费提醒
   */
  sendPaymentReminder(studentId, feeItemName, deadline) {
    return request({
      url: '/api/v1/notifications/reminders/payment',
      method: 'post',
      data: { studentId, feeItemName, deadline }
    })
  },

  /**
   * 发送考试提醒
   */
  sendExamReminder(studentId, examName, examTime) {
    return request({
      url: '/api/v1/notifications/reminders/exam',
      method: 'post',
      data: { studentId, examName, examTime }
    })
  },

  /**
   * 发送作业提醒
   */
  sendAssignmentReminder(studentId, assignmentTitle, dueDate) {
    return request({
      url: '/api/v1/notifications/reminders/assignment',
      method: 'post',
      data: { studentId, assignmentTitle, dueDate }
    })
  },

  /**
   * 发送成绩发布通知
   */
  sendGradePublishedNotification(studentId, courseName) {
    return request({
      url: '/api/v1/notifications/reminders/grade-published',
      method: 'post',
      data: { studentId, courseName }
    })
  },

  // ==================== 统计分析 ====================

  /**
   * 获取通知统计信息
   */
  getNotificationStatistics(params = {}) {
    return request({
      url: '/api/v1/notifications/statistics',
      method: 'get',
      params
    })
  },

  /**
   * 获取通知阅读统计
   */
  getNotificationReadStatistics(notificationId) {
    return request({
      url: `/api/v1/notifications/${notificationId}/read-statistics`,
      method: 'get'
    })
  },

  /**
   * 获取用户通知统计
   */
  getUserNotificationStatistics(userId, params = {}) {
    return request({
      url: `/api/v1/notifications/statistics/user/${userId}`,
      method: 'get',
      params
    })
  },

  /**
   * 获取系统通知统计
   */
  getSystemNotificationStatistics(params = {}) {
    return request({
      url: '/api/v1/notifications/statistics/system',
      method: 'get',
      params
    })
  },

  // ==================== 模板管理 ====================

  /**
   * 获取通知模板列表
   */
  getNotificationTemplates(params = {}) {
    return request({
      url: '/api/v1/notifications/templates',
      method: 'get',
      params
    })
  },

  /**
   * 根据模板代码获取模板
   */
  getTemplateByCode(templateCode) {
    return request({
      url: `/api/v1/notifications/templates/${templateCode}`,
      method: 'get'
    })
  },

  /**
   * 创建通知模板
   */
  createNotificationTemplate(data) {
    return request({
      url: '/api/v1/notifications/templates',
      method: 'post',
      data
    })
  },

  /**
   * 更新通知模板
   */
  updateNotificationTemplate(id, data) {
    return request({
      url: `/api/v1/notifications/templates/${id}`,
      method: 'put',
      data
    })
  },

  /**
   * 删除通知模板
   */
  deleteNotificationTemplate(id) {
    return request({
      url: `/api/v1/notifications/templates/${id}`,
      method: 'delete'
    })
  }
}

export default notificationApi
