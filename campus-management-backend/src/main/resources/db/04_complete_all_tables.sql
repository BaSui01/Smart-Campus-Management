-- =====================================================
-- Smart Campus Management System - 完整表数据补充脚本
-- 文件: 04_complete_all_tables.sql
-- 描述: 为所有35张表补充基础测试数据，确保每张表都有数据
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 编码: UTF-8
-- 
-- 目标：确保方案1也能为所有表插入数据
-- 数据规模：基于200个用户生成适量的业务数据
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

-- 设置优化参数
SET autocommit = 0;
SET unique_checks = 0;
SET foreign_key_checks = 0;

-- 开始事务
START TRANSACTION;

SELECT '=== Smart Campus Management System 完整表数据补充开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 学生表数据补充 (基于现有用户)
-- =====================================================

SELECT '步骤1: 补充学生表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_student (
    user_id, student_no, grade, major, class_id, enrollment_year, enrollment_date,
    academic_status, student_type, training_mode, academic_system, current_semester,
    total_credits, earned_credits, gpa, status, deleted
)
SELECT 
    u.id as user_id,
    CONCAT('2024', LPAD(ROW_NUMBER() OVER (ORDER BY u.id), 6, '0')) as student_no,
    '2024级' as grade,
    CASE 
        WHEN (u.id % 10) = 0 THEN '计算机科学与技术'
        WHEN (u.id % 10) = 1 THEN '软件工程'
        WHEN (u.id % 10) = 2 THEN '数据科学与大数据技术'
        WHEN (u.id % 10) = 3 THEN '人工智能'
        WHEN (u.id % 10) = 4 THEN '网络工程'
        WHEN (u.id % 10) = 5 THEN '信息安全'
        WHEN (u.id % 10) = 6 THEN '物联网工程'
        WHEN (u.id % 10) = 7 THEN '电子信息工程'
        WHEN (u.id % 10) = 8 THEN '通信工程'
        ELSE '自动化'
    END as major,
    ((u.id - 1) % 10) + 1 as class_id,  -- 分配到前10个班级
    2024 as enrollment_year,
    '2024-09-01' as enrollment_date,
    1 as academic_status,
    '本科生' as student_type,
    '全日制' as training_mode,
    4 as academic_system,
    '2024-2025-1' as current_semester,
    0 as total_credits,
    0 as earned_credits,
    0 as gpa,
    1 as status,
    0 as deleted
FROM tb_user u 
WHERE u.username LIKE 'student%' AND u.deleted = 0;

SELECT '✓ 学生表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 2. 班级表数据补充
-- =====================================================

SELECT '步骤2: 补充班级表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_class (
    class_name, class_code, department_id, major, grade, enrollment_year,
    head_teacher_id, student_count, max_capacity, classroom_location,
    class_status, enrollment_date, expected_graduation_date, academic_year,
    status, deleted
)
SELECT 
    CONCAT(d.department_name, '2024级', n.num, '班') as class_name,
    CONCAT(d.department_code, '2024', LPAD(n.num, 2, '0')) as class_code,
    d.id as department_id,
    CASE 
        WHEN n.num % 5 = 1 THEN '计算机科学与技术'
        WHEN n.num % 5 = 2 THEN '软件工程'
        WHEN n.num % 5 = 3 THEN '数据科学与大数据技术'
        WHEN n.num % 5 = 4 THEN '人工智能'
        ELSE '网络工程'
    END as major,
    '2024级' as grade,
    2024 as enrollment_year,
    t.id as head_teacher_id,
    30 + (n.num % 20) as student_count,
    50 as max_capacity,
    CONCAT('教学楼', ((n.num - 1) % 5 + 1), '-', LPAD(n.num + 100, 3, '0')) as classroom_location,
    1 as class_status,
    '2024-09-01' as enrollment_date,
    '2028-06-30' as expected_graduation_date,
    '2024-2025' as academic_year,
    1 as status,
    0 as deleted
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) n
CROSS JOIN (SELECT id, department_name, department_code FROM tb_department WHERE deleted = 0 LIMIT 5) d
LEFT JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0) t 
    ON t.rn = ((d.id - 1) * 10 + n.num - 1) % 50 + 1;

SELECT '✓ 班级表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 课程表数据补充
-- =====================================================

