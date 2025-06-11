-- =====================================================
-- Smart Campus Management System - 优化数据生成脚本
-- 文件: 03_insert_large_data.sql
-- 描述: 使用高效批量插入方法生成大规模测试数据
-- 数据规模: 15,000用户 + 200班级 + 完整业务数据
-- 版本: 4.0.0 (优化版)
-- 创建时间: 2025-06-08
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 使用数据库
USE campus_management_db;

-- 设置优化参数
SET autocommit = 0;
SET unique_checks = 0;
SET foreign_key_checks = 0;
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- 开始事务
START TRANSACTION;

SELECT '=== Smart Campus Management System 优化数据生成开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 清理现有数据 (保留基础配置)
-- =====================================================

SELECT '步骤1: 清理现有业务数据...' as '状态', NOW() as '时间';

-- 清理业务数据表 (保留基础配置表)
TRUNCATE TABLE tb_payment_record;
TRUNCATE TABLE tb_assignment_submission;
TRUNCATE TABLE tb_exam_record;
TRUNCATE TABLE tb_attendance;
TRUNCATE TABLE tb_grade;
TRUNCATE TABLE tb_course_selection;
TRUNCATE TABLE tb_course_schedule;
TRUNCATE TABLE tb_assignment;
TRUNCATE TABLE tb_exam;
TRUNCATE TABLE tb_course;
TRUNCATE TABLE tb_student;
TRUNCATE TABLE tb_class;
TRUNCATE TABLE tb_user_role;
DELETE FROM tb_user WHERE id > 1; -- 保留默认管理员

-- 重置自增ID
ALTER TABLE tb_user AUTO_INCREMENT = 2;
ALTER TABLE tb_class AUTO_INCREMENT = 1;
ALTER TABLE tb_student AUTO_INCREMENT = 1;
ALTER TABLE tb_course AUTO_INCREMENT = 1;
ALTER TABLE tb_course_schedule AUTO_INCREMENT = 1;
ALTER TABLE tb_exam AUTO_INCREMENT = 1;
ALTER TABLE tb_assignment AUTO_INCREMENT = 1;
ALTER TABLE tb_course_selection AUTO_INCREMENT = 1;
ALTER TABLE tb_grade AUTO_INCREMENT = 1;
ALTER TABLE tb_attendance AUTO_INCREMENT = 1;
ALTER TABLE tb_exam_record AUTO_INCREMENT = 1;
ALTER TABLE tb_assignment_submission AUTO_INCREMENT = 1;
ALTER TABLE tb_payment_record AUTO_INCREMENT = 1;
ALTER TABLE tb_user_role AUTO_INCREMENT = 1;

SELECT '✓ 数据清理完成' as '状态', NOW() as '时间';

-- =====================================================
-- 2. 创建高效的临时表和预生成数据
-- =====================================================

SELECT '步骤2: 创建临时表和预生成数据...' as '状态', NOW() as '时间';

-- 创建序列号临时表 (使用更高效的方法)
DROP TEMPORARY TABLE IF EXISTS temp_numbers;
CREATE TEMPORARY TABLE temp_numbers (
    num INT PRIMARY KEY
) ENGINE=MEMORY;

-- 设置递归深度限制并生成序列号
SET SESSION cte_max_recursion_depth = 20000;

-- 使用递归CTE生成序列号 (MySQL 8.0+)
INSERT INTO temp_numbers (num)
WITH RECURSIVE number_series AS (
    SELECT 1 as num
    UNION ALL
    SELECT num + 1 FROM number_series WHERE num < 15000
)
SELECT num FROM number_series;

-- 创建预生成的姓名数据表
DROP TEMPORARY TABLE IF EXISTS temp_names;
CREATE TEMPORARY TABLE temp_names (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(20),
    gender VARCHAR(5)
) ENGINE=MEMORY;

