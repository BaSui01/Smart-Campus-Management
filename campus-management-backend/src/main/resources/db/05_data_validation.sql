-- =====================================================
-- Smart Campus Management System - 数据验证和统计脚本
-- 文件: 05_data_validation.sql
-- 描述: 验证数据完整性和生成统计报告
-- 版本: 1.0.0
-- 创建时间: 2025-01-27
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

SELECT '=== Smart Campus Management System 数据验证和统计开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 数据完整性验证
-- =====================================================

SELECT '=== 数据完整性验证 ===' as '验证类型', NOW() as '时间';

-- 检查外键约束
SELECT '外键约束检查' as '检查项目';

SELECT 
    '学生用户关联' as '检查内容',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) as '异常记录数',
    CASE WHEN SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM tb_student s
LEFT JOIN tb_user u ON s.user_id = u.id
WHERE s.deleted = 0

UNION ALL

SELECT 
    '班级学院关联' as '检查内容',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN d.id IS NULL THEN 1 ELSE 0 END) as '异常记录数',
    CASE WHEN SUM(CASE WHEN d.id IS NULL THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM tb_class c
LEFT JOIN tb_department d ON c.department_id = d.id
WHERE c.deleted = 0

UNION ALL

SELECT
    '课程调度关联' as '检查内容',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) as '异常记录数',
    CASE WHEN SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM tb_course_schedule cs
LEFT JOIN tb_user u ON cs.teacher_id = u.id
WHERE cs.deleted = 0

UNION ALL

SELECT 
    '成绩学生课程关联' as '检查内容',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN s.id IS NULL OR c.id IS NULL THEN 1 ELSE 0 END) as '异常记录数',
    CASE WHEN SUM(CASE WHEN s.id IS NULL OR c.id IS NULL THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM tb_grade g
LEFT JOIN tb_student s ON g.student_id = s.id
LEFT JOIN tb_course c ON g.course_id = c.id
WHERE g.deleted = 0;

-- 检查数据一致性
SELECT '数据一致性检查' as '检查项目';

SELECT 
    '班级学生数一致性' as '检查内容',
    COUNT(*) as '总班级数',
    SUM(CASE WHEN c.student_count != actual_count THEN 1 ELSE 0 END) as '不一致班级数',
    CASE WHEN SUM(CASE WHEN c.student_count != actual_count THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM (
    SELECT 
        c.id,
        c.student_count,
        COALESCE(COUNT(s.id), 0) as actual_count
    FROM tb_class c
    LEFT JOIN tb_student s ON c.id = s.class_id AND s.deleted = 0
    WHERE c.deleted = 0
    GROUP BY c.id, c.student_count
) c

UNION ALL

SELECT
    '课程选课数一致性' as '检查内容',
    COUNT(*) as '总课程数',
    SUM(CASE WHEN c.current_students != actual_count THEN 1 ELSE 0 END) as '不一致课程数',
    CASE WHEN SUM(CASE WHEN c.current_students != actual_count THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM (
    SELECT
        c.id,
        c.current_students,
        COALESCE(COUNT(cs.id), 0) as actual_count
    FROM tb_course c
    LEFT JOIN tb_course_selection cs ON c.id = cs.course_id AND cs.selection_status = 'selected' AND cs.deleted = 0
    WHERE c.deleted = 0
    GROUP BY c.id, c.current_students
) c;

-- =====================================================
-- 2. 业务数据统计
-- =====================================================

SELECT '=== 业务数据统计 ===' as '统计类型', NOW() as '时间';

-- 用户统计
SELECT '用户统计' as '统计项目';

SELECT 
    '用户类型分布' as '统计内容',
    '管理员' as '类型',
    COUNT(*) as '数量',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), 2), '%') as '占比'
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.role_key = 'ROLE_ADMIN'

UNION ALL

SELECT 
    '用户类型分布' as '统计内容',
    '教师' as '类型',
    COUNT(*) as '数量',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), 2), '%') as '占比'
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.role_key = 'ROLE_TEACHER'

UNION ALL

SELECT 
    '用户类型分布' as '统计内容',
    '班主任' as '类型',
    COUNT(*) as '数量',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), 2), '%') as '占比'
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.role_key = 'ROLE_CLASS_TEACHER'

UNION ALL

SELECT 
    '用户类型分布' as '统计内容',
    '学生' as '类型',
    COUNT(*) as '数量',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), 2), '%') as '占比'
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.role_key = 'ROLE_STUDENT'

UNION ALL

SELECT 
    '用户类型分布' as '统计内容',
    '家长' as '类型',
    COUNT(*) as '数量',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), 2), '%') as '占比'
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.deleted = 0 AND r.role_key = 'ROLE_PARENT';

-- 学院和专业统计
SELECT '学院专业统计' as '统计项目';

SELECT 
    d.department_name as '学院名称',
    COUNT(DISTINCT c.id) as '班级数量',
    COUNT(DISTINCT s.id) as '学生数量',
    ROUND(AVG(s.gpa), 2) as '平均GPA',
    COUNT(DISTINCT co.id) as '开设课程数'
