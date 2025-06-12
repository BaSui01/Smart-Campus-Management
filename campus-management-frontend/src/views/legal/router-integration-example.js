/**
 * 智慧校园管理系统 - 路由集成示例
 * 
 * 此文件展示了如何在Vue Router中集成角色导航组件
 * 包含完整的路由配置、权限验证和角色管理
 */

import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import RoleBasedLayout from '@/views/legal/RoleBasedLayout.vue'

// 导入页面组件
const Login = () => import('@/views/Login.vue')
const Home = () => import('@/views/Home.vue')
const NotFound = () => import('@/views/404.vue')
const Forbidden = () => import('@/views/403.vue')

// 学生页面组件
const StudentDashboard = () => import('@/views/student/Dashboard.vue')
const StudentCourses = () => import('@/views/student/Courses.vue')
const StudentGrades = () => import('@/views/student/Grades.vue')
const StudentProfile = () => import('@/views/student/Profile.vue')
const StudentSchedule = () => import('@/views/student/Schedule.vue')
const StudentAssignments = () => import('@/views/student/Assignments.vue')
const StudentExams = () => import('@/views/student/Exams.vue')
const StudentLibrary = () => import('@/views/student/Library.vue')
const CourseSelection = () => import('@/views/student/CourseSelection.vue')

// 教师页面组件
const TeacherDashboard = () => import('@/views/teacher/Dashboard.vue')
const TeacherCourses = () => import('@/views/teacher/Courses.vue')
const TeacherStudents = () => import('@/views/teacher/Students.vue')
const TeacherGrades = () => import('@/views/teacher/Grades.vue')
const TeacherProfile = () => import('@/views/teacher/Profile.vue')
const TeacherSchedule = () => import('@/views/teacher/Schedule.vue')
const TeacherAssignments = () => import('@/views/teacher/Assignments.vue')
const TeacherExams = () => import('@/views/teacher/Exams.vue')

// 家长页面组件
const ParentDashboard = () => import('@/views/parent/Dashboard.vue')
const ParentChildren = () => import('@/views/parent/Children.vue')
const ParentGrades = () => import('@/views/parent/Grades.vue')
const ParentProfile = () => import('@/views/parent/Profile.vue')
const ParentAttendance = () => import('@/views/parent/Attendance.vue')
const ParentCommunication = () => import('@/views/parent/Communication.vue')
const ParentActivities = () => import('@/views/parent/Activities.vue')
const ParentPayments = () => import('@/views/parent/Payments.vue')
const ParentMonitor = () => import('@/views/parent/LearningMonitor.vue')

