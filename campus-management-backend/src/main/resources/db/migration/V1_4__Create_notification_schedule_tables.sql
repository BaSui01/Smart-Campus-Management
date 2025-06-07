-- 创建通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) NOT NULL COMMENT '通知类型',
    target_audience VARCHAR(50) NOT NULL COMMENT '目标受众',
    sender_id BIGINT COMMENT '发送者ID',
    sender_name VARCHAR(100) COMMENT '发送者姓名',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '通知状态',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级',
    is_pinned BOOLEAN DEFAULT FALSE COMMENT '是否置顶',
    publish_time DATETIME COMMENT '发布时间',
    expire_time DATETIME COMMENT '过期时间',
    read_count INT DEFAULT 0 COMMENT '阅读次数',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建者ID',
    updated_by BIGINT COMMENT '更新者ID'
) COMMENT='通知表';

-- 创建课表表
CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) NOT NULL COMMENT '课程代码',
    teacher_id BIGINT COMMENT '教师ID',
    teacher_name VARCHAR(100) COMMENT '教师姓名',
    class_id BIGINT COMMENT '班级ID',
    class_name VARCHAR(100) COMMENT '班级名称',
    classroom VARCHAR(50) NOT NULL COMMENT '教室',
    weekday INT NOT NULL COMMENT '星期几(1-7)',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    semester VARCHAR(50) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    week_start INT COMMENT '周次范围开始',
    week_end INT COMMENT '周次范围结束',
    status VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '课表状态',
    remarks VARCHAR(500) COMMENT '备注',
    has_conflict BOOLEAN DEFAULT FALSE COMMENT '是否有冲突',
    conflict_description VARCHAR(500) COMMENT '冲突描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by BIGINT COMMENT '创建者ID',
    updated_by BIGINT COMMENT '更新者ID'
) COMMENT='课表表';

-- 插入通知测试数据
INSERT INTO notifications (title, content, type, target_audience, sender_name, status, priority, publish_time, create_time) VALUES
('系统维护通知', '系统将于今晚22:00-24:00进行维护，请提前保存数据。', 'SYSTEM', 'ALL', '系统管理员', 'PUBLISHED', 'HIGH', NOW(), NOW()),
('课程安排更新', '计算机科学课程时间已调整，请查看最新安排。', 'ACADEMIC', 'STUDENTS', '教务处', 'PUBLISHED', 'NORMAL', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR),
('学费缴纳提醒', '请及时缴纳本学期学费，截止日期为本月底。', 'FINANCE', 'STUDENTS', '财务处', 'PUBLISHED', 'URGENT', NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY),
('新学期开学通知', '新学期将于下周一正式开始，请做好准备。', 'ACADEMIC', 'ALL', '教务处', 'DRAFT', 'NORMAL', NULL, NOW() - INTERVAL 3 DAY),
('图书馆开放时间调整', '图书馆开放时间调整为8:00-22:00。', 'SYSTEM', 'ALL', '图书馆', 'PUBLISHED', 'LOW', NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR);

-- 插入课表测试数据
INSERT INTO schedules (course_id, course_name, course_code, teacher_name, classroom, weekday, start_time, end_time, semester, academic_year, status, create_time) VALUES
(1, 'Java程序设计', 'CS101', '张教授', 'A101', 1, '08:00:00', '09:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(2, '数据结构', 'CS102', '李教授', 'A102', 2, '10:00:00', '11:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(3, '数据库原理', 'CS103', '王教授', 'A103', 3, '14:00:00', '15:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(4, '操作系统', 'CS104', '赵教授', 'A104', 4, '16:00:00', '17:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(5, '计算机网络', 'CS105', '钱教授', 'A105', 5, '08:00:00', '09:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(1, 'Java程序设计实验', 'CS101L', '张教授', 'B201', 1, '14:00:00', '15:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(2, '数据结构实验', 'CS102L', '李教授', 'B202', 2, '16:00:00', '17:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(6, '高等数学', 'MATH101', '孙教授', 'C101', 1, '10:00:00', '11:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(7, '线性代数', 'MATH102', '周教授', 'C102', 3, '08:00:00', '09:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(8, '大学英语', 'ENG101', '吴教授', 'D101', 2, '14:00:00', '15:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(9, '体育', 'PE101', '郑教授', '体育馆', 4, '14:00:00', '15:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW()),
(10, '思想政治理论', 'POL101', '王老师', 'E101', 5, '10:00:00', '11:40:00', '2024春季学期', '2023-2024', 'NORMAL', NOW());

-- 创建索引
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_type ON notifications(type);
CREATE INDEX idx_notifications_target_audience ON notifications(target_audience);
CREATE INDEX idx_notifications_publish_time ON notifications(publish_time);

CREATE INDEX idx_schedules_semester ON schedules(semester);
CREATE INDEX idx_schedules_weekday ON schedules(weekday);
CREATE INDEX idx_schedules_classroom ON schedules(classroom);
CREATE INDEX idx_schedules_teacher_id ON schedules(teacher_id);
CREATE INDEX idx_schedules_course_id ON schedules(course_id);
CREATE INDEX idx_schedules_class_id ON schedules(class_id);
CREATE INDEX idx_schedules_time ON schedules(weekday, start_time, end_time);
