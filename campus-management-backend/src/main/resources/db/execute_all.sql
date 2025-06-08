-- =====================================================
-- Smart Campus Management System - 完整数据库初始化脚本
-- =====================================================
-- 文件名: execute_all.sql
-- 描述: 执行所有SQL脚本，完整初始化数据库
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 兼容性: MySQL 8.0+
-- =====================================================

-- 设置执行环境
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET AUTOCOMMIT = 0;

-- 显示开始信息
SELECT '开始执行Smart Campus Management System数据库初始化...' as '执行状态';

-- =====================================================
-- 步骤1: 创建数据库
-- =====================================================
SELECT '步骤1: 创建数据库...' as '当前步骤';
SOURCE 01_create_database.sql;

-- =====================================================
-- 步骤2: 创建表结构
-- =====================================================
SELECT '步骤2: 创建表结构...' as '当前步骤';
SOURCE 02_create_tables.sql;

-- =====================================================
-- 步骤3: 插入基础数据
-- =====================================================
SELECT '步骤3: 插入基础数据（权限、角色、系统配置）...' as '当前步骤';
SOURCE 03_insert_basic_data.sql;

-- =====================================================
-- 步骤4: 插入组织架构数据
-- =====================================================
SELECT '步骤4: 插入组织架构数据（院系、班级、教室）...' as '当前步骤';
SOURCE 04_insert_organization_data.sql;

-- =====================================================
-- 步骤5: 插入用户数据
-- =====================================================
SELECT '步骤5: 插入用户数据（管理员、教师）...' as '当前步骤';
SOURCE 05_insert_user_data.sql;

-- =====================================================
-- 步骤6: 插入学生数据
-- =====================================================
SELECT '步骤6: 插入学生数据（19,000+ 学生记录）...' as '当前步骤';
SOURCE 06_insert_student_data.sql;

-- =====================================================
-- 步骤7: 插入课程数据
-- =====================================================
SELECT '步骤7: 插入课程数据（课程、课程安排、缴费项目）...' as '当前步骤';
SOURCE 07_insert_course_data.sql;

-- 恢复设置
SET FOREIGN_KEY_CHECKS = 1;
SET AUTOCOMMIT = 1;

-- =====================================================
-- 执行完成统计
-- =====================================================
SELECT 
    '数据库初始化完成！' as '执行结果',
    NOW() as '完成时间';

-- 显示数据统计
SELECT 
    '数据统计' as '类别',
    (SELECT COUNT(*) FROM tb_permission) as '权限数量',
    (SELECT COUNT(*) FROM tb_role) as '角色数量',
    (SELECT COUNT(*) FROM tb_user) as '用户总数',
    (SELECT COUNT(*) FROM tb_student) as '学生数量',
    (SELECT COUNT(*) FROM tb_department) as '院系数量',
    (SELECT COUNT(*) FROM tb_school_class) as '班级数量',
    (SELECT COUNT(*) FROM tb_classroom) as '教室数量',
    (SELECT COUNT(*) FROM tb_course) as '课程数量',
    (SELECT COUNT(*) FROM tb_fee_item) as '缴费项目数量';

-- 显示各年级学生分布
SELECT 
    grade as '年级',
    COUNT(*) as '学生人数',
    COUNT(DISTINCT major) as '专业数量',
    COUNT(DISTINCT class_id) as '班级数量'
FROM tb_student 
GROUP BY grade 
ORDER BY grade;

-- 显示各院系信息
SELECT 
    d.dept_name as '院系名称',
    COUNT(DISTINCT c.id) as '班级数量',
    COUNT(s.id) as '学生人数'
FROM tb_department d
LEFT JOIN tb_school_class c ON d.id = c.department_id
LEFT JOIN tb_student s ON c.id = s.class_id
WHERE d.dept_level = 1
GROUP BY d.id, d.dept_name
ORDER BY d.sort_order;

-- 显示课程统计
SELECT 
    course_type as '课程类型',
    COUNT(*) as '课程数量',
    AVG(credits) as '平均学分',
    SUM(credits) as '总学分'
FROM tb_course 
GROUP BY course_type;

-- 显示缴费项目统计
SELECT 
    fee_type as '费用类型',
    COUNT(*) as '项目数量',
    AVG(amount) as '平均金额',
    SUM(amount) as '总金额'
FROM tb_fee_item 
GROUP BY fee_type;

SELECT '所有数据库脚本执行完成！系统已准备就绪。' as '最终状态';
