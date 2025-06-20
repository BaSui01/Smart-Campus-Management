-- 智慧校园管理系统 - 数据迁移脚本
-- 创建时间: 2025-06-20
-- 说明: 将现有数据迁移到分表中

-- 开始事务
START TRANSACTION;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 迁移学生数据到分表
-- 注意：这里假设原表中有enrollment_year字段，如果没有需要先添加

-- 检查原学生表是否存在数据
SELECT '开始迁移学生数据...' as status;

-- 迁移2024年入学学生
INSERT IGNORE INTO tb_student_2024 
SELECT * FROM tb_student 
WHERE enrollment_year = 2024 AND deleted = 0;

-- 迁移2025年入学学生
INSERT IGNORE INTO tb_student_2025 
SELECT * FROM tb_student 
WHERE enrollment_year = 2025 AND deleted = 0;

-- 迁移2026年入学学生
INSERT IGNORE INTO tb_student_2026 
SELECT * FROM tb_student 
WHERE enrollment_year = 2026 AND deleted = 0;

-- 迁移2027年入学学生
INSERT IGNORE INTO tb_student_2027 
SELECT * FROM tb_student 
WHERE enrollment_year = 2027 AND deleted = 0;

-- 迁移2028年入学学生
INSERT IGNORE INTO tb_student_2028 
SELECT * FROM tb_student 
WHERE enrollment_year = 2028 AND deleted = 0;

-- 验证学生数据迁移
SELECT 
    'tb_student_2024' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_student_2024
UNION ALL
SELECT 
    'tb_student_2025' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_student_2025
UNION ALL
SELECT 
    'tb_student_2026' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_student_2026
UNION ALL
SELECT 
    'tb_student_2027' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_student_2027
UNION ALL
SELECT 
    'tb_student_2028' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_student_2028
UNION ALL
SELECT 
    'tb_student_original' as table_name, 
    COUNT(*) as original_count 
FROM tb_student 
WHERE deleted = 0;

-- 2. 迁移成绩数据到分表
-- 注意：这里假设原表中有semester字段

SELECT '开始迁移成绩数据...' as status;

-- 迁移2024年第1学期成绩
INSERT IGNORE INTO tb_grade_2024_1 
SELECT * FROM tb_grade 
WHERE semester = '2024-1' AND deleted = 0;

-- 迁移2024年第2学期成绩
INSERT IGNORE INTO tb_grade_2024_2 
SELECT * FROM tb_grade 
WHERE semester = '2024-2' AND deleted = 0;

-- 迁移2025年第1学期成绩
INSERT IGNORE INTO tb_grade_2025_1 
SELECT * FROM tb_grade 
WHERE semester = '2025-1' AND deleted = 0;

-- 迁移2025年第2学期成绩
INSERT IGNORE INTO tb_grade_2025_2 
SELECT * FROM tb_grade 
WHERE semester = '2025-2' AND deleted = 0;

-- 迁移2026年第1学期成绩
INSERT IGNORE INTO tb_grade_2026_1 
SELECT * FROM tb_grade 
WHERE semester = '2026-1' AND deleted = 0;

-- 迁移2026年第2学期成绩
INSERT IGNORE INTO tb_grade_2026_2 
SELECT * FROM tb_grade 
WHERE semester = '2026-2' AND deleted = 0;

-- 验证成绩数据迁移
SELECT 
    'tb_grade_2024_1' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_grade_2024_1
UNION ALL
SELECT 
    'tb_grade_2024_2' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_grade_2024_2
UNION ALL
SELECT 
    'tb_grade_2025_1' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_grade_2025_1
UNION ALL
SELECT 
    'tb_grade_2025_2' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_grade_2025_2
UNION ALL
SELECT 
    'tb_grade_2026_1' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_grade_2026_1
UNION ALL
SELECT 
    'tb_grade_2026_2' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_grade_2026_2
UNION ALL
SELECT 
    'tb_grade_original' as table_name, 
    COUNT(*) as original_count 
FROM tb_grade 
WHERE deleted = 0;

-- 3. 迁移考勤数据到分表
-- 注意：这里假设原表中有attendance_date字段

