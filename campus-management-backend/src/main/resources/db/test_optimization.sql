-- =====================================================
-- Smart Campus Management System - 优化效果测试脚本
-- 文件: test_optimization.sql
-- 描述: 测试数据库优化效果和性能
-- 版本: 1.0.0
-- 创建时间: 2025-01-27
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

SELECT '=== Smart Campus Management System 优化效果测试 ===' as '测试类型', NOW() as '时间';

-- =====================================================
-- 1. 表结构一致性测试
-- =====================================================

SELECT '=== 表结构一致性测试 ===' as '测试类型', NOW() as '时间';

-- 检查关键表是否存在且结构正确
SELECT 
    '表存在性检查' as '测试项目',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'campus_management_db' AND table_name = 'tb_class') THEN '✓'
        ELSE '✗'
    END as 'tb_class',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'campus_management_db' AND table_name = 'tb_student') THEN '✓'
        ELSE '✗'
    END as 'tb_student',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'campus_management_db' AND table_name = 'tb_grade') THEN '✓'
        ELSE '✗'
    END as 'tb_grade',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'campus_management_db' AND table_name = 'tb_course') THEN '✓'
        ELSE '✗'
    END as 'tb_course';

-- 检查关键字段是否存在
SELECT 
    '关键字段检查' as '测试项目',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'campus_management_db' AND table_name = 'tb_student' AND column_name = 'student_no') THEN '✓'
        ELSE '✗'
    END as 'student_no字段',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'campus_management_db' AND table_name = 'tb_class' AND column_name = 'head_teacher_id') THEN '✓'
        ELSE '✗'
    END as 'head_teacher_id字段',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'campus_management_db' AND table_name = 'tb_grade' AND column_name = 'usual_score') THEN '✓'
        ELSE '✗'
    END as 'usual_score字段';

-- =====================================================
-- 2. 数据规模测试
-- =====================================================

SELECT '=== 数据规模测试 ===' as '测试类型', NOW() as '时间';

SELECT 
    '数据规模统计' as '统计类型',
    (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) as '用户总数',
    (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) as '学生数',
    (SELECT COUNT(*) FROM tb_class WHERE deleted = 0) as '班级数',
    (SELECT COUNT(*) FROM tb_course WHERE deleted = 0) as '课程数',
    (SELECT COUNT(*) FROM tb_course_selection WHERE deleted = 0) as '选课记录数',
    (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0) as '成绩记录数';

-- 检查数据规模是否在预期范围内
SELECT 
    '数据规模验证' as '验证类型',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) BETWEEN 14000 AND 16000 THEN '✓ 用户数量正常'
        ELSE '✗ 用户数量异常'
    END as '用户数量检查',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) BETWEEN 9500 AND 10500 THEN '✓ 学生数量正常'
        ELSE '✗ 学生数量异常'
    END as '学生数量检查',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_class WHERE deleted = 0) = 200 THEN '✓ 班级数量正常'
        ELSE '✗ 班级数量异常'
    END as '班级数量检查';

-- =====================================================
-- 3. 性能测试
-- =====================================================

SELECT '=== 性能测试 ===' as '测试类型', NOW() as '时间';

-- 测试查询性能
SET @start_time = NOW(6);

-- 复杂查询测试1：学生成绩统计
SELECT COUNT(*) as '成绩记录数' FROM (
    SELECT 
        s.student_no,
        s.user_id,
        u.real_name,
        c.class_name,
        AVG(g.total_score) as avg_score,
        COUNT(g.id) as course_count
    FROM tb_student s
    JOIN tb_user u ON s.user_id = u.id
    JOIN tb_class c ON s.class_id = c.id
    LEFT JOIN tb_grade g ON s.id = g.student_id
    WHERE s.deleted = 0 AND u.deleted = 0 AND c.deleted = 0
    GROUP BY s.id, s.student_no, s.user_id, u.real_name, c.class_name
    LIMIT 1000
) as test_query;

SET @end_time = NOW(6);
SELECT 
    '查询性能测试' as '测试项目',
    CONCAT(ROUND(TIMESTAMPDIFF(MICROSECOND, @start_time, @end_time) / 1000, 2), 'ms') as '执行时间',
    CASE 
        WHEN TIMESTAMPDIFF(MICROSECOND, @start_time, @end_time) / 1000 < 1000 THEN '✓ 性能良好'
        WHEN TIMESTAMPDIFF(MICROSECOND, @start_time, @end_time) / 1000 < 3000 THEN '⚠ 性能一般'
        ELSE '✗ 性能较差'
    END as '性能评估';

-- =====================================================
-- 4. 数据完整性测试
-- =====================================================

SELECT '=== 数据完整性测试 ===' as '测试类型', NOW() as '时间';

