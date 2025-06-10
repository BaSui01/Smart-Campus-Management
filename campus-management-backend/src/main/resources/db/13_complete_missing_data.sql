-- =====================================================
-- Smart Campus Management System - 补充缺失数据脚本
-- 文件: 13_complete_missing_data.sql
-- 描述: 为所有空表生成完整的测试数据
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

SELECT '=== Smart Campus Management System 补充缺失数据开始 ===' as '状态', NOW() as '时间';

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
-- 2. 教师课程权限表 (tb_teacher_course_permission)
-- =====================================================

SELECT '步骤2: 生成教师课程权限数据...' as '状态', NOW() as '时间';

INSERT INTO tb_teacher_course_permission (teacher_id, course_id, permission_type, academic_year, 
                                         semester, status, deleted)
SELECT DISTINCT
    cs.teacher_id,
    cs.course_id,
    CASE 
        WHEN (cs.teacher_id + cs.course_id) % 3 = 0 THEN '主讲教师'
        WHEN (cs.teacher_id + cs.course_id) % 3 = 1 THEN '辅导教师'
        ELSE '实验教师'
    END as permission_type,
    cs.academic_year,
    cs.semester,
    1 as status,
    0 as deleted
FROM tb_course_schedule cs
WHERE cs.deleted = 0;

SELECT '✓ 教师课程权限数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 选课时间段表 (tb_course_selection_period)
-- =====================================================

SELECT '步骤3: 生成选课时间段数据...' as '状态', NOW() as '时间';

INSERT INTO tb_course_selection_period (period_name, academic_year, semester, start_time, end_time, 
                                       target_grades, selection_type, max_credits, min_credits, 
                                       description, status, deleted)
VALUES
('2024年第一学期选课', '2024', '第一学期', '2024-08-15 09:00:00', '2024-08-25 18:00:00', 
 '2021级,2022级,2023级,2024级', 'normal', 25.0, 15.0, '2024年第一学期正常选课时间段', 1, 0),
('2024年第二学期选课', '2024', '第二学期', '2024-12-15 09:00:00', '2024-12-25 18:00:00', 
 '2021级,2022级,2023级,2024级', 'normal', 25.0, 15.0, '2024年第二学期正常选课时间段', 1, 0),
('2024年第一学期补选', '2024', '第一学期', '2024-09-01 09:00:00', '2024-09-05 18:00:00', 
 '2021级,2022级,2023级,2024级', 'makeup', 30.0, 10.0, '2024年第一学期补选时间段', 1, 0),
('2024年第二学期补选', '2024', '第二学期', '2025-01-01 09:00:00', '2025-01-05 18:00:00', 
 '2021级,2022级,2023级,2024级', 'makeup', 30.0, 10.0, '2024年第二学期补选时间段', 1, 0),
('2024年重修选课', '2024', '第一学期', '2024-08-20 09:00:00', '2024-08-30 18:00:00', 
 '2021级,2022级,2023级', 'retake', 20.0, 5.0, '2024年重修课程选课时间段', 1, 0);

SELECT '✓ 选课时间段数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 考试表 (tb_exam)
-- =====================================================

SELECT '步骤4: 生成考试数据...' as '状态', NOW() as '时间';

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
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
) n
JOIN tb_course c ON c.id <= 50  -- 为前50门课程生成考试
LEFT JOIN tb_course_schedule cs ON cs.course_id = c.id
WHERE c.deleted = 0 AND cs.teacher_id IS NOT NULL
LIMIT 1500;

SELECT '✓ 考试数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 考试记录表 (tb_exam_record)
-- =====================================================

SELECT '步骤5: 生成考试记录数据...' as '状态', NOW() as '时间';

INSERT INTO tb_exam_record (exam_id, student_id, obtained_score, exam_status, start_time, end_time,
                           submit_time, ip_address, browser_info, remarks, status, deleted)
