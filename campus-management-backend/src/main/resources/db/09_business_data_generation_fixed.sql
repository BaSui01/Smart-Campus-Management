-- =====================================================
-- Smart Campus Management System - 业务数据生成脚本 (修复版)
-- 文件: 09_business_data_generation_fixed.sql
-- 描述: 生成课程、选课、成绩、财务等业务数据
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

SELECT '=== Smart Campus Management System 业务数据生成开始 ===' as '状态', NOW() as '时间';

-- =====================================================
-- 1. 创建临时表
-- =====================================================

-- 创建序列号临时表
DROP TEMPORARY TABLE IF EXISTS temp_numbers;
CREATE TEMPORARY TABLE temp_numbers (
    num INT PRIMARY KEY
);

-- 生成1到5000的序列号
INSERT INTO temp_numbers (num)
SELECT a.N + b.N * 10 + c.N * 100 + d.N * 1000 + 1 as number_val
FROM
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) c,
    (SELECT 0 as N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) d
WHERE (a.N + b.N * 10 + c.N * 100 + d.N * 1000 + 1) <= 5000;

-- 创建课程名称临时表
DROP TEMPORARY TABLE IF EXISTS temp_course_names;
CREATE TEMPORARY TABLE temp_course_names (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100)
);

INSERT INTO temp_course_names (course_name) VALUES
-- 基础课程
('高等数学'),('线性代数'),('概率论与数理统计'),('大学物理'),('大学化学'),
('大学英语'),('思想道德修养与法律基础'),('马克思主义基本原理'),('毛泽东思想和中国特色社会主义理论体系概论'),('中国近现代史纲要'),
('体育'),('军事理论'),('大学语文'),('计算机基础'),('程序设计基础'),
-- 专业基础课程
('数据结构'),('算法分析与设计'),('计算机组成原理'),('操作系统'),('数据库系统原理'),
('计算机网络'),('软件工程'),('编译原理'),('人工智能'),('机器学习'),
('电路分析'),('模拟电子技术'),('数字电子技术'),('信号与系统'),('通信原理'),
('自动控制原理'),('单片机原理'),('嵌入式系统'),('数字信号处理'),('电磁场与电磁波'),
('机械制图'),('理论力学'),('材料力学'),('机械原理'),('机械设计'),
('工程材料'),('制造技术基础'),('机械制造工艺学'),('数控技术'),('机电一体化'),
('管理学原理'),('微观经济学'),('宏观经济学'),('会计学原理'),('财务管理'),
('市场营销'),('人力资源管理'),('运营管理'),('战略管理'),('组织行为学'),
-- 专业课程
('Java程序设计'),('C++程序设计'),('Python程序设计'),('Web开发技术'),('移动应用开发'),
('大数据技术'),('云计算技术'),('网络安全'),('区块链技术'),('物联网技术'),
('图像处理'),('计算机视觉'),('自然语言处理'),('深度学习'),('强化学习'),
('电力系统分析'),('电机学'),('电力电子技术'),('高电压技术'),('继电保护'),
('PLC编程'),('工业机器人'),('传感器技术'),('测控技术'),('过程控制'),
('CAD/CAM技术'),('有限元分析'),('机械振动'),('液压与气压传动'),('工业设计'),
('国际贸易实务'),('电子商务'),('物流管理'),('供应链管理'),('项目管理'),
('投资学'),('金融学'),('保险学'),('税法'),('审计学'),
-- 实践课程
('课程设计'),('专业实习'),('毕业设计'),('创新创业实践'),('社会实践'),
('实验课程'),('上机实践'),('工厂实习'),('认识实习'),('生产实习'),
-- 选修课程
('音乐欣赏'),('美术欣赏'),('文学欣赏'),('历史文化'),('哲学思辨'),
('心理健康'),('职业规划'),('创业基础'),('法律基础'),('环境保护'),
('科技前沿'),('学术写作'),('演讲与口才'),('团队合作'),('领导力培养'),
('数学建模'),('统计分析'),('数据挖掘'),('信息检索'),('文献综述'),
-- 外语课程
('大学英语听说'),('大学英语读写'),('专业英语'),('商务英语'),('科技英语'),
('日语基础'),('德语基础'),('法语基础'),('俄语基础'),('韩语基础'),
-- 体育课程
('篮球'),('足球'),('排球'),('乒乓球'),('羽毛球'),
('游泳'),('田径'),('健美操'),('武术'),('瑜伽'),
-- 艺术课程
('绘画基础'),('书法'),('摄影'),('舞蹈'),('戏剧表演'),
('音乐理论'),('声乐'),('器乐'),('合唱'),('乐队'),
-- 新兴课程
('人工智能伦理'),('数字化转型'),('智能制造'),('绿色能源'),('可持续发展'),
('跨文化交流'),('全球化视野'),('创新思维'),('批判性思维'),('系统思维');

