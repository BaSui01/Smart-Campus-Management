-- =====================================================
-- Smart Campus Management System - 完整数据生成脚本 (修复版)
-- 文件: 08_complete_data_generation_fixed.sql
-- 描述: 为所有表生成合理规模的测试数据
-- 数据规模: 15,000用户 + 200班级 + 完整业务数据
-- 版本: 3.0.0 (修复版)
-- 创建时间: 2025-01-27
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

-- 开始事务
START TRANSACTION;

SELECT '=== Smart Campus Management System 完整数据生成开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 清理现有数据 (保留基础配置)
-- =====================================================

SELECT '步骤1: 清理现有业务数据...' as '状态', NOW() as '时间';

-- 清理业务数据表 (保留基础配置表)
DELETE FROM tb_payment_record WHERE id > 0;
DELETE FROM tb_student_fee WHERE id > 0;
DELETE FROM tb_course_selection WHERE id > 0;
DELETE FROM tb_grade WHERE id > 0;
DELETE FROM tb_exam_record WHERE id > 0;
DELETE FROM tb_assignment_submission WHERE id > 0;
DELETE FROM tb_attendance WHERE id > 0;
DELETE FROM tb_course_schedule WHERE id > 0;
DELETE FROM tb_course WHERE id > 0;
DELETE FROM tb_student WHERE id > 0;
DELETE FROM tb_teacher WHERE id > 0;
DELETE FROM tb_class WHERE id > 0;
DELETE FROM tb_user_role WHERE id > 0;
DELETE FROM tb_user WHERE id > 1; -- 保留默认管理员

-- 重置自增ID
ALTER TABLE tb_user AUTO_INCREMENT = 2;
ALTER TABLE tb_class AUTO_INCREMENT = 1;
ALTER TABLE tb_teacher AUTO_INCREMENT = 1;
ALTER TABLE tb_student AUTO_INCREMENT = 1;
ALTER TABLE tb_course AUTO_INCREMENT = 1;
ALTER TABLE tb_course_schedule AUTO_INCREMENT = 1;
ALTER TABLE tb_attendance AUTO_INCREMENT = 1;
ALTER TABLE tb_assignment_submission AUTO_INCREMENT = 1;
ALTER TABLE tb_exam_record AUTO_INCREMENT = 1;
ALTER TABLE tb_grade AUTO_INCREMENT = 1;
ALTER TABLE tb_course_selection AUTO_INCREMENT = 1;
ALTER TABLE tb_student_fee AUTO_INCREMENT = 1;
ALTER TABLE tb_payment_record AUTO_INCREMENT = 1;
ALTER TABLE tb_user_role AUTO_INCREMENT = 1;

SELECT '✓ 数据清理完成' as '状态', NOW() as '时间';

-- =====================================================
-- 2. 创建临时表和基础数据
-- =====================================================

SELECT '步骤2: 创建临时表和基础数据...' as '状态', NOW() as '时间';

-- 创建序列号临时表
DROP TEMPORARY TABLE IF EXISTS temp_numbers;
CREATE TEMPORARY TABLE temp_numbers (
    num INT PRIMARY KEY
);

-- 生成1到15000的序列号
INSERT INTO temp_numbers (num)
SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 + 1 as number_val
FROM
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) d,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) e
WHERE (a.N + b.N * 10 + c.N * 100 + d.N * 1000 + e.N * 10000 + 1) <= 15000;

-- 创建姓名数据表
DROP TEMPORARY TABLE IF EXISTS temp_surnames;
CREATE TEMPORARY TABLE temp_surnames (
    id INT AUTO_INCREMENT PRIMARY KEY,
    surname VARCHAR(10)
);

