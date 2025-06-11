import request from './request'

/**
 * 缴费管理API
 * 基于后端 PaymentController、FeeItemController 等接口实现
 */
export const paymentApi = {
  // ==================== 缴费记录管理 ====================
  // 获取缴费项目列表
  getFeeItems: (params) => {
    return request({
      url: '/payment/fee-items',
      method: 'get',
      params
    })
  },

  // 获取学生缴费记录
  getStudentPayments: (params) => {
    return request({
      url: '/student/payments',
      method: 'get',
      params
    })
  },

  // 获取未缴费项目
  getUnpaidItems: () => {
    return request({
      url: '/student/unpaid-items',
      method: 'get'
    })
  },

  // 创建支付订单
  createPaymentOrder: (data) => {
    return request({
      url: '/payment/orders',
      method: 'post',
      data
    })
  },

  // 确认支付
  confirmPayment: (orderId, data) => {
    return request({
      url: `/payment/orders/${orderId}/confirm`,
      method: 'post',
      data
    })
  },

  // 获取支付记录详情
  getPaymentDetail: (paymentId) => {
    return request({
      url: `/payment/records/${paymentId}`,
      method: 'get'
    })
  },

  // 家长查看子女缴费记录
  getChildrenPayments: (studentId, params) => {
    return request({
      url: `/parent/children/${studentId}/payments`,
      method: 'get',
      params
    })
  },

  // 家长代缴费用
  payForChild: (studentId, data) => {
    return request({
      url: `/parent/children/${studentId}/pay`,
      method: 'post',
      data
    })
  },

  // 管理员创建缴费项目
  createFeeItem: (data) => {
    return request({
      url: '/admin/fee-items',
      method: 'post',
      data
    })
  },

  // 管理员更新缴费项目
  updateFeeItem: (itemId, data) => {
    return request({
      url: `/admin/fee-items/${itemId}`,
      method: 'put',
      data
    })
  },

  // 管理员删除缴费项目
  deleteFeeItem: (itemId) => {
    return request({
      url: `/admin/fee-items/${itemId}`,
      method: 'delete'
    })
  },

  // 获取缴费统计
  getPaymentStats: (params) => {
    return request({
      url: '/admin/payment-stats',
      method: 'get',
      params
    })
  },

  // 导出缴费记录
  exportPayments: (params) => {
    return request({
      url: '/admin/payments/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 批量导入缴费记录
  importPayments: (file) => {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/admin/payments/import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}