SELECT '✓ 临时表创建完成' as '状态', NOW() as '时间';

-- =====================================================
-- 2. 生成课程数据
-- =====================================================

SELECT '步骤1: 生成课程数据...' as '状态', NOW() as '时间';

-- 生成1000门课程
INSERT INTO tb_course (course_name, course_code, department_id, teacher_id, credits, course_type, semester, academic_year, max_students, current_students, classroom_id, course_description, prerequisites, status, deleted)
SELECT
    tcn.course_name as course_name,
    CONCAT('C', LPAD(n.num, 6, '0')) as course_code,
    ((n.num - 1) % 50) + 1 as department_id,
    ((n.num - 1) % 500) + 1 as teacher_id,
    CASE 
        WHEN n.num % 10 IN (1,2) THEN 1
        WHEN n.num % 10 IN (3,4,5,6) THEN 2
        WHEN n.num % 10 IN (7,8) THEN 3
        ELSE 4
    END as credits,
    CASE 
        WHEN n.num % 5 = 1 THEN '必修课'
        WHEN n.num % 5 = 2 THEN '专业必修'
        WHEN n.num % 5 = 3 THEN '专业选修'
        WHEN n.num % 5 = 4 THEN '公共选修'
        ELSE '实践课'
    END as course_type,
    CASE WHEN n.num % 2 = 1 THEN '第一学期' ELSE '第二学期' END as semester,
    '2024-2025' as academic_year,
    CASE 
        WHEN n.num % 10 IN (1,2) THEN 30
        WHEN n.num % 10 IN (3,4,5) THEN 50
        WHEN n.num % 10 IN (6,7) THEN 80
        ELSE 120
    END as max_students,
    0 as current_students,
    ((n.num - 1) % 100) + 1 as classroom_id,
    CONCAT(tcn.course_name, '课程描述') as course_description,
    CASE WHEN n.num % 3 = 0 THEN '高等数学' ELSE NULL END as prerequisites,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN temp_course_names tcn ON tcn.id = ((n.num - 1) % 120) + 1
WHERE n.num <= 1000;

SELECT '✓ 课程数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 3. 生成课程表数据
-- =====================================================

SELECT '步骤2: 生成课程表数据...' as '状态', NOW() as '时间';

-- 为每门课程生成2-3个课程表时间段
INSERT INTO tb_course_schedule (course_id, classroom_id, time_slot_id, day_of_week, start_week, end_week, status, deleted)
SELECT
    c.id as course_id,
    c.classroom_id as classroom_id,
    ((n.num - 1) % 10) + 1 as time_slot_id,
    ((n.num - 1) % 5) + 1 as day_of_week,
    1 as start_week,
    18 as end_week,
    1 as status,
    0 as deleted
FROM temp_numbers n
JOIN tb_course c ON c.id = ((n.num - 1) % 1000) + 1
WHERE n.num <= 2500;

SELECT '✓ 课程表数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 4. 生成选课数据
-- =====================================================

SELECT '步骤3: 生成选课数据...' as '状态', NOW() as '时间';

-- 为每个学生生成5-8门选课记录
INSERT INTO tb_course_selection (student_id, course_id, selection_time, selection_status, final_grade, grade_point, attendance_rate, status, deleted)
SELECT
    ((n.num - 1) % 10000) + 1 as student_id,
    ((n.num - 1) % 1000) + 1 as course_id,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 180) DAY) as selection_time,
    CASE 
        WHEN n.num % 20 = 0 THEN '已退课'
        WHEN n.num % 50 = 0 THEN '待确认'
        ELSE '已确认'
    END as selection_status,
    CASE 
        WHEN n.num % 20 = 0 THEN NULL
        ELSE FLOOR(60 + RAND() * 40)
    END as final_grade,
    CASE 
        WHEN n.num % 20 = 0 THEN NULL
        ELSE ROUND(1.0 + RAND() * 3.0, 1)
    END as grade_point,
    CASE 
        WHEN n.num % 20 = 0 THEN NULL
        ELSE ROUND(0.7 + RAND() * 0.3, 2)
    END as attendance_rate,
    1 as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 50000;

-- 更新课程的当前学生数
UPDATE tb_course c SET current_students = (
    SELECT COUNT(*) FROM tb_course_selection cs 
    WHERE cs.course_id = c.id AND cs.selection_status = '已确认' AND cs.deleted = 0
);

SELECT '✓ 选课数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 5. 生成成绩数据
-- =====================================================

SELECT '步骤4: 生成成绩数据...' as '状态', NOW() as '时间';

