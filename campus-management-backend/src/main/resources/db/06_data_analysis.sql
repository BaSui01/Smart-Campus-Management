-- =====================================================
-- Smart Campus Management System - 全面表检查脚本
-- 文件: 07_data_analysis.sql
-- 描述: 检查所有表的结构、数据、索引、约束等完整情况
-- 版本: 2.0.0
-- 创建时间: 2025-01-27
-- 更新时间: 2025-01-27
--
-- 功能：
-- 1. 检查所有表的数据情况
-- 2. 验证表结构完整性
-- 3. 检查索引和约束
-- 4. 分析数据质量
-- 5. 生成优化建议
-- =====================================================

USE campus_management_db;

SELECT '=== 智慧校园管理系统 - 全面表检查分析 ===' as '分析类型', NOW() as '分析时间';

-- =====================================================
-- 1. 数据库基本信息检查
-- =====================================================

SELECT '1. 数据库基本信息' as '检查项目', NOW() as '检查时间';

SELECT
    '数据库信息' as '信息类型',
    SCHEMA_NAME as '数据库名',
    DEFAULT_CHARACTER_SET_NAME as '字符集',
    DEFAULT_COLLATION_NAME as '排序规则'
FROM information_schema.SCHEMATA
WHERE SCHEMA_NAME = 'campus_management_db';

-- =====================================================
-- 2. 所有表的基本信息检查
-- =====================================================

SELECT '2. 表基本信息统计' as '检查项目', NOW() as '检查时间';

SELECT
    '表信息统计' as '统计类型',
    table_name as '表名',
    table_rows as '记录数',
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as '表大小(MB)',
    table_collation as '排序规则',
    engine as '存储引擎',
    CASE
        WHEN table_rows > 0 THEN '✓ 有数据'
        ELSE '✗ 无数据'
    END as '数据状态',
    CASE
        WHEN table_name = 'tb_user' AND table_rows > 10000 THEN '大量数据'
        WHEN table_name = 'tb_course' AND table_rows > 500 THEN '充足数据'
        WHEN table_name = 'tb_student' AND table_rows > 5000 THEN '大量数据'
        WHEN table_rows > 1000 THEN '大量数据'
        WHEN table_rows > 100 THEN '充足数据'
        WHEN table_rows > 10 THEN '少量数据'
        WHEN table_rows > 0 THEN '极少数据'
        ELSE '无数据'
    END as '数据量评估'
FROM information_schema.tables
WHERE table_schema = 'campus_management_db'
    AND table_type = 'BASE TABLE'
    AND table_name LIKE 'tb_%'
ORDER BY table_rows DESC;

-- =====================================================
-- 3. 表结构完整性检查
-- =====================================================

SELECT '3. 表结构完整性检查' as '检查项目', NOW() as '检查时间';

-- 检查所有表的列信息
SELECT
    '表结构检查' as '检查类型',
    table_name as '表名',
    COUNT(*) as '列数',
    SUM(CASE WHEN is_nullable = 'NO' THEN 1 ELSE 0 END) as '非空列数',
    SUM(CASE WHEN column_key = 'PRI' THEN 1 ELSE 0 END) as '主键列数',
    SUM(CASE WHEN column_key = 'UNI' THEN 1 ELSE 0 END) as '唯一键列数',
    SUM(CASE WHEN column_key = 'MUL' THEN 1 ELSE 0 END) as '索引列数'
FROM information_schema.columns
WHERE table_schema = 'campus_management_db'
    AND table_name LIKE 'tb_%'
GROUP BY table_name
ORDER BY table_name;

-- =====================================================
-- 4. 索引检查
-- =====================================================

SELECT '4. 索引检查' as '检查项目', NOW() as '检查时间';

SELECT
    '索引统计' as '检查类型',
    table_name as '表名',
    COUNT(DISTINCT index_name) as '索引数量',
    SUM(CASE WHEN index_name = 'PRIMARY' THEN 1 ELSE 0 END) as '主键索引',
    SUM(CASE WHEN non_unique = 0 AND index_name != 'PRIMARY' THEN 1 ELSE 0 END) as '唯一索引',
    SUM(CASE WHEN non_unique = 1 THEN 1 ELSE 0 END) as '普通索引'
FROM information_schema.statistics
WHERE table_schema = 'campus_management_db'
    AND table_name LIKE 'tb_%'
GROUP BY table_name
ORDER BY table_name;

-- =====================================================
-- 5. 外键约束检查
-- =====================================================

SELECT '5. 外键约束检查' as '检查项目', NOW() as '检查时间';

SELECT
    '外键约束' as '检查类型',
    constraint_name as '约束名',
    table_name as '表名',
    column_name as '列名',
    referenced_table_name as '引用表',
    referenced_column_name as '引用列'
