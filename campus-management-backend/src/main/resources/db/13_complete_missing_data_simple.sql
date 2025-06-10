-- =====================================================
-- Smart Campus Management System - 简化补充数据脚本
-- 文件: 13_complete_missing_data_simple.sql
-- 描述: 为关键空表生成基础测试数据
-- 版本: 1.0.0
-- 创建时间: 2025-01-27
-- 编码: UTF-8
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

SELECT '=== Smart Campus Management System 简化补充数据开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 家长学生关系表 (tb_parent_student_relation)
-- =====================================================

SELECT '步骤1: 生成家长学生关系数据...' as '状态', NOW() as '时间';

INSERT INTO tb_parent_student_relation (parent_id, student_id, relationship, is_primary, status, deleted)
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
ON s.rn <= 2 AND s.rn = ((p.rn - 1) % 2) + 1  -- 每个家长对应1-2个学生
WHERE p.rn <= 4250;

SELECT '✓ 家长学生关系数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 2. 考试表 (tb_exam)
-- =====================================================

SELECT '步骤2: 生成考试数据...' as '状态', NOW() as '时间';

INSERT INTO tb_exam (exam_name, course_id, teacher_id, exam_type, exam_date, start_time, end_time, 
                    duration, classroom_id, total_score, pass_score, exam_status, exam_instructions, 
                    is_online, status, deleted)
SELECT 
    CONCAT(c.course_name, CASE 
        WHEN n.num % 3 = 0 THEN '期末考试'
        WHEN n.num % 3 = 1 THEN '期中考试'
        ELSE '随堂测验'
    END) as exam_name,
    c.id as course_id,
    cs.teacher_id,
    CASE 
        WHEN n.num % 3 = 0 THEN 'final'
        WHEN n.num % 3 = 1 THEN 'midterm'
        ELSE 'quiz'
    END as exam_type,
    DATE_ADD('2024-06-01', INTERVAL (n.num % 30) DAY) as exam_date,
    TIME(CONCAT(8 + (n.num % 6), ':00:00')) as start_time,
    TIME(CONCAT(10 + (n.num % 6), ':00:00')) as end_time,
    CASE 
        WHEN n.num % 3 = 0 THEN 120  -- 期末考试2小时
        WHEN n.num % 3 = 1 THEN 90   -- 期中考试1.5小时
        ELSE 45                      -- 随堂测验45分钟
    END as duration,
    ((n.num - 1) % 100) + 1 as classroom_id,
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
    CONCAT('系统生成的', c.course_name, '考试说明') as exam_instructions,
    CASE WHEN n.num % 5 = 0 THEN 1 ELSE 0 END as is_online,
    1 as status,
    0 as deleted
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) n
JOIN tb_course c ON c.id <= 50  -- 为前50门课程生成考试
LEFT JOIN tb_course_schedule cs ON cs.course_id = c.id
WHERE c.deleted = 0 AND cs.teacher_id IS NOT NULL
LIMIT 1000;

SELECT '✓ 考试数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 作业表 (tb_assignment)
-- =====================================================

SELECT '步骤3: 生成作业数据...' as '状态', NOW() as '时间';

INSERT INTO tb_assignment (assignment_title, course_id, teacher_id, assignment_description, assignment_type, 
                          due_date, total_score, submission_format, allow_late_submission, 
                          late_penalty, status, deleted)
SELECT 
    CONCAT(c.course_name, '作业', n.num) as assignment_title,
    c.id as course_id,
    cs.teacher_id,
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
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) n
JOIN tb_course c ON c.id <= 100  -- 为前100门课程生成作业
LEFT JOIN tb_course_schedule cs ON cs.course_id = c.id
WHERE c.deleted = 0 AND cs.teacher_id IS NOT NULL
LIMIT 1000;

SELECT '✓ 作业数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 通知表 (tb_notification)
-- =====================================================

SELECT '步骤4: 生成通知数据...' as '状态', NOW() as '时间';

INSERT INTO tb_notification (title, content, notification_type, sender_id, target_type, target_ids,
                            send_time, read_count, priority_level, expire_time, is_published,
                            status, deleted)
VALUES
('新学期选课通知', '各位同学，新学期选课系统已开放，请及时登录系统进行选课。', 'system', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 7 DAY), 8500, 'high', DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 1, 0),
('期末考试安排通知', '期末考试时间已确定，请各位同学查看考试安排，做好复习准备。', 'academic', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 5 DAY), 9200, 'high', DATE_ADD(NOW(), INTERVAL 15 DAY), 1, 1, 0),
('图书馆开放时间调整', '图书馆开放时间调整为8:00-22:00，请同学们注意。', 'general', 1, 'all_users', NULL,
 DATE_SUB(NOW(), INTERVAL 3 DAY), 12000, 'normal', DATE_ADD(NOW(), INTERVAL 60 DAY), 1, 1, 0),
('校园网络维护通知', '本周六晚上22:00-24:00进行网络维护，期间可能影响网络使用。', 'maintenance', 1, 'all_users', NULL,
 DATE_SUB(NOW(), INTERVAL 2 DAY), 11500, 'normal', DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 1, 0),
('学费缴费提醒', '请各位同学及时缴纳本学期学费，缴费截止日期为本月底。', 'financial', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 10 DAY), 7800, 'high', DATE_ADD(NOW(), INTERVAL 20 DAY), 1, 1, 0);

SELECT '✓ 通知数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 系统配置表 (tb_system_config)
-- =====================================================

SELECT '步骤5: 生成系统配置数据...' as '状态', NOW() as '时间';

INSERT INTO tb_system_config (config_key, config_value, config_type, description,
                             config_group, is_system, is_encrypted, status, deleted)
VALUES
('system.name', 'Smart Campus Management System', 'string', '系统名称', 'basic', 1, 0, 1, 0),
('system.version', '2.0.0', 'string', '系统版本', 'basic', 1, 0, 1, 0),
('course.max_selection', '8', 'integer', '学生最大选课数量', 'academic', 0, 0, 1, 0),
('grade.pass_score', '60', 'decimal', '及格分数线', 'academic', 0, 0, 1, 0),
('file.max_upload_size', '50', 'integer', '文件最大上传大小(MB)', 'system', 0, 0, 1, 0),
('security.session_timeout', '30', 'integer', '会话超时时间(分钟)', 'security', 0, 0, 1, 0);

SELECT '✓ 系统配置数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 恢复数据库设置并提交事务
-- =====================================================

-- 恢复数据库设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

-- 提交事务
COMMIT;

-- =====================================================
-- 7. 最终数据统计
-- =====================================================

SELECT '=== 简化补充数据生成统计报告 ===' as '状态', NOW() as '时间';

SELECT 
    table_name as '表名',
    table_rows as '记录数',
    CASE 
        WHEN table_rows > 0 THEN '✓ 有数据'
        ELSE '✗ 无数据'
    END as '数据状态'
FROM information_schema.tables 
WHERE table_schema = 'campus_management_db' 
    AND table_type = 'BASE TABLE'
ORDER BY table_rows DESC;

SELECT '=== Smart Campus Management System 简化补充数据完成 ===' as '状态', NOW() as '时间';
SELECT '🎉 关键表的测试数据已生成完毕！' as '提示信息';
