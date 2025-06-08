-- =====================================================
-- Smart Campus Management System - 数据质量验证脚本
-- =====================================================
-- 文件名: 08_data_validation.sql
-- 描述: 验证插入数据的完整性和正确性
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 兼容性: MySQL 8.0+
-- =====================================================

USE campus_management_db;

-- 设置字符集
SET NAMES utf8mb4;

SELECT '开始数据质量验证...' as '验证状态';

-- =====================================================
-- 1. 基础数据验证
-- =====================================================

SELECT '=== 基础数据验证 ===' as '验证类别';

-- 权限数据验证
SELECT 
    '权限数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT permission_key) as '唯一权限键数量',
    COUNT(CASE WHEN is_system = 1 THEN 1 END) as '系统权限数量',
    COUNT(CASE WHEN permission_type = 'MENU' THEN 1 END) as '菜单权限数量',
    COUNT(CASE WHEN permission_type = 'BUTTON' THEN 1 END) as '按钮权限数量',
    COUNT(CASE WHEN permission_type = 'API' THEN 1 END) as 'API权限数量'
FROM tb_permission;

-- 角色数据验证
SELECT 
    '角色数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT role_key) as '唯一角色键数量',
    COUNT(CASE WHEN is_system = 1 THEN 1 END) as '系统角色数量',
    MIN(role_level) as '最小角色级别',
    MAX(role_level) as '最大角色级别'
FROM tb_role;

-- 系统配置验证
SELECT 
    '系统配置' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT config_key) as '唯一配置键数量',
    COUNT(DISTINCT config_group) as '配置分组数量',
    COUNT(CASE WHEN is_system = 1 THEN 1 END) as '系统配置数量'
FROM tb_system_config;

-- 通知模板验证
SELECT 
    '通知模板' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT template_code) as '唯一模板编码数量',
    COUNT(CASE WHEN is_system = 1 THEN 1 END) as '系统模板数量',
    COUNT(DISTINCT template_type) as '模板类型数量'
FROM tb_notification_template;

-- =====================================================
-- 2. 组织架构数据验证
-- =====================================================

SELECT '=== 组织架构数据验证 ===' as '验证类别';

-- 院系数据验证
SELECT 
    '院系数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(CASE WHEN dept_level = 1 THEN 1 END) as '一级学院数量',
    COUNT(CASE WHEN dept_level = 2 THEN 1 END) as '二级系部数量',
    COUNT(DISTINCT dept_type) as '院系类型数量'
FROM tb_department;

-- 班级数据验证
SELECT 
    '班级数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT grade) as '年级数量',
    COUNT(DISTINCT major) as '专业数量',
    COUNT(DISTINCT department_id) as '所属院系数量',
    AVG(max_students) as '平均最大学生数'
FROM tb_school_class;

-- 按年级统计班级分布
SELECT 
    grade as '年级',
    COUNT(*) as '班级数量',
    COUNT(DISTINCT major) as '专业数量',
    SUM(max_students) as '最大容量',
    AVG(max_students) as '平均班级容量'
FROM tb_school_class 
GROUP BY grade 
ORDER BY grade;

-- 教室数据验证
SELECT 
    '教室数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT building) as '教学楼数量',
    COUNT(DISTINCT room_type) as '教室类型数量',
    AVG(capacity) as '平均容量',
    SUM(capacity) as '总容量'
FROM tb_classroom;

-- 按教室类型统计
SELECT 
    room_type as '教室类型',
    COUNT(*) as '数量',
    AVG(capacity) as '平均容量',
    SUM(capacity) as '总容量'
FROM tb_classroom 
GROUP BY room_type;

-- 时间段数据验证
SELECT 
    '时间段数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT day_of_week) as '星期数量',
    COUNT(CASE WHEN day_of_week <= 5 THEN 1 END) as '工作日时间段',
    COUNT(CASE WHEN day_of_week > 5 THEN 1 END) as '周末时间段',
    COUNT(CASE WHEN is_active = 1 THEN 1 END) as '激活时间段数量'