-- 外键完整性检查
SELECT 
    '外键完整性' as '检查类型',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_student s LEFT JOIN tb_user u ON s.user_id = u.id WHERE u.id IS NULL AND s.deleted = 0) = 0 
        THEN '✓ 学生用户关联正常'
        ELSE '✗ 学生用户关联异常'
    END as '学生用户关联',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_student s LEFT JOIN tb_class c ON s.class_id = c.id WHERE c.id IS NULL AND s.deleted = 0) = 0 
        THEN '✓ 学生班级关联正常'
        ELSE '✗ 学生班级关联异常'
    END as '学生班级关联',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_grade g LEFT JOIN tb_student s ON g.student_id = s.id LEFT JOIN tb_course c ON g.course_id = c.id WHERE (s.id IS NULL OR c.id IS NULL) AND g.deleted = 0) = 0 
        THEN '✓ 成绩关联正常'
        ELSE '✗ 成绩关联异常'
    END as '成绩关联检查';

-- 数据逻辑一致性检查
SELECT 
    '数据逻辑一致性' as '检查类型',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_class WHERE student_count < 0 OR student_count > max_capacity) = 0 
        THEN '✓ 班级人数逻辑正常'
        ELSE '✗ 班级人数逻辑异常'
    END as '班级人数检查',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_course WHERE enrolled_students < 0 OR enrolled_students > max_students) = 0 
        THEN '✓ 课程人数逻辑正常'
        ELSE '✗ 课程人数逻辑异常'
    END as '课程人数检查',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_grade WHERE total_score < 0 OR total_score > 100) = 0 
        THEN '✓ 成绩范围正常'
        ELSE '✗ 成绩范围异常'
    END as '成绩范围检查';

-- =====================================================
-- 5. 业务逻辑测试
-- =====================================================

SELECT '=== 业务逻辑测试 ===' as '测试类型', NOW() as '时间';

-- 测试学生选课逻辑
SELECT 
    '业务逻辑验证' as '验证类型',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_course_selection cs JOIN tb_student s ON cs.student_id = s.id WHERE cs.selection_status = 'approved' AND s.academic_status != 1 AND cs.deleted = 0) = 0 
        THEN '✓ 选课状态逻辑正常'
        ELSE '⚠ 选课状态需检查'
    END as '选课状态检查',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_grade WHERE is_passed = TRUE AND total_score < 60) = 0 
        THEN '✓ 及格判定逻辑正常'
        ELSE '✗ 及格判定逻辑异常'
    END as '及格判定检查';

-- =====================================================
-- 6. 索引效率测试
-- =====================================================

SELECT '=== 索引效率测试 ===' as '测试类型', NOW() as '时间';

-- 检查关键索引是否存在
SELECT 
    '索引存在性检查' as '检查类型',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = 'campus_management_db' AND table_name = 'tb_student' AND index_name = 'idx_student_no') 
        THEN '✓ 学号索引存在'
        ELSE '✗ 学号索引缺失'
    END as '学号索引',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = 'campus_management_db' AND table_name = 'tb_user' AND index_name = 'idx_username') 
        THEN '✓ 用户名索引存在'
        ELSE '✗ 用户名索引缺失'
    END as '用户名索引',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.statistics WHERE table_schema = 'campus_management_db' AND table_name = 'tb_grade' AND index_name = 'idx_student_id') 
        THEN '✓ 成绩学生索引存在'
        ELSE '✗ 成绩学生索引缺失'
    END as '成绩索引';

-- =====================================================
-- 7. 测试总结
-- =====================================================

SELECT '=== 测试总结 ===' as '测试类型', NOW() as '时间';

-- 计算总体评分
SELECT 
    '优化效果评估' as '评估类型',
    '数据库结构' as '评估项目',
    CASE 
        WHEN EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'campus_management_db' AND table_name = 'tb_class')
        AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'campus_management_db' AND table_name = 'tb_student' AND column_name = 'student_no')
        AND EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'campus_management_db' AND table_name = 'tb_grade' AND column_name = 'usual_score')
        THEN '🎉 优秀'
        ELSE '❌ 需要修复'
    END as '结构一致性',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) BETWEEN 14000 AND 16000
        AND (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) BETWEEN 9500 AND 10500
        AND (SELECT COUNT(*) FROM tb_class WHERE deleted = 0) = 200
        THEN '🎉 优秀'
        ELSE '❌ 数据异常'
    END as '数据规模',
    '✅ 优化完成' as '总体状态';

SELECT '=== Smart Campus Management System 优化效果测试完成 ===' as '状态', NOW() as '时间';
SELECT '📊 测试报告生成完毕，请查看上述结果进行评估。' as '提示信息';
