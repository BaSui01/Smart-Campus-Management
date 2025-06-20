-- 智慧校园管理系统性能优化索引
-- 添加必要的数据库索引以提升查询性能
-- 执行时间：2025-06-20

-- 用户表索引优化
-- 用户名唯一索引（如果不存在）
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_username ON tb_user(username) WHERE deleted = 0;

-- 邮箱唯一索引（如果不存在）
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON tb_user(email) WHERE deleted = 0;

-- 手机号索引
CREATE INDEX IF NOT EXISTS idx_user_phone ON tb_user(phone) WHERE deleted = 0;

-- 用户状态索引
CREATE INDEX IF NOT EXISTS idx_user_status ON tb_user(status, deleted);

-- 用户类型索引
CREATE INDEX IF NOT EXISTS idx_user_type ON tb_user(user_type, deleted);

-- 创建时间索引（用于排序和时间范围查询）
CREATE INDEX IF NOT EXISTS idx_user_created_at ON tb_user(created_at);

-- 学生表索引优化
-- 学号唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_student_number ON tb_student(student_number) WHERE deleted = 0;

-- 班级ID索引
CREATE INDEX IF NOT EXISTS idx_student_class_id ON tb_student(class_id, deleted);

-- 专业ID索引
CREATE INDEX IF NOT EXISTS idx_student_major_id ON tb_student(major_id, deleted);

-- 入学年份索引
CREATE INDEX IF NOT EXISTS idx_student_enrollment_year ON tb_student(enrollment_year, deleted);

-- 学生状态索引
CREATE INDEX IF NOT EXISTS idx_student_status ON tb_student(status, deleted);

-- 复合索引：班级+状态
CREATE INDEX IF NOT EXISTS idx_student_class_status ON tb_student(class_id, status, deleted);

-- 教师表索引优化
-- 工号唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_teacher_number ON tb_teacher(teacher_number) WHERE deleted = 0;

-- 部门ID索引
CREATE INDEX IF NOT EXISTS idx_teacher_department_id ON tb_teacher(department_id, deleted);

-- 职称索引
CREATE INDEX IF NOT EXISTS idx_teacher_title ON tb_teacher(title, deleted);

-- 教师状态索引
CREATE INDEX IF NOT EXISTS idx_teacher_status ON tb_teacher(status, deleted);

-- 课程表索引优化
-- 课程代码唯一索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_course_code ON tb_course(course_code) WHERE deleted = 0;

-- 学期索引
CREATE INDEX IF NOT EXISTS idx_course_semester ON tb_course(semester, deleted);

-- 教师ID索引
CREATE INDEX IF NOT EXISTS idx_course_teacher_id ON tb_course(teacher_id, deleted);

-- 课程类型索引
CREATE INDEX IF NOT EXISTS idx_course_type ON tb_course(course_type, deleted);

-- 课程状态索引
CREATE INDEX IF NOT EXISTS idx_course_status ON tb_course(status, deleted);

-- 复合索引：学期+状态
CREATE INDEX IF NOT EXISTS idx_course_semester_status ON tb_course(semester, status, deleted);

-- 复合索引：教师+学期
CREATE INDEX IF NOT EXISTS idx_course_teacher_semester ON tb_course(teacher_id, semester, deleted);

-- 选课表索引优化
-- 学生ID索引
CREATE INDEX IF NOT EXISTS idx_course_selection_student_id ON tb_course_selection(student_id, deleted);

-- 课程ID索引
CREATE INDEX IF NOT EXISTS idx_course_selection_course_id ON tb_course_selection(course_id, deleted);

-- 学期索引
CREATE INDEX IF NOT EXISTS idx_course_selection_semester ON tb_course_selection(semester, deleted);

-- 选课状态索引
CREATE INDEX IF NOT EXISTS idx_course_selection_status ON tb_course_selection(selection_status, deleted);

-- 复合索引：学生+学期（最重要的查询组合）
CREATE INDEX IF NOT EXISTS idx_course_selection_student_semester ON tb_course_selection(student_id, semester, deleted);

-- 复合索引：课程+学期
CREATE INDEX IF NOT EXISTS idx_course_selection_course_semester ON tb_course_selection(course_id, semester, deleted);

-- 复合索引：学生+课程（检查重复选课）
CREATE UNIQUE INDEX IF NOT EXISTS idx_course_selection_student_course ON tb_course_selection(student_id, course_id, semester) WHERE deleted = 0;

-- 成绩表索引优化
-- 学生ID索引
CREATE INDEX IF NOT EXISTS idx_grade_student_id ON tb_grade(student_id, deleted);

-- 课程ID索引
CREATE INDEX IF NOT EXISTS idx_grade_course_id ON tb_grade(course_id, deleted);

-- 学期索引
CREATE INDEX IF NOT EXISTS idx_grade_semester ON tb_grade(semester, deleted);

-- 考试类型索引
CREATE INDEX IF NOT EXISTS idx_grade_exam_type ON tb_grade(exam_type, deleted);

-- 复合索引：学生+学期
CREATE INDEX IF NOT EXISTS idx_grade_student_semester ON tb_grade(student_id, semester, deleted);

-- 复合索引：课程+学期
CREATE INDEX IF NOT EXISTS idx_grade_course_semester ON tb_grade(course_id, semester, deleted);

-- 复合索引：学生+课程+考试类型（唯一约束）
CREATE UNIQUE INDEX IF NOT EXISTS idx_grade_student_course_exam ON tb_grade(student_id, course_id, exam_type, semester) WHERE deleted = 0;

-- 考试表索引优化
-- 课程ID索引
CREATE INDEX IF NOT EXISTS idx_exam_course_id ON tb_exam(course_id, deleted);

