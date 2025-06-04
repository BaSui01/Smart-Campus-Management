-- =====================================================
-- 智慧校园管理平台 - 表结构创建脚本
-- 版本: 1.0.0
-- 创建时间: 2025-06-04
-- 描述: 创建所有业务表结构
-- =====================================================

USE campus_management;
SET FOREIGN_KEY_CHECKS = 0;

-- ===========================================
-- 用户管理模块
-- ===========================================

-- 1. 用户基础表
CREATE TABLE tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='用户基础表';

-- 2. 角色表
CREATE TABLE tb_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='角色表';

-- 3. 用户角色关联表
CREATE TABLE tb_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT='用户角色关联表';

-- 4. 学生表
CREATE TABLE tb_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(20) COMMENT '年级',
    class_id BIGINT COMMENT '班级ID',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在读，2-毕业，3-休学，0-退学',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE
) COMMENT='学生表';

-- 5. 教师表
CREATE TABLE tb_teacher (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '教师ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    teacher_no VARCHAR(50) NOT NULL UNIQUE COMMENT '教师工号',
    department VARCHAR(100) COMMENT '所属部门',
    title VARCHAR(50) COMMENT '职称',
    hire_date DATE COMMENT '入职日期',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在职，0-离职',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE
) COMMENT='教师表';

-- 6. 家长表
CREATE TABLE tb_parent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '家长ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    relationship VARCHAR(20) COMMENT '与学生关系',
    occupation VARCHAR(100) COMMENT '职业',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE
) COMMENT='家长表';

-- 7. 学生家长关联表
CREATE TABLE tb_student_parent (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    parent_id BIGINT NOT NULL COMMENT '家长ID',
    relationship VARCHAR(20) COMMENT '关系',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主要联系人：1-是，0-否',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES tb_parent(id) ON DELETE CASCADE,
    UNIQUE KEY uk_student_parent (student_id, parent_id)
) COMMENT='学生家长关联表';

-- ===========================================
-- 教务管理模块
-- ===========================================

-- 8. 班级表
CREATE TABLE tb_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    class_no VARCHAR(50) NOT NULL UNIQUE COMMENT '班级编号',
    grade VARCHAR(20) COMMENT '年级',
    head_teacher_id BIGINT COMMENT '班主任ID',
    max_students INT DEFAULT 50 COMMENT '最大学生数',
    current_students INT DEFAULT 0 COMMENT '当前学生数',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-停用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (head_teacher_id) REFERENCES tb_teacher(id)
) COMMENT='班级表';

-- 9. 课程表
CREATE TABLE tb_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_code VARCHAR(50) NOT NULL UNIQUE COMMENT '课程编码',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    credits DECIMAL(3,1) COMMENT '学分',
    course_type VARCHAR(20) COMMENT '课程类型：必修/选修',
    department VARCHAR(100) COMMENT '开课部门',
    description TEXT COMMENT '课程描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-开课，0-停课',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='课程表';

-- 10. 课程安排表
CREATE TABLE tb_course_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    class_id BIGINT COMMENT '班级ID（可为空，表示选修课）',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    day_of_week TINYINT COMMENT '星期几：1-7',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    classroom VARCHAR(50) COMMENT '教室',
    max_students INT COMMENT '最大选课人数',
    current_students INT DEFAULT 0 COMMENT '当前选课人数',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-取消',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    FOREIGN KEY (teacher_id) REFERENCES tb_teacher(id),
    FOREIGN KEY (class_id) REFERENCES tb_class(id)
) COMMENT='课程安排表';

-- 11. 选课记录表
CREATE TABLE tb_course_selection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    schedule_id BIGINT NOT NULL COMMENT '课程安排ID',
    selection_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-已选，2-已退课',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES tb_course_schedule(id),
    UNIQUE KEY uk_student_schedule (student_id, schedule_id)
) COMMENT='选课记录表';

-- 12. 成绩表
CREATE TABLE tb_grade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    usual_score DECIMAL(5,2) COMMENT '平时成绩',
    exam_score DECIMAL(5,2) COMMENT '考试成绩',
    final_score DECIMAL(5,2) COMMENT '总成绩',
    grade_level VARCHAR(5) COMMENT '等级：A/B/C/D/F',
    is_pass TINYINT COMMENT '是否通过：1-通过，0-不通过',
    teacher_id BIGINT COMMENT '录入教师ID',
    remarks TEXT COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    FOREIGN KEY (teacher_id) REFERENCES tb_teacher(id),
    UNIQUE KEY uk_student_course_semester (student_id, course_id, semester)
) COMMENT='成绩表';

-- ===========================================
-- 考试管理模块
-- ===========================================

