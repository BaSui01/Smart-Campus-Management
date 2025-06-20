-- 智慧校园管理系统 - 成绩表分表脚本
-- 创建时间: 2025-06-20
-- 说明: 按学期对成绩表进行分表

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 2024年第1学期成绩表
CREATE TABLE IF NOT EXISTS tb_grade_2024_1 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    grade_level VARCHAR(10) COMMENT '成绩等级',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    exam_date DATE COMMENT '考试日期',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks TEXT COMMENT '备注',
    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
    retake_score DECIMAL(5,2) COMMENT '补考成绩',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester (semester),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_exam_type (exam_type, deleted),
    INDEX idx_score (score),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年第1学期成绩表';

-- 2024年第2学期成绩表
CREATE TABLE IF NOT EXISTS tb_grade_2024_2 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    grade_level VARCHAR(10) COMMENT '成绩等级',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    exam_date DATE COMMENT '考试日期',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks TEXT COMMENT '备注',
    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
    retake_score DECIMAL(5,2) COMMENT '补考成绩',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester (semester),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_exam_type (exam_type, deleted),
    INDEX idx_score (score),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2024年第2学期成绩表';

-- 2025年第1学期成绩表
CREATE TABLE IF NOT EXISTS tb_grade_2025_1 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    grade_level VARCHAR(10) COMMENT '成绩等级',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    exam_date DATE COMMENT '考试日期',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks TEXT COMMENT '备注',
    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
    retake_score DECIMAL(5,2) COMMENT '补考成绩',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester (semester),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_exam_type (exam_type, deleted),
    INDEX idx_score (score),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2025年第1学期成绩表';

-- 2025年第2学期成绩表
CREATE TABLE IF NOT EXISTS tb_grade_2025_2 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    grade_level VARCHAR(10) COMMENT '成绩等级',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    exam_date DATE COMMENT '考试日期',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks TEXT COMMENT '备注',
    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
    retake_score DECIMAL(5,2) COMMENT '补考成绩',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester (semester),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_exam_type (exam_type, deleted),
    INDEX idx_score (score),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2025年第2学期成绩表';

-- 2026年第1学期成绩表
CREATE TABLE IF NOT EXISTS tb_grade_2026_1 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    grade_level VARCHAR(10) COMMENT '成绩等级',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    exam_date DATE COMMENT '考试日期',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks TEXT COMMENT '备注',
    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
    retake_score DECIMAL(5,2) COMMENT '补考成绩',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester (semester),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_exam_type (exam_type, deleted),
    INDEX idx_score (score),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2026年第1学期成绩表';

-- 2026年第2学期成绩表
CREATE TABLE IF NOT EXISTS tb_grade_2026_2 (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    exam_type VARCHAR(20) NOT NULL COMMENT '考试类型',
    score DECIMAL(5,2) NOT NULL COMMENT '成绩分数',
    grade_level VARCHAR(10) COMMENT '成绩等级',
    semester VARCHAR(20) NOT NULL COMMENT '学期',
    academic_year VARCHAR(20) NOT NULL COMMENT '学年',
    exam_date DATE COMMENT '考试日期',
    teacher_id BIGINT COMMENT '任课教师ID',
    remarks TEXT COMMENT '备注',
    is_retake TINYINT DEFAULT 0 COMMENT '是否补考',
    retake_score DECIMAL(5,2) COMMENT '补考成绩',
    final_score DECIMAL(5,2) COMMENT '最终成绩',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
    
    -- 索引
    INDEX idx_student_id (student_id),
    INDEX idx_course_id (course_id),
    INDEX idx_semester (semester),
    INDEX idx_student_course (student_id, course_id),
    INDEX idx_exam_type (exam_type, deleted),
    INDEX idx_score (score),
    INDEX idx_created_at (created_at),
    
    -- 外键约束
    FOREIGN KEY (course_id) REFERENCES tb_course(id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES tb_user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='2026年第2学期成绩表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建结果
SELECT 'Grade sharding tables created successfully' as status;
SELECT COUNT(*) as grade_sharding_tables_count 
FROM information_schema.tables 
WHERE table_schema = DATABASE() 
AND table_name LIKE 'tb_grade_20%';

COMMIT;