SELECT
    e.id as exam_id,
    s.id as student_id,
    CASE
        WHEN (e.id + s.id) % 20 = 0 THEN NULL  -- 缺考
        ELSE ROUND(50 + (e.id + s.id * 17) % 50, 1)  -- 50-100分
    END as obtained_score,
    CASE
        WHEN (e.id + s.id) % 20 = 0 THEN 'not_started'
        WHEN (e.id + s.id) % 30 = 0 THEN 'in_progress'
        ELSE 'completed'
    END as exam_status,
    CASE
        WHEN (e.id + s.id) % 20 = 0 THEN NULL
        ELSE TIMESTAMP(e.exam_date, e.start_time)
    END as start_time,
    CASE
        WHEN (e.id + s.id) % 20 = 0 THEN NULL
        ELSE DATE_ADD(TIMESTAMP(e.exam_date, e.start_time), INTERVAL e.duration MINUTE)
    END as end_time,
    CASE
        WHEN (e.id + s.id) % 20 = 0 THEN NULL
        ELSE DATE_ADD(TIMESTAMP(e.exam_date, e.start_time), INTERVAL (e.duration - 5) MINUTE)
    END as submit_time,
    CONCAT('192.168.', 1 + (e.id + s.id) % 254, '.', 1 + (e.id * s.id) % 254) as ip_address,
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36' as browser_info,
    CASE
        WHEN (e.id + s.id) % 20 = 0 THEN '学生缺考'
        WHEN (e.id + s.id) % 30 = 0 THEN '考试进行中'
        ELSE '正常完成考试'
    END as remarks,
    1 as status,
    0 as deleted
FROM tb_exam e
JOIN tb_course_selection cs ON cs.course_id = e.course_id AND cs.selection_status = 'selected'
JOIN tb_student s ON s.id = cs.student_id
WHERE e.deleted = 0 AND s.deleted = 0 AND cs.deleted = 0
LIMIT 8000;

SELECT '✓ 考试记录数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 作业表 (tb_assignment)
-- =====================================================

SELECT '步骤6: 生成作业数据...' as '状态', NOW() as '时间';

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
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20
) n
JOIN tb_course c ON c.id <= 100  -- 为前100门课程生成作业
LEFT JOIN tb_course_schedule cs ON cs.course_id = c.id
WHERE c.deleted = 0 AND cs.teacher_id IS NOT NULL
LIMIT 2000;

SELECT '✓ 作业数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 7. 作业提交表 (tb_assignment_submission)
-- =====================================================

SELECT '步骤7: 生成作业提交数据...' as '状态', NOW() as '时间';

INSERT INTO tb_assignment_submission (assignment_id, student_id, submission_content, file_path,
                                     submission_time, score, feedback, grading_time,
                                     submission_status, is_late, status, deleted)
SELECT
    a.id as assignment_id,
    s.id as student_id,
    CONCAT('学生', s.id, '提交的', a.assignment_title, '作业内容') as submission_content,
    CONCAT('/submissions/', a.id, '_', s.id, '_assignment.pdf') as file_path,
    CASE
        WHEN (a.id + s.id) % 10 = 0 THEN NULL  -- 未提交
        WHEN (a.id + s.id) % 8 = 0 THEN DATE_ADD(a.due_date, INTERVAL 1 DAY)  -- 迟交
        ELSE DATE_SUB(a.due_date, INTERVAL (a.id + s.id) % 5 DAY)  -- 正常提交
    END as submission_time,
    CASE
        WHEN (a.id + s.id) % 10 = 0 THEN NULL  -- 未提交
        WHEN (a.id + s.id) % 15 = 0 THEN NULL  -- 未评分
        ELSE ROUND(15 + (a.id + s.id * 13) % 6, 1)  -- 15-20分
    END as score,
    CASE
        WHEN (a.id + s.id) % 10 = 0 THEN NULL
        WHEN (a.id + s.id) % 15 = 0 THEN NULL
        ELSE CONCAT('作业完成质量', CASE WHEN (a.id + s.id) % 3 = 0 THEN '优秀' WHEN (a.id + s.id) % 3 = 1 THEN '良好' ELSE '一般' END)
    END as feedback,
    CASE
        WHEN (a.id + s.id) % 10 = 0 THEN NULL
        WHEN (a.id + s.id) % 15 = 0 THEN NULL
        ELSE DATE_ADD(a.due_date, INTERVAL 3 DAY)
    END as grading_time,
    CASE
        WHEN (a.id + s.id) % 10 = 0 THEN 'not_submitted'
        WHEN (a.id + s.id) % 15 = 0 THEN 'submitted'
        ELSE 'graded'
    END as submission_status,
    CASE
        WHEN (a.id + s.id) % 8 = 0 THEN 1
        ELSE 0
    END as is_late,
    1 as status,
    0 as deleted
