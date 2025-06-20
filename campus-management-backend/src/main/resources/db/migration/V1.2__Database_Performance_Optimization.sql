-- 智慧校园管理系统 - 数据库性能优化
-- 创建时间: 2025-06-20
-- 版本: V1.2
-- 说明: 添加复合索引、优化查询性能

-- ================================
-- 用户表性能优化
-- ================================

-- 用户登录查询优化索引
CREATE INDEX IF NOT EXISTS idx_user_login ON tb_user(username, password, deleted);

-- 用户状态查询优化索引
CREATE INDEX IF NOT EXISTS idx_user_status_type ON tb_user(user_type, status, deleted);

-- 用户创建时间范围查询索引
CREATE INDEX IF NOT EXISTS idx_user_created_range ON tb_user(created_at, deleted);

-- 用户邮箱手机查询索引
CREATE INDEX IF NOT EXISTS idx_user_contact ON tb_user(email, phone, deleted);

-- ================================
-- 学生表性能优化
-- ================================

-- 学生班级年级查询优化索引
CREATE INDEX IF NOT EXISTS idx_student_class_grade ON tb_student(class_id, grade, deleted);

-- 学生入学年份专业查询索引
CREATE INDEX IF NOT EXISTS idx_student_enrollment_major ON tb_student(enrollment_year, major, deleted);

-- 学生状态查询索引
CREATE INDEX IF NOT EXISTS idx_student_status ON tb_student(student_status, deleted);

-- 学生用户关联查询索引
CREATE INDEX IF NOT EXISTS idx_student_user_relation ON tb_student(user_id, deleted);

-- ================================
-- 教师表性能优化
-- ================================

-- 教师部门职位查询索引
CREATE INDEX IF NOT EXISTS idx_teacher_dept_position ON tb_teacher(department_id, position, deleted);

-- 教师用户关联查询索引
CREATE INDEX IF NOT EXISTS idx_teacher_user_relation ON tb_teacher(user_id, deleted);

-- 教师状态查询索引
CREATE INDEX IF NOT EXISTS idx_teacher_status ON tb_teacher(teacher_status, deleted);

-- ================================
-- 课程表性能优化
-- ================================

-- 课程教师学期查询索引
CREATE INDEX IF NOT EXISTS idx_course_teacher_semester ON tb_course(teacher_id, semester, deleted);

-- 课程类型学分查询索引
CREATE INDEX IF NOT EXISTS idx_course_type_credits ON tb_course(course_type, credits, deleted);

-- 课程状态查询索引
CREATE INDEX IF NOT EXISTS idx_course_status ON tb_course(course_status, deleted);

-- 课程代码查询索引
CREATE INDEX IF NOT EXISTS idx_course_code ON tb_course(course_code, deleted);

-- ================================
-- 成绩表性能优化
-- ================================

-- 成绩学生课程查询索引（最重要的复合索引）
CREATE INDEX IF NOT EXISTS idx_grade_student_course ON tb_grade(student_id, course_id, deleted);

-- 成绩学期考试类型查询索引
CREATE INDEX IF NOT EXISTS idx_grade_semester_exam ON tb_grade(semester, exam_type, deleted);

-- 成绩分数范围查询索引
CREATE INDEX IF NOT EXISTS idx_grade_score_range ON tb_grade(score, deleted);

-- 成绩教师查询索引
CREATE INDEX IF NOT EXISTS idx_grade_teacher ON tb_grade(teacher_id, semester, deleted);

-- 成绩统计查询索引
CREATE INDEX IF NOT EXISTS idx_grade_stats ON tb_grade(student_id, semester, exam_type, deleted);

-- ================================
-- 考勤表性能优化
-- ================================

-- 考勤学生日期查询索引（最重要的复合索引）
CREATE INDEX IF NOT EXISTS idx_attendance_student_date ON tb_attendance(student_id, attendance_date, deleted);

-- 考勤课程日期查询索引
CREATE INDEX IF NOT EXISTS idx_attendance_course_date ON tb_attendance(course_id, attendance_date, deleted);

