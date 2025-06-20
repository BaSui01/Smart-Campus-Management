-- 智慧校园管理系统 - 学生表分表脚本
-- 创建时间: 2025-06-20
-- 说明: 按入学年份对学生表进行分表

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 2024年入学学生表
CREATE TABLE IF NOT EXISTS tb_student_2024 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联的用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级/专业',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    student_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '学生状态',
    academic_advisor_id BIGINT COMMENT '学术导师ID',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    dormitory_info VARCHAR(200) COMMENT '宿舍信息',
    scholarship_info TEXT COMMENT '奖学金信息',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_student_status (student_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL,
    FOREIGN KEY (academic_advisor_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年入学学生表';

-- 2025年入学学生表
CREATE TABLE IF NOT EXISTS tb_student_2025 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联的用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级/专业',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    student_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '学生状态',
    academic_advisor_id BIGINT COMMENT '学术导师ID',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    dormitory_info VARCHAR(200) COMMENT '宿舍信息',
    scholarship_info TEXT COMMENT '奖学金信息',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_student_status (student_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL,
    FOREIGN KEY (academic_advisor_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2025年入学学生表';

-- 2026年入学学生表
CREATE TABLE IF NOT EXISTS tb_student_2026 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联的用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级/专业',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    student_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '学生状态',
    academic_advisor_id BIGINT COMMENT '学术导师ID',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    dormitory_info VARCHAR(200) COMMENT '宿舍信息',
    scholarship_info TEXT COMMENT '奖学金信息',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_student_status (student_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL,
    FOREIGN KEY (academic_advisor_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2026年入学学生表';

-- 2027年入学学生表
CREATE TABLE IF NOT EXISTS tb_student_2027 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联的用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级/专业',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    student_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '学生状态',
    academic_advisor_id BIGINT COMMENT '学术导师ID',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    dormitory_info VARCHAR(200) COMMENT '宿舍信息',
    scholarship_info TEXT COMMENT '奖学金信息',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_student_status (student_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL,
    FOREIGN KEY (academic_advisor_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2027年入学学生表';

-- 2028年入学学生表
CREATE TABLE IF NOT EXISTS tb_student_2028 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '学生ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '关联的用户ID',
    student_no VARCHAR(20) NOT NULL UNIQUE COMMENT '学号',
    grade VARCHAR(50) NOT NULL COMMENT '年级/专业',
    major VARCHAR(100) COMMENT '专业',
    class_id BIGINT COMMENT '班级ID',
    enrollment_year INT NOT NULL COMMENT '入学年份',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    student_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '学生状态',
    academic_advisor_id BIGINT COMMENT '学术导师ID',
    emergency_contact VARCHAR(100) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    dormitory_info VARCHAR(200) COMMENT '宿舍信息',
    scholarship_info TEXT COMMENT '奖学金信息',
    notes TEXT COMMENT '备注',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_user_id (user_id),
    INDEX idx_student_no (student_no),
    INDEX idx_class_id (class_id),
    INDEX idx_enrollment_year (enrollment_year),
    INDEX idx_student_status (student_status, deleted),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (user_id) REFERENCES tb_user(id) ON DELETE RESTRICT,
    FOREIGN KEY (class_id) REFERENCES tb_class(id) ON DELETE SET NULL,
    FOREIGN KEY (academic_advisor_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2028年入学学生表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建结果
SELECT 'Student sharding tables created successfully' as status;
SELECT COUNT(*) as student_sharding_tables_count 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name LIKE 'tb_student_20%';

COMMIT;
