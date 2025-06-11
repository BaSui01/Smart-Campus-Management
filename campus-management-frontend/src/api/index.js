/**
 * API统一导出文件
 * 智慧校园管理系统前端API接口集合
 */

// 基础配置
export { default as request } from './request'
export { default as config } from './config'

// 认证相关
export { authApi } from './auth'
export { adminAuthApi } from './adminAuth'

// 用户管理
export { studentApi } from './student'
export { teacherApi } from './teacher'
export { parentApi } from './parent'

// 教务管理
export { courseApi } from './course'
export { classApi } from './class'
export { assignmentApi } from './assignment'
export { examApi } from './exam'
export { gradeApi } from './grade'
export { attendanceApi } from './attendance'

// 系统管理
export { notificationApi } from './notification'
export { paymentApi } from './payment'
export { permissionApi } from './permission'
export { systemApi } from './system'
export { departmentApi } from './department'

// 模块化API - 针对不同用户角色的专用接口
export { studentModule } from './modules/studentModule'
export { teacherModule } from './modules/teacherModule'
export { parentModule } from './modules/parentModule'

// API集合对象，便于统一管理
export const api = {
  // 认证相关
  auth: () => import('./auth').then(m => m.authApi),
  adminAuth: () => import('./adminAuth').then(m => m.adminAuthApi),
  
  // 用户管理
  student: () => import('./student').then(m => m.studentApi),
  teacher: () => import('./teacher').then(m => m.teacherApi),
  parent: () => import('./parent').then(m => m.parentApi),
  
  // 教务管理
  course: () => import('./course').then(m => m.courseApi),
  class: () => import('./class').then(m => m.classApi),
  assignment: () => import('./assignment').then(m => m.assignmentApi),
  exam: () => import('./exam').then(m => m.examApi),
  grade: () => import('./grade').then(m => m.gradeApi),
  attendance: () => import('./attendance').then(m => m.attendanceApi),
  
  // 系统管理
  notification: () => import('./notification').then(m => m.notificationApi),
  payment: () => import('./payment').then(m => m.paymentApi),
  permission: () => import('./permission').then(m => m.permissionApi),
  system: () => import('./system').then(m => m.systemApi),
  department: () => import('./department').then(m => m.departmentApi),
  
  // 模块化API
  modules: {
    student: () => import('./modules/studentModule').then(m => m.studentModule),
    teacher: () => import('./modules/teacherModule').then(m => m.teacherModule),
    parent: () => import('./modules/parentModule').then(m => m.parentModule)
  }
}

/**
 * 动态加载API模块
 * @param {string} moduleName - 模块名称
 * @returns {Promise} API模块
 */
export const loadApi = async (moduleName) => {
  if (!api[moduleName]) {
    throw new Error(`API模块 ${moduleName} 不存在`)
  }
  return await api[moduleName]()
}

/**
 * 批量加载API模块
 * @param {string[]} moduleNames - 模块名称数组
 * @returns {Promise<Object>} API模块集合
 */
export const loadApis = async (moduleNames) => {
  const results = {}
  await Promise.all(
    moduleNames.map(async (name) => {
      results[name] = await loadApi(name)
    })
  )
  return results
}

/**
 * API模块信息
 */
export const apiModules = {
  auth: {
    name: '认证管理',
    description: '用户登录、注册、权限验证等功能',
    version: '1.0.0'
  },
  adminAuth: {
    name: '管理员认证',
    description: '管理员后台认证相关功能',
    version: '1.0.0'
  },
  student: {
    name: '学生管理',
    description: '学生信息管理、学习记录等功能',
    version: '1.0.0'
  },
  teacher: {
    name: '教师管理',
    description: '教师信息管理、教学相关功能',
    version: '1.0.0'
  },
  parent: {
    name: '家长管理',
    description: '家长信息管理、学生监控功能',
    version: '1.0.0'
  },
  course: {
    name: '课程管理',
    description: '课程信息、选课、排课等功能',
    version: '1.0.0'
  },
  class: {
    name: '班级管理',
    description: '班级信息、学生管理等功能',
    version: '1.0.0'
  },
  assignment: {
    name: '作业管理',
    description: '作业发布、提交、评分等功能',
    version: '1.0.0'
  },
  exam: {
    name: '考试管理',
    description: '考试安排、题目管理、成绩录入等功能',
    version: '1.0.0'
  },
  grade: {
    name: '成绩管理',
    description: '成绩录入、查询、统计分析等功能',
    version: '1.0.0'
  },
  attendance: {
    name: '考勤管理',
    description: '考勤记录、统计分析等功能',
    version: '1.0.0'
  },
  notification: {
    name: '通知管理',
    description: '通知发布、消息管理等功能',
    version: '1.0.0'
  },
  payment: {
    name: '缴费管理',
    description: '缴费项目、支付记录等功能',
    version: '1.0.0'
  },
  permission: {
    name: '权限管理',
    description: '角色权限、用户授权等功能',
    version: '1.0.0'
  },
  system: {
    name: '系统管理',
    description: '系统配置、监控、维护等功能',
    version: '1.0.0'
  },
  department: {
    name: '院系管理',
    description: '院系信息、层级管理等功能',
    version: '1.0.0'
  }
}

/**
 * 获取所有API模块名称
 * @returns {string[]} 模块名称数组
 */
export const getApiModuleNames = () => {
  return Object.keys(api)
}

/**
 * 获取API模块信息
 * @param {string} moduleName - 模块名称
 * @returns {Object} 模块信息
 */
export const getApiModuleInfo = (moduleName) => {
  return apiModules[moduleName] || null
}

/**
 * 检查API模块是否存在
 * @param {string} moduleName - 模块名称
 * @returns {boolean} 是否存在
 */
export const hasApiModule = (moduleName) => {
  return !!api[moduleName]
}

// 默认导出
export default {
  // API模块
  ...api,
  
  // 工具函数
  loadApi,
  loadApis,
  getApiModuleNames,
  getApiModuleInfo,
  hasApiModule,
  
  // 模块信息
  modules: apiModules
}
