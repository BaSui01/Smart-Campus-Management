# 智慧校园管理系统 - API控制器完整映射表

## 📋 概述

本文档详细列出了自动化API测试脚本 `auto-api-test.bat` 中测试的所有36个REST API控制器和55个核心接口的完整映射关系。

## 🎯 测试覆盖统计

- **API控制器总数**: 36个
- **核心接口总数**: 55个
- **业务模块数**: 8大模块
- **测试文件数**: 56个 (含健康检查和登录)

## 📊 按模块分组的API控制器映射

### 1. 认证模块 (1个控制器，1个接口)

| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| AuthApiController | /api/auth | GET | /api/auth/me | 02_auth_current_user | 获取当前用户信息 |

### 2. 核心业务模块 (5个控制器，12个接口)

#### 2.1 用户管理 (UserApiController + OptimizedUserApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| UserApiController | /api/users | GET | /api/users?page=0&size=10 | 03_users_list | 用户列表 |
| OptimizedUserApiController | /api/v1/users | GET | /api/v1/users/stats | 04_users_stats | 用户统计 |
| OptimizedUserApiController | /api/v1/users | GET | /api/v1/users/count | 05_users_count | 用户计数 |

#### 2.2 学生管理 (StudentApiController + OptimizedStudentApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| StudentApiController | /api/students | GET | /api/students?page=0&size=10 | 06_students_list | 学生列表 |
| OptimizedStudentApiController | /api/v1/students | GET | /api/v1/students/stats | 07_students_stats | 学生统计 |
| OptimizedStudentApiController | /api/v1/students | GET | /api/v1/students/count | 08_students_count | 学生计数 |

#### 2.3 课程管理 (CourseApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| CourseApiController | /api/courses | GET | /api/courses?page=0&size=10 | 09_courses_list | 课程列表 |
| CourseApiController | /api/courses | GET | /api/courses/stats | 10_courses_stats | 课程统计 |

#### 2.4 院系管理 (DepartmentApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| DepartmentApiController | /api/departments | GET | /api/departments?page=0&size=10 | 11_departments_list | 院系列表 |
| DepartmentApiController | /api/departments | GET | /api/departments/tree | 12_departments_tree | 院系树结构 |

#### 2.5 班级管理 (ClassApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| ClassApiController | /api/classes | GET | /api/classes?page=0&size=10 | 13_classes_list | 班级列表 |
| ClassApiController | /api/classes | GET | /api/classes/stats | 14_classes_stats | 班级统计 |

### 3. 学术管理模块 (5个控制器，10个接口)

#### 3.1 作业管理 (AssignmentApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| AssignmentApiController | /api/assignments | GET | /api/assignments?page=0&size=10 | 15_assignments_list | 作业列表 |
| AssignmentApiController | /api/assignments | GET | /api/assignments/stats | 16_assignments_stats | 作业统计 |

#### 3.2 考勤管理 (AttendanceApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| AttendanceApiController | /api/attendance | GET | /api/attendance?page=0&size=10 | 17_attendance_list | 考勤列表 |
| AttendanceApiController | /api/attendance | GET | /api/attendance/stats | 18_attendance_stats | 考勤统计 |

#### 3.3 考试管理 (ExamApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| ExamApiController | /api/exams | GET | /api/exams?page=0&size=10 | 19_exams_list | 考试列表 |
| ExamApiController | /api/exams | GET | /api/exams/stats | 20_exams_stats | 考试统计 |

#### 3.4 成绩管理 (GradeApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| GradeApiController | /api/grades | GET | /api/grades?page=0&size=10 | 21_grades_list | 成绩列表 |
| GradeApiController | /api/grades | GET | /api/grades/stats | 22_grades_stats | 成绩统计 |

#### 3.5 选课管理 (CourseSelectionApiController + CourseSelectionPeriodApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| CourseSelectionApiController | /api/course-selections | GET | /api/course-selections?page=0&size=10 | 23_course_selections_list | 选课列表 |
| CourseSelectionPeriodApiController | /api/course-selection-periods | GET | /api/course-selection-periods?page=0&size=10 | 24_course_selection_periods | 选课时段 |

### 4. 系统管理模块 (4个控制器，7个接口)

#### 4.1 仪表盘 (DashboardApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| DashboardApiController | /api/dashboard | GET | /api/dashboard/stats | 25_dashboard_stats | 仪表盘统计 |
| DashboardApiController | /api/dashboard | GET | /api/dashboard/activities | 26_dashboard_activities | 仪表盘活动 |

#### 4.2 系统管理 (SystemApiController + SystemConfigApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| SystemApiController | /api/system | GET | /api/system/info | 27_system_info | 系统信息 |
| SystemConfigApiController | /api/system | GET | /api/system/config | 28_system_config | 系统配置 |

#### 4.3 通知管理 (NotificationApiController + NotificationTemplateApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| NotificationApiController | /api/notifications | GET | /api/notifications?page=0&size=10 | 29_notifications_list | 通知列表 |
| NotificationTemplateApiController | /api/notification-templates | GET | /api/notification-templates?page=0&size=10 | 30_notification_templates | 通知模板 |