FROM tb_assignment a
JOIN tb_course_selection cs ON cs.course_id = a.course_id AND cs.selection_status = 'selected'
JOIN tb_student s ON s.id = cs.student_id
WHERE a.deleted = 0 AND s.deleted = 0 AND cs.deleted = 0
LIMIT 12000;

SELECT '✓ 作业提交数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 8. 课程资源表 (tb_course_resource)
-- =====================================================

SELECT '步骤8: 生成课程资源数据...' as '状态', NOW() as '时间';

INSERT INTO tb_course_resource (course_id, resource_name, resource_type, file_path, file_size,
                               upload_time, uploader_id, download_count, description,
                               is_public, status, deleted)
SELECT
    c.id as course_id,
    CONCAT(c.course_name, CASE
        WHEN n.num % 5 = 0 THEN '课件'
        WHEN n.num % 5 = 1 THEN '教学视频'
        WHEN n.num % 5 = 2 THEN '参考资料'
        WHEN n.num % 5 = 3 THEN '习题集'
        ELSE '实验指导'
    END, n.num) as resource_name,
    CASE
        WHEN n.num % 5 = 0 THEN 'document'
        WHEN n.num % 5 = 1 THEN 'video'
        WHEN n.num % 5 = 2 THEN 'document'
        WHEN n.num % 5 = 3 THEN 'document'
        ELSE 'document'
    END as resource_type,
    CONCAT('/course_resources/', c.id, '/', n.num, CASE
        WHEN n.num % 5 = 0 THEN '.pptx'
        WHEN n.num % 5 = 1 THEN '.mp4'
        WHEN n.num % 5 = 2 THEN '.pdf'
        WHEN n.num % 5 = 3 THEN '.pdf'
        ELSE '.doc'
    END) as file_path,
    CASE
        WHEN n.num % 5 = 0 THEN 1024 * (50 + n.num % 50)  -- PPT 50-100KB
        WHEN n.num % 5 = 1 THEN 1024 * 1024 * (100 + n.num % 200)  -- 视频 100-300MB
        WHEN n.num % 5 = 2 THEN 1024 * (500 + n.num % 500)  -- PDF 500KB-1MB
        WHEN n.num % 5 = 3 THEN 1024 * (200 + n.num % 300)  -- 习题 200-500KB
        ELSE 1024 * (100 + n.num % 200)  -- 文档 100-300KB
    END as file_size,
    DATE_SUB(NOW(), INTERVAL (n.num * 7) DAY) as upload_time,
    cs.teacher_id as uploader_id,
    (n.num + c.id) % 100 as download_count,
    CONCAT('这是', c.course_name, '的教学资源，供学生学习使用。') as description,
    CASE WHEN n.num % 4 = 0 THEN 0 ELSE 1 END as is_public,
    CASE WHEN n.num % 30 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) n
JOIN tb_course c ON c.id <= 200  -- 为前200门课程生成资源
LEFT JOIN tb_course_schedule cs ON cs.course_id = c.id
WHERE c.deleted = 0 AND cs.teacher_id IS NOT NULL
LIMIT 2000;