-- 考试时间索引
CREATE INDEX IF NOT EXISTS idx_exam_time ON tb_exam(exam_time, deleted);

-- 考试状态索引
CREATE INDEX IF NOT EXISTS idx_exam_status ON tb_exam(exam_status, deleted);

-- 复合索引：课程+时间
CREATE INDEX IF NOT EXISTS idx_exam_course_time ON tb_exam(course_id, exam_time, deleted);

-- 缴费记录表索引优化
-- 学生ID索引
CREATE INDEX IF NOT EXISTS idx_payment_student_id ON tb_payment_record(student_id, deleted);

-- 缴费类型索引
CREATE INDEX IF NOT EXISTS idx_payment_type ON tb_payment_record(payment_type, deleted);

-- 缴费状态索引
CREATE INDEX IF NOT EXISTS idx_payment_status ON tb_payment_record(payment_status, deleted);

-- 缴费时间索引
CREATE INDEX IF NOT EXISTS idx_payment_time ON tb_payment_record(payment_time, deleted);

-- 复合索引：学生+状态
CREATE INDEX IF NOT EXISTS idx_payment_student_status ON tb_payment_record(student_id, payment_status, deleted);

-- 复合索引：类型+状态
CREATE INDEX IF NOT EXISTS idx_payment_type_status ON tb_payment_record(payment_type, payment_status, deleted);

-- 通知表索引优化
-- 用户ID索引
CREATE INDEX IF NOT EXISTS idx_notification_user_id ON tb_notification(user_id, deleted);

-- 通知状态索引
CREATE INDEX IF NOT EXISTS idx_notification_status ON tb_notification(notification_status, deleted);

-- 通知类型索引
CREATE INDEX IF NOT EXISTS idx_notification_type ON tb_notification(notification_type, deleted);

-- 创建时间索引
CREATE INDEX IF NOT EXISTS idx_notification_created_at ON tb_notification(created_at);

-- 复合索引：用户+状态
CREATE INDEX IF NOT EXISTS idx_notification_user_status ON tb_notification(user_id, notification_status, deleted);

-- 复合索引：用户+时间
CREATE INDEX IF NOT EXISTS idx_notification_user_time ON tb_notification(user_id, created_at, deleted);

-- 活动日志表索引优化
-- 用户ID索引
CREATE INDEX IF NOT EXISTS idx_activity_log_user_id ON tb_activity_log(user_id);

-- 操作类型索引
CREATE INDEX IF NOT EXISTS idx_activity_log_action ON tb_activity_log(action);

-- 创建时间索引
CREATE INDEX IF NOT EXISTS idx_activity_log_created_at ON tb_activity_log(created_at);

-- 复合索引：用户+时间
CREATE INDEX IF NOT EXISTS idx_activity_log_user_time ON tb_activity_log(user_id, created_at);

-- 复合索引：操作+时间
CREATE INDEX IF NOT EXISTS idx_activity_log_action_time ON tb_activity_log(action, created_at);

-- 系统配置表索引优化
-- 配置键索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_system_config_key ON tb_system_config(config_key) WHERE deleted = 0;

-- 配置分组索引
CREATE INDEX IF NOT EXISTS idx_system_config_group ON tb_system_config(config_group, deleted);

-- 班级表索引优化
-- 班级名称索引
CREATE INDEX IF NOT EXISTS idx_class_name ON tb_class(class_name, deleted);

-- 专业ID索引
CREATE INDEX IF NOT EXISTS idx_class_major_id ON tb_class(major_id, deleted);

-- 年级索引
CREATE INDEX IF NOT EXISTS idx_class_grade ON tb_class(grade, deleted);

-- 复合索引：专业+年级
CREATE INDEX IF NOT EXISTS idx_class_major_grade ON tb_class(major_id, grade, deleted);

-- 专业表索引优化
-- 专业名称索引
CREATE INDEX IF NOT EXISTS idx_major_name ON tb_major(major_name, deleted);

-- 学院ID索引
CREATE INDEX IF NOT EXISTS idx_major_department_id ON tb_major(department_id, deleted);

-- 学院表索引优化
-- 学院名称索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_department_name ON tb_department(department_name) WHERE deleted = 0;

-- 学院代码索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_department_code ON tb_department(department_code) WHERE deleted = 0;

-- 课程表索引优化
-- 教室索引
CREATE INDEX IF NOT EXISTS idx_schedule_classroom ON tb_schedule(classroom, deleted);

-- 时间段索引
CREATE INDEX IF NOT EXISTS idx_schedule_time_slot ON tb_schedule(time_slot, deleted);

-- 星期索引
CREATE INDEX IF NOT EXISTS idx_schedule_day_of_week ON tb_schedule(day_of_week, deleted);

-- 复合索引：课程+时间
CREATE INDEX IF NOT EXISTS idx_schedule_course_time ON tb_schedule(course_id, day_of_week, time_slot, deleted);

-- 复合索引：教室+时间（检查冲突）
CREATE INDEX IF NOT EXISTS idx_schedule_classroom_time ON tb_schedule(classroom, day_of_week, time_slot, deleted);

-- 添加统计信息更新
-- 更新表统计信息以优化查询计划
ANALYZE tb_user;
ANALYZE tb_student;
ANALYZE tb_teacher;
ANALYZE tb_course;
ANALYZE tb_course_selection;
ANALYZE tb_grade;
ANALYZE tb_exam;
ANALYZE tb_payment_record;
ANALYZE tb_notification;
ANALYZE tb_activity_log;
ANALYZE tb_system_config;
ANALYZE tb_class;
ANALYZE tb_major;
ANALYZE tb_department;
ANALYZE tb_schedule;

-- 索引创建完成提示
SELECT 'Performance indexes created successfully!' as message;