-- 批量插入预生成的姓名数据
INSERT INTO temp_names (full_name, gender) VALUES
-- 男性姓名
('王伟', '男'), ('李强', '男'), ('张磊', '男'), ('刘军', '男'), ('陈勇', '男'),
('杨涛', '男'), ('黄明', '男'), ('赵超', '男'), ('周亮', '男'), ('吴华', '男'),
('徐建', '男'), ('孙国', '男'), ('朱峰', '男'), ('马辉', '男'), ('胡成', '男'),
('郭龙', '男'), ('林文', '男'), ('何斌', '男'), ('高刚', '男'), ('梁健', '男'),
('郑世', '男'), ('罗广', '男'), ('宋志', '男'), ('谢良', '男'), ('唐海', '男'),
('韩山', '男'), ('曹仁', '男'), ('许波', '男'), ('邓宁', '男'), ('萧贵', '男'),
('冯福', '男'), ('曾生', '男'), ('程元', '男'), ('蔡全', '男'), ('彭胜', '男'),
('潘学', '男'), ('袁祥', '男'), ('于才', '男'), ('董发', '男'), ('余武', '男'),
('苏新', '男'), ('叶利', '男'), ('吕清', '男'), ('魏飞', '男'), ('蒋彬', '男'),
('田富', '男'), ('杜顺', '男'), ('丁信', '男'), ('沈子', '男'), ('姜杰', '男'),
-- 女性姓名
('王芳', '女'), ('李娜', '女'), ('张静', '女'), ('刘敏', '女'), ('陈雪', '女'),
('杨丽', '女'), ('黄美', '女'), ('赵娟', '女'), ('周英', '女'), ('吴华', '女'),
('徐慧', '女'), ('孙巧', '女'), ('朱淑', '女'), ('马惠', '女'), ('胡珠', '女'),
('郭翠', '女'), ('林雅', '女'), ('何芝', '女'), ('高玉', '女'), ('梁萍', '女'),
('郑红', '女'), ('罗娥', '女'), ('宋玲', '女'), ('谢芬', '女'), ('唐燕', '女'),
('韩彩', '女'), ('曹春', '女'), ('许菊', '女'), ('邓兰', '女'), ('萧凤', '女'),
('冯洁', '女'), ('曾梅', '女'), ('程琳', '女'), ('蔡素', '女'), ('彭云', '女'),
('潘莲', '女'), ('袁真', '女'), ('于环', '女'), ('董荣', '女'), ('余爱', '女'),
('苏妹', '女'), ('叶霞', '女'), ('吕香', '女'), ('魏月', '女'), ('蒋莺', '女'),
('田媛', '女'), ('杜艳', '女'), ('丁瑞', '女'), ('沈凡', '女'), ('姜佳', '女'),
-- 通用姓名
('王德', '男'), ('李仁', '女'), ('张义', '男'), ('刘礼', '女'), ('陈智', '男'),
('杨信', '女'), ('黄忠', '男'), ('赵孝', '女'), ('周廉', '男'), ('吴耻', '女'),
('徐温', '男'), ('孙良', '女'), ('朱恭', '男'), ('马俭', '女'), ('胡让', '男'),
('郭谦', '女'), ('林和', '男'), ('何平', '女'), ('高安', '男'), ('梁宁', '女'),
('郑静', '男'), ('罗淡', '女'), ('宋雅', '男'), ('谢正', '女'), ('唐福', '男'),
('韩禄', '女'), ('曹寿', '男'), ('许喜', '女'), ('邓财', '男'), ('萧宝', '女');

-- 创建专业数据临时表
DROP TEMPORARY TABLE IF EXISTS temp_majors;
CREATE TEMPORARY TABLE temp_majors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    major_name VARCHAR(100),
    department_id INT
) ENGINE=MEMORY;

INSERT INTO temp_majors (major_name, department_id) VALUES
-- 计算机科学与技术学院专业 (department_id = 1)
('计算机科学与技术', 1), ('软件工程', 1), ('网络工程', 1), ('信息安全', 1), ('数据科学与大数据技术', 1),
-- 机械工程学院专业 (department_id = 2)
('机械设计制造及其自动化', 2), ('机械电子工程', 2), ('车辆工程', 2), ('工业设计', 2), ('智能制造工程', 2),
-- 电子信息工程学院专业 (department_id = 3)
('电子信息工程', 3), ('通信工程', 3), ('自动化', 3), ('电气工程及其自动化', 3), ('光电信息科学与工程', 3),
-- 经济管理学院专业 (department_id = 4)
('工商管理', 4), ('市场营销', 4), ('国际经济与贸易', 4), ('会计学', 4), ('财务管理', 4),
-- 外国语学院专业 (department_id = 5)
('英语', 5), ('日语', 5), ('德语', 5), ('法语', 5), ('俄语', 5),
-- 数学与统计学院专业 (department_id = 6)
('数学与应用数学', 6), ('统计学', 6), ('信息与计算科学', 6), ('应用统计学', 6), ('金融数学', 6),
-- 物理与光电工程学院专业 (department_id = 7)
('应用物理学', 7), ('光电信息科学与工程', 7), ('材料物理', 7), ('新能源科学与工程', 7), ('核工程与核技术', 7),
-- 化学与化工学院专业 (department_id = 8)
('化学', 8), ('化学工程与工艺', 8), ('应用化学', 8), ('材料化学', 8), ('制药工程', 8),
-- 生命科学学院专业 (department_id = 9)
('生物科学', 9), ('生物技术', 9), ('生物工程', 9), ('生态学', 9), ('生物信息学', 9),
-- 材料科学与工程学院专业 (department_id = 10)
('材料科学与工程', 10), ('高分子材料与工程', 10), ('复合材料与工程', 10), ('纳米材料与技术', 10), ('功能材料', 10);

