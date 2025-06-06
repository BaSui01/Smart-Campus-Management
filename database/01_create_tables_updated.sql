-- ================================================
-- 校园管理系统数据库表创建脚本 (更新版)
-- 创建时间: 2025-06-06
-- 编码: UTF-8
-- 版本: 2.0
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 院系表 (tb_department) - 新增
-- ================================================
DROP TABLE IF EXISTS tb_department;
CREATE TABLE tb_department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '院系ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '院系名称',
    dept_code VARCHAR(20) NOT NULL UNIQUE COMMENT '院系代码',
    parent_id BIGINT COMMENT '上级院系ID',
    dept_type VARCHAR(20) COMMENT '院系类型：学院、系、部门等',
    dept_level INT COMMENT '院系级别：1-学院级,2-系级,3-专业级',
    sort_order INT COMMENT '排序序号',
    leader_id BIGINT COMMENT '院系负责人ID',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(200) COMMENT '办公地址',
    description VARCHAR(1000) COMMENT '院系描述',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_dept_code (dept_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status_deleted (status, deleted),
    
    FOREIGN KEY (parent_id) REFERENCES tb_department(id) ON DELETE SET NULL,
    FOREIGN KEY (leader_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='院系表';

-- ================================================
-- 2. 用户表 (tb_user) - 增强版
-- ================================================
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(11) COMMENT '手机号',
    gender VARCHAR(10) COMMENT '性别：男、女、其他',
    birthday DATE COMMENT '生日',
    id_card VARCHAR(18) COMMENT '身份证号',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    address VARCHAR(200) COMMENT '地址',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    reset_token VARCHAR(100) COMMENT '密码重置令牌',
    reset_token_expire DATETIME COMMENT '密码重置令牌过期时间',
    account_non_expired TINYINT DEFAULT 1 COMMENT '账户是否未过期',
    account_non_locked TINYINT DEFAULT 1 COMMENT '账户是否未锁定',
    credentials_non_expired TINYINT DEFAULT 1 COMMENT '密码是否未过期',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ================================================
-- 3. 角色表 (tb_role)
-- ================================================
DROP TABLE IF EXISTS tb_role;
CREATE TABLE tb_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色标识',
    role_sort INT DEFAULT 0 COMMENT '角色排序',
    data_scope TINYINT DEFAULT 1 COMMENT '数据范围：1-全部，2-部门，3-部门及下级，4-仅本人',
    menu_check_strictly TINYINT DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    dept_check_strictly TINYINT DEFAULT 1 COMMENT '部门树选择项是否关联显示',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ================================================
-- 4. 权限表 (tb_permission)
-- ================================================
DROP TABLE IF EXISTS tb_permission;
CREATE TABLE tb_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    permission_code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    resource_type VARCHAR(20) COMMENT '资源类型：menu-菜单，button-按钮，api-接口',
    resource_url VARCHAR(200) COMMENT '资源URL',
    permission_desc VARCHAR(255) COMMENT '权限描述',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ================================================
-- 5. 用户角色关联表 (tb_user_role)
-- ================================================
DROP TABLE IF EXISTS tb_user_role;
CREATE TABLE tb_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ================================================
-- 6. 角色权限关联表 (tb_role_permission)
-- ================================================
DROP TABLE IF EXISTS tb_role_permission;
CREATE TABLE tb_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES tb_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ================================================
-- 7. 班级表 (tb_class) - 增强版
-- ================================================
DROP TABLE IF EXISTS tb_class;
CREATE TABLE tb_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    class_code VARCHAR(20) NOT NULL UNIQUE COMMENT '班级代码',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    department_id BIGINT COMMENT '院系ID',
    head_teacher_id BIGINT COMMENT '班主任ID',
    assistant_teacher_id BIGINT COMMENT '副班主任ID',
    enrollment_year INT COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    expected_graduation_date DATE COMMENT '预计毕业日期',
    class_type VARCHAR(20) COMMENT '班级类型：普通班、实验班等',
    academic_system INT COMMENT '学制（年）',
    description VARCHAR(500) COMMENT '班级描述',
    motto VARCHAR(200) COMMENT '班级口号',
    goals VARCHAR(500) COMMENT '班级目标',
    max_students INT COMMENT '最大学生数',
    student_count INT DEFAULT 0 COMMENT '当前学生数',
    male_count INT DEFAULT 0 COMMENT '男生人数',
    female_count INT DEFAULT 0 COMMENT '女生人数',
    classroom VARCHAR(50) COMMENT '教室',
    class_status TINYINT NOT NULL DEFAULT 1 COMMENT '班级状态：1-正常，2-毕业，3-解散',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_class_code (class_code),
    INDEX idx_department_id (department_id),
    INDEX idx_head_teacher_id (head_teacher_id),
    INDEX idx_grade (grade),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_status_deleted (status, deleted),
    
    FOREIGN KEY (department_id) REFERENCES tb_department(id) ON DELETE SET NULL,
    FOREIGN KEY (head_teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (assistant_teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ================================================
-- 8. 学生表 (tb_student) - 增强版
-- ================================================
DROP TABLE IF EXISTS tb_student;
CREATE TABLE tb_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    academic_status TINYINT NOT NULL DEFAULT 1 COMMENT '学籍状态：1-在读，2-毕业，3-退学，4-休学，5-转学',
    student_type VARCHAR(20) COMMENT '学生类型：本科生、研究生等',
    training_mode VARCHAR(20) COMMENT '培养方式：全日制、非全日制',
    academic_system INT COMMENT '学制（年）',
    current_semester VARCHAR(20) COMMENT '当前学期',
    total_credits DECIMAL(5,1) DEFAULT 0.0 COMMENT '总学分',
    earned_credits DECIMAL(5,1) DEFAULT 0.0 COMMENT '已获得学分',
    gpa DECIMAL(3,2) DEFAULT 0.00 COMMENT 'GPA',
    parent_name VARCHAR(50) COMMENT '家长姓名',
    parent_phone VARCHAR(11) COMMENT '家长电话',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(11) COMMENT '紧急联系人电话',
    dormitory VARCHAR(50) COMMENT '宿舍号',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id),
    INDEX idx_grade (grade),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_status_deleted (status, deleted),
    
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ================================================
-- 9. 课程表 (tb_course) - 增强版
-- ================================================
DROP TABLE IF EXISTS tb_course;
CREATE TABLE tb_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程代码',
    course_name_en VARCHAR(100) COMMENT '课程英文名称',
    credits DECIMAL(3,1) NOT NULL COMMENT '学分',
    hours INT COMMENT '学时',
    theory_hours INT COMMENT '理论学时',
    lab_hours INT COMMENT '实验学时',
    practice_hours INT COMMENT '实习学时',
    department_id BIGINT COMMENT '院系ID',
    description VARCHAR(1000) COMMENT '课程描述',
    objectives VARCHAR(1000) COMMENT '课程目标',
    teacher_id BIGINT COMMENT '主讲教师ID',
    course_type VARCHAR(20) NOT NULL COMMENT '课程类型：必修课、选修课等',
    course_nature VARCHAR(20) COMMENT '课程性质：公共基础课、专业基础课等',
    applicable_majors VARCHAR(200) COMMENT '适用专业',
    prerequisites VARCHAR(200) COMMENT '先修课程',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year INT NOT NULL COMMENT '学年',
    class_time VARCHAR(100) COMMENT '上课时间',
    classroom VARCHAR(100) COMMENT '上课地点',
    max_students INT COMMENT '最大学生数',
    enrolled_students INT DEFAULT 0 COMMENT '已选学生数',
    selection_start_time DATETIME COMMENT '选课开始时间',
    selection_end_time DATETIME COMMENT '选课结束时间',
    assessment_method VARCHAR(50) COMMENT '考核方式',
    regular_score_ratio DECIMAL(3,2) COMMENT '平时成绩占比',
    midterm_score_ratio DECIMAL(3,2) COMMENT '期中成绩占比',
    final_score_ratio DECIMAL(3,2) COMMENT '期末成绩占比',
    textbook VARCHAR(500) COMMENT '教材信息',
    `references` VARCHAR(1000) COMMENT '参考书目',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    INDEX idx_course_code (course_code),
    INDEX idx_department_id (department_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_course_type (course_type),
    INDEX idx_semester (semester),
    INDEX idx_status_deleted (status, deleted),
    
    FOREIGN KEY (department_id) REFERENCES tb_department(id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ================================================
-- 10. 课程表排课表 (tb_course_schedule)
-- ================================================
DROP TABLE IF EXISTS tb_course_schedule;
CREATE TABLE tb_course_schedule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程表ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    class_id BIGINT COMMENT '班级ID',
    semester VARCHAR(50) NOT NULL COMMENT '学期',
    day_of_week INT NOT NULL COMMENT '星期几：1-星期一，7-星期日',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    classroom VARCHAR(50) NOT NULL COMMENT '教室',
    max_students INT COMMENT '最大学生数',
    enrolled_students INT DEFAULT 0 COMMENT '已选学生数',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表排课表';

-- ================================================
-- 11. 选课表 (tb_course_selection)
-- ================================================
DROP TABLE IF EXISTS tb_course_selection;
CREATE TABLE tb_course_selection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '选课ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    schedule_id BIGINT NOT NULL COMMENT '课程表ID',
    semester VARCHAR(50) COMMENT '学期',
    selection_time DATETIME COMMENT '选课时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-已选，2-已退选，0-禁用',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY uk_student_schedule (student_id, schedule_id),
    
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES tb_course_schedule(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课表';

-- ================================================
-- 12. 成绩表 (tb_grade)
-- ================================================
DROP TABLE IF EXISTS tb_grade;
CREATE TABLE tb_grade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    schedule_id BIGINT NOT NULL COMMENT '课程表ID',
    selection_id BIGINT COMMENT '选课ID',
    semester VARCHAR(50) COMMENT '学期',
    score DECIMAL(5,2) COMMENT '总成绩',
    regular_score DECIMAL(5,2) COMMENT '平时成绩',
    midterm_score DECIMAL(5,2) COMMENT '期中成绩',
    final_score DECIMAL(5,2) COMMENT '期末成绩',
    grade_point DECIMAL(3,1) COMMENT '绩点',
    level VARCHAR(10) COMMENT '等级：A+,A,A-,B+,B,B-,C+,C,C-,D,F',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补考：0-否，1-是',
    is_retake TINYINT DEFAULT 0 COMMENT '是否重修：0-否，1-是',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks VARCHAR(500) COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未录入，1-已录入，2-已确认',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (schedule_id) REFERENCES tb_course_schedule(id) ON DELETE CASCADE,
    FOREIGN KEY (selection_id) REFERENCES tb_course_selection(id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- ================================================
-- 13. 缴费项目表 (tb_fee_item)
-- ================================================
DROP TABLE IF EXISTS tb_fee_item;
CREATE TABLE tb_fee_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缴费项目ID',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) NOT NULL UNIQUE COMMENT '项目编码',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    fee_type VARCHAR(20) COMMENT '费用类型：学费，住宿费，教材费，实验费等',
    applicable_grade VARCHAR(50) COMMENT '适用年级',
    due_date DATE COMMENT '缴费截止日期',
    description TEXT COMMENT '项目描述',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费项目表';

-- ================================================
-- 14. 缴费记录表 (tb_payment_record)
-- ================================================
DROP TABLE IF EXISTS tb_payment_record;
CREATE TABLE tb_payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '缴费记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    fee_item_id BIGINT NOT NULL COMMENT '缴费项目ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    payment_method VARCHAR(20) COMMENT '缴费方式：现金，银行卡，支付宝，微信等',
    payment_time DATETIME COMMENT '缴费时间',
    transaction_no VARCHAR(100) COMMENT '交易流水号',
    operator_id BIGINT COMMENT '操作员ID',
    remarks TEXT COMMENT '备注',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：1-已缴费，2-已退费，0-无效',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_item_id) REFERENCES tb_fee_item(id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费记录表';

-- ================================================
-- 15. 系统设置表 (tb_system_settings) - 新增
-- ================================================
DROP TABLE IF EXISTS tb_system_settings;
CREATE TABLE tb_system_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设置ID',
    setting_key VARCHAR(100) NOT NULL UNIQUE COMMENT '设置键',
    setting_value TEXT COMMENT '设置值',
    setting_type VARCHAR(20) DEFAULT 'string' COMMENT '设置类型：string,int,boolean,json',
    category VARCHAR(50) COMMENT '设置分类',
    description VARCHAR(500) COMMENT '设置描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统设置：0-否，1-是',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统设置表';

-- 显示创建的表
SHOW TABLES;

-- 显示表创建完成信息
SELECT 'Enhanced database schema created successfully!' AS result;