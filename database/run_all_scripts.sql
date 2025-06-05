-- ================================================
-- 校园管理系统数据库完整初始化脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- 功能: 执行所有数据库脚本，创建完整的校园管理系统数据库
-- ================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 显示开始信息
SELECT '开始执行校园管理系统数据库初始化...' AS message;
SELECT NOW() AS start_time;

-- ================================================
-- 1. 创建数据库
-- ================================================
SELECT '正在创建数据库...' AS step;
SOURCE 00_create_database.sql;

-- ================================================
-- 2. 创建表结构
-- ================================================
SELECT '正在创建表结构...' AS step;
SOURCE 01_create_tables.sql;

-- ================================================
-- 3. 创建索引和外键
-- ================================================
SELECT '正在创建索引和外键...' AS step;
SOURCE 02_create_indexes.sql;

-- ================================================
-- 4. 插入基础数据
-- ================================================
SELECT '正在插入基础数据...' AS step;
SOURCE 03_insert_base_data.sql;

-- ================================================
-- 5. 插入用户和学生数据
-- ================================================
SELECT '正在插入用户和学生数据...' AS step;
SOURCE 04_insert_user_student_data.sql;

-- ================================================
-- 6. 插入课程数据
-- ================================================
SELECT '正在插入课程数据...' AS step;
SOURCE 05_insert_course_data.sql;

-- ================================================
-- 7. 插入选课数据
-- ================================================
SELECT '正在插入选课数据...' AS step;
SOURCE 06_insert_selection_data.sql;

-- ================================================
-- 8. 插入成绩数据
-- ================================================
SELECT '正在插入成绩数据...' AS step;
SOURCE 07_insert_grade_data.sql;

-- ================================================
-- 9. 插入缴费记录数据
-- ================================================
SELECT '正在插入缴费记录数据...' AS step;
SOURCE 08_insert_payment_data.sql;

-- ================================================
-- 10. 生成最终统计报告
-- ================================================
SELECT '正在生成最终统计报告...' AS step;
SOURCE 09_final_statistics.sql;

-- ================================================
-- 11. 初始化完成
-- ================================================
SELECT '数据库初始化完成！' AS message;
SELECT NOW() AS end_time;

-- 显示所有表的记录数统计
SELECT '=== 数据库表记录统计 ===' AS summary;

SELECT 'tb_user' AS table_name, COUNT(*) AS record_count FROM tb_user
UNION ALL
SELECT 'tb_role' AS table_name, COUNT(*) AS record_count FROM tb_role
UNION ALL
SELECT 'tb_permission' AS table_name, COUNT(*) AS record_count FROM tb_permission
UNION ALL
SELECT 'tb_user_role' AS table_name, COUNT(*) AS record_count FROM tb_user_role
UNION ALL
SELECT 'tb_role_permission' AS table_name, COUNT(*) AS record_count FROM tb_role_permission
UNION ALL
SELECT 'tb_class' AS table_name, COUNT(*) AS record_count FROM tb_class
UNION ALL
SELECT 'tb_student' AS table_name, COUNT(*) AS record_count FROM tb_student
UNION ALL
SELECT 'tb_course' AS table_name, COUNT(*) AS record_count FROM tb_course
UNION ALL
SELECT 'tb_course_schedule' AS table_name, COUNT(*) AS record_count FROM tb_course_schedule
UNION ALL
SELECT 'tb_course_selection' AS table_name, COUNT(*) AS record_count FROM tb_course_selection
UNION ALL
SELECT 'tb_grade' AS table_name, COUNT(*) AS record_count FROM tb_grade
UNION ALL
SELECT 'tb_fee_item' AS table_name, COUNT(*) AS record_count FROM tb_fee_item
UNION ALL
SELECT 'tb_payment_record' AS table_name, COUNT(*) AS record_count FROM tb_payment_record;

-- 显示数据库大小信息
SELECT 
    table_name,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS size_mb
FROM information_schema.tables 
WHERE table_schema = 'campus_management_db'
ORDER BY (data_length + index_length) DESC;

-- 显示关键业务统计
SELECT '=== 关键业务数据统计 ===' AS business_summary;

-- 用户统计
SELECT '用户类型统计:' AS category;
SELECT r.role_name, COUNT(ur.user_id) as user_count
FROM tb_role r
LEFT JOIN tb_user_role ur ON r.id = ur.role_id
LEFT JOIN tb_user u ON ur.user_id = u.id AND u.status = 1
GROUP BY r.id, r.role_name
ORDER BY user_count DESC;

-- 学生年级统计
SELECT '学生年级统计:' AS category;
SELECT grade, COUNT(*) as student_count 
FROM tb_student 
WHERE status = 1 
GROUP BY grade 
ORDER BY grade;

-- 课程类型统计
SELECT '课程类型统计:' AS category;
SELECT course_type, COUNT(*) as course_count 
FROM tb_course 
WHERE status = 1 
GROUP BY course_type;

-- 选课统计
SELECT '选课统计:' AS category;
SELECT 
    COUNT(*) as total_selections,
    COUNT(DISTINCT student_id) as students_with_selections,
    COUNT(DISTINCT course_id) as courses_with_selections,
    ROUND(AVG(selections_per_student), 2) as avg_selections_per_student
FROM (
    SELECT student_id, COUNT(*) as selections_per_student
    FROM tb_course_selection
    WHERE status = 1
    GROUP BY student_id
) t;

-- 成绩统计
SELECT '成绩等级分布:' AS category;
SELECT level, COUNT(*) as grade_count,
       ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_grade WHERE is_makeup = 0), 2) as percentage
FROM tb_grade 
WHERE is_makeup = 0
GROUP BY level 
ORDER BY FIELD(level, 'A+', 'A', 'A-', 'B+', 'B', 'B-', 'C+', 'C', 'C-', 'D', 'F');

-- 缴费统计
SELECT '缴费统计:' AS category;
SELECT 
    COUNT(*) as total_payments,
    COUNT(DISTINCT student_id) as students_with_payments,
    ROUND(SUM(amount), 2) as total_amount,
    ROUND(AVG(amount), 2) as avg_amount
FROM tb_payment_record 
WHERE status = 1;

SELECT '校园管理系统数据库初始化完成！所有数据已成功插入。' AS final_message;
