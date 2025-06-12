import request from './request'

/**
 * 图书馆管理API
 * 基于后端 LibraryController、BookController 等接口实现
 */
export const libraryApi = {
  // ==================== 图书查询 ====================

  /**
   * 搜索图书
   * 对应后端: GET /api/v1/library/books/search
   */
  searchBooks(params = {}) {
    return request({
      url: '/library/books/search',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        keyword: '',
        category: '',
        status: '',
        ...params
      }
    })
  },

  /**
   * 获取图书详情
   * 对应后端: GET /api/v1/library/books/{id}
   */
  getBookDetail(bookId) {
    return request({
      url: `/library/books/${bookId}`,
      method: 'get'
    })
  },

  /**
   * 获取图书分类
   * 对应后端: GET /api/v1/library/categories
   */
  getBookCategories() {
    return request({
      url: '/library/categories',
      method: 'get'
    })
  },

  /**
   * 获取热门图书
   * 对应后端: GET /api/v1/library/books/popular
   */
  getPopularBooks(params = {}) {
    return request({
      url: '/library/books/popular',
      method: 'get',
      params
    })
  },

  /**
   * 获取推荐图书
   * 对应后端: GET /api/v1/library/books/recommendations
   */
  getRecommendedBooks(params = {}) {
    return request({
      url: '/library/books/recommendations',
      method: 'get',
      params
    })
  },

  // ==================== 借阅管理 ====================

  /**
   * 借阅图书
   * 对应后端: POST /api/v1/library/borrow
   */
  borrowBook(data) {
    return request({
      url: '/library/borrow',
      method: 'post',
      data: {
        bookId: data.bookId,
        borrowDays: data.borrowDays || 30,
        ...data
      }
    })
  },

  /**
   * 归还图书
   * 对应后端: POST /api/v1/library/return
   */
  returnBook(borrowId) {
    return request({
      url: `/library/return/${borrowId}`,
      method: 'post'
    })
  },

  /**
   * 续借图书
   * 对应后端: POST /api/v1/library/renew
   */
  renewBook(borrowId, days = 30) {
    return request({
      url: `/library/renew/${borrowId}`,
      method: 'post',
      data: { days }
    })
  },

  /**
   * 预约图书
   * 对应后端: POST /api/v1/library/reserve
   */
  reserveBook(bookId) {
    return request({
      url: '/library/reserve',
      method: 'post',
      data: { bookId }
    })
  },

  /**
   * 取消预约
   * 对应后端: DELETE /api/v1/library/reserve/{id}
   */
  cancelReservation(reservationId) {
    return request({
      url: `/library/reserve/${reservationId}`,
      method: 'delete'
    })
  },

  // ==================== 个人借阅记录 ====================

  /**
   * 获取我的借阅记录
   * 对应后端: GET /api/v1/library/my-borrows
   */
  getMyBorrows(params = {}) {
    return request({
      url: '/library/my-borrows',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all', // all, borrowed, returned, overdue
        ...params
      }
    })
  },

  /**
   * 获取我的预约记录
   * 对应后端: GET /api/v1/library/my-reservations
   */
  getMyReservations(params = {}) {
    return request({
      url: '/library/my-reservations',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        status: 'all', // all, active, expired, fulfilled
        ...params
      }
    })
  },

  /**
   * 获取借阅历史
   * 对应后端: GET /api/v1/library/borrow-history
   */
  getBorrowHistory(params = {}) {
    return request({
      url: '/library/borrow-history',
      method: 'get',
      params: {
        page: 1,
        size: 20,
        startDate: '',
        endDate: '',
        ...params
      }
    })
  },

  // ==================== 统计信息 ====================

  /**
   * 获取图书馆统计信息
   * 对应后端: GET /api/v1/library/stats
   */
  getLibraryStats() {
    return request({
      url: '/library/stats',
      method: 'get'
    })
  },

  /**
   * 获取个人借阅统计
   * 对应后端: GET /api/v1/library/my-stats
   */
  getMyLibraryStats() {
    return request({
      url: '/library/my-stats',
      method: 'get'
    })
  },

  // ==================== 其他功能 ====================

  /**
   * 收藏图书
   * 对应后端: POST /api/v1/library/favorites
   */
  addToFavorites(bookId) {
    return request({
      url: '/library/favorites',
      method: 'post',
      data: { bookId }
    })
  },

  /**
   * 取消收藏
   * 对应后端: DELETE /api/v1/library/favorites/{bookId}
   */
  removeFromFavorites(bookId) {
    return request({
      url: `/library/favorites/${bookId}`,
      method: 'delete'
    })
  },

  /**
   * 获取收藏列表
   * 对应后端: GET /api/v1/library/favorites
   */
  getFavorites(params = {}) {
    return request({
      url: '/library/favorites',
      method: 'get',
      params
    })
  },

  /**
   * 图书评价
   * 对应后端: POST /api/v1/library/books/{id}/review
   */
  reviewBook(bookId, data) {
    return request({
      url: `/library/books/${bookId}/review`,
      method: 'post',
      data: {
        rating: data.rating,
        comment: data.comment,
        ...data
      }
    })
  },

  /**
   * 获取图书评价
   * 对应后端: GET /api/v1/library/books/{id}/reviews
   */
  getBookReviews(bookId, params = {}) {
    return request({
      url: `/library/books/${bookId}/reviews`,
      method: 'get',
      params
    })
  },

  /**
   * 导出借阅记录
   * 对应后端: GET /api/v1/library/export-borrows
   */
  exportBorrowHistory(params = {}) {
    return request({
      url: '/library/export-borrows',
      method: 'get',
      params: {
        format: 'excel',
        ...params
      },
      responseType: 'blob'
    })
  }
}

export default libraryApi