-- 13. 考试表
CREATE TABLE tb_exam (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试ID',
    exam_name VARCHAR(200) NOT NULL COMMENT '考试名称',
    exam_code VARCHAR(50) NOT NULL UNIQUE COMMENT '考试编码',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型：期中/期末/随堂/补考',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    exam_date DATE NOT NULL COMMENT '考试日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    duration INT NOT NULL COMMENT '考试时长（分钟）',
    total_score DECIMAL(5,2) DEFAULT 100.00 COMMENT '总分',
    pass_score DECIMAL(5,2) DEFAULT 60.00 COMMENT '及格分数',
    exam_mode VARCHAR(20) DEFAULT 'OFFLINE' COMMENT '考试模式：ONLINE-在线/OFFLINE-线下',
    exam_status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '考试状态：DRAFT-草稿/PUBLISHED-已发布/ONGOING-进行中/FINISHED-已结束',
    teacher_id BIGINT COMMENT '出题教师ID',
    description TEXT COMMENT '考试说明',
    exam_rules TEXT COMMENT '考试规则',
    allow_late_submit TINYINT DEFAULT 0 COMMENT '是否允许迟交：1-是，0-否',
    late_submit_penalty DECIMAL(3,2) DEFAULT 0.00 COMMENT '迟交扣分比例',
    shuffle_questions TINYINT DEFAULT 0 COMMENT '是否打乱题目顺序：1-是，0-否',
    shuffle_options TINYINT DEFAULT 0 COMMENT '是否打乱选项顺序：1-是，0-否',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    FOREIGN KEY (teacher_id) REFERENCES tb_teacher(id)
) COMMENT='考试表';

-- 14. 考试教室表
CREATE TABLE tb_exam_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '考试教室ID',
    room_name VARCHAR(100) NOT NULL COMMENT '教室名称',
    room_no VARCHAR(50) NOT NULL UNIQUE COMMENT '教室编号',
    capacity INT NOT NULL COMMENT '容量',
    floor VARCHAR(20) COMMENT '楼层',
    building VARCHAR(50) COMMENT '所属建筑',
    equipment TEXT COMMENT '设备配置',
    status TINYINT DEFAULT 1 COMMENT '状态：1-可用，0-维护中',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='考试教室表';

-- 15. 学生考试记录表
CREATE TABLE tb_exam_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    room_id BIGINT COMMENT '考试教室ID',
    seat_number VARCHAR(10) COMMENT '座位号',
    start_time DATETIME COMMENT '实际开始时间',
    submit_time DATETIME COMMENT '提交时间',
    exam_duration INT COMMENT '实际考试时长（分钟）',
    total_score DECIMAL(5,2) COMMENT '总分',
    exam_status VARCHAR(20) DEFAULT 'REGISTERED' COMMENT '考试状态：REGISTERED-已报名/ONGOING-考试中/SUBMITTED-已提交/GRADED-已评分/ABSENT-缺考',
    is_late_submit TINYINT DEFAULT 0 COMMENT '是否迟交：1-是，0-否',
    answer_content TEXT COMMENT '答案内容（JSON格式）',
    remarks TEXT COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (exam_id) REFERENCES tb_exam(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES tb_exam_room(id),
    UNIQUE KEY uk_exam_student (exam_id, student_id)
) COMMENT='学生考试记录表';

-- 16. 题库表
CREATE TABLE tb_question_bank (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '题目ID',
    question_code VARCHAR(50) UNIQUE COMMENT '题目编码',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    chapter VARCHAR(100) COMMENT '所属章节',
    question_type VARCHAR(20) NOT NULL COMMENT '题目类型：SINGLE-单选/MULTIPLE-多选/JUDGE-判断/FILL-填空/ESSAY-问答',
    difficulty_level VARCHAR(10) NOT NULL COMMENT '难度等级：EASY-简单/MEDIUM-中等/HARD-困难',
    question_content TEXT NOT NULL COMMENT '题目内容',
    option_a VARCHAR(500) COMMENT '选项A',
    option_b VARCHAR(500) COMMENT '选项B',
    option_c VARCHAR(500) COMMENT '选项C',
    option_d VARCHAR(500) COMMENT '选项D',
    correct_answer TEXT NOT NULL COMMENT '正确答案',
    answer_analysis TEXT COMMENT '答案解析',
    score DECIMAL(4,2) DEFAULT 1.00 COMMENT '题目分值',
    usage_count INT DEFAULT 0 COMMENT '使用次数',
    creator_id BIGINT COMMENT '创建者ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (course_id) REFERENCES tb_course(id),
    FOREIGN KEY (creator_id) REFERENCES tb_teacher(id)
) COMMENT='题库表';

-- ===========================================
-- 财务管理模块
-- ===========================================

-- 17. 缴费项目表
CREATE TABLE tb_fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缴费项目ID',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) NOT NULL UNIQUE COMMENT '项目编码',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    fee_type VARCHAR(20) COMMENT '费用类型：学费/杂费/书费等',
    applicable_grade VARCHAR(50) COMMENT '适用年级',
    due_date DATE COMMENT '缴费截止日期',
    description TEXT COMMENT '项目描述',
    status TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='缴费项目表';

