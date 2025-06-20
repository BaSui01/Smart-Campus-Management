-- 智慧校园管理系统 - 实体类拆分优化SQL脚本
-- 用于创建拆分后的新表和数据迁移

-- ================================
-- 1. 创建学生详细档案表
-- ================================
CREATE TABLE IF NOT EXISTS `tb_student_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '关联的学生ID',
    `parent_name` VARCHAR(50) COMMENT '家长/监护人姓名',
    `parent_phone` VARCHAR(255) COMMENT '家长/监护人电话（加密）',
    `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(255) COMMENT '紧急联系人电话（加密）',
    `dormitory` VARCHAR(50) COMMENT '宿舍号',
    `bed_number` VARCHAR(20) COMMENT '床位号',
    `home_address` VARCHAR(200) COMMENT '家庭住址',
    `postal_code` VARCHAR(6) COMMENT '邮政编码',
    `ethnicity` VARCHAR(20) COMMENT '民族',
    `political_status` VARCHAR(20) COMMENT '政治面貌',
    `native_place` VARCHAR(100) COMMENT '籍贯',
    `hobbies` VARCHAR(500) COMMENT '特长爱好',
    `health_status` VARCHAR(200) COMMENT '健康状况',
    `allergy_history` VARCHAR(300) COMMENT '过敏史',
    `reward_punishment` VARCHAR(1000) COMMENT '奖惩记录',
    `remarks` VARCHAR(500) COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：1-已删除，0-未删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_dormitory` (`dormitory`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生详细档案表';

-- ================================
-- 2. 创建学生学术信息表
-- ================================
CREATE TABLE IF NOT EXISTS `tb_student_academic` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `student_id` BIGINT NOT NULL COMMENT '关联的学生ID',
    `academic_status` TINYINT NOT NULL DEFAULT 1 COMMENT '学籍状态：1-在读，2-毕业，3-退学，4-休学，5-转学',
    `student_type` VARCHAR(20) COMMENT '学生类型：本科生、研究生、博士生等',
    `training_mode` VARCHAR(20) COMMENT '培养方式：全日制、非全日制',
    `academic_system` INT COMMENT '学制（年）',
    `current_semester` VARCHAR(20) COMMENT '当前学期',
    `total_credits` DECIMAL(5,1) DEFAULT 0.0 COMMENT '总学分',
    `earned_credits` DECIMAL(5,1) DEFAULT 0.0 COMMENT '已获得学分',
    `gpa` DECIMAL(3,2) DEFAULT 0.00 COMMENT 'GPA',
    `enrollment_date` DATE COMMENT '入学日期',
    `graduation_date` DATE COMMENT '毕业日期',
    `expected_graduation_date` DATE COMMENT '预计毕业日期',
    `degree_type` VARCHAR(20) COMMENT '学位类型',
    `thesis_title` VARCHAR(200) COMMENT '论文题目',
    `supervisor_id` BIGINT COMMENT '导师ID',
    `research_direction` VARCHAR(100) COMMENT '研究方向',
    `academic_achievements` VARCHAR(1000) COMMENT '学术成果',
    `internship_experience` VARCHAR(1000) COMMENT '实习经历',
    `social_practice` VARCHAR(1000) COMMENT '社会实践',
    `awards` VARCHAR(1000) COMMENT '获奖情况',
    `academic_remarks` VARCHAR(500) COMMENT '学术备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：1-已删除，0-未删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_id` (`student_id`),
    KEY `idx_student_id` (`student_id`),
    KEY `idx_academic_status` (`academic_status`),
    KEY `idx_gpa` (`gpa`),
    KEY `idx_graduation_date` (`graduation_date`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生学术信息表';

-- ================================
-- 3. 创建用户详细档案表
-- ================================
CREATE TABLE IF NOT EXISTS `tb_user_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '关联的用户ID',
    `birthday` DATE COMMENT '生日',
    `id_card` VARCHAR(255) COMMENT '身份证号（加密）',
    `avatar_url` VARCHAR(500) COMMENT '头像URL',
    `address` VARCHAR(200) COMMENT '地址',
    `ethnicity` VARCHAR(20) COMMENT '民族',
    `political_status` VARCHAR(20) COMMENT '政治面貌',
    `native_place` VARCHAR(100) COMMENT '籍贯',
    `marital_status` VARCHAR(10) COMMENT '婚姻状况',
    `education` VARCHAR(20) COMMENT '学历',
    `graduate_school` VARCHAR(100) COMMENT '毕业院校',
    `major` VARCHAR(50) COMMENT '专业',
    `work_unit` VARCHAR(100) COMMENT '工作单位',
    `position` VARCHAR(50) COMMENT '职务',
    `title` VARCHAR(50) COMMENT '职称',
    `emergency_contact` VARCHAR(50) COMMENT '紧急联系人',
    `emergency_phone` VARCHAR(255) COMMENT '紧急联系人电话（加密）',
    `bio` VARCHAR(1000) COMMENT '个人简介',
    `skills` VARCHAR(500) COMMENT '特长技能',
    `hobbies` VARCHAR(500) COMMENT '兴趣爱好',
    `remarks` VARCHAR(500) COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：1-已删除，0-未删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_id_card` (`id_card`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户详细档案表';

-- ================================
-- 4. 创建用户登录日志表
-- ================================
CREATE TABLE IF NOT EXISTS `tb_user_login_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT NOT NULL COMMENT '关联的用户ID',
    `login_time` DATETIME NOT NULL COMMENT '登录时间',
    `login_ip` VARCHAR(50) COMMENT '登录IP',
    `login_status` INT NOT NULL COMMENT '登录状态：1-成功，0-失败',
    `login_type` TINYINT DEFAULT 1 COMMENT '登录方式：1-用户名密码，2-手机验证码，3-邮箱验证码，4-第三方登录',
    `user_agent` VARCHAR(500) COMMENT '用户代理（浏览器信息）',
    `operating_system` VARCHAR(50) COMMENT '操作系统',
    `browser` VARCHAR(50) COMMENT '浏览器',
    `device_type` VARCHAR(20) COMMENT '设备类型：PC、Mobile、Tablet',
    `login_location` VARCHAR(100) COMMENT '登录地点（根据IP解析）',
    `failure_reason` VARCHAR(200) COMMENT '失败原因（登录失败时记录）',
    `logout_time` DATETIME COMMENT '登出时间',
    `session_duration` INT COMMENT '会话时长（分钟）',
    `remarks` VARCHAR(500) COMMENT '备注',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：1-已删除，0-未删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `created_by` BIGINT COMMENT '创建人ID',
    `updated_by` BIGINT COMMENT '更新人ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_login_ip` (`login_ip`),
    KEY `idx_login_status` (`login_status`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录日志表';

-- ================================
-- 5. 数据迁移脚本（如果已有数据）
-- ================================

-- 迁移学生详细信息到 tb_student_profile
INSERT INTO `tb_student_profile` (
    `student_id`, `parent_name`, `parent_phone`, `emergency_contact`, `emergency_phone`,
    `dormitory`, `remarks`, `created_at`, `updated_at`, `created_by`, `updated_by`
)
SELECT 
    `id`, `parent_name`, `parent_phone`, `emergency_contact`, `emergency_phone`,
    `dormitory`, `remarks`, `created_at`, `updated_at`, `created_by`, `updated_by`
FROM `tb_student`
WHERE `id` NOT IN (SELECT `student_id` FROM `tb_student_profile`)
ON DUPLICATE KEY UPDATE
    `parent_name` = VALUES(`parent_name`),
    `parent_phone` = VALUES(`parent_phone`),
    `emergency_contact` = VALUES(`emergency_contact`),
    `emergency_phone` = VALUES(`emergency_phone`),
    `dormitory` = VALUES(`dormitory`),
    `remarks` = VALUES(`remarks`),
    `updated_at` = VALUES(`updated_at`),
    `updated_by` = VALUES(`updated_by`);

-- 迁移学生学术信息到 tb_student_academic
INSERT INTO `tb_student_academic` (
    `student_id`, `academic_status`, `student_type`, `training_mode`, `academic_system`,
    `current_semester`, `total_credits`, `earned_credits`, `gpa`, `enrollment_date`,
    `graduation_date`, `created_at`, `updated_at`, `created_by`, `updated_by`
)
SELECT 
    `id`, `academic_status`, `student_type`, `training_mode`, `academic_system`,
    `current_semester`, `total_credits`, `earned_credits`, `gpa`, `enrollment_date`,
    `graduation_date`, `created_at`, `updated_at`, `created_by`, `updated_by`
FROM `tb_student`
WHERE `id` NOT IN (SELECT `student_id` FROM `tb_student_academic`)
ON DUPLICATE KEY UPDATE
    `academic_status` = VALUES(`academic_status`),
    `student_type` = VALUES(`student_type`),
    `training_mode` = VALUES(`training_mode`),
    `academic_system` = VALUES(`academic_system`),
    `current_semester` = VALUES(`current_semester`),
    `total_credits` = VALUES(`total_credits`),
    `earned_credits` = VALUES(`earned_credits`),
    `gpa` = VALUES(`gpa`),
    `enrollment_date` = VALUES(`enrollment_date`),
    `graduation_date` = VALUES(`graduation_date`),
    `updated_at` = VALUES(`updated_at`),
    `updated_by` = VALUES(`updated_by`);

-- 迁移用户详细信息到 tb_user_profile
INSERT INTO `tb_user_profile` (
    `user_id`, `birthday`, `id_card`, `avatar_url`, `address`, `remarks`,
    `created_at`, `updated_at`, `created_by`, `updated_by`
)
SELECT 
    `id`, `birthday`, `id_card`, `avatar_url`, `address`, `remarks`,
    `created_at`, `updated_at`, `created_by`, `updated_by`
FROM `tb_user`
WHERE `id` NOT IN (SELECT `user_id` FROM `tb_user_profile`)
ON DUPLICATE KEY UPDATE
    `birthday` = VALUES(`birthday`),
    `id_card` = VALUES(`id_card`),
    `avatar_url` = VALUES(`avatar_url`),
    `address` = VALUES(`address`),
    `remarks` = VALUES(`remarks`),
    `updated_at` = VALUES(`updated_at`),
    `updated_by` = VALUES(`updated_by`);

-- ================================
-- 6. 创建外键约束（可选，根据需要启用）
-- ================================

-- ALTER TABLE `tb_student_profile` ADD CONSTRAINT `fk_student_profile_student` 
--     FOREIGN KEY (`student_id`) REFERENCES `tb_student` (`id`) ON DELETE CASCADE;

-- ALTER TABLE `tb_student_academic` ADD CONSTRAINT `fk_student_academic_student` 
--     FOREIGN KEY (`student_id`) REFERENCES `tb_student` (`id`) ON DELETE CASCADE;

-- ALTER TABLE `tb_user_profile` ADD CONSTRAINT `fk_user_profile_user` 
--     FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE;

-- ALTER TABLE `tb_user_login_log` ADD CONSTRAINT `fk_user_login_log_user` 
--     FOREIGN KEY (`user_id`) REFERENCES `tb_user` (`id`) ON DELETE CASCADE;

-- ================================
-- 7. 创建索引优化查询性能
-- ================================

-- 为经常查询的字段创建复合索引
CREATE INDEX `idx_student_profile_dormitory_status` ON `tb_student_profile` (`dormitory`, `status`, `deleted`);
CREATE INDEX `idx_student_academic_status_gpa` ON `tb_student_academic` (`academic_status`, `gpa`, `deleted`);
CREATE INDEX `idx_user_login_log_user_time` ON `tb_user_login_log` (`user_id`, `login_time`, `login_status`);
CREATE INDEX `idx_user_login_log_ip_time` ON `tb_user_login_log` (`login_ip`, `login_time`);

-- ================================
-- 8. 创建视图简化查询（可选）
-- ================================

-- 学生完整信息视图
CREATE OR REPLACE VIEW `v_student_full_info` AS
SELECT 
    s.id,
    s.user_id,
    s.student_no,
    s.grade,
    s.major,
    s.class_id,
    s.enrollment_year,
    u.real_name,
    u.gender,
    u.phone,
    u.email,
    sp.parent_name,
    sp.parent_phone,
    sp.dormitory,
    sa.academic_status,
    sa.gpa,
    sa.total_credits,
    sa.earned_credits,
    s.status,
    s.deleted,
    s.created_at,
    s.updated_at
FROM `tb_student` s
LEFT JOIN `tb_user` u ON s.user_id = u.id
LEFT JOIN `tb_student_profile` sp ON s.id = sp.student_id
LEFT JOIN `tb_student_academic` sa ON s.id = sa.student_id
WHERE s.deleted = 0;

-- 用户完整信息视图
CREATE OR REPLACE VIEW `v_user_full_info` AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.real_name,
    u.phone,
    u.gender,
    u.last_login_time,
    u.last_login_ip,
    u.login_count,
    up.birthday,
    up.id_card,
    up.avatar_url,
    up.address,
    up.ethnicity,
    up.political_status,
    u.status,
    u.deleted,
    u.created_at,
    u.updated_at
FROM `tb_user` u
LEFT JOIN `tb_user_profile` up ON u.id = up.user_id
WHERE u.deleted = 0;
