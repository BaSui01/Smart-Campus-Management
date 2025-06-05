-- ================================================
-- 校园管理系统数据库表创建脚本
-- 创建时间: 2025-06-05
-- 编码: UTF-8
-- ================================================

-- 使用数据库
USE campus_management_db;

-- 设置字符编码
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ================================================
-- 1. 用户表 (tb_user)
-- ================================================
DROP TABLE IF EXISTS tb_user;
CREATE TABLE tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    avatar_url VARCHAR(500) COMMENT '头像URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ================================================
-- 2. 角色表 (tb_role)
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
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    remark VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ================================================
-- 3. 权限表 (tb_permission)
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
-- 4. 用户角色关联表 (tb_user_role)
-- ================================================
DROP TABLE IF EXISTS tb_user_role;
CREATE TABLE tb_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ================================================
-- 5. 角色权限关联表 (tb_role_permission)
-- ================================================
DROP TABLE IF EXISTS tb_role_permission;
CREATE TABLE tb_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ================================================
-- 6. 班级表 (tb_class)
-- ================================================
DROP TABLE IF EXISTS tb_class;
CREATE TABLE tb_class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '班级名称',
    class_code VARCHAR(20) NOT NULL UNIQUE COMMENT '班级代码',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    department_id BIGINT COMMENT '院系ID',
    head_teacher_id BIGINT COMMENT '班主任ID',
    description VARCHAR(500) COMMENT '班级描述',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- ================================================
-- 7. 学生表 (tb_student)
-- ================================================
DROP TABLE IF EXISTS tb_student;
CREATE TABLE tb_student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(20) NOT NULL COMMENT '年级',
    class_id BIGINT COMMENT '班级ID',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-在读，2-毕业，3-退学，0-禁用',
    remarks VARCHAR(500) COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生表';

-- ================================================
-- 8. 课程表 (tb_course)
-- ================================================
DROP TABLE IF EXISTS tb_course;
CREATE TABLE tb_course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_code VARCHAR(20) NOT NULL UNIQUE COMMENT '课程代码',
    credits INT NOT NULL COMMENT '学分',
    department_id BIGINT COMMENT '院系ID',
    description VARCHAR(500) COMMENT '课程描述',
    teacher_id BIGINT COMMENT '主讲教师ID',
    max_students INT COMMENT '最大学生数',
    enrolled_students INT DEFAULT 0 COMMENT '已选学生数',
    course_type VARCHAR(50) COMMENT '课程类型：必修课，选修课，实践课',
    semester VARCHAR(50) COMMENT '学期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- ================================================
-- 9. 课程表排课表 (tb_course_schedule)
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
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表排课表';

-- ================================================
-- 10. 选课表 (tb_course_selection)
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
    UNIQUE KEY uk_student_schedule (student_id, schedule_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='选课表';

-- ================================================
-- 11. 成绩表 (tb_grade)
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
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成绩表';

-- ================================================
-- 12. 缴费项目表 (tb_fee_item)
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
-- 13. 缴费记录表 (tb_payment_record)
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
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缴费记录表';

-- 显示创建的表
SHOW TABLES;

-- 显示表创建完成信息
SELECT 'All tables created successfully!' AS result;