-- =====================================================
-- Smart Campus Management System - 学生和课程数据生成脚本
-- 文件: 04_generate_students_and_courses.sql
-- 描述: 生成学生信息、课程数据和关联关系
-- 版本: 2.0.0
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

-- =====================================================
-- 高性能批量生成学生信息
-- =====================================================

-- 创建临时表用于学生分配
DROP TEMPORARY TABLE IF EXISTS temp_student_assignment;
CREATE TEMPORARY TABLE temp_student_assignment (
    user_id BIGINT,
    username VARCHAR(50),
    real_name VARCHAR(100),
    class_id BIGINT,
    enrollment_year INT,
    grade VARCHAR(20),
    major VARCHAR(100),
    row_num INT
);

-- 为学生用户分配班级（按顺序分配，每个班级50人）
-- 先创建学生序号表
DROP TEMPORARY TABLE IF EXISTS temp_student_numbers;
CREATE TEMPORARY TABLE temp_student_numbers (
    user_id BIGINT,
    username VARCHAR(50),
    real_name VARCHAR(100),
    student_row INT
);

INSERT INTO temp_student_numbers (user_id, username, real_name, student_row)
SELECT
    u.id,
    u.username,
    u.real_name,
    ROW_NUMBER() OVER (ORDER BY u.id) as student_row
FROM tb_user u
JOIN tb_user_role ur ON u.id = ur.user_id
JOIN tb_role r ON ur.role_id = r.id
WHERE r.role_key = 'ROLE_STUDENT'
AND u.deleted = 0 AND u.status = 1;

-- 创建班级序号表
DROP TEMPORARY TABLE IF EXISTS temp_class_numbers;
CREATE TEMPORARY TABLE temp_class_numbers (
    class_id BIGINT,
    enrollment_year INT,
    grade VARCHAR(20),
    major VARCHAR(100),
    class_row INT
);

INSERT INTO temp_class_numbers (class_id, enrollment_year, grade, major, class_row)
SELECT
    id,
    enrollment_year,
    grade,
    major,
    ROW_NUMBER() OVER (ORDER BY id) as class_row
FROM tb_school_class
WHERE deleted = 0 AND status = 1;

-- 分配学生到班级
INSERT INTO temp_student_assignment (user_id, username, real_name, class_id, enrollment_year, grade, major, row_num)
SELECT
    tsn.user_id,
    tsn.username,
    tsn.real_name,
    tcn.class_id,
    tcn.enrollment_year,
    tcn.grade,
    tcn.major,
    tsn.student_row
FROM temp_student_numbers tsn
JOIN temp_class_numbers tcn ON tcn.class_row = CEIL(tsn.student_row / 50.0);

-- 批量插入学生信息
INSERT INTO tb_student (
    user_id, student_no, grade, major, class_id,
    enrollment_year, enrollment_date, academic_status,
    student_type, training_mode, academic_system,
    current_semester, total_credits, earned_credits, gpa,
    status, deleted
)
SELECT
    tsa.user_id,
    CONCAT(tsa.enrollment_year, SUBSTRING(tsa.username, -6)) as student_no,
    tsa.grade,
    tsa.major,
    tsa.class_id,
    tsa.enrollment_year,
    DATE_ADD(CONCAT(tsa.enrollment_year, '-09-01'), INTERVAL FLOOR(RAND() * 30) DAY) as enrollment_date,
    1 as academic_status,
    '本科生' as student_type,
    '全日制' as training_mode,
    4 as academic_system,
    CASE
        WHEN tsa.enrollment_year = 2025 THEN '第1学期'
        WHEN tsa.enrollment_year = 2024 THEN '第3学期'
        WHEN tsa.enrollment_year = 2023 THEN '第5学期'
        WHEN tsa.enrollment_year = 2022 THEN '第7学期'
        WHEN tsa.enrollment_year = 2021 THEN '第8学期'
        ELSE '第1学期'
    END as current_semester,
    CASE
        WHEN tsa.enrollment_year = 2025 THEN ROUND(RAND() * 20, 1)
        WHEN tsa.enrollment_year = 2024 THEN ROUND(20 + RAND() * 40, 1)
        WHEN tsa.enrollment_year = 2023 THEN ROUND(60 + RAND() * 40, 1)
        WHEN tsa.enrollment_year = 2022 THEN ROUND(100 + RAND() * 40, 1)
        WHEN tsa.enrollment_year = 2021 THEN ROUND(140 + RAND() * 30, 1)
        ELSE 0
    END as total_credits,
    CASE
        WHEN tsa.enrollment_year = 2025 THEN ROUND(RAND() * 15, 1)
        WHEN tsa.enrollment_year = 2024 THEN ROUND(15 + RAND() * 35, 1)
        WHEN tsa.enrollment_year = 2023 THEN ROUND(50 + RAND() * 35, 1)
        WHEN tsa.enrollment_year = 2022 THEN ROUND(85 + RAND() * 35, 1)
        WHEN tsa.enrollment_year = 2021 THEN ROUND(120 + RAND() * 25, 1)
        ELSE 0
    END as earned_credits,
    ROUND(2.0 + RAND() * 2.0, 2) as gpa,
    1 as status,
    0 as deleted
