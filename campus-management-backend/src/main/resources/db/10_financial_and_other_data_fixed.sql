-- =====================================================
-- Smart Campus Management System - 财务和其他数据生成脚本 (修复版)
-- 文件: 10_financial_and_other_data_fixed.sql
-- 描述: 生成财务、作业、考勤、通知等数据
-- 版本: 3.0.0 (修复版)
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

SELECT '=== Smart Campus Management System 财务和其他数据生成开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 创建临时表
-- =====================================================

-- 创建序列号临时表
DROP TEMPORARY TABLE IF EXISTS temp_numbers;
CREATE TEMPORARY TABLE temp_numbers (
    num INT PRIMARY KEY
);

-- 生成1到20000的序列号
INSERT INTO temp_numbers (num)
SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 + 1 as number_val
FROM
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) d,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) e
WHERE (a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 + 1) <= 20000;

SELECT '✓ 临时表创建完成' as '状态', NOW() as '时间';

-- =====================================================
-- 2. 生成学生费用数据
-- =====================================================

SELECT '步骤1: 生成学生费用数据...' as '状态', NOW() as '时间';

-- 为每个学生生成费用记录
INSERT INTO tb_student_fee (student_id, fee_item_id, amount, due_date, payment_status, academic_year, semester, status, deleted)
SELECT
    ((n.num - 1) % 10000) + 1 as student_id,
    ((n.num - 1) % 20) + 1 as fee_item_id,
    CASE 
        WHEN ((n.num - 1) % 20) + 1 <= 10 THEN 5000.00  -- 学费
        WHEN ((n.num - 1) % 20) + 1 <= 15 THEN 1200.00  -- 住宿费
        ELSE 300.00  -- 其他费用
    END as amount,
    DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 90) DAY) as due_date,
    CASE 
        WHEN n.num % 5 = 0 THEN '未缴费'
        WHEN n.num % 10 = 0 THEN '部分缴费'
        ELSE '已缴费'
    END as payment_status,
    '2024-2025' as academic_year,
    CASE WHEN n.num % 2 = 1 THEN '第一学期' ELSE '第二学期' END as semester,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 15000;

SELECT '✓ 学生费用数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 生成缴费记录数据
-- =====================================================

SELECT '步骤2: 生成缴费记录数据...' as '状态', NOW() as '时间';

-- 为已缴费的学生生成缴费记录
INSERT INTO tb_payment_record (student_id, fee_item_id, amount, payment_method, payment_time, transaction_id, payment_status, operator_id, status, deleted)
SELECT
    sf.student_id,
    sf.fee_item_id,
    sf.amount,
    CASE 
        WHEN n.num % 4 = 1 THEN '银行转账'
        WHEN n.num % 4 = 2 THEN '支付宝'
        WHEN n.num % 4 = 3 THEN '微信支付'
        ELSE '现金'
    END as payment_method,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 180) DAY) as payment_time,
    CONCAT('TXN', LPAD(n.num, 10, '0')) as transaction_id,
    '支付成功' as payment_status,
    ((n.num - 1) % 50) + 2 as operator_id,  -- 管理员用户ID
    1 as status,
    0 as deleted
FROM temp_numbers n
JOIN tb_student_fee sf ON sf.id = ((n.num - 1) % (SELECT COUNT(*) FROM tb_student_fee WHERE payment_status IN ('已缴费', '部分缴费'))) + 1
WHERE n.num <= 12000 AND sf.payment_status IN ('已缴费', '部分缴费');

SELECT '✓ 缴费记录数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 生成作业数据
-- =====================================================

SELECT '步骤3: 生成作业数据...' as '状态', NOW() as '时间';

-- 为每门课程生成3-5个作业
INSERT INTO tb_assignment (course_id, teacher_id, title, description, assignment_type, due_date, total_score, submission_format, status, deleted)
SELECT
    ((n.num - 1) % 1000) + 1 as course_id,
    c.teacher_id,
    CONCAT(c.course_name, '作业', ((n.num - 1) % 5) + 1) as title,
    CONCAT(c.course_name, '作业描述内容') as description,
    CASE 
        WHEN n.num % 4 = 1 THEN '课后作业'
        WHEN n.num % 4 = 2 THEN '实验报告'
        WHEN n.num % 4 = 3 THEN '课程设计'
        ELSE '小组项目'
    END as assignment_type,
    DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 30) DAY) as due_date,
    CASE 
        WHEN n.num % 4 = 3 THEN 100  -- 课程设计
        WHEN n.num % 4 = 0 THEN 80   -- 小组项目
        ELSE 50  -- 其他作业
    END as total_score,
    CASE 
        WHEN n.num % 3 = 1 THEN '在线提交'
        WHEN n.num % 3 = 2 THEN '纸质提交'
        ELSE '文件上传'
    END as submission_format,
    CASE WHEN n.num % 30 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN tb_course c ON c.id = ((n.num - 1) % 1000) + 1