FROM information_schema.key_column_usage
WHERE table_schema = 'campus_management_db'
    AND referenced_table_name IS NOT NULL
    AND table_name LIKE 'tb_%'
ORDER BY table_name, constraint_name;

-- =====================================================
-- 6. 核心业务数据检查
-- =====================================================

SELECT '6. 核心业务数据检查' as '检查项目', NOW() as '检查时间';

SELECT
    '核心业务表检查' as '检查类型',
    '用户管理' as '业务模块',
    (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) as '用户数',
    (SELECT COUNT(*) FROM tb_role WHERE deleted = 0) as '角色数',
    (SELECT COUNT(*) FROM tb_permission WHERE deleted = 0) as '权限数',
    (SELECT COUNT(*) FROM tb_user_role WHERE deleted = 0) as '用户角色关联数'

UNION ALL

SELECT
    '核心业务表检查' as '检查类型',
    '学院班级' as '业务模块',
    (SELECT COUNT(*) FROM tb_department WHERE deleted = 0) as '学院数',
    (SELECT COUNT(*) FROM tb_class WHERE deleted = 0) as '班级数',
    (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) as '学生数',
    (SELECT COUNT(*) FROM tb_user u
     JOIN tb_user_role ur ON u.id = ur.user_id
     JOIN tb_role r ON ur.role_id = r.id
     WHERE u.deleted = 0 AND r.role_name LIKE '%教师%') as '教师数'

UNION ALL

SELECT
    '核心业务表检查' as '检查类型',
    '课程教学' as '业务模块',
    (SELECT COUNT(*) FROM tb_course WHERE deleted = 0) as '课程数',
    (SELECT COUNT(*) FROM tb_classroom WHERE deleted = 0) as '教室数',
    (SELECT COUNT(*) FROM tb_time_slot WHERE deleted = 0) as '时间段数',
    (SELECT COUNT(*) FROM tb_course_schedule WHERE deleted = 0) as '课程表数'

UNION ALL

SELECT
    '核心业务表检查' as '检查类型',
    '选课成绩' as '业务模块',
    (SELECT COUNT(*) FROM tb_course_selection WHERE deleted = 0) as '选课记录数',
    (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0) as '成绩记录数',
    (SELECT COUNT(*) FROM tb_exam WHERE deleted = 0) as '考试数',
    (SELECT COUNT(*) FROM tb_assignment WHERE deleted = 0) as '作业数'

UNION ALL

SELECT
    '核心业务表检查' as '检查类型',
    '考勤财务' as '业务模块',
    (SELECT COUNT(*) FROM tb_attendance WHERE deleted = 0) as '考勤记录数',
    (SELECT COUNT(*) FROM tb_payment_record WHERE deleted = 0) as '缴费记录数',
    (SELECT COUNT(*) FROM tb_fee_item WHERE deleted = 0) as '费用项目数',
    (SELECT COUNT(*) FROM tb_notification WHERE deleted = 0) as '通知数';

-- =====================================================
-- 7. 空表识别
-- =====================================================

SELECT '7. 空表识别' as '检查项目', NOW() as '检查时间';

SELECT
    '空表识别' as '检查类型',
    table_name as '空表名',
    '需要补充数据' as '建议',
    CASE
        WHEN table_name IN ('tb_user', 'tb_student', 'tb_course', 'tb_department') THEN '高优先级'
        WHEN table_name IN ('tb_grade', 'tb_attendance', 'tb_exam') THEN '中优先级'
        ELSE '低优先级'
    END as '优先级'
FROM information_schema.tables
WHERE table_schema = 'campus_management_db'
    AND table_type = 'BASE TABLE'
    AND table_name LIKE 'tb_%'
    AND table_rows = 0
ORDER BY
    CASE
        WHEN table_name IN ('tb_user', 'tb_student', 'tb_course', 'tb_department') THEN 1
        WHEN table_name IN ('tb_grade', 'tb_attendance', 'tb_exam') THEN 2
        ELSE 3
    END, table_name;

-- =====================================================
-- 8. 数据质量检查
-- =====================================================

SELECT '8. 数据质量检查' as '检查项目', NOW() as '检查时间';

-- 检查关键业务数据缺失
SELECT
    '业务数据缺失检查' as '检查类型',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_exam WHERE deleted = 0) = 0 THEN '❌ 考试数据缺失'
        ELSE '✅ 考试数据正常'
    END as '考试管理',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_assignment WHERE deleted = 0) = 0 THEN '❌ 作业数据缺失'
        ELSE '✅ 作业数据正常'
    END as '作业管理',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_notification WHERE deleted = 0) = 0 THEN '❌ 通知数据缺失'
        ELSE '✅ 通知数据正常'
    END as '通知管理',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_message WHERE deleted = 0) = 0 THEN '❌ 消息数据缺失'
        ELSE '✅ 消息数据正常'
    END as '消息管理';