SELECT '✓ 课程资源数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 9. 资源访问日志表 (tb_resource_access_log)
-- =====================================================

SELECT '步骤9: 生成资源访问日志数据...' as '状态', NOW() as '时间';

INSERT INTO tb_resource_access_log (resource_id, user_id, access_time, access_type, ip_address,
                                   user_agent, download_success, access_duration, status, deleted)
SELECT
    cr.id as resource_id,
    s.user_id as user_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as access_time,
    CASE
        WHEN (cr.id + s.id) % 3 = 0 THEN 'view'
        WHEN (cr.id + s.id) % 3 = 1 THEN 'download'
        ELSE 'preview'
    END as access_type,
    CONCAT('192.168.', 1 + (cr.id + s.id) % 254, '.', 1 + (cr.id * s.id) % 254) as ip_address,
    CASE
        WHEN (cr.id + s.id) % 4 = 0 THEN 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        WHEN (cr.id + s.id) % 4 = 1 THEN 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        WHEN (cr.id + s.id) % 4 = 2 THEN 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'
        ELSE 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15'
    END as user_agent,
    CASE
        WHEN (cr.id + s.id) % 20 = 0 THEN 0  -- 下载失败
        ELSE 1  -- 下载成功
    END as download_success,
    30 + (cr.id + s.id) % 300 as access_duration,  -- 30-330秒
    1 as status,
    0 as deleted
FROM tb_course_resource cr
JOIN tb_course_selection cs ON cs.course_id = cr.course_id AND cs.selection_status = 'selected'
JOIN tb_student s ON s.id = cs.student_id
WHERE cr.deleted = 0 AND s.deleted = 0 AND cs.deleted = 0
LIMIT 15000;

SELECT '✓ 资源访问日志数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 10. 通知表 (tb_notification)
-- =====================================================

SELECT '步骤10: 生成通知数据...' as '状态', NOW() as '时间';

INSERT INTO tb_notification (title, content, notification_type, sender_id, target_type, target_ids,
                            send_time, read_count, total_count, priority, expire_time,
                            status, deleted)
VALUES
('新学期选课通知', '各位同学，新学期选课系统已开放，请及时登录系统进行选课。', 'system', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 7 DAY), 8500, 10000, 'high', DATE_ADD(NOW(), INTERVAL 30 DAY), 1, 0),
('期末考试安排通知', '期末考试时间已确定，请各位同学查看考试安排，做好复习准备。', 'academic', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 5 DAY), 9200, 10000, 'high', DATE_ADD(NOW(), INTERVAL 15 DAY), 1, 0),
('图书馆开放时间调整', '图书馆开放时间调整为8:00-22:00，请同学们注意。', 'general', 1, 'all_users', NULL,
 DATE_SUB(NOW(), INTERVAL 3 DAY), 12000, 15000, 'medium', DATE_ADD(NOW(), INTERVAL 60 DAY), 1, 0),
('校园网络维护通知', '本周六晚上22:00-24:00进行网络维护，期间可能影响网络使用。', 'maintenance', 1, 'all_users', NULL,
 DATE_SUB(NOW(), INTERVAL 2 DAY), 11500, 15000, 'medium', DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 0),
('学费缴费提醒', '请各位同学及时缴纳本学期学费，缴费截止日期为本月底。', 'financial', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 10 DAY), 7800, 10000, 'high', DATE_ADD(NOW(), INTERVAL 20 DAY), 1, 0),
('课程调停课通知', '由于教师出差，部分课程时间有调整，请查看最新课表。', 'academic', 1, 'specific_students', '1,2,3,4,5',
 DATE_SUB(NOW(), INTERVAL 1 DAY), 150, 200, 'high', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 0),
('宿舍安全检查通知', '本周将进行宿舍安全检查，请同学们配合工作。', 'dormitory', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 4 DAY), 8900, 10000, 'medium', DATE_ADD(NOW(), INTERVAL 10 DAY), 1, 0),
('奖学金申请通知', '本学期奖学金申请已开始，符合条件的同学请及时申请。', 'scholarship', 1, 'all_students', NULL,
 DATE_SUB(NOW(), INTERVAL 15 DAY), 6500, 10000, 'medium', DATE_ADD(NOW(), INTERVAL 45 DAY), 1, 0);

