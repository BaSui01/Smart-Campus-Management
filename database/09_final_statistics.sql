-- ================================================
-- 校园管理系统最终数据统计脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 显示完整的数据库统计信息
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 基础数据统计
-- ================================================
SELECT '=== 校园管理系统数据库统计报告 ===' AS title;
SELECT NOW() AS report_time;

-- 表记录数统计
SELECT '=== 数据表记录统计 ===' AS section;
SELECT 'tb_user' AS table_name, COUNT(*) AS record_count, '用户表' AS description FROM tb_user
UNION ALL
SELECT 'tb_role', COUNT(*), '角色表' FROM tb_role
UNION ALL
SELECT 'tb_permission', COUNT(*), '权限表' FROM tb_permission
UNION ALL
SELECT 'tb_user_role', COUNT(*), '用户角色关联表' FROM tb_user_role
UNION ALL
SELECT 'tb_role_permission', COUNT(*), '角色权限关联表' FROM tb_role_permission
UNION ALL
SELECT 'tb_class', COUNT(*), '班级表' FROM tb_class
UNION ALL
SELECT 'tb_student', COUNT(*), '学生表' FROM tb_student
UNION ALL
SELECT 'tb_course', COUNT(*), '课程表' FROM tb_course
UNION ALL
SELECT 'tb_course_schedule', COUNT(*), '课程表排课表' FROM tb_course_schedule
UNION ALL
SELECT 'tb_course_selection', COUNT(*), '选课表' FROM tb_course_selection
UNION ALL
SELECT 'tb_grade', COUNT(*), '成绩表' FROM tb_grade
UNION ALL
SELECT 'tb_fee_item', COUNT(*), '缴费项目表' FROM tb_fee_item
UNION ALL
SELECT 'tb_payment_record', COUNT(*), '缴费记录表' FROM tb_payment_record;

-- ================================================
-- 2. 用户角色统计
-- ================================================
SELECT '=== 用户角色分布 ===' AS section;
SELECT r.role_name AS role_name, COUNT(ur.user_id) as user_count
FROM tb_role r
LEFT JOIN tb_user_role ur ON r.id = ur.role_id
LEFT JOIN tb_user u ON ur.user_id = u.id AND u.status = 1
GROUP BY r.id, r.role_name
ORDER BY user_count DESC;

-- ================================================
-- 3. 学生年级和专业统计
-- ================================================
SELECT '=== 学生年级分布 ===' AS section;
SELECT grade, COUNT(*) as student_count 
FROM tb_student 
WHERE status = 1 
GROUP BY grade 
ORDER BY grade;

SELECT '=== 专业班级分布 ===' AS section;
SELECT 
    CASE 
        WHEN class_code LIKE 'CS%' THEN '计算机科学与技术'
        WHEN class_code LIKE 'SE%' THEN '软件工程'
        WHEN class_code LIKE 'IS%' THEN '信息安全'
        WHEN class_code LIKE 'DS%' THEN '数据科学与大数据技术'
        WHEN class_code LIKE 'AI%' THEN '人工智能'
        WHEN class_code LIKE 'IOT%' THEN '物联网工程'
        WHEN class_code LIKE 'NE%' THEN '网络工程'
        ELSE '其他'
    END AS major,
    COUNT(*) as class_count,
    SUM(student_count) as total_students
FROM tb_class 
GROUP BY 
    CASE 
        WHEN class_code LIKE 'CS%' THEN '计算机科学与技术'
        WHEN class_code LIKE 'SE%' THEN '软件工程'
        WHEN class_code LIKE 'IS%' THEN '信息安全'
        WHEN class_code LIKE 'DS%' THEN '数据科学与大数据技术'
        WHEN class_code LIKE 'AI%' THEN '人工智能'
        WHEN class_code LIKE 'IOT%' THEN '物联网工程'
        WHEN class_code LIKE 'NE%' THEN '网络工程'
        ELSE '其他'
    END
ORDER BY total_students DESC;

-- ================================================
-- 4. 课程统计
-- ================================================
SELECT '=== 课程类型分布 ===' AS section;
SELECT course_type, COUNT(*) as course_count,
       ROUND(AVG(credits), 1) as avg_credits,
       SUM(enrolled_students) as total_enrolled
FROM tb_course 
WHERE status = 1 
GROUP BY course_type
ORDER BY course_count DESC;

SELECT '=== 课程选课热度TOP10 ===' AS section;
SELECT c.course_name, c.course_code, c.course_type,
       c.enrolled_students, c.max_students,
       ROUND(c.enrolled_students * 100.0 / c.max_students, 1) as enrollment_rate
FROM tb_course c
WHERE c.status = 1 AND c.max_students > 0
ORDER BY enrollment_rate DESC
LIMIT 10;

-- ================================================
-- 5. 选课统计
-- ================================================
SELECT '=== 选课统计概览 ===' AS section;
SELECT 
    COUNT(*) as total_selections,
    COUNT(DISTINCT student_id) as students_with_selections,
    COUNT(DISTINCT course_id) as courses_with_selections,
    ROUND(AVG(selections_per_student), 1) as avg_selections_per_student