-- 创建课程名称临时表
DROP TEMPORARY TABLE IF EXISTS temp_course_names;
CREATE TEMPORARY TABLE temp_course_names (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100),
    course_type VARCHAR(20),
    credits DECIMAL(3,1)
) ENGINE=MEMORY;

INSERT INTO temp_course_names (course_name, course_type, credits) VALUES
-- 基础课程
('高等数学', '必修课', 4.0), ('线性代数', '必修课', 3.0), ('概率论与数理统计', '必修课', 3.0),
('大学物理', '必修课', 4.0), ('大学化学', '必修课', 3.0), ('大学英语', '必修课', 4.0),
('思想道德修养与法律基础', '必修课', 2.0), ('马克思主义基本原理', '必修课', 3.0),
('毛泽东思想和中国特色社会主义理论体系概论', '必修课', 4.0), ('中国近现代史纲要', '必修课', 2.0),
('体育', '必修课', 1.0), ('军事理论', '必修课', 1.0), ('大学语文', '选修课', 2.0),
-- 专业基础课程
('计算机基础', '专业必修', 3.0), ('程序设计基础', '专业必修', 4.0), ('数据结构', '专业必修', 4.0),
('算法分析与设计', '专业必修', 3.0), ('计算机组成原理', '专业必修', 4.0), ('操作系统', '专业必修', 4.0),
('数据库系统原理', '专业必修', 4.0), ('计算机网络', '专业必修', 4.0), ('软件工程', '专业必修', 3.0),
('编译原理', '专业选修', 3.0), ('人工智能', '专业选修', 3.0), ('机器学习', '专业选修', 3.0),
-- 工程类课程
('电路分析', '专业必修', 4.0), ('模拟电子技术', '专业必修', 4.0), ('数字电子技术', '专业必修', 4.0),
('信号与系统', '专业必修', 3.0), ('通信原理', '专业必修', 4.0), ('自动控制原理', '专业必修', 4.0),
('机械制图', '专业必修', 3.0), ('理论力学', '专业必修', 4.0), ('材料力学', '专业必修', 4.0),
('机械原理', '专业必修', 3.0), ('机械设计', '专业必修', 4.0), ('工程材料', '专业必修', 3.0),
-- 管理类课程
('管理学原理', '专业必修', 3.0), ('微观经济学', '专业必修', 3.0), ('宏观经济学', '专业必修', 3.0),
('会计学原理', '专业必修', 3.0), ('财务管理', '专业必修', 3.0), ('市场营销', '专业必修', 3.0),
-- 实践课程
('课程设计', '实践课', 2.0), ('专业实习', '实践课', 4.0), ('毕业设计', '实践课', 8.0),
('创新创业实践', '实践课', 2.0), ('社会实践', '实践课', 1.0), ('实验课程', '实践课', 1.0);

SELECT '✓ 临时表创建完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 高效批量生成用户数据
-- =====================================================

SELECT '步骤3: 批量生成用户数据...' as '状态', NOW() as '时间';