FROM temp_student_assignment tsa;

-- 批量更新班级学生数量
UPDATE tb_school_class sc
SET student_count = (
    SELECT COUNT(*)
    FROM tb_student s
    WHERE s.class_id = sc.id AND s.deleted = 0
)
WHERE sc.deleted = 0;

-- =====================================================
-- 高性能批量生成课程数据
-- =====================================================

-- 创建课程名称临时表
DROP TEMPORARY TABLE IF EXISTS temp_course_names;
CREATE TEMPORARY TABLE temp_course_names (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100)
);

INSERT INTO temp_course_names (course_name) VALUES
-- 基础数学类
('高等数学'),('线性代数'),('概率论与数理统计'),('离散数学'),('数值分析'),
-- 计算机类
('数据结构'),('算法分析'),('计算机组成原理'),('操作系统'),('计算机网络'),
('数据库系统'),('软件工程'),('编译原理'),('人工智能'),('机器学习'),
('深度学习'),('计算机图形学'),('网络安全'),('信息安全'),('分布式系统'),
('云计算'),('大数据技术'),('物联网技术'),('移动应用开发'),('Web开发技术'),
('Java程序设计'),('C++程序设计'),('Python程序设计'),('JavaScript程序设计'),('数据挖掘'),
-- 机械工程类
('机械制图'),('工程力学'),('材料力学'),('流体力学'),('热力学'),
('机械设计'),('机械制造'),('数控技术'),('CAD/CAM'),('工业设计'),
-- 电子电气类
('数字电路'),('模拟电路'),('信号与系统'),('通信原理'),('电磁场理论'),
('自动控制原理'),('现代控制理论'),('电力系统'),('电机学'),('电子技术基础'),
-- 管理经济类
('财务管理'),('市场营销'),('国际贸易'),('电子商务'),('物流管理'),
('运营管理'),('战略管理'),('组织行为学'),('管理信息系统'),('会计学原理'),
-- 外语类
('英语听力'),('英语口语'),('英语阅读'),('英语写作'),('英语语法'),
('英语翻译'),('商务英语'),('科技英语'),('英美文学'),('语言学概论'),
-- 物理类
('物理学'),('普通物理'),('理论力学'),('量子力学'),('热力学与统计物理'),
('电动力学'),('固体物理'),('原子物理'),('核物理'),('光学'),
-- 化学类
('化学'),('无机化学'),('有机化学'),('分析化学'),('物理化学'),
('结构化学'),('化工原理'),('化学反应工程'),('分离工程'),('化工设备'),
-- 生物类
('生物学'),('植物学'),('动物学'),('微生物学'),('遗传学'),
('细胞生物学'),('分子生物学'),('生态学'),('进化生物学'),('生物技术'),
-- 材料类
('材料科学与工程'),('高分子材料'),('复合材料'),('功能材料'),('新能源材料'),
-- 土木建筑类
('土木工程'),('建筑环境'),('给排水工程'),('工程管理'),('工程造价'),
('建筑设计'),('建筑构造'),('建筑力学'),('建筑材料'),('建筑结构'),
-- 环境能源类
('环境工程'),('环境监测'),('环境评价'),('水污染控制'),('大气污染控制'),
('能源与动力工程'),('新能源技术'),('太阳能技术'),('风能技术'),('核能技术'),
-- 交通航空类
('交通工程'),('交通规划'),('交通设计'),('交通管理'),('智能交通'),
('航空工程'),('航天工程'),('飞行器设计'),('航空发动机'),('飞行力学'),
-- 海洋类
('海洋工程'),('船舶设计'),('海洋平台'),('海洋环境'),('海洋资源'),
-- 法学类
('法理学'),('宪法学'),('民法学'),('刑法学'),('行政法学'),
('经济法学'),('国际法学'),('诉讼法学'),('知识产权法'),('环境法学'),
-- 文学类
('文学概论'),('中国古代文学'),('中国现代文学'),('中国当代文学'),('外国文学'),
('比较文学'),('文艺理论'),('语言学'),('现代汉语'),('古代汉语'),
-- 新闻传播类
('新闻学'),('传播学'),('广告学'),('编辑出版学'),('网络传播'),
('数字媒体'),('影视制作'),('摄影摄像'),('平面设计'),('动画设计'),
-- 音乐舞蹈类
('音乐理论'),('音乐史'),('声乐'),('器乐'),('作曲'),
('指挥'),('音乐教育'),('舞蹈基础'),('舞蹈编导'),('舞蹈史'),
-- 美术设计类
('美术史'),('绘画'),('雕塑'),('书法'),('篆刻'),
('设计基础'),('视觉传达设计'),('环境设计'),('产品设计'),('服装设计'),
-- 体育类
('体育概论'),('运动解剖学'),('运动生理学'),('体育心理学'),('运动训练学'),
('体育教学法'),('田径'),('游泳'),('球类运动'),('体操'),
-- 思政教育类
('马克思主义基本原理'),('毛泽东思想概论'),('中国特色社会主义理论'),('思想道德修养'),('中国近现代史纲要'),
('形势与政策'),('政治经济学'),('科学社会主义'),('中共党史'),('教育学'),
-- 心理学类
('心理学'),('教育心理学'),('发展心理学'),('认知心理学'),('社会心理学'),
('教育技术学'),('课程与教学论'),('教育评价'),('教育管理'),('学前教育');