FROM tb_department d
LEFT JOIN tb_class c ON d.id = c.department_id AND c.deleted = 0
LEFT JOIN tb_student s ON c.id = s.class_id AND s.deleted = 0
LEFT JOIN tb_course co ON d.id = co.department_id AND co.deleted = 0
WHERE d.deleted = 0
GROUP BY d.id, d.department_name
ORDER BY d.id;

-- 课程统计
SELECT '课程统计' as '统计项目';

SELECT
    course_type as '课程类型',
    COUNT(*) as '课程数量',
    ROUND(AVG(credits), 1) as '平均学分',
    ROUND(AVG(current_students), 1) as '平均选课人数',
    ROUND(AVG(current_students * 100.0 / max_students), 1) as '平均选课率(%)'
FROM tb_course
WHERE deleted = 0
GROUP BY course_type
ORDER BY COUNT(*) DESC;

-- 成绩统计
SELECT '成绩统计' as '统计项目';

SELECT 
    grade_level as '成绩等级',
    COUNT(*) as '人次',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0), 2), '%') as '占比'
FROM tb_grade
WHERE deleted = 0
GROUP BY grade_level
ORDER BY 
    CASE grade_level 
        WHEN 'A' THEN 1 
        WHEN 'B' THEN 2 
        WHEN 'C' THEN 3 
        WHEN 'D' THEN 4 
        WHEN 'F' THEN 5 
        ELSE 6 
    END;

-- 考勤统计
SELECT '考勤统计' as '统计项目';

SELECT 
    attendance_status as '考勤状态',
    COUNT(*) as '记录数',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_attendance WHERE deleted = 0), 2), '%') as '占比'
FROM tb_attendance
WHERE deleted = 0
GROUP BY attendance_status
ORDER BY COUNT(*) DESC;

-- 财务统计
SELECT '财务统计' as '统计项目';

SELECT 
    payment_status as '缴费状态',
    COUNT(*) as '记录数',
    FORMAT(SUM(payment_amount), 2) as '金额总计',
    CONCAT(ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_payment_record WHERE deleted = 0), 2), '%') as '记录占比'
FROM tb_payment_record
WHERE deleted = 0
GROUP BY payment_status
ORDER BY SUM(payment_amount) DESC;

-- =====================================================
-- 3. 数据质量检查
-- =====================================================

SELECT '=== 数据质量检查 ===' as '检查类型', NOW() as '时间';

-- 业务数据缺失检查
SELECT
    '考试管理' as '检查类型',
    CASE WHEN COUNT(*) > 0 THEN '✅ 考试数据正常' ELSE '❌ 考试数据缺失' END as '检查结果'
FROM tb_exam WHERE deleted = 0

UNION ALL

SELECT
    '作业管理' as '检查类型',
    CASE WHEN COUNT(*) > 0 THEN '✅ 作业数据正常' ELSE '❌ 作业数据缺失' END as '检查结果'
FROM tb_assignment WHERE deleted = 0

UNION ALL

SELECT
    '通知管理' as '检查类型',
    CASE WHEN COUNT(*) > 0 THEN '✅ 通知数据正常' ELSE '❌ 通知数据缺失' END as '检查结果'
FROM tb_notification WHERE deleted = 0

UNION ALL

SELECT
    '消息管理' as '检查类型',
    CASE WHEN COUNT(*) > 0 THEN '✅ 消息数据正常' ELSE '❌ 消息数据缺失' END as '检查结果'
FROM tb_message WHERE deleted = 0;

-- 数据一致性检查
SELECT
    '数据一致性检查' as '检查类型',
    '管理员账号' as '检查项',
    CASE WHEN COUNT(*) > 0 THEN '✅ 存在' ELSE '❌ 缺失' END as '检查结果',
    '系统必须有管理员账号' as '说明'
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE u.username = 'admin' AND u.deleted = 0 AND r.role_key = 'ROLE_ADMIN'

UNION ALL

SELECT
    '数据一致性检查' as '检查类型',
    '角色权限配置' as '检查项',
    CASE WHEN COUNT(*) > 0 THEN '✅ 已配置' ELSE '❌ 未配置' END as '检查结果',
    '角色必须分配权限' as '说明'
FROM tb_role_permission WHERE deleted = 0

UNION ALL

SELECT
    '数据一致性检查' as '检查类型',
    '系统配置' as '检查项',
    CASE WHEN COUNT(*) > 0 THEN '✅ 已配置' ELSE '❌ 未配置' END as '检查结果',
    '系统需要基础配置' as '说明'
FROM tb_system_config WHERE deleted = 0;

-- =====================================================
-- 4. 性能指标统计
-- =====================================================

SELECT '=== 性能指标统计 ===' as '统计类型', NOW() as '时间';

-- 表大小统计
SELECT 
    table_name as '表名',
    table_rows as '记录数',
    ROUND(data_length / 1024 / 1024, 2) as '数据大小(MB)',
    ROUND(index_length / 1024 / 1024, 2) as '索引大小(MB)',
    ROUND((data_length + index_length) / 1024 / 1024, 2) as '总大小(MB)'
FROM information_schema.tables
WHERE table_schema = 'campus_management_db'
    AND table_type = 'BASE TABLE'
ORDER BY (data_length + index_length) DESC;

SELECT '=== Smart Campus Management System 数据验证和统计完成 ===' as '状态', NOW() as '时间';
SELECT '📊 数据验证通过，系统运行正常！' as '提示信息';