SELECT '✓ 通知数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 11. 通知模板表 (tb_notification_template)
-- =====================================================

SELECT '步骤11: 生成通知模板数据...' as '状态', NOW() as '时间';

INSERT INTO tb_notification_template (template_name, template_type, title_template, content_template,
                                     variables, default_priority, default_expire_days,
                                     description, status, deleted)
VALUES
('选课提醒模板', 'academic', '选课提醒 - {course_name}', '亲爱的{student_name}同学，您好！课程{course_name}的选课即将截止，截止时间为{deadline}，请及时完成选课。',
 'student_name,course_name,deadline', 'medium', 7, '用于选课截止前的提醒通知', 1, 0),
('成绩发布模板', 'academic', '成绩发布通知 - {course_name}', '{student_name}同学，您的{course_name}课程成绩已发布，总分：{total_score}，等级：{grade_level}。',
 'student_name,course_name,total_score,grade_level', 'high', 30, '用于成绩发布后的通知', 1, 0),
('作业提醒模板', 'assignment', '作业提醒 - {assignment_title}', '{student_name}同学，您有一项作业{assignment_title}即将到期，截止时间：{due_date}，请及时提交。',
 'student_name,assignment_title,due_date', 'medium', 3, '用于作业截止前的提醒', 1, 0),
('考试通知模板', 'exam', '考试通知 - {exam_name}', '{student_name}同学，{exam_name}将于{exam_date} {exam_time}在{classroom}举行，请准时参加。',
 'student_name,exam_name,exam_date,exam_time,classroom', 'high', 7, '用于考试前的通知', 1, 0),
('缴费提醒模板', 'financial', '缴费提醒 - {fee_item}', '{student_name}同学，您的{fee_item}费用{amount}元尚未缴纳，请于{deadline}前完成缴费。',
 'student_name,fee_item,amount,deadline', 'high', 15, '用于费用缴纳提醒', 1, 0);

SELECT '✓ 通知模板数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 12. 消息表 (tb_message)
-- =====================================================

SELECT '步骤12: 生成消息数据...' as '状态', NOW() as '时间';

INSERT INTO tb_message (sender_id, receiver_id, message_type, title, content, send_time,
                       read_time, is_read, reply_to_id, attachment_path, priority,
                       status, deleted)
