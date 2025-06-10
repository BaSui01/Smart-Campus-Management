-- =====================================================
-- Smart Campus Management System - 完整表结构创建脚本
-- 文件: 01_create_complete_tables.sql
-- 描述: 基于35个实体类创建完整的MySQL表结构
-- 版本: 2.0.0
-- 创建时间: 2025-01-27
-- 编码: UTF-8
-- =====================================================

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 创建数据库
DROP DATABASE IF EXISTS campus_management_db;
CREATE DATABASE campus_management_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE campus_management_db;


-- 设置SQL模式和时区
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';
SET time_zone = '+08:00';

-- 临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 基础管理表
-- =====================================================

-- 用户表 (User)
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    email VARCHAR(100) COMMENT '邮箱',
    real_name VARCHAR(100) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    id_card VARCHAR(18) COMMENT '身份证号',
    address VARCHAR(200) COMMENT '地址',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    is_locked TINYINT DEFAULT 0 COMMENT '是否锁定',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表 (Role)
CREATE TABLE IF NOT EXISTS tb_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(100) NOT NULL UNIQUE COMMENT '角色标识',
    description VARCHAR(200) COMMENT '角色描述',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统角色',
    role_level INT DEFAULT 0 COMMENT '角色级别',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_role_key (role_key),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表 (Permission)
CREATE TABLE IF NOT EXISTS tb_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    permission_key VARCHAR(100) NOT NULL UNIQUE COMMENT '权限标识',
    description VARCHAR(200) COMMENT '权限描述',
    permission_type VARCHAR(20) DEFAULT 'api' COMMENT '权限类型',
    resource_path VARCHAR(200) COMMENT '资源路径',
    http_method VARCHAR(10) DEFAULT 'ALL' COMMENT 'HTTP方法',
    permission_level INT DEFAULT 0 COMMENT '权限级别',
    is_system TINYINT NOT NULL DEFAULT 0 COMMENT '是否系统权限',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_permission_key (permission_key),
    INDEX idx_permission_type (permission_type),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表 (UserRole)