-- 考勤状态查询索引
CREATE INDEX IF NOT EXISTS idx_attendance_status ON tb_attendance(attendance_status, deleted);

-- 考勤教师查询索引
CREATE INDEX IF NOT EXISTS idx_attendance_teacher ON tb_attendance(teacher_id, attendance_date, deleted);

-- 考勤时间范围查询索引
CREATE INDEX IF NOT EXISTS idx_attendance_time_range ON tb_attendance(attendance_date, attendance_time, deleted);

-- ================================
-- 班级表性能优化
-- ================================

-- 班级年级专业查询索引
CREATE INDEX IF NOT EXISTS idx_class_grade_major ON tb_class(grade, major_id, deleted);

-- 班级教师查询索引
CREATE INDEX IF NOT EXISTS idx_class_teacher ON tb_class(teacher_id, deleted);

-- 班级状态查询索引
CREATE INDEX IF NOT EXISTS idx_class_status ON tb_class(class_status, deleted);

-- ================================
-- 选课表性能优化
-- ================================

-- 选课学生课程查询索引
CREATE INDEX IF NOT EXISTS idx_course_selection_student_course ON tb_course_selection(student_id, course_id, deleted);

-- 选课学期状态查询索引
CREATE INDEX IF NOT EXISTS idx_course_selection_semester_status ON tb_course_selection(semester, selection_status, deleted);

-- 选课时间查询索引
CREATE INDEX IF NOT EXISTS idx_course_selection_time ON tb_course_selection(selection_time, deleted);

-- ================================
-- 作业表性能优化
-- ================================

-- 作业课程查询索引
CREATE INDEX IF NOT EXISTS idx_assignment_course ON tb_assignment(course_id, deleted);

-- 作业截止时间查询索引
CREATE INDEX IF NOT EXISTS idx_assignment_deadline ON tb_assignment(due_date, deleted);

-- 作业状态查询索引
CREATE INDEX IF NOT EXISTS idx_assignment_status ON tb_assignment(assignment_status, deleted);

-- ================================
-- 作业提交表性能优化
-- ================================

-- 作业提交学生作业查询索引
CREATE INDEX IF NOT EXISTS idx_assignment_submission_student_assignment ON tb_assignment_submission(student_id, assignment_id, deleted);

-- 作业提交时间查询索引
CREATE INDEX IF NOT EXISTS idx_assignment_submission_time ON tb_assignment_submission(submission_time, deleted);

-- 作业提交状态查询索引
CREATE INDEX IF NOT EXISTS idx_assignment_submission_status ON tb_assignment_submission(submission_status, deleted);

-- ================================
-- 考试表性能优化
-- ================================

-- 考试课程时间查询索引
CREATE INDEX IF NOT EXISTS idx_exam_course_time ON tb_exam(course_id, exam_date, deleted);

-- 考试类型状态查询索引
CREATE INDEX IF NOT EXISTS idx_exam_type_status ON tb_exam(exam_type, exam_status, deleted);

-- 考试地点查询索引
CREATE INDEX IF NOT EXISTS idx_exam_location ON tb_exam(exam_location, exam_date, deleted);

-- ================================
-- 考试记录表性能优化
-- ================================

-- 考试记录学生考试查询索引
CREATE INDEX IF NOT EXISTS idx_exam_record_student_exam ON tb_exam_record(student_id, exam_id, deleted);

-- 考试记录分数查询索引
CREATE INDEX IF NOT EXISTS idx_exam_record_score ON tb_exam_record(score, deleted);

-- 考试记录状态查询索引
CREATE INDEX IF NOT EXISTS idx_exam_record_status ON tb_exam_record(exam_status, deleted);

-- ================================
-- 通知表性能优化
-- ================================

-- 通知类型状态查询索引
CREATE INDEX IF NOT EXISTS idx_notification_type_status ON tb_notification(notification_type, status, deleted);

-- 通知发布时间查询索引
CREATE INDEX IF NOT EXISTS idx_notification_publish_time ON tb_notification(publish_time, deleted);