SELECT '步骤3: 补充课程表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_course (
    course_name, course_code, department_id, course_type, credits, hours,
    theory_hours, practice_hours, description, max_students, current_students,
    is_elective, status, deleted
)
SELECT 
    CONCAT(d.department_name, '专业课程', n.num) as course_name,
    CONCAT(d.department_code, LPAD(n.num, 4, '0')) as course_code,
    d.id as department_id,
    CASE 
        WHEN n.num % 4 = 1 THEN '必修课'
        WHEN n.num % 4 = 2 THEN '选修课'
        WHEN n.num % 4 = 3 THEN '实践课'
        ELSE '通识课'
    END as course_type,
    CASE 
        WHEN n.num % 4 = 1 THEN 4.0
        WHEN n.num % 4 = 2 THEN 2.0
        WHEN n.num % 4 = 3 THEN 3.0
        ELSE 2.0
    END as credits,
    CASE 
        WHEN n.num % 4 = 1 THEN 64
        WHEN n.num % 4 = 2 THEN 32
        WHEN n.num % 4 = 3 THEN 48
        ELSE 32
    END as hours,
    CASE 
        WHEN n.num % 4 = 1 THEN 48
        WHEN n.num % 4 = 2 THEN 24
        WHEN n.num % 4 = 3 THEN 16
        ELSE 24
    END as theory_hours,
    CASE 
        WHEN n.num % 4 = 1 THEN 16
        WHEN n.num % 4 = 2 THEN 8
        WHEN n.num % 4 = 3 THEN 32
        ELSE 8
    END as practice_hours,
    CONCAT('这是', d.department_name, '的专业课程', n.num, '，涵盖相关理论知识和实践技能。') as description,
    CASE 
        WHEN n.num % 4 = 1 THEN 120
        WHEN n.num % 4 = 2 THEN 80
        WHEN n.num % 4 = 3 THEN 60
        ELSE 100
    END as max_students,
    CASE 
        WHEN n.num % 4 = 1 THEN 80
        WHEN n.num % 4 = 2 THEN 45
        WHEN n.num % 4 = 3 THEN 30
        ELSE 60
    END as current_students,
    CASE WHEN n.num % 4 = 2 THEN 1 ELSE 0 END as is_elective,
    1 as status,
    0 as deleted
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) n
CROSS JOIN (SELECT id, department_name, department_code FROM tb_department WHERE deleted = 0 LIMIT 10) d;

SELECT '✓ 课程表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 课程调度表数据补充
-- =====================================================

SELECT '步骤4: 补充课程调度表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_course_schedule (
    course_id, teacher_id, classroom_id, time_slot_id, day_of_week,
    week_start, week_end, academic_year, semester, schedule_type,
    status, deleted
)
SELECT 
    c.id as course_id,
    t.id as teacher_id,
    ((c.id - 1) % 50) + 1 as classroom_id,
    ((c.id - 1) % 10) + 1 as time_slot_id,
    ((c.id - 1) % 5) + 1 as day_of_week,  -- 周一到周五
    1 as week_start,
    18 as week_end,
    '2024-2025' as academic_year,
    '第一学期' as semester,
    'regular' as schedule_type,
    1 as status,
    0 as deleted
FROM tb_course c
LEFT JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0) t 
    ON t.rn = ((c.id - 1) % 50) + 1
WHERE c.deleted = 0
LIMIT 200;

SELECT '✓ 课程调度表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 选课记录表数据补充
-- =====================================================

SELECT '步骤5: 补充选课记录表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_course_selection (
    student_id, course_id, semester, academic_year, selection_time,
    selection_status, selection_type, priority_level, is_retake,
    status, deleted
)
SELECT 
    s.id as student_id,
    c.id as course_id,
    '第一学期' as semester,
    '2024-2025' as academic_year,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as selection_time,
    'selected' as selection_status,
    'normal' as selection_type,
    0 as priority_level,
    0 as is_retake,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN tb_course c
WHERE s.deleted = 0 AND c.deleted = 0 
    AND ((s.id + c.id) % 3 = 0)  -- 每个学生选择约1/3的课程
LIMIT 500;

SELECT '✓ 选课记录表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 成绩表数据补充
-- =====================================================

SELECT '步骤6: 补充成绩表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_grade (
    student_id, course_id, semester, academic_year, usual_score, midterm_score,
    final_score, total_score, grade_level, gpa, is_passed,
    record_time, grade_status, teacher_id, status, deleted
)
SELECT
    cs.student_id,
    cs.course_id,
    cs.semester,
    cs.academic_year,
    ROUND(70 + RAND() * 25, 1) as usual_score,
    ROUND(65 + RAND() * 30, 1) as midterm_score,
    ROUND(60 + RAND() * 35, 1) as final_score,
    ROUND(65 + RAND() * 30, 1) as total_score,
    CASE
        WHEN ROUND(65 + RAND() * 30, 1) >= 90 THEN 'A'
        WHEN ROUND(65 + RAND() * 30, 1) >= 80 THEN 'B'
        WHEN ROUND(65 + RAND() * 30, 1) >= 70 THEN 'C'
        WHEN ROUND(65 + RAND() * 30, 1) >= 60 THEN 'D'
        ELSE 'F'
    END as grade_level,
    CASE
        WHEN ROUND(65 + RAND() * 30, 1) >= 90 THEN 4.0
        WHEN ROUND(65 + RAND() * 30, 1) >= 80 THEN 3.0
        WHEN ROUND(65 + RAND() * 30, 1) >= 70 THEN 2.0
        WHEN ROUND(65 + RAND() * 30, 1) >= 60 THEN 1.0
        ELSE 0.0
    END as gpa,
    CASE WHEN ROUND(65 + RAND() * 30, 1) >= 60 THEN TRUE ELSE FALSE END as is_passed,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 60) DAY) as record_time,
    'confirmed' as grade_status,
    sch.teacher_id,
    1 as status,
    0 as deleted
FROM tb_course_selection cs
LEFT JOIN tb_course_schedule sch ON sch.course_id = cs.course_id
WHERE cs.deleted = 0 AND cs.selection_status = 'selected'
LIMIT 400;

SELECT '✓ 成绩表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 7. 考勤表数据补充
-- =====================================================