#### 4.4 消息管理 (MessageApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| MessageApiController | /api/messages | GET | /api/messages?page=0&size=10 | 31_messages_list | 消息列表 |

### 5. 权限管理模块 (2个控制器，4个接口)

#### 5.1 角色管理 (RoleApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| RoleApiController | /api/roles | GET | /api/roles?page=0&size=10 | 32_roles_list | 角色列表 |
| RoleApiController | /api/roles | GET | /api/roles/stats | 33_roles_stats | 角色统计 |

#### 5.2 权限管理 (PermissionApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| PermissionApiController | /api/permissions | GET | /api/permissions?page=0&size=10 | 34_permissions_list | 权限列表 |
| PermissionApiController | /api/permissions | GET | /api/permissions/tree | 35_permissions_tree | 权限树结构 |

### 6. 财务管理模块 (2个控制器，3个接口)

#### 6.1 支付管理 (PaymentApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| PaymentApiController | /api/payments | GET | /api/payments?page=0&size=10 | 36_payments_list | 支付列表 |
| PaymentApiController | /api/payments | GET | /api/payments/stats | 37_payments_stats | 支付统计 |

#### 6.2 费用项目 (FeeItemApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| FeeItemApiController | /api/fee-items | GET | /api/fee-items?page=0&size=10 | 38_fee_items_list | 费用项目列表 |

### 7. 特殊功能模块 (5个控制器，8个接口)

#### 7.1 自动排课 (AutoScheduleApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| AutoScheduleApiController | /api/auto-schedule | GET | /api/auto-schedule/status | 39_auto_schedule_status | 自动排课状态 |
| AutoScheduleApiController | /api/auto-schedule | GET | /api/auto-schedule/config | 40_auto_schedule_config | 自动排课配置 |

#### 7.2 课表管理 (ScheduleApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| ScheduleApiController | /api/schedules | GET | /api/schedules?page=0&size=10 | 41_schedules_list | 课表列表 |

#### 7.3 课程安排 (CourseScheduleApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| CourseScheduleApiController | /api/course-schedules | GET | /api/course-schedules?page=0&size=10 | 42_course_schedules_list | 课程安排列表 |

#### 7.4 教室管理 (ClassroomApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| ClassroomApiController | /api/classrooms | GET | /api/classrooms?page=0&size=10 | 43_classrooms_list | 教室列表 |
| ClassroomApiController | /api/classrooms | GET | /api/classrooms/stats | 44_classrooms_stats | 教室统计 |

#### 7.5 缓存管理 (CacheManagementApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| CacheManagementApiController | /api/cache | GET | /api/cache/stats | 45_cache_stats | 缓存统计 |
| CacheManagementApiController | /api/cache | GET | /api/cache/info | 46_cache_info | 缓存信息 |

### 8. 扩展功能模块 (9个控制器，10个接口)

#### 8.1 活动日志 (ActivityLogApiController)
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| ActivityLogApiController | /api/activity-logs | GET | /api/activity-logs?page=0&size=10 | 47_activity_logs_list | 活动日志列表 |
| ActivityLogApiController | /api/activity-logs | GET | /api/activity-logs/stats | 48_activity_logs_stats | 活动日志统计 |

#### 8.2 其他扩展功能
| 控制器 | 路径 | 方法 | 接口端点 | 输出文件 | 描述 |
|--------|------|------|----------|----------|------|
| AssignmentSubmissionApiController | /api/assignment-submissions | GET | /api/assignment-submissions?page=0&size=10 | 49_assignment_submissions | 作业提交 |
| ExamQuestionApiController | /api/exam-questions | GET | /api/exam-questions?page=0&size=10 | 50_exam_questions | 考试题目 |
| ExamRecordApiController | /api/exam-records | GET | /api/exam-records?page=0&size=10 | 51_exam_records | 考试记录 |
| CourseResourceApiController | /api/course-resources | GET | /api/course-resources?page=0&size=10 | 52_course_resources | 课程资源 |
| ParentStudentRelationApiController | /api/parent-student-relations | GET | /api/parent-student-relations?page=0&size=10 | 53_parent_student_relations | 家长学生关系 |
| StudentEvaluationApiController | /api/student-evaluations | GET | /api/student-evaluations?page=0&size=10 | 54_student_evaluations | 学生评价 |
| TimeSlotApiController | /api/time-slots | GET | /api/time-slots?page=0&size=10 | 55_time_slots | 时间段 |

## 🔧 技术说明

### 认证方式
所有API接口都使用Bearer Token认证：
```
Authorization: Bearer {JWT_TOKEN}
```

### 响应格式
所有API接口返回统一的JSON格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2025-06-11T14:30:22"
}
```

### 分页参数
列表接口支持分页参数：
- `page`: 页码 (从0开始)
- `size`: 每页大小 (默认10)

## 📊 测试文件命名规则

```
{序号}_{模块名}_{功能}_{时间戳}.txt
```

例如：`03_users_list_20250611_143022.txt`

## 🎯 使用建议

1. **按模块分析**: 根据业务模块分组查看测试结果
2. **数据验证**: 重点检查统计接口的数据准确性
3. **性能监控**: 关注响应时间和数据量
4. **错误排查**: 优先检查核心业务模块的接口
5. **持续优化**: 根据测试结果优化API性能
