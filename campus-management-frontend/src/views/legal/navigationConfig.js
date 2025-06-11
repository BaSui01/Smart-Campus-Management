/**
 * 智慧校园管理系统 - 导航配置文件
 * 
 * 此文件包含了不同用户角色的导航菜单配置、权限设置和快捷操作定义
 * 支持动态菜单生成和权限验证
 */

// 图标映射 - 统一管理所有使用的图标
export const ICONS = {
  // 基础图标
  HOME: 'House',
  USER: 'User',
  SETTING: 'Setting',
  SEARCH: 'Search',
  BELL: 'Bell',
  
  // 功能图标
  READING: 'Reading',
  DOCUMENT: 'Document',
  EDIT: 'Edit',
  PROMOTION: 'Promotion',
  CALENDAR: 'Calendar',
  USER_FILLED: 'UserFilled',
  CREDIT_CARD: 'CreditCard',
  MANAGEMENT: 'Management',
  DATA_ANALYSIS: 'DataAnalysis',
  MONITOR: 'Monitor',
  CHAT: 'ChatDotRound',
  FILES: 'Files',
  TROPHY: 'Trophy',
  NOTEBOOK: 'Notebook',
  PLUS: 'Plus',
  DOWNLOAD: 'Download',
  UPLOAD: 'Upload',
  MESSAGE: 'Message'
}

// 用户角色枚举
export const USER_ROLES = {
  STUDENT: 'STUDENT',
  TEACHER: 'TEACHER', 
  PARENT: 'PARENT',
  ADMIN: 'ADMIN'
}

// 角色显示名称映射
export const ROLE_DISPLAY_NAMES = {
  [USER_ROLES.STUDENT]: '学生',
  [USER_ROLES.TEACHER]: '教师',
  [USER_ROLES.PARENT]: '家长',
  [USER_ROLES.ADMIN]: '管理员'
}

// 角色主题色配置
export const ROLE_THEMES = {
  [USER_ROLES.STUDENT]: {
    primary: '#22c55e',
    secondary: '#16a34a',
    background: 'rgba(34, 197, 94, 0.1)',
    class: 'role-student'
  },
  [USER_ROLES.TEACHER]: {
    primary: '#3b82f6',
    secondary: '#2563eb',
    background: 'rgba(59, 130, 246, 0.1)',
    class: 'role-teacher'
  },
  [USER_ROLES.PARENT]: {
    primary: '#a855f7',
    secondary: '#9333ea',
    background: 'rgba(168, 85, 247, 0.1)',
    class: 'role-parent'
  },
  [USER_ROLES.ADMIN]: {
    primary: '#ef4444',
    secondary: '#dc2626',
    background: 'rgba(239, 68, 68, 0.1)',
    class: 'role-admin'
  }
}

// 学生端菜单配置
export const STUDENT_MENU_ITEMS = [
  {
    path: '/student/dashboard',
    title: '首页概览',
    icon: ICONS.HOME,
    permission: 'student:dashboard:view',
    meta: { title: '首页概览', keepAlive: true }
  },
  {
    path: '/student/courses',
    title: '我的课程',
    icon: ICONS.READING,
    permission: 'student:courses:view',
    meta: { title: '我的课程' },
    children: [
      {
        path: '/student/courses/list',
        title: '课程列表',
        icon: ICONS.READING,
        permission: 'student:courses:list'
      },
      {
        path: '/student/courses/detail',
        title: '课程详情',
        icon: ICONS.DOCUMENT,
        permission: 'student:courses:detail'
      }
    ]
  },
  {
    path: '/student/selection',
    title: '选课系统',
    icon: ICONS.DOCUMENT,
    permission: 'student:selection:view',
    meta: { title: '选课系统' }
  },
  {
    path: '/student/grades',
    title: '成绩查询',
    icon: ICONS.PROMOTION,
    permission: 'student:grades:view',
    meta: { title: '成绩查询' }
  },
  {
    path: '/student/schedule',
    title: '课程表',
    icon: ICONS.CALENDAR,
    permission: 'student:schedule:view',
    meta: { title: '课程表', keepAlive: true }
  },
  {
    path: '/student/assignments',
    title: '作业管理',
    icon: ICONS.EDIT,
    permission: 'student:assignments:view',
    meta: { title: '作业管理' }
  },
  {
    path: '/student/exams',
    title: '考试安排',
    icon: ICONS.NOTEBOOK,
    permission: 'student:exams:view',
    meta: { title: '考试安排' }
  },
  {
    path: '/student/library',
    title: '图书馆',
    icon: ICONS.FILES,
    permission: 'student:library:view',
    meta: { title: '图书馆' }
  },
  {
    path: '/student/profile',
    title: '个人信息',
    icon: ICONS.USER,
    permission: 'student:profile:view',
    meta: { title: '个人信息', keepAlive: true }
  }
]