CREATE TABLE IF NOT EXISTS tb_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表 (RolePermission)
CREATE TABLE IF NOT EXISTS tb_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (role_id) REFERENCES tb_role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES tb_permission(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- =====================================================
-- 2. 学院和班级管理表
-- =====================================================

-- 学院表 (Department)
CREATE TABLE IF NOT EXISTS tb_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学院ID',
    department_name VARCHAR(100) NOT NULL COMMENT '学院名称',
    department_code VARCHAR(20) NOT NULL UNIQUE COMMENT '学院代码',
    description VARCHAR(500) COMMENT '学院描述',
    dean_id BIGINT COMMENT '院长ID',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    office_location VARCHAR(200) COMMENT '办公地点',
    website_url VARCHAR(200) COMMENT '官网地址',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_department_code (department_code),
    INDEX idx_dean_id (dean_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (dean_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- 班级表 (SchoolClass) - 修复表名和字段
CREATE TABLE IF NOT EXISTS tb_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    class_name VARCHAR(100) NOT NULL COMMENT '班级名称',
    class_code VARCHAR(50) NOT NULL UNIQUE COMMENT '班级代码',
    department_id BIGINT NOT NULL COMMENT '学院ID',
    major VARCHAR(100) COMMENT '专业',
    grade VARCHAR(20) COMMENT '年级',
    enrollment_year INT COMMENT '入学年份',
    head_teacher_id BIGINT COMMENT '班主任ID',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    max_capacity INT DEFAULT 50 COMMENT '最大容量',
    classroom_location VARCHAR(100) COMMENT '教室位置',
    class_status TINYINT DEFAULT 1 COMMENT '班级状态：1-正常,2-毕业,3-解散',
    enrollment_date DATE COMMENT '入学日期',
    expected_graduation_date DATE COMMENT '预期毕业日期',
    academic_year VARCHAR(20) COMMENT '学年',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_class_code (class_code),
    INDEX idx_department_id (department_id),
    INDEX idx_head_teacher_id (head_teacher_id),
    INDEX idx_grade (grade),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (department_id) REFERENCES tb_department(id) ON DELETE CASCADE,
    FOREIGN KEY (head_teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 学生表 (Student) - 修复字段名和外键引用
CREATE TABLE IF NOT EXISTS tb_student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_no VARCHAR(50) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    academic_status TINYINT DEFAULT 1 COMMENT '学籍状态：1-在读,2-休学,3-退学,4-毕业',
    student_type VARCHAR(20) DEFAULT '本科生' COMMENT '学生类型',
    training_mode VARCHAR(20) DEFAULT '全日制' COMMENT '培养方式',
    academic_system INT DEFAULT 4 COMMENT '学制',
    current_semester VARCHAR(20) COMMENT '当前学期',
    total_credits DECIMAL(5,1) DEFAULT 0 COMMENT '总学分',
    earned_credits DECIMAL(5,1) DEFAULT 0 COMMENT '已获学分',
    gpa DECIMAL(3,2) DEFAULT 0 COMMENT 'GPA',
    parent_name VARCHAR(100) COMMENT '家长姓名',
    parent_phone VARCHAR(20) COMMENT '家长电话',
    parent_relationship VARCHAR(20) COMMENT '家长关系',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    dormitory_info VARCHAR(100) COMMENT '宿舍信息',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_student_no (student_no),
    INDEX idx_user_id (user_id),
    INDEX idx_class_id (class_id),
    INDEX idx_grade (grade),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 家长学生关系表 (ParentStudentRelation)
CREATE TABLE IF NOT EXISTS tb_parent_student_relation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关系ID',
    parent_id BIGINT NOT NULL COMMENT '家长ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    relationship VARCHAR(20) NOT NULL COMMENT '关系类型',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主要联系人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_parent_student (parent_id, student_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (parent_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家长学生关系表';

-- =====================================================
-- 3. 课程和教学管理表
-- =====================================================

-- 教室表 (Classroom)
CREATE TABLE IF NOT EXISTS tb_classroom (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教室ID',
    classroom_name VARCHAR(100) NOT NULL COMMENT '教室名称',
    classroom_code VARCHAR(50) NOT NULL UNIQUE COMMENT '教室代码',
    building VARCHAR(100) COMMENT '所在建筑',
    floor_number INT COMMENT '楼层',
    room_number VARCHAR(20) COMMENT '房间号',
    capacity INT DEFAULT 50 COMMENT '容量',
    classroom_type VARCHAR(50) COMMENT '教室类型',
    equipment TEXT COMMENT '设备信息',
    location_description VARCHAR(200) COMMENT '位置描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_classroom_code (classroom_code),
    INDEX idx_building (building),
    INDEX idx_classroom_type (classroom_type),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室表';

-- 时间段表 (TimeSlot)
CREATE TABLE IF NOT EXISTS tb_time_slot (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间段ID',
    slot_name VARCHAR(50) NOT NULL COMMENT '时间段名称',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    slot_order INT NOT NULL COMMENT '时间段顺序',
    is_active TINYINT DEFAULT 1 COMMENT '是否启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_slot_order (slot_order),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

-- 课程表 (Course)
CREATE TABLE IF NOT EXISTS tb_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(50) NOT NULL UNIQUE COMMENT '课程代码',
    department_id BIGINT NOT NULL COMMENT '开课学院ID',
    course_type VARCHAR(20) DEFAULT '必修课' COMMENT '课程类型',
    credits DECIMAL(3,1) NOT NULL COMMENT '学分',
    hours INT NOT NULL COMMENT '学时',
    theory_hours INT DEFAULT 0 COMMENT '理论学时',
    practice_hours INT DEFAULT 0 COMMENT '实践学时',
    description TEXT COMMENT '课程描述',
    prerequisites VARCHAR(500) COMMENT '先修课程',
    objectives TEXT COMMENT '课程目标',
    syllabus TEXT COMMENT '教学大纲',
    textbook VARCHAR(200) COMMENT '教材',
    reference_books TEXT COMMENT '参考书目',
    assessment_method VARCHAR(200) COMMENT '考核方式',
    max_students INT DEFAULT 100 COMMENT '最大选课人数',
    current_students INT DEFAULT 0 COMMENT '当前选课人数',
    is_elective TINYINT DEFAULT 0 COMMENT '是否选修课',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_code (course_code),
    INDEX idx_department_id (department_id),
    INDEX idx_course_type (course_type),
    INDEX idx_credits (credits),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (department_id) REFERENCES tb_department(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 课程资源表 (CourseResource)
CREATE TABLE IF NOT EXISTS tb_course_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    resource_name VARCHAR(200) NOT NULL COMMENT '资源名称',
    resource_type VARCHAR(50) COMMENT '资源类型',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    uploader_id BIGINT COMMENT '上传者ID',
    description TEXT COMMENT '资源描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_id (course_id),
    INDEX idx_resource_type (resource_type),
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (uploader_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源表';

-- 教师课程权限表 (TeacherCoursePermission)
CREATE TABLE IF NOT EXISTS tb_teacher_course_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    permission_type VARCHAR(50) NOT NULL COMMENT '权限类型',
    academic_year VARCHAR(20) COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_teacher_course (teacher_id, course_id, permission_type),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_course_id (course_id),
    INDEX idx_permission_type (permission_type),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师课程权限表';

-- 课程调度表 (CourseSchedule)
CREATE TABLE IF NOT EXISTS tb_course_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '调度ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    classroom_id BIGINT COMMENT '教室ID',
    time_slot_id BIGINT NOT NULL COMMENT '时间段ID',
    day_of_week INT NOT NULL COMMENT '星期几',
    week_start INT COMMENT '开始周次',
    week_end INT COMMENT '结束周次',
    academic_year VARCHAR(20) COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    schedule_type VARCHAR(20) DEFAULT 'regular' COMMENT '调度类型',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_classroom_id (classroom_id),
    INDEX idx_time_slot_id (time_slot_id),
    INDEX idx_day_of_week (day_of_week),
    INDEX idx_academic_year_semester (academic_year, semester),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id) ON DELETE SET NULL,
    FOREIGN KEY (time_slot_id) REFERENCES tb_time_slot(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程调度表';

-- 选课时间段表 (CourseSelectionPeriod)
CREATE TABLE IF NOT EXISTS tb_course_selection_period (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '选课时间段ID',
    period_name VARCHAR(100) NOT NULL COMMENT '时间段名称',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    target_grades VARCHAR(200) COMMENT '目标年级',
    selection_type VARCHAR(50) DEFAULT 'normal' COMMENT '选课类型',
    max_credits DECIMAL(3,1) COMMENT '最大学分',
    min_credits DECIMAL(3,1) COMMENT '最小学分',
    description TEXT COMMENT '描述',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_academic_year_semester (academic_year, semester),
    INDEX idx_start_end_time (start_time, end_time),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课时间段表';

-- 选课记录表 (CourseSelection)
CREATE TABLE IF NOT EXISTS tb_course_selection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '选课ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    semester VARCHAR(20) COMMENT '学期',
    academic_year VARCHAR(20) COMMENT '学年',
    selection_time DATETIME COMMENT '选课时间',
    selection_status VARCHAR(20) DEFAULT 'selected' COMMENT '选课状态',
    selection_type VARCHAR(20) DEFAULT 'normal' COMMENT '选课类型',
    priority_level INT DEFAULT 0 COMMENT '优先级',
    is_retake TINYINT DEFAULT 0 COMMENT '是否重修',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_student_course_semester (student_id, course_id, semester, academic_year),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_academic_year_semester (academic_year, semester),
    INDEX idx_selection_status (selection_status),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选课记录表';

-- =====================================================
-- 4. 成绩和考试管理表
-- =====================================================

-- 成绩表 (Grade) - 修复字段名与实体类一致
CREATE TABLE IF NOT EXISTS tb_grade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT COMMENT '教师ID',
    class_id BIGINT COMMENT '班级ID',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year INT NOT NULL COMMENT '学年',
    usual_score DECIMAL(5,2) COMMENT '平时成绩',
    midterm_score DECIMAL(5,2) COMMENT '期中成绩',
    final_score DECIMAL(5,2) COMMENT '期末成绩',
    total_score DECIMAL(5,2) COMMENT '总成绩',
    grade_level VARCHAR(10) COMMENT '等级成绩',
    gpa DECIMAL(3,2) COMMENT 'GPA绩点',
    credit DECIMAL(4,1) COMMENT '学分',
    grade_status VARCHAR(20) DEFAULT 'pending' COMMENT '成绩状态',
    is_passed BOOLEAN DEFAULT FALSE COMMENT '是否及格',
    makeup_count INT DEFAULT 0 COMMENT '补考次数',
    highest_score DECIMAL(5,2) COMMENT '最高成绩',
    record_time DATETIME COMMENT '录入时间',
    confirm_time DATETIME COMMENT '确认时间',
    schedule_id BIGINT COMMENT '课程安排ID',
    selection_id BIGINT COMMENT '选课记录ID',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_student_course_semester (student_id, course_id, semester, academic_year),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_class_id (class_id),
    INDEX idx_semester (semester),
    INDEX idx_academic_year_semester (academic_year, semester),
    INDEX idx_grade_status (grade_status),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- 考试表 (Exam)
CREATE TABLE IF NOT EXISTS tb_exam (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考试ID',
    exam_name VARCHAR(200) NOT NULL COMMENT '考试名称',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '出题教师ID',
    exam_type VARCHAR(50) DEFAULT 'final' COMMENT '考试类型',
    exam_date DATE COMMENT '考试日期',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    duration INT COMMENT '考试时长(分钟)',
    classroom_id BIGINT COMMENT '考试教室ID',
    total_score DECIMAL(5,2) DEFAULT 100 COMMENT '总分',
    pass_score DECIMAL(5,2) DEFAULT 60 COMMENT '及格分',
    exam_instructions TEXT COMMENT '考试说明',
    exam_status VARCHAR(20) DEFAULT 'draft' COMMENT '考试状态',
    is_online TINYINT DEFAULT 0 COMMENT '是否在线考试',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_classroom_id (classroom_id),
    INDEX idx_exam_date (exam_date),
    INDEX idx_exam_type (exam_type),
    INDEX idx_exam_status (exam_status),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (classroom_id) REFERENCES tb_classroom(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试表';

-- 考试题目表 (ExamQuestion)
CREATE TABLE IF NOT EXISTS tb_exam_question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '题目ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    question_text TEXT NOT NULL COMMENT '题目内容',
    question_type VARCHAR(50) NOT NULL COMMENT '题目类型',
    question_order INT NOT NULL COMMENT '题目顺序',
    score DECIMAL(5,2) NOT NULL COMMENT '分值',
    options TEXT COMMENT '选项(JSON格式)',
    correct_answer TEXT COMMENT '正确答案',
    explanation TEXT COMMENT '解析',
    difficulty_level VARCHAR(20) DEFAULT 'medium' COMMENT '难度等级',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_exam_id (exam_id),
    INDEX idx_question_type (question_type),
    INDEX idx_question_order (question_order),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (exam_id) REFERENCES tb_exam(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试题目表';

-- 考试记录表 (ExamRecord)
CREATE TABLE IF NOT EXISTS tb_exam_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考试记录ID',
    exam_id BIGINT NOT NULL COMMENT '考试ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    submit_time DATETIME COMMENT '提交时间',
    total_score DECIMAL(5,2) COMMENT '总分',
    obtained_score DECIMAL(5,2) COMMENT '得分',
    exam_status VARCHAR(20) DEFAULT 'not_started' COMMENT '考试状态',
    answers TEXT COMMENT '答案(JSON格式)',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    browser_info VARCHAR(200) COMMENT '浏览器信息',
    violation_count INT DEFAULT 0 COMMENT '违规次数',
    remarks TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_exam_student (exam_id, student_id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_student_id (student_id),
    INDEX idx_exam_status (exam_status),
    INDEX idx_submit_time (submit_time),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (exam_id) REFERENCES tb_exam(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 作业表 (Assignment)
CREATE TABLE IF NOT EXISTS tb_assignment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作业ID',
    assignment_title VARCHAR(200) NOT NULL COMMENT '作业标题',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT NOT NULL COMMENT '教师ID',
    assignment_description TEXT COMMENT '作业描述',
    assignment_type VARCHAR(50) DEFAULT 'homework' COMMENT '作业类型',
    total_score DECIMAL(5,2) DEFAULT 100 COMMENT '总分',
    due_date DATETIME COMMENT '截止时间',
    allow_late_submission TINYINT DEFAULT 0 COMMENT '是否允许迟交',
    late_penalty DECIMAL(3,2) DEFAULT 0 COMMENT '迟交扣分比例',
    submission_format VARCHAR(100) COMMENT '提交格式',
    attachment_path VARCHAR(500) COMMENT '附件路径',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_course_id (course_id),
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_due_date (due_date),
    INDEX idx_assignment_type (assignment_type),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- 作业提交表 (AssignmentSubmission)
CREATE TABLE IF NOT EXISTS tb_assignment_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '提交ID',
    assignment_id BIGINT NOT NULL COMMENT '作业ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    submission_content TEXT COMMENT '提交内容',
    attachment_path VARCHAR(500) COMMENT '附件路径',
    submission_time DATETIME COMMENT '提交时间',
    is_late TINYINT DEFAULT 0 COMMENT '是否迟交',
    score DECIMAL(5,2) COMMENT '得分',
    feedback TEXT COMMENT '反馈',
    graded_by BIGINT COMMENT '评分教师ID',
    graded_time DATETIME COMMENT '评分时间',
    submission_status VARCHAR(20) DEFAULT 'submitted' COMMENT '提交状态',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_assignment_student (assignment_id, student_id),
    INDEX idx_assignment_id (assignment_id),
    INDEX idx_student_id (student_id),
    INDEX idx_graded_by (graded_by),
    INDEX idx_submission_time (submission_time),
    INDEX idx_submission_status (submission_status),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (assignment_id) REFERENCES tb_assignment(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (graded_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- =====================================================
-- 5. 考勤和评价管理表
-- =====================================================

-- 考勤表 (Attendance)
CREATE TABLE IF NOT EXISTS tb_attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    time_slot_id BIGINT COMMENT '时间段ID',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '签到位置',
    device_info VARCHAR(200) COMMENT '设备信息',
    remarks TEXT COMMENT '备注',
    recorded_by BIGINT COMMENT '记录人ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    UNIQUE KEY uk_student_course_date (student_id, course_id, attendance_date, time_slot_id),
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_attendance_status (attendance_status),
    INDEX idx_recorded_by (recorded_by),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE CASCADE,
    FOREIGN KEY (time_slot_id) REFERENCES tb_time_slot(id) ON DELETE SET NULL,
    FOREIGN KEY (recorded_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- 学生评价表 (StudentEvaluation)
CREATE TABLE IF NOT EXISTS tb_student_evaluation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    evaluator_id BIGINT NOT NULL COMMENT '评价人ID',
    evaluation_type VARCHAR(50) NOT NULL COMMENT '评价类型',
    evaluation_period VARCHAR(50) COMMENT '评价周期',
    academic_performance DECIMAL(3,2) COMMENT '学业表现',
    behavior_performance DECIMAL(3,2) COMMENT '行为表现',
    participation_level DECIMAL(3,2) COMMENT '参与度',
    overall_rating DECIMAL(3,2) COMMENT '综合评分',
    strengths TEXT COMMENT '优点',
    weaknesses TEXT COMMENT '不足',
    suggestions TEXT COMMENT '建议',
    evaluation_date DATE COMMENT '评价日期',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_student_id (student_id),
    INDEX idx_evaluator_id (evaluator_id),
    INDEX idx_evaluation_type (evaluation_type),
    INDEX idx_evaluation_date (evaluation_date),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (evaluator_id) REFERENCES tb_user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生评价表';

-- =====================================================
-- 6. 财务管理表
-- =====================================================

-- 费用项目表 (FeeItem)
CREATE TABLE IF NOT EXISTS tb_fee_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '费用项目ID',
    item_name VARCHAR(100) NOT NULL COMMENT '项目名称',
    item_code VARCHAR(50) NOT NULL UNIQUE COMMENT '项目代码',
    item_type VARCHAR(50) NOT NULL COMMENT '项目类型',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    academic_year VARCHAR(20) COMMENT '学年',
    semester VARCHAR(20) COMMENT '学期',
    target_grades VARCHAR(200) COMMENT '目标年级',
    target_majors VARCHAR(500) COMMENT '目标专业',
    due_date DATE COMMENT '缴费截止日期',
    description TEXT COMMENT '项目描述',
    is_mandatory TINYINT DEFAULT 1 COMMENT '是否必缴',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_item_code (item_code),
    INDEX idx_item_type (item_type),
    INDEX idx_academic_year_semester (academic_year, semester),
    INDEX idx_due_date (due_date),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用项目表';

-- 缴费记录表 (PaymentRecord)
CREATE TABLE IF NOT EXISTS tb_payment_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '缴费记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    fee_item_id BIGINT NOT NULL COMMENT '费用项目ID',
    payment_amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    payment_method VARCHAR(50) COMMENT '缴费方式',
    payment_time DATETIME COMMENT '缴费时间',
    payment_status VARCHAR(20) DEFAULT 'pending' COMMENT '缴费状态',
    transaction_id VARCHAR(100) COMMENT '交易流水号',
    payment_channel VARCHAR(50) COMMENT '缴费渠道',
    receipt_number VARCHAR(100) COMMENT '收据号',
    operator_id BIGINT COMMENT '操作员ID',
    remarks TEXT COMMENT '备注',
    refund_amount DECIMAL(10,2) DEFAULT 0 COMMENT '退款金额',
    refund_time DATETIME COMMENT '退款时间',
    refund_reason TEXT COMMENT '退款原因',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_student_id (student_id),
    INDEX idx_fee_item_id (fee_item_id),
    INDEX idx_payment_status (payment_status),
    INDEX idx_payment_time (payment_time),
    INDEX idx_transaction_id (transaction_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (student_id) REFERENCES tb_student(id) ON DELETE CASCADE,
    FOREIGN KEY (fee_item_id) REFERENCES tb_fee_item(id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='缴费记录表';

-- =====================================================
-- 7. 通知和消息管理表
-- =====================================================

-- 通知模板表 (NotificationTemplate)
CREATE TABLE IF NOT EXISTS tb_notification_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    template_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    template_code VARCHAR(50) NOT NULL UNIQUE COMMENT '模板代码',
    template_type VARCHAR(50) NOT NULL COMMENT '模板类型',
    title_template VARCHAR(200) COMMENT '标题模板',
    content_template TEXT COMMENT '内容模板',
    variables TEXT COMMENT '变量说明',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统模板',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_template_code (template_code),
    INDEX idx_template_type (template_type),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知模板表';

-- 通知表 (Notification)
CREATE TABLE IF NOT EXISTS tb_notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    notification_type VARCHAR(50) NOT NULL COMMENT '通知类型',
    priority_level VARCHAR(20) DEFAULT 'normal' COMMENT '优先级',
    sender_id BIGINT COMMENT '发送者ID',
    target_type VARCHAR(50) COMMENT '目标类型',
    target_ids TEXT COMMENT '目标ID列表',
    send_time DATETIME COMMENT '发送时间',
    expire_time DATETIME COMMENT '过期时间',
    is_published TINYINT DEFAULT 0 COMMENT '是否发布',
    read_count INT DEFAULT 0 COMMENT '阅读数',
    attachment_path VARCHAR(500) COMMENT '附件路径',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_notification_type (notification_type),
    INDEX idx_sender_id (sender_id),
    INDEX idx_target_type (target_type),
    INDEX idx_send_time (send_time),
    INDEX idx_priority_level (priority_level),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (sender_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 消息表 (Message)
CREATE TABLE IF NOT EXISTS tb_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    message_type VARCHAR(50) DEFAULT 'private' COMMENT '消息类型',
    title VARCHAR(200) COMMENT '消息标题',
    content TEXT NOT NULL COMMENT '消息内容',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读',
    read_time DATETIME COMMENT '阅读时间',
    parent_id BIGINT COMMENT '父消息ID',
    attachment_path VARCHAR(500) COMMENT '附件路径',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_message_type (message_type),
    INDEX idx_is_read (is_read),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (sender_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES tb_user(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES tb_message(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';

-- =====================================================
-- 8. 系统管理和日志表
-- =====================================================

-- 系统配置表 (SystemConfig)
CREATE TABLE IF NOT EXISTS tb_system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_type VARCHAR(50) DEFAULT 'string' COMMENT '配置类型',
    config_group VARCHAR(50) COMMENT '配置分组',
    description VARCHAR(200) COMMENT '配置描述',
    is_system TINYINT DEFAULT 0 COMMENT '是否系统配置',
    is_encrypted TINYINT DEFAULT 0 COMMENT '是否加密',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_config_key (config_key),
    INDEX idx_config_group (config_group),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 系统设置表 (SystemSettings)
CREATE TABLE IF NOT EXISTS tb_system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '设置ID',
    setting_name VARCHAR(100) NOT NULL COMMENT '设置名称',
    setting_key VARCHAR(100) NOT NULL UNIQUE COMMENT '设置键',
    setting_value TEXT COMMENT '设置值',
    default_value TEXT COMMENT '默认值',
    setting_type VARCHAR(50) DEFAULT 'string' COMMENT '设置类型',
    category VARCHAR(50) COMMENT '设置分类',
    description VARCHAR(200) COMMENT '设置描述',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_setting_key (setting_key),
    INDEX idx_category (category),
    INDEX idx_status_deleted (status, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统设置表';

-- 活动日志表 (ActivityLog)
CREATE TABLE IF NOT EXISTS tb_activity_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作动作',
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id BIGINT COMMENT '资源ID',
    description TEXT COMMENT '操作描述',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    request_method VARCHAR(10) COMMENT '请求方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_params TEXT COMMENT '请求参数',
    response_status INT COMMENT '响应状态',
    execution_time BIGINT COMMENT '执行时间(毫秒)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_resource_type (resource_type),
    INDEX idx_resource_id (resource_id),
    INDEX idx_created_at (created_at),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动日志表';

-- 资源访问日志表 (ResourceAccessLog)
CREATE TABLE IF NOT EXISTS tb_resource_access_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '访问日志ID',
    user_id BIGINT COMMENT '用户ID',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    access_type VARCHAR(50) NOT NULL COMMENT '访问类型',
    access_time DATETIME NOT NULL COMMENT '访问时间',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    session_id VARCHAR(100) COMMENT '会话ID',
    duration BIGINT COMMENT '访问时长(秒)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_user_id (user_id),
    INDEX idx_resource_type_id (resource_type, resource_id),
    INDEX idx_access_type (access_type),
    INDEX idx_access_time (access_time),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源访问日志表';

-- 调度表 (Schedule)
CREATE TABLE IF NOT EXISTS tb_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '调度ID',
    schedule_name VARCHAR(100) NOT NULL COMMENT '调度名称',
    schedule_type VARCHAR(50) NOT NULL COMMENT '调度类型',
    target_id BIGINT COMMENT '目标ID',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    start_time TIME COMMENT '开始时间',
    end_time TIME COMMENT '结束时间',
    repeat_type VARCHAR(50) DEFAULT 'none' COMMENT '重复类型',
    repeat_config TEXT COMMENT '重复配置',
    location VARCHAR(200) COMMENT '地点',
    description TEXT COMMENT '描述',
    organizer_id BIGINT COMMENT '组织者ID',
    participants TEXT COMMENT '参与者',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态',
    INDEX idx_schedule_type (schedule_type),
    INDEX idx_target_id (target_id),
    INDEX idx_start_date (start_date),
    INDEX idx_organizer_id (organizer_id),
    INDEX idx_status_deleted (status, deleted),
    FOREIGN KEY (organizer_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调度表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建结果
SELECT 'Smart Campus Management System 表结构创建完成' as '状态';
SELECT COUNT(*) as '总表数量' FROM information_schema.tables WHERE table_schema = 'campus_management_db';

COMMIT;