SELECT '步骤7: 补充考勤表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_attendance (
    student_id, course_id, attendance_date, time_slot_id,
    attendance_status, check_in_time, check_out_time, location,
    device_info, remarks, recorded_by, status, deleted
)
SELECT
    cs.student_id,
    cs.course_id,
    DATE_SUB(NOW(), INTERVAL n.num DAY) as attendance_date,
    sch.time_slot_id,
    CASE
        WHEN n.num % 10 = 0 THEN '缺勤'
        WHEN n.num % 15 = 0 THEN '迟到'
        WHEN n.num % 20 = 0 THEN '早退'
        WHEN n.num % 25 = 0 THEN '请假'
        ELSE '正常'
    END as attendance_status,
    CASE
        WHEN n.num % 15 = 0 THEN TIME(DATE_ADD(CONCAT(DATE_SUB(NOW(), INTERVAL n.num DAY), ' 08:15:00'), INTERVAL FLOOR(RAND() * 30) MINUTE))
        ELSE TIME(DATE_ADD(CONCAT(DATE_SUB(NOW(), INTERVAL n.num DAY), ' 08:00:00'), INTERVAL FLOOR(RAND() * 10) MINUTE))
    END as check_in_time,
    CASE
        WHEN n.num % 20 = 0 THEN TIME(DATE_ADD(CONCAT(DATE_SUB(NOW(), INTERVAL n.num DAY), ' 09:30:00'), INTERVAL FLOOR(RAND() * 15) MINUTE))
        ELSE TIME(DATE_ADD(CONCAT(DATE_SUB(NOW(), INTERVAL n.num DAY), ' 09:45:00'), INTERVAL FLOOR(RAND() * 10) MINUTE))
    END as check_out_time,
    CONCAT('教学楼', ((cs.course_id - 1) % 5 + 1), '-', LPAD((cs.course_id % 20) + 101, 3, '0')) as location,
    'Mobile App' as device_info,
    CASE
        WHEN n.num % 25 = 0 THEN '因病请假'
        WHEN n.num % 10 = 0 THEN '无故缺勤'
        WHEN n.num % 15 = 0 THEN '交通拥堵迟到'
        ELSE NULL
    END as remarks,
    sch.teacher_id as recorded_by,
    1 as status,
    0 as deleted
FROM tb_course_selection cs
LEFT JOIN tb_course_schedule sch ON sch.course_id = cs.course_id
CROSS JOIN (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) n
WHERE cs.deleted = 0 AND cs.selection_status = 'selected' AND sch.teacher_id IS NOT NULL
LIMIT 1000;

SELECT '✓ 考勤表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 8. 缴费记录表数据补充
-- =====================================================

SELECT '步骤8: 补充缴费记录表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_payment_record (
    student_id, fee_item_id, payment_amount, payment_method, payment_status,
    payment_time, transaction_id, payment_channel, receipt_number,
    operator_id, remarks, status, deleted
)
SELECT
    s.id as student_id,
    f.id as fee_item_id,
    f.amount as payment_amount,
    CASE
        WHEN (s.id + f.id) % 4 = 0 THEN '支付宝'
        WHEN (s.id + f.id) % 4 = 1 THEN '微信支付'
        WHEN (s.id + f.id) % 4 = 2 THEN '银行卡'
        ELSE '现金'
    END as payment_method,
    CASE
        WHEN (s.id + f.id) % 20 = 0 THEN 'pending'
        WHEN (s.id + f.id) % 30 = 0 THEN 'failed'
        ELSE 'completed'
    END as payment_status,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY) as payment_time,
    CONCAT('TXN', YEAR(NOW()), LPAD(s.id, 6, '0'), LPAD(f.id, 4, '0')) as transaction_id,
    'online' as payment_channel,
    CONCAT('RCP', YEAR(NOW()), LPAD(s.id, 6, '0'), LPAD(f.id, 4, '0')) as receipt_number,
    1 as operator_id,
    CASE
        WHEN (s.id + f.id) % 10 = 0 THEN '享受助学金减免'
        WHEN (s.id + f.id) % 20 = 0 THEN '待处理'
        WHEN (s.id + f.id) % 30 = 0 THEN '支付失败，需重新支付'
        ELSE '正常缴费'
    END as remarks,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN tb_fee_item f
WHERE s.deleted = 0 AND f.deleted = 0
    AND ((s.id + f.id) % 5 = 0)  -- 每个学生缴纳约1/5的费用项目
LIMIT 300;

SELECT '✓ 缴费记录表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 9. 考试表数据补充
-- =====================================================

SELECT '步骤9: 补充考试表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_exam (
    exam_name, course_id, teacher_id, exam_type, exam_date, start_time, end_time,
    duration, classroom_id, total_score, pass_score, exam_status,
    exam_instructions, is_online, status, deleted
)
SELECT
    CONCAT(c.course_name, CASE
        WHEN n.num % 3 = 0 THEN '期末考试'
        WHEN n.num % 3 = 1 THEN '期中考试'
        ELSE '随堂测验'
    END) as exam_name,
    c.id as course_id,
    sch.teacher_id,
    CASE
        WHEN n.num % 3 = 0 THEN 'final'
        WHEN n.num % 3 = 1 THEN 'midterm'
        ELSE 'quiz'
    END as exam_type,
    DATE_ADD('2024-12-01', INTERVAL (n.num % 30) DAY) as exam_date,
    TIME(CONCAT(8 + (n.num % 6), ':00:00')) as start_time,
    TIME(CONCAT(10 + (n.num % 6), ':00:00')) as end_time,
    CASE
        WHEN n.num % 3 = 0 THEN 120
        WHEN n.num % 3 = 1 THEN 90
        ELSE 45
    END as duration,
    ((c.id - 1) % 50) + 1 as classroom_id,
    CASE
        WHEN n.num % 3 = 0 THEN 100.0
        WHEN n.num % 3 = 1 THEN 100.0
        ELSE 50.0
    END as total_score,
    CASE
        WHEN n.num % 3 = 0 THEN 60.0
        WHEN n.num % 3 = 1 THEN 60.0
        ELSE 30.0
    END as pass_score,
    CASE
        WHEN n.num % 10 = 0 THEN 'cancelled'
        WHEN n.num % 20 = 0 THEN 'scheduled'
        ELSE 'completed'
    END as exam_status,
    CONCAT('本次考试为', c.course_name, '的阶段性考核，请同学们认真对待。') as exam_instructions,
    CASE WHEN n.num % 5 = 0 THEN 1 ELSE 0 END as is_online,
    1 as status,
    0 as deleted
