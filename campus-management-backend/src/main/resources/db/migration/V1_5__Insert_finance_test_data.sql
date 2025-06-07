-- 插入收费项目测试数据
INSERT INTO fee_items (item_name, item_code, amount, fee_type, category, description, is_mandatory, is_active, effective_time, created_time, updated_time) VALUES
('学费', 'TUITION_2024_SPRING', 5000.00, 'TUITION', 'MANDATORY', '2024春季学期学费', TRUE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('住宿费', 'ACCOMMODATION_2024_SPRING', 1200.00, 'ACCOMMODATION', 'MANDATORY', '2024春季学期住宿费', TRUE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('教材费', 'TEXTBOOK_2024_SPRING', 300.00, 'TEXTBOOK', 'AGENCY', '2024春季学期教材费', FALSE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('实验费', 'LAB_2024_SPRING', 200.00, 'TRAINING', 'MANDATORY', '2024春季学期实验费', TRUE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('保险费', 'INSURANCE_2024', 100.00, 'INSURANCE', 'OPTIONAL', '2024年度学生保险费', FALSE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('体检费', 'MEDICAL_2024', 80.00, 'OTHER', 'MANDATORY', '2024年度体检费', TRUE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('网络费', 'NETWORK_2024_SPRING', 50.00, 'SERVICE', 'OPTIONAL', '2024春季学期网络使用费', FALSE, TRUE, '2024-01-01 00:00:00', NOW(), NOW()),
('图书证工本费', 'LIBRARY_CARD_2024', 20.00, 'OTHER', 'AGENCY', '图书证工本费', FALSE, TRUE, '2024-01-01 00:00:00', NOW(), NOW());

-- 为现有学生生成缴费记录
-- 假设学生ID从1到50
INSERT INTO payment_records (student_id, student_name, fee_item_id, fee_item_name, amount, status, due_date, create_time, update_time)
SELECT 
    s.id as student_id,
    s.name as student_name,
    fi.id as fee_item_id,
    fi.item_name as fee_item_name,
    fi.amount,
    CASE 
        WHEN RAND() < 0.7 THEN '已缴费'
        WHEN RAND() < 0.9 THEN '待缴费'
        ELSE '逾期'
    END as status,
    DATE_ADD(NOW(), INTERVAL 30 DAY) as due_date,
    NOW() as create_time,
    NOW() as update_time
FROM students s
CROSS JOIN fee_items fi
WHERE s.id <= 50 AND fi.is_active = TRUE;

-- 为已缴费的记录设置缴费时间和缴费方式
UPDATE payment_records 
SET 
    payment_time = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    payment_method = CASE 
        WHEN RAND() < 0.4 THEN '银行转账'
        WHEN RAND() < 0.7 THEN '支付宝'
        WHEN RAND() < 0.9 THEN '微信支付'
        ELSE '现金'
    END,
    operator = '财务处'
WHERE status = '已缴费';

-- 为逾期记录设置逾期时间
UPDATE payment_records 
SET due_date = DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 10 + 1) DAY)
WHERE status = '逾期';

-- 插入一些额外的通知数据
INSERT INTO notifications (title, content, type, target_audience, sender_name, status, priority, publish_time, create_time) VALUES
('缴费提醒', '请各位同学及时缴纳本学期相关费用，避免影响正常学习。', 'FINANCE', 'STUDENTS', '财务处', 'PUBLISHED', 'NORMAL', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('教室调整通知', '由于设备维护，部分教室使用安排有所调整。', 'ACADEMIC', 'ALL', '教务处', 'PUBLISHED', 'HIGH', NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('期末考试安排', '期末考试时间安排已发布，请查看详细安排。', 'ACADEMIC', 'STUDENTS', '教务处', 'PUBLISHED', 'URGENT', NOW() - INTERVAL 6 HOUR, NOW() - INTERVAL 6 HOUR),
('校园网络升级', '校园网络将于本周末进行升级，期间可能影响网络使用。', 'SYSTEM', 'ALL', '网络中心', 'PUBLISHED', 'NORMAL', NOW() - INTERVAL 12 HOUR, NOW() - INTERVAL 12 HOUR),
('社团活动通知', '本周六将举办社团招新活动，欢迎同学们参加。', 'ACTIVITY', 'STUDENTS', '学生处', 'PUBLISHED', 'LOW', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
('教师培训通知', '下周将举办教学方法培训，请相关教师准时参加。', 'ACADEMIC', 'TEACHERS', '人事处', 'PUBLISHED', 'NORMAL', NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY),
('停电通知', '明日上午9:00-12:00将进行电力设备检修，部分区域停电。', 'MAINTENANCE', 'ALL', '后勤处', 'PUBLISHED', 'HIGH', NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY),
('奖学金评选通知', '本学期奖学金评选工作即将开始，请符合条件的同学准备材料。', 'ACADEMIC', 'STUDENTS', '学生处', 'DRAFT', 'NORMAL', NULL, NOW() - INTERVAL 4 DAY);

-- 更新一些通知的阅读次数
UPDATE notifications SET read_count = FLOOR(RAND() * 100 + 10) WHERE status = 'PUBLISHED';

-- 设置一些通知为置顶
UPDATE notifications SET is_pinned = TRUE WHERE priority = 'URGENT' OR priority = 'HIGH' LIMIT 3;

-- 插入更多课表数据（不同学期）
INSERT INTO schedules (course_id, course_name, course_code, teacher_name, classroom, weekday, start_time, end_time, semester, academic_year, status, create_time) VALUES
-- 2024秋季学期课表
(11, '软件工程', 'CS201', '陈教授', 'A201', 1, '08:00:00', '09:40:00', '2024秋季学期', '2024-2025', 'NORMAL', NOW()),
(12, '算法设计与分析', 'CS202', '刘教授', 'A202', 2, '10:00:00', '11:40:00', '2024秋季学期', '2024-2025', 'NORMAL', NOW()),
(13, '人工智能', 'CS203', '杨教授', 'A203', 3, '14:00:00', '15:40:00', '2024秋季学期', '2024-2025', 'NORMAL', NOW()),
(14, '机器学习', 'CS204', '黄教授', 'A204', 4, '16:00:00', '17:40:00', '2024秋季学期', '2024-2025', 'NORMAL', NOW()),
(15, '数据挖掘', 'CS205', '林教授', 'A205', 5, '08:00:00', '09:40:00', '2024秋季学期', '2024-2025', 'NORMAL', NOW()),

-- 一些调课记录
(1, 'Java程序设计', 'CS101', '张教授', 'B101', 2, '08:00:00', '09:40:00', '2024春季学期', '2023-2024', 'ADJUSTED', NOW()),
(2, '数据结构', 'CS102', '李教授', 'B102', 3, '10:00:00', '11:40:00', '2024春季学期', '2023-2024', 'ADJUSTED', NOW());

-- 创建一些冲突的课表（用于测试冲突检测）
INSERT INTO schedules (course_id, course_name, course_code, teacher_name, classroom, weekday, start_time, end_time, semester, academic_year, status, has_conflict, conflict_description, create_time) VALUES
(16, '测试课程A', 'TEST101', '测试教师A', 'A101', 1, '08:30:00', '10:10:00', '2024春季学期', '2023-2024', 'NORMAL', TRUE, '教室冲突: 与Java程序设计时间重叠', NOW()),
(17, '测试课程B', 'TEST102', '张教授', 'C201', 1, '08:00:00', '09:40:00', '2024春季学期', '2023-2024', 'NORMAL', TRUE, '教师冲突: 张教授同时间段有其他课程', NOW());

-- 更新统计信息
-- 这里可以添加一些触发器或存储过程来自动维护统计信息