// 教师端菜单配置
export const TEACHER_MENU_ITEMS = [
  {
    path: '/teacher/dashboard',
    title: '工作台',
    icon: ICONS.HOME,
    permission: 'teacher:dashboard:view',
    meta: { title: '教师工作台', keepAlive: true }
  },
  {
    path: '/teacher/courses',
    title: '课程管理',
    icon: ICONS.READING,
    permission: 'teacher:courses:view',
    meta: { title: '课程管理' },
    children: [
      {
        path: '/teacher/courses/list',
        title: '课程列表',
        icon: ICONS.READING,
        permission: 'teacher:courses:list'
      },
      {
        path: '/teacher/courses/create',
        title: '创建课程',
        icon: ICONS.PLUS,
        permission: 'teacher:courses:create'
      },
      {
        path: '/teacher/courses/detail',
        title: '课程详情',
        icon: ICONS.DOCUMENT,
        permission: 'teacher:courses:detail'
      }
    ]
  },
  {
    path: '/teacher/students',
    title: '学生管理',
    icon: ICONS.USER_FILLED,
    permission: 'teacher:students:view',
    meta: { title: '学生管理' }
  },
  {
    path: '/teacher/grades',
    title: '成绩录入',
    icon: ICONS.PROMOTION,
    permission: 'teacher:grades:manage',
    meta: { title: '成绩录入' }
  },
  {
    path: '/teacher/assignments',
    title: '作业管理',
    icon: ICONS.EDIT,
    permission: 'teacher:assignments:manage',
    meta: { title: '作业管理' }
  },
  {
    path: '/teacher/exams',
    title: '考试管理',
    icon: ICONS.NOTEBOOK,
    permission: 'teacher:exams:manage',
    meta: { title: '考试管理' }
  },
  {
    path: '/teacher/schedule',
    title: '课程安排',
    icon: ICONS.CALENDAR,
    permission: 'teacher:schedule:manage',
    meta: { title: '课程安排' }
  },
  {
    path: '/teacher/analytics',
    title: '数据分析',
    icon: ICONS.DATA_ANALYSIS,
    permission: 'teacher:analytics:view',
    meta: { title: '数据分析' }
  },
  {
    path: '/teacher/profile',
    title: '个人信息',
    icon: ICONS.USER,
    permission: 'teacher:profile:view',
    meta: { title: '个人信息', keepAlive: true }
  }
]

// 家长端菜单配置
export const PARENT_MENU_ITEMS = [
  {
    path: '/parent/dashboard',
    title: '家长中心',
    icon: ICONS.HOME,
    permission: 'parent:dashboard:view',
    meta: { title: '家长中心', keepAlive: true }
  },
  {
    path: '/parent/children',
    title: '子女信息',
    icon: ICONS.USER_FILLED,
    permission: 'parent:children:view',
    meta: { title: '子女信息' }
  },
  {
    path: '/parent/grades',
    title: '成绩查看',
    icon: ICONS.PROMOTION,
    permission: 'parent:grades:view',
    meta: { title: '成绩查看' }
  },
  {
    path: '/parent/attendance',
    title: '考勤记录',
    icon: ICONS.CALENDAR,
    permission: 'parent:attendance:view',
    meta: { title: '考勤记录' }
  },
  {
    path: '/parent/communication',
    title: '家校沟通',
    icon: ICONS.CHAT,
    permission: 'parent:communication:view',
    meta: { title: '家校沟通' }
  },
  {
    path: '/parent/activities',
    title: '活动通知',
    icon: ICONS.BELL,
    permission: 'parent:activities:view',
    meta: { title: '活动通知' }
  },
  {
    path: '/parent/payments',
    title: '缴费记录',
    icon: ICONS.CREDIT_CARD,
    permission: 'parent:payments:view',
    meta: { title: '缴费记录' }
  },
  {
    path: '/parent/monitor',
    title: '学习监控',
    icon: ICONS.MONITOR,
    permission: 'parent:monitor:view',
    meta: { title: '学习监控' }
  },
  {
    path: '/parent/profile',
    title: '个人信息',
    icon: ICONS.USER,
    permission: 'parent:profile:view',
    meta: { title: '个人信息', keepAlive: true }
  }
]