FROM tb_course c
LEFT JOIN tb_course_schedule sch ON sch.course_id = c.id
CROSS JOIN (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) n
WHERE c.deleted = 0 AND sch.teacher_id IS NOT NULL
LIMIT 100;

SELECT '✓ 考试表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 10. 作业表数据补充
-- =====================================================

SELECT '步骤10: 补充作业表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_assignment (
    assignment_title, course_id, teacher_id, assignment_description, assignment_type,
    due_date, total_score, submission_format, allow_late_submission,
    late_penalty, status, deleted
)
SELECT
    CONCAT(c.course_name, '作业', n.num) as assignment_title,
    c.id as course_id,
    sch.teacher_id,
    CONCAT('这是', c.course_name, '的第', n.num, '次作业，请认真完成。') as assignment_description,
    CASE
        WHEN n.num % 4 = 0 THEN 'homework'
        WHEN n.num % 4 = 1 THEN 'project'
        WHEN n.num % 4 = 2 THEN 'report'
        ELSE 'experiment'
    END as assignment_type,
    DATE_ADD(NOW(), INTERVAL (n.num * 7) DAY) as due_date,
    CASE
        WHEN n.num % 4 = 0 THEN 20.0
        WHEN n.num % 4 = 1 THEN 50.0
        WHEN n.num % 4 = 2 THEN 30.0
        ELSE 25.0
    END as total_score,
    CASE
        WHEN n.num % 4 = 0 THEN 'doc,docx,pdf'
        WHEN n.num % 4 = 1 THEN 'zip,rar,pdf'
        WHEN n.num % 4 = 2 THEN 'pdf,doc'
        ELSE 'doc,pdf,jpg'
    END as submission_format,
    CASE WHEN n.num % 3 = 0 THEN 1 ELSE 0 END as allow_late_submission,
    CASE
        WHEN n.num % 3 = 0 THEN 0.1
        WHEN n.num % 3 = 1 THEN 0.2
        ELSE 0.15
    END as late_penalty,
    CASE WHEN n.num % 20 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM tb_course c
LEFT JOIN tb_course_schedule sch ON sch.course_id = c.id
CROSS JOIN (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) n
WHERE c.deleted = 0 AND sch.teacher_id IS NOT NULL
LIMIT 100;

SELECT '✓ 作业表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 11. 家长学生关系表数据补充
-- =====================================================

SELECT '步骤11: 补充家长学生关系表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_parent_student_relation (
    parent_id, student_id, relationship, is_primary, status, deleted
)
SELECT
    p.id as parent_id,
    s.id as student_id,
    CASE
        WHEN (p.id + s.id) % 4 = 0 THEN '父亲'
        WHEN (p.id + s.id) % 4 = 1 THEN '母亲'
        WHEN (p.id + s.id) % 4 = 2 THEN '监护人'
        ELSE '其他亲属'
    END as relationship,
    CASE WHEN (p.id + s.id) % 2 = 0 THEN 1 ELSE 0 END as is_primary,
    1 as status,
    0 as deleted
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'parent%' AND deleted = 0) p
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_student WHERE deleted = 0) s
ON s.rn <= 2 AND s.rn = ((p.rn - 1) % 2) + 1;

SELECT '✓ 家长学生关系表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 12. 消息表数据补充
-- =====================================================

SELECT '步骤12: 补充消息表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_message (
    sender_id, receiver_id, message_type, title, content, is_read, status, deleted
)
SELECT
    t.id as sender_id,
    s.user_id as receiver_id,
    'private' as message_type,
    CONCAT('关于', c.course_name, '的学习反馈') as title,
    CONCAT('您在', c.course_name, '课程中表现良好，继续保持。') as content,
    CASE WHEN (t.id + s.id) % 3 = 0 THEN 1 ELSE 0 END as is_read,
    1 as status,
    0 as deleted
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0 LIMIT 50) t
CROSS JOIN tb_student s
LEFT JOIN tb_course c ON c.id = ((s.id - 1) % 20) + 1
WHERE s.deleted = 0
LIMIT 500;

SELECT '✓ 消息表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 13. 考试记录表数据补充
-- =====================================================