INSERT INTO temp_surnames (surname) VALUES
('王'),('李'),('张'),('刘'),('陈'),('杨'),('黄'),('赵'),('周'),('吴'),
('徐'),('孙'),('朱'),('马'),('胡'),('郭'),('林'),('何'),('高'),('梁'),
('郑'),('罗'),('宋'),('谢'),('唐'),('韩'),('曹'),('许'),('邓'),('萧'),
('冯'),('曾'),('程'),('蔡'),('彭'),('潘'),('袁'),('于'),('董'),('余'),
('苏'),('叶'),('吕'),('魏'),('蒋'),('田'),('杜'),('丁'),('沈'),('姜'),
('范'),('江'),('傅'),('钟'),('卢'),('汪'),('戴'),('崔'),('任'),('陆'),
('廖'),('姚'),('方'),('金'),('邱'),('夏'),('谭'),('韦'),('贾'),('邹'),
('石'),('熊'),('孟'),('秦'),('阎'),('薛'),('侯'),('雷'),('白'),('龙'),
('段'),('郝'),('孔'),('邵'),('史'),('毛'),('常'),('万'),('顾'),('赖'),
('武'),('康'),('贺'),('严'),('尹'),('钱'),('施'),('牛'),('洪'),('龚'),
('温'),('莫'),('蓝'),('甘'),('易'),('华'),('欧'),('柯'),('卜'),('路'),
('聂'),('关'),('苗'),('池'),('向'),('时'),('包'),('司'),('柳'),('黎'),
('鲁'),('韦'),('昌'),('马'),('苏'),('孔'),('曹'),('严'),('华'),('金'),
('魏'),('陶'),('姜'),('戚'),('谢'),('邹'),('喻'),('柏'),('水'),('窦'),
('章'),('云'),('苏'),('潘'),('葛'),('奚'),('范'),('彭'),('郎'),('鲁'),
('欧阳'),('太史'),('端木'),('上官'),('司马'),('东方'),('独孤'),('南宫'),
('万俟'),('闻人'),('夏侯'),('诸葛'),('尉迟'),('公羊'),('赫连'),('澹台'),
('皇甫'),('宗政'),('濮阳'),('公冶'),('太叔'),('申屠'),('公孙'),('慕容'),
('仲孙'),('钟离'),('长孙'),('宇文'),('司徒'),('鲜于'),('司空'),('闾丘'),
('子车'),('亓官'),('司寇'),('巫马'),('公西'),('颛孙'),('壤驷'),('公良');

DROP TEMPORARY TABLE IF EXISTS temp_given_names;
CREATE TEMPORARY TABLE temp_given_names (
    id INT AUTO_INCREMENT PRIMARY KEY,
    given_name VARCHAR(10),
    gender VARCHAR(5)
);

