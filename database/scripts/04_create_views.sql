-- =====================================================
-- 智慧校园管理平台 - 视图创建脚本
-- 版本: 1.0.0
-- 创建时间: 2025-06-03
-- 描述: 创建业务查询视图
-- =====================================================

USE campus_management;

-- ===========================================
-- 用户管理相关视图
-- ===========================================

-- 1. 学生信息概览视图
CREATE VIEW v_student_overview AS
SELECT
    s.id,
    s.student_no,
    u.real_name,
    u.email,
    u.phone,
    u.status as user_status,
    s.grade,
    c.class_name,
    c.class_no,
    tu.real_name as head_teacher_name,
    s.enrollment_date,
    s.graduation_date,
    s.status as student_status,
    s.created_time
FROM tb_student s
JOIN tb_user u ON s.user_id = u.id
LEFT JOIN tb_class c ON s.class_id = c.id
LEFT JOIN tb_teacher t ON c.head_teacher_id = t.id
LEFT JOIN tb_user tu ON t.user_id = tu.id;

-- 2. 教师信息概览视图
CREATE VIEW v_teacher_overview AS
SELECT
    t.id,
    t.teacher_no,
    u.real_name,
    u.email,
    u.phone,
    u.status as user_status,
    t.department,
    t.title,
    t.hire_date,
    t.status as teacher_status,
    COUNT(DISTINCT c.id) as class_count,
    COUNT(DISTINCT cs.id) as course_count,
    t.created_time
FROM tb_teacher t
JOIN tb_user u ON t.user_id = u.id
LEFT JOIN tb_class c ON t.id = c.head_teacher_id
LEFT JOIN tb_course_schedule cs ON t.id = cs.teacher_id AND cs.status = 1
GROUP BY t.id, t.teacher_no, u.real_name, u.email, u.phone, u.status, t.department, t.title, t.hire_date, t.status, t.created_time;

-- 3. 家长学生关系视图
CREATE VIEW v_parent_student_relation AS
SELECT
    p.id as parent_id,
    pu.real_name as parent_name,
    pu.email as parent_email,
    pu.phone as parent_phone,
    s.id as student_id,
    s.student_no,
    su.real_name as student_name,
    sp.relationship,
    sp.is_primary,
    c.class_name,
    s.grade
FROM tb_parent p
JOIN tb_user pu ON p.user_id = pu.id
JOIN tb_student_parent sp ON p.id = sp.parent_id
JOIN tb_student s ON sp.student_id = s.id
JOIN tb_user su ON s.user_id = su.id
LEFT JOIN tb_class c ON s.class_id = c.id;

-- ===========================================
-- 教务管理相关视图
-- ===========================================

-- 4. 教师课程安排视图
CREATE VIEW v_teacher_schedule AS
SELECT
    cs.id,
    t.teacher_no,
    tu.real_name as teacher_name,
    c.course_code,
    c.course_name,
    c.credits,
    cs.semester,
    cs.day_of_week,
    cs.start_time,
    cs.end_time,
    cs.classroom,
    cl.class_name,
    cs.current_students,
    cs.max_students,
    cs.status
FROM tb_course_schedule cs
JOIN tb_teacher t ON cs.teacher_id = t.id
JOIN tb_user tu ON t.user_id = tu.id
JOIN tb_course c ON cs.course_id = c.id
LEFT JOIN tb_class cl ON cs.class_id = cl.id;

-- 5. 学生课程表视图
CREATE VIEW v_student_schedule AS
SELECT
    s.id as student_id,
    s.student_no,
    su.real_name as student_name,
    c.course_code,
    c.course_name,
    c.credits,
    cs.semester,
    cs.day_of_week,
    cs.start_time,
    cs.end_time,
    cs.classroom,
    tu.real_name as teacher_name,
    sel.selection_time,
    sel.status as selection_status
FROM tb_student s
JOIN tb_user su ON s.user_id = su.id
JOIN tb_course_selection sel ON s.id = sel.student_id AND sel.status = 1
JOIN tb_course_schedule cs ON sel.schedule_id = cs.id
JOIN tb_course c ON cs.course_id = c.id
JOIN tb_teacher t ON cs.teacher_id = t.id
JOIN tb_user tu ON t.user_id = tu.id;

-- 6. 学生成绩统计视图
CREATE VIEW v_student_grade_stats AS
SELECT
    s.id as student_id,
    s.student_no,
    u.real_name as student_name,
    s.grade,
    c.class_name,
    g.semester,
    COUNT(g.id) as total_courses,
    ROUND(AVG(g.final_score), 2) as avg_score,
    SUM(CASE WHEN g.is_pass = 1 THEN 1 ELSE 0 END) as pass_courses,
    SUM(CASE WHEN g.is_pass = 0 THEN 1 ELSE 0 END) as fail_courses,
    SUM(CASE WHEN g.final_score >= 90 THEN 1 ELSE 0 END) as excellent_courses,
    SUM(CASE WHEN g.final_score >= 80 AND g.final_score < 90 THEN 1 ELSE 0 END) as good_courses,
    SUM(co.credits) as total_credits