SELECT '步骤13: 补充考试记录表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_exam_record (
    exam_id, student_id, start_time, end_time, submit_time, obtained_score,
    exam_status, answers, remarks, status, deleted
)
SELECT
    e.id as exam_id,
    s.id as student_id,
    CONCAT(e.exam_date, ' ', e.start_time) as start_time,
    CONCAT(e.exam_date, ' ', e.end_time) as end_time,
    DATE_ADD(CONCAT(e.exam_date, ' ', e.start_time), INTERVAL (e.duration - 10) MINUTE) as submit_time,
    ROUND(50 + (s.id * 7 + e.id * 13) % 45, 2) as obtained_score,
    'submitted' as exam_status,
    CONCAT('{"question1":"答案A","question2":"答案B","question3":"答案C"}') as answers,
    CASE
        WHEN (s.id + e.id) % 10 = 0 THEN '表现优秀'
        WHEN (s.id + e.id) % 15 = 0 THEN '需要加强复习'
        ELSE '正常完成'
    END as remarks,
    1 as status,
    0 as deleted
FROM tb_exam e
CROSS JOIN tb_student s
WHERE e.deleted = 0 AND s.deleted = 0 AND e.exam_status = 'completed'
    AND ((s.id + e.id) % 5 = 0)  -- 每个考试约20%的学生参加
LIMIT 800;

SELECT '✓ 考试记录表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 14. 作业提交表数据补充
-- =====================================================

SELECT '步骤14: 补充作业提交表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_assignment_submission (
    assignment_id, student_id, submission_time, submission_content, attachment_path,
    submission_status, score, graded_time, graded_by, feedback, status, deleted
)
SELECT
    a.id as assignment_id,
    s.id as student_id,
    DATE_SUB(a.due_date, INTERVAL FLOOR((s.id * 3 + a.id * 7) % 5) DAY) as submission_time,
    CONCAT('这是学生', s.student_no, '提交的', a.assignment_title, '作业内容。') as submission_content,
    CONCAT('/uploads/assignments/', s.student_no, '_', a.id, '.pdf') as attachment_path,
    CASE
        WHEN (s.id + a.id) % 20 = 0 THEN 'late'
        WHEN (s.id + a.id) % 30 = 0 THEN 'pending'
        ELSE 'submitted'
    END as submission_status,
    ROUND(60 + (s.id * 11 + a.id * 17) % 35, 2) as score,
    DATE_ADD(a.due_date, INTERVAL 3 DAY) as graded_time,
    a.teacher_id as graded_by,
    CASE
        WHEN ROUND(60 + (s.id * 11 + a.id * 17) % 35, 2) >= 85 THEN '优秀作业，逻辑清晰，内容完整。'
        WHEN ROUND(60 + (s.id * 11 + a.id * 17) % 35, 2) >= 70 THEN '良好，但还有改进空间。'
        ELSE '需要进一步完善，注意格式和内容质量。'
    END as feedback,
    1 as status,
    0 as deleted
FROM tb_assignment a
CROSS JOIN tb_student s
WHERE a.deleted = 0 AND s.deleted = 0
    AND ((s.id + a.id) % 4 = 0)  -- 每个作业约25%的学生提交
LIMIT 600;

SELECT '✓ 作业提交表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 15. 学生评价表数据补充
-- =====================================================

SELECT '步骤15: 补充学生评价表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_student_evaluation (
    student_id, evaluator_id, evaluation_type, evaluation_period, academic_performance,
    behavior_performance, participation_level, overall_rating, strengths, weaknesses,
    suggestions, evaluation_date, status, deleted
)
SELECT
    s.id as student_id,
    t.id as evaluator_id,
    CASE
        WHEN (s.id + t.id) % 3 = 0 THEN '期末评价'
        WHEN (s.id + t.id) % 3 = 1 THEN '期中评价'
        ELSE '日常评价'
    END as evaluation_type,
    '2024-2025-1' as evaluation_period,
    ROUND(3.0 + (s.id * 7 + t.id * 11) % 200 / 100.0, 2) as academic_performance,
    ROUND(3.5 + (s.id * 11 + t.id * 7) % 150 / 100.0, 2) as behavior_performance,
    ROUND(3.2 + (s.id * 13 + t.id * 5) % 180 / 100.0, 2) as participation_level,
    ROUND(3.3 + (s.id * 5 + t.id * 13) % 170 / 100.0, 2) as overall_rating,
    CASE
        WHEN (s.id + t.id) % 5 = 0 THEN '学习认真，思维活跃'
        WHEN (s.id + t.id) % 5 = 1 THEN '团队协作能力强'
        WHEN (s.id + t.id) % 5 = 2 THEN '创新思维突出'
        WHEN (s.id + t.id) % 5 = 3 THEN '专业基础扎实'
        ELSE '学习态度端正'
    END as strengths,
    CASE
        WHEN (s.id + t.id) % 4 = 0 THEN '需要提高自主学习能力'
        WHEN (s.id + t.id) % 4 = 1 THEN '课堂参与度有待提升'
        WHEN (s.id + t.id) % 4 = 2 THEN '时间管理需要改善'
        ELSE '表达能力有待加强'
    END as weaknesses,
    CASE
        WHEN (s.id + t.id) % 6 = 0 THEN '建议多参与课堂讨论'
        WHEN (s.id + t.id) % 6 = 1 THEN '建议加强实践练习'
        WHEN (s.id + t.id) % 6 = 2 THEN '建议多阅读专业文献'
        WHEN (s.id + t.id) % 6 = 3 THEN '建议参加学科竞赛'
        WHEN (s.id + t.id) % 6 = 4 THEN '建议加强小组合作'
        ELSE '建议制定学习计划'
    END as suggestions,
    DATE_SUB(NOW(), INTERVAL FLOOR((s.id + t.id) / 10) DAY) as evaluation_date,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0 LIMIT 20) t
