-- =====================================================
-- 修复学生-班级关联数据脚本
-- =====================================================
-- 文件名: fix_student_class_references.sql
-- 描述: 修复学生表中班级引用的外键约束错误
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 兼容性: MySQL 8.0+
-- =====================================================

USE campus_management_db;

-- 设置字符集
SET NAMES utf8mb4;

-- 开始事务
START TRANSACTION;

-- 1. 查找引用了不存在班级ID的学生记录
SELECT 'Invalid class references in student table:' AS message;
SELECT s.id, s.student_no, s.class_id
FROM tb_student s
LEFT JOIN tb_school_class c ON s.class_id = c.id
WHERE s.class_id IS NOT NULL AND c.id IS NULL;

-- 2. 临时表格用于存储问题学生列表
DROP TEMPORARY TABLE IF EXISTS invalid_student_class_refs;
CREATE TEMPORARY TABLE invalid_student_class_refs AS
SELECT s.id, s.student_no, s.class_id, s.grade, s.major
FROM tb_student s
LEFT JOIN tb_school_class c ON s.class_id = c.id
WHERE s.class_id IS NOT NULL AND c.id IS NULL;

-- 3. 修复方案1: 将无效班级引用设为NULL (保守方案)
UPDATE tb_student
SET class_id = NULL
WHERE id IN (SELECT id FROM invalid_student_class_refs);

-- 4. 显示修复结果
SELECT 'Fixed student records by setting class_id to NULL:' AS message;
SELECT COUNT(*) AS fixed_records FROM invalid_student_class_refs;

-- 如需根据学生的年级和专业分配到正确班级，可使用如下方案
/*
-- 为每个学生根据年级和专业查找匹配的班级
UPDATE tb_student s
JOIN invalid_student_class_refs isr ON s.id = isr.id
JOIN tb_school_class c ON
  c.grade = s.grade AND
  c.major = s.major
SET s.class_id = c.id
WHERE s.id IN (SELECT id FROM invalid_student_class_refs);

-- 显示更新统计
SELECT 'Students matched to correct classes:' AS message;
SELECT COUNT(*) FROM tb_student
WHERE id IN (SELECT id FROM invalid_student_class_refs)
AND class_id IS NOT NULL;
*/

-- 最后，再次验证是否还有外键问题
SELECT 'Remaining invalid class references:' AS message;
SELECT COUNT(*) AS remaining_issues
FROM tb_student s
LEFT JOIN tb_school_class c ON s.class_id = c.id
WHERE s.class_id IS NOT NULL AND c.id IS NULL;

-- 提交事务
COMMIT;

-- 结束报告
SELECT 'Student-class reference fix completed.' AS message,
NOW() AS completion_time;