WHERE n.num <= 3000;

SELECT '✓ 作业数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 生成作业提交数据
-- =====================================================

SELECT '步骤4: 生成作业提交数据...' as '状态', NOW() as '时间';

-- 为学生生成作业提交记录
INSERT INTO tb_assignment_submission (assignment_id, student_id, submission_content, submission_time, score, feedback, submission_status, status, deleted)
SELECT
    ((n.num - 1) % 3000) + 1 as assignment_id,
    ((n.num - 1) % 10000) + 1 as student_id,
    CONCAT('作业提交内容', n.num) as submission_content,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as submission_time,
    CASE 
        WHEN n.num % 10 = 0 THEN NULL  -- 未评分
        ELSE FLOOR(60 + RAND() * 40)  -- 60-100分
    END as score,
    CASE 
        WHEN n.num % 10 = 0 THEN NULL
        ELSE CONCAT('作业反馈', n.num)
    END as feedback,
    CASE 
        WHEN n.num % 20 = 0 THEN '未提交'
        WHEN n.num % 15 = 0 THEN '迟交'
        ELSE '已提交'
    END as submission_status,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 20000;

SELECT '✓ 作业提交数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 生成考勤数据
-- =====================================================

SELECT '步骤5: 生成考勤数据...' as '状态', NOW() as '时间';

-- 为学生生成考勤记录
INSERT INTO tb_attendance (student_id, course_id, attendance_date, attendance_status, check_in_time, check_out_time, teacher_id, status, deleted)
SELECT
    ((n.num - 1) % 10000) + 1 as student_id,
    ((n.num - 1) % 1000) + 1 as course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 90) DAY) as attendance_date,
    CASE 
        WHEN n.num % 20 = 0 THEN '缺勤'
        WHEN n.num % 15 = 0 THEN '迟到'
        WHEN n.num % 25 = 0 THEN '早退'
        ELSE '正常'
    END as attendance_status,
    CASE 
        WHEN n.num % 20 = 0 THEN NULL  -- 缺勤
        WHEN n.num % 15 = 0 THEN TIME(DATE_ADD(CONCAT(CURDATE(), ' 08:00:00'), INTERVAL FLOOR(RAND() * 30) MINUTE))  -- 迟到
        ELSE '08:00:00'  -- 正常
    END as check_in_time,
    CASE 
        WHEN n.num % 20 = 0 THEN NULL  -- 缺勤
        WHEN n.num % 25 = 0 THEN TIME(DATE_SUB(CONCAT(CURDATE(), ' 10:00:00'), INTERVAL FLOOR(RAND() * 30) MINUTE))  -- 早退
        ELSE '10:00:00'  -- 正常
    END as check_out_time,
    ((n.num - 1) % 500) + 1 as teacher_id,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 15000;

SELECT '✓ 考勤数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 7. 生成通知数据
-- =====================================================

SELECT '步骤6: 生成通知数据...' as '状态', NOW() as '时间';

-- 生成系统通知
INSERT INTO tb_notification (title, content, notification_type, target_type, target_id, sender_id, send_time, is_read, priority_level, status, deleted)
SELECT
    CASE 
        WHEN n.num % 5 = 1 THEN '课程通知'
        WHEN n.num % 5 = 2 THEN '考试通知'
        WHEN n.num % 5 = 3 THEN '缴费通知'
        WHEN n.num % 5 = 4 THEN '活动通知'
        ELSE '系统通知'
    END as title,
    CONCAT('通知内容', n.num, '，请及时查看相关信息。') as content,
    CASE 
        WHEN n.num % 4 = 1 THEN '系统通知'
        WHEN n.num % 4 = 2 THEN '课程通知'
        WHEN n.num % 4 = 3 THEN '考试通知'
        ELSE '缴费通知'
    END as notification_type,
    CASE 
        WHEN n.num % 3 = 1 THEN '全体学生'
        WHEN n.num % 3 = 2 THEN '指定班级'
        ELSE '指定学生'
    END as target_type,
    CASE 
        WHEN n.num % 3 = 2 THEN ((n.num - 1) % 200) + 1  -- 班级ID
        WHEN n.num % 3 = 0 THEN ((n.num - 1) % 10000) + 1  -- 学生ID
        ELSE NULL
    END as target_id,
    ((n.num - 1) % 50) + 2 as sender_id,  -- 管理员或教师ID
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as send_time,
    CASE WHEN n.num % 3 = 0 THEN 1 ELSE 0 END as is_read,
    CASE 
        WHEN n.num % 4 = 1 THEN '高'
        WHEN n.num % 4 = 2 THEN '中'
        ELSE '低'
    END as priority_level,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 500;

SELECT '✓ 通知数据生成完成' as '状态', NOW() as '时间';

COMMIT;

SELECT '=== 财务和其他数据生成完成 ===' as '状态', NOW() as '时间';