-- 创建序列号临时表
DROP TEMPORARY TABLE IF EXISTS temp_course_numbers;
CREATE TEMPORARY TABLE temp_course_numbers (
    num INT PRIMARY KEY
);

INSERT INTO temp_course_numbers (num)
SELECT a.N + b.N * 10 + c.N * 100 + 1 as num
FROM
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c
WHERE (a.N + b.N * 10 + c.N * 100 + 1) <= 250;

-- 批量生成课程数据（每个学院200-250门课程）
INSERT INTO tb_course (
    course_name, course_code, department_id, course_type,
    credits, hours, theory_hours, practice_hours,
    description, max_students, current_students,
    is_elective, status, deleted
)
SELECT
    tcn.course_name,
    CONCAT(d.department_code, LPAD(tcnum.num, 4, '0')) as course_code,
    d.id as department_id,
    CASE WHEN (tcnum.num % 10) < 6 THEN '必修课' ELSE '选修课' END as course_type,
    CASE WHEN (tcnum.num % 10) < 6 THEN 2.0 + (tcnum.num % 4) * 0.5 ELSE 1.0 + (tcnum.num % 3) * 0.5 END as credits,
    CASE WHEN (tcnum.num % 10) < 6 THEN (2.0 + (tcnum.num % 4) * 0.5) * 16 ELSE (1.0 + (tcnum.num % 3) * 0.5) * 16 END as hours,
    FLOOR(CASE WHEN (tcnum.num % 10) < 6 THEN (2.0 + (tcnum.num % 4) * 0.5) * 16 ELSE (1.0 + (tcnum.num % 3) * 0.5) * 16 END * 0.7) as theory_hours,
    FLOOR(CASE WHEN (tcnum.num % 10) < 6 THEN (2.0 + (tcnum.num % 4) * 0.5) * 16 ELSE (1.0 + (tcnum.num % 3) * 0.5) * 16 END * 0.3) as practice_hours,
    CONCAT(tcn.course_name, '课程描述') as description,
    CASE WHEN (tcnum.num % 10) < 6 THEN 200 ELSE 100 END as max_students,
    0 as current_students,
    CASE WHEN (tcnum.num % 10) < 6 THEN 0 ELSE 1 END as is_elective,
    1 as status,
    0 as deleted
