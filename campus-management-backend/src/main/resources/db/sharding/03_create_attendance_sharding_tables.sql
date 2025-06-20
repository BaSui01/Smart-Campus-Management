-- 智慧校园管理系统 - 考勤表分表脚本
-- 创建时间: 2025-06-20
-- 说明: 按年月对考勤表进行分表

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 2024年6月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202406 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年6月考勤表';

-- 2024年7月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202407 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年7月考勤表';

-- 2024年8月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202408 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年8月考勤表';

-- 2024年9月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202409 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年9月考勤表';

-- 2024年10月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202410 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年10月考勤表';

-- 2024年11月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202411 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年11月考勤表';

-- 2024年12月考勤表
CREATE TABLE IF NOT EXISTS tb_attendance_202412 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    attendance_time TIME COMMENT '考勤时间',
    attendance_status VARCHAR(20) NOT NULL COMMENT '考勤状态',
    check_in_time DATETIME COMMENT '签到时间',
    check_out_time DATETIME COMMENT '签退时间',
    location VARCHAR(100) COMMENT '考勤地点',
    device_info VARCHAR(200) COMMENT '设备信息',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    remarks TEXT COMMENT '备注',
    teacher_id BIGINT COMMENT '任课教师ID',
    is_makeup TINYINT DEFAULT 0 COMMENT '是否补签',
    makeup_reason VARCHAR(200) COMMENT '补签原因',
    approved_by BIGINT COMMENT '审批人ID',
    approved_at DATETIME COMMENT '审批时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_student_date (student_id, attendance_date),
    INDEX idx_attendance_status (attendance_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年12月考勤表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建结果
SELECT 'Attendance sharding tables created successfully' as status;
SELECT COUNT(*) as attendance_sharding_tables_count 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name LIKE 'tb_attendance_2024%';

COMMIT;