SELECT
    CASE
        WHEN n.num % 3 = 0 THEN t.id  -- 教师发送
        WHEN n.num % 3 = 1 THEN s.user_id  -- 学生发送
        ELSE 1  -- 管理员发送
    END as sender_id,
    CASE
        WHEN n.num % 3 = 0 THEN s.user_id  -- 发给学生
        WHEN n.num % 3 = 1 THEN t.id  -- 发给教师
        ELSE s.user_id  -- 发给学生
    END as receiver_id,
    CASE
        WHEN n.num % 4 = 0 THEN 'academic'
        WHEN n.num % 4 = 1 THEN 'personal'
        WHEN n.num % 4 = 2 THEN 'system'
        ELSE 'notification'
    END as message_type,
    CASE
        WHEN n.num % 4 = 0 THEN CONCAT('关于', c.course_name, '课程的问题')
        WHEN n.num % 4 = 1 THEN '个人学习咨询'
        WHEN n.num % 4 = 2 THEN '系统使用问题'
        ELSE '重要通知'
    END as title,
    CASE
        WHEN n.num % 4 = 0 THEN CONCAT('老师您好，我对', c.course_name, '课程的某个知识点有疑问，希望得到您的指导。')
        WHEN n.num % 4 = 1 THEN '老师您好，我想咨询一下关于专业学习方向的建议。'
        WHEN n.num % 4 = 2 THEN '您好，我在使用系统时遇到了一些问题，希望能得到帮助。'
        ELSE '这是一条重要通知，请及时查看。'
    END as content,
    DATE_SUB(NOW(), INTERVAL (n.num % 30) DAY) as send_time,
    CASE
        WHEN n.num % 5 = 0 THEN NULL  -- 未读
        ELSE DATE_SUB(NOW(), INTERVAL (n.num % 25) DAY)  -- 已读
    END as read_time,
    CASE WHEN n.num % 5 = 0 THEN 0 ELSE 1 END as is_read,
    NULL as reply_to_id,
    CASE
        WHEN n.num % 10 = 0 THEN CONCAT('/attachments/msg_', n.num, '.pdf')
        ELSE NULL
    END as attachment_path,
    CASE
        WHEN n.num % 3 = 0 THEN 'high'
        WHEN n.num % 3 = 1 THEN 'medium'
        ELSE 'low'
    END as priority,
    1 as status,
    0 as deleted
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30 UNION
    SELECT 31 UNION SELECT 32 UNION SELECT 33 UNION SELECT 34 UNION SELECT 35 UNION
    SELECT 36 UNION SELECT 37 UNION SELECT 38 UNION SELECT 39 UNION SELECT 40 UNION
    SELECT 41 UNION SELECT 42 UNION SELECT 43 UNION SELECT 44 UNION SELECT 45 UNION
    SELECT 46 UNION SELECT 47 UNION SELECT 48 UNION SELECT 49 UNION SELECT 50
) n
CROSS JOIN (SELECT id, user_id FROM tb_student WHERE deleted = 0 LIMIT 100) s
CROSS JOIN (SELECT id FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0 LIMIT 10) t
CROSS JOIN (SELECT id, course_name FROM tb_course WHERE deleted = 0 LIMIT 10) c
LIMIT 5000;

SELECT '✓ 消息数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 13. 学生评价表 (tb_student_evaluation)
-- =====================================================

SELECT '步骤13: 生成学生评价数据...' as '状态', NOW() as '时间';

INSERT INTO tb_student_evaluation (student_id, course_id, teacher_id, evaluation_type,
                                  teaching_quality, course_content, teaching_method,
                                  homework_load, overall_rating, comments, evaluation_time,
                                  is_anonymous, status, deleted)
SELECT
    s.id as student_id,
    cs.course_id,
    sch.teacher_id,
    'course_evaluation' as evaluation_type,
    4 + (s.id + cs.course_id) % 2 as teaching_quality,  -- 4-5分
    4 + (s.id + cs.course_id + 1) % 2 as course_content,
    3 + (s.id + cs.course_id + 2) % 3 as teaching_method,  -- 3-5分
    3 + (s.id + cs.course_id + 3) % 3 as homework_load,
    4 + (s.id + cs.course_id + 4) % 2 as overall_rating,
    CASE
        WHEN (s.id + cs.course_id) % 4 = 0 THEN '老师讲课很好，内容丰富，受益匪浅。'
        WHEN (s.id + cs.course_id) % 4 = 1 THEN '课程内容实用，教学方法新颖，很喜欢这门课。'
        WHEN (s.id + cs.course_id) % 4 = 2 THEN '老师认真负责，课堂氛围活跃，学到了很多知识。'
        ELSE '总体来说还不错，希望能增加更多实践环节。'
    END as comments,
    DATE_SUB(NOW(), INTERVAL (s.id % 60) DAY) as evaluation_time,
    CASE WHEN (s.id + cs.course_id) % 3 = 0 THEN 1 ELSE 0 END as is_anonymous,
    1 as status,
    0 as deleted
FROM tb_student s
JOIN tb_course_selection cs ON cs.student_id = s.id AND cs.selection_status = 'selected'
LEFT JOIN tb_course_schedule sch ON sch.course_id = cs.course_id
WHERE s.deleted = 0 AND cs.deleted = 0 AND sch.teacher_id IS NOT NULL
LIMIT 8000;

