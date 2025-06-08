-- =====================================================
-- Smart Campus Management System - 表结构创建脚本
-- =====================================================
-- 文件名: 02_create_tables.sql
-- 描述: 创建智慧校园管理系统所有数据表
-- 版本: 1.0.0
-- 创建时间: 2025-06-08
-- 兼容性: MySQL 8.0+
-- =====================================================

USE campus_management_db;

-- 设置字符集和外键检查
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 基础管理表
-- =====================================================

-- 删除已存在的表（按依赖关系倒序）
DROP TABLE IF EXISTS tb_resource_access_log;
DROP TABLE IF EXISTS tb_student_evaluation;
DROP TABLE IF EXISTS tb_course_resource;
DROP TABLE IF EXISTS tb_payment_record;
DROP TABLE IF EXISTS tb_notification;
DROP TABLE IF EXISTS tb_notification_template;
DROP TABLE IF EXISTS tb_assignment_submission;
DROP TABLE IF EXISTS tb_exam_record;
DROP TABLE IF EXISTS tb_exam_question;
DROP TABLE IF EXISTS tb_assignment;
DROP TABLE IF EXISTS tb_exam;
DROP TABLE IF EXISTS tb_grade;
DROP TABLE IF EXISTS tb_course_selection;
DROP TABLE IF EXISTS tb_course_schedule;
DROP TABLE IF EXISTS tb_schedule;
DROP TABLE IF EXISTS tb_attendance;
DROP TABLE IF EXISTS tb_course_selection_period;
DROP TABLE IF EXISTS tb_course;
DROP TABLE IF EXISTS tb_classroom;
DROP TABLE IF EXISTS tb_time_slot;
DROP TABLE IF EXISTS tb_student;
DROP TABLE IF EXISTS tb_school_class;
DROP TABLE IF EXISTS tb_department;
DROP TABLE IF EXISTS tb_user_role;
DROP TABLE IF EXISTS tb_role_permission;
DROP TABLE IF EXISTS tb_parent_student_relation;
DROP TABLE IF EXISTS tb_teacher_course_permission;
DROP TABLE IF EXISTS tb_message;
DROP TABLE IF EXISTS tb_activity_log;
DROP TABLE IF EXISTS tb_fee_item;
DROP TABLE IF EXISTS tb_system_config;
DROP TABLE IF EXISTS tb_user;
DROP TABLE IF EXISTS tb_role;
DROP TABLE IF EXISTS tb_permission;

-- 1.1 权限表
CREATE TABLE tb_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_key VARCHAR(50) NOT NULL UNIQUE COMMENT '权限键',
    permission_type VARCHAR(20) NOT NULL DEFAULT 'MENU' COMMENT '权限类型：MENU菜单,BUTTON按钮,API接口',
    parent_id BIGINT DEFAULT NULL COMMENT '父权限ID',
    path VARCHAR(200) DEFAULT NULL COMMENT '路径',
    component VARCHAR(200) DEFAULT NULL COMMENT '组件',
    icon VARCHAR(50) DEFAULT NULL COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统权限',
    description VARCHAR(200) DEFAULT NULL COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_permission_key (permission_key),
    INDEX idx_parent_id (parent_id),
    INDEX idx_permission_type (permission_type),
    INDEX idx_status_deleted (status, deleted),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 1.2 角色表
CREATE TABLE tb_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL UNIQUE COMMENT '角色键',
    description VARCHAR(200) DEFAULT NULL COMMENT '角色描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统内置角色',
    role_level INT DEFAULT 99 COMMENT '角色级别',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_role_key (role_key),
    INDEX idx_role_name (role_name),
    INDEX idx_status_deleted (status, deleted),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 1.3 用户表
CREATE TABLE tb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号码',
    gender VARCHAR(10) COMMENT '性别',
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
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1.4 角色权限关联表
CREATE TABLE tb_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES tb_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 1.5 用户角色关联表
CREATE TABLE tb_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    assigned_by BIGINT COMMENT '分配人ID',
    assigned_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分配时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_assigned_by (assigned_by),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 2. 组织架构表