// 路由配置
const routes = [
  // 公共路由
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: '智慧校园管理系统',
      requiresAuth: false
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '用户登录',
      requiresAuth: false
    }
  },

  // 学生端路由
  {
    path: '/student',
    component: RoleBasedLayout,
    meta: {
      requiresAuth: true,
      roles: ['STUDENT'],
      title: '学生端'
    },
    children: [
      {
        path: '',
        redirect: '/student/dashboard'
      },
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: StudentDashboard,
        meta: {
          title: '首页概览',
          icon: 'House',
          keepAlive: true,
          permission: 'student:dashboard:view'
        }
      },
      {
        path: 'courses',
        name: 'StudentCourses',
        component: StudentCourses,
        meta: {
          title: '我的课程',
          icon: 'Reading',
          permission: 'student:courses:view'
        }
      },
      {
        path: 'selection',
        name: 'CourseSelection',
        component: CourseSelection,
        meta: {
          title: '选课系统',
          icon: 'Document',
          permission: 'student:selection:view'
        }
      },
      {
        path: 'grades',
        name: 'StudentGrades',
        component: StudentGrades,
        meta: {
          title: '成绩查询',
          icon: 'Promotion',
          permission: 'student:grades:view'
        }
      },
      {
        path: 'schedule',
        name: 'StudentSchedule',
        component: StudentSchedule,
        meta: {
          title: '课程表',
          icon: 'Calendar',
          keepAlive: true,
          permission: 'student:schedule:view'
        }
      },
      {
        path: 'assignments',
        name: 'StudentAssignments',
        component: StudentAssignments,
        meta: {
          title: '作业管理',
          icon: 'Edit',
          permission: 'student:assignments:view'
        }
      },
      {
        path: 'exams',
        name: 'StudentExams',
        component: StudentExams,
        meta: {
          title: '考试安排',
          icon: 'Notebook',
          permission: 'student:exams:view'
        }
      },
      {
        path: 'library',
        name: 'StudentLibrary',
        component: StudentLibrary,
        meta: {
          title: '图书馆',
          icon: 'Files',
          permission: 'student:library:view'
        }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: StudentProfile,
        meta: {
          title: '个人信息',
          icon: 'User',
          keepAlive: true,
          permission: 'student:profile:view'
        }
      }
    ]
  },

  // 教师端路由
  {
    path: '/teacher',
    component: RoleBasedLayout,
    meta: {
      requiresAuth: true,
      roles: ['TEACHER'],
      title: '教师端'
    },
    children: [
      {
        path: '',
        redirect: '/teacher/dashboard'
      },
      {
        path: 'dashboard',
        name: 'TeacherDashboard',
        component: TeacherDashboard,
        meta: {
          title: '教师工作台',
          icon: 'House',
          keepAlive: true,
          permission: 'teacher:dashboard:view'
        }
      },
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: TeacherCourses,
        meta: {
          title: '课程管理',
          icon: 'Reading',
          permission: 'teacher:courses:view'
        }
      },
      {
        path: 'students',
        name: 'TeacherStudents',
        component: TeacherStudents,
        meta: {
          title: '学生管理',
          icon: 'UserFilled',
          permission: 'teacher:students:view'
        }
      },
      {
        path: 'grades',
        name: 'TeacherGrades',
        component: TeacherGrades,
        meta: {
          title: '成绩录入',
          icon: 'Promotion',
          permission: 'teacher:grades:manage'
        }
      },
      {
        path: 'assignments',
        name: 'TeacherAssignments',
        component: TeacherAssignments,
        meta: {
          title: '作业管理',
          icon: 'Edit',
          permission: 'teacher:assignments:manage'
        }
      },
      {
        path: 'exams',
        name: 'TeacherExams',
        component: TeacherExams,
        meta: {
          title: '考试管理',
          icon: 'Notebook',
          permission: 'teacher:exams:manage'
        }
      },
      {
        path: 'schedule',
        name: 'TeacherSchedule',
        component: TeacherSchedule,
        meta: {
          title: '课程安排',
          icon: 'Calendar',
          permission: 'teacher:schedule:manage'
        }
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: TeacherProfile,
        meta: {
          title: '个人信息',
          icon: 'User',
          keepAlive: true,
          permission: 'teacher:profile:view'
        }
      }
    ]
  },

  // 家长端路由
  {
    path: '/parent',
    component: RoleBasedLayout,
    meta: {
      requiresAuth: true,
      roles: ['PARENT'],
      title: '家长端'
    },
    children: [
      {
        path: '',
        redirect: '/parent/dashboard'
      },
      {
        path: 'dashboard',
        name: 'ParentDashboard',
        component: ParentDashboard,
        meta: {
          title: '家长中心',
          icon: 'House',
          keepAlive: true,
          permission: 'parent:dashboard:view'
        }
      },
      {
        path: 'children',
        name: 'ParentChildren',
        component: ParentChildren,
        meta: {
          title: '子女信息',
          icon: 'UserFilled',
          permission: 'parent:children:view'
        }
      },
      {
        path: 'grades',
        name: 'ParentGrades',
        component: ParentGrades,
        meta: {
          title: '成绩查看',
          icon: 'Promotion',
          permission: 'parent:grades:view'
        }
      },
      {
        path: 'attendance',
        name: 'ParentAttendance',
        component: ParentAttendance,
        meta: {
          title: '考勤记录',
          icon: 'Calendar',
          permission: 'parent:attendance:view'
        }
      },
      {
        path: 'communication',
        name: 'ParentCommunication',
        component: ParentCommunication,
        meta: {
          title: '家校沟通',
          icon: 'ChatDotRound',
          permission: 'parent:communication:view'
        }
      },
      {
        path: 'activities',
        name: 'ParentActivities',
        component: ParentActivities,
        meta: {
          title: '活动通知',
          icon: 'Bell',
          permission: 'parent:activities:view'
        }
      },
      {
        path: 'payments',
        name: 'ParentPayments',
        component: ParentPayments,
        meta: {
          title: '缴费记录',
          icon: 'CreditCard',
          permission: 'parent:payments:view'
        }
      },
      {
        path: 'monitor',
        name: 'ParentMonitor',
        component: ParentMonitor,
        meta: {
          title: '学习监控',
          icon: 'Monitor',
          permission: 'parent:monitor:view'
        }
      },
      {
        path: 'profile',
        name: 'ParentProfile',
        component: ParentProfile,
        meta: {
          title: '个人信息',
          icon: 'User',
          keepAlive: true,
          permission: 'parent:profile:view'
        }
      }
    ]
  },

  // 错误页面
  {
    path: '/403',
    name: 'Forbidden',
    component: Forbidden,
    meta: {
      title: '访问被拒绝'
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: {
      title: '页面不存在'
    }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由前置守卫 - 权限验证
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 智慧校园管理系统`
  }

  const authStore = useAuthStore()

  // 检查是否需要认证
  if (to.meta.requiresAuth) {
    // 检查用户是否已登录
    if (!authStore.isAuthenticated) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      const userRole = authStore.userRole
      if (!to.meta.roles.includes(userRole)) {
        ElMessage.error('您没有权限访问此页面')
        next('/403')
        return
      }
    }

    // 检查具体权限
    if (to.meta.permission) {
      const userPermissions = authStore.permissions || []
      const hasPermission = userPermissions.includes(to.meta.permission) || userPermissions.includes('*')
      
      if (!hasPermission) {
        ElMessage.error('您没有权限访问此功能')
        next('/403')
        return
      }
    }
  }

  // 如果已登录用户访问登录页，重定向到对应的首页
  if (to.path === '/login' && authStore.isAuthenticated) {
    const userRole = authStore.userRole
    const homePathMap = {
      'STUDENT': '/student/dashboard',
      'TEACHER': '/teacher/dashboard',
      'PARENT': '/parent/dashboard',
      'ADMIN': '/admin/dashboard'
    }
    next(homePathMap[userRole] || '/dashboard')
    return
  }

  next()
})

// 路由后置守卫 - 页面加载完成
router.afterEach((to, from) => {
  // 可以在这里添加页面访问统计、埋点等逻辑
  console.log(`页面导航: ${from.path} -> ${to.path}`)
})

export default router