FROM (
    SELECT student_id, COUNT(*) as selections_per_student
    FROM tb_course_selection
    WHERE status = 1
    GROUP BY student_id
) t;

-- ================================================
-- 6. 成绩统计
-- ================================================
SELECT '=== 成绩等级分布 ===' AS section;
SELECT level, COUNT(*) as grade_count,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_grade WHERE is_makeup = 0), 2) as percentage
FROM tb_grade 
WHERE is_makeup = 0
GROUP BY level 
ORDER BY FIELD(level, 'A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D', 'F');

SELECT '=== 学期平均成绩 ===' AS section;
SELECT semester, COUNT(*) as grade_count,
       ROUND(AVG(score), 2) as avg_score,
       ROUND(MIN(score), 2) as min_score,
       ROUND(MAX(score), 2) as max_score
FROM tb_grade 
WHERE is_makeup = 0 AND score IS NOT NULL
GROUP BY semester 
ORDER BY semester;

-- ================================================
-- 7. 缴费统计
-- ================================================
SELECT '=== 缴费统计概览 ===' AS section;
SELECT 
    COUNT(*) as total_payments,
    COUNT(DISTINCT student_id) as students_with_payments,
    ROUND(SUM(amount), 2) as total_amount,
    ROUND(AVG(amount), 2) as avg_amount
FROM tb_payment_record 
WHERE status = 1;

SELECT '=== 缴费方式分布 ===' AS section;
SELECT payment_method, COUNT(*) as payment_count,
       ROUND(SUM(amount), 2) as total_amount,
       ROUND(AVG(amount), 2) as avg_amount
FROM tb_payment_record 
WHERE status = 1
GROUP BY payment_method 
ORDER BY total_amount DESC;

SELECT '=== 缴费项目统计 ===' AS section;
SELECT f.fee_type, COUNT(pr.id) as payment_count,
       ROUND(SUM(pr.amount), 2) as total_amount
FROM tb_fee_item f
LEFT JOIN tb_payment_record pr ON f.id = pr.fee_item_id AND pr.status = 1
GROUP BY f.fee_type
ORDER BY total_amount DESC;

-- ================================================
-- 8. 数据库大小统计
-- ================================================
SELECT '=== 数据库存储统计 ===' AS section;
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb,
    table_rows,
    ROUND((data_length / 1024 / 1024), 2) AS data_mb,
    ROUND((index_length / 1024 / 1024), 2) AS index_mb
FROM information_schema.tables 
WHERE table_schema = 'campus_management_db'
ORDER BY (data_length + index_length) DESC;

-- ================================================
-- 9. 数据完整性检查
-- ================================================
SELECT '=== 数据完整性检查 ===' AS section;

-- 检查孤立记录
SELECT '学生表中无对应用户的记录' as check_item, COUNT(*) as count
FROM tb_student s 
LEFT JOIN tb_user u ON s.user_id = u.id 
WHERE u.id IS NULL

UNION ALL

SELECT '选课表中无对应学生的记录', COUNT(*)
FROM tb_course_selection cs 
LEFT JOIN tb_student s ON cs.student_id = s.id 
WHERE s.id IS NULL

UNION ALL

SELECT '成绩表中无对应选课的记录', COUNT(*)
FROM tb_grade g 
LEFT JOIN tb_course_selection cs ON g.selection_id = cs.id 
WHERE cs.id IS NULL AND g.selection_id IS NOT NULL

UNION ALL

SELECT '缴费记录中无对应学生的记录', COUNT(*)
FROM tb_payment_record pr 
LEFT JOIN tb_student s ON pr.student_id = s.id 
WHERE s.id IS NULL;

-- ================================================
-- 10. 总结报告
-- ================================================
SELECT '=== 系统总结报告 ===' AS section;
SELECT 
    CONCAT('数据库包含 ', 
           (SELECT COUNT(*) FROM tb_user), ' 个用户，',
           (SELECT COUNT(*) FROM tb_student WHERE status = 1), ' 名在校学生，',
           (SELECT COUNT(*) FROM tb_class), ' 个班级，',
           (SELECT COUNT(*) FROM tb_course WHERE status = 1), ' 门课程') AS summary;

SELECT 
    CONCAT('共产生 ', 
           (SELECT COUNT(*) FROM tb_course_selection WHERE status = 1), ' 条选课记录，',
           (SELECT COUNT(*) FROM tb_grade), ' 条成绩记录，',
           (SELECT COUNT(*) FROM tb_payment_record WHERE status = 1), ' 条缴费记录') AS activity_summary;

SELECT 
    CONCAT('数据库总大小约 ', 
           ROUND(SUM((data_length + index_length) / 1024 / 1024), 2), ' MB') AS storage_summary
FROM information_schema.tables 
WHERE table_schema = 'campus_management_db';

SELECT '校园管理系统数据库统计报告完成！' AS final_message;