FROM tb_time_slot;

-- =====================================================
-- 3. 用户数据验证
-- =====================================================

SELECT '=== 用户数据验证 ===' as '验证类别';

-- 用户总体统计
SELECT 
    '用户总体' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT username) as '唯一用户名数量',
    COUNT(DISTINCT email) as '唯一邮箱数量',
    COUNT(DISTINCT phone) as '唯一手机号数量',
    COUNT(CASE WHEN gender = '男' THEN 1 END) as '男性用户数量',
    COUNT(CASE WHEN gender = '女' THEN 1 END) as '女性用户数量'
FROM tb_user;

-- 按用户类型统计
SELECT 
    CASE 
        WHEN username LIKE 'admin%' OR username = 'system' THEN '管理员'
        WHEN username LIKE 'teacher%' THEN '教师'
        WHEN username LIKE 'student%' THEN '学生'
        ELSE '其他'
    END as '用户类型',
    COUNT(*) as '数量',
    COUNT(CASE WHEN gender = '男' THEN 1 END) as '男性',
    COUNT(CASE WHEN gender = '女' THEN 1 END) as '女性'
FROM tb_user 
GROUP BY 
    CASE 
        WHEN username LIKE 'admin%' OR username = 'system' THEN '管理员'
        WHEN username LIKE 'teacher%' THEN '教师'
        WHEN username LIKE 'student%' THEN '学生'
        ELSE '其他'
    END;

-- 学生数据验证
SELECT 
    '学生数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT student_no) as '唯一学号数量',
    COUNT(DISTINCT grade) as '年级数量',
    COUNT(DISTINCT major) as '专业数量',
    COUNT(DISTINCT class_id) as '班级数量',
    COUNT(CASE WHEN student_status = 'ENROLLED' THEN 1 END) as '在读学生数量'
FROM tb_student;

-- 按年级和专业统计学生分布
SELECT 
    grade as '年级',
    major as '专业',
    COUNT(*) as '学生数量',
    COUNT(CASE WHEN s.gender = '男' THEN 1 END) as '男生数量',
    COUNT(CASE WHEN s.gender = '女' THEN 1 END) as '女生数量'
FROM tb_student st
JOIN tb_user s ON st.user_id = s.id
GROUP BY grade, major 
ORDER BY grade, major;

-- 用户角色关联验证
SELECT 
    '用户角色关联' as '数据类型',
    COUNT(*) as '总关联数量',
    COUNT(DISTINCT user_id) as '有角色用户数量',
    COUNT(DISTINCT role_id) as '被分配角色数量'
FROM tb_user_role;

-- =====================================================
-- 4. 课程数据验证
-- =====================================================

SELECT '=== 课程数据验证 ===' as '验证类别';

-- 课程数据验证
SELECT 
    '课程数据' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT course_code) as '唯一课程编码数量',
    COUNT(CASE WHEN course_type = 'REQUIRED' THEN 1 END) as '必修课数量',
    COUNT(CASE WHEN course_type = 'ELECTIVE' THEN 1 END) as '选修课数量',
    AVG(credits) as '平均学分',
    SUM(credits) as '总学分'
FROM tb_course;

-- 按院系统计课程分布
SELECT 
    d.dept_name as '开课院系',
    COUNT(c.id) as '课程数量',
    AVG(c.credits) as '平均学分',
    COUNT(CASE WHEN c.course_type = 'REQUIRED' THEN 1 END) as '必修课数量',
    COUNT(CASE WHEN c.course_type = 'ELECTIVE' THEN 1 END) as '选修课数量'
FROM tb_course c
JOIN tb_department d ON c.department_id = d.id
GROUP BY d.dept_name
ORDER BY COUNT(c.id) DESC;

-- 课程安排验证
SELECT 
    '课程安排' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT course_id) as '已安排课程数量',
    COUNT(DISTINCT teacher_id) as '授课教师数量',
    COUNT(DISTINCT classroom_id) as '使用教室数量',
    COUNT(DISTINCT time_slot_id) as '使用时间段数量'