-- =====================================================

-- 2.1 院系表
CREATE TABLE tb_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '院系ID',
    dept_name VARCHAR(100) NOT NULL COMMENT '院系名称',
    dept_code VARCHAR(20) NOT NULL UNIQUE COMMENT '院系编码',
    parent_id BIGINT DEFAULT NULL COMMENT '父院系ID',
    dept_level INT DEFAULT 1 COMMENT '院系层级',
    dept_type VARCHAR(20) DEFAULT 'COLLEGE' COMMENT '院系类型',
    leader_id BIGINT COMMENT '负责人ID',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    address VARCHAR(200) COMMENT '地址',
    description TEXT COMMENT '描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_dept_code (dept_code),
    INDEX idx_parent_id (parent_id),
    INDEX idx_leader_id (leader_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (leader_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='院系表';

-- 2.2 班级表
CREATE TABLE tb_school_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    class_code VARCHAR(20) NOT NULL UNIQUE COMMENT '班级编码',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    department_id BIGINT COMMENT '院系ID',
    head_teacher_id BIGINT COMMENT '班主任ID',
    assistant_teacher_id BIGINT COMMENT '辅导员ID',
    max_students INT DEFAULT 50 COMMENT '最大学生数',
    current_students INT DEFAULT 0 COMMENT '当前学生数',
    classroom VARCHAR(50) COMMENT '教室',
    academic_year INT COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_class_code (class_code),
    INDEX idx_grade (grade),
    INDEX idx_major (major),
    INDEX idx_department_id (department_id),
    INDEX idx_head_teacher_id (head_teacher_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (department_id) REFERENCES tb_department(id) ON DELETE SET NULL,
    FOREIGN KEY (head_teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (assistant_teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 2.3 学生表
CREATE TABLE tb_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_date DATE COMMENT '入学日期',
    enrollment_year INT COMMENT '入学年份',
    graduation_date DATE COMMENT '毕业日期',
    student_type VARCHAR(20) DEFAULT 'UNDERGRADUATE' COMMENT '学生类型',
    student_status VARCHAR(20) DEFAULT 'ENROLLED' COMMENT '学生状态',
    dormitory_no VARCHAR(50) COMMENT '宿舍号',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    hometown VARCHAR(100) COMMENT '籍贯',
    political_status VARCHAR(20) COMMENT '政治面貌',
    family_address VARCHAR(200) COMMENT '家庭地址',
    guardian_name VARCHAR(50) COMMENT '监护人姓名',
    guardian_phone VARCHAR(20) COMMENT '监护人电话',
    guardian_relation VARCHAR(20) COMMENT '监护人关系',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_grade (grade),
    INDEX idx_major (major),
    INDEX idx_class_id (class_id),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES tb_school_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- =====================================================
-- 3. 课程管理表
-- =====================================================

-- 3.1 时间段表
CREATE TABLE tb_time_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间段ID',
    slot_name VARCHAR(50) NOT NULL COMMENT '时间段名称',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    day_of_week INT NOT NULL COMMENT '星期几(1-7)',
    slot_order INT NOT NULL COMMENT '时间段顺序',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    academic_year INT COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    description VARCHAR(200) COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_day_of_week (day_of_week),
    INDEX idx_slot_order (slot_order),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='时间段表';

-- 3.2 教室表
CREATE TABLE tb_classroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教室ID',
    room_name VARCHAR(50) NOT NULL COMMENT '教室名称',
    room_code VARCHAR(20) NOT NULL UNIQUE COMMENT '教室编码',
    building VARCHAR(50) COMMENT '建筑物',
    floor INT COMMENT '楼层',
    room_type VARCHAR(20) DEFAULT 'NORMAL' COMMENT '教室类型',
    capacity INT DEFAULT 50 COMMENT '容量',
    equipment TEXT COMMENT '设备信息',
    administrator_id BIGINT COMMENT '管理员ID',
    location VARCHAR(200) COMMENT '位置描述',
    is_available TINYINT DEFAULT 1 COMMENT '是否可用',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_room_code (room_code),
    INDEX idx_building (building),
    INDEX idx_room_type (room_type),
    INDEX idx_administrator_id (administrator_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (administrator_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教室表';

-- 3.3 课程表
CREATE TABLE tb_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程编码',
    course_type VARCHAR(20) DEFAULT 'REQUIRED' COMMENT '课程类型',
    credits DECIMAL(3,1) NOT NULL COMMENT '学分',
    total_hours INT NOT NULL COMMENT '总学时',
    theory_hours INT DEFAULT 0 COMMENT '理论学时',
    practice_hours INT DEFAULT 0 COMMENT '实践学时',
    department_id BIGINT COMMENT '开课院系ID',
    teacher_id BIGINT COMMENT '主讲教师ID',
    prerequisite_courses TEXT COMMENT '先修课程',
    applicable_majors TEXT COMMENT '适用专业',
    applicable_grades TEXT COMMENT '适用年级',
    max_students INT DEFAULT 100 COMMENT '最大选课人数',
    min_students INT DEFAULT 10 COMMENT '最小开课人数',
    course_description TEXT COMMENT '课程描述',
    teaching_objectives TEXT COMMENT '教学目标',
    assessment_method TEXT COMMENT '考核方式',
    textbook VARCHAR(200) COMMENT '教材',
    reference_books TEXT COMMENT '参考书目',
    academic_year INT COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_code (course_code),
    INDEX idx_course_type (course_type),
    INDEX idx_department_id (department_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (department_id) REFERENCES tb_department(id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- =====================================================
-- 4. 教学安排表
-- =====================================================

-- 4.1 课程安排表
CREATE TABLE tb_course_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程安排ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    classroom_id BIGINT COMMENT '教室ID',
    time_slot_id BIGINT NOT NULL COMMENT '时间段ID',
    class_id BIGINT COMMENT '班级ID',
    week_start INT NOT NULL COMMENT '开始周次',
    week_end INT NOT NULL COMMENT '结束周次',
    academic_year INT NOT NULL COMMENT '学年',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    schedule_type VARCHAR(20) DEFAULT 'REGULAR' COMMENT '安排类型',
    max_students INT DEFAULT 50 COMMENT '最大学生数',
    current_students INT DEFAULT 0 COMMENT '当前学生数',
    is_confirmed TINYINT DEFAULT 0 COMMENT '是否确认',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_classroom_id (classroom_id),
    INDEX idx_time_slot_id (time_slot_id),
    INDEX idx_class_id (class_id),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id) ON DELETE SET NULL,
    FOREIGN KEY (time_slot_id) REFERENCES tb_time_slot(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES tb_school_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程安排表';

-- 4.2 选课时间段表
CREATE TABLE tb_course_selection_period (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '选课时间段ID',
    period_name VARCHAR(100) NOT NULL COMMENT '时间段名称',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    applicable_grades TEXT COMMENT '适用年级',
    applicable_majors TEXT COMMENT '适用专业',
    max_courses INT DEFAULT 10 COMMENT '最大选课数',
    min_credits DECIMAL(4,1) DEFAULT 0 COMMENT '最小学分',
    max_credits DECIMAL(4,1) DEFAULT 30 COMMENT '最大学分',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    academic_year INT NOT NULL COMMENT '学年',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课时间段表';

-- 4.3 选课记录表
CREATE TABLE tb_course_selection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    course_schedule_id BIGINT COMMENT '课程安排ID',
    selection_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    selection_type VARCHAR(20) DEFAULT 'NORMAL' COMMENT '选课类型',
    selection_status VARCHAR(20) DEFAULT 'SELECTED' COMMENT '选课状态',
    academic_year INT NOT NULL COMMENT '学年',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    is_retake TINYINT DEFAULT 0 COMMENT '是否重修',
    retake_reason VARCHAR(200) COMMENT '重修原因',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_student_course_semester (student_id, course_id, academic_year, semester),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_course_schedule_id (course_schedule_id),
    INDEX idx_selection_time (selection_time),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (course_schedule_id) REFERENCES tb_course_schedule(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课记录表';

-- =====================================================
-- 5. 成绩考试表
-- =====================================================

-- 5.1 成绩表
CREATE TABLE tb_grade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT COMMENT '教师ID',
    usual_score DECIMAL(5,2) COMMENT '平时成绩',
    midterm_score DECIMAL(5,2) COMMENT '期中成绩',
    final_score DECIMAL(5,2) COMMENT '期末成绩',
    total_score DECIMAL(5,2) COMMENT '总成绩',
    grade_point DECIMAL(3,2) COMMENT '绩点',
    letter_grade VARCHAR(5) COMMENT '等级成绩',
    is_pass TINYINT DEFAULT 0 COMMENT '是否通过',
    exam_type VARCHAR(20) DEFAULT 'NORMAL' COMMENT '考试类型',
    academic_year INT NOT NULL COMMENT '学年',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    input_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    input_by BIGINT COMMENT '录入人',
    audit_time DATETIME COMMENT '审核时间',
    audit_by BIGINT COMMENT '审核人',
    is_audited TINYINT DEFAULT 0 COMMENT '是否审核',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_student_course_semester (student_id, course_id, academic_year, semester, exam_type),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_academic_year (academic_year),
    INDEX idx_total_score (total_score),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- 5.2 考试表
CREATE TABLE tb_exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考试ID',
    exam_name VARCHAR(100) NOT NULL COMMENT '考试名称',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    exam_type VARCHAR(20) DEFAULT 'FINAL' COMMENT '考试类型',
    exam_mode VARCHAR(20) DEFAULT 'OFFLINE' COMMENT '考试模式',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    duration INT NOT NULL COMMENT '考试时长(分钟)',
    total_score DECIMAL(5,2) DEFAULT 100 COMMENT '总分',
    pass_score DECIMAL(5,2) DEFAULT 60 COMMENT '及格分',
    classroom_id BIGINT COMMENT '考试教室ID',
    max_students INT COMMENT '最大考生数',
    current_students INT DEFAULT 0 COMMENT '当前考生数',
    exam_instructions TEXT COMMENT '考试说明',
    is_published TINYINT DEFAULT 0 COMMENT '是否发布',
    is_auto_grade TINYINT DEFAULT 0 COMMENT '是否自动评分',
    allow_review TINYINT DEFAULT 1 COMMENT '是否允许查看',
    academic_year INT NOT NULL COMMENT '学年',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_start_time (start_time),
    INDEX idx_exam_type (exam_type),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试表';

-- =====================================================
-- 6. 财务管理表
-- =====================================================

-- 6.1 缴费项目表
CREATE TABLE tb_fee_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '缴费项目ID',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) NOT NULL UNIQUE COMMENT '项目编码',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    fee_type VARCHAR(20) NOT NULL COMMENT '费用类型',
    applicable_grade VARCHAR(100) COMMENT '适用年级',
    applicable_major VARCHAR(200) COMMENT '适用专业',
    academic_year INT NOT NULL COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    due_date DATE COMMENT '缴费截止日期',
    start_date DATE COMMENT '缴费开始日期',
    is_mandatory TINYINT NOT NULL DEFAULT 1 COMMENT '是否必缴',
    allow_installment TINYINT NOT NULL DEFAULT 0 COMMENT '是否允许分期',
    installment_count INT COMMENT '分期数量',
    late_fee_rate DECIMAL(5,2) COMMENT '逾期滞纳金比例',
    discount_amount DECIMAL(10,2) COMMENT '优惠金额',
    discount_deadline DATE COMMENT '优惠截止日期',
    description TEXT COMMENT '项目描述',
    payment_instructions TEXT COMMENT '缴费说明',
    created_by BIGINT COMMENT '创建人ID',
    approved_by BIGINT COMMENT '审核人ID',
    approved_time DATETIME COMMENT '审核时间',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_item_code (item_code),
    INDEX idx_fee_type (fee_type),
    INDEX idx_applicable_grade (applicable_grade),
    INDEX idx_due_date (due_date),
    INDEX idx_academic_year (academic_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (created_by) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费项目表';

-- 6.2 缴费记录表
CREATE TABLE tb_payment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '缴费记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    fee_item_id BIGINT NOT NULL COMMENT '缴费项目ID',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '缴费方式',
    payment_time DATETIME COMMENT '缴费时间',
    transaction_no VARCHAR(100) UNIQUE COMMENT '交易流水号',
    operator_id BIGINT COMMENT '操作员ID',
    payment_status TINYINT NOT NULL DEFAULT 1 COMMENT '缴费状态',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_student_id (student_id),
    INDEX idx_fee_item_id (fee_item_id),
    INDEX idx_payment_time (payment_time),
    INDEX idx_transaction_no (transaction_no),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_item_id) REFERENCES tb_fee_item(id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费记录表';

-- =====================================================
-- 7. 系统配置表
-- =====================================================

-- 7.1 系统配置表
CREATE TABLE tb_system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_name VARCHAR(100) NOT NULL COMMENT '配置名称',
    config_type VARCHAR(20) DEFAULT 'STRING' COMMENT '配置类型',
    config_group VARCHAR(50) DEFAULT 'DEFAULT' COMMENT '配置分组',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置',
    is_encrypted TINYINT DEFAULT 0 COMMENT '是否加密',
    sort_order INT DEFAULT 0 COMMENT '排序',
    description VARCHAR(500) COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =====================================================
-- 8. 通知消息表
-- =====================================================

-- 8.1 通知模板表
CREATE TABLE tb_notification_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板编码',
    template_type VARCHAR(20) NOT NULL COMMENT '模板类型',
    title_template VARCHAR(200) NOT NULL COMMENT '标题模板',
    content_template TEXT NOT NULL COMMENT '内容模板',
    variables TEXT COMMENT '变量说明',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统模板',
    description VARCHAR(500) COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_template_code (template_code),
    INDEX idx_template_type (template_type),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- 8.2 通知表
CREATE TABLE tb_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    notification_type VARCHAR(20) NOT NULL COMMENT '通知类型',
    priority_level INT DEFAULT 1 COMMENT '优先级',
    sender_id BIGINT COMMENT '发送人ID',
    target_type VARCHAR(20) NOT NULL COMMENT '目标类型',
    target_ids TEXT COMMENT '目标ID列表',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    expire_time DATETIME COMMENT '过期时间',
    is_published TINYINT DEFAULT 0 COMMENT '是否发布',
    read_count INT DEFAULT 0 COMMENT '阅读数量',
    total_count INT DEFAULT 0 COMMENT '总目标数量',
    template_id BIGINT COMMENT '模板ID',
    attachment_urls TEXT COMMENT '附件URL',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_notification_type (notification_type),
    INDEX idx_sender_id (sender_id),
    INDEX idx_target_type (target_type),
    INDEX idx_send_time (send_time),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (sender_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (template_id) REFERENCES tb_notification_template(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- =====================================================
-- 9. 日志审计表
-- =====================================================

-- 9.1 活动日志表
CREATE TABLE tb_activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    username VARCHAR(50) COMMENT '用户名',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型',
    operation_name VARCHAR(100) NOT NULL COMMENT '操作名称',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    operation_details TEXT COMMENT '操作详情',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_status INT COMMENT '响应状态',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent TEXT COMMENT '用户代理',
    execution_time BIGINT COMMENT '执行时间(毫秒)',
    operation_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    is_success TINYINT DEFAULT 1 COMMENT '是否成功',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '删除标志',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_user_id (user_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_resource_type (resource_type),
    INDEX idx_operation_time (operation_time),
    INDEX idx_ip_address (ip_address),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='活动日志表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示完成信息
SELECT '所有数据表创建完成！' as '执行结果',
       COUNT(*) as '表数量'
FROM information_schema.tables
WHERE table_schema = 'campus_management_db';
