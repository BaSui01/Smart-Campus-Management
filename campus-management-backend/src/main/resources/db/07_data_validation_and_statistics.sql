-- =====================================================
-- Smart Campus Management System - 数据验证和统计脚本 (修复版)
-- 文件: 07_data_validation_and_statistics.sql
-- 描述: 数据完整性验证和详细统计报告
-- 版本: 3.0.0 (修复版)
-- 创建时间: 2025-01-27
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

SELECT '=====================================================' as '';
SELECT 'Smart Campus Management System 数据验证和统计报告' as '';
SELECT '=====================================================' as '';
SELECT NOW() as '报告生成时间';
SELECT '' as '';

-- =====================================================
-- 1. 基础数据统计
-- =====================================================

SELECT '=== 1. 基础数据统计 ===' as '报告类型';

SELECT 
    '数据库表总数' as '统计项目',
    COUNT(*) as '数量',
    '个' as '单位'
FROM information_schema.tables
WHERE table_schema = 'campus_management_db'
UNION ALL
SELECT 
    '用户总数' as '统计项目',
    COUNT(*) as '数量',
    '人' as '单位'
FROM tb_user WHERE deleted = 0
UNION ALL
SELECT 
    '学院总数' as '统计项目',
    COUNT(*) as '数量',
    '个' as '单位'
FROM tb_department WHERE deleted = 0
UNION ALL
SELECT
    '班级总数' as '统计项目',
    COUNT(*) as '数量',
    '个' as '单位'
FROM tb_class WHERE deleted = 0
UNION ALL
SELECT 
    '课程总数' as '统计项目',
    COUNT(*) as '数量',
    '门' as '单位'
FROM tb_course WHERE deleted = 0
UNION ALL
SELECT 
    '教室总数' as '统计项目',
    COUNT(*) as '数量',
    '间' as '单位'
FROM tb_classroom WHERE deleted = 0;

SELECT '' as '';

-- =====================================================
-- 2. 用户角色分布统计
-- =====================================================

SELECT '=== 2. 用户角色分布统计 ===' as '报告类型';

SELECT 
    r.role_name as '角色名称',
    COUNT(ur.user_id) as '用户数量',
    ROUND(COUNT(ur.user_id) * 100.0 / (SELECT COUNT(*) FROM tb_user WHERE deleted = 0), 2) as '占比(%)',
    CASE 
        WHEN r.role_key = 'ROLE_ADMIN' THEN '系统管理'
        WHEN r.role_key = 'ROLE_TEACHER' THEN '教学管理'
        WHEN r.role_key = 'ROLE_CLASS_TEACHER' THEN '班级管理'
        WHEN r.role_key = 'ROLE_STUDENT' THEN '学习活动'
        WHEN r.role_key = 'ROLE_PARENT' THEN '家校沟通'
        ELSE '其他'
    END as '主要职能'
FROM tb_role r
LEFT JOIN tb_user_role ur ON r.id = ur.role_id AND ur.deleted = 0
LEFT JOIN tb_user u ON ur.user_id = u.id AND u.deleted = 0
WHERE r.deleted = 0
GROUP BY r.id, r.role_name, r.role_key
ORDER BY COUNT(ur.user_id) DESC;

SELECT '' as '';

-- =====================================================
-- 3. 学院和班级分布统计
-- =====================================================

SELECT '=== 3. 学院和班级分布统计 ===' as '报告类型';

SELECT
    d.department_name as '学院名称',
    d.department_code as '学院代码',
    COUNT(DISTINCT sc.id) as '班级数量',
    COUNT(DISTINCT s.id) as '学生总数',
    CASE
        WHEN COUNT(DISTINCT sc.id) > 0 THEN ROUND(COUNT(DISTINCT s.id) / COUNT(DISTINCT sc.id), 1)
        ELSE 0
    END as '平均班级人数',
    COUNT(DISTINCT c.id) as '开设课程数'
FROM tb_department d
LEFT JOIN tb_class sc ON d.id = sc.department_id AND sc.deleted = 0
LEFT JOIN tb_student s ON sc.id = s.class_id AND s.deleted = 0
LEFT JOIN tb_course c ON d.id = c.department_id AND c.deleted = 0
WHERE d.deleted = 0
GROUP BY d.id, d.department_name, d.department_code
ORDER BY COUNT(DISTINCT s.id) DESC;

SELECT '' as '';

-- =====================================================
-- 4. 年级分布统计
-- =====================================================

SELECT '=== 4. 年级分布统计 ===' as '报告类型';