SELECT '开始迁移考勤数据...' as status;

-- 迁移2024年6月考勤数据
INSERT IGNORE INTO tb_attendance_202406 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202406' AND deleted = 0;

-- 迁移2024年7月考勤数据
INSERT IGNORE INTO tb_attendance_202407 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202407' AND deleted = 0;

-- 迁移2024年8月考勤数据
INSERT IGNORE INTO tb_attendance_202408 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202408' AND deleted = 0;

-- 迁移2024年9月考勤数据
INSERT IGNORE INTO tb_attendance_202409 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202409' AND deleted = 0;

-- 迁移2024年10月考勤数据
INSERT IGNORE INTO tb_attendance_202410 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202410' AND deleted = 0;

-- 迁移2024年11月考勤数据
INSERT IGNORE INTO tb_attendance_202411 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202411' AND deleted = 0;

-- 迁移2024年12月考勤数据
INSERT IGNORE INTO tb_attendance_202412 
SELECT * FROM tb_attendance 
WHERE DATE_FORMAT(attendance_date, '%Y%m') = '202412' AND deleted = 0;

-- 验证考勤数据迁移
SELECT 
    'tb_attendance_202406' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202406
UNION ALL
SELECT 
    'tb_attendance_202407' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202407
UNION ALL
SELECT 
    'tb_attendance_202408' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202408
UNION ALL
SELECT 
    'tb_attendance_202409' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202409
UNION ALL
SELECT 
    'tb_attendance_202410' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202410
UNION ALL
SELECT 
    'tb_attendance_202411' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202411
UNION ALL
SELECT 
    'tb_attendance_202412' as table_name, 
    COUNT(*) as migrated_count 
FROM tb_attendance_202412
UNION ALL
SELECT 
    'tb_attendance_original' as table_name, 
    COUNT(*) as original_count 
FROM tb_attendance 
WHERE deleted = 0;

-- 4. 数据完整性验证
SELECT '数据迁移完整性验证...' as status;

-- 验证学生数据完整性
SELECT 
    '学生数据验证' as check_type,
    (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) as original_count,
    (SELECT COUNT(*) FROM tb_student_2024) + 
    (SELECT COUNT(*) FROM tb_student_2025) + 
    (SELECT COUNT(*) FROM tb_student_2026) + 
    (SELECT COUNT(*) FROM tb_student_2027) + 
    (SELECT COUNT(*) FROM tb_student_2028) as migrated_count,
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) = 
             ((SELECT COUNT(*) FROM tb_student_2024) + 
              (SELECT COUNT(*) FROM tb_student_2025) + 
              (SELECT COUNT(*) FROM tb_student_2026) + 
              (SELECT COUNT(*) FROM tb_student_2027) + 
              (SELECT COUNT(*) FROM tb_student_2028))
        THEN '✅ 数据一致'
        ELSE '❌ 数据不一致'
    END as validation_result;

-- 验证成绩数据完整性
SELECT 
    '成绩数据验证' as check_type,
    (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0) as original_count,
    (SELECT COUNT(*) FROM tb_grade_2024_1) + 
    (SELECT COUNT(*) FROM tb_grade_2024_2) + 
    (SELECT COUNT(*) FROM tb_grade_2025_1) + 
    (SELECT COUNT(*) FROM tb_grade_2025_2) + 
    (SELECT COUNT(*) FROM tb_grade_2026_1) + 
    (SELECT COUNT(*) FROM tb_grade_2026_2) as migrated_count,
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0) = 
             ((SELECT COUNT(*) FROM tb_grade_2024_1) + 
              (SELECT COUNT(*) FROM tb_grade_2024_2) + 
              (SELECT COUNT(*) FROM tb_grade_2025_1) + 
              (SELECT COUNT(*) FROM tb_grade_2025_2) + 
              (SELECT COUNT(*) FROM tb_grade_2026_1) + 
              (SELECT COUNT(*) FROM tb_grade_2026_2))
        THEN '✅ 数据一致'
        ELSE '❌ 数据不一致'
    END as validation_result;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 提交事务
COMMIT;

SELECT '数据迁移完成！' as final_status;