FROM tb_course_schedule;

-- 缴费项目验证
SELECT 
    '缴费项目' as '数据类型',
    COUNT(*) as '总数量',
    COUNT(DISTINCT item_code) as '唯一项目编码数量',
    COUNT(DISTINCT fee_type) as '费用类型数量',
    AVG(amount) as '平均金额',
    SUM(amount) as '总金额',
    COUNT(CASE WHEN is_mandatory = 1 THEN 1 END) as '必缴项目数量'
FROM tb_fee_item;

-- =====================================================
-- 5. 数据完整性检查
-- =====================================================

SELECT '=== 数据完整性检查 ===' as '验证类别';

-- 检查外键完整性
SELECT '外键完整性检查' as '检查项目';

-- 学生用户关联检查
SELECT 
    '学生-用户关联' as '检查项',
    COUNT(*) as '学生记录数',
    COUNT(u.id) as '关联用户数',
    COUNT(*) - COUNT(u.id) as '缺失用户数'
FROM tb_student s
LEFT JOIN tb_user u ON s.user_id = u.id;

-- 班级-院系关联检查
SELECT 
    '班级-院系关联' as '检查项',
    COUNT(*) as '班级记录数',
    COUNT(d.id) as '关联院系数',
    COUNT(*) - COUNT(d.id) as '缺失院系数'
FROM tb_school_class c
LEFT JOIN tb_department d ON c.department_id = d.id;

-- 课程-教师关联检查
SELECT 
    '课程-教师关联' as '检查项',
    COUNT(*) as '课程记录数',
    COUNT(u.id) as '关联教师数',
    COUNT(*) - COUNT(u.id) as '缺失教师数'
FROM tb_course c
LEFT JOIN tb_user u ON c.teacher_id = u.id;

-- =====================================================
-- 6. 数据质量评估
-- =====================================================

SELECT '=== 数据质量评估 ===' as '验证类别';

-- 用户名唯一性检查
SELECT 
    '用户名唯一性' as '检查项',
    COUNT(*) as '总用户数',
    COUNT(DISTINCT username) as '唯一用户名数',
    CASE WHEN COUNT(*) = COUNT(DISTINCT username) THEN '通过' ELSE '失败' END as '检查结果'
FROM tb_user;

-- 邮箱唯一性检查
SELECT 
    '邮箱唯一性' as '检查项',
    COUNT(*) as '总用户数',
    COUNT(DISTINCT email) as '唯一邮箱数',
    CASE WHEN COUNT(*) = COUNT(DISTINCT email) THEN '通过' ELSE '失败' END as '检查结果'
FROM tb_user;

-- 手机号唯一性检查
SELECT 
    '手机号唯一性' as '检查项',
    COUNT(*) as '总用户数',
    COUNT(DISTINCT phone) as '唯一手机号数',
    CASE WHEN COUNT(*) = COUNT(DISTINCT phone) THEN '通过' ELSE '失败' END as '检查结果'
FROM tb_user;

-- 学号唯一性检查
SELECT 
    '学号唯一性' as '检查项',
    COUNT(*) as '总学生数',
    COUNT(DISTINCT student_no) as '唯一学号数',
    CASE WHEN COUNT(*) = COUNT(DISTINCT student_no) THEN '通过' ELSE '失败' END as '检查结果'
FROM tb_student;

-- 最终验证总结
SELECT 
    '数据验证完成！' as '验证状态',
    NOW() as '验证时间',
    (SELECT COUNT(*) FROM tb_user) as '总用户数',
    (SELECT COUNT(*) FROM tb_student) as '学生数',
    (SELECT COUNT(*) FROM tb_course) as '课程数',
    (SELECT COUNT(*) FROM tb_school_class) as '班级数',
    (SELECT COUNT(*) FROM tb_classroom) as '教室数';