SELECT 
    sc.grade as '年级',
    sc.enrollment_year as '入学年份',
    COUNT(DISTINCT sc.id) as '班级数量',
    COUNT(DISTINCT s.id) as '学生总数',
    ROUND(AVG(sc.student_count), 1) as '平均班级人数',
    COUNT(DISTINCT sc.class_teacher_id) as '班主任数量',
    CASE 
        WHEN sc.enrollment_year = 2025 THEN '大一'
        WHEN sc.enrollment_year = 2024 THEN '大二'
        WHEN sc.enrollment_year = 2023 THEN '大三'
        WHEN sc.enrollment_year = 2022 THEN '大四'
        WHEN sc.enrollment_year = 2021 THEN '大五/研究生'
        ELSE '其他'
    END as '学习阶段'
FROM tb_school_class sc
LEFT JOIN tb_student s ON sc.id = s.class_id AND s.deleted = 0
WHERE sc.deleted = 0
GROUP BY sc.grade, sc.enrollment_year
ORDER BY sc.enrollment_year DESC;

SELECT '' as '';

-- =====================================================
-- 5. 课程统计分析
-- =====================================================

SELECT '=== 5. 课程统计分析 ===' as '报告类型';

SELECT 
    c.course_type as '课程类型',
    COUNT(*) as '课程数量',
    ROUND(AVG(c.credits), 1) as '平均学分',
    ROUND(AVG(c.hours), 1) as '平均学时',
    ROUND(AVG(c.max_students), 0) as '平均最大选课人数',
    ROUND(AVG(c.current_students), 0) as '平均当前选课人数',
    CASE 
        WHEN AVG(c.max_students) > 0 THEN ROUND(AVG(c.current_students) * 100.0 / AVG(c.max_students), 1)
        ELSE 0 
    END as '平均选课率(%)'
FROM tb_course c
WHERE c.deleted = 0
GROUP BY c.course_type
ORDER BY COUNT(*) DESC;

SELECT '' as '';

-- =====================================================
-- 6. 选课统计分析
-- =====================================================

SELECT '=== 6. 选课统计分析 ===' as '报告类型';

SELECT 
    cs.selection_status as '选课状态',
    COUNT(*) as '记录数量',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_course_selection WHERE deleted = 0), 2) as '占比(%)',
    COUNT(DISTINCT cs.student_id) as '涉及学生数',
    COUNT(DISTINCT cs.course_id) as '涉及课程数'
FROM tb_course_selection cs
WHERE cs.deleted = 0
GROUP BY cs.selection_status
ORDER BY COUNT(*) DESC;

SELECT '' as '';

-- =====================================================
-- 7. 成绩统计分析
-- =====================================================

SELECT '=== 7. 成绩统计分析 ===' as '报告类型';

SELECT 
    g.letter_grade as '等级成绩',
    COUNT(*) as '记录数量',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_grade WHERE deleted = 0), 2) as '占比(%)',
    ROUND(AVG(g.total_score), 1) as '平均分数',
    ROUND(AVG(g.gpa_points), 2) as '平均GPA',
    CASE 
        WHEN g.letter_grade IN ('A+', 'A', 'A-') THEN '优秀'
        WHEN g.letter_grade IN ('B+', 'B', 'B-') THEN '良好'
        WHEN g.letter_grade IN ('C+', 'C', 'C-') THEN '中等'
        WHEN g.letter_grade = 'D' THEN '及格'
        ELSE '不及格'
    END as '成绩等级'
FROM tb_grade g
WHERE g.deleted = 0
GROUP BY g.letter_grade
ORDER BY AVG(g.gpa_points) DESC;

SELECT '' as '';

-- =====================================================
-- 8. 缴费统计分析
-- =====================================================

SELECT '=== 8. 缴费统计分析 ===' as '报告类型';

SELECT 
    pr.payment_status as '缴费状态',
    COUNT(*) as '记录数量',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_payment_record WHERE deleted = 0), 2) as '占比(%)',
    ROUND(SUM(pr.payment_amount), 2) as '总金额(元)',
    ROUND(AVG(pr.payment_amount), 2) as '平均金额(元)',
    COUNT(DISTINCT pr.student_id) as '涉及学生数'
FROM tb_payment_record pr
WHERE pr.deleted = 0
GROUP BY pr.payment_status
ORDER BY SUM(pr.payment_amount) DESC;

SELECT '' as '';

-- 缴费方式统计
SELECT 
    pr.payment_method as '缴费方式',
    COUNT(*) as '使用次数',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_payment_record WHERE payment_status = 'paid' AND deleted = 0), 2) as '占比(%)',
    ROUND(SUM(pr.payment_amount), 2) as '总金额(元)'
FROM tb_payment_record pr
WHERE pr.payment_status = 'paid' AND pr.deleted = 0
GROUP BY pr.payment_method
ORDER BY COUNT(*) DESC;