// 快捷操作配置
export const QUICK_ACTIONS = {
  [USER_ROLES.STUDENT]: [
    {
      key: 'new-assignment',
      title: '新作业',
      icon: ICONS.EDIT,
      tooltip: '查看新布置的作业',
      type: 'primary',
      permission: 'student:assignments:view',
      action: 'navigate',
      target: '/student/assignments'
    },
    {
      key: 'course-selection',
      title: '选课',
      icon: ICONS.PLUS,
      tooltip: '进行课程选择',
      type: 'success',
      permission: 'student:selection:view',
      action: 'navigate',
      target: '/student/selection'
    },
    {
      key: 'view-grades',
      title: '查成绩',
      icon: ICONS.PROMOTION,
      tooltip: '查看最新成绩',
      type: 'info',
      permission: 'student:grades:view',
      action: 'navigate',
      target: '/student/grades'
    }
  ],
  [USER_ROLES.TEACHER]: [
    {
      key: 'create-assignment',
      title: '布置作业',
      icon: ICONS.EDIT,
      tooltip: '创建新的作业任务',
      type: 'primary',
      permission: 'teacher:assignments:create',
      action: 'modal',
      target: 'CreateAssignmentModal'
    },
    {
      key: 'grade-entry',
      title: '录入成绩',
      icon: ICONS.PROMOTION,
      tooltip: '录入学生成绩',
      type: 'warning',
      permission: 'teacher:grades:create',
      action: 'navigate',
      target: '/teacher/grades'
    },
    {
      key: 'export-data',
      title: '导出数据',
      icon: ICONS.DOWNLOAD,
      tooltip: '导出教学数据',
      type: 'info',
      permission: 'teacher:data:export',
      action: 'function',
      target: 'exportTeachingData'
    },
    {
      key: 'student-analysis',
      title: '学情分析',
      icon: ICONS.DATA_ANALYSIS,
      tooltip: '查看学生学习分析',
      type: 'success',
      permission: 'teacher:analytics:view',
      action: 'navigate',
      target: '/teacher/analytics'
    }
  ],
  [USER_ROLES.PARENT]: [
    {
      key: 'view-grades',
      title: '查看成绩',
      icon: ICONS.PROMOTION,
      tooltip: '查看子女成绩',
      type: 'primary',
      permission: 'parent:grades:view',
      action: 'navigate',
      target: '/parent/grades'
    },
    {
      key: 'communication',
      title: '联系老师',
      icon: ICONS.MESSAGE,
      tooltip: '与老师进行沟通',
      type: 'success',
      permission: 'parent:communication:create',
      action: 'modal',
      target: 'ContactTeacherModal'
    },
    {
      key: 'payment',
      title: '在线缴费',
      icon: ICONS.CREDIT_CARD,
      tooltip: '进行在线缴费',
      type: 'warning',
      permission: 'parent:payments:create',
      action: 'navigate',
      target: '/parent/payments'
    }
  ]
}

// 根据角色获取菜单项
export function getMenuItemsByRole(role) {
  const menuMap = {
    [USER_ROLES.STUDENT]: STUDENT_MENU_ITEMS,
    [USER_ROLES.TEACHER]: TEACHER_MENU_ITEMS,
    [USER_ROLES.PARENT]: PARENT_MENU_ITEMS
  }
  return menuMap[role] || []
}

// 根据角色获取快捷操作
export function getQuickActionsByRole(role) {
  return QUICK_ACTIONS[role] || []
}

// 根据角色获取主题配置
export function getThemeByRole(role) {
  return ROLE_THEMES[role] || ROLE_THEMES[USER_ROLES.STUDENT]
}

// 权限验证函数
export function hasPermission(userPermissions, requiredPermission) {
  if (!requiredPermission) return true
  if (!userPermissions || userPermissions.length === 0) return false
  
  // 支持通配符权限
  if (userPermissions.includes('*')) return true
  
  // 检查具体权限
  return userPermissions.includes(requiredPermission)
}

// 过滤有权限的菜单项
export function filterMenuByPermissions(menuItems, userPermissions) {
  return menuItems.filter(item => {
    // 检查当前菜单项权限
    if (!hasPermission(userPermissions, item.permission)) {
      return false
    }
    
    // 如果有子菜单，递归过滤
    if (item.children && item.children.length > 0) {
      item.children = filterMenuByPermissions(item.children, userPermissions)
      // 如果所有子菜单都被过滤掉，则隐藏父菜单
      return item.children.length > 0
    }
    
    return true
  })
}

// 获取角色首页路径
export function getRoleHomePath(role) {
  const homePathMap = {
    [USER_ROLES.STUDENT]: '/student/dashboard',
    [USER_ROLES.TEACHER]: '/teacher/dashboard',
    [USER_ROLES.PARENT]: '/parent/dashboard',
    [USER_ROLES.ADMIN]: '/admin/dashboard'
  }
  return homePathMap[role] || '/dashboard'
}

// 导出默认配置
export default {
  ICONS,
  USER_ROLES,
  ROLE_DISPLAY_NAMES,
  ROLE_THEMES,
  STUDENT_MENU_ITEMS,
  TEACHER_MENU_ITEMS,
  PARENT_MENU_ITEMS,
  QUICK_ACTIONS,
  getMenuItemsByRole,
  getQuickActionsByRole,
  getThemeByRole,
  hasPermission,
  filterMenuByPermissions,
  getRoleHomePath
}