-- 通知目标用户查询索引
CREATE INDEX IF NOT EXISTS idx_notification_target ON tb_notification(target_type, target_id, deleted);

-- ================================
-- 通知接收表性能优化
-- ================================

-- 通知接收用户通知查询索引
CREATE INDEX IF NOT EXISTS idx_notification_receipt_user_notification ON tb_notification_receipt(user_id, notification_id);

-- 通知接收状态查询索引
CREATE INDEX IF NOT EXISTS idx_notification_receipt_status ON tb_notification_receipt(read_status, receive_time);

-- ================================
-- 角色权限表性能优化
-- ================================

-- 角色权限关联查询索引
CREATE INDEX IF NOT EXISTS idx_role_permission_relation ON tb_role_permission(role_id, permission_id);

-- 用户角色关联查询索引
CREATE INDEX IF NOT EXISTS idx_user_role_relation ON tb_user_role(user_id, role_id);

-- ================================
-- 系统日志表性能优化
-- ================================

-- 系统日志用户操作查询索引
CREATE INDEX IF NOT EXISTS idx_system_log_user_operation ON tb_system_log(user_id, operation_type, created_at);

-- 系统日志IP查询索引
CREATE INDEX IF NOT EXISTS idx_system_log_ip ON tb_system_log(client_ip, created_at);

-- 系统日志时间范围查询索引
CREATE INDEX IF NOT EXISTS idx_system_log_time_range ON tb_system_log(created_at, operation_type);

-- ================================
-- 查询性能优化视图
-- ================================

-- 学生成绩统计视图
CREATE OR REPLACE VIEW v_student_grade_stats AS
SELECT 
    s.id as student_id,
    s.student_no,
    u.real_name as student_name,
    s.grade,
    s.major,
    c.class_name,
    COUNT(g.id) as total_courses,
    AVG(g.score) as avg_score,
    MAX(g.score) as max_score,
    MIN(g.score) as min_score,
    COUNT(CASE WHEN g.score >= 90 THEN 1 END) as excellent_count,
    COUNT(CASE WHEN g.score >= 80 AND g.score < 90 THEN 1 END) as good_count,
    COUNT(CASE WHEN g.score >= 70 AND g.score < 80 THEN 1 END) as medium_count,
    COUNT(CASE WHEN g.score >= 60 AND g.score < 70 THEN 1 END) as pass_count,
    COUNT(CASE WHEN g.score < 60 THEN 1 END) as fail_count
FROM tb_student s
LEFT JOIN tb_user u ON s.user_id = u.id
LEFT JOIN tb_class c ON s.class_id = c.id
LEFT JOIN tb_grade g ON s.id = g.student_id AND g.deleted = 0
WHERE s.deleted = 0 AND u.deleted = 0
GROUP BY s.id, s.student_no, u.real_name, s.grade, s.major, c.class_name;

-- 课程选课统计视图
CREATE OR REPLACE VIEW v_course_selection_stats AS
SELECT 
    c.id as course_id,
    c.course_name,
    c.course_code,
    c.credits,
    c.max_students,
    t.real_name as teacher_name,
    COUNT(cs.id) as current_students,
    (c.max_students - COUNT(cs.id)) as available_slots,
    ROUND(COUNT(cs.id) * 100.0 / c.max_students, 2) as selection_rate
FROM tb_course c
LEFT JOIN tb_user t ON c.teacher_id = t.id
LEFT JOIN tb_course_selection cs ON c.id = cs.course_id AND cs.deleted = 0 AND cs.selection_status = 'SELECTED'
WHERE c.deleted = 0 AND t.deleted = 0
GROUP BY c.id, c.course_name, c.course_code, c.credits, c.max_students, t.real_name;