WHERE s.deleted = 0
    AND ((s.id + t.id) % 8 = 0)  -- 每个学生约12.5%被评价
LIMIT 400;

SELECT '✓ 学生评价表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 16. 系统活动日志表数据补充
-- =====================================================

SELECT '步骤16: 补充系统活动日志表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_activity_log (
    user_id, action, description, ip_address, user_agent,
    response_status, request_params, status, deleted
)
SELECT
    u.id as user_id,
    CASE
        WHEN (u.id % 10) = 0 THEN 'LOGIN'
        WHEN (u.id % 10) = 1 THEN 'LOGOUT'
        WHEN (u.id % 10) = 2 THEN 'COURSE_SELECT'
        WHEN (u.id % 10) = 3 THEN 'GRADE_VIEW'
        WHEN (u.id % 10) = 4 THEN 'ASSIGNMENT_SUBMIT'
        WHEN (u.id % 10) = 5 THEN 'EXAM_TAKE'
        WHEN (u.id % 10) = 6 THEN 'PROFILE_UPDATE'
        WHEN (u.id % 10) = 7 THEN 'PASSWORD_CHANGE'
        WHEN (u.id % 10) = 8 THEN 'ATTENDANCE_CHECK'
        ELSE 'SYSTEM_ACCESS'
    END as action,
    CASE
        WHEN (u.id % 10) = 0 THEN CONCAT('用户 ', u.username, ' 登录系统')
        WHEN (u.id % 10) = 1 THEN CONCAT('用户 ', u.username, ' 退出系统')
        WHEN (u.id % 10) = 2 THEN CONCAT('用户 ', u.username, ' 进行选课操作')
        WHEN (u.id % 10) = 3 THEN CONCAT('用户 ', u.username, ' 查看成绩')
        WHEN (u.id % 10) = 4 THEN CONCAT('用户 ', u.username, ' 提交作业')
        WHEN (u.id % 10) = 5 THEN CONCAT('用户 ', u.username, ' 参加考试')
        WHEN (u.id % 10) = 6 THEN CONCAT('用户 ', u.username, ' 更新个人信息')
        WHEN (u.id % 10) = 7 THEN CONCAT('用户 ', u.username, ' 修改密码')
        WHEN (u.id % 10) = 8 THEN CONCAT('用户 ', u.username, ' 签到考勤')
        ELSE CONCAT('用户 ', u.username, ' 访问系统')
    END as description,
    CONCAT('192.168.', FLOOR((u.id - 1) / 254) + 1, '.', ((u.id - 1) % 254) + 1) as ip_address,
    CASE
        WHEN u.id % 4 = 0 THEN 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        WHEN u.id % 4 = 1 THEN 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        WHEN u.id % 4 = 2 THEN 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15'
        ELSE 'Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36'
    END as user_agent,
    CASE WHEN u.id % 20 = 0 THEN 500 ELSE 200 END as response_status,
    CONCAT('{"timestamp":"', NOW(), '","session_id":"', UPPER(LEFT(MD5(CONCAT(u.id, NOW())), 8)), '"}') as request_params,
    1 as status,
    0 as deleted
FROM tb_user u
WHERE u.deleted = 0
LIMIT 1000;

SELECT '✓ 系统活动日志表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 17. 通知表数据补充
-- =====================================================

SELECT '步骤17: 补充通知表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_notification (
    title, content, notification_type, priority_level, sender_id, target_type,
    target_ids, send_time, expire_time, is_published, read_count, status, deleted
)
VALUES
('开学通知', '2024-2025学年第一学期开学时间为9月1日，请同学们按时返校报到。', 'announcement', 'high', 1, 'all', NULL, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 0, 1, 0),
('选课提醒', '第一学期选课时间为8月25日-8月30日，请同学们及时完成选课。', 'reminder', 'medium', 1, 'student', NULL, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(NOW(), INTERVAL 25 DAY), 1, 150, 1, 0),
('考试安排', '期中考试时间安排已发布，请关注各科目具体考试时间。', 'exam', 'high', 1, 'student', NULL, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(NOW(), INTERVAL 20 DAY), 1, 200, 1, 0),
('成绩发布', '第一学期期中考试成绩已发布，请登录系统查看。', 'grade', 'medium', 1, 'student', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_ADD(NOW(), INTERVAL 27 DAY), 1, 180, 1, 0),
('缴费通知', '学费缴纳截止时间为本月底，请及时完成缴费。', 'payment', 'high', 1, 'student', NULL, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_ADD(NOW(), INTERVAL 23 DAY), 1, 120, 1, 0),
('系统维护', '系统将于本周末进行维护，维护期间无法访问，请合理安排时间。', 'system', 'low', 1, 'all', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 28 DAY), 1, 50, 1, 0),
('图书馆通知', '图书馆新书到馆，欢迎同学们前来借阅。', 'announcement', 'low', 1, 'all', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 29 DAY), 1, 30, 1, 0);

SELECT '✓ 通知表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 18. 课程资源表数据补充
-- =====================================================