FROM tb_department d
CROSS JOIN temp_course_numbers tcnum
CROSS JOIN temp_course_names tcn
WHERE d.deleted = 0 AND d.status = 1
AND tcnum.num <= 225  -- 每个学院225门课程
AND tcn.id = ((d.id - 1) * 225 + tcnum.num - 1) % 200 + 1;  -- 循环使用课程名称

-- =====================================================
-- 高性能批量分配班主任
-- =====================================================

-- 为班级分配班主任（优先使用班主任角色，不足时使用教师角色）
UPDATE tb_school_class sc
SET class_teacher_id = (
    SELECT teacher_id FROM (
        SELECT
            u.id as teacher_id,
            ROW_NUMBER() OVER (ORDER BY u.id) as teacher_row
        FROM tb_user u
        JOIN tb_user_role ur ON u.id = ur.user_id
        JOIN tb_role r ON ur.role_id = r.id
        WHERE r.role_key IN ('ROLE_CLASS_TEACHER', 'ROLE_TEACHER')
        AND u.deleted = 0 AND u.status = 1
    ) teachers
    WHERE teachers.teacher_row = ((sc.id - 1) % 2800) + 1  -- 2800个教师循环分配
)
WHERE sc.deleted = 0 AND sc.status = 1;

-- =====================================================
-- 执行数据生成
-- =====================================================

START TRANSACTION;

SELECT '=== 开始生成学生和课程数据 ===' as '状态', NOW() as '时间';

-- 步骤1: 生成学生信息
SELECT '步骤1: 生成学生信息...' as '状态', NOW() as '时间';
-- 学生信息已在上面批量生成
SELECT '✓ 学生信息生成完成' as '状态', NOW() as '时间';

-- 步骤2: 生成课程数据
SELECT '步骤2: 生成课程数据...' as '状态', NOW() as '时间';
-- 课程数据已在上面批量生成
SELECT '✓ 课程数据生成完成' as '状态', NOW() as '时间';

-- 步骤3: 分配班主任
SELECT '步骤3: 分配班主任...' as '状态', NOW() as '时间';
-- 班主任分配已在上面批量完成
SELECT '✓ 班主任分配完成' as '状态', NOW() as '时间';

-- 恢复设置
SET foreign_key_checks = 1;
SET unique_checks = 1;
SET autocommit = 1;

COMMIT;

-- 显示统计信息
SELECT '=== 学生和课程数据生成完成 ===' as '状态', NOW() as '时间';

SELECT 
    '学生总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_student WHERE deleted = 0
UNION ALL
SELECT 
    '课程总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_course WHERE deleted = 0
UNION ALL
SELECT 
    '班级总数' as '统计项目',
    COUNT(*) as '数量'
FROM tb_school_class WHERE deleted = 0;

SELECT 
    sc.grade as '年级',
    COUNT(*) as '班级数量',
    SUM(sc.student_count) as '学生总数',
    ROUND(AVG(sc.student_count), 1) as '平均班级人数'
FROM tb_school_class sc
WHERE sc.deleted = 0
GROUP BY sc.grade
ORDER BY sc.grade;

SELECT '✓ 学生和课程数据生成完成' as '状态', NOW() as '时间';

-- 清理临时表
DROP TEMPORARY TABLE IF EXISTS temp_student_assignment;
DROP TEMPORARY TABLE IF EXISTS temp_student_numbers;
DROP TEMPORARY TABLE IF EXISTS temp_class_numbers;
DROP TEMPORARY TABLE IF EXISTS temp_course_names;
DROP TEMPORARY TABLE IF EXISTS temp_course_numbers;