INSERT INTO temp_given_names (given_name, gender) VALUES
-- 男性名字
('伟','男'),('强','男'),('磊','男'),('军','男'),('勇','男'),('涛','男'),('明','男'),('超','男'),('亮','男'),('华','男'),
('建','男'),('国','男'),('峰','男'),('辉','男'),('成','男'),('龙','男'),('文','男'),('斌','男'),('刚','男'),('健','男'),
('世','男'),('广','男'),('志','男'),('良','男'),('海','男'),('山','男'),('仁','男'),('波','男'),('宁','男'),('贵','男'),
('福','男'),('生','男'),('元','男'),('全','男'),('胜','男'),('学','男'),('祥','男'),('才','男'),('发','男'),('武','男'),
('新','男'),('利','男'),('清','男'),('飞','男'),('彬','男'),('富','男'),('顺','男'),('信','男'),('子','男'),('杰','男'),
('浩','男'),('宇','男'),('轩','男'),('博','男'),('昊','男'),('然','男'),('睿','男'),('晨','男'),('阳','男'),('煜','男'),
('泽','男'),('凯','男'),('锐','男'),('翔','男'),('鹏','男'),('豪','男'),('旭','男'),('琦','男'),('皓','男'),('晟','男'),
('恒','男'),('航','男'),('乐','男'),('逸','男'),('风','男'),('雨','男'),('林','男'),('森','男'),('江','男'),('河','男'),
('天','男'),('地','男'),('星','男'),('辰','男'),('日','男'),('月','男'),('光','男'),('照','男'),('熙','男'),('炎','男'),
('威','男'),('猛','男'),('虎','男'),('豹','男'),('狮','男'),('鹰','男'),('隼','男'),('熊','男'),('狼','男'),('剑','男'),
-- 女性名字
('芳','女'),('娜','女'),('静','女'),('敏','女'),('雪','女'),('丽','女'),('美','女'),('娟','女'),('英','女'),('华','女'),
('慧','女'),('巧','女'),('淑','女'),('惠','女'),('珠','女'),('翠','女'),('雅','女'),('芝','女'),('玉','女'),('萍','女'),
('红','女'),('娥','女'),('玲','女'),('芬','女'),('燕','女'),('彩','女'),('春','女'),('菊','女'),('兰','女'),('凤','女'),
('洁','女'),('梅','女'),('琳','女'),('素','女'),('云','女'),('莲','女'),('真','女'),('环','女'),('荣','女'),('爱','女'),
('妹','女'),('霞','女'),('香','女'),('月','女'),('莺','女'),('媛','女'),('艳','女'),('瑞','女'),('凡','女'),('佳','女'),
('涵','女'),('欣','女'),('怡','女'),('诗','女'),('语','女'),('梦','女'),('思','女'),('雨','女'),('晴','女'),('雪','女'),
('冰','女'),('霜','女'),('露','女'),('珊','女'),('瑚','女'),('贝','女'),('珍','女'),('宝','女'),('钰','女'),('琪','女'),
('琳','女'),('瑶','女'),('瑾','女'),('璇','女'),('璐','女'),('娅','女'),('嫣','女'),('妍','女'),('嫒','女'),('姝','女'),
('珠','女'),('宝','女'),('钻','女'),('金','女'),('银','女'),('铜','女'),('铁','女'),('玉','女'),('翡','女'),('翠','女'),
('琼','女'),('璧','女'),('璜','女'),('琪','女'),('琳','女'),('瑜','女'),('瑕','女'),('玛','女'),('瑙','女'),('珊','女'),
-- 通用名字
('德','通用'),('仁','通用'),('义','通用'),('礼','通用'),('智','通用'),('信','通用'),('忠','通用'),('孝','通用'),
('廉','通用'),('耻','通用'),('温','通用'),('良','通用'),('恭','通用'),('俭','通用'),('让','通用'),('谦','通用'),
('和','通用'),('平','通用'),('安','通用'),('宁','通用'),('静','通用'),('淡','通用'),('雅','通用'),('正','通用'),
('福','通用'),('禄','通用'),('寿','通用'),('喜','通用'),('财','通用'),('宝','通用'),('贵','通用'),('荣','通用'),
('华','通用'),('富','通用'),('康','通用'),('健','通用'),('安','通用'),('泰','通用'),('祥','通用'),('瑞','通用'),
('吉','通用'),('庆','通用'),('禧','通用'),('福','通用'),('恩','通用'),('惠','通用'),('慈','通用'),('善','通用'),
('成','通用'),('功','通用'),('就','通用'),('业','通用'),('立','通用'),('建','通用'),('创','通用'),('新','通用'),
('进','通用'),('步','通用'),('升','通用'),('高','通用'),('远','通用'),('达','通用'),('通','通用'),('畅','通用'),
('顺','通用'),('利','通用'),('胜','通用'),('赢','通用'),('超','通用'),('越','通用'),('优','通用'),('秀','通用'),
('学','通用'),('习','通用'),('知','通用'),('识','通用'),('智','通用'),('慧','通用'),('聪','通用'),('明','通用');

SELECT '✓ 临时表创建完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 生成用户数据
-- =====================================================

SELECT '步骤3: 生成用户数据...' as '状态', NOW() as '时间';

