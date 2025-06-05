import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入页面组件
const Home = () => import('@/views/Home.vue')
const Login = () => import('@/views/Login.vue')
const Layout = () => import('@/views/Layout.vue')

// 学生端组件
const StudentDashboard = () => import('@/views/student/Dashboard.vue')
const StudentCourses = () => import('@/views/student/Courses.vue')
const StudentGrades = () => import('@/views/student/Grades.vue')
const StudentSchedule = () => import('@/views/student/Schedule.vue')
const StudentProfile = () => import('@/views/student/Profile.vue')
const StudentCourseSelection = () => import('@/views/student/CourseSelection.vue')

// 教师端组件
const TeacherDashboard = () => import('@/views/teacher/Dashboard.vue')
const TeacherCourses = () => import('@/views/teacher/Courses.vue')
const TeacherGrades = () => import('@/views/teacher/Grades.vue')

// 家长端组件
const ParentDashboard = () => import('@/views/parent/Dashboard.vue')
const ParentChildren = () => import('@/views/parent/Children.vue')
const ParentGrades = () => import('@/views/parent/Grades.vue')
const ParentAttendance = () => import('@/views/parent/Attendance.vue')
const ParentPayments = () => import('@/views/parent/Payments.vue')

// 错误页面
const NotFound = () => import('@/views/404.vue')
const Forbidden = () => import('@/views/403.vue')

// 其他组件（动态导入）
const StudentCourseDetail = () => import('@/views/student/CourseDetail.vue')
const StudentAssignments = () => import('@/views/student/Assignments.vue')
const StudentExams = () => import('@/views/student/Exams.vue')
const StudentLibrary = () => import('@/views/student/Library.vue')

const TeacherCourseDetail = () => import('@/views/teacher/CourseDetail.vue')
const TeacherStudents = () => import('@/views/teacher/Students.vue')
const TeacherAssignments = () => import('@/views/teacher/Assignments.vue')
const TeacherExams = () => import('@/views/teacher/Exams.vue')
const TeacherSchedule = () => import('@/views/teacher/Schedule.vue')
const TeacherProfile = () => import('@/views/teacher/Profile.vue')

const ParentCommunication = () => import('@/views/parent/Communication.vue')
const ParentActivities = () => import('@/views/parent/Activities.vue')
const ParentProfile = () => import('@/views/parent/Profile.vue')