-- 为已确认的选课记录生成成绩
INSERT INTO tb_grade (student_id, course_id, exam_type, score, grade_level, semester, academic_year, exam_date, teacher_id, status, deleted)
SELECT
    cs.student_id,
    cs.course_id,
    CASE
        WHEN n.num % 4 = 1 THEN '期中考试'
        WHEN n.num % 4 = 2 THEN '期末考试'
        WHEN n.num % 4 = 3 THEN '平时成绩'
        ELSE '实验成绩'
    END as exam_type,
    CASE
        WHEN n.num % 4 = 3 THEN FLOOR(70 + RAND() * 30)  -- 平时成绩较高
        ELSE FLOOR(50 + RAND() * 50)  -- 考试成绩分布更广
    END as score,
    CASE
        WHEN FLOOR(50 + RAND() * 50) >= 90 THEN 'A'
        WHEN FLOOR(50 + RAND() * 50) >= 80 THEN 'B'
        WHEN FLOOR(50 + RAND() * 50) >= 70 THEN 'C'
        WHEN FLOOR(50 + RAND() * 50) >= 60 THEN 'D'
        ELSE 'F'
    END as grade_level,
    c.semester,
    c.academic_year,
    DATE_SUB(CURDATE(), INTERVAL FLOOR(RAND() * 90) DAY) as exam_date,
    c.teacher_id,
    1 as status,
    0 as deleted
FROM temp_numbers n
JOIN tb_course_selection cs ON cs.id = ((n.num - 1) % (SELECT COUNT(*) FROM tb_course_selection WHERE selection_status = '已确认')) + 1
JOIN tb_course c ON c.id = cs.course_id
WHERE n.num <= 30000 AND cs.selection_status = '已确认';

SELECT '✓ 成绩数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 6. 生成考试数据
-- =====================================================

SELECT '步骤5: 生成考试数据...' as '状态', NOW() as '时间';

-- 为每门课程生成2-3次考试
INSERT INTO tb_exam (course_id, exam_name, exam_type, exam_date, start_time, end_time, classroom_id, teacher_id, total_score, duration_minutes, exam_description, status, deleted)
SELECT
    c.id as course_id,
    CONCAT(c.course_name,
        CASE
            WHEN n.num % 3 = 1 THEN '期中考试'
            WHEN n.num % 3 = 2 THEN '期末考试'
            ELSE '随堂测验'
        END
    ) as exam_name,
    CASE
        WHEN n.num % 3 = 1 THEN '期中考试'
        WHEN n.num % 3 = 2 THEN '期末考试'
        ELSE '随堂测验'
    END as exam_type,
    DATE_ADD(CURDATE(), INTERVAL FLOOR(RAND() * 60) DAY) as exam_date,
    CASE
        WHEN n.num % 4 = 1 THEN '08:00:00'
        WHEN n.num % 4 = 2 THEN '10:00:00'
        WHEN n.num % 4 = 3 THEN '14:00:00'
        ELSE '16:00:00'
    END as start_time,
    CASE
        WHEN n.num % 4 = 1 THEN '10:00:00'
        WHEN n.num % 4 = 2 THEN '12:00:00'
        WHEN n.num % 4 = 3 THEN '16:00:00'
        ELSE '18:00:00'
    END as end_time,
    c.classroom_id,
    c.teacher_id,
    CASE
        WHEN n.num % 3 = 0 THEN 50  -- 随堂测验
        ELSE 100  -- 期中期末考试
    END as total_score,
    CASE
        WHEN n.num % 3 = 0 THEN 60  -- 随堂测验1小时
        ELSE 120  -- 期中期末考试2小时
    END as duration_minutes,
    CONCAT(c.course_name, '考试说明') as exam_description,
    CASE WHEN n.num % 20 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
JOIN tb_course c ON c.id = ((n.num - 1) % 1000) + 1
WHERE n.num <= 2500;

SELECT '✓ 考试数据生成完成' as '状态', NOW() as '时间';

-- =====================================================
-- 7. 生成考试记录数据
-- =====================================================

SELECT '步骤6: 生成考试记录数据...' as '状态', NOW() as '时间';

-- 为学生生成考试记录
INSERT INTO tb_exam_record (student_id, exam_id, score, submit_time, exam_duration, answer_sheet, status, deleted)
SELECT
    ((n.num - 1) % 10000) + 1 as student_id,
    ((n.num - 1) % 2500) + 1 as exam_id,
    FLOOR(40 + RAND() * 60) as score,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY) as submit_time,
    FLOOR(60 + RAND() * 60) as exam_duration,
    CONCAT('答题内容', n.num) as answer_sheet,
    CASE WHEN n.num % 50 = 0 THEN 0 ELSE 1 END as status,
    0 as deleted
FROM temp_numbers n
WHERE n.num <= 25000;

SELECT '✓ 考试记录数据生成完成' as '状态', NOW() as '时间';

COMMIT;

SELECT '=== 业务数据生成第二阶段完成 ===' as '状态', NOW() as '时间';