-- 生成50个管理员用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('admin', LPAD(100000 + num, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('admin', LPAD(100000 + num, 6, '0'), '@university.edu.cn') as email,
    CONCAT(s.surname, g.given_name) as real_name,
    CONCAT('1', FLOOR(3 + (num % 6)), LPAD(FLOOR(RAND() * 100000000), 8, '0')) as phone,
    CASE WHEN num % 2 = 0 THEN '男' ELSE '女' END as gender,
    CASE WHEN num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_surnames s ON s.id = (n.num % 200) + 1
JOIN temp_given_names g ON g.id = (n.num % 300) + 1
WHERE n.num <= 50;

-- 生成500个教师用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('teacher', LPAD(200000 + num, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('teacher', LPAD(200000 + num, 6, '0'), '@university.edu.cn') as email,
    CONCAT(s.surname, g.given_name) as real_name,
    CONCAT('1', FLOOR(3 + (num % 6)), LPAD(FLOOR(RAND() * 100000000), 8, '0')) as phone,
    g.gender as gender,
    CASE WHEN num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_surnames s ON s.id = (n.num % 200) + 1
JOIN temp_given_names g ON g.id = (n.num % 300) + 1
WHERE n.num <= 500;

-- 生成200个班主任用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('classteacher', LPAD(300000 + num, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('classteacher', LPAD(300000 + num, 6, '0'), '@university.edu.cn') as email,
    CONCAT(s.surname, g.given_name) as real_name,
    CONCAT('1', FLOOR(3 + (num % 6)), LPAD(FLOOR(RAND() * 100000000), 8, '0')) as phone,
    g.gender as gender,
    CASE WHEN num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_surnames s ON s.id = (n.num % 200) + 1
JOIN temp_given_names g ON g.id = (n.num % 300) + 1
WHERE n.num <= 200;

-- 生成10000个学生用户
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('student', LPAD(400000 + num, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('student', LPAD(400000 + num, 6, '0'), '@university.edu.cn') as email,
    CONCAT(s.surname, g.given_name) as real_name,
    CONCAT('1', FLOOR(3 + (num % 6)), LPAD(FLOOR(RAND() * 100000000), 8, '0')) as phone,
    g.gender as gender,
    CASE WHEN num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_surnames s ON s.id = (n.num % 200) + 1
JOIN temp_given_names g ON g.id = (n.num % 300) + 1
WHERE n.num <= 10000;

-- 生成4250个家长用户 (约42.5%的学生有家长账户)
INSERT INTO tb_user (username, password, email, real_name, phone, gender, status, deleted)
SELECT
    CONCAT('parent', LPAD(500000 + num, 6, '0')) as username,
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXIGfZEKtY5iHNrXqPHNmzNVjIK' as password,
    CONCAT('parent', LPAD(500000 + num, 6, '0'), '@university.edu.cn') as email,
    CONCAT(s.surname, g.given_name) as real_name,
    CONCAT('1', FLOOR(3 + (num % 6)), LPAD(FLOOR(RAND() * 100000000), 8, '0')) as phone,
    g.gender as gender,
    CASE WHEN num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_surnames s ON s.id = (n.num % 200) + 1
JOIN temp_given_names g ON g.id = (n.num % 300) + 1
WHERE n.num <= 4250;

SELECT '✓ 用户数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 分配用户角色
-- =====================================================

SELECT '步骤4: 分配用户角色...' as '状态', NOW() as '时间';

-- 分配管理员角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
JOIN tb_role r ON r.role_key = 'ROLE_ADMIN'
WHERE u.username LIKE 'admin%' AND u.deleted = 0;

-- 分配教师角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
JOIN tb_role r ON r.role_key = 'ROLE_TEACHER'
WHERE u.username LIKE 'teacher%' AND u.deleted = 0;

-- 分配班主任角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
JOIN tb_role r ON r.role_key = 'ROLE_CLASS_TEACHER'
WHERE u.username LIKE 'classteacher%' AND u.deleted = 0;

-- 分配学生角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
JOIN tb_role r ON r.role_key = 'ROLE_STUDENT'
WHERE u.username LIKE 'student%' AND u.deleted = 0;

-- 分配家长角色
INSERT INTO tb_user_role (user_id, role_id, status, deleted)
SELECT u.id, r.id, 1, 0
FROM tb_user u
JOIN tb_role r ON r.role_key = 'ROLE_PARENT'
WHERE u.username LIKE 'parent%' AND u.deleted = 0;

SELECT '✓ 用户角色分配完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 生成专业数据
-- =====================================================

SELECT '步骤5: 生成专业数据...' as '状态', NOW() as '时间';

-- 创建专业临时表
DROP TEMPORARY TABLE IF EXISTS temp_majors;
CREATE TEMPORARY TABLE temp_majors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    major_name VARCHAR(100),
    department_id INT
);

INSERT INTO temp_majors (major_name, department_id) VALUES
-- 计算机科学与技术学院专业
('计算机科学与技术', 1),('软件工程', 1),('网络工程', 1),('信息安全', 1),('数据科学与大数据技术', 1),
-- 机械工程学院专业
('机械设计制造及其自动化', 2),('机械电子工程', 2),('车辆工程', 2),('工业设计', 2),('智能制造工程', 2),
-- 电子信息工程学院专业
('电子信息工程', 3),('通信工程', 3),('自动化', 3),('电气工程及其自动化', 3),('光电信息科学与工程', 3),
-- 经济管理学院专业
('工商管理', 4),('市场营销', 4),('国际经济与贸易', 4),('会计学', 4),('财务管理', 4),
-- 外国语学院专业
('英语', 5),('日语', 5),('德语', 5),('法语', 5),('俄语', 5),
-- 数学与统计学院专业
('数学与应用数学', 6),('统计学', 6),('信息与计算科学', 6),('应用统计学', 6),('金融数学', 6),
-- 物理与光电工程学院专业
('应用物理学', 7),('光电信息科学与工程', 7),('材料物理', 7),('新能源科学与工程', 7),('核工程与核技术', 7),
-- 化学与化工学院专业
('化学', 8),('化学工程与工艺', 8),('应用化学', 8),('材料化学', 8),('制药工程', 8),
-- 生命科学学院专业
('生物科学', 9),('生物技术', 9),('生物工程', 9),('生态学', 9),('生物信息学', 9),
-- 材料科学与工程学院专业
('材料科学与工程', 10),('高分子材料与工程', 10),('复合材料与工程', 10),('纳米材料与技术', 10),('功能材料', 10);

SELECT '✓ 专业数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 生成班级数据
-- =====================================================

SELECT '步骤6: 生成班级数据...' as '状态', NOW() as '时间';

-- 生成200个班级 (每个专业4个班级)
INSERT INTO tb_class (class_name, class_code, department_id, major, grade_level, class_teacher_id, max_students, current_students, academic_year, status, deleted)
SELECT
    CONCAT(m.major_name, FLOOR((n.num - 1) / 50) + 2021, '级', ((n.num - 1) % 4) + 1, '班') as class_name,
    CONCAT('C', LPAD(n.num, 6, '0')) as class_code,
    m.department_id as department_id,
    m.major_name as major,
    CONCAT(FLOOR((n.num - 1) / 50) + 2021, '级') as grade_level,
    ct.id as class_teacher_id,
    50 as max_students,
    0 as current_students,
    CONCAT(FLOOR((n.num - 1) / 50) + 2021, '-', FLOOR((n.num - 1) / 50) + 2025) as academic_year,
    1 as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_majors m ON m.id = ((n.num - 1) % 50) + 1
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'classteacher%' AND deleted = 0) ct ON ct.rn = n.num
WHERE n.num <= 200;

SELECT '✓ 班级数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 7. 生成教师数据
-- =====================================================

SELECT '步骤7: 生成教师数据...' as '状态', NOW() as '时间';

-- 生成500个教师记录
INSERT INTO tb_teacher (user_id, teacher_code, department_id, title, education, specialization, hire_date, employment_type, office_location, office_phone, research_direction, status, deleted)
SELECT
    u.id as user_id,
    CONCAT('T', LPAD(n.num, 6, '0')) as teacher_code,
    ((n.num - 1) % 50) + 1 as department_id,
    CASE
        WHEN n.num % 10 = 1 THEN '教授'
        WHEN n.num % 10 IN (2,3) THEN '副教授'
        WHEN n.num % 10 IN (4,5,6) THEN '讲师'
        ELSE '助教'
    END as title,
    CASE
        WHEN n.num % 5 = 1 THEN '博士'
        WHEN n.num % 5 IN (2,3) THEN '硕士'
        ELSE '学士'
    END as education,
    m.major_name as specialization,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 3650) DAY) as hire_date,
    CASE WHEN n.num % 20 = 0 THEN '兼职' ELSE '全职' END as employment_type,
    CONCAT('教学楼', FLOOR((n.num - 1) / 100) + 1, '号', FLOOR(((n.num - 1) % 100) / 10) + 1, '层', ((n.num - 1) % 10) + 1, '室') as office_location,
    CONCAT('0571-', LPAD(FLOOR(RAND() * 100000000), 8, '0')) as office_phone,
    CONCAT(m.major_name, '相关研究') as research_direction,
    CASE WHEN n.num % 25 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'teacher%' AND deleted = 0) u ON u.rn = n.num
JOIN temp_majors m ON m.id = ((n.num - 1) % 50) + 1
WHERE n.num <= 500;

SELECT '✓ 教师数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 8. 生成学生数据
-- =====================================================

SELECT '步骤8: 生成学生数据...' as '状态', NOW() as '时间';

-- 生成10000个学生记录
INSERT INTO tb_student (user_id, student_code, class_id, admission_date, graduation_date, student_type, enrollment_status, gpa, total_credits, emergency_contact, emergency_phone, dormitory, status, deleted)
SELECT
    u.id as user_id,
    CONCAT('S', LPAD(n.num, 6, '0')) as student_code,
    ((n.num - 1) % 200) + 1 as class_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR((n.num - 1) / 2500) * 365 + FLOOR(RAND() * 365) DAY) as admission_date,
    DATE_ADD(DATE_SUB(CURDATE(), INTERVAL FLOOR((n.num - 1) / 2500) * 365 + FLOOR(RAND() * 365) DAY), INTERVAL 4 YEAR) as graduation_date,
    CASE WHEN n.num % 100 = 0 THEN '交换生' ELSE '普通学生' END as student_type,
    CASE
        WHEN n.num % 50 = 0 THEN '休学'
        WHEN n.num % 100 = 0 THEN '毕业'
        ELSE '在读'
    END as enrollment_status,
    ROUND(2.0 + RAND() * 2.0, 2) as gpa,
    FLOOR(RAND() * 120) + 20 as total_credits,
    CONCAT(s.surname, g.given_name) as emergency_contact,
    CONCAT('1', FLOOR(3 + (n.num % 6)), LPAD(FLOOR(RAND() * 100000000), 8, '0')) as emergency_phone,
    CONCAT('宿舍楼', FLOOR((n.num - 1) / 1000) + 1, '号', FLOOR(((n.num - 1) % 1000) / 100) + 1, '层', LPAD(((n.num - 1) % 100) + 1, 3, '0'), '室') as dormitory,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN (SELECT id, ROW_NUMBER() OVER (ORDER BY id) as rn FROM tb_user WHERE username LIKE 'student%' AND deleted = 0) u ON u.rn = n.num
JOIN temp_surnames s ON s.id = (n.num % 200) + 1
JOIN temp_given_names g ON g.id = (n.num % 300) + 1
WHERE n.num <= 10000;

-- 更新班级的当前学生数
UPDATE tb_class c SET current_students = (
    SELECT COUNT(*) FROM tb_student s WHERE s.class_id = c.id AND s.deleted = 0
);

SELECT '✓ 学生数据生成完成' as '状态', NOW() as '时间';

COMMIT;

SELECT '=== 第三阶段完成，继续执行后续脚本 ===' as '状态', NOW() as '时间';