-- 生成50个管理员用户 (使用单个INSERT语句)
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('admin', LPAD(n.num + 100000, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('admin', LPAD(n.num + 100000, 6, '0'), '@university.edu.cn') as email,
    tn.full_name as real_name,
    CONCAT('1', 3 + (n.num % 6), LPAD(10000000 + (n.num * 137) % 90000000, 8, '0')) as phone,
    tn.gender as gender,
    CASE WHEN n.num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_names tn ON tn.id = (n.num % 100) + 1
WHERE n.num <= 50;

-- 生成500个教师用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('teacher', LPAD(n.num + 200000, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('teacher', LPAD(n.num + 200000, 6, '0'), '@university.edu.cn') as email,
    tn.full_name as real_name,
    CONCAT('1', 3 + (n.num % 6), LPAD(10000000 + (n.num * 139) % 90000000, 8, '0')) as phone,
    tn.gender as gender,
    CASE WHEN n.num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_names tn ON tn.id = (n.num % 100) + 1
WHERE n.num <= 500;

-- 生成200个班主任用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('classteacher', LPAD(n.num + 300000, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('classteacher', LPAD(n.num + 300000, 6, '0'), '@university.edu.cn') as email,
    tn.full_name as real_name,
    CONCAT('1', 3 + (n.num % 6), LPAD(10000000 + (n.num * 141) % 90000000, 8, '0')) as phone,
    tn.gender as gender,
    CASE WHEN n.num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_names tn ON tn.id = (n.num % 100) + 1
WHERE n.num <= 200;

-- 生成10000个学生用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('student', LPAD(n.num + 400000, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('student', LPAD(n.num + 400000, 6, '0'), '@university.edu.cn') as email,
    tn.full_name as real_name,
    CONCAT('1', 3 + (n.num % 6), LPAD(10000000 + (n.num * 143) % 90000000, 8, '0')) as phone,
    tn.gender as gender,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_names tn ON tn.id = (n.num % 100) + 1
WHERE n.num <= 10000;

-- 生成4250个家长用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('parent', LPAD(n.num + 500000, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('parent', LPAD(n.num + 500000, 6, '0'), '@university.edu.cn') as email,
    tn.full_name as real_name,
    CONCAT('1', 3 + (n.num % 6), LPAD(10000000 + (n.num * 147) % 90000000, 8, '0')) as phone,
    tn.gender as gender,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_names tn ON tn.id = (n.num % 100) + 1
WHERE n.num <= 4250;

SELECT '✓ 用户数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 高效批量分配用户角色
-- =====================================================

SELECT '步骤4: 批量分配用户角色...' as '状态', NOW() as '时间';

-- 批量分配管理员角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
CROSS JOIN tb_role r
WHERE u.username LIKE 'admin%' AND u.deleted = 0 AND r.role_key = 'ROLE_ADMIN';

-- 批量分配教师角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
CROSS JOIN tb_role r
WHERE u.username LIKE 'teacher%' AND u.deleted = 0 AND r.role_key = 'ROLE_TEACHER';

-- 批量分配班主任角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
CROSS JOIN tb_role r
WHERE u.username LIKE 'classteacher%' AND u.deleted = 0 AND r.role_key = 'ROLE_CLASS_TEACHER';

-- 批量分配学生角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
CROSS JOIN tb_role r
WHERE u.username LIKE 'student%' AND u.deleted = 0 AND r.role_key = 'ROLE_STUDENT';

-- 批量分配家长角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
CROSS JOIN tb_role r
WHERE u.username LIKE 'parent%' AND u.deleted = 0 AND r.role_key = 'ROLE_PARENT';

SELECT '✓ 用户角色分配完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 高效批量生成班级数据
-- =====================================================

SELECT '步骤5: 批量生成班级数据...' as '状态', NOW() as '时间';

-- 生成200个班级 (每个专业4个班级，50个专业)
INSERT INTO tb_class (class_name, class_code, department_id, major, grade, enrollment_year, head_teacher_id,
                     max_capacity, student_count, class_status, enrollment_date, expected_graduation_date,
                     academic_year, status, deleted)
SELECT
    CONCAT(m.major_name, FLOOR((n.num - 1) / 50) + 2021, '级', ((n.num - 1) % 4) + 1, '班') as class_name,
    CONCAT('C', LPAD(n.num, 6, '0')) as class_code,
    m.department_id as department_id,
    m.major_name as major,
    CONCAT(FLOOR((n.num - 1) / 50) + 2021, '级') as grade,
    FLOOR((n.num - 1) / 50) + 2021 as enrollment_year,
    ct.id as head_teacher_id,
    50 as max_capacity,
    0 as student_count,
    1 as class_status,
    DATE(CONCAT(FLOOR((n.num - 1) / 50) + 2021, '-09-01')) as enrollment_date,
    DATE(CONCAT(FLOOR((n.num - 1) / 50) + 2025, '-06-30')) as expected_graduation_date,
    CONCAT(FLOOR((n.num - 1) / 50) + 2021, '-', FLOOR((n.num - 1) / 50) + 2025) as academic_year,
    1 as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_majors m ON m.id = ((n.num - 1) % 50) + 1
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'classteacher%' AND deleted = 0) ct ON ct.rn = n.num
WHERE n.num <= 200;

SELECT '✓ 班级数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 高效批量生成学生数据
-- =====================================================

SELECT '步骤6: 批量生成学生数据...' as '状态', NOW() as '时间';

-- 生成10000个学生记录
INSERT INTO tb_student (user_id, student_no, grade, major, class_id, enrollment_year, enrollment_date,
                       graduation_date, academic_status, student_type, training_mode, academic_system,
                       current_semester, total_credits, earned_credits, gpa, emergency_contact,
                       emergency_phone, dormitory_info, status, deleted)
SELECT
    u.id as user_id,
    CONCAT('S', LPAD(n.num, 6, '0')) as student_no,
    c.grade as grade,
    c.major as major,
    c.id as class_id,
    c.enrollment_year as enrollment_year,
    c.enrollment_date as enrollment_date,
    c.expected_graduation_date as graduation_date,
    CASE
        WHEN n.num % 100 = 0 THEN 2  -- 休学
        WHEN n.num % 200 = 0 THEN 4  -- 毕业
        ELSE 1  -- 在读
    END as academic_status,
    CASE WHEN n.num % 100 = 0 THEN '交换生' ELSE '普通学生' END as student_type,
    '全日制' as training_mode,
    4 as academic_system,
    CASE
        WHEN c.enrollment_year = 2024 THEN '第一学期'
        WHEN c.enrollment_year = 2023 THEN '第三学期'
        WHEN c.enrollment_year = 2022 THEN '第五学期'
        ELSE '第七学期'
    END as current_semester,
    CASE
        WHEN c.enrollment_year = 2024 THEN 30 + (n.num % 20)
        WHEN c.enrollment_year = 2023 THEN 60 + (n.num % 30)
        WHEN c.enrollment_year = 2022 THEN 90 + (n.num % 40)
        ELSE 120 + (n.num % 50)
    END as total_credits,
    CASE
        WHEN c.enrollment_year = 2024 THEN 20 + (n.num % 15)
        WHEN c.enrollment_year = 2023 THEN 45 + (n.num % 25)
        WHEN c.enrollment_year = 2022 THEN 75 + (n.num % 35)
        ELSE 110 + (n.num % 45)
    END as earned_credits,
    ROUND(2.0 + (n.num % 200) / 100.0, 2) as gpa,
    tn.full_name as emergency_contact,
    CONCAT('1', 3 + (n.num % 6), LPAD(10000000 + (n.num * 151) % 90000000, 8, '0')) as emergency_phone,
    CONCAT('宿舍楼', FLOOR((n.num - 1) / 1000) + 1, '号', FLOOR(((n.num - 1) % 1000) / 100) + 1, '层', LPAD(((n.num - 1) % 100) + 1, 3, '0'), '室') as dormitory_info,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'student%' AND deleted = 0) u ON u.rn = n.num
JOIN tb_class c ON c.id = ((n.num - 1) % 200) + 1
JOIN temp_names tn ON tn.id = (n.num % 100) + 1
WHERE n.num <= 10000;

-- 更新班级的当前学生数
UPDATE tb_class c SET student_count = (
    SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id AND s.deleted = 0
);

SELECT '✓ 学生数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 7. 高效批量生成课程数据
-- =====================================================

SELECT '步骤7: 批量生成课程数据...' as '状态', NOW() as '时间';

-- 生成1000门课程
INSERT INTO tb_course (course_name, course_code, department_id, course_type, credits, hours, theory_hours,
                      practice_hours, description, assessment_method, max_students, current_students,
                      is_elective, status, deleted)
SELECT
    tcn.course_name as course_name,
    CONCAT('C', LPAD(n.num, 6, '0')) as course_code,
    ((n.num - 1) % 10) + 1 as department_id,
    tcn.course_type as course_type,
    tcn.credits as credits,
    CAST(tcn.credits * 16 AS UNSIGNED) as hours,
    CASE
        WHEN tcn.course_type = '实践课' THEN CAST(tcn.credits * 4 AS UNSIGNED)
        ELSE CAST(tcn.credits * 12 AS UNSIGNED)
    END as theory_hours,
    CASE
        WHEN tcn.course_type = '实践课' THEN CAST(tcn.credits * 12 AS UNSIGNED)
        ELSE CAST(tcn.credits * 4 AS UNSIGNED)
    END as practice_hours,
    CONCAT(tcn.course_name, '课程描述') as description,
    CASE
        WHEN tcn.course_type = '实践课' THEN '实践考核'
        WHEN tcn.course_type = '选修课' THEN '考查'
        ELSE '考试'
    END as assessment_method,
    CASE
        WHEN tcn.course_type = '实践课' THEN 30
        WHEN tcn.course_type = '专业选修' THEN 50
        WHEN tcn.course_type = '专业必修' THEN 80
        ELSE 120
    END as max_students,
    0 as current_students,
    CASE WHEN tcn.course_type IN ('选修课', '专业选修') THEN 1 ELSE 0 END as is_elective,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_course_names tcn ON tcn.id = ((n.num - 1) % 50) + 1
WHERE n.num <= 1000;

SELECT '✓ 课程数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 8. 高效批量生成课程表数据
-- =====================================================

SELECT '步骤8: 批量生成课程表数据...' as '状态', NOW() as '时间';

-- 为每门课程生成2-3个课程表时间段
INSERT INTO tb_course_schedule (course_id, teacher_id, classroom_id, time_slot_id, day_of_week,
                               week_start, week_end, academic_year, semester, status, deleted)
SELECT
    c.id as course_id,
    t.id as teacher_id,
    ((n.num - 1) % 100) + 1 as classroom_id,
    ((n.num - 1) % 10) + 1 as time_slot_id,
    ((n.num - 1) % 5) + 1 as day_of_week,
    1 as week_start,
    18 as week_end,
    '2024' as academic_year,
    CASE WHEN n.num % 2 = 1 THEN '第一学期' ELSE '第二学期' END as semester,
    1 as status,
    0 as deleted
FROM temp_numbers n
JOIN tb_course c ON c.id = ((n.num - 1) % 1000) + 1
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0) t ON t.rn = ((n.num - 1) % 500) + 1
WHERE n.num <= 2500;

SELECT '✓ 课程表数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 9. 高效批量生成选课数据
-- =====================================================

SELECT '步骤9: 批量生成选课数据...' as '状态', NOW() as '时间';

-- 为每个学生生成5-8门选课记录 (使用高效的批量插入，避免重复)
INSERT INTO tb_course_selection (student_id, course_id, semester, academic_year, selection_time,
                                selection_status, selection_type, is_retake, status, deleted)
SELECT DISTINCT
    s.id as student_id,
    c.id as course_id,
    CASE WHEN (s.id + c.id) % 2 = 1 THEN '第一学期' ELSE '第二学期' END as semester,
    '2024' as academic_year,
    DATE_SUB(NOW(), INTERVAL FLOOR((s.id + c.id) / 100) DAY) as selection_time,
    CASE
        WHEN (s.id + c.id) % 20 = 0 THEN 'withdrawn'
        WHEN (s.id + c.id) % 50 = 0 THEN 'pending'
        ELSE 'selected'
    END as selection_status,
    CASE
        WHEN (s.id + c.id) % 100 = 0 THEN 'retake'
        WHEN (s.id + c.id) % 50 = 0 THEN 'makeup'
        ELSE 'normal'
    END as selection_type,
    CASE WHEN (s.id + c.id) % 100 = 0 THEN 1 ELSE 0 END as is_retake,
    1 as status,
    0 as deleted
FROM tb_student s
CROSS JOIN tb_course c
WHERE s.deleted = 0 AND c.deleted = 0
AND (s.id + c.id) % 4 = 0  -- 每个学生选择25%的课程
LIMIT 50000;

-- 更新课程的当前学生数
UPDATE tb_course c SET current_students = (
    SELECT COUNT(*) FROM tb_course_selection cs
    WHERE cs.course_id = c.id AND cs.selection_status = 'selected' AND cs.deleted = 0
);

SELECT '✓ 选课数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 10. 高效批量生成成绩数据
-- =====================================================

SELECT '步骤10: 批量生成成绩数据...' as '状态', NOW() as '时间';

-- 为已确认的选课记录生成成绩 (避免重复)
INSERT INTO tb_grade (student_id, course_id, teacher_id, class_id, semester, academic_year,
                     usual_score, midterm_score, final_score, total_score, grade_level, gpa,
                     credit, grade_status, is_passed, record_time, status, deleted)
SELECT DISTINCT
    cs.student_id,
    cs.course_id,
    sch.teacher_id,
    s.class_id,
    cs.semester,
    cs.academic_year,
    ROUND(70 + (cs.student_id * 17 + cs.course_id * 13) % 30, 2) as usual_score,
    ROUND(60 + (cs.student_id * 19 + cs.course_id * 11) % 40, 2) as midterm_score,
    ROUND(50 + (cs.student_id * 23 + cs.course_id * 7) % 50, 2) as final_score,
    ROUND(
        (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
        (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
        (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
    ) as total_score,
    CASE
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 90 THEN 'A'
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 80 THEN 'B'
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 70 THEN 'C'
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 60 THEN 'D'
        ELSE 'F'
    END as grade_level,
    CASE
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 90 THEN 4.0
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 80 THEN 3.0
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 70 THEN 2.0
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 60 THEN 1.0
        ELSE 0.0
    END as gpa,
    c.credits as credit,
    'confirmed' as grade_status,
    CASE
        WHEN ROUND(
            (70 + (cs.student_id * 17 + cs.course_id * 13) % 30) * 0.3 +
            (60 + (cs.student_id * 19 + cs.course_id * 11) % 40) * 0.3 +
            (50 + (cs.student_id * 23 + cs.course_id * 7) % 50) * 0.4, 2
        ) >= 60 THEN TRUE
        ELSE FALSE
    END as is_passed,
    DATE_SUB(NOW(), INTERVAL FLOOR((cs.student_id + cs.course_id) / 100) DAY) as record_time,
    1 as status,
    0 as deleted
FROM tb_course_selection cs
JOIN tb_course c ON c.id = cs.course_id
JOIN tb_student s ON s.id = cs.student_id
LEFT JOIN tb_course_schedule sch ON sch.course_id = cs.course_id
WHERE cs.selection_status = 'selected' AND cs.deleted = 0
LIMIT 30000;

SELECT '✓ 成绩数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 11. 高效批量生成考勤数据
-- =====================================================

SELECT '步骤11: 批量生成考勤数据...' as '状态', NOW() as '时间';

-- 为学生生成考勤记录 (最近30天)
INSERT INTO tb_attendance (student_id, course_id, attendance_date, time_slot_id, attendance_status,
                          check_in_time, location, recorded_by, status, deleted)
SELECT
    ((n.num - 1) % 10000) + 1 as student_id,
    ((n.num - 1) % 1000) + 1 as course_id,
    DATE_SUB(CURDATE(), INTERVAL (n.num % 30) DAY) as attendance_date,
    ((n.num - 1) % 10) + 1 as time_slot_id,
    CASE
        WHEN n.num % 20 = 0 THEN '缺勤'
        WHEN n.num % 15 = 0 THEN '迟到'
        WHEN n.num % 25 = 0 THEN '早退'
        ELSE '正常'
    END as attendance_status,
    CASE
        WHEN n.num % 20 = 0 THEN NULL
        WHEN n.num % 15 = 0 THEN DATE_ADD(
            CONCAT(DATE_SUB(CURDATE(), INTERVAL (n.num % 30) DAY), ' 08:10:00'),
            INTERVAL (n.num % 15) MINUTE
        )
        ELSE DATE_ADD(
            CONCAT(DATE_SUB(CURDATE(), INTERVAL (n.num % 30) DAY), ' 08:00:00'),
            INTERVAL (n.num % 5) MINUTE
        )
    END as check_in_time,
    CONCAT('教学楼', FLOOR((n.num - 1) / 100) + 1, '-', LPAD(((n.num - 1) % 100) + 1, 3, '0')) as location,
    ((n.num - 1) % 500) + 1 as recorded_by,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 25000;

SELECT '✓ 考勤数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 12. 高效批量生成财务数据
-- =====================================================

SELECT '步骤12: 批量生成财务数据...' as '状态', NOW() as '时间';

-- 为学生生成缴费记录
INSERT INTO tb_payment_record (student_id, fee_item_id, payment_amount, payment_method, payment_time,
                              payment_status, transaction_id, payment_channel, receipt_number,
                              operator_id, status, deleted)
SELECT
    ((n.num - 1) % 10000) + 1 as student_id,
    ((n.num - 1) % 10) + 1 as fee_item_id,
    CASE
        WHEN n.num % 10 = 1 THEN 5000.00  -- 学费
        WHEN n.num % 10 = 2 THEN 1200.00  -- 住宿费
        WHEN n.num % 10 = 3 THEN 800.00   -- 教材费
        WHEN n.num % 10 = 4 THEN 300.00   -- 体检费
        WHEN n.num % 10 = 5 THEN 150.00   -- 保险费
        ELSE 100.00  -- 其他费用
    END as payment_amount,
    CASE
        WHEN n.num % 4 = 0 THEN '银行转账'
        WHEN n.num % 4 = 1 THEN '支付宝'
        WHEN n.num % 4 = 2 THEN '微信支付'
        ELSE '现金'
    END as payment_method,
    DATE_SUB(NOW(), INTERVAL FLOOR((n.num - 1) / 100) DAY) as payment_time,
    CASE
        WHEN n.num % 20 = 0 THEN 'pending'
        WHEN n.num % 50 = 0 THEN 'failed'
        ELSE 'completed'
    END as payment_status,
    CONCAT('TXN', LPAD(n.num, 12, '0')) as transaction_id,
    CASE
        WHEN n.num % 3 = 0 THEN '网上银行'
        WHEN n.num % 3 = 1 THEN '移动支付'
        ELSE '现场缴费'
    END as payment_channel,
    CONCAT('RCP', LPAD(n.num, 10, '0')) as receipt_number,
    ((n.num - 1) % 50) + 1 as operator_id,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 15000;

SELECT '✓ 财务数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 13. 恢复数据库设置并提交事务
-- =====================================================

SELECT '步骤13: 恢复数据库设置...' as '状态', NOW() as '时间';

-- 恢复数据库设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

-- 提交事务
COMMIT;

SELECT '✓ 数据库设置恢复完成' as '状态', NOW() as '时间';

-- =====================================================
-- 14. 数据统计和验证
-- =====================================================

SELECT '=== 数据生成统计报告 ===' as '状态', NOW() as '时间';

SELECT
    '用户数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT(
        '管理员: ', SUM(CASE WHEN username LIKE 'admin%' THEN 1 ELSE 0 END), ', ',
        '教师: ', SUM(CASE WHEN username LIKE 'teacher%' THEN 1 ELSE 0 END), ', ',
        '班主任: ', SUM(CASE WHEN username LIKE 'classteacher%' THEN 1 ELSE 0 END), ', ',
        '学生: ', SUM(CASE WHEN username LIKE 'student%' THEN 1 ELSE 0 END), ', ',
        '家长: ', SUM(CASE WHEN username LIKE 'parent%' THEN 1 ELSE 0 END)
    ) as '详细分布'
FROM tb_user WHERE deleted = 0

UNION ALL

SELECT
    '班级数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT('平均学生数: ', ROUND(AVG(student_count), 1)) as '详细分布'
FROM tb_class WHERE deleted = 0

UNION ALL

SELECT
    '学生数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT('平均GPA: ', ROUND(AVG(gpa), 2)) as '详细分布'
FROM tb_student WHERE deleted = 0

UNION ALL

SELECT
    '课程数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT('平均选课人数: ', ROUND(AVG(current_students), 1)) as '详细分布'
FROM tb_course WHERE deleted = 0

UNION ALL

SELECT
    '选课数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT(
        '已确认: ', SUM(CASE WHEN selection_status = 'approved' THEN 1 ELSE 0 END), ', ',
        '待审核: ', SUM(CASE WHEN selection_status = 'pending' THEN 1 ELSE 0 END), ', ',
        '已退课: ', SUM(CASE WHEN selection_status = 'withdrawn' THEN 1 ELSE 0 END)
    ) as '详细分布'
FROM tb_course_selection WHERE deleted = 0

UNION ALL

SELECT
    '成绩数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT('平均分: ', ROUND(AVG(total_score), 2), ', 及格率: ', ROUND(AVG(CASE WHEN is_passed THEN 1.0 ELSE 0.0 END) * 100, 1), '%') as '详细分布'
FROM tb_grade WHERE deleted = 0

UNION ALL

SELECT
    '考勤数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT('出勤率: ', ROUND(AVG(CASE WHEN attendance_status = '正常' THEN 1.0 ELSE 0.0 END) * 100, 1), '%') as '详细分布'
FROM tb_attendance WHERE deleted = 0

UNION ALL

SELECT
    '缴费数据' as '数据类型',
    COUNT(*) as '记录数',
    CONCAT('总金额: ¥', FORMAT(SUM(payment_amount), 2), ', 完成率: ', ROUND(AVG(CASE WHEN payment_status = 'completed' THEN 1.0 ELSE 0.0 END) * 100, 1), '%') as '详细分布'
FROM tb_payment_record WHERE deleted = 0;

SELECT '=== Smart Campus Management System 优化数据生成完成 ===' as '状态', NOW() as '时间';
SELECT '🎉 数据生成成功！系统已准备就绪，可以开始使用。' as '提示信息';