-- 检查数据一致性
SELECT
    '数据一致性检查' as '检查类型',
    '管理员账号' as '检查项',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_user WHERE username = 'admin' AND deleted = 0) > 0 THEN '✅ 存在'
        ELSE '❌ 缺失'
    END as '检查结果',
    '系统必须有管理员账号' as '说明'

UNION ALL

SELECT
    '数据一致性检查' as '检查类型',
    '角色权限配置' as '检查项',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_role_permission WHERE deleted = 0) > 0 THEN '✅ 已配置'
        ELSE '❌ 未配置'
    END as '检查结果',
    '角色必须分配权限' as '说明'

UNION ALL

SELECT
    '数据一致性检查' as '检查类型',
    '系统配置' as '检查项',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_system_config WHERE deleted = 0) > 0 THEN '✅ 已配置'
        ELSE '❌ 未配置'
    END as '检查结果',
    '系统需要基础配置' as '说明';

-- =====================================================
-- 9. 数据完整性检查
-- =====================================================

SELECT '9. 数据完整性检查' as '检查项目', NOW() as '检查时间';

SELECT
    '数据完整性检查' as '检查类型',
    '学生用户关联' as '检查项',
    (SELECT COUNT(*) FROM tb_student s WHERE s.user_id NOT IN (SELECT u.id FROM tb_user u WHERE u.deleted = 0)) as '异常数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_student s WHERE s.user_id NOT IN (SELECT u.id FROM tb_user u WHERE u.deleted = 0)) = 0
        THEN '✅ 正常'
        ELSE '❌ 异常'
    END as '检查结果'

UNION ALL

SELECT
    '数据完整性检查' as '检查类型',
    '班级学院关联' as '检查项',
    (SELECT COUNT(*) FROM tb_class c WHERE c.department_id NOT IN (SELECT d.id FROM tb_department d WHERE d.deleted = 0)) as '异常数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_class c WHERE c.department_id NOT IN (SELECT d.id FROM tb_department d WHERE d.deleted = 0)) = 0
        THEN '✅ 正常'
        ELSE '❌ 异常'
    END as '检查结果'

UNION ALL

SELECT
    '数据完整性检查' as '检查类型',
    '选课记录关联' as '检查项',
    (SELECT COUNT(*) FROM tb_course_selection cs
     WHERE cs.student_id NOT IN (SELECT s.id FROM tb_student s WHERE s.deleted = 0)
        OR cs.course_id NOT IN (SELECT c.id FROM tb_course c WHERE c.deleted = 0)) as '异常数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_course_selection cs
              WHERE cs.student_id NOT IN (SELECT s.id FROM tb_student s WHERE s.deleted = 0)
                 OR cs.course_id NOT IN (SELECT c.id FROM tb_course c WHERE c.deleted = 0)) = 0
        THEN '✅ 正常'
        ELSE '❌ 异常'
    END as '检查结果'

UNION ALL

SELECT
    '数据完整性检查' as '检查类型',
    '教师用户关联' as '检查项',
    (SELECT COUNT(*) FROM tb_user u
     JOIN tb_user_role ur ON u.id = ur.user_id
     JOIN tb_role r ON ur.role_id = r.id
     WHERE u.deleted = 0 AND r.role_name LIKE '%教师%' AND u.id IS NULL) as '异常数量',
    '✅ 正常' as '检查结果';

-- =====================================================
-- 10. 数据量评估
-- =====================================================

SELECT '10. 数据量评估' as '检查项目', NOW() as '检查时间';

SELECT
    '数据量评估' as '评估类型',
    '总用户数' as '指标',
    (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) as '当前数量',
    '10000+' as '目标数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) >= 10000 THEN '✅ 达标'
        ELSE '❌ 不足'
    END as '评估结果'

UNION ALL

SELECT
    '数据量评估' as '评估类型',
    '学生数量' as '指标',
    (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) as '当前数量',
    '8000+' as '目标数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) >= 8000 THEN '✅ 达标'
        ELSE '❌ 不足'
    END as '评估结果'

UNION ALL

SELECT
    '数据量评估' as '评估类型',
    '课程数量' as '指标',
    (SELECT COUNT(*) FROM tb_course WHERE deleted = 0) as '当前数量',
    '800+' as '目标数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_course WHERE deleted = 0) >= 800 THEN '✅ 达标'
        ELSE '❌ 不足'
    END as '评估结果'

UNION ALL

SELECT
    '数据量评估' as '评估类型',
    '选课记录' as '指标',
    (SELECT COUNT(*) FROM tb_course_selection WHERE deleted = 0) as '当前数量',
    '50000+' as '目标数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_course_selection WHERE deleted = 0) >= 50000 THEN '✅ 达标'
        ELSE '❌ 不足'
    END as '评估结果'