// 页脚页面组件
const Features = () => import('@/views/about/Features.vue')
const About = () => import('@/views/about/About.vue')
const Privacy = () => import('@/views/legal/Privacy.vue')

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: '首页',
      requiresAuth: false
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  // 页脚页面路由
  {
    path: '/features',
    name: 'Features',
    component: Features,
    meta: {
      title: '功能介绍',
      requiresAuth: false
    }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: {
      title: '关于我们',
      requiresAuth: false
    }
  },
  {
    path: '/privacy',
    name: 'Privacy',
    component: Privacy,
    meta: {
      title: '隐私政策',
      requiresAuth: false
    }
  },
  // 临时占位页面路由
  {
    path: '/pricing',
    name: 'Pricing',
    redirect: '/features',
    meta: {
      title: '价格方案',
      requiresAuth: false
    }
  },
  {
    path: '/cases',
    name: 'Cases',
    redirect: '/features',
    meta: {
      title: '案例展示',
      requiresAuth: false
    }
  },
  {
    path: '/updates',
    name: 'Updates',
    redirect: '/features',
    meta: {
      title: '产品更新',
      requiresAuth: false
    }
  },
  {
    path: '/help',
    name: 'Help',
    redirect: '/features',
    meta: {
      title: '帮助中心',
      requiresAuth: false
    }
  },
  {
    path: '/docs',
    name: 'Docs',
    redirect: '/features',
    meta: {
      title: '技术文档',
      requiresAuth: false
    }
  },
  {
    path: '/faq',
    name: 'FAQ',
    redirect: '/features',
    meta: {
      title: '常见问题',
      requiresAuth: false
    }
  },
  {
    path: '/support',
    name: 'Support',
    redirect: '/features',
    meta: {
      title: '联系支持',
      requiresAuth: false
    }
  },
  {
    path: '/team',
    name: 'Team',
    redirect: '/about',
    meta: {
      title: '团队成员',
      requiresAuth: false
    }
  },
  {
    path: '/careers',
    name: 'Careers',
    redirect: '/about',
    meta: {
      title: '招贤纳士',
      requiresAuth: false
    }
  },
  {
    path: '/contact',
    name: 'Contact',
    redirect: '/about',
    meta: {
      title: '联系我们',
      requiresAuth: false
    }
  },
  {
    path: '/terms',
    name: 'Terms',
    redirect: '/privacy',
    meta: {
      title: '服务条款',
      requiresAuth: false
    }
  },
  {
    path: '/legal',
    name: 'Legal',
    redirect: '/privacy',
    meta: {
      title: '法律声明',
      requiresAuth: false
    }
  },
  // 学生端路由
  {
    path: '/student',
    component: Layout,
    meta: {
      requiresAuth: true,
      roles: ['student']
    },
    children: [
      {
        path: '',
        name: 'StudentDashboard',
        component: StudentDashboard,
        meta: {
          title: '学生仪表盘',
          icon: 'Odometer'
        }
      },
      {
        path: 'courses',
        name: 'StudentCourses',
        component: StudentCourses,
        meta: {
          title: '我的课程',
          icon: 'Document'
        }
      },
      {
        path: 'courses/:id',
        name: 'StudentCourseDetail',
        component: StudentCourseDetail,
        meta: {
          title: '课程详情',
          hideInMenu: true
        }
      },
      {
        path: 'grades',
        name: 'StudentGrades',
        component: StudentGrades,
        meta: {
          title: '成绩查询',
          icon: 'TrendCharts'
        }
      },
      {
        path: 'schedule',
        name: 'StudentSchedule',
        component: StudentSchedule,
        meta: {
          title: '课程表',
          icon: 'Calendar'
        }
      },
      {
        path: 'assignments',
        name: 'StudentAssignments',
        component: StudentAssignments,
        meta: {
          title: '作业管理',
          icon: 'EditPen'
        }
      },
      {
        path: 'exams',
        name: 'StudentExams',
        component: StudentExams,
        meta: {
          title: '考试安排',
          icon: 'DocumentChecked'
        }
      },
      {
        path: 'library',
        name: 'StudentLibrary',
        component: StudentLibrary,
        meta: {
          title: '图书馆',
          icon: 'Reading'
        }
      },
      {
        path: 'selection',
        name: 'StudentCourseSelection',
        component: StudentCourseSelection,
        meta: {
          title: '选课系统',
          icon: 'Select'
        }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: StudentProfile,
        meta: {
          title: '个人信息',
          icon: 'User'
        }
      }
    ]
  },
  // 教师端路由
  {
    path: '/teacher',
    component: Layout,
    meta: {
      requiresAuth: true,
      roles: ['teacher']
    },
    children: [
      {
        path: '',
        name: 'TeacherDashboard',
        component: TeacherDashboard,
        meta: {
          title: '教师仪表盘',
          icon: 'Odometer'
        }
      },
      {
        path: 'courses',
        name: 'TeacherCourses',
        component: TeacherCourses,
        meta: {
          title: '课程管理',
          icon: 'Document'
        }
      },
      {
        path: 'courses/:id',
        name: 'TeacherCourseDetail',
        component: TeacherCourseDetail,
        meta: {
          title: '课程详情',
          hideInMenu: true
        }
      },
      {
        path: 'students',
        name: 'TeacherStudents',
        component: TeacherStudents,
        meta: {
          title: '学生管理',
          icon: 'UserFilled'
        }
      },
      {
        path: 'assignments',
        name: 'TeacherAssignments',
        component: TeacherAssignments,
        meta: {
          title: '作业管理',
          icon: 'EditPen'
        }
      },
      {
        path: 'exams',
        name: 'TeacherExams',
        component: TeacherExams,
        meta: {
          title: '考试管理',
          icon: 'DocumentChecked'
        }
      },
      {
        path: 'schedule',
        name: 'TeacherSchedule',
        component: TeacherSchedule,
        meta: {
          title: '课程表',
          icon: 'Calendar'
        }
      },
      {
        path: 'grades',
        name: 'TeacherGrades',
        component: TeacherGrades,
        meta: {
          title: '成绩管理',
          icon: 'TrendCharts'
        }
      },
      {
        path: 'profile',
        name: 'TeacherProfile',
        component: TeacherProfile,
        meta: {
          title: '个人信息',
          icon: 'User'
        }
      }
    ]
  },
  // 家长端路由
  {
    path: '/parent',
    component: Layout,
    meta: {
      requiresAuth: true,
      roles: ['parent']
    },
    children: [
      {
        path: '',
        name: 'ParentDashboard',
        component: ParentDashboard,
        meta: {
          title: '家长仪表盘',
          icon: 'Odometer'
        }
      },
      {
        path: 'children',
        name: 'ParentChildren',
        component: ParentChildren,
        meta: {
          title: '子女信息',
          icon: 'UserFilled'
        }
      },
      {
        path: 'grades',
        name: 'ParentGrades',
        component: ParentGrades,
        meta: {
          title: '成绩查看',
          icon: 'TrendCharts'
        }
      },
      {
        path: 'attendance',
        name: 'ParentAttendance',
        component: ParentAttendance,
        meta: {
          title: '出勤记录',
          icon: 'Calendar'
        }
      },
      {
        path: 'communication',
        name: 'ParentCommunication',
        component: ParentCommunication,
        meta: {
          title: '家校沟通',
          icon: 'ChatLineRound'
        }
      },
      {
        path: 'activities',
        name: 'ParentActivities',
        component: ParentActivities,
        meta: {
          title: '校园活动',
          icon: 'Trophy'
        }
      },
      {
        path: 'payments',
        name: 'ParentPayments',
        component: ParentPayments,
        meta: {
          title: '缴费管理',
          icon: 'Money'
        }
      },
      {
        path: 'profile',
        name: 'ParentProfile',
        component: ParentProfile,
        meta: {
          title: '个人信息',
          icon: 'User'
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
      title: '权限不足',
      requiresAuth: false
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: {
      title: '页面未找到',
      requiresAuth: false
    }
  }
]

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

// 获取用户信息
function getUserInfo() {
  try {
    const userInfo = localStorage.getItem('userInfo')
    return userInfo ? JSON.parse(userInfo) : null
  } catch (error) {
    console.error('解析用户信息失败:', error)
    return null
  }
}

// 检查用户是否已认证
function isAuthenticated() {
  const token = localStorage.getItem('userToken')
  const userInfo = getUserInfo()
  return !!(token && userInfo)
}

// 检查用户角色权限
function hasPermission(userRole, requiredRoles) {
  if (!requiredRoles || requiredRoles.length === 0) {
    return true
  }
  return requiredRoles.includes(userRole)
}

// 路由前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 智慧校园管理平台`
  } else {
    document.title = '智慧校园管理平台'
  }

  // 检查是否需要认证
  if (to.meta.requiresAuth === true) {
    // 检查用户是否已登录
    if (!isAuthenticated()) {
      ElMessage.warning('请先登录')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    // 检查角色权限
    const userInfo = getUserInfo()
    const userRole = userInfo?.role

    if (to.meta.roles && !hasPermission(userRole, to.meta.roles)) {
      ElMessage.error('您没有权限访问此页面')
      next('/403')
      return
    }
  }

  // 如果已登录用户访问登录页，重定向到对应的首页
  if (to.path === '/login' && isAuthenticated()) {
    const userInfo = getUserInfo()
    const roleRoutes = {
      student: '/student',
      teacher: '/teacher',
      parent: '/parent'
    }
    
    next(roleRoutes[userInfo.role] || '/')
    return
  }

  next()
})

// 路由错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  ElMessage.error('页面加载失败，请刷新重试')
})

export default router