SELECT '' as '';

-- =====================================================
-- 9. 考勤统计分析
-- =====================================================

SELECT '=== 9. 考勤统计分析 ===' as '报告类型';

SELECT 
    a.attendance_status as '考勤状态',
    COUNT(*) as '记录数量',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_attendance WHERE deleted = 0), 2) as '占比(%)',
    COUNT(DISTINCT a.student_id) as '涉及学生数',
    COUNT(DISTINCT a.course_id) as '涉及课程数',
    CASE 
        WHEN a.attendance_status = 'present' THEN '正常出勤'
        WHEN a.attendance_status = 'late' THEN '迟到'
        WHEN a.attendance_status = 'leave' THEN '请假'
        WHEN a.attendance_status = 'absent' THEN '缺勤'
        ELSE '其他'
    END as '状态说明'
FROM tb_attendance a
WHERE a.deleted = 0
GROUP BY a.attendance_status
ORDER BY COUNT(*) DESC;

SELECT '' as '';

-- =====================================================
-- 10. 家长学生关系统计
-- =====================================================

SELECT '=== 10. 家长学生关系统计 ===' as '报告类型';

SELECT 
    psr.relationship as '关系类型',
    COUNT(*) as '关系数量',
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tb_parent_student_relation WHERE deleted = 0), 2) as '占比(%)',
    COUNT(DISTINCT psr.parent_id) as '涉及家长数',
    COUNT(DISTINCT psr.student_id) as '涉及学生数'
FROM tb_parent_student_relation psr
WHERE psr.deleted = 0
GROUP BY psr.relationship
ORDER BY COUNT(*) DESC;

SELECT '' as '';

-- =====================================================
-- 11. 数据完整性检查
-- =====================================================

SELECT '=== 11. 数据完整性检查 ===' as '报告类型';

SELECT 
    '用户角色关联完整性' as '检查项目',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_user u WHERE u.deleted = 0 AND u.id NOT IN (SELECT ur.user_id FROM tb_user_role ur WHERE ur.deleted = 0)) = 0 
        THEN '通过' 
        ELSE '失败' 
    END as '检查结果',
    (SELECT COUNT(*) FROM tb_user u WHERE u.deleted = 0 AND u.id NOT IN (SELECT ur.user_id FROM tb_user_role ur WHERE ur.deleted = 0)) as '问题数量'
UNION ALL
SELECT 
    '学生信息完整性' as '检查项目',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_student s WHERE s.deleted = 0 AND (s.user_id IS NULL OR s.class_id IS NULL)) = 0 
        THEN '通过' 
        ELSE '失败' 
    END as '检查结果',
    (SELECT COUNT(*) FROM tb_student s WHERE s.deleted = 0 AND (s.user_id IS NULL OR s.class_id IS NULL)) as '问题数量'
UNION ALL
SELECT 
    '班级容量检查' as '检查项目',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_school_class sc WHERE sc.deleted = 0 AND sc.student_count > sc.max_capacity) = 0 
        THEN '通过' 
        ELSE '警告' 
    END as '检查结果',
    (SELECT COUNT(*) FROM tb_school_class sc WHERE sc.deleted = 0 AND sc.student_count > sc.max_capacity) as '问题数量'
UNION ALL
SELECT 
    '课程选课人数检查' as '检查项目',
    CASE 
        WHEN (SELECT COUNT(*) FROM tb_course c WHERE c.deleted = 0 AND c.current_students > c.max_students) = 0 
        THEN '通过' 
        ELSE '警告' 
    END as '检查结果',
    (SELECT COUNT(*) FROM tb_course c WHERE c.deleted = 0 AND c.current_students > c.max_students) as '问题数量';

SELECT '' as '';

-- =====================================================
-- 12. 数据库性能统计
-- =====================================================

SELECT '=== 12. 数据库性能统计 ===' as '报告类型';

SELECT 
    t.table_name as '表名',
    t.table_rows as '记录数量',
    ROUND(t.data_length / 1024 / 1024, 2) as '数据大小(MB)',
    ROUND(t.index_length / 1024 / 1024, 2) as '索引大小(MB)',
    ROUND((t.data_length + t.index_length) / 1024 / 1024, 2) as '总大小(MB)'
FROM information_schema.tables t
WHERE t.table_schema = 'campus_management_db'
AND t.table_type = 'BASE TABLE'
ORDER BY (t.data_length + t.index_length) DESC;

SELECT '' as '';

-- =====================================================
-- 13. 数据质量报告
-- =====================================================

SELECT '=== 13. 数据质量报告 ===' as '报告类型';