FROM tb_student s
JOIN tb_user u ON s.user_id = u.id
LEFT JOIN tb_class c ON s.class_id = c.id
LEFT JOIN tb_grade g ON s.id = g.student_id
LEFT JOIN tb_course co ON g.course_id = co.id
GROUP BY s.id, s.student_no, u.real_name, s.grade, c.class_name, g.semester;

-- 7. 课程选课统计视图
CREATE VIEW v_course_selection_stats AS
SELECT
    c.id as course_id,
    c.course_code,
    c.course_name,
    c.credits,
    c.course_type,
    cs.semester,
    tu.real_name as teacher_name,
    cs.max_students,
    cs.current_students,
    ROUND((cs.current_students / cs.max_students) * 100, 2) as selection_rate,
    COUNT(sel.id) as total_selections,
    COUNT(CASE WHEN sel.status = 1 THEN 1 END) as current_selections,
    COUNT(CASE WHEN sel.status = 2 THEN 1 END) as dropped_selections
FROM tb_course c
JOIN tb_course_schedule cs ON c.id = cs.course_id
JOIN tb_teacher t ON cs.teacher_id = t.id
JOIN tb_user tu ON t.user_id = tu.id
LEFT JOIN tb_course_selection sel ON cs.id = sel.schedule_id
GROUP BY c.id, c.course_code, c.course_name, c.credits, c.course_type, cs.semester, tu.real_name, cs.max_students, cs.current_students;

-- ===========================================
-- 考试管理相关视图
-- ===========================================

-- 8. 考试安排概览视图
CREATE VIEW v_exam_overview AS
SELECT
    e.id,
    e.exam_name,
    e.exam_code,
    c.course_name,
    c.course_code,
    e.exam_type,
    e.semester,
    e.exam_date,
    e.start_time,
    e.end_time,
    e.duration,
    e.exam_mode,
    e.exam_status,
    tu.real_name as teacher_name,
    COUNT(DISTINCT es.id) as registered_students,
    COUNT(DISTINCT CASE WHEN es.exam_status = 'SUBMITTED' THEN es.id END) as submitted_students,
    COUNT(DISTINCT CASE WHEN es.exam_status = 'GRADED' THEN es.id END) as graded_students
FROM tb_exam e
JOIN tb_course c ON e.course_id = c.id
LEFT JOIN tb_teacher t ON e.teacher_id = t.id
LEFT JOIN tb_user tu ON t.user_id = tu.id
LEFT JOIN tb_exam_student es ON e.id = es.exam_id
GROUP BY e.id, e.exam_name, e.exam_code, c.course_name, c.course_code, e.exam_type, e.semester,
         e.exam_date, e.start_time, e.end_time, e.duration, e.exam_mode, e.exam_status, tu.real_name;

-- 9. 学生考试记录视图
CREATE VIEW v_student_exam_record AS
SELECT
    s.id as student_id,
    s.student_no,
    su.real_name as student_name,
    e.exam_name,
    e.exam_code,
    c.course_name,
    e.exam_date,
    e.start_time,
    e.end_time,
    es.start_time as actual_start_time,
    es.submit_time,
    es.exam_duration,
    es.total_score,
    es.exam_status,
    es.is_late_submit,
    er.room_name,
    es.seat_number
FROM tb_student s
JOIN tb_user su ON s.user_id = su.id
JOIN tb_exam_student es ON s.id = es.student_id
JOIN tb_exam e ON es.exam_id = e.id
JOIN tb_course c ON e.course_id = c.id
LEFT JOIN tb_exam_room er ON es.room_id = er.id;

-- ===========================================
-- 财务管理相关视图
-- ===========================================

-- 10. 学生缴费记录视图
CREATE VIEW v_student_payment_record AS
SELECT
    s.id as student_id,
    s.student_no,
    su.real_name as student_name,
    s.grade,
    c.class_name,
    fi.item_name,
    fi.item_code,
    fi.fee_type,
    fi.amount as item_amount,
    pr.amount as paid_amount,
    pr.payment_method,
    pr.payment_time,
    pr.transaction_no,
    pr.status as payment_status,
    ou.real_name as operator_name,
    pr.remarks
FROM tb_student s
JOIN tb_user su ON s.user_id = su.id
LEFT JOIN tb_class c ON s.class_id = c.id
JOIN tb_payment_record pr ON s.id = pr.student_id
JOIN tb_fee_item fi ON pr.fee_item_id = fi.id
LEFT JOIN tb_user ou ON pr.operator_id = ou.id;