SELECT '步骤18: 补充课程资源表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_course_resource (
    course_id, resource_name, resource_type, file_path, file_size,
    uploader_id, download_count, description, status, deleted
)
SELECT
    c.id as course_id,
    CONCAT(c.course_name, '教学资源', n.num) as resource_name,
    CASE
        WHEN n.num % 4 = 1 THEN 'document'
        WHEN n.num % 4 = 2 THEN 'video'
        WHEN n.num % 4 = 3 THEN 'audio'
        ELSE 'image'
    END as resource_type,
    CONCAT('/resources/', c.id, '/', n.num,
        CASE
            WHEN n.num % 4 = 1 THEN '.pdf'
            WHEN n.num % 4 = 2 THEN '.mp4'
            WHEN n.num % 4 = 3 THEN '.mp3'
            ELSE '.jpg'
        END) as file_path,
    CASE
        WHEN n.num % 4 = 1 THEN 1024000 + (n.num * 100000)
        WHEN n.num % 4 = 2 THEN 10240000 + (n.num * 500000)
        WHEN n.num % 4 = 3 THEN 5120000 + (n.num * 200000)
        ELSE 512000 + (n.num * 50000)
    END as file_size,
    sch.teacher_id as uploader_id,
    FLOOR(RAND() * 100) as download_count,
    CONCAT('这是', c.course_name, '课程的第', n.num, '个教学资源') as description,
    1 as status,
    0 as deleted
FROM tb_course c
LEFT JOIN tb_course_schedule sch ON sch.course_id = c.id
CROSS JOIN (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3
) n
WHERE c.deleted = 0 AND sch.teacher_id IS NOT NULL
LIMIT 300;

SELECT '✓ 课程资源表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 19. 选课时间段表数据补充
-- =====================================================

SELECT '步骤19: 补充选课时间段表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_course_selection_period (
    period_name, academic_year, semester, start_time, end_time,
    target_grades, selection_type, max_credits, min_credits,
    description, status, deleted
)
VALUES
('2024-2025第一学期选课', '2024-2025', '第一学期', '2024-08-20 08:00:00', '2024-08-30 18:00:00',
 '2021级,2022级,2023级,2024级', 'normal', 25.0, 12.0, '2024-2025学年第一学期正常选课', 1, 0),
('2024-2025第二学期选课', '2024-2025', '第二学期', '2025-01-10 08:00:00', '2025-01-20 18:00:00',
 '2021级,2022级,2023级,2024级', 'normal', 25.0, 12.0, '2024-2025学年第二学期正常选课', 1, 0),
('补选阶段', '2024-2025', '第一学期', '2024-09-01 08:00:00', '2024-09-03 18:00:00',
 '2021级,2022级,2023级,2024级', 'makeup', 30.0, 0.0, '第一学期补选阶段', 1, 0),
('退选阶段', '2024-2025', '第一学期', '2024-09-15 08:00:00', '2024-09-20 18:00:00',
 '2021级,2022级,2023级,2024级', 'withdraw', 25.0, 10.0, '第一学期退选阶段', 1, 0);

SELECT '✓ 选课时间段表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 20. 考试题目表数据补充
-- =====================================================

SELECT '步骤20: 补充考试题目表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_exam_question (
    exam_id, question_text, question_type, question_order, score,
    options, correct_answer, explanation, difficulty_level, status, deleted
)
SELECT
    e.id as exam_id,
    CONCAT('题目', n.num, '：这是一道关于', c.course_name, '的',
        CASE WHEN n.num % 3 = 1 THEN '选择题'
             WHEN n.num % 3 = 2 THEN '判断题'
             ELSE '简答题' END) as question_text,
    CASE WHEN n.num % 3 = 1 THEN 'multiple_choice'
         WHEN n.num % 3 = 2 THEN 'true_false'
         ELSE 'short_answer' END as question_type,
    n.num as question_order,
    CASE WHEN n.num % 3 = 1 THEN 5.0
         WHEN n.num % 3 = 2 THEN 3.0
         ELSE 10.0 END as score,
    CASE WHEN n.num % 3 = 1 THEN '{"A":"选项A","B":"选项B","C":"选项C","D":"选项D"}'
         WHEN n.num % 3 = 2 THEN '{"A":"正确","B":"错误"}'
         ELSE NULL END as options,
    CASE WHEN n.num % 3 = 1 THEN 'A'
         WHEN n.num % 3 = 2 THEN 'A'
         ELSE '这是标准答案' END as correct_answer,
    CONCAT('这是题目', n.num, '的解析说明') as explanation,
    CASE WHEN (e.id + n.num) % 3 = 0 THEN 'easy'
         WHEN (e.id + n.num) % 3 = 1 THEN 'medium'
         ELSE 'hard' END as difficulty_level,
    1 as status,
    0 as deleted
FROM tb_exam e
LEFT JOIN tb_course c ON c.id = e.course_id
CROSS JOIN (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) n
WHERE e.deleted = 0 AND c.deleted = 0
LIMIT 300;

SELECT '✓ 考试题目表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 21. 资源访问日志表数据补充
-- =====================================================

SELECT '步骤21: 补充资源访问日志表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_resource_access_log (
    user_id, resource_type, resource_id, access_time, access_type,
    ip_address, user_agent, duration, status, deleted
)
SELECT
    u.id as user_id,
    'course_resource' as resource_type,
    cr.id as resource_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as access_time,
    CASE WHEN (u.id + cr.id) % 3 = 0 THEN 'download'
         WHEN (u.id + cr.id) % 3 = 1 THEN 'view'
         ELSE 'preview' END as access_type,
    CONCAT('192.168.', FLOOR((u.id - 1) / 254) + 1, '.', ((u.id - 1) % 254) + 1) as ip_address,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' as user_agent,
    FLOOR(RAND() * 3600) as duration,
    1 as status,
    0 as deleted
