-- =====================================================
-- Smart Campus Management System - 最终统计报告
-- 文件: 14_final_statistics.sql
-- 描述: 生成完整的数据统计报告
-- 版本: 1.0.0
-- 创建时间: 2025-01-27
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

SELECT '=== Smart Campus Management System 最终统计报告 ===' as '报告类型', NOW() as '生成时间';

-- =====================================================
-- 1. 所有表的数据统计
-- =====================================================

SELECT '=== 所有表数据统计 ===' as '统计类型', NOW() as '时间';

SELECT 
    '用户管理' as '模块',
    'tb_user' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_user WHERE deleted = 0

UNION ALL

SELECT 
    '用户管理' as '模块',
    'tb_role' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_role WHERE deleted = 0

UNION ALL

SELECT 
    '用户管理' as '模块',
    'tb_permission' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_permission WHERE deleted = 0

UNION ALL

SELECT 
    '用户管理' as '模块',
    'tb_user_role' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_user_role WHERE deleted = 0

UNION ALL

SELECT 
    '用户管理' as '模块',
    'tb_role_permission' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_role_permission WHERE deleted = 0

UNION ALL

SELECT 
    '学院管理' as '模块',
    'tb_department' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_department WHERE deleted = 0

UNION ALL

SELECT 
    '班级管理' as '模块',
    'tb_class' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_class WHERE deleted = 0

UNION ALL

SELECT 
    '学生管理' as '模块',
    'tb_student' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_student WHERE deleted = 0

UNION ALL

SELECT 
    '家长管理' as '模块',
    'tb_parent_student_relation' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_parent_student_relation WHERE deleted = 0

UNION ALL

SELECT 
    '课程管理' as '模块',
    'tb_course' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_course WHERE deleted = 0

UNION ALL

SELECT 
    '课程管理' as '模块',
    'tb_course_schedule' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_course_schedule WHERE deleted = 0

UNION ALL

SELECT 
    '选课管理' as '模块',
    'tb_course_selection' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_course_selection WHERE deleted = 0

UNION ALL

SELECT 
    '成绩管理' as '模块',
    'tb_grade' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_grade WHERE deleted = 0

UNION ALL

SELECT 
    '考试管理' as '模块',
    'tb_exam' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_exam WHERE deleted = 0

UNION ALL

SELECT 
    '作业管理' as '模块',
    'tb_assignment' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_assignment WHERE deleted = 0

UNION ALL

SELECT 
    '考勤管理' as '模块',
    'tb_attendance' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_attendance WHERE deleted = 0

UNION ALL

SELECT 
    '财务管理' as '模块',
    'tb_payment_record' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_payment_record WHERE deleted = 0

UNION ALL

SELECT 
    '财务管理' as '模块',
    'tb_fee_item' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_fee_item WHERE deleted = 0

UNION ALL

SELECT 
    '通知管理' as '模块',
    'tb_notification' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_notification WHERE deleted = 0

UNION ALL

SELECT 
    '系统管理' as '模块',
    'tb_system_config' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_system_config WHERE deleted = 0

UNION ALL

SELECT 
    '基础设施' as '模块',
    'tb_classroom' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_classroom WHERE deleted = 0

UNION ALL

SELECT 
    '基础设施' as '模块',
    'tb_time_slot' as '表名',
    COUNT(*) as '记录数',
    CASE WHEN COUNT(*) > 0 THEN '✓ 有数据' ELSE '✗ 无数据' END as '状态'
FROM tb_time_slot WHERE deleted = 0

ORDER BY 模块, 记录数 DESC;

-- =====================================================
-- 2. 核心业务数据统计
-- =====================================================

SELECT '=== 核心业务数据统计 ===' as '统计类型', NOW() as '时间';

SELECT 
    '用户总数' as '统计项目',
    COUNT(*) as '数量',
    CONCAT(
        '管理员: ', SUM(CASE WHEN u.username LIKE 'admin%' THEN 1 ELSE 0 END), ', ',
        '教师: ', SUM(CASE WHEN u.username LIKE 'teacher%' THEN 1 ELSE 0 END), ', ',
        '班主任: ', SUM(CASE WHEN u.username LIKE 'classteacher%' THEN 1 ELSE 0 END), ', ',
        '学生: ', SUM(CASE WHEN u.username LIKE 'student%' THEN 1 ELSE 0 END), ', ',
        '家长: ', SUM(CASE WHEN u.username LIKE 'parent%' THEN 1 ELSE 0 END)
    ) as '详细分布'