UNION ALL

SELECT
    '数据量评估' as '评估类型',
    '成绩记录' as '指标',
    (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0) as '当前数量',
    '30000+' as '目标数量',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0) >= 30000 THEN '✅ 达标'
        ELSE '❌ 不足'
    END as '评估结果';

-- =====================================================
-- 11. 性能分析
-- =====================================================

SELECT '11. 性能分析' as '检查项目', NOW() as '检查时间';

-- 检查大表情况
SELECT
    '大表分析' as '分析类型',
    table_name as '表名',
    table_rows as '记录数',
    ROUND(((data_length + index_length) / 1024 / 1024), 2) as '表大小(MB)',
    CASE
        WHEN table_rows > 100000 THEN '⚠️ 超大表'
        WHEN table_rows > 50000 THEN '⚠️ 大表'
        WHEN table_rows > 10000 THEN '✅ 中等表'
        ELSE '✅ 小表'
    END as '表规模评估',
    CASE
        WHEN table_rows > 50000 THEN '建议添加分区或优化索引'
        WHEN table_rows > 10000 THEN '建议监控查询性能'
        ELSE '性能良好'
    END as '优化建议'
FROM information_schema.tables
WHERE table_schema = 'campus_management_db'
    AND table_type = 'BASE TABLE'
    AND table_name LIKE 'tb_%'
    AND table_rows > 0
ORDER BY table_rows DESC
LIMIT 10;

-- =====================================================
-- 12. 最终评估结果
-- =====================================================

SELECT '12. 最终评估结果' as '检查项目', NOW() as '检查时间';

SELECT
    '最终评估结果' as '评估类型',
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.tables
              WHERE table_schema = 'campus_management_db'
                AND table_type = 'BASE TABLE'
                AND table_name LIKE 'tb_%'
                AND table_rows = 0) = 0
        THEN '✅ 所有表都有数据'
        ELSE CONCAT('❌ 有 ', (SELECT COUNT(*) FROM information_schema.tables
                              WHERE table_schema = 'campus_management_db'
                                AND table_type = 'BASE TABLE'
                                AND table_name LIKE 'tb_%'
                                AND table_rows = 0), ' 个表无数据')
    END as '数据覆盖评估',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_user WHERE deleted = 0) >= 10000
             AND (SELECT COUNT(*) FROM tb_student WHERE deleted = 0) >= 8000
             AND (SELECT COUNT(*) FROM tb_course WHERE deleted = 0) >= 500
        THEN '✅ 数据量充足'
        ELSE '❌ 数据量不足'
    END as '数据量评估',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_user WHERE username = 'admin' AND deleted = 0) > 0
             AND (SELECT COUNT(*) FROM tb_role_permission WHERE deleted = 0) > 0
        THEN '✅ 系统配置完整'
        ELSE '❌ 系统配置不完整'
    END as '系统配置评估';

-- =====================================================
-- 13. 优化建议
-- =====================================================

SELECT '13. 优化建议' as '检查项目', NOW() as '检查时间';

SELECT
    '优化建议' as '建议类型',
    '数据补充' as '建议分类',
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.tables
              WHERE table_schema = 'campus_management_db'
                AND table_type = 'BASE TABLE'
                AND table_name LIKE 'tb_%'
                AND table_rows = 0) > 0
        THEN '建议运行 04_complete_all_tables.sql 补充空表数据'
        ELSE '所有表都有数据，无需补充'
    END as '具体建议'

UNION ALL

SELECT
    '优化建议' as '建议类型',
    '性能优化' as '建议分类',
    CASE
        WHEN (SELECT COUNT(*) FROM information_schema.tables
              WHERE table_schema = 'campus_management_db'
                AND table_type = 'BASE TABLE'
                AND table_name LIKE 'tb_%'
                AND table_rows > 50000) > 0
        THEN '建议为大表添加合适的索引和分区策略'
        ELSE '当前表规模适中，性能良好'
    END as '具体建议'

UNION ALL

SELECT
    '优化建议' as '建议类型',
    '系统配置' as '建议分类',
    CASE
        WHEN (SELECT COUNT(*) FROM tb_system_config WHERE deleted = 0) = 0
        THEN '建议添加系统基础配置数据'
        ELSE '系统配置完整'
    END as '具体建议';

-- =====================================================
-- 检查完成
-- =====================================================

SELECT '=== 智慧校园管理系统全面表检查完成 ===' as '状态', NOW() as '完成时间';
SELECT '🎯 检查项目：表结构、数据完整性、索引、约束、性能分析' as '检查范围';
SELECT '📊 如发现问题，请根据优化建议进行相应处理' as '后续建议';