SELECT '✓ 学生评价数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 14. 活动日志表 (tb_activity_log)
-- =====================================================

SELECT '步骤14: 生成活动日志数据...' as '状态', NOW() as '时间';

INSERT INTO tb_activity_log (user_id, activity_type, activity_description, ip_address,
                            user_agent, activity_time, result, error_message,
                            request_url, request_method, status, deleted)
SELECT
    u.id as user_id,
    CASE
        WHEN n.num % 8 = 0 THEN 'login'
        WHEN n.num % 8 = 1 THEN 'logout'
        WHEN n.num % 8 = 2 THEN 'course_selection'
        WHEN n.num % 8 = 3 THEN 'grade_query'
        WHEN n.num % 8 = 4 THEN 'assignment_submit'
        WHEN n.num % 8 = 5 THEN 'resource_download'
        WHEN n.num % 8 = 6 THEN 'profile_update'
        ELSE 'system_access'
    END as activity_type,
    CASE
        WHEN n.num % 8 = 0 THEN '用户登录系统'
        WHEN n.num % 8 = 1 THEN '用户退出系统'
        WHEN n.num % 8 = 2 THEN '用户进行选课操作'
        WHEN n.num % 8 = 3 THEN '用户查询成绩'
        WHEN n.num % 8 = 4 THEN '用户提交作业'
        WHEN n.num % 8 = 5 THEN '用户下载课程资源'
        WHEN n.num % 8 = 6 THEN '用户更新个人信息'
        ELSE '用户访问系统功能'
    END as activity_description,
    CONCAT('192.168.', 1 + (u.id + n.num) % 254, '.', 1 + (u.id * n.num) % 254) as ip_address,
    CASE
        WHEN n.num % 4 = 0 THEN 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        WHEN n.num % 4 = 1 THEN 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36'
        WHEN n.num % 4 = 2 THEN 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36'
        ELSE 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15'
    END as user_agent,
    DATE_SUB(NOW(), INTERVAL (n.num % 30) DAY) as activity_time,
    CASE WHEN n.num % 20 = 0 THEN 'failure' ELSE 'success' END as result,
    CASE
        WHEN n.num % 20 = 0 THEN '操作失败：权限不足或系统错误'
        ELSE NULL
    END as error_message,
    CASE
        WHEN n.num % 8 = 0 THEN '/api/auth/login'
        WHEN n.num % 8 = 1 THEN '/api/auth/logout'
        WHEN n.num % 8 = 2 THEN '/api/course/select'
        WHEN n.num % 8 = 3 THEN '/api/grade/query'
        WHEN n.num % 8 = 4 THEN '/api/assignment/submit'
        WHEN n.num % 8 = 5 THEN '/api/resource/download'
        WHEN n.num % 8 = 6 THEN '/api/user/profile'
        ELSE '/api/system/access'
    END as request_url,
    CASE
        WHEN n.num % 8 IN (0, 1, 3) THEN 'GET'
        ELSE 'POST'
    END as request_method,
    1 as status,
    0 as deleted
FROM (
    SELECT 1 as num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION
    SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14 UNION SELECT 15 UNION
    SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION
    SELECT 21 UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION
    SELECT 26 UNION SELECT 27 UNION SELECT 28 UNION SELECT 29 UNION SELECT 30
) n
CROSS JOIN (SELECT id FROM tb_user WHERE deleted = 0 LIMIT 500) u
LIMIT 10000;

SELECT '✓ 活动日志数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 15. 系统配置表 (tb_system_config)
-- =====================================================

SELECT '步骤15: 生成系统配置数据...' as '状态', NOW() as '时间';

INSERT INTO tb_system_config (config_key, config_value, config_type, description,
                             is_editable, category, sort_order, status, deleted)