FROM tb_user u WHERE u.deleted = 0

UNION ALL

SELECT 
    '学院班级统计' as '统计项目',
    COUNT(DISTINCT d.id) as '数量',
    CONCAT('学院数: ', COUNT(DISTINCT d.id), ', 班级数: ', COUNT(DISTINCT c.id)) as '详细分布'
FROM tb_department d
LEFT JOIN tb_class c ON d.id = c.department_id AND c.deleted = 0
WHERE d.deleted = 0

UNION ALL

SELECT 
    '课程教学统计' as '统计项目',
    COUNT(DISTINCT co.id) as '数量',
    CONCAT('课程数: ', COUNT(DISTINCT co.id), ', 课程安排: ', COUNT(DISTINCT cs.id)) as '详细分布'
FROM tb_course co
LEFT JOIN tb_course_schedule cs ON co.id = cs.course_id AND cs.deleted = 0
WHERE co.deleted = 0

UNION ALL

SELECT 
    '选课成绩统计' as '统计项目',
    COUNT(DISTINCT sel.id) as '数量',
    CONCAT('选课记录: ', COUNT(DISTINCT sel.id), ', 成绩记录: ', COUNT(DISTINCT g.id)) as '详细分布'
FROM tb_course_selection sel
LEFT JOIN tb_grade g ON sel.student_id = g.student_id AND sel.course_id = g.course_id AND g.deleted = 0
WHERE sel.deleted = 0

UNION ALL

SELECT 
    '考试作业统计' as '统计项目',
    COUNT(DISTINCT e.id) as '数量',
    CONCAT('考试数: ', COUNT(DISTINCT e.id), ', 作业数: ', COUNT(DISTINCT a.id)) as '详细分布'
FROM tb_exam e
CROSS JOIN tb_assignment a
WHERE e.deleted = 0 AND a.deleted = 0

UNION ALL

SELECT 
    '考勤财务统计' as '统计项目',
    COUNT(DISTINCT att.id) as '数量',
    CONCAT('考勤记录: ', COUNT(DISTINCT att.id), ', 缴费记录: ', COUNT(DISTINCT pay.id)) as '详细分布'
FROM tb_attendance att
CROSS JOIN tb_payment_record pay
WHERE att.deleted = 0 AND pay.deleted = 0;

-- =====================================================
-- 3. 数据完整性检查
-- =====================================================

SELECT '=== 数据完整性检查 ===' as '统计类型', NOW() as '时间';

SELECT 
    '外键完整性检查' as '检查类型',
    '学生用户关联' as '检查项目',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) as '异常记录数',
    CASE WHEN SUM(CASE WHEN u.id IS NULL THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM tb_student s
LEFT JOIN tb_user u ON s.user_id = u.id
WHERE s.deleted = 0

UNION ALL

SELECT 
    '数据一致性检查' as '检查类型',
    '班级学生数一致性' as '检查项目',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN c.student_count != actual_count THEN 1 ELSE 0 END) as '异常记录数',
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
    '业务逻辑检查' as '检查类型',
    '成绩及格判定' as '检查项目',
    COUNT(*) as '总记录数',
    SUM(CASE WHEN is_passed = TRUE AND total_score < 60 THEN 1 ELSE 0 END) as '异常记录数',
    CASE WHEN SUM(CASE WHEN is_passed = TRUE AND total_score < 60 THEN 1 ELSE 0 END) = 0 THEN '✓ 通过' ELSE '✗ 失败' END as '检查结果'
FROM tb_grade WHERE deleted = 0;

-- =====================================================
-- 4. 系统状态总结
-- =====================================================

SELECT '=== 系统状态总结 ===' as '统计类型', NOW() as '时间';

SELECT 
    'Smart Campus Management System' as '系统名称',
    '2.0.0 (优化版)' as '版本',
    CONCAT(
        (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), ' 用户, ',
        (SELECT COUNT(*) FROM tb_student WHERE deleted = 0), ' 学生, ',
        (SELECT COUNT(*) FROM tb_course WHERE deleted = 0), ' 课程, ',
        (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0), ' 成绩记录'
    ) as '数据规模',
    '✅ 系统就绪' as '状态',
    NOW() as '统计时间';

SELECT '=== Smart Campus Management System 最终统计报告完成 ===' as '状态', NOW() as '时间';
SELECT '🎉 数据库初始化和数据生成全部完成！系统已准备就绪。' as '提示信息';