SELECT 
    '用户真实姓名空值' as '质量检查项',
    COUNT(*) as '问题数量',
    CASE WHEN COUNT(*) = 0 THEN '优秀' WHEN COUNT(*) < 10 THEN '良好' ELSE '需改进' END as '质量等级'
FROM tb_user WHERE (real_name IS NULL OR real_name = '') AND deleted = 0
UNION ALL
SELECT 
    '用户邮箱空值' as '质量检查项',
    COUNT(*) as '问题数量',
    CASE WHEN COUNT(*) = 0 THEN '优秀' WHEN COUNT(*) < 10 THEN '良好' ELSE '需改进' END as '质量等级'
FROM tb_user WHERE (email IS NULL OR email = '') AND deleted = 0
UNION ALL
SELECT 
    '学生学号重复' as '质量检查项',
    COUNT(*) - COUNT(DISTINCT student_no) as '问题数量',
    CASE WHEN COUNT(*) = COUNT(DISTINCT student_no) THEN '优秀' ELSE '需改进' END as '质量等级'
FROM tb_student WHERE deleted = 0
UNION ALL
SELECT 
    '课程代码重复' as '质量检查项',
    COUNT(*) - COUNT(DISTINCT course_code) as '问题数量',
    CASE WHEN COUNT(*) = COUNT(DISTINCT course_code) THEN '优秀' ELSE '需改进' END as '质量等级'
FROM tb_course WHERE deleted = 0;

SELECT '' as '';

-- =====================================================
-- 14. 系统总结报告
-- =====================================================

SELECT '=== 14. 系统总结报告 ===' as '报告类型';

SELECT 
    '数据库名称' as '项目',
    'campus_management_db' as '值'
UNION ALL
SELECT 
    '初始化时间' as '项目',
    DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') as '值'
UNION ALL
SELECT 
    '总表数量' as '项目',
    CAST(COUNT(*) AS CHAR) as '值'
FROM information_schema.tables WHERE table_schema = 'campus_management_db'
UNION ALL
SELECT
    '总记录数量' as '项目',
    CAST((
        SELECT SUM(table_rows)
        FROM information_schema.tables
        WHERE table_schema = 'campus_management_db'
    ) AS CHAR) as '值'
UNION ALL
SELECT
    '数据库大小(MB)' as '项目',
    CAST(ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS CHAR) as '值'
FROM information_schema.tables WHERE table_schema = 'campus_management_db';

SELECT '' as '';
SELECT '状态' as '', '说明' as '';
SELECT '数据库初始化完成！' as '', '请检查以上统计信息确认数据正确性' as '';

SELECT '' as '';
SELECT '重要提示：' as '';
SELECT '1. 请检查上述数据验证报告确认数据正确性' as '';
SELECT '2. 默认管理员账户：admin001，密码：123456' as '';
SELECT '3. 所有用户默认密码：123456（已加密存储）' as '';
SELECT '4. 建议在生产环境中修改默认密码' as '';
SELECT '5. 数据库连接信息请查看application.yml配置' as '';

SELECT '' as '';
SELECT '快速测试查询：' as '';

SELECT 
    '用户统计' as '测试项目',
    COUNT(*) as '实际数量'
FROM tb_user WHERE deleted = 0
UNION ALL
SELECT 
    '角色分配' as '测试项目',
    COUNT(*) as '实际数量'
FROM tb_user_role WHERE deleted = 0
UNION ALL
SELECT 
    '学院数据' as '测试项目',
    COUNT(*) as '实际数量'
FROM tb_department WHERE deleted = 0
UNION ALL
SELECT 
    '班级数据' as '测试项目',
    COUNT(*) as '实际数量'
FROM tb_school_class WHERE deleted = 0
UNION ALL
SELECT 
    '课程数据' as '测试项目',
    COUNT(*) as '实际数量'
FROM tb_course WHERE deleted = 0;

SELECT '' as '';
SELECT '配置说明：' as '';

SELECT 
    'URL配置' as '配置项目',
    'spring.datasource.url=jdbc:mysql://localhost:3306/campus_management_db?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai' as '配置值'
UNION ALL
SELECT 
    '用户名配置' as '配置项目',
    'spring.datasource.username=root' as '配置值'
UNION ALL
SELECT 
    '密码配置' as '配置项目',
    'spring.datasource.password=xiaoxiao123' as '配置值'
UNION ALL
SELECT 
    '驱动配置' as '配置项目',
    'spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver' as '配置值';

SELECT '' as '';
SELECT '状态' as '', '说明' as '', '完成时间' as '';
SELECT '🎉 数据库初始化成功完成！' as '', '系统已准备就绪，可以启动应用程序' as '', DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s') as '';