VALUES
('system.name', 'Smart Campus Management System', 'string', '系统名称', 1, 'basic', 1, 1, 0),
('system.version', '2.0.0', 'string', '系统版本', 0, 'basic', 2, 1, 0),
('system.logo', '/images/logo.png', 'string', '系统Logo路径', 1, 'basic', 3, 1, 0),
('course.max_selection', '8', 'integer', '学生最大选课数量', 1, 'academic', 10, 1, 0),
('course.min_selection', '3', 'integer', '学生最小选课数量', 1, 'academic', 11, 1, 0),
('grade.pass_score', '60', 'decimal', '及格分数线', 1, 'academic', 20, 1, 0),
('attendance.late_minutes', '10', 'integer', '迟到判定分钟数', 1, 'attendance', 30, 1, 0),
('file.max_upload_size', '50', 'integer', '文件最大上传大小(MB)', 1, 'system', 40, 1, 0),
('notification.auto_delete_days', '90', 'integer', '通知自动删除天数', 1, 'system', 41, 1, 0),
('backup.auto_backup', 'true', 'boolean', '是否自动备份', 1, 'system', 50, 1, 0),
('security.session_timeout', '30', 'integer', '会话超时时间(分钟)', 1, 'security', 60, 1, 0),
('security.password_min_length', '6', 'integer', '密码最小长度', 1, 'security', 61, 1, 0),
('email.smtp_host', 'smtp.university.edu.cn', 'string', 'SMTP服务器地址', 1, 'email', 70, 1, 0),
('email.smtp_port', '587', 'integer', 'SMTP端口', 1, 'email', 71, 1, 0),
('maintenance.mode', 'false', 'boolean', '维护模式', 1, 'system', 80, 1, 0);

SELECT '✓ 系统配置数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 16. 系统设置表 (tb_system_settings)
-- =====================================================

SELECT '步骤16: 生成系统设置数据...' as '状态', NOW() as '时间';

INSERT INTO tb_system_settings (setting_name, setting_value, setting_type, description,
                               default_value, is_public, category, status, deleted)
VALUES
('theme_color', '#1890ff', 'color', '系统主题色', '#1890ff', 1, 'appearance', 1, 0),
('page_size', '20', 'number', '分页大小', '20', 1, 'display', 1, 0),
('language', 'zh-CN', 'select', '系统语言', 'zh-CN', 1, 'localization', 1, 0),
('timezone', 'Asia/Shanghai', 'select', '时区设置', 'Asia/Shanghai', 1, 'localization', 1, 0),
('date_format', 'YYYY-MM-DD', 'string', '日期格式', 'YYYY-MM-DD', 1, 'display', 1, 0),
('time_format', 'HH:mm:ss', 'string', '时间格式', 'HH:mm:ss', 1, 'display', 1, 0),
('enable_notifications', 'true', 'boolean', '启用通知', 'true', 1, 'notification', 1, 0),
('auto_save_interval', '300', 'number', '自动保存间隔(秒)', '300', 1, 'editor', 1, 0),
('max_login_attempts', '5', 'number', '最大登录尝试次数', '5', 0, 'security', 1, 0),
('session_warning_time', '5', 'number', '会话警告时间(分钟)', '5', 1, 'security', 1, 0);

SELECT '✓ 系统设置数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 17. 恢复数据库设置并提交事务
-- =====================================================

SELECT '步骤17: 恢复数据库设置...' as '状态', NOW() as '时间';

-- 恢复数据库设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

-- 提交事务
COMMIT;

SELECT '✓ 数据库设置恢复完成' as '状态', NOW() as '时间';

-- =====================================================
-- 18. 最终数据统计
-- =====================================================

SELECT '=== 补充数据生成统计报告 ===' as '状态', NOW() as '时间';

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
    AND table_name NOT LIKE 'tb_schedule'  -- 排除未使用的表
ORDER BY table_rows DESC;

SELECT '=== Smart Campus Management System 补充缺失数据完成 ===' as '状态', NOW() as '时间';
SELECT '🎉 所有表的测试数据已生成完毕！' as '提示信息';