-- 11. 缴费统计视图
CREATE VIEW v_payment_statistics AS
SELECT
    fi.id as fee_item_id,
    fi.item_name,
    fi.item_code,
    fi.fee_type,
    fi.applicable_grade,
    fi.amount as standard_amount,
    fi.due_date,
    COUNT(pr.id) as total_payments,
    COUNT(CASE WHEN pr.status = 1 THEN 1 END) as successful_payments,
    COUNT(CASE WHEN pr.status = 0 THEN 1 END) as failed_payments,
    COUNT(CASE WHEN pr.status = 2 THEN 1 END) as refunded_payments,
    SUM(CASE WHEN pr.status = 1 THEN pr.amount ELSE 0 END) as total_amount,
    ROUND(AVG(CASE WHEN pr.status = 1 THEN pr.amount END), 2) as avg_amount
FROM tb_fee_item fi
LEFT JOIN tb_payment_record pr ON fi.id = pr.fee_item_id
GROUP BY fi.id, fi.item_name, fi.item_code, fi.fee_type, fi.applicable_grade, fi.amount, fi.due_date;

-- ===========================================
-- 消息通知相关视图
-- ===========================================

-- 12. 用户消息概览视图
CREATE VIEW v_user_message_overview AS
SELECT
    u.id as user_id,
    u.username,
    u.real_name,
    COUNT(mr.id) as total_messages,
    COUNT(CASE WHEN mr.is_read = 0 AND mr.is_deleted = 0 THEN 1 END) as unread_count,
    COUNT(CASE WHEN mr.is_starred = 1 AND mr.is_deleted = 0 THEN 1 END) as starred_count,
    COUNT(CASE WHEN mr.is_deleted = 1 THEN 1 END) as deleted_count,
    MAX(CASE WHEN mr.is_deleted = 0 THEN mr.created_time END) as latest_message_time,
    MAX(CASE WHEN mr.is_read = 1 THEN mr.read_time END) as latest_read_time
FROM tb_user u
LEFT JOIN tb_message_read mr ON u.id = mr.user_id
GROUP BY u.id, u.username, u.real_name;

-- 13. 消息发送统计视图
CREATE VIEW v_message_send_statistics AS
SELECT
    m.id as message_id,
    m.message_code,
    m.title,
    m.message_type,
    m.priority_level,
    m.target_type,
    m.status,
    m.scheduled_time,
    m.sent_time,
    su.real_name as sender_name,
    m.total_recipients,
    m.sent_count,
    m.read_count,
    ROUND((m.read_count / m.sent_count) * 100, 2) as read_rate,
    COUNT(mr.id) as actual_recipients,
    COUNT(CASE WHEN mr.is_read = 1 THEN 1 END) as actual_read_count,
    m.created_time
FROM tb_message m
LEFT JOIN tb_user su ON m.sender_id = su.id
LEFT JOIN tb_message_read mr ON m.id = mr.message_id AND mr.is_deleted = 0
GROUP BY m.id, m.message_code, m.title, m.message_type, m.priority_level, m.target_type,
         m.status, m.scheduled_time, m.sent_time, su.real_name, m.total_recipients,
         m.sent_count, m.read_count, m.created_time;

-- ===========================================
-- 系统管理相关视图
-- ===========================================

-- 14. 用户活动统计视图
CREATE VIEW v_user_activity_stats AS
SELECT
    u.id as user_id,
    u.username,
    u.real_name,
    u.last_login_time,
    COUNT(ol.id) as total_operations,
    COUNT(CASE WHEN ol.status = 1 THEN 1 END) as successful_operations,
    COUNT(CASE WHEN ol.status = 0 THEN 1 END) as failed_operations,
    MAX(ol.created_time) as latest_operation_time,
    COUNT(CASE WHEN DATE(ol.created_time) = CURDATE() THEN 1 END) as today_operations
FROM tb_user u
LEFT JOIN tb_operation_log ol ON u.id = ol.user_id
GROUP BY u.id, u.username, u.real_name, u.last_login_time;

-- 15. 班级学生统计视图
CREATE VIEW v_class_student_stats AS
SELECT
    c.id as class_id,
    c.class_name,
    c.class_no,
    c.grade,
    ht.real_name as head_teacher_name,
    c.max_students,
    c.current_students,
    COUNT(s.id) as actual_students,
    COUNT(CASE WHEN s.status = 1 THEN 1 END) as active_students,
    COUNT(CASE WHEN s.status = 2 THEN 1 END) as graduated_students,
    COUNT(CASE WHEN s.status = 3 THEN 1 END) as suspended_students,
    COUNT(CASE WHEN s.status = 0 THEN 1 END) as dropped_students
FROM tb_class c
LEFT JOIN tb_teacher t ON c.head_teacher_id = t.id
LEFT JOIN tb_user ht ON t.user_id = ht.id
LEFT JOIN tb_student s ON c.id = s.class_id
GROUP BY c.id, c.class_name, c.class_no, c.grade, ht.real_name, c.max_students, c.current_students;

SELECT '所有视图创建成功' AS message;
SHOW FULL TABLES WHERE Table_type = 'VIEW';