FROM tb_user u
CROSS JOIN tb_course_resource cr
WHERE u.username LIKE 'student%' AND u.deleted = 0 AND cr.deleted = 0
    AND ((u.id + cr.id) % 10 = 0)
LIMIT 200;

SELECT '✓ 资源访问日志表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 22. 日程安排表数据补充
-- =====================================================

SELECT '步骤22: 补充日程安排表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_schedule (
    schedule_name, description, schedule_type, start_date, end_date, start_time, end_time, location,
    organizer_id, participants, status, deleted
)
SELECT
    CASE WHEN n.num % 5 = 0 THEN '教学会议'
         WHEN n.num % 5 = 1 THEN '学术讲座'
         WHEN n.num % 5 = 2 THEN '考试安排'
         WHEN n.num % 5 = 3 THEN '培训活动'
         ELSE '其他活动' END as schedule_name,
    CONCAT('这是一个',
        CASE WHEN n.num % 5 = 0 THEN '教学会议'
             WHEN n.num % 5 = 1 THEN '学术讲座'
             WHEN n.num % 5 = 2 THEN '考试安排'
             WHEN n.num % 5 = 3 THEN '培训活动'
             ELSE '其他活动' END, '，请相关人员准时参加') as description,
    CASE WHEN n.num % 5 = 0 THEN 'meeting'
         WHEN n.num % 5 = 1 THEN 'lecture'
         WHEN n.num % 5 = 2 THEN 'exam'
         WHEN n.num % 5 = 3 THEN 'training'
         ELSE 'other' END as schedule_type,
    DATE_ADD(CURDATE(), INTERVAL n.num DAY) as start_date,
    DATE_ADD(CURDATE(), INTERVAL n.num DAY) as end_date,
    TIME('09:00:00') as start_time,
    TIME('11:00:00') as end_time,
    CONCAT('会议室', (n.num % 10) + 1) as location,
    t.id as organizer_id,
    CONCAT('参与人员', n.num) as participants,
    1 as status,
    0 as deleted
FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0 LIMIT 20) t
CROSS JOIN (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
) n
LIMIT 100;

SELECT '✓ 日程安排表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 23. 教师课程权限表数据补充
-- =====================================================

SELECT '步骤23: 补充教师课程权限表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_teacher_course_permission (
    teacher_id, course_id, permission_type, academic_year, semester,
    status, deleted
)
SELECT
    t.id as teacher_id,
    c.id as course_id,
    CASE WHEN (t.id + c.id) % 4 = 0 THEN 'teaching'
         WHEN (t.id + c.id) % 4 = 1 THEN 'grading'
         WHEN (t.id + c.id) % 4 = 2 THEN 'management'
         ELSE 'assistance' END as permission_type,
    '2024-2025' as academic_year,
    '第一学期' as semester,
    1 as status,
    0 as deleted
FROM (SELECT id FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0 LIMIT 30) t
CROSS JOIN (SELECT id FROM tb_course WHERE deleted = 0 LIMIT 20) c
WHERE ((t.id + c.id) % 6 = 0)
LIMIT 100;

SELECT '✓ 教师课程权限表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 24. 系统设置表数据补充
-- =====================================================

SELECT '步骤24: 补充系统设置表数据...' as '状态', NOW() as '时间';

INSERT IGNORE INTO tb_system_settings (
    setting_key, setting_value, setting_type, category, setting_name,
    description, sort_order, status, deleted
)
VALUES
('theme.primary_color', '#1890ff', 'color', 'appearance', '主题色', '系统主题色配置', 1, 1, 0),
('theme.secondary_color', '#52c41a', 'color', 'appearance', '辅助色', '系统辅助色配置', 2, 1, 0),
('page.size.default', '20', 'number', 'pagination', '默认分页大小', '列表页默认显示条数', 1, 1, 0),
('page.size.options', '10,20,50,100', 'string', 'pagination', '分页选项', '可选择的分页大小', 2, 1, 0),
('session.timeout', '30', 'number', 'security', '会话超时', '用户会话超时时间(分钟)', 1, 1, 0),
('password.complexity', 'medium', 'string', 'security', '密码复杂度', '密码复杂度要求', 2, 1, 0),
('backup.auto_enable', 'true', 'string', 'system', '自动备份', '是否启用自动备份功能', 1, 1, 0),
('backup.interval_hours', '24', 'number', 'system', '备份间隔', '自动备份间隔时间(小时)', 2, 1, 0),
('notification.email_enable', 'true', 'string', 'notification', '邮件通知', '是否启用邮件通知功能', 1, 1, 0),
('notification.sms_enable', 'false', 'string', 'notification', '短信通知', '是否启用短信通知功能', 2, 1, 0);

SELECT '✓ 系统设置表数据补充完成' as '状态', NOW() as '时间';

-- =====================================================
-- 25. 恢复数据库设置并提交事务
-- =====================================================

-- 恢复数据库设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

-- 提交事务
COMMIT;

SELECT '=== Smart Campus Management System 完整表数据补充完成 ===' as '状态', NOW() as '时间';
SELECT '🎉 所有35张表的完整测试数据已生成完毕！包含更多业务数据和系统日志。' as '提示信息';