-- 考勤统计视图
CREATE OR REPLACE VIEW v_attendance_stats AS
SELECT 
    s.id as student_id,
    s.student_no,
    u.real_name as student_name,
    c.course_name,
    COUNT(a.id) as total_attendance,
    COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) as present_count,
    COUNT(CASE WHEN a.attendance_status = 'ABSENT' THEN 1 END) as absent_count,
    COUNT(CASE WHEN a.attendance_status = 'LATE' THEN 1 END) as late_count,
    COUNT(CASE WHEN a.attendance_status = 'LEAVE' THEN 1 END) as leave_count,
    ROUND(COUNT(CASE WHEN a.attendance_status = 'PRESENT' THEN 1 END) * 100.0 / COUNT(a.id), 2) as attendance_rate
FROM tb_student s
LEFT JOIN tb_user u ON s.user_id = u.id
LEFT JOIN tb_attendance a ON s.id = a.student_id AND a.deleted = 0
LEFT JOIN tb_course c ON a.course_id = c.id AND c.deleted = 0
WHERE s.deleted = 0 AND u.deleted = 0
GROUP BY s.id, s.student_no, u.real_name, c.course_name;

-- ================================
-- 性能优化存储过程
-- ================================

-- 批量更新学生成绩统计
DELIMITER //
CREATE PROCEDURE UpdateStudentGradeStats()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE student_id BIGINT;
    DECLARE avg_score DECIMAL(5,2);
    DECLARE total_credits INT;
    
    DECLARE student_cursor CURSOR FOR 
        SELECT s.id, AVG(g.score), SUM(c.credits)
        FROM tb_student s
        LEFT JOIN tb_grade g ON s.id = g.student_id AND g.deleted = 0
        LEFT JOIN tb_course c ON g.course_id = c.id AND c.deleted = 0
        WHERE s.deleted = 0
        GROUP BY s.id;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN student_cursor;
    
    read_loop: LOOP
        FETCH student_cursor INTO student_id, avg_score, total_credits;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 这里可以更新学生的统计信息到专门的统计表
        -- UPDATE tb_student_stats SET avg_score = avg_score, total_credits = total_credits WHERE student_id = student_id;
        
    END LOOP;
    
    CLOSE student_cursor;
END //
DELIMITER ;

-- ================================
-- 数据库配置优化建议
-- ================================

-- 设置查询缓存（如果使用MySQL 5.7及以下版本）
-- SET GLOBAL query_cache_size = 268435456; -- 256MB
-- SET GLOBAL query_cache_type = ON;

-- 设置InnoDB缓冲池大小（建议设置为系统内存的70-80%）
-- SET GLOBAL innodb_buffer_pool_size = 2147483648; -- 2GB

-- 设置InnoDB日志文件大小
-- SET GLOBAL innodb_log_file_size = 268435456; -- 256MB

-- 设置最大连接数
-- SET GLOBAL max_connections = 1000;

-- 设置慢查询日志
-- SET GLOBAL slow_query_log = ON;
-- SET GLOBAL long_query_time = 2;

-- ================================
-- 索引使用情况监控
-- ================================

-- 查看索引使用情况的查询语句
-- SELECT 
--     TABLE_SCHEMA,
--     TABLE_NAME,
--     INDEX_NAME,
--     COLUMN_NAME,
--     CARDINALITY
-- FROM information_schema.STATISTICS 
-- WHERE TABLE_SCHEMA = 'campus_management_db'
-- ORDER BY TABLE_NAME, INDEX_NAME;

-- 查看未使用的索引
-- SELECT 
--     s.TABLE_SCHEMA,
--     s.TABLE_NAME,
--     s.INDEX_NAME
-- FROM information_schema.STATISTICS s
-- LEFT JOIN performance_schema.table_io_waits_summary_by_index_usage t 
--     ON s.TABLE_SCHEMA = t.OBJECT_SCHEMA 
--     AND s.TABLE_NAME = t.OBJECT_NAME 
--     AND s.INDEX_NAME = t.INDEX_NAME
-- WHERE s.TABLE_SCHEMA = 'campus_management_db'
--     AND t.INDEX_NAME IS NULL
--     AND s.INDEX_NAME != 'PRIMARY';

COMMIT;
