/**
 * API配置管理
 * 统一管理不同端的API接口配置
 */

// API基础配置
export const API_CONFIG = {
  // 基础URL配置
  BASE_URL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8889/api',
  ADMIN_BASE_URL: import.meta.env.VITE_ADMIN_BASE_URL || 'http://localhost:8889/admin',
  
  // 超时配置
  TIMEOUT: 15000,
  
  // 请求头配置
  HEADERS: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  
  // 认证配置
  AUTH: {
    TOKEN_KEY: 'userToken',
    REFRESH_TOKEN_KEY: 'refreshToken',
    USER_INFO_KEY: 'userInfo',
    TOKEN_PREFIX: 'Bearer '
  }
}

// 学生端API路径
export const STUDENT_API_PATHS = {
  // 基础信息
  PROFILE: '/student/profile',
  DASHBOARD: '/student/dashboard',
  
  // 课程相关
  COURSES: '/student/courses',
  COURSE_SELECTION: '/student/course-selection',
  SCHEDULE: '/student/schedule',
  
  // 学习相关
  ASSIGNMENTS: '/student/assignments',
  EXAMS: '/student/exams',
  GRADES: '/student/grades',
  
  // 其他功能
  ATTENDANCE: '/student/attendance',
  NOTIFICATIONS: '/student/notifications',
  PAYMENTS: '/student/payments',
  LIBRARY: '/student/library'
}

// 教师端API路径
export const TEACHER_API_PATHS = {
  // 基础信息
  PROFILE: '/teacher/profile',
  DASHBOARD: '/teacher/dashboard',
  
  // 教学管理
  COURSES: '/teacher/courses',
  CLASSES: '/teacher/classes',
  STUDENTS: '/teacher/students',
  SCHEDULE: '/teacher/schedule',
  
  // 教学活动
  ASSIGNMENTS: '/teacher/assignments',
  EXAMS: '/teacher/exams',
  GRADES: '/teacher/grades',
  ATTENDANCE: '/teacher/attendance',
  
  // 教学资源
  RESOURCES: '/teacher/resources',
  QUESTION_BANK: '/teacher/question-bank',
  
  // 其他功能
  EVALUATIONS: '/teacher/evaluations',
  RESEARCH: '/teacher/research'
}

// 家长端API路径
export const PARENT_API_PATHS = {
  // 基础信息
  PROFILE: '/parent/profile',
  DASHBOARD: '/parent/dashboard',
  
  // 子女管理
  CHILDREN: '/parent/children',
  CHILD_GRADES: '/parent/children/{childId}/grades',
  CHILD_ATTENDANCE: '/parent/children/{childId}/attendance',
  CHILD_SCHEDULE: '/parent/children/{childId}/schedule',
  
  // 家校沟通
  COMMUNICATION: '/parent/communication',
  NOTIFICATIONS: '/parent/notifications',
  MEETINGS: '/parent/meetings',
  
  // 其他功能
  PAYMENTS: '/parent/payments',
  ACTIVITIES: '/parent/activities'
}

// 管理员API路径
export const ADMIN_API_PATHS = {
  // 认证相关
  LOGIN: '/admin/login',
  LOGOUT: '/admin/logout',
  CAPTCHA: '/admin/captcha',
  
  // 系统管理
  DASHBOARD: '/admin/dashboard',
  USERS: '/api/v1/users',
  ROLES: '/api/v1/roles',
  PERMISSIONS: '/api/v1/permissions',
  SYSTEM_CONFIG: '/admin/system-config',
  
  // 学术管理
  DEPARTMENTS: '/admin/departments',
  COURSES: '/admin/courses',
  CLASSES: '/admin/classes',
  STUDENTS: '/admin/students',
  TEACHERS: '/admin/teachers'
}

// 公共API路径
export const COMMON_API_PATHS = {
  // 认证
  LOGIN: '/auth/login',
  LOGOUT: '/auth/logout',
  REFRESH_TOKEN: '/auth/refresh-token',
  
  // 通用功能
  UPLOAD: '/common/upload',
  DOWNLOAD: '/common/download',
  CAPTCHA: '/common/captcha',
  
  // 系统信息
  HEALTH: '/health',
  VERSION: '/version'
}

// API版本配置
export const API_VERSIONS = {
  V1: '/v1',
  V2: '/v2',
  V3: '/v3'
}

// 请求方法枚举
export const HTTP_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
  PATCH: 'PATCH'
}

// 响应状态码
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
}

// 错误消息映射
export const ERROR_MESSAGES = {
  [HTTP_STATUS.BAD_REQUEST]: '请求参数错误',
  [HTTP_STATUS.UNAUTHORIZED]: '请先登录',
  [HTTP_STATUS.FORBIDDEN]: '权限不足',
  [HTTP_STATUS.NOT_FOUND]: '请求的资源不存在',
  [HTTP_STATUS.INTERNAL_SERVER_ERROR]: '服务器内部错误',
  NETWORK_ERROR: '网络连接失败',
  TIMEOUT: '请求超时',
  UNKNOWN: '未知错误'
}

export default {
  API_CONFIG,
  STUDENT_API_PATHS,
  TEACHER_API_PATHS,
  PARENT_API_PATHS,
  ADMIN_API_PATHS,
  COMMON_API_PATHS,
  API_VERSIONS,
  HTTP_METHODS,
  HTTP_STATUS,
  ERROR_MESSAGES
}