-- 18. 缴费记录表
CREATE TABLE tb_payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缴费记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    fee_item_id BIGINT NOT NULL COMMENT '缴费项目ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    payment_method VARCHAR(20) COMMENT '缴费方式：现金/银行卡/支付宝/微信',
    payment_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '缴费时间',
    transaction_no VARCHAR(100) COMMENT '交易流水号',
    operator_id BIGINT COMMENT '操作员ID',
    remarks TEXT COMMENT '备注',
    status TINYINT DEFAULT 1 COMMENT '状态：1-成功，2-退款，0-失败',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (student_id) REFERENCES tb_student(id),
    FOREIGN KEY (fee_item_id) REFERENCES tb_fee_item(id)
) COMMENT='缴费记录表';

-- ===========================================
-- 消息通知模块
-- ===========================================

-- 19. 消息模板表
CREATE TABLE tb_message_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_type VARCHAR(20) NOT NULL COMMENT '模板类型：SYSTEM-系统/CUSTOM-自定义',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型：NOTICE-通知/REMINDER-提醒/WARNING-警告/ANNOUNCEMENT-公告',
    title_template VARCHAR(200) NOT NULL COMMENT '标题模板',
    content_template TEXT NOT NULL COMMENT '内容模板',
    variables JSON COMMENT '模板变量定义（JSON格式）',
    send_channels JSON COMMENT '发送渠道：["SYSTEM","EMAIL","SMS"]',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用：1-是，0-否',
    creator_id BIGINT COMMENT '创建者ID',
    description TEXT COMMENT '模板描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (creator_id) REFERENCES tb_user(id)
) COMMENT='消息模板表';

-- 20. 消息表
CREATE TABLE tb_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    message_code VARCHAR(50) UNIQUE COMMENT '消息编码',
    template_id BIGINT COMMENT '模板ID',
    sender_id BIGINT COMMENT '发送者ID',
    sender_type VARCHAR(20) DEFAULT 'USER' COMMENT '发送者类型：USER-用户/SYSTEM-系统',
    title VARCHAR(200) NOT NULL COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    message_type VARCHAR(20) NOT NULL COMMENT '消息类型：NOTICE-通知/REMINDER-提醒/WARNING-警告/ANNOUNCEMENT-公告',
    priority_level VARCHAR(10) DEFAULT 'NORMAL' COMMENT '优先级：LOW-低/NORMAL-普通/HIGH-高/URGENT-紧急',
    send_method VARCHAR(20) DEFAULT 'SYSTEM' COMMENT '发送方式：SYSTEM-站内信/EMAIL-邮件/SMS-短信/ALL-全部',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型：USER-指定用户/ROLE-角色/CLASS-班级/GRADE-年级/ALL-全部',
    target_users TEXT COMMENT '目标用户ID列表（JSON格式）',
    scheduled_time DATETIME COMMENT '定时发送时间',
    sent_time DATETIME COMMENT '实际发送时间',
    expire_time DATETIME COMMENT '过期时间',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿/SCHEDULED-定时/SENDING-发送中/SENT-已发送/FAILED-失败/CANCELLED-已取消',
    total_recipients INT DEFAULT 0 COMMENT '总接收人数',
    sent_count INT DEFAULT 0 COMMENT '已发送数量',
    read_count INT DEFAULT 0 COMMENT '已阅读数量',
    is_top TINYINT DEFAULT 0 COMMENT '是否置顶：1-是，0-否',
    allow_reply TINYINT DEFAULT 0 COMMENT '是否允许回复：1-是，0-否',
    business_type VARCHAR(50) COMMENT '业务类型：COURSE-课程/EXAM-考试/PAYMENT-缴费/GRADE-成绩等',
    business_id BIGINT COMMENT '关联业务ID',
    extra_data JSON COMMENT '扩展数据',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (template_id) REFERENCES tb_message_template(id),
    FOREIGN KEY (sender_id) REFERENCES tb_user(id)
) COMMENT='消息表';

-- 21. 消息阅读状态表
CREATE TABLE tb_message_read (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    message_id BIGINT NOT NULL COMMENT '消息ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：1-是，0-否',
    read_time DATETIME COMMENT '阅读时间',
    is_starred TINYINT DEFAULT 0 COMMENT '是否收藏：1-是，0-否',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：1-是，0-否',
    delete_time DATETIME COMMENT '删除时间',
    reply_content TEXT COMMENT '回复内容',
    reply_time DATETIME COMMENT '回复时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (message_id) REFERENCES tb_message(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    UNIQUE KEY uk_message_user (message_id, user_id)
) COMMENT='消息阅读状态表';

-- ===========================================
-- 系统管理模块
-- ===========================================

-- 22. 系统配置表
CREATE TABLE tb_system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(255) COMMENT '配置描述',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型：STRING/INT/BOOLEAN/JSON',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置：1-是，0-否',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='系统配置表';

-- 23. 操作日志表
CREATE TABLE tb_operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    user_id BIGINT COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作内容',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    execution_time BIGINT COMMENT '执行时间（毫秒）',
    status TINYINT COMMENT '操作状态：1-成功，0-失败',
    error_msg TEXT COMMENT '错误信息',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) COMMENT='操作日志表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建结果
SELECT '所有表创建成功' AS message;
SHOW TABLES;
