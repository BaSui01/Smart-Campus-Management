-- =====================================================
-- 智慧校园管理平台 - 测试数据脚本
-- 版本: 1.0.0
-- 创建时间: 2025-06-03
-- 描述: 插入开发调试用的测试数据
-- =====================================================

USE campus_management;

-- ===========================================
-- 测试教师数据
-- ===========================================

-- 创建测试教师用户
INSERT INTO tb_user (username, password, email, real_name, phone, status) VALUES
('teacher001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'zhang.teacher@campus.edu', '张伟', '13800001001', 1),
('teacher002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'li.teacher@campus.edu', '李明', '13800001002', 1),
('teacher003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'wang.teacher@campus.edu', '王磊', '13800001003', 1),
('teacher004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'chen.teacher@campus.edu', '陈慧', '13800001004', 1);

-- 绑定教师角色
INSERT INTO tb_user_role (user_id, role_id) VALUES
(2, 2), (3, 2), (4, 2), (5, 2);

-- 创建教师详细信息
INSERT INTO tb_teacher (user_id, teacher_no, department, title, hire_date, status) VALUES
(2, 'T2024001', '计算机科学与技术学院', '副教授', '2020-09-01', 1),
(3, 'T2024002', '数学与应用数学学院', '讲师', '2021-03-01', 1),
(4, 'T2024003', '英语语言文学学院', '助教', '2022-09-01', 1),
(5, 'T2024004', '物理学院', '教授', '2018-09-01', 1);

-- ===========================================
-- 测试班级数据
-- ===========================================

-- 创建测试班级
INSERT INTO tb_class (class_name, class_no, grade, head_teacher_id, max_students, current_students, status) VALUES
('计算机科学2023-1班', 'CS2023-1', '2023级', 1, 30, 0, 1),
('计算机科学2023-2班', 'CS2023-2', '2023级', 2, 30, 0, 1),
('数学2023-1班', 'MATH2023-1', '2023级', 3, 25, 0, 1),
('英语2023-1班', 'ENG2023-1', '2023级', 4, 25, 0, 1);

-- ===========================================
-- 测试学生数据
-- ===========================================

-- 创建测试学生用户
INSERT INTO tb_user (username, password, email, real_name, phone, status) VALUES
('student001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'wang.xiaoming@stu.campus.edu', '王小明', '13900001001', 1),
('student002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'li.xiaohong@stu.campus.edu', '李小红', '13900001002', 1),
('student003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'zhang.ming@stu.campus.edu', '张明', '13900001003', 1),
('student004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'liu.hua@stu.campus.edu', '刘华', '13900001004', 1),
('student005', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'chen.lei@stu.campus.edu', '陈磊', '13900001005', 1),
('student006', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'zhao.mei@stu.campus.edu', '赵美', '13900001006', 1);

-- 绑定学生角色
INSERT INTO tb_user_role (user_id, role_id) VALUES
(6, 3), (7, 3), (8, 3), (9, 3), (10, 3), (11, 3);

-- 创建学生详细信息
INSERT INTO tb_student (user_id, student_no, grade, class_id, enrollment_date, status) VALUES
(6, '2023001001', '2023级', 1, '2023-09-01', 1),
(7, '2023001002', '2023级', 1, '2023-09-01', 1),
(8, '2023001003', '2023级', 2, '2023-09-01', 1),
(9, '2023001004', '2023级', 2, '2023-09-01', 1),
(10, '2023001005', '2023级', 3, '2023-09-01', 1),
(11, '2023001006', '2023级', 4, '2023-09-01', 1);

-- 更新班级学生数量
UPDATE tb_class SET current_students = 2 WHERE id IN (1, 2);
UPDATE tb_class SET current_students = 1 WHERE id IN (3, 4);

-- ===========================================
-- 测试家长数据
-- ===========================================

-- 创建测试家长用户
INSERT INTO tb_user (username, password, email, real_name, phone, status) VALUES
('parent001', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'wang.father@campus.edu', '王建国', '13700001001', 1),
('parent002', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'wang.mother@campus.edu', '张丽华', '13700001002', 1),
('parent003', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'li.father@campus.edu', '李大明', '13700001003', 1),
('parent004', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBaLzQKW5QxJFy', 'zhang.mother@campus.edu', '赵小芳', '13700001004', 1);

-- 绑定家长角色
INSERT INTO tb_user_role (user_id, role_id) VALUES
(12, 4), (13, 4), (14, 4), (15, 4);

-- 创建家长详细信息
INSERT INTO tb_parent (user_id, relationship, occupation) VALUES
(12, '父亲', '工程师'),
(13, '母亲', '教师'),
(14, '父亲', '医生'),
(15, '母亲', '会计师');

-- 创建学生家长关联关系
INSERT INTO tb_student_parent (student_id, parent_id, relationship, is_primary) VALUES
(1, 1, '父亲', 1),  -- 王小明的父亲王建国
(1, 2, '母亲', 0),  -- 王小明的母亲张丽华
(2, 3, '父亲', 1),  -- 李小红的父亲李大明
(3, 4, '母亲', 1);  -- 张明的母亲赵小芳

-- ===========================================
-- 测试课程数据
-- ===========================================

-- 创建测试课程
INSERT INTO tb_course (course_code, course_name, credits, course_type, department, description, status) VALUES
('CS101', 'Java程序设计', 4.0, '必修', '计算机科学与技术学院', 'Java语言基础及面向对象编程', 1),
('CS102', '数据结构与算法', 4.0, '必修', '计算机科学与技术学院', '基本数据结构和算法设计与分析', 1),
('MATH101', '高等数学', 5.0, '必修', '数学与应用数学学院', '微积分基础理论与应用', 1),
('MATH102', '线性代数', 3.0, '必修', '数学与应用数学学院', '矩阵理论与线性方程组', 1),
('ENG101', '大学英语', 3.0, '必修', '英语语言文学学院', '基础英语听说读写能力培养', 1),
('PHY101', '大学物理', 4.0, '选修', '物理学院', '物理学基础理论与实验', 1);

-- ===========================================
-- 测试课程安排数据
-- ===========================================

-- 创建课程安排
INSERT INTO tb_course_schedule (course_id, teacher_id, class_id, semester, day_of_week, start_time, end_time, classroom, max_students, current_students, status) VALUES
-- 2024春季学期
(1, 1, 1, '2024春', 1, '08:00:00', '09:40:00', 'A101', 30, 2, 1),  -- Java程序设计 CS2023-1班
(1, 1, 2, '2024春', 1, '10:00:00', '11:40:00', 'A102', 30, 2, 1),  -- Java程序设计 CS2023-2班
(2, 1, 1, '2024春', 3, '14:00:00', '15:40:00', 'A201', 30, 2, 1),  -- 数据结构 CS2023-1班
(3, 2, NULL, '2024春', 2, '08:00:00', '09:40:00', 'B101', 60, 4, 1), -- 高等数学 选修课
(4, 2, NULL, '2024春', 4, '10:00:00', '11:40:00', 'B102', 40, 2, 1), -- 线性代数 选修课
(5, 3, NULL, '2024春', 5, '14:00:00', '15:40:00', 'C101', 50, 3, 1), -- 大学英语 选修课
(6, 4, NULL, '2024春', 3, '16:00:00', '17:40:00', 'D101', 40, 1, 1); -- 大学物理 选修课

-- ===========================================
-- 测试选课数据
-- ===========================================

-- 学生选课记录
INSERT INTO tb_course_selection (student_id, schedule_id, selection_time, status) VALUES
-- 王小明(student_id=1)选课
(1, 1, '2024-02-20 09:00:00', 1),  -- Java程序设计
(1, 3, '2024-02-20 09:05:00', 1),  -- 数据结构
(1, 4, '2024-02-20 09:10:00', 1),  -- 高等数学
(1, 6, '2024-02-20 09:15:00', 1),  -- 大学英语

-- 李小红(student_id=2)选课
(2, 1, '2024-02-20 10:00:00', 1),  -- Java程序设计
(2, 3, '2024-02-20 10:05:00', 1),  -- 数据结构
(2, 4, '2024-02-20 10:10:00', 1),  -- 高等数学

-- 张明(student_id=3)选课
(3, 2, '2024-02-20 11:00:00', 1),  -- Java程序设计
(3, 4, '2024-02-20 11:05:00', 1),  -- 高等数学
(3, 6, '2024-02-20 11:10:00', 1),  -- 大学英语

-- 刘华(student_id=4)选课
(4, 2, '2024-02-20 12:00:00', 1),  -- Java程序设计
(4, 5, '2024-02-20 12:05:00', 1),  -- 线性代数

-- 陈磊(student_id=5)选课
(5, 4, '2024-02-20 13:00:00', 1),  -- 高等数学
(5, 5, '2024-02-20 13:05:00', 1),  -- 线性代数
(5, 6, '2024-02-20 13:10:00', 1),  -- 大学英语

-- 赵美(student_id=6)选课
(6, 7, '2024-02-20 14:00:00', 1);   -- 大学物理

-- ===========================================
-- 测试成绩数据
-- ===========================================

-- 学生成绩记录
INSERT INTO tb_grade (student_id, course_id, semester, usual_score, exam_score, final_score, grade_level, is_pass, teacher_id, remarks) VALUES
-- 王小明的成绩
(1, 1, '2024春', 85.0, 88.0, 87.0, 'B', 1, 1, '表现优秀'),
(1, 2, '2024春', 92.0, 89.0, 90.0, 'A', 1, 1, '算法理解能力强'),
(1, 3, '2024春', 78.0, 82.0, 80.0, 'B', 1, 2, '需要加强练习'),

-- 李小红的成绩
(2, 1, '2024春', 88.0, 85.0, 86.0, 'B', 1, 1, '编程能力不错'),
(2, 2, '2024春', 85.0, 87.0, 86.0, 'B', 1, 1, '逻辑思维清晰'),
(2, 3, '2024春', 90.0, 88.0, 89.0, 'B', 1, 2, '数学基础扎实'),

-- 张明的成绩
(3, 1, '2024春', 75.0, 78.0, 77.0, 'C', 1, 1, '需要多练习'),
(3, 3, '2024春', 82.0, 85.0, 84.0, 'B', 1, 2, '进步明显'),

-- 刘华的成绩
(4, 1, '2024春', 95.0, 92.0, 93.0, 'A', 1, 1, '优秀学生'),
(4, 4, '2024春', 88.0, 85.0, 86.0, 'B', 1, 2, '理解能力强');

-- ===========================================
-- 测试考试数据
-- ===========================================

-- 创建测试考试
INSERT INTO tb_exam (exam_name, exam_code, course_id, exam_type, semester, exam_date, start_time, end_time, duration, total_score, pass_score, exam_mode, exam_status, teacher_id, description) VALUES
('Java程序设计期中考试', 'EXAM_CS101_MID_2024S', 1, '期中', '2024春', '2024-04-15', '14:00:00', '16:00:00', 120, 100.00, 60.00, 'OFFLINE', 'FINISHED', 1, 'Java基础语法和面向对象概念'),
('高等数学期末考试', 'EXAM_MATH101_FINAL_2024S', 3, '期末', '2024春', '2024-06-20', '09:00:00', '11:00:00', 120, 100.00, 60.00, 'OFFLINE', 'FINISHED', 2, '微积分综合应用'),
('数据结构期中考试', 'EXAM_CS102_MID_2024S', 2, '期中', '2024春', '2024-04-20', '14:00:00', '16:00:00', 120, 100.00, 60.00, 'ONLINE', 'PUBLISHED', 1, '基础数据结构和算法');

-- ===========================================
-- 测试题库数据
-- ===========================================

-- 创建测试题目
INSERT INTO tb_question_bank (question_code, course_id, chapter, question_type, difficulty_level, question_content, option_a, option_b, option_c, option_d, correct_answer, answer_analysis, score, creator_id, status) VALUES
-- Java程序设计题目
('Q_CS101_001', 1, '第一章 Java基础', 'SINGLE', 'EASY', 'Java中哪个关键字用于定义类？', 'class', 'Class', 'public', 'static', 'A', 'class是Java中定义类的关键字', 2.00, 1, 1),
('Q_CS101_002', 1, '第二章 面向对象', 'MULTIPLE', 'MEDIUM', 'Java面向对象的特性包括哪些？', '封装', '继承', '多态', '抽象', 'A,B,C,D', '面向对象的四大特性：封装、继承、多态、抽象', 3.00, 1, 1),
('Q_CS101_003', 1, '第三章 异常处理', 'JUDGE', 'EASY', 'try-catch语句可以处理Java中的异常。', '', '', '', '', 'TRUE', 'try-catch是Java异常处理的基本语法', 1.00, 1, 1),

-- 高等数学题目
('Q_MATH101_001', 3, '第一章 极限', 'SINGLE', 'MEDIUM', '函数f(x)=sin(x)/x在x趋向0时的极限是多少？', '0', '1', '无穷大', '不存在', 'B', '这是重要极限之一，sin(x)/x在x趋向0时极限为1', 3.00, 2, 1),
('Q_MATH101_002', 3, '第二章 导数', 'FILL', 'HARD', '函数f(x)=x²的导数是____。', '', '', '', '', '2x', '根据幂函数求导法则，x的n次方的导数是nx的n-1次方', 4.00, 2, 1),

-- 数据结构题目
('Q_CS102_001', 2, '第一章 线性表', 'SINGLE', 'MEDIUM', '数组和链表相比，数组的优势是什么？', '插入效率高', '删除效率高', '随机访问效率高', '空间利用率高', 'C', '数组支持常数时间复杂度的随机访问', 3.00, 1, 1);

-- ===========================================
-- 测试缴费记录
-- ===========================================

-- 学生缴费记录
INSERT INTO tb_payment_record (student_id, fee_item_id, amount, payment_method, payment_time, transaction_no, operator_id, remarks, status) VALUES
-- 王小明的缴费记录
(1, 1, 5000.00, '银行卡', '2024-02-25 10:30:00', 'TXN202402250001', 1, '2024春季学费', 1),
(1, 2, 1200.00, '微信', '2024-02-25 10:35:00', 'WX202402250001', 1, '2024春季住宿费', 1),
(1, 3, 300.00, '支付宝', '2024-03-10 14:20:00', 'ZFB202403100001', 1, '2024春季教材费', 1),

-- 李小红的缴费记录
(2, 1, 5000.00, '现金', '2024-02-26 09:15:00', 'CASH202402260001', 1, '2024春季学费', 1),
(2, 2, 1200.00, '银行卡', '2024-02-26 09:20:00', 'TXN202402260001', 1, '2024春季住宿费', 1),

-- 张明的缴费记录
(3, 1, 5000.00, '银行卡', '2024-02-28 16:45:00', 'TXN202402280001', 1, '2024春季学费', 1),
(3, 3, 300.00, '微信', '2024-03-12 11:30:00', 'WX202403120001', 1, '2024春季教材费', 1);

-- ===========================================
-- 测试消息数据
-- ===========================================

-- 发送测试消息
INSERT INTO tb_message (message_code, template_id, sender_id, title, content, message_type, priority_level, target_type, target_users, sent_time, status, total_recipients, sent_count, read_count) VALUES
('MSG_2024_001', 1, 1, 'Java程序设计成绩已发布', '您好，王小明同学！您的Java程序设计课程成绩已发布：总成绩：87分，等级：B。请及时登录系统查看详细成绩信息。', 'NOTICE', 'NORMAL', 'USER', '[1]', '2024-05-15 16:30:00', 'SENT', 1, 1, 0),
('MSG_2024_002', 3, 1, 'Java程序设计期中考试考试提醒', '您好，李小红同学！提醒您：考试名称：Java程序设计期中考试，考试时间：2024-04-15 14:00-16:00，考试地点：A101。请提前30分钟到达考场。', 'REMINDER', 'HIGH', 'USER', '[2]', '2024-04-14 10:00:00', 'SENT', 1, 1, 1),
('MSG_2024_003', 4, 1, '学费缴费提醒', '您好，张明的家长！学费缴费即将到期：缴费金额：5000元，截止日期：2024-03-01。请及时完成缴费。', 'REMINDER', 'HIGH', 'USER', '[4]', '2024-02-20 09:00:00', 'SENT', 1, 1, 1);

-- 消息阅读状态
INSERT INTO tb_message_read (message_id, user_id, is_read, read_time, is_starred, is_deleted) VALUES
(1, 6, 0, NULL, 0, 0),        -- 王小明未读成绩通知
(2, 7, 1, '2024-04-14 15:30:00', 1, 0),  -- 李小红已读考试提醒并收藏
(3, 15, 1, '2024-02-20 20:30:00', 0, 0); -- 张明母亲已读缴费提醒

-- ===========================================
-- 验证测试数据插入结果
-- ===========================================

SELECT '测试数据初始化完成' AS message;
SELECT 'tb_teacher' AS table_name, COUNT(*) AS record_count FROM tb_teacher
UNION ALL
SELECT 'tb_student', COUNT(*) FROM tb_student
UNION ALL
SELECT 'tb_parent', COUNT(*) FROM tb_parent
UNION ALL
SELECT 'tb_class', COUNT(*) FROM tb_class
UNION ALL
SELECT 'tb_course', COUNT(*) FROM tb_course
UNION ALL
SELECT 'tb_course_schedule', COUNT(*) FROM tb_course_schedule
UNION ALL
SELECT 'tb_course_selection', COUNT(*) FROM tb_course_selection
UNION ALL
SELECT 'tb_grade', COUNT(*) FROM tb_grade
UNION ALL
SELECT 'tb_exam', COUNT(*) FROM tb_exam
UNION ALL
SELECT 'tb_question_bank', COUNT(*) FROM tb_question_bank
UNION ALL
SELECT 'tb_payment_record', COUNT(*) FROM tb_payment_record
UNION ALL
SELECT 'tb_message', COUNT(*) FROM tb_message
UNION ALL
SELECT 'tb_message_read', COUNT(*) FROM tb_message_read;

-- 显示一些关键统计信息
SELECT '=== 用户统计 ===' AS info;
SELECT
    r.role_name,
    COUNT(ur.user_id) as user_count
FROM tb_role r
LEFT JOIN tb_user_role ur ON r.id = ur.role_id
GROUP BY r.id, r.role_name;

SELECT '=== 班级学生统计 ===' AS info;
SELECT
    c.class_name,
    c.current_students,
    COUNT(s.id) as actual_students
FROM tb_class c
LEFT JOIN tb_student s ON c.id = s.class_id
GROUP BY c.id, c.class_name, c.current_students;

SELECT '=== 选课统计 ===' AS info;
SELECT
    co.course_name,
    cs.semester,
    cs.max_students,
    COUNT(sel.id) as selected_students
FROM tb_course co
JOIN tb_course_schedule cs ON co.id = cs.course_id
LEFT JOIN tb_course_selection sel ON cs.id = sel.schedule_id AND sel.status = 1
GROUP BY co.id, co.course_name, cs.semester, cs.max_